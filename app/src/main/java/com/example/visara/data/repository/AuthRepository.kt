package com.example.visara.data.repository

import com.example.visara.data.local.datasource.AuthLocalDataSource
import com.example.visara.data.remote.datasource.AuthRemoteDataSource
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
) {
    suspend fun login(username: String, password: String): Boolean {
        val newToken = authRemoteDataSource.login(username, password)

        if (!newToken.isNullOrEmpty()) {
            authLocalDataSource.setCurrentUsername(username)
            authLocalDataSource.saveToken(newToken)
            return true
        }

        return false
    }

    fun isUserLoggedIn(): Boolean {
        val token = authLocalDataSource.getToken()
        return !token.isNullOrEmpty()
    }

    suspend fun logout() {
        authLocalDataSource.removeToken()
    }
}
