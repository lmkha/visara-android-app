package com.example.visara.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.VideoModel
import com.example.visara.data.model.VideoPrivacy
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditVideoViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditVideoScreenUiState())
    val uiState = _uiState.asStateFlow()
    private val _eventChannel = Channel<EditVideoScreenEvent>()
    val eventFlow: Flow<EditVideoScreenEvent> = _eventChannel.receiveAsFlow()

    fun setVideo(video: VideoModel) {
        viewModelScope.launch {
            if (uiState.value.video == null) {
                _uiState.update { it.copy(video = video) }
            }
        }
    }

    fun updateVideo(
        title: String,
        thumbnailUri: Uri?,
        description: String,
        hashtags: List<String>,
        privacy: VideoPrivacy,
        isAllowComment: Boolean,
    ) {
        viewModelScope.launch {
            delay(1000)
            val videoId = _uiState.value.video?.id
            if (videoId != null) {
                val result = videoRepository.updateVideo(
                    videoId = videoId,
                    title = title,
                    thumbnailUri = thumbnailUri,
                    description = description,
                    hashtags = hashtags,
                    privacy = privacy,
                    isAllowComment = isAllowComment
                )
                if (result) _eventChannel.send(EditVideoScreenEvent.UpdateVideoSuccess)
                else _eventChannel.send(EditVideoScreenEvent.UpdateVideoFailure)
            }
        }
    }
}

sealed class EditVideoScreenEvent {
    data object UpdateVideoSuccess: EditVideoScreenEvent()
    data object UpdateVideoFailure: EditVideoScreenEvent()
}

data class EditVideoScreenUiState(
    val video: VideoModel? = null,
)
