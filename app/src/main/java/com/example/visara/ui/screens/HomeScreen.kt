package com.example.visara.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen() {
//    val videoUrl = "http://10.0.2.2:8080/67d93e93ca386d2312a19f5c/output.mpd"
//    VideoPlayerDash(
//        modifier = Modifier.fillMaxWidth(),
//        url = videoUrl,
//    )
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(100) {
            Text("Item $it")
        }
    }
}
