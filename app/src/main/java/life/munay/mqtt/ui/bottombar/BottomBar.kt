package life.munay.mqtt.ui.bottombar

import android.content.res.Configuration
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import life.munay.core.navigation.NavigationItem
import life.munay.core.resusables.composables.fonts.LocalAppFonts
import life.munay.core.resusables.composables.themes.AppTheme
import life.munay.core.resusables.composables.themes.extendedColorScheme
import java.util.Locale
import kotlin.collections.forEach

@Composable
fun BottomBar(
    navController: NavController,
    menuItems: List<NavigationItem>
) {
    if (menuItems.isNotEmpty()) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: menuItems[0].route

        val routes = remember { menuItems.map { it.route } }
        if (currentRoute in routes) {
            NavigationBar {
                menuItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.image,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                text = item.title.uppercase(Locale.getDefault()),
                                style = LocalAppFonts.current.bottomMenu.copy(color = MaterialTheme.extendedColorScheme.primary)
                            )
                        },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (item.route != currentRoute) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        alwaysShowLabel = false,
                        modifier = Modifier.navigationBarsPadding(),
                        colors = navigationBarColors()
                    )
                }
            }
        }
    }
}

@Composable
private fun navigationBarColors() =
    NavigationBarItemColors(
        selectedIconColor = MaterialTheme.extendedColorScheme.white,
        selectedTextColor = MaterialTheme.extendedColorScheme.black,
        selectedIndicatorColor = MaterialTheme.extendedColorScheme.primary,
        unselectedIconColor = MaterialTheme.extendedColorScheme.grey300,
        unselectedTextColor = MaterialTheme.extendedColorScheme.grey300,
        disabledIconColor = MaterialTheme.extendedColorScheme.grey200,
        disabledTextColor = MaterialTheme.extendedColorScheme.grey200
    )

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewBottomBar() {
    val navController = androidx.navigation.compose.rememberNavController()
    val menuItems =
        listOf(
            NavigationItem(
                route = "webrtc",
                title = "WebRTC",
                image = Icons.Default.WifiTethering
            ),
            NavigationItem(
                route = "settings",
                title = "Settings",
                image = Icons.Default.Settings
            )
        )

    AppTheme {
        BottomBar(navController = navController, menuItems = menuItems)
    }
}
