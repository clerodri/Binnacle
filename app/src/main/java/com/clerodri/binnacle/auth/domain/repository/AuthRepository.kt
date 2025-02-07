package com.clerodri.binnacle.auth.domain.repository

import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.Result
import com.clerodri.binnacle.auth.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(identification: String): Result<Unit, DataError.Network>


    suspend fun getUserData(): Flow<UserData?>


    suspend fun clearUserData()
}