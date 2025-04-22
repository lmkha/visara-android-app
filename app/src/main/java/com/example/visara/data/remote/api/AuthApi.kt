package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.data.remote.dto.EmailAvailabilityDto
import com.example.visara.data.remote.dto.RegisterAccountDto
import com.example.visara.data.remote.dto.UsernameAvailabilityDto
import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.ApiError
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun login(username: String, password: String): ApiResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                    .addPathSegments("auth/signIn")
                    .build()

                val requestBody = gson.toJson(
                    mapOf(
                        "username" to username,
                        "password" to password,
                    )
                ).toRequestBody("application/json".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                unauthorizedOkHttpClient.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        return@withContext ApiResult.Failure(
                            ApiError(
                                code = response.code,
                                errorCode = response.code.toString(),
                                message = response.message,
                                rawBody = responseBody
                            )
                        )
                    }

                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val token = jsonObject["data"] as? String

                    return@withContext if (!token.isNullOrEmpty()) {
                        ApiResult.Success(token)
                    } else {
                        ApiResult.Failure(
                            ApiError(
                                code = response.code,
                                errorCode = response.code.toString(),
                                message = "Token not found in response.",
                                rawBody = responseBody
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }
    suspend fun checkUsernameAvailability(username: String): ApiResult<UsernameAvailabilityDto> {
        return withContext(Dispatchers.IO) {
            try {
                val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                    .addPathSegments("users/checkUsernameAvailability")
                    .addQueryParameter("username", username)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                unauthorizedOkHttpClient.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        return@withContext ApiResult.Failure(
                            ApiError(
                                code = response.code,
                                errorCode = response.code.toString(),
                                message = response.message,
                                rawBody = responseBody
                            )
                        )
                    }

                    val result = gson.fromJson(responseBody, UsernameAvailabilityDto::class.java)
                    return@withContext ApiResult.Success(result)
                }
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }
    suspend fun checkEmailAvailability(email: String): ApiResult<EmailAvailabilityDto> {
        return withContext(Dispatchers.IO) {
            try {
                val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                    .addPathSegments("users/checkEmailAvailability")
                    .addQueryParameter("email", email)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                unauthorizedOkHttpClient.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        return@withContext ApiResult.Failure(
                            ApiError(
                                code = response.code,
                                errorCode = response.code.toString(),
                                message = response.message,
                                rawBody = responseBody
                            )
                        )
                    }

                    val result = gson.fromJson(responseBody, EmailAvailabilityDto::class.java)
                    return@withContext ApiResult.Success(result)
                }
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }
    suspend fun registerAccount(
        username: String,
        password: String,
        email: String,
        phone: String,
        isPrivate: Boolean,
        dateOfBirth: String
    ): ApiResult<RegisterAccountDto> {
        return withContext(Dispatchers.IO) {
            try {
                val url = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                    .addPathSegments("auth/register")
                    .build()

                val requestBody: RequestBody = gson.toJson(
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

                unauthorizedOkHttpClient.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                            return@withContext ApiResult.Failure(
                                ApiError(
                                    code = response.code,
                                    errorCode = response.code.toString(),
                                    message = response.message,
                                    rawBody = responseBody
                                )
                            )
                        }
                    }

                    val result = gson.fromJson(responseBody, RegisterAccountDto::class.java)
                    return@withContext ApiResult.Success(result)
                }
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }
}
