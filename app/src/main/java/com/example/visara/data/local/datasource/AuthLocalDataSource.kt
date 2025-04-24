package com.example.visara.data.local.datasource

import com.example.visara.data.local.shared_preference.TokenStorage
import com.example.visara.data.local.shared_preference.UserSessionManager
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val userSessionManager: UserSessionManager,
) {
    fun getAccessToken() : String? = tokenStorage.getAccessToken()
    fun getRefreshToken() : String? = tokenStorage.getRefreshToken()
    suspend fun saveAccessToken(accessToken: String) {
        tokenStorage.saveAccessToken(accessToken)
    }
    suspend fun saveRefreshToken(refreshToken: String) {
        tokenStorage.saveRefreshToken(refreshToken)
    }
    fun setCurrentUsername(username: String) {
        userSessionManager.setCurrentUsername(username)
    }
    fun clearCurrentUsername() {
        userSessionManager.clearCurrentUsername()
    }
    suspend fun clearToken() {
        tokenStorage.removeAccessToken()
        tokenStorage.removeRefreshToken()
    }
}
