package com.clerodri.binnacle.core

sealed interface DataError : Error {

    enum class Network : DataError {
        GUARD_NOT_FOUND,
        REQUEST_TIMEOUT,
        NO_INTERNET,
        CONFLICT,
        ROUND_NOT_FOUND
    }

    enum class AuthNetwork : DataError {
        GUARD_NOT_FOUND,
        SERVICE_UNAVAILABLE,
    }

    enum class CheckError : DataError {
        GUARD_NOT_FOUND,
        REQUEST_TIMEOUT,
        NO_INTERNET,
    }

    enum class LocalityError : DataError {
        SERVICE_UNAVAILABLE,
        ROUTES_NOT_FOUND
    }


    enum class Report : DataError {
        REQUEST_TIMEOUT,
        NO_INTERNET,
        SERVICE_UNAVAILABLE
    }
}
