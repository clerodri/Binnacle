package com.clerodri.binnacle.authentication.domain.repository

import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.authentication.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(identification: String): Result<Unit, DataError.AuthNetwork>


    suspend fun getUserData(): Flow<UserData?>


    suspend fun clearUserData()
}