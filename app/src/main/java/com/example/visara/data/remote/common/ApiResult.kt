package com.example.visara.data.remote.common

sealed class ApiResult<out T> {
    sealed class NetworkResult<out T> : ApiResult<T>() {
        data class Success<out T>(val data: T) : NetworkResult<T>()
        data class Failure(
            val code: Int? = null,
            val message: String? = null,
            val extras: Any? = null,
        ) : NetworkResult<Nothing>()
    }

    data class Error(val exception: Exception) : ApiResult<Nothing>()
}
