package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.api.PlaylistApi
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.PlayListDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRemoteDatasource @Inject constructor(
    private val playlistApi: PlaylistApi,
    gson: Gson,
) : RemoteDataSource(gson) {
    suspend fun createPlaylist(name: String, description: String, videoIdsList: List<String>) : ApiResult<PlayListDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = playlistApi.createPlaylist(name, description, videoIdsList)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val playlistDto = gson.fromJson(dataJson, PlayListDto::class.java)
                    ApiResult.Success(playlistDto)
                } else parseFailureFromResponse(responseBody)
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun uploadThumbnailForPlaylist(playlistId: String, thumbnailFile: File) : ApiResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = playlistApi.uploadThumbnailForPlaylist(playlistId, thumbnailFile)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val data = jsonObject["data"]
                    val thumbnailUrl = data as? String ?: ""
                    ApiResult.Success(thumbnailUrl)
                } else parseFailureFromResponse(responseBody)
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun getPlaylistById(playlistId: String) : ApiResult<PlayListDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = playlistApi.getPlaylistById(playlistId)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val playlistDto = gson.fromJson(dataJson, PlayListDto::class.java)
                    ApiResult.Success(playlistDto)
                } else parseFailureFromResponse(responseBody)
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun addVideoToPlaylist(playlistId: String, videoId: String) : ApiResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = playlistApi.addVideoToPlaylist(playlistId, videoId)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val success = jsonObject["success"]
                    val result = success as? Boolean == true
                    ApiResult.Success(result)
                } else  parseFailureFromResponse(responseBody)
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }

    }

    suspend fun removeVideoFromPlaylist(playlistId: String, videoId: String) : ApiResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = playlistApi.removeVideoFromPlaylist(playlistId, videoId)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val success = jsonObject["success"]
                    val result = success as? Boolean == true
                    ApiResult.Success(result)
                } else parseFailureFromResponse(responseBody)
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun getAllPlaylistByUserId(userId: Long) : ApiResult<List<PlayListDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = playlistApi.getAllPlaylistByUserId(userId)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<PlayListDto>>() {}.type
                    val playlistDtoList: List<PlayListDto> = gson.fromJson(dataJson, type)
                    ApiResult.Success(playlistDtoList)
                } else parseFailureFromResponse(responseBody)
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }
}
