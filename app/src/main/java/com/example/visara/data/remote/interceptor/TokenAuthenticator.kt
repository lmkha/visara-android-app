package com.example.visara.data.remote.interceptor

import android.util.Log
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
import kotlin.collections.get

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
        Log.i("CHECK_VAR", "refresh token: $refreshToken")
        Log.i("CHECK_VAR", "current username: $currentUsername")
        Log.i("CHECK_VAR", "call refresh: ${newTokenApiResponseBody.toString()}")

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
