package com.example.visara.data.remote.api

import androidx.media3.exoplayer.dash.manifest.BaseUrl
import com.example.visara.BuildConfig
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject

class CommentApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {

    fun addComment(videoId: String, replyTo: String, content: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("comments/")
            .build()

        val requestBody: RequestBody = gson.toJson(
            mapOf(
                "videoId" to videoId,
                "replyTo" to replyTo,
                "content" to content,
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun getCommentById(commentId: String) : Response  {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("comments")
            .addPathSegment(commentId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun getAllParentCommentsByVideoId(
        videoId: String,
        order: String,
        page: Int = 0,
        size: Int = 10,
    ) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("comments/video/parent")
            .addPathSegment(videoId)
            .addQueryParameter("order", order)
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", size.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }
    fun getAllChildrenComment(
        parentId: String,
        order: String,
        page: Int = 0,
        size: Int = 10,
    ) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("comments")
            .addPathSegment(parentId)
            .addPathSegment("children")
            .addQueryParameter("order", order)
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", size.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun likeComment(commentId: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("comments/like")
            .addPathSegment(commentId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post("".toRequestBody())
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun unlikeComment(commentId: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("comments/like")
            .addPathSegment(commentId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .delete()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun checkCommentLike(commentId: String) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("comments/like")
            .addPathSegment(commentId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun deleteComment(commentId: String) : Response {
        val url: HttpUrl  = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("comments")
            .addPathSegment(commentId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .delete()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun updateComment(commentId: String, content: String) : Response {
        val url: HttpUrl  = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("comments")
            .build()

        val requestBody: RequestBody = gson.toJson(
            mapOf(
                "id" to commentId,
                "content" to content
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }
}
