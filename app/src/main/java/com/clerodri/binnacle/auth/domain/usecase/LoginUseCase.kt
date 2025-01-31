package com.clerodri.binnacle.auth.domain.usecase

import com.clerodri.binnacle.auth.domain.model.DataError
import com.clerodri.binnacle.auth.domain.model.Guard
import com.clerodri.binnacle.auth.domain.model.Result
import com.clerodri.binnacle.auth.domain.repository.AuthRepository
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(
   private val repository: AuthRepository
) {

    suspend fun login(identification: String): Result<Guard, DataError.Network> {
        return repository.login(identification)
    }


}