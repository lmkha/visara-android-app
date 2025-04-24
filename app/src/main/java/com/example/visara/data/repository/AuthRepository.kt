package com.example.visara.data.repository

import android.util.Log
import com.example.visara.data.local.dao.UserDao
import com.example.visara.data.local.datasource.AuthLocalDataSource
import com.example.visara.data.model.toEntity
import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.datasource.AuthRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
    private val userRepository: UserRepository,
    private val userDao: UserDao,
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
            authLocalDataSource.saveAccessToken(token)
            _isAuthenticated.value = true

            val currentUserModel = userRepository.getCurrentUser()
            Log.i("CHECK_VAR", "current user model: ${currentUserModel.toString()}")
            val userEntity = currentUserModel?.toEntity()
            if (userEntity != null) {
                userDao.insertUser(userEntity)
            }

            return true
        }
        return false
    }
    suspend fun logout() {
        // Clear token first — username is needed to locate the correct token key.
        authLocalDataSource.clearToken()
        authLocalDataSource.clearCurrentUsername()
        _isAuthenticated.value = false
    }
    fun refreshAuthenticationState() {
        val hasToken = !authLocalDataSource.getAccessToken().isNullOrEmpty()
        _isAuthenticated.value = hasToken
    }
}
