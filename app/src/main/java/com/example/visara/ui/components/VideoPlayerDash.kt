package com.example.visara.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class VideoPlayerManager(context: Context) {
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    fun playDash(url: String) {
        exoPlayer.stop()

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun release() {
        exoPlayer.release()
    }
}

@Composable
fun rememberVideoPlayerManager() : VideoPlayerManager {
    val context = LocalContext.current
    return remember { VideoPlayerManager(context) }
}

@Composable
fun VideoPlayerDash(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    showControls: Boolean = true,
) {
    DisposableEffect(Unit) {
        onDispose {
            videoPlayerManager.pause()
        }
    }

    AndroidView(
        factory = { context->
            PlayerView(context).apply {
                player = videoPlayerManager.exoPlayer
                useController = showControls
            }
        },
        modifier = modifier,
    )
}
