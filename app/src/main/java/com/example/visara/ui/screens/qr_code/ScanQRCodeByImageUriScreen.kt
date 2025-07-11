package com.example.visara.ui.screens.qr_code

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage

@Composable
fun ScanQRCodeByImageUriScreen(
    modifier: Modifier = Modifier,
    onScan: (qrCodeImageUri: Uri) -> Unit,
) {
    val context = LocalContext.current
    var qrImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                qrImageUri = it
                onScan(it)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            AsyncImage(
                model = qrImageUri,
                contentDescription = "QR Code from library",
                modifier = Modifier.fillMaxSize()
            )
        }
        Button(onClick = {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }) { Text("Select picture") }
    }
}
