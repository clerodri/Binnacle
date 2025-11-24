package com.clerodri.binnacle.addreport.data.datasource.network.dto

data class EventDto(
    val roundId: Int,
    val title: String,
    val detail: String?=null,
    val images: List<String>,
    val imageType:String
) {

}