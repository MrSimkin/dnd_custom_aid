package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Responsive multi-column host for the P6 spatial reorder engine.
 *
 * The real normal grid is always the layout. The lifted item keeps occupying its transient preview
 * slot (that slot is therefore the placeholder), while its visual drawing follows the pointer.
 * Other cards animate their bounds into the newly available slots through Lookahead/animateBounds.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun CharacterSpatialGridV4(
    ids: List<String>,
    columns: Int,
    reorderState: CharacterSpatialReorderStateV4,
    reorderEnabled: Boolean,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = appSpacingV4(5.dp),
    verticalSpacing: Dp = appSpacingV4(5.dp),
    itemContent: @Composable (
        id: String,
        pickupModifier: Modifier,
    ) -> Unit,
) {
    val safeColumns = columns.coerceAtLeast(1)
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val availableSpacing = horizontalSpacing * (safeColumns - 1)
        val itemWidth = ((maxWidth - availableSpacing) / safeColumns).coerceAtLeast(1.dp)

        LookaheadScope {
            FlowRow(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                maxItemsInEachRow = safeColumns,
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            ) {
                ids.forEach { id ->
                    key(id) {
                        val active = reorderState.draggedId == id
                        val placementAnimation = if (active) {
                            Modifier
                        } else {
                            Modifier.animateBounds(this@LookaheadScope)
                        }
                        Box(
                            modifier = Modifier
                                .width(itemWidth)
                                .then(placementAnimation)
                                .characterSpatialReorderBoundsV4(reorderState, id)
                                .characterSpatialReorderVisualV4(reorderState, id),
                        ) {
                            itemContent(
                                id,
                                Modifier.characterSpatialReorderDragHandleV4(
                                    state = reorderState,
                                    id = id,
                                    enabled = reorderEnabled,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
