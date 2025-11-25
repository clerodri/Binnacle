package com.clerodri.binnacle.core.domain

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("timestamp")
    val timestamp: String? = null
) {

    val isSuccess: Boolean get() = status == "success"
    val isError: Boolean get() = status == "error"
}