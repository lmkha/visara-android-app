package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.domain.LoginUseCase
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
    private val loginUseCase: LoginUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var _uiState: MutableStateFlow<LoginScreenUiState> = MutableStateFlow<LoginScreenUiState>(LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.asStateFlow()

    init {
        val token = authRepository.getToken()
        if (!token.isNullOrEmpty()) {
            _uiState.update { oldState-> oldState.copy(authenticated = true, accessToken = token) }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val token = loginUseCase(username, password)
            if (!token.isNullOrEmpty()) {
                _uiState.update {oldState-> oldState.copy(authenticated = true, accessToken = token) }
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

    fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = userRepository.getCurrentUser()
            if (!currentUser?.email.isNullOrEmpty()) {
                _uiState.update { oldState-> oldState.copy(email = currentUser.email) }
            }
        }
    }

}

data class LoginScreenUiState(
    val authenticated: Boolean = false,
    val accessToken: String = "",
    val email: String = "",
)
