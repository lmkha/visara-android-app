package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.UserModel
import com.example.visara.data.repository.AppSettingsRepository
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoDetailRepository
import com.example.visara.data.repository.VideoDetailState
import com.example.visara.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val userRepository: UserRepository,
    private val videoDetailRepository: VideoDetailRepository,
) : ViewModel() {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        observerTheme()
        observerAuthenticationState()
        observerCurrentUser()
        observerVideoDetail()
    }

    private fun observerTheme() {
        viewModelScope.launch {
            appSettingsRepository.appSettingsFlow.map { prefs ->
                val themeKey = appSettingsRepository.themeKey
                val themeName = prefs[themeKey]
                AppTheme.entries.firstOrNull { it.name == themeName } ?: AppTheme.SYSTEM
            }
            .collect { theme ->
                _appState.update { it.copy(appTheme = theme) }
            }
        }
    }

    private fun observerAuthenticationState() {
        viewModelScope.launch {
            authRepository.isAuthenticated.collect { isAuthenticated->
                _appState.update { it.copy(isAuthenticated = isAuthenticated) }
            }
        }
    }

    private fun observerCurrentUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { currentUser->
                _appState.update { it.copy(currentUser = currentUser) }
            }
        }
    }

    private fun observerVideoDetail() {
        viewModelScope.launch {
            videoDetailRepository.videoDetail.collect { videoDetail ->
                _appState.update { it.copy(videoDetailState = videoDetail) }
            }
        }
    }

    fun minimizeVideoDetail() {
        viewModelScope.launch {
            videoDetailRepository.enableMinimizedMode()
        }
    }

    fun pauseVideoDetail() {
        viewModelScope.launch {
            videoDetailRepository.dashVideoPlayerManager.player.pause()
        }
    }

    fun hideVideoDetail() {
        videoDetailRepository.hide()
    }

    fun displayVideoDetail() {
        videoDetailRepository.display()
    }

    fun syncCurrentUser() {
        viewModelScope.launch { userRepository.syncCurrentUser() }
    }
}

data class AppState(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isAuthenticated: Boolean = false,
    val currentUser: UserModel? = null,
    val videoDetailState: VideoDetailState = VideoDetailState(),
)
