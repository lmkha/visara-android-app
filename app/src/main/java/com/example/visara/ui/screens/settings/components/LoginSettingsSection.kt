package com.example.visara.ui.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.visara.R
import com.example.visara.data.model.UserModel
import com.example.visara.ui.components.UserAvatar

@Composable
fun LoginSettingsSection(
    isAuthenticated: Boolean,
    currentUser: UserModel? = null,
    onLoginSelected: () -> Unit,
    onItemSelected: (item: SettingItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.login),
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Box(modifier = Modifier) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                if (isAuthenticated) {
                    SettingsItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.published_with_changes_24px),
                                contentDescription = null
                            )
                        },
                        label = stringResource(R.string.switch_account),
                        onClick = { onItemSelected(LoginSettings.SwitchAccount) },
                    )
                }

                if (isAuthenticated) {
                    SettingsItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.logout_24px),
                                contentDescription = null
                            )
                        },
                        trailIcon = {
                            UserAvatar(
                                avatarLink =currentUser?.networkAvatarUrl ,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.logout),
                        onClick = { onItemSelected(LoginSettings.Logout) },
                    )
                } else {
                    SettingsItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null
                            )
                        },
                        label = stringResource(R.string.login),
                        onClick = onLoginSelected,
                    )
                }
            }
        }
    }
}

enum class LoginSettings : SettingItem {
    SwitchAccount, Logout
}
