package com.example.visara.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.components.rememberVideoPlayerManager
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.add_new_video.AddNewVideoScreen
import com.example.visara.ui.screens.following.FollowingScreen
import com.example.visara.ui.screens.home.HomeScreen
import com.example.visara.ui.screens.mail.MailScreen
import com.example.visara.ui.screens.profile.ProfileScreen
import com.example.visara.ui.screens.search.SearchScreen
import com.example.visara.ui.screens.video_detail.VideoDetailScreen
import com.example.visara.ui.screens.video_detail.rememberVideoDetailState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    authenticated: Boolean = false,
    navigateToLogin: () -> Unit,
    displaySnackBar: (message: String, bottomPadding: Dp) -> Unit = {_,_-> },
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
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
            label = "Mail",
            icon = Icons.Filled.Email,
            destination = Destination.Main.Mail
        ),
        BottomNavigationItemData(
            label = "Profile",
            icon = Icons.Filled.AccountCircle,
            destination = Destination.Main.Profile
        ),
    )
    var displaySearchOverlay by remember { mutableStateOf(false) }
    val videoPlayerManager = rememberVideoPlayerManager(LocalContext.current)
    val videoDetailState = rememberVideoDetailState(manager = videoPlayerManager)
    val scope = rememberCoroutineScope()
    var isBottomBarHidden by remember { mutableStateOf(false) }

    if (displaySearchOverlay) {
        BackHandler {
            displaySearchOverlay = false
        }
    }

    if (videoDetailState.isFullScreenMode) {
        BackHandler(enabled = videoDetailState.isFullScreenMode) {
            videoDetailState.enableMinimizedMode()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.zIndex(0f),
            bottomBar = {
                AnimatedVisibility(
                    visible = !isBottomBarHidden,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 80)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 80)
                    ),
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                    ) {
                        botNavItems.forEach { item ->
                            NavigationBarItem(
                                label = { Text(item.label) },
                                selected = currentRoute == item.destination.toString(),
                                onClick = {
                                    scope.launch {
                                        if (item.destination == Destination.Main.AddNewVideo) {
                                            videoPlayerManager.dashExoPlayer.pause()
                                            videoDetailState.isVisible = false
                                        }
                                        navController.navigate(item.destination)
                                    }
                                },
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
            },
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                navController = navController,
                startDestination = Destination.Main.Home,
            ) {
                composable<Destination.Main.Home> {
                    HomeScreen(
                        onVideoSelect = {
                            scope.launch {
//                                val videoUrl = "http://10.0.2.2:8080/67d93e93ca386d2312a19f5c/output.mpd"
                                val videoUrl2 = "http://10.0.2.2:8080/67e42c30bb79412ece6f639a/output.mpd"
                                videoPlayerManager.playDash(videoUrl2)
                                videoDetailState.videoId = "mock-id"
                                videoDetailState.isVisible = true
                                videoDetailState.isFullScreenMode = true
                            }
                        },
                        onOpenSearchOverlay = { displaySearchOverlay = true },
                    )
                }
                composable<Destination.Main.Following> {
                    FollowingScreen(
                        displaySnackBar = { message ->
                            displaySnackBar(message, innerPadding.calculateBottomPadding())
                        },
                    )
                }
                composable<Destination.Main.AddNewVideo> {
                    AddNewVideoScreen(
                        videoPlayerManager = videoPlayerManager,
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                        ,
                        onHideBottomNavigationBar = { isBottomBarHidden = true },
                        onShowBottomNavigationBar = { isBottomBarHidden = false },
                    )
                }
                composable<Destination.Main.Mail> {
                    MailScreen()
                }
                composable<Destination.Main.Profile> {
                    ProfileScreen(
                        authenticated = authenticated,
                        navigateToLogin = navigateToLogin,
                    )
                }
            }
        }

        SearchScreen(
            modifier = Modifier
                .zIndex(if (displaySearchOverlay) 1f else -1f)
                .fillMaxSize(),
            goBack = { displaySearchOverlay = false },
            onSelectResult = {videoId->
                videoDetailState.isFullScreenMode = true
            }
        )

        VideoDetailScreen(
            videoId = videoDetailState.videoId,
            videoPlayerManager = videoPlayerManager,
            isFullScreenMode = videoDetailState.isFullScreenMode,
            onPlay = {
                scope.launch {
                    videoPlayerManager.dashExoPlayer.play()
                }
            },
            onPause = {
                scope.launch {
                    videoPlayerManager.dashExoPlayer.pause()
                }
            },
            onEnableFullScreenMode = {
                videoDetailState.enableFullScreenMode()
            },
            onClose = {
                scope.launch {
                    videoPlayerManager.dashExoPlayer.pause()
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
                    .clip(RoundedCornerShape(15.dp))
            ,
        )
    }
}

private data class BottomNavigationItemData (
    val label: String,
    val icon: ImageVector,
    val destination: Destination,
)
