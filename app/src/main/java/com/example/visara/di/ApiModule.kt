package com.example.visara.di

import com.example.visara.data.remote.api.AuthApi
import com.example.visara.data.remote.api.RefreshTokenApi
import com.example.visara.data.remote.api.UserApi
import com.example.visara.data.remote.api.VideoApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideAuthApi(
        @AuthorizedOkHttpClient authorizedOkHttpClient: OkHttpClient,
        @UnauthenticatedOkhttpClient unauthenticatedOkhttpClient: OkHttpClient,
        gson: Gson,
    ) : AuthApi {
        return AuthApi(authorizedOkHttpClient, unauthenticatedOkhttpClient, gson)
    }

    @Provides
    @Singleton
    fun provideUserApi(
        @AuthorizedOkHttpClient authorizedOkHttpClient: OkHttpClient,
        @UnauthenticatedOkhttpClient unauthenticatedOkhttpClient: OkHttpClient,
        gson: Gson,
    ) : UserApi {
        return UserApi(authorizedOkHttpClient, unauthenticatedOkhttpClient, gson)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenApi(@UnauthenticatedOkhttpClient unauthenticatedOkhttpClient: OkHttpClient) : RefreshTokenApi {
        return RefreshTokenApi(unauthenticatedOkhttpClient)
    }

    @Provides
    @Singleton
    fun provideVideoApi(
        @AuthorizedOkHttpClient authorizedOkHttpClient: OkHttpClient,
        @UnauthenticatedOkhttpClient unauthenticatedOkhttpClient: OkHttpClient,
        gson: Gson,
    ) : VideoApi {
        return VideoApi(authorizedOkHttpClient, unauthenticatedOkhttpClient, gson)
    }
}
