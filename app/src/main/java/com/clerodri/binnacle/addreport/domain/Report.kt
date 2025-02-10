package com.clerodri.binnacle.addreport.domain

import android.graphics.Bitmap

data class Report(
    val title:String,
    val description:String,
    val routeId:Int,
    val roundId:Int,
    val localityId:Int,
    val imageBitmap: Bitmap? = null
)
