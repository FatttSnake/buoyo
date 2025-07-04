package top.fatweb.buoyo.ui.left

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun LeftRoute(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean
) {
    LeftScreen(
        modifier = modifier,
        onShowSnackbar = onShowSnackbar
    )
}

@Composable
internal fun LeftScreen(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (message: String, action: String?) -> Boolean
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("LeftScreen")
        }
    }
}
