package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class VideoApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    suspend fun getRandomVideos(numOfVideos: Int = 10) : Response {
        return withContext(Dispatchers.IO) {
            val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
                .addPathSegments("videos/random")
                .addQueryParameter("count", numOfVideos.toString())
                .build()

            val request: Request = Request.Builder()
                .url(url)
                .get()
                .build()

            return@withContext unauthorizedOkHttpClient.newCall(request).execute()
        }
    }
}
