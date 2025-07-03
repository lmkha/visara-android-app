package com.example.visara.ui.screens.video_detail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.ui.components.UserAvatar
import com.example.visara.utils.getTimeAgo
import com.example.visara.viewmodels.CommentWithReplies

@Composable
fun ParentCommentItem(
    modifier: Modifier = Modifier,
    commentWithReplies: CommentWithReplies?,
    fetchChildrenComment: () -> Unit,
    onLikeComment: (
        commentId: String,
        current: Boolean,
        onImplementImmediateWhenAuthenticated: () -> Unit,
        onFailure: () -> Unit,
    ) -> Unit,
    onReply: (
        commentId: String?,
        username: String
    ) -> Unit,
    isAdding: Boolean = false,
) {
    var liked: Boolean by rememberSaveable {
        mutableStateOf( commentWithReplies?.comment?.isLiked == true )
    }
    var likeCount by rememberSaveable {
        mutableLongStateOf(commentWithReplies?.comment?.likeCount ?: 0L)
    }

    var openReplies: Boolean by rememberSaveable { mutableStateOf(false) }

    commentWithReplies?.comment?.let { parentComment->
        Column(modifier = modifier) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                UserAvatar(
                    avatarLink = parentComment.userAvatarUrl,
                    modifier = Modifier.size(40.dp)
                )
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = parentComment.username,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                        )

                        Text(
                            text = "-",
                            color = Color.Gray,
                        )

                        Text(
                            text = getTimeAgo(parentComment.createdAt),
                            color = Color.Gray,
                        )
                    }
                    Text(
                        text = parentComment.content,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clickable { liked = !liked },
                        ) {
                            Icon(
                                painter = painterResource(id = if (liked) R.drawable.heart_filled_24px else R.drawable.heart_outlined_24px),
                                contentDescription = null,
                                tint = if (liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.clickable {
                                    onLikeComment(
                                        parentComment.id, // CommentId
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
                            text = stringResource(R.string.reply),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onReply(parentComment.id, parentComment.username) },
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            // Children comments
            AnimatedVisibility(
                visible = openReplies,
                enter = expandVertically(
                    animationSpec = tween(durationMillis = 300),
                    expandFrom = Alignment.Top
                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = 250),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(animationSpec = tween(durationMillis = 100)),
            ) {
                Column(modifier = Modifier.padding(start = 40.dp)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        commentWithReplies.replies.forEach { childrenComment->
                            ChildCommentItem(
                                comment = childrenComment,
                                onLikeComment = onLikeComment,
                                onReply = { username->
                                    onReply(parentComment.id, username)
                                }
                            )
                        }
                    }
                }
            }

            // show or hide children comments
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp, top = 4.dp),
            ) {
                HorizontalDivider(modifier = Modifier.width(32.dp))
                if (parentComment.replyCount > 0 && !openReplies) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                openReplies = true
                                if (commentWithReplies.replies.isEmpty()) { fetchChildrenComment() }
                            },
                    ) {
                        Text(
                            text = pluralStringResource(
                                id = R.plurals.see_more_replies,
                                count = parentComment.replyCount.toInt(),
                                parentComment.replyCount.toString(),
                            ),
                            color = Color.Gray,
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Gray,
                        )
                    }
                }
                if (openReplies) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                openReplies = false
                            },
                    ) {
                        Text(
                            text = stringResource(R.string.hide),
                            color = Color.Gray,
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = Color.Gray,
                        )
                    }
                }
            }
        }
    }
}
