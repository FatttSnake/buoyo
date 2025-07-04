package top.fatweb.buoyo.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.buoyo.ui.home.HomeRoute

const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions) =
    navigate(route = HOME_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.homeScreen(
    isVertical: Boolean,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean
) {
    composable(
        route = HOME_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                LEFT_ROUTE ->
                    if (isVertical) slideInHorizontally { it }
                    else slideInVertically { it }

                RIGHT_ROUTE ->
                    if (isVertical) slideInHorizontally { -it }
                    else slideInVertically { -it }

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                LEFT_ROUTE ->
                    if (isVertical) slideOutHorizontally { it }
                    else slideOutVertically { it }

                RIGHT_ROUTE ->
                    if (isVertical) slideOutHorizontally { -it }
                    else slideOutVertically { -it }

                else -> null
            }
        }
    ) {
        HomeRoute(
            onShowSnackbar = onShowSnackbar
        )
    }
}
