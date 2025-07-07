package com.example.visara.data.remote.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
sealed class ApiResult<out T>(val success: Boolean) {
    @Serializable
    data class Success<T>(
        val data: T?,
        val message: String? = null,
    ) : ApiResult<T>(true)

    @Serializable
    data class Failure(
        val message: String? = null,
        val code: Int? = null,
        val extras: JsonElement? = null,
    ) : ApiResult<Nothing>(false)

    data class Error(val exception: Exception) : ApiResult<Nothing>(false)
}
