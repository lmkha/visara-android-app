package com.example.datn_mobile.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.datn_mobile.ui.components.VideoPlayerDash

@Composable
fun MainScreen() {
    val videoUrl = "http://10.0.2.2:8080/67d93e93ca386d2312a19f5c/output.mpd"
    VideoPlayerDash(url = videoUrl, modifier = Modifier.fillMaxSize())
}
