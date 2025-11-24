package com.clerodri.binnacle.addreport.data

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.clerodri.binnacle.addreport.data.datasource.network.ReportService
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventDto
import com.clerodri.binnacle.addreport.domain.AddReportResponse
import com.clerodri.binnacle.addreport.domain.Report
import com.clerodri.binnacle.addreport.domain.ReportRepository
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.util.rotate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume


class ReportRepositoryImpl @Inject constructor(
    private val reportService: ReportService,
    private val application: Application
) : ReportRepository {

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


            Result.Success(AddReportResponse(response.eventId))
        } catch (e: HttpException) {
            when (e.code()) {
                408 -> Result.Failure(DataError.Report.REQUEST_TIMEOUT)
                else -> Result.Failure(DataError.Report.NO_INTERNET)
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
                        Log.d("CameraX", "Capture successful")
                        val bitmap =
                            image.toBitmap().rotate(image.imageInfo.rotationDegrees.toFloat())
                        image.close()
                        if (continuation.isActive) continuation.resume(bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.d("CameraX", "Photo capture failed: ${exception.message}", exception)
                        if (continuation.isActive) continuation.resume(null)
                    }

                }
            )
        }
    }

    override suspend fun uploadPhoto(
        response: AddReportResponse,
        bitmap: Bitmap
    ): Result<Unit, DataError.Report> = withContext(Dispatchers.IO) {

        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            val mediaType = "image/jpeg".toMediaType()
            val requestBody = byteArray.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(response.eventId!!)
                .put(requestBody)
                .addHeader("Content-Type", "image/jpeg")
                .build()


            val response = OkHttpClient().newCall(request).execute()

            if (response.isSuccessful) {
                Log.d("CameraX", "Upload successful")
                Result.Success(Unit)
            } else {
                val errorBody = response.body?.string()
                Log.e("CameraX", "Upload failed: ${response.code} - $errorBody")
                Result.Failure(DataError.Report.REQUEST_TIMEOUT)
            }
        } catch (e: Exception) {
            Log.e("CameraX", "Exception: ${e.localizedMessage}", e)
            Result.Failure(DataError.Report.REQUEST_TIMEOUT)
        }
    }


}


