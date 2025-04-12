package com.example.visara.ui.screens.video_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.ui.components.VideoPlayerManager
import com.example.visara.ui.components.VisaraVideoPlayer

@Composable
fun rememberVideoDetailState(manager: VideoPlayerManager): VideoDetailState {
    return remember {
        VideoDetailState(manager = manager)
    }
}

@Composable
fun MinimizedVideoDetail(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    isVisible: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onExpand: () -> Unit,
    onClose: () -> Unit,
) {
    if (isVisible) {
        Box(modifier = modifier.clickable(onClick = onExpand)) {
            VisaraVideoPlayer(
                videoPlayerManager = videoPlayerManager,
                showControls = false,
                modifier = Modifier
                    .width(200.dp)
                ,
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = Color.White,
                )
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                if (isPlaying) {
                    IconButton(onClick = onPause) {
                        Icon(
                            painter = painterResource(id = R.drawable.pause_circle_24px),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                } else {
                    IconButton(onClick = onPlay) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}
