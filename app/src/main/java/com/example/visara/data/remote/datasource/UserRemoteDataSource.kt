package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiError
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.UserApi
import com.example.visara.data.remote.dto.FollowerUserDto
import com.example.visara.data.remote.dto.FollowingUserDto
import com.example.visara.data.remote.dto.UserDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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

    suspend fun followUser(username: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.followUser(username)
                val responseBody = response.body?.string()

                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val successValue = jsonObject["success"]
                val success = successValue is Boolean && successValue == true

                if (response.isSuccessful && success == true) {
                    ApiResult.Success(Unit)
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

    suspend fun unfollowUser(username: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.unfollowUser(username)
                val responseBody = response.body?.string()

                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val successValue = jsonObject["success"]
                val success = successValue is Boolean && successValue == true

                if (response.isSuccessful && success == true) {
                    ApiResult.Success(Unit)
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

    suspend fun checkUserIsMyFollower(username: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.checkUserIsMyFollower(username)
                val responseBody = response.body?.string()

                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val messageValue = jsonObject["message"]
                val success = messageValue is String && messageValue == "Followed"

                if (response.isSuccessful && success) {
                    ApiResult.Success(Unit)
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

    suspend fun checkIsFollowingThisUser(username: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.checkIsFollowingThisUser(username)
                val responseBody = response.body?.string()

                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val messageValue = jsonObject["message"]
                val success = messageValue is String && messageValue == "Followed"

                if (response.isSuccessful && success) {
                    ApiResult.Success(Unit)
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

    suspend fun getAllFollower(page: Int, size: Long) : ApiResult<List<FollowerUserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getAllFollower(page, size)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<FollowerUserDto>>() {}.type
                    val userDtoList: List<FollowerUserDto> = gson.fromJson(dataJson, type)
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
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun getAllFollowing(page: Int, size: Long): ApiResult<List<FollowingUserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApi.getAllFollowing(page, size)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<FollowingUserDto>>() {}.type
                    val userDtoList: List<FollowingUserDto> = gson.fromJson(dataJson, type)
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
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }
}
