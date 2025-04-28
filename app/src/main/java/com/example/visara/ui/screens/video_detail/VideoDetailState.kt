package com.example.visara.ui.screens.video_detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.components.video_player.VideoPlayerManager

class VideoDetailState(
    manager: VideoPlayerManager,
    initialIsVisible: Boolean = false,
    initialIsFullScreenMode: Boolean = false,
) {
    var video by mutableStateOf<VideoModel?>(null)
    var isVisible by mutableStateOf(initialIsVisible)
    var isPlaying by mutableStateOf(manager.player.isPlaying)
    var isFullScreenMode by mutableStateOf(initialIsFullScreenMode)

    init {
        manager.player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
        })
    }

    fun enableFullScreenMode() {
        isFullScreenMode = true
    }

    fun enableMinimizedMode() {
        isFullScreenMode = false
    }

    fun close() {
        video = null
        isVisible = false
        isFullScreenMode = false
    }
}

@Composable
fun rememberVideoDetailState(manager: VideoPlayerManager): VideoDetailState {
    return remember {
        VideoDetailState(manager = manager)
    }
}