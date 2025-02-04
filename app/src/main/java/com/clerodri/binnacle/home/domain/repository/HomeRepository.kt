package com.clerodri.binnacle.home.domain.repository

import com.clerodri.binnacle.home.domain.model.Home
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getLocalityInfo()

    suspend fun getHomeData(): Flow<Home?>

    suspend fun saveHomeData(home: Home)

    suspend fun clearHomeData()
}