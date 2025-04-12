package com.example.visara.ui.screens.add_new_video

import android.net.Uri
import android.util.Log
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.visara.ui.components.VisaraVideoPlayer
import com.example.visara.ui.components.rememberVideoPlayerManager

@Composable
fun AddNewVideoScreen(
    modifier: Modifier = Modifier,
) {
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val videoPlayerManager = rememberVideoPlayerManager()
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri->
        videoUri = uri
        if (uri != null) {
            Log.d("CHECK_VAR", "Selected URI: $uri")
            videoPlayerManager.playFromUrl(uri)
        } else {
            Log.d("CHECK_VAR", "No media selected")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
        }) {
            Text("Open video picker")
        }
        if (videoUri != null) {
            VisaraVideoPlayer(
                videoPlayerManager = videoPlayerManager,
            )
        }
    }
}
