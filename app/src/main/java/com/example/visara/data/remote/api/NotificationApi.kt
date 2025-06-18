package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.di.AuthorizedOkHttpClient
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
) {
    fun getAllNotifications(username: String, page: Int, size: Int) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("notifications/messages")
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", size.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-Username", username)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }
}
