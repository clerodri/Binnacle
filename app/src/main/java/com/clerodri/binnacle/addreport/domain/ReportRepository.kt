package com.clerodri.binnacle.addreport.domain

import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventResponse
import com.clerodri.binnacle.addreport.ui.PhotoItem
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result

interface ReportRepository {


    suspend fun addReport(report: Report): Result<Unit, DataError.Report>
    suspend fun captureImage(imageCapture: ImageCapture): Bitmap?
    suspend fun uploadPhotos(photos:  List<PhotoItem>, result:  List<EventResponse>): Result<Unit, DataError.Report>
}