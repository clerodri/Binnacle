package com.clerodri.binnacle.addreport.domain


data class Report(
    val title: String,
    val description: String? = null,
    val roundId: Int,
    val image: String?,
    val imageType: String,
)
