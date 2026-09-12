package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Explicit interaction policy for F16 Table/read-only mode.
 *
 * Table mode is not a blanket input lock: presentation-only actions and intentional session/live
 * operations remain available. Only structural character/configuration writes are suppressed.
 */
enum class CharacterTableInteractionKind {
    PRESENTATION,
    OPERATIONAL,
    STRUCTURAL,
}

fun isCharacterInteractionAllowedInTableMode(
    tableModeEnabled: Boolean,
    kind: CharacterTableInteractionKind,
): Boolean = !tableModeEnabled || kind != CharacterTableInteractionKind.STRUCTURAL

fun isCharacterStructuralEditingEnabled(tableModeEnabled: Boolean): Boolean =
    isCharacterInteractionAllowedInTableMode(tableModeEnabled, CharacterTableInteractionKind.STRUCTURAL)

/**
 * Merge an operational CharacterSheet proposal onto the persisted structural definition.
 * Only explicit live/session values cross this boundary.
 */
fun mergeCharacterOperationalState(
    persisted: CharacterSheet,
    proposed: CharacterSheet,
): CharacterSheet {
    val proposedSlots = proposed.spellSlots.associateBy(CharacterSpellSlot::level)
    val proposedItems = proposed.inventoryItems.associateBy(CharacterInventoryItem::id)
    val proposedTraits = proposed.traits.associateBy(CharacterTrait::id)
    val proposedResources = proposed.resources.associateBy(CharacterResource::id)

    return persisted.copy(
        currentHp = proposed.currentHp.coerceIn(0, persisted.maxHp.coerceAtLeast(0)),
        tempHp = proposed.tempHp.coerceAtLeast(0),
        inspiration = proposed.inspiration,
        deathSaveSuccesses = proposed.deathSaveSuccesses.coerceIn(0, 3),
        deathSaveFailures = proposed.deathSaveFailures.coerceIn(0, 3),
        spellSlots = persisted.spellSlots.map { slot ->
            val proposedSpent = proposedSlots[slot.level]?.spentSlots ?: slot.spentSlots
            slot.copy(spentSlots = proposedSpent.coerceIn(0, slot.totalSlots.coerceAtLeast(0)))
        },
        inventoryItems = persisted.inventoryItems.map { item ->
            item.copy(quantity = proposedItems[item.id]?.quantity?.coerceAtLeast(0) ?: item.quantity)
        },
        traits = persisted.traits.map { trait ->
            val proposedSpent = proposedTraits[trait.id]?.spentUses ?: trait.spentUses
            val normalizedSpent = trait.maxUses?.let { maximum ->
                proposedSpent.coerceIn(0, maximum.coerceAtLeast(0))
            } ?: proposedSpent.coerceAtLeast(0)
            trait.copy(spentUses = normalizedSpent)
        },
        resources = persisted.resources.map { resource ->
            val proposedValue = proposedResources[resource.id]?.currentValue ?: resource.currentValue
            val normalizedValue = resource.maxValue?.let { maximum ->
                proposedValue.coerceIn(0, maximum.coerceAtLeast(0))
            } ?: proposedValue.coerceAtLeast(0)
            resource.copy(currentValue = normalizedValue)
        },
    )
}

/** Table Mode closure-state boundary: keep session controls, reject structural configuration. */
fun mergeCharacterOperationalClosureState(
    persisted: CharacterClosureState,
    proposed: CharacterClosureState,
): CharacterClosureState = persisted.copy(
    exhaustionLevel = proposed.exhaustionLevel.coerceAtLeast(0),
    concentration = proposed.concentration,
    tableModeEnabled = proposed.tableModeEnabled,
    hapticsEnabled = proposed.hapticsEnabled,
    conditions = proposed.conditions,
    reconciliationCheckpoints = proposed.reconciliationCheckpoints,
    temporaryEffects = proposed.temporaryEffects,
)

/** Table Mode successor-state boundary: live marker values and presentation visibility only. */
fun mergeCharacterOperationalSuccessorState(
    persisted: CharacterSuccessorState,
    proposed: CharacterSuccessorState,
): CharacterSuccessorState {
    val proposedMarkers = proposed.customMarkers.associateBy(CharacterCustomMarker::id)
    val mergedMarkers = persisted.customMarkers.map { marker ->
        val proposedValue = proposedMarkers[marker.id]?.currentValue ?: marker.currentValue
        val normalizedValue = when (marker.valueKind) {
            CharacterTrackableValueKind.BINARY -> proposedValue.coerceIn(0, 1)
            CharacterTrackableValueKind.CURRENT_MAX -> marker.maxValue?.let { maximum ->
                proposedValue.coerceIn(0, maximum.coerceAtLeast(0))
            } ?: proposedValue.coerceAtLeast(0)
            CharacterTrackableValueKind.COUNTER -> proposedValue.coerceAtLeast(0)
        }
        marker.copy(currentValue = normalizedValue)
    }
    return persisted.copy(
        customMarkers = mergedMarkers,
        preferences = persisted.preferences.copy(
            inspirationVisible = proposed.preferences.inspirationVisible,
        ),
    )
}
