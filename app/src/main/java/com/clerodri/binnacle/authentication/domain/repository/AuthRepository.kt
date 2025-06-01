package com.clerodri.binnacle.authentication.domain.repository

import android.content.Context
import com.clerodri.binnacle.authentication.domain.model.AuthData
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(identification: String): Result<Unit, DataError.AuthNetwork>

    suspend fun getAuthData(): Flow<AuthData?>

    suspend fun clearAuthData()
}