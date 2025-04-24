package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.di.UnauthenticatedOkhttpClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class RefreshTokenApi @Inject constructor(@UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient) {
    fun refreshToken(refreshToken: String) : Response {
        val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("auth/refreshToken")
            .addQueryParameter("token", refreshToken)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }
}
