package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.UserModel
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoDetailRepository
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val videoRepository: VideoRepository,
    private val videoDetailRepository: VideoDetailRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<ProfileScreenUiState> = MutableStateFlow(ProfileScreenUiState(isLoading = true))
    val uiState: StateFlow<ProfileScreenUiState> = _uiState.asStateFlow()

    init {
        observerAuthenticationState()
        observerCurrentUser()
    }

    fun setProfile(isMyProfileRequested: Boolean, username: String?) {
        viewModelScope.launch {
            val currentUser = userRepository.currentUser.first()

            if (isMyProfileRequested && currentUser == null) {
                _uiState.update {
                    ProfileScreenUiState(
                        isMyProfileRequested = true,
                        isAuthenticated = false,
                    )
                }
                return@launch
            }

            if (isMyProfileRequested && currentUser != null) {
                val videos = videoRepository.getAllVideoByUserId(currentUser.id)
                _uiState.update {
                    ProfileScreenUiState(
                        isMyProfileRequested = true,
                        isAuthenticated = true,
                        isMyProfile = true,
                        user = currentUser,
                        videos = videos,
                    )
                }
                return@launch
            }

            // Only update when username is different with current username
            if (username!= null && username != _uiState.value.user?.username) {
                _uiState.update { oldState ->
                    val user = if (username == oldState.user?.username) oldState.user
                    else userRepository.getPublicUser(username)

                    val isFollowing = if (!oldState.isAuthenticated) false
                    else if(username == oldState.user?.username) oldState.isFollowing
                    else userRepository.checkIsFollowingThisUser(username)

                    val videos: List<VideoModel> = if (user?.id != null) videoRepository.getAllVideoByUserId(user.id)
                    else emptyList()

                    ProfileScreenUiState(
                        isMyProfileRequested = false,
                        isMyProfile = username == currentUser?.username,
                        isAuthenticated = currentUser != null,
                        user = user,
                        isFollowing = isFollowing,
                        videos = videos,
                    )
                }
            }
        }
    }

    private fun observerAuthenticationState() {
        viewModelScope.launch {
            authRepository.isAuthenticated.collect { isAuthenticated->
                _uiState.update { it.copy(isAuthenticated = isAuthenticated) }
            }
        }
    }

    private fun observerCurrentUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { currentUser->
                _uiState.update { oldState ->
                    val isFollowing = if (currentUser == null) false
                    else oldState.user?.username?.let { userRepository.checkIsFollowingThisUser(it) } == true

                    oldState.copy(isFollowing = isFollowing)
                }
            }
        }
    }

    fun selectVideo(video: VideoModel) {
        viewModelScope.launch {
            videoDetailRepository.setVideoDetail(video)
        }
    }

    fun follow() {
        viewModelScope.launch {
            val username = _uiState.value.user?.username
            if (username == null) return@launch
            if (_uiState.value.isFollowing) return@launch
            val currentUser = userRepository.currentUser.first()
            if (currentUser == null) return@launch

            _uiState.value.user?.let { user ->
                val newUser = user.copy(followerCount = user.followerCount + 1)
                _uiState.update { it.copy(isFollowing = true, user = newUser) }

                if (username != currentUser.username) {
                    val result = userRepository.followUser(username)
                    if (!result) {
                        _uiState.update { it.copy(isFollowing = false, user = user) } // Rollback
                    }
                }
            }
        }
    }

    fun unfollow() {
        viewModelScope.launch {
            val username = _uiState.value.user?.username
            if (username == null) return@launch
            if (_uiState.value.isFollowing == false) return@launch
            val currentUser = userRepository.currentUser.first()
            if (currentUser == null) return@launch

            _uiState.value.user?.let { user ->
                val newUser = user.copy(followerCount = user.followerCount - 1)
                _uiState.update { it.copy(isFollowing = false, user = newUser) }

                if (username != currentUser.username) {
                    val result = userRepository.unfollowUser(username)
                    if (!result) {
                        _uiState.update { it.copy(isFollowing = true, user = user) } // Rollback
                    }
                }
            }
        }
    }
}

data class ProfileScreenUiState(
    val isMyProfileRequested: Boolean = false,
    val isMyProfile: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isFollowing: Boolean = false,
    val user: UserModel? = null,
    val videos: List<VideoModel> = emptyList(),
    val isLoading: Boolean = false,
)
