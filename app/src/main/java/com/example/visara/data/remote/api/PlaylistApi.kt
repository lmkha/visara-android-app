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
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistApi @Inject constructor(
    @param:AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
    @param:UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    fun createPlaylist(name: String, description: String, videoIdsList: List<String>) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("playlists/")
            .build()

        val payload = mapOf(
            "name" to name,
            "description" to description,
            "videoIdsList" to videoIdsList
        )

        val requestBody = gson.toJson(payload)
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun uploadThumbnailForPlaylist(playlistId: String, thumbnailFile: File) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegments("playlists/${playlistId}/thumbnail")
            .build()

        val mediaType = thumbnailFile.getMimeTypeOrNull()?.toMediaTypeOrNull()

        val progressRequestBody = ProgressRequestBody(
            file = thumbnailFile,
            mediaType =  mediaType,
            progressListener = {},
        )

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = "img",
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

    fun getPlaylistById(playlistId: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("playlists")
            .addPathSegment(playlistId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }

    fun addVideoToPlaylist(playlistId: String, videoId: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("playlists")
            .addPathSegment(playlistId)
            .addPathSegment("add")
            .addQueryParameter("videoId", videoId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .patch("".toRequestBody())
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun removeVideoFromPlaylist(playlistId: String, videoId: String) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("playlists")
            .addPathSegment(playlistId)
            .addPathSegment("remove")
            .addQueryParameter("videoId", videoId)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .patch("".toRequestBody())
            .build()

        return authorizedOkHttpClient.newCall(request).execute()
    }

    fun getAllPlaylistByUserId(userId: Long) : Response {
        val url: HttpUrl = BuildConfig.API_URL.toHttpUrl().newBuilder()
            .addPathSegment("playlists")
            .addPathSegment("user")
            .addPathSegment(userId.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return unauthorizedOkHttpClient.newCall(request).execute()
    }
}
