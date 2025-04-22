package com.example.visara.data.repository

import com.example.visara.data.model.UserModel
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
) {
    suspend fun getCurrentUser() : UserModel? {
        return userRemoteDataSource.getCurrentUser()
    }
}
