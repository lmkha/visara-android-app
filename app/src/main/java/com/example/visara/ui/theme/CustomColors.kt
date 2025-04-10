package com.example.visara.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class VisaraCustomColors(
    val expandedCommentSectionBackground: Color,
)

val LocalVisaraCustomColors = staticCompositionLocalOf<VisaraCustomColors> {
    error("No custom colors provided")
}

val LightCustomColors = VisaraCustomColors(
    expandedCommentSectionBackground = Color.White,
)

val DarkCustomColors = VisaraCustomColors(
    expandedCommentSectionBackground = Color.DarkGray,
)
