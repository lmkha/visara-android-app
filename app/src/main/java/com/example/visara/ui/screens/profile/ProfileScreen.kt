package com.example.visara.ui.screens.profile

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.example.visara.ui.components.UserAvatar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    isMyProfile: Boolean = false,
    bottomNavBar: @Composable () -> Unit,
    onBack: () -> Unit,
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

                Log.i("CHECK_VAR", "isScrollUp: $isScrollingUp")
                Log.i("CHECK_VAR", "CanScrollOutsideUp: $canScrollOutsideUp")

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
                                text = "lmkha",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        if (isMyProfile) {
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
                            IconButton(onClick = { displayBottomSheet = true }) {
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
                if (isMyProfile) {
                    bottomNavBar()
                }
            }
        ) { innerPadding ->
            LazyColumn(
                state = mainScrollState,
                modifier = Modifier.padding(innerPadding)
                ,
            ) {
                // Avatar, name and username(0)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Box(modifier = Modifier.size(120.dp)) {
                            UserAvatar(modifier = Modifier.size(120.dp))
                            if (isMyProfile) {
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
                            text = "lmkha27",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "lmkha27",
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
                            count = "7",
                            modifier = Modifier.width(100.dp)
                        )
                        MetricItem(
                            label = "Follower",
                            count = "7",
                            modifier = Modifier.width(100.dp)
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
                            @Composable
                            fun ActionButton(
                                modifier: Modifier = Modifier,
                                onClick: () -> Unit,
                                content: @Composable () -> Unit,
                            ) {
                                Box(
                                    modifier = modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(color = Color.LightGray)
                                        .clickable(onClick = onClick),
                                    contentAlignment = Alignment.Center
                                ) {
                                    content()
                                }
                            }

                            ActionButton(onClick = {}) {
                                Text(
                                    text = "Edit profile",
                                    modifier = Modifier.padding(8.dp),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            ActionButton(onClick = {}) {
                                Text(
                                    text = "Share profile",
                                    modifier = Modifier.padding(8.dp),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            ActionButton(onClick = {}) {
                                Icon(
                                    painter = painterResource(R.drawable.studio_24px),
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp),
                                )
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
                            modifier = Modifier
                                .height(screenHeight)
                                .nestedScroll(insideScrollConnection),
                        ) {
                            items(100) { index ->
                                Box(modifier = Modifier.padding(vertical = 8.dp)) {
                                    TabVideoItem(title = index.toString())
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            state = playlistScrollState,
                            modifier = Modifier
                                .height(screenHeight)
                                .nestedScroll(insideScrollConnection),
                        ) {
                            items(15) { index ->
                                Box(modifier = Modifier.padding(vertical = 8.dp)) {
                                    TabPlaylistItem(title = index.toString())
                                }
                            }
                        }
                    }
                }
            }

        }
        // Bottom Sheet
        Box {
            val backgroundAlpha by animateFloatAsState(
                targetValue = if (displayBottomSheet) 0.5f else 0f,
                animationSpec = tween(durationMillis = 300),
                label = "backgroundAlpha"
            )
            // Back layer
            AnimatedVisibility(
                visible = displayBottomSheet,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .zIndex(1f)
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = backgroundAlpha))
                        .clickable { displayBottomSheet = false }
                )
            }

            // Content
            AnimatedVisibility(
                visible = displayBottomSheet,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                ),
                modifier = modifier
                    .zIndex(2f)
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(color = Color.White)
                        .clickable { }
                ) {
                }
            }
        }
    }
}

@Composable
fun TabVideoItem(
    modifier: Modifier = Modifier,
    title: String,
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun TabPlaylistItem(
    modifier: Modifier = Modifier,
    title: String,
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun MetricItem(
    modifier: Modifier = Modifier,
    label: String,
    count: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        )
        Text(
            text = label,
            color = Color.Gray,
        )
    }
}
