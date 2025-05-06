package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.data.model.UserModel
import com.example.visara.ui.components.UserAvatar

@Composable
fun AuthorAccountInfoSection(
    author: UserModel? = null,
    currentUser: UserModel? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UserAvatar(
                avatarLink = author?.networkAvatarUrl,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = author?.username ?: "username",
                fontWeight = FontWeight.W500
            )
            Text(
                text = author?.followerCount.toString(),
                fontWeight = FontWeight.Normal
            )
        }

        if (currentUser?.username != author?.username) {
            FilledTonalButton(
                onClick = {},
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            ) {
                Text("Subscribe")
            }
        }
    }
    Spacer(Modifier.height(16.dp).fillMaxWidth())
}
