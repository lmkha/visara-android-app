package com.example.visara.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    avatarLink: String? = null,
) {
    if (avatarLink != null) {
        AsyncImage(
            model = avatarLink,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(CircleShape)
                .background(Color.Black)
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = modifier,
        )
    }
}
