package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject

class AuthApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    suspend fun login(username: String, password: String): Response {
        return withContext(Dispatchers.IO) {
            val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                .addPathSegments("auth/signIn")
                .build()

            val requestBody = gson.toJson(
                mapOf("username" to username, "password" to password)
            ).toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            return@withContext unauthorizedOkHttpClient.newCall(request).execute()
        }
    }
    suspend fun checkUsernameAvailability(username: String): Response {
        return withContext(Dispatchers.IO) {
            val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                .addPathSegments("users/checkUsernameAvailability")
                .addQueryParameter("username", username)
                .build()

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            return@withContext unauthorizedOkHttpClient.newCall(request).execute()
        }
    }

    suspend fun checkEmailAvailability(email: String): Response {
        return withContext(Dispatchers.IO) {
            val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                .addPathSegments("users/checkEmailAvailability")
                .addQueryParameter("email", email)
                .build()

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            return@withContext unauthorizedOkHttpClient.newCall(request).execute()
        }
    }

    suspend fun registerAccount(
        username: String,
        password: String,
        email: String,
        phone: String,
        isPrivate: Boolean,
        dateOfBirth: String
    ): Response {
        return withContext(Dispatchers.IO) {
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

            return@withContext unauthorizedOkHttpClient.newCall(request).execute()
        }
    }

}
