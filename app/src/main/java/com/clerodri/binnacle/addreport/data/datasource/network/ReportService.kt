package com.clerodri.binnacle.addreport.data.datasource.network

import android.graphics.Bitmap
import android.util.Log
import com.clerodri.binnacle.addreport.data.datasource.network.dto.AddReportResponse
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventDto
import com.clerodri.binnacle.core.di.ApiRutas
import com.clerodri.binnacle.core.di.ApiRutas.extractDataOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ReportService @Inject constructor(
    private val reportClient: ReportClient

) {

    companion object {
        const val TAG = "ReportService"
        const val JPEG_QUALITY = 90
        const val JPEG_MIME_TYPE = "image/jpeg"
    }

    suspend fun addReport(event: EventDto): AddReportResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = reportClient.addReport(event)

                val message = extractDataOrThrow(response, ApiRutas.REPORT.ADD_REPORT)
                Log.d(TAG, "addReport: $message")
                message
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun prepareS3UploadRequest(
        preSignedUrl: String?,
        bitmap: Bitmap
    ): Request? {
        if (preSignedUrl.isNullOrBlank()) {
            Log.e(TAG, "Presigned URL es null o vacío")
            return null
        }

        if (bitmap.width == 0 || bitmap.height == 0) {
            Log.e(TAG, "Bitmap inválido - width: ${bitmap.width}, height: ${bitmap.height}")
            return null
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
        val byteArray = stream.toByteArray()

        Log.d(TAG, "Bitmap comprimido: ${byteArray.size / 1024} KB")

        // Paso 2: Crear RequestBody
        val mediaType = JPEG_MIME_TYPE.toMediaType()
        val requestBody = byteArray.toRequestBody(mediaType)

        //Paso 3: Construir Request
        val request = Request.Builder()
            .url(preSignedUrl)
            .put(requestBody)
            .build()

        Log.d(TAG, "Request construido:")
        Log.d(TAG, "   URL: $preSignedUrl")
        Log.d(TAG, "   Method: ${request.method}")
        Log.d(TAG, "   Body size: ${byteArray.size} bytes")
        Log.d(TAG, "   Headers: ${request.headers.size}")

        return request
    }


}