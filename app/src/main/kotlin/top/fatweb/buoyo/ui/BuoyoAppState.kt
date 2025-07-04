package top.fatweb.buoyo.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import top.fatweb.buoyo.monitor.TimeZoneMonitor
import top.fatweb.buoyo.navigation.HOME_ROUTE
import top.fatweb.buoyo.navigation.LEFT_ROUTE
import top.fatweb.buoyo.navigation.RIGHT_ROUTE
import top.fatweb.buoyo.navigation.TopLevelDestination
import top.fatweb.buoyo.navigation.navigateToAbout
import top.fatweb.buoyo.navigation.navigateToHome
import top.fatweb.buoyo.navigation.navigateToLeft
import top.fatweb.buoyo.navigation.navigateToLibraries
import top.fatweb.buoyo.navigation.navigateToRight
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberBuoyoAppState(
    windowSizeClass: WindowSizeClass,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
): BuoyoAppState = remember(
    windowSizeClass,
    timeZoneMonitor,
    coroutineScope,
    navController
) {
    BuoyoAppState(
        windowSizeClass,
        timeZoneMonitor,
        coroutineScope,
        navController
    )
}

@Stable
class BuoyoAppState(
    private val windowSizeClass: WindowSizeClass,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope,
    val navController: NavHostController
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            LEFT_ROUTE -> TopLevelDestination.Left
            HOME_ROUTE -> TopLevelDestination.Home
            RIGHT_ROUTE -> TopLevelDestination.Right
            else -> null
        }

    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            scope = coroutineScope,
            initialValue = TimeZone.currentSystemDefault(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds)
        )

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.Left -> navController.navigateToLeft(topLevelNavOptions)
            TopLevelDestination.Home -> navController.navigateToHome(topLevelNavOptions)
            TopLevelDestination.Right -> navController.navigateToRight(topLevelNavOptions)
        }
    }

    fun navigateToLibraries() = navController.navigateToLibraries()

    fun navigateToAbout() = navController.navigateToAbout()
}
