package com.example.datn_mobile.data.local.preference

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class TokenStorage @Inject constructor(context: Context) {
    private val prefName = "app_prefs"
    private val keyAccessToken = "ACCESS_TOKEN"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun getToken() : String {
        return sharedPreferences.getString(keyAccessToken, "") ?: ""
    }

    fun saveToken(token: String) {
        sharedPreferences.edit { putString(keyAccessToken, token) }
    }

    fun removeToken() {
        sharedPreferences.edit { remove(keyAccessToken) }
    }
}
