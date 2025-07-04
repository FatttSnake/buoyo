package top.fatweb.buoyo.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.buoyo.ui.about.LibrariesRoute

const val LIBRARIES_ROUTE = "libraries_route"

fun NavController.navigateToLibraries(navOptions: NavOptions? = null) =
    navigate(route = LIBRARIES_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.librariesScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = LIBRARIES_ROUTE,
        enterTransition = {
            slideInHorizontally { it }
        },
        popEnterTransition = null,
        popExitTransition = {
            slideOutHorizontally { it }
        }
    ) {
        LibrariesRoute(
            onBackClick = onBackClick
        )
    }
}
