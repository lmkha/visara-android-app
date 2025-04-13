package com.example.visara.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class VideoPlayerManager(context: Context) {
    var dashExoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    var localExoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    fun playDash(url: String, playWhenReady: Boolean = true) {
        dashExoPlayer.clearMediaItems()
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
        dashExoPlayer.setMediaItem(mediaItem)
        dashExoPlayer.prepare()
        dashExoPlayer.playWhenReady = playWhenReady
    }
    fun playFromUrl(uri: android.net.Uri, playWhenReady: Boolean = true) {
        val mediaItem = MediaItem.fromUri(uri)
        localExoPlayer.setMediaItem(mediaItem)
        localExoPlayer.prepare()
        localExoPlayer.playWhenReady = playWhenReady
    }
}

@Composable
fun rememberVideoPlayerManager(context: Context): VideoPlayerManager {
    return remember { VideoPlayerManager(context) }
}

@Composable
fun VisaraDashPlayer(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    showControls: Boolean = true,
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = videoPlayerManager.dashExoPlayer
                useController = showControls
            }
        },
        modifier = modifier,
    )
}

@Composable
fun VisaraUriVideoPlayer(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    showControls: Boolean = true,
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = videoPlayerManager.localExoPlayer
                useController = showControls
            }
        },
        modifier = modifier,
    )
}