package com.clerodri.binnacle.authentication.data.datasource.network.dto

//data class LoginResponse(
//    val id:Int,
//    val fullname: String,
//    val localityId: Int,
//    val accessToken: String,
//    val refreshToken: String
//)
data class LoginResponse(
    val accessToken: String,
    val role: String,
    val fullname: String,
    val guardId: String,
    val localityId: String
)
