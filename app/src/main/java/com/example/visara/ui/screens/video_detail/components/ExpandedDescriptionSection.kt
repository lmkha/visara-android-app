package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visara.data.model.VideoModel
import com.example.visara.utils.getTimeAgo

@Composable
fun ExpandedDescriptionSection(
    video: VideoModel?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        video?.let {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = video.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                // Likes, views, time
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(70.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = video.likesCount.toString(),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Likes",
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = video.viewsCount.toString(),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Views",
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = getTimeAgo(video.createdAt),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                text = "Ago",
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }

                Text(
                    text = video.description,
                )
            }
        }
    }
}
