package com.example.visara.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
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

    fun playDash(url: String, playWhenReady: Boolean = true) {
        exoPlayer.clearMediaItems()
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = playWhenReady
    }

    fun playFromUrl(uri: android.net.Uri, playWhenReady: Boolean = true) {
        exoPlayer.stop()

        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = playWhenReady
    }
}

@Composable
fun rememberVideoPlayerManager() : VideoPlayerManager {
    val context = LocalContext.current
    return remember { VideoPlayerManager(context) }
}

@Composable
fun VisaraVideoPlayer(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    showControls: Boolean = true,
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = videoPlayerManager.exoPlayer
                useController = showControls
            }
        },
        modifier = modifier,
    )
}