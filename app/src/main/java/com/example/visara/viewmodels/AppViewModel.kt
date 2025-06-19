package com.example.visara.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.UserModel
import com.example.visara.data.repository.AppSettingsRepository
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.PlayerManager
import com.example.visara.VideoDetailState
import com.example.visara.di.gson
import com.example.visara.utils.NetworkMonitor
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.theme.AppTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    private val authRepository: AuthRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val userRepository: UserRepository,
    private val playerManager: PlayerManager,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()
    private val _eventChannel = Channel<AppEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()
    private var wasOfflineBefore = false

    init {
        Log.d("CHECK_VAR", "init app view model")
        observerTheme()
        observerAuthenticationState()
        observerCurrentUser()
        observerVideoDetail()
        observerNetworkState()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("CHECK_VAR", "Fetching FCM registration failed!", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            Log.d("CHECK_VAR", "FCM token: $token")
        })
    }

    private fun observerTheme() {
        viewModelScope.launch {
            appSettingsRepository.appSettingsFlow.map { prefs ->
                val themeKey = AppSettingsRepository.themeKey
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
            playerManager.videoDetail.collect { videoDetail ->
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
            playerManager.enableMinimizedMode()
        }
    }

    fun pauseVideoDetail() {
        viewModelScope.launch {
            playerManager.pause()
        }
    }

    fun hideVideoDetail() {
        playerManager.hide()
    }

    fun displayVideoDetail() {
        playerManager.display()
    }

    fun syncCurrentUser() {
        viewModelScope.launch { userRepository.syncCurrentUser() }
    }

    fun saveCurrentPlaybackState() {
        playerManager.saveCurrentPlaybackState()
    }

    fun restoreStoredPlaybackState() {
        playerManager.restoreStoredPlaybackState()
    }

    fun handleNewIntent(intent: Intent?) {
        viewModelScope.launch {
            val requestType = intent?.getStringExtra("request-type")
            if (requestType == "navigation") {
                val route = intent.getStringExtra("route")
                var destination: Destination? = null
                try {
                    when (route) {
                        "studio" -> {
                            destination = intent.getStringExtra("destination")?.let {
                                gson.fromJson(it, Destination.Studio::class.java)
                            }
                        }

                        else -> {}
                    }
                } catch (e : Exception) {
                    Log.e("CHECK_VAR", "request navigate: $e")
                }
                if (destination != null) {
                    _eventChannel.send(AppEvent.NavigateToScreen(destination))
                }
            }
        }
    }
}

sealed class AppEvent {
    data class NavigateToScreen(val destination: Destination) : AppEvent()
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
