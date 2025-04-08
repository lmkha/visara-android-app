package com.example.visara.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
) {
    val userImgLink = "http://res.cloudinary.com/drnufn5sf/image/upload/v1743007869/videoplatform/profile/152.jpg"
//    val barcelona = "http://res.cloudinary.com/drnufn5sf/image/upload/v1742437855/videoplatform/profile/102.png"

    AsyncImage(
        model = userImgLink,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Black)
    )
}
