package top.fatweb.buoyo.ui.component.scrollbar

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * The time period for showing the scrollbar thumb after interacting with it, before it fades away
 */
private const val SCROLLBAR_INACTIVE_TO_DORMANT_TIME_IN_MS = 2_000L

/**
 * A [Scrollbar] that allows for fast scrolling of content by dragging its thumb.
 * Its thumb disappears when the scrolling container is dormant.
 * @param modifier a [Modifier] for the [Scrollbar]
 * @param state the driving state for the [Scrollbar]
 * @param orientation the orientation of the scrollbar
 * @param onThumbMoved the fast scroll implementation
 */
@Composable
fun ScrollableState.DraggableScrollbar(
    modifier: Modifier = Modifier,
    state: ScrollbarState,
    orientation: Orientation,
    onThumbMoved: (Float) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Scrollbar(
        modifier = modifier,
        orientation = orientation,
        interactionSource = interactionSource,
        state = state,
        thumb = {
            DraggableScrollbarThumb(
                interactionSource = interactionSource,
                orientation = orientation
            )
        },
        onThumbMoved = onThumbMoved
    )
}

/**
 * A scrollbar thumb that is intended to also be a touch target for fast scrolling.
 */
@Composable
private fun ScrollableState.DraggableScrollbarThumb(
    interactionSource: InteractionSource,
    orientation: Orientation
) {
    Box(
        modifier = Modifier
            .run {
                when (orientation) {
                    Orientation.Vertical -> width(2.dp).fillMaxHeight()
                    Orientation.Horizontal -> height(2.dp).fillMaxWidth()
                }
            }
            .scrollThumb(this, interactionSource)
    )
}

/**
 * A simple [Scrollbar].
 * Its thumb disappears when the scrolling container is dormant.
 * @param modifier a [Modifier] for the [Scrollbar]
 * @param state the driving state for the [Scrollbar]
 * @param orientation the orientation of the scrollbar
 */
@Composable
fun ScrollableState.DecorativeScrollbar(
    modifier: Modifier,
    state: ScrollbarState,
    orientation: Orientation
) {
    val interactionSource = remember { MutableInteractionSource() }
    Scrollbar(
        modifier = modifier,
        orientation = orientation,
        interactionSource = interactionSource,
        state = state,
        thumb = {
            DecorativeScrollbarThumb(
                interactionSource = interactionSource,
                orientation = orientation
            )
        }
    )
}

/**
 * A decorative scrollbar thumb used solely for communicating a user's position in a list.
 */
@Composable
private fun ScrollableState.DecorativeScrollbarThumb(
    interactionSource: InteractionSource,
    orientation: Orientation
) {
    Box(
        modifier = Modifier
            .run {
                when (orientation) {
                    Orientation.Vertical -> width(2.dp).fillMaxHeight()
                    Orientation.Horizontal -> height(2.dp).fillMaxWidth()
                }
            }
            .scrollThumb(this, interactionSource)
    )
}

@Composable
private fun Modifier.scrollThumb(
    scrollbarState: ScrollableState,
    interactionSource: InteractionSource
): Modifier {
    val colorState =
        scrollbarThumbColor(scrollableState = scrollbarState, interactionSource = interactionSource)
    return this then ScrollThumbElement { colorState.value }
}

@SuppressLint("ModifierNodeInspectableProperties")
private data class ScrollThumbElement(val colorProducer: ColorProducer) :
    ModifierNodeElement<ScrollThumbNode>() {
    override fun create(): ScrollThumbNode = ScrollThumbNode(colorProducer)

    override fun update(node: ScrollThumbNode) {
        node.colorProducer = colorProducer
        node.invalidateDraw()
    }
}

private class ScrollThumbNode(var colorProducer: ColorProducer) : DrawModifierNode,
    Modifier.Node() {
    private val shape = RoundedCornerShape(16.dp)

    // Naive cache outline calculation if size is th same
    private var lastSize: Size? = null
    private var lastLayoutDirection: LayoutDirection? = null
    private var lastoutline: Outline? = null

    override fun ContentDrawScope.draw() {
        val color = colorProducer()
        val outline =
            if (size == lastSize && layoutDirection == lastLayoutDirection) {
                lastoutline!!
            } else {
                shape.createOutline(size, layoutDirection, this)
            }
        if (color != Color.Unspecified) drawOutline(outline, color)

        lastoutline = outline
        lastSize = size
        lastLayoutDirection = layoutDirection
    }
}

/**
 * The color of the scrollbar thumb as a function of its interaction state.
 * @param interactionSource source of interactions in the scrolling container
 */
@Composable
private fun scrollbarThumbColor(
    scrollableState: ScrollableState,
    interactionSource: InteractionSource
): State<Color> {
    var state by remember { mutableStateOf(ThumbState.Dormant) }
    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val dragged by interactionSource.collectIsDraggedAsState()
    val active =
        (scrollableState.canScrollForward || scrollableState.canScrollBackward && (pressed || hovered || dragged || scrollableState.isScrollInProgress))

    val color = animateColorAsState(
        targetValue = when (state) {
            ThumbState.Active -> MaterialTheme.colorScheme.onSurface.copy(0.5f)
            ThumbState.Inactive -> MaterialTheme.colorScheme.onSurface.copy(0.2f)
            ThumbState.Dormant -> Color.Transparent
        },
        animationSpec = SpringSpec(
            stiffness = Spring.StiffnessLow
        ),
        label = "Scrollbar thumb color"
    )
    LaunchedEffect(active) {
        when (active) {
            true -> state = ThumbState.Active
            false -> if (state == ThumbState.Active) {
                state = ThumbState.Inactive
                delay(SCROLLBAR_INACTIVE_TO_DORMANT_TIME_IN_MS)
                state = ThumbState.Dormant
            }
        }
    }

    return color
}

private enum class ThumbState {
    Active,
    Inactive,
    Dormant,
}
