package com.example.visara.ui.app

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.navigation.Destination
import com.example.visara.viewmodels.AppState

@Composable
fun BottomNavBar(
    activeDestination: Destination,
    appState: AppState,
    onNavigate: (Destination) -> Unit
) {
    NavigationBar(containerColor = Color.Transparent) {
        BotNavItems.items.forEach { item ->
            val label = stringResource(id = item.stringRes)
            NavigationBarItem(
                label = { Text(label) },
                selected = item.destination.route == activeDestination.route,
                onClick = { onNavigate(item.destination) },
                icon = {
                    if (item.destination.route != Destination.Main.Profile().route) {
                        Icon(imageVector = item.icon, contentDescription = label)
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

private object BotNavItems {
    val items: List<BottomNavigationItemData> = listOf(
        BottomNavigationItemData(R.string.home, Icons.Filled.Home, Destination.Main.Home),
        BottomNavigationItemData(R.string.following_nav_label, Icons.Filled.Star, Destination.Main.FollowingFeed),
        BottomNavigationItemData(R.string.add_video_bot_nav_label, Icons.Filled.AddCircle, Destination.Main.AddNewVideo()),
        BottomNavigationItemData(R.string.inbox, Icons.Filled.Email, Destination.Main.Inbox),
        BottomNavigationItemData(R.string.profile, Icons.Filled.AccountCircle, Destination.Main.Profile(shouldNavigateToMyProfile = true)),
    )
}

private data class BottomNavigationItemData (
    @param:StringRes val stringRes: Int,
    val icon: ImageVector,
    val destination: Destination,
)
