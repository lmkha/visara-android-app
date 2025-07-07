package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentApi @Inject constructor(
    @param:AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @param:UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val json: Json,
) {

    fun addComment(videoId: String, replyTo: String?, content: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("comments/")
            .build()

        val payload = mutableMapOf<String, Any>(
            "videoId" to videoId,
            "content" to content,
        )
        replyTo?.let { payload["replyTo"] = it }


        val requestBody: RequestBody = json.encodeToString(payload)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun getCommentById(commentId: String) : Response  {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
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
        needAuthenticate: Boolean = false,
        videoId: String,
        order: String,
        page: Int = 0,
        size: Int = 10,
    ) : Response {
        val urlBuilder: HttpUrl.Builder = BuildConfig.API_URL.toHttpUrl().newBuilder()

        if (needAuthenticate) {
            urlBuilder.addPathSegments("comments/video/parent/authenticated")
        } else {
            urlBuilder.addPathSegments("comments/video/parent")
        }

        val url: HttpUrl = urlBuilder
            .addPathSegment(videoId)
            .addQueryParameter("order", order)
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", size.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return if (needAuthenticate) authorizedOkHttpClient.newCall(request).execute()
        else unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun getAllChildrenComment(
        needAuthenticate: Boolean = false,
        parentId: String,
        order: String,
        page: Int = 0,
        size: Int = 10,
    ) : Response {
        val urlBuilder= BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("comments")
            .addPathSegment(parentId)

        if (needAuthenticate) {
            urlBuilder.addPathSegments("children/authenticated")
        } else {
            urlBuilder.addPathSegment("children")
        }

        val url: HttpUrl = urlBuilder
            .addQueryParameter("order", order)
            .addQueryParameter("page", page.toString())
            .addQueryParameter("size", size.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return if (needAuthenticate) authorizedOkHttpClient.newCall(request).execute()
        else unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun likeComment(commentId: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
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
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
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
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
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
        val url: HttpUrl  = BuildConfig.API_URL.toHttpUrl().newBuilder()
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
        val url: HttpUrl  = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("comments")
            .build()

        val requestBody: RequestBody = json.encodeToString(
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
