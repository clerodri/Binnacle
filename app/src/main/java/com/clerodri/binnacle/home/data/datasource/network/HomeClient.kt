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
import retrofit2.http.Path

interface HomeClient {

    @GET("api/v1/localities/{id}")
    suspend fun getLocality(@Path("id") localityId: Int): Response<LocalityResponse>

    @POST("api/v1/check")
    suspend fun makeCheckIn(@Body checkInDto: CheckInDto): Response<CheckInResponse>

    @PATCH("api/v1/check/{id}")
    suspend fun makeCheckOut(@Path("id") id: Int): Response<Unit>

    @GET("api/v1/check/{id}")
    suspend fun validateCheckStatus(@Path("id") id: Int): Response<ECheckIn>

    @POST("api/v1/round")
    suspend fun startRound(@Body roundDto: RoundDto): Response<RoundResponse>

    @PATCH("api/v1/round/{id}")
    suspend fun stopRound(@Path("id") id: Int): Response<Unit>
}