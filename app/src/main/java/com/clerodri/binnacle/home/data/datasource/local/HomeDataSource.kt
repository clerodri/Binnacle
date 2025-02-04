package com.clerodri.binnacle.home.data.datasource.local

import com.clerodri.binnacle.home.data.storage.HomeInformation
import com.clerodri.binnacle.home.domain.model.Home
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeDataSource @Inject constructor(
    private val homeInfo: HomeInformation
) {

    fun getHomeInfo(): Flow<Home?> = homeInfo.homeData


    suspend fun saveHomeInfo(home: Home) = homeInfo.saveHomeState(home)


    suspend fun clearHomeState() = homeInfo.clearHomeState()
}