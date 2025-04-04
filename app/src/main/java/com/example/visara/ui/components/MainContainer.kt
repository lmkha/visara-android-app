package com.example.visara.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.visara.ui.CheckScreen
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.HomeScreen
import com.example.visara.ui.screens.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    authenticated: Boolean = false,
    navigateToLogin: () -> Unit,
    navigateToSearch: () -> Unit,
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
    val suggestionTags = listOf("All", "football", "barca", "champion league", "music", "hiphop", "rap", "information technology")

    LaunchedEffect(currentRoute) {
        currentRoute?.let {
            scrollBehavior.state.contentOffset = 0f
            scrollBehavior.state.heightOffset = 0f
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("Visara")
                    },
                    actions = {
                        IconButton(onClick = navigateToSearch) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "",
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )

                SuggestionTag(
                    tags = suggestionTags,
                    modifier = if (currentRoute == Destination.Main.Home.toString() && scrollBehavior.state.collapsedFraction < 1f) {
                        Modifier.wrapContentHeight()
                    } else {
                        Modifier.height(0.dp)
                    }

                )
            }
        },
        bottomBar = {
            NavigationBar {
                botNavItems.forEach { item->
                    NavigationBarItem(
                        label = { Text(item.label) },
                        selected = currentRoute == item.destination.toString(),
                        onClick = { navController.navigate(item.destination) },
                        icon = { Icon(imageVector = item.icon, contentDescription =  item.label)}
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Main.Home,
            modifier = Modifier.padding(padding)
        ) {
            composable<Destination.Main.Home> { HomeScreen() }
            composable<Destination.Main.Following> { CheckScreen("Following Screen") }
            composable<Destination.Main.AddNewVideo> { CheckScreen("Add new video Screen") }
            composable<Destination.Main.Mail> { CheckScreen("Mail Screen") }
            composable<Destination.Main.Profile> { ProfileScreen(authenticated = authenticated, navigateToLogin = navigateToLogin) }
        }
    }
}

data class BottomNavigationItemData (
    val label: String,
    val icon: ImageVector,
    val destination: Destination,
)

@Composable
fun SuggestionTag(tags: List<String>, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags.size) {index->
            SuggestionChip(
                onClick = {},
                label = { Text(tags[index]) }
            )
        }
    }
}
