package com.clerodri.binnacle.auth.data


import android.util.Log
import com.clerodri.binnacle.auth.data.network.LoginService
import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.model.Guard
import com.clerodri.binnacle.auth.domain.Result
import com.clerodri.binnacle.auth.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val api: LoginService
) : AuthRepository {


    override suspend fun login(identification: String): Result<Guard, DataError.Network> {

        return try {
            val response = api.doLogin(identification)
            Log.d("RR", "HttpException $response")
            val guard = Guard(
                response.id,
                response.fullname,
                response.localityId,
                response.accessToken,
                response.refreshToken
            )
            Result.Success(guard)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.Failure(DataError.Network.GUARD_NOT_FOUND)
                408 -> Result.Failure(DataError.Network.REQUEST_TIMEOUT)
                503 -> Result.Failure(DataError.Network.NO_INTERNET)
                else -> {
                    Log.d("RR", "HttpException ${e.code()}")
                    Result.Failure(DataError.Network.REQUEST_TIMEOUT)
                }
            }
        } catch (io: IOException) {
            Result.Failure(DataError.Network.REQUEST_TIMEOUT)
        }
    }
}