package com.example.visara.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun VideoItem(
    modifier: Modifier = Modifier,
    onVideoSelect: (videoId: String) -> Unit = {},
) {
    val cloudinaryImageUrl = "http://res.cloudinary.com/drnufn5sf/image/upload/v1743006773/videoplatform/thumbnail/67e42c30bb79412ece6f639a.jpg"
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = {onVideoSelect("best-video-id")})
    ) {
        Column {
            AsyncImage(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                ,
                model = cloudinaryImageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
            )
            Row(
                modifier = Modifier
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                UserAvatar(modifier = Modifier.size(50.dp))
                Column {
                    Row {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                            ,
                            text = "FC Barcelona 1 vs 1 Betis | Laliga 2024/25 MD30",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            fontWeight = FontWeight.W600,
                            fontSize = 20.sp
                        )
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "FC Barcelona"
                        )
                        Text("-")
                        Text(
                            text = "601K views"
                        )
                        Text("-")
                        Text(
                            text = "10 hours ago"
                        )
                    }
                }
            }
        }
    }
}

/*
    val videoUrl = "http://10.0.2.2:8080/67d93e93ca386d2312a19f5c/output.mpd"
    VideoPlayerDash(
        modifier = Modifier.fillMaxWidth(),
        url = videoUrl,
    )
 */

