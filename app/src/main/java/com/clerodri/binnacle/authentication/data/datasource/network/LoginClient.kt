package com.clerodri.binnacle.authentication.data.datasource.network

import com.clerodri.binnacle.authentication.data.datasource.network.dto.LoginResponse
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginClient {

    @POST("api/v1/auth/guard-login")
    suspend fun doLoginCall(@Body loginRequest: LoginRequest): Response<LoginResponse>
}

@Serializable
data class LoginRequest(
    private val identification: String,
    private val localityId: String?
)


