package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.data.model.UserModel
import com.example.visara.ui.theme.LocalVisaraCustomColors
import com.example.visara.viewmodels.CommentWithReplies

@Composable
fun ExpandedCommentSection(
    modifier: Modifier = Modifier,
    currentUser: UserModel?,
    commentList: List<CommentWithReplies>,
    onFetchReplies: (parentIndex: Int) -> Unit,
    onLikeComment: (
        commentId: String,
        isUnlike: Boolean,
        onImplementImmediateWhenAuthenticated: () -> Unit,
        onFailure: () -> Unit,
    ) -> Unit,
    addComment: (
        content: String,
        parentId: String?,
        parentIndex: Int
    ) -> Unit,
    onClose: () -> Unit,
) {
    val headerHeight = 50.dp
    val commentInputHeight = 100.dp
    val commentInputState = rememberCommentInputState()
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier.imePadding()) {
        Box(
            modifier = Modifier
                .height(headerHeight)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "6.778 comments",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
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
            items(commentList.size) { index->
                ParentCommentItem(
                    commentWithReplies = commentList.getOrNull(index = index),
                    onReply = { commentId, username->
                        commentInputState.reset()
                        commentInputState.repliedUsername = username
                        commentInputState.repliedCommentId = commentId
                        commentInputState.parentIndex = index
                        commentInputState.focusRequester.requestFocus()
                    },
                    fetchChildrenComment = { onFetchReplies(index) },
                    onLikeComment = onLikeComment
                )
            }
        }
        CommentInput(
            state = commentInputState,
            currentUser = currentUser,
            onSubmit = {
                addComment(
                    commentInputState.content,
                    commentInputState.repliedCommentId,
                    commentInputState.parentIndex,
                )
                commentInputState.reset()
                focusManager.clearFocus()
            },
            modifier = Modifier
                .height(commentInputHeight)
                .fillMaxWidth()
                .background(color = LocalVisaraCustomColors.current.expandedCommentSectionBackground)
                .align(Alignment.BottomStart)
        )
    }
}
