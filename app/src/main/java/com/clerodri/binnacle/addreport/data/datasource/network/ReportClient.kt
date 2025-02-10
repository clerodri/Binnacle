package com.clerodri.binnacle.addreport.data.datasource.network

import com.clerodri.binnacle.addreport.data.datasource.network.dto.ReportResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ReportClient {


    @Multipart
    @POST("api/v1/round/report")
    suspend fun addReport(
        @Part("report") reportDto: RequestBody,
        @Part imageFile: MultipartBody.Part?
    ): Response<ReportResponse>

}