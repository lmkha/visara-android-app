package com.example.visara.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.UserModel
import com.example.visara.data.repository.AppSettingsRepository
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoDetailRepository
import com.example.visara.data.repository.VideoDetailState
import com.example.visara.device.NetworkMonitor
import com.example.visara.ui.theme.AppTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()
    private var wasOfflineBefore = false

    init {
        observerTheme()
        observerAuthenticationState()
        observerCurrentUser()
        observerVideoDetail()
        observerNetworkState()

//        val tag = "CHECK_VAR"
//
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(tag, "Fetching FCM registration failed!", task.exception)
//                return@OnCompleteListener
//            }
//
//            val token = task.result
//
//            Log.d(tag, "FCM token: $token")
//        })
        Log.i("CHECK_VAR", "init app view model")
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

    private fun observerNetworkState() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                if (!isOnline) {
                    wasOfflineBefore = true
                    _appState.update { it.copy(isOnline = false, shouldDisplayIsOnlineStatus = true) }
                } else {
                    if (wasOfflineBefore) {
                        _appState.update { it.copy(isOnline = true, shouldDisplayIsOnlineStatus = true) }
                        delay(5000)
                        _appState.update { it.copy(shouldDisplayIsOnlineStatus = false) }
                    } else {
                        _appState.update { it.copy(isOnline = true, shouldDisplayIsOnlineStatus = false) }
                    }
                }
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

    fun enableLandscapeMode() {
        _appState.update { it.copy(isLandscapeMode = true) }
    }

    fun enablePortraitMode() {
        _appState.update { it.copy(isLandscapeMode = false) }
    }
}

data class AppState(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isAuthenticated: Boolean = false,
    val currentUser: UserModel? = null,
    val videoDetailState: VideoDetailState = VideoDetailState(),
    val isOnline: Boolean = false,
    val shouldDisplayIsOnlineStatus: Boolean = false,
    val isLandscapeMode: Boolean = false,
)
