package com.example.visara.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.ui.components.VideoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onVideoSelect: (videoId: String) -> Unit = {},
    onOpenSearchOverlay: () -> Unit = {},
    bottomNavBar: @Composable () -> Unit,
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
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            scope.launch {
                delay(1000)
                isRefreshing = false
            }
        }
    }

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
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
        bottomBar = { bottomNavBar() }
    ) { innerPadding->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            SuggestionTag(
                tags = suggestionTags,
                modifier = Modifier.wrapContentHeight()
            )

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    state = lazyColumnState,
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
