package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterSpellOperationsTest {
    private val wizard = Uuid.parse("00000000-0000-0000-0000-000000000301")
    private val cleric = Uuid.parse("00000000-0000-0000-0000-000000000302")
    private val magicMissileId = Uuid.parse("00000000-0000-0000-0000-000000000311")
    private val shieldId = Uuid.parse("00000000-0000-0000-0000-000000000312")
    private val blessId = Uuid.parse("00000000-0000-0000-0000-000000000313")
    private val detectMagicId = Uuid.parse("00000000-0000-0000-0000-000000000314")
    private val cantripId = Uuid.parse("00000000-0000-0000-0000-000000000315")

    private val spells = listOf(
        spell(
            id = magicMissileId,
            name = "Proyectil mágico",
            level = 1,
            sortOrder = 2,
            associations = listOf(CharacterSpellSourceAssociation(wizard, prepared = true)),
            verbal = true,
            somatic = true,
        ),
        spell(
            id = shieldId,
            name = "Escudo",
            level = 1,
            sortOrder = 0,
            associations = listOf(CharacterSpellSourceAssociation(wizard, prepared = false)),
            somatic = true,
        ),
        spell(
            id = blessId,
            name = "Bendición",
            level = 1,
            sortOrder = 1,
            associations = listOf(CharacterSpellSourceAssociation(cleric, prepared = true)),
            concentration = true,
            material = true,
        ),
        spell(
            id = detectMagicId,
            name = "Detectar magia",
            level = 1,
            sortOrder = 3,
            associations = listOf(
                CharacterSpellSourceAssociation(wizard, prepared = false),
                CharacterSpellSourceAssociation(cleric, prepared = true),
            ),
            ritual = true,
            verbal = true,
            somatic = true,
        ),
        spell(
            id = cantripId,
            name = "Luz",
            level = 0,
            sortOrder = 0,
            associations = listOf(CharacterSpellSourceAssociation(cleric, prepared = false)),
        ),
    )

    @Test
    fun manualAndAlphabeticalProjectionStayInsideLevelAndNeverRewriteStoredOrder() {
        val manual = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            order = CharacterPresentationOrder.MANUAL,
        )
        val alphabetical = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            order = CharacterPresentationOrder.ALPHABETICAL,
        )

        assertEquals(listOf(shieldId, blessId, magicMissileId, detectMagicId), manual.map { it.id })
        assertEquals(listOf(blessId, detectMagicId, shieldId, magicMissileId), alphabetical.map { it.id })
        assertEquals(listOf(2, 0, 1, 3, 0), spells.map { it.sortOrder })
    }

    @Test
    fun sourceViewIsFilteredProjectionOfOneConceptualCollection() {
        val wizardView = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            selectedSourceId = wizard,
        )
        val clericView = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            selectedSourceId = cleric,
        )

        assertEquals(listOf(shieldId, magicMissileId, detectMagicId), wizardView.map { it.id })
        assertEquals(listOf(blessId, detectMagicId), clericView.map { it.id })
        assertEquals(1, spells.count { it.id == detectMagicId })
    }

    @Test
    fun preparedFilterIsSourceSpecificButTodosMeansPreparedThroughAnySource() {
        val allPrepared = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            query = CharacterCollectionQuery(activeFilterKeys = setOf(CHARACTER_SPELL_PREPARED_FILTER_KEY)),
        )
        val wizardPrepared = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            selectedSourceId = wizard,
            query = CharacterCollectionQuery(activeFilterKeys = setOf(CHARACTER_SPELL_PREPARED_FILTER_KEY)),
        )
        val clericPrepared = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            selectedSourceId = cleric,
            query = CharacterCollectionQuery(activeFilterKeys = setOf(CHARACTER_SPELL_PREPARED_FILTER_KEY)),
        )

        assertEquals(setOf(blessId, magicMissileId, detectMagicId), allPrepared.map { it.id }.toSet())
        assertEquals(listOf(magicMissileId), wizardPrepared.map { it.id })
        assertEquals(listOf(blessId, detectMagicId), clericPrepared.map { it.id })
        assertTrue(spellPreparedForView(spells.first { it.id == detectMagicId }, null))
        assertFalse(spellPreparedForView(spells.first { it.id == detectMagicId }, wizard))
        assertTrue(spellPreparedForView(spells.first { it.id == detectMagicId }, cleric))
    }

    @Test
    fun filtersAreAndCombinedAndFavoriteUsesExternalQuickAccessState() {
        val visible = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            query = CharacterCollectionQuery(
                activeFilterKeys = setOf(
                    CHARACTER_SPELL_FAVORITE_FILTER_KEY,
                    CHARACTER_SPELL_CONCENTRATION_FILTER_KEY,
                    CHARACTER_SPELL_MATERIAL_FILTER_KEY,
                ),
            ),
            isFavorite = { it.id == blessId },
        )
        val noMatch = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            query = CharacterCollectionQuery(
                activeFilterKeys = setOf(
                    CHARACTER_SPELL_RITUAL_FILTER_KEY,
                    CHARACTER_SPELL_CONCENTRATION_FILTER_KEY,
                ),
            ),
        )

        assertEquals(listOf(blessId), visible.map { it.id })
        assertTrue(noMatch.isEmpty())
    }

    @Test
    fun searchIsAccentInsensitiveAndIncludesSourceNames() {
        val byName = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            query = CharacterCollectionQuery(searchText = "magico"),
        )
        val bySource = presentCharacterSpellLevel(
            spells = spells,
            level = 1,
            query = CharacterCollectionQuery(searchText = "clerigo"),
            sourceName = { id -> when (id) { wizard -> "Mago"; cleric -> "Clérigo"; else -> null } },
        )

        assertEquals(listOf(magicMissileId), byName.map { it.id })
        assertEquals(setOf(blessId, detectMagicId), bySource.map { it.id }.toSet())
    }

    @Test
    fun sourceFilteredManualMoveUsesOnlyVisibleSlotsAndPreservesHiddenSpellPosition() {
        val moved = moveCharacterSpellManual(
            spells = spells,
            spellId = detectMagicId,
            offset = -1,
            selectedSourceId = wizard,
        )
        val levelOrder = moved.filter { it.level == 1 }.sortedBy { it.sortOrder }.map { it.id }

        // Original level order: Shield (wizard), Bless (cleric-hidden), Missile (wizard), Detect (wizard).
        // Moving Detect one visible step upward swaps the visible wizard occupants of positions 2 and 3 only.
        assertEquals(listOf(shieldId, blessId, detectMagicId, magicMissileId), levelOrder)
        assertEquals(1, moved.first { it.id == blessId }.sortOrder)
        assertEquals(0, moved.first { it.id == cantripId }.sortOrder)
    }

    @Test
    fun movingInTodosUsesWholeLevelAndNeverMovesAcrossLevels() {
        val moved = moveCharacterSpellManual(
            spells = spells,
            spellId = magicMissileId,
            offset = -1,
            selectedSourceId = null,
        )

        assertEquals(
            listOf(shieldId, magicMissileId, blessId, detectMagicId),
            moved.filter { it.level == 1 }.sortedBy { it.sortOrder }.map { it.id },
        )
        assertEquals(0, moved.first { it.id == cantripId }.sortOrder)
    }

    @Test
    fun duplicatePreservesAssociationsAndSpellMetadataWithFreshIdentity() {
        val duplicateId = Uuid.parse("00000000-0000-0000-0000-000000000399")
        val source = spells.first { it.id == detectMagicId }
        val duplicate = duplicateCharacterSpell(source, duplicateId, sortOrder = 4)

        assertEquals(duplicateId, duplicate.id)
        assertEquals("Detectar magia (copia)", duplicate.name)
        assertEquals(source.sourceAssociations, duplicate.sourceAssociations)
        assertEquals(source.ritual, duplicate.ritual)
        assertEquals(source.verbal, duplicate.verbal)
        assertEquals(4, duplicate.sortOrder)
    }

    @Test
    fun nextSortOrderIsLevelScopedAndSupportsMovingSpellBetweenLevels() {
        assertEquals(4, nextCharacterSpellSortOrder(spells, level = 1))
        assertEquals(0, nextCharacterSpellSortOrder(spells, level = 2))
        assertEquals(3, nextCharacterSpellSortOrder(spells, level = 1, excludingId = detectMagicId))
    }

    private fun spell(
        id: Uuid,
        name: String,
        level: Int,
        sortOrder: Int,
        associations: List<CharacterSpellSourceAssociation>,
        verbal: Boolean = false,
        somatic: Boolean = false,
        material: Boolean = false,
        concentration: Boolean = false,
        ritual: Boolean = false,
    ): CharacterSpell = CharacterSpell(
        id = id,
        name = name,
        level = level,
        castingTime = "1 acción",
        rangeText = "18 m",
        verbal = verbal,
        somatic = somatic,
        material = material,
        materialText = if (material) "un símbolo" else null,
        duration = if (concentration) "Concentración, 1 minuto" else "Instantáneo",
        concentration = concentration,
        ritual = ritual,
        description = "Descripción de $name",
        notes = null,
        sortOrder = sortOrder,
        sourceAssociations = associations,
    )
}
