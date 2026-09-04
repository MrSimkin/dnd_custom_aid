package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharacterTableModePolicyTest {
    @Test
    fun tableModeAllowsPresentationAndOperationalInteractionsButBlocksStructuralWrites() {
        assertTrue(
            isCharacterInteractionAllowedInTableMode(
                tableModeEnabled = true,
                kind = CharacterTableInteractionKind.PRESENTATION,
            ),
        )
        assertTrue(
            isCharacterInteractionAllowedInTableMode(
                tableModeEnabled = true,
                kind = CharacterTableInteractionKind.OPERATIONAL,
            ),
        )
        assertFalse(
            isCharacterInteractionAllowedInTableMode(
                tableModeEnabled = true,
                kind = CharacterTableInteractionKind.STRUCTURAL,
            ),
        )
        assertFalse(isCharacterStructuralEditingEnabled(tableModeEnabled = true))
    }

    @Test
    fun normalModeAllowsAllInteractionKinds() {
        CharacterTableInteractionKind.entries.forEach { kind ->
            assertTrue(isCharacterInteractionAllowedInTableMode(tableModeEnabled = false, kind = kind))
        }
        assertTrue(isCharacterStructuralEditingEnabled(tableModeEnabled = false))
    }
}
