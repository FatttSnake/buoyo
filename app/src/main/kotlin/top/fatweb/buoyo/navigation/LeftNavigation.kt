package top.fatweb.buoyo.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.buoyo.ui.left.LeftRoute

const val LEFT_ROUTE = "left_route"

fun NavController.navigateToLeft(navOptions: NavOptions) =
    navigate(route = LEFT_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.leftScreen(
    isVertical: Boolean,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean
) {
    composable(
        route = LEFT_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                HOME_ROUTE, RIGHT_ROUTE ->
                    if (isVertical) slideInHorizontally { -it }
                    else slideInVertically { -it }

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                HOME_ROUTE, RIGHT_ROUTE ->
                    if (isVertical) slideOutHorizontally { -it }
                    else slideOutVertically { -it }

                else -> null
            }
        }
    ) {
        LeftRoute(
            onShowSnackbar = onShowSnackbar
        )
    }
}
