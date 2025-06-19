package com.example.visara.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.data.model.VideoModel
import com.example.visara.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    bottomNavBar: @Composable () -> Unit,
    onBack: () -> Unit,
    onNavigateToFollowScreen: (startedTabIndex: Int) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToSettingsScreen: () -> Unit,
    onNavigateToStudioScreen: () -> Unit,
    onNavigateToQRCodeScreen: () -> Unit,
    onNavigateToAddNewVideoScreen: () -> Unit,
    onNavigateToEditVideoScreen: (video: VideoModel) -> Unit,
) {
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
                uiEvent = viewModel.eventFlow,
                bottomNavBar = bottomNavBar,
                onBack = onBack,
                onNavigateToFollowScreen = onNavigateToFollowScreen,
                onNavigateToSettingsScreen = onNavigateToSettingsScreen,
                onNavigateToStudioScreen = onNavigateToStudioScreen,
                onNavigateToQRCodeScreen = onNavigateToQRCodeScreen,
                onNavigateToLoginScreen = onNavigateToLoginScreen,
                onNavigateToAddNewVideoScreen = onNavigateToAddNewVideoScreen,
                onNavigateToEditVideoScreen = onNavigateToEditVideoScreen,
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
