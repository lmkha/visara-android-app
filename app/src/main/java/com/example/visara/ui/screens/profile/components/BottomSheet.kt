package com.example.visara.ui.screens.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.visara.R
import com.example.visara.ui.theme.LocalVisaraCustomColors

@Composable
fun BottomSheet(
    isAuthenticated: Boolean,
    displayBottomSheet: Boolean,
    onClose: () -> Unit,
    onItemSelected: (item: String) -> Unit = {},
) {
    Box {
        val backgroundAlpha by animateFloatAsState(
            targetValue = if (displayBottomSheet) 0.5f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "backgroundAlpha"
        )
        // Back layer
        AnimatedVisibility(
            visible = displayBottomSheet,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .zIndex(1f)
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backgroundAlpha))
                    .clickable(
                        onClick = onClose,
                        interactionSource = null,
                        indication = null,
                    )
            )
        }

        // Content
        AnimatedVisibility(
            visible = displayBottomSheet,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .zIndex(2f)
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(color = LocalVisaraCustomColors.current.bottomSheetBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    @Composable
                    fun Item(
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

                    if (isAuthenticated) {
                        Item(
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.studio_24px),
                                    contentDescription = null,
                                )
                            },
                            label = "VSara Studio",
                            onClick = { onItemSelected("studio") },
                        )

                        Item(
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.qr_code_24px),
                                    contentDescription = null,
                                )
                            },
                            label = "My QR Code",
                            onClick = { onItemSelected("qr") },
                        )
                    }

                    Item(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                            )
                        },
                        label = "Settings and privacy",
                        onClick = { onItemSelected("settings") },
                        hasBottomBorder = false,
                    )
                }
            }
        }
    }
}

