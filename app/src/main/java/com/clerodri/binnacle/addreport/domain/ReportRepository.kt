package com.clerodri.binnacle.addreport.domain

import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import com.clerodri.binnacle.addreport.data.datasource.network.dto.AddReportResponse
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result

interface ReportRepository {
    suspend fun addReport(report: Report): Result<AddReportResponse, DataError.Report>
    suspend fun captureImage(imageCapture: ImageCapture): Bitmap?
    suspend fun uploadPhoto(preSignedUrl: String?, bitmap: Bitmap): Result<Unit, DataError.Report>
}