package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterM4ScopeOperationsTest {
    @Test
    fun proficiencyManualOrderNormalizesAndMovesWithoutChangingIdentity() {
        val first = CharacterProficiency(
            id = Uuid.random(),
            type = CharacterProficiencyType.LANGUAGE,
            name = "Enano",
            sortOrder = 8,
        )
        val second = CharacterProficiency(
            id = Uuid.random(),
            type = CharacterProficiencyType.TOOL,
            name = "Herramientas de ladrón",
            sortOrder = 2,
        )

        val normalized = normalizeCharacterProficiencies(listOf(first, second))
        assertEquals(listOf(second.id, first.id), normalized.map { it.id })
        assertEquals(listOf(0, 1), normalized.map { it.sortOrder })

        val moved = moveCharacterProficiencyManual(normalized, first.id, -1)
        assertEquals(listOf(first.id, second.id), moved.map { it.id })
        assertEquals(listOf(0, 1), moved.map { it.sortOrder })
    }

    @Test
    fun resourceQuickAccessUsesGenericAuthoritativeReferenceAndCanBeRemoved() {
        val resourceId = Uuid.random()
        val added = CharacterClosureState().withQuickAccess(
            kind = CharacterQuickAccessKind.RESOURCE,
            targetId = resourceId,
            enabled = true,
        )

        assertTrue(added.hasQuickAccess(CharacterQuickAccessKind.RESOURCE, resourceId))

        val removed = added.withQuickAccess(
            kind = CharacterQuickAccessKind.RESOURCE,
            targetId = resourceId,
            enabled = false,
        )
        assertFalse(removed.hasQuickAccess(CharacterQuickAccessKind.RESOURCE, resourceId))
        assertEquals(emptyList(), removed.quickAccess)
    }
}
