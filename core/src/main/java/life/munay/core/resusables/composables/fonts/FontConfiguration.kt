package life.munay.core.resusables.composables.fonts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import life.munay.core.resusables.composables.themes.extendedColorScheme

@Composable
fun defaultTextStyle() =
    TextStyle(
        fontFamily = AppFontFamily,
        platformStyle =
        PlatformTextStyle(
            includeFontPadding = false
        ),
        lineHeightStyle =
        LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

// Fallback for non-composable contexts
val defaultTextStyleFallback =
    TextStyle(
        fontFamily = AppFontFamily,
        platformStyle =
        PlatformTextStyle(
            includeFontPadding = false
        ),
        lineHeightStyle =
        LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        ),
        color = Color.Black
    )

@Composable
fun fontConfiguration(): AppFonts {
    val baseTextStyle = defaultTextStyle()

    return AppFonts(
        displayLarge =
        baseTextStyle.copy(
            fontSize = 57.sp,
            lineHeight = 64.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        displayMedium =
        baseTextStyle.copy(
            fontSize = 45.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        displaySmall =
        baseTextStyle.copy(
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        headlineLarge =
        baseTextStyle.copy(
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            lineBreak = LineBreak.Heading,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        headlineMedium =
        baseTextStyle.copy(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
            lineBreak = LineBreak.Heading,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        headlineSmall =
        baseTextStyle.copy(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            lineBreak = LineBreak.Heading,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        titleLarge =
        baseTextStyle.copy(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium,
            lineBreak = LineBreak.Heading,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        titleMedium =
        baseTextStyle.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            lineBreak = LineBreak.Heading,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        titleSmall =
        baseTextStyle.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            lineBreak = LineBreak.Heading,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        labelLarge =
        baseTextStyle.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        labelMedium =
        baseTextStyle.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        labelSmall =
        baseTextStyle.copy(
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        bodyLarge =
        baseTextStyle.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            lineBreak = LineBreak.Paragraph,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        bodyMedium =
        baseTextStyle.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            lineBreak = LineBreak.Paragraph,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        bodySmall =
        baseTextStyle.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            lineBreak = LineBreak.Paragraph,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        inputFiller =
        baseTextStyle.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.extendedColorScheme.input
        ),
        notificationTitle =
        baseTextStyle.copy(
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        notificationBody =
        baseTextStyle.copy(
            fontSize = 9.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Thin,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        bottomMenu =
        baseTextStyle.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.extendedColorScheme.textPrimary
        ),
        buttonLabel =
        baseTextStyle.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColorScheme.white
        )
    )
}

// Fallback FontConfiguration for backward compatibility (non-composable contexts)
val FontConfiguration =
    AppFonts(
        displayLarge =
        defaultTextStyleFallback.copy(
            fontSize = 57.sp,
            lineHeight = 64.sp,
            fontWeight = FontWeight.Bold
        ),
        displayMedium =
        defaultTextStyleFallback.copy(
            fontSize = 45.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Bold
        ),
        displaySmall =
        defaultTextStyleFallback.copy(
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Bold
        ),
        headlineLarge =
        defaultTextStyleFallback.copy(
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            lineBreak = LineBreak.Heading
        ),
        headlineMedium =
        defaultTextStyleFallback.copy(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
            lineBreak = LineBreak.Heading
        ),
        headlineSmall =
        defaultTextStyleFallback.copy(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            lineBreak = LineBreak.Heading
        ),
        titleLarge =
        defaultTextStyleFallback.copy(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium,
            lineBreak = LineBreak.Heading
        ),
        titleMedium =
        defaultTextStyleFallback.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            lineBreak = LineBreak.Heading
        ),
        titleSmall =
        defaultTextStyleFallback.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            lineBreak = LineBreak.Heading
        ),
        labelLarge =
        defaultTextStyleFallback.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        ),
        labelMedium =
        defaultTextStyleFallback.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal
        ),
        labelSmall =
        defaultTextStyleFallback.copy(
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyLarge =
        defaultTextStyleFallback.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            lineBreak = LineBreak.Paragraph
        ),
        bodyMedium =
        defaultTextStyleFallback.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            lineBreak = LineBreak.Paragraph
        ),
        bodySmall =
        defaultTextStyleFallback.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            lineBreak = LineBreak.Paragraph
        ),
        inputFiller =
        defaultTextStyleFallback.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Light
        ),
        notificationTitle =
        defaultTextStyleFallback.copy(
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        notificationBody =
        defaultTextStyleFallback.copy(
            fontSize = 9.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Thin
        ),
        bottomMenu =
        defaultTextStyleFallback.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Light
        ),
        buttonLabel =
        defaultTextStyleFallback.copy(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold
        )
    )
