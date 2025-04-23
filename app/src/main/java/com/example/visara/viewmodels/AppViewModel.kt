package com.example.visara.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.local.data_store.appSettingsDataStore
import com.example.visara.data.repository.AuthRepository
import com.example.visara.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val themeKey = SettingsViewModel.THEME_KEY
    private val appSettingsDataStore = appContext.appSettingsDataStore

    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        observerTheme()
        observerAuthenticationState()
    }

    private fun observerTheme() {
        viewModelScope.launch {
            appSettingsDataStore.data
                .map { prefs ->
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
}

data class AppState(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isLogged: Boolean = false,
)
