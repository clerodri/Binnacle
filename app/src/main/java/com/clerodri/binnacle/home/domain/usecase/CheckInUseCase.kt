package com.clerodri.binnacle.home.domain.usecase

import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.home.domain.model.CheckIn
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import javax.inject.Inject

class CheckInUseCase @Inject constructor(private val repository: HomeRepository) {


    suspend operator fun invoke(id: Int): Result<CheckIn, DataError.CheckError> =
        repository.makeCheckIn(id)
}