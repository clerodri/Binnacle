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
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventResponse
import com.clerodri.binnacle.addreport.domain.AddReportResponse
import com.clerodri.binnacle.addreport.domain.Report
import com.clerodri.binnacle.addreport.domain.ReportRepository
import com.clerodri.binnacle.addreport.ui.PhotoItem
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
import okhttp3.Response
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume


class ReportRepositoryImpl @Inject constructor(
    private val reportService: ReportService,
    private val application: Application
) : ReportRepository {

    override suspend fun addReport(report: Report): Result<Unit, DataError.Report> {

        return try {
            val eventDto = EventDto(
                title = report.title,
                detail = report.description,
                roundId = report.roundId,
                images = report.images.map { it.fileName }
            )
            val response = reportService.addReport(eventDto)
            println("TEST addReport $response")

            if (response.isNotEmpty()) {
                val uploadResults = uploadPhotos(
                    photos = report.images,
                    result = response
                )
                if (uploadResults is Result.Failure) {
                    return uploadResults
                }
            }
            //here call the other service to upload the images
            Result.Success(Unit)
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

    override suspend fun uploadPhotos(
        photos: List<PhotoItem>,
        result: List<EventResponse>
    ): Result<Unit, DataError.Report> = withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        photos.forEach { photo ->
            val uploadUrl = getUploadUrlFor(photo, result)

            if (uploadUrl.isNullOrEmpty()) {
                Log.w("Upload", "Skipping ${photo.fileName}: upload URL not provided.")
                return@forEach
            }


            val request = buildUploadRequest(photo, uploadUrl)
            val response = executeUpload(client, request)

            if (!response.isSuccessful) {
                return@withContext failure("Failed to upload ${photo.fileName}: ${response.code}")
            }

            Log.d("Upload", "Uploaded ${photo.fileName} successfully")
        }

        Result.Success(Unit)
    }


    private fun getUploadUrlFor(photo: PhotoItem, eventResponses: List<EventResponse>): String? {
        return eventResponses.find { it.filename == photo.fileName }?.urlUpload
    }

    private fun buildUploadRequest(photo: PhotoItem, url: String): Request {
        val byteArray = ByteArrayOutputStream().use { stream ->
            photo.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.toByteArray()
        }

        val mediaType = "image/jpeg".toMediaType()
        val body = byteArray.toRequestBody(mediaType)

        return Request.Builder()
            .url(url)
            .put(body)
            .addHeader("Content-Type", "image/jpeg")
            .build()
    }

    private fun executeUpload(client: OkHttpClient, request: Request): Response {
        return client.newCall(request).execute()
    }
    private fun failure(message: String): Result.Failure<Unit, DataError.Report> {
        Log.e("Upload", message)
        return Result.Failure(DataError.Report.REQUEST_TIMEOUT)
    }

}


