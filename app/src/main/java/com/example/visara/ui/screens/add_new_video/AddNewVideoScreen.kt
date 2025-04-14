package com.example.visara.ui.screens.add_new_video

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.visara.ui.components.VideoPlayerManager
import com.example.visara.ui.screens.add_new_video.components.enter_video_info.EnterVideoInfoStep
import com.example.visara.ui.screens.add_new_video.components.review.ReviewSectionStep
import com.example.visara.ui.screens.add_new_video.components.select.SelectVideoStep
import kotlinx.coroutines.launch

@Composable
fun AddNewVideoScreen(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    onHideBottomNavigationBar: () -> Unit,
    onShowBottomNavigationBar: () -> Unit,
) {
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val scope = rememberCoroutineScope()
    var step by remember { mutableIntStateOf(3) }
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri->
        videoUri = uri
        uri?.let {
            scope.launch {
                videoPlayerManager.playFromUrl(it)
                step = 2
            }
        }
    }

    LaunchedEffect(Unit) {
        onHideBottomNavigationBar()
    }

    DisposableEffect(Unit) {
        onDispose { onShowBottomNavigationBar() }
    }

    /*
    if (step > 1) {
        BackHandler {
            step -= 1
        }
    }
     */

    Box(modifier = modifier) {
        when (step) {
            1 -> {
                SelectVideoStep(
                    onOpenPhotoPicker = {
                        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
                    },
                    modifier = modifier,
                )
            }
            2 -> {
                if (videoUri != null) {
                    ReviewSectionStep(
                        videoPlayerManager = videoPlayerManager,
                        modifier = modifier,
                        onBack = { step -= 1},
                        onGoNext = { step += 1 },
                    )
                }
            }
            3 -> {
                EnterVideoInfoStep()
            }
            else -> { }
        }
    }
}
