package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterTraitOperationsTest {
    private val classId = Uuid.parse("00000000-0000-0000-0000-000000000201")
    private val featId = Uuid.parse("00000000-0000-0000-0000-000000000202")
    private val secondClassId = Uuid.parse("00000000-0000-0000-0000-000000000203")
    private val customId = Uuid.parse("00000000-0000-0000-0000-000000000204")

    private val traits = listOf(
        trait(
            id = classId,
            name = "Defensa Arcana",
            source = "Mago",
            type = CharacterTraitType.CLASS,
            sortOrder = 2,
            maxUses = 4,
            spentUses = 1,
        ),
        trait(
            id = featId,
            name = "Alerta",
            source = "Manual del Jugador",
            type = CharacterTraitType.FEAT,
            sortOrder = 0,
        ),
        trait(
            id = secondClassId,
            name = "Recuperación Arcana",
            source = "Mago",
            type = CharacterTraitType.CLASS,
            sortOrder = 3,
        ),
        trait(
            id = customId,
            name = "Eco de la noche",
            source = "",
            type = CharacterTraitType.OTHER,
            sortOrder = 1,
            notes = "Creación propia",
        ),
    )

    @Test
    fun searchAndTypeSourceFavoriteFiltersPreserveStoredManualOrder() {
        val query = CharacterCollectionQuery(
            searchText = "arcana",
            activeFilterKeys = setOf(
                characterTraitTypeFilterKey(CharacterTraitType.CLASS),
                characterTraitSourceFilterKey("MAGO"),
                CHARACTER_TRAIT_FAVORITE_FILTER_KEY,
            ),
        )
        val visible = presentCharacterTraits(
            traits,
            query = query,
            isFavorite = { it.id == secondClassId },
        )

        assertEquals(listOf(secondClassId), visible.map { it.id })
        assertEquals(listOf(2, 0, 3, 1), traits.map { it.sortOrder })
    }

    @Test
    fun sourceSearchIsAccentInsensitiveAndBlankSourcesCanBeFiltered() {
        val customSearch = presentCharacterTraits(
            traits,
            query = CharacterCollectionQuery(searchText = "creacion propia"),
        )
        val noSource = presentCharacterTraits(
            traits,
            query = CharacterCollectionQuery(activeFilterKeys = setOf(characterTraitSourceFilterKey(""))),
        )

        assertEquals(listOf(customId), customSearch.map { it.id })
        assertEquals(listOf(customId), noSource.map { it.id })
    }

    @Test
    fun groupingIsPresentationOnlyAndKeepsManualOrderInsideGroups() {
        val visible = presentCharacterTraits(traits)
        val byType = groupCharacterTraits(visible, CharacterTraitGrouping.TYPE)
        val bySource = groupCharacterTraits(visible, CharacterTraitGrouping.SOURCE)

        val classGroup = byType.single { it.key == characterTraitTypeFilterKey(CharacterTraitType.CLASS) }
        assertEquals(listOf(classId, secondClassId), classGroup.traits.map { it.id })

        val mageGroup = bySource.single { it.key == characterTraitSourceFilterKey("Mago") }
        assertEquals(listOf(classId, secondClassId), mageGroup.traits.map { it.id })
        assertEquals(listOf(2, 0, 3, 1), traits.map { it.sortOrder })
    }

    @Test
    fun movingInsideGroupedProjectionChangesOnlyThatGroupsOccupiedSlots() {
        val moved = moveCharacterTraitManual(
            traits = traits,
            traitId = secondClassId,
            offset = -1,
            grouping = CharacterTraitGrouping.TYPE,
        )

        assertEquals(
            listOf(featId, customId, secondClassId, classId),
            moved.sortedBy { it.sortOrder }.map { it.id },
        )
        assertEquals(listOf(0, 1, 2, 3), moved.sortedBy { it.sortOrder }.map { it.sortOrder })
    }

    @Test
    fun movingUngroupedUsesWholeManualSequenceAndOutOfBoundsIsStable() {
        val moved = moveCharacterTraitManual(
            traits = traits,
            traitId = customId,
            offset = -1,
            grouping = CharacterTraitGrouping.NONE,
        )
        val unchangedAtBoundary = moveCharacterTraitManual(
            traits = moved,
            traitId = customId,
            offset = -1,
            grouping = CharacterTraitGrouping.NONE,
        )

        assertEquals(listOf(customId, featId, classId, secondClassId), moved.sortedBy { it.sortOrder }.map { it.id })
        assertEquals(moved, unchangedAtBoundary)
    }

    @Test
    fun duplicateGetsNewIdentityAndKeepsTraitState() {
        val duplicateId = Uuid.parse("00000000-0000-0000-0000-000000000299")
        val duplicate = duplicateCharacterTrait(
            source = traits.first { it.id == classId },
            newId = duplicateId,
            sortOrder = 4,
        )

        assertEquals(duplicateId, duplicate.id)
        assertEquals("Defensa Arcana (copia)", duplicate.name)
        assertEquals(CharacterTraitType.CLASS, duplicate.type)
        assertEquals("Mago", duplicate.source)
        assertEquals(4, duplicate.maxUses)
        assertEquals(1, duplicate.spentUses)
        assertEquals(4, duplicate.sortOrder)
    }

    @Test
    fun usageMeterClampsMalformedSpentStateAndHandlesUnlimitedTraits() {
        val meter = characterTraitUsageMeter(
            traits.first { it.id == classId }.copy(spentUses = 99),
        )
        val unlimited = characterTraitUsageMeter(traits.first { it.id == featId })

        requireNotNull(meter)
        assertEquals(0, meter.remaining)
        assertEquals(4, meter.max)
        assertEquals(4, meter.spent)
        assertEquals(0f, meter.remainingFraction)
        assertNull(unlimited)
    }

    @Test
    fun filterCategoriesAreOrWithinCategoryAndAndAcrossCategories() {
        val twoTypes = presentCharacterTraits(
            traits,
            query = CharacterCollectionQuery(
                activeFilterKeys = setOf(
                    characterTraitTypeFilterKey(CharacterTraitType.CLASS),
                    characterTraitTypeFilterKey(CharacterTraitType.FEAT),
                ),
            ),
        )
        val classFromWrongSource = presentCharacterTraits(
            traits,
            query = CharacterCollectionQuery(
                activeFilterKeys = setOf(
                    characterTraitTypeFilterKey(CharacterTraitType.CLASS),
                    characterTraitSourceFilterKey("Manual del Jugador"),
                ),
            ),
        )

        assertEquals(setOf(featId, classId, secondClassId), twoTypes.map { it.id }.toSet())
        assertTrue(classFromWrongSource.isEmpty())
    }

    private fun trait(
        id: Uuid,
        name: String,
        source: String,
        type: CharacterTraitType,
        sortOrder: Int,
        maxUses: Int? = null,
        spentUses: Int = 0,
        notes: String? = null,
    ): CharacterTrait = CharacterTrait(
        id = id,
        name = name,
        source = source,
        type = type,
        description = "Descripción de $name",
        notes = notes,
        maxUses = maxUses,
        spentUses = spentUses,
        recovery = maxUses?.let { "Descanso largo" },
        activation = CharacterActivationType.ACTION,
        sortOrder = sortOrder,
    )
}
