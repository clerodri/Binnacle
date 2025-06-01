package com.clerodri.binnacle.authentication.domain.model

data class AuthData(
    val accessToken: String?,
    val role:String?,
    val isAuthenticated: Boolean = false,
    val fullName: String?,
    val guardId:String?,
    val localityId:String?
)
