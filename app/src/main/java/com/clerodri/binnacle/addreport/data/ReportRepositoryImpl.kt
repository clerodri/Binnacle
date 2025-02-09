package com.clerodri.binnacle.addreport.data

import com.clerodri.binnacle.addreport.data.datasource.network.ReportService
import com.clerodri.binnacle.addreport.data.datasource.network.dto.ReportDto
import com.clerodri.binnacle.addreport.domain.Report
import com.clerodri.binnacle.addreport.domain.ReportRepository
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import retrofit2.HttpException
import javax.inject.Inject


class ReportRepositoryImpl @Inject constructor(
    private val reportService: ReportService
) : ReportRepository {


    override suspend fun addReport(
        content: String,
        imgUrl: String,
        routeId: Int,
        roundId: Int,
        localityId: Int
    ): Result<Report, DataError.Report> {
//        Log.d("ReportRepositoryImpl", "addReport: $content")
//        Log.d("ReportRepositoryImpl", "addReport: $imgUrl")
//        Log.d("ReportRepositoryImpl", "addReport: $routeId")
//        Log.d("ReportRepositoryImpl", "addReport: $roundId")
//        Log.d("ReportRepositoryImpl", "addReport: $localityId")
        return try {
            val result =
                reportService.addReport(ReportDto(content, imgUrl, routeId, roundId, localityId))
                    .body()
            Result.Success(Report(result?.id!!, result.createdAt))
        } catch (e: HttpException) {
            when (e.code()) {
                408 -> Result.Failure(DataError.Report.REQUEST_TIMEOUT)
                else -> Result.Failure(DataError.Report.NO_INTERNET)
            }
        }
    }
}


