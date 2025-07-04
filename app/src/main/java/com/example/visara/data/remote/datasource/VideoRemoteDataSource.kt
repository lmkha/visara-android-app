package com.example.visara.data.remote.datasource

import com.example.visara.data.model.VideoModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.VideoApi
import com.example.visara.data.remote.dto.VideoDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRemoteDataSource @Inject constructor(
    private val videoApi: VideoApi,
    gson: Gson,
) : RemoteDataSource(gson) {
    suspend fun getVideoById(videoId: String) : ApiResult<VideoModel> {
        return callApi({ videoApi.getVideoById(videoId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val videoModel = gson.fromJson(dataJson, VideoModel::class.java)
                ApiResult.NetworkResult.Success(videoModel)
            } else extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun getRandomVideos(numOfVideos: Int = 10) : ApiResult<List<VideoDto>> {
        return callApi({ videoApi.getRandomVideos(numOfVideos) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<VideoDto>>() {}.type
                val videoList: List<VideoDto> = gson.fromJson(dataJson, type)

                ApiResult.NetworkResult.Success(videoList)

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
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val videoDto: VideoDto = gson.fromJson(dataJson, VideoDto::class.java)

                ApiResult.NetworkResult.Success(videoDto)

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
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val videoDto: VideoDto = gson.fromJson(dataJson, VideoDto::class.java)

                ApiResult.NetworkResult.Success(videoDto)

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
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val data = jsonObject["data"]
                val thumbnailLink = if (data is String && data.isNotBlank()) data else null

                if (!thumbnailLink.isNullOrEmpty()) {
                    ApiResult.NetworkResult.Success(thumbnailLink)
                } else extractFailureFromResponseBody(responseBody)
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
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getAllVideoByUserId(userId: Long) : ApiResult<List<VideoDto>> {
        return callApi({ videoApi.getAllVideoByUserId(userId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<VideoDto>>() {}.type
                val videoDtoList: List<VideoDto> = gson.fromJson(dataJson, type)
                ApiResult.NetworkResult.Success(videoDtoList)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun likeVideo(videoId: String) : ApiResult<Unit> {
        return callApi({ videoApi.likeVideo(videoId) }) { response ->
            val responseBody = response.body?.string()
            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val successValue = jsonObject["success"]
            val success = successValue is Boolean && successValue

            if (response.isSuccessful && success) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun unlikeVideo(videoId: String) : ApiResult<Unit> {
        return callApi({ videoApi.unlikeVideo(videoId) }) { response ->
            val responseBody = response.body?.string()
            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val successValue = jsonObject["success"]
            val success = successValue is Boolean && successValue

            if (response.isSuccessful && success) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun isVideoLiked(videoId: String) : ApiResult<Boolean> {
        return callApi({ videoApi.getIsVideoLiked(videoId) }) { response ->
            val responseBody = response.body?.string()
            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val messageValue = jsonObject["message"]
            val liked = messageValue is String && messageValue == "Liked"

            if (response.isSuccessful) {
                ApiResult.NetworkResult.Success(liked)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun searchVideo(type: String, pattern: String, count: Long) : ApiResult<List<VideoDto>> {
        return callApi({ videoApi.searchVideo(type, pattern, count) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<VideoDto>>() {}.type
                val videoDtoList: List<VideoDto> = gson.fromJson(dataJson, type)
                ApiResult.NetworkResult.Success(videoDtoList)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun increaseVideoView(videoId: String) : ApiResult<Long> {
        return callApi({ videoApi.increaseVideoView(videoId) }) { response ->
            val responseBody = response.body?.string()
            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val messageValue = jsonObject["message"]
            val videoViewCount = if (messageValue is String) {
                messageValue.toLongOrNull() ?: 0L
            } else 0L

            if (response.isSuccessful) {
                ApiResult.NetworkResult.Success(videoViewCount)
            } else {
                extractFailureFromResponseBody(responseBody)
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
            val responseBody = response.body?.string()
            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val successValue = jsonObject["success"]
            val success = successValue is Boolean && successValue

            if (response.isSuccessful && success) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getFollowingVideos(count: Long) : ApiResult<List<VideoDto>> {
        return callApi({ videoApi.getFollowingVideos(count) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<VideoDto>>() {}.type
                val videoDtoList: List<VideoDto> = gson.fromJson(dataJson, type)
                ApiResult.NetworkResult.Success(videoDtoList)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun deleteVideo(videoId: String) : ApiResult<Unit> {
        return callApi({ videoApi.deleteVideo(videoId) }) { response ->
            val responseBody = response.body?.string()
            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val successValue = jsonObject["success"]
            val success = successValue is Boolean && successValue

            if (response.isSuccessful && success) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }
}
