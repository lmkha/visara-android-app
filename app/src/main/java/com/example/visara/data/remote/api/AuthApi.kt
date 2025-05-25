package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    fun login(username: String, password: String): Response {
        val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("auth/sign-in")
            .build()

        val requestBody = gson.toJson(
            mapOf("username" to username, "password" to password)
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun checkUsernameAvailability(username: String): Response {
        val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/checkUsernameAvailability")
            .addQueryParameter("username", username)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun checkEmailAvailability(email: String): Response {
        val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/checkEmailAvailability")
            .addQueryParameter("email", email)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun registerAccount(
        username: String,
        password: String,
        email: String,
        phone: String,
        isPrivate: Boolean,
        dateOfBirth: String
    ): Response {
        val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("auth/register")
            .build()

        val requestBody = gson.toJson(
            mapOf(
                "username" to username,
                "password" to password,
                "email" to email,
                "phone" to phone,
                "isPrivate" to isPrivate,
                "dateOfBirth" to dateOfBirth
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }
}
