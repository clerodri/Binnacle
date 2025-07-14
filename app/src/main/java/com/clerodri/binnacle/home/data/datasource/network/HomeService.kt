package com.clerodri.binnacle.home.data.datasource.network

import com.clerodri.binnacle.home.data.datasource.local.RouteDao
import com.clerodri.binnacle.home.data.datasource.local.toDomain
import com.clerodri.binnacle.home.data.datasource.local.toEntity
import com.clerodri.binnacle.home.data.datasource.network.dto.CheckInDto
import com.clerodri.binnacle.home.data.datasource.network.dto.RoundDto
import com.clerodri.binnacle.home.data.datasource.network.dto.toDomain
import com.clerodri.binnacle.home.domain.model.CheckIn
import com.clerodri.binnacle.home.domain.model.ECheckIn
import com.clerodri.binnacle.home.domain.model.Round
import com.clerodri.binnacle.home.domain.model.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class HomeService @Inject constructor(
    private val homeClient: HomeClient,
    private val routeDao: RouteDao,
) {


    suspend fun fetchRoutes(): List<Route> = withContext(Dispatchers.IO) {
        val cached = routeDao.getAll()
        if (cached.isNotEmpty()) {
            return@withContext cached.map { it.toDomain() }
        }

        val response = homeClient.getRoutes().body()
            ?: throw IllegalStateException("Response body is null")

        val routes = response.map { it.toDomain() }

        routeDao.insertAll(routes.map { it.toEntity() })
        routes
    }


    suspend fun makeCheckIn(id: Int): CheckIn {
        return withContext(Dispatchers.IO) {
            val response = homeClient.makeCheckIn(CheckInDto(id, "test", "test")).body()
            CheckIn(response?.id!!, response.status)
        }
    }

    suspend fun validateCheckStatus(id: Int): ECheckIn {
        return withContext(Dispatchers.IO) {
            val response = homeClient.validateCheckStatus(id).body()
            response!!
        }
    }

    suspend fun makeCheckOut(id: Int) {
        withContext(Dispatchers.IO) {
            homeClient.makeCheckOut(id)
        }
    }

    suspend fun startRound(guardId: String): Round {
        return withContext(Dispatchers.IO) {
            val response = homeClient.startRound(RoundDto(guardId)).body()
            Round(response?.id!!)
        }
    }

    suspend fun stopRound(roundId: Long) {
        withContext(Dispatchers.IO) {
            homeClient.stopRound(roundId)
        }
    }

    suspend fun pingServer(): Response<Unit> {
        return withContext(Dispatchers.IO) {
            homeClient.pingServer()
        }
    }
}