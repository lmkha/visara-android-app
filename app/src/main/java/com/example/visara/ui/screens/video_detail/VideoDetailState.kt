package com.example.visara.ui.screens.video_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import com.example.visara.ui.components.VideoPlayerManager

class VideoDetailState(
    manager: VideoPlayerManager,
    initialVideoId: String? = null,
    initialIsFullScreenMode: Boolean = false,
    initialIsMinimizedMode: Boolean = false,
) {
    var videoId by mutableStateOf(initialVideoId)
    var isPlaying by mutableStateOf(manager.exoPlayer.isPlaying)
    var isFullScreenMode by mutableStateOf(initialIsFullScreenMode)
    var isMinimizedMode by mutableStateOf(initialIsMinimizedMode)

    init {
        manager.exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
        })
    }

    fun enableFullScreenMode() {
        isFullScreenMode = true
        isMinimizedMode = false
    }

    fun enableMinimizedMode() {
        isFullScreenMode = false
        isMinimizedMode = true
    }

    fun close() {
        videoId = null
        isPlaying = false
        isFullScreenMode = false
        isMinimizedMode = false
    }
}