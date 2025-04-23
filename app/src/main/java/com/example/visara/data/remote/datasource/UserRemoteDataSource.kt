package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.ApiError
import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.api.UserApi
import com.example.visara.data.remote.dto.UserDto
import com.google.gson.Gson
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi,
    private val gson: Gson,
) {
    suspend fun getCurrentUser() : ApiResult<UserDto> {
        try {
            val response = userApi.getCurrentUser()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val user = gson.fromJson(dataJson, UserDto::class.java)
                return ApiResult.Success(user)
            } else {
                return ApiResult.Failure(
                    ApiError(
                        code = response.code,
                        errorCode = response.code.toString(),
                        message = response.message,
                        rawBody = responseBody
                    )
                )
            }
        } catch (e: Exception) {
            return ApiResult.Error(e)
        }
    }
}
