package com.example.datn_mobile.di

import com.example.datn_mobile.data.local.datasource.AuthLocalDataSource
import com.example.datn_mobile.data.remote.datasource.AuthRemoteDataSource
import com.example.datn_mobile.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource,
        authLocalDataSource: AuthLocalDataSource,
    ) : AuthRepository {
        return AuthRepository(authRemoteDataSource, authLocalDataSource)
    }
}
