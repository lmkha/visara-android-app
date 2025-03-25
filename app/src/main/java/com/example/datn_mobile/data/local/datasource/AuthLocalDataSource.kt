package com.example.datn_mobile.data.local.datasource

import com.example.datn_mobile.data.local.preference.TokenStorage
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val tokenStorage: TokenStorage,
) {
    fun getToken() : String? = tokenStorage.getToken()
    fun saveToken(token: String) {
        tokenStorage.saveToken(token)
    }
    fun removeToken() {
        tokenStorage.removeToken()
    }
}
