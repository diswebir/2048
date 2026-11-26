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
import com.example.model.ColorPalette
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
    val palette: ColorPalette,
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
    val toastBorder: Color,
    val confettiColors: List<Color>
)

fun palettePreviewSwatches(palette: ColorPalette): List<Color> = when (palette) {
    ColorPalette.NUDE_MOCHA -> listOf(
        Color(0xFFF8F1E7),
        Color(0xFFE7CEB3),
        Color(0xFFC5936B),
        Color(0xFF7B4C33)
    )
    ColorPalette.PASTEL_BLUSH -> listOf(
        Color(0xFFFBF1F1),
        Color(0xFFF0D2D2),
        Color(0xFFD49B9E),
        Color(0xFF8C535B)
    )
    ColorPalette.PASTEL_SAGE -> listOf(
        Color(0xFFF3F6F0),
        Color(0xFFD5E1CD),
        Color(0xFF97B28E),
        Color(0xFF536F4E)
    )
    ColorPalette.PASTEL_LAVENDER -> listOf(
        Color(0xFFF6F3FA),
        Color(0xFFDED3EC),
        Color(0xFFAE99CA),
        Color(0xFF6B5687)
    )
    ColorPalette.PASTEL_PEACH -> listOf(
        Color(0xFFFDF3EC),
        Color(0xFFF4D5BF),
        Color(0xFFDF9E78),
        Color(0xFF94573B)
    )
}

private fun buildAppColors(palette: ColorPalette, isLight: Boolean): AppColors {
    return when (palette) {
        // 1. NUDE MOCHA (کرم و موکا — Porcelain Cream, Oat Milk, Caramel & Mocha Brown)
        ColorPalette.NUDE_MOCHA -> if (isLight) {
            AppColors(
                isLight = true,
                palette = palette,
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
                toastBorder = Color(0x40DEC4A8),
                confettiColors = listOf(
                    Color(0xFFF6E7D4), Color(0xFFE7C49A), Color(0xFFCF9A6E),
                    Color(0xFFB57E56), Color(0xFF8E5D3E), Color(0xFFDDB792)
                )
            )
        } else {
            AppColors(
                isLight = false,
                palette = palette,
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
                toastBorder = Color(0x3AEED9C4),
                confettiColors = listOf(
                    Color(0xFFF6E7D4), Color(0xFFE7C49A), Color(0xFFCF9A6E),
                    Color(0xFFB57E56), Color(0xFF8E5D3E), Color(0xFFDDB792)
                )
            )
        }

        // 2. PASTEL BLUSH (رز پاستیلی — Soft Blush Cream, Dusty Rose, Rosewood & Velvet Mauve)
        ColorPalette.PASTEL_BLUSH -> if (isLight) {
            AppColors(
                isLight = true,
                palette = palette,
                bgBase = Color(0xFFFDF7F7),
                bgMid = Color(0xFFF6EAEA),
                bgGlowPrimary = Color(0x38E2B4B8),
                bgGlowSecondary = Color(0x30F0CFD0),
                bgGlowTertiary = Color(0x28C99197),
                text = Color(0xFF3E2629),
                muted = Color(0xFF88676C),
                line = Color(0x247A4E55),
                lineStrong = Color(0x407A4E55),
                glass = Color(0xCEFFF9F9),
                glass2 = Color(0xEDFBF0F0),
                glassHover = Color(0xFFFFFCFC),
                good = Color(0xFFA85D69),
                danger = Color(0xFFB8545A),
                dangerBg = Color(0x1FB8545A),
                dangerBorder = Color(0x52B8545A),
                dangerText = Color(0xFF943C42),
                brand = Color(0xFFB0727A),
                brand2 = Color(0xFF945861),
                brand3 = Color(0xFF864E57),
                gold = Color(0xFFC88A7A),
                goldText = Color(0xFF844D40),
                goldBgStart = Color(0x33DFADA0),
                goldBorder = Color(0x57C48B7D),
                boardBgStart = Color(0xFFDEC8C9),
                boardBgMid = Color(0xFFD3BABC),
                boardBgEnd = Color(0xFFC8ADAF),
                boardBorder = Color(0x50784F55),
                cell = Color(0xFFB99C9F),
                cellLine = Color(0x52FFF7F8),
                kbdBg = Color(0x1F7A4E55),
                overlayBg = Color(0xE6FAF0F0),
                modalBgStart = Color(0xFFFFFAFA),
                modalBgEnd = Color(0xFFF5E6E7),
                toastBg = Color(0xF54A2E33),
                toastText = Color(0xFFFFF7F8),
                toastBorder = Color(0x40E5C2C6),
                confettiColors = listOf(
                    Color(0xFFF8E3E5), Color(0xFFEBBEC2), Color(0xFFD6989E),
                    Color(0xFFBC777F), Color(0xFF93545D), Color(0xFFE5B5A6)
                )
            )
        } else {
            AppColors(
                isLight = false,
                palette = palette,
                bgBase = Color(0xFF1D1316),
                bgMid = Color(0xFF271A1E),
                bgGlowPrimary = Color(0x36C98791),
                bgGlowSecondary = Color(0x28E6BEC3),
                bgGlowTertiary = Color(0x24995C66),
                text = Color(0xFFF9ECEE),
                muted = Color(0xFFC4A3A8),
                line = Color(0x24F2D3D8),
                lineStrong = Color(0x3DF2D3D8),
                glass = Color(0x14FAE3E6),
                glass2 = Color(0x20FAE3E6),
                glassHover = Color(0x30FAE3E6),
                good = Color(0xFFE7B2BA),
                danger = Color(0xFFD6787E),
                dangerBg = Color(0x26D6787E),
                dangerBorder = Color(0x5CD6787E),
                dangerText = Color(0xFFEE9DA2),
                brand = Color(0xFFC4848D),
                brand2 = Color(0xFFE2B0B7),
                brand3 = Color(0xFF965A64),
                gold = Color(0xFFE2B2A2),
                goldText = Color(0xFFF2CFC3),
                goldBgStart = Color(0x2EE2B2A2),
                goldBorder = Color(0x52E2B2A2),
                boardBgStart = Color(0xFF342227),
                boardBgMid = Color(0xFF2B1C20),
                boardBgEnd = Color(0xFF24161A),
                boardBorder = Color(0x33F2D3D8),
                cell = Color(0x1FF7DFE3),
                cellLine = Color(0x14FFF2F4),
                kbdBg = Color(0x1EF7DFE3),
                overlayBg = Color(0xDA1D1316),
                modalBgStart = Color(0xFA37242A),
                modalBgEnd = Color(0xFA26181C),
                toastBg = Color(0xF53E282E),
                toastText = Color(0xFFFCF0F2),
                toastBorder = Color(0x3AF2D3D8),
                confettiColors = listOf(
                    Color(0xFFF8E3E5), Color(0xFFEBBEC2), Color(0xFFD6989E),
                    Color(0xFFBC777F), Color(0xFF93545D), Color(0xFFE5B5A6)
                )
            )
        }

        // 3. PASTEL SAGE (سبز مریم‌گلی — Soft Eucalyptus Cream, Matcha Pastel, Pistachio & Olive Tea)
        ColorPalette.PASTEL_SAGE -> if (isLight) {
            AppColors(
                isLight = true,
                palette = palette,
                bgBase = Color(0xFFF6F8F4),
                bgMid = Color(0xFFEAEFE6),
                bgGlowPrimary = Color(0x36AEC4A5),
                bgGlowSecondary = Color(0x30CBE0C3),
                bgGlowTertiary = Color(0x2897B08D),
                text = Color(0xFF243322),
                muted = Color(0xFF60745C),
                line = Color(0x24465C43),
                lineStrong = Color(0x40465C43),
                glass = Color(0xCEFAFDF8),
                glass2 = Color(0xEDF1F6EE),
                glassHover = Color(0xFFFCFFFA),
                good = Color(0xFF537B4E),
                danger = Color(0xFFA85B51),
                dangerBg = Color(0x1FA85B51),
                dangerBorder = Color(0x52A85B51),
                dangerText = Color(0xFF8B443B),
                brand = Color(0xFF6C8A65),
                brand2 = Color(0xFF577451),
                brand3 = Color(0xFF4B6745),
                gold = Color(0xFFB5965E),
                goldText = Color(0xFF6E572A),
                goldBgStart = Color(0x33CEB482),
                goldBorder = Color(0x57B2945B),
                boardBgStart = Color(0xFFC7D3C0),
                boardBgMid = Color(0xFFBAC8B3),
                boardBgEnd = Color(0xFFAEBDA6),
                boardBorder = Color(0x504C6247),
                cell = Color(0xFF9DAD95),
                cellLine = Color(0x52F6FBF4),
                kbdBg = Color(0x1F465C43),
                overlayBg = Color(0xE6F1F5EE),
                modalBgStart = Color(0xFFFAFDF8),
                modalBgEnd = Color(0xFFE7EFE3),
                toastBg = Color(0xF5293A27),
                toastText = Color(0xFFF5FAF3),
                toastBorder = Color(0x40B8CBAF),
                confettiColors = listOf(
                    Color(0xFFE3EDE0), Color(0xFFC2D7BA), Color(0xFF9AB691),
                    Color(0xFF77976E), Color(0xFF56744E), Color(0xFFD6BE92)
                )
            )
        } else {
            AppColors(
                isLight = false,
                palette = palette,
                bgBase = Color(0xFF131913),
                bgMid = Color(0xFF1A231A),
                bgGlowPrimary = Color(0x3386A67E),
                bgGlowSecondary = Color(0x26B2CCA9),
                bgGlowTertiary = Color(0x245C7A56),
                text = Color(0xFFEAF2E8),
                muted = Color(0xFF9FB39B),
                line = Color(0x24CCE0C8),
                lineStrong = Color(0x3DCCE0C8),
                glass = Color(0x14DCECD8),
                glass2 = Color(0x20DCECD8),
                glassHover = Color(0x30DCECD8),
                good = Color(0xFFA7CBA0),
                danger = Color(0xFFCA7B70),
                dangerBg = Color(0x26CA7B70),
                dangerBorder = Color(0x5CCA7B70),
                dangerText = Color(0xFFE49D93),
                brand = Color(0xFF83A27B),
                brand2 = Color(0xFFADC9A6),
                brand3 = Color(0xFF5D7A56),
                gold = Color(0xFFD6BA84),
                goldText = Color(0xFFEAD4A8),
                goldBgStart = Color(0x2ED6BA84),
                goldBorder = Color(0x52D6BA84),
                boardBgStart = Color(0xFF232F22),
                boardBgMid = Color(0xFF1C271C),
                boardBgEnd = Color(0xFF172017),
                boardBorder = Color(0x33CCE0C8),
                cell = Color(0x1FD8EAD4),
                cellLine = Color(0x14F0F8EE),
                kbdBg = Color(0x1ED8EAD4),
                overlayBg = Color(0xDA131913),
                modalBgStart = Color(0xFA253224),
                modalBgEnd = Color(0xFA192218),
                toastBg = Color(0xF52A3929),
                toastText = Color(0xFFEEF6EC),
                toastBorder = Color(0x3ACCE0C8),
                confettiColors = listOf(
                    Color(0xFFE3EDE0), Color(0xFFC2D7BA), Color(0xFF9AB691),
                    Color(0xFF77976E), Color(0xFF56744E), Color(0xFFD6BE92)
                )
            )
        }

        // 4. PASTEL LAVENDER (یاسی پاستیلی — Soft Oat-Lavender, Lilac Mist, Dusty Mauve & Plum Taupe)
        ColorPalette.PASTEL_LAVENDER -> if (isLight) {
            AppColors(
                isLight = true,
                palette = palette,
                bgBase = Color(0xFFF9F7FC),
                bgMid = Color(0xFFEEEAF6),
                bgGlowPrimary = Color(0x36BFAEE0),
                bgGlowSecondary = Color(0x30D8CEEC),
                bgGlowTertiary = Color(0x28A794CA),
                text = Color(0xFF2D243B),
                muted = Color(0xFF706484),
                line = Color(0x24594A73),
                lineStrong = Color(0x40594A73),
                glass = Color(0xCEFCF9FF),
                glass2 = Color(0xEDF4EFFA),
                glassHover = Color(0xFFFEFCFF),
                good = Color(0xFF7A639E),
                danger = Color(0xFFB0586A),
                dangerBg = Color(0x1FB0586A),
                dangerBorder = Color(0x52B0586A),
                dangerText = Color(0xFF8F4050),
                brand = Color(0xFF8A74AC),
                brand2 = Color(0xFF715B94),
                brand3 = Color(0xFF634D85),
                gold = Color(0xFFC2957A),
                goldText = Color(0xFF7A5138),
                goldBgStart = Color(0x33DAB29A),
                goldBorder = Color(0x57BC8E74),
                boardBgStart = Color(0xFFD1C7DF),
                boardBgMid = Color(0xFFC5B9D6),
                boardBgEnd = Color(0xFFB9ABCD),
                boardBorder = Color(0x505D4E75),
                cell = Color(0xFFA899BE),
                cellLine = Color(0x52FAF7FF),
                kbdBg = Color(0x1F594A73),
                overlayBg = Color(0xE6F5F0FA),
                modalBgStart = Color(0xFFFCF9FF),
                modalBgEnd = Color(0xFFEDE6F6),
                toastBg = Color(0xF5342946),
                toastText = Color(0xFFF9F6FD),
                toastBorder = Color(0x40CBBEE0),
                confettiColors = listOf(
                    Color(0xFFEBE4F5), Color(0xFFD1C2E8), Color(0xFFB29DD2),
                    Color(0xFF937BB8), Color(0xFF6F5894), Color(0xFFE0B8A8)
                )
            )
        } else {
            AppColors(
                isLight = false,
                palette = palette,
                bgBase = Color(0xFF16131D),
                bgMid = Color(0xFF1F1B29),
                bgGlowPrimary = Color(0x369C86C2),
                bgGlowSecondary = Color(0x28C6B6E2),
                bgGlowTertiary = Color(0x24735D99),
                text = Color(0xFFF1ECF9),
                muted = Color(0xFFB0A3C4),
                line = Color(0x24DDD2F0),
                lineStrong = Color(0x3DDDD2F0),
                glass = Color(0x14E8E0F7),
                glass2 = Color(0x20E8E0F7),
                glassHover = Color(0x30E8E0F7),
                good = Color(0xFFC2AEE3),
                danger = Color(0xFFD47A8C),
                dangerBg = Color(0x26D47A8C),
                dangerBorder = Color(0x5CD47A8C),
                dangerText = Color(0xFFEC9EAD),
                brand = Color(0xFF9E88C4),
                brand2 = Color(0xFFC5B3E6),
                brand3 = Color(0xFF745E9A),
                gold = Color(0xFFE0B79E),
                goldText = Color(0xFFF2D3BF),
                goldBgStart = Color(0x2EE0B79E),
                goldBorder = Color(0x52E0B79E),
                boardBgStart = Color(0xFF2A2336),
                boardBgMid = Color(0xFF221C2C),
                boardBgEnd = Color(0xFF1C1724),
                boardBorder = Color(0x33DDD2F0),
                cell = Color(0x1FE5DCF5),
                cellLine = Color(0x14F6F2FC),
                kbdBg = Color(0x1EE5DCF5),
                overlayBg = Color(0xDA16131D),
                modalBgStart = Color(0xFA2C2538),
                modalBgEnd = Color(0xFA1E1927),
                toastBg = Color(0xF5322A40),
                toastText = Color(0xFFF4EFFB),
                toastBorder = Color(0x3ADDD2F0),
                confettiColors = listOf(
                    Color(0xFFEBE4F5), Color(0xFFD1C2E8), Color(0xFFB29DD2),
                    Color(0xFF937BB8), Color(0xFF6F5894), Color(0xFFE0B8A8)
                )
            )
        }

        // 5. PASTEL PEACH (هلویی و ماسه‌ای — Warm Apricot Milk, Pastel Peach, Terracotta Nude & Cinnamon Sand)
        ColorPalette.PASTEL_PEACH -> if (isLight) {
            AppColors(
                isLight = true,
                palette = palette,
                bgBase = Color(0xFFFDF7F3),
                bgMid = Color(0xFFF7EAE1),
                bgGlowPrimary = Color(0x38EAB596),
                bgGlowSecondary = Color(0x30F5CEB8),
                bgGlowTertiary = Color(0x29D69775),
                text = Color(0xFF3E261B),
                muted = Color(0xFF8A6756),
                line = Color(0x247A4E3A),
                lineStrong = Color(0x407A4E3A),
                glass = Color(0xCEFFFAF7),
                glass2 = Color(0xEDFCF1EA),
                glassHover = Color(0xFFFFFCFA),
                good = Color(0xFFA8603B),
                danger = Color(0xFFB85448),
                dangerBg = Color(0x1FB85448),
                dangerBorder = Color(0x52B85448),
                dangerText = Color(0xFF943C32),
                brand = Color(0xFFB87454),
                brand2 = Color(0xFF9C5B3D),
                brand3 = Color(0xFF8C4E32),
                gold = Color(0xFFCC8D52),
                goldText = Color(0xFF825021),
                goldBgStart = Color(0x33E3AD78),
                goldBorder = Color(0x57C68B53),
                boardBgStart = Color(0xFFE0C8B8),
                boardBgMid = Color(0xFFD5B9A7),
                boardBgEnd = Color(0xFFCAAB97),
                boardBorder = Color(0x50784F3B),
                cell = Color(0xFFBB9B87),
                cellLine = Color(0x52FFF8F4),
                kbdBg = Color(0x1F7A4E3A),
                overlayBg = Color(0xE6FBF0E9),
                modalBgStart = Color(0xFFFFFAF7),
                modalBgEnd = Color(0xFFF6E6DC),
                toastBg = Color(0xF54A2D20),
                toastText = Color(0xFFFFF8F4),
                toastBorder = Color(0x40E8C2AC),
                confettiColors = listOf(
                    Color(0xFFFBE6D8), Color(0xFFF2C2A5), Color(0xFFE09C76),
                    Color(0xFFC67A54), Color(0xFF9A5637), Color(0xFFEAC28E)
                )
            )
        } else {
            AppColors(
                isLight = false,
                palette = palette,
                bgBase = Color(0xFF1C130F),
                bgMid = Color(0xFF261A14),
                bgGlowPrimary = Color(0x36CE8968),
                bgGlowSecondary = Color(0x28EBC0A8),
                bgGlowTertiary = Color(0x249E5D3F),
                text = Color(0xFFFAEDE7),
                muted = Color(0xFFC7A494),
                line = Color(0x24F5D5C4),
                lineStrong = Color(0x3DF5D5C4),
                glass = Color(0x14FCE4D8),
                glass2 = Color(0x20FCE4D8),
                glassHover = Color(0x30FCE4D8),
                good = Color(0xFFEAB699),
                danger = Color(0xFFD6786B),
                dangerBg = Color(0x26D6786B),
                dangerBorder = Color(0x5CD6786B),
                dangerText = Color(0xFFEE9E93),
                brand = Color(0xFFC98362),
                brand2 = Color(0xFFE8B196),
                brand3 = Color(0xFF9C5B3D),
                gold = Color(0xFFE6B67E),
                goldText = Color(0xFFF5D2A6),
                goldBgStart = Color(0x2EE6B67E),
                goldBorder = Color(0x52E6B67E),
                boardBgStart = Color(0xFF33221B),
                boardBgMid = Color(0xFF2A1C16),
                boardBgEnd = Color(0xFF231611),
                boardBorder = Color(0x33F5D5C4),
                cell = Color(0x1FFAE0D2),
                cellLine = Color(0x14FFF3EC),
                kbdBg = Color(0x1EFAE0D2),
                overlayBg = Color(0xDA1C130F),
                modalBgStart = Color(0xFA36241C),
                modalBgEnd = Color(0xFA251812),
                toastBg = Color(0xF53D281F),
                toastText = Color(0xFFFDF0EA),
                toastBorder = Color(0x3AF5D5C4),
                confettiColors = listOf(
                    Color(0xFFFBE6D8), Color(0xFFF2C2A5), Color(0xFFE09C76),
                    Color(0xFFC67A54), Color(0xFF9A5637), Color(0xFFEAC28E)
                )
            )
        }
    }
}

val LocalAppColors = staticCompositionLocalOf { buildAppColors(ColorPalette.NUDE_MOCHA, isLight = true) }

@Immutable
data class TileVisualStyle(
    val background: Brush,
    val textColor: Color,
    val glowColor: Color = Color.Transparent,
    val borderColor: Color? = null,
    val particleColor: Color = Color(0xFFC2956E),
    val hasSheen: Boolean = false
)

private data class TilePaletteStep(
    val start: Color,
    val end: Color,
    val text: Color,
    val border: Color,
    val particle: Color,
    val glow: Color = Color.Transparent,
    val sheen: Boolean = false
)

fun tileStyleFor(
    value: Int,
    isLight: Boolean,
    palette: ColorPalette = ColorPalette.NUDE_MOCHA
): TileVisualStyle {
    val step = when (palette) {
        ColorPalette.NUDE_MOCHA -> mochaTileStep(value, isLight)
        ColorPalette.PASTEL_BLUSH -> blushTileStep(value, isLight)
        ColorPalette.PASTEL_SAGE -> sageTileStep(value, isLight)
        ColorPalette.PASTEL_LAVENDER -> lavenderTileStep(value, isLight)
        ColorPalette.PASTEL_PEACH -> peachTileStep(value, isLight)
    }
    return TileVisualStyle(
        background = Brush.linearGradient(listOf(step.start, step.end)),
        textColor = step.text,
        glowColor = step.glow,
        borderColor = step.border,
        particleColor = step.particle,
        hasSheen = step.sheen
    )
}

private fun mochaTileStep(value: Int, isLight: Boolean): TilePaletteStep = when (value) {
    2 -> if (isLight) TilePaletteStep(Color(0xFFFFFDF9), Color(0xFFF7EFE4), Color(0xFF4A3728), Color(0xFFD4C0AA), Color(0xFFE6D3BC))
    else TilePaletteStep(Color(0xFF46362A), Color(0xFF392B21), Color(0xFFF5EAE0), Color(0x5CE2CCB4), Color(0xFFA58870))
    4 -> if (isLight) TilePaletteStep(Color(0xFFF5E5D1), Color(0xFFE9D2B7), Color(0xFF493322), Color(0xFFCDB294), Color(0xFFDDC09E))
    else TilePaletteStep(Color(0xFF5A4535), Color(0xFF4B382A), Color(0xFFFAF0E6), Color(0x66E8D0B6), Color(0xFFB7967C))
    8 -> if (isLight) TilePaletteStep(Color(0xFFE8C9AB), Color(0xFFDBB48F), Color(0xFF3E291A), Color(0xFFC49B74), Color(0xFFDFB893))
    else TilePaletteStep(Color(0xFF705440), Color(0xFF5D4433), Color(0xFFFFF6EE), Color(0x52F4DFC8), Color(0xFFC7A183))
    16 -> if (isLight) TilePaletteStep(Color(0xFFDFB28A), Color(0xFFCE9A6D), Color(0xFFFFFCF8), Color(0x4DFFF8F0), Color(0xFFD8A77C))
    else TilePaletteStep(Color(0xFF86634A), Color(0xFF71503A), Color(0xFFFFF8F2), Color(0x4DFFF3E6), Color(0xFFD0A27D))
    32 -> if (isLight) TilePaletteStep(Color(0xFFCE966A), Color(0xFFBA7E52), Color(0xFFFFFCF8), Color(0x4DFFF8F0), Color(0xFFCA9064), Color(0x38C68D61))
    else TilePaletteStep(Color(0xFF9C7050), Color(0xFF855D40), Color(0xFFFFF9F3), Color(0x52FFF3E6), Color(0xFFD7A680), Color(0x3B9C7050))
    64 -> if (isLight) TilePaletteStep(Color(0xFFBE7D56), Color(0xFFA76540), Color(0xFFFFFCF8), Color(0x52FFF8F0), Color(0xFFBD7B54), Color(0x40B4714B))
    else TilePaletteStep(Color(0xFFB07C59), Color(0xFF976444), Color(0xFFFFFCF8), Color(0x57FFF3E6), Color(0xFFE0AC85), Color(0x45B07C59))
    128 -> if (isLight) TilePaletteStep(Color(0xFFA86A45), Color(0xFF8F5534), Color(0xFFFFFBF6), Color(0x59FFF4E8), Color(0xFFC68961), Color(0x4AA0613D))
    else TilePaletteStep(Color(0xFFBA865D), Color(0xFF9E6D47), Color(0xFFFFFCF8), Color(0x5EFFF3E6), Color(0xFFE4B58C), Color(0x4DBA865D))
    256 -> if (isLight) TilePaletteStep(Color(0xFF915738), Color(0xFF794429), Color(0xFFFFF8F2), Color(0x59FFF2E5), Color(0xFFB57954), Color(0x528B5032))
    else TilePaletteStep(Color(0xFFC8956A), Color(0xFFAC7950), Color(0xFF2A1A0F), Color(0x6EFFF4E6), Color(0xFFE8BC93), Color(0x57C8956A))
    512 -> if (isLight) TilePaletteStep(Color(0xFF7B472D), Color(0xFF633620), Color(0xFFFFF6EE), Color(0x66FFEBD9), Color(0xFFC9956C), Color(0x59744128), true)
    else TilePaletteStep(Color(0xFFD5A576), Color(0xFFB98758), Color(0xFF27170C), Color(0x7AFFF6EA), Color(0xFFECC69D), Color(0x61D5A576), true)
    1024 -> if (isLight) TilePaletteStep(Color(0xFF623621), Color(0xFF4A2616), Color(0xFFFCEEE0), Color(0x75F0D2B2), Color(0xFFD9AE84), Color(0x615D331F), true)
    else TilePaletteStep(Color(0xFFE1B78B), Color(0xFFC59667), Color(0xFF25150A), Color(0x85FFF7ED), Color(0xFFF3D4B0), Color(0x6BE1B78B), true)
    2048 -> TilePaletteStep(Color(0xFFEBC99C), Color(0xFFC7945B), Color(0xFF2D190C), Color(0xA6FFF8EE), Color(0xFFE7BD88), Color(0x8CD5A56C), true)
    4096 -> TilePaletteStep(Color(0xFF543524), Color(0xFF382115), Color(0xFFF6E0C7), Color(0x99E6C299), Color(0xFFE0B88E), Color(0x73BA8D65), true)
    8192 -> TilePaletteStep(Color(0xFF3E2518), Color(0xFF26150C), Color(0xFFFCEAD5), Color(0xB3EDCA9F), Color(0xFFECC89E), Color(0x80D5A975), true)
    else -> TilePaletteStep(Color(0xFF2C1910), Color(0xFF1A0F09), Color(0xFFFDEEDB), Color(0xCCF1D1A9), Color(0xFFF3D3AB), Color(0x8CE1B785), true)
}

private fun blushTileStep(value: Int, isLight: Boolean): TilePaletteStep = when (value) {
    2 -> if (isLight) TilePaletteStep(Color(0xFFFFFCFC), Color(0xFFF9EEEE), Color(0xFF4D3135), Color(0xFFDCC2C4), Color(0xFFECD3D5))
    else TilePaletteStep(Color(0xFF4B3338), Color(0xFF3D282C), Color(0xFFF8EBEF), Color(0x5CE5C5CB), Color(0xFFB58C93))
    4 -> if (isLight) TilePaletteStep(Color(0xFFF7E2E3), Color(0xFFEECDCB), Color(0xFF4F2F34), Color(0xFFD6B0B3), Color(0xFFE5BEC1))
    else TilePaletteStep(Color(0xFF614148), Color(0xFF50343A), Color(0xFFFCEFF1), Color(0x66EBCAD0), Color(0xFFC79BA3))
    8 -> if (isLight) TilePaletteStep(Color(0xFFECC2C5), Color(0xFFE0A9AE), Color(0xFF44252A), Color(0xFFCC959A), Color(0xFFE5B1B6))
    else TilePaletteStep(Color(0xFF785058), Color(0xFF644048), Color(0xFFFFF4F6), Color(0x52F7DBE0), Color(0xFFD4A3AC))
    16 -> if (isLight) TilePaletteStep(Color(0xFFE2A5AC), Color(0xFFD38B94), Color(0xFFFFFAFA), Color(0x4DFFF6F7), Color(0xFFDC9BA3))
    else TilePaletteStep(Color(0xFF8F5E68), Color(0xFF784C55), Color(0xFFFFF6F8), Color(0x4DFFEDF0), Color(0xFFDEA6B0))
    32 -> if (isLight) TilePaletteStep(Color(0xFFD48892), Color(0xFFC26E7A), Color(0xFFFFFAFA), Color(0x4DFFF6F7), Color(0xFFCF828C), Color(0x38CC7E88))
    else TilePaletteStep(Color(0xFFA66B77), Color(0xFF8D5762), Color(0xFFFFF8F9), Color(0x52FFEDF0), Color(0xFFE5ACBA), Color(0x3BA66B77))
    64 -> if (isLight) TilePaletteStep(Color(0xFFC46E7B), Color(0xFFAD5563), Color(0xFFFFFAFA), Color(0x52FFF6F7), Color(0xFFC26C79), Color(0x40BA6270))
    else TilePaletteStep(Color(0xFFBA7886), Color(0xFF9F5F6D), Color(0xFFFFFAFA), Color(0x57FFEDF0), Color(0xFFECB2BF), Color(0x45BA7886))
    128 -> if (isLight) TilePaletteStep(Color(0xFFAE5A68), Color(0xFF944653), Color(0xFFFFF8F9), Color(0x59FFECF0), Color(0xFFCC7F8C), Color(0x4AA65260))
    else TilePaletteStep(Color(0xFFC78593), Color(0xFFAC6B79), Color(0xFFFFFAFA), Color(0x5EFFEDF0), Color(0xFFF0BCC7), Color(0x4DC78593))
    256 -> if (isLight) TilePaletteStep(Color(0xFF964856), Color(0xFF7D3744), Color(0xFFFFF6F7), Color(0x59FFEAEF), Color(0xFFBD6E7C), Color(0x528F4250))
    else TilePaletteStep(Color(0xFFD698A5), Color(0xFFBA7B88), Color(0xFF2E161B), Color(0x6EFFF0F3), Color(0xFFF3C6D0), Color(0x57D698A5))
    512 -> if (isLight) TilePaletteStep(Color(0xFF7F3947), Color(0xFF662936), Color(0xFFFFF4F6), Color(0x66FFE2E8), Color(0xFFD18A97), Color(0x59783442), true)
    else TilePaletteStep(Color(0xFFE2ABB6), Color(0xFFC78C98), Color(0xFF2A1318), Color(0x7AFFF2F5), Color(0xFFF7D2DA), Color(0x61E2ABB6), true)
    1024 -> if (isLight) TilePaletteStep(Color(0xFF662B37), Color(0xFF4D1D27), Color(0xFFFCE8EC), Color(0x75F2C6D0), Color(0xFFE0A3AF), Color(0x615F2632), true)
    else TilePaletteStep(Color(0xFFECBEC7), Color(0xFFD29DA8), Color(0xFF261015), Color(0x85FFF5F7), Color(0xFFFBE0E6), Color(0x6BECBEC7), true)
    2048 -> TilePaletteStep(Color(0xFFF2C8B8), Color(0xFFD19582), Color(0xFF331814), Color(0xA6FFF6F2), Color(0xFFEDBCA8), Color(0x8CDCA492), true)
    4096 -> TilePaletteStep(Color(0xFF582934), Color(0xFF3B1921), Color(0xFFF8DCE2), Color(0x99EBB5C1), Color(0xFFE6B0BC), Color(0x73C28291), true)
    8192 -> TilePaletteStep(Color(0xFF421C25), Color(0xFF280F15), Color(0xFFFCE6EB), Color(0xB3F0C2CC), Color(0xFFF0C2CC), Color(0x80DA9DAB), true)
    else -> TilePaletteStep(Color(0xFF2E1219), Color(0xFF1B090E), Color(0xFFFDECEF), Color(0xCCF5CCD5), Color(0xFFF6D0D8), Color(0x8CE6AEBB), true)
}

private fun sageTileStep(value: Int, isLight: Boolean): TilePaletteStep = when (value) {
    2 -> if (isLight) TilePaletteStep(Color(0xFFFCFEFA), Color(0xFFEEF4EA), Color(0xFF2C3E2A), Color(0xFFBCCBB6), Color(0xFFD1E0CB))
    else TilePaletteStep(Color(0xFF31402F), Color(0xFF263325), Color(0xFFE8F2E6), Color(0x5CBBD1B7), Color(0xFF7D9A78))
    4 -> if (isLight) TilePaletteStep(Color(0xFFE1ECD9), Color(0xFFD0E0C6), Color(0xFF2A3D27), Color(0xFFADC2A4), Color(0xFFC2D6B8))
    else TilePaletteStep(Color(0xFF3F543D), Color(0xFF334531), Color(0xFFEEF7EC), Color(0x66C4DEC0), Color(0xFF8FAE8A))
    8 -> if (isLight) TilePaletteStep(Color(0xFFC5D8B9), Color(0xFFB1C8A3), Color(0xFF233520), Color(0xFF96B288), Color(0xFFBCD1AF))
    else TilePaletteStep(Color(0xFF4F694C), Color(0xFF40573E), Color(0xFFF4FBF2), Color(0x52D6ECD2), Color(0xFF9FC099))
    16 -> if (isLight) TilePaletteStep(Color(0xFFA7C298), Color(0xFF91AE81), Color(0xFFFAFDFA), Color(0x4DF5FBF3), Color(0xFF9DB98E))
    else TilePaletteStep(Color(0xFF5F7E5B), Color(0xFF4E6A4A), Color(0xFFF6FCF4), Color(0x4DE4F4E1), Color(0xFFAED0A8))
    32 -> if (isLight) TilePaletteStep(Color(0xFF8CAB7C), Color(0xFF769666), Color(0xFFFAFDFA), Color(0x4DF5FBF3), Color(0xFF85A475), Color(0x387FA06F))
    else TilePaletteStep(Color(0xFF70936B), Color(0xFF5D7D58), Color(0xFFF8FDF7), Color(0x52E4F4E1), Color(0xFFBCE0B6), Color(0x3B70936B))
    64 -> if (isLight) TilePaletteStep(Color(0xFF749464), Color(0xFF5E7E4F), Color(0xFFFAFDFA), Color(0x52F5FBF3), Color(0xFF729262), Color(0x406A8A5B))
    else TilePaletteStep(Color(0xFF81A67C), Color(0xFF6B8E66), Color(0xFFFAFDFA), Color(0x57E4F4E1), Color(0xFFCAEAC4), Color(0x4581A67C))
    128 -> if (isLight) TilePaletteStep(Color(0xFF607F51), Color(0xFF4B693E), Color(0xFFF8FCF6), Color(0x59EAF6E7), Color(0xFF7E9E6E), Color(0x4A577649))
    else TilePaletteStep(Color(0xFF91B68B), Color(0xFF799D74), Color(0xFFFAFDFA), Color(0x5EE4F4E1), Color(0xFFD2F0CD), Color(0x4D91B68B))
    256 -> if (isLight) TilePaletteStep(Color(0xFF4E6B40), Color(0xFF3B5530), Color(0xFFF6FBF4), Color(0x59E6F4E3), Color(0xFF6D8D5E), Color(0x5248643B))
    else TilePaletteStep(Color(0xFFA4C79E), Color(0xFF8BAE85), Color(0xFF172615), Color(0x6EEBF7E9), Color(0xFFDDF5D8), Color(0x57A4C79E))
    512 -> if (isLight) TilePaletteStep(Color(0xFF3E5832), Color(0xFF2D4323), Color(0xFFF4FAF2), Color(0x66DCEEE8), Color(0xFF83A574), Color(0x5939522E), true)
    else TilePaletteStep(Color(0xFFB7D6B1), Color(0xFF9CBE96), Color(0xFF142212), Color(0x7AF0FAF0), Color(0xFFE6FAE2), Color(0x61B7D6B1), true)
    1024 -> if (isLight) TilePaletteStep(Color(0xFF304626), Color(0xFF203218), Color(0xFFE6F4E2), Color(0x75BAD4B3), Color(0xFF98BA89), Color(0x612C4122), true)
    else TilePaletteStep(Color(0xFFC9E3C4), Color(0xFFAED0A8), Color(0xFF111E0F), Color(0x85F4FCF3), Color(0xFFEEFCEC), Color(0x6BC9E3C4), true)
    2048 -> TilePaletteStep(Color(0xFFE2CCA0), Color(0xFFBCA06B), Color(0xFF232914), Color(0xA6FAF6EC), Color(0xFFDEC492), Color(0x8CCBB07A), true)
    4096 -> TilePaletteStep(Color(0xFF283B21), Color(0xFF192614), Color(0xFFDCECD7), Color(0x99A8C6A0), Color(0xFFA8C6A0), Color(0x736E9166), true)
    8192 -> TilePaletteStep(Color(0xFF1D2C17), Color(0xFF101A0C), Color(0xFFE5F3E1), Color(0xB3B8D6B0), Color(0xFFB8D6B0), Color(0x8084A87B), true)
    else -> TilePaletteStep(Color(0xFF141F10), Color(0xFF0A1108), Color(0xFFECF7E9), Color(0xCCC6E2BE), Color(0xFFC8E4C0), Color(0x8C96BA8D), true)
}

private fun lavenderTileStep(value: Int, isLight: Boolean): TilePaletteStep = when (value) {
    2 -> if (isLight) TilePaletteStep(Color(0xFFFDFAFF), Color(0xFFF3EEF9), Color(0xFF382D4A), Color(0xFFCBC0DC), Color(0xFFDDD3EC))
    else TilePaletteStep(Color(0xFF3D334D), Color(0xFF30283E), Color(0xFFEEE8F7), Color(0x5CCBC0E0), Color(0xFF9686B3))
    4 -> if (isLight) TilePaletteStep(Color(0xFFEAE1F5), Color(0xFFDCD0EC), Color(0xFF372A4C), Color(0xFFBDB0D4), Color(0xFFD0C2E5))
    else TilePaletteStep(Color(0xFF4F4264), Color(0xFF403553), Color(0xFFF3EEFA), Color(0x66D5C8EC), Color(0xFFA898C7))
    8 -> if (isLight) TilePaletteStep(Color(0xFFD6C6EA), Color(0xFFC5B1DF), Color(0xFF2F2144), Color(0xFFA993C8), Color(0xFFCCB9E4))
    else TilePaletteStep(Color(0xFF63527D), Color(0xFF514269), Color(0xFFF8F4FC), Color(0x52E4DAFA), Color(0xFFB9A6D8))
    16 -> if (isLight) TilePaletteStep(Color(0xFFBEA8DA), Color(0xFFAC92CC), Color(0xFFFCFAFF), Color(0x4DF8F4FF), Color(0xFFB69FD3))
    else TilePaletteStep(Color(0xFF776296), Color(0xFF634F80), Color(0xFFFAF7FD), Color(0x4DF0E8FC), Color(0xFFC7B3E6))
    32 -> if (isLight) TilePaletteStep(Color(0xFFA78CC8), Color(0xFF9376B8), Color(0xFFFCFAFF), Color(0x4DF8F4FF), Color(0xFFA185C3), Color(0x389B7FBE))
    else TilePaletteStep(Color(0xFF8B73AF), Color(0xFF755E98), Color(0xFFFBFAFE), Color(0x52F0E8FC), Color(0xFFD3BFF0), Color(0x3B8B73AF))
    64 -> if (isLight) TilePaletteStep(Color(0xFF9173B6), Color(0xFF7C5DA3), Color(0xFFFCFAFF), Color(0x52F8F4FF), Color(0xFF8F71B4), Color(0x408769AC))
    else TilePaletteStep(Color(0xFF9E85C4), Color(0xFF866DAD), Color(0xFFFCFAFF), Color(0x57F0E8FC), Color(0xFFDFCCF8), Color(0x459E85C4))
    128 -> if (isLight) TilePaletteStep(Color(0xFF7D5FA3), Color(0xFF684B8E), Color(0xFFFAF7FE), Color(0x59EFE8FC), Color(0xFF9A7EC0), Color(0x4A74569A))
    else TilePaletteStep(Color(0xFFAE96D2), Color(0xFF957CBA), Color(0xFFFCFAFF), Color(0x5EF0E8FC), Color(0xFFE6D5FC), Color(0x4DAE96D2))
    256 -> if (isLight) TilePaletteStep(Color(0xFF694C8F), Color(0xFF553A79), Color(0xFFF8F4FD), Color(0x59ECE4FA), Color(0xFF896CAF), Color(0x52624687))
    else TilePaletteStep(Color(0xFFBEA8DF), Color(0xFFA48CC8), Color(0xFF211633), Color(0x6EF4EEFC), Color(0xFFEEE0FC), Color(0x57BEA8DF))
    512 -> if (isLight) TilePaletteStep(Color(0xFF563B7A), Color(0xFF432B64), Color(0xFFF6F2FC), Color(0x66E4DAF7), Color(0xFFA084C6), Color(0x59503673), true)
    else TilePaletteStep(Color(0xFFCEBBE9), Color(0xFFB49FD3), Color(0xFF1D132E), Color(0x7AF6F2FD), Color(0xFFF4EAFC), Color(0x61CEBBE9), true)
    1024 -> if (isLight) TilePaletteStep(Color(0xFF432C63), Color(0xFF311E4D), Color(0xFFECE2F9), Color(0x75C9B8E6), Color(0xFFB49AD8), Color(0x613E285C), true)
    else TilePaletteStep(Color(0xFFDDCDEE), Color(0xFFC4B0DE), Color(0xFF191029), Color(0x85F9F6FE), Color(0xFFF8F2FE), Color(0x6BDDCDEE), true)
    2048 -> TilePaletteStep(Color(0xFFEAC7B0), Color(0xFFC69478), Color(0xFF2B1924), Color(0xA6FFF7F2), Color(0xFFE6BCA2), Color(0x8CD4A488), true)
    4096 -> TilePaletteStep(Color(0xFF392554), Color(0xFF26173B), Color(0xFFE4D8F6), Color(0x99BAAAEC), Color(0xFFC0AEEC), Color(0x738A72B5), true)
    8192 -> TilePaletteStep(Color(0xFF2A1A40), Color(0xFF190E29), Color(0xFFEDE2FA), Color(0xB3CBBCEB), Color(0xFFCCBCEC), Color(0x809E86C9), true)
    else -> TilePaletteStep(Color(0xFF1D112E), Color(0xFF10091B), Color(0xFFF3ECFC), Color(0xCCD8CBF2), Color(0xFFDAD0F4), Color(0x8CB098DC), true)
}

