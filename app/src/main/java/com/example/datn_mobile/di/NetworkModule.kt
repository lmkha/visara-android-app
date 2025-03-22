package com.example.datn_mobile.di

import com.example.datn_mobile.data.local.preference.TokenStorage
import com.example.datn_mobile.data.remote.interceptor.AuthInterceptor
import com.example.datn_mobile.data.remote.interceptor.BaseUrlInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenStorage: TokenStorage) : AuthInterceptor {
        return AuthInterceptor(tokenStorage)
    }

    @Provides
    @Singleton
    fun provideBaseUrlInterceptor() : BaseUrlInterceptor {
        return BaseUrlInterceptor()
    }

    @AuthorizedOkHttpClient
    @Provides
    @Singleton
    fun provideAuthorizedOkHttpClient(authInterceptor: AuthInterceptor) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @UnauthenticatedOkhttpClient
    @Provides
    @Singleton
    fun provideUnauthorizedOkHttpClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }
}
