package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.visara.R

@Composable
fun ActionsSection(
    liked: Boolean,
    likeCount: Long,
    likeClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Like
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = likeClick,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(id = if (liked) R.drawable.heart_filled_24px else R.drawable.heart_outlined_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = likeCount.toString(),
                )
            }
        }
        // Row of other actions:
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState())
            ,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            FilledTonalButton(
                onClick = {},
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                ),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Icon(Icons.Default.Share, null)
                Spacer(Modifier.width(8.dp))
                Text("Share")
            }
            FilledTonalButton(
                onClick = {},
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                ),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.download_24px),
                    contentDescription = null,
                )
                Spacer(Modifier.width(8.dp))
                Text("Download")
            }
            FilledTonalButton(
                onClick = {},
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                ),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Icon(Icons.Default.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text("Save")
            }
            FilledTonalButton(
                onClick = {},
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                ),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.flag_24px),
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text("Report")
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}
