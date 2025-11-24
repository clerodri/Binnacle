package com.clerodri.binnacle.addreport.domain

import android.graphics.Bitmap
import javax.inject.Inject

class UploadPhotoUseCase @Inject constructor(
    private val reportRepository: ReportRepository
){
    suspend operator fun invoke(preSignedUrl: AddReportResponse, bitmap: Bitmap) = reportRepository.uploadPhoto(preSignedUrl, bitmap)
}