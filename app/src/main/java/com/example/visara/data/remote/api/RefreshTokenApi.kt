package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshTokenApi @Inject constructor(
    @param:UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    fun refreshToken(refreshToken: String, username: String) : Response {
        val url = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("auth/refresh")
            .build()

        val requestBody: RequestBody = gson.toJson(
            mapOf("username" to username)
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("rt", refreshToken)
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }
}
