package com.example.visara.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.example.visara.BuildConfig
import com.example.visara.data.local.dao.VideoDao
import com.example.visara.data.local.entity.LocalVideoEntity
import com.example.visara.data.local.entity.VideoStatus
import com.example.visara.data.model.PlaylistModel
import com.example.visara.data.model.UserModel
import com.example.visara.data.model.VideoModel
import com.example.visara.data.model.VideoPrivacy
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.VideoRemoteDataSource
import com.example.visara.ui.screens.add_new_video.components.enter_video_info.PrivacyState
import com.example.visara.viewmodels.AddVideoSubmitData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val videoDao: VideoDao,
    @param:ApplicationContext private val appContext: Context,
    private val json: Json,
) {
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

        if (uploadVideoMetaDataResult !is ApiResult.Success || uploadVideoMetaDataResult.data == null) return null

        val videoEntity = LocalVideoEntity(
            remoteId = uploadVideoMetaDataResult.data.id,
            userId = currentUser.id,
            username = currentUser.username,
            userFullName = currentUser.fullName,
            userProfilePic = currentUser.networkAvatarUrl,
            title = title,
            description = description,
            playlistsJson = json.encodeToString(playlists),
            hashtagsJson = json.encodeToString(hashtags),
            localVideoUriString = videoUri.toString(),
            localThumbnailUriString = thumbnailUri.toString(),
            isPrivate = privacy == VideoPrivacy.ONLY_ME,
            isCommentOff = !isAllowComment,
            statusCode = VideoStatus.UPLOADING.code,
        )

        if (draftId == null) {
            videoDao.insertVideo(videoEntity)
            return videoDao.getVideoByRemoteId(uploadVideoMetaDataResult.data.id)
                .distinctUntilChanged()
                .first()
                ?.let { convertVideoEntityToModel(it) }
        }

        videoDao.updateVideo(videoEntity.copy(localId = draftId))

        return uploadVideoMetaDataResult.data.toVideoModel().copy(
            localId = draftId,
            localVideoUri = videoUri,
            localThumbnailUri = thumbnailUri,
        )
    }

    suspend fun uploadVideoFile(
        videoMetaData: VideoModel,
        videoUri: Uri,
        onProgressChange: (progress: Int) -> Unit,
    ) : ApiResult<Unit> {
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

        val videoEntity = videoMetaData.localId?.let { getLocalVideoEntityById(it) }
        if (uploadVideoFileResult is ApiResult.Success) {
            videoEntity?.let {
                videoDao.updateVideo(it.copy(statusCode = VideoStatus.PROCESSING.code))
            }
        } else {
            videoEntity?.let {
                videoDao.updateVideo(it.copy(statusCode = VideoStatus.PENDING_RE_UPLOAD.code))
            }
        }

        if (uploadVideoFileResult != null) {
            return uploadVideoFileResult
        }

        return ApiResult.Error(exception = Exception("Upload video failed!"))
    }

    suspend fun uploadThumbnailFile(
        videoId: String,
        thumbnailUri: Uri,
    ) {
        val thumbnailFile = uriToFile(appContext, thumbnailUri)
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
                    progressListener = {}
                )
            } finally {
                thumbnailFile.delete()
            }
        }

        return true
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
            playlistsJson = json.encodeToString(playlists),
            hashtagsJson = json.encodeToString(hashtags),
            localVideoUriString = videoUri.toString(),
            localThumbnailUriString = thumbnailUri.toString(),
            isPrivate = privacy == VideoPrivacy.ONLY_ME,
            isCommentOff = !isAllowComment,
            statusCode = VideoStatus.DRAFT.code,
        )

        if (draftId == null) {
            videoDao.insertVideo(videoEntity)
        } else {
            videoDao.updateVideo(videoEntity.copy(localId = draftId))
        }

        return true
    }

    suspend fun getVideoById(videoId: String) : VideoModel? {
        val apiResult = videoRemoteDataSource.getVideoById(videoId)
        if (apiResult is ApiResult.Success) {
            return apiResult.data?.toVideoModel()
        }
        return null
    }

    suspend fun getVideoForHomeScreen(): List<VideoModel> {
        val videoListResult = videoRemoteDataSource.getRandomVideos(50)
        return if (videoListResult is ApiResult.Success && videoListResult.data != null) {
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
        return if (videoListResult is ApiResult.Success && videoListResult.data != null) {
            videoListResult.data
                .asSequence()
                .map { it.toVideoModel() }
                .filter { it.id != video.id }
                .toList()
        } else {
            emptyList()
        }
    }

    fun getVideoUrl(videoId: String): String {
        return "${BuildConfig.BACKEND_URL.removeSuffix("/")}/$videoId/output.mpd"
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

        if (apiResult is ApiResult.Success && apiResult.data != null) {
            return apiResult.data.map { it.toVideoModel() }
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

        val result = if (apiResult is ApiResult.Success && apiResult.data != null) {
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
        return apiResult.data?.map { it.toVideoModel() } ?: emptyList()
    }

    suspend fun getDraftVideoByLocalId(localDraftVideoId: Long) : AddVideoSubmitData? {
        val videoEntity = videoDao.getVideoByLocalId(localDraftVideoId)
        val draftData = videoEntity.first()?.let { convertVideoEntityToSubmitData(it) }
        return draftData
    }

    private fun convertVideoEntityToSubmitData(entity: LocalVideoEntity) : AddVideoSubmitData {
        with(entity) {
            val playlists = json.decodeFromString<List<PlaylistModel>>(this.playlistsJson)
            val hashtags: List<String> = json.decodeFromString<List<String>>(this.hashtagsJson)

            return AddVideoSubmitData(
                localId = localId,
                title = title,
                description = description,
                playlists = playlists,
                hashtags = hashtags,
                privacy = PrivacyState.ALL,
                isAllowComment = !isCommentOff,
                thumbnailUri = localThumbnailUriString?.toUri(),
                videoUri = localVideoUriString?.toUri(),
            )
        }
    }

    fun convertVideoEntityToModel(entity: LocalVideoEntity) : VideoModel {
        with(entity) {
            return VideoModel(
                id = remoteId,
                localId = localId,
                username = username,
                userId = userId,
                userProfilePic = userProfilePic,
                userFullName = userFullName,
                title = title,
                description = description,
                duration = duration,
                isPrivate = isPrivate,
                isCommentOff = isCommentOff,
                localVideoUri = localVideoUriString?.toUri(),
                localThumbnailUri = localThumbnailUriString?.toUri()
            )
        }
    }

    suspend fun getLocalVideoEntityById(localId: Long) : LocalVideoEntity? {
        return videoDao.getVideoByLocalId(localId).distinctUntilChanged().first()
    }

    fun getAllLocalVideoEntityFlow(username: String) : Flow<List<LocalVideoEntity>> {
        return videoDao.getAllLocalVideoByUsername(username)
            .distinctUntilChanged()
    }

    suspend fun updateLocalVideoEntity(videoEntity: LocalVideoEntity) {
        videoDao.updateVideo(videoEntity)
    }

    suspend fun deleteLocalVideoEntityByLocalId(localId: Long) {
        val videoEntity = getLocalVideoEntityById(localId)
        videoEntity?.let { videoDao.deleteVideo(it) }
    }

    suspend fun deleteLocalVideoEntity(videoEntity: LocalVideoEntity) {
        videoDao.deleteVideo(videoEntity)
    }

    suspend fun getLocalVideoEntityByTitle(title: String) : LocalVideoEntity? {
        return videoDao.getLocalVideoByTitle(title)
            .distinctUntilChanged()
            .first()
    }

    suspend fun deleteVideo(videoId: String) : Result<Unit> {
        videoDao.getVideoByRemoteId(videoId).distinctUntilChanged().first()?.let {
            deleteLocalVideoEntity(it)
        }
        if (videoId.isBlank()) return Result.failure(Throwable("VideoId is blank"))
        return when(val apiResult = videoRemoteDataSource.deleteVideo(videoId)) {
            is ApiResult.Error -> Result.failure(apiResult.exception)
            is ApiResult.Failure -> Result.failure(Throwable(apiResult.message))
            is ApiResult.Success-> Result.success(Unit)
        }
    }
}
