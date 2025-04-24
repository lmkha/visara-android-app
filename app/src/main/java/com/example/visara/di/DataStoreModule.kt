package com.example.visara.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


val Context.AppSettingsLocalDataSource: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(
    name = "app_settings"
)
