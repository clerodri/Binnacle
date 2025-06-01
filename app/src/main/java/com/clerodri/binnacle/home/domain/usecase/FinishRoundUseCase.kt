package com.clerodri.binnacle.home.domain.usecase

import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import javax.inject.Inject

class FinishRoundUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {


    suspend operator fun invoke(roundId: Long): Result<Unit, DataError.Network> =
        homeRepository.stopRound(roundId)
}