package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class PcSheetV11SpellSourceLabelsTest {
    @Test
    fun distinctRealSourcesCannotCreateAFalseUniversalP4Reference() {
        val originalId = Uuid.parse("12345600-0000-4000-8000-000000000001")
        val otherId = Uuid.parse("12345601-0000-4000-8000-000000000002")
        fun profile(id: Uuid, dc: Int?, attack: Int?, ability: CharacterAbilityReference) =
            PcSheetSpellbookSourceProjection(
                sourceId = id, sourceName = "Mismo nombre",
                originKind = null, prepared = false,
                castingAbility = ability, saveDc = dc,
                spellAttackModifier = attack,
            )
        val spell = CharacterSpell(
            id = Uuid.parse("aa000001-0000-4000-8000-000000000001"),
            name = "Conjuro de prueba", level = 1,
            castingTime = "", rangeText = "", verbal = false,
            somatic = false, material = false, materialText = null,
            duration = "", concentration = false, ritual = false,
            description = "", notes = null, sortOrder = 0,
        )
        val known = profile(originalId, 13, 5, CharacterAbilityReference.NONE)
        val other = profile(otherId, 15, null, CharacterAbilityReference.NONE)
        val entries = listOf(PcSheetSpellbookEntry(spell, listOf(known, other)))
        assertEquals(setOf("CD SALVACIÓN", "ATAQUE MÁGICO"),
            PcSheetV11SpellSourceLabels.conflictingReferenceFields(entries))
        // Same values from two source IDs are NOT a contradiction,
        // even when their display names happen to match.
        assertTrue(PcSheetV11SpellSourceLabels.conflictingReferenceFields(
            listOf(PcSheetSpellbookEntry(spell, listOf(known, known.copy(
                sourceId = otherId,
            )))),
        ).isEmpty())
        assertTrue(PcSheetV11SpellSourceLabels.conflictingReferenceFields(
            listOf(PcSheetSpellbookEntry(spell, listOf(known))),
        ).isEmpty())
    }

    @Test
    fun equalVisibleNamesKeepDistinctRealIdsAndIndependentPreparation() {
        val one = Uuid.parse("12345600-0000-4000-8000-000000000001")
        val two = Uuid.parse("12345601-0000-4000-8000-000000000002")
        val three = Uuid.parse("88888888-0000-4000-8000-000000000003")
        fun source(id: Uuid, name: String, prepared: Boolean) =
            PcSheetSpellbookSourceProjection(
                sourceId = id, sourceName = name,
                originKind = null, prepared = prepared,
                castingAbility = CharacterAbilityReference.NONE,
                saveDc = null, spellAttackModifier = null,
            )
        val first = PcSheetSpellbookEntry(
            spell = CharacterSpell(
                id = Uuid.parse("aa000001-0000-4000-8000-000000000001"),
                name = "Prueba", level = 1,
                castingTime = "", rangeText = "", verbal = false,
                somatic = false, material = false, materialText = null,
                duration = "", concentration = false, ritual = false,
                description = "", notes = null, sortOrder = 0,
            ),
            sources = listOf(
                source(one, "Mago", true),
                source(two, "mago ", false),
                source(three, "Dote", false),
            ),
        )
        val names = PcSheetV11SpellSourceLabels.forSpells(listOf(first))
        assertEquals("Mago (#12345600)", names.getValue(one))
        assertEquals("mago (#12345601)", names.getValue(two))
        assertEquals("Dote", names.getValue(three))
        val result = PcSheetV11SpellSourceLabels.perSpell(first, names)
        assertTrue(result.contains("[X] Mago (#12345600)"))
        assertTrue(result.contains("[ ] mago (#12345601)"))
        assertTrue(result.contains("[ ] Dote"))
    }
}
