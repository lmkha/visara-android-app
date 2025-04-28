package com.example.visara.ui.screens.add_new_video.components.enter_video_info

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddDescriptionBox(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    initialDescription: String = "",
    initialAddedHashTags: List<String> = emptyList(),
    onSubmit: (description: String, addedHashtags: List<String>) -> Unit,
) {
    var description by remember { mutableStateOf(initialDescription) }
    var addedHashtags by remember { mutableStateOf<List<String>>(initialAddedHashTags) }
    var newHashtag by remember { mutableStateOf("") }
    val allHashtags = listOf<String>(
        "music",
        "trending",
        "tiktok",
        "playoff",
        "football",
        "barca",
        "champion league",
        "android",
        "kotlin",
        "jetpack compose",
        "information technology"
    )
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()
    var filteredHashtagResults by remember { mutableStateOf(emptyList<String>()) }
    val hashtagRowListState = rememberLazyListState()

    LaunchedEffect(newHashtag) {
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(500)
            filteredHashtagResults = if (newHashtag.isBlank()) emptyList()
            else allHashtags.filter { it.contains(other = newHashtag, ignoreCase = true) }
        }
    }

    LaunchedEffect(addedHashtags.size) {
        if (addedHashtags.isNotEmpty()) {
            scope.launch {
                delay(100)
                hashtagRowListState.animateScrollToItem(addedHashtags.lastIndex)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onBack,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
                Text(
                    text = "Add description and hashtags",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )

                Button(
                    onClick = { onSubmit(description, addedHashtags) },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                ) {
                    Text("OK")
                }
            }

            // Description
            TextField(
                label = { Text("Description") },
                value = description,
                onValueChange = { description = it },
                minLines = 5,
                maxLines = 5,
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
            )

            // Hashtag area
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Hashtags",
                    fontWeight = FontWeight.Medium,
                )

                // Added hashtags
                LazyRow(
                    state = hashtagRowListState,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(addedHashtags.size) { index ->
                        val hashtag = addedHashtags[index]
                        val displayText =
                            "#" + if (hashtag.length > 15) hashtag.take(15) + "..." else hashtag
                        Row(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = Color.DarkGray)
                        ) {
                            Text(
                                text = displayText,
                                modifier = Modifier.padding(4.dp)
                            )

                            // Remove button
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(30.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topEnd = 8.dp,
                                            bottomEnd = 8.dp
                                        )
                                    )
                                    .background(color = Color.LightGray)
                                    .clickable {
                                        addedHashtags = addedHashtags - hashtag
                                    }
                                ,
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                TextField(
                    value = newHashtag,
                    placeholder = { Text("Add or search for hashtag") },
                    onValueChange = { newHashtag = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    trailingIcon = {
                        // Add new hashtag button
                        if (newHashtag.isNotEmpty() && !addedHashtags.contains(newHashtag)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(60.dp)
                                    .background(color = MaterialTheme.colorScheme.onSurface)
                                    .clickable {
                                        addedHashtags = addedHashtags + newHashtag
                                        newHashtag = ""
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Add",
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.background
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 24.dp)
                        .clip(RoundedCornerShape(8.dp))
                    ,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredHashtagResults.size) { index ->
                        val hashtagResultItem = filteredHashtagResults[index]
                        Row(
                            modifier = Modifier
                                .height(50.dp)
                                .background(color = MaterialTheme.colorScheme.surface)
                                .fillMaxWidth()
                                .clickable {
                                    if (hashtagResultItem.isNotEmpty() && !addedHashtags.contains(hashtagResultItem)) {
                                        addedHashtags = addedHashtags + hashtagResultItem
                                    }
                                }
                            ,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                ,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = "#$hashtagResultItem")
                                Text("17.8K videos")
                            }
                        }
                    }
                }
            }
        }
    }
}
