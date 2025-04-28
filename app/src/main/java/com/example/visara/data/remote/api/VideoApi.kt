package com.example.visara.data.remote.api

import com.example.visara.BuildConfig
import com.example.visara.common.getMimeTypeOrNull
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

class VideoApi @Inject constructor(
    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    fun getRandomVideos(numOfVideos: Int = 10) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/random")
            .addQueryParameter("count", numOfVideos.toString())
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
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
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
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
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

    fun uploadVideoMetaData(title: String, description: String, hashtags: List<String>, isPrivate: Boolean, isCommentOff: Boolean) : Response {
        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegments("videos/new/")
            .build()

        val requestBody: RequestBody = gson.toJson(
            mapOf(
                "title" to title,
                "description" to description,
                "tags" to hashtags,
                "isPrivate" to isPrivate,
                "isCommentOff" to isCommentOff,
            )
        ).toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

}
