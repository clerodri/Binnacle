package com.clerodri.binnacle.auth.domain.model

data class Guard(
    val id: String?,
    val fullname:String?,
    val localityId:String?,
    val accessToken:String?,
    val refreshToken:String?
)
