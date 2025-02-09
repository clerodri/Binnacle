package com.clerodri.binnacle.home.data.datasource.network

import android.util.Log
import com.clerodri.binnacle.home.data.datasource.network.dto.CheckInDto
import com.clerodri.binnacle.home.data.datasource.network.dto.RoundDto
import com.clerodri.binnacle.home.domain.model.CheckIn
import com.clerodri.binnacle.home.domain.model.ECheckIn
import com.clerodri.binnacle.home.domain.model.Locality
import com.clerodri.binnacle.home.domain.model.Round
import com.clerodri.binnacle.home.domain.model.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeService @Inject constructor(
    private val homeClient: HomeClient
) {


    suspend fun getLocalities(localityId: Int): Locality {
        return withContext(Dispatchers.IO) {
            val response = homeClient.getLocality(localityId).body()
            Log.d("RR", "HomeService getLocalities called $response")
            val routes = response?.routes?.map {
                Route(it.id, it.localityId, it.name)
            }
            response?.let { Locality(it.id, response.name, routes) }!!
        }

    }


    suspend fun makeCheckIn(id: Int): CheckIn {
        return withContext(Dispatchers.IO) {
            val response = homeClient.makeCheckIn(CheckInDto(id, "test", "test")).body()
            Log.d("RR", "HomeService makeCheckIn called $response")
            CheckIn(response?.id!!, response.status)
        }
    }

    suspend fun validateCheckStatus(id: Int): ECheckIn {
        return withContext(Dispatchers.IO) {
            val response = homeClient.validateCheckStatus(id).body()
            Log.d("RR", "HomeService validateCheckStatus called $response")
            response!!
        }
    }

    suspend fun makeCheckOut(id: Int) {
        withContext(Dispatchers.IO) {
            Log.d("RR", "HomeService makeCheckOut called")
            homeClient.makeCheckOut(id)
        }
    }

    suspend fun startRound(guardId: Int): Round {
        return withContext(Dispatchers.IO) {
            val response = homeClient.startRound(RoundDto(guardId)).body()
            Log.d("RR", "HomeService startRound $response")
            Round(response?.id!!, response.startedTime, response.status)
        }
    }

    suspend fun stopRound(roundId: Int) {
        withContext(Dispatchers.IO) {
            Log.d("RR", "HomeService stopRound ")
            homeClient.stopRound(roundId)
        }
    }
}