package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.AuthApi
import com.example.visara.data.remote.dto.LoginDto
import com.example.visara.data.remote.dto.UserDto
import com.example.visara.data.remote.dto.UsernameAvailabilityDto
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.get

@Singleton
class AuthRemoteDataSource @Inject constructor(
    private val authApi: AuthApi,
    gson: Gson,
) : RemoteDataSource(gson) {
    suspend fun login(username: String, password: String): ApiResult<LoginDto> {
        return callApi({ authApi.login(username, password)}) { response ->
            val responseBody = response.body?.string()

            if (!response.isSuccessful) return@callApi extractFailureFromResponseBody(responseBody)

            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val dataJson = gson.toJson(jsonObject["data"])
            val loginDto = gson.fromJson(dataJson, LoginDto::class.java)

            if (loginDto.refreshToken.isBlank() || loginDto.accessToken.isBlank()) {
                return@callApi extractFailureFromResponseBody(responseBody)
            }
            return@callApi ApiResult.NetworkResult.Success(loginDto)
        }
    }

    suspend fun updateUser(
        isPrivate: Boolean? = null,
        fullName: String? = null,
        bio: String? = null,
    ) : ApiResult<UserDto> {
        return callApi(
            request = {
                authApi.updateUser(
                    isPrivate = isPrivate,
                    bio = bio,
                    fullName = fullName,
                )
            }
        ) { response ->
            val response = authApi.updateUser(
                isPrivate = isPrivate,
                bio = bio,
                fullName = fullName,
            )
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val userDto: UserDto = gson.fromJson(dataJson, UserDto::class.java)
                ApiResult.NetworkResult .Success(userDto)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun checkUsernameAvailability(username: String): ApiResult<UsernameAvailabilityDto> {
        return callApi({ authApi.checkUsernameAvailability(username) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful) {
                val result = gson.fromJson(responseBody, UsernameAvailabilityDto::class.java)
                ApiResult.NetworkResult.Success(result)
            } else {
                return@callApi extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun refreshToken(refreshToken: String) : ApiResult<String> {
        return ApiResult.NetworkResult.Success("newAccessToken")
    }

    suspend fun addFcmTokenForAccount(fcmToken: String, username: String) : ApiResult<Unit> {
        return callApi({ authApi.addFcmToken(token = fcmToken, username = username) }) { response ->
            val responseBody = response.body?.string()
            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val success = jsonObject["success"]?.let { it is Boolean && it }

            if (response.isSuccessful && success == true) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                return@callApi extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun removeFcmTokenForAccount(fcmToken: String, username: String) : ApiResult<Unit> {
        return callApi({ authApi.removeFcmToken(token = fcmToken, username = username) }) { response ->
            val responseBody = response.body?.string()
            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val success = jsonObject["success"]?.let { it is Boolean && it }

            if (response.isSuccessful && success == true) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                return@callApi extractFailureFromResponseBody(responseBody)
            }
        }
    }
}
