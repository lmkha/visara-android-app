package com.example.datn_mobile.data.repository

import com.example.datn_mobile.data.model.UserModel
import com.example.datn_mobile.data.remote.datasource.UserRemoteDataSource
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
) {
    fun getCurrentUser() : UserModel? {
        return userRemoteDataSource.getCurrentUser()
    }
}
