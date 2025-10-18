package life.munay.mqtt.ui.utilities

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import life.munay.core.resusables.composables.themes.extendedColorScheme

@Composable
fun defaultOutlinedTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,

    focusedIndicatorColor = MaterialTheme.extendedColorScheme.primary,
    unfocusedIndicatorColor = MaterialTheme.extendedColorScheme.grey200,
    disabledIndicatorColor = MaterialTheme.extendedColorScheme.grey300,

    cursorColor = MaterialTheme.extendedColorScheme.primary,

    focusedLabelColor = MaterialTheme.extendedColorScheme.grey300,
    unfocusedLabelColor = MaterialTheme.extendedColorScheme.grey300,
    disabledLabelColor = MaterialTheme.extendedColorScheme.grey300,

    focusedTextColor = MaterialTheme.extendedColorScheme.textPrimary,
    unfocusedTextColor = MaterialTheme.extendedColorScheme.textPrimary,
    disabledTextColor = MaterialTheme.extendedColorScheme.grey300,

    focusedPlaceholderColor = MaterialTheme.extendedColorScheme.textPrimary.copy(alpha = 0.38f),
    unfocusedPlaceholderColor = MaterialTheme.extendedColorScheme.textPrimary.copy(alpha = 0.38f),
    disabledPlaceholderColor = MaterialTheme.extendedColorScheme.grey300
)
