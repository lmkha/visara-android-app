package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiError
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.UserApi
import com.example.visara.data.remote.dto.UserDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi,
    private val gson: Gson,
) {
    suspend fun getCurrentUser() : ApiResult<UserDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getCurrentUser()
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val user = gson.fromJson(dataJson, UserDto::class.java)
                    ApiResult.Success(user)
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

    suspend fun getPublicUser(username: String) : ApiResult<UserDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getPublicUser(username)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val user = gson.fromJson(dataJson, UserDto::class.java)
                    ApiResult.Success(user)
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
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun searchUser(pattern: String) : ApiResult<List<UserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.searchUser(pattern)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<UserDto>>() {}.type
                    val userDtoList: List<UserDto> = gson.fromJson(dataJson, type)
                    ApiResult.Success(userDtoList)
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
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }
    }
}
