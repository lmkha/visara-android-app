package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
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
    @param:AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @param:UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val json: Json,
) {
    fun login(username: String, password: String): Response {
        val url = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("auth/sign-in")
            .build()

        val requestBody = json.encodeToString(
            mapOf("username" to username, "password" to password)
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun checkUsernameAvailability(username: String): Response {
        val url = BuildConfig.API_URL.toHttpUrl().newBuilder()
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
        val url = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/checkEmailAvailability")
            .addQueryParameter("email", email)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun updateUser(
        isPrivate: Boolean? = null,
        fullName: String? = null,
        bio: String? = null,
    ) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/")
            .build()

        val payload = json.encodeToString(
            mapOf(
                "isPrivate" to isPrivate,
                "fullName" to fullName,
                "bio" to bio,
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url(url)
            .put(payload)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun registerAccount(
        username: String,
        password: String,
        email: String,
        phone: String,
        isPrivate: Boolean,
        dateOfBirth: String
    ): Response {
        val url = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("auth/register")
            .build()

        val requestBody = json.encodeToString(
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

    fun addFcmToken(token: String, username: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("notifications/token")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post("".toRequestBody())
            .addHeader("X-FCMToken", token)
            .addHeader("X-Username", username)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun removeFcmToken(token: String, username: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("notifications/token")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .delete()
            .addHeader("X-FCMToken", token)
            .addHeader("X-Username", username)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }
}
