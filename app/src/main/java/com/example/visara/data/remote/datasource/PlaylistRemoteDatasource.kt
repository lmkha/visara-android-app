package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.api.PlaylistApi
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.PlayListDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRemoteDatasource @Inject constructor(
    private val playlistApi: PlaylistApi,
    gson: Gson,
) : RemoteDataSource(gson) {
    suspend fun createPlaylist(name: String, description: String, videoIdsList: List<String>) : ApiResult<PlayListDto> {
        return callApi({ playlistApi.createPlaylist(name, description, videoIdsList) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val playlistDto = gson.fromJson(dataJson, PlayListDto::class.java)
                ApiResult.NetworkResult.Success(playlistDto)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun uploadThumbnailForPlaylist(playlistId: String, thumbnailFile: File) : ApiResult<String> {
        return callApi({ playlistApi.uploadThumbnailForPlaylist(playlistId, thumbnailFile) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val data = jsonObject["data"]
                val thumbnailUrl = data as? String ?: ""
                ApiResult.NetworkResult.Success(thumbnailUrl)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun getPlaylistById(playlistId: String) : ApiResult<PlayListDto> {
        return callApi({ playlistApi.getPlaylistById(playlistId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val playlistDto = gson.fromJson(dataJson, PlayListDto::class.java)
                ApiResult.NetworkResult.Success(playlistDto)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun addVideoToPlaylist(playlistId: String, videoId: String) : ApiResult<Boolean> {
        return callApi({ playlistApi.addVideoToPlaylist(playlistId, videoId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val success = jsonObject["success"]
                val result = success as? Boolean == true
                ApiResult.NetworkResult.Success(result)
            } else  extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun removeVideoFromPlaylist(playlistId: String, videoId: String) : ApiResult<Boolean> {
        return callApi({ playlistApi.removeVideoFromPlaylist(playlistId, videoId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val success = jsonObject["success"]
                val result = success as? Boolean == true
                ApiResult.NetworkResult.Success(result)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun getAllPlaylistByUserId(userId: Long) : ApiResult<List<PlayListDto>> {
        return callApi({ playlistApi.getAllPlaylistByUserId(userId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<PlayListDto>>() {}.type
                val playlistDtoList: List<PlayListDto> = gson.fromJson(dataJson, type)
                ApiResult.NetworkResult.Success(playlistDtoList)
            } else extractFailureFromResponseBody(responseBody)
        }
    }
}
