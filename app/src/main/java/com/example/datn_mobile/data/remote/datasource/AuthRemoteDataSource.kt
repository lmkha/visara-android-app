package com.example.datn_mobile.data.remote.datasource

import com.example.datn_mobile.data.remote.api.AuthApi
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(private val authApi: AuthApi) {
    fun login(username: String, password: String) : String? {
        val loginResponse = authApi.login(username, password)
        return loginResponse?.data
    }
}
