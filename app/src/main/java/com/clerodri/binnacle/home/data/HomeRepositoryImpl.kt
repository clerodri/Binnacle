package com.clerodri.binnacle.home.data

import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.home.data.datasource.local.HomeDataSource
import com.clerodri.binnacle.home.data.datasource.network.HomeService
import com.clerodri.binnacle.home.domain.model.CheckIn
import com.clerodri.binnacle.home.domain.model.ECheckIn
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Round
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource,
    private val homeService: HomeService,
) : HomeRepository {

    override suspend fun getHomeData(): Flow<Home?> = homeDataSource.getHomeInfo()

    override suspend fun saveHomeData(home: Home) = homeDataSource.saveHomeInfo(home)

    override suspend fun clearHomeData() = homeDataSource.clearHomeState()


    override suspend fun getRoutes(): Result<List<Route>, DataError.LocalityError> {
        return try {
            val result = homeService.fetchRoutes()
            Result.Success(result)
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> Result.Failure(DataError.LocalityError.ROUTES_NOT_FOUND)
                408 -> Result.Failure(DataError.LocalityError.SERVICE_UNAVAILABLE)
                else -> Result.Failure(DataError.LocalityError.SERVICE_UNAVAILABLE)
            }
        } catch (e: SocketTimeoutException) {
            Result.Failure(DataError.LocalityError.SERVICE_UNAVAILABLE)
        } catch (e: IOException) {
            Result.Failure(DataError.LocalityError.SERVICE_UNAVAILABLE)
        }
    }


    override suspend fun makeCheckIn(id: Int): Result<CheckIn, DataError.CheckError> {
        return try {
            Result.Success(homeService.makeCheckIn(id))
        } catch (e: HttpException) {
            when (e.code()) {
                409 -> Result.Failure(DataError.CheckError.GUARD_NOT_FOUND)
                408 -> Result.Failure(DataError.CheckError.REQUEST_TIMEOUT)
                else -> Result.Failure(DataError.CheckError.NO_INTERNET)
            }
        }

    }


    override suspend fun makeCheckOut(id: Int): Result<Unit, DataError.CheckError> {
        return try {
            Result.Success(homeService.makeCheckOut(id))
        } catch (e: HttpException) {
            when (e.code()) {
                409 -> Result.Failure(DataError.CheckError.GUARD_NOT_FOUND)
                408 -> Result.Failure(DataError.CheckError.REQUEST_TIMEOUT)
                else -> Result.Failure(DataError.CheckError.NO_INTERNET)
            }
        }
    }


    override suspend fun validateCheckIn(id: Int): Result<ECheckIn, DataError.CheckError> {
        return try {
            Result.Success(homeService.validateCheckStatus(id))
        } catch (e: HttpException) {
            when (e.code()) {
                409 -> Result.Failure(DataError.CheckError.GUARD_NOT_FOUND)
                408 -> Result.Failure(DataError.CheckError.REQUEST_TIMEOUT)
                else -> Result.Failure(DataError.CheckError.NO_INTERNET)
            }
        }
    }

    override suspend fun startRound(guardId: String?, localityId: String?): Result<Round, DataError.Network> {
        return try {
            Result.Success(homeService.startRound(guardId, localityId))
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> Result.Failure(DataError.Network.GUARD_NOT_FOUND)
                408 -> Result.Failure(DataError.Network.REQUEST_TIMEOUT)
                409 -> Result.Failure(DataError.Network.CONFLICT)
                else -> Result.Failure(DataError.Network.NO_INTERNET)
            }
        }
    }

    override suspend fun stopRound(roundId: Long): Result<Unit, DataError.Network> {
        return try {
            Result.Success(homeService.stopRound(roundId))
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> Result.Failure(DataError.Network.ROUND_NOT_FOUND)
                408 -> Result.Failure(DataError.Network.REQUEST_TIMEOUT)
                else -> Result.Failure(DataError.Network.NO_INTERNET)
            }
        }
    }


    override suspend fun validateSession(): Result<Unit, DataError> {
        return try {
            val response = homeService.pingServer()
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Failure(DataError.AuthNetwork.SERVICE_UNAVAILABLE)
            }
        } catch (e: IOException) {
            Result.Failure(DataError.AuthNetwork.SERVICE_UNAVAILABLE)
        }
    }
}