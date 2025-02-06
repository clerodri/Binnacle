package com.clerodri.binnacle.home.domain.usecase

import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeUseCase @Inject constructor(private val repository: HomeRepository) {


    suspend fun getHomeData() = repository.getHomeData()

    suspend fun saveHomeData(home: Home) = repository.saveHomeData(home)

    suspend fun clearHomeData() = repository.clearHomeData()


    suspend fun makeCheckIn(id:Int?) = repository.makeCheckIn(id!!)

    suspend fun makeCheckOut(id:Int?) = repository.makeCheckOut(id!!)

    suspend fun validateCheckStatus(id:Int) = repository.validateCheckIn(id)
}