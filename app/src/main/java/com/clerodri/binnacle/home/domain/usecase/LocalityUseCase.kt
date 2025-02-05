package com.clerodri.binnacle.home.domain.usecase

import com.clerodri.binnacle.home.domain.model.Locality
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import javax.inject.Inject

class LocalityUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(localityId: Int): Locality? {
        return homeRepository.getLocalityInfo(localityId)

    }
}