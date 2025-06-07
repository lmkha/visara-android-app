package com.example.visara.data.remote.interceptor

import com.example.visara.data.local.shared_preference.TokenStorage
import com.example.visara.data.local.shared_preference.UserSessionManager
import com.example.visara.data.remote.api.RefreshTokenApi
import com.example.visara.data.remote.dto.RefreshTokenDto
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.get

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val userSessionManager: UserSessionManager,
    private val refreshTokenApi: RefreshTokenApi,
    private val gson: Gson,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenStorage.getRefreshToken() ?: return null
        val currentUsername = userSessionManager.getCurrentUsername() ?: return null
        val newTokenApiResponse = runBlocking { refreshTokenApi.refreshToken(refreshToken, currentUsername) }
        val newTokenApiResponseBody = newTokenApiResponse.body?.string()

        if (newTokenApiResponse.isSuccessful && !newTokenApiResponseBody.isNullOrEmpty()) {
            val jsonObject = gson.fromJson(newTokenApiResponseBody, Map::class.java)
            val dataJson = gson.toJson(jsonObject["data"])
            val refreshTokenDto = gson.fromJson(dataJson, RefreshTokenDto::class.java)

            val newToken = refreshTokenDto?.accessToken

            if (!newToken.isNullOrBlank()) {
                runBlocking { tokenStorage.saveAccessToken(newToken) }

                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            }
        }
        return null
    }
}
