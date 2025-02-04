package com.clerodri.binnacle.auth.domain.usecase

import com.clerodri.binnacle.auth.domain.repository.AuthRepository
import javax.inject.Inject

class UserUseCase @Inject constructor(private val repository: AuthRepository) {


    suspend fun getUserData() = repository.getUserData()

    suspend fun clearUserData() = repository.clearUserData()

}