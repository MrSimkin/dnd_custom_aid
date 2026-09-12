package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterReorderResult
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterReorderSlot
import io.github.mrsimkin.dndcustomaid.shared.character.nearestCharacterReorderIndex
import io.github.mrsimkin.dndcustomaid.shared.character.previewCharacterReorder
import kotlin.math.abs
import kotlin.math.min

/**
 * Screen-level arbitration for P6 reorder sessions.
 *
 * A screen may contain several independently reorderable sections (for example ordinary and
 * special Equipment), but only one pointer/semantic reorder transaction may own the interaction at
 * a time. This avoids overlapping auto-scroll, haptics and draft commits.
 */
@Stable
internal class CharacterReorderCoordinatorV4 {
    var activeSessionKey: String? by mutableStateOf(null)
        private set

    fun tryAcquire(sessionKey: String): Boolean {
        val current = activeSessionKey
        if (current != null && current != sessionKey) return false
        activeSessionKey = sessionKey
        return true
    }

    fun release(sessionKey: String) {
        if (activeSessionKey == sessionKey) activeSessionKey = null
    }
}

@Composable
internal fun rememberCharacterReorderCoordinatorV4(): CharacterReorderCoordinatorV4 =
    remember { CharacterReorderCoordinatorV4() }

/**
 * Hardened P6 reorder session.
 *
 * The session owns only transient interaction state. A successful drop or semantic accessibility
 * action emits one validated final stable-id order to the caller; the caller applies that order to
 * the character structural draft. Durable persistence remains the normal character Save/Cancel
 * boundary.
 */
