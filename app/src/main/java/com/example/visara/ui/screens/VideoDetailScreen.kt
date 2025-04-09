package com.example.visara.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.visara.R
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.components.VideoItem
import com.example.visara.ui.components.VideoPlayerDash
import com.example.visara.ui.components.VideoPlayerManager
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun VideoDetailScreen(
    modifier: Modifier = Modifier,
    isDisplay: Boolean = false,
    videoPlayerManager: VideoPlayerManager,
) {
    val scope = rememberCoroutineScope()
    val thresholdDp: Dp = 300.dp
    val thresholdPx: Float = with(LocalDensity.current) { thresholdDp.toPx() }
    val offsetY = remember { Animatable(0f) }
    val defaultHeight = 600.dp
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

    LaunchedEffect(isDisplay) {
        videoPlayerManager.exoPlayer.play()
    }

    Box(
        modifier = modifier
            .background(color = Color.Black)
    ) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // Video
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isDisplay) {
                    VideoPlayerDash(
                        videoPlayerManager = videoPlayerManager,
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
                        .height(dynamicHeight)
                        .fillMaxWidth()
                        .padding(4.dp)
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
//                                    offsetY.animateTo(maxOffsetPx)
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

                Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
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
                                .background(color = MaterialTheme.colorScheme.surface)
                            ,
                            onClose = { isOpenExpandedCommentSection = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoHeaderSection() {
    Column(
        modifier = Modifier
            .clickable(onClick = {

            })
    ) {
        Text(
            text = "FC Barcelona 1 vs 1 Betis | Laliga 2024/25 MD30",
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("22,007 views")
            Text("8h ago")
            Text("#BLVAnhQuan")
            Text("...more")
        }

    }
    Spacer(Modifier.height(16.dp).fillMaxWidth())
}

@Composable
private fun AuthorAccountInfoSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UserAvatar(modifier = Modifier.size(40.dp))
            Text(
                text = "BLV Anh Quân",
                fontWeight = FontWeight.W500
            )
            Text(
                text = "1.38M",
                fontWeight = FontWeight.Normal
            )
        }

        FilledTonalButton(
            onClick = {},
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        ) {
            Text("Subscribe")
        }
    }
    Spacer(Modifier.height(16.dp).fillMaxWidth())
}

@Composable
private fun ActionsSection() {
    var liked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Like
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = {
                    liked = !liked
                },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(id = if (liked) R.drawable.heart_filled_24px else R.drawable.heart_outlined_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "19K",
                )
            }
        }
        // Row of other actions:
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            FilledTonalButton(
                onClick = {},
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                ),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Icon(Icons.Default.Share, null)
                Spacer(Modifier.width(8.dp))
                Text("Share")
            }
            FilledTonalButton(
                onClick = {},
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                ),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.download_24px),
                    contentDescription = null,
                )
                Spacer(Modifier.width(8.dp))
                Text("Download")
            }
            FilledTonalButton(
                onClick = {},
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                ),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Icon(Icons.Default.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text("Save")
            }
            FilledTonalButton(
                onClick = {},
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                ),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.flag_24px),
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text("Report")
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun MinimizedCommentSection(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = "Comments",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                UserAvatar(modifier = Modifier.size(36.dp))
                Text(
                    text = "bài hát khá là hay giai điệu khá là tuyệt vời một bài hát truyền cảm hứng tới dân bay phòng bay bar, một bài nhạc rất thích hợp với kẹo và keee, ông nào chơi vào mà nghe bài này chắc lên tận cung trăng. Nói chung là nhạc siêu hay",
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
fun ExpandedCommentSection(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "6.778 comments",
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        LazyColumn {
            item {
                ParentCommentItem()
            }
        }

    }
}

@Composable
fun ParentCommentItem(
    modifier: Modifier = Modifier,
    childCount: Int = 3,
) {
    var liked by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserAvatar(modifier = Modifier.size(32.dp))
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "lmkha",
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "7h ago",
                    color = Color.LightGray
                )
            }
            Text(
                text = "This is most interesting match I had seen!",
                fontWeight = FontWeight.Normal
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable(onClick = { liked = !liked })
                ) {
                    Icon(
                        painter = painterResource(id = if (liked) R.drawable.heart_filled_24px else R.drawable.heart_outlined_24px),
                        contentDescription = null,
                        tint = if (liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "9",
                    )
                }

                Text(
                    text = "Reply",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = {})
                    ,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun ChildCommentItem(
    modifier: Modifier = Modifier,
) {

}
