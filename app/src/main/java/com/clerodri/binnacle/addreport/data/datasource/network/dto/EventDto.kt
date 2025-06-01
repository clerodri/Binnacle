package com.clerodri.binnacle.addreport.data.datasource.network.dto

data class EventDto(
    val title: String,
    val detail: String?=null,
    val roundId: Int,
    val image:String?,
    val imageType:String
) {

}