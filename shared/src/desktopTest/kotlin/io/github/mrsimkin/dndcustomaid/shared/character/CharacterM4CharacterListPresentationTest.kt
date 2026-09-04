package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class CharacterM4CharacterListPresentationTest {
    @Test
    fun classSummaryIncludesSubclassLevelAndSavedClassOrder() {
        val firstId = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val secondId = Uuid.parse("00000000-0000-0000-0000-000000000002")
        val classes = listOf(
            CharacterClassLevel(
                id = secondId,
                name = "Guerrero",
                level = 2,
                hitDieSides = 10,
                hitDiceRemaining = 2,
                sortOrder = 1,
                subclassName = "Maestro de Batalla",
            ),
            CharacterClassLevel(
                id = firstId,
                name = "Mago",
                level = 5,
                hitDieSides = 6,
                hitDiceRemaining = 5,
                sortOrder = 0,
                subclassName = "Evocación",
            ),
        )

        assertEquals(
            "Mago 5 (Evocación) / Guerrero 2 (Maestro de Batalla)",
            characterListClassSummary(classes),
        )
    }

    @Test
    fun classSummaryHandlesNoClassesAndBlankSubclass() {
        assertEquals("Sin clase registrada", characterListClassSummary(emptyList()))

        val classLevel = CharacterClassLevel(
            id = Uuid.parse("00000000-0000-0000-0000-000000000003"),
            name = "Pícaro",
            level = 3,
            hitDieSides = 8,
            hitDiceRemaining = 3,
            sortOrder = 0,
            subclassName = "   ",
        )
        assertEquals("Pícaro 3", characterListClassSummary(listOf(classLevel)))
    }

    @Test
    fun freshnessUsesStableHumanScaleBoundaries() {
        val now = 1_000_000L

        assertEquals("Actualizado ahora", characterListFreshnessLabel(now - 59, now))
        assertEquals("Actualizado hace 1 min", characterListFreshnessLabel(now - 60, now))
        assertEquals("Actualizado hace 59 min", characterListFreshnessLabel(now - 3_599, now))
        assertEquals("Actualizado hace 1 h", characterListFreshnessLabel(now - 3_600, now))
        assertEquals("Actualizado hace 23 h", characterListFreshnessLabel(now - 86_399, now))
        assertEquals("Actualizado hace 1 d", characterListFreshnessLabel(now - 86_400, now))
        assertEquals("Actualizado ahora", characterListFreshnessLabel(now + 60, now))
    }
}
