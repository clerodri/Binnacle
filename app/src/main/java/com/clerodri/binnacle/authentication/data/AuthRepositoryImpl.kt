package com.clerodri.binnacle.authentication.data


import android.util.Log
import com.clerodri.binnacle.authentication.data.datasource.local.LocalDataSource
import com.clerodri.binnacle.authentication.data.datasource.network.LoginService
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.authentication.domain.model.UserData
import com.clerodri.binnacle.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val api: LoginService,
    private val localDataSource: LocalDataSource
) : AuthRepository {


    override suspend fun login(identification: String): Result<Unit, DataError.AuthNetwork> {

        return try {
            val response = api.doLogin(identification)
            Log.d("RR", "HttpException $response")

            localDataSource.saveUserData(
                UserData(
                    id = response.id,
                    fullname = response.fullname,
                    localityId = response.localityId,
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    isAuthenticated = true
                )
            )
            Result.Success(Unit)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.Failure(DataError.AuthNetwork.GUARD_NOT_FOUND)
                408 -> Result.Failure(DataError.AuthNetwork.REQUEST_TIMEOUT)
                503 -> Result.Failure(DataError.AuthNetwork.NO_INTERNET)
                else -> {
                    Log.d("RR", "HttpException ${e.code()}")
                    Result.Failure(DataError.AuthNetwork.REQUEST_TIMEOUT)
                }
            }
        } catch (io: IOException) {
            Result.Failure(DataError.AuthNetwork.REQUEST_TIMEOUT)
        }
    }

    override suspend fun getUserData(): Flow<UserData?> {
        return localDataSource.getUserData()
    }

    override suspend fun clearUserData() {
        Log.d("OO", "Auth repository clearUserData")
        localDataSource.clearUserData()
    }
}