package com.clerodri.binnacle.core.di

import com.clerodri.binnacle.core.domain.ApiResponse
import retrofit2.HttpException
import retrofit2.Response

/**
 * Author: Ronaldo R.
 * Date:  11/24/2025
 * Description:
 **/
object ApiRutas {
    object HOME {
        const val ROUTES = "api/v1/route"
        const val CHECK_IN = "api/v1/check"
        const val CHECK_OUT = "api/v1/check/{id}"
        const val VALIDATE_CHECK = "api/v1/check/{id}"
        const val START_ROUND = "api/v1/round"
        const val STOP_ROUND = "api/v1/round/{id}/finish"
        const val PING_SERVER = "api/v1/auth/ping"
    }

    object REPORT {
        const val ADD_REPORT = "api/v1/round/event"

    }

    fun <T> extractDataOrThrow(response: Response<ApiResponse<T>>, endpoint: String): T {
        if (!response.isSuccessful) {
            throw HttpException(response)
        }

        val apiResponse = response.body()
            ?: throw IllegalStateException("Response body is null from $endpoint")

        if (apiResponse.isError) {
            throw ApiErrorException(
                status = apiResponse.status,
                message = apiResponse.message ?: "Unknown error",
                code = apiResponse.code ?: response.code()
            )
        }

        return apiResponse.data
            ?: throw IllegalStateException("Response data is null from $endpoint")
    }

    class ApiErrorException(
        val status: String,
        override val message: String,
        val code: Int
    ) : Exception("[$code] $message")
}
