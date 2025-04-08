package com.example.visara.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.ui.components.VideoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onVideoSelect: (videoId: String) -> Unit = {},
    onOpenSearchOverlay: () -> Unit = {},
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
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Row {
                        Image(
                            painter = painterResource(R.drawable.app_logo),
                            contentDescription = "App logo",
                            modifier = Modifier.size(24.dp),
                        )
                        Text(text = stringResource(id = R.string.app_name).drop(1))
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSearchOverlay) {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                    }
                },
            )
        },
    ) { innerPadding->
        Column(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        ) {
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
