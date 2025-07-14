package com.clerodri.binnacle.home.data.datasource.network.dto

import com.clerodri.binnacle.home.domain.model.Route

data class RouteResponse(
    val id: Long,
    val name: String,
    val order: Int
)

fun RouteResponse.toDomain() = Route(id, name, order)