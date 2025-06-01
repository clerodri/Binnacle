package com.clerodri.binnacle.authentication.data


import com.clerodri.binnacle.authentication.data.datasource.local.LocalDataSource
import com.clerodri.binnacle.authentication.data.datasource.network.LoginService
import com.clerodri.binnacle.authentication.domain.model.AuthData
import com.clerodri.binnacle.authentication.domain.repository.AuthRepository
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
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


            localDataSource.saveAuthData(
                AuthData(
                    accessToken = response.accessToken,
                    role = response.role,
                    isAuthenticated = true,
                    fullName = response.fullname,
                    guardId = response.guardId,
                    localityId = response.localityId
                )
            )
            Result.Success(Unit)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.Failure(DataError.AuthNetwork.GUARD_NOT_FOUND)
                503 -> Result.Failure(DataError.AuthNetwork.SERVICE_UNAVAILABLE)
                else -> {
                    Result.Failure(DataError.AuthNetwork.SERVICE_UNAVAILABLE)
                }
            }
        } catch (io: IOException) {
            return Result.Failure(DataError.AuthNetwork.SERVICE_UNAVAILABLE)
        }
    }

    override suspend fun getAuthData(): Flow<AuthData?> {
        return localDataSource.getAuthData()
    }

    override suspend fun clearAuthData() {
        localDataSource.clearAuthData()
    }
}