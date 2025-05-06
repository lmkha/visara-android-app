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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.visara.R

@Composable
fun TabPlaylistItem(
    modifier: Modifier = Modifier,
    title: String,
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
    ) {
        val cloudinaryImageUrl1 = "http://res.cloudinary.com/drnufn5sf/image/upload/v1743006316/videoplatform/thumbnail/67e42a68bb79412ece6f6399.jpg"
//        val cloudinaryImageUrl2 = "http://res.cloudinary.com/drnufn5sf/image/upload/v1743007784/videoplatform/thumbnail/67e42e7fbb79412ece6f639b.jpg"
        val title = "Nhạc Remix Căng Cực Cuốn Bay TikTok 2025"
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(190.dp)
                        .align(Alignment.TopCenter)
                ) {
                    AsyncImage(
                        model = cloudinaryImageUrl1,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .blur(40.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth()
                        .shadow(8.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    AsyncImage(
                        model = cloudinaryImageUrl1,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(
                                end = 8.dp,
                                bottom = 8.dp,
                            )
                            .clip(RoundedCornerShape(4.dp))
                            .background(color = Color.Black.copy(alpha = 0.7f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.playlist_play_24px),
                                contentDescription = null,
                                tint = Color.White,
                            )
                            Text(
                                text = "56",
                                color = Color.White,
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
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
                    text = "H2O Remix - Danh sách phát",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
                Text(
                    text = "Cập nhật hôm nay",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}

