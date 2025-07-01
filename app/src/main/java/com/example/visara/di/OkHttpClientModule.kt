package com.example.visara.di

import com.example.visara.data.remote.interceptor.AuthInterceptor
import com.example.visara.data.remote.interceptor.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OkHttpClientModule {
    @AuthorizedOkHttpClient
    @Provides
    @Singleton
    fun provideAuthorizedOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ) : OkHttpClient {
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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnauthenticatedOkhttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthorizedOkHttpClient
