package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterInventoryOperationsTest {
    private val swordId = Uuid.parse("00000000-0000-0000-0000-000000000101")
    private val arrowsId = Uuid.parse("00000000-0000-0000-0000-000000000102")
    private val chestId = Uuid.parse("00000000-0000-0000-0000-000000000103")

    private val items = listOf(
        CharacterInventoryItem(swordId, "Espada", 1, 3.0, true, null, 2, false, null, "Cinturón", false),
        CharacterInventoryItem(arrowsId, "Flechas", 20, 0.05, false, null, 0, false, null, "Carcaj", false),
        CharacterInventoryItem(chestId, "Cofre de viaje", 1, 10.0, false, null, 1, true, "Guardado en la posada", "Posada", false),
    )

    private val usageById = mapOf(
        arrowsId to CharacterInventoryUsage(
            arrowsId,
            CharacterConsumableKind.AMMUNITION,
            quickUseAmount = 2,
            carryState = CharacterInventoryCarryState.CARRIED,
        ),
        chestId to CharacterInventoryUsage(
            chestId,
            CharacterConsumableKind.NONE,
            carryState = CharacterInventoryCarryState.STORED,
        ),
    )

    private fun usage(item: CharacterInventoryItem): CharacterInventoryUsage =
        usageById[item.id] ?: CharacterInventoryUsage(item.id)

    @Test
    fun ordinaryAndSpecialOrderingAreIndependentPresentationOnly() {
        val manual = presentCharacterInventorySection(
            items,
            special = false,
            order = CharacterPresentationOrder.MANUAL,
            usageFor = ::usage,
        )
        val alphabetical = presentCharacterInventorySection(
            items,
            special = false,
            order = CharacterPresentationOrder.ALPHABETICAL,
            usageFor = ::usage,
        )
        val special = presentCharacterInventorySection(
            items,
            special = true,
            order = CharacterPresentationOrder.ALPHABETICAL,
            usageFor = ::usage,
        )

        assertEquals(listOf("Flechas", "Espada"), manual.map { it.name })
        assertEquals(listOf("Espada", "Flechas"), alphabetical.map { it.name })
        assertEquals(listOf("Cofre de viaje"), special.map { it.name })
        assertEquals(listOf(2, 0, 1), items.map { it.sortOrder })
    }

    @Test
    fun searchAndGroupedFiltersDoNotChangeStoredOrder() {
        val stored = presentCharacterInventorySection(
            items,
            special = true,
            order = CharacterPresentationOrder.MANUAL,
            query = CharacterCollectionQuery(activeFilterKeys = setOf(CharacterInventoryFilterKey.STORED.key)),
            usageFor = ::usage,
        )
        val ammo = presentCharacterInventorySection(
            items,
            special = false,
            order = CharacterPresentationOrder.MANUAL,
            query = CharacterCollectionQuery(
                searchText = "carcaj",
                activeFilterKeys = setOf(CharacterInventoryFilterKey.AMMUNITION.key),
            ),
            usageFor = ::usage,
        )

        assertEquals(listOf("Cofre de viaje"), stored.map { it.name })
        assertEquals(listOf("Flechas"), ammo.map { it.name })
        assertEquals(listOf(2, 0, 1), items.map { it.sortOrder })
    }

    @Test
    fun equippedItemIsEffectivelyCarriedAndStoredItemsDoNotAddCarriedWeight() {
        val contradictorySword = CharacterInventoryUsage(swordId, carryState = CharacterInventoryCarryState.STORED)
        val weight = carriedInventoryWeightLb(items) { item ->
            if (item.id == swordId) contradictorySword else usage(item)
        }

        assertEquals(CharacterInventoryCarryState.CARRIED, effectiveInventoryCarryState(items[0], contradictorySword))
        assertEquals(4.0, weight, absoluteTolerance = 0.000001)
    }

    @Test
    fun quickUseIsBoundedAndNonConsumablesRemainUnchanged() {
        val afterArrows = consumeInventoryItem(items, usageById.getValue(arrowsId))
        val depleted = consumeInventoryItem(
            afterArrows,
            CharacterInventoryUsage(arrowsId, CharacterConsumableKind.AMMUNITION, quickUseAmount = 50),
        )
        val unchanged = consumeInventoryItem(items, CharacterInventoryUsage(swordId))

        assertEquals(18, afterArrows.first { it.id == arrowsId }.quantity)
        assertEquals(0, depleted.first { it.id == arrowsId }.quantity)
        assertEquals(items, unchanged)
    }

    @Test
    fun defaultUsageIsSparseAndDuplicateKeepsMetadataForNewIdentity() {
        val defaultState = CharacterClosureState().withInventoryUsage(CharacterInventoryUsage(swordId))
        assertTrue(defaultState.inventoryUsage.isEmpty())

        val duplicateId = Uuid.parse("00000000-0000-0000-0000-000000000199")
        val duplicate = duplicateInventoryItem(items[1], duplicateId, sortOrder = 3)
        val duplicateUsage = duplicateInventoryUsage(usageById.getValue(arrowsId), duplicateId)

        assertEquals("Flechas (copia)", duplicate.name)
        assertEquals(3, duplicate.sortOrder)
        assertEquals(duplicateId, duplicateUsage.itemId)
        assertEquals(CharacterConsumableKind.AMMUNITION, duplicateUsage.kind)
        assertEquals(CharacterInventoryCarryState.CARRIED, duplicateUsage.carryState)
    }
}
