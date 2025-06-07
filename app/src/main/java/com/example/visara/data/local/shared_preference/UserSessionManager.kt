package com.example.visara.data.local.shared_preference

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit
import javax.inject.Singleton

@Singleton
class UserSessionManager @Inject constructor(private val encryptedSharedPreference: SharedPreferences) {
    companion object {
        private const val CURRENT_USER_KEY = "CURRENT_USERNAME"
    }

    fun getCurrentUsername(): String? {
        return encryptedSharedPreference.getString(CURRENT_USER_KEY, null)
    }

    fun setCurrentUsername(username: String) {
        encryptedSharedPreference.edit { putString(CURRENT_USER_KEY, username) }
    }

    fun clearCurrentUsername() {
        encryptedSharedPreference.edit { remove(CURRENT_USER_KEY) }
    }
}
