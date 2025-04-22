package com.example.visara.data.local.shared_preference

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TokenStorage @Inject constructor(
    private val encryptedSharedPreference: SharedPreferences,
    private val userSessionManager: UserSessionManager
) {
    // Use get() to ensure the key always reflects the latest current user ID at access time
    private val keyAccessToken: String get() = "ACCESS_TOKEN_${userSessionManager.getCurrentUsername()}"
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val token = getTokenInternal()
            _isLoggedIn.value = !token.isNullOrEmpty()
        }
    }
    private suspend fun getTokenInternal(): String? = withContext(Dispatchers.IO) {
        encryptedSharedPreference.getString(keyAccessToken, null)
    }

    fun getToken(): String? {
        return encryptedSharedPreference.getString(keyAccessToken, null)
    }

    suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        encryptedSharedPreference.edit { putString(keyAccessToken, token) }
        _isLoggedIn.value = true
    }

    suspend fun removeToken() = withContext(Dispatchers.IO) {
        encryptedSharedPreference.edit { remove(keyAccessToken) }
        _isLoggedIn.value = false
    }
}
