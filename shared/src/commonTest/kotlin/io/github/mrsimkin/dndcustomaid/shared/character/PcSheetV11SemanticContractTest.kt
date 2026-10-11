package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class PcSheetV11SemanticContractTest {
    @Test
    fun variantsAreExplicitAndDoNotAddAFifthLegacyVisualFamily() {
        assertEquals(listOf(PcSheetV11Variant.A, PcSheetV11Variant.B), PcSheetV11Variant.entries.toList())
        assertEquals(4, PcSheetVisualFamily.entries.size)
        val legacyBefore = PcSheetVisualFamily.entries.associateWith(PcSheetPdfExportPlanner::basePages)
        val a = map(PcSheetV11Variant.A)
        val b = map(PcSheetV11Variant.B)
        assertEquals(a.characterId, b.characterId)
        assertEquals(a.source, b.source)
        assertEquals(PcSheetV11Variant.A, a.request.variant)
        assertEquals(PcSheetV11Variant.B, b.request.variant)
        assertEquals(legacyBefore, PcSheetVisualFamily.entries.associateWith(PcSheetPdfExportPlanner::basePages))
        assertEquals(6, a.attributes.size)
        assertEquals(SkillKey.entries.size, a.skills.size)
    }

    @Test
    fun permanentCurrentAndMissingCurrentAreDistinguishedWithNotices() {
        val permanent = aggregate(baseSheet().copy(currentHp = 30))
        val current = aggregate(baseSheet().copy(currentHp = 7))
        val request = PcSheetV11ExportRequest(
            variant = PcSheetV11Variant.B,
            stateSelection = PcSheetExportStateSelection.CURRENT_SNAPSHOT,
        )
        val selected = PcSheetV11SemanticMapper.map(
            request,
            PcSheetExportSources(permanent, current),
        )
        assertEquals(PcSheetExportStateSelection.CURRENT_SNAPSHOT, selected.selectedState)
        assertEquals(7, selected.source.sheet.currentHp)
        assertTrue(selected.notices.isEmpty())

        val missing = PcSheetV11SemanticMapper.map(request, PcSheetExportSources(permanent))
        assertEquals(PcSheetExportStateSelection.PERMANENT, missing.selectedState)
        assertEquals(30, missing.source.sheet.currentHp)
        assertEquals(PcSheetExportNoticeCode.CURRENT_SNAPSHOT_UNAVAILABLE, missing.notices.single().code)
        assertEquals(30, permanent.sheet.currentHp)
    }

    @Test
    fun customAttributeAndSkillUseStableRelationshipsAndMissingTargetIsNotFabricated() {
        val attributeId = id("30000000-0000-0000-0000-000000000001")
        val missingId = id("30000000-0000-0000-0000-000000000099")
        val skillId = id("30000000-0000-0000-0000-000000000002")
        val missingSkillId = id("30000000-0000-0000-0000-000000000003")
        val custom = CharacterCustomAttribute(
            id = attributeId, name = "Cordura", abbreviation = "COR",
            score = 18, savingThrowEnabled = true, savingThrowProficient = true,
        )
        val skills = listOf(
            CharacterCustomSkill(
                id = skillId, name = "Ocultismo", ability = CharacterAbility.WISDOM,
                training = SkillTraining.PROFICIENT,
            ),
            CharacterCustomSkill(
                id = missingSkillId, name = "Pérdida", ability = CharacterAbility.WISDOM,
            ),
        )
        val successor = CharacterSuccessorState(
            customAttributes = listOf(custom),
            customSkillAbilities = listOf(
                CharacterCustomSkillAbilityConfiguration(skillId, CharacterAbilityReference.custom(attributeId)),
                CharacterCustomSkillAbilityConfiguration(missingSkillId, CharacterAbilityReference.custom(missingId)),
            ),
        )
        val snapshot = map(
            sources = PcSheetExportSources(
                aggregate(baseSheet(), CharacterClosureState(customSkills = skills), successor),
            ),
        )
        assertEquals(7, snapshot.attributes.size)
        val mapped = snapshot.attributes.last()
        assertEquals(attributeId, mapped.reference.customAttributeId)
        assertEquals(18, mapped.score)
        assertEquals(4, mapped.modifier)
        assertEquals(7, mapped.savingThrowTotal)
        val existingSkill = snapshot.skills.single { it.customSkillId == skillId }
        assertEquals(attributeId, existingSkill.ability.customAttributeId)
        assertEquals(7, existingSkill.total)
        val missing = snapshot.skills.single { it.customSkillId == missingSkillId }
        assertEquals(missingId, missing.ability.customAttributeId)
        assertNull(missing.total)
    }

    @Test
    fun spellsAreRealOnlyAndPreparedStateBelongsToEachSource() {
        val sourceA = CharacterSpellcastingSource(
            id = id("40000000-0000-0000-0000-000000000001"),
            name = "Mago", linkedClassId = null, sortOrder = 0,
        )
        val sourceB = CharacterSpellcastingSource(
            id = id("40000000-0000-0000-0000-000000000002"),
            name = "Don", linkedClassId = null, sortOrder = 1,
        )
        val spell = CharacterSpell(
            id = id("40000000-0000-0000-0000-000000000003"),
            name = "Escudo", level = 1, castingTime = "1 reacción", rangeText = "Personal",
            verbal = true, somatic = true, material = false, materialText = null,
            duration = "1 asalto", concentration = false, ritual = false,
            description = "Descripción íntegra.", notes = null, sortOrder = 0,
            sourceAssociations = listOf(
                CharacterSpellSourceAssociation(sourceB.id, prepared = false),
                CharacterSpellSourceAssociation(sourceA.id, prepared = true),
            ),
        )
        val sheet = baseSheet().copy(
            spells = listOf(spell),
            spellcastingSources = listOf(sourceA, sourceB),
            spellSlots = listOf(CharacterSpellSlot(level = 1, totalSlots = 3, spentSlots = 2)),
        )
        val withoutBook = map(sources = PcSheetExportSources(aggregate(sheet)))
        assertTrue(withoutBook.hasQuickSpellList)
        assertNull(withoutBook.spellbookEntries)
        assertEquals(2, withoutBook.spells.single().sources.size)
        assertEquals(listOf(sourceA.id, sourceB.id), withoutBook.spells.single().sources.map { it.sourceId })
        assertEquals(listOf(true, false), withoutBook.spells.single().sources.map { it.prepared })
        assertEquals(3, withoutBook.spellSlots.single().totalSlots)
        assertEquals(2, withoutBook.spellSlots.single().spentSlots)

        val withBook = map(
            request = PcSheetV11ExportRequest(PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true),
            sources = PcSheetExportSources(aggregate(sheet)),
        )
        assertEquals(spell.id, assertNotNull(withBook.spellbookEntries).single().spell.id)

        val capacityOnly = map(
            request = PcSheetV11ExportRequest(PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true),
            sources = PcSheetExportSources(
                aggregate(sheet.copy(spells = emptyList(), spellcasterEnabled = true)),
            ),
        )
        assertFalse(capacityOnly.hasQuickSpellList)
        assertTrue(capacityOnly.spells.isEmpty())
        assertNull(capacityOnly.spellbookEntries)
        assertEquals(PcSheetExportNoticeCode.SPELLBOOK_REQUESTED_WITHOUT_ATTACHED_SPELLS,
            capacityOnly.notices.single().code)
    }

    @Test
    fun inventoryZeroCurrencyDigitalNotesAndPortraitKeepTheirOriginalMeaning() {
        val ordinary = item("50000000-0000-0000-0000-000000000001", special = false)
        val special = item("50000000-0000-0000-0000-000000000002", special = true)
            .copy(equipped = true, attuned = true)
        val cardId = id("50000000-0000-0000-0000-000000000003")
        val sheet = baseSheet().copy(
            inventoryItems = listOf(special, ordinary),
            currencies = listOf(CharacterCurrency("pp", "Platino", 0, 0, false)),
            generalNotes = "Nota general\\nSegunda línea",
            noteCards = listOf(CharacterNote(cardId, "Secreto", "Contenido largo", 1)),
        )
        val closure = CharacterClosureState(
            portraitRef = "portrait://missing",
            inventoryUsage = listOf(CharacterInventoryUsage(itemId = special.id,
                carryState = CharacterInventoryCarryState.CARRIED)),
            temporaryEffects = listOf(CharacterTemporaryEffect(
                id = id("50000000-0000-0000-0000-000000000004"),
                name = "Otro estado", active = true,
            )),
        )
        val result = map(
            request = PcSheetV11ExportRequest(
                PcSheetV11Variant.B, PcSheetExportStateSelection.PERMANENT,
                portraitFitMode = PcSheetPortraitFitMode.FIT_ENTIRE_IMAGE,
            ),
            sources = PcSheetExportSources(aggregate(sheet, closure)),
        )
        assertEquals(ordinary.id, result.ordinaryEquipment.single().sourceId)
        assertEquals(special.id, result.specialEquipment.single().sourceId)
        assertNull(result.specialEquipment.single().active)
        assertEquals("Mochila", result.specialEquipment.single().location)
        assertEquals("Descripción detallada", result.specialEquipment.single().description)
        assertEquals(0, result.currencies.single().amount)
        assertEquals(2, result.digitalNotes.size)
        assertNull(result.digitalNotes.first().sourceId)
        assertEquals("Nota general\\nSegunda línea", result.digitalNotes.first().content)
        assertEquals(cardId, result.digitalNotes.last().sourceId)
        assertEquals("Secreto", result.digitalNotes.last().title)
        assertEquals(PcSheetPortraitFitMode.FIT_ENTIRE_IMAGE, result.portrait.fitMode)
        assertFalse(result.portrait.locallyAvailable)
        assertTrue(result.notices.any { it.code == PcSheetExportNoticeCode.PORTRAIT_NOT_AVAILABLE_LOCALLY })
    }

    private fun map(
        variant: PcSheetV11Variant = PcSheetV11Variant.A,
        request: PcSheetV11ExportRequest =
            PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
        sources: PcSheetExportSources = PcSheetExportSources(aggregate(baseSheet())),
    ): PcSheetV11SemanticData = PcSheetV11SemanticMapper.map(request, sources)

    private fun item(rawId: String, special: Boolean): CharacterInventoryItem = CharacterInventoryItem(
        id = id(rawId), name = "Anillo", quantity = 1, weightLb = null, equipped = false,
        notes = "No perder", sortOrder = 0, special = special,
        description = "Descripción detallada", location = "Mochila", attuned = false,
    )

    private fun aggregate(
        sheet: CharacterSheet,
        closure: CharacterClosureState = CharacterClosureState(),
        successor: CharacterSuccessorState = CharacterSuccessorState(),
    ) = PcSheetExportAggregate(sheet, closure, successor)

    private fun baseSheet(): CharacterSheet = CharacterSheet(
        id = id("00000000-0000-0000-0000-000000000001"),
        campaignId = id("00000000-0000-0000-0000-000000000002"),
        name = "Aster", status = CharacterStatus.ACTIVE,
        updatedAtEpochSeconds = 1_700_000_000L,
        strength = 10, dexterity = 14, constitution = 14, intelligence = 18,
        wisdom = 12, charisma = 8, armorClass = 15,
        maxHp = 30, currentHp = 30, tempHp = 0,
        initiativeAdjustment = 0, speed = 30, proficiencyBonus = 3,
        savingThrows = CharacterAbility.entries.map {
            CharacterSavingThrow(it, proficient = false, adjustment = 0)
        },
        passivePerceptionAdjustment = 0, spellSaveDc = null,
        classes = listOf(
            CharacterClassLevel(
                id = id("00000000-0000-0000-0000-000000000003"),
                name = "Wizard", level = 5, hitDieSides = 6,
                hitDiceRemaining = 5, sortOrder = 0,
            ),
        ),
        skills = SkillKey.entries.map {
            CharacterSkill(it, adjustment = 0, training = SkillTraining.NONE)
        },
    )

    private fun id(raw: String): Uuid = Uuid.parse(raw)
}
