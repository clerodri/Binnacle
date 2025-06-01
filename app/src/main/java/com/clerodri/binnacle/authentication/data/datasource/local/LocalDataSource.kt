package com.clerodri.binnacle.authentication.data.datasource.local

import com.clerodri.binnacle.authentication.data.storage.AuthInformation
import com.clerodri.binnacle.authentication.domain.model.AuthData
import com.clerodri.binnacle.authentication.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val userInformation: AuthInformation) {

    fun getAuthData(): Flow<AuthData?> = userInformation.authData

    suspend fun saveAuthData(auth: AuthData) = userInformation.saveAuthData(auth)


    suspend fun clearAuthData() = userInformation.clearAuthData()



}