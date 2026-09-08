package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
 */
internal fun Modifier.characterLongPressDragV4(
    enabled: Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    onDragStart: () -> Unit,
    onDragDelta: (Float) -> Boolean,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit = onDragEnd,
): Modifier = if (!enabled) {
    this
} else {
    pointerInput(enabled) {
        detectDragGesturesAfterLongPress(
            onDragStart = {
                onHaptic(CharacterHapticEventV4.DRAG_PICKUP)
                onDragStart()
            },
            onDragEnd = {
                onDragEnd()
                onHaptic(CharacterHapticEventV4.DRAG_DROP)
            },
            onDragCancel = {
                onDragCancel()
                onHaptic(CharacterHapticEventV4.DRAG_DROP)
            },
            onDrag = { change, dragAmount ->
                change.consume()
                if (onDragDelta(dragAmount.y)) {
                    onHaptic(CharacterHapticEventV4.DRAG_STEP)
                }
            },
        )
    }
}
