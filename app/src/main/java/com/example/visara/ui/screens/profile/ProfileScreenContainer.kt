package com.example.visara.ui.screens.profile

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.visara.R
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.screens.profile.components.ActionButton
import com.example.visara.ui.screens.profile.components.BottomSheet
import com.example.visara.ui.screens.profile.components.MetricItem
import com.example.visara.ui.screens.profile.components.SheetResult
import com.example.visara.ui.screens.profile.components.SheetType
import com.example.visara.ui.screens.profile.components.TabPlaylistItem
import com.example.visara.ui.screens.profile.components.TabVideoItem
import com.example.visara.ui.theme.LocalVisaraCustomColors
import com.example.visara.viewmodels.ProfileScreenUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileScreenContainer(
    modifier: Modifier = Modifier,
    uiState: ProfileScreenUiState,
    bottomNavBar: @Composable () -> Unit,
    onBack: () -> Unit,
    follow: () -> Unit,
    unfollow: () -> Unit,
    onNavigateToFollowScreen: (startedTabIndex: Int) -> Unit,
    onNavigateToSettingsScreen: () -> Unit,
    onNavigateToStudioScreen: () -> Unit,
    onNavigateToQRCodeScreen: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onVideoSelected: (video: VideoModel) -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var displayBottomSheet by remember { mutableStateOf(false) }
    var mainScrollState = rememberLazyListState()
    var videoScrollState = rememberLazyListState()
    var playlistScrollState = rememberLazyListState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val showTitle by remember {
        derivedStateOf {
            mainScrollState.firstVisibleItemIndex > 0
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val insideScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val isScrollingUp = available.y < 0
                val canScrollOutsideUp = mainScrollState.firstVisibleItemIndex < 4

                if (isScrollingUp && canScrollOutsideUp) {
                    coroutineScope.launch {
                        mainScrollState.scrollBy(-available.y)
                    }
                    return Offset(0f, available.y)
                }

                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return Velocity.Zero
            }
        }
    }
    var bottomSheetType by remember { mutableStateOf<SheetType>(SheetType.SETTINGS) }

    BackHandler(enabled = displayBottomSheet) {
        displayBottomSheet = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.zIndex(0f),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (showTitle) {
                            Text(
                                text = uiState.user?.username ?: "username",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        if (uiState.isMyProfile) {
                            Row(modifier = Modifier.padding(start = 8.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.app_logo),
                                    contentDescription = "App logo",
                                    modifier = Modifier.size(24.dp),
                                )
                                Text(
                                    text = stringResource(id = R.string.app_name).drop(1),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        } else {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                    actions = {
                        Row {
                            IconButton(onClick = {
                                bottomSheetType = SheetType.SETTINGS
                                displayBottomSheet = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                    expandedHeight = 50.dp,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                )
            },
            bottomBar = {
                if (uiState.isMyProfile) {
                    bottomNavBar()
                }
            }
        ) { innerPadding ->
            LazyColumn(
                state = mainScrollState,
                modifier = Modifier.padding(innerPadding)
            ) {
                // Avatar, name and username(0)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Box(modifier = Modifier.size(120.dp)) {
                            UserAvatar(
                                avatarLink = uiState.user?.networkAvatarUrl,
                                modifier = Modifier.size(120.dp)
                            )
                            if (uiState.isMyProfile) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .border(
                                            width = 5.dp,
                                            color = MaterialTheme.colorScheme.background,
                                            shape = CircleShape
                                        )
                                        .background(color = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    ) {
                        Text(
                            text = uiState.user?.username ?: "username",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = uiState.user?.fullName ?: "Full Name",
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                        )
                    }
                }

                // Following, follower, like(1)
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        MetricItem(
                            label = "Following",
                            count = uiState.user?.followingCount?.toString() ?: "0",
                            modifier = Modifier
                                .width(100.dp)
                                .clickable {
                                    if (uiState.isMyProfile) {
                                        onNavigateToFollowScreen(0)
                                    }
                                }
                        )
                        MetricItem(
                            label = "Follower",
                            count = uiState.user?.followerCount?.toString() ?: "0",
                            modifier = Modifier
                                .width(100.dp)
                                .clickable {
                                    if (uiState.isMyProfile) {
                                        onNavigateToFollowScreen(1)
                                    }
                                }
                        )
                        MetricItem(
                            label = "Like",
                            count = "0",
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }

                /**
                 * Actions(2)
                 *  MyProfile: Edit profile, share profile, open studio
                 *  Not followed: Follow, Send message
                 *  Following: Send message, unfollow(wrap in a icon button)
                 */
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (uiState.isMyProfile) {
                                ActionButton(onClick = {}) {
                                    Text(
                                        text = "Edit profile",
                                        modifier = Modifier.padding(8.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = LocalVisaraCustomColors.current.profileActionButtonContentColor,
                                    )
                                }
                                ActionButton(onClick = {}) {
                                    Text(
                                        text = "Share profile",
                                        modifier = Modifier.padding(8.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = LocalVisaraCustomColors.current.profileActionButtonContentColor,
                                    )
                                }
                                ActionButton(onClick = {}) {
                                    Icon(
                                        painter = painterResource(R.drawable.studio_24px),
                                        contentDescription = null,
                                        modifier = Modifier.padding(8.dp),
                                        tint = LocalVisaraCustomColors.current.profileActionButtonContentColor,
                                    )
                                }
                            } else {
                                ActionButton(onClick = {}) {
                                    Text(
                                        text = "Send message",
                                        modifier = Modifier.padding(8.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = LocalVisaraCustomColors.current.profileActionButtonContentColor,
                                    )
                                }
                                if (uiState.isFollowing) {
                                    ActionButton(
                                        onClick = {
                                            bottomSheetType = SheetType.UNFOLLOW_CONFIRM
                                            displayBottomSheet = true
                                        },
                                        modifier = Modifier
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = "Followed",
                                                modifier = Modifier.padding(8.dp),
                                                fontWeight = FontWeight.Bold,
                                                color = LocalVisaraCustomColors.current.profileActionButtonContentColor,
                                            )

                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                } else {
                                    ActionButton(
                                        isPrimary = true,
                                        onClick = {
                                            if (uiState.isAuthenticated) {
                                                follow()
                                            } else {
                                                bottomSheetType = SheetType.LOGIN_REQUEST
                                                displayBottomSheet = true
                                            }
                                        },
                                        modifier = Modifier
                                    ) {
                                        Text(
                                            text = "Follow",
                                            modifier = Modifier.padding(8.dp),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bio(3)
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "...checkmate information technology",
                            maxLines = 1,
                            modifier = Modifier.width(300.dp),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Tab header(4)
                stickyHeader {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = {
                                Text(
                                    text = "Video",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.video_tab_24px),
                                    contentDescription = null
                                )
                            }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = {
                                Text(
                                    text = "Playlist",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.playlist_tab_24px),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }

                // Tabs content(5)
                item {
                    if (selectedTabIndex == 0) {
                        LazyColumn(
                            state = videoScrollState,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .height(screenHeight)
                                .nestedScroll(insideScrollConnection)
                        ) {
                            item {
                                var selectedFilterIndex by remember { mutableIntStateOf(0) }
                                @Composable
                                fun CustomFilterChip(
                                    label: String,
                                    onClick: () -> Unit,
                                    selected: Boolean,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color = if (selected) Color.White else Color.DarkGray)
                                            .clickable(onClick = onClick)
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (selected) Color.Black else Color.White,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CustomFilterChip(
                                        label = "Newest",
                                        selected = selectedFilterIndex == 0,
                                        onClick = { selectedFilterIndex = 0 }
                                    )
                                    CustomFilterChip(
                                        label = "Popular",
                                        selected = selectedFilterIndex == 1,
                                        onClick = { selectedFilterIndex = 1 }
                                    )
                                    CustomFilterChip(
                                        label = "Oldest",
                                        selected = selectedFilterIndex == 2,
                                        onClick = { selectedFilterIndex = 2 }
                                    )
                                }
                            }

                            items(uiState.videos.size) { index ->
                                TabVideoItem(
                                    video = uiState.videos.getOrNull(index),
                                    onVideoSelected = { uiState.videos.getOrNull(index)?.let { onVideoSelected(it) } }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            state = playlistScrollState,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .height(screenHeight)
                                .nestedScroll(insideScrollConnection),
                        ) {
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.sort_24px),
                                        contentDescription = null,
                                        tint = Color.White,
                                    )
                                    Text(
                                        text = "Sort by",
                                        color = Color.White,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                    )
                                }
                            }

                            items(15) { index ->
                                TabPlaylistItem(title = index.toString())
                            }
                        }
                    }
                }
            }

        }

        // Bottom Sheet
        BottomSheet(
            type = bottomSheetType,
            isAuthenticated = uiState.isAuthenticated,
            displayBottomSheet = displayBottomSheet,
            onClose = { displayBottomSheet = false },
            onItemSelected = { item ->
                displayBottomSheet = false
                coroutineScope.launch {
                    delay(350)
                    when (item) {
                        SheetResult.STUDIO -> { onNavigateToStudioScreen() }
                        SheetResult.MY_QR -> { onNavigateToQRCodeScreen() }
                        SheetResult.SETTINGS ->  { onNavigateToSettingsScreen() }
                        SheetResult.UNFOLLOW -> { unfollow() }
                        SheetResult.LOGIN -> { onNavigateToLoginScreen() }
                    }
                }
            }
        )
    }
}
