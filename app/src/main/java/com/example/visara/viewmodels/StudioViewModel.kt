package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudioViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<StudioScreenUiState> = MutableStateFlow(StudioScreenUiState())
    val uiState: StateFlow<StudioScreenUiState> = _uiState.asStateFlow()

    init {
        observerPostingVideo()
    }

    private fun observerPostingVideo() {
        viewModelScope.launch {
            videoRepository.postingVideo.collectLatest { postingVideo ->
                if (postingVideo != null) {
                    videoRepository.syncPostingVideo()
                    _uiState.update { it.copy(postingVideo = postingVideo) }
                } else _uiState.update { it.copy(postingVideo = null) }
            }
        }
    }
}

data class StudioScreenUiState(
    val postingVideo: VideoModel? = null,
)
