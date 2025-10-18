package life.munay.core.resusables.composables.themes

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import life.munay.core.resusables.composables.fonts.AppFonts
import life.munay.core.resusables.composables.fonts.LocalAppFonts
import life.munay.core.resusables.composables.fonts.fontConfiguration

private val extendedColorLight = ExtendedColorScheme.lightDefault()
private val extendedColorDark = ExtendedColorScheme.darkDefault()

val LocalExtendedColorScheme = staticCompositionLocalOf { extendedColorLight }
val MaterialTheme.extendedColorScheme: ExtendedColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColorScheme.current

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    fonts: AppFonts? = null,
    content: @Composable () -> Unit
) {
    val extendedColorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) extendedColorDark else extendedColorLight
            }

            darkTheme -> extendedColorDark
            else -> extendedColorLight
        }

    CompositionLocalProvider(
        LocalExtendedColorScheme provides extendedColorScheme
    ) {
        MaterialTheme(
            shapes = shapes
        ) {
            // Generate fonts after MaterialTheme is available
            val themeFonts = fonts ?: fontConfiguration()

            ProvideAppThemeValues(fonts = themeFonts) {
                content()
            }
        }
    }
}

@Composable
fun ProvideAppThemeValues(
    fonts: AppFonts,
    content: @Composable () -> Unit
) {
    fonts.update(fonts)
    CompositionLocalProvider(
        LocalAppFonts provides fonts,
        content = content
    )
}
