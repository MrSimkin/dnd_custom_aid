package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

fun setCharacterQuickAccessFavorite(
    quickAccess: List<CharacterQuickAccessRef>,
    kind: CharacterQuickAccessKind,
    targetId: Uuid,
    favorite: Boolean,
): List<CharacterQuickAccessRef> {
    val ordered = quickAccess.sortedWith(
        compareBy<CharacterQuickAccessRef> { it.sortOrder }
            .thenBy { it.kind.name }
            .thenBy { it.targetId.toString() },
    )
    val retained = ordered.filterNot { it.kind == kind && it.targetId == targetId }
    val updated = if (favorite) {
        retained + CharacterQuickAccessRef(kind = kind, targetId = targetId, sortOrder = retained.size)
    } else {
        retained
    }
    return updated.mapIndexed { index, reference -> reference.copy(sortOrder = index) }
}

fun pruneCharacterQuickAccessKind(
    quickAccess: List<CharacterQuickAccessRef>,
    kind: CharacterQuickAccessKind,
    liveTargetIds: Set<Uuid>,
): List<CharacterQuickAccessRef> = quickAccess
    .sortedWith(
        compareBy<CharacterQuickAccessRef> { it.sortOrder }
            .thenBy { it.kind.name }
            .thenBy { it.targetId.toString() },
    )
    .filterNot { it.kind == kind && it.targetId !in liveTargetIds }
    .mapIndexed { index, reference -> reference.copy(sortOrder = index) }
