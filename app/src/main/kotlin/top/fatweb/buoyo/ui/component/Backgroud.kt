package top.fatweb.buoyo.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.fatweb.buoyo.ui.theme.BuoyoPreviews
import top.fatweb.buoyo.ui.theme.BuoyoTheme
import top.fatweb.buoyo.ui.theme.GradientColors
import top.fatweb.buoyo.ui.theme.LocalBackgroundTheme
import top.fatweb.buoyo.ui.theme.LocalGradientColors
import kotlin.math.tan

@Composable
fun Background(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val color = LocalBackgroundTheme.current.color
    val tonalElevation = LocalBackgroundTheme.current.tonalElevation

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = if (color == Color.Unspecified) Color.Transparent else color,
        tonalElevation = if (tonalElevation == Dp.Unspecified) 0.dp else tonalElevation
    ) {
        CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
            content()
        }
    }
}

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    gradientColors: GradientColors = LocalGradientColors.current,
    content: @Composable () -> Unit
) {
    val currentTopColor by rememberUpdatedState(gradientColors.top)
    val currentBottomColor by rememberUpdatedState(gradientColors.bottom)

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = if (gradientColors.container == Color.Unspecified) Color.Transparent else gradientColors.container
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val offset = size.height * tan(
                        Math
                            .toRadians(11.06)
                            .toFloat()
                    )

                    val start = Offset(size.width / 2 + offset / 2, 0f)
                    val end = Offset(size.width / 2 - offset / 2, size.height)

                    val topGradient = Brush.linearGradient(
                        0f to if (currentTopColor == Color.Unspecified) Color.Transparent else currentTopColor,
                        0.724f to Color.Transparent,
                        start = start,
                        end = end
                    )

                    val bottomGradient = Brush.linearGradient(
                        0.2552f to Color.Transparent,
                        1f to if (currentBottomColor == Color.Unspecified) Color.Transparent else currentBottomColor,
                        start = start,
                        end = end
                    )

                    onDrawBehind {
                        drawRect(topGradient)
                        drawRect(bottomGradient)
                    }
                }
        ) {
            content()
        }
    }
}

@BuoyoPreviews
@Composable
fun DefaultBackgroundPreview() {
    BuoyoTheme(
        dynamicColor = false
    ) {
        Background(
            modifier = Modifier
                .size(100.dp)
        ) {}
    }
}

@BuoyoPreviews
@Composable
fun DynamicBackgroundPreview() {
    BuoyoTheme(dynamicColor = true) {
        Background(
            modifier = Modifier
                .size(100.dp)
        ) {}
    }
}

@BuoyoPreviews
@Composable
fun AndroidBackgroundPreview() {
    BuoyoTheme(androidTheme = true) {
        Background(
            modifier = Modifier
                .size(100.dp)
        ) {}
    }
}

@BuoyoPreviews
@Composable
fun GradientDefaultBackgroundPreview() {
    BuoyoTheme(dynamicColor = false) {
        GradientBackground(
            modifier = Modifier
                .size(100.dp)
        ) {}
    }
}

@BuoyoPreviews
@Composable
fun GradientDynamicBackgroundPreview() {
    BuoyoTheme(dynamicColor = true) {
        GradientBackground(
            modifier = Modifier
                .size(100.dp)
        ) {}
    }
}

@BuoyoPreviews
@Composable
fun GradientAndroidBackgroundPreview() {
    BuoyoTheme(androidTheme = true) {
        GradientBackground(
            modifier = Modifier
                .size(100.dp)
        ) {}
    }
}
