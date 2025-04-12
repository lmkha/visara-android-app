package com.example.visara.ui.screens.video_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.example.visara.ui.components.VideoItem
import com.example.visara.ui.components.VideoPlayerManager
import com.example.visara.ui.components.VisaraVideoPlayer
import com.example.visara.ui.screens.video_detail.components.ActionsSection
import com.example.visara.ui.screens.video_detail.components.AuthorAccountInfoSection
import com.example.visara.ui.screens.video_detail.components.ExpandedCommentSection
import com.example.visara.ui.screens.video_detail.components.MinimizedCommentSection
import com.example.visara.ui.screens.video_detail.components.MinimizedModeControl
import com.example.visara.ui.screens.video_detail.components.VideoHeaderSection
import com.example.visara.ui.theme.LocalVisaraCustomColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun VideoDetailScreen(
    modifier: Modifier = Modifier,
    videoPlayerManager: VideoPlayerManager,
    isFullScreenMode: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onClose: () -> Unit,
    onEnableFullScreenMode: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val thresholdDp: Dp = 300.dp
    val defaultHeight = 600.dp
    val offsetY = remember { Animatable(0f) }
    val thresholdPx: Float = with(LocalDensity.current) { thresholdDp.toPx() }
    val defaultHeightPx = with(LocalDensity.current) { defaultHeight.toPx() }
    var dynamicHeight = defaultHeight - with(LocalDensity.current) { offsetY.value.toDp() }
    val columnState = rememberLazyListState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0 && columnState.firstVisibleItemIndex == 0 && columnState.firstVisibleItemScrollOffset == 0) {
                    // User is pulling down from top -> consume and apply to offsetY
                    scope.launch {
                        offsetY.snapTo((offsetY.value + available.y).coerceAtLeast(0f))
                    }
                    return Offset(x = 0f, y = available.y) // consume vertical
                }
                return Offset.Zero // let LazyColumn handle normally
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (offsetY.value > thresholdPx) {
                    offsetY.animateTo(thresholdPx)
                } else {
                    offsetY.animateTo(0f)
                }
                return super.onPostFling(consumed, available)
            }
        }
    }
    var isOpenExpandedCommentSection by remember { mutableStateOf(false) }

    BackHandler(enabled = isOpenExpandedCommentSection) {
        isOpenExpandedCommentSection = false
    }

    Box(modifier = modifier.background(color = Color.Black)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Video
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                if (isFullScreenMode) {
                    VisaraVideoPlayer(
                        videoPlayerManager = videoPlayerManager,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    VisaraVideoPlayer(
                        videoPlayerManager = videoPlayerManager,
                        showControls = false,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    MinimizedModeControl(
                        isPlaying = isPlaying,
                        onPlay = onPlay,
                        onPause = onPause,
                        onClose = onClose,
                        onEnableFullScreenMode = onEnableFullScreenMode,
                    )
                }
            }
            // Info
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, offsetY.value.roundToInt()) }
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                LazyColumn(
                    state = columnState,
                    modifier = Modifier
                        .nestedScroll(nestedScrollConnection)
//                        .height(dynamicHeight)
                        .height(if (isFullScreenMode) dynamicHeight else 0.dp)
                        .fillMaxWidth()
                        .padding(8.dp)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                scope.launch {
                                    offsetY.snapTo((offsetY.value + delta).coerceAtLeast(0f))
                                }
                            },
                            onDragStopped = { velocity ->
                                scope.launch {
                                    if (offsetY.value > thresholdPx) {
                                        offsetY.animateTo(defaultHeightPx)
                                    } else {
                                        offsetY.animateTo(0f)
                                    }
                                }
                            }
                        ),
                    horizontalAlignment = Alignment.Start,
                ) {
                    // Title, description
                    item {
                        VideoHeaderSection()
                    }
                    // Author account info, subscribe button
                    item {
                        AuthorAccountInfoSection()
                    }
                    // Actions: like, share, download, save, report
                    item {
                        ActionsSection()
                    }
                    // Comment
                    item {
                        MinimizedCommentSection(
                            onClick = { isOpenExpandedCommentSection = true }
                        )
                    }
                    // Recommend videos
                    items(5) {
                        VideoItem(
                            onVideoSelect = {},
                            modifier = Modifier
                                .fillMaxWidth(),
                            videoHeight = 200.dp,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Column(modifier = Modifier.background(color = Color.Transparent)) {
                    AnimatedVisibility(
                        visible = isOpenExpandedCommentSection,
                        enter = slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight },
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { fullHeight -> fullHeight },
                            animationSpec = tween(durationMillis = 200)
                        ),
                    ) {
                        ExpandedCommentSection(
                            modifier = Modifier
                                .height(defaultHeight)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(color = LocalVisaraCustomColors.current.expandedCommentSectionBackground)
                                // Prevent unexpected drag down
                                .draggable(
                                    state = rememberDraggableState {  },
                                    orientation = Orientation.Vertical,
                                )
                            ,
                            onClose = { isOpenExpandedCommentSection = false }
                        )
                    }
                }
            }
        }
    }
}
