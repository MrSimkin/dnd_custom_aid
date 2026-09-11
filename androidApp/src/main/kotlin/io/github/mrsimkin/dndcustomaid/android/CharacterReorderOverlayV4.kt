package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

/**
 * Keeps the lifted card composed independently from a lazy list/grid placeholder.
 *
 * The scrolling content should render `session.previewOrder`, keep the active item's normal layout
 * slot as a transparent placeholder, and register rendered slot bounds. The overlay copy remains
 * visible even when that placeholder is recycled outside the lazy viewport.
 */
@Composable
internal fun CharacterReorderOverlayHostV4(
    session: CharacterReorderSessionV4,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
    liftedContent: @Composable BoxScope.(draggedId: String) -> Unit,
) {
    var hostBounds by remember(session) { mutableStateOf<Rect?>(null) }
    val density = LocalDensity.current

    Box(
        modifier = modifier.onGloballyPositioned { hostBounds = it.boundsInRoot() },
    ) {
        content()

        val id = session.draggedId
        val visualBounds = session.draggedVisualBounds
        val root = hostBounds
        if (id != null && visualBounds != null && root != null) {
            val width = with(density) { visualBounds.width.toDp() }
            val height = with(density) { visualBounds.height.toDp() }
            Box(
                modifier = Modifier
                    .zIndex(100f)
                    .graphicsLayer {
                        translationX = visualBounds.left - root.left
                        translationY = visualBounds.top - root.top
                        scaleX = 1.035f
                        scaleY = 1.035f
                        shadowElevation = 18.dp.toPx()
                    }
                    .size(width = width, height = height),
            ) {
                liftedContent(id)
            }
        }
    }
}

/** Keep the active item as a real measured destination slot while its visual copy lives in overlay. */
@Composable
internal fun Modifier.characterReorderPlaceholderV4(
    session: CharacterReorderSessionV4,
    id: String,
): Modifier {
    val hidden = session.draggedId == id
    return graphicsLayer { alpha = if (hidden) 0f else 1f }
}
