package com.clerodri.binnacle.home.data.datasource.network

import com.clerodri.binnacle.home.data.datasource.network.dto.CheckInDto
import com.clerodri.binnacle.home.data.datasource.network.dto.CheckInResponse
import com.clerodri.binnacle.home.data.datasource.network.dto.CheckOutDto
import com.clerodri.binnacle.home.data.datasource.network.dto.LocalityResponse
import com.clerodri.binnacle.home.domain.model.CheckStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HomeClient {

    @GET("api/v1/localities/{id}")
    suspend fun getLocality(@Path("id") localityId: Int): Response<LocalityResponse>

    @POST("api/v1/check/check-in")
    suspend fun makeCheckIn(@Body checkInDto: CheckInDto): Response<CheckInResponse>

    @POST("api/v1/check/check-out")
    suspend fun makeCheckOut(@Body checkOutDto: CheckOutDto): Response<Unit>

    @GET("api/v1/check/{id}")
    suspend fun validateCheckStatus(@Path("id") id: Int): Response<CheckStatus>
}