package com.clerodri.binnacle.home.domain.usecase

import com.clerodri.binnacle.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetCheckInStatusUseCase @Inject constructor(private val repository: HomeRepository) {

    suspend operator fun invoke(id: Int) = repository.validateCheckIn(id)
}