package com.clerodri.binnacle.authentication.domain.usecase

import com.clerodri.binnacle.authentication.domain.model.Locality
import com.clerodri.binnacle.authentication.domain.repository.LocalityRepository
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import jakarta.inject.Inject

/**
 * Author: Ronaldo R.
 * Date:  11/22/2025
 * Description:
 **/
class LoadAvailableLocalitiesUseCase @Inject constructor(
    private val localityRepository: LocalityRepository
) {
    suspend operator fun invoke(): Result<List<Locality>, DataError.LocalityError> =
        localityRepository.findLocalities()
}