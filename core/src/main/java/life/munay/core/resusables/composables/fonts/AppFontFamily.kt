package life.munay.core.resusables.composables.fonts

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import life.munay.core.R

/**
 * Comprehensive Lato font family for the application.
 *
 * Provides a full range of Lato font weights and styles for consistent typography
 * across the application, from thin to black weights with italic variants.
 */
val AppFontFamily =
    FontFamily(
        // Thin weights
        Font(R.font.lato_thin, FontWeight.Thin, FontStyle.Normal),
        Font(R.font.lato_thin_italic, FontWeight.Thin, FontStyle.Italic),
        // Light weights
        Font(R.font.lato_light, FontWeight.Light, FontStyle.Normal),
        Font(R.font.lato_light_italic, FontWeight.Light, FontStyle.Italic),
        // Regular weights
        Font(R.font.lato_regular, FontWeight.Normal, FontStyle.Normal),
        Font(R.font.lato_italic, FontWeight.Normal, FontStyle.Italic),
        // Bold weights
        Font(R.font.lato_bold, FontWeight.Bold, FontStyle.Normal),
        Font(R.font.lato_bold_italic, FontWeight.Bold, FontStyle.Italic),
        // Black weights
        Font(R.font.lato_black, FontWeight.Black, FontStyle.Normal),
        Font(R.font.lato_black_italic, FontWeight.Black, FontStyle.Italic)
    )
