package com.example.datn_mobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun CloudinaryImage() {
    val cloudinaryImageUrl = "http://res.cloudinary.com/drnufn5sf/image/upload/v1743006773/videoplatform/thumbnail/67e42c30bb79412ece6f639a.jpg"

    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        model = cloudinaryImageUrl,
        contentDescription = ""
    )
}

