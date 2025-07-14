package com.clerodri.binnacle.authentication.data


import com.clerodri.binnacle.authentication.data.datasource.local.LocalDataSource
import com.clerodri.binnacle.authentication.data.datasource.network.LoginService
import com.clerodri.binnacle.authentication.domain.model.AuthData
import com.clerodri.binnacle.authentication.domain.repository.AuthRepository
import com.clerodri.binnacle.core.DataError.*
import com.clerodri.binnacle.core.Result
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


internal class AuthRepositoryImpl @Inject constructor(
    private val api: LoginService,
    private val localDataSource: LocalDataSource
) : AuthRepository {


    override suspend fun login(identification: String): Result<Unit, AuthNetwork> {

        return try {
            val response = api.doLogin(identification)


            localDataSource.saveAuthData(
                AuthData(
                    accessToken = response.accessToken,
                    role = response.role,
                    isAuthenticated = true,
                    fullName = response.fullname,
                    guardId = response.guardId
                )
            )
            Result.Success(Unit)
        } catch (e: HttpException) {
            println("TEST: ${e.code()}")
            when (e.code()) {
                404 -> Result.Failure(AuthNetwork.GUARD_NOT_FOUND)
                400 -> Result.Failure(AuthNetwork.BAD_CREDENTIAL)
                503 -> Result.Failure(AuthNetwork.SERVICE_UNAVAILABLE)
                else -> {
                    Result.Failure(AuthNetwork.SERVICE_UNAVAILABLE)
                }
            }
        } catch (io: IOException) {
            return Result.Failure(AuthNetwork.SERVICE_UNAVAILABLE)
        }
    }

    override suspend fun getAuthData(): Flow<AuthData?> {
        return localDataSource.getAuthData()
    }

    override suspend fun clearAuthData() {
        localDataSource.clearAuthData()
    }
}