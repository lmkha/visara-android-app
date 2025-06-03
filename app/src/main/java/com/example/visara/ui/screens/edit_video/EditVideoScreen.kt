package com.example.visara.ui.screens.edit_video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.visara.data.model.VideoModel

@Composable
fun EditVideoScreen(
   modifier: Modifier = Modifier,
   video: VideoModel,
) {
   Column(
      modifier = modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
   ) {
      Text(video.title)
   }
}
