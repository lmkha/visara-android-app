package com.example.visara.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.visara.di.AppSettingsLocalDataSource
import com.example.visara.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppSettingsRepository @Inject constructor(private val appContext: Context) {
    val appSettingsFlow = appContext.AppSettingsLocalDataSource.data
    val themeKey = stringPreferencesKey("theme_pref")

    suspend fun updateTheme(theme: AppTheme) {
        withContext(Dispatchers.IO) {
            appContext.AppSettingsLocalDataSource.edit { prefs ->
                prefs[themeKey] = theme.name
            }
        }
    }
}
