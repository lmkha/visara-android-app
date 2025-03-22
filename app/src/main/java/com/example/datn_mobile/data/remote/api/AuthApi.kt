package com.example.datn_mobile.data.remote.api

import com.example.datn_mobile.BuildConfig
import com.example.datn_mobile.data.remote.dto.LoginResponse
import com.example.datn_mobile.di.AuthorizedOkHttpClient
import com.example.datn_mobile.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AuthApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    fun login(username: String, password: String): LoginResponse? {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments(ApiEndpoints.Auth.LOGIN)
            .build()

        val requestBody: RequestBody = gson.toJson(
            mapOf(
                "username" to username,
                "password" to password,
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        unauthorizedOkHttpClient.newCall(request).execute().use { response->
            val responseBody = response.body?.string()
            if (!response.isSuccessful) {
                println("API Error: ${response.code} - ${response.message} - $responseBody")
                return null
            }
            if (responseBody.isNullOrEmpty()) return null
            return gson.fromJson(responseBody, LoginResponse::class.java)
        }
    }
}
