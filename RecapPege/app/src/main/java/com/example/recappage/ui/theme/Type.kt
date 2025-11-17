package com.example.recappage.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.recappage.R

val SourceSerifPro = FontFamily(
    // R.font.source_serif_pro_regular membutuhkan import com.example.recappage.R
    Font(R.font.source_serif_pro_regular, FontWeight.Normal),
    Font(R.font.source_serif_pro_bold, FontWeight.Bold)
)

// Source Sans 3
val SourceSans3 = FontFamily(
    Font(R.font.source_sans3_regular, FontWeight.Normal),
    Font(R.font.source_serif_pro_bold, FontWeight.Bold),
    // ✅ TAMBAHKAN BARIS INI (dengan asumsi file font semibold ada)
    Font(R.font.source_sans3_semibold, FontWeight.SemiBold)
)

// ✅ Gunakan AppTypography (supaya tidak bentrok dengan default Typography)
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = SourceSans3,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SourceSans3,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SourceSans3,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    titleLarge = TextStyle(
        fontFamily = com.example.recappage.ui.theme.SourceSerifPro,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = com.example.recappage.ui.theme.SourceSerifPro,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = com.example.recappage.ui.theme.SourceSerifPro,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)