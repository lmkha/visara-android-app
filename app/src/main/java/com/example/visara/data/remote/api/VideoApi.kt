package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.utils.getMimeTypeOrNull
import com.example.visara.data.remote.common.ProgressRequestBody
import com.example.visara.di.AuthorizedOkHttpClient
import com.example.visara.di.UnauthenticatedOkhttpClient
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    fun getVideoById(videoId: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("videos")
            .addPathSegment(videoId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun getRandomVideos(numOfVideos: Int = 10) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/random")
            .addQueryParameter("count", numOfVideos.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun getAllVideoByUserId(userId: Long) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/user")
            .addPathSegment(userId.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun uploadVideoFile(
        videoId: String,
        videoFile: File,
        progressListener: (percent: Int) -> Unit = { },
    ) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("file/video/${videoId}")
            .build()

        val mediaType = videoFile.getMimeTypeOrNull()?.toMediaTypeOrNull()

        val progressRequestBody = ProgressRequestBody(
            file = videoFile,
            mediaType =  mediaType,
            progressListener = progressListener,
        )

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = "file",
                filename = videoFile.name,
                body = progressRequestBody
            )
            .build()


        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun uploadThumbnailFile(
        videoId: String,
        thumbnailFile: File,
        progressListener: (percent: Int) -> Unit = { },
    ) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/${videoId}/thumbnail")
            .build()

        val mediaType = thumbnailFile.getMimeTypeOrNull()?.toMediaTypeOrNull()

        val progressRequestBody = ProgressRequestBody(
            file = thumbnailFile,
            mediaType =  mediaType,
            progressListener = progressListener,
        )

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = "file",
                filename = thumbnailFile.name,
                body = progressRequestBody
            )
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun uploadVideoMetaData(
        title: String,
        description: String,
        hashtags: List<String>,
        isPrivate: Boolean,
        isCommentOff: Boolean,
        playlistIds: List<String>,
    ) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/new/")
            .build()

        val requestBody: RequestBody = gson.toJson(
            mapOf(
                "title" to title,
                "description" to description,
                "tags" to hashtags,
                "isPrivate" to isPrivate,
                "isCommentOff" to isCommentOff,
                "playlistIds" to playlistIds,
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun likeVideo(videoId: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/likes")
            .addPathSegment(videoId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post("".toRequestBody())
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun unlikeVideo(videoId: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/likes")
            .addPathSegment(videoId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .delete()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun getIsVideoLiked(videoId: String): Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/likes")
            .addPathSegment(videoId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun searchVideo(type: String, pattern: String, count: Long) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/search")
            .addQueryParameter("type", type)
            .addQueryParameter("pattern", pattern)
            .addQueryParameter("count", count.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun updateVideo(
        videoId: String,
        title: String,
        description: String,
        hashtags: List<String>,
        isCommentOff: Boolean,
        isPrivate: Boolean
    ): Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("videos")
            .build()

        val payload = mapOf(
            "id" to videoId,
            "title" to title,
            "description" to description,
            "tags" to hashtags,
            "isCommentOff" to isCommentOff,
            "isPrivate" to isPrivate
        )

        val requestBody = gson.toJson(payload)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun increaseVideoView(videoId: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("videos")
            .addPathSegment(videoId)
            .addPathSegment("view")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun addVideoToHistory(
        videoId: String,
        videoTitle: String,
        thumbnailUrl: String,
        ownerId: String,
        ownerUsername: String,
        ownerFullName: String,
        viewerId: String,
        viewerUsername: String,
    ) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/history")
            .build()

        val requestBody = gson.toJson(
            mapOf(
                "videoId" to videoId,
                "videoTitle" to videoTitle,
                "thumbnailUrl" to thumbnailUrl,
                "ownerId" to ownerId,
                "ownerUsername" to ownerUsername,
                "ownerFullname" to ownerFullName,
                "viewerId" to viewerId,
                "viewerUsername" to viewerUsername,
            )
        ).toRequestBody(
            contentType = "application/json".toMediaTypeOrNull()
        )

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun getFollowingVideos(count: Long) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/following/random")
            .addQueryParameter("count", count.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun deleteVideo(videoId: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("videos")
            .addPathSegment(videoId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .delete()
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }
}
