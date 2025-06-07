package com.example.visara.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.visara.di.AppSettingsLocalDataSource
import com.example.visara.ui.theme.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    val appSettingsFlow = appContext.AppSettingsLocalDataSource.data
    private val fcmTokenKey = stringPreferencesKey("fcm_token_pref")

    companion object {
        val themeKey = stringPreferencesKey("theme_pref")
    }

    suspend fun updateTheme(theme: AppTheme) {
        withContext(Dispatchers.IO) {
            appContext.AppSettingsLocalDataSource.edit { prefs ->
                prefs[themeKey] = theme.name
            }
        }
    }

    suspend fun updateFCMToken(newToken: String) {
        if (newToken.isBlank()) return
        withContext(Dispatchers.IO) {
            appContext.AppSettingsLocalDataSource.edit { prefs ->
                prefs[fcmTokenKey] = newToken
            }
        }
    }

    suspend fun getFcmToken() : String? {
        return withContext(Dispatchers.IO) {
            appContext.AppSettingsLocalDataSource.data.first()[fcmTokenKey]
        }
    }
}
