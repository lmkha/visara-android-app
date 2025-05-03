package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.data.model.UserModel
import com.example.visara.ui.components.UserAvatar

@Composable
fun CommentInput(
    modifier: Modifier = Modifier,
    currentUser: UserModel? = null,
    state: CommentInputState,
    onSubmit: () -> Unit,
) {
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
            UserAvatar(
                avatarLink = currentUser?.networkAvatarUrl,
                modifier = Modifier.size(56.dp)
            )
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(30.dp))
                    .focusRequester(state.focusRequester)
                ,
                value = state.content,
                placeholder = {
                    if (!state.repliedUsername.isNullOrEmpty()) {
                        Text(state.repliedUsername!!)
                    }
                },
                onValueChange = { state.content = it },
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.LightGray,
                    focusedContainerColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                ),
                trailingIcon = {
                    Row {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(id = R.drawable.at_24px),
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }
                        if (state.content.isNotEmpty()) {
                            IconButton(
                                onClick = onSubmit,
                                modifier = Modifier
                                    .background(
                                        if (state.content.isNotEmpty()) MaterialTheme.colorScheme.primary
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

class CommentInputState {
    var content by mutableStateOf("")
    var repliedUsername by mutableStateOf<String?>(null)
    var repliedCommentId by mutableStateOf<String?>(null)
    var parentIndex by mutableIntStateOf(0)
    var focusRequester = FocusRequester()

    fun reset() {
        content = ""
        repliedUsername = null
        repliedCommentId = null
        parentIndex = 0
    }
}

@Composable
fun rememberCommentInputState() = remember { CommentInputState() }
