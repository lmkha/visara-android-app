package com.example.visara.ui.screens.inbox.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visara.data.model.MessageModel
import com.example.visara.ui.components.UserAvatar
import com.example.visara.viewmodels.ChatInboxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatInboxScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatInboxViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputValue by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()
    val inputHeight = 70.dp

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            val lastIndex = uiState.messages.lastIndex

            if (!uiState.firstTime) {
                lazyListState.animateScrollToItem(index = lastIndex)
            } else {
                lazyListState.requestScrollToItem(index = lastIndex)
            }
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
                                text = "Minh Kha",
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
        if (!uiState.isLoading) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                val listHeight = this.maxHeight - inputHeight
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                ) {
                    item {
                        LazyColumn(
                            state = lazyListState,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .height(listHeight)
                                .fillMaxWidth()
                        ) {
                            items(uiState.messages.size) { index ->
                                val shouldShowAvatar =
                                    !(uiState.messages[index].isMine || uiState.messages.getOrNull(
                                        index + 1
                                    )?.isMine == false)
                                Message(
                                    message = uiState.messages[index],
                                    shouldShowAvatar = shouldShowAvatar,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp)
                                )
                            }
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
                                        color = Color.Black,
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
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
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

@Composable
fun Message(
    modifier: Modifier = Modifier,
    message: MessageModel,
    shouldShowAvatar: Boolean,
) {
    BoxWithConstraints(modifier = modifier) {
        val width = this.maxWidth
        Box(
            contentAlignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart,
            modifier = Modifier
                .width((width.value * 0.8).dp)
                .align(if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(30.dp)) {
                    if (!message.isMine && shouldShowAvatar) {
                        UserAvatar(modifier = Modifier.fillMaxSize())
                    }
                }
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(color = if (message.isMine) Color.Blue else Color.LightGray)
                ) {
                    Text(
                        text = message.content,
                        color = if (message.isMine) Color.White else Color.Black,
                        modifier = Modifier.padding(
                            vertical = 4.dp,
                            horizontal = 8.dp,
                        ),
                    )
                }
            }
        }
    }
}
