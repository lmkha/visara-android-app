package com.example.visara.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.R
import com.example.visara.ui.App
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.theme.AppTheme
import com.example.visara.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
   viewModel: SettingsViewModel,
    onBack: () -> Unit,
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
                    onItemSelected = {}
                )
            }
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
                    onClick = {},
                )
                SettingsItem(
                    icon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                    label = "Privacy",
                    onClick = {},
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.security_24px), contentDescription = null) },
                    label = "Security and permissions",
                    onClick = {},
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.forward_24px), contentDescription = null) },
                    label = "Share profile",
                    onClick = {},
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
                    onClick = {},
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.dark_mode_24px), contentDescription = null) },
                    label = "Theme",
                    onClick = { onItemSelected(ContentDisplaySettings.Theme) },
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.security_24px), contentDescription = null) },
                    label = "Language",
                    onClick = {},
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
                    onClick = {},
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.logout_24px), contentDescription = null) },
                    trailIcon = { UserAvatar(modifier = Modifier.size(24.dp)) },
                    label = "Logout",
                    onClick = {},
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
        modifier = Modifier
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
    Account, Privacy, SecurityAndPermission
}

enum class ContentDisplaySettings : SettingItem {
    Notification, Language, Theme
}

enum class LoginSettings : SettingItem {
    SwitchAccount, Logout
}
