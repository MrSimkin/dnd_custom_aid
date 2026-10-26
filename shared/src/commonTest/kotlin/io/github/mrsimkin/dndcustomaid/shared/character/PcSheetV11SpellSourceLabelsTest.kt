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
    fun perSourceReferenceListsEachRealSourceWithItsOwnValue() {
        val spell = CharacterSpell(
            id = Uuid.parse("aa000004-0000-4000-8000-000000000001"),
            name = "Dos fuentes", level = 1,
            castingTime = "", rangeText = "", verbal = false,
            somatic = false, material = false, materialText = null,
            duration = "", concentration = false, ritual = false,
            description = "", notes = null, sortOrder = 0,
        )
        val mago = PcSheetSpellbookSourceProjection(
            sourceId = Uuid.parse("12345600-0000-4000-8000-000000000006"),
            sourceName = "Mago", originKind = CharacterSpellcastingOriginKind.CLASS, prepared = true,
            castingAbility = CharacterAbilityReference.builtIn(CharacterAbility.INTELLIGENCE),
            saveDc = 15, spellAttackModifier = 7,
        )
        val hechicero = PcSheetSpellbookSourceProjection(
            sourceId = Uuid.parse("12345600-0000-4000-8000-000000000007"),
            sourceName = "Hechicero", originKind = CharacterSpellcastingOriginKind.CLASS, prepared = false,
            castingAbility = CharacterAbilityReference.builtIn(CharacterAbility.CHARISMA),
            saveDc = 17, spellAttackModifier = null,
        )
        val entries = listOf(PcSheetSpellbookEntry(spell, listOf(mago, hechicero)))
        fun ref(field: String) =
            PcSheetV11SpellSourceLabels.perSourceReference(entries, field) { null }
        assertEquals("Mago 15 / Hechicero 17", ref("CD SALVACIÓN"))
        assertEquals("Mago INT / Hechicero CAR", ref("APTITUD"))
        // A source without the metric shows a dash, never a invented number.
        assertEquals("Mago +7 / Hechicero —", ref("ATAQUE MÁGICO"))
        assertEquals(null, PcSheetV11SpellSourceLabels.perSourceReference(
            emptyList(), "CD SALVACIÓN",
        ) { null })
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
    fun p1AndP4UseSameSourceProfileTruthForThreeFields() {
        val spell = CharacterSpell(
            id = Uuid.parse("aa000005-0000-4000-8000-000000000001"),
            name = "Referencia por fuente", level = 1, castingTime = "",
            rangeText = "", verbal = false, somatic = false,
            material = false, materialText = null, duration = "",
            concentration = false, ritual = false, description = "",
            notes = null, sortOrder = 0,
        )
        val source = PcSheetSpellbookSourceProjection(
            sourceId = Uuid.parse("12345600-0000-4000-8000-000000000011"),
            sourceName = "Mago", originKind = null, prepared = true,
            castingAbility = CharacterAbilityReference.builtIn(CharacterAbility.WISDOM),
            saveDc = 17, spellAttackModifier = 5,
        )
        val entries = listOf(PcSheetSpellbookEntry(spell, listOf(source)))
        val labels = PcSheetV11SpellSourceLabels
        assertEquals(setOf("APTITUD", "CD SALVACIÓN", "ATAQUE MÁGICO"),
            labels.contradictoryOverallFields(
                entries, SpellcastingAbility.INTELLIGENCE, 15, 7,
            ))
        assertTrue(labels.contradictoryOverallFields(
            entries, SpellcastingAbility.WISDOM, 17, 5,
        ).isEmpty())
        assertTrue(labels.contradictoryOverallFields(
            emptyList(), SpellcastingAbility.INTELLIGENCE, 15, 7,
        ).isEmpty())
        val unknown = source.copy(
            sourceId = Uuid.parse("12345600-0000-4000-8000-000000000012"),
            castingAbility = CharacterAbilityReference.NONE,
            saveDc = null, spellAttackModifier = null,
        )
        assertEquals(setOf("APTITUD", "CD SALVACIÓN", "ATAQUE MÁGICO"),
            labels.contradictoryOverallFields(
                listOf(PcSheetSpellbookEntry(spell, listOf(source, unknown))),
                SpellcastingAbility.WISDOM, 17, 5,
            ))
    }


    @Test
    fun customSpellSourceNeverLeavesSourceBackedAptitudeBlankForOtherOrNone() {
        val spell = CharacterSpell(
            id = Uuid.parse("aa000006-0000-4000-8000-000000000001"),
            name = "Cronomancia", level = 1, castingTime = "", rangeText = "",
            verbal = false, somatic = false, material = false, materialText = null,
            duration = "", concentration = false, ritual = false,
            description = "", notes = null, sortOrder = 0,
        )
        val customAbility = CharacterAbilityReference.custom(
            Uuid.parse("7a300000-0000-4000-8000-000000000502"),
        )
        val configured = PcSheetSpellbookSourceProjection(
            sourceId = Uuid.parse("7a300000-0000-4000-8000-000000000100"),
            sourceName = "Cronomancia umbral", originKind = null, prepared = true,
            castingAbility = customAbility, saveDc = 17, spellAttackModifier = 8,
        )
        val real = listOf(PcSheetSpellbookEntry(spell, listOf(configured)))
        for (global in listOf(SpellcastingAbility.OTHER, SpellcastingAbility.NONE)) {
            assertEquals(setOf("APTITUD"),
                PcSheetV11SpellSourceLabels.contradictoryOverallFields(
                    real, global, 17, 8,
                ),
            )
        }
        assertTrue(PcSheetV11SpellSourceLabels.contradictoryOverallFields(
            emptyList(), SpellcastingAbility.OTHER, 17, 8,
        ).isEmpty(), "No persisted spell associations cannot claim a source.")
        val unknown = listOf(PcSheetSpellbookEntry(spell, listOf(
            configured.copy(castingAbility = CharacterAbilityReference.NONE),
        )))
        assertTrue(PcSheetV11SpellSourceLabels.contradictoryOverallFields(
            unknown, SpellcastingAbility.OTHER, 17, 8,
        ).isEmpty(), "Unconfigured source ability must not fabricate a field.")
        assertTrue(PcSheetV11SpellSourceLabels.contradictoryOverallFields(
            real, SpellcastingAbility.INTELLIGENCE, 17, 8,
        ).contains("APTITUD"), "Already-proven C224 contradiction is preserved.")
    }


    @Test
    fun realSourceMetricsWithMissingGlobalOnlyGuardKnownFields() {
        val spell = CharacterSpell(
            id = Uuid.parse("aa000007-0000-4000-8000-000000000001"),
            name = "Referencia derivada", level = 1, castingTime = "",
            rangeText = "", verbal = false, somatic = false, material = false,
            materialText = null, duration = "", concentration = false,
            ritual = false, description = "", notes = null, sortOrder = 0,
        )
        val source = PcSheetSpellbookSourceProjection(
            sourceId = Uuid.parse("12345600-0000-4000-8000-000000000020"),
            sourceName = "Mago", originKind = null, prepared = false,
            castingAbility = CharacterAbilityReference.builtIn(CharacterAbility.INTELLIGENCE),
            saveDc = 15, spellAttackModifier = 7,
        )
        val actual = listOf(PcSheetSpellbookEntry(spell, listOf(source)))
        assertEquals(setOf("CD SALVACIÓN", "ATAQUE MÁGICO"),
            PcSheetV11SpellSourceLabels.contradictoryOverallFields(
                actual, SpellcastingAbility.INTELLIGENCE, null, null,
            ))
        assertTrue(PcSheetV11SpellSourceLabels.contradictoryOverallFields(
            actual, SpellcastingAbility.INTELLIGENCE, 15, 7,
        ).isEmpty())
        assertTrue(PcSheetV11SpellSourceLabels.contradictoryOverallFields(
            emptyList(), SpellcastingAbility.INTELLIGENCE, null, null,
        ).isEmpty())
        assertEquals(setOf("CD SALVACIÓN"),
            PcSheetV11SpellSourceLabels.contradictoryOverallFields(
                listOf(PcSheetSpellbookEntry(spell, listOf(source.copy(
                    spellAttackModifier = null,
                )))),
                SpellcastingAbility.INTELLIGENCE, null, null,
            ))
        assertTrue(PcSheetV11SpellSourceLabels.contradictoryOverallFields(
            listOf(PcSheetSpellbookEntry(spell, listOf(source.copy(
                saveDc = null, spellAttackModifier = null,
            )))),
            SpellcastingAbility.INTELLIGENCE, null, null,
        ).isEmpty(), "An unknown source cannot manufacture either global metric.")
    }

    @Test
    fun sourcesAreLabelledByOriginTypeAndOnlyClassKeepsItsName() {
        val one = Uuid.parse("12345600-0000-4000-8000-000000000001")
        val two = Uuid.parse("12345601-0000-4000-8000-000000000002")
        val three = Uuid.parse("88888888-0000-4000-8000-000000000003")
        val four = Uuid.parse("99999999-0000-4000-8000-000000000004")
        val five = Uuid.parse("77777777-0000-4000-8000-000000000005")
        fun source(
            id: Uuid, name: String, kind: CharacterSpellcastingOriginKind?, prepared: Boolean,
        ) = PcSheetSpellbookSourceProjection(
            sourceId = id, sourceName = name,
            originKind = kind, prepared = prepared,
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
                source(one, "Mago", CharacterSpellcastingOriginKind.CLASS, true),
                source(two, "mago ", CharacterSpellcastingOriginKind.CLASS, false),
                source(three, "Sage — Magic Initiate (Wizard)", CharacterSpellcastingOriginKind.FEAT, false),
                source(four, "Otra dote", CharacterSpellcastingOriginKind.FEAT, true),
                source(five, "Sin tipo", null, false),
            ),
        )
        val names = PcSheetV11SpellSourceLabels.forSpells(listOf(first))
        // Two class sources with the same visible name keep a collision-free id prefix.
        assertEquals("Mago (#12345600)", names.getValue(one))
        assertEquals("mago (#12345601)", names.getValue(two))
        // Non-class sources show only their type, even when two share it (owner decision).
        assertEquals("Dote", names.getValue(three))
        assertEquals("Dote", names.getValue(four))
        // No stored type means no specific name is printed either.
        assertEquals("Otro", names.getValue(five))
        val result = PcSheetV11SpellSourceLabels.perSpell(first, names)
        assertTrue(result.contains("[X] Mago (#12345600)"))
        assertTrue(result.contains("[ ] mago (#12345601)"))
        assertTrue(result.contains("[ ] Dote / [X] Dote"))
        assertTrue(result.contains("[ ] Otro"))
        assertTrue(!result.contains("Magic Initiate") && !result.contains("Otra dote"))
    }
}