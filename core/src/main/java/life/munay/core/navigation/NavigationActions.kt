package life.munay.core.navigation

import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Comprehensive navigation utilities with advanced features for complex navigation scenarios.
 *
 * Combines basic navigation actions with enhanced features including:
 * - Safe navigation with error handling
 * - Navigation state management and event streaming
 * - Back stack tracking and manipulation
 * - Deep link handling with validation
 * - Arguments passing and route building
 */
class NavigationActions(
    private val navController: NavController
) {
    // Navigation events for observing navigation changes
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>(replay = 0)
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // Legacy simple navigation methods (maintained for backward compatibility)
    val navigateBack: () -> Unit = {
        navigateBackSafely()
    }

    val popToStartDestination: () -> Unit = {
        navigateToStart()
    }

    /**
     * Navigate with safe error handling
     */
    fun navigateToSafely(
        route: String,
        clearBackStack: Boolean = false,
        singleTop: Boolean = false
    ): Boolean =
        runCatching {
            if (clearBackStack) {
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                    launchSingleTop = singleTop
                }
            } else {
                navController.navigate(route) {
                    launchSingleTop = singleTop
                }
            }

            emitNavigationEvent(NavigationEvent.NavigatedTo(route))
            true
        }.getOrElse {
            emitNavigationEvent(NavigationEvent.NavigationError(route, it))
            false
        }

    /**
     * Navigate back with validation
     */
    fun navigateBackSafely(): Boolean =
        runCatching {
            val canGoBack = navController.previousBackStackEntry != null
            if (canGoBack) {
                navController.popBackStack()
                emitNavigationEvent(NavigationEvent.NavigatedBack)
                true
            } else {
                emitNavigationEvent(NavigationEvent.BackNavigationBlocked)
                false
            }
        }.getOrElse {
            emitNavigationEvent(NavigationEvent.BackNavigationError(it))
            false
        }

    /**
     * Navigate to start destination
     */
    fun navigateToStart(): Boolean =
        runCatching {
            navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
            emitNavigationEvent(NavigationEvent.NavigatedToStart)
            true
        }.getOrElse {
            emitNavigationEvent(NavigationEvent.NavigationError("start", it))
            false
        }

    /**
     * Clear entire back stack and navigate to route
     */
    fun navigateAndClearStack(route: String): Boolean =
        runCatching {
            navController.navigate(route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            emitNavigationEvent(NavigationEvent.StackCleared(route))
            true
        }.getOrElse {
            emitNavigationEvent(NavigationEvent.NavigationError(route, it))
            false
        }

    /**
     * Navigate with arguments
     */
    fun navigateWithArgs(
        route: String,
        args: Map<String, Any>
    ): Boolean =
        runCatching {
            val routeWithArgs = buildRouteWithArgs(route, args)
            navController.navigate(routeWithArgs)
            emitNavigationEvent(NavigationEvent.NavigatedWithArgs(route, args))
            true
        }.getOrElse {
            emitNavigationEvent(NavigationEvent.NavigationError(route, it))
            false
        }

    /**
     * Get current destination route
     */
    fun getCurrentRoute(): String? = navController.currentDestination?.route

    /**
     * Get back stack size
     */
    fun getBackStackSize(): Int =
        runCatching {
            // Use safe alternative to avoid RestrictedApi
            var count = 0
            var entry = navController.currentBackStackEntry
            while (entry != null) {
                count++
                entry = navController.previousBackStackEntry
                if (count > 100) break // Safety check to prevent infinite loop
            }
            count
        }.getOrElse { 0 }

    /**
     * Check if can navigate back
     */
    fun canNavigateBack(): Boolean = navController.previousBackStackEntry != null

    /**
     * Get all routes in back stack
     */
    fun getBackStackRoutes(): List<String> =
        runCatching {
            // Use safe alternative to avoid RestrictedApi
            val routes = mutableListOf<String>()
            navController.currentDestination?.route?.let { routes.add(it) }
            // Note: Full back stack traversal requires restricted APIs
            // This provides current route as a safe alternative
            routes
        }.getOrElse { emptyList() }

    /**
     * Pop back stack to specific route
     */
    fun popBackStackTo(
        route: String,
        inclusive: Boolean = false
    ): Boolean =
        runCatching {
            val result = navController.popBackStack(route, inclusive)
            if (result) {
                emitNavigationEvent(NavigationEvent.PoppedToRoute(route, inclusive))
            }
            result
        }.getOrElse {
            emitNavigationEvent(NavigationEvent.NavigationError(route, it))
            false
        }

    /**
     * Handle deep link navigation
     */
    fun handleDeepLink(deepLink: String): Boolean =
        runCatching {
            // Parse and validate deep link
            val route = parseDeepLink(deepLink)
            if (route.isNotEmpty()) {
                navController.navigate(route)
                emitNavigationEvent(NavigationEvent.DeepLinkHandled(deepLink, route))
                true
            } else {
                emitNavigationEvent(NavigationEvent.InvalidDeepLink(deepLink))
                false
            }
        }.getOrElse {
            emitNavigationEvent(NavigationEvent.DeepLinkError(deepLink, it))
            false
        }

    /**
     * Build route with arguments
     */
    private fun buildRouteWithArgs(
        route: String,
        args: Map<String, Any>
    ): String {
        if (args.isEmpty()) return route

        val argsString =
            args.entries.joinToString("&") { (key, value) ->
                "$key=$value"
            }

        return if (route.contains("?")) {
            "$route&$argsString"
        } else {
            "$route?$argsString"
        }
    }

    /**
     * Parse deep link to navigation route
     */
    private fun parseDeepLink(deepLink: String): String {
        // Implement your deep link parsing logic here
        // This is a basic implementation - customize based on your app's deep link structure
        return when {
            deepLink.contains("/home") -> "home"
            deepLink.contains("/profile") -> "profile"
            deepLink.contains("/settings") -> "settings"
            else -> ""
        }
    }

    /**
     * Emit navigation event safely
     */
    private fun emitNavigationEvent(event: NavigationEvent) {
        _navigationEvents.tryEmit(event)
    }

    /**
     * Navigation events for observing navigation state changes
     */
    sealed class NavigationEvent {
        data class NavigatedTo(
            val route: String
        ) : NavigationEvent()

        data class NavigatedWithArgs(
            val route: String,
            val args: Map<String, Any>
        ) : NavigationEvent()

        data object NavigatedBack : NavigationEvent()

        data object NavigatedToStart : NavigationEvent()

        data class StackCleared(
            val newRoute: String
        ) : NavigationEvent()

        data class PoppedToRoute(
            val route: String,
            val inclusive: Boolean
        ) : NavigationEvent()

        data class DeepLinkHandled(
            val deepLink: String,
            val route: String
        ) : NavigationEvent()

        data class NavigationError(
            val route: String,
            val error: Throwable
        ) : NavigationEvent()

        data class BackNavigationError(
            val error: Throwable
        ) : NavigationEvent()

        data object BackNavigationBlocked : NavigationEvent()

        data class InvalidDeepLink(
            val deepLink: String
        ) : NavigationEvent()

        data class DeepLinkError(
            val deepLink: String,
            val error: Throwable
        ) : NavigationEvent()
    }
}
