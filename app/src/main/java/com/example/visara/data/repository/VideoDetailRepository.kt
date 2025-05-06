package com.example.visara.data.repository

import android.content.Context
import androidx.media3.common.Player
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.components.video_player.DashVideoPlayerManager
import com.example.visara.ui.components.video_player.LocalVideoPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class VideoDetailRepository @Inject constructor(
    appContext: Context,
    private val videoRepository: VideoRepository,
) {
    val dashVideoPlayerManager: DashVideoPlayerManager = DashVideoPlayerManager(appContext)
    val localVideoPlayerManager: LocalVideoPlayerManager = LocalVideoPlayerManager(appContext)

    private val _videoDetail: MutableStateFlow<VideoDetailState> = MutableStateFlow(VideoDetailState())
    val videoDetail: StateFlow<VideoDetailState> = _videoDetail.asStateFlow()

    init {
        dashVideoPlayerManager.player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                _videoDetail.update { it.copy(isPlaying = isPlayingNow) }
            }
        })
    }

    fun setVideoDetail(video: VideoModel) {
        if (video.id != _videoDetail.value.video?.id) {
            val videoUrl = videoRepository.getVideoUrl(video.id)
            dashVideoPlayerManager.play(videoUrl)
        }
        _videoDetail.update {
            it.copy(
                video = video,
                isVisible = true,
                isFullScreenMode = true,
            )
        }
    }

    fun enableFullScreenMode() {
        _videoDetail.update { it.copy(isFullScreenMode = true) }
    }

    fun enableMinimizedMode() {
        _videoDetail.update { it.copy(isFullScreenMode = false) }
    }

    fun close() {
        _videoDetail.update {
            it.copy(
                video = null,
                isVisible = false,
                isFullScreenMode = false,
            )
        }
    }

    fun hide() {
        _videoDetail.update { it.copy(isVisible = false) }
    }

    fun display() {
        _videoDetail.update { it.copy(isVisible = true) }
    }
}

data class VideoDetailState(
    val video: VideoModel? = null,
    val isVisible: Boolean = false,
    val isPlaying: Boolean = false,
    val isFullScreenMode: Boolean = false,
)
