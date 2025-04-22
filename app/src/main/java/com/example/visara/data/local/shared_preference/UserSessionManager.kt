package com.example.visara.data.local.shared_preference

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class UserSessionManager @Inject constructor(private val sharedPrefs: SharedPreferences) {
    companion object {
        private const val CURRENT_USER_KEY = "CURRENT_USERNAME"
    }

    fun getCurrentUsername(): String? {
        return sharedPrefs.getString(CURRENT_USER_KEY, null)
    }

    fun setCurrentUsername(username: String) {
        sharedPrefs.edit { putString(CURRENT_USER_KEY, username) }
    }

    fun clearCurrentUsername() {
        sharedPrefs.edit { remove(CURRENT_USER_KEY) }
    }
}
