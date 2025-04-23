package com.example.visara.data.local.datasource

import com.example.visara.data.local.shared_preference.TokenStorage
import com.example.visara.data.local.shared_preference.UserSessionManager
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val userSessionManager: UserSessionManager,
) {
    fun getToken() : String? = tokenStorage.getToken()
    suspend fun saveToken(token: String) {
        tokenStorage.saveToken(token)
    }
    suspend fun removeToken() {
        tokenStorage.removeToken()
    }

    fun setCurrentUsername(username: String) {
        userSessionManager.setCurrentUsername(username)
    }

    fun clearCurrentUsername() {
        userSessionManager.clearCurrentUsername()
    }
}
