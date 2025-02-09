package com.clerodri.binnacle.home.data.datasource.network.dto

import com.clerodri.binnacle.home.domain.model.ECheckIn

data class CheckInResponse(
    val id: Int,
    val status: ECheckIn
)
