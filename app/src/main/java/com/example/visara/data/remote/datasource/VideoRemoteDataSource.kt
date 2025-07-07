package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.VideoApi
import com.example.visara.data.remote.common.ApiResponse
import com.example.visara.data.remote.dto.VideoDto
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRemoteDataSource @Inject constructor(
    private val videoApi: VideoApi,
    json: Json,
) : RemoteDataSource(json) {
    suspend fun getVideoById(videoId: String) : ApiResult<VideoDto> {
        return callApi({ videoApi.getVideoById(videoId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResult.Success<VideoDto>>(responseBody)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun getRandomVideos(numOfVideos: Int = 10) : ApiResult<List<VideoDto>> {
        return callApi({ videoApi.getRandomVideos(numOfVideos) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResult.Success<List<VideoDto>>>(responseBody)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun uploadVideoMetaData(
        title: String,
        description: String,
        hashtags: List<String>,
        isPrivate: Boolean,
        isCommentOff: Boolean,
        playlistIds: List<String>,
    ) : ApiResult<VideoDto> {
        return callApi(
            request = {
                videoApi.uploadVideoMetaData(
                    title = title,
                    description = description,
                    hashtags = hashtags,
                    isPrivate = isPrivate,
                    isCommentOff = isCommentOff,
                    playlistIds = playlistIds,
                )
            }
        ) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResult.Success<VideoDto>>(responseBody)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun updateVideo(
        videoId: String,
        title: String,
        description: String,
        hashtags: List<String>,
        isPrivate: Boolean,
        isCommentOff: Boolean,
    ) : ApiResult<VideoDto> {
        return callApi(
            request = {
                videoApi.updateVideo(
                    videoId = videoId,
                    title = title,
                    description = description,
                    hashtags = hashtags,
                    isCommentOff = isCommentOff,
                    isPrivate = isPrivate
                )
            }
        ) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResult.Success<VideoDto>>(responseBody)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun uploadThumbnailFile(
        videoId: String,
        thumbnailFile: File,
        progressListener: (percent: Int) -> Unit = { },
    ) : ApiResult<String> {
        return callApi({ videoApi.uploadThumbnailFile(videoId, thumbnailFile, progressListener) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResult.Success<String>>(responseBody)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun uploadVideoFile(
        videoId: String,
        videoFile: File,
        progressListener: (percent: Int) -> Unit = { },
    ) : ApiResult<Unit> {
        return callApi({ videoApi.uploadVideoFile(videoId, videoFile, progressListener) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getAllVideoByUserId(userId: Long) : ApiResult<List<VideoDto>> {
        return callApi({ videoApi.getAllVideoByUserId(userId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResult.Success<List<VideoDto>>>(responseBody)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun likeVideo(videoId: String) : ApiResult<Unit> {
        return callApi({ videoApi.likeVideo(videoId) }) { response ->
            val responseBody = response.body?.string() ?: return@callApi extractFailureFromResponseBody(null)
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).toApiResult()
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                if (it.success) {
                    ApiResult.Success(Unit)
                } else {
                    extractFailureFromResponseBody(responseBody)
                }
            }
        }
    }

    suspend fun unlikeVideo(videoId: String) : ApiResult<Unit> {
        return callApi({ videoApi.unlikeVideo(videoId) }) { response ->
            val responseBody = response.body?.string() ?: return@callApi extractFailureFromResponseBody(null)
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                if (it.success) {
                    ApiResult.Success(Unit)
                } else {
                    extractFailureFromResponseBody(responseBody)
                }
            }
        }
    }

    suspend fun isVideoLiked(videoId: String) : ApiResult<Boolean> {
        return callApi({ videoApi.getIsVideoLiked(videoId) }) { response ->
            val responseBody = response.body?.string() ?: return@callApi extractFailureFromResponseBody(null)
            if (!response.isSuccessful) return@callApi extractFailureFromResponseBody(responseBody)
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                if (it.message == "Liked") {
                    ApiResult.Success(true)
                } else {
                    extractFailureFromResponseBody(responseBody)
                }
            }
        }
    }

    suspend fun searchVideo(type: String, pattern: String, count: Long) : ApiResult<List<VideoDto>> {
        return callApi({ videoApi.searchVideo(type, pattern, count) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResult.Success<List<VideoDto>>>(responseBody)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun increaseVideoView(videoId: String) : ApiResult<Long> {
        return callApi({ videoApi.increaseVideoView(videoId) }) { response ->
            if (!response.isSuccessful) return@callApi ApiResult.Failure()
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).let { result ->
                val videoViewCount = result.message?.toLongOrNull()
                if (videoViewCount != null) {
                    ApiResult.Success(videoViewCount)
                } else {
                    ApiResult.Failure()
                }
            }
        }
    }

    suspend fun addVideoToHistory(
        videoId: String,
        videoTitle: String,
        thumbnailUrl: String,
        ownerId: String,
        ownerUsername: String,
        ownerFullName: String,
        viewerId: String,
        viewerUsername: String,
    ) : ApiResult<Unit> {
        return callApi(
            request = {
                videoApi.addVideoToHistory(
                    videoId = videoId,
                    videoTitle = videoTitle,
                    thumbnailUrl = thumbnailUrl,
                    ownerId = ownerId,
                    ownerUsername = ownerUsername,
                    ownerFullName = ownerFullName,
                    viewerId = viewerId,
                    viewerUsername = viewerUsername,
                )
            }
        ) { response ->
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                if (it.success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure()
                }
            }
        }
    }

    suspend fun getFollowingVideos(count: Long) : ApiResult<List<VideoDto>> {
        return callApi({ videoApi.getFollowingVideos(count) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResult.Success<List<VideoDto>>>(responseBody)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun deleteVideo(videoId: String) : ApiResult<Unit> {
        return callApi({ videoApi.deleteVideo(videoId) }) { response ->
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()
            json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                if (it.success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure()
                }
            }
        }
    }
}
