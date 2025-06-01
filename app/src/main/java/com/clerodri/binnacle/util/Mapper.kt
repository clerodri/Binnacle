package com.clerodri.binnacle.util

import com.clerodri.binnacle.home.data.datasource.network.dto.RoundResponse
import com.clerodri.binnacle.home.domain.model.Round


fun RoundResponse.toModel() = Round(
    id = id,
//    startedTime = startedTime,
//    status = status
)
