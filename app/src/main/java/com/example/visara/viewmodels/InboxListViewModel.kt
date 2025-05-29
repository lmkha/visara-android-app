package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class InboxListViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState: MutableStateFlow<InboxListScreenUiState> = MutableStateFlow(InboxListScreenUiState())
    val uiState: StateFlow<InboxListScreenUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                chats = listOf(
                    "mkhang",
                    "lamine",
                    "lmkha",
                    "rapha",
                    "iniesta",
                    "lionel",
                )
            )
        }
    }
}

data class InboxListScreenUiState(
    val chats: List<String> = emptyList(),
)
