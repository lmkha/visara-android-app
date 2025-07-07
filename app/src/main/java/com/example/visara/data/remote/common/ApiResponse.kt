package com.example.visara.data.remote.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val httpStatus: String? = null,
    val data: T? = null,
    val code: Int? = null,
    val extras: JsonElement? = null,
) {
    fun toApiResult(): ApiResult<T> {
        return if (success) {
            ApiResult.Success(
                message = message,
                data = data,
            )
        } else {
            ApiResult.Failure(
                message = message,
                code = code,
                extras = extras,
            )
        }
    }
}
