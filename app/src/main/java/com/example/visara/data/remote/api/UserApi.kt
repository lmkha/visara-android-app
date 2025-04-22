package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.data.remote.ApiError
import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.dto.UserDto
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun getPublicUserByUsername(username: String): ApiResult<UserDto> {
        return withContext(Dispatchers.IO) {
            try {
                val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                    .addPathSegment("users")
                    .addPathSegment(username)
                    .addPathSegment("profile")
                    .build()

                val request: Request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                unauthorizedOkHttpClient.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        return@withContext ApiResult.Failure(
                            ApiError(
                                code = response.code,
                                message = "API Error: ${response.code} - ${response.message} - $responseBody",
                                rawBody = responseBody
                            )
                        )
                    }

                    val userDto = gson.fromJson(responseBody, UserDto::class.java)
                    return@withContext ApiResult.Success(userDto)
                }
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }
    suspend fun getCurrentUser(): ApiResult<UserDto> {
        return withContext(Dispatchers.IO) {
            try {
                val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                    .addPathSegments("users/")
                    .build()

                val request: Request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                authorizedOkHttpClient.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        return@withContext ApiResult.Failure(
                            ApiError(
                                code = response.code,
                                message = "API Error: ${response.code} - ${response.message} - $responseBody",
                                rawBody = responseBody
                            )
                        )
                    }

                    // Parse the response body into UserDto
                    val userDto = gson.fromJson(responseBody, UserDto::class.java)
                    return@withContext ApiResult.Success(userDto)
                }
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }

}
