package com.clerodri.binnacle.home.domain.usecase

import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.home.domain.model.Locality
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import javax.inject.Inject

class LocalityUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(localityId: String): Result<Locality, DataError.LocalityError> {
        return homeRepository.getRoutes(localityId)


    }

}