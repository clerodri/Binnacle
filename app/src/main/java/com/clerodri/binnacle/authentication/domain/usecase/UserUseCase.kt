package com.clerodri.binnacle.authentication.domain.usecase

import com.clerodri.binnacle.authentication.domain.repository.AuthRepository
import javax.inject.Inject

class UserUseCase @Inject constructor(private val repository: AuthRepository) {


    suspend fun getUserData() = repository.getUserData()

    suspend fun clearUserData() = repository.clearUserData()

}