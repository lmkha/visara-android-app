package com.example.visara.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    isMyProfileRequested: Boolean,
    username: String?,
    bottomNavBar: @Composable () -> Unit,
    onBack: () -> Unit,
    onNavigateToFollowScreen: (startedTabIndex: Int) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToSettingsScreen: () -> Unit,
    onNavigateToStudioScreen: () -> Unit,
    onNavigateToQRCodeScreen: () -> Unit,
    onNavigateToAddNewVideoScreen: () -> Unit,
) {
    LaunchedEffect(isMyProfileRequested, username) {
        viewModel.setProfile(isMyProfileRequested, username)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.isLoading) {
        if (uiState.isMyProfileRequested && !uiState.isAuthenticated) {
            UnauthenticatedProfileContainer(
                bottomNavBar = bottomNavBar,
                onBack = onBack,
                onNavigateToLoginScreen = onNavigateToLoginScreen,
                onNavigateToSettingsScreen = onNavigateToSettingsScreen,
            )
        } else {
            ProfileScreenContainer(
                modifier = modifier,
                uiState = uiState,
                bottomNavBar = bottomNavBar,
                onBack = onBack,
                onNavigateToFollowScreen = onNavigateToFollowScreen,
                onNavigateToSettingsScreen = onNavigateToSettingsScreen,
                onNavigateToStudioScreen = onNavigateToStudioScreen,
                onNavigateToQRCodeScreen = onNavigateToQRCodeScreen,
                onNavigateToLoginScreen = onNavigateToLoginScreen,
                onNavigateToAddNewVideoScreen = onNavigateToAddNewVideoScreen,
                onVideoSelected = { video -> viewModel.selectVideo(video) },
                follow = viewModel::follow,
                unfollow = viewModel::unfollow,
            )
        }
    }
}
