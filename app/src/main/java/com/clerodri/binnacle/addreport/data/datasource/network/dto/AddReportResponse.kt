package com.clerodri.binnacle.addreport.data.datasource.network.dto

data class AddReportResponse(
    val signedImages: Map<String, String>
){
    val hasImages: Boolean get() = signedImages.isNotEmpty()
    val imageCount: Int get() = signedImages.size
    fun getSignedUrlFor(filename: String): String? = signedImages[filename]
}