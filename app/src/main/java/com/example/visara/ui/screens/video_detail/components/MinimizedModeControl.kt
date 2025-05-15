package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.visara.R

@Composable
fun MinimizedModeControl(
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onClose: () -> Unit,
    onEnableFullScreenMode: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onEnableFullScreenMode)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(bottomStart = 8.dp))
                .background(color = Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onClose)

        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (isPlaying) {
            IconButton(
                onClick = onPause,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.7f),
                    contentColor = Color.White,
                ),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pause_circle_24px),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        } else {
            IconButton(
                onClick = onPlay,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.7f),
                    contentColor = Color.White,
                ),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
    }
}