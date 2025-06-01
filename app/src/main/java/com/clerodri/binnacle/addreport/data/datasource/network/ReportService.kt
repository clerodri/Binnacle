package com.clerodri.binnacle.addreport.data.datasource.network

import android.util.Log
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventDto
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class ReportService @Inject constructor(
    private val reportClient: ReportClient

) {

    suspend fun addReport(event: EventDto): EventResponse {
        Log.d("CameraX", "report service: $event")

        // Make the API call
        val response = reportClient.addReport(event)

        if (response.isSuccessful) {
            // Get the response body safely
            val responseBody = response.body()
            if (responseBody != null) {
                Log.d("CameraX", "addReport success: $responseBody")
                return responseBody
            } else {
                throw Exception("Empty response body") // Or a more specific exception
            }
        } else {
            // Log the error
            Log.d("CameraX", "addReport error: code=${response.code()} errorBody=${response.errorBody()?.string()}")
            // Throw HttpException so your repository can catch it
            throw HttpException(response)
        }
    }



}