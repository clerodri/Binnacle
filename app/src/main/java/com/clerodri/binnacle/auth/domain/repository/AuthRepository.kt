package com.clerodri.binnacle.auth.domain.repository

import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.model.Guard
import com.clerodri.binnacle.auth.domain.Result

interface AuthRepository {

    suspend fun login(identification: String): Result<Guard, DataError.Network>
}