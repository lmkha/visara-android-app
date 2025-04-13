package com.example.visara.ui.screens.add_new_video

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.visara.ui.components.VideoPlayerManager
import com.example.visara.ui.components.VisaraUriVideoPlayer
import kotlinx.coroutines.launch

@Composable
fun AddNewVideoScreen(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
) {
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val scope = rememberCoroutineScope()
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri->
        videoUri = uri
        uri?.let { scope.launch { videoPlayerManager.playFromUrl(it) }}
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
        }) {
            Text("Open video picker")
        }
        if (videoUri != null) {
            VisaraUriVideoPlayer(
                videoPlayerManager = videoPlayerManager,
            )
        }
    }
}
