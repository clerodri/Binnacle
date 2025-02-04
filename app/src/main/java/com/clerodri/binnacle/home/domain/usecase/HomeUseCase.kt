package com.clerodri.binnacle.home.domain.usecase

import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeUseCase @Inject constructor(private val repository: HomeRepository) {


    suspend fun getHomeData() = repository.getHomeData()

    suspend fun saveHomeData(home: Home) = repository.saveHomeData(home)

    suspend fun clearHomeData() = repository.clearHomeData()
}