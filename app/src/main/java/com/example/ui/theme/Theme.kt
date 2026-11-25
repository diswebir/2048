package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ThemeMode

val VazirmatnFontFamily = FontFamily(
    Font(R.font.vazirmatn, FontWeight.Normal),
    Font(R.font.vazirmatn, FontWeight.SemiBold),
    Font(R.font.vazirmatn, FontWeight.Bold),
    Font(R.font.vazirmatn, FontWeight.ExtraBold)
)

@Immutable
data class AppColors(
    val isLight: Boolean,
    val bgBase: Color,
    val bgMid: Color,
    val bgGlowPurple: Color,
    val bgGlowCyan: Color,
    val bgGlowCoral: Color,
    val text: Color,
    val muted: Color,
    val line: Color,
    val lineStrong: Color,
    val glass: Color,
    val glass2: Color,
    val glassHover: Color,
    val good: Color,
    val danger: Color,
    val dangerBg: Color,
    val dangerBorder: Color,
    val dangerText: Color,
    val brand: Color,
    val brand2: Color,
    val brand3: Color,
    val gold: Color,
    val goldText: Color,
    val goldBgStart: Color,
    val goldBorder: Color,
    val boardBgStart: Color,
    val boardBgMid: Color,
    val boardBgEnd: Color,
    val cell: Color,
    val cellLine: Color,
    val kbdBg: Color,
    val overlayBg: Color,
    val modalBgStart: Color,
    val modalBgEnd: Color,
    val toastBg: Color,
    val toastText: Color,
    val toastBorder: Color
)

private val DarkAppColors = AppColors(
    isLight = false,
    bgBase = Color(0xFF070B16),
    bgMid = Color(0xFF0A1020),
    bgGlowPurple = Color(0x617C5CFF),
    bgGlowCyan = Color(0x2B22D3EE),
    bgGlowCoral = Color(0x1FFF6B6B),
    text = Color(0xFFE9EEFC),
    muted = Color(0xFF92A0C2),
    line = Color(0x17FFFFFF),
    lineStrong = Color(0x29FFFFFF),
    glass = Color(0x0CFFFFFF),
    glass2 = Color(0x13FFFFFF),
    glassHover = Color(0x21FFFFFF),
    good = Color(0xFF4ADE80),
    danger = Color(0xFFFF6B6B),
    dangerBg = Color(0x24FF6B6B),
    dangerBorder = Color(0x59FF6B6B),
    dangerText = Color(0xFFFF8F8F),
    brand = Color(0xFF7C5CFF),
    brand2 = Color(0xFF22D3EE),
    brand3 = Color(0xFF5B8CFF),
    gold = Color(0xFFFFC94D),
    goldText = Color(0xFFFFD479),
    goldBgStart = Color(0x29FFC94D),
    goldBorder = Color(0x47FFC94D),
    boardBgStart = Color(0xFF18203A),
    boardBgMid = Color(0xFF111830),
    boardBgEnd = Color(0xFF0E1528),
    cell = Color(0x0CFFFFFF),
    cellLine = Color(0x08FFFFFF),
    kbdBg = Color(0x14FFFFFF),
    overlayBg = Color(0xCC080C18),
    modalBgStart = Color(0xFA1C243C),
    modalBgEnd = Color(0xFA0E1426),
    toastBg = Color(0xF5141B2E),
    toastText = Color(0xFFEAF0FF),
    toastBorder = Color(0x24FFFFFF)
)

private val LightAppColors = AppColors(
    isLight = true,
    bgBase = Color(0xFFF7F2EA),
    bgMid = Color(0xFFF4EDE3),
    bgGlowPurple = Color(0x337C5CFF),
    bgGlowCyan = Color(0x3322D3EE),
    bgGlowCoral = Color(0x38FFB26B),
    text = Color(0xFF241F18),
    muted = Color(0xFF7B6F60),
    line = Color(0x1F503E28),
    lineStrong = Color(0x38503E28),
    glass = Color(0x9EFFFFFF),
    glass2 = Color(0xCCFFFFFF),
    glassHover = Color(0xFFFFFDF9),
    good = Color(0xFF16A34A),
    danger = Color(0xFFD64545),
    dangerBg = Color(0x24FF6B6B),
    dangerBorder = Color(0x59FF6B6B),
    dangerText = Color(0xFFCC3333),
    brand = Color(0xFF7C5CFF),
    brand2 = Color(0xFF0891B2),
    brand3 = Color(0xFF5B8CFF),
    gold = Color(0xFFFFC94D),
    goldText = Color(0xFFB97A00),
    goldBgStart = Color(0x29FFC94D),
    goldBorder = Color(0x47FFC94D),
    boardBgStart = Color(0xFFF7F1E8),
    boardBgMid = Color(0xFFF3ECE1),
    boardBgEnd = Color(0xFFEFE7DB),
    cell = Color(0x1C786042),
    cellLine = Color(0x8CFFFFFF),
    kbdBg = Color(0x17503E28),
    overlayBg = Color(0xD9FFFAF4),
    modalBgStart = Color(0xFFFFFDF9),
    modalBgEnd = Color(0xFFF6EFE5),
    toastBg = Color(0xFFFFFDF9),
    toastText = Color(0xFF241F18),
    toastBorder = Color(0x29503E28)
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

@Immutable
data class TileVisualStyle(
    val background: Brush,
    val textColor: Color,
    val glowColor: Color = Color.Transparent,
    val borderColor: Color? = null
)

fun tileStyleFor(value: Int, isLight: Boolean): TileVisualStyle {
    return when (value) {
        2 -> if (isLight) {
            TileVisualStyle(SolidColor(Color(0xFFEFE8DF)), Color(0xFF6B5C4D))
        } else {
            TileVisualStyle(SolidColor(Color(0xFF26304D)), Color(0xFFCCD8F6))
        }
        4 -> if (isLight) {
            TileVisualStyle(SolidColor(Color(0xFFE9DCCB)), Color(0xFF6B5C4D))
        } else {
            TileVisualStyle(SolidColor(Color(0xFF2F3D63)), Color(0xFFD6E1FF))
        }
        8 -> if (isLight) {
            TileVisualStyle(SolidColor(Color(0xFFF2B179)), Color.White)
        } else {
            TileVisualStyle(SolidColor(Color(0xFF3A56A4)), Color.White)
        }
        16 -> if (isLight) {
            TileVisualStyle(SolidColor(Color(0xFFF59563)), Color.White)
        } else {
            TileVisualStyle(SolidColor(Color(0xFF4A6FD6)), Color.White)
        }
        32 -> if (isLight) {
            TileVisualStyle(SolidColor(Color(0xFFF67C5F)), Color.White)
        } else {
            TileVisualStyle(SolidColor(Color(0xFF5F6BFF)), Color.White)
        }
        64 -> TileVisualStyle(SolidColor(Color(0xFF8B5CF6)), Color.White)
        128 -> TileVisualStyle(SolidColor(Color(0xFFC04AE0)), Color.White, Color(0x59C04AE0))
        256 -> TileVisualStyle(SolidColor(Color(0xFFEF4FA6)), Color.White, Color(0x59EF4FA6))
        512 -> TileVisualStyle(SolidColor(Color(0xFFFF6A5A)), Color.White, Color(0x59FF6A5A))
        1024 -> TileVisualStyle(
            Brush.linearGradient(listOf(Color(0xFFFFB347), Color(0xFFFF8A3D))),
            Color(0xFF2B1700),
            Color(0x66FF963C)
        )
        2048 -> TileVisualStyle(
            Brush.linearGradient(listOf(Color(0xFFFFE066), Color(0xFFFFAB2E))),
            Color(0xFF2A1A00),
            Color(0x99FFBE3C)
        )
        4096 -> TileVisualStyle(
            Brush.linearGradient(listOf(Color(0xFF7CF5D0), Color(0xFF22D3EE))),
            Color(0xFF032018),
            Color(0x8022D3EE)
        )
        8192 -> TileVisualStyle(
            Brush.linearGradient(listOf(Color(0xFFA5B4FC), Color(0xFF7C5CFF))),
            Color.White,
            Color(0x8C7C5CFF)
        )
        else -> TileVisualStyle(
            Brush.linearGradient(listOf(Color(0xFF1D2745), Color(0xFF0F1730))),
            Color(0xFFCFD9FF),
            Color(0x477C5CFF),
            Color(0x807C5CFF)
        )
    }
}

private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.5.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.5.sp,
        lineHeight = 16.sp
    )
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun Puzzle2048Theme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useLight = when (themeMode) {
        ThemeMode.LIGHT -> true
        ThemeMode.DARK -> false
        ThemeMode.AUTO -> !systemDark
    }
    val appColors = if (useLight) LightAppColors else DarkAppColors
    val m3Scheme = if (useLight) {
        lightColorScheme(
            primary = appColors.brand,
            onPrimary = Color.White,
            secondary = appColors.brand2,
            background = appColors.bgBase,
            surface = appColors.modalBgStart,
            onBackground = appColors.text,
            onSurface = appColors.text,
            outline = appColors.lineStrong,
            error = appColors.danger
        )
    } else {
        darkColorScheme(
            primary = appColors.brand,
            onPrimary = Color.White,
            secondary = appColors.brand2,
            background = appColors.bgBase,
            surface = appColors.modalBgStart,
            onBackground = appColors.text,
            onSurface = appColors.text,
            outline = appColors.lineStrong,
            error = appColors.danger
        )
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = m3Scheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
