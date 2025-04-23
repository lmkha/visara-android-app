package com.example.visara.data.repository

import com.example.visara.data.model.UserModel
import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
) {
    suspend fun getCurrentUser() : UserModel? {
        val apiResult = userRemoteDataSource.getCurrentUser()
        if (apiResult is ApiResult.Success) {
            val userDto = apiResult.data
            return UserModel(
                id = userDto.id,
                username = userDto.username,
                email = userDto.email,
                phone = userDto.phone,
            )
        }
        return null
    }
}
