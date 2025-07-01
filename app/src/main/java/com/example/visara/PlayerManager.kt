package com.example.visara

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.VideoRepository
import com.example.visara.service.play_back.PlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext appContext: Context,
    private val videoRepository: VideoRepository,
) {
    var mediaBrowser: MediaBrowser? = null
    private val _videoDetail: MutableStateFlow<VideoDetailState> = MutableStateFlow(VideoDetailState())
    val videoDetail: StateFlow<VideoDetailState> = _videoDetail.asStateFlow()
    private var storedPlaybackState: PlaybackStateSnapshot? = null

    init {
        val sessionToken = SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
        MediaBrowser.Builder(appContext, sessionToken).buildAsync().apply {
            addListener({
                mediaBrowser = get()
            }, ContextCompat.getMainExecutor(appContext))
        }
    }

    fun setVideoDetail(video: VideoModel) {
        if (video.id != _videoDetail.value.video?.id) {
            val videoUrl = videoRepository.getVideoUrl(video.id)
            playDash(url = videoUrl, videoModel = video)
        }
        _videoDetail.update {
            it.copy(
                video = video,
                isVisible = true,
                isFullScreenMode = true,
            )
        }
    }

    private fun playDash(url: String, videoModel: VideoModel, playWhenReady: Boolean = true) {
        mediaBrowser?.stop()
        mediaBrowser?.clearMediaItems()

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtist(videoModel.username)
                    .setTitle(videoModel.title)
                    .setArtworkUri(videoModel.thumbnailUrl.toUri())
                    .build()
            )
            .build()

        mediaBrowser?.setMediaItem(mediaItem)
        mediaBrowser?.playWhenReady = playWhenReady
        mediaBrowser?.prepare()
    }

    fun playUri(uri: Uri, playWhenReady: Boolean = true) {
        mediaBrowser?.clearMediaItems()
        mediaBrowser?.clearMediaItems()

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .build()

        mediaBrowser?.setMediaItem(mediaItem)
        mediaBrowser?.playWhenReady = playWhenReady
        mediaBrowser?.prepare()
    }

    fun saveCurrentPlaybackState() {
        mediaBrowser.let { mediaController ->
            val currentItem = mediaController?.currentMediaItem
            if (currentItem != null && mediaController.playbackState != Player.STATE_IDLE) {
                if (mediaController.isPlaying) {
                    mediaController.pause()
                }

                this.storedPlaybackState = PlaybackStateSnapshot(
                    mediaItem = currentItem,
                    positionMs = mediaController.currentPosition,
                    playWhenReady = mediaController.playWhenReady,
                    currentWindowIndex = mediaController.currentMediaItemIndex
                )
            } else {
                this.storedPlaybackState = null
            }
        }
    }

    fun restoreStoredPlaybackState() {
        mediaBrowser.let { mediaController ->
            storedPlaybackState?.let { snapshot ->
                mediaController?.stop()
                mediaController?.clearMediaItems()

                mediaController?.setMediaItem(snapshot.mediaItem)
                mediaController?.seekTo(snapshot.positionMs)
                mediaController?.playWhenReady = false
                mediaController?.prepare()
            }
            this.storedPlaybackState = null
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
            mediaBrowser?.stop()
            mediaBrowser?.clearMediaItems()
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

    fun play() {
        mediaBrowser?.play()
    }

    fun pause() {
        mediaBrowser?.pause()
    }
}

data class PlaybackStateSnapshot(
    val mediaItem: MediaItem,
    val positionMs: Long,
    val playWhenReady: Boolean,
    val currentWindowIndex: Int = 0
)

data class VideoDetailState(
    val video: VideoModel? = null,
    val isVisible: Boolean = false,
    val isPlaying: Boolean = false,
    val isFullScreenMode: Boolean = false,
)
