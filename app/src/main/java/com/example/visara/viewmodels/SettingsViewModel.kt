package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.UserModel
import com.example.visara.data.repository.AppSettingsRepository
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsScreenUiState> = MutableStateFlow(SettingsScreenUiState())
    val uiState: StateFlow<SettingsScreenUiState> = _uiState.asStateFlow()

    init {
        observerThemeSettingsState()
        observerAuthenticationState()
        observerCurrentUser()
    }

    private fun observerThemeSettingsState() {
        viewModelScope.launch {
            appSettingsRepository.appSettingsFlow.collectLatest { prefs ->
                val themeKey = appSettingsRepository.themeKey
                val themeName = prefs[themeKey]
                val theme = AppTheme.entries.find { it.name == themeName } ?: AppTheme.SYSTEM
                _uiState.update { it.copy(theme = theme) }
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
                _uiState.update { it.copy(currentUser = currentUser) }
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            appSettingsRepository.updateTheme(theme)
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.logout()
        }
    }
}

data class SettingsScreenUiState (
    val theme: AppTheme = AppTheme.SYSTEM,
    val isAuthenticated: Boolean = false,
    val currentUser: UserModel? = null,
)
