package com.clerodri.binnacle.authentication.data.datasource.network.dto


data class LoginResponse(
    val accessToken: String,
    val role: String,
    val guardId: String,
    val fullname: String,
)
