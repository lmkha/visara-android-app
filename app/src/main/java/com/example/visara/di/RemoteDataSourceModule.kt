package com.example.visara.di

import com.example.visara.data.remote.api.AuthApi
import com.example.visara.data.remote.api.UserApi
import com.example.visara.data.remote.datasource.AuthRemoteDataSource
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import com.google.gson.Gson
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
    fun provideAuthRemoteDataSource(authApi: AuthApi, gson: Gson) : AuthRemoteDataSource {
        return AuthRemoteDataSource(authApi, gson)
    }

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(userApi: UserApi, gson: Gson) : UserRemoteDataSource {
        return UserRemoteDataSource(userApi, gson)
    }
}
