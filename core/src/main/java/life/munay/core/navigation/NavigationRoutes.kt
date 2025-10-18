package life.munay.core.navigation

sealed class NavigationRoutes(
    val route: String
) {
    data object Root : NavigationRoutes("root")

    // Bottom Menu Navigation
    data object Mqtt : NavigationRoutes("mqtt")

    data object About : NavigationRoutes("about")
}
