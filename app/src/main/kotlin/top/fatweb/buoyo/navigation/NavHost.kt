package top.fatweb.buoyo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.savedstate.SavedState
import top.fatweb.buoyo.ui.BuoyoAppState
import top.fatweb.buoyo.ui.util.LocalFullScreen

@Composable
fun NavHost(
    modifier: Modifier = Modifier,
    appState: BuoyoAppState,
    startDestination: String,
    isVertical: Boolean,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean
) {
    val navController = appState.navController
    val fullScreen = LocalFullScreen.current

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: SavedState?
            ) {
                fullScreen.onStateChange.invoke(false)
            }
        })
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        leftScreen(
            isVertical = isVertical,
            onShowSnackbar = onShowSnackbar
        )
        homeScreen(
            isVertical = isVertical,
            onShowSnackbar = onShowSnackbar
        )
        rightScreen(
            isVertical = isVertical,
            onShowSnackbar = onShowSnackbar
        )

        aboutScreen(
            onBackClick = navController::popBackStack,
            onNavigateToLibraries = navController::navigateToLibraries
        )
        librariesScreen(
            onBackClick = navController::popBackStack
        )
    }
}
