package com.clerodri.binnacle.addreport.domain

import javax.inject.Inject

data class AddReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {

    suspend operator fun invoke(report: Report) = reportRepository.addReport(report)
}
