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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.ui.components.UserAvatar

@Composable
fun ChildCommentItem(
    modifier: Modifier = Modifier,
) {
    var liked by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserAvatar(modifier = Modifier.size(32.dp))
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
                fontWeight = FontWeight.Normal
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
                    )
                    Text(
                        text = "9",
                    )
                }

                Text(
                    text = "Reply",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = {})
                    ,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
