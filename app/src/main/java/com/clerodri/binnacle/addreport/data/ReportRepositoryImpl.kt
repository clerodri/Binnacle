package com.clerodri.binnacle.addreport.data

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.clerodri.binnacle.addreport.data.datasource.network.ReportService
import com.clerodri.binnacle.addreport.data.datasource.network.dto.AddReportResponse
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventDto
import com.clerodri.binnacle.addreport.domain.Report
import com.clerodri.binnacle.addreport.domain.ReportRepository
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.core.di.S3OkHttpClient
import com.clerodri.binnacle.util.rotate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume


class ReportRepositoryImpl @Inject constructor(
    private val reportService: ReportService,
    private val application: Application,
    @S3OkHttpClient private val s3OkHttpClient: OkHttpClient
) : ReportRepository {

    companion object {
        const val TAG = "ReportPhotoRepository"
    }

    override suspend fun addReport(report: Report): Result<AddReportResponse, DataError.Report> {

        return try {
            val eventDto = EventDto(
                title = report.title,
                detail = report.description,
                roundId = report.roundId,
                images = report.images,
                imageType = report.imageType
            )
            val response = reportService.addReport(eventDto)

            Result.Success(response)
        } catch (e: HttpException) {
            when (e.code()) {
                101 -> Result.Failure(DataError.Report.S3_DISABLE)
                408 -> Result.Failure(DataError.Report.REQUEST_TIMEOUT)
                else -> Result.Failure(DataError.Report.SERVICE_UNAVAILABLE)
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.Failure(DataError.Report.SERVICE_UNAVAILABLE)
        } catch (e: IOException) {
            Result.Failure(DataError.Report.NO_INTERNET)
        }
    }


    override suspend fun captureImage(imageCapture: ImageCapture): Bitmap? {
        return suspendCancellableCoroutine { continuation ->
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(application),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bitmap = image.toBitmap().rotate(
                            image.imageInfo.rotationDegrees.toFloat()
                        )
                        image.close()
                        if (continuation.isActive) continuation.resume(bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exception.message}")
                        if (continuation.isActive) continuation.resume(null)
                    }
                }
            )
        }
    }

    override suspend fun uploadPhoto(
        preSignedUrl: String?,
        bitmap: Bitmap
    ): Result<Unit, DataError.Report> = withContext(Dispatchers.IO) {
        try {
            val request = reportService.prepareS3UploadRequest(preSignedUrl, bitmap)
            if (request == null) {
                Log.e(TAG, "No se pudo preparar request para S3")
                return@withContext Result.Failure(DataError.Report.S3_REQUEST_FAILED)
            }

            Log.d(TAG, "Iniciando upload a S3...")
            val response = s3OkHttpClient.newCall(request).execute()
            when {
                response.isSuccessful -> {
                    Log.d(TAG, "Upload exitoso a S3")
                    response.close()
                    Result.Success(Unit)
                }

                response.code == 403 -> {
                    val errorBody = response.body?.string() ?: "No body"
                    Log.e(TAG, "403 Forbidden - Signature mismatch")
                    Log.e(TAG, "Error body: $errorBody")
                    response.close()
                    Result.Failure(DataError.Report.S3_INVALID_CREDENTIALS)
                }

                response.code == 404 -> {
                    Log.e(TAG, "404 Not Found - Bucket no existe")
                    response.close()
                    Result.Failure(DataError.Report.BUCKET_NOT_EXISTS)
                }

                else -> {
                    val errorBody = response.body?.string() ?: "No body"
                    Log.e(TAG, "Upload falló: ${response.code}")
                    Log.e(TAG, "Error body: $errorBody")
                    response.close()
                    Result.Failure(DataError.Report.S3_UNKNOWN_ERROR)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception durante upload: ${e.message}", e)
            Result.Failure(DataError.Report.PHOTO_UPLOAD_FAILED)
        }
    }


}


