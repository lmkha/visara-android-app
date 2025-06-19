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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.visara.R
import com.example.visara.data.model.PlaylistModel

@Composable
fun TabPlaylistItem(
    modifier: Modifier = Modifier,
    playlist: PlaylistModel,
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
            ) {
                if (playlist.thumbnail.isNotBlank()) {
                    AsyncImage(
                        model = playlist.thumbnail,
                        contentDescription = null
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.playlist_tab_24px),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(2.dp).align(Alignment.BottomEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.playlist_play_24px),
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Text(
                        text = playlist.videoIds.size.toString(),
                        color = Color.White,
                    )
                }
            }

            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = playlist.name,
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
                    text = playlist.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}
