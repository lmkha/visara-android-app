package com.example.visara.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.utils.formatDuration
import com.example.visara.ui.utils.formatViews
import com.example.visara.ui.utils.toTimeAgo

@Composable
fun VideoItem(
    modifier: Modifier = Modifier,
    state: VideoModel,
    videoHeight: Dp = 250.dp,
    onVideoSelect: (video: VideoModel) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = {onVideoSelect(state)})
    ) {
        Column {
            Box {
                AsyncImage(
                    model = state.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(videoHeight)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            bottom = 16.dp,
                            end = 16.dp,
                        )
                        .width(60.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.Black)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatDuration(state.duration),
                        color = Color.White,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(8.dp)
                ,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                UserAvatar(
                    avatarLink = state.userProfilePic,
                    modifier = Modifier.size(50.dp)
                )
                Column {
                    Row {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                            ,
                            text = state.title,
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
                            text = state.username.toString(),
                        )
                        Text("-")
                        Text(
                            text = formatViews(state.viewsCount)
                        )
                        Text("-")
                        Text(
                            text = state.createdAt.toTimeAgo(),
                        )
                    }
                }
            }
        }
    }
}
