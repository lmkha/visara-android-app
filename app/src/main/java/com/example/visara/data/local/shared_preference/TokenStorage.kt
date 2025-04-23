package com.example.visara.data.local.shared_preference

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenStorage @Inject constructor(
    private val encryptedSharedPreference: SharedPreferences,
    private val userSessionManager: UserSessionManager,
) {
    // Use get() to ensure the key always reflects the latest current user ID at access time
    private val keyAccessToken: String get() {
        val currentUsername = userSessionManager.getCurrentUsername()
        return "ACCESS_TOKEN_$currentUsername"
    }

    fun getToken(): String? {
        return userSessionManager.getCurrentUsername()?.let {
            encryptedSharedPreference.getString(keyAccessToken, null)
        }
    }

    suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        userSessionManager.getCurrentUsername()?.let {
            encryptedSharedPreference.edit { putString(keyAccessToken, token) }
        }
    }

    suspend fun removeToken() = withContext(Dispatchers.IO) {
        userSessionManager.getCurrentUsername()?.let {
            encryptedSharedPreference.edit { remove(keyAccessToken) }
        }
    }
}
