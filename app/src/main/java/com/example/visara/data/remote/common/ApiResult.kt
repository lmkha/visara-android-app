package com.example.visara.data.remote.common

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Failure(val error: ApiError) : ApiResult<Nothing>()
    data class Error(val exception: Exception) : ApiResult<Nothing>()
}
data class ApiError(
    val code: Int?,
    val message: String?,
    val success: Boolean = false,
    val errorCode: String? = null,
    val rawBody: String? = null,
)
