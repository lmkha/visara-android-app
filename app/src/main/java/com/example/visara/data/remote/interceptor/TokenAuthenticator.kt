package com.example.visara.data.remote.interceptor

import com.example.visara.data.local.shared_preference.TokenStorage
import com.example.visara.data.local.shared_preference.UserSessionManager
import com.example.visara.data.remote.api.RefreshTokenApi
import com.example.visara.data.remote.common.ApiResponse
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.RefreshTokenDto
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val userSessionManager: UserSessionManager,
    private val refreshTokenApi: RefreshTokenApi,
    private val json: Json,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenStorage.getRefreshToken() ?: return null
        val currentUsername = userSessionManager.getCurrentUsername() ?: return null
        val newTokenApiResponse = runBlocking { refreshTokenApi.refreshToken(refreshToken, currentUsername) }
        val newTokenApiResponseBody = newTokenApiResponse.body?.string()

        if (!newTokenApiResponse.isSuccessful || newTokenApiResponseBody.isNullOrEmpty()) return null

        return json.decodeFromString<ApiResponse<RefreshTokenDto>>(newTokenApiResponseBody).toApiResult().let { result ->
            if (result !is ApiResult.Success || result.data == null) return@let null
            val newAccessToken = result.data.accessToken
            if (newAccessToken.isBlank()) return@let null

            runBlocking { tokenStorage.saveAccessToken(newAccessToken) }
            return@let response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }
}
