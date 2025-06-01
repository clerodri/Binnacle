package com.clerodri.binnacle.authentication.domain.model

data class UserData(
    val id: Int?,
    val fullname: String?,
    val localityId: Int?,
    val accessToken: String?,
   // val refreshToken: String?,
    val isAuthenticated: Boolean = false
)
