package com.clerodri.binnacle.home.domain.usecase

import com.clerodri.binnacle.home.domain.repository.HomeRepository
import javax.inject.Inject

class CreateRoundUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator  fun invoke(guardId: String) = homeRepository.startRound(guardId);
}