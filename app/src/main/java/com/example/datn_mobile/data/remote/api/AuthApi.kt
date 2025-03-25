package com.example.datn_mobile.data.remote.api

import com.example.datn_mobile.BuildConfig
import com.example.datn_mobile.data.remote.dto.EmailAvailabilityDto
import com.example.datn_mobile.data.remote.dto.RegisterAccountDto
import com.example.datn_mobile.data.remote.dto.UsernameAvailabilityDto
import com.example.datn_mobile.data.remote.response.ApiResponse
import com.example.datn_mobile.di.AuthorizedOkHttpClient
import com.example.datn_mobile.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    fun login(username: String, password: String): ApiResponse<String>? {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("auth/signIn")
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
            return gson.fromJson(responseBody, object : TypeToken<ApiResponse<String>>() {}.type)
        }
    }

    fun checkUsernameAvailability(username: String) : ApiResponse<UsernameAvailabilityDto>? {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/checkUsernameAvailability")
            .addQueryParameter("username", username)
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
            return gson.fromJson(responseBody, object : TypeToken<ApiResponse<UsernameAvailabilityDto>>() {}.type)
        }
    }

    fun checkEmailAvailability(email: String) : ApiResponse<EmailAvailabilityDto>? {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/checkEmailAvailability")
            .addQueryParameter("email", email)
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
            return gson.fromJson(responseBody, object : TypeToken<ApiResponse<EmailAvailabilityDto>>() {}.type)
        }
    }

    fun registerAccount(
        username: String,
        password: String,
        email: String,
        phone: String,
        isPrivate: Boolean,
        dateOfBirth: String
    ) : ApiResponse<RegisterAccountDto>? {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("auth/register")
            .build()

        val requestBody: RequestBody = gson.toJson(
            mapOf(
                "username" to username,
                "password" to password,
                "email" to email,
                "phone" to phone,
                "isPrivate" to isPrivate,
                "dateOfBirth" to dateOfBirth,
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
            return gson.fromJson(responseBody, object : TypeToken<ApiResponse<RegisterAccountDto>>() {}.type)
        }

        return null
    }




}
