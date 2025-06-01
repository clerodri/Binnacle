package com.clerodri.binnacle.addreport.data.datasource.network

import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventDto
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ReportClient {


//    @Multipart
//    @POST("api/v1/round/event")
//    suspend fun addReport(
//        @Part("report") reportDto: RequestBody,
//        @Part imageFile: MultipartBody.Part?
//    ): Response<ReportResponse>

    @POST("api/v1/round/event")
    suspend fun addReport(@Body eventDto: EventDto): Response<EventResponse>

}