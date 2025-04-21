package com.example.visara.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.components.rememberDashVideoPlayerManager
import com.example.visara.ui.components.rememberLocalVideoPlayerManager
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.add_new_video.AddNewVideoScreen
import com.example.visara.ui.screens.following.FollowingScreen
import com.example.visara.ui.screens.home.HomeScreen
import com.example.visara.ui.screens.login.LoginScreen
import com.example.visara.ui.screens.inbox.InboxScreen
import com.example.visara.ui.screens.profile.ProfileScreen
import com.example.visara.ui.screens.search.SearchScreen
import com.example.visara.ui.screens.settings.SettingsScreen
import com.example.visara.ui.screens.test.TestScreen
import com.example.visara.ui.screens.video_detail.VideoDetailScreen
import com.example.visara.ui.screens.video_detail.rememberVideoDetailState
import com.example.visara.ui.theme.VisaraTheme
import com.example.visara.viewmodels.AppViewModel
import com.example.visara.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App(viewModel: AppViewModel = viewModel()) {

    val appState by viewModel.appState.collectAsStateWithLifecycle()

    VisaraTheme(appTheme = appState.appTheme) {
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        val snackBarHostState = remember { SnackbarHostState() }
        val dashPlayerManager = rememberDashVideoPlayerManager(LocalContext.current)
        val localVideoPlayerManager = rememberLocalVideoPlayerManager(LocalContext.current)
        val videoDetailState = rememberVideoDetailState(manager = dashPlayerManager)
        @Composable fun BotNavNar(activeDestination: Destination) {
            val botNavItems = listOf<BottomNavigationItemData>(
                BottomNavigationItemData(
                    label = "Home",
                    icon = Icons.Filled.Home,
                    destination = Destination.Main.Home
                ),
                BottomNavigationItemData(
                    label = "Following",
                    icon = Icons.Filled.Star,
                    destination = Destination.Main.Following
                ),
                BottomNavigationItemData(
                    label = "Add",
                    icon = Icons.Filled.AddCircle,
                    destination = Destination.Main.AddNewVideo
                ),
                BottomNavigationItemData(
                    label = "Inbox",
                    icon = Icons.Filled.Email,
                    destination = Destination.Main.Inbox
                ),
                BottomNavigationItemData(
                    label = "Profile",
                    icon = Icons.Filled.AccountCircle,
                    destination = Destination.Main.Profile(username = "lmkha27")
                ),
            )
            NavigationBar(containerColor = Color.Transparent) {
                botNavItems.forEach { item ->
                    NavigationBarItem(
                        label = { Text(item.label) },
                        selected = item.destination.route == activeDestination.route,
                        onClick = { scope.launch {navController.navigate(item.destination) }},
                        icon = {
                            if (item.destination != Destination.Main.Profile) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            } else {
                                UserAvatar(modifier = Modifier.size(24.dp))
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

        BackHandler(enabled = videoDetailState.isFullScreenMode) {
            videoDetailState.enableMinimizedMode()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.zIndex(0f),
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackBarHostState,
                    )
                },
            ) {
                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    startDestination = Destination.Main(),
                ) {
                    navigation<Destination.Main>(startDestination = Destination.Main.Home) {
                        composable<Destination.Main.Home> {
                            HomeScreen(
                                onVideoSelect = {
                                    scope.launch {
//                                val videoUrl = "http://10.0.2.2:8080/67d93e93ca386d2312a19f5c/output.mpd"
                                        val videoUrl2 = "http://10.0.2.2:8080/67e42c30bb79412ece6f639a/output.mpd"
                                        dashPlayerManager.play(videoUrl2)
                                        videoDetailState.videoId = "mock-id"
                                        videoDetailState.isVisible = true
                                        videoDetailState.isFullScreenMode = true
                                    }
                                },
                                onOpenSearchOverlay = { navController.navigate(Destination.Search) },
                                bottomNavBar = { BotNavNar(Destination.Main.Home) }
                            )
                        }
                        composable<Destination.Main.Following> {
                            FollowingScreen(
                                onChangeTheme = {  },
                                bottomNavBar = { BotNavNar(Destination.Main.Following) },
                                navigateToTestScreen = { navController.navigate(Destination.Test) }
                            )
                        }
                        composable<Destination.Main.AddNewVideo>(
                            enterTransition = {
                                scaleIn(transformOrigin = TransformOrigin(0.5f, 1f), initialScale = 0.8f) + fadeIn()
                            },
                            exitTransition = {
                                scaleOut(transformOrigin = TransformOrigin(0.5f, 1f), targetScale = 0.8f) + fadeOut()
                            }
                        ) {
                            val isVideoDetailVisibleBefore = videoDetailState.isVisible
                            scope.launch {
                                dashPlayerManager.player.pause()
                                videoDetailState.isVisible = false
                            }

                            DisposableEffect(Unit) {
                                onDispose {
                                    if (isVideoDetailVisibleBefore) {
                                        videoDetailState.isVisible = true
                                    }
                                }
                            }

                            AddNewVideoScreen(
                                videoPlayerManager = localVideoPlayerManager,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                            )
                        }
                        composable<Destination.Main.Inbox> {
                            InboxScreen(
                                bottomNavBar = { BotNavNar(Destination.Main.Inbox) },
                            )
                        }
                        composable<Destination.Main.Profile> { backStackEntry->
                            ProfileScreen(
                                isMyProfile = true,
                                bottomNavBar = { BotNavNar(Destination.Main.Profile()) },
                                onBack = { navController.popBackStack() },
                                onNavigateToStudioScreen = {},
                                onNavigateToSettingsScreen = { navController.navigate(Destination.Settings) },
                                onNavigateToQRCodeScreen = {},
                            )
                        }
                    }
                    composable<Destination.Search> {
                        SearchScreen(
                            modifier = Modifier.fillMaxSize(),
                            goBack = { navController.popBackStack() },
                            onSelectResult = { videoId ->
                                videoDetailState.isFullScreenMode = true
                            }
                        )
                    }
                    composable<Destination.Login> {
                        LoginScreen {
                            navController.popBackStack()
                        }
                    }
                    composable<Destination.Test> {
                        TestScreen()
                    }
                    composable<Destination.Settings> {
                        val settingsViewModel: SettingsViewModel = hiltViewModel()
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }

            VideoDetailScreen(
                videoId = videoDetailState.videoId,
                videoPlayerManager = dashPlayerManager,
                isFullScreenMode = videoDetailState.isFullScreenMode,
                onPlay = {
                    scope.launch {
                        dashPlayerManager.player.play()
                    }
                },
                onPause = {
                    scope.launch {
                        dashPlayerManager.player.pause()
                    }
                },
                onEnableFullScreenMode = {
                    videoDetailState.enableFullScreenMode()
                },
                onClose = {
                    scope.launch {
                        dashPlayerManager.player.pause()
                        videoDetailState.close()
                    }
                },
                isPlaying = videoDetailState.isPlaying,
                modifier = if (videoDetailState.isFullScreenMode) Modifier
                    .zIndex(if (videoDetailState.isVisible) 10f else -10f)
                    .fillMaxSize()
                    .statusBarsPadding()
                else Modifier
                    .width(220.dp)
                    .height(220.dp)
                    .zIndex(if (videoDetailState.isVisible) 10f else -10f)
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 24.dp)
                    .clip(RoundedCornerShape(15.dp)),
            )
        }
    }
}
private data class BottomNavigationItemData (
    val label: String,
    val icon: ImageVector,
    val destination: Destination,
)
