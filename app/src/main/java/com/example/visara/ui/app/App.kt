package com.example.visara.ui.app

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.Destination
import com.example.visara.ui.components.LoginRequestDialog
import com.example.visara.ui.components.rememberLoginRequestDialogState
import com.example.visara.ui.screens.add_new_video.AddNewVideoScreen
import com.example.visara.ui.screens.add_new_video.AddNewVideoStep
import com.example.visara.ui.screens.edit_profile.EditProfileScreen
import com.example.visara.ui.screens.edit_video.EditVideoScreen
import com.example.visara.ui.screens.follow.FollowScreen
import com.example.visara.ui.screens.following_feed.FollowingFeedScreen
import com.example.visara.ui.screens.home.HomeScreen
import com.example.visara.ui.screens.inbox.activity.ActivityInboxScreen
import com.example.visara.ui.screens.inbox.chat.ChatInboxScreen
import com.example.visara.ui.screens.inbox.list.InboxListScreen
import com.example.visara.ui.screens.inbox.new_followers.NewFollowersInboxScreen
import com.example.visara.ui.screens.inbox.studio.StudioInboxScreen
import com.example.visara.ui.screens.inbox.system_notification.SystemNotificationInboxScreen
import com.example.visara.ui.screens.login.LoginScreen
import com.example.visara.ui.screens.profile.ProfileScreen
import com.example.visara.ui.screens.qrcode.QRCodeGereratorScreen
import com.example.visara.ui.screens.qrcode.QRCodeScannerScreen
import com.example.visara.ui.screens.qrcode.QRCodeScreen
import com.example.visara.ui.screens.search.SearchScreen
import com.example.visara.ui.screens.settings.SettingsScreen
import com.example.visara.ui.screens.studio.StudioScreen
import com.example.visara.ui.screens.studio.StudioSelectedTag
import com.example.visara.ui.screens.test.TestScreen
import com.example.visara.ui.screens.video_detail.VideoDetailScreen
import com.example.visara.ui.theme.AppTheme
import com.example.visara.ui.theme.VisaraTheme
import com.example.visara.viewmodels.AddNewVideoViewModel
import com.example.visara.viewmodels.AppEvent
import com.example.visara.viewmodels.AppViewModel
import com.example.visara.viewmodels.ChatInboxViewModel
import com.example.visara.viewmodels.EditVideoViewModel
import com.example.visara.viewmodels.FollowScreenViewModel
import com.example.visara.viewmodels.ProfileViewModel
import com.example.visara.viewmodels.SearchViewModel
import com.example.visara.viewmodels.VideoDetailViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt

@Composable
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter",
    "ConfigurationScreenWidthHeight",
)
fun App(
    appViewModel: AppViewModel,
    onRequireLandscapeMode: () -> Unit,
    onRequirePortraitMode: () -> Unit,
    onRequireAppearanceLightStatusBars: () -> Unit,
    onRequireAppearanceDarkStatusBars: () -> Unit,
    onRequireAppearanceDefaultStatusBars: () -> Unit,
) {

    val appState by appViewModel.appState.collectAsStateWithLifecycle()

    val videoDetailViewModel: VideoDetailViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    var enableBottomPaddingForVideoDetail by remember { mutableStateOf(true) }
    val loginRequestDialogState = rememberLoginRequestDialogState()

    LaunchedEffect(
        key1 = appState.appTheme,
        key2 = appState.videoDetailState.isFullScreenMode,
    ) {
        if (appState.videoDetailState.isFullScreenMode) {
            onRequireAppearanceDarkStatusBars()
        } else {
            when(appState.appTheme) {
                AppTheme.LIGHT -> onRequireAppearanceLightStatusBars()
                AppTheme.DARK -> onRequireAppearanceDarkStatusBars()
                AppTheme.SYSTEM -> onRequireAppearanceDefaultStatusBars()
            }
        }
    }

    val lifeCycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifeCycleOwner.lifecycle) {
        lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            appViewModel.eventFlow.collect { event ->
                when (event) {
                    is AppEvent.NavigateToScreen -> {
                        navController.navigate(event.destination)
                    }
                }
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
                bottomBar = {
                    Column {
                        BottomNavBar(
                            navController = navController,
                            currentUserAvatarUrl = appState.currentUser?.networkAvatarUrl,
                            onNavigate = { navController.navigate(it) }
                        )
                        AnimatedVisibility(visible = appState.shouldDisplayIsOnlineStatus) {
                            Box(Modifier
                                .fillMaxWidth()
                                .height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                                .background(color = if (appState.isOnline) Color.Green else Color.Red)
                            )
                        }
                    }
                },
                modifier = Modifier.zIndex(0f)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Destination.Main,
//                    startDestination = Destination.QRCode,
                    modifier = Modifier.fillMaxSize()
                ) {
                    navigation<Destination.Main>(startDestination = Destination.Main.Home) {
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
                            )
                        }
                        composable<Destination.Main.FollowingFeed> {
                            FollowingFeedScreen(
                                onNavigateToProfileScreen = { username ->
                                    navController.navigate(Destination.Main.Profile(username = username))
                                },
                                onNavigateToFollowingScreen = {
                                    navController.navigate(Destination.Follow(startedTabIndex = 0))
                                },
                                onNavigateToLoginScreen = {
                                    navController.navigate(Destination.Login)
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
                        }) { backStackEntry ->
                            appViewModel.hideVideoDetail()
                            val isVideoDetailVisibleBefore = appState.videoDetailState.isVisible
                            LaunchedEffect(Unit) {
                                appViewModel.saveCurrentPlaybackState()
                            }
                            DisposableEffect(Unit) {
                                onDispose {
                                    if (isVideoDetailVisibleBefore) {
                                        appViewModel.displayVideoDetail()
                                    }
                                    appViewModel.restoreStoredPlaybackState()
                                    videoDetailViewModel.refreshPlayer()
                                }
                            }

                            val viewModel: AddNewVideoViewModel = hiltViewModel()
                            val route: Destination.Main.AddNewVideo = backStackEntry.toRoute()
                            val postFromDraft = route.isPostDraft && route.localDraftVideoId != null
                            if (postFromDraft) {
                                route.localDraftVideoId.let { viewModel.prepareDraftData(it) }
                            }

                            AddNewVideoScreen(
                                viewModel = viewModel,
                                startingStep = if (postFromDraft) AddNewVideoStep.REVIEW_VIDEO
                                else AddNewVideoStep.SELECT_VIDEO,
                                onNavigateToStudio = { selectedTag ->
                                    when (selectedTag) {
                                        StudioSelectedTag.UPLOADING -> {
                                            navController.navigate(Destination.Studio(selectedTagIndex = StudioSelectedTag.UPLOADING.ordinal)) {
                                                popUpTo(navController.currentDestination?.id ?: return@navigate) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                        StudioSelectedTag.DRAFT -> {
                                            navController.navigate(Destination.Studio(selectedTagIndex = StudioSelectedTag.DRAFT.ordinal)) {
                                                popUpTo(navController.currentDestination?.id ?: return@navigate) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                        else -> {
                                            navController.navigate(Destination.Studio(selectedTagIndex = StudioSelectedTag.ACTIVE.ordinal)) {
                                                popUpTo(navController.currentDestination?.id ?: return@navigate) {
                                                    inclusive = true
                                                }
                                            }
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
                                    onOpenActivityInbox = { navController.navigate(Destination.Main.Inbox.ActivityInbox) },
                                    onOpenNewFollowersInbox = { navController.navigate(Destination.Main.Inbox.NewFollowersInbox) },
                                    onOpenSystemNotificationInbox = { navController.navigate(
                                        Destination.Main.Inbox.SystemNotificationInbox) },
                                    openStudioInbox = { navController.navigate(Destination.Main.Inbox.Studio) },
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
                                NewFollowersInboxScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable<Destination.Main.Inbox.ActivityInbox> {
                                ActivityInboxScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable<Destination.Main.Inbox.Studio> {
                                StudioInboxScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable<Destination.Main.Inbox.SystemNotificationInbox> {
                                SystemNotificationInboxScreen()
                            }
                        }
                        composable<Destination.Main.Profile>(
                            deepLinks = listOf(
                                navDeepLink<Destination.Main.Profile>(
                                    basePath = "https://visara.com/profile"
                                )
                            )
                        ) { backStackEntry ->
                            val route: Destination.Main.Profile = backStackEntry.toRoute()
                            val viewModel: ProfileViewModel = hiltViewModel()
                            viewModel.setProfile(
                                isMyProfileRequested = route.shouldNavigateToMyProfile,
                                username = route.username
                            )
                            ProfileScreen(
                                viewModel = viewModel,
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
                                onNavigateToAddNewVideoScreen = { navController.navigate(Destination.Main.AddNewVideo()) },
                                onNavigateToStudioScreen = { navController.navigate(Destination.Studio()) },
                                onNavigateToQRCodeScreen = {},
                                onNavigateToEditVideoScreen = { video ->
                                    coroutineScope.launch {
                                        val videoJson = Json.encodeToString(video)
                                        navController.navigate(Destination.EditVideo(videoJson = videoJson))
                                    }
                                },
                                onNavigateToEditProfileScreen = {
                                    navController.navigate(Destination.EditProfile)
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
                    composable<Destination.Studio> { backStackEntry ->
                        val route: Destination.Studio = backStackEntry.toRoute()
                        val selectedTag = StudioSelectedTag.entries.getOrNull(route.selectedTagIndex)
                        StudioScreen(
                            initialSelectedTag = selectedTag ?: StudioSelectedTag.ACTIVE,
                            onNavigateToAddNewVideoScreen = {
                                navController.navigate(
                                    Destination.Main.AddNewVideo(
                                    isPostDraft = true,
                                    localDraftVideoId = it,
                                ))
                            },
                            onBack = {
                                val popped = navController.popBackStack(route = Destination.Main, inclusive = false)
                                if (!popped) navController.navigate(Destination.Main)
                            }
                        )
                    }
                    composable<Destination.EditVideo> { backStackEntry ->
                        val route: Destination.EditVideo = backStackEntry.toRoute()
                        val video = Json.decodeFromString<VideoModel>(route.videoJson)
                        val viewModel: EditVideoViewModel = hiltViewModel()
                        viewModel.setVideo(video)
                        EditVideoScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable<Destination.EditProfile> {
                        EditProfileScreen(
                            onBack = { navController.popBackStack() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable<Destination.QRCodeGenerate> {
                        QRCodeGereratorScreen(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable<Destination.QRCodeScan> {
                        QRCodeScannerScreen(
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    composable<Destination.QRCode> {
                        QRCodeScreen(
                            onNavigateToGenerateQRCode = { navController.navigate(Destination.QRCodeGenerate) },
                            onNavigateToScanQRCode = { navController.navigate(Destination.QRCodeScan) }
                        )
                    }
                }
            }

            // Video detail
            Box(modifier = Modifier
                .zIndex(if (appState.videoDetailState.isVisible) 10f else -10f)
                .fillMaxSize()
                .background(color = if (appState.videoDetailState.isFullScreenMode) Color.Black else Color.Transparent)
                .statusBarsPadding()
                .imePadding()) {
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

                VideoDetailScreen(
                    viewModel = videoDetailViewModel,
                    isFullScreenMode = isFullScreen,
                    isLandscapeMode = appState.isLandscapeMode,
                    requirePortraitMode = onRequirePortraitMode,
                    requireLandscapeMode = onRequireLandscapeMode,
                    onNavigateToProfileScreen = { username ->
                        appViewModel.minimizeVideoDetail()
                        try {
                            val route = navController.currentBackStackEntry?.toRoute<Destination.Main.Profile>()
                            if (route == null || route.username != username) {
                                navController.navigate(Destination.Main.Profile(username = username))
                            }
                        } catch (_: Exception) {
                            navController.navigate(Destination.Main.Profile(username = username))
                        }
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
                            else Modifier
                                .width(250.dp)
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(shape))
                        )
                        .animateContentSize()
                )
            }

            LoginRequestDialog(
                state = loginRequestDialogState,
                onLogin = { navController.navigate(Destination.Login) },
            )
        }
    }
}
