package com.example.visara.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.visara.ui.components.VideoPlayerManager

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    authenticated: Boolean = false,
    navigateToLogin: () -> Unit = {}
) {
    Column(modifier = modifier.background(color = Color.Blue)) {

    }
}
