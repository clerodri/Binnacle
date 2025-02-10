package com.clerodri.binnacle.addreport.data

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.clerodri.binnacle.addreport.data.datasource.network.ReportService
import com.clerodri.binnacle.addreport.data.datasource.network.dto.ReportDto
import com.clerodri.binnacle.addreport.domain.Report
import com.clerodri.binnacle.addreport.domain.ReportRepository
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.util.rotate
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume


class ReportRepositoryImpl @Inject constructor(
    private val reportService: ReportService,
    private val application: Application
) : ReportRepository {


    override suspend fun addReport(report: Report): Result<Unit, DataError.Report> {

        return try {
            val imageFile = saveBitmapToFile(report.imageBitmap!!)
            val result = reportService.addReport(
                ReportDto(
                    report.title,
                    report.description,
                    report.routeId,
                    report.roundId,
                    report.localityId
                ), imageFile
            )
            Result.Success(Unit)
        } catch (e: HttpException) {
            when (e.code()) {
                408 -> Result.Failure(DataError.Report.REQUEST_TIMEOUT)
                else -> Result.Failure(DataError.Report.NO_INTERNET)
            }
        }
    }


    override suspend fun captureImage(imageCapture: ImageCapture): Bitmap? {
        return suspendCancellableCoroutine { continuation ->
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(application),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        Log.d("CameraX", "captureImage: $image")
                        super.onCaptureSuccess(image)
                        val bitmap =
                            image.toBitmap().rotate(image.imageInfo.rotationDegrees.toFloat())
                        image.close()
                        Log.d("CameraX", " Image captured successfully")
                        continuation.resume(bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.d("CameraX", "Photo capture failed: ${exception.message}", exception)
                    }

                }
            )
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File? {
        return try {
            val file = File.createTempFile("photo_", ".jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: IOException) {
            Log.e("CameraX", "Error saving bitmap: ${e.message}", e)
            null
        }


    }

}


