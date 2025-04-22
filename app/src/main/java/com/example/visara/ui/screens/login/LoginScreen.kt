package com.example.visara.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onAuthenticated: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLogged) {
        if (uiState.isLogged) {
            onAuthenticated()
        }
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        TextField(
            label = { Text("UserName") },
            value = username,
            onValueChange = { username = it }
        )

        TextField(
            label = { Text("Password") },
            value = password,
            onValueChange = { password = it }
        )

        Button(
            onClick = { viewModel.login(username = username, password = password) },
        ) {
            Text("Login")
        }
    }
}
