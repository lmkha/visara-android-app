package com.example.datn_mobile.data.remote.interceptor

import com.example.datn_mobile.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BaseUrlInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        if (originalUrl.host.isNotEmpty()) {
            return chain.proceed(originalRequest)
        }

        val newUrl: HttpUrl = originalUrl.newBuilder()
            .scheme("http")
            .host(BuildConfig.BASE_URL.removePrefix("http://").removePrefix("https://"))
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
