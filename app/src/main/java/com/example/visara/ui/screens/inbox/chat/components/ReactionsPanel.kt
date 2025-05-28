package com.example.visara.ui.screens.inbox.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ReactionsPanel(
    modifier: Modifier = Modifier,
    visible: Boolean = false,
) {

    val visibleStates = remember(visible) {
        mutableStateListOf<Boolean>().apply {
            repeat(Emoji.entries.size) { add(false) }
        }
    }

    LaunchedEffect(visible) {
        if (visible) {
            Emoji.entries.indices.forEach { index ->
                visibleStates[index] = true
                delay(40)
            }
        } else {
            Emoji.entries.indices.reversed().forEach { index ->
                visibleStates[index] = false
                delay(20)
            }
        }
    }

    if (visible) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
                .background(color = Color.DarkGray)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Emoji.entries.forEachIndexed { index, emoji ->
                    AnimatedVisibility(
                        visible = visibleStates.getOrNull(index) == true,
                        enter = scaleIn(animationSpec = tween(durationMillis = 300)),
                        exit = scaleOut(animationSpec = tween(durationMillis = 200)),
                    ) {
                        IconButton(onClick = {
                            // Handle emoji click here
                        }) {
                            Text(
                                text = emoji.code,
                                fontSize = 25.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Emoji(val label: String, val code: String) {
    LIKE("Like", "\uD83D\uDC4D"),
    HEART("Heart", "\u2764\uFE0F"),
    LAUGH("Laugh", "\uD83D\uDE02"),
    WOW("Wow", "\uD83D\uDE2E"),
    SAD("Sad", "\uD83D\uDE22"),
    ANGRY("Angry", "\uD83D\uDE21"),
}