@Stable
internal class CharacterReorderSessionV4 internal constructor(
    private val sessionKey: String,
    initialCanonicalOrder: List<String>,
    private val coordinator: CharacterReorderCoordinatorV4,
) {
    private var canonicalOrderSnapshot: List<String> = initialCanonicalOrder.distinct()
    private var reorderGroupSnapshot: Map<String, String> = emptyMap()
    private val itemBounds = mutableStateMapOf<String, Rect>()

    var previewOrder: List<String> by mutableStateOf(canonicalOrderSnapshot)
        private set

    var draggedId: String? by mutableStateOf(null)
        private set

    var pointerInRoot: Offset? by mutableStateOf(null)
        private set

    var viewportBounds: Rect? by mutableStateOf(null)
        private set

    var enabled: Boolean by mutableStateOf(false)
        private set

    private var sourceBounds: Rect? = null
    private var dragDelta: Offset = Offset.Zero
    private var onCommitOrder: (List<String>) -> Unit = {}
    private var onHaptic: (CharacterHapticEventV4) -> Unit = {}
    private var autoScrollBy: (suspend (Float) -> Float)? = null

    val active: Boolean
        get() = draggedId != null

    val draggedVisualBounds: Rect?
        get() {
            if (!active) return null
            val source = sourceBounds ?: return null
            return source.translate(dragDelta)
        }

    fun updateCallbacks(
        onCommitOrder: (List<String>) -> Unit,
        onHaptic: (CharacterHapticEventV4) -> Unit,
        autoScrollBy: (suspend (Float) -> Float)?,
    ) {
        this.onCommitOrder = onCommitOrder
        this.onHaptic = onHaptic
        this.autoScrollBy = autoScrollBy
    }

    /**
     * Synchronize external structural state and current reorder eligibility.
     *
     * Any material external order/group change or loss of eligibility during a live drag cancels
     * the transient session. We never reinterpret a gesture against a structurally different draft.
     */
    fun sync(
        canonicalOrder: List<String>,
        enabled: Boolean,
        reorderGroupById: Map<String, String> = emptyMap(),
    ) {
        val normalized = canonicalOrder.distinct()
        val normalizedGroups = reorderGroupById.filterKeys { it in normalized }
        val externalOrderChanged = normalized != canonicalOrderSnapshot
        val externalGroupsChanged = normalizedGroups != reorderGroupSnapshot
        if (active && (!enabled || externalOrderChanged || externalGroupsChanged)) {
            cancelDrag()
        }
        this.enabled = enabled
        if (!active) {
            canonicalOrderSnapshot = normalized
            reorderGroupSnapshot = normalizedGroups
            if (previewOrder != normalized) previewOrder = normalized
            itemBounds.keys.retainAll(normalized.toSet())
        }
    }

    fun registerBounds(id: String, bounds: Rect) {
        if (id !in previewOrder) return
        itemBounds[id] = bounds
        if (active) retargetFromGeometry()
    }

    fun unregisterBounds(id: String) {
        itemBounds.remove(id)
    }

    fun setViewport(bounds: Rect) {
        viewportBounds = bounds
    }

    fun beginDragAtRoot(id: String, pointerRoot: Offset): Boolean {
        if (!enabled || active || id !in previewOrder) return false
        val bounds = itemBounds[id] ?: return false
        if (!coordinator.tryAcquire(sessionKey)) return false
        canonicalOrderSnapshot = previewOrder.toList()
        draggedId = id
        sourceBounds = bounds
        dragDelta = Offset.Zero
        pointerInRoot = pointerRoot
        onHaptic(CharacterHapticEventV4.DRAG_PICKUP)
        return true
    }

    fun dragBy(delta: Offset) {
        if (!active) return
        dragDelta += delta
        pointerInRoot = pointerInRoot?.plus(delta)
        retargetFromGeometry()
    }

    /** Translation for non-lazy/bounded adapters where the original item remains composed. */
    fun draggedTranslation(id: String): Offset {
        if (id != draggedId) return Offset.Zero
        val source = sourceBounds ?: return dragDelta
        val current = itemBounds[id] ?: return dragDelta
        return (source.topLeft + dragDelta) - current.topLeft
    }

    fun finishDrag() {
        if (!active) return
        val finalOrder = applyCharacterReorderResult(canonicalOrderSnapshot, previewOrder)
        val changed = finalOrder != canonicalOrderSnapshot
        clearTransient()
        if (changed) {
            canonicalOrderSnapshot = finalOrder
            previewOrder = finalOrder
            onCommitOrder(finalOrder)
            onHaptic(CharacterHapticEventV4.DRAG_DROP)
        }
    }

    fun cancelDrag() {
        if (!active) return
        previewOrder = canonicalOrderSnapshot
        clearTransient()
    }

    /**
     * Equivalent non-pointer action for TalkBack/switch/keyboard adapters.
     * Each action is one complete validated structural-draft reorder transaction.
     */
    fun semanticMove(id: String, delta: Int): Boolean {
        if (!enabled || active || delta == 0 || id !in canonicalOrderSnapshot) return false
        if (!coordinator.tryAcquire(sessionKey)) return false
        return try {
            val eligibleIds = eligibleOrderFor(id, canonicalOrderSnapshot)
            val sourceIndex = eligibleIds.indexOf(id)
            if (sourceIndex < 0) return false
            val targetIndex = (sourceIndex + delta).coerceIn(0, eligibleIds.lastIndex)
            if (targetIndex == sourceIndex) return false
            val targetId = eligibleIds[targetIndex]
            val globalTargetIndex = canonicalOrderSnapshot.indexOf(targetId)
            val proposed = previewCharacterReorder(canonicalOrderSnapshot, id, globalTargetIndex)
            val finalOrder = applyCharacterReorderResult(canonicalOrderSnapshot, proposed)
            if (finalOrder == canonicalOrderSnapshot) {
                false
            } else {
                canonicalOrderSnapshot = finalOrder
                previewOrder = finalOrder
                onCommitOrder(finalOrder)
                onHaptic(CharacterHapticEventV4.DRAG_STEP)
                true
            }
        } finally {
            coordinator.release(sessionKey)
        }
    }

    fun dispose() {
        if (active) cancelDrag() else coordinator.release(sessionKey)
        itemBounds.clear()
        viewportBounds = null
    }

    private fun clearTransient() {
        draggedId = null
        sourceBounds = null
        dragDelta = Offset.Zero
        pointerInRoot = null
        coordinator.release(sessionKey)
    }

    private fun sameReorderGroup(firstId: String, secondId: String): Boolean {
        if (reorderGroupSnapshot.isEmpty()) return true
        return reorderGroupSnapshot[firstId] == reorderGroupSnapshot[secondId]
    }

    private fun eligibleOrderFor(dragged: String, order: List<String>): List<String> =
        if (reorderGroupSnapshot.isEmpty()) order else order.filter { sameReorderGroup(dragged, it) }

    private fun retargetFromGeometry() {
        val dragged = draggedId ?: return
        val visualBounds = draggedVisualBounds ?: return
        val eligibleOrder = eligibleOrderFor(dragged, previewOrder)
        if (eligibleOrder.size < 2) return
        val eligibleSet = eligibleOrder.toSet()
        val slots = itemBounds.mapNotNull { (id, bounds) ->
            if (id in eligibleSet) {
                CharacterReorderSlot(id = id, centerX = bounds.center.x, centerY = bounds.center.y)
            } else {
                null
            }
        }
        val eligibleTargetIndex = nearestCharacterReorderIndex(
            order = eligibleOrder,
            draggedId = dragged,
            visualCenterX = visualBounds.center.x,
            visualCenterY = visualBounds.center.y,
            renderedSlots = slots,
        )
        val targetId = eligibleOrder.getOrNull(eligibleTargetIndex) ?: return
        val globalTargetIndex = previewOrder.indexOf(targetId)
        if (globalTargetIndex < 0) return
        val reordered = previewCharacterReorder(previewOrder, dragged, globalTargetIndex)
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

        val maxPerFrame = 22f * density
        val minPerFrame = 2f * density
        val requested = direction * (minPerFrame + (maxPerFrame - minPerFrame) * proximity * proximity)
        val consumed = scroll(requested)
        if (abs(consumed) > 0.01f) retargetFromGeometry()
        return abs(consumed) > 0.01f
    }
}

@Composable
internal fun rememberCharacterReorderSessionV4(
    sessionKey: String,
    canonicalOrder: List<String>,
    enabled: Boolean,
    coordinator: CharacterReorderCoordinatorV4,
    onCommitOrder: (List<String>) -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    autoScrollBy: (suspend (Float) -> Float)? = null,
    reorderGroupById: Map<String, String> = emptyMap(),
): CharacterReorderSessionV4 {
    val session = remember(sessionKey, coordinator) {
        CharacterReorderSessionV4(
            sessionKey = sessionKey,
            initialCanonicalOrder = canonicalOrder,
            coordinator = coordinator,
        )
    }
    val currentCommit by rememberUpdatedState(onCommitOrder)
    val currentHaptic by rememberUpdatedState(onHaptic)
    val currentAutoScroll by rememberUpdatedState(autoScrollBy)

    SideEffect {
        session.updateCallbacks(
            onCommitOrder = { currentCommit(it) },
            onHaptic = { currentHaptic(it) },
            autoScrollBy = currentAutoScroll,
        )
        session.sync(
            canonicalOrder = canonicalOrder,
            enabled = enabled,
            reorderGroupById = reorderGroupById,
        )
    }
    DisposableEffect(session) {
        onDispose { session.dispose() }
    }
    return session
}