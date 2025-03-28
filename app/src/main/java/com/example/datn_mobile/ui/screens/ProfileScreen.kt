package com.example.datn_mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.datn_mobile.ui.components.CloudinaryImage


@Composable
fun ProfileScreen(username:String, isMyProfile: Boolean = false) {
    ProfileScreenContent(username, isMyProfile)
}

@Composable
internal fun ProfileScreenContent(username: String, isMyProfile: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Username: ${if(isMyProfile) "lmkha" else "Other username"}")
        Text("email: ${if (isMyProfile) "lmkha0201@gmail.com" else "example@gmail.com"}")
        CloudinaryImage()
    }
}
