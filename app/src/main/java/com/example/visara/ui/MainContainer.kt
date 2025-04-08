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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.visara.ui.components.UserAvatar
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
    var displayVideoDetailOverlay by remember { mutableStateOf(false) }

    BackHandler(enabled = displaySearchOverlay) {
        displaySearchOverlay = false
    }

    BackHandler(enabled = displayVideoDetailOverlay) {
        displayVideoDetailOverlay = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .zIndex(0f),
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
                            }
                        )
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                navController = navController,
                startDestination = Destination.Main.Home,
            ) {
                composable<Destination.Main.Home> {
                    HomeScreen(
                        onVideoSelect = { displayVideoDetailOverlay = true },
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
            goBack = { displaySearchOverlay = false },
            modifier = Modifier
                .zIndex(if (displaySearchOverlay) 1f else -1f)
                .fillMaxSize()
        )

        VideoDetailScreen(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(if (displayVideoDetailOverlay) 2f else -2f)
                .statusBarsPadding()
        )
    }
}

private data class BottomNavigationItemData (
    val label: String,
    val icon: ImageVector,
    val destination: Destination,
)
