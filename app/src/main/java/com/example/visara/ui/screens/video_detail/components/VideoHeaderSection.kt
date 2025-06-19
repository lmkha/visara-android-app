package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visara.utils.formatViews
import com.example.visara.utils.toTimeAgo

@Composable
fun VideoHeaderSection(
    title: String,
    createdAt: String,
    viewsCount: Long,
    hashtags: List<String> = emptyList(),
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.clickable {onClick()}
        ,
    ) {
        Text(
            text = title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = formatViews(viewsCount), color = MaterialTheme.colorScheme.onBackground)
            Text(text = createdAt.toTimeAgo(), color = MaterialTheme.colorScheme.onBackground)
            hashtags.firstOrNull()?.let {
                Text(text = it, color = MaterialTheme.colorScheme.onBackground)
            }
            Text(text = "...more", color = MaterialTheme.colorScheme.onBackground)
        }

    }
    Spacer(Modifier.height(16.dp).fillMaxWidth())
}
