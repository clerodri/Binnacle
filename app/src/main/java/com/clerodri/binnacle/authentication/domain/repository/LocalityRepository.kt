package com.clerodri.binnacle.authentication.domain.repository

import com.clerodri.binnacle.authentication.domain.model.Locality
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result

/**
 * Author: Ronaldo R.
 * Date:  11/22/2025
 * Description:
 **/
interface LocalityRepository {
    suspend fun findLocalities(): Result<List<Locality>, DataError.LocalityError>
}