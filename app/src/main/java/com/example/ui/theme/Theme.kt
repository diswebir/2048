package com.example.ui.theme

import android.app.Activity
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.R
import com.example.model.ThemeMode

val VazirmatnFontFamily = FontFamily(
    Font(R.font.vazirmatn, FontWeight.Normal),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
    Font(R.font.vazirmatn_extrabold, FontWeight.ExtraBold)
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
    val boardBorder: Color,
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
    bgMid = Color(0xFF0B1122),
    bgGlowPurple = Color(0x617C5CFF),
    bgGlowCyan = Color(0x2E22D3EE),
    bgGlowCoral = Color(0x22FF6B6B),
    text = Color(0xFFE9EEFC),
    muted = Color(0xFF96A5C7),
    line = Color(0x19FFFFFF),
    lineStrong = Color(0x2EFFFFFF),
    glass = Color(0x0EFFFFFF),
    glass2 = Color(0x16FFFFFF),
    glassHover = Color(0x24FFFFFF),
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
    goldBgStart = Color(0x2BFFC94D),
    goldBorder = Color(0x4DFFC94D),
    boardBgStart = Color(0xFF17203A),
    boardBgMid = Color(0xFF11182F),
    boardBgEnd = Color(0xFF0D1427),
    boardBorder = Color(0x30FFFFFF),
    cell = Color(0x12FFFFFF),
    cellLine = Color(0x0BFFFFFF),
    kbdBg = Color(0x16FFFFFF),
    overlayBg = Color(0xD4080C18),
    modalBgStart = Color(0xFA1C243C),
    modalBgEnd = Color(0xFA0E1426),
    toastBg = Color(0xF5141B2E),
    toastText = Color(0xFFEAF0FF),
    toastBorder = Color(0x2BFFFFFF)
)

private val LightAppColors = AppColors(
    isLight = true,
    bgBase = Color(0xFFF6F1E9),
    bgMid = Color(0xFFEFE8DC),
    bgGlowPurple = Color(0x2B7C5CFF),
    bgGlowCyan = Color(0x2B22D3EE),
    bgGlowCoral = Color(0x30FFB26B),
    text = Color(0xFF231D16),
    muted = Color(0xFF706354),
    line = Color(0x24503E28),
    lineStrong = Color(0x42503E28),
    glass = Color(0xBDFFFFFF),
    glass2 = Color(0xE0FFFFFF),
    glassHover = Color(0xFFFFFDF9),
    good = Color(0xFF15803D),
    danger = Color(0xFFDC2626),
    dangerBg = Color(0x20EF4444),
    dangerBorder = Color(0x5CEf4444),
    dangerText = Color(0xFFB91C1C),
    brand = Color(0xFF6D4CFF),
    brand2 = Color(0xFF0891B2),
    brand3 = Color(0xFF4F7CFF),
    gold = Color(0xFFF59E0B),
    goldText = Color(0xFFA16207),
    goldBgStart = Color(0x36FBBF24),
    goldBorder = Color(0x5CEAB308),
    // Distinct warm sandstone/linen board tray so ivory tile 2 and cream tile 4 pop with sharp contrast
    boardBgStart = Color(0xFFD5C7B5),
    boardBgMid = Color(0xFFCBBCAA),
    boardBgEnd = Color(0xFFC1B09C),
    boardBorder = Color(0x52644F37),
    cell = Color(0xFFB3A18D),
    cellLine = Color(0x4DFFF9F0),
    kbdBg = Color(0x1F503E28),
    overlayBg = Color(0xE0FAF5ED),
    modalBgStart = Color(0xFFFFFDF9),
    modalBgEnd = Color(0xFFF5EDE1),
    toastBg = Color(0xFFFFFDF9),
    toastText = Color(0xFF231D16),
    toastBorder = Color(0x33503E28)
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

@Immutable
data class TileVisualStyle(
    val background: Brush,
    val textColor: Color,
    val glowColor: Color = Color.Transparent,
    val borderColor: Color? = null,
    val particleColor: Color = Color(0xFF7C5CFF),
    val hasSheen: Boolean = false
)

fun tileStyleFor(value: Int, isLight: Boolean): TileVisualStyle {
    return when (value) {
        2 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFFFFEFC), Color(0xFFF9F3E9))),
                textColor = Color(0xFF3E3024),
                borderColor = Color(0xFFD2C1AC),
                particleColor = Color(0xFFE2C9A8)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF2B385C), Color(0xFF222D4C))),
                textColor = Color(0xFFDCE6FF),
                borderColor = Color(0x527B96D4),
                particleColor = Color(0xFF6B85C9)
            )
        }
        4 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFFDF0D5), Color(0xFFF6DCA8))),
                textColor = Color(0xFF4F3418),
                borderColor = Color(0xFFDDBB83),
                particleColor = Color(0xFFE6B668)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF384C7F), Color(0xFF2D3E6A))),
                textColor = Color(0xFFE6EEFF),
                borderColor = Color(0x668AA6EC),
                particleColor = Color(0xFF7B98E8)
            )
        }
        8 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFF6AB68), Color(0xFFEA8A42))),
                textColor = Color.White,
                borderColor = Color(0x4DFFFFFF),
                particleColor = Color(0xFFF39C52)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF4163BA), Color(0xFF344FA0))),
                textColor = Color.White,
                borderColor = Color(0x40FFFFFF),
                particleColor = Color(0xFF567BE0)
            )
        }
        16 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFF58549), Color(0xFFE4632E))),
                textColor = Color.White,
                borderColor = Color(0x4DFFFFFF),
                particleColor = Color(0xFFF0753D)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF547EE8), Color(0xFF4268CC))),
                textColor = Color.White,
                borderColor = Color(0x45FFFFFF),
                particleColor = Color(0xFF608BFF)
            )
        }
        32 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFF4654F), Color(0xFFDC413A))),
                textColor = Color.White,
                glowColor = Color(0x3DF4654F),
                borderColor = Color(0x4DFFFFFF),
                particleColor = Color(0xFFF4654F)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF6D78FF), Color(0xFF5660EA))),
                textColor = Color.White,
                glowColor = Color(0x406D78FF),
                borderColor = Color(0x4DFFFFFF),
                particleColor = Color(0xFF7B85FF)
            )
        }
        64 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFE53E70), Color(0xFFC5235B))),
                textColor = Color.White,
                glowColor = Color(0x47E53E70),
                borderColor = Color(0x52FFFFFF),
                particleColor = Color(0xFFE53E70)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF9868FF), Color(0xFF7B4BE8))),
                textColor = Color.White,
                glowColor = Color(0x4D9868FF),
                borderColor = Color(0x52FFFFFF),
                particleColor = Color(0xFFA276FF)
            )
        }
        128 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFFCC55EE), Color(0xFFA936D2))),
            textColor = Color.White,
            glowColor = Color(0x5EC04AE0),
            borderColor = Color(0x59FFFFFF),
            particleColor = Color(0xFFD060F2)
        )
        256 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFFF45BB0), Color(0xFFD93B94))),
            textColor = Color.White,
            glowColor = Color(0x5EEF4FA6),
            borderColor = Color(0x59FFFFFF),
            particleColor = Color(0xFFF768B8)
        )
        512 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFFFF7A5C), Color(0xFFE84C42))),
            textColor = Color.White,
            glowColor = Color(0x66FF6A5A),
            borderColor = Color(0x66FFFFFF),
            particleColor = Color(0xFFFF7A5C),
            hasSheen = true
        )
        1024 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFFFFBC52), Color(0xFFFF8636))),
            textColor = Color(0xFF2B1500),
            glowColor = Color(0x73FF963C),
            borderColor = Color(0x80FFF4D6),
            particleColor = Color(0xFFFFB347),
            hasSheen = true
        )
        2048 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFFFFE56B), Color(0xFFFFA826))),
            textColor = Color(0xFF2A1800),
            glowColor = Color(0xA6FFBE3C),
            borderColor = Color(0x99FFFBEB),
            particleColor = Color(0xFFFFD84D),
            hasSheen = true
        )
        4096 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFF7CF5D0), Color(0xFF1CC9E8))),
            textColor = Color(0xFF032018),
            glowColor = Color(0x8C22D3EE),
            borderColor = Color(0x8CFFFFFF),
            particleColor = Color(0xFF38E8D6),
            hasSheen = true
        )
        8192 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFFB2BFFF), Color(0xFF7C5CFF))),
            textColor = Color.White,
            glowColor = Color(0x997C5CFF),
            borderColor = Color(0x8CFFFFFF),
            particleColor = Color(0xFFA5B4FC),
            hasSheen = true
        )
        else -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFF253258), Color(0xFF101833))),
            textColor = Color(0xFFE0E8FF),
            glowColor = Color(0x667C5CFF),
            borderColor = Color(0x997C5CFF),
            particleColor = Color(0xFF7C5CFF),
            hasSheen = true
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = useLight
            insetsController.isAppearanceLightNavigationBars = useLight
        }
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
