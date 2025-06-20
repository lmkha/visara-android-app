package com.example.visara.ui.screens.inbox.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.screens.inbox.chat.components.GoToEndButton
import com.example.visara.ui.screens.inbox.chat.components.MessageItem
import com.example.visara.ui.screens.inbox.chat.components.MoreActionPanel
import com.example.visara.ui.screens.inbox.chat.components.ReactionsPanel
import com.example.visara.viewmodels.ChatInboxViewModel
import com.example.visara.viewmodels.MessageItem
import com.example.visara.viewmodels.MessageListItemType
import com.example.visara.viewmodels.TimeItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatInboxScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatInboxViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isUiRendered by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var inputValue by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()
    val reactionPanelHeight = 50.dp
    val inputHeight = 70.dp
    val itemVerticalPadding = 8.dp
    val showGoToEndButton by remember {
        derivedStateOf {
            if (lazyListState.layoutInfo.visibleItemsInfo.isEmpty()) return@derivedStateOf false
            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.last().index
            lastVisibleItemIndex < lazyListState.layoutInfo.totalItemsCount - 1
        }
    }
    var reactionVisibleItemIndex by remember { mutableStateOf<Int?>(null) }
    var reactionVisibleItemIntOffset by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            val lastIndex = uiState.messages.lastIndex

            if (!uiState.firstTime) {
                lazyListState.animateScrollToItem(index = lastIndex)
            } else {
                lazyListState.scrollToItem(index = lastIndex)
                isUiRendered = true
            }
        }
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (lazyListState.isScrollInProgress && reactionVisibleItemIndex != null) {
            reactionVisibleItemIndex = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier.size(50.dp)) {
                            UserAvatar(
                                modifier = Modifier.size(50.dp)
                            )
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(color = MaterialTheme.colorScheme.background)
//                                    .background(color = Color.Red)
                            ) {
                                Box(modifier = Modifier
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(color = Color.Green)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = uiState.partnerUsername,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                text = "Online 8 minutes ago",
                                fontSize = 15.sp,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        modifier = modifier,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val navigationBarPadding = WindowInsets.navigationBars
                .asPaddingValues()
                .calculateBottomPadding()
            val listHeight = this.maxHeight - inputHeight - navigationBarPadding
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                item {
                    Box {
                        LazyColumn(
                            state = lazyListState,
                            verticalArrangement = Arrangement.spacedBy(itemVerticalPadding),
                            modifier = Modifier
                                .height(listHeight)
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { reactionVisibleItemIndex = null }
                                    )
                                }
                        ) {
                            items(
                                count = uiState.messages.size,
                                contentType = { uiState.messages[it].type },
                            ) { index ->
                                if (uiState.messages[index].type == MessageListItemType.MESSAGE) {
                                    val messageItem = uiState.messages[index] as MessageItem
                                    val isCurrentMessageFromCurrentUser = viewModel.isSentByCurrentUser(uiState.messages[index])
                                    val isNextMessageFromCurrentUser = viewModel.isSentByCurrentUser(uiState.messages.getOrNull(index + 1))
                                    val shouldShowAvatar = !(isCurrentMessageFromCurrentUser || isNextMessageFromCurrentUser)

                                    MessageItem(
                                        message = messageItem.data,
                                        isMyMessage = isCurrentMessageFromCurrentUser,
                                        shouldShowAvatar = shouldShowAvatar,
                                        onShowReactionsPanel = {
                                            if (uiState.messages[index].type == MessageListItemType.MESSAGE && !isCurrentMessageFromCurrentUser) {
                                                val itemInfo =
                                                    lazyListState.layoutInfo.visibleItemsInfo.find { it.index == index }
                                                if (itemInfo != null) {
                                                    reactionVisibleItemIntOffset = itemInfo.offset
                                                }
                                                reactionVisibleItemIndex = index
                                            }
                                        },
                                        onDismissReactionsPanel = {
                                            reactionVisibleItemIndex = null
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp)
                                    )
                                } else {
                                    val timeItem = uiState.messages[index] as TimeItem
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = timeItem.data,
                                            color = Color.Gray,
                                        )
                                    }
                                }
                            }
                        }

                        // Reactions panel
                        ReactionsPanel(
                            visible = reactionVisibleItemIndex != null,
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = 30.dp.roundToPx(),
                                        y = (reactionVisibleItemIntOffset ?: 0) - (reactionPanelHeight + itemVerticalPadding).roundToPx()
                                    )
                                }
                                .height(reactionPanelHeight)
                                .width(320.dp)
                                .clickable { }
                        )

                        // Go to end of chat button
                        GoToEndButton(
                            visible = showGoToEndButton,
                            onClick = {
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(index = uiState.messages.lastIndex)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                                .alpha(0.8f)
                        )
                    }
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .height(inputHeight)
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.background)
                            .padding(vertical = 8.dp)
                    ) {
                        TextField(
                            value = inputValue,
                            placeholder = {
                                Text(
                                    text = "Send message",
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {}
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = null,
                                    )
                                }
                            },
                            onValueChange = { inputValue = it },
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    viewModel.sendMessage(inputValue)
                                    inputValue = ""
                                }
                            ),
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(25.dp))
                        )
                        IconButton(
                            onClick = {},
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }

            // More action panel
            MoreActionPanel(
                visible = reactionVisibleItemIndex != null,
                onReply = {},
                onCopy = {},
                onDelete = {},
                onForward = {},
                modifier = Modifier.align(Alignment.BottomCenter).imePadding()
            )

            // Circular progress while fetching data and uiRendering
            if (uiState.isLoading || !isUiRendered) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Gray,
                    )
                }
            }
        }
    }
}
