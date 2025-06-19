package com.example.visara.data.remote.datasource

import android.util.Log
import com.example.visara.data.model.VideoModel
import com.example.visara.data.remote.common.ApiError
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.api.VideoApi
import com.example.visara.data.remote.dto.VideoDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRemoteDataSource @Inject constructor(
    private val videoApi: VideoApi,
    private val gson: Gson,
) {
    suspend fun getVideoById(videoId: String) : ApiResult<VideoModel> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.getVideoById(videoId)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val videoModel = gson.fromJson(dataJson, VideoModel::class.java)
                    ApiResult.Success(videoModel)
                } else  ApiResult.Failure(
                    ApiError(
                        code = response.code,
                        errorCode = response.code.toString(),
                        message = response.message,
                        rawBody = responseBody
                    )
                )
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun getRandomVideos(numOfVideos: Int = 10) : ApiResult<List<VideoDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.getRandomVideos(numOfVideos)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<VideoDto>>() {}.type
                    val videoList: List<VideoDto> = gson.fromJson(dataJson, type)

                    ApiResult.Success(videoList)

                } else ApiResult.Failure(
                    ApiError(
                        code = response.code,
                        errorCode = response.code.toString(),
                        message = response.message,
                        rawBody = responseBody
                    )
                )
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
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
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.uploadVideoMetaData(
                    title = title,
                    description = description,
                    hashtags = hashtags,
                    isPrivate = isPrivate,
                    isCommentOff = isCommentOff,
                    playlistIds = playlistIds,
                )
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val videoDto: VideoDto = gson.fromJson(dataJson, VideoDto::class.java)

                    ApiResult.Success(videoDto)

                } else ApiResult.Failure(
                    ApiError(
                        code = response.code,
                        errorCode = response.code.toString(),
                        message = response.message,
                        rawBody = responseBody
                    )
                )
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
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
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.updateVideo(
                    videoId = videoId,
                    title = title,
                    description = description,
                    hashtags = hashtags,
                    isCommentOff = isCommentOff,
                    isPrivate = isPrivate
                )
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val videoDto: VideoDto = gson.fromJson(dataJson, VideoDto::class.java)

                    ApiResult.Success(videoDto)

                } else ApiResult.Failure(
                    ApiError(
                        code = response.code,
                        errorCode = response.code.toString(),
                        message = response.message,
                        rawBody = responseBody
                    )
                )
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun uploadThumbnailFile(
        videoId: String,
        thumbnailFile: File,
        progressListener: (percent: Int) -> Unit = { },
    ) : ApiResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.uploadThumbnailFile(videoId, thumbnailFile, progressListener)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val data = jsonObject["data"]
                    val thumbnailLink = if (data is String && data.isNotBlank()) data else null

                    if (!thumbnailLink.isNullOrEmpty()) {
                        ApiResult.Success(thumbnailLink)
                    } else {
                        ApiResult.Failure(
                            ApiError(
                                code = response.code,
                                errorCode = "EMPTY_THUMBNAIL_LINK",
                                message = "Thumbnail link not found",
                                rawBody = responseBody
                            )
                        )
                    }
                } else ApiResult.Failure(
                    ApiError(
                        code = response.code,
                        errorCode = response.code.toString(),
                        message = response.message,
                        rawBody = responseBody
                    )
                )
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }

    }

    suspend fun uploadVideoFile(
        videoId: String,
        videoFile: File,
        progressListener: (percent: Int) -> Unit = { },
    ) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.uploadVideoFile(videoId, videoFile, progressListener)
                val responseBody = response.body?.string()

                if (response.isSuccessful) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun getAllVideoByUserId(userId: Long) : ApiResult<List<VideoDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.getAllVideoByUserId(userId)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<VideoDto>>() {}.type
                    val videoDtoList: List<VideoDto> = gson.fromJson(dataJson, type)
                    ApiResult.Success(videoDtoList)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun likeVideo(videoId: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.likeVideo(videoId)
                val responseBody = response.body?.string()
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val successValue = jsonObject["success"]
                val success = successValue is Boolean && successValue == true

                if (response.isSuccessful && success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun unlikeVideo(videoId: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.unlikeVideo(videoId)
                val responseBody = response.body?.string()
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val successValue = jsonObject["success"]
                val success = successValue is Boolean && successValue == true

                if (response.isSuccessful && success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun isVideoLiked(videoId: String) : ApiResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.getIsVideoLiked(videoId)
                val responseBody = response.body?.string()
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val messageValue = jsonObject["message"]
                val liked = messageValue is String && messageValue == "Liked"

                if (response.isSuccessful) {
                    ApiResult.Success(liked)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun searchVideo(type: String, pattern: String, count: Long) : ApiResult<List<VideoDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.searchVideo(type, pattern, count)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<VideoDto>>() {}.type
                    val videoDtoList: List<VideoDto> = gson.fromJson(dataJson, type)
                    ApiResult.Success(videoDtoList)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun increaseVideoView(videoId: String) : ApiResult<Long> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.increaseVideoView(videoId)
                val responseBody = response.body?.string()
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val messageValue = jsonObject["message"]
                val videoViewCount = if (messageValue is String) {
                    messageValue.toLongOrNull() ?: 0L
                } else 0L

                if (response.isSuccessful) {
                    ApiResult.Success(videoViewCount)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
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
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.addVideoToHistory(
                    videoId = videoId,
                    videoTitle = videoTitle,
                    thumbnailUrl = thumbnailUrl,
                    ownerId = ownerId,
                    ownerUsername = ownerUsername,
                    ownerFullName = ownerFullName,
                    viewerId = viewerId,
                    viewerUsername = viewerUsername,
                )
                val responseBody = response.body?.string()
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val successValue = jsonObject["success"]
                val success = successValue is Boolean && successValue == true

                if (response.isSuccessful && success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun getFollowingVideos(count: Long) : ApiResult<List<VideoDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.getFollowingVideos(count)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<VideoDto>>() {}.type
                    val videoDtoList: List<VideoDto> = gson.fromJson(dataJson, type)
                    ApiResult.Success(videoDtoList)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun deleteVideo(videoId: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.deleteVideo(videoId)
                val responseBody = response.body?.string()
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val successValue = jsonObject["success"]
                val success = successValue is Boolean && successValue == true

                if (response.isSuccessful && success) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e : Exception) {
                ApiResult.Error(e)
            }
        }
    }
}
