package com.example.visara.data.local.shared_preference

import android.content.SharedPreferences
import android.util.Base64
import javax.inject.Inject
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TokenStorage @Inject constructor(
    private val encryptedSharedPreference: SharedPreferences,
    private val userSessionManager: UserSessionManager,
) {
    // Use get() to ensure the key always reflects the latest current user ID at access time
    private val keyAccessToken: String get() {
        val currentUsername = userSessionManager.getCurrentUsername()
        return "ACCESS_TOKEN_$currentUsername"
    }
    private val keyRefreshToken: String get() {
        val currentUsername = userSessionManager.getCurrentUsername()
        return "REFRESH_TOKEN_$currentUsername"
    }

    fun getAccessToken(): String? {
        return userSessionManager.getCurrentUsername()?.let {
            encryptedSharedPreference.getString(keyAccessToken, null)
        }
    }
    fun getRefreshToken(): String? {
        return userSessionManager.getCurrentUsername()?.let {
            encryptedSharedPreference.getString(keyRefreshToken, null)
        }
    }
    suspend fun saveAccessToken(accessToken: String) = withContext(Dispatchers.IO) {
        userSessionManager.getCurrentUsername()?.let {
            encryptedSharedPreference.edit { putString(keyAccessToken, accessToken) }
        }
    }
    suspend fun saveRefreshToken(refreshToken: String) = withContext(Dispatchers.IO) {
        userSessionManager.getCurrentUsername()?.let {
            encryptedSharedPreference.edit { putString(keyRefreshToken, refreshToken) }
        }
    }
    suspend fun removeAccessToken() = withContext(Dispatchers.IO) {
        userSessionManager.getCurrentUsername()?.let {
            encryptedSharedPreference.edit { remove(keyAccessToken) }
        }
    }
    suspend fun removeRefreshToken() = withContext(Dispatchers.IO) {
        userSessionManager.getCurrentUsername()?.let {
            encryptedSharedPreference.edit { remove(keyRefreshToken) }
        }
    }
    fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true

            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val payload = JSONObject(payloadJson)

            if (!payload.has("exp")) {
                return true
            }

            val exp = payload.getLong("exp")
            val now = System.currentTimeMillis() / 1000

            exp < now
        } catch (_: Exception) {
            true
        }
    }
}
