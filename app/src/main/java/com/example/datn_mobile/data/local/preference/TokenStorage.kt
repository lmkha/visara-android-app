package com.example.datn_mobile.data.local.preference

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenStorage @Inject constructor(context: Context) {
    private val prefName = "app_prefs"
    private val keyAccessToken = "ACCESS_TOKEN"
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val encryptedSharedPreference: SharedPreferences = EncryptedSharedPreferences.create(
        prefName,
        masterKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun getToken() : String? {
        return encryptedSharedPreference.getString(keyAccessToken, "")
    }

    fun saveToken(token: String) {
        encryptedSharedPreference.edit { putString(keyAccessToken, token) }
    }

    fun removeToken() {
        encryptedSharedPreference.edit { remove(keyAccessToken) }
    }
}
