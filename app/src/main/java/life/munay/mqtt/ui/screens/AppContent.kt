package life.munay.mqtt.ui.screens

import android.webkit.URLUtil
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import life.munay.core.navigation.NavigationTopBarProvider
import life.munay.core.resusables.composables.themes.extendedColorScheme
import life.munay.mqtt.nav.NavGraph
import life.munay.mqtt.ui.bottombar.BottomBar
import life.munay.mqtt.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    navController: NavHostController,
    currentRoute: String?,
    mainViewModel: MainViewModel,
    handleLink: (String) -> Unit,
    isStatusBarLight: (Boolean) -> Unit
) {
    val menuItems = mainViewModel.getNavigationItems()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val topAppBarState =
        remember {
            mutableStateOf<@Composable () -> Unit>({})
        }

    val defaultColor = MaterialTheme.extendedColorScheme.backgroundScreen
    var statusBarColor by remember { mutableStateOf(defaultColor) }

    LaunchedEffect(Unit) {
        scope.launch {
            mainViewModel.destination.collect { route ->
                if (URLUtil.isValidUrl(route)) {
                    handleLink(route)
                } else {
                    navController.navigate(route = route)
                }
            }
        }
    }

    CompositionLocalProvider(
        NavigationTopBarProvider provides topAppBarState
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = statusBarColor,
            topBar = {
                NavigationTopBarProvider.current.value()
            },
            bottomBar = {
                BottomBar(
                    navController = navController,
                    menuItems = menuItems
                )
            },
            content = { innerPadding ->
                NavGraph(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    statusBarUpdates = { color, isLight ->
                        statusBarColor = color
                        isStatusBarLight(isLight)
                    }
                )
            }
        )
    }
}
