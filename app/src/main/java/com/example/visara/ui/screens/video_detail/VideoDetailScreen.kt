package com.example.visara.ui.screens.video_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.ui.components.LoginRequestDialog
import com.example.visara.ui.components.VideoItem
import com.example.visara.ui.components.VisaraVideoPlayer
import com.example.visara.ui.components.rememberLoginRequestDialogState
import com.example.visara.ui.screens.video_detail.components.ActionsSection
import com.example.visara.ui.screens.video_detail.components.AuthorAccountInfoSection
import com.example.visara.ui.screens.video_detail.components.ExpandedCommentSection
import com.example.visara.ui.screens.video_detail.components.MinimizedCommentSection
import com.example.visara.ui.screens.video_detail.components.MinimizedModeControl
import com.example.visara.ui.screens.video_detail.components.VideoHeaderSection
import com.example.visara.ui.theme.LocalVisaraCustomColors
import com.example.visara.viewmodels.VideoDetailScreenEvent
import com.example.visara.viewmodels.VideoDetailViewModel

@Composable
fun VideoDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoDetailViewModel,
    isFullScreenMode: Boolean,
    isLandscapeMode: Boolean,
    requireLandscapeMode: () -> Unit,
    requirePortraitMode: () -> Unit,
    onNavigateToProfileScreen: (username: String) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentMediaController by viewModel.player.collectAsStateWithLifecycle()
    var liked by remember(uiState.isVideoLiked) {
        mutableStateOf(uiState.isVideoLiked)
    }
    var likeCount by remember(uiState.video?.likesCount) {
        mutableLongStateOf(uiState.video?.likesCount ?: 0L)
    }
    val columnState = rememberLazyListState()
    val loginRequestDialogState = rememberLoginRequestDialogState()
    var reloadKey by remember { mutableIntStateOf(0) }

    if (uiState.isOpenExpandedCommentSection) {
        BackHandler {
            viewModel.minimizeCommentSection()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is VideoDetailScreenEvent.RequireReloadPlayer -> {
                    reloadKey = reloadKey + 1
                }
            }
        }
    }

    BoxWithConstraints(modifier = modifier.background(color = Color.Black)) {
        val screenHeight = this.maxHeight
        val screenWidth = this.maxWidth
        val playerHeight = screenWidth * (9f / 16f)
        val remainingHeight = screenHeight - playerHeight
        Column(modifier = Modifier.fillMaxSize()) {
            // Video
            if (currentMediaController != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(playerHeight)
                        .aspectRatio(16f / 9f)
                ) {
                    key(reloadKey) {
                        VisaraVideoPlayer(
                            mediaController = currentMediaController!!,
                            showControls = uiState.isFullScreenMode,
                            requireLandscapeMode = requireLandscapeMode,
                            requirePortraitMode = requirePortraitMode,
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(16f / 9f)
                        )
                    }
                    if (!isFullScreenMode) {
                        MinimizedModeControl(
                            isPlaying = uiState.isPlaying,
                            onPlay = viewModel::play,
                            onPause = viewModel::pause,
                            onClose = viewModel::close,
                            onEnableFullScreenMode = viewModel::enableFullScreenMode,
                        )
                    }
                }
            }
            // Info
            Box(modifier = Modifier
                .height(if (!isFullScreenMode || isLandscapeMode) 0.dp else remainingHeight)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.background)) {
                LazyColumn(
                    state = columnState,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    // Title, description
                    item {
                        VideoHeaderSection(
                            title = uiState.video?.title ?: "",
                            createdAt = uiState.video?.createdAt ?: "",
                            viewsCount = uiState.video?.viewsCount ?: 0L,
                        )
                    }
                    // Author account info, follow button
                    item {
                        AuthorAccountInfoSection(
                            author = uiState.author,
                            isFollowing = uiState.isFollowing,
                            currentUser = uiState.currentUser,
                            onAuthorClick = {
                                viewModel.enableMinimizedMode()
                                uiState.author?.username?.let { onNavigateToProfileScreen(it) }
                            },
                            onFollowUser = { onFailure ->
                                if (uiState.isUserAuthenticated) {
                                    viewModel.followAuthor(onFailure)
                                } else {
                                    onFailure()
                                    loginRequestDialogState.show("Please log in to follow this user.")
                                }
                            },
                            onUnfollowUser = { onFailure ->
                                if (uiState.isUserAuthenticated) {
                                    viewModel.unfollowAuthor(onFailure)
                                } else {
                                    onFailure()
                                    loginRequestDialogState.show("Please log in and follow this user first.")
                                }
                            },
                        )
                    }
                    // Actions: like, share, download, save, report
                    item {
                        ActionsSection(
                            liked = liked,
                            likeCount = likeCount,
                            likeClick = {
                                if (uiState.isUserAuthenticated) {
                                    liked = !liked // change state first
                                    likeCount = if (liked) likeCount + 1
                                    else likeCount - 1
                                    viewModel.changeVideoLike(
                                        current = !liked, // pass !liked because has been changed above, before call this function
                                        onFailure = { liked = !liked }
                                    )
                                } else {
                                    loginRequestDialogState.show("You need to log in to like this video!")
                                }
                            }
                        )
                    }
                    // Comment
                    item {
                        MinimizedCommentSection(
                            isCommentOff = uiState.video?.isCommentOff == true,
                            commentsCount = uiState.video?.commentsCount ?: 0L,
                            coverComment = uiState.commentList.firstOrNull()?.comment,
                            onClick = {
                                if (uiState.video?.isCommentOff == false) {
                                    viewModel.expandCommentSection()
                                }
                            }
                        )
                    }
                    // Recommend videos
                    items(uiState.recommendedVideos) { recommendedVideo ->
                        VideoItem(
                            state = recommendedVideo,
                            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                            onVideoSelect = {
                                viewModel.selectRecommendedVideo(recommendedVideo)
                            },
                            onAuthorSelected = {
                                onNavigateToProfileScreen(recommendedVideo.username)
                            }
                        )
                    }
                }

                // Expanded Comment section: Wrap content in column to use slideInVertically and slideOutVertically
                Column(modifier = Modifier.background(color = Color.Transparent)) {
                    AnimatedVisibility(
                        visible = uiState.isOpenExpandedCommentSection,
                        enter = slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight },
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { fullHeight -> fullHeight },
                            animationSpec = tween(durationMillis = 200)
                        ),
                    ) {
                        ExpandedCommentSection(
                            commentList = uiState.commentList,
                            currentUser = uiState.currentUser,
                            onClose = { viewModel.minimizeCommentSection() },
                            onFetchReplies = { parentIndex ->
                                viewModel.fetchChildrenComment(
                                    parentIndex
                                )
                            },
                            onLikeComment = { commentId: String,
                                              current: Boolean,
                                              onImplementImmediateWhenAuthenticated: () -> Unit,
                                              onFailure: () -> Unit
                                ->
                                if (uiState.isUserAuthenticated) {
                                    onImplementImmediateWhenAuthenticated()
                                    viewModel.changeCommentLike(
                                        current = current,
                                        commentId = commentId,
                                        onFailure = onFailure
                                    )
                                } else {
                                    loginRequestDialogState.show("You need to log in to like this comment!")
                                }
                            },
                            addComment = { content, parentId, parentIndex ->
                                if (uiState.isUserAuthenticated) {
                                    viewModel.addComment(content, parentId, parentIndex)
                                } else {
                                    loginRequestDialogState.show("You need to log in to comment!")
                                }
                            },
                            modifier = Modifier
                                .height(remainingHeight)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(color = LocalVisaraCustomColors.current.expandedCommentSectionBackground)
                                // Prevent unexpected drag down
                                .draggable(
                                    state = rememberDraggableState { },
                                    orientation = Orientation.Vertical,
                                )
                        )
                    }
                }
            }

            LoginRequestDialog(
                state = loginRequestDialogState,
                onLogin = onNavigateToLoginScreen,
            )
        }
    }
}
