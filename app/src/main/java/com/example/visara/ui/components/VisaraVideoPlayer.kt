package com.example.visara.ui.components

import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_SURFACE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.example.visara.R
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VisaraVideoPlayer(
    modifier: Modifier = Modifier,
    mediaBrowser: MediaBrowser,
    showControls: Boolean = true,
    requireLandscapeMode: () -> Unit,
    requirePortraitMode: () -> Unit,
) {
    var showControlsState by remember(showControls) { mutableStateOf(showControls) }
    val presentationState = rememberPresentationState(mediaBrowser)
    val scaledModifier = Modifier.resizeWithContentScale(
        contentScale = ContentScale.Fit,
        sourceSizeDp = presentationState.videoSizeDp
    )

    Box(
        modifier = modifier.clickable(
            interactionSource = null,
            indication = null,
            onClick = {
                showControlsState  = !showControlsState
            }
        )

    ) {
        PlayerSurface(
            player = mediaBrowser,
            surfaceType = SURFACE_TYPE_SURFACE_VIEW,
            modifier = scaledModifier,
        )

        if (presentationState.coverSurface) {
            // Cover the surface that is being prepared with a shutter
            Box(modifier = Modifier.matchParentSize().background(Color.Black))
        }

        AnimatedVisibility(
            visible = showControlsState,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerControls(
                player = mediaBrowser,
                requireLandscapeMode = requireLandscapeMode,
                requirePortraitMode = requirePortraitMode,
                modifier = Modifier.fillMaxSize(),
            )
            LaunchedEffect(showControlsState, mediaBrowser.isPlaying) {
                if (showControlsState && mediaBrowser.isPlaying) {
                    delay(2000)
                    showControlsState = false
                }
            }
        }
    }
}

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    player: MediaController,
    requireLandscapeMode: () -> Unit,
    requirePortraitMode: () -> Unit,
) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }
    var currentPosition by remember { mutableLongStateOf(player.currentPosition) }
    val duration = player.duration
    val safeDuration = duration.takeIf { it > 0 } ?: 0L
    val orientation = LocalConfiguration.current.orientation
    val isLandscapeMode = orientation == Configuration.ORIENTATION_LANDSCAPE

    // Listen for playback state
    LaunchedEffect(player) {
        while (true) {
            currentPosition = player.currentPosition
            isPlaying = player.isPlaying
            delay(500)
        }
    }

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            IconButton(
                onClick = {
                    val newPosition = (currentPosition - 9999).coerceAtLeast(0L)
                    player.seekTo(newPosition)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Rewind 9 seconds",
                    tint = Color.White
                )
            }

            // Play/Pause button
            IconButton(
                onClick = {
                    if (player.isPlaying) player.pause() else player.play()
                    isPlaying = player.isPlaying
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                if (isPlaying) {
                    Icon(
                        painter = painterResource(id = R.drawable.pause_circle_24px),
                        contentDescription = "Pause",
                        tint = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White
                    )
                }
            }

            IconButton(
                onClick = {
                    val newPosition = (currentPosition + 10000).coerceAtMost(safeDuration)
                    player.seekTo(newPosition)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Fast Forward 10 seconds",
                    tint = Color.White
                )
            }
        }

        // Time, fullscreen mode and slider
        if (safeDuration > 0) {
            Column(
                modifier = Modifier
                    .padding(
                        bottom = if (isLandscapeMode) 50.dp else 0.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                    .align(Alignment.BottomCenter)
            ) {
                // Time text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${formatTime(currentPosition)} / ${formatTime(safeDuration)}",
                        color = Color.White,
                        fontSize = 12.sp
                    )

                    IconButton(
                        onClick = {
                            if (isLandscapeMode) {
                                requirePortraitMode()
                            } else {
                                requireLandscapeMode()
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(alpha = 0.7f)
                        )
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (!isLandscapeMode) R.drawable.fullscreen_24px
                                else R.drawable.fullscreen_exit_24px
                            ),
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
                // Slider
                BufferedSlider(
                    currentPosition = currentPosition,
                    bufferedPosition = player.bufferedPosition,
                    duration = safeDuration,
                    onSeek = { player.seekTo(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BufferedSlider(
    currentPosition: Long,
    bufferedPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val safeDuration = duration.takeIf { it > 0 } ?: 1L
    val bufferedFraction = bufferedPosition.toFloat() / safeDuration

    var sliderPosition by remember { mutableFloatStateOf(currentPosition.toFloat()) }
    var isUserInteracting by remember { mutableStateOf(false) }

    // Sync slider value with player only if user is not interacting
    LaunchedEffect(currentPosition, isUserInteracting) {
        if (!isUserInteracting) {
            sliderPosition = currentPosition.coerceAtMost(duration).toFloat()
        }
    }

    val progressFraction = sliderPosition / safeDuration

    Box(modifier = modifier.height(32.dp)) {
        // Track nền
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.CenterStart)
                .background(Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(50))
        )

        // Buffered track
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = bufferedFraction.coerceIn(0f, 1f))
                .height(4.dp)
                .align(Alignment.CenterStart)
                .background(Color.LightGray, shape = RoundedCornerShape(50))
        )

        // Played track
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progressFraction.coerceIn(0f, 1f))
                .height(4.dp)
                .align(Alignment.CenterStart)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50))
        )

        // Slider tương tác
        Slider(
            value = sliderPosition,
            onValueChange = {
                isUserInteracting = true
                sliderPosition = it
            },
            onValueChangeFinished = {
                isUserInteracting = false
                onSeek(sliderPosition.toLong())
            },
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent,
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        )

    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
