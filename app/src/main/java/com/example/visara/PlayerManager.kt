package com.example.visara

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.VideoRepository
import com.example.visara.service.play_back.PlaybackService
import com.google.common.util.concurrent.MoreExecutors
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
    private val _mediaControllerFlow: MutableStateFlow<MediaController?> = MutableStateFlow(null)
    val mediaControllerFlow: StateFlow<MediaController?> = _mediaControllerFlow.asStateFlow()
    private val _videoDetail: MutableStateFlow<VideoDetailState> = MutableStateFlow(VideoDetailState())
    val videoDetail: StateFlow<VideoDetailState> = _videoDetail.asStateFlow()
    private var storedPlaybackState: PlaybackStateSnapshot? = null

    init {
        Log.i("CHECK_VAR", "Init player manager")
        val sessionToken = SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()
        controllerFuture.addListener({
            this._mediaControllerFlow.value = controllerFuture.get().apply {
                this.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                        _videoDetail.update { it.copy(isPlaying = isPlayingNow) }
                    }
                })
            }
        }, MoreExecutors.directExecutor())
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

    fun playDash(url: String, videoModel: VideoModel, playWhenReady: Boolean = true) {
        _mediaControllerFlow.value?.stop()
        _mediaControllerFlow.value?.clearMediaItems()

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

        _mediaControllerFlow.value?.setMediaItem(mediaItem)
        _mediaControllerFlow.value?.playWhenReady = playWhenReady
        _mediaControllerFlow.value?.prepare()
    }

    fun playUri(uri: Uri, playWhenReady: Boolean = true) {
        _mediaControllerFlow.value?.stop()
        _mediaControllerFlow.value?.clearMediaItems()

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .build()

        _mediaControllerFlow.value?.setMediaItem(mediaItem)
        _mediaControllerFlow.value?.playWhenReady = playWhenReady
        _mediaControllerFlow.value?.prepare()
    }

    fun saveCurrentPlaybackState() {
        _mediaControllerFlow.value?.let { mediaController ->
            val currentItem = mediaController.currentMediaItem
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
        _mediaControllerFlow.value?.let { mediaController ->
            storedPlaybackState?.let { snapshot ->
                mediaController.stop()
                mediaController.clearMediaItems()

                mediaController.setMediaItem(snapshot.mediaItem)
                mediaController.seekTo(snapshot.positionMs)
                mediaController.playWhenReady = false
                mediaController.prepare()
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
            mediaControllerFlow.value?.stop()
            mediaControllerFlow.value?.clearMediaItems()
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
        _mediaControllerFlow.value?.play()
    }

    fun pause() {
        _mediaControllerFlow.value?.pause()
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
