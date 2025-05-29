package com.example.visara.ui.screens.inbox.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.R

@Composable
fun MoreActionPanel(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onReply: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onForward: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 200)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 200)
        ),
        modifier = modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        Box(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
                .border(width = 5.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape =  RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = {}
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Item(
                    title = "Reply",
                    onClick = onReply
                ) {
                    Icon(
                        painter = painterResource(R.drawable.inbox_reply),
                        contentDescription = null
                    )
                }
                Item(
                    title = "Copy",
                    onClick = onCopy,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.inbox_copy),
                        tint = Color.Blue.copy(alpha = 0.5f),
                        contentDescription = null,
                    )
                }
                Item(
                    title = "Delete",
                    onClick = onDelete,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.inbox_delete),
                        tint = Color.Red.copy(alpha = 0.5f),
                        contentDescription = null
                    )
                }
                Item(
                    title = "Forward",
                    onClick = onForward,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.inbox_forward),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun Item(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    icon: @Composable (() -> Unit),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        IconButton(onClick = onClick) {
            icon()
        }
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
        )
    }
}
