package com.example.visara.ui.screens.add_new_video.components.enter_video_info

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacySelectBox (
    modifier: Modifier = Modifier,
    currentPrivacy: PrivacyState,
    onBack: () -> Unit = {},
    onSelected: (value: PrivacyState) -> Unit = { _ -> },
) {
    val options = listOf<PrivacyState>(
        PrivacyState.ALL,
        PrivacyState.FOLLOWER,
        PrivacyState.ONLY_ME
    )
    var selectedOption by remember { mutableStateOf<PrivacyState>(currentPrivacy) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
            Text(
                text = "Who can see this video",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(Modifier.height(16.dp))

        options.forEach { option ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelected(option) }
                ) {
                    RadioButton(
                        selected = option == selectedOption,
                        onClick = {
                            selectedOption = option
                            onSelected(option)
                        }
                    )
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = option.label,
                                fontWeight = FontWeight.Medium,
                            )
                            Icon(
                                painter = painterResource(id = option.iconId),
                                contentDescription = null
                            )
                        }
                        Text(option.detailInfo)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
