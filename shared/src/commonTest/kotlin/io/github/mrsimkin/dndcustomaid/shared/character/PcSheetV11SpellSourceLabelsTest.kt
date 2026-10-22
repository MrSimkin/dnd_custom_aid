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
    fun uniformGenuineSourcesMayDisagreeWithOverallStatsWithoutContradictingEachOther() {
        val spell = CharacterSpell(
            id = Uuid.parse("aa000003-0000-4000-8000-000000000001"),
            name = "Fuente perfilada", level = 1,
            castingTime = "", rangeText = "", verbal = false,
            somatic = false, material = false, materialText = null,
            duration = "", concentration = false, ritual = false,
            description = "", notes = null, sortOrder = 0,
        )
        val first = PcSheetSpellbookSourceProjection(
            sourceId = Uuid.parse("12345600-0000-4000-8000-000000000004"),
            sourceName = "Mago", originKind = null, prepared = true,
            castingAbility = CharacterAbilityReference.NONE,
            saveDc = 18, spellAttackModifier = 5,
        )
        val entries = listOf(PcSheetSpellbookEntry(
            spell, listOf(
                first, first.copy(sourceId =
                    Uuid.parse("12345600-0000-4000-8000-000000000005")),
            ),
        ))
        // C221 alone sees no contradiction: both sources have the SAME profile.
        assertTrue(PcSheetV11SpellSourceLabels.conflictingReferenceFields(entries).isEmpty())
        // But the overall 15/+7 is NOT a faithful universal P4 source value.
        assertEquals(setOf("CD SALVACIÓN", "ATAQUE MÁGICO"),
            PcSheetV11SpellSourceLabels.globalReferenceDisagreements(
                entries, overallDc = 15, overallAttackModifier = 7,
            ))
        assertTrue(PcSheetV11SpellSourceLabels.globalReferenceDisagreements(
            entries, overallDc = 18, overallAttackModifier = 5,
        ).isEmpty())
        // Missing source-specific data does not create a fabricated disagreement.
        val noProfile = entries.map {
            it.copy(sources = it.sources.map { source ->
                source.copy(saveDc = null, spellAttackModifier = null)
            })
        }
        assertTrue(PcSheetV11SpellSourceLabels.globalReferenceDisagreements(
            noProfile, overallDc = 15, overallAttackModifier = 7,
        ).isEmpty())
    }


    @Test
    fun uniformExplicitAbilityCannotBeReplacedByContradictoryGlobalInt() {
        val spell = CharacterSpell(
            id = Uuid.parse("aa000004-0000-4000-8000-000000000001"),
            name = "Aptitud por origen", level = 1,
            castingTime = "", rangeText = "", verbal = false,
            somatic = false, material = false, materialText = null,
            duration = "", concentration = false, ritual = false,
            description = "", notes = null, sortOrder = 0,
        )
        val first = PcSheetSpellbookSourceProjection(
            sourceId = Uuid.parse("12345600-0000-4000-8000-000000000007"),
            sourceName = "Mago", originKind = null, prepared = true,
            castingAbility = CharacterAbilityReference.builtIn(CharacterAbility.WISDOM),
            saveDc = 15, spellAttackModifier = 7,
        )
        val entries = listOf(PcSheetSpellbookEntry(spell, listOf(
            first, first.copy(sourceId = Uuid.parse(
                "12345600-0000-4000-8000-000000000008")),
        )))
        assertTrue(PcSheetV11SpellSourceLabels.conflictingReferenceFields(entries).isEmpty())
        assertEquals(setOf("APTITUD"), PcSheetV11SpellSourceLabels.globalAbilityDisagreement(
            entries, SpellcastingAbility.INTELLIGENCE,
        ))
        assertTrue(PcSheetV11SpellSourceLabels.globalAbilityDisagreement(
            entries, SpellcastingAbility.WISDOM,
        ).isEmpty())
        val unknown = entries.map { it.copy(sources = it.sources.map { source ->
            source.copy(castingAbility = CharacterAbilityReference.NONE)
        }) }
        assertTrue(PcSheetV11SpellSourceLabels.globalAbilityDisagreement(
            unknown, SpellcastingAbility.INTELLIGENCE,
        ).isEmpty())
        assertTrue(PcSheetV11SpellSourceLabels.globalAbilityDisagreement(
            entries, SpellcastingAbility.NONE,
        ).isEmpty())
        assertTrue(PcSheetV11SpellSourceLabels.globalAbilityDisagreement(
            entries, SpellcastingAbility.OTHER,
        ).isEmpty())
        val custom = entries.map { it.copy(sources = it.sources.map { source ->
            source.copy(castingAbility = CharacterAbilityReference.custom(
                Uuid.parse("12345600-0000-4000-8000-000000000009")))
        }) }
        assertEquals(setOf("APTITUD"), PcSheetV11SpellSourceLabels.globalAbilityDisagreement(
            custom, SpellcastingAbility.INTELLIGENCE,
        ))
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
