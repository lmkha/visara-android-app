package com.example.datn_mobile.data.remote.api

import com.example.datn_mobile.BuildConfig
import com.example.datn_mobile.data.remote.dto.UserDto
import com.example.datn_mobile.data.remote.response.ApiResponse
import com.example.datn_mobile.di.AuthorizedOkHttpClient
import com.example.datn_mobile.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class UserApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    fun getPublicUserByUsername(username: String) : ApiResponse<UserDto>? {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("users")
            .addPathSegment(username)
            .addPathSegment("profile")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        unauthorizedOkHttpClient.newCall(request).execute().use { response->
            val responseBody = response.body?.string()
            if (!response.isSuccessful) {
                println("API Error: ${response.code} - ${response.message} - $responseBody")
                return null
            }
            if (responseBody.isNullOrEmpty()) return null
            return gson.fromJson(responseBody, object : TypeToken<ApiResponse<UserDto>>() {}.type)
        }
    }

    fun getCurrentUser() : ApiResponse<UserDto>? {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        authorizedOkHttpClient.newCall(request).execute().use { response->
            val responseBody = response.body?.string()
            if (!response.isSuccessful) {
                println("API Error: ${response.code} - ${response.message} - $responseBody")
                return null
            }
            if (responseBody.isNullOrEmpty()) return null
            return gson.fromJson(responseBody, object : TypeToken<ApiResponse<UserDto>>() {}.type)
        }
    }
}
