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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.visara.R
import com.example.visara.ui.Destination
import com.example.visara.ui.components.UserAvatar


/**
 * Composable function for the application's bottom navigation bar.
 * This bar is conditionally displayed based on the current navigation destination.
 *
 * **Reference:** This implementation pattern for handling bottom navigation visibility
 * is inspired by the AndroidX Navigation Compose demos, specifically:
 * [BottomBarNavDemo.kt](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-compose/integration-tests/navigation-demos/src/main/java/androidx/navigation/compose/demos/BottomBarNavDemo.kt;l=64)
 *
 * @param currentNavBackstackEntry The [NavBackStackEntry] representing the current screen's entry
 * in the navigation stack. This is typically obtained from
 * `navController.currentBackStackEntryAsState().value`.
 * @param currentUserAvatarUrl The URL for the current user's avatar, used for the profile icon. Can be null.
 * @param onNavigate A lambda function to handle navigation actions when a bottom navigation item is clicked.
 */
@Composable
fun BottomNavBar(
    currentNavBackstackEntry: NavBackStackEntry?,
    currentUserAvatarUrl: String?,
    onNavigate: (Destination) -> Unit
) {

//    val navBackstackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = currentNavBackstackEntry?.destination

    val shouldShowBottomBar = currentDestination?.hierarchy?.any { navDestination ->
        requiredDisplayBotNavBarDestinations.any { destination -> navDestination.hasRoute(destination::class) }
    } == true

    if (shouldShowBottomBar) {
        NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
            bottomNavItems.forEach { item ->
                val label = stringResource(id = item.stringRes)
                NavigationBarItem(
                    label = { Text(label) },
                    selected = currentDestination.hierarchy.any { it.hasRoute(item.destination::class) },
                    onClick = {
                        onNavigate(item.destination)
                    },
                    icon = {
                        if (item.destination.route != Destination.Main.Profile(null).route) {
                            Icon(imageVector = item.icon, contentDescription = label)
                        } else {
                            UserAvatar(
                                avatarLink = currentUserAvatarUrl,
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
}

/**
 * A list of [Destination] objects that, if the current navigation destination matches any of them,
 * will cause the [BottomNavBar] to be displayed. Destinations not in this list will hide the bar.
 */
private val requiredDisplayBotNavBarDestinations: List<Destination> = listOf(
    Destination.Main.Home,
    Destination.Main.FollowingFeed,
    Destination.Main.Inbox,
    Destination.Main.Profile(null),
)

/**
 * The static list of items to be displayed in the [BottomNavBar].
 * Each item defines its display text, icon, and the [Destination] it navigates to.
 */
private val bottomNavItems: List<BottomNavigationItemData> = listOf(
    BottomNavigationItemData(R.string.home, Icons.Filled.Home, Destination.Main.Home),
    BottomNavigationItemData(R.string.following_nav_label, Icons.Filled.Star, Destination.Main.FollowingFeed),
    BottomNavigationItemData(R.string.add_video_bot_nav_label, Icons.Filled.AddCircle, Destination.Main.AddNewVideo()),
    BottomNavigationItemData(R.string.inbox, Icons.Filled.Email, Destination.Main.Inbox),
    BottomNavigationItemData(R.string.profile, Icons.Filled.AccountCircle, Destination.Main.Profile(null, shouldNavigateToMyProfile = true)),
)

/**
 * Data class representing a single item in the bottom navigation bar.
 *
 * @property stringRes The string resource ID for the item's label.
 * @property icon The [ImageVector] for the item's icon.
 * @property destination The [Destination] associated with this navigation item.
 */
private data class BottomNavigationItemData (
    @param:StringRes val stringRes: Int,
    val icon: ImageVector,
    val destination: Destination,
)
