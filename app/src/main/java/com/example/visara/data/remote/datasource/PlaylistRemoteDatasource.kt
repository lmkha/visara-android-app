package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.api.PlaylistApi
import com.example.visara.data.remote.common.ApiResponse
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.PlayListDto
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRemoteDatasource @Inject constructor(
    private val playlistApi: PlaylistApi,
    json: Json
) : RemoteDataSource(json) {
    suspend fun createPlaylist(name: String, description: String, videoIdsList: List<String>) : ApiResult<PlayListDto> {
        return callApi({ playlistApi.createPlaylist(name, description, videoIdsList) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<PlayListDto>>(responseBody).toApiResult()
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun uploadThumbnailForPlaylist(playlistId: String, thumbnailFile: File) : ApiResult<String> {
        return callApi({ playlistApi.uploadThumbnailForPlaylist(playlistId, thumbnailFile) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<String>>(responseBody).toApiResult()
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun getPlaylistById(playlistId: String) : ApiResult<PlayListDto> {
        return callApi({ playlistApi.getPlaylistById(playlistId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<PlayListDto>>(responseBody).toApiResult()
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun addVideoToPlaylist(playlistId: String, videoId: String) : ApiResult<Boolean> {
        return callApi({ playlistApi.addVideoToPlaylist(playlistId, videoId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<Nothing>>(responseBody).toApiResult()
            } else  extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun removeVideoFromPlaylist(playlistId: String, videoId: String) : ApiResult<Boolean> {
        return callApi({ playlistApi.removeVideoFromPlaylist(playlistId, videoId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                    if (it.success) {
                        ApiResult.Success(true)
                    } else {
                        ApiResult.Failure()
                    }
                }
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun getAllPlaylistByUserId(userId: Long) : ApiResult<List<PlayListDto>> {
        return callApi({ playlistApi.getAllPlaylistByUserId(userId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<List<PlayListDto>>>(responseBody).toApiResult()
            } else extractFailureFromResponseBody(responseBody)
        }
    }
}
