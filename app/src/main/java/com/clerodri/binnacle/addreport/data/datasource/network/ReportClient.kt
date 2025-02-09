package com.clerodri.binnacle.addreport.data.datasource.network

import com.clerodri.binnacle.addreport.data.datasource.network.dto.ReportDto
import com.clerodri.binnacle.addreport.data.datasource.network.dto.ReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportClient {


    @POST("api/v1/round/report")
    suspend fun addReport(@Body report: ReportDto): Response<ReportResponse>
}