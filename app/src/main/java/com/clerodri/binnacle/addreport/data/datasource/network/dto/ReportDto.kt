package com.clerodri.binnacle.addreport.data.datasource.network.dto

data class ReportDto(
    val title: String,
    val description: String,
    val routeId: Int,
    val roundId: Int,
    val localityId: Int,
) {
//    fun toDomain(): Report {
//        return Report(
//            content = content,
//            imgUrl = imgUrl,
//            routeId = routeId,
//            roundId = roundId,
//    }
}