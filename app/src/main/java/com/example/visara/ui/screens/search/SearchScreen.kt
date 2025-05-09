package com.example.visara.ui.screens.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.R
import com.example.visara.ui.components.VideoItem
import com.example.visara.ui.screens.search.components.SearchBar
import com.example.visara.ui.screens.search.components.UserItem
import com.example.visara.ui.theme.LocalVisaraCustomColors
import com.example.visara.viewmodels.SearchType
import com.example.visara.viewmodels.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    goBack: () -> Unit,
    onViewUserProfile: (username: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var pattern by remember(uiState.pattern) { mutableStateOf(uiState.pattern) }
    val lazyListState = rememberLazyListState()
    var selectedType by remember { mutableStateOf(uiState.searchType) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
        ) {
            // Header + tab select
            stickyHeader {
                Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                    // Header
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = goBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "GoBack"
                            )
                        }
                        SearchBar(
                            text = pattern,
                            onTextChange = { newQuery -> pattern = newQuery },
                            onSubmit = {
                                if (selectedType == SearchType.TITLE) {
                                    viewModel.searchVideoByTitle(pattern)
                                } else if (selectedType == SearchType.HASHTAG) {
                                    viewModel.searchVideoByHashtag(pattern)

                                } else if (selectedType == SearchType.USER) {
                                    viewModel.searchUser(pattern)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {},
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.mic_24px),
                                contentDescription = "Mic icon",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    // tab select
                    if (uiState.hasSearched) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 7.dp)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    selectedType = SearchType.TITLE
                                    viewModel.searchVideoByTitle(pattern)
                                },
                                label = { Text("Videos") },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (selectedType == SearchType.TITLE) LocalVisaraCustomColors.current.selectedChipContainerColor
                                    else LocalVisaraCustomColors.current.unselectedChipContainerColor,
                                    labelColor = if (selectedType == SearchType.TITLE) LocalVisaraCustomColors.current.selectedChipContentColor
                                    else LocalVisaraCustomColors.current.unselectedChipContentColor
                                )
                            )
                            SuggestionChip(
                                onClick = {
                                    selectedType = SearchType.USER
                                    viewModel.searchUser(pattern)
                                },
                                label = { Text("Users") },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (selectedType == SearchType.USER) LocalVisaraCustomColors.current.selectedChipContainerColor
                                    else LocalVisaraCustomColors.current.unselectedChipContainerColor,
                                    labelColor = if (selectedType == SearchType.USER) LocalVisaraCustomColors.current.selectedChipContentColor
                                    else LocalVisaraCustomColors.current.unselectedChipContentColor
                                )
                            )
                        }
                    }
                }
            }

            when (selectedType) {
                SearchType.TITLE, SearchType.HASHTAG -> {
                    items(uiState.videos.size) { index->
                        VideoItem(
                            state = uiState.videos[index],
                            onVideoSelect = { viewModel.selectVideo(it) },
                            onAuthorSelected = { onViewUserProfile(it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                SearchType.USER -> {
                    items(uiState.users.size) {  index ->
                        UserItem(
                            user = uiState.users[index],
                            onViewProfile = { onViewUserProfile(uiState.users[index].username) },
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color = Color.LightGray)
                        )
                    }
                }
            }
        }
    }
}
