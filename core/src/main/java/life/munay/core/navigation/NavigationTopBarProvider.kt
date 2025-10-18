package life.munay.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf

val NavigationTopBarProvider = staticCompositionLocalOf<MutableState<@Composable () -> Unit>> { mutableStateOf({}) }

@Composable
fun CompositionLocal<MutableState<@Composable () -> Unit>>.update(content: @Composable () -> Unit) {
    this.current.value = content
}
