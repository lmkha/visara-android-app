package com.example.visara.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.visara.R
import com.example.visara.ui.CheckScreen
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.HomeScreen
import com.example.visara.ui.screens.ProfileScreen
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.visara.ui.screens.SearchScreen
import com.example.visara.ui.screens.VideoDetailScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    authenticated: Boolean = false,
    navigateToLogin: () -> Unit,
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
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var scaffoldBottomPadding by remember { mutableStateOf(0.dp) }
    var displaySearchOverlay by remember { mutableStateOf(false) }
    var displayVideoDetailOverlay by remember { mutableStateOf(false) }

    BackHandler(enabled = displaySearchOverlay) {
        displaySearchOverlay = false
    }

    BackHandler(enabled = displayVideoDetailOverlay) {
        displayVideoDetailOverlay = false
    }

    LaunchedEffect(currentRoute) {
        currentRoute?.let {
            scrollBehavior.state.contentOffset = 0f
            scrollBehavior.state.heightOffset = 0f
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .zIndex(0f)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    actions = {
                        IconButton(onClick = { displaySearchOverlay = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
//                        scrolledContainerColor = Color.Black.copy(alpha = 0.3f),
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(0.98f),
                ) {
                    botNavItems.forEach { item ->
                        NavigationBarItem(
                            label = { Text(item.label) },
                            selected = currentRoute == item.destination.toString(),
                            onClick = { navController.navigate(item.destination) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            }
                        )
                    }
                }
            },
        ) { innerPadding ->
            scaffoldBottomPadding = innerPadding.calculateBottomPadding()
            NavHost(
                navController = navController,
                startDestination = Destination.Main.Home,
            ) {
                composable<Destination.Main.Home> {
                    HomeScreen(
                        modifier = Modifier
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                            ),
                        bottomPadding = innerPadding.calculateBottomPadding(),
                        onVideoSelect = { displayVideoDetailOverlay = true }
                    )
                }
                composable<Destination.Main.Following> { CheckScreen("Following Screen") }
                composable<Destination.Main.AddNewVideo> { CheckScreen("Add new video Screen") }
                composable<Destination.Main.Mail> { CheckScreen("Mail Screen") }
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
                .zIndex(if (displayVideoDetailOverlay) 2f else -2f)
                .statusBarsPadding()
                .padding(bottom = scaffoldBottomPadding)
        )
    }
}

data class BottomNavigationItemData (
    val label: String,
    val icon: ImageVector,
    val destination: Destination,
)
