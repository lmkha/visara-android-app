package com.example.visara.ui.screens.following

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// New video of user that you are following
@Composable
fun FollowingScreen(
    modifier: Modifier = Modifier,
    displaySnackBar: (message: String) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = { displaySnackBar("You are offline, please check you network!")}
        ) {
            Text("Show snackBar!")
        }
    }
}
