package com.example.visara.data.repository

import com.example.visara.data.model.UserModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import com.example.visara.data.remote.dto.toUserModel
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
) {
    suspend fun getCurrentUser() : UserModel? {
        val apiResult = userRemoteDataSource.getCurrentUser()
        if (apiResult is ApiResult.Success) {
            val userModel = apiResult.data.toUserModel()
            return userModel
        }
        return null
    }
}
