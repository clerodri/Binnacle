package com.clerodri.binnacle.home.data.datasource.network


import com.clerodri.binnacle.core.di.ApiRutas.extractDataOrThrow
import com.clerodri.binnacle.core.domain.ApiResponse
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

        try {
            val response = homeClient.getRoutes()


            val routeResponses = extractDataOrThrow(response, "fetchRoutes")

            val routes = routeResponses.map { it.toDomain() }
            routeDao.insertAll(routes.map { it.toEntity() })
            routes
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun makeCheckIn(id: Int): CheckIn {
        return withContext(Dispatchers.IO) {
            try {
                val response = homeClient.makeCheckIn(CheckInDto(id, "test", "test"))

                val checkInResponse = extractDataOrThrow(response, "makeCheckIn")

                CheckIn(checkInResponse.id, checkInResponse.status)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun validateCheckStatus(id: Int): ECheckIn {
        return withContext(Dispatchers.IO) {
            try {
                val response = homeClient.validateCheckStatus(id)

                val eCheckIn = extractDataOrThrow(response, "validateCheckStatus")

                eCheckIn
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun makeCheckOut(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val response = homeClient.makeCheckOut(id)
                extractDataOrThrow(response, "makeCheckOut")
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun startRound(guardId: String?, localityId: String?): Round {
        return withContext(Dispatchers.IO) {
            try {
                val response = homeClient.startRound(RoundDto(guardId, localityId))

                val roundResponse = extractDataOrThrow(response, "startRound")

                Round(roundResponse.id)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun stopRound(roundId: Long) {
        withContext(Dispatchers.IO) {
            try {
                val response = homeClient.stopRound(roundId)
                val message = extractDataOrThrow(response, "stopRound")
                message
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun pingServer(): Response<ApiResponse<String>> {
        return withContext(Dispatchers.IO) {
            val response = homeClient.pingServer()
            response
        }
    }


}

