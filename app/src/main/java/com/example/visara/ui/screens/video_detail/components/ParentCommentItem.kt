package com.example.visara.ui.screens.video_detail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.ui.components.UserAvatar

@Composable
fun ParentCommentItem(
    modifier: Modifier = Modifier,
    childCount: Int = 3,
    onReply: () -> Unit = {},
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
                            .clickable{ onReply() }
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
