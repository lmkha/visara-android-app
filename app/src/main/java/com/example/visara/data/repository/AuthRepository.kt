package com.example.visara.data.repository

import com.example.visara.data.local.datasource.AuthLocalDataSource
import com.example.visara.data.model.UserModel
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

    suspend fun login(username: String, password: String): Result<Unit> {
        return when (val loginResult = authRemoteDataSource.login(username, password)) {
            is ApiResult.Error -> {
                Result.failure(Throwable(message = loginResult.exception.message))
            }
            is ApiResult.Failure -> {
                Result.failure(Throwable(message = loginResult.error.message))
            }
            is ApiResult.Success -> {
                // Must set current username before saving token — token key depends on it.
                authLocalDataSource.setCurrentUsername(username)
                val accessToken = loginResult.data.accessToken
                val refreshToken = loginResult.data.refreshToken
                authLocalDataSource.saveAccessToken(accessToken)
                authLocalDataSource.saveRefreshToken(refreshToken)
                _isAuthenticated.update { true }
                appSettingsRepository.getFcmToken()?.let { fcmToken ->
                    authRemoteDataSource.addFcmTokenForAccount(username = username, fcmToken = fcmToken)
                }
                Result.success(Unit)
            }
        }
    }

    suspend fun updateUser(
        isPrivate: Boolean? = null,
        fullName: String? = null,
        bio: String? = null,
    ) : Result<UserModel> {
        return when (val apiResult = authRemoteDataSource.updateUser(isPrivate = isPrivate, fullName = fullName, bio = bio)) {
            is ApiResult.Error -> Result.failure(apiResult.exception)
            is ApiResult.Failure -> Result.failure(Throwable(apiResult.error.message))
            is ApiResult.Success -> Result.success(apiResult.data.toUserModel())
        }
    }

    suspend fun logout() {
        // Clear token first — username is needed to locate the correct token key.
        val currentUsername = authLocalDataSource.getCurrentUsername()
        val currentFcmToken = appSettingsRepository.getFcmToken()
        if (currentUsername != null && currentFcmToken != null) {
            val result = authRemoteDataSource.removeFcmTokenForAccount(
                fcmToken = currentFcmToken,
                username = currentUsername
            )

            if (result is ApiResult.Success) {
                authLocalDataSource.clearToken()
                authLocalDataSource.clearCurrentUsername()
                _isAuthenticated.update { false }
            }
        }
    }

    private fun refreshAuthenticationState() {
        val hasToken = !authLocalDataSource.getAccessToken().isNullOrEmpty()
        _isAuthenticated.value = hasToken
    }
}
