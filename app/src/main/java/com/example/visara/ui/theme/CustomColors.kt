package com.example.visara.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class VisaraCustomColors(
    val expandedCommentSectionBackground: Color,
    val border: Color,
    val darkGrayCustom: Color,
    val bottomSheetBackground: Color,
    val onBottomSheetBackground: Color,
)

val LocalVisaraCustomColors = staticCompositionLocalOf<VisaraCustomColors> {
    error("No custom colors provided")
}

val LightCustomColors = VisaraCustomColors(
    expandedCommentSectionBackground = Color.White,
    border = Color.LightGray,
    darkGrayCustom = Color(0xFF2C2A32),
    bottomSheetBackground = Color.White,
    onBottomSheetBackground = Color.Black,
)

val DarkCustomColors = VisaraCustomColors(
    expandedCommentSectionBackground = Color.DarkGray,
    border = Color.DarkGray,
    darkGrayCustom = Color(0xFF2C2A32),
    bottomSheetBackground = Color.DarkGray,
    onBottomSheetBackground = Color.White,
)
