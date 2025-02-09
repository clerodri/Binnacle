package com.clerodri.binnacle.addreport.domain

import javax.inject.Inject

data class AddReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {

    suspend operator fun invoke(
        content: String,
        imgUrl: String,
        routeId: Int,
        roundId: Int,
        localityId: Int
    ) = reportRepository.addReport(content, imgUrl, routeId, roundId, localityId)
}
