package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    fun getCurrentUser(): Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun getPublicUser(username: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("users")
            .addPathSegment(username)
            .addPathSegment("public-username")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun searchUser(pattern: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/search")
            .addQueryParameter("pattern", pattern)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun followUser(username: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("users")
            .addPathSegment(username)
            .addPathSegment("follow")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post("".toRequestBody())
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun unfollowUser(username: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("users")
            .addPathSegment(username)
            .addPathSegment("unfollow")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post("".toRequestBody())
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun checkUserIsMyFollower(username: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("users")
            .addPathSegment(username)
            .addPathSegment("checkFollower")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun checkIsFollowingThisUser(username: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("users")
            .addPathSegment(username)
            .addPathSegment("checkFollowing")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun getAllFollower(page: Int, size: Long) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/followers")
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", size.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun getAllFollowing(page: Int, size: Long) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("users/followings")
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", size.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }
}
