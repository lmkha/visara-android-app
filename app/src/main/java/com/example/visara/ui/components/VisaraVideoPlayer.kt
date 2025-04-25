package com.example.visara.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

interface VideoPlayerManager {
    fun release()
    fun pause()
    val player: ExoPlayer
}
class DashVideoPlayerManager(context: Context) : VideoPlayerManager {
    override val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun play(url: String, playWhenReady: Boolean = true) {
        player.stop()
        player.clearMediaItems()

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    player.playWhenReady = playWhenReady
                    player.removeListener(this)
                }
            }
        })
    }

    override fun release() {
        player.release()
    }

    override fun pause() {
        player.pause()
    }
}

class LocalVideoPlayerManager(context: Context) : VideoPlayerManager {
    override val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun play(uri: android.net.Uri, playWhenReady: Boolean = true) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = playWhenReady
    }

    override fun release() {
        player.release()
    }

    override fun pause() {
        player.pause()
    }
}

@Composable
fun rememberDashVideoPlayerManager(context: Context): DashVideoPlayerManager {
    return remember { DashVideoPlayerManager(context) }
}

@Composable
fun rememberLocalVideoPlayerManager(context: Context): LocalVideoPlayerManager {
    return remember { LocalVideoPlayerManager(context) }
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
                player = videoPlayerManager.player
                useController = showControls
            }
        },
        modifier = modifier,
    )
}
