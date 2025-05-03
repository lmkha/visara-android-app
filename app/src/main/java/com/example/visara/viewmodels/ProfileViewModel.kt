package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.UserModel
import com.example.visara.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<ProfileScreenUiState> = MutableStateFlow(ProfileScreenUiState())
    val uiState: StateFlow<ProfileScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getCurrentUser()?.let { currentUser->
                _uiState.update { it.copy(user = currentUser) }
            }
        }
    }
}

data class ProfileScreenUiState(
    val user: UserModel? = null,
    val isLoading: Boolean = false,
)

