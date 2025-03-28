package com.example.datn_mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.datn_mobile.ui.components.CloudinaryImage
import com.example.datn_mobile.ui.components.VideoPlayerDash

@Composable
fun HomeScreen(navigateToProfile: () -> Unit) {
    val videoUrl = "http://10.0.2.2:8080/67d93e93ca386d2312a19f5c/output.mpd"
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { navigateToProfile() }) { Text("Edit profile") }
//        VideoPlayerDash(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(400.dp),
//            url = videoUrl,
//        )
        CloudinaryImage()
    }
}
