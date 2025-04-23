package com.example.visara.viewmodels

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.local.data_store.appSettingsDataStore
import com.example.visara.data.repository.AuthRepository
import com.example.visara.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val appSettingsDataStore = appContext.appSettingsDataStore
    private val _uiState: MutableStateFlow<SettingsScreenUiState> = MutableStateFlow(SettingsScreenUiState())
    val uiState: StateFlow<SettingsScreenUiState> = _uiState.asStateFlow()
    companion object {
        val THEME_KEY = stringPreferencesKey("theme_pref")
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val prefs = appSettingsDataStore.data.first()
            val themeName = prefs[THEME_KEY]
            val theme = AppTheme.entries.find { it.name == themeName } ?: AppTheme.SYSTEM
            _uiState.value = _uiState.value.copy(theme = theme)
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsDataStore.edit { prefs ->
                prefs[THEME_KEY] = theme.name
                _uiState.value = _uiState.value.copy(theme = theme)
            }
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
)
