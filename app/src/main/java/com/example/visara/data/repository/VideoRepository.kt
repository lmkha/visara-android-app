package com.example.visara.data.repository

import com.example.visara.data.model.VideoModel
import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.datasource.VideoRemoteDataSource
import com.example.visara.data.remote.dto.toVideoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val videoRemoteDataSource: VideoRemoteDataSource,
) {
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

    fun getVideoLink(videoId: String) : String {
        return "http://10.0.2.2:8080/${videoId}/output.mpd"
    }
}
