package com.clerodri.binnacle.addreport.data.datasource.network

import com.clerodri.binnacle.addreport.data.datasource.network.dto.AddReportResponse
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventDto
import com.clerodri.binnacle.core.di.ApiRutas
import com.clerodri.binnacle.core.domain.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ReportClient {


    @POST(ApiRutas.REPORT.ADD_REPORT)
    suspend fun addReport(@Body eventDto: EventDto): Response<ApiResponse<AddReportResponse>>

}