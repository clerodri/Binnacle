package com.clerodri.binnacle.auth.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("auth/guard/login")
    suspend fun doLoginCall(@Body loginRequest: LoginRequest): LoginResponse
}

data class LoginResponse(
    private val accessToken:String,
    private val refreshToken:String
)

data class LoginRequest(
    private val identification:String
)