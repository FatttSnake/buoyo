package top.fatweb.buoyo.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import top.fatweb.buoyo.ui.right.RightRoute

const val RIGHT_ROUTE = "right_route"

fun NavController.navigateToRight(navOptions: NavOptions) =
    navigate(route = RIGHT_ROUTE, navOptions = navOptions)

fun NavGraphBuilder.rightScreen(
    isVertical: Boolean,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean
) {
    composable(
        route = RIGHT_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                LEFT_ROUTE, HOME_ROUTE ->
                    if (isVertical) slideInHorizontally { it }
                    else slideInVertically { it }

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                LEFT_ROUTE, HOME_ROUTE ->
                    if (isVertical) slideOutHorizontally { it }
                    else slideOutVertically { it }

                else -> null
            }
        }
    ) {
        RightRoute(
            onShowSnackbar = onShowSnackbar
        )
    }
}
