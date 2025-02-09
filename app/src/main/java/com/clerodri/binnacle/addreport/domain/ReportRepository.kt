package com.clerodri.binnacle.addreport.domain

import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.core.DataError

interface ReportRepository {


    suspend fun addReport(
        content:String,
        imgUrl:String,
        routeId:Int,
        roundId:Int,
        localityId:Int
    ): Result<Report, DataError.Report>

}