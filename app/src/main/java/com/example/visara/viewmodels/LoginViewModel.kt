package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var _uiState: MutableStateFlow<LoginScreenUiState> = MutableStateFlow<LoginScreenUiState>(LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.asStateFlow()

    init {
        val isLogged = authRepository.isAuthenticated.value
        if (isLogged) {
            _uiState.update { it.copy(isLogged = true) }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val isAuthenticated = authRepository.login(username, password)
            if (isAuthenticated) {
                userRepository.syncCurrentUser()
            }

            if (isAuthenticated) {
                _uiState.update { oldState-> oldState.copy(isLogged = true, isProcessing = false) }
            } else {
                _uiState.update { oldState-> oldState.copy(isLogged = false, isProcessing = false) }
            }
        }
    }
}

data class LoginScreenUiState(
    val isLogged: Boolean = false,
    val email: String = "",
    val isProcessing: Boolean = false,
)
