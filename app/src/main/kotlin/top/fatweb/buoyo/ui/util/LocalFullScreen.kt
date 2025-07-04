package top.fatweb.buoyo.ui.util

import androidx.compose.runtime.compositionLocalOf

data class FullScreen(
    val enable: Boolean = false,
    val onStateChange: (Boolean) -> Unit = {}
)

val LocalFullScreen = compositionLocalOf { FullScreen() }
