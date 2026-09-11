package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.min

/**
 * P6 replacement reorder engine.
 *
 * This is intentionally a pick-up-and-move interaction, not platform data drag-and-drop and not
 * the former threshold/index-step gesture. A collection owns one session. Items report their real
 * rendered bounds; the lifted item follows the pointer continuously while preview order is derived
 * from the nearest rendered slot. Canonical order is committed only when the gesture ends.
 */
@Stable
internal class CharacterSpatialReorderStateV4 internal constructor(
    initialCanonicalOrder: List<String>,
) {
    private var canonicalOrderSnapshot: List<String> = initialCanonicalOrder.distinct()
    private val itemBounds = mutableStateMapOf<String, Rect>()

    var previewOrder: List<String> by mutableStateOf(canonicalOrderSnapshot)
        private set

    var draggedId: String? by mutableStateOf(null)
        private set

    var dragDelta: Offset by mutableStateOf(Offset.Zero)
        private set

    var pointerInRoot: Offset? by mutableStateOf(null)
        private set

    var viewportBounds: Rect? by mutableStateOf(null)
        private set

    private var sourceBounds: Rect? = null
    private var pickupOffsetInItem: Offset = Offset.Zero
    private var onCommitOrder: (List<String>) -> Unit = {}
    private var onHaptic: (CharacterHapticEventV4) -> Unit = {}
    private var autoScrollBy: (suspend (Float) -> Float)? = null

    val active: Boolean
        get() = draggedId != null

    fun updateCallbacks(
        onCommitOrder: (List<String>) -> Unit,
        onHaptic: (CharacterHapticEventV4) -> Unit,
        autoScrollBy: (suspend (Float) -> Float)?,
    ) {
        this.onCommitOrder = onCommitOrder
        this.onHaptic = onHaptic
        this.autoScrollBy = autoScrollBy
    }

    fun syncCanonicalOrder(order: List<String>) {
        val normalized = order.distinct()
        if (active) return
        canonicalOrderSnapshot = normalized
        if (previewOrder != normalized) previewOrder = normalized
        itemBounds.keys.retainAll(normalized.toSet())
    }

    fun registerBounds(id: String, bounds: Rect) {
        if (id !in previewOrder) return
        itemBounds[id] = bounds
        if (active) retargetFromGeometry()
    }

    fun setViewport(bounds: Rect) {
        viewportBounds = bounds
    }

    fun beginDrag(id: String, pickupOffset: Offset): Boolean {
        if (active || id !in previewOrder) return false
        val bounds = itemBounds[id] ?: return false
        canonicalOrderSnapshot = previewOrder.toList()
        draggedId = id
        sourceBounds = bounds
        pickupOffsetInItem = pickupOffset
        dragDelta = Offset.Zero
        pointerInRoot = bounds.topLeft + pickupOffset
        onHaptic(CharacterHapticEventV4.DRAG_PICKUP)
        return true
    }

    fun dragBy(delta: Offset) {
        if (!active) return
        dragDelta += delta
        pointerInRoot = pointerInRoot?.plus(delta)
        retargetFromGeometry()
    }

    fun draggedTranslation(id: String): Offset {
        if (id != draggedId) return Offset.Zero
        val initial = sourceBounds ?: return dragDelta
        val current = itemBounds[id] ?: return dragDelta
        // Keep the original pickup point visually anchored under the pointer even after preview
        // reflow or scroll moves the item's layout slot beneath the lifted drawing.
        return (initial.topLeft + dragDelta) - current.topLeft
    }

    fun finishDrag() {
        if (!active) return
        val finalOrder = previewOrder.toList()
        val changed = finalOrder != canonicalOrderSnapshot
        clearTransient(keepPreview = true)
        if (changed) {
            canonicalOrderSnapshot = finalOrder
            onCommitOrder(finalOrder)
        }
        onHaptic(CharacterHapticEventV4.DRAG_DROP)
    }

    fun cancelDrag() {
        if (!active) return
        previewOrder = canonicalOrderSnapshot
        clearTransient(keepPreview = true)
    }

    private fun clearTransient(keepPreview: Boolean) {
        draggedId = null
        sourceBounds = null
        pickupOffsetInItem = Offset.Zero
        dragDelta = Offset.Zero
        pointerInRoot = null
        if (!keepPreview) previewOrder = canonicalOrderSnapshot
    }

    private fun retargetFromGeometry() {
        val dragged = draggedId ?: return
        val initial = sourceBounds ?: return
        val visualCenter = initial.center + dragDelta

        var bestIndex = previewOrder.indexOf(dragged)
        var bestDistance = Float.POSITIVE_INFINITY

        previewOrder.forEachIndexed { index, id ->
            val bounds = itemBounds[id] ?: return@forEachIndexed
            val slotCenter = bounds.center
            val distance = hypot(
                (visualCenter.x - slotCenter.x).toDouble(),
                (visualCenter.y - slotCenter.y).toDouble(),
            ).toFloat()
            if (distance < bestDistance) {
                bestDistance = distance
                bestIndex = index
            }
        }

        val currentIndex = previewOrder.indexOf(dragged)
        if (currentIndex < 0 || bestIndex < 0 || bestIndex == currentIndex) return

        val reordered = previewOrder.toMutableList()
        reordered.removeAt(currentIndex)
        val insertionIndex = bestIndex.coerceIn(0, reordered.size)
        reordered.add(insertionIndex, dragged)
        if (reordered != previewOrder) {
            previewOrder = reordered
            onHaptic(CharacterHapticEventV4.DRAG_STEP)
        }
    }

    suspend fun autoScrollOneFrame(density: Float): Boolean {
        val scroll = autoScrollBy ?: return false
        val viewport = viewportBounds ?: return false
        val pointer = pointerInRoot ?: return false
        if (!active || viewport.height <= 0f) return false

        val maxBandPx = 88f * density
        val minBandPx = 44f * density
        val band = min(maxBandPx, viewport.height * 0.18f).coerceAtLeast(minBandPx)
        val topDistance = pointer.y - viewport.top
        val bottomDistance = viewport.bottom - pointer.y
        val direction: Float
        val proximity: Float
        when {
            topDistance < band -> {
                direction = -1f
                proximity = (1f - (topDistance / band)).coerceIn(0f, 1f)
            }
            bottomDistance < band -> {
                direction = 1f
                proximity = (1f - (bottomDistance / band)).coerceIn(0f, 1f)
            }
            else -> return false
        }

        // Per-frame speed: deliberately modest at the band edge, accelerating near the viewport
        // boundary. It is a scroll assist, not a fling.
        val maxPerFrame = 22f * density
        val minPerFrame = 2f * density
        val requested = direction * (minPerFrame + (maxPerFrame - minPerFrame) * proximity * proximity)
        val consumed = scroll(requested)
        return abs(consumed) > 0.01f
    }
}

