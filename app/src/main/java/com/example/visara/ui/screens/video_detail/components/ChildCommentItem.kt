package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.data.model.CommentModel
import com.example.visara.ui.components.UserAvatar
import com.example.visara.utils.toTimeAgo

@Composable
fun ChildCommentItem(
    modifier: Modifier = Modifier,
    comment: CommentModel,
    onLikeComment: (
        commentId: String,
        current: Boolean,
        onImplementImmediateWhenAuthenticated: () -> Unit,
        onFailure: () -> Unit,
    ) -> Unit,
    onReply: (username: String) -> Unit,
    isAdding: Boolean = false,
) {
    var liked: Boolean by rememberSaveable { mutableStateOf(comment.isLiked) }
    var likeCount by rememberSaveable { mutableLongStateOf(comment.likeCount) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserAvatar(
            avatarLink = comment.userAvatarUrl,
            modifier = Modifier.size(32.dp)
        )
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = comment.username,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                )

                Text(
                    text = "-",
                    color = Color.Gray,
                )

                Text(
                    text = comment.createdAt.toTimeAgo(),
                    color = Color.Gray,
                )
            }
            Text(
                text = comment.content,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground,
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
                        modifier = Modifier.clickable {
                            onLikeComment(
                                comment.id, // Comment id
                                liked, // current like state
                                {
                                    if (!liked) {
                                        liked = true
                                        likeCount += 1
                                    } else {
                                        liked = false
                                        likeCount -= 1
                                    }
                                }, // call immediately if user authenticated
                                {
                                    if (liked) {
                                        liked = false
                                        likeCount -= 1
                                    } else {
                                        liked = true
                                        likeCount += 1
                                    }
                                }, // onFailure: rollback
                            )
                        }
                    )
                    Text(
                        text = likeCount.toString(),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                Text(
                    text = "Reply",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onReply(comment.username) },
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
