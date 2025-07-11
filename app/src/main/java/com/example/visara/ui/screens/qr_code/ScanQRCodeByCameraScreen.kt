package com.example.visara.ui.screens.qr_code

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.visara.R
import com.example.visara.utils.BarcodeAnalyzer

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQRCodeByCameraScreen(
    modifier: Modifier = Modifier,
    barcodeAnalyzer: BarcodeAnalyzer,
    onChangeScanBy: () -> Unit,
) {
    val context = LocalContext.current
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Camera permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = modifier) {
        QRCodeCameraPreview(
            modifier = Modifier.fillMaxSize(),
            barcodeAnalyzer = barcodeAnalyzer
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 90.dp)
                .align(Alignment.BottomCenter)
        ) {
            FloatingButton(
                modifier = Modifier.size(60.dp),
                label = "My QR",
                contentDescription = "Show my qr code button",
                painter = painterResource(R.drawable.qr_code_24px),
                onClick = {

                }
            )

            FloatingButton(
                label = "From library",
                contentDescription = "Scan by image button",
                painter = painterResource(R.drawable.photo_library_24px),
                modifier = Modifier.size(60.dp),
                onClick = onChangeScanBy,
            )
        }
    }
}

@Composable
private fun QRCodeCameraPreview(
    modifier: Modifier = Modifier,
    barcodeAnalyzer: BarcodeAnalyzer,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { previewView ->
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(context), barcodeAnalyzer)
                    }

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@Composable
private fun FloatingButton(
    modifier: Modifier = Modifier,
    label: String,
    painter: Painter,
    onClick: () -> Unit,
    contentDescription: String? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(
            modifier = modifier,
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.3f)
            ),
        ) {
            Box(
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(
                    painter = painter,
                    contentDescription = contentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(90.dp)
                )
            }
        }
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
