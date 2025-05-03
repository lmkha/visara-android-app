package com.example.visara.data.repository

import com.example.visara.data.local.datasource.UserLocalDataSource
import com.example.visara.data.model.UserModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import com.example.visara.data.remote.dto.toUserModel
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) {
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
}
