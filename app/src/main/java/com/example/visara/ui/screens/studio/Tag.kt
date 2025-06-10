package com.example.visara.ui.screens.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Tag(
    text: String,
    onClick: () -> Unit,
    selected: Boolean,
    iconColor: Color = Color.Transparent,
) {
    SuggestionChip(
        onClick = onClick,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = if (selected) Color.LightGray
            else Color.DarkGray,
            labelColor = if (selected) Color.Black
            else Color.White,
        ),
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = text)
                Box(modifier = Modifier
                    .size(15.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, Color.Black, CircleShape)
                    .background(color = iconColor))
            }
        },
    )
}
