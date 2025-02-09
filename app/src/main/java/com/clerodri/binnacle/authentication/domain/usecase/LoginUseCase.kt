package com.clerodri.binnacle.authentication.domain.usecase

import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.authentication.domain.repository.AuthRepository
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(
   private val repository: AuthRepository
) {



    suspend operator  fun invoke(identification: String): Result<Unit, DataError.AuthNetwork> {
        return repository.login(identification)
    }


}