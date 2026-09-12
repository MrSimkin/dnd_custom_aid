package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterTableModePolicyTest {
    private fun sheet(): CharacterSheet {
        val itemId = Uuid.random()
        val traitId = Uuid.random()
        val resourceId = Uuid.random()
        return CharacterSheet(
            id = Uuid.random(),
            campaignId = Uuid.random(),
            name = "Persisted",
            status = CharacterStatus.ACTIVE,
            updatedAtEpochSeconds = 1L,
            strength = 10,
            dexterity = 12,
            constitution = 14,
            intelligence = 16,
            wisdom = 8,
            charisma = 11,
            armorClass = 15,
            maxHp = 30,
            currentHp = 25,
            tempHp = 0,
            initiativeAdjustment = 1,
            speed = 30,
            proficiencyBonus = 2,
            savingThrows = emptyList(),
            passivePerceptionAdjustment = 0,
            spellSaveDc = 13,
            classes = emptyList(),
            skills = emptyList(),
            spellSlots = listOf(CharacterSpellSlot(level = 1, totalSlots = 4, spentSlots = 1)),
            inventoryItems = listOf(
                CharacterInventoryItem(
                    id = itemId,
                    name = "Potion",
                    quantity = 3,
                    weightLb = 0.5,
                    equipped = false,
                    notes = null,
                    sortOrder = 0,
                    special = false,
                    description = null,
                    location = null,
                    attuned = false,
                ),
            ),
            traits = listOf(
                CharacterTrait(
                    id = traitId,
                    name = "Second Wind",
                    source = "Class",
                    type = CharacterTraitType.CLASS,
                    description = "Persisted description",
                    notes = null,
                    maxUses = 2,
                    spentUses = 0,
                    recovery = "Short rest",
                    activation = CharacterActivationType.BONUS_ACTION,
                    sortOrder = 0,
                ),
            ),
            resources = listOf(
                CharacterResource(
                    id = resourceId,
                    name = "Ki",
                    currentValue = 2,
                    maxValue = 5,
                ),
            ),
        )
    }

    @Test
    fun operationalSheetMergeAppliesLiveValuesAndRejectsStructure() {
        val persisted = sheet()
        val proposed = persisted.copy(
            name = "Structural overwrite",
            status = CharacterStatus.DEAD,
            armorClass = 99,
            maxHp = 999,
            currentHp = 7,
            tempHp = 4,
            inspiration = true,
            deathSaveSuccesses = 2,
            deathSaveFailures = 1,
            spellSlots = persisted.spellSlots.map { it.copy(totalSlots = 99, spentSlots = 3) },
            inventoryItems = persisted.inventoryItems.map { it.copy(name = "Renamed", quantity = 1) },
            traits = persisted.traits.map { it.copy(description = "Overwritten", spentUses = 1) },
            resources = persisted.resources.map { it.copy(name = "Renamed resource", currentValue = 4, maxValue = 99) },
        )

        val merged = mergeCharacterOperationalState(persisted, proposed)

        assertEquals("Persisted", merged.name)
        assertEquals(CharacterStatus.ACTIVE, merged.status)
        assertEquals(15, merged.armorClass)
        assertEquals(30, merged.maxHp)
        assertEquals(7, merged.currentHp)
        assertEquals(4, merged.tempHp)
        assertTrue(merged.inspiration)
        assertEquals(2, merged.deathSaveSuccesses)
        assertEquals(1, merged.deathSaveFailures)
        assertEquals(4, merged.spellSlots.single().totalSlots)
        assertEquals(3, merged.spellSlots.single().spentSlots)
        assertEquals("Potion", merged.inventoryItems.single().name)
        assertEquals(1, merged.inventoryItems.single().quantity)
        assertEquals("Persisted description", merged.traits.single().description)
        assertEquals(1, merged.traits.single().spentUses)
        assertEquals("Ki", merged.resources.single().name)
        assertEquals(5, merged.resources.single().maxValue)
        assertEquals(4, merged.resources.single().currentValue)
    }

    @Test
    fun closureMergeKeepsSessionStateButRejectsStructuralConfiguration() {
        val condition = CharacterCondition(Uuid.random(), "Prone")
        val persisted = CharacterClosureState(
            portraitRef = "portrait-a",
            tableModeEnabled = true,
            hapticsEnabled = true,
            moduleOverrides = listOf(CharacterModuleOverride(CharacterModuleKind.ARTIFICER, CharacterModuleOverrideMode.AUTO)),
        )
        val proposed = persisted.copy(
            portraitRef = "portrait-b",
            tableModeEnabled = false,
            hapticsEnabled = false,
            exhaustionLevel = 2,
            conditions = listOf(condition),
            moduleOverrides = listOf(CharacterModuleOverride(CharacterModuleKind.ARTIFICER, CharacterModuleOverrideMode.FORCE_SHOW)),
        )

        val merged = mergeCharacterOperationalClosureState(persisted, proposed)

        assertEquals("portrait-a", merged.portraitRef)
        assertFalse(merged.tableModeEnabled)
        assertFalse(merged.hapticsEnabled)
        assertEquals(2, merged.exhaustionLevel)
        assertEquals(listOf(condition), merged.conditions)
        assertEquals(persisted.moduleOverrides, merged.moduleOverrides)
    }

    @Test
    fun successorMergeAllowsMarkerValueAndPresentationOnly() {
        val marker = CharacterCustomMarker(
            id = Uuid.random(),
            name = "Momentum",
            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
            currentValue = 1,
            maxValue = 5,
        )
        val persisted = CharacterSuccessorState(
            customMarkers = listOf(marker),
            preferences = CharacterSuccessorPreferences(
                valuablesText = "Keep me",
                tabOrder = listOf(CharacterSheetTabKey.OVERVIEW, CharacterSheetTabKey.COMBAT),
                inspirationVisible = true,
            ),
        )
        val proposed = persisted.copy(
            customMarkers = listOf(marker.copy(name = "Renamed", currentValue = 4, maxValue = 99)),
            preferences = CharacterSuccessorPreferences(
                valuablesText = "Overwrite",
                tabOrder = listOf(CharacterSheetTabKey.NOTES),
                inspirationVisible = false,
            ),
        )

        val merged = mergeCharacterOperationalSuccessorState(persisted, proposed)

        assertEquals("Momentum", merged.customMarkers.single().name)
        assertEquals(5, merged.customMarkers.single().maxValue)
        assertEquals(4, merged.customMarkers.single().currentValue)
        assertEquals("Keep me", merged.preferences.valuablesText)
        assertEquals(listOf(CharacterSheetTabKey.OVERVIEW, CharacterSheetTabKey.COMBAT), merged.preferences.tabOrder)
        assertFalse(merged.preferences.inspirationVisible)
    }
}
