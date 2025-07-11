package com.example.visara.di

import android.app.LocaleManager
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavHostController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideJson() : Json {
        return Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            explicitNulls = false
            useAlternativeNames = true
        }
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideNavHostController(@ApplicationContext appContext: Context) : NavHostController = NavHostController(appContext)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Provides
    @Singleton
    fun provideLocaleManager(@ApplicationContext appContext: Context) : LocaleManager {
        return appContext.getSystemService(LocaleManager::class.java)
    }
}
