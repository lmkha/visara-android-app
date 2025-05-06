package com.example.visara.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.add_new_video.AddNewVideoScreen
import com.example.visara.ui.screens.following.FollowingScreen
import com.example.visara.ui.screens.home.HomeScreen
import com.example.visara.ui.screens.inbox.InboxScreen
import com.example.visara.ui.screens.login.LoginScreen
import com.example.visara.ui.screens.profile.ProfileScreen
import com.example.visara.ui.screens.search.SearchScreen
import com.example.visara.ui.screens.settings.SettingsScreen
import com.example.visara.ui.screens.test.TestScreen
import com.example.visara.ui.screens.video_detail.VideoDetailScreen
import com.example.visara.ui.theme.VisaraTheme
import com.example.visara.viewmodels.AppState
import com.example.visara.viewmodels.AppViewModel
import com.example.visara.viewmodels.VideoDetailViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App(viewModel: AppViewModel = hiltViewModel()) {

    val appState by viewModel.appState.collectAsStateWithLifecycle()
    val videoDetailViewModel: VideoDetailViewModel = hiltViewModel()

    VisaraTheme(appTheme = appState.appTheme) {
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        val snackBarHostState = remember { SnackbarHostState() }
        fun botNavBarNavigate(dest: Destination) {
            scope.launch {
                val popped = navController.popBackStack(route = dest, inclusive = false)
                if (!popped) navController.navigate(dest)
            }
        }
        BackHandler(enabled = appState.videoDetailState.isFullScreenMode) {
            viewModel.minimizeVideoDetail()
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
                                onOpenSearchOverlay = { navController.navigate(Destination.Search) },
                                bottomNavBar = {
                                    BotNavBar(
                                        activeDestination = Destination.Main.Home,
                                        appState = appState,
                                        onNavigate = { botNavBarNavigate(it) }
                                    )
                                }
                            )
                        }
                        composable<Destination.Main.Following> {
                            FollowingScreen(
                                onChangeTheme = {  },
                                navigateToTestScreen = { navController.navigate(Destination.Test) },
                                bottomNavBar = {
                                    BotNavBar(
                                        activeDestination = Destination.Main.Following,
                                        appState = appState,
                                        onNavigate = { botNavBarNavigate(it) }
                                    )
                                },
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
                            val isVideoDetailVisibleBefore = appState.videoDetailState.isVisible
                            scope.launch {
                                viewModel.pauseVideoDetail()
                                viewModel.hideVideoDetail()
                            }

                            DisposableEffect(Unit) {
                                onDispose {
                                    if (isVideoDetailVisibleBefore) {
                                        viewModel.displayVideoDetail()
                                    }
                                }
                            }

                            AddNewVideoScreen(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                            )
                        }
                        composable<Destination.Main.Inbox> {
                            InboxScreen(
                                bottomNavBar = {
                                    BotNavBar(
                                        activeDestination = Destination.Main.Inbox,
                                        appState = appState,
                                        onNavigate = { botNavBarNavigate(it) }
                                    )
                                },
                            )
                        }
                        composable<Destination.Main.Profile> { backStackEntry->
                            val profile: Destination.Main.Profile = backStackEntry.toRoute()
                            ProfileScreen(
                                username = profile.username,
                                isMyProfileRequested = profile.shouldNavigateToMyProfile,
                                onBack = { navController.popBackStack() },
                                onNavigateToLoginScreen = { navController.navigate(Destination.Login) },
                                onNavigateToSettingsScreen = { navController.navigate(Destination.Settings) },
                                onNavigateToStudioScreen = {},
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
                    composable<Destination.Search> {
                        SearchScreen(
                            modifier = Modifier.fillMaxSize(),
                            goBack = { navController.popBackStack() },
                            onSelectResult = { videoId ->
                            }
                        )
                    }
                    composable<Destination.Login> {
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
                }
            }

            Box(
                modifier = if (appState.videoDetailState.isFullScreenMode) Modifier
                    .zIndex(if (appState.videoDetailState.isVisible) 10f else -10f)
                    .fillMaxSize()
                else Modifier
                    .zIndex(if (appState.videoDetailState.isVisible) 10f else -10f)
                    .fillMaxSize()
                    .padding(bottom = 120.dp, end = 24.dp)
            ) {
                appState.videoDetailState.video?.let { videoModel->
                    key(videoModel.id) {
                        VideoDetailScreen(
                            viewModel = videoDetailViewModel,
                            modifier = if (appState.videoDetailState.isFullScreenMode) Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                            else Modifier
                                .width(250.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .aspectRatio(16f / 9f)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
            }
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
        BottomNavigationItemData("Following", Icons.Filled.Star, Destination.Main.Following),
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