@Composable
internal fun rememberCharacterSpatialReorderStateV4(
    canonicalOrder: List<String>,
    onCommitOrder: (List<String>) -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    autoScrollBy: (suspend (Float) -> Float)? = null,
): CharacterSpatialReorderStateV4 {
    val state = remember { CharacterSpatialReorderStateV4(canonicalOrder) }
    val currentCommit by rememberUpdatedState(onCommitOrder)
    val currentHaptic by rememberUpdatedState(onHaptic)
    val currentAutoScroll by rememberUpdatedState(autoScrollBy)
    SideEffect {
        state.updateCallbacks(
            onCommitOrder = { currentCommit(it) },
            onHaptic = { currentHaptic(it) },
            autoScrollBy = currentAutoScroll,
        )
        state.syncCanonicalOrder(canonicalOrder)
    }
    return state
}

/** Register the scrollable collection viewport used by edge auto-scroll. */
internal fun Modifier.characterSpatialReorderViewportV4(
    vararg states: CharacterSpatialReorderStateV4,
): Modifier = onGloballyPositioned { coordinates ->
    val bounds = coordinates.boundsInRoot()
    states.forEach { it.setViewport(bounds) }
}

/**
 * Attach to the reorderable card/row body. Short taps are left to existing click handlers; the
 * reorder session starts only after Compose resolves a long-press drag gesture.
 */
internal fun Modifier.characterSpatialReorderItemV4(
    state: CharacterSpatialReorderStateV4,
    id: String,
    enabled: Boolean,
): Modifier {
    val geometry = onGloballyPositioned { state.registerBounds(id, it.boundsInRoot()) }
    return if (!enabled) {
        geometry
    } else {
        geometry.pointerInput(state, id, enabled) {
            var pickupAccepted = false
            detectDragGesturesAfterLongPress(
                onDragStart = { localOffset ->
                    pickupAccepted = state.beginDrag(id, localOffset)
                },
                onDrag = { change, amount ->
                    if (pickupAccepted) {
                        change.consume()
                        state.dragBy(amount)
                    }
                },
                onDragEnd = {
                    if (pickupAccepted) state.finishDrag()
                    pickupAccepted = false
                },
                onDragCancel = {
                    if (pickupAccepted) state.cancelDrag()
                    pickupAccepted = false
                },
            )
        }
    }
}

/**
 * Draw feedback for the actively lifted item. Active pointer tracking is intentionally direct;
 * only the pickup scale/elevation settles through a spring.
 */
@Composable
internal fun Modifier.characterSpatialReorderVisualV4(
    state: CharacterSpatialReorderStateV4,
    id: String,
): Modifier {
    val active = state.draggedId == id
    val scale = animateFloatAsState(
        targetValue = if (active) 1.035f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "spatial-reorder-pickup-scale",
    ).value
    val elevation = animateFloatAsState(
        targetValue = if (active) 18f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "spatial-reorder-pickup-elevation",
    ).value
    val translation = if (active) state.draggedTranslation(id) else Offset.Zero
    return zIndex(if (active) 50f else 0f).graphicsLayer {
        translationX = translation.x
        translationY = translation.y
        scaleX = scale
        scaleY = scale
        shadowElevation = elevation.dp.toPx()
    }
}

/** Run edge auto-scroll only while a reorder session is active. */
@Composable
internal fun CharacterSpatialReorderAutoScrollEffectV4(
    state: CharacterSpatialReorderStateV4,
) {
    val density = LocalDensity.current.density
    LaunchedEffect(state, state.active) {
        if (!state.active) return@LaunchedEffect
        while (isActive && state.active) {
            androidx.compose.runtime.withFrameNanos { }
            state.autoScrollOneFrame(density)
        }
    }
}
