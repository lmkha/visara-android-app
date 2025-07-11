package com.example.visara.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeAnalyzer @Inject constructor() : ImageAnalysis.Analyzer {
    private val scanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    private var onQrCodeScanned: ((String?) -> Unit)? = null

    fun setOnQrCodeScannedListener(listener: (String?) -> Unit) {
        this.onQrCodeScanned = listener
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach { barcode ->
                    barcode.rawValue?.let { result ->
                        onQrCodeScanned?.invoke(result)
                    }
                }
            }
            .addOnFailureListener { e ->
                onQrCodeScanned?.invoke(null)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    suspend fun analyzeImageFromUri(context: Context, uri: Uri) : String? {
        try {
            val image = InputImage.fromFilePath(context, uri)
            return scanner.process(image).await().firstOrNull()?.rawValue
        } catch (e: Exception) {
            Log.d("CHECK_VAR", "error: ${e.toString()}")
            return null
        }
    }
}
