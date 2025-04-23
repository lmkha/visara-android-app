package com.example.visara.data.repository

import com.example.visara.data.local.datasource.AuthLocalDataSource
import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.datasource.AuthRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
) {
    private val _isAuthenticated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        refreshAuthenticationState()
    }

    suspend fun login(username: String, password: String): Boolean {
        val loginResult = authRemoteDataSource.login(username, password)

        if (loginResult is ApiResult.Success && !loginResult.data.isEmpty()) {
            // Must set current username before saving token — token key depends on it.
            authLocalDataSource.setCurrentUsername(username)
            val token = loginResult.data
            authLocalDataSource.saveToken(token)
            _isAuthenticated.value = true
            return true
        }
        return false
    }
    suspend fun logout() {
        // Clear token first — username is needed to locate the correct token key.
        authLocalDataSource.removeToken()
        authLocalDataSource.clearCurrentUsername()
        _isAuthenticated.value = false
    }
    fun refreshAuthenticationState() {
        val token = authLocalDataSource.getToken()
        _isAuthenticated.value = !token.isNullOrEmpty()
    }
}
