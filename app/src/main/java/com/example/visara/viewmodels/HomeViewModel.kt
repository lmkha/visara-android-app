package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeScreenUiState> = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val videoList = videoRepository.getVideoForHomeScreen()
            if (videoList != null) {
                _uiState.update { it.copy(videos = videoList, isLoading = false) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isRefreshing = true) }
            val newVideoList = videoRepository.getVideoForHomeScreen()
            if (newVideoList != null) {
                _uiState.update { it.copy(videos = newVideoList, isLoading = false, isRefreshing = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
            }
        }
    }
}

data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val videos: List<VideoModel> = emptyList(),
)
