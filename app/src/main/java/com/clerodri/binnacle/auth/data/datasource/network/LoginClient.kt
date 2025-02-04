package com.clerodri.binnacle.auth.data.datasource.network

import com.clerodri.binnacle.auth.data.datasource.network.dto.LoginResponse
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginClient {

    @POST("auth/guard/login")
    suspend fun doLoginCall(@Body loginRequest: LoginRequest): Response<LoginResponse>
}

@Serializable
data class LoginRequest(
    private val identification: String
)


