package com.example.datn_mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datn_mobile.viewmodels.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (uiState.authenticated) {
            Text("Access token")
            Text(uiState.accessToken)
            Button(onClick = { viewModel.logout()} ) {
                Text("Logout")
            }
        } else {
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
}
