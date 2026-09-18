package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class PcSheetPdfExportPlannerTest {
    @Test
    fun customV2FamiliesUseAlternativeFirstPagesButShareRemainingOwnerTemplatePages() {
        val perAttribute = PcSheetPdfExportPlanner.basePages(PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE)
        val perAbility = PcSheetPdfExportPlanner.basePages(PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY)

        assertEquals(listOf(1, 3, 4, 5), perAttribute.map { it.sourcePageNumber })
        assertEquals(listOf(2, 3, 4, 5), perAbility.map { it.sourcePageNumber })
        assertTrue(perAttribute.all { it.sourceTemplatePath == PcSheetPdfExportPlanner.CUSTOM_V2_TEMPLATE_PATH })
        assertTrue(perAbility.all { it.sourceTemplatePath == PcSheetPdfExportPlanner.CUSTOM_V2_TEMPLATE_PATH })
    }

    @Test
    fun customStatisticsModesSelectFaithfulOrModifiedBaseAndMandatoryExtendedPage() {
        val aggregate = aggregateWithCustomStatistics()
        val sources = PcSheetExportSources(permanent = aggregate)

        val extended = PcSheetPdfExportPlanner.plan(
            request(PcSheetCustomStatisticsPresentation.EXTENDED_PAGE),
            sources,
        )
        assertEquals(PcSheetBaseLayoutMode.FAITHFUL, extended.baseLayoutMode)
        assertEquals(listOf(PcSheetExtendedPageKind.CUSTOM_STATISTICS), extended.mandatoryExtendedPages)

        val modified = PcSheetPdfExportPlanner.plan(
            request(PcSheetCustomStatisticsPresentation.APP_MODIFIED_SHEET),
            sources,
        )
        assertEquals(PcSheetBaseLayoutMode.APP_MODIFIED, modified.baseLayoutMode)
        assertTrue(modified.mandatoryExtendedPages.isEmpty())

        val both = PcSheetPdfExportPlanner.plan(
            request(PcSheetCustomStatisticsPresentation.MODIFIED_SHEET_AND_COMPLETE_EXTENDED_PAGE),
            sources,
        )
        assertEquals(PcSheetBaseLayoutMode.APP_MODIFIED, both.baseLayoutMode)
        assertEquals(listOf(PcSheetExtendedPageKind.CUSTOM_STATISTICS), both.mandatoryExtendedPages)
        assertEquals(15, both.snapshot.customStatistics.attributes.single().savingThrowTotal)
        assertEquals(13, both.snapshot.customStatistics.skills.single().total)
    }

    @Test
    fun currentSnapshotUsesCallerSuppliedCurrentAggregateAndFallsBackExplicitlyWhenMissing() {
        val permanent = aggregate(currentHp = 30, tempHp = 0)
        val current = aggregate(currentHp = 12, tempHp = 5)

        val selected = PcSheetPdfExportPlanner.plan(
            PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.CURRENT_SNAPSHOT,
            ),
            PcSheetExportSources(permanent = permanent, currentSnapshot = current),
        )
        assertEquals(12, selected.snapshot.aggregate.sheet.currentHp)
        assertEquals(5, selected.snapshot.aggregate.sheet.tempHp)
        assertTrue(selected.notices.isEmpty())

        val fallback = PcSheetPdfExportPlanner.plan(
            PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.CURRENT_SNAPSHOT,
            ),
            PcSheetExportSources(permanent = permanent),
        )
        assertEquals(30, fallback.snapshot.aggregate.sheet.currentHp)
        assertEquals(PcSheetExportStateSelection.PERMANENT, fallback.snapshot.selectedState)
        assertEquals(
            PcSheetExportNoticeCode.CURRENT_SNAPSHOT_UNAVAILABLE,
            fallback.notices.single().code,
        )
    }

    @Test
    fun missingLocalPortraitNeverBlocksPlanningAndProducesBlankAreaNotice() {
        val permanent = aggregate(
            closure = CharacterClosureState(portraitRef = "portrait://pc-1"),
        )
        val missing = PcSheetPdfExportPlanner.plan(
            PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
                portraitFitMode = PcSheetPortraitFitMode.FIT_ENTIRE_IMAGE,
            ),
            PcSheetExportSources(permanent = permanent),
        )

        assertEquals("portrait://pc-1", missing.snapshot.portrait.portraitRef)
        assertEquals(PcSheetPortraitFitMode.FIT_ENTIRE_IMAGE, missing.snapshot.portrait.fitMode)
        assertFalse(missing.snapshot.portrait.locallyAvailable)
        assertTrue(missing.notices.any { it.code == PcSheetExportNoticeCode.PORTRAIT_NOT_AVAILABLE_LOCALLY })

        val available = PcSheetPdfExportPlanner.plan(
            missing.request,
            PcSheetExportSources(
                permanent = permanent,
                locallyAvailablePortraitRefs = setOf("portrait://pc-1"),
            ),
        )
        assertTrue(available.snapshot.portrait.locallyAvailable)
        assertFalse(available.notices.any { it.code == PcSheetExportNoticeCode.PORTRAIT_NOT_AVAILABLE_LOCALLY })
    }

    @Test
    fun spellbookIncludesEveryAttachedSpellSortedByLevelAndNameWithAllSourceRelationships() {
        val sourceA = CharacterSpellcastingSource(
            id = uuid("10000000-0000-0000-0000-000000000001"),
            name = "Wizard",
            linkedClassId = null,
            sortOrder = 0,
            originKind = CharacterSpellcastingOriginKind.CLASS,
        )
        val sourceB = CharacterSpellcastingSource(
            id = uuid("10000000-0000-0000-0000-000000000002"),
            name = "Magic Initiate",
            linkedClassId = null,
            sortOrder = 1,
            originKind = CharacterSpellcastingOriginKind.FEAT,
        )
        val fireBolt = spell(
            id = "20000000-0000-0000-0000-000000000001",
            name = "Fire Bolt",
            level = 0,
            sources = listOf(CharacterSpellSourceAssociation(sourceA.id, prepared = true)),
        )
        val shield = spell(
            id = "20000000-0000-0000-0000-000000000002",
            name = "Shield",
            level = 1,
            sources = listOf(
                CharacterSpellSourceAssociation(sourceB.id, prepared = false),
                CharacterSpellSourceAssociation(sourceA.id, prepared = true),
            ),
        )
        val absorb = spell(
            id = "20000000-0000-0000-0000-000000000003",
            name = "Absorb Elements",
            level = 1,
            sources = emptyList(),
        )
        val sheet = baseSheet().copy(
            intelligence = 18,
            spellcastingSources = listOf(sourceA, sourceB),
            spells = listOf(shield, fireBolt, absorb),
        )
        val successor = CharacterSuccessorState(
            spellcastingProfiles = listOf(
                CharacterSpellcastingProfile(
                    sourceId = sourceA.id,
                    ability = CharacterAbilityReference.builtIn(CharacterAbility.INTELLIGENCE),
                ),
                CharacterSpellcastingProfile(
                    sourceId = sourceB.id,
                    ability = CharacterAbilityReference.builtIn(CharacterAbility.INTELLIGENCE),
                    saveDcAdjustment = 1,
                    spellAttackAdjustment = 2,
                ),
            ),
        )
        val plan = PcSheetPdfExportPlanner.plan(
            PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CUSTOM_V1,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true,
            ),
            PcSheetExportSources(
                permanent = PcSheetExportAggregate(sheet, CharacterClosureState(), successor),
            ),
        )

        val spellbook = assertNotNull(plan.snapshot.spellbook)
        assertTrue(spellbook.indexRequired)
        assertEquals(listOf("Fire Bolt", "Absorb Elements", "Shield"), spellbook.entries.map { it.spell.name })
        assertEquals(listOf(0, 1), spellbook.entriesByLevel.keys.toList())
        val shieldEntry = spellbook.entries.single { it.spell.name == "Shield" }
        assertEquals(listOf("Wizard", "Magic Initiate"), shieldEntry.sources.map { it.sourceName })
        assertEquals(listOf(true, false), shieldEntry.sources.map { it.prepared })
        assertEquals(15, shieldEntry.sources[0].saveDc)
        assertEquals(7, shieldEntry.sources[0].spellAttackModifier)
        assertEquals(16, shieldEntry.sources[1].saveDc)
        assertEquals(9, shieldEntry.sources[1].spellAttackModifier)
        assertTrue(spellbook.entries.single { it.spell.name == "Absorb Elements" }.sources.isEmpty())
    }

    @Test
    fun requestedSpellbookWithNoAttachedSpellsProducesNoticeInsteadOfEmptyAppendix() {
        val plan = PcSheetPdfExportPlanner.plan(
            PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true,
            ),
            PcSheetExportSources(permanent = aggregate()),
        )

        assertNull(plan.snapshot.spellbook)
        assertTrue(plan.notices.any {
            it.code == PcSheetExportNoticeCode.SPELLBOOK_REQUESTED_WITHOUT_ATTACHED_SPELLS
        })
    }

    @Test
    fun sourceAggregateIdentityMismatchIsRejected() {
        val permanent = aggregate()
        val different = aggregate().let {
            it.copy(sheet = it.sheet.copy(id = uuid("00000000-0000-0000-0000-000000000099")))
        }
        val result = runCatching {
            PcSheetExportSources(permanent = permanent, currentSnapshot = different)
        }
        assertTrue(result.isFailure)
    }

    private fun request(mode: PcSheetCustomStatisticsPresentation) = PcSheetPdfExportRequest(
        visualFamily = PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
        stateSelection = PcSheetExportStateSelection.PERMANENT,
        customStatisticsPresentation = mode,
    )

    private fun aggregateWithCustomStatistics(): PcSheetExportAggregate {
        val attributeId = uuid("30000000-0000-0000-0000-000000000001")
        val skillId = uuid("30000000-0000-0000-0000-000000000002")
        val attribute = CharacterCustomAttribute(
            id = attributeId,
            name = "Sanity",
            abbreviation = "SAN",
            score = 18,
            savingThrowEnabled = true,
            savingThrowProficient = true,
            savingThrowAdjustment = 8,
        )
        val skill = CharacterCustomSkill(
            id = skillId,
            name = "Occultism",
            ability = CharacterAbility.INTELLIGENCE,
            training = SkillTraining.PROFICIENT,
            adjustment = 6,
        )
        return aggregate(
            closure = CharacterClosureState(customSkills = listOf(skill)),
            successor = CharacterSuccessorState(
                customAttributes = listOf(attribute),
                customSkillAbilities = listOf(
                    CharacterCustomSkillAbilityConfiguration(
                        customSkillId = skillId,
                        ability = CharacterAbilityReference.custom(attributeId),
                    ),
                ),
            ),
        )
    }

    private fun aggregate(
        currentHp: Int = 30,
        tempHp: Int = 0,
        closure: CharacterClosureState = CharacterClosureState(),
        successor: CharacterSuccessorState = CharacterSuccessorState(),
    ): PcSheetExportAggregate = PcSheetExportAggregate(
        sheet = baseSheet().copy(currentHp = currentHp, tempHp = tempHp),
        closure = closure,
        successor = successor,
    )

    private fun baseSheet(): CharacterSheet = CharacterSheet(
        id = uuid("00000000-0000-0000-0000-000000000001"),
        campaignId = uuid("00000000-0000-0000-0000-000000000002"),
        name = "Aster Vale",
        status = CharacterStatus.ACTIVE,
        updatedAtEpochSeconds = 1_700_000_000L,
        strength = 10,
        dexterity = 14,
        constitution = 14,
        intelligence = 18,
        wisdom = 12,
        charisma = 8,
        armorClass = 15,
        maxHp = 30,
        currentHp = 30,
        tempHp = 0,
        initiativeAdjustment = 0,
        speed = 30,
        proficiencyBonus = 3,
        savingThrows = CharacterAbility.entries.map {
            CharacterSavingThrow(ability = it, proficient = false, adjustment = 0)
        },
        passivePerceptionAdjustment = 0,
        spellSaveDc = null,
        classes = listOf(
            CharacterClassLevel(
                id = uuid("00000000-0000-0000-0000-000000000003"),
                name = "Wizard",
                level = 5,
                hitDieSides = 6,
                hitDiceRemaining = 5,
                sortOrder = 0,
            ),
        ),
        skills = SkillKey.entries.map {
            CharacterSkill(key = it, adjustment = 0, training = SkillTraining.NONE)
        },
        proficiencyBonusAdjustment = 0,
    )

    private fun spell(
        id: String,
        name: String,
        level: Int,
        sources: List<CharacterSpellSourceAssociation>,
    ): CharacterSpell = CharacterSpell(
        id = uuid(id),
        name = name,
        level = level,
        castingTime = "1 action",
        rangeText = "60 ft",
        verbal = true,
        somatic = true,
        material = false,
        materialText = null,
        duration = "Instantaneous",
        concentration = false,
        ritual = false,
        description = "Dummy spell description.",
        notes = null,
        sortOrder = 0,
        sourceAssociations = sources,
    )

    private fun uuid(raw: String): Uuid = Uuid.parse(raw)
}
