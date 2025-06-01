

package com.clerodri.binnacle.util

sealed class Async<out T> {
    data class Error(val errorMessage: Int) : Async<Nothing>()
    data class Success<out T>(val data: T) : Async<T>()
}
