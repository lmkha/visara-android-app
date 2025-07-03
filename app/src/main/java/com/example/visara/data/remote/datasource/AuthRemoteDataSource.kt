package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.AuthApi
import com.example.visara.data.remote.dto.LoginDto
import com.example.visara.data.remote.dto.UserDto
import com.example.visara.data.remote.dto.UsernameAvailabilityDto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.get

@Singleton
class AuthRemoteDataSource @Inject constructor(
    private val authApi: AuthApi,
    gson: Gson,
) : RemoteDataSource(gson) {
    suspend fun login(username: String, password: String): ApiResult<LoginDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.login(username, password)
                val responseBody = response.body?.string()

                if (!response.isSuccessful) {
                    return@withContext parseFailureFromResponse(responseBody)
                }

                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val loginDto = gson.fromJson(dataJson, LoginDto::class.java)

                if (loginDto.refreshToken.isNotBlank() && loginDto.accessToken.isNotBlank())
                    return@withContext ApiResult.Success(loginDto)
                else {
                    return@withContext parseFailureFromResponse(responseBody)
                }
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }

    suspend fun updateUser(
        isPrivate: Boolean? = null,
        fullName: String? = null,
        bio: String? = null,
    ) : ApiResult<UserDto> {
        return withContext(Dispatchers.IO) {
            try {
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
                    ApiResult.Success(userDto)
                } else {
                    parseFailureFromResponse(responseBody)
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

                if (response.isSuccessful) {
                    val result = gson.fromJson(responseBody, UsernameAvailabilityDto::class.java)
                    ApiResult.Success(result)
                } else {
                    return@withContext parseFailureFromResponse(responseBody)
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

    suspend fun addFcmTokenForAccount(fcmToken: String, username: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.addFcmToken(token = fcmToken, username = username)
                val responseBody = response.body?.string()
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val success = jsonObject["success"]?.let { it is Boolean && it }

                if (response.isSuccessful && success == true) {
                    ApiResult.Success(Unit)
                } else {
                    return@withContext parseFailureFromResponse(responseBody)
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun removeFcmTokenForAccount(fcmToken: String, username: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.removeFcmToken(token = fcmToken, username = username)
                val responseBody = response.body?.string()
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val success = jsonObject["success"]?.let { it is Boolean && it }

                if (response.isSuccessful && success == true) {
                    ApiResult.Success(Unit)
                } else {
                    return@withContext parseFailureFromResponse(responseBody)
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }
}
