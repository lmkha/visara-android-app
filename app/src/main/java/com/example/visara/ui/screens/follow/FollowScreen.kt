package com.example.visara.ui.screens.follow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.data.model.FollowUserModel
import com.example.visara.data.model.UserModel
import com.example.visara.ui.components.UserAvatar
import com.example.visara.viewmodels.FollowScreenViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FollowScreen(
    modifier: Modifier = Modifier,
    viewModel: FollowScreenViewModel,
    goBack: () -> Unit,
    navigateToProfileScreen: (username: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var columnState = rememberLazyListState()
    var selectedTabIndex by remember(uiState.startedTabIndex) { mutableIntStateOf(uiState.startedTabIndex) }
    var pattern by remember { mutableStateOf("") }

    LazyColumn(
        state = columnState,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // header, tabs selector
        stickyHeader {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                    Text(
                        text = uiState.currentUser?.username ?: "username",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    }
                }

                // Tab selector
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = {
                            pattern = ""
                            selectedTabIndex = 0
                            viewModel.fetchAllFollowings()
                        },
                        text = {
                            Text(
                                text = "Following",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            pattern = ""
                            selectedTabIndex = 1
                            viewModel.fetchAllFollowers()
                        },
                        text = {
                            Text(
                                text = "Followers",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                    )
                }
            }
        }

        // Search text field
        item {
            TextField(
                value = pattern,
                onValueChange = {
                    pattern = it
                    if (selectedTabIndex == 0) viewModel.filterFollowings(it)
                    else if (selectedTabIndex == 1) viewModel.filterFollowers(it)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                    )
                },
                placeholder = { Text("Search") },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.LightGray,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        }

        // List of users
        val count = if (selectedTabIndex == 0) {
            if (pattern.trim().isBlank()) {
                uiState.followings.size
            } else {
                uiState.filteredFollowings.size
            }
        } else if (selectedTabIndex == 1) {
            if (pattern.trim().isBlank()) {
                uiState.followers.size
            } else {
                uiState.filteredFollowers.size
            }
        } else {
            0
        }
        items(count) { index ->
            val user = if (selectedTabIndex == 0) {
                if (pattern.trim().isBlank()) {
                    uiState.followings[index]
                } else {
                    uiState.filteredFollowings[index]
                }
            } else if (selectedTabIndex == 1) {
                if (pattern.trim().isBlank()) {
                    uiState.followers[index]
                } else {
                    uiState.filteredFollowers[index]
                }
            } else {
                FollowUserModel(user = UserModel())
            }
            FollowUserItem(
                user = user.user,
                isFollowingInitialValue = user.isFollowing,
                follow = { onFailure: () -> Unit ->
                    viewModel.follow(user = user) { onFailure() }
                },
                unfollow = { onFailure: () -> Unit ->
                    viewModel.unfollow(user = user) { onFailure() }
                },
                modifier = Modifier
                    .height(70.dp)
                    .padding(horizontal = 4.dp)
                    .clickable { navigateToProfileScreen(user.user.username) }
            )
        }
    }
}

@Composable
private fun FollowUserItem(
    modifier: Modifier = Modifier,
    isFollowingInitialValue: Boolean = false,
    user: UserModel,
    follow: (onFailure: () -> Unit) -> Unit = {},
    unfollow: (onFailure: () -> Unit) -> Unit = {},
) {
    var isFollowing by remember(isFollowingInitialValue) { mutableStateOf(isFollowingInitialValue) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UserAvatar(
                avatarLink = user.networkAvatarUrl,
                modifier = Modifier.size(60.dp),
            )
            Column {
                Text(
                    text = user.fullName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.username,
                )
            }
        }

        if (isFollowing) {
            Button(
                onClick = {
                    isFollowing = false
                    unfollow { isFollowing = true }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Following",
                )

            }
        } else {
            Button(
                onClick = {
                    isFollowing = true
                    follow { isFollowing = false }
                }
            ) {
                Text(
                    text = "Follow",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
