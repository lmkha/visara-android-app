package com.example.visara.data.remote.interceptor

import com.example.visara.data.local.shared_preference.TokenStorage
import com.example.visara.data.remote.api.RefreshTokenApi
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val refreshTokenApi: RefreshTokenApi,
    private val gson: Gson,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenStorage.getRefreshToken() ?: return null
        val newTokenApiResponse = runBlocking { refreshTokenApi.refreshToken(refreshToken) }
        val newTokenApiResponseBody = newTokenApiResponse.body?.string()

        if (newTokenApiResponse.isSuccessful && !newTokenApiResponseBody.isNullOrEmpty()) {
            val jsonObject = gson.fromJson(newTokenApiResponseBody, Map::class.java)
            val data = jsonObject["data"]

            val newToken = if (data is String && data.isNotBlank()) data else null

            if (!newToken.isNullOrEmpty()) {
                runBlocking { tokenStorage.saveAccessToken(newToken) }

                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            }
        }
        return null
    }
}
