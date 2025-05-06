package com.example.visara.data.repository

import com.example.visara.data.local.datasource.UserLocalDataSource
import com.example.visara.data.model.UserModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import com.example.visara.data.remote.dto.toUserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        _currentUser.value = user
    }

    suspend fun saveUser(userModel: UserModel) {
        userLocalDataSource.saveUser(userModel)
    }

    suspend fun getCurrentUser() : UserModel? {
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
}
