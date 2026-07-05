package com.nova.app.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

object NovaPalette {
    val Purple50 = Color(0xFF8B5CF6)
    val Purple60 = Color(0xFFA855F7)
    val Purple70 = Color(0xFFD946EF)
    val AccentPink = Color(0xFFFF4D9D)
    val Success = Color(0xFF22C55E)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)

    val Background = Color(0xFF09090B)
    val BackgroundAlt = Color(0xFF111118)
    val BackgroundAlt2 = Color(0xFF161622)
    val Card = Color(0xFF1A1A27)
    val CardAlt = Color(0xFF202032)
    val Border = Color(0x14FFFFFF)
    val BorderStrong = Color(0x1FFFFFFF)

    val TextPrimary = Color(0xFFF7F7FB)
    val TextSecondary = Color(0xFFB5B7C8)
    val TextMuted = Color(0xFF8D90A7)
    val LightBackground = Color(0xFFF6F4FB)
    val LightBackgroundAlt = Color(0xFFFFFFFF)
    val LightCard = Color(0xFFF1EDF8)
    val LightCardAlt = Color(0xFFFFFFFF)
    val LightBorder = Color(0x1A0B1020)
    val LightTextPrimary = Color(0xFF12131B)
    val LightTextSecondary = Color(0xFF4D5166)
}

object NovaDimens {
    val Grid = 8.dp
    val Spacing8 = 8.dp
    val Spacing16 = 16.dp
    val Spacing24 = 24.dp
    val Spacing32 = 32.dp
    val Spacing48 = 48.dp
    val CardRadius = 24.dp
    val LargeRadius = 28.dp
    val PillRadius = 999.dp
    val TouchTarget = 44.dp
}

val NovaShapes = Shapes(
    extraSmall = RoundedCornerShape(16.dp),
    small = RoundedCornerShape(20.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

private val NovaFontFamily = FontFamily.SansSerif

val NovaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 60.sp,
        letterSpacing = (-1.2).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.9).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.4).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.2).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = NovaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
    ),
)

private val NovaDarkColors = darkColorScheme(
    primary = NovaPalette.Purple50,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2B184E),
    onPrimaryContainer = Color(0xFFF2EAFE),
    secondary = NovaPalette.AccentPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3D1230),
    onSecondaryContainer = Color(0xFFFFE8F2),
    tertiary = NovaPalette.Purple70,
    onTertiary = Color.White,
    background = NovaPalette.Background,
    onBackground = NovaPalette.TextPrimary,
    surface = NovaPalette.Card,
    onSurface = NovaPalette.TextPrimary,
    surfaceVariant = NovaPalette.BackgroundAlt2,
    onSurfaceVariant = NovaPalette.TextSecondary,
    outline = NovaPalette.Border,
    outlineVariant = NovaPalette.BorderStrong,
    error = NovaPalette.Error,
    onError = Color.White,
    errorContainer = Color(0xFF3E1418),
    onErrorContainer = Color(0xFFFFECEC),
)

private val NovaLightColors = lightColorScheme(
    primary = NovaPalette.Purple60,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8DDFE),
    onPrimaryContainer = Color(0xFF21133E),
    secondary = NovaPalette.AccentPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDEA),
    onSecondaryContainer = Color(0xFF43152E),
    tertiary = NovaPalette.Purple70,
    onTertiary = Color.White,
    background = NovaPalette.LightBackground,
    onBackground = NovaPalette.LightTextPrimary,
    surface = NovaPalette.LightCard,
    onSurface = NovaPalette.LightTextPrimary,
    surfaceVariant = NovaPalette.LightBackgroundAlt,
    onSurfaceVariant = NovaPalette.LightTextSecondary,
    outline = NovaPalette.LightBorder,
    outlineVariant = Color(0x1A5B5E74),
    error = NovaPalette.Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFE2E5),
    onErrorContainer = Color(0xFF460C12),
)

fun novaPrimaryGradient(): Brush = Brush.linearGradient(
    colors = listOf(NovaPalette.Purple50, NovaPalette.Purple60, NovaPalette.Purple70),
)

fun novaAccentGradient(): Brush = Brush.linearGradient(
    colors = listOf(NovaPalette.AccentPink, NovaPalette.Purple70, NovaPalette.Purple50),
)

@Composable
fun novaGlassGradient(): Brush = Brush.linearGradient(
    colors = listOf(
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
        Color.Transparent,
    ),
)

@Composable
fun NovaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme: ColorScheme = if (darkTheme) NovaDarkColors else NovaLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NovaTypography,
        shapes = NovaShapes,
        content = content,
    )
}
