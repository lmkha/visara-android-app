package com.example.visara.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.visara.data.local.shared_preference.TokenStorage
import com.example.visara.data.local.shared_preference.UserSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefModule {
    private const val PREFS_FILE_NAME ="app_prefs"
    @Provides
    @Singleton
    fun provideEncryptedSharedPreference(@ApplicationContext context: Context) : SharedPreferences {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val encryptedSharedPreference: SharedPreferences = EncryptedSharedPreferences.create(
            PREFS_FILE_NAME,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        return encryptedSharedPreference
    }

    @Provides
    @Singleton
    fun provideUserSessionManager(sharedPrefs: SharedPreferences): UserSessionManager {
        return UserSessionManager(sharedPrefs)
    }
    @Provides
    @Singleton
    fun providerTokenStorage(encryptedSharedPreference: SharedPreferences, userSessionManager: UserSessionManager) : TokenStorage {
        return TokenStorage(encryptedSharedPreference, userSessionManager)
    }
}
