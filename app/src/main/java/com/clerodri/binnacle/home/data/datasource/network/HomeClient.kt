package com.clerodri.binnacle.home.data.datasource.network

import com.clerodri.binnacle.home.data.datasource.network.dto.CheckInDto
import com.clerodri.binnacle.home.data.datasource.network.dto.CheckInResponse
import com.clerodri.binnacle.home.data.datasource.network.dto.CheckOutDto
import com.clerodri.binnacle.home.data.datasource.network.dto.LocalityResponse
import com.clerodri.binnacle.home.data.datasource.network.dto.RoundDto
import com.clerodri.binnacle.home.data.datasource.network.dto.RoundResponse
import com.clerodri.binnacle.home.domain.model.CheckStatus
import com.clerodri.binnacle.home.domain.model.ECheckIn
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HomeClient {

    @GET("api/v1/locality/{id}/routes")
    suspend fun getRoutes(@Path("id") localityId: String): Response<LocalityResponse>

    @POST("api/v1/check")
    suspend fun makeCheckIn(@Body checkInDto: CheckInDto): Response<CheckInResponse>

    @PATCH("api/v1/check/{id}")
    suspend fun makeCheckOut(@Path("id") id: Int): Response<Unit>

    @GET("api/v1/check/{id}")
    suspend fun validateCheckStatus(@Path("id") id: Int): Response<ECheckIn>



    @POST("api/v1/round")
    suspend fun startRound(@Body roundDto: RoundDto): Response<RoundResponse>

    @PUT("api/v1/round/{id}/finish")
    suspend fun stopRound(@Path("id") id: Long): Response<Unit>

    @GET("api/v1/auth/ping")
    suspend fun pingServer(): Response<Unit>
}