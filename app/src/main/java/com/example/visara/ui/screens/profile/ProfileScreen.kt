package com.example.visara.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.visara.ui.components.VisaraVideoPlayer
import com.example.visara.ui.components.rememberVideoPlayerManager

@Composable
fun ProfileScreen(
    authenticated: Boolean = false,
    navigateToLogin: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val videoPlayerManager = rememberVideoPlayerManager()
        LaunchedEffect(Unit) {
            val videoUrl = "http://10.0.2.2:8080/67d93e93ca386d2312a19f5c/output.mpd"
            val videoUrl2 = "http://10.0.2.2:8080/67e42c30bb79412ece6f639a/output.mpd"
            videoPlayerManager.playDash(videoUrl)
        }

        DisposableEffect(Unit) {
            onDispose { videoPlayerManager.exoPlayer.pause() }
        }


        VisaraVideoPlayer(
            videoPlayerManager = videoPlayerManager
        )
    }
}
