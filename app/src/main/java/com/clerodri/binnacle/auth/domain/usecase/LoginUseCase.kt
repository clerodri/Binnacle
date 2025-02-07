package com.clerodri.binnacle.auth.domain.usecase

import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.Result
import com.clerodri.binnacle.auth.domain.repository.AuthRepository
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(
   private val repository: AuthRepository
) {



    suspend operator  fun invoke(identification: String): Result<Unit, DataError.Network> {
        return repository.login(identification)
    }


}