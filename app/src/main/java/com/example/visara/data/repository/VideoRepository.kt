package com.example.visara.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.visara.data.local.dao.VideoDao
import com.example.visara.data.local.entity.LocalVideoEntity
import com.example.visara.data.local.entity.LocalVideoStatus
import com.example.visara.data.model.PlaylistModel
import com.example.visara.data.model.UserModel
import com.example.visara.data.model.VideoModel
import com.example.visara.data.model.VideoPrivacy
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.VideoRemoteDataSource
import com.example.visara.di.gson
import com.example.visara.viewmodels.AddVideoSubmitData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val videoDao: VideoDao,
    @ApplicationContext private val appContext: Context,
) {
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

    fun getRecommendedHashtag() : List<String> {
        return emptyList()
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
        playlists: List<PlaylistModel>,
        privacy: VideoPrivacy,
        isAllowComment: Boolean,
        videoUri: Uri,
        thumbnailUri: Uri?,
        draftId: Long?,
        currentUser: UserModel?,
    ) : VideoModel? {

        if (title.isBlank() || currentUser == null) return null

        val uploadVideoMetaDataResult = videoRemoteDataSource.uploadVideoMetaData(
            title = title,
            description = description,
            hashtags = hashtags,
            isPrivate = privacy != VideoPrivacy.ALL,
            isCommentOff = !isAllowComment,
            playlistIds = playlists.map { it.id },
        )

        if (uploadVideoMetaDataResult !is ApiResult.Success) return null

        val videoEntity = LocalVideoEntity(
            userId = currentUser.id,
            username = currentUser.username,
            userFullName = currentUser.fullName,
            userProfilePic = currentUser.networkAvatarUrl,
            title = title,
            description = description,
            playlistsJson = gson.toJson(playlists),
            hashtagsJson = gson.toJson(hashtags),
            localVideoUriString = videoUri.toString(),
            localThumbnailUriString = thumbnailUri.toString(),
            isPrivate = privacy == VideoPrivacy.ONLY_ME,
            isCommentOff = !isAllowComment,
            statusCode = LocalVideoStatus.UPLOADING.code,
        )

        if (draftId == null) {
            videoDao.insertVideo(videoEntity)
        } else {
            videoDao.updateVideo(videoEntity.copy(localId = draftId))
        }

        val result = uploadVideoMetaDataResult.data.toVideoModel().copy(
            localId = draftId,
            localVideoUri = videoUri,
            localThumbnailUri = thumbnailUri,
        )

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

        val videoEntity = videoMetaData.localId?.let { getLocalVideoEntityById(it) }
        if (result) {
            videoEntity?.let {
                videoDao.updateVideo(it.copy(statusCode = LocalVideoStatus.PROCESSING.code))
            }
        } else {
            videoEntity?.let {
                videoDao.updateVideo(it.copy(statusCode = LocalVideoStatus.PENDING_RE_UPLOAD.code))
            }
        }

        val thumbnailFile = thumbnailUri?.let { uriToFile(appContext, it) }
        thumbnailFile?.let {
            try {
                videoRemoteDataSource.uploadThumbnailFile(
                    videoId = videoId,
                    thumbnailFile = thumbnailFile,
                    progressListener = {}
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

    suspend fun draftVideoPost(
        title: String,
        description: String,
        hashtags: List<String>,
        playlists: List<PlaylistModel>,
        privacy: VideoPrivacy,
        isAllowComment: Boolean,
        videoUri: Uri,
        thumbnailUri: Uri?,
        currentUser: UserModel?,
        draftId: Long?,
    ) : Boolean {
        if (currentUser == null) return false
        val videoEntity = LocalVideoEntity(
            userId = currentUser.id,
            username = currentUser.username,
            userFullName = currentUser.fullName,
            userProfilePic = currentUser.networkAvatarUrl,
            title = title,
            description = description,
            playlistsJson = gson.toJson(playlists),
            hashtagsJson = gson.toJson(hashtags),
            localVideoUriString = videoUri.toString(),
            localThumbnailUriString = thumbnailUri.toString(),
            isPrivate = privacy == VideoPrivacy.ONLY_ME,
            isCommentOff = !isAllowComment,
            statusCode = LocalVideoStatus.DRAFT.code,
        )

        if (draftId == null) {
            videoDao.insertVideo(videoEntity)
        } else {
            videoDao.updateVideo(videoEntity.copy(localId = draftId))
        }

        return true
    }

    suspend fun getDraftVideoByLocalId(localDraftVideoId: Long) : AddVideoSubmitData? {
        val videoEntity = videoDao.getVideoByLocalId(localDraftVideoId)
        val draftData = videoEntity.first()?.toAddVideoSubmitData()
        return draftData
    }

    suspend fun getLocalVideoEntityById(localId: Long) : LocalVideoEntity? {
        return videoDao.getVideoByLocalId(localId).distinctUntilChanged().first()
    }

    suspend fun getAllLocalVideoEntity(username: String) : List<LocalVideoEntity> {
        return videoDao.getAllLocalVideoByUsername(username)
            .distinctUntilChanged()
            .first()
    }
}
