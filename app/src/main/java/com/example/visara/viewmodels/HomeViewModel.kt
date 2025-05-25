package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.VideoDetailRepository
import com.example.visara.data.repository.VideoRepository
import com.example.visara.common.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val videoDetailRepository: VideoDetailRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeScreenUiState> = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()
    var shouldReFetchWhenOnline: Boolean = false

    init {
        observerNetworkState()
        refresh()
    }

    private fun observerNetworkState() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                if (isOnline && shouldReFetchWhenOnline) {
                    refresh()
                    shouldReFetchWhenOnline = false
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            if (networkMonitor.isOnline.first()) {
                _uiState.update { it.copy(isLoading = true, isRefreshing = true) }
                val newVideoList = videoRepository.getVideoForHomeScreen()
                if (newVideoList != null) {
                    _uiState.update {
                        it.copy(
                            videos = newVideoList,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                }
            } else {
                shouldReFetchWhenOnline = true
            }
        }
    }

    fun selectVideo(video: VideoModel) {
        viewModelScope.launch {
            videoDetailRepository.setVideoDetail(video)
        }
    }
}

data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val videos: List<VideoModel> = emptyList(),
)
