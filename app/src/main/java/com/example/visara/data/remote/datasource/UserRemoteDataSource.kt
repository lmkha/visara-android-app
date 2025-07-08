package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.UserApi
import com.example.visara.data.remote.common.ApiResponse
import com.example.visara.data.remote.dto.FollowerUserDto
import com.example.visara.data.remote.dto.FollowingUserDto
import com.example.visara.data.remote.dto.UserDto
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi,
    json: Json,
) : RemoteDataSource(json) {
    suspend fun getCurrentUser() : ApiResult<UserDto> {
        return callApi({ userApi.getCurrentUser() }) { response ->
            if (!response.isSuccessful) return@callApi extractFailureFromResponseBody(response.body?.string())
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()
            json.decodeFromString<ApiResponse<UserDto>>(responseBody).toApiResult()
        }
    }

    suspend fun getPublicUser(username: String) : ApiResult<UserDto> {
        return callApi({ userApi.getPublicUser(username) }) { response ->
            val responseBody = response.body?.string()
            if (responseBody.isNullOrEmpty()) return@callApi ApiResult.Failure()
            json.decodeFromString<ApiResponse<UserDto>>(responseBody).toApiResult()
        }
    }

    suspend fun searchUser(pattern: String) : ApiResult<List<UserDto>> {
        return callApi({ userApi.searchUser(pattern) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<List<UserDto>>>(responseBody).toApiResult()
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun followUser(username: String) : ApiResult<Unit> {
        return callApi({ userApi.followUser(username) }) { response ->
            if (!response.isSuccessful) return@callApi ApiResult.Failure()
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()

            json.decodeFromString<ApiResponse<Nothing>>(responseBody).toApiResult()
        }
    }

    suspend fun unfollowUser(username: String) : ApiResult<Unit> {
        return callApi({ userApi.unfollowUser(username) }) { response ->
            if (!response.isSuccessful) return@callApi ApiResult.Failure()
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()

            json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                if (it.success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure()
                }
            }
        }
    }

    suspend fun checkUserIsMyFollower(username: String) : ApiResult<Unit> {
        return callApi({ userApi.checkUserIsMyFollower(username) }) { response ->
            if (!response.isSuccessful) return@callApi ApiResult.Failure()
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                if (it.message == "Followed") {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure()
                }
            }
        }
    }

    suspend fun checkIsFollowingThisUser(username: String) : ApiResult<Unit> {
        return callApi({ userApi.checkIsFollowingThisUser(username) }) { response ->
            if (!response.isSuccessful) return@callApi ApiResult.Failure()
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                if (it.message == "Followed") {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure()
                }
            }
        }
    }

    suspend fun getAllFollower(page: Int, size: Long) : ApiResult<List<FollowerUserDto>> {
        return callApi({ userApi.getAllFollower(page, size) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<List<FollowerUserDto>>>(responseBody).toApiResult()
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getAllFollowing(page: Int, size: Long): ApiResult<List<FollowingUserDto>> {
        return callApi({ userApi.getAllFollowing(page, size) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<List<FollowingUserDto>>>(responseBody).toApiResult()
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }
}
