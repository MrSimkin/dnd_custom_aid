package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

/*
 * Base para DM · what a PC needs and a tablet does not (owner request 2026-11-12): a visible
 * scroll bar on long columns and the hand cursor over what can be clicked. On Android these do
 * nothing visible; the «← Atrás» button and the Esc / Alt+← keys live in DmWorkspace.
 */

/**
 * Vertical scrolling for a desk column; on a PC it also draws a thin scroll bar so the DM sees
 * that there is more below (the mouse wheel scrolls it).
 */
@Composable
internal fun Modifier.dmScroll(): Modifier {
    val state = rememberScrollState()
    val desktop = LocalDmPlatform.current.isDesktop
    if (!desktop) return verticalScroll(state)
    val thumb = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f)
    return this
        .drawWithContent {
            drawContent()
            val max = state.maxValue
            if (max > 0 && max != Int.MAX_VALUE) {
                val viewport = size.height
                val total = viewport + max
                val height = (viewport * viewport / total).coerceAtLeast(24.dp.toPx())
                val top = (viewport - height) * state.value / max
                val width = 6.dp.toPx()
                drawRoundRect(
                    color = thumb,
                    topLeft = Offset(size.width - width - 2.dp.toPx(), top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(width / 2, width / 2),
                )
            }
        }
        .verticalScroll(state)
}

/** The hand cursor over a clickable element on a PC (no effect on touch screens). */
internal fun Modifier.handCursor(): Modifier = pointerHoverIcon(PointerIcon.Hand)
