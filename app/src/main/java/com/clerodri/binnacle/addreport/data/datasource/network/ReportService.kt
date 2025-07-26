package com.clerodri.binnacle.addreport.data.datasource.network

import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventDto
import com.clerodri.binnacle.addreport.data.datasource.network.dto.EventResponse
import retrofit2.HttpException
import javax.inject.Inject

class ReportService @Inject constructor(
    private val reportClient: ReportClient

) {

    suspend fun addReport(event: EventDto): List<EventResponse> {
        val response = reportClient.addReport(event)
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {

                return responseBody
            } else {
                throw Exception("Empty response body")
            }
        } else {
            throw HttpException(response)
        }
    }


}