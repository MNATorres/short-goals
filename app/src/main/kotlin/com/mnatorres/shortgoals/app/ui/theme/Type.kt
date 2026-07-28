package com.mnatorres.shortgoals.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Style for data values (percentages, streaks, dates): monospaced with
 * tabular figures so columns of numbers stay aligned as they change.
 */
val DataStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontFeatureSettings = "tnum",
)

val DataLarge = DataStyle.copy(fontSize = 30.sp, fontWeight = FontWeight.Bold)
val DataSmall = DataStyle.copy(fontSize = 12.sp)

/**
 * Uppercase label style for section headers and stat captions
 * ("CUMPLIDOS HOY", "ACTIVOS"), per the Tablero mockups.
 */
val LabelStyle = TextStyle(
    fontSize = 11.sp,
    letterSpacing = 1.2.sp,
    fontWeight = FontWeight.Medium,
)

val TableroTypography = Typography()
