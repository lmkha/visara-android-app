package com.example.visara.ui.app

import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.navigation.Destination
import com.example.visara.viewmodels.AppState

@Composable
fun BottomNavBar(
    activeDestination: Destination,
    appState: AppState,
    onNavigate: (Destination) -> Unit
) {
    val botNavItems = listOf(
        BottomNavigationItemData("Home", Icons.Filled.Home, Destination.Main.Home),
        BottomNavigationItemData("Following", Icons.Filled.Star, Destination.Main.FollowingFeed),
        BottomNavigationItemData("Add", Icons.Filled.AddCircle, Destination.Main.AddNewVideo()),
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
