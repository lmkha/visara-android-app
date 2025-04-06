package com.example.visara.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.ui.components.VideoItem

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onVideoSelect: (videoId: String) -> Unit = {},
    bottomPadding: Dp = 0.dp,
) {
    val suggestionTags = listOf(
        "football",
        "barca",
        "champion league",
        "music",
        "hiphop",
        "rap",
        "information technology"
    )
    val lazyColumnState = rememberLazyListState()

    Column(modifier = modifier) {
        SuggestionTag(
            tags = suggestionTags,
            modifier = Modifier.wrapContentHeight()
        )

        LazyColumn(
            state = lazyColumnState,
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(3) {
                VideoItem(
                    onVideoSelect = { videoId -> onVideoSelect(videoId) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(bottomPadding))
            }
        }
    }
}

@Composable
fun SuggestionTag(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            AssistChip(
                onClick = {},
                label = { Text("Explore") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.explore_24px),
                        contentDescription = "Trending"
                    )
                },
            )
        }
        item {
            SuggestionChip(
                onClick = {},
                label = { Text("All") }
            )
        }
        items(tags.size) {index->
            SuggestionChip(
                onClick = {},
                label = { Text(tags[index]) }
            )
        }
    }
}
