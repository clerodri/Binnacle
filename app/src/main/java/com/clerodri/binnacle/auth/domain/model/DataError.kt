package com.clerodri.binnacle.auth.domain.model

sealed interface DataError:Error{
    enum class Network:DataError{
        GUARD_NOT_FOUND,
        REQUEST_TIMEOUT,
        NO_INTERNET
    }
}
