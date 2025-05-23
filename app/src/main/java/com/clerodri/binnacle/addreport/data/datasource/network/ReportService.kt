package com.clerodri.binnacle.addreport.data.datasource.network

import android.util.Log
import com.clerodri.binnacle.addreport.data.datasource.network.dto.ReportDto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class ReportService @Inject constructor(
    private val reportClient: ReportClient

) {

    suspend fun addReport(report: ReportDto, imageFile: File?) {
        Log.d("CameraX", "report service: $report")
        return withContext(Dispatchers.IO) {
            val gson = Gson()
            val reportJson = gson.toJson(report)
            val reportRequestBody =
                RequestBody.create(MediaType.parse("application/json"), reportJson)

            val imageFile = imageFile?.let {
                val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), it)
                MultipartBody.Part.createFormData("imageFile", it.name, requestFile)
            }

            val result = reportClient.addReport(reportRequestBody, imageFile)
            if (result.isSuccessful) {
                Log.d("CameraX", "addReport: $result")
            } else {
                Log.d("CameraX", "ERROR: $result")
            }
        }
    }

}