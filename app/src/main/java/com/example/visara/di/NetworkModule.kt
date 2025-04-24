package com.example.visara.di

import com.example.visara.data.local.shared_preference.TokenStorage
import com.example.visara.data.remote.api.RefreshTokenApi
import com.example.visara.data.remote.interceptor.AuthInterceptor
import com.example.visara.data.remote.interceptor.BaseUrlInterceptor
import com.example.visara.data.remote.interceptor.TokenAuthenticator
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
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

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenStorage: TokenStorage,
        refreshTokenApi: RefreshTokenApi,
        gson: Gson,
    ) : Authenticator {
        return TokenAuthenticator(tokenStorage, refreshTokenApi, gson)
    }

    @AuthorizedOkHttpClient
    @Provides
    @Singleton
    fun provideAuthorizedOkHttpClient(authInterceptor: AuthInterceptor, tokenAuthenticator: TokenAuthenticator) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
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
