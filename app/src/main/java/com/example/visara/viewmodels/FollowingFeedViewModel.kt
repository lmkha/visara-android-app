package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.PlayerManager
import com.example.visara.data.model.UserModel
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingFeedViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository,
    private val playerManager: PlayerManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FollowingScreenUiState())
    val uiState = _uiState.asStateFlow()
    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingFollowings = true,
                    isLoadingVideos = true,
                )
            }
            val isAuthenticated = authRepository.isAuthenticated.first()
            if (isAuthenticated) {
                val followingsDeferred = async { userRepository.getAllFollowings(0, 100) }
                val videosDeferred = async { videoRepository.getFollowingVideos(50) }
                val followings = followingsDeferred.await().map { it.user }
                val videos = videosDeferred.await()
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        isLoadingVideos = false,
                        isLoadingFollowings = false,
                        followings = followings,
                        videos = videos,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isAuthenticated = false,
                        isLoadingFollowings = false,
                        isLoadingVideos = false,
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            if (_uiState.value.isAuthenticated) {
                _uiState.update {
                    it.copy(
                        isLoadingFollowings = true,
                        isLoadingVideos = true,
                    )
                }
                val followingsDeferred = async { userRepository.getAllFollowings(0, 100) }
                val videosDeferred = async { videoRepository.getFollowingVideos(50) }
                val followings = followingsDeferred.await().map { it.user }
                val videos = videosDeferred.await()
                _uiState.update {
                    it.copy(
                        isLoadingVideos = false,
                        isLoadingFollowings = false,
                        followings = followings,
                        videos = videos,
                    )
                }
            }
        }
    }

    fun selectVideo(video: VideoModel) {
        playerManager.setVideoDetail(video)
    }
}

data class FollowingScreenUiState(
    val followings: List<UserModel> = emptyList(),
    val videos: List<VideoModel> = emptyList(),
    val isLoadingFollowings: Boolean = false,
    val isLoadingVideos: Boolean = false,
    val isAuthenticated: Boolean = false,
)
