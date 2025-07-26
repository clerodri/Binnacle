package com.clerodri.binnacle.addreport.domain

import com.clerodri.binnacle.addreport.ui.PhotoItem


data class Report(
    val title: String,
    val description: String? = null,
    val roundId: Int,
    val images: List<PhotoItem>
)
