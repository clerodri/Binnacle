package com.clerodri.binnacle.home.data

import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.Result
import com.clerodri.binnacle.home.data.datasource.local.HomeDataSource
import com.clerodri.binnacle.home.data.datasource.network.HomeService
import com.clerodri.binnacle.home.domain.model.CheckIn
import com.clerodri.binnacle.home.domain.model.CheckStatus
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Locality
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource,
    private val homeService: HomeService
) : HomeRepository {


    override suspend fun getLocalityInfo(localityId: Int): Locality =
        homeService.getLocalities(localityId)

    override suspend fun getHomeData(): Flow<Home?> = homeDataSource.getHomeInfo()

    override suspend fun saveHomeData(home: Home) = homeDataSource.saveHomeInfo(home)

    override suspend fun clearHomeData() = homeDataSource.clearHomeState()

    override suspend fun makeCheckIn(id: Int): CheckIn = homeService.makeCheckIn(id)


    override suspend fun makeCheckOut(id: Int): Result<Unit, DataError.Check> {
        return try {
            homeService.makeCheckOut(id)
            Result.Success(Unit)
        } catch (e: HttpException) {
            when (e.code()) {
                409 -> Result.Failure(DataError.Check.CONFLICT)
                else -> {
                    Result.Failure(DataError.Check.CONFLICT)
                }
            }
        }
    }


    override suspend fun validateCheckIn(id: Int): Result<CheckStatus, DataError.Check> {
        return try {
            Result.Success(homeService.validateCheckStatus(id))
        } catch (e: HttpException) {
            when (e.code()) {
                409 -> Result.Failure(DataError.Check.CONFLICT)
                else -> {
                    Result.Failure(DataError.Check.CONFLICT)
                }
            }
        }
    }
}