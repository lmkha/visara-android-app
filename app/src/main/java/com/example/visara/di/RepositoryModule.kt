package com.example.visara.di

import com.example.visara.data.local.datasource.AuthLocalDataSource
import com.example.visara.data.remote.datasource.AuthRemoteDataSource
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
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


    @Provides
    @Singleton
    fun provideUserRepository(
        userRemoteDataSource: UserRemoteDataSource,
    ) : UserRepository {
        return UserRepository(userRemoteDataSource)
    }
}
