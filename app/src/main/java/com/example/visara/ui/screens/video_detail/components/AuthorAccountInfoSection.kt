package com.example.visara.ui.screens.video_detail.components

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.data.model.UserModel
import com.example.visara.ui.components.UserAvatar
import com.example.visara.ui.theme.LocalVisaraCustomColors

@Composable
fun AuthorAccountInfoSection(
    author: UserModel? = null,
    isFollowing: Boolean,
    currentUser: UserModel? = null,
    onAuthorClick: () -> Unit,
    onFollowUser: (onFailure: () -> Unit) -> Unit,
    onUnfollowUser: (onFailure: () -> Unit) -> Unit,
) {
    var isFollowingState by remember(isFollowing) { mutableStateOf(isFollowing) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable(onClick = onAuthorClick)
        ) {
            UserAvatar(
                avatarLink = author?.networkAvatarUrl,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = author?.username ?: "username",
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = author?.followerCount.toString(),
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        if (currentUser?.username != author?.username) {
            FilledTonalButton(
                onClick = {
                    if (isFollowingState) {
                        isFollowingState = false
                        onUnfollowUser { isFollowingState = true }
                    } else {
                        isFollowingState = true
                        onFollowUser { isFollowingState = false }
                    }
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isFollowing) LocalVisaraCustomColors.current.unfollowButtonContainerColor
                    else MaterialTheme.colorScheme.primary
                    ,
                    contentColor = if (isFollowing) LocalVisaraCustomColors.current.unfollowButtonContentColor
                    else Color.White,
                )
            ) {
                Text(
                    text = if (isFollowingState) stringResource(R.string.following)
                    else stringResource(R.string.follow)
                )
            }
        }
    }
    Spacer(Modifier.height(16.dp).fillMaxWidth())
}
