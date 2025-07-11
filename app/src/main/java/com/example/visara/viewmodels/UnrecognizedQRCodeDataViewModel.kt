package com.example.visara.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnrecognizedQRCodeDataViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) : ViewModel() {
    private val clipboardManager by lazy {
        appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    private val _uiState = MutableStateFlow(UnrecognizedQRCodeDataScreenUiState())
    val uiState = _uiState.asStateFlow()
    private val _eventChannel = Channel<UnRecognizedQRCodeScreenEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun setQRCodeData(data: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(data = data) }
        }
    }

    fun saveDataToClipboard() {
        viewModelScope.launch {
            try {
                val data = _uiState.value.data
                val clipData = ClipData.newPlainText("QR_CODE_CONTENT", data)
                clipboardManager.setPrimaryClip(clipData)
                _eventChannel.send(UnRecognizedQRCodeScreenEvent.SaveDataToClipboardSuccess)
            } catch (_: Exception) {
                _eventChannel.send(UnRecognizedQRCodeScreenEvent.SaveDataToClipboardFailure)
            }
        }
    }
}

sealed class UnRecognizedQRCodeScreenEvent {
    data object SaveDataToClipboardSuccess : UnRecognizedQRCodeScreenEvent()
    data object SaveDataToClipboardFailure : UnRecognizedQRCodeScreenEvent()
}

data class UnrecognizedQRCodeDataScreenUiState(
    val data: String = "",
)
