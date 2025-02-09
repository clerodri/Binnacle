package com.clerodri.binnacle.addreport.data.datasource.network

import android.util.Log
import com.clerodri.binnacle.addreport.data.datasource.network.dto.ReportDto
import com.clerodri.binnacle.addreport.data.datasource.network.dto.ReportResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class ReportService @Inject constructor(
    private val reportClient: ReportClient

) {

    suspend fun addReport(report: ReportDto): Response<ReportResponse> {
        return withContext(Dispatchers.IO) {
           val result = reportClient.addReport(report)
            Log.d("ReportService", "addReport: $result")
            result
        }
    }

}