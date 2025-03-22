package com.example.datn_mobile.data.repository

import com.example.datn_mobile.data.local.datasource.AuthLocalDataSource
import com.example.datn_mobile.data.remote.datasource.AuthRemoteDataSource
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
) {
    fun login(username: String, password: String) {
        val newToken = authRemoteDataSource.login(username, password)

        if (!newToken.isNullOrEmpty()) {
            authLocalDataSource.saveToken(newToken)
        }
    }

    fun getToken() : String? {
        return authLocalDataSource.getToken()
    }

    fun removeToken() {
        authLocalDataSource.removeToken()
    }
}
