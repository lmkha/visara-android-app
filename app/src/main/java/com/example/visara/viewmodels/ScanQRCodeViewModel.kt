package com.example.visara.viewmodels

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.utils.BarcodeAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ScanQRCodeViewModel @Inject constructor(
    @param:ApplicationContext val appContext: Context,
    val barcodeAnalyzer: BarcodeAnalyzer,
) : ViewModel() {
    private val _uiState = MutableStateFlow(QRCodeScannerScreenUiState())
    val uiState = _uiState.asStateFlow()
    private val _eventChannel = Channel<ScanQRCodeScreenEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()
    private val baseUrlHost = "visara.com"
    private var isProcessingQRData = false
    private var previousQrData: String? = null

    init {
        barcodeAnalyzer.setOnQrCodeScannedListener { data ->
            onQrCodeScanned(data)
        }
    }

    private fun validateQRCodeValue(qrCodeData: String?) : QRCodeScanResult {
        val uri = qrCodeData?.toUri() ?: return QRCodeScanResult.Failure

        // https://visara.com/profile/123e4567-e89b-12d3-a456-426614174000
        val isValidateScheme = (uri.scheme == "http" || uri.scheme == "https") && uri.host == baseUrlHost

        if (!isValidateScheme) QRCodeScanResult.Unrecognized(qrCodeData)

        val qrCodeResult = when (uri.pathSegments.firstOrNull()) {
            AllowedQRCode.PROFILE.path -> {
                if (uri.pathSegments.size != 2) return QRCodeScanResult.Unrecognized(qrCodeData)
                val username = uri.lastPathSegment
                val isValidUsername = !username.isNullOrBlank() && username.length < 20

                return if (isValidUsername) {
                    QRCodeScanResult.Profile(username)
                } else {
                    QRCodeScanResult.Unrecognized(qrCodeData)
                }
            }
            AllowedQRCode.VIDEO.path -> {
                val videoId = uri.lastPathSegment
                val isValidVideoId = !videoId.isNullOrBlank() && videoId.isUuid()
                return if (isValidVideoId) {
                    QRCodeScanResult.Video(videoId)
                } else {
                    QRCodeScanResult.Unrecognized(qrCodeData)
                }
            }
            else -> {
                QRCodeScanResult.Unrecognized(qrCodeData)
            }
        }
        return qrCodeResult
    }

    fun scanFromImageUri(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val data = barcodeAnalyzer.analyzeImageFromUri(context = appContext, uri = uri)
                onQrCodeScanned(data)
            }
        }
    }

    private fun String?.isUuid() : Boolean {
        if (this == null) return false
        return try {
            UUID.fromString(this)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun onQrCodeScanned(data: String?) {
        viewModelScope.launch {
            if (!isProcessingQRData && data != previousQrData) {
                isProcessingQRData = true
                val result = validateQRCodeValue(data)
                when (result) {
                    is QRCodeScanResult.Failure -> {
                        _eventChannel.send(ScanQRCodeScreenEvent.ScanFailed("Scan failed"))
                    }

                    is QRCodeScanResult.Unrecognized -> {
                        _eventChannel.send(ScanQRCodeScreenEvent.UnrecognizedDataScanned(result.rawData))
                    }

                    is QRCodeScanResult.Profile -> {
                        _eventChannel.send(ScanQRCodeScreenEvent.ProfileScanned(result.username))
                    }

                    is QRCodeScanResult.Video -> {
                        _eventChannel.send(ScanQRCodeScreenEvent.VideoScanned(result.videoId))
                    }
                }
                isProcessingQRData = false
            }
        }
    }

    fun changeScanBy(scanBy: ScanBy) {
        viewModelScope.launch {
            _uiState.update { it.copy(scanBy = scanBy) }
        }
    }
}

sealed class QRCodeScanResult {
    data class Profile(val username: String) : QRCodeScanResult()
    data class Video(val videoId: String) : QRCodeScanResult()
    data class Unrecognized(val rawData: String) : QRCodeScanResult()
    data object Failure : QRCodeScanResult()
}

sealed class ScanQRCodeScreenEvent {
    data class ProfileScanned(val username: String) : ScanQRCodeScreenEvent()
    data class VideoScanned(val videoId: String) : ScanQRCodeScreenEvent()
    data class UnrecognizedDataScanned(val rawData: String) : ScanQRCodeScreenEvent()
    data class ScanFailed(val message: String) : ScanQRCodeScreenEvent()
}

enum class AllowedQRCode(val path: String) {
    PROFILE("profile"),
    VIDEO("video")
}

enum class ScanBy {
    CAMERA_DIRECTLY,
    IMAGE_FROM_DEVICE,
}

data class QRCodeScannerScreenUiState(
    val isLoading: Boolean = false,
    val scanBy: ScanBy = ScanBy.CAMERA_DIRECTLY,
)
