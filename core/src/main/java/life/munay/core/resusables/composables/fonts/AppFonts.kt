package life.munay.core.resusables.composables.fonts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

class AppFonts(
    displayLarge: TextStyle,
    displayMedium: TextStyle,
    displaySmall: TextStyle,
    headlineLarge: TextStyle,
    headlineMedium: TextStyle,
    headlineSmall: TextStyle,
    titleLarge: TextStyle,
    titleMedium: TextStyle,
    titleSmall: TextStyle,
    labelLarge: TextStyle,
    labelMedium: TextStyle,
    labelSmall: TextStyle,
    bodyLarge: TextStyle,
    bodyMedium: TextStyle,
    bodySmall: TextStyle,
    inputFiller: TextStyle,
    notificationTitle: TextStyle,
    notificationBody: TextStyle,
    bottomMenu: TextStyle,
    buttonLabel: TextStyle
) {
    var displayLarge by mutableStateOf(displayLarge)
        private set

    var displayMedium by mutableStateOf(displayMedium)
        private set

    var displaySmall by mutableStateOf(displaySmall)
        private set

    var headlineLarge by mutableStateOf(headlineLarge)
        private set

    var headlineMedium by mutableStateOf(headlineMedium)
        private set

    var headlineSmall by mutableStateOf(headlineSmall)
        private set

    var titleLarge by mutableStateOf(titleLarge)
        private set

    var titleMedium by mutableStateOf(titleMedium)
        private set

    var titleSmall by mutableStateOf(titleSmall)
        private set

    var labelLarge by mutableStateOf(labelLarge)
        private set

    var labelMedium by mutableStateOf(labelMedium)
        private set

    var labelSmall by mutableStateOf(labelSmall)
        private set

    var bodyLarge by mutableStateOf(bodyLarge)
        private set

    var bodyMedium by mutableStateOf(bodyMedium)
        private set

    var bodySmall by mutableStateOf(bodySmall)
        private set

    var inputFiller by mutableStateOf(inputFiller)
        private set

    var notificationTitle by mutableStateOf(notificationTitle)
        private set

    var notificationBody by mutableStateOf(notificationBody)
        private set

    var bottomMenu by mutableStateOf(bottomMenu)
        private set

    var buttonLabel by mutableStateOf(buttonLabel)
        private set

    fun update(other: AppFonts) {
        displayLarge = other.displayLarge
        displayMedium = other.displayMedium
        displaySmall = other.displaySmall
        headlineLarge = other.headlineLarge
        headlineMedium = other.headlineMedium
        headlineSmall = other.headlineSmall
        titleLarge = other.titleLarge
        titleMedium = other.titleMedium
        titleSmall = other.titleSmall
        labelLarge = other.labelLarge
        labelMedium = other.labelMedium
        labelSmall = other.labelSmall
        bodyLarge = other.bodyLarge
        bodyMedium = other.bodyMedium
        bodySmall = other.bodySmall
        inputFiller = other.inputFiller
        notificationTitle = other.notificationTitle
        notificationBody = other.notificationBody
        buttonLabel = other.buttonLabel
        bottomMenu = other.bottomMenu
    }
}

val LocalAppFonts =
    staticCompositionLocalOf<AppFonts> {
        error("No App Fonts provided")
    }
