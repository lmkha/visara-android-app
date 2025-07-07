package com.example.visara.ui.screens.following_feed

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.R
import com.example.visara.ui.components.BottomNavBar
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.components.VideoItem
import com.example.visara.ui.Destination
import com.example.visara.viewmodels.FollowingFeedViewModel

// New video of user that you are following
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingFeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FollowingFeedViewModel = hiltViewModel(),
    onNavigateToProfileScreen: (authorUsername: String) -> Unit,
    onNavigateToFollowingScreen: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onBotNavigate: (Destination) -> Unit,
    currentAvatarUrl: String?,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                activeRoute = Destination.Main.FollowingFeed.route,
                currentUserAvatarUrl = currentAvatarUrl,
            ) {
                onBotNavigate(it)
            }
        },
    ) { innerPadding->
        if (!uiState.isAuthenticated && !uiState.isLoadingFollowings && !uiState.isLoadingVideos) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = stringResource(
                        id = R.string.request_login_message,
                        stringResource(R.string.see_videos_from_following),
                    ),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToLoginScreen,
                    modifier = Modifier
                        .height(50.dp)
                        .width(150.dp)
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        } else {
            PullToRefreshBox(
                isRefreshing = uiState.isLoadingVideos,
                onRefresh = { viewModel.refresh() },
                state = pullToRefreshState,
                indicator = {
                    Indicator(
                        isRefreshing = uiState.isLoadingVideos,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        color = MaterialTheme.colorScheme.primary,
                        state = pullToRefreshState,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 100.dp)
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 4.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .height(120.dp)
                                .fillMaxWidth()
                        ) {
                            LazyRow(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                            ) {
                                items(uiState.followings) { following ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.width(80.dp)
                                    ) {
                                        UserAvatar(
                                            avatarLink = following.networkAvatarUrl,
                                            modifier = Modifier.size(80.dp)
                                        )
                                        Text(
                                            text = following.username,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .height(80.dp)
                                    .width(50.dp)
                            ) {
                                TextButton(onClick = onNavigateToFollowingScreen) {
                                    Text("All")
                                }
                            }
                        }
                    }

                    items(uiState.videos) { video ->
                        VideoItem(
                            state = video,
                            onVideoSelect = {
                                viewModel.selectVideo(video)
                            },
                            onAuthorSelected = {
                                onNavigateToProfileScreen(video.username)
                            }
                        )
                    }
                }
            }
        }
    }
}
