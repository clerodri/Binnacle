package com.clerodri.binnacle.addreport.domain

import androidx.camera.core.ImageCapture
import javax.inject.Inject

class TakePhotoUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {

    suspend operator fun invoke(image: ImageCapture) = reportRepository.captureImage(image)
}