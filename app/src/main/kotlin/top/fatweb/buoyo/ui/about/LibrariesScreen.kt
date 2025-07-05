package top.fatweb.buoyo.ui.about

import android.content.Intent
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.fatweb.buoyo.R
import top.fatweb.buoyo.icon.BuoyoIcons
import top.fatweb.buoyo.ui.component.Indicator
import top.fatweb.buoyo.ui.component.TopAppBar
import top.fatweb.buoyo.ui.component.scrollbar.DraggableScrollbar
import top.fatweb.buoyo.ui.component.scrollbar.rememberDraggableScroller
import top.fatweb.buoyo.ui.component.scrollbar.scrollbarState
import top.fatweb.buoyo.ui.theme.BuoyoPreviews
import top.fatweb.buoyo.ui.theme.BuoyoTheme

@Composable
internal fun LibrariesRoute(
    modifier: Modifier = Modifier,
    viewModel: LibrariesScreenViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val librariesScreenUiState by viewModel.librariesScreenUiState.collectAsStateWithLifecycle()

    LibrariesScreen(
        modifier = modifier,
        librariesScreenUiState = librariesScreenUiState,
        onBackClick = onBackClick,
        onSearch = viewModel::onSearchValueChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibrariesScreen(
    modifier: Modifier = Modifier,
    librariesScreenUiState: LibrariesScreenUiState,
    onBackClick: () -> Unit,
    onSearch: (String) -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val context = LocalContext.current

    val isLibrariesLoading = librariesScreenUiState is LibrariesScreenUiState.Loading
    ReportDrawnWhen { !isLibrariesLoading }

    val itemsAvailable = howManyItems(librariesScreenUiState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(itemsAvailable)

    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }
    var dialogUrl by remember { mutableStateOf("") }

    var canScroll by remember { mutableStateOf(true) }
    val topAppBarScrollBehavior =
        if (canScroll) TopAppBarDefaults.enterAlwaysScrollBehavior() else TopAppBarDefaults.pinnedScrollBehavior()

    var activeSearch by remember { mutableStateOf(false) }
    var searchValue by remember { mutableStateOf("") }

    LaunchedEffect(activeSearch) {
        canScroll = !activeSearch
    }

    Scaffold(
        modifier = modifier
            .nestedScroll(connection = topAppBarScrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        ) {
            TopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.feature_settings_more_open_source_license),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = BuoyoIcons.Back,
                navigationIconContentDescription = stringResource(R.string.core_back),
                actionIcon = BuoyoIcons.Search,
                actionIconContentDescription = stringResource(R.string.core_search),
                activeSearch = activeSearch,
                query = searchValue,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                onNavigationClick = onBackClick,
                onActionClick = {
                    activeSearch = true
                },
                onQueryChange = {
                    searchValue = it
                    onSearch(it)
                },
                onSearch = onSearch,
                onCancelSearch = {
                    searchValue = ""
                    activeSearch = false
                    onSearch("")
                }
            )
            Box {
                when (librariesScreenUiState) {
                    LibrariesScreenUiState.Loading -> {
                        Indicator()
                    }

                    LibrariesScreenUiState.Nothing -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(state = rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = stringResource(R.string.core_nothing))
                        }
                    }

                    LibrariesScreenUiState.NotFound -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(state = rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = stringResource(R.string.core_nothing_found))
                        }
                    }

                    is LibrariesScreenUiState.Success -> {
                        val handleOnClickLicense = { key: String ->
                            val license = librariesScreenUiState.dependencies.licenses[key]
                            if (license != null) {
                                showDialog = true
                                dialogTitle = license.name
                                dialogContent = license.content.orEmpty()
                                dialogUrl = license.url.orEmpty()
                            }
                        }

                        LazyVerticalStaggeredGrid(
                            modifier = Modifier
                                .fillMaxSize(),
                            columns = StaggeredGridCells.Adaptive(300.dp),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalItemSpacing = 24.dp,
                            state = state
                        ) {
                            librariesPanel(
                                librariesScreenUiState = librariesScreenUiState,
                                onClickLicense = handleOnClickLicense
                            )
                        }

                        state.DraggableScrollbar(
                            modifier = Modifier
                                .fillMaxHeight()
                                .windowInsetsPadding(WindowInsets.systemBars)
                                .padding(horizontal = 2.dp)
                                .align(Alignment.CenterEnd),
                            state = scrollbarState,
                            orientation = Orientation.Vertical,
                            onThumbMoved = state.rememberDraggableScroller(itemsAvailable = itemsAvailable)
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            modifier = Modifier
                .widthIn(max = windowInfo.containerSize.width.dp - 80.dp)
                .heightIn(max = windowInfo.containerSize.height.dp - 40.dp),
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(state = rememberScrollState())
                ) {
                    Text(text = dialogContent)
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, dialogUrl.toUri()))
                        }
                    ) {
                        Text(text = stringResource(R.string.core_website))
                    }
                    TextButton(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text(text = stringResource(R.string.core_close))
                    }
                }
            }
        )
    }
}

private fun howManyItems(librariesScreenUiState: LibrariesScreenUiState) =
    when (librariesScreenUiState) {
        LibrariesScreenUiState.Loading, LibrariesScreenUiState.Nothing, LibrariesScreenUiState.NotFound -> 0

        is LibrariesScreenUiState.Success -> librariesScreenUiState.dependencies.libraries.size
    }

@BuoyoPreviews
@Composable
private fun LibrariesScreenLoadingPreview() {
    BuoyoTheme {
        LibrariesScreen(
            librariesScreenUiState = LibrariesScreenUiState.Loading,
            onBackClick = {},
            onSearch = {}
        )
    }
}
