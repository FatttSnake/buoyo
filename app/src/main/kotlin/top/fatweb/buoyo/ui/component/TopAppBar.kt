package top.fatweb.buoyo.ui.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.fatweb.buoyo.R
import top.fatweb.buoyo.icon.BuoyoIcons
import top.fatweb.buoyo.ui.theme.BuoyoPreviews
import top.fatweb.buoyo.ui.theme.BuoyoTheme

import android.R as androidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    expandedHeight: Dp = 48.dp,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: @Composable () -> Unit = {},
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    activeSearch: Boolean = false,
    searchButtonPosition: SearchButtonPosition = SearchButtonPosition.Action,
    query: String = "",
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onCancelSearch: () -> Unit = {}
) {
    val topInset by animateIntAsState(
        targetValue = if (scrollBehavior != null && -scrollBehavior.state.heightOffset >= with(
                LocalDensity.current
            ) { expandedHeight.toPx() }
        ) 0
        else TopAppBarDefaults.windowInsets.getTop(LocalDensity.current),
        label = ""
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
        onSearch(query)
    }

    LaunchedEffect(activeSearch) {
        if (activeSearch) {
            focusRequester.requestFocus()
        }
    }

    CenterAlignedTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        expandedHeight = expandedHeight,
        title = {
            if (activeSearch) TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .focusRequester(focusRequester)
                    .onKeyEvent {
                        if (it.key == Key.Enter) {
                            onSearchExplicitlyTriggered()
                            true
                        } else {
                            false
                        }
                    },
                value = query,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchExplicitlyTriggered()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        imageVector = BuoyoIcons.Search,
                        contentDescription = stringResource(R.string.core_search)
                    )
                },
                maxLines = 1,
                singleLine = true,
                textStyle = MaterialTheme.typography.titleSmall,
                onValueChange = {
                    if ("\n" !in it) onQueryChange(it)
                }
            )
            else title()
        },
        navigationIcon = {
            if (activeSearch && searchButtonPosition == SearchButtonPosition.Navigation) IconButton(
                onClick = onCancelSearch
            ) {
                Icon(
                    imageVector = BuoyoIcons.Close,
                    contentDescription = stringResource(R.string.core_close)
                )
            }
            else navigationIcon?.let {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            if (activeSearch && searchButtonPosition == SearchButtonPosition.Action) IconButton(
                onClick = onCancelSearch
            ) {
                Icon(
                    imageVector = BuoyoIcons.Close,
                    contentDescription = stringResource(R.string.core_close)
                )
            }
            else actionIcon?.let {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = actionIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = colors,
        windowInsets = WindowInsets(top = topInset)
    )
}

enum class SearchButtonPosition {
    Navigation, Action
}

@OptIn(ExperimentalMaterial3Api::class)
@BuoyoPreviews
@Composable
private fun TopAppBarPreview() {
    BuoyoTheme {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(androidR.string.untitled),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = BuoyoIcons.Search,
            navigationIconContentDescription = "Navigation icon",
            actionIcon = BuoyoIcons.MoreVert,
            actionIconContentDescription = "Action icon"
        )
    }
}
