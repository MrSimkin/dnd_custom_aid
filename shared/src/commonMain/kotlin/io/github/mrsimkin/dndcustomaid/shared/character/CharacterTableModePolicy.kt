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
