package top.fatweb.buoyo.ui

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavDestination
import top.fatweb.buoyo.R
import top.fatweb.buoyo.icon.BuoyoIcons
import top.fatweb.buoyo.navigation.ABOUT_ROUTE
import top.fatweb.buoyo.navigation.HOME_ROUTE
import top.fatweb.buoyo.navigation.NavHost
import top.fatweb.buoyo.navigation.TopLevelDestination
import top.fatweb.buoyo.navigation.isTopLevelDestinationInHierarchy
import top.fatweb.buoyo.ui.component.Background
import top.fatweb.buoyo.ui.component.GradientBackground
import top.fatweb.buoyo.ui.component.NavigationBar
import top.fatweb.buoyo.ui.component.NavigationBarItem
import top.fatweb.buoyo.ui.component.NavigationRail
import top.fatweb.buoyo.ui.component.NavigationRailItem
import top.fatweb.buoyo.ui.component.TopAppBar
import top.fatweb.buoyo.ui.settings.SettingsDialog
import top.fatweb.buoyo.ui.theme.GradientColors
import top.fatweb.buoyo.ui.theme.LocalGradientColors
import top.fatweb.buoyo.ui.util.FullScreen
import top.fatweb.buoyo.ui.util.LocalFullScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuoyoApp(
    appState: BuoyoAppState,
    showSettingsDialogState: MutableState<Boolean>
) {
    val shouldShowGradientBackground =
        appState.currentDestination?.route == ABOUT_ROUTE
    var showSettingsDialog by showSettingsDialogState

    val context = LocalContext.current
    val window = (context as ComponentActivity).window
    val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
    var isFullScreen by remember { mutableStateOf(false) }
    val fullScreen = FullScreen(
        enable = isFullScreen,
        onStateChange = {
            isFullScreen = it
        }
    )

    DisposableEffect(Unit) {
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        val listener = WindowInsetsControllerCompat.OnControllableInsetsChangedListener { _, _ ->
            isFullScreen = false
        }
        windowInsetsController.addOnControllableInsetsChangedListener(listener)
        onDispose {
            windowInsetsController.removeOnControllableInsetsChangedListener(listener)
        }
    }

    LaunchedEffect(isFullScreen) {
        if (isFullScreen) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    CompositionLocalProvider(LocalFullScreen provides fullScreen) {
        Background {
            GradientBackground(
                gradientColors = if (shouldShowGradientBackground) LocalGradientColors.current else GradientColors()
            ) {
                val destination = appState.currentTopLevelDestination

                val snackbarHostState = remember { SnackbarHostState() }

                var canScroll by remember { mutableStateOf(true) }
                val topAppBarScrollBehavior =
                    if (canScroll) TopAppBarDefaults.enterAlwaysScrollBehavior() else TopAppBarDefaults.pinnedScrollBehavior()

                if (showSettingsDialog) {
                    SettingsDialog(
                        onDismiss = { showSettingsDialog = false },
                        onNavigateToLibraries = appState::navigateToLibraries,
                        onNavigateToAbout = appState::navigateToAbout
                    )
                }

                LaunchedEffect(destination) {
                    topAppBarScrollBehavior.state.heightOffset = 0f
                }

                Scaffold(
                    modifier = Modifier
                        .nestedScroll(connection = topAppBarScrollBehavior.nestedScrollConnection),
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    bottomBar = {
                        AnimatedVisibility(
                            visible = appState.shouldShowBottomBar && destination != null
                        ) {
                            BottomBar(
                                destinations = appState.topLevelDestinations,
                                currentDestination = appState.currentDestination,
                                onNavigateToDestination = appState::navigateToTopLevelDestination
                            )
                        }
                    }
                ) { padding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        AnimatedVisibility(
                            visible = appState.shouldShowNavRail && destination != null
                        ) {
                            NavRail(
                                modifier = Modifier
                                    .padding(padding)
                                    .consumeWindowInsets(padding)
                                    .safeDrawingPadding(),
                                destinations = appState.topLevelDestinations,
                                currentDestination = appState.currentDestination,
                                onNavigateToDestination = appState::navigateToTopLevelDestination
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            AnimatedVisibility(
                                visible = destination != null
                            ) {
                                TopAppBar(
                                    scrollBehavior = topAppBarScrollBehavior,
                                    title = {
                                        destination?.let {
                                            Text(
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                text = stringResource(destination.titleTextId)
                                            )
                                        }
                                    },
                                    actionIcon = BuoyoIcons.MoreVert,
                                    actionIconContentDescription = stringResource(R.string.feature_settings_more),
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = Color.Transparent,
                                        scrolledContainerColor = Color.Transparent
                                    ),
                                    onActionClick = {
                                        showSettingsDialog = true
                                    }
                                )
                            }

                            NavHost(
                                appState = appState,
                                startDestination = HOME_ROUTE,
                                isVertical = appState.shouldShowBottomBar,
                                onShowSnackbar = { message, action ->
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        actionLabel = action,
                                        duration = SnackbarDuration.Short
                                    ) == SnackbarResult.ActionPerformed
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationBarItem(
                modifier = modifier,
                selected = selected,
                label = {
                    Text(text = stringResource(destination.titleTextId))
                },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = stringResource(destination.iconTextId)
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(destination.iconTextId)
                    )
                },
                onClick = { onNavigateToDestination(destination) }
            )
        }
    }
}

@Composable
private fun NavRail(
    modifier: Modifier = Modifier,
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    NavigationRail(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationRailItem(
                modifier = modifier,
                selected = selected,
                label = {
                    Text(text = stringResource(destination.titleTextId))
                },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = stringResource(destination.iconTextId)
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(destination.iconTextId)
                    )
                },
                onClick = { onNavigateToDestination(destination) }
            )
        }
    }
}
