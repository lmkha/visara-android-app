package com.example.visara.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class VisaraCustomColors(
    val expandedCommentSectionBackground: Color,
    val border: Color,
    val darkGrayCustom: Color,
    val bottomSheetBackground: Color,
    val onBottomSheetBackground: Color,
    val scrollToFirstItemButtonBackground: Color,
    val selectedChipContainerColor: Color,
    val unselectedChipContainerColor: Color,
    val selectedChipContentColor: Color,
    val unselectedChipContentColor: Color,
    val profileActionButtonContainerColor: Color,
    val profileActionButtonContentColor: Color,
    val searchBarContainerColor: Color,
    val searchResultUserItemContainerColor: Color,
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
    scrollToFirstItemButtonBackground = Color.LightGray,
    selectedChipContainerColor = Color.Gray,
    selectedChipContentColor = Color.White,
    unselectedChipContainerColor = Color.White,
    unselectedChipContentColor = Color.Black,
    profileActionButtonContainerColor = Color.LightGray,
    profileActionButtonContentColor = Color.Black,
    searchBarContainerColor = Color.LightGray,
    searchResultUserItemContainerColor = Color.LightGray,
)

val DarkCustomColors = VisaraCustomColors(
    expandedCommentSectionBackground = Color.DarkGray,
    border = Color.DarkGray,
    darkGrayCustom = Color(0xFF2C2A32),
    bottomSheetBackground = Color.DarkGray,
    onBottomSheetBackground = Color.White,
    scrollToFirstItemButtonBackground = Color.Gray,
    selectedChipContainerColor = Color.Gray,
    selectedChipContentColor = Color.White,
    unselectedChipContainerColor = Color.Black,
    unselectedChipContentColor = Color.White,
    profileActionButtonContainerColor = Color.DarkGray,
    profileActionButtonContentColor = Color.White,
    searchBarContainerColor = Color(0xFF2C2A32),
    searchResultUserItemContainerColor = Color.DarkGray,
)
