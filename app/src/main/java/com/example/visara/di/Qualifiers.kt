package com.example.visara.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnauthenticatedOkhttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthorizedOkHttpClient
