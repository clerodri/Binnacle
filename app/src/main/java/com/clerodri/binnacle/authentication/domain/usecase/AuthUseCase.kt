package com.clerodri.binnacle.authentication.domain.usecase

import com.clerodri.binnacle.authentication.domain.repository.AuthRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val repository: AuthRepository)  {

    suspend fun getAuthData() = repository.getAuthData()

    suspend fun clearAuthData() = repository.clearAuthData()
}