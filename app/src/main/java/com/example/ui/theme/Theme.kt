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
    val bgGlowPrimary: Color,
    val bgGlowSecondary: Color,
    val bgGlowTertiary: Color,
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

// Cozy Dark Roast, Espresso & Warm Cocoa Nude Palette
private val DarkAppColors = AppColors(
    isLight = false,
    bgBase = Color(0xFF1A1410),
    bgMid = Color(0xFF231B15),
    bgGlowPrimary = Color(0x36BA8E6B),
    bgGlowSecondary = Color(0x28DFC2A3),
    bgGlowTertiary = Color(0x248F6041),
    text = Color(0xFFF6EDE3),
    muted = Color(0xFFBBA693),
    line = Color(0x24EED9C4),
    lineStrong = Color(0x3DEED9C4),
    glass = Color(0x14F7E7D6),
    glass2 = Color(0x20F7E7D6),
    glassHover = Color(0x30F7E7D6),
    good = Color(0xFFE3B98A),
    danger = Color(0xFFC97B67),
    dangerBg = Color(0x26C97B67),
    dangerBorder = Color(0x5CC97B67),
    dangerText = Color(0xFFE59D8B),
    brand = Color(0xFFB58865),
    brand2 = Color(0xFFD8B490),
    brand3 = Color(0xFF8C6144),
    gold = Color(0xFFDFB580),
    goldText = Color(0xFFEDD0A6),
    goldBgStart = Color(0x2EDFB580),
    goldBorder = Color(0x52DFB580),
    boardBgStart = Color(0xFF2F241C),
    boardBgMid = Color(0xFF271D16),
    boardBgEnd = Color(0xFF211812),
    boardBorder = Color(0x33F0DAC4),
    cell = Color(0x1FF5E3D0),
    cellLine = Color(0x14FFF4E8),
    kbdBg = Color(0x1EF5E3D0),
    overlayBg = Color(0xDA1A1410),
    modalBgStart = Color(0xFA32261E),
    modalBgEnd = Color(0xFA221913),
    toastBg = Color(0xF5382A20),
    toastText = Color(0xFFF9F0E7),
    toastBorder = Color(0x3AEED9C4)
)

// Velvety Porcelain Cream, Oat Milk, Latte & Soft Mocha Nude Palette
private val LightAppColors = AppColors(
    isLight = true,
    bgBase = Color(0xFFFBF7F2),
    bgMid = Color(0xFFF3EAE0),
    bgGlowPrimary = Color(0x36D7B695),
    bgGlowSecondary = Color(0x30E7CEB5),
    bgGlowTertiary = Color(0x29C49B79),
    text = Color(0xFF3A2A1E),
    muted = Color(0xFF816B59),
    line = Color(0x246D523C),
    lineStrong = Color(0x406D523C),
    glass = Color(0xCEFDF9F4),
    glass2 = Color(0xEDFBF4EB),
    glassHover = Color(0xFFFFFDF9),
    good = Color(0xFF9A6336),
    danger = Color(0xFFA65A48),
    dangerBg = Color(0x1FA65A48),
    dangerBorder = Color(0x52A65A48),
    dangerText = Color(0xFF8B4636),
    brand = Color(0xFF9E704F),
    brand2 = Color(0xFF8A5D3B),
    brand3 = Color(0xFF7D5236),
    gold = Color(0xFFC29058),
    goldText = Color(0xFF7A5128),
    goldBgStart = Color(0x33D6AA76),
    goldBorder = Color(0x57B98A56),
    // Soft warm oatmeal-taupe tray with clearly recessed sand-driftwood cells so cream tile 2 pops cleanly
    boardBgStart = Color(0xFFD7C6B4),
    boardBgMid = Color(0xFFCCB9A5),
    boardBgEnd = Color(0xFFC2AD97),
    boardBorder = Color(0x506B523C),
    cell = Color(0xFFB39D86),
    cellLine = Color(0x52FFF9F2),
    kbdBg = Color(0x1F6D523C),
    overlayBg = Color(0xE6F8F1E8),
    modalBgStart = Color(0xFFFDF9F3),
    modalBgEnd = Color(0xFFF2E7DA),
    toastBg = Color(0xF5423023),
    toastText = Color(0xFFFDF7F0),
    toastBorder = Color(0x40DEC4A8)
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

@Immutable
data class TileVisualStyle(
    val background: Brush,
    val textColor: Color,
    val glowColor: Color = Color.Transparent,
    val borderColor: Color? = null,
    val particleColor: Color = Color(0xFFC2956E),
    val hasSheen: Boolean = false
)

fun tileStyleFor(value: Int, isLight: Boolean): TileVisualStyle {
    return when (value) {
        // 2: Porcelain Cream (Light) / Warm Cocoa (Dark)
        2 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFFFFDF9), Color(0xFFF7EFE4))),
                textColor = Color(0xFF4A3728),
                borderColor = Color(0xFFD4C0AA),
                particleColor = Color(0xFFE6D3BC)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF46362A), Color(0xFF392B21))),
                textColor = Color(0xFFF5EAE0),
                borderColor = Color(0x5CE2CCB4),
                particleColor = Color(0xFFA58870)
            )
        }

        // 4: Warm Vanilla Oat (Light) / Toasted Taupe (Dark)
        4 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFF5E5D1), Color(0xFFE9D2B7))),
                textColor = Color(0xFF493322),
                borderColor = Color(0xFFCDB294),
                particleColor = Color(0xFFDDC09E)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF5A4535), Color(0xFF4B382A))),
                textColor = Color(0xFFFAF0E6),
                borderColor = Color(0x66E8D0B6),
                particleColor = Color(0xFFB7967C)
            )
        }

        // 8: Soft Sand Biscuit (Light) / Warm Latte (Dark)
        8 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFE8C9AB), Color(0xFFDBB48F))),
                textColor = Color(0xFF3E291A),
                borderColor = Color(0xFFC49B74),
                particleColor = Color(0xFFDFB893)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF705440), Color(0xFF5D4433))),
                textColor = Color(0xFFFFF6EE),
                borderColor = Color(0x52F4DFC8),
                particleColor = Color(0xFFC7A183)
            )
        }

        // 16: Honey Almond Nude (Light) / Creamy Mocha (Dark)
        16 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFDFB28A), Color(0xFFCE9A6D))),
                textColor = Color(0xFFFFFCF8),
                borderColor = Color(0x4DFFF8F0),
                particleColor = Color(0xFFD8A77C)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF86634A), Color(0xFF71503A))),
                textColor = Color(0xFFFFF8F2),
                borderColor = Color(0x4DFFF3E6),
                particleColor = Color(0xFFD0A27D)
            )
        }

        // 32: Warm Caramel Nude
        32 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFCE966A), Color(0xFFBA7E52))),
                textColor = Color(0xFFFFFCF8),
                glowColor = Color(0x38C68D61),
                borderColor = Color(0x4DFFF8F0),
                particleColor = Color(0xFFCA9064)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF9C7050), Color(0xFF855D40))),
                textColor = Color(0xFFFFF9F3),
                glowColor = Color(0x3B9C7050),
                borderColor = Color(0x52FFF3E6),
                particleColor = Color(0xFFD7A680)
            )
        }

        // 64: Muted Cinnamon Terracotta
        64 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFBE7D56), Color(0xFFA76540))),
                textColor = Color(0xFFFFFCF8),
                glowColor = Color(0x40B4714B),
                borderColor = Color(0x52FFF8F0),
                particleColor = Color(0xFFBD7B54)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFB07C59), Color(0xFF976444))),
                textColor = Color(0xFFFFFCF8),
                glowColor = Color(0x45B07C59),
                borderColor = Color(0x57FFF3E6),
                particleColor = Color(0xFFE0AC85)
            )
        }

        // 128: Roasted Hazelnut
        128 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFA86A45), Color(0xFF8F5534))),
                textColor = Color(0xFFFFFBF6),
                glowColor = Color(0x4AA0613D),
                borderColor = Color(0x59FFF4E8),
                particleColor = Color(0xFFC68961)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFBA865D), Color(0xFF9E6D47))),
                textColor = Color(0xFFFFFCF8),
                glowColor = Color(0x4DBA865D),
                borderColor = Color(0x5EFFF3E6),
                particleColor = Color(0xFFE4B58C)
            )
        }

        // 256: Velvet Milk Chocolate (Light) / Golden Biscuit (Dark)
        256 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF915738), Color(0xFF794429))),
                textColor = Color(0xFFFFF8F2),
                glowColor = Color(0x528B5032),
                borderColor = Color(0x59FFF2E5),
                particleColor = Color(0xFFB57954)
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFC8956A), Color(0xFFAC7950))),
                textColor = Color(0xFF2A1A0F),
                glowColor = Color(0x57C8956A),
                borderColor = Color(0x6EFFF4E6),
                particleColor = Color(0xFFE8BC93)
            )
        }

        // 512: Rich Chestnut Mocha (Light) / Honey Latte (Dark)
        512 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF7B472D), Color(0xFF633620))),
                textColor = Color(0xFFFFF6EE),
                glowColor = Color(0x59744128),
                borderColor = Color(0x66FFEBD9),
                particleColor = Color(0xFFC9956C),
                hasSheen = true
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFD5A576), Color(0xFFB98758))),
                textColor = Color(0xFF27170C),
                glowColor = Color(0x61D5A576),
                borderColor = Color(0x7AFFF6EA),
                particleColor = Color(0xFFECC69D),
                hasSheen = true
            )
        }

        // 1024: Deep Dark Cocoa (Light) / Warm Cream Caramel (Dark)
        1024 -> if (isLight) {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFF623621), Color(0xFF4A2616))),
                textColor = Color(0xFFFCEEE0),
                glowColor = Color(0x615D331F),
                borderColor = Color(0x75F0D2B2),
                particleColor = Color(0xFFD9AE84),
                hasSheen = true
            )
        } else {
            TileVisualStyle(
                background = Brush.linearGradient(listOf(Color(0xFFE1B78B), Color(0xFFC59667))),
                textColor = Color(0xFF25150A),
                glowColor = Color(0x6BE1B78B),
                borderColor = Color(0x85FFF7ED),
                particleColor = Color(0xFFF3D4B0),
                hasSheen = true
            )
        }

        // 2048: Radiant Champagne Caramel Gold
        2048 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFFEBC99C), Color(0xFFC7945B))),
            textColor = Color(0xFF2D190C),
            glowColor = Color(0x8CD5A56C),
            borderColor = Color(0xA6FFF8EE),
            particleColor = Color(0xFFE7BD88),
            hasSheen = true
        )

        // 4096: Velvet Espresso Reserve
        4096 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFF543524), Color(0xFF382115))),
            textColor = Color(0xFFF6E0C7),
            glowColor = Color(0x73BA8D65),
            borderColor = Color(0x99E6C299),
            particleColor = Color(0xFFE0B88E),
            hasSheen = true
        )

        // 8192: Royal Dark Roast & Champagne
        8192 -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFF3E2518), Color(0xFF26150C))),
            textColor = Color(0xFFFCEAD5),
            glowColor = Color(0x80D5A975),
            borderColor = Color(0xB3EDCA9F),
            particleColor = Color(0xFFECC89E),
            hasSheen = true
        )

        // 16384+: Obsidian Espresso & Antique Cream
        else -> TileVisualStyle(
            background = Brush.linearGradient(listOf(Color(0xFF2C1910), Color(0xFF1A0F09))),
            textColor = Color(0xFFFDEEDB),
            glowColor = Color(0x8CE1B785),
            borderColor = Color(0xCCF1D1A9),
            particleColor = Color(0xFFF3D3AB),
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
            onPrimary = Color(0xFFFFFBF6),
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
            onPrimary = Color(0xFFFFFBF6),
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
