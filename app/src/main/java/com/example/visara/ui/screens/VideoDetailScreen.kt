package com.example.visara.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.example.visara.R
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.components.VideoItem
import com.example.visara.ui.components.VideoPlayerDash
import com.example.visara.ui.components.VideoPlayerManager
import com.example.visara.ui.theme.LocalVisaraCustomColors
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

    LaunchedEffect(isDisplay) {
        videoPlayerManager.exoPlayer.play()
    }

    Box(modifier = modifier.background(color = Color.Black)) {
        Column(modifier = modifier.fillMaxSize()) {
            // Video
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
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

@Composable
private fun VideoHeaderSection() {
    Column(
        modifier = Modifier
            .clickable {

            }
        ,
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
        modifier = Modifier.fillMaxWidth(),
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Like
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                .horizontalScroll(rememberScrollState())
            ,
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
        ,
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
private fun ExpandedCommentSection(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val headerHeight = 50.dp
    val commentInputHeight = 100.dp
    Box(
        modifier = modifier.imePadding()) {
        Box(
            modifier = Modifier
                .height(headerHeight)
                .fillMaxWidth()
                .padding(4.dp)
            ,
        ) {
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
        LazyColumn(
            modifier = Modifier
                .padding(
                    top = headerHeight,
                    bottom = commentInputHeight,
                    start = 8.dp,
                    end = 8.dp
                )
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(8) {
                ParentCommentItem()
            }
        }
        CommentInput(
            modifier = Modifier
                .height(commentInputHeight)
                .fillMaxWidth()
                .background(color = LocalVisaraCustomColors.current.expandedCommentSectionBackground)
                .align(Alignment.BottomStart)
            ,
        )
    }
}

@Composable
private fun ParentCommentItem(
    modifier: Modifier = Modifier,
    childCount: Int = 3,
) {
    var liked by remember { mutableStateOf(false) }
    var openReplies by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UserAvatar(modifier = Modifier.size(40.dp))
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "lmkha",
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                    )

                    Text(
                        text = "-",
                        color = Color.Gray,
                    )

                    Text(
                        text = "7h ago",
                        color = Color.Gray,
                    )
                }
                Text(
                    text = "This is most interesting match I had seen!",
                    fontWeight = FontWeight.Normal,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clickable { liked = !liked }
                        ,
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
                            .clickable{ }
                        ,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = openReplies,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 100)),
        ) {
            Column(modifier = Modifier.padding(start = 40.dp)) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 0 until childCount) {
                        ChildCommentItem()
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 32.dp, top = 4.dp),
        ) {
            HorizontalDivider(modifier = Modifier.width(32.dp))
            if (!openReplies) {
                Row(
                    modifier = Modifier
                        .clickable {
                            openReplies = true
                        }
                    ,
                ) {
                    Text("See more 5 replies")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                    )
                }
            }
            if (openReplies) {
                Row(
                    modifier = Modifier
                        .clickable {
                            openReplies = false
                        }
                    ,
                ) {
                    Text("Hide")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun ChildCommentItem(
    modifier: Modifier = Modifier,
) {
    var liked by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserAvatar(modifier = Modifier.size(32.dp))
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "lmkha",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                )

                Text(
                    text = "-",
                    color = Color.Gray,
                )

                Text(
                    text = "7h ago",
                    color = Color.Gray,
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
                        .clickable { liked = !liked }
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
private fun CommentInput(
    modifier: Modifier = Modifier,
) {
    var content by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = 8.dp,
                )
        ) {
            UserAvatar(modifier = Modifier.size(56.dp))
            TextField(
                value = content,
                onValueChange = { content = it },
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.LightGray,
                    focusedContainerColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                ),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(30.dp)),
                trailingIcon = {
                    Row {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(id = R.drawable.at_24px),
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }
                        if (content.isNotEmpty()) {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .background(
                                        if (content.isNotEmpty()) MaterialTheme.colorScheme.primary
                                        else Color.LightGray
                                    )
                                ,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_upward_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}
