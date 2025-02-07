package com.clerodri.binnacle.auth.data.datasource.network.dto

data class LoginResponse(
    val id:Int,
    val fullname: String,
    val localityId: Int,
    val accessToken: String,
    val refreshToken: String
)
