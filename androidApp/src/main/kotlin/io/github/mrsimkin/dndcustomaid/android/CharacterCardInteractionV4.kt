package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder
import kotlin.math.abs

internal fun characterReorderEnabledV4(
    structuralEditingEnabled: Boolean,
    order: CharacterPresentationOrder,
    query: CharacterCollectionQuery,
): Boolean =
    structuralEditingEnabled &&
        order == CharacterPresentationOrder.MANUAL &&
        query.searchText.isBlank() &&
        query.activeFilterKeys.isEmpty()

/**
 * Low-level long-press drag detector with callback state kept current across recomposition.
 * Prefer [characterMeasuredReorderDragV4] for reorderable cards so movement uses the rendered
 * card geometry and cannot execute several logical moves from one pointer update.
 */
@Composable
internal fun Modifier.characterLongPressDragV4(
    enabled: Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    onDragStart: () -> Unit,
    onDragDelta: (Float) -> Boolean,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit = onDragEnd,
): Modifier {
    val currentHaptic by rememberUpdatedState(onHaptic)
    val currentDragStart by rememberUpdatedState(onDragStart)
    val currentDragDelta by rememberUpdatedState(onDragDelta)
    val currentDragEnd by rememberUpdatedState(onDragEnd)
    val currentDragCancel by rememberUpdatedState(onDragCancel)

    return if (!enabled) {
        this
    } else {
        pointerInput(enabled) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    currentHaptic(CharacterHapticEventV4.DRAG_PICKUP)
                    currentDragStart()
                },
                onDragEnd = {
                    currentDragEnd()
                    currentHaptic(CharacterHapticEventV4.DRAG_DROP)
                },
                onDragCancel = {
                    currentDragCancel()
                    currentHaptic(CharacterHapticEventV4.DRAG_DROP)
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    if (currentDragDelta(dragAmount.y)) {
                        currentHaptic(CharacterHapticEventV4.DRAG_STEP)
                    }
                },
            )
        }
    }
}

/**
 * Whole-card reorder primitive.
 *
 * The rendered card height determines the movement threshold. A single pointer update can perform
 * at most one logical move, and accumulated distance resets after a successful move. This avoids
 * the old fixed-dp / `while` behavior that could skip multiple cards. Callback freshness is
 * inherited from [characterLongPressDragV4], so a live reorder never keeps acting on a stale index.
 *
 * Attach this to the card surface (or its primary body), not to a visible drag handle. Ordinary
 * taps remain available to nested controls; reorder starts only after the long-press gesture.
 */
@Composable
internal fun Modifier.characterMeasuredReorderDragV4(
    enabled: Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    onMove: (Int) -> Boolean,
    onVisualStateChange: (CharacterDragVisualStateV4) -> Unit,
    thresholdFraction: Float = 0.55f,
): Modifier {
    require(thresholdFraction in 0.35f..0.9f)
    var measuredHeightPx by remember { mutableIntStateOf(0) }
    var accumulatedDrag by remember { mutableFloatStateOf(0f) }
    val minimumStepPx = with(LocalDensity.current) { 32.dp.toPx() }
    val currentMove by rememberUpdatedState(onMove)
    val currentVisualStateChange by rememberUpdatedState(onVisualStateChange)

    fun publish(active: Boolean) {
        currentVisualStateChange(
            CharacterDragVisualStateV4(
                active = active,
                offsetY = accumulatedDrag,
                showDropBefore = active && accumulatedDrag < 0f,
                showDropAfter = active && accumulatedDrag > 0f,
            ),
        )
    }

    return if (!enabled) {
        this
    } else {
        this
            .onSizeChanged { measuredHeightPx = it.height }
            .characterLongPressDragV4(
                enabled = true,
                onHaptic = onHaptic,
                onDragStart = {
                    accumulatedDrag = 0f
                    publish(active = true)
                },
                onDragDelta = { deltaY ->
                    accumulatedDrag += deltaY
                    val stepPx = (measuredHeightPx * thresholdFraction).coerceAtLeast(minimumStepPx)
                    val moved = if (abs(accumulatedDrag) >= stepPx) {
                        val direction = if (accumulatedDrag > 0f) 1 else -1
                        if (currentMove(direction)) {
                            accumulatedDrag = 0f
                            true
                        } else {
                            accumulatedDrag = 0f
                            false
                        }
                    } else {
                        false
                    }
                    publish(active = true)
                    moved
                },
                onDragEnd = {
                    accumulatedDrag = 0f
                    publish(active = false)
                },
                onDragCancel = {
                    accumulatedDrag = 0f
                    publish(active = false)
                },
            )
    }
}

/**
 * Two-dimensional counterpart for card collections laid out in multiple columns.
 * It deliberately coexists with [characterMeasuredReorderDragV4]: one-column lists keep the
 * already-auditioned vertical interaction, while grid callers can express row/column movement.
 */
@Composable
internal fun Modifier.characterLongPressDrag2DV4(
    enabled: Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    onDragStart: () -> Unit,
    onDragDelta: (Offset) -> Boolean,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit = onDragEnd,
): Modifier {
    val currentHaptic by rememberUpdatedState(onHaptic)
    val currentDragStart by rememberUpdatedState(onDragStart)
    val currentDragDelta by rememberUpdatedState(onDragDelta)
    val currentDragEnd by rememberUpdatedState(onDragEnd)
    val currentDragCancel by rememberUpdatedState(onDragCancel)

    return if (!enabled) {
        this
    } else {
        pointerInput(enabled) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    currentHaptic(CharacterHapticEventV4.DRAG_PICKUP)
                    currentDragStart()
                },
                onDragEnd = {
                    currentDragEnd()
                    currentHaptic(CharacterHapticEventV4.DRAG_DROP)
                },
                onDragCancel = {
                    currentDragCancel()
                    currentHaptic(CharacterHapticEventV4.DRAG_DROP)
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    if (currentDragDelta(dragAmount)) {
                        currentHaptic(CharacterHapticEventV4.DRAG_STEP)
                    }
                },
            )
        }
    }
}

/**
 * Measured whole-card reorder for row-major multi-column collections.
 *
 * X movement requests a column step and Y movement requests a row step. Diagonal motion follows
 * the axis that has crossed the larger normalized fraction of its own measured card dimension.
 * One pointer update still performs at most one logical move. The dragged card visibly follows
 * both axes, so horizontal/diagonal intent is no longer invisible.
 */
@Composable
internal fun Modifier.characterMeasuredGridReorderDragV4(
    enabled: Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    onMove: (Int, Int) -> Boolean,
    onVisualStateChange: (CharacterDragVisualStateV4) -> Unit,
    thresholdFraction: Float = 0.55f,
): Modifier {
    require(thresholdFraction in 0.35f..0.9f)
    var measuredWidthPx by remember { mutableIntStateOf(0) }
    var measuredHeightPx by remember { mutableIntStateOf(0) }
    var accumulatedX by remember { mutableFloatStateOf(0f) }
    var accumulatedY by remember { mutableFloatStateOf(0f) }
    val minimumStepPx = with(LocalDensity.current) { 32.dp.toPx() }
    val currentMove by rememberUpdatedState(onMove)
    val currentVisualStateChange by rememberUpdatedState(onVisualStateChange)

    fun stepX(): Float = (measuredWidthPx * thresholdFraction).coerceAtLeast(minimumStepPx)
    fun stepY(): Float = (measuredHeightPx * thresholdFraction).coerceAtLeast(minimumStepPx)

    fun publish(active: Boolean) {
        val normalizedX = abs(accumulatedX) / stepX()
        val normalizedY = abs(accumulatedY) / stepY()
        val verticalDominant = normalizedY >= normalizedX
        currentVisualStateChange(
            CharacterDragVisualStateV4(
                active = active,
                offsetX = accumulatedX,
                offsetY = accumulatedY,
                showDropBefore = active && verticalDominant && accumulatedY < 0f,
                showDropAfter = active && verticalDominant && accumulatedY > 0f,
            ),
        )
    }

    return if (!enabled) {
        this
    } else {
        this
            .onSizeChanged {
                measuredWidthPx = it.width
                measuredHeightPx = it.height
            }
            .characterLongPressDrag2DV4(
                enabled = true,
                onHaptic = onHaptic,
                onDragStart = {
                    accumulatedX = 0f
                    accumulatedY = 0f
                    publish(active = true)
                },
                onDragDelta = { delta ->
                    accumulatedX += delta.x
                    accumulatedY += delta.y
                    val normalizedX = abs(accumulatedX) / stepX()
                    val normalizedY = abs(accumulatedY) / stepY()
                    var moved = false
                    if (normalizedX >= 1f || normalizedY >= 1f) {
                        val horizontal = normalizedX >= normalizedY
                        val rowDelta = if (horizontal) 0 else if (accumulatedY > 0f) 1 else -1
                        val columnDelta = if (horizontal) if (accumulatedX > 0f) 1 else -1 else 0
                        moved = currentMove(rowDelta, columnDelta)
                        if (moved) {
                            accumulatedX = 0f
                            accumulatedY = 0f
                        } else if (horizontal) {
                            accumulatedX = 0f
                        } else {
                            accumulatedY = 0f
                        }
                    }
                    publish(active = true)
                    moved
                },
                onDragEnd = {
                    accumulatedX = 0f
                    accumulatedY = 0f
                    publish(active = false)
                },
                onDragCancel = {
                    accumulatedX = 0f
                    accumulatedY = 0f
                    publish(active = false)
                },
            )
    }
}
