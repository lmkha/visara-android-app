package com.example.visara.ui.screens.edit_video.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.visara.R
import com.example.visara.ui.theme.LocalVisaraCustomColors
import coil3.compose.AsyncImage
import com.example.visara.ui.components.VideoThumbnailFromVideoUri

@Composable
fun EnterVideoInfoStep(
    modifier: Modifier = Modifier,
    videoUri: Uri? = null,
    onBack: () -> Unit,
    onSubmit: (
        title: String,
        description: String,
        hashtags: List<String>,
        privacy: PrivacyState,
        isAllowComment: Boolean,
        thumbnailUri: Uri?,
    ) -> Unit,
) {
    var thumbnailUri by remember { mutableStateOf<Uri?>(null) }
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri->
        uri?.let { thumbnailUri = uri }
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hashTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var privacy by remember { mutableStateOf(PrivacyState.ALL) }
    var isAllowComment by remember { mutableStateOf(true) }
    var selectedPlaylists by remember { mutableStateOf<List<String>>(emptyList()) }

    var openAddDescriptionBox by remember { mutableStateOf(false) }
    var openSelectPrivacyBox by remember { mutableStateOf(false) }
    var openAddPlaylistBox by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    BackHandler(enabled = openSelectPrivacyBox) {
        openSelectPrivacyBox = false
    }
    BackHandler(enabled = openAddPlaylistBox) {
        openAddPlaylistBox = false
    }
    BackHandler(enabled = openAddDescriptionBox) {
        openAddDescriptionBox = false
    }

    Box(modifier = modifier) {
        // Base layer
        Column(modifier = Modifier.zIndex(0f).padding(bottom = 8.dp)) {
            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.TopStart),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            ) {
                // thumbnail
                Box {
                    Box(
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = Color.Black),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (thumbnailUri != null) {
                            AsyncImage(
                                model = thumbnailUri,
                                contentDescription = null,
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                            )
                        } else {
                            VideoThumbnailFromVideoUri(
                                uri = videoUri,
                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                    IconButton(
                        onClick = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.Black,
                        ),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .border(
                                width = 3.dp,
                                color = LocalVisaraCustomColors.current.border,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                }

                // Title
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            text = "Add title for video",
                            color = Color.Gray,
                        )
                    },
                    maxLines = 4,
                    minLines = 4,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.background,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 3.dp,
                            color = LocalVisaraCustomColors.current.border,
                            shape = RoundedCornerShape(8.dp)
                        )
                    ,
                )

                // Description and hashtags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = LocalVisaraCustomColors.current.border,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp)
                        .clickable { openAddDescriptionBox = true }
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                    )
                    Text(
                        text = "Add description and hashtags",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }

                // Privacy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = LocalVisaraCustomColors.current.border,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp)
                        .clickable { openSelectPrivacyBox = true }
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = privacy.iconId),
                        contentDescription = null,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Who can see this video",
                        )
                        Text(
                            text = privacy.label,
                            color = Color.Gray,
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }

                // Allow comments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = LocalVisaraCustomColors.current.border,
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

                // Add to playlist
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = LocalVisaraCustomColors.current.border,
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
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                        ,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Add video to playlist",
                        )
                        if (selectedPlaylists.isNotEmpty()) {
                            Text(
                                text = "${selectedPlaylists.size} selected",
                                color = Color.Gray
                                ,
                            )
                        }
                    }
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
                    onClick = {
                        if (!isProcessing) {
                            isProcessing = true
                            onSubmit(
                                title,
                                description,
                                hashTags,
                                privacy,
                                isAllowComment,
                                thumbnailUri
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isProcessing) MaterialTheme.colorScheme.primary
                        else Color.LightGray,
                        contentColor = if (!isProcessing) MaterialTheme.colorScheme.onPrimary
                        else Color.Black
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                ) {
                    if (!isProcessing) {
                        Icon(
                            painter = painterResource(id = R.drawable.publish_24px),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Post")
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color.Black,
                        )
                    }
                }
            }
        }

        // Add description and hashtags
        AnimatedVisibility(
            visible = openAddDescriptionBox,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
        ) {
            AddDescriptionBox(
                onBack = { openAddDescriptionBox = false },
                modifier = Modifier.fillMaxSize(),
                initialDescription = description,
                initialAddedHashTags = hashTags,
                onSubmit = { newDescription, newHashTags ->
                    description = newDescription
                    hashTags = newHashTags
                    openAddDescriptionBox = false
                }
            )
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
                currentPrivacy = privacy,
                onBack = { openSelectPrivacyBox = false },
                onSelected = {
                    privacy = it
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
                currentSelectedPlaylists = selectedPlaylists,
                onSelectFinished = {
                    selectedPlaylists = it
                    openAddPlaylistBox = false
                }
            )
        }
    }
}
