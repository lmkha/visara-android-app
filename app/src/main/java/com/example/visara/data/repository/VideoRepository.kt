package com.example.visara.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.visara.data.model.Privacy
import com.example.visara.data.model.VideoModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.VideoRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val appContext: Context,
) {
    suspend fun getVideoById(videoId: String) : VideoModel? {
        val apiResult = videoRemoteDataSource.getVideoById(videoId)
        if (apiResult is ApiResult.Success) {
            return apiResult.data
        }
        return null
    }
    suspend fun getVideoForHomeScreen(): List<VideoModel>? {
        return withContext(Dispatchers.IO) {
            val videoListResult = videoRemoteDataSource.getRandomVideos(50)

            if (videoListResult is ApiResult.Success) {
                val videoModelList = videoListResult.data.map { it.toVideoModel() }
                videoModelList
            } else {
                null
            }
        }
    }

    fun getVideoUrl(videoId: String) : String {
        return "http://10.0.2.2:8080/${videoId}/output.mpd"
    }

    suspend fun postVideo(
        videoUri: Uri?,
        thumbnailUri: Uri?,
        title: String,
        description: String,
        hashtags: List<String>,
        privacy: Privacy,
        isAllowComment: Boolean,
    ) : Boolean {
        if (videoUri == null) return false

        val uploadVideoMetaDataResult = videoRemoteDataSource.uploadVideoMetaData(
            title = title,
            description = description,
            hashtags = hashtags,
            isPrivate = privacy != Privacy.ALL,
            isCommentOff = isAllowComment
        )

        if (uploadVideoMetaDataResult !is ApiResult.Success) return false

        val videoId = uploadVideoMetaDataResult.data.id

        val videoFile = uriToFile(context = appContext, uri = videoUri)
        val uploadVideoFileResult = videoFile?.let {
            try {
                videoRemoteDataSource.uploadVideoFile(
                    videoId = videoId,
                    videoFile = videoFile,
                    progressListener = { progress-> Log.i("CHECK_VAR", "upload video progress: $progress %") }
                )
            } finally {
                videoFile.delete()
            }
        }

        val result = uploadVideoFileResult != null && uploadVideoFileResult is ApiResult.Success

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

}
