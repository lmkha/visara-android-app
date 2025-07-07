package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.repository.AuthRepository
import com.example.visara.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private var _uiState = MutableStateFlow(LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.asStateFlow()
    private val _eventChannel = Channel<LoginScreenUiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        val isAuthenticated = authRepository.isAuthenticated.value
        if (isAuthenticated) {
            _uiState.update { it.copy(isAuthenticated = true) }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val loginResult = loginUseCase(username, password)
            if (loginResult.isSuccess) {
                _uiState.update { oldState-> oldState.copy(isAuthenticated = true, isProcessing = false) }
                _eventChannel.send(LoginScreenUiEvent.LoginSuccess)
            } else {
                val errorMessage = loginResult.exceptionOrNull()?.message ?: "Login failed"
                _eventChannel.send(LoginScreenUiEvent.LoginFailure(errorMessage))
                _uiState.update { oldState->
                    oldState.copy(
                        isAuthenticated = false,
                        isProcessing = false,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }
}

sealed class LoginScreenUiEvent {
    data object LoginSuccess : LoginScreenUiEvent()

    data class LoginFailure(val message: String) : LoginScreenUiEvent()
}

data class LoginScreenUiState(
    val isAuthenticated: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String = "",
)
