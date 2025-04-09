package com.example.visara.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.HomeScreen
import com.example.visara.ui.screens.ProfileScreen
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.components.VideoPlayerDash
import com.example.visara.ui.components.VideoPlayerManager
import com.example.visara.ui.components.rememberVideoPlayerManager
import com.example.visara.ui.screens.AddNewVideoScreen
import com.example.visara.ui.screens.FollowingScreen
import com.example.visara.ui.screens.MailScreen
import com.example.visara.ui.screens.SearchScreen
import com.example.visara.ui.screens.VideoDetailScreen

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
    var scaffoldBottomPadding by remember { mutableStateOf(0.dp) }
    var displaySearchOverlay by remember { mutableStateOf(false) }
    val videoDetailState = rememberVideoDetailState()
    val videoPlayerManager = rememberVideoPlayerManager()

    BackHandler(enabled = displaySearchOverlay) {
        displaySearchOverlay = false
    }

    BackHandler(enabled = videoDetailState.display) {
        videoDetailState.minimize()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .zIndex(0f)
            ,
            bottomBar = {
                NavigationBar(
                    containerColor = Color.Transparent,
                ) {
                    botNavItems.forEach { item ->
                        NavigationBarItem(
                            label = { Text(item.label) },
                            selected = currentRoute == item.destination.toString(),
                            onClick = { navController.navigate(item.destination) },
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
            },
        ) { innerPadding ->
            NavHost(
                modifier = Modifier
                    .padding(
                        bottom = innerPadding.calculateBottomPadding()
                    )
                ,
                navController = navController,
                startDestination = Destination.Main.Home,
            ) {
                composable<Destination.Main.Home> {
                    HomeScreen(
                        onVideoSelect = {
                            val videoUrl = "http://10.0.2.2:8080/67d93e93ca386d2312a19f5c/output.mpd"
                            videoPlayerManager.playDash(videoUrl)
                            videoDetailState.display = true
                        },
                        onOpenSearchOverlay = { displaySearchOverlay = true },
                    )
                }
                composable<Destination.Main.Following> {
                    FollowingScreen(
                        displaySnackBar = { message ->
                            displaySnackBar(message, scaffoldBottomPadding)
                        },
                    )
                }
                composable<Destination.Main.AddNewVideo> { AddNewVideoScreen() }
                composable<Destination.Main.Mail> { MailScreen() }
                composable<Destination.Main.Profile> {
                    ProfileScreen(
                        authenticated = authenticated,
                        navigateToLogin = navigateToLogin
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
                videoDetailState.display = true
            }
        )

        VideoDetailScreen(
            videoPlayerManager = videoPlayerManager,
            isDisplay = videoDetailState.display,
            modifier = Modifier
                .zIndex(if (videoDetailState.display) 2f else -2f)
                .fillMaxSize()
                .statusBarsPadding()
//                .navigationBarsPadding()
        )

        if (videoDetailState.minimized) {
            MinimizedVideoDetail(
                videoPlayerManager = videoPlayerManager,
                modifier = Modifier
                    .zIndex(if (videoDetailState.minimized) 3f else -3f)
                    .align(Alignment.BottomEnd)
                    .padding(
                        bottom = 120.dp,
                        end = 24.dp,
                    )
                    .clip(RoundedCornerShape(15.dp))
                ,
                onExpand = {
                    videoDetailState.expand()
                },
                onClose = {
                    videoPlayerManager.exoPlayer.stop()
                    videoDetailState.close()
                },
            )
        }

    }
}

private data class BottomNavigationItemData (
    val label: String,
    val icon: ImageVector,
    val destination: Destination,
)

class VideoDetailState(
    initialDisplay: Boolean = false,
    initialMinimized: Boolean = false,
    initialVideoId: String = ""
) {
    var display by mutableStateOf(initialDisplay)
    var minimized by mutableStateOf(initialMinimized)
    var videoId by mutableStateOf(initialVideoId)

    fun expand() {
        display = true
        minimized = false
    }

    fun minimize() {
        minimized = true
        display = false
    }

    fun close() {
        display = false
        minimized = false
        videoId = ""
    }
}

@Composable
fun rememberVideoDetailState() = remember { VideoDetailState() }

@Composable
private fun MinimizedVideoDetail(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    onExpand: () -> Unit,
    onClose: () -> Unit,
) {
    Box(modifier = modifier.clickable(onClick = onExpand)) {
        VideoPlayerDash(
            videoPlayerManager = videoPlayerManager,
            showControls = false,
            modifier = Modifier
                .width(200.dp)
            ,
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}
