package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.UserApi
import com.example.visara.data.remote.dto.FollowerUserDto
import com.example.visara.data.remote.dto.FollowingUserDto
import com.example.visara.data.remote.dto.UserDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi,
    gson: Gson,
) : RemoteDataSource(gson) {
    suspend fun getCurrentUser() : ApiResult<UserDto> {
        return callApi({ userApi.getCurrentUser() }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val user = gson.fromJson(dataJson, UserDto::class.java)
                ApiResult.NetworkResult.Success(user)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getPublicUser(username: String) : ApiResult<UserDto> {
        return callApi({ userApi.getPublicUser(username) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val user = gson.fromJson(dataJson, UserDto::class.java)
                ApiResult.NetworkResult.Success(user)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun searchUser(pattern: String) : ApiResult<List<UserDto>> {
        return callApi({ userApi.searchUser(pattern) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<UserDto>>() {}.type
                val userDtoList: List<UserDto> = gson.fromJson(dataJson, type)
                ApiResult.NetworkResult.Success(userDtoList)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun followUser(username: String) : ApiResult<Unit> {
        return callApi({ userApi.followUser(username) }) { response ->
            val responseBody = response.body?.string()

            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val successValue = jsonObject["success"]
            val success = successValue is Boolean && successValue

            if (response.isSuccessful && success) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun unfollowUser(username: String) : ApiResult<Unit> {
        return callApi({ userApi.unfollowUser(username) }) { response ->
            val responseBody = response.body?.string()

            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val successValue = jsonObject["success"]
            val success = successValue is Boolean && successValue

            if (response.isSuccessful && success) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun checkUserIsMyFollower(username: String) : ApiResult<Unit> {
        return callApi({ userApi.checkUserIsMyFollower(username) }) { response ->
            val responseBody = response.body?.string()

            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val messageValue = jsonObject["message"]
            val success = messageValue is String && messageValue == "Followed"

            if (response.isSuccessful && success) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun checkIsFollowingThisUser(username: String) : ApiResult<Unit> {
        return callApi({ userApi.checkIsFollowingThisUser(username) }) { response ->
            val responseBody = response.body?.string()

            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val messageValue = jsonObject["message"]
            val success = messageValue is String && messageValue == "Followed"

            if (response.isSuccessful && success) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getAllFollower(page: Int, size: Long) : ApiResult<List<FollowerUserDto>> {
        return callApi({ userApi.getAllFollower(page, size) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<FollowerUserDto>>() {}.type
                val userDtoList: List<FollowerUserDto> = gson.fromJson(dataJson, type)
                ApiResult.NetworkResult.Success(userDtoList)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getAllFollowing(page: Int, size: Long): ApiResult<List<FollowingUserDto>> {
        return callApi({ userApi.getAllFollowing(page, size) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<FollowingUserDto>>() {}.type
                val userDtoList: List<FollowingUserDto> = gson.fromJson(dataJson, type)
                ApiResult.NetworkResult.Success(userDtoList)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }
}
