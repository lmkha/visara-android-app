package com.example.datn_mobile.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnauthenticatedOkhttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthorizedOkHttpClient
