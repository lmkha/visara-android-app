package com.example.visara.ui.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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

@Composable
fun ContentAndDisplaySettingsSection(
    onItemSelected: (item: SettingItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.content_and_display),
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
                SettingsItem(
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    label = stringResource(R.string.notification),
                    onClick = { onItemSelected(ContentDisplaySettings.Notification) },
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.dark_mode_24px), contentDescription = null) },
                    label = stringResource(R.string.theme),
                    onClick = { onItemSelected(ContentDisplaySettings.Theme) },
                )
                SettingsItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.security_24px), contentDescription = null) },
                    label = stringResource(R.string.language),
                    onClick = { onItemSelected(ContentDisplaySettings.Language) },
                )
            }
        }
    }
}

enum class ContentDisplaySettings : SettingItem {
    Notification, Language, Theme
}
