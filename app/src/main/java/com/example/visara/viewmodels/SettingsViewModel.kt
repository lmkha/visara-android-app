package com.example.visara.viewmodels

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.UserModel
import com.example.visara.data.repository.AppSettingsRepository
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val localeManager: LocaleManager,
    @param:ApplicationContext private val appContext: Context,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsScreenUiState> = MutableStateFlow(SettingsScreenUiState())
    val uiState: StateFlow<SettingsScreenUiState> = _uiState.asStateFlow()
    private val _eventFlow = MutableSharedFlow<SettingsScreenEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        replay = 0,
    )
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getLocales()
        observerThemeSettingsState()
        observerAuthenticationState()
        observerCurrentUser()
    }

    private fun getLocales() {
        try {
            val appLocales = appContext.resources.configuration.locales
            val appLocalesX = localeManager.applicationLocales
            Log.d("CHECK_VAR", "Locales in configuration: $appLocales")
            Log.d("CHECK_VAR", "Locales in localeManager: $appLocalesX")
            val currentLocale = if (appLocales.size() > 0) {
                appLocales.get(0)
            } else { null }
            _uiState.update {
                it.copy(
                    appLocales = appLocales,
                    currentLocale = currentLocale,
                )
            }
        } catch (e: Exception) {
            Log.e("CHECK_VAR", e.toString())
        }
    }

    private fun observerThemeSettingsState() {
        viewModelScope.launch {
            appSettingsRepository.appSettingsFlow.collectLatest { prefs ->
                val themeKey = AppSettingsRepository.themeKey
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

    fun changePrivacy(isPrivate: Boolean) {
        viewModelScope.launch {
            val result = authRepository.updateUser(isPrivate = isPrivate)
            if (result.isSuccess) {
                userRepository.syncCurrentUser()
                result.getOrNull()?.let { currentUpdatedUser ->
                    _uiState.update {
                        it.copy(currentUser = currentUpdatedUser)
                    }
                    _eventFlow.emit(SettingsScreenEvent.ChangePrivacySuccess)
                }
            } else {
                _eventFlow.emit(SettingsScreenEvent.ChangePrivacyFailure)
            }
        }
    }

    fun changeLocale(locale: Locale) {
        viewModelScope.launch {
            if (locale.toLanguageTag() != uiState.value.currentLocale?.toLanguageTag()) {
                localeManager.applicationLocales = LocaleList(locale)
                _uiState.update { it.copy(currentLocale = locale) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.logout()
            userRepository.refreshCurrentUser()
            _eventFlow.emit(SettingsScreenEvent.LogoutSuccess)
        }
    }
}

data class SettingsScreenUiState (
    val theme: AppTheme = AppTheme.SYSTEM,
    val isAuthenticated: Boolean = false,
    val currentUser: UserModel? = null,
    val appLocales: LocaleList? = null,
    val currentLocale: Locale? = null,
)

sealed class SettingsScreenEvent {
    data object ChangePrivacySuccess : SettingsScreenEvent()
    data object ChangePrivacyFailure : SettingsScreenEvent()
    data object LogoutSuccess : SettingsScreenEvent()
    data object LogoutFailure : SettingsScreenEvent()
}
