package com.example.visara.ui.screens.add_new_video.components.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.ui.components.VideoPlayerManager
import com.example.visara.ui.components.VisaraVideoPlayer

@Composable
fun ReviewSectionStep(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    onBack: () -> Unit,
    onGoNext: () -> Unit,
) {
    DisposableEffect(Unit) {
        onDispose { videoPlayerManager.mediaController.pause() }
    }

    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onBack,
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }

                Text(
                    text = "Review video",
                    fontWeight = FontWeight.Medium,
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.Center)
                .background(color = Color.Black)
        ) {
            VisaraVideoPlayer(
                player = videoPlayerManager.mediaController,
                requireLandscapeMode = {},
                requirePortraitMode = {},
            )
        }
    }
}
