package com.example.visara.data.repository

import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.components.VideoPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoDetailRepository @Inject constructor(
    private val videoRepository: VideoRepository,
) {
    var videoPlayerManager: VideoPlayerManager? = null
        private set

    private val _videoDetail: MutableStateFlow<VideoDetailState> = MutableStateFlow(VideoDetailState())
    val videoDetail: StateFlow<VideoDetailState> = _videoDetail.asStateFlow()

    fun setPlayer(mediaPlayer: MediaController) {
        if (videoPlayerManager == null) {
            videoPlayerManager = VideoPlayerManager(mediaPlayer).apply {
                this.mediaController.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                        _videoDetail.update { it.copy(isPlaying = isPlayingNow) }
                    }
                })
            }
        }
    }

    fun setVideoDetail(video: VideoModel) {
        if (video.id != _videoDetail.value.video?.id) {
            val videoUrl = videoRepository.getVideoUrl(video.id)
            videoPlayerManager?.playDash(url = videoUrl, videoModel = video)
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
