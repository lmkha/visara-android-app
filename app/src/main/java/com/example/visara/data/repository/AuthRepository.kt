package com.example.visara.data.repository

import com.example.visara.data.local.datasource.AuthLocalDataSource
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.AuthRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
    private val appSettingsRepository: AppSettingsRepository,
) {
    private val _isAuthenticated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        refreshAuthenticationState()
    }

    suspend fun login(username: String, password: String): Boolean {
        val loginResult = authRemoteDataSource.login(username, password)

        if (loginResult !is ApiResult.Success) return false

        // Must set current username before saving token — token key depends on it.
        authLocalDataSource.setCurrentUsername(username)
        val accessToken = loginResult.data.accessToken
        val refreshToken = loginResult.data.refreshToken
        authLocalDataSource.saveAccessToken(accessToken)
        authLocalDataSource.saveRefreshToken(refreshToken)
        _isAuthenticated.update { true }
        val fcmToken = appSettingsRepository.getFcmToken()
        if (fcmToken != null) {
            authRemoteDataSource.addFcmTokenForAccount(
                username = username,
                fcmToken = fcmToken
            )
        }

        return true
    }

    suspend fun logout() {
        // Clear token first — username is needed to locate the correct token key.
        val currentUsername = authLocalDataSource.getCurrentUsername()
        val currentFcmToken = appSettingsRepository.getFcmToken()
        if (currentUsername != null && currentFcmToken != null) {
            authRemoteDataSource.removeFcmTokenForAccount(
                fcmToken = currentFcmToken,
                username = currentUsername
            )
        }
        authLocalDataSource.clearToken()
        authLocalDataSource.clearCurrentUsername()
        _isAuthenticated.update { false }
    }

    fun refreshAuthenticationState() {
        val hasToken = !authLocalDataSource.getAccessToken().isNullOrEmpty()
        _isAuthenticated.value = hasToken
    }
}
