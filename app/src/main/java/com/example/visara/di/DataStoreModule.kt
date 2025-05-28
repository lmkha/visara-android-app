package com.example.visara.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore

val Context.AppSettingsLocalDataSource: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
