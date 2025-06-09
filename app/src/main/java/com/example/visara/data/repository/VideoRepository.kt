package com.example.visara.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.visara.data.model.UserModel
import com.example.visara.data.model.VideoPrivacy
import com.example.visara.data.model.VideoModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.VideoRemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val videoRemoteDataSource: VideoRemoteDataSource,
    @ApplicationContext private val appContext: Context,
) {
    private val _postingVideo: MutableStateFlow<VideoModel?> = MutableStateFlow(null)
    val postingVideo: StateFlow<VideoModel?> = _postingVideo.asStateFlow()

    suspend fun getVideoById(videoId: String) : VideoModel? {
        val apiResult = videoRemoteDataSource.getVideoById(videoId)
        if (apiResult is ApiResult.Success) {
            return apiResult.data
        }
        return null
    }

    suspend fun getVideoForHomeScreen(): List<VideoModel> {
        val videoListResult = videoRemoteDataSource.getRandomVideos(50)
        return if (videoListResult is ApiResult.Success) {
            videoListResult.data.map { it.toVideoModel() }
        } else {
            emptyList()
        }
    }

    suspend fun getRecommendedVideos(video: VideoModel) : List<VideoModel> {
        val videoListResult = videoRemoteDataSource.getRandomVideos(50)
        return if (videoListResult is ApiResult.Success) {
            videoListResult.data
                .asSequence()
                .map { it.toVideoModel() }
                .filter { it.id != video.id }
                .toList()
        } else {
            emptyList()
        }
    }

    fun getVideoUrl(videoId: String) : String {
        return "http://10.0.2.2:8080/${videoId}/output.mpd"
    }

    suspend fun uploadVideoMetaData(
        title: String,
        description: String,
        hashtags: List<String>,
        privacy: VideoPrivacy,
        isAllowComment: Boolean,
        videoUri: Uri,
        thumbnailUri: Uri?,
    ) : VideoModel? {
        val uploadVideoMetaDataResult = videoRemoteDataSource.uploadVideoMetaData(
            title = title,
            description = description,
            hashtags = hashtags,
            isPrivate = privacy != VideoPrivacy.ALL,
            isCommentOff = !isAllowComment
        )

        if (uploadVideoMetaDataResult !is ApiResult.Success) return null
        val result = uploadVideoMetaDataResult.data.toVideoModel().copy(
            localVideoUri = videoUri,
            localThumbnailUri = thumbnailUri,
        )

        _postingVideo.update { result }

        return result
    }

    suspend fun uploadVideoFile(
        videoMetaData: VideoModel,
        videoUri: Uri,
        thumbnailUri: Uri?,
        onProgressChange: (progress: Int) -> Unit,
    ) : Boolean {
        val videoId = videoMetaData.id
        val videoFile = uriToFile(context = appContext, uri = videoUri)
        val uploadVideoFileResult = videoFile?.let {
            try {
                videoRemoteDataSource.uploadVideoFile(
                    videoId = videoId,
                    videoFile = videoFile,
                    progressListener = onProgressChange,
                )
            } finally {
                videoFile.delete()
            }
        }
        val result = uploadVideoFileResult != null && uploadVideoFileResult is ApiResult.Success
        if (result) _postingVideo.update { it?.copy(isUploaded = true) }
        val thumbnailFile = thumbnailUri?.let { uriToFile(appContext, it) }
        thumbnailFile?.let {
            try {
                videoRemoteDataSource.uploadThumbnailFile(
                    videoId = videoId,
                    thumbnailFile = thumbnailFile,
                    progressListener = { progress-> Log.i("CHECK_VAR", "upload thumbnail progress: $progress %") }
                )
            } finally {
                thumbnailFile.delete()
            }
        }

        return result
    }

    suspend fun updateVideo(
        videoId: String,
        title: String,
        thumbnailUri: Uri?,
        description: String,
        hashtags: List<String>,
        privacy: VideoPrivacy,
        isAllowComment: Boolean,
    ) : Boolean {
        // Create data for new video
        val updateVideoResult = videoRemoteDataSource.updateVideo(
            videoId = videoId,
            title = title,
            description = description,
            hashtags = hashtags,
            isPrivate = privacy != VideoPrivacy.ALL,
            isCommentOff = !isAllowComment
        )

        val result = updateVideoResult is ApiResult.Success
        if (!result) return false

        val thumbnailFile = thumbnailUri?.let { uriToFile(appContext, it) }
        thumbnailFile?.let {
            try {
                videoRemoteDataSource.uploadThumbnailFile(
                    videoId = videoId,
                    thumbnailFile = thumbnailFile,
                    progressListener = { progress-> Log.i("CHECK_VAR", "upload thumbnail progress: $progress %") }
                )
            } finally {
                thumbnailFile.delete()
            }
        }

        return true
    }

    suspend fun syncPostingVideo() {
        postingVideo.value?.id?.let { videoId ->
            getVideoById(videoId)?.let { remoteVideo ->
                // Uploaded and processed, video is not still in POST progress
                if (remoteVideo.isProcessed) {
                    _postingVideo.update { null }
                } else {
                    _postingVideo.update { it?.copy(isProcessed = false) }
                }
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri)) ?: "tmp"

        val file = File(context.cacheDir, "temp_file.$extension")

        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val outputStream: OutputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                outputStream.flush()
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    suspend fun getAllVideoByUserId(userId: Long) : List<VideoModel> {
        val apiResult = videoRemoteDataSource.getAllVideoByUserId(userId)

        if (apiResult is ApiResult.Success) {
            val videoModelList = apiResult.data.map { it.toVideoModel() }
            return videoModelList
        }

        return emptyList()
    }

    suspend fun likeVideo(videoId: String) : Boolean {
        val apiResult = videoRemoteDataSource.likeVideo(videoId)
        return apiResult is ApiResult.Success
    }

    suspend fun unlikeVideo(videoId: String) : Boolean {
        val apiResult = videoRemoteDataSource.unlikeVideo(videoId)
        return apiResult is ApiResult.Success
    }

    suspend fun isVideoLiked(videoId: String) : Boolean {
        val apiResult = videoRemoteDataSource.isVideoLiked(videoId)
        return apiResult is ApiResult.Success && apiResult.data == true
    }

    suspend fun searchVideo(type: String, pattern: String, count: Long) : List<VideoModel> {
        if (type != "title" && type != "hashtag") return emptyList()
        if (pattern.isEmpty() || pattern.isBlank()) return emptyList()
        if (count <= 0) return emptyList()

        val apiResult = videoRemoteDataSource.searchVideo(type, pattern, count)

        val result = if (apiResult is ApiResult.Success) {
            apiResult.data.map { it.toVideoModel() }
        } else {
            emptyList()
        }

        return result
    }

    suspend fun increaseVideoView(videoId: String) : Boolean {
        val apiResult = videoRemoteDataSource.increaseVideoView(videoId)
        return apiResult is ApiResult.Success
    }

    suspend fun addVideoToHistory(video: VideoModel, currentUser: UserModel?) : Boolean {
        if (currentUser == null) return false
        val apiResult = videoRemoteDataSource.addVideoToHistory(
            videoId = video.id,
            videoTitle = video.title,
            thumbnailUrl = video.thumbnailUrl,
            ownerId = video.userId.toString(),
            ownerUsername = video.username,
            ownerFullName = video.userFullName,
            viewerId = currentUser.id.toString(),
            viewerUsername = currentUser.username,
        )
        return apiResult is ApiResult.Success
    }

    suspend fun getFollowingVideos(count: Long = 50) : List<VideoModel> {
        val apiResult = videoRemoteDataSource.getFollowingVideos(count)
        if (apiResult !is ApiResult.Success) return emptyList()
        return apiResult.data.map { it.toVideoModel() }
    }
}
