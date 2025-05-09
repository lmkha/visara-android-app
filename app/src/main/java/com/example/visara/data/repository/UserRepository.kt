package com.example.visara.data.repository

import com.example.visara.data.local.datasource.UserLocalDataSource
import com.example.visara.data.model.UserModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import com.example.visara.data.remote.dto.toUserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) {
    private val _currentUser: MutableStateFlow<UserModel?> = MutableStateFlow(null)
    val currentUser: StateFlow<UserModel?> = _currentUser.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshCurrentUser()
        }
    }

    suspend fun refreshCurrentUser() {
        val user = getCurrentUser()
        _currentUser.update { user }
    }

    suspend fun syncCurrentUser() {
        val apiResult = userRemoteDataSource.getCurrentUser()
        val user = if (apiResult is ApiResult.Success) apiResult.data.toUserModel()
        else null

        user?.let { saveUser(it) }
        _currentUser.update { user }
    }

    suspend fun saveUser(userModel: UserModel) {
        userLocalDataSource.saveUser(userModel)
    }

    private suspend fun getCurrentUser() : UserModel? {
        val currentLocalUser = userLocalDataSource.getCurrentUser()
        if (currentLocalUser != null) return currentLocalUser

        val apiResult = userRemoteDataSource.getCurrentUser()
        if (apiResult is ApiResult.Success) {
            val userModel = apiResult.data.toUserModel()
            return userModel
        }
        return null
    }

    suspend fun getPublicUser(username: String) : UserModel? {
        val apiResult =  userRemoteDataSource.getPublicUser(username)
        if (apiResult is ApiResult.Success) {
            return apiResult.data.toUserModel()
        }
        return null
    }

    suspend fun searchUser(pattern: String) : List<UserModel> {
        if (pattern.isEmpty() || pattern.isBlank()) return emptyList()

        val apiResult = userRemoteDataSource.searchUser(pattern)
        val result = if (apiResult is ApiResult.Success) {
            apiResult.data.map { it.toUserModel() }
        } else {
            emptyList()
        }
        return result
    }

    suspend fun followUser(username: String) : Boolean {
        val apiResult = userRemoteDataSource.followUser(username)
        if (apiResult is ApiResult.Success) { syncCurrentUser() }
        return apiResult is ApiResult.Success
    }

    suspend fun unfollowUser(username: String) : Boolean {
        val apiResult = userRemoteDataSource.unfollowUser(username)
        if (apiResult is ApiResult.Success) { syncCurrentUser() }
        return apiResult is ApiResult.Success
    }

    suspend fun checkIsMyFollower(username: String) : Boolean {
        val apiResult = userRemoteDataSource.checkUserIsMyFollower(username)
        return apiResult is ApiResult.Success
    }

    suspend fun checkIsFollowingThisUser(username: String) : Boolean {
        val apiResult = userRemoteDataSource.checkIsFollowingThisUser(username)
        return apiResult is ApiResult.Success
    }

    suspend fun getAllFollower(page: Int = 0, size: Long = 100L) : List<UserModel> {
        val apiResult = userRemoteDataSource.getAllFollower(page, size)
        if (apiResult !is ApiResult.Success) return emptyList()

        val userModelList = apiResult.data.map { it.toUserModel() }

        return userModelList
    }

    suspend fun getAllFollowing(page: Int, size: Long) : List<UserModel> {
        val apiResult = userRemoteDataSource.getAllFollowing(page, size)
        if (apiResult !is ApiResult.Success) return emptyList()

        val userModelList = apiResult.data.map { it.toUserModel() }

        return userModelList
    }

    suspend fun getAllFollowerTEMP(page: Int = 0, size: Long = 100L) : List<UserModel> {
        val apiResult = userRemoteDataSource.getAllFollowerTEMP(page, size)
        if (apiResult !is ApiResult.Success) return emptyList()
        val usernameList = apiResult.data

        val userModelList = coroutineScope {
            usernameList.map { username ->
                async {
                    getPublicUser(username)
                }
            }.mapNotNull { it.await() }
        }

        return userModelList
    }

    suspend fun getAllFollowingTEMP(page: Int, size: Long) : List<UserModel> {
        val apiResult = userRemoteDataSource.getAllFollowingTEMP(page, size)
        if (apiResult !is ApiResult.Success) return emptyList()
        val usernameList = apiResult.data

        val userModelList = coroutineScope {
            usernameList.map { username ->
                async {
                    getPublicUser(username)
                }
            }.mapNotNull { it.await() }
        }

        return userModelList
    }
}
