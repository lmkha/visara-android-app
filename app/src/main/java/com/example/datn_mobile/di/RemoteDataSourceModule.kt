package com.example.datn_mobile.di

import com.example.datn_mobile.data.remote.api.AuthApi
import com.example.datn_mobile.data.remote.api.UserApi
import com.example.datn_mobile.data.remote.datasource.AuthRemoteDataSource
import com.example.datn_mobile.data.remote.datasource.UserRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {
    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(authApi: AuthApi) : AuthRemoteDataSource {
        return AuthRemoteDataSource(authApi)
    }

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(userApi: UserApi) : UserRemoteDataSource {
        return UserRemoteDataSource(userApi)
    }
}
