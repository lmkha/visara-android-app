package com.example.visara.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LoginRequestDialog(
    state: LoginRequestDialogState,
    onLogin: () -> Unit,
) {
    if (state.display.value) {
        Dialog(onDismissRequest = { state.dismiss() }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(370.dp)
                    .height(180.dp)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Text(
                        text = state.message.value,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        onClick = {
                            state.dismiss()
                            onLogin()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .height(50.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Login")
                    }
                }
            }
        }
    }
}

class LoginRequestDialogState {
    var message = mutableStateOf("")
        private set
    var display = mutableStateOf(false)
        private set

    fun show(message: String) {
        this.message.value = message
        this.display.value = true
    }

    fun dismiss() {
        this.message.value = ""
        this.display.value = false
    }
}

@Composable
fun rememberLoginRequestDialogState() = remember { LoginRequestDialogState() }
