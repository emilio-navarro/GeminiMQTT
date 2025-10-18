package life.munay.mqtt

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import life.munay.core.resusables.composables.themes.AppTheme
import life.munay.mqtt.ui.screens.AppContent
import life.munay.mqtt.viewmodels.MainViewModel
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val mainViewModel: MainViewModel by viewModels()

            var isStatusBarLight by remember { mutableStateOf(true) }

            AppTheme {
                AppContent(
                    navController = navController,
                    currentRoute = currentRoute,
                    mainViewModel = mainViewModel,
                    handleLink = ::handleLink,
                    isStatusBarLight = { isStatusBarLight = it }
                )
            }
        }
    }

    fun handleLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
    }
}
