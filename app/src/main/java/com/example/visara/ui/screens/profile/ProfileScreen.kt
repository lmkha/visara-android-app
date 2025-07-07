package com.example.visara.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.data.model.VideoModel
import com.example.visara.ui.components.BottomNavBar
import com.example.visara.ui.Destination
import com.example.visara.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onNavigateToFollowScreen: (startedTabIndex: Int) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToSettingsScreen: () -> Unit,
    onNavigateToStudioScreen: () -> Unit,
    onNavigateToQRCodeScreen: () -> Unit,
    onNavigateToAddNewVideoScreen: () -> Unit,
    onNavigateToEditVideoScreen: (video: VideoModel) -> Unit,
    onNavigateToEditProfileScreen: () -> Unit,
    profileRoute: String,
    onBotNavigate: (Destination) -> Unit,
    currentAvatarUrl: String?,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.isLoading) {
        if (uiState.isMyProfileRequested && !uiState.isAuthenticated) {
            UnauthenticatedProfileContainer(
                bottomNavBar = {
                    BottomNavBar(
                        activeRoute = profileRoute,
                        currentUserAvatarUrl = currentAvatarUrl,
                    ) {
                        onBotNavigate(it)
                    }
                },
                onBack = onBack,
                onNavigateToLoginScreen = onNavigateToLoginScreen,
                onNavigateToSettingsScreen = onNavigateToSettingsScreen,
            )
        } else {
            ProfileScreenContainer(
                modifier = modifier,
                uiState = uiState,
                uiEvent = viewModel.eventFlow,
                bottomNavBar = {
                    BottomNavBar(
                        activeRoute = profileRoute,
                        currentUserAvatarUrl = currentAvatarUrl,
                    ) {
                        onBotNavigate(it)
                    }
                },
                onBack = onBack,
                onNavigateToFollowScreen = onNavigateToFollowScreen,
                onNavigateToSettingsScreen = onNavigateToSettingsScreen,
                onNavigateToStudioScreen = onNavigateToStudioScreen,
                onNavigateToQRCodeScreen = onNavigateToQRCodeScreen,
                onNavigateToLoginScreen = onNavigateToLoginScreen,
                onNavigateToAddNewVideoScreen = onNavigateToAddNewVideoScreen,
                onNavigateToEditVideoScreen = onNavigateToEditVideoScreen,
                onNavigateToEditProfileScreen = onNavigateToEditProfileScreen,
                onVideoSelected = viewModel::selectVideo,
                follow = viewModel::follow,
                unfollow = viewModel::unfollow,
                deleteVideo = viewModel::deleteVideo,
                onAddNewPlaylist = viewModel::addNewPlaylist,
                onAddVideoToPlaylists = viewModel::addVideoToPlaylists,
            )
        }
    }
}
