package com.example.visara.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.repository.UserRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.EnumMap
import javax.inject.Inject

@HiltViewModel
class MyQRCodeScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyQRCodeScreenUiState())
    val uiState = _uiState.asStateFlow()
    private val barcodeEncoder by lazy { BarcodeEncoder() }

    init {
        observerCurrentUser()
    }

    private suspend fun generateQrCodeBitmap(
        data: String,
        sizePx: Int = 1024,
        errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.H
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel)
                put(EncodeHintType.MARGIN, 1)
            }

            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                data,
                BarcodeFormat.QR_CODE,
                sizePx,
                sizePx,
                hints
            )

            return@withContext barcodeEncoder.createBitmap(bitMatrix)

        } catch (e: WriterException) {
            e.printStackTrace()
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun observerCurrentUser() {
        viewModelScope.launch {
            userRepository.currentUser.collectLatest { currentUser->
                if (currentUser?.username != null) {
                    val newQrCodeBitmap = generateQrCodeBitmap("https://visara.com/profile/${currentUser.username}")
                    _uiState.update {
                        it.copy(
                            qrCodeBitmap = newQrCodeBitmap,
                            username = currentUser.username,
                            userAvatarUrl = currentUser.networkAvatarUrl,
                        )
                    }
                } else {
                    _uiState.update { MyQRCodeScreenUiState() }
                }
            }
        }
    }
}

data class MyQRCodeScreenUiState(
    val qrCodeBitmap: Bitmap? = null,
    val username: String? = null,
    val userAvatarUrl: String? = null,
)
