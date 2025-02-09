package com.clerodri.binnacle.core

sealed interface DataError: Error {

    enum class Network: DataError {
        GUARD_NOT_FOUND,
        REQUEST_TIMEOUT,
        NO_INTERNET,
        CONFLICT,
        NO_PERMISSION,
        ROUND_NOT_FOUND
    }
    enum class AuthNetwork: DataError {
        GUARD_NOT_FOUND,
        REQUEST_TIMEOUT,
        NO_INTERNET,
    }

    enum class CheckError: DataError {
        GUARD_NOT_FOUND,
        REQUEST_TIMEOUT,
        NO_INTERNET,
    }
    enum class Round: DataError {
        GUARD_NOT_FOUND,
        REQUEST_TIMEOUT,
        NO_INTERNET
    }
    enum class Report: DataError {
        REQUEST_TIMEOUT,
        NO_INTERNET
    }
}
