package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import com.example.visara.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(

) : ViewModel() {
    private val _appState: MutableStateFlow<AppState> = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    fun setTheme(theme: AppTheme) {
        if (theme != _appState.value.appTheme) {
            _appState.update { it.copy(appTheme = theme) }
        }
    }
}

data class AppState(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isLoggedIn: Boolean = false,
    val username: String = ""
)
