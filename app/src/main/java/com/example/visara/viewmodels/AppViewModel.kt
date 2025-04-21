package com.example.visara.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.local.data_store.settingsDataStore
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
) : ViewModel() {
    private val themeKey = SettingsViewModel.THEME_KEY
    private val dataStore = appContext.settingsDataStore

    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data
                .map { prefs ->
                    val themeName = prefs[themeKey]
                    AppTheme.entries.firstOrNull { it.name == themeName } ?: AppTheme.SYSTEM
                }
                .collect { theme ->
                    _appState.update { it.copy(appTheme = theme) }
                }
        }
    }
}


data class AppState(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isLoggedIn: Boolean = false,
    val username: String = ""
)
