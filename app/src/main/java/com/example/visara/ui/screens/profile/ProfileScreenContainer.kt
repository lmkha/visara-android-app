package com.example.visara.ui.screens.profile

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.visara.R
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.components.ObserverAsEvents
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.screens.profile.components.ActionButton
import com.example.visara.ui.screens.profile.components.AddNewPlaylistDialog
import com.example.visara.ui.screens.profile.components.AddVideoToPlaylistBottomSheet
import com.example.visara.ui.screens.profile.components.BottomSheet
import com.example.visara.ui.screens.profile.components.DeleteVideoBottomSheet
import com.example.visara.ui.screens.profile.components.MetricItem
import com.example.visara.ui.screens.profile.components.SheetResult
import com.example.visara.ui.screens.profile.components.SheetType
import com.example.visara.ui.screens.profile.components.TabPlaylistItem
import com.example.visara.ui.screens.profile.components.TabVideoItem
import com.example.visara.ui.screens.profile.components.VideoMoreActionBottomSheet
import com.example.visara.ui.theme.LocalVisaraCustomColors
import com.example.visara.viewmodels.ProfileEvent
import com.example.visara.viewmodels.ProfileScreenUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContainer(
    modifier: Modifier = Modifier,
    uiState: ProfileScreenUiState,
    uiEvent: Flow<ProfileEvent>,
    bottomNavBar: @Composable () -> Unit,
    onBack: () -> Unit,
    follow: () -> Unit,
    unfollow: () -> Unit,
    deleteVideo: (video: VideoModel) -> Unit,
    onNavigateToFollowScreen: (startedTabIndex: Int) -> Unit,
    onNavigateToSettingsScreen: () -> Unit,
    onNavigateToStudioScreen: () -> Unit,
    onNavigateToQRCodeScreen: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToAddNewVideoScreen: () -> Unit,
    onVideoSelected: (video: VideoModel) -> Unit,
    onAddNewPlaylist: (title: String) -> Unit,
    onAddVideoToPlaylists: (videoId: String, playlistIds: List<String>) -> Unit,
    onNavigateToEditVideoScreen: (video: VideoModel) -> Unit,
    onNavigateToEditProfileScreen: () -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var showBottomSheet by remember { mutableStateOf(false) }
    val mainScrollState = rememberLazyListState()
    val videoScrollState = rememberLazyListState()
    val playlistScrollState = rememberLazyListState()
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
    var bottomSheetType by remember { mutableStateOf(SheetType.SETTINGS) }
    var showMoreActionBottomSheet by remember { mutableStateOf(false) }
    var showAddNewPlaylistDialog by remember { mutableStateOf(false) }
    var selectedMoreActionItem by remember { mutableStateOf<VideoModel?>(null) }
    var selectedDeleteVideo by remember { mutableStateOf<VideoModel?>(null) }
    var showAddVideoToPlaylistBottomSheet by remember { mutableStateOf(false) }

    ObserverAsEvents(uiEvent) { event ->
        when (event) {
            is ProfileEvent.CreatePlaylistSuccess -> {
                showAddNewPlaylistDialog = false
            }
            is ProfileEvent.CreatePlaylistFailure -> {
                showAddNewPlaylistDialog = false
            }
            is ProfileEvent.AddVideoToPlaylistsSuccess -> {
                showAddVideoToPlaylistBottomSheet = false
            }
            is ProfileEvent.AddVideoToPlaylistsFailure -> {
                showAddVideoToPlaylistBottomSheet = false
            }
            ProfileEvent.DeleteVideoFailure -> { selectedDeleteVideo = null }
            ProfileEvent.DeleteVideoSuccess -> { selectedDeleteVideo = null }
        }
    }

    BackHandler(enabled = showBottomSheet) {
        showBottomSheet = false
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
                                showBottomSheet = true
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Avatar, name and username(0)
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
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
                                                .clickable(onClick = onNavigateToAddNewVideoScreen)
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
                        }
                    }

                    /**
                     * Actions(2)
                     *  MyProfile: Edit profile, share profile, open studio
                     *  Not followed: Follow, Send message
                     *  Following: Send message, unfollow(wrap in a icon button)
                     */
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (uiState.isMyProfile) {
                                    ActionButton(onClick = onNavigateToEditProfileScreen) {
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
                                    ActionButton(onClick = onNavigateToStudioScreen) {
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
                                                showBottomSheet = true
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
                                                    showBottomSheet = true
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
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            val bio = uiState.user?.bio
                            Text(
                                text = if (bio?.isNotBlank() == true) bio else "No bio yet",
                                maxLines = 1,
                                modifier = Modifier.width(300.dp),
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    // Tab header(4)
                    stickyHeader {
                        PrimaryTabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .background(color = Color.Red)
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
                                items(uiState.videos) { video ->
                                    TabVideoItem(
                                        video = video,
                                        showMoreActionButton = uiState.isMyProfile,
                                        onVideoSelected = { onVideoSelected(video) },
                                        onMoreAction = {
                                            selectedMoreActionItem = video
                                            showMoreActionBottomSheet = true
                                        }
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
                                items(uiState.playlists) { playlist ->
                                    TabPlaylistItem(
                                        playlist = playlist
                                    )
                                }
                            }
                        }
                    }
            }
        }

        if (showBottomSheet) {
            BottomSheet(
                type = bottomSheetType,
                isAuthenticated = uiState.isAuthenticated,
                onClose = { showBottomSheet = false },
                onItemSelected = { item ->
                    showBottomSheet = false
                    coroutineScope.launch {
                        delay(350)
                        when (item) {
                            SheetResult.STUDIO -> {
                                onNavigateToStudioScreen()
                            }

                            SheetResult.MY_QR -> {
                                onNavigateToQRCodeScreen()
                            }

                            SheetResult.SETTINGS -> {
                                onNavigateToSettingsScreen()
                            }

                            SheetResult.UNFOLLOW -> {
                                unfollow()
                            }

                            SheetResult.LOGIN -> {
                                onNavigateToLoginScreen()
                            }
                        }
                    }
                }
            )
        }

        if (uiState.isMyProfile) {
            if (showMoreActionBottomSheet) {
                VideoMoreActionBottomSheet(
                    onDismissRequest = { showMoreActionBottomSheet = false },
                    onSelectAddVideoToPlaylist = {
                        showMoreActionBottomSheet = false
                        showAddVideoToPlaylistBottomSheet = true
                    },
                    onSelectEditVideo = {
                        selectedMoreActionItem?.let { onNavigateToEditVideoScreen(it) }
                    },
                    onSelectDeleteVideo = {
                        showMoreActionBottomSheet = false
                        selectedDeleteVideo = selectedMoreActionItem
                    }
                )
            }

            if (showAddVideoToPlaylistBottomSheet) {
                AddVideoToPlaylistBottomSheet(
                    playlists = uiState.playlists,
                    onDismissRequest = { showAddVideoToPlaylistBottomSheet = false },
                    onSelectAddNewPlaylist = {
                        showAddVideoToPlaylistBottomSheet = false
                        showAddNewPlaylistDialog = true
                    },
                    onFinish = { selectedPlaylistId ->
                        selectedMoreActionItem?.id?.let { videoId ->
                            onAddVideoToPlaylists(videoId, selectedPlaylistId)
                        }
                    }
                )
            }

            if (showAddNewPlaylistDialog) {
                AddNewPlaylistDialog(
                    onSubmit = onAddNewPlaylist,
                    onDismissRequest = { showAddNewPlaylistDialog = false }
                )
            }

            if (selectedDeleteVideo != null) {
                DeleteVideoBottomSheet(
                    video = selectedDeleteVideo!!,
                    onDismiss = { selectedDeleteVideo = null },
                    onSubmit = {
                        selectedDeleteVideo?.let { deleteVideo(it) }
                    },
                    modifier = Modifier
                )
            }
        }
    }
}
