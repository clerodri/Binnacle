package com.clerodri.binnacle.auth.data.datasource.network.dto

data class LoginResponse(
    val id:String,
    val fullname: String,
    val localityId: String,
    val accessToken: String,
    val refreshToken: String
)
