package com.clerodri.binnacle.home.data

import com.clerodri.binnacle.home.data.datasource.local.HomeDataSource
import com.clerodri.binnacle.home.data.datasource.network.HomeService
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Locality
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource,
    private val homeService: HomeService
) : HomeRepository {
    override suspend fun getLocalityInfo(localityId: Int):Locality? {
       return homeService.getLocalities(localityId)
    }

    override suspend fun getHomeData(): Flow<Home?> {
        return homeDataSource.getHomeInfo()
    }

    override suspend fun saveHomeData(home: Home) {
        homeDataSource.saveHomeInfo(home)
    }

    override suspend fun clearHomeData() {
        homeDataSource.clearHomeState()
    }
}