package com.example.visara.ui.screens.inbox.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.visara.data.model.MessageModel
import com.example.visara.ui.components.UserAvatar

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    message: MessageModel,
    isMyMessage: Boolean,
    shouldShowAvatar: Boolean,
    onShowReactionsPanel: () -> Unit,
    onDismissReactionsPanel: () -> Unit,
) {
    val avatarSize = 30.dp
    val horizontalPadding = 8.dp

    BoxWithConstraints(modifier = modifier) {
        val width = (this.maxWidth.value * 0.8).dp
        Box(
            contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart,
            modifier = Modifier
                .width(width)
                .align(if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onShowReactionsPanel() },
                        onTap = { onDismissReactionsPanel() }
                    )
                }
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(horizontalPadding)) {
                Box(modifier = Modifier.size(avatarSize)) {
                    if (!isMyMessage && shouldShowAvatar) {
                        UserAvatar(modifier = Modifier.fillMaxSize())
                    }
                }
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(color = if (isMyMessage) Color.Blue else Color.LightGray)
                ) {
                    Text(
                        text = message.content,
                        color = if (isMyMessage) Color.White else Color.Black,
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
