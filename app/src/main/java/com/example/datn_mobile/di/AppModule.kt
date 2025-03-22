package com.example.datn_mobile.di

import android.content.Context
import com.example.datn_mobile.data.local.preference.TokenStorage
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
    fun providerTokenStorage(@ApplicationContext context: Context) : TokenStorage {
        return TokenStorage(context)
    }

    @Provides
    @Singleton
    fun provideGson() : Gson {
        return GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create()
    }
}