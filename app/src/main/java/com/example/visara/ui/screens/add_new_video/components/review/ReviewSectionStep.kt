package com.example.visara.ui.screens.add_new_video.components.review

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.visara.ui.components.LocalVideoPlayerManager
import com.example.visara.ui.components.VisaraVideoPlayer

@Composable
fun ReviewSectionStep(
    modifier: Modifier = Modifier,
    videoPlayerManager: LocalVideoPlayerManager,
    onBack: () -> Unit,
    onGoNext: () -> Unit,
) {
    LaunchedEffect(Unit) {
        videoPlayerManager.player.play()
    }

    DisposableEffect(Unit) {
        onDispose { videoPlayerManager.player.pause() }
    }

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart),
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
        Box(modifier = modifier.weight(1f)) {
//            VisaraVideoPlayer(videoPlayerManager = videoPlayerManager)
            VisaraVideoPlayer(
                player = videoPlayerManager.player,
                modifier = Modifier.fillMaxWidth(),
                requireLandscapeMode = {},
                requirePortraitMode = {},
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onGoNext,
                modifier = Modifier.align(Alignment.BottomEnd),
            ) {
                Text("Next")
            }
        }
    }
}

