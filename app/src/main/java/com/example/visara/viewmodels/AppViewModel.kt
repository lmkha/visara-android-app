package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.repository.AppSettingsRepository
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.VideoRepository
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
    private val videoRepository: VideoRepository,
) : ViewModel() {
    private val themeKey = SettingsViewModel.THEME_KEY

    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        observerTheme()
        observerAuthenticationState()
    }

    private fun observerTheme() {
        viewModelScope.launch {
            appSettingsRepository.appSettingsFlow.map { prefs ->
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
            authRepository.isAuthenticated.collect { isLogged->
                _appState.update { it.copy(isLogged = isLogged) }
            }
        }
    }

    fun getVideoUrl(videoId: String) : String {
        return videoRepository.getVideoUrl(videoId)
    }
}

data class AppState(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isLogged: Boolean = false,
)
