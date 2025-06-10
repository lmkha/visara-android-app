package com.example.visara.ui.screens.add_new_video

import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.visara.ui.screens.add_new_video.components.enter_video_info.EnterVideoInfoStep
import com.example.visara.ui.screens.add_new_video.components.review.ReviewSectionStep
import com.example.visara.ui.screens.add_new_video.components.select.SelectVideoStep
import com.example.visara.ui.screens.studio.StudioSelectedTag
import com.example.visara.viewmodels.AddNewVideoScreenEvent
import com.example.visara.viewmodels.AddNewVideoViewModel

@Composable
fun AddNewVideoScreen(
    modifier: Modifier = Modifier,
    viewModel: AddNewVideoViewModel,
    startingStep: AddNewVideoStep = AddNewVideoStep.SELECT_VIDEO,
    onNavigateToStudio: (selectedTag: StudioSelectedTag) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current.applicationContext
    val currentMediaController by viewModel.mediaController.collectAsStateWithLifecycle()
    var videoUri by remember(uiState.draftData.videoUri) { mutableStateOf(uiState.draftData.videoUri) }
    var step by remember(startingStep) { mutableStateOf(startingStep) }
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                videoUri = it
                step = AddNewVideoStep.REVIEW_VIDEO

            } catch (e : Exception) {
                Log.e("DraftVideo", "Error: Cannot get persistable permission for video URI: ${e.message}")
            }
        }
    }

    LaunchedEffect(videoUri) {
        videoUri?.let { viewModel.playUriVideo(it) }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is AddNewVideoScreenEvent.UploadNewVideoMetaDataSuccess -> {
                        onNavigateToStudio(StudioSelectedTag.UPLOADING)
                    }
                    is AddNewVideoScreenEvent.DraftVideoPostSuccess -> {
                        onNavigateToStudio(StudioSelectedTag.DRAFT)
                    }
                    else -> {}
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { currentMediaController?.stop() }
    }

    if (step != AddNewVideoStep.entries.first()) {
        BackHandler {
            val stepIndex = AddNewVideoStep.entries.indexOf(step)
            AddNewVideoStep.entries.getOrNull(stepIndex - 1)?.let {
                step = it
            }
        }
    }

    Box(modifier = modifier
        .statusBarsPadding()
        .navigationBarsPadding()) {
        when (step) {
            AddNewVideoStep.SELECT_VIDEO -> {
                SelectVideoStep(
                    onOpenPhotoPicker = {
                        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
                    },
                    modifier = modifier,
                )
            }
            AddNewVideoStep.REVIEW_VIDEO -> {
                ReviewSectionStep(
                    mediaController = currentMediaController,
                    modifier = modifier,
                    onBack = { step = AddNewVideoStep.SELECT_VIDEO },
                    onGoNext = { step = AddNewVideoStep.ENTER_DATA_AND_POST },
                )
            }
            AddNewVideoStep.ENTER_DATA_AND_POST -> {
                EnterVideoInfoStep(
                    videoUri = videoUri,
                    uiState = uiState,
                    onBack = { step = AddNewVideoStep.REVIEW_VIDEO },
                    onPost = { viewModel.postVideo(it.copy(videoUri = videoUri)) },
                    onDraft = { viewModel.draftVideoPost(it.copy(videoUri = videoUri)) }
                )
            }
        }
    }
}

enum class AddNewVideoStep {
    SELECT_VIDEO,
    REVIEW_VIDEO,
    ENTER_DATA_AND_POST
}
