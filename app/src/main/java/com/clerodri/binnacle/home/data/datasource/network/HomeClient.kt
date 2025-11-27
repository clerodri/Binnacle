package com.clerodri.binnacle.home.data.datasource.network

import com.clerodri.binnacle.core.di.ApiRutas
import com.clerodri.binnacle.core.domain.ApiResponse
import com.clerodri.binnacle.home.data.datasource.network.dto.CheckInDto
import com.clerodri.binnacle.home.data.datasource.network.dto.CheckInResponse
import com.clerodri.binnacle.home.data.datasource.network.dto.RoundDto
import com.clerodri.binnacle.home.data.datasource.network.dto.RoundResponse
import com.clerodri.binnacle.home.data.datasource.network.dto.RouteResponse
import com.clerodri.binnacle.home.domain.model.ECheckIn
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HomeClient {


    @GET( "api/v1/route")
    suspend fun getRoutes(): Response<ApiResponse<List<RouteResponse>>>


    @POST(ApiRutas.HOME.CHECK_IN)
    suspend fun makeCheckIn(@Body checkInDto: CheckInDto): Response<ApiResponse<CheckInResponse>>


    @PATCH(ApiRutas.HOME.CHECK_OUT)
    suspend fun makeCheckOut(@Path("id") id: Int): Response<ApiResponse<String>>


    @GET(ApiRutas.HOME.VALIDATE_CHECK)
    suspend fun validateCheckStatus(@Path("id") id: Int): Response<ApiResponse<ECheckIn>>


    @POST(ApiRutas.HOME.START_ROUND)
    suspend fun startRound(@Body roundDto: RoundDto): Response<ApiResponse<RoundResponse>>


    @PUT(ApiRutas.HOME.STOP_ROUND)
    suspend fun stopRound(@Path("id") id: Long): Response<ApiResponse<String>>


    @GET(ApiRutas.HOME.PING_SERVER)
    suspend fun pingServer(): Response<ApiResponse<String>>
}