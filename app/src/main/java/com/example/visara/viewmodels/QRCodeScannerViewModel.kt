package com.example.visara.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.visara.utils.BarcodeAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class QRCodeScannerViewModel @Inject constructor(
    @param:ApplicationContext val appContext: Context,
    val barcodeAnalyzer: BarcodeAnalyzer,
) : ViewModel() {
    private val _uiState = MutableStateFlow(QRCodeScannerScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        barcodeAnalyzer.setOnQrCodeScannedListener { data ->
            onQrCodeScanned(data)
        }
    }

    private fun onQrCodeScanned(data: String) {
        _uiState.update { it.copy(data) }
    }
}

data class QRCodeScannerScreenUiState(
    val qrCodeStringValue: String? = null,
)
