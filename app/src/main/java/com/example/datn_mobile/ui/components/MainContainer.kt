package com.example.datn_mobile.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.datn_mobile.ui.CheckScreen
import com.example.datn_mobile.ui.navigation.Destination
import com.example.datn_mobile.ui.screens.ProfileScreen

@SuppressLint("RestrictedApi")
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YouTube") },
                actions = {
                    Button(onClick = { navigateToSearch() }) {
                        Text("Search")
                    }
                }
            )
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
            composable<Destination.Main.Home> { CheckScreen("Home Screen") }
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
