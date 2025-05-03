package com.example.visara.di

import com.example.visara.data.local.dao.UserDao
import com.example.visara.data.local.datasource.AuthLocalDataSource
import com.example.visara.data.local.datasource.UserLocalDataSource
import com.example.visara.data.local.shared_preference.TokenStorage
import com.example.visara.data.local.shared_preference.UserSessionManager
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
    fun provideAuthLocalDataSource(tokenStorage: TokenStorage, userSessionManager: UserSessionManager) : AuthLocalDataSource {
        return AuthLocalDataSource(tokenStorage, userSessionManager)
    }

    @Provides
    @Singleton
    fun provideUserLocalDataSource(userDao: UserDao, userSessionManager: UserSessionManager) : UserLocalDataSource {
        return UserLocalDataSource(userDao, userSessionManager)
    }
}
