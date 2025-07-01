package com.example.visara.di

import android.app.LocaleManager
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGson() : Gson {
        return GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create()
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
