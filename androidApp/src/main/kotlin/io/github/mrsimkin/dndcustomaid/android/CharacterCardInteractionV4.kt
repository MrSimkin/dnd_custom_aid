package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder

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
 * Attach this only to the non-interactive body of a reorderable card.
 * Buttons, text fields, menus and other nested controls must remain outside this modifier so
 * their own gestures never become drag initiation points.
 *
 * [onDragDelta] returns true only when the drag crossed a logical reorder step. That keeps
 * haptic step feedback tied to actual movement rather than every pointer event.
 *
 * Callback state is kept current while the pointer gesture remains active. This matters during
 * live reordering because recomposition can move the same card to a new index before the user
 * lifts their finger; restarting pointerInput would cancel the drag, while stale callbacks would
 * keep acting on the old index.
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
