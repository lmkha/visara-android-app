package com.example.visara.ui.screens.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visara.R
import com.example.visara.ui.theme.LocalVisaraCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    type: SheetType,
    isAuthenticated: Boolean,
    onClose: () -> Unit,
    onItemSelected: (selected: SheetResult) -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
    ) {
        @Composable fun Item(
            icon: @Composable () -> Unit,
            label: String,
            onClick: () -> Unit,
            hasBottomBorder: Boolean = true,
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                ) {
                    icon()

                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                if (hasBottomBorder) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Content
        when (type) {
            SheetType.SETTINGS -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isAuthenticated) {
                            Item(
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.studio_24px),
                                        contentDescription = null,
                                    )
                                },
                                label = stringResource(R.string.studio),
                                onClick = { onItemSelected(SheetResult.STUDIO) },
                            )

                            Item(
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.qr_code_24px),
                                        contentDescription = null,
                                    )
                                },
                                label = stringResource(R.string.my_qr_code),
                                onClick = { onItemSelected(SheetResult.MY_QR) },
                            )
                        }

                        Item(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                )
                            },
                            label = stringResource(R.string.settings_and_privacy),
                            onClick = { onItemSelected(SheetResult.SETTINGS) },
                            hasBottomBorder = false,
                        )
                    }
                }
            }
            SheetType.UNFOLLOW_CONFIRM -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    if (isAuthenticated) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(R.string.unfollow_confirm_message),
                                fontWeight = FontWeight.Medium,
                            )
                            Button(
                                onClick = { onItemSelected(SheetResult.UNFOLLOW) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LocalVisaraCustomColors.current.unfollowButtonContainerColor,
                                    contentColor = LocalVisaraCustomColors.current.unfollowButtonContentColor,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.unfollow),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                )
                            }
                        }
                    }
                }
            }
            SheetType.LOGIN_REQUEST -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(id = R.string.request_login_message, stringResource(R.string.follow_this_user)),
                            fontWeight = FontWeight.Medium,
                        )
                        Button(
                            onClick = { onItemSelected(SheetResult.LOGIN) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.login),
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class SheetType {
    UNFOLLOW_CONFIRM,
    LOGIN_REQUEST,
    SETTINGS,
}

enum class SheetResult {
    SETTINGS,
    MY_QR,
    STUDIO,
    UNFOLLOW,
    LOGIN,
}
