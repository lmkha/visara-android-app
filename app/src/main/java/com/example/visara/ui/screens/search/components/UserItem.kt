package com.example.visara.ui.screens.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visara.data.model.UserModel
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.theme.LocalVisaraCustomColors

@Composable
fun UserItem(
    modifier: Modifier = Modifier,
    user: UserModel,
    onViewProfile: () -> Unit,
) {
    Box(modifier = modifier.background(color = LocalVisaraCustomColors.current.searchResultUserItemContainerColor)) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UserAvatar(
                    avatarLink = user.networkAvatarUrl,
                    modifier = Modifier.size(50.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = user.fullName,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "@${user.username}",
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${user.followerCount} followers",
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        Text(
                            text = "-",
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        Text(
                            text = "9999 videos",
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onViewProfile,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 48.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Text(
                        text = "View profile",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}
