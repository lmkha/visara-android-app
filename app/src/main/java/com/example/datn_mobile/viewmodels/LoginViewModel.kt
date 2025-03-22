package com.example.datn_mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn_mobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private var _uiState: MutableStateFlow<UiState> = MutableStateFlow<UiState>(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val token = authRepository.getToken()
        if (!token.isNullOrEmpty()) {
            _uiState.update { oldState-> oldState.copy(authenticated = true, accessToken = token) }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.login(username, password)
            val token = authRepository.getToken()
            token?.let {
                _uiState.update {oldState-> oldState.copy(authenticated = true, accessToken = it) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.removeToken()
            val token = authRepository.getToken()
            if (token.isNullOrEmpty()) {
                _uiState.update { oldState-> oldState.copy(authenticated = false, accessToken = "") }
            }
        }
    }
}

data class UiState(
    val authenticated: Boolean = false,
    val accessToken: String = "",
)
