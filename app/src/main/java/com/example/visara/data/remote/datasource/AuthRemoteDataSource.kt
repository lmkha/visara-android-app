package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiError
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.AuthApi
import com.example.visara.data.remote.dto.LoginDto
import com.example.visara.data.remote.dto.UsernameAvailabilityDto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authApi: AuthApi,
    private val gson: Gson,
) {
    suspend fun login(username: String, password: String): ApiResult<LoginDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.login(username, password)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val loginDto = gson.fromJson(dataJson, LoginDto::class.java)

                    if (loginDto.refreshToken.isNotBlank() && loginDto.accessToken.isNotBlank())
                        ApiResult.Success(loginDto)
                    else ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = "EMPTY_TOKEN",
                            message = "Token not found.",
                            rawBody = responseBody
                        )
                    )
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun checkUsernameAvailability(username: String): ApiResult<UsernameAvailabilityDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.checkUsernameAvailability(username)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val result = gson.fromJson(responseBody, UsernameAvailabilityDto::class.java)
                    ApiResult.Success(result)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun refreshToken(refreshToken: String) : ApiResult<String> {
        return withContext(Dispatchers.IO) {
            ApiResult.Success("newAccessToken")
        }
    }
}
