package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/**
 * A lightweight resolved view of a Quick Access reference.
 *
 * The projection deliberately carries identity + display name only. Live values remain owned by
 * their authoritative character/closure domains and must be looked up by [kind] + [targetId] when
 * a UI needs to render or mutate them.
 */
data class CharacterQuickAccessProjection(
    val kind: CharacterQuickAccessKind,
    val targetId: Uuid,
    val name: String,
    val sortOrder: Int,
)

/**
 * Resolves ordered Quick Access references against authoritative character + closure state.
 *
 * Stale references are ignored safely. `OTHER` has no durable target domain in the current model,
 * so it is intentionally unresolved rather than inventing copied display state.
 */
fun projectCharacterQuickAccess(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
): List<CharacterQuickAccessProjection> = closureState.quickAccess
    .sortedBy(CharacterQuickAccessRef::sortOrder)
    .mapNotNull { reference ->
        val name = when (reference.kind) {
            CharacterQuickAccessKind.COMBAT_ENTRY ->
                sheet.combatEntries.firstOrNull { it.id == reference.targetId }?.name
            CharacterQuickAccessKind.TRAIT ->
                sheet.traits.firstOrNull { it.id == reference.targetId }?.name
            CharacterQuickAccessKind.SPELL ->
                sheet.spells.firstOrNull { it.id == reference.targetId }?.name
            CharacterQuickAccessKind.RESOURCE ->
                sheet.resources.firstOrNull { it.id == reference.targetId }?.name
            CharacterQuickAccessKind.CLASS_OPTION ->
                sheet.classOptions.firstOrNull { it.id == reference.targetId }?.name
            CharacterQuickAccessKind.FORM ->
                sheet.forms.firstOrNull { it.id == reference.targetId }?.name
            CharacterQuickAccessKind.COMPANION ->
                sheet.companions.firstOrNull { it.id == reference.targetId }?.name
            CharacterQuickAccessKind.CUSTOM_SKILL ->
                closureState.customSkills.firstOrNull { it.id == reference.targetId }?.name
            CharacterQuickAccessKind.TEMPORARY_EFFECT ->
                closureState.temporaryEffects.firstOrNull { it.id == reference.targetId }?.name
            CharacterQuickAccessKind.OTHER -> null
        }

        name?.let {
            CharacterQuickAccessProjection(
                kind = reference.kind,
                targetId = reference.targetId,
                name = it,
                sortOrder = reference.sortOrder,
            )
        }
    }
