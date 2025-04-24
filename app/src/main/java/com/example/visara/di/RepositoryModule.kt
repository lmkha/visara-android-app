package com.example.visara.di

import android.content.Context
import com.example.visara.data.local.dao.UserDao
import com.example.visara.data.local.datasource.AuthLocalDataSource
import com.example.visara.data.remote.datasource.AuthRemoteDataSource
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import com.example.visara.data.repository.AppSettingsRepository
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        userRepository: UserRepository,
        userDao: UserDao,
    ) : AuthRepository {
        return AuthRepository(authRemoteDataSource, authLocalDataSource, userRepository, userDao)
    }


    @Provides
    @Singleton
    fun provideUserRepository(
        userRemoteDataSource: UserRemoteDataSource,
    ) : UserRepository {
        return UserRepository(userRemoteDataSource)
    }


    @Provides
    @Singleton
    fun provideAppSettingsRepository(@ApplicationContext appContext: Context) : AppSettingsRepository {
        return AppSettingsRepository(appContext)
    }
}
