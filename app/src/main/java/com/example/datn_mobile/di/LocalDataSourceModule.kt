package com.example.datn_mobile.di

import com.example.datn_mobile.data.local.datasource.AuthLocalDataSource
import com.example.datn_mobile.data.local.preference.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataSourceModule {
    @Provides
    @Singleton
    fun provideAuthLocalDataSource(tokenStorage: TokenStorage) : AuthLocalDataSource {
        return AuthLocalDataSource(tokenStorage)
    }
}