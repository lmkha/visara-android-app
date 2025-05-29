package com.example.visara.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.visara.ui.components.LoginRequestDialog
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.components.rememberLoginRequestDialogState
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.add_new_video.AddNewVideoScreen
import com.example.visara.ui.screens.follow.FollowScreen
import com.example.visara.ui.screens.following_feed.FollowingFeedScreen
import com.example.visara.ui.screens.home.HomeScreen
import com.example.visara.ui.screens.inbox.activity.ActivityInboxScreen
import com.example.visara.ui.screens.inbox.chat.ChatInboxScreen
import com.example.visara.ui.screens.inbox.list.InboxListScreen
import com.example.visara.ui.screens.inbox.new_followers.NewFollowersInboxScreen
import com.example.visara.ui.screens.inbox.system_notification.SystemNotificationInboxScreen
import com.example.visara.ui.screens.login.LoginScreen
import com.example.visara.ui.screens.profile.ProfileScreen
import com.example.visara.ui.screens.search.SearchScreen
import com.example.visara.ui.screens.settings.SettingsScreen
import com.example.visara.ui.screens.studio.StudioScreen
import com.example.visara.ui.screens.test.TestScreen
import com.example.visara.ui.screens.video_detail.VideoDetailScreen
import com.example.visara.ui.theme.VisaraTheme
import com.example.visara.viewmodels.AppState
import com.example.visara.viewmodels.AppViewModel
import com.example.visara.viewmodels.ChatInboxViewModel
import com.example.visara.viewmodels.FollowScreenViewModel
import com.example.visara.viewmodels.SearchViewModel
import com.example.visara.viewmodels.VideoDetailViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter",
    "ConfigurationScreenWidthHeight",
)
fun App(
    appViewModel: AppViewModel,
    requireLandscapeMode: () -> Unit,
    requirePortraitMode: () -> Unit,
) {

    val appState by appViewModel.appState.collectAsStateWithLifecycle()

    val videoDetailViewModel: VideoDetailViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    var enableBottomPaddingForVideoDetail by remember { mutableStateOf(true) }
    val loginRequestDialogState = rememberLoginRequestDialogState()
    fun botNavBarNavigate(dest: Destination) {
        coroutineScope.launch {
            if (dest.route == Destination.Main.AddNewVideo.route && !appState.isAuthenticated) {
                loginRequestDialogState.show("Please log in to post a new video.")
            } else {
                val popped = navController.popBackStack(route = dest, inclusive = false)
                if (!popped) navController.navigate(dest)
            }
        }
    }

    if (appState.videoDetailState.isFullScreenMode) {
        BackHandler {
            appViewModel.minimizeVideoDetail()
        }
    }

    VisaraTheme(appTheme = appState.appTheme) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.zIndex(0f),
                bottomBar = {
                    Column {
                        AnimatedVisibility(
                            visible = appState.shouldDisplayIsOnlineStatus,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                                    .background(color = if (appState.isOnline) Color.Green else Color.Red)
                            )
                        }
                    }
                }
            ) {
                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    startDestination = Destination.Main,
                ) {
                    navigation<Destination.Main>(
                        startDestination = Destination.Main.Home,
//                        startDestination = Destination.Main.Inbox,
                    ) {
                        composable<Destination.Main.Home> {
                            HomeScreen(
                                navigateToSearchScreen = { navController.navigate(Destination.Search()) },
                                navigateToProfileScreen = {
                                    navController.navigate(
                                        Destination.Main.Profile(
                                            username = it
                                        )
                                    )
                                },
                                bottomNavBar = {
                                    BotNavBar(
                                        activeDestination = Destination.Main.Home,
                                        appState = appState,
                                        onNavigate = { botNavBarNavigate(it) }
                                    )
                                }
                            )
                        }
                        composable<Destination.Main.FollowingFeed> {
                            FollowingFeedScreen(
                                bottomNavBar = {
                                    BotNavBar(
                                        activeDestination = Destination.Main.FollowingFeed,
                                        appState = appState,
                                        onNavigate = { botNavBarNavigate(it) }
                                    )
                                },
                            )
                        }
                        composable<Destination.Main.AddNewVideo>(enterTransition = {
                            scaleIn(
                                transformOrigin = TransformOrigin(0.5f, 1f),
                                initialScale = 0.8f
                            ) + fadeIn()
                        }, exitTransition = {
                            scaleOut(
                                transformOrigin = TransformOrigin(0.5f, 1f),
                                targetScale = 0.8f
                            ) + fadeOut()
                        }) {
                            val isVideoDetailVisibleBefore = appState.videoDetailState.isVisible
                            coroutineScope.launch {
                                appViewModel.pauseVideoDetail()
                                appViewModel.hideVideoDetail()
                            }

                            DisposableEffect(Unit) {
                                onDispose {
                                    if (isVideoDetailVisibleBefore) {
                                        appViewModel.displayVideoDetail()
                                    }
                                }
                            }

                            AddNewVideoScreen(
                                onNavigateToStudio = {
                                    navController.navigate(Destination.Studio) {
                                        popUpTo(Destination.Main.AddNewVideo) {
                                            inclusive = true
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                            )
                        }
                        navigation<Destination.Main.Inbox>(startDestination = Destination.Main.Inbox.InboxList) {
                            composable<Destination.Main.Inbox.InboxList> {
                                InboxListScreen(
                                    onOpenChatInbox = { username->
                                        navController.navigate(Destination.Main.Inbox.ChatInbox(username = username))
                                    },
                                    onOpenActivityInbox = { navController.navigate(Destination.Main.Inbox.ActivityInbox) },
                                    onOpenNewFollowersInbox = { navController.navigate(Destination.Main.Inbox.NewFollowersInbox) },
                                    onOpenSystemNotificationInbox = { navController.navigate(Destination.Main.Inbox.SystemNotificationInbox) },
                                    bottomNavBar = {
                                        BotNavBar(
                                            activeDestination = Destination.Main.Inbox,
                                            appState = appState,
                                            onNavigate = { botNavBarNavigate(it) }
                                        )
                                    },
                                )
                            }
                            composable<Destination.Main.Inbox.ChatInbox>(
                                deepLinks = emptyList()
                            ) { backStackEntry ->
                                val route: Destination.Main.Inbox.ChatInbox = backStackEntry.toRoute()
                                val chatInboxScreenViewModel: ChatInboxViewModel = hiltViewModel()
                                chatInboxScreenViewModel.setPartnerUsername(route.username)

                                DisposableEffect(Unit) {
                                    onDispose { chatInboxScreenViewModel.clearPartnerUsername() }
                                }

                                ChatInboxScreen(
                                    viewModel = chatInboxScreenViewModel,
                                    onBack = { navController.popBackStack() },
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                            composable<Destination.Main.Inbox.NewFollowersInbox> {
                                NewFollowersInboxScreen()
                            }
                            composable<Destination.Main.Inbox.ActivityInbox> {
                                ActivityInboxScreen()
                            }
                            composable<Destination.Main.Inbox.SystemNotificationInbox> {
                                SystemNotificationInboxScreen()
                            }
                        }
                        composable<Destination.Main.Profile> { backStackEntry ->
                            val route: Destination.Main.Profile = backStackEntry.toRoute()
                            LaunchedEffect(route.shouldNavigateToMyProfile) {
                                if (route.shouldNavigateToMyProfile) {
                                    appViewModel.syncCurrentUser()
                                }
                            }
                            ProfileScreen(
                                username = route.username,
                                isMyProfileRequested = route.shouldNavigateToMyProfile,
                                onBack = { navController.popBackStack() },
                                onNavigateToFollowScreen = {
                                    navController.navigate(
                                        Destination.Follow(
                                            it
                                        )
                                    )
                                },
                                onNavigateToLoginScreen = { navController.navigate(Destination.Login) },
                                onNavigateToSettingsScreen = {
                                    navController.navigate(
                                        Destination.Settings
                                    )
                                },
                                onNavigateToAddNewVideoScreen = { navController.navigate(Destination.Main.AddNewVideo) },
                                onNavigateToStudioScreen = { navController.navigate(Destination.Studio) },
                                onNavigateToQRCodeScreen = {},
                                bottomNavBar = {
                                    BotNavBar(
                                        activeDestination = Destination.Main.Profile(),
                                        appState = appState,
                                        onNavigate = { botNavBarNavigate(it) }
                                    )
                                },
                            )
                        }
                    }
                    composable<Destination.Search> { backStackEntry ->
                        val search: Destination.Search = backStackEntry.toRoute()
                        val searchViewModel: SearchViewModel = hiltViewModel()
                        if (search.type == "hashtag" && search.pattern.isNotBlank()) {
                            searchViewModel.searchVideoByHashtag(search.pattern)
                        }
                        LaunchedEffect(Unit) { enableBottomPaddingForVideoDetail = false }
                        DisposableEffect(Unit) {
                            onDispose {
                                enableBottomPaddingForVideoDetail = true
                            }
                        }
                        SearchScreen(
                            viewModel = searchViewModel,
                            goBack = { navController.popBackStack() },
                            onViewUserProfile = { username ->
                                navController.navigate(
                                    Destination.Main.Profile(
                                        username = username
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .navigationBarsPadding()
                        )
                    }
                    composable<Destination.Login> {
                        val isVideoDetailVisibleBefore = appState.videoDetailState.isVisible
                        coroutineScope.launch {
                            appViewModel.pauseVideoDetail()
                            appViewModel.hideVideoDetail()
                        }

                        DisposableEffect(Unit) {
                            onDispose {
                                if (isVideoDetailVisibleBefore) {
                                    appViewModel.displayVideoDetail()
                                }
                            }
                        }
                        LoginScreen(
                            onAuthenticated = { navController.popBackStack() }
                        )
                    }
                    composable<Destination.Test> {
                        TestScreen()
                    }
                    composable<Destination.Settings> {
                        SettingsScreen(
                            onBack = { navController.popBackStack() },
                            navigateToLoginScreen = { navController.navigate(Destination.Login) },
                            navigateAfterLogout = {
                                navController.navigate(Destination.Main.Home) {
                                    popUpTo(Destination.Main.Home) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                        )
                    }
                    composable<Destination.Follow> { backStackEntry ->
                        val route: Destination.Follow = backStackEntry.toRoute()
                        val viewModel: FollowScreenViewModel = hiltViewModel()
                        viewModel.setStartedTabIndex(route.startedTabIndex)
                        viewModel.fetchData()

                        DisposableEffect(Unit) {
                            onDispose { appViewModel.syncCurrentUser() }
                        }

                        FollowScreen(
                            viewModel = viewModel,
                            goBack = { navController.popBackStack() },
                            navigateToProfileScreen = {
                                navController.navigate(
                                    Destination.Main.Profile(
                                        username = it
                                    )
                                )
                            },
                        )
                    }
                    composable<Destination.Studio> {
                        StudioScreen(
                            onBack = {
                                val popped = navController.popBackStack(route = Destination.Main, inclusive = false)
                                if (!popped) navController.navigate(Destination.Main)
                            }
                        )
                    }
                }
            }

            // Video detail
            Box(modifier = Modifier.zIndex(if (appState.videoDetailState.isVisible) 10f else -10f)) {
                val isFullScreen = appState.videoDetailState.isFullScreenMode
                val shape by animateDpAsState(targetValue = if (isFullScreen) 0.dp else 15.dp, label = "shape")
                val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
                val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
                val density = LocalDensity.current

                // Video size when not in full screen mode
                val boxWidth = 250.dp
                val boxHeight = boxWidth * 9f / 16f

                // Convert dimensions from dp to pixels using current screen density
                val screenWidthPx = with(density) { screenWidthDp.toPx() }
                val screenHeightPx = with(density) { screenHeightDp.toPx() }
                val boxWidthPx = with(density) { boxWidth.toPx() }
                val boxHeightPx = with(density) { boxHeight.toPx() }

                // Bottom and end (right) padding depending on fullscreen state or other flags
                val bottomPaddingDp = if (isFullScreen) 0.dp else if (enableBottomPaddingForVideoDetail) 180.dp else 24.dp
                val endPaddingDp = if (isFullScreen) 0.dp else 24.dp

                // Convert padding from dp to pixels
                val bottomPaddingPx = with(density) { bottomPaddingDp.toPx() }
                val endPaddingPx = with(density) { endPaddingDp.toPx() }
                val startPaddingPx = with(density) { 24.dp.toPx() }

                // Maximum allowed offsets for X and Y to keep the video inside the screen boundaries
                val maxOffsetX = screenWidthPx - boxWidthPx - endPaddingPx
                val maxOffsetY = screenHeightPx - boxHeightPx - bottomPaddingPx

                val offsetX = remember { Animatable(0f, Float.VectorConverter) }
                val offsetY = remember { Animatable(0f, Float.VectorConverter) }

                LaunchedEffect(appState.videoDetailState.isFullScreenMode) {
                    if (!appState.videoDetailState.isFullScreenMode &&
                        appState.videoDetailState.video?.id != null &&
                        appState.videoDetailState.isVisible
                    ) {
                        offsetX.snapTo(maxOffsetX)
                        offsetY.snapTo(maxOffsetY)
                    }
                    else if (
                        appState.videoDetailState.isFullScreenMode &&
                        appState.videoDetailState.video?.id != null &&
                        appState.videoDetailState.isVisible
                    ) {
                        offsetX.snapTo(0f)
                        offsetY.snapTo(0f)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .imePadding()
                ) {
                    key(appState.videoDetailState.video?.id) {
                        VideoDetailScreen(
                            viewModel = videoDetailViewModel,
                            isFullScreenMode = isFullScreen,
                            isLandscapeMode = appState.isLandscapeMode,
                            requirePortraitMode = requirePortraitMode,
                            requireLandscapeMode = requireLandscapeMode,
                            onNavigateToProfileScreen = { username ->
                                navController.navigate(Destination.Main.Profile(username = username))
                            },
                            onNavigateToLoginScreen = {
                                appViewModel.minimizeVideoDetail()
                                navController.navigate(Destination.Login)
                            },
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        offsetX.value.roundToInt(),
                                        offsetY.value.roundToInt()
                                    )
                                }
                                // Only allow drag when in minimized mode
                                .then(
                                    if (!isFullScreen) Modifier.pointerInput(Unit) {
                                        detectDragGestures(
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                coroutineScope.launch {
                                                    val newX = offsetX.value + dragAmount.x
                                                    val newY = offsetY.value + dragAmount.y

                                                    offsetX.snapTo(newX)
                                                    offsetY.snapTo(newY)
                                                }
                                            },
                                            onDragEnd = {
                                                val midX = screenWidthPx / 2
                                                val midY = screenHeightPx / 2
                                                // Decide target snap position based on whether box center is in left or right half,
                                                // and top or bottom half of the screen
                                                val targetX = if (offsetX.value + boxWidthPx / 2 < midX) startPaddingPx else maxOffsetX
                                                val targetY = if (offsetY.value + boxHeightPx / 2 < midY) 0f else maxOffsetY
                                                coroutineScope.launch {
                                                    // Animate snapping to nearest corner smoothly
                                                    launch {
                                                        offsetX.animateTo(
                                                            targetValue = targetX,
                                                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                                                        )
                                                    }
                                                    launch {
                                                        offsetY.animateTo(
                                                            targetValue = targetY,
                                                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    } else Modifier
                                )
                                .then(
                                    if (isFullScreen) Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            scaleX = 1f
                                            scaleY = 1f
                                            transformOrigin = TransformOrigin(1f, 1f)
                                        }
                                        .statusBarsPadding()
                                    else Modifier
                                        .width(250.dp)
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(shape))
                                )
                                .animateContentSize()
                        )
                    }
                }
            }

            LoginRequestDialog(
                state = loginRequestDialogState,
                onLogin = { navController.navigate(Destination.Login) },
            )
        }
    }
}

@Composable
private fun BotNavBar(
    activeDestination: Destination,
    appState: AppState,
    onNavigate: (Destination) -> Unit
) {
    val botNavItems = listOf(
        BottomNavigationItemData("Home", Icons.Filled.Home, Destination.Main.Home),
        BottomNavigationItemData("Following", Icons.Filled.Star, Destination.Main.FollowingFeed),
        BottomNavigationItemData("Add", Icons.Filled.AddCircle, Destination.Main.AddNewVideo),
        BottomNavigationItemData("Inbox", Icons.Filled.Email, Destination.Main.Inbox),
        BottomNavigationItemData("Profile", Icons.Filled.AccountCircle, Destination.Main.Profile(shouldNavigateToMyProfile = true)),
    )
    NavigationBar(containerColor = Color.Transparent) {
        botNavItems.forEach { item ->
            NavigationBarItem(
                label = { Text(item.label) },
                selected = item.destination.route == activeDestination.route,
                onClick = { onNavigate(item.destination) },
                icon = {
                    if (item.destination.route != Destination.Main.Profile().route) {
                        Icon(imageVector = item.icon, contentDescription = item.label)
                    } else {
                        UserAvatar(
                            avatarLink = appState.currentUser?.networkAvatarUrl,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    }
}

private data class BottomNavigationItemData (
    val label: String,
    val icon: ImageVector,
    val destination: Destination,
)
