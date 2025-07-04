package top.fatweb.buoyo.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import top.fatweb.buoyo.R
import top.fatweb.buoyo.icon.BuoyoIcons

enum class TopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @param:StringRes val iconTextId: Int,
    @param:StringRes val titleTextId: Int
) {
    Left(
        route = LEFT_ROUTE,
        selectedIcon = BuoyoIcons.Apps,
        unselectedIcon = BuoyoIcons.AppsBorder,
        iconTextId = R.string.feature_left_title,
        titleTextId = R.string.feature_left_title
    ),

    Home(
        route = HOME_ROUTE,
        selectedIcon = BuoyoIcons.Home,
        unselectedIcon = BuoyoIcons.HomeBorder,
        iconTextId = R.string.feature_home_title,
        titleTextId = R.string.feature_home_title
    ),

    Right(
        route = RIGHT_ROUTE,
        selectedIcon = BuoyoIcons.Apps,
        unselectedIcon = BuoyoIcons.AppsBorder,
        iconTextId = R.string.feature_right_title,
        titleTextId = R.string.feature_right_title
    )
}

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.equals(destination.route) == true
    } == true
