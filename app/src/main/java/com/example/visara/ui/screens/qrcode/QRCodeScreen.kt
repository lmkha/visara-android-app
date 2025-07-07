package com.example.visara.ui.screens.qrcode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun QRCodeScreen(
    modifier: Modifier = Modifier,
    onNavigateToGenerateQRCode: () -> Unit,
    onNavigateToScanQRCode: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            Button(
                onClick = onNavigateToGenerateQRCode,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black,
                )
            ) { Text("Generate") }

            Button(onClick = onNavigateToScanQRCode) { Text("Scan") }
        }
    }
}
