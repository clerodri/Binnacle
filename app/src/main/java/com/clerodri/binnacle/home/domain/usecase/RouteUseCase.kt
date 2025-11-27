package com.clerodri.binnacle.home.domain.usecase

import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.domain.repository.HomeRepository
import javax.inject.Inject

class RouteUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(): Result<List<Route>, DataError.LocalityError> {
        return homeRepository.getRoutes()


    }

}