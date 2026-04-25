package com.smartbite.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val SmartBiteTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, color = TextDark),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextDark),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextDark),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextDark),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = TextDark),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = TextGrey),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, color = TextGrey),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 11.sp, color = TextGrey)
)
