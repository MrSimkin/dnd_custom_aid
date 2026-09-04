package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterConditionalModuleOperationsTest {
    private val artificerClassId = Uuid.random()

    private fun option(
        name: String,
        kind: CharacterClassOptionKind,
        order: Int,
        active: Boolean = true,
        source: String? = "Artífice",
    ) = CharacterClassOption(
        id = Uuid.random(),
        linkedClassId = artificerClassId,
        kind = kind,
        name = name,
        source = source,
        costText = "1 uso",
        effectSummary = "Resumen de $name",
        notes = "Nota $name",
        active = active,
        sortOrder = order,
    )

    private fun form(
        name: String,
        order: Int,
        source: String? = "Druida",
    ) = CharacterForm(
        id = Uuid.random(),
        name = name,
        source = source,
        challengeRatingText = "1/4",
        armorClass = 12,
        hitPoints = 11,
        movement = "40 ft",
        senses = "Percepción",
        actionSummary = "Acción de $name",
        notes = "Nota $name",
        sortOrder = order,
    )

    @Test
    fun artificeProjectionExcludesOtherClassOptionFamiliesAndSearchesReferenceText() {
        val plan = option("Bolsa portátil", CharacterClassOptionKind.ARTIFICER_PLAN, 0)
        val device = option("Torreta", CharacterClassOptionKind.ARTIFICER_DEVICE, 1)
        val technique = option("Maniobra ajena", CharacterClassOptionKind.TECHNIQUE, 2)

        val all = presentCharacterArtificeOptions(listOf(plan, device, technique))
        val searched = presentCharacterArtificeOptions(
            listOf(plan, device, technique),
            query = CharacterCollectionQuery(searchText = "torreta"),
        )

        assertEquals(listOf(plan.id, device.id), all.map { it.id })
        assertEquals(listOf(device.id), searched.map { it.id })
        assertFalse(technique.id in all.map { it.id })
    }

    @Test
    fun artificeFiltersCombineKindFavoriteAndActiveWithoutChangingManualOrder() {
        val favoritePlan = option("Plan favorito", CharacterClassOptionKind.ARTIFICER_PLAN, 4)
        val inactivePlan = option("Plan inactivo", CharacterClassOptionKind.ARTIFICER_PLAN, 2, active = false)
        val device = option("Dispositivo", CharacterClassOptionKind.ARTIFICER_DEVICE, 1)
        val options = listOf(favoritePlan, inactivePlan, device)

        val filtered = presentCharacterArtificeOptions(
            options = options,
            query = CharacterCollectionQuery(
                activeFilterKeys = setOf(
                    CHARACTER_ARTIFICE_PLAN_FILTER_KEY,
                    CHARACTER_ARTIFICE_FAVORITE_FILTER_KEY,
                    CHARACTER_ARTIFICE_ACTIVE_FILTER_KEY,
                ),
            ),
            isFavorite = { it.id == favoritePlan.id },
        )

        assertEquals(listOf(favoritePlan.id), filtered.map { it.id })
        assertEquals(listOf(4, 2, 1), options.map { it.sortOrder })
    }

    @Test
    fun alphabeticalArtificePresentationNeverRewritesManualOrder() {
        val zeta = option("Zeta", CharacterClassOptionKind.ARTIFICER_PLAN, 0)
        val alfa = option("Álfa", CharacterClassOptionKind.ARTIFICER_DEVICE, 1)
        val options = listOf(zeta, alfa)

        val alphabetical = presentCharacterArtificeOptions(
            options = options,
            order = CharacterPresentationOrder.ALPHABETICAL,
        )

        assertEquals(listOf(alfa.id, zeta.id), alphabetical.map { it.id })
        assertEquals(listOf(0, 1), options.map { it.sortOrder })
    }

    @Test
    fun movingArtificeOptionSwapsOnlyVisibleArtificePositionsAndPreservesHiddenFamilySlot() {
        val firstPlan = option("Plan A", CharacterClassOptionKind.ARTIFICER_PLAN, 0)
        val hiddenTechnique = option("Técnica", CharacterClassOptionKind.TECHNIQUE, 1)
        val device = option("Dispositivo", CharacterClassOptionKind.ARTIFICER_DEVICE, 2)
        val secondPlan = option("Plan B", CharacterClassOptionKind.ARTIFICER_PLAN, 3)

        val moved = moveCharacterArtificeOptionManual(
            listOf(firstPlan, hiddenTechnique, device, secondPlan),
            optionId = firstPlan.id,
            offset = 1,
        )

        assertEquals(
            listOf(device.id, hiddenTechnique.id, firstPlan.id, secondPlan.id),
            moved.sortedBy { it.sortOrder }.map { it.id },
        )
        assertEquals(1, moved.single { it.id == hiddenTechnique.id }.sortOrder)
    }

    @Test
    fun duplicatedArtificeOptionGetsFreshIdentityAndKeepsReferenceState() {
        val original = option("Plano", CharacterClassOptionKind.ARTIFICER_PLAN, 3)
            .copy(costText = "50 po", active = false, pinned = true)
        val newId = Uuid.random()

        val copy = duplicateCharacterClassOption(original, newId, 8)

        assertEquals(newId, copy.id)
        assertEquals("Plano (copia)", copy.name)
        assertEquals(original.linkedClassId, copy.linkedClassId)
        assertEquals(original.kind, copy.kind)
        assertEquals(original.source, copy.source)
        assertEquals("50 po", copy.costText)
        assertEquals(original.effectSummary, copy.effectSummary)
        assertEquals(original.notes, copy.notes)
        assertFalse(copy.active)
        assertTrue(copy.pinned)
        assertEquals(8, copy.sortOrder)
    }

    @Test
    fun formsSupportSearchSourceFavoriteAndAlphabeticalPresentationWithoutMutatingManualOrder() {
        val wolf = form("Lobo", 0, source = "Manual")
        val bear = form("Oso", 1, source = "Druida")
        val eagle = form("Águila", 2, source = "Druida")
        val forms = listOf(wolf, bear, eagle)

        val filtered = presentCharacterForms(
            forms = forms,
            order = CharacterPresentationOrder.ALPHABETICAL,
            query = CharacterCollectionQuery(
                searchText = "aguila",
                activeFilterKeys = setOf(
                    characterFormSourceFilterKey("Druida"),
                    CHARACTER_FORM_FAVORITE_FILTER_KEY,
                ),
            ),
            isFavorite = { it.id == eagle.id },
        )

        assertEquals(listOf(eagle.id), filtered.map { it.id })
        assertEquals(listOf(0, 1, 2), forms.map { it.sortOrder })
    }

    @Test
    fun movingAndDuplicatingFormsKeepsDenseManualOrderAndReferenceFields() {
        val wolf = form("Lobo", 7)
        val bear = form("Oso", 2)
        val eagle = form("Águila", 5)

        val moved = moveCharacterFormManual(listOf(wolf, bear, eagle), wolf.id, offset = -1)
        val ordered = moved.sortedBy { it.sortOrder }

        assertEquals(listOf(bear.id, wolf.id, eagle.id), ordered.map { it.id })
        assertEquals(listOf(0, 1, 2), ordered.map { it.sortOrder })

        val duplicateId = Uuid.random()
        val copy = duplicateCharacterForm(wolf, duplicateId, nextCharacterFormSortOrder(moved))
        assertEquals(duplicateId, copy.id)
        assertEquals("Lobo (copia)", copy.name)
        assertEquals(wolf.challengeRatingText, copy.challengeRatingText)
        assertEquals(wolf.armorClass, copy.armorClass)
        assertEquals(wolf.hitPoints, copy.hitPoints)
        assertEquals(wolf.movement, copy.movement)
        assertEquals(wolf.senses, copy.senses)
        assertEquals(wolf.actionSummary, copy.actionSummary)
        assertEquals(wolf.notes, copy.notes)
        assertEquals(3, copy.sortOrder)
    }

    @Test
    fun nextClassOptionOrderAccountsForHiddenH2Families() {
        val plan = option("Plan", CharacterClassOptionKind.ARTIFICER_PLAN, 1)
        val invocation = option("Invocación", CharacterClassOptionKind.INVOCATION, 9)
        assertEquals(10, nextCharacterClassOptionSortOrder(listOf(plan, invocation)))
    }
}