private fun peachTileStep(value: Int, isLight: Boolean): TilePaletteStep = when (value) {
    2 -> if (isLight) TilePaletteStep(Color(0xFFFFFCFA), Color(0xFFF9EFE9), Color(0xFF4D3022), Color(0xFFDEC1B0), Color(0xFFEED3C3))
    else TilePaletteStep(Color(0xFF4A3329), Color(0xFF3C281F), Color(0xFFF8EAE2), Color(0x5CE6C6B5), Color(0xFFB58B76))
    4 -> if (isLight) TilePaletteStep(Color(0xFFF9E3D4), Color(0xFFF0CEB8), Color(0xFF4F2E1E), Color(0xFFD9B097), Color(0xFFE8C0A6))
    else TilePaletteStep(Color(0xFF604133), Color(0xFF4F3428), Color(0xFFFCF0E8), Color(0x66ECCBB8), Color(0xFFC79A82))
    8 -> if (isLight) TilePaletteStep(Color(0xFFF0C4A8), Color(0xFFE4AC89), Color(0xFF442415), Color(0xFFCE9774), Color(0xFFE9B493))
    else TilePaletteStep(Color(0xFF77503E), Color(0xFF634031), Color(0xFFFFF5EE), Color(0x52F8DDD0), Color(0xFFD4A288))
    16 -> if (isLight) TilePaletteStep(Color(0xFFE6A782), Color(0xFFD88D63), Color(0xFFFFFBF8), Color(0x4DFFF7F2), Color(0xFFE09E77))
    else TilePaletteStep(Color(0xFF8E5E49), Color(0xFF774C3A), Color(0xFFFFF7F2), Color(0x4DFFEEF5), Color(0xFFDEA588))
    32 -> if (isLight) TilePaletteStep(Color(0xFFD98A62), Color(0xFFC77046), Color(0xFFFFFBF8), Color(0x4DFFF7F2), Color(0xFFD4845B), Color(0x38D08057))
    else TilePaletteStep(Color(0xFFA56B52), Color(0xFF8C5740), Color(0xFFFFF9F5), Color(0x52FFF0E8), Color(0xFFE6AB8E), Color(0x3BA56B52))
    64 -> if (isLight) TilePaletteStep(Color(0xFFC97049), Color(0xFFB25732), Color(0xFFFFFBF8), Color(0x52FFF7F2), Color(0xFFC76E47), Color(0x40BF643E))
    else TilePaletteStep(Color(0xFFB9785C), Color(0xFF9E5F44), Color(0xFFFFFBF8), Color(0x57FFF0E8), Color(0xFFEDB195), Color(0x45B9785C))
    128 -> if (isLight) TilePaletteStep(Color(0xFFB35B37), Color(0xFF994726), Color(0xFFFFF9F5), Color(0x59FFEEE5), Color(0xFFD1805C), Color(0x4AAB5330))
    else TilePaletteStep(Color(0xFFC68567), Color(0xFFAB6B4E), Color(0xFFFFFBF8), Color(0x5EFFF0E8), Color(0xFFF1BB9F), Color(0x4DC68567))
    256 -> if (isLight) TilePaletteStep(Color(0xFF9B4928), Color(0xFF81381B), Color(0xFFFFF7F2), Color(0x59FFEBE0), Color(0xFFC26F4C), Color(0x52944323))
    else TilePaletteStep(Color(0xFFD5987A), Color(0xFFB97B5E), Color(0xFF2E160C), Color(0x6EFFF2EB), Color(0xFFF4C5AC), Color(0x57D5987A))
    512 -> if (isLight) TilePaletteStep(Color(0xFF833A1D), Color(0xFF692A11), Color(0xFFFFF5EE), Color(0x66FFE4D6), Color(0xFFD68B69), Color(0x597C3519), true)
    else TilePaletteStep(Color(0xFFE1AB90), Color(0xFFC68C70), Color(0xFF2A130A), Color(0x7AFFF4EE), Color(0xFFF8D1BC), Color(0x61E1AB90), true)
    1024 -> if (isLight) TilePaletteStep(Color(0xFF6A2C13), Color(0xFF501E0A), Color(0xFFFCEAE0), Color(0x75F4C8B0), Color(0xFFE4A484), Color(0x61632710), true)
    else TilePaletteStep(Color(0xFFECBEA6), Color(0xFFD29D84), Color(0xFF261007), Color(0x85FFF7F2), Color(0xFFFBDFCF), Color(0x6BECBEA6), true)
    2048 -> TilePaletteStep(Color(0xFFF2CC96), Color(0xFFCE9856), Color(0xFF2E190A), Color(0xA6FFF8EE), Color(0xFFEEC082), Color(0x8CDAA666), true)
    4096 -> TilePaletteStep(Color(0xFF5A2916), Color(0xFF3D190B), Color(0xFFF8DDD0), Color(0x99ECB69B), Color(0xFFE8B094), Color(0x73C68364), true)
    8192 -> TilePaletteStep(Color(0xFF431C0D), Color(0xFF290F05), Color(0xFFFCE7DC), Color(0xB3F2C3AC), Color(0xFFF2C3AC), Color(0x80DE9E7E), true)
    else -> TilePaletteStep(Color(0xFF2F1207), Color(0xFF1C0903), Color(0xFFFDEEE6), Color(0xCCF6CEB8), Color(0xFFF7D1BC), Color(0x8CEAA888), true)
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
    palette: ColorPalette = ColorPalette.NUDE_MOCHA,
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

    val appColors = buildAppColors(palette = palette, isLight = useLight)
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
