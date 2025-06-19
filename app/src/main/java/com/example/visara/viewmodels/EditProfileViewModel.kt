package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.UserModel
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileScreenUiState())
    val uiState = _uiState.asStateFlow()
    private val _eventChannel = Channel<EditProfileScreenEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        observerCurrentUser()
        observerAuthenticationState()
    }

    private fun observerCurrentUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { currentUser->
                _uiState.update {
                    it.copy(currentUser = currentUser)
                }
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

    fun updateFullName(newFullName: String) {
        viewModelScope.launch {
            val result = authRepository.updateUser(fullName = newFullName)
            if (result.isSuccess) {
                _uiState.update { oldState ->
                    oldState.copy(currentUser = result.getOrNull() ?: oldState.currentUser)
                }
                _eventChannel.send(EditProfileScreenEvent.UpdateFullNameSuccess)
                userRepository.syncCurrentUser()
            } else {
                _eventChannel.send(EditProfileScreenEvent.UpdateFullNameFailure)
            }
        }
    }

    fun updateBio(bio: String) {
        viewModelScope.launch {
            val result = authRepository.updateUser(bio = bio)
            if (result.isSuccess) {
                _uiState.update { oldState ->
                    oldState.copy(currentUser = result.getOrNull() ?: oldState.currentUser)
                }
                _eventChannel.send(EditProfileScreenEvent.UpdateBioSuccess)
                userRepository.syncCurrentUser()
            } else {
                _eventChannel.send(EditProfileScreenEvent.UpdateBioFailure)
            }
        }
    }
}

sealed class EditProfileScreenEvent {
    data object UpdateBioSuccess : EditProfileScreenEvent()
    data object UpdateBioFailure : EditProfileScreenEvent()
    data object UpdateFullNameSuccess : EditProfileScreenEvent()
    data object UpdateFullNameFailure : EditProfileScreenEvent()
}

data class EditProfileScreenUiState(
    val isAuthenticated: Boolean = false,
    val currentUser: UserModel? = null,
)
