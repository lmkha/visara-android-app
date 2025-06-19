package com.example.visara.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.ui.screens.settings.components.AccountSettings
import com.example.visara.ui.screens.settings.components.AccountSettingsSection
import com.example.visara.ui.screens.settings.components.ContentAndDisplaySettingsSection
import com.example.visara.ui.screens.settings.components.ContentDisplaySettings
import com.example.visara.ui.screens.settings.components.LanguageSettingScreen
import com.example.visara.ui.screens.settings.components.LoginSettings
import com.example.visara.ui.screens.settings.components.LoginSettingsSection
import com.example.visara.ui.screens.settings.components.LogoutBottomSheet
import com.example.visara.ui.screens.settings.components.PrivacySettingScreen
import com.example.visara.ui.screens.settings.components.SettingItem
import com.example.visara.ui.screens.settings.components.ThemeSettingScreen
import com.example.visara.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToLoginScreen: () -> Unit,
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
                if (uiState.isAuthenticated) {
                    AccountSettingsSection(
                        onItemSelected = {selectedItem = it }
                    )
                }

                ContentAndDisplaySettingsSection(
                    onItemSelected = { selectedItem = it }
                )

                LoginSettingsSection(
                    isAuthenticated = uiState.isAuthenticated,
                    currentUser = uiState.currentUser,
                    onItemSelected = { selectedItem = it },
                    onLoginSelected = navigateToLoginScreen,
                )
            }
        }

        PrivacySettingScreen(
            onBack = { selectedItem = null },
            isOpen = selectedItem == AccountSettings.Privacy,
            isPrivate = uiState.currentUser?.isPrivate == true,
            onIsPrivateChange = { viewModel.changePrivacy(it) },
        )

        ThemeSettingScreen(
            onBack = { selectedItem = null },
            isOpen = selectedItem == ContentDisplaySettings.Theme,
            currentTheme = uiState.theme,
            onSelected = { theme ->
                viewModel.setTheme(theme)
            },
        )


        LanguageSettingScreen(
            onBack = { selectedItem = null },
            isOpen = selectedItem == ContentDisplaySettings.Language,
            currentLanguage = "vn",
            allLanguages = listOf("vn", "eng")
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
