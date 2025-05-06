package com.example.visara.data.repository

import android.content.Context
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.components.video_player.DashVideoPlayerManager
import com.example.visara.ui.components.video_player.LocalVideoPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class VideoDetailRepository @Inject constructor(
    private val appContext: Context,
    private val videoRepository: VideoRepository,
) {
    val dashVideoPlayerManager: DashVideoPlayerManager = DashVideoPlayerManager(appContext)
    val localVideoPlayerManager: LocalVideoPlayerManager = LocalVideoPlayerManager(appContext)

    private val _videoDetail: MutableStateFlow<VideoDetailState> = MutableStateFlow(VideoDetailState())
    val videoDetail: StateFlow<VideoDetailState> = _videoDetail.asStateFlow()

    fun setVideo(video: VideoModel) {
        val videoUrl = videoRepository.getVideoUrl(video.id)
        dashVideoPlayerManager.play(videoUrl)
        _videoDetail.update {
            VideoDetailState(
                video = video,
                isVisible = true,
                isFullScreenMode = true,
            )
        }
    }
}

data class VideoDetailState(
    val video: VideoModel? = null,
    val isVisible: Boolean = false,
    val isPlaying: Boolean = false,
    val isFullScreenMode: Boolean = false,
)
