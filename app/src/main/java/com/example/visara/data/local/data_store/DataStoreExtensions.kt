package com.example.visara.data.local.data_store

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.settingsDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)
