package com.example.visara.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.R
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.theme.AppTheme
import com.example.visara.ui.theme.LocalVisaraCustomColors
import com.example.visara.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateAfterLogout: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedItem by remember { mutableStateOf<SettingItem?>(null) }

    BackHandler(selectedItem != null) {
        selectedItem = null
    }

    Box(modifier = modifier) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Settings and privacy",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    )
                )
            }
        ) { innerPadding->
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                ,
            ) {
                AccountSettingsSection(
                    onItemSelected = {}
                )
                ContentAndDisplaySettingsSection(
                    onItemSelected = { selectedItem = it }
                )
                LoginSettingsSection(
                    onItemSelected = { selectedItem = it }
                )
            }
        }

        ThemeSettingScreen(
            onBack = { selectedItem = null },
            isOpen = selectedItem == ContentDisplaySettings.Theme,
            currentTheme = uiState.theme,
            onSelected = { theme ->
                viewModel.setTheme(theme)
            },
        )

        LogoutBottomSheet(
            displayBottomSheet = selectedItem == LoginSettings.Logout,
            onClose = { selectedItem = null },
            onLogoutSelected = {
                viewModel.logout()
                navigateAfterLogout()
            },
            onSwitchAccountSelected = {},
        )
    }
}

@Composable
fun AccountSettingsSection(
    onItemSelected: (item: SettingItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Account",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Box(modifier = Modifier) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                SettingsItem(
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    label = "Account",
                    onClick = { onItemSelected(AccountSettings.Account) },
                )
                SettingsItem(
                    icon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                    label = "Privacy",
                    onClick = { onItemSelected(AccountSettings.Privacy) },
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.security_24px), contentDescription = null) },
                    label = "Security and permissions",
                    onClick = { onItemSelected(AccountSettings.SecurityAndPermission) },
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.forward_24px), contentDescription = null) },
                    label = "Share profile",
                    onClick = { onItemSelected(AccountSettings.ShareProfile)},
                )
            }
        }
    }
}

@Composable
fun ContentAndDisplaySettingsSection(
    onItemSelected: (item: SettingItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Content and display",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Box(modifier = Modifier) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                SettingsItem(
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    label = "Notification",
                    onClick = { onItemSelected(ContentDisplaySettings.Notification) },
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.dark_mode_24px), contentDescription = null) },
                    label = "Theme",
                    onClick = { onItemSelected(ContentDisplaySettings.Theme) },
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.security_24px), contentDescription = null) },
                    label = "Language",
                    onClick = { onItemSelected(ContentDisplaySettings.Language) },
                )
            }
        }
    }
}

@Composable
fun LoginSettingsSection(
    onItemSelected: (item: SettingItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Login",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Box(modifier = Modifier) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.published_with_changes_24px), contentDescription = null) },
                    label = "Switch account",
                    onClick = { onItemSelected(LoginSettings.SwitchAccount) },
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.logout_24px), contentDescription = null) },
                    trailIcon = { UserAvatar(modifier = Modifier.size(24.dp)) },
                    label = "Logout",
                    onClick = { onItemSelected(LoginSettings.Logout) },
                )
            }
        }
    }
}

@Composable
private fun SettingsItem(
    modifier: Modifier = Modifier,
    icon: @Composable ()  -> Unit,
    trailIcon: @Composable () -> Unit = {},
    label: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
        ,
    ) {
        icon()
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )

        Row {
            trailIcon()
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSettingScreen(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    onBack: () -> Unit,
    currentTheme: AppTheme,
    onSelected: (theme: AppTheme) -> Unit,
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(durationMillis = 300)
        ),
    ) {
        Scaffold(
            modifier = modifier.background(color = MaterialTheme.colorScheme.background),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Display",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    Text(
                        text = "Appearance",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    AppTheme.entries.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelected(option) }
                        ) {
                            RadioButton(
                                selected = option == currentTheme,
                                onClick = {
                                    onSelected(option)
                                }
                            )
                            Text(
                                text = when(option) {
                                    AppTheme.LIGHT -> "Light"
                                    AppTheme.SYSTEM -> "System"
                                    AppTheme.DARK -> "Dark"
                                },
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }
    }
}


sealed interface SettingItem

enum class AccountSettings : SettingItem {
    Account, Privacy, SecurityAndPermission, ShareProfile
}

enum class ContentDisplaySettings : SettingItem {
    Notification, Language, Theme
}

enum class LoginSettings : SettingItem {
    SwitchAccount, Logout
}

@Composable
private fun LogoutBottomSheet(
    displayBottomSheet: Boolean,
    onClose: () -> Unit,
    onLogoutSelected: () -> Unit,
    onSwitchAccountSelected: () -> Unit,
) {
    Box {
        val backgroundAlpha by animateFloatAsState(
            targetValue = if (displayBottomSheet) 0.5f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "backgroundAlpha"
        )
        // Back layer
        AnimatedVisibility(
            visible = displayBottomSheet,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .zIndex(1f)
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backgroundAlpha))
                    .clickable(onClick = onClose)
            )
        }

        // Content
        AnimatedVisibility(
            visible = displayBottomSheet,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .zIndex(2f)
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(color = LocalVisaraCustomColors.current.bottomSheetBackground)
                    .clickable {}
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 16.dp,
                            bottom = 32.dp,
                        )
                    ,
                ) {
                    Text(
                        text = "Are you sure you want to log out?",
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .height(70.dp)
                            .fillMaxWidth()
                            .clickable(onClick = onSwitchAccountSelected)
                    ) {
                        Text(
                            text = "Switch account",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)
                    Box(
                        modifier = Modifier
                            .height(70.dp)
                            .fillMaxWidth()
                            .clickable(onClick = onLogoutSelected)
                    ) {
                        Text(
                            text = "Logout",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)
                    Box(
                        modifier = Modifier
                            .height(70.dp)
                            .fillMaxWidth()
                            .clickable(onClick = onClose)
                    ) {
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
