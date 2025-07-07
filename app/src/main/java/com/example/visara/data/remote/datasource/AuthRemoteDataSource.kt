package com.example.visara.data.remote.datasource

import android.util.Log
import com.example.visara.data.remote.api.AuthApi
import com.example.visara.data.remote.common.ApiResponse
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.LoginDto
import com.example.visara.data.remote.dto.UserDto
import com.example.visara.data.remote.dto.UsernameAvailabilityDto
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSource @Inject constructor(
    private val authApi: AuthApi,
    json: Json,
) : RemoteDataSource(json) {
    suspend fun login(username: String, password: String): ApiResult<LoginDto> {
        return callApi({ authApi.login(username, password)}) { response ->
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()

            if (!response.isSuccessful) return@callApi extractFailureFromResponseBody(responseBody)
            json.decodeFromString<ApiResponse<LoginDto>>(responseBody).toApiResult()
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

            if (!response.isSuccessful || responseBody.isNullOrEmpty()) return@callApi ApiResult.Failure()
            json.decodeFromString<ApiResponse<UserDto>>(responseBody).toApiResult()
        }
    }

    suspend fun checkUsernameAvailability(username: String): ApiResult<UsernameAvailabilityDto> {
        return callApi({ authApi.checkUsernameAvailability(username) }) { response ->
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()

            if (response.isSuccessful) {
                json.decodeFromString<ApiResult.Success<UsernameAvailabilityDto>>(responseBody)
            } else {
                return@callApi extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun addFcmTokenForAccount(fcmToken: String, username: String) : ApiResult<Nothing> {
        return callApi({ authApi.addFcmToken(token = fcmToken, username = username) }) { response ->
            val responseBody = response.body?.string()
            if (!response.isSuccessful || responseBody.isNullOrEmpty()) return@callApi ApiResult.Failure()

            val apiResponse = json.decodeFromString<ApiResponse<Nothing>>(responseBody)
            apiResponse.toApiResult()
        }
    }

    suspend fun removeFcmTokenForAccount(fcmToken: String, username: String) : ApiResult<Nothing> {
        return callApi({ authApi.removeFcmToken(token = fcmToken, username = username) }) { response ->
            val responseBody = response.body?.string()
            if (!response.isSuccessful || responseBody.isNullOrEmpty()) return@callApi ApiResult.Failure()
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).toApiResult()
        }
    }
}
