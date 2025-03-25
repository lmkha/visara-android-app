package com.example.datn_mobile.data.remote.interceptor

import com.example.datn_mobile.data.local.preference.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenStorage: TokenStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token: String? = tokenStorage.getToken()
        if (token.isNullOrEmpty()) throw IllegalStateException("Access token is not exist in storage!")
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}
