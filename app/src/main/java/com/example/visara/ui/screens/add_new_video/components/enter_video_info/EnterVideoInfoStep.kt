package com.example.visara.ui.screens.add_new_video.components.enter_video_info

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.visara.R

@Composable
fun EnterVideoInfoStep(
    modifier: Modifier = Modifier,
) {
    val thumbnail = "http://res.cloudinary.com/drnufn5sf/image/upload/v1743007784/videoplatform/thumbnail/67e42e7fbb79412ece6f639b.jpg"
    var isAllowComment by remember { mutableStateOf(true) }
    var openSelectPrivacyBox by remember { mutableStateOf(false) }
    var openAddPlaylistBox by remember { mutableStateOf(false) }
    var privacyState by remember { mutableStateOf(Privacy.ALL) }

    BackHandler(enabled = openSelectPrivacyBox) {
        openSelectPrivacyBox = false
    }

    BackHandler(enabled = openAddPlaylistBox) {
        openAddPlaylistBox = false
    }

    Box(modifier = modifier) {
        // Base layer
        Column(modifier = Modifier.zIndex(0f)) {
            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.TopStart),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            }

            // Body
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            ) {
                // thumbnail
                Box {
                    AsyncImage(
                        model = thumbnail,
                        contentDescription = null,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    )
                    IconButton(
                        onClick = {},
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.Black,
                        ),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                }

                // Title
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = {
                        Text(
                            text = "Add title for video",
                            color = Color.Gray,
                        )
                    },
                    maxLines = 3,
                    minLines = 3,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        unfocusedPlaceholderColor = Color.Black,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

                // Description
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                    )
                    Text(
                        text = "Add description for video",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
                Spacer(Modifier.height(8.dp))

                // Privacy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp)
                        .clickable { openSelectPrivacyBox = true }
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = privacyState.iconId),
                        contentDescription = null,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Who can see this video",
                            color = Color.Gray,
                        )
                        Text(
                            text = privacyState.label
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
                Spacer(Modifier.height(8.dp))

                // Allow comments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                    )
                    Text(
                        text = "Allow comment",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isAllowComment,
                        onCheckedChange = { isAllowComment = it },
                    )
                }
                Spacer(Modifier.height(8.dp))

                // Add to playlist
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp)
                        .clickable { openAddPlaylistBox = true }
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                    )
                    Text(
                        text = "Add video to playlist",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }

            // Main buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black,
                    ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.page_header_24px),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Draft")
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.publish_24px),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Post")
                }
            }
            HorizontalDivider(Modifier.padding(top = 8.dp))
        }

        // Select privacy Box
        AnimatedVisibility(
            visible = openSelectPrivacyBox,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
        ) {
            PrivacySelectBox(
                modifier = Modifier.fillMaxSize(),
                currentPrivacy = privacyState,
                onBack = { openSelectPrivacyBox = false },
                onSelected = {
                    privacyState = it
                    openSelectPrivacyBox = false
                }
            )
        }

        // Add video to playlists
        AnimatedVisibility(
            visible = openAddPlaylistBox,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
        ) {
            AddVideoToPlaylistsBox(
                modifier = Modifier.fillMaxSize(),
                onBack = { openAddPlaylistBox = false },
                onSelected = {
                    openAddPlaylistBox = false
                }
            )
        }
    }
}
