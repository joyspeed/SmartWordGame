package com.smartwordgame.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val SmartWordGameTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 28.sp,
        lineHeight = 39.sp,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = TextStyle(
        fontSize = 24.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight.Bold
    ),
    bodyLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    labelLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp
    )
)
