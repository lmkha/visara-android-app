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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.visara.ui.components.VideoPlayerManager
import com.example.visara.ui.components.VisaraUriVideoPlayer

@Composable
fun ReviewSectionStep(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    onBack: () -> Unit,
    onGoNext: () -> Unit,
) {
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
            VisaraUriVideoPlayer(videoPlayerManager = videoPlayerManager)
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

