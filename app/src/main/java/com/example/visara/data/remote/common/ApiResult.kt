package com.example.visara.data.remote.common

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Failure(
        val code: Int? = null,
        val message: String? = null,
        val extras: Any? = null,
    ) : ApiResult<Nothing>()
    data class Error(val exception: Exception) : ApiResult<Nothing>()
}
