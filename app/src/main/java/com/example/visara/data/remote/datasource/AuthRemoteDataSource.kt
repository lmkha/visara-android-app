package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.api.AuthApi
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(private val authApi: AuthApi) {
    suspend fun login(username: String, password: String): String? {
        val loginResult = authApi.login(username, password)
        return when (loginResult) {
            is ApiResult.Success -> loginResult.data
            else -> null
        }
    }
}
