package com.example.visara.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.zxing.*
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.EnumMap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class QRCodeViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow(QRCodeScreenUiState())
    val uiState = _uiState.asStateFlow()
    fun generateQRCode(username: String) {
        viewModelScope.launch {
            val bitmap = generateQrCodeBitmap("http://com.example.visara/profile/${username}")
            _uiState.update { it.copy(bitmap = bitmap) }
        }
    }

    suspend fun generateQrCodeBitmap(
        data: String,
        sizePx: Int = 1024,
        errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.H
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8" // Đảm bảo hỗ trợ các ký tự Unicode
            hints[EncodeHintType.ERROR_CORRECTION] = errorCorrectionLevel
            hints[EncodeHintType.MARGIN] = 1 // Giảm lề trắng xung quanh mã QR (thường là 4 mặc định)

            // Sử dụng MultiFormatWriter để mã hóa dữ liệu thành BitMatrix
            // MultiFormatWriter có thể mã hóa nhiều loại mã vạch/QR khác nhau
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                data,
                BarcodeFormat.QR_CODE,
                sizePx,
                sizePx,
                hints
            )

            // Chuyển đổi BitMatrix thành Bitmap
            val bitmap = createBitmap(sizePx, sizePx)
            for (x in 0 until sizePx) {
                for (y in 0 until sizePx) {
                    // bitMatrix.get(x, y) trả về true nếu ô màu đen, false nếu màu trắng
                    bitmap[x, y] = if (bitMatrix.get(x, y)) {
                        android.graphics.Color.BLACK
                    } else {
                        android.graphics.Color.WHITE
                    }
                }
            }
            return@withContext bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    fun clear() {
        _uiState.update { it.copy(bitmap = null) }
    }
}

data class QRCodeScreenUiState(
    val bitmap: Bitmap? = null,
)
