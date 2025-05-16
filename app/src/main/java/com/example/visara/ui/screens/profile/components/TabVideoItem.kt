package com.example.visara.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.utils.formatDuration
import com.example.visara.ui.utils.toTimeAgo

@Composable
fun TabVideoItem(
    modifier: Modifier = Modifier,
    video: VideoModel? = null,
    onVideoSelected: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
            .clickable { onVideoSelected() }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = video?.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            bottom = 8.dp,
                            end = 8.dp,
                        )
                        .width(60.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.Black)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatDuration(video?.duration ?: 0L),
                        color = Color.White,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = video?.title ?: "Title",
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clickable {

                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(4.dp).align(Alignment.TopEnd)
                        )
                    }
                }
                Text(
                    text = video?.createdAt?.toTimeAgo() ?: "created time",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}

