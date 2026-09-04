package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterH2ConditionalModuleOperationsTest {
    private val linkedClassId = Uuid.random()

    private fun option(
        name: String,
        kind: CharacterClassOptionKind,
        order: Int,
        source: String? = "Manual",
        active: Boolean = true,
    ) = CharacterClassOption(
        id = Uuid.random(),
        linkedClassId = linkedClassId,
        kind = kind,
        name = name,
        source = source,
        costText = "Referencia $name",
        effectSummary = "Resumen $name",
        notes = "Notas $name",
        active = active,
        sortOrder = order,
    )

    @Test
    fun h2ProjectionsOwnOnlyTheirExplicitKinds() {
        val technique = option("Maniobra", CharacterClassOptionKind.TECHNIQUE, 0)
        val metamagic = option("Metamagia", CharacterClassOptionKind.METAMAGIC, 1)
        val invocation = option("Invocación", CharacterClassOptionKind.INVOCATION, 2)
        val pactChoice = option("Pacto del Tomo", CharacterClassOptionKind.PACT_CHOICE, 3)
        val subclassState = option("Estado genérico", CharacterClassOptionKind.SUBCLASS_STATE, 4)
        val other = option("Otro genérico", CharacterClassOptionKind.OTHER, 5)
        val artificer = option("Plan", CharacterClassOptionKind.ARTIFICER_PLAN, 6)
        val options = listOf(technique, metamagic, invocation, pactChoice, subclassState, other, artificer)

        assertEquals(listOf(technique.id), presentCharacterTechniqueOptions(options).map { it.id })
        assertEquals(listOf(metamagic.id), presentCharacterMetamagicOptions(options).map { it.id })
        assertEquals(listOf(invocation.id, pactChoice.id), presentCharacterPactOptions(options).map { it.id })
        assertFalse(isPactCharacterOption(subclassState))
        assertFalse(isPactCharacterOption(other))
        assertFalse(isTechniqueCharacterOption(artificer))
        assertFalse(isMetamagicCharacterOption(invocation))
    }

    @Test
    fun h2SearchSourceActiveFavoriteAndAlphabeticalArePresentationOnly() {
        val alpha = option("Álfa", CharacterClassOptionKind.TECHNIQUE, 8, source = "Guerrero")
        val zeta = option("Zeta", CharacterClassOptionKind.TECHNIQUE, 2, source = "Guerrero")
        val inactive = option("Dormida", CharacterClassOptionKind.TECHNIQUE, 4, source = "Otra", active = false)
        val options = listOf(alpha, zeta, inactive)

        val filtered = presentCharacterTechniqueOptions(
            options = options,
            order = CharacterPresentationOrder.ALPHABETICAL,
            query = CharacterCollectionQuery(
                searchText = "alfa",
                activeFilterKeys = setOf(
                    CHARACTER_CLASS_OPTION_ACTIVE_FILTER_KEY,
                    CHARACTER_CLASS_OPTION_FAVORITE_FILTER_KEY,
                    characterClassOptionSourceFilterKey("Guerrero"),
                ),
            ),
            isFavorite = { it.id == alpha.id },
        )

        assertEquals(listOf(alpha.id), filtered.map { it.id })
        assertEquals(listOf(8, 2, 4), options.map { it.sortOrder })

        val alphabetical = presentCharacterTechniqueOptions(
            options = listOf(alpha, zeta),
            order = CharacterPresentationOrder.ALPHABETICAL,
        )
        assertEquals(listOf(alpha.id, zeta.id), alphabetical.map { it.id })
        assertEquals(listOf(8, 2), listOf(alpha.sortOrder, zeta.sortOrder))
    }

    @Test
    fun pactKindFiltersDistinguishPactChoicesFromInvocations() {
        val invocation = option("Agonizing Blast", CharacterClassOptionKind.INVOCATION, 0)
        val choice = option("Pacto del Tomo", CharacterClassOptionKind.PACT_CHOICE, 1)
        val options = listOf(invocation, choice)

        val onlyChoices = presentCharacterPactOptions(
            options,
            query = CharacterCollectionQuery(activeFilterKeys = setOf(CHARACTER_PACT_CHOICE_FILTER_KEY)),
        )
        val onlyInvocations = presentCharacterPactOptions(
            options,
            query = CharacterCollectionQuery(activeFilterKeys = setOf(CHARACTER_PACT_INVOCATION_FILTER_KEY)),
        )

        assertEquals(listOf(choice.id), onlyChoices.map { it.id })
        assertEquals(listOf(invocation.id), onlyInvocations.map { it.id })
        assertEquals("Pacto / elección", characterClassOptionKindDisplayLabel(CharacterClassOptionKind.PACT_CHOICE))
    }

    @Test
    fun movingTechniqueChangesOnlyTechniqueOwnedPositions() {
        val techniqueA = option("Técnica A", CharacterClassOptionKind.TECHNIQUE, 0)
        val metamagic = option("Metamagia", CharacterClassOptionKind.METAMAGIC, 1)
        val techniqueB = option("Técnica B", CharacterClassOptionKind.TECHNIQUE, 2)
        val pact = option("Pacto", CharacterClassOptionKind.PACT_CHOICE, 3)
        val techniqueC = option("Técnica C", CharacterClassOptionKind.TECHNIQUE, 4)

        val moved = moveCharacterTechniqueOptionManual(
            listOf(techniqueA, metamagic, techniqueB, pact, techniqueC),
            optionId = techniqueA.id,
            offset = 1,
        ).sortedBy { it.sortOrder }

        assertEquals(
            listOf(techniqueB.id, metamagic.id, techniqueA.id, pact.id, techniqueC.id),
            moved.map { it.id },
        )
        assertEquals(1, moved.single { it.id == metamagic.id }.sortOrder)
        assertEquals(3, moved.single { it.id == pact.id }.sortOrder)
    }

    @Test
    fun movingPactOptionsPreservesTechniqueMetamagicArtificeAndGenericSlots() {
        val invocation = option("Invocación", CharacterClassOptionKind.INVOCATION, 0)
        val technique = option("Técnica", CharacterClassOptionKind.TECHNIQUE, 1)
        val pactChoice = option("Pacto", CharacterClassOptionKind.PACT_CHOICE, 2)
        val metamagic = option("Metamagia", CharacterClassOptionKind.METAMAGIC, 3)
        val invocationB = option("Invocación B", CharacterClassOptionKind.INVOCATION, 4)
        val artificer = option("Plan", CharacterClassOptionKind.ARTIFICER_PLAN, 5)
        val generic = option("Estado", CharacterClassOptionKind.SUBCLASS_STATE, 6)

        val moved = moveCharacterPactOptionManual(
            listOf(invocation, technique, pactChoice, metamagic, invocationB, artificer, generic),
            optionId = invocation.id,
            offset = 1,
        ).sortedBy { it.sortOrder }

        assertEquals(
            listOf(pactChoice.id, technique.id, invocation.id, metamagic.id, invocationB.id, artificer.id, generic.id),
            moved.map { it.id },
        )
        assertEquals(1, moved.single { it.id == technique.id }.sortOrder)
        assertEquals(3, moved.single { it.id == metamagic.id }.sortOrder)
        assertEquals(5, moved.single { it.id == artificer.id }.sortOrder)
        assertEquals(6, moved.single { it.id == generic.id }.sortOrder)
    }

    @Test
    fun duplicatedPactChoiceKeepsOwnershipAndAppendsAfterAllFamilies() {
        val source = option("Pacto del Tomo", CharacterClassOptionKind.PACT_CHOICE, 3)
            .copy(active = false, pinned = true)
        val hiddenArtifice = option("Plan", CharacterClassOptionKind.ARTIFICER_PLAN, 9)
        val newId = Uuid.random()

        val copy = duplicateCharacterClassOption(
            source = source,
            newId = newId,
            sortOrder = nextCharacterClassOptionSortOrder(listOf(source, hiddenArtifice)),
        )

        assertEquals(newId, copy.id)
        assertEquals(CharacterClassOptionKind.PACT_CHOICE, copy.kind)
        assertEquals("Pacto del Tomo (copia)", copy.name)
        assertEquals(10, copy.sortOrder)
        assertFalse(copy.active)
        assertTrue(copy.pinned)
    }

    @Test
    fun pactChoiceRoundTripsThroughExistingClassOptionPersistenceWithoutSchemaChange() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val campaign = campaigns.createCampaign("H2")
            val created = characters.createCharacter(campaign.id, "Brujo")
            val warlockId = Uuid.random()
            val pactChoice = CharacterClassOption(
                id = Uuid.random(),
                linkedClassId = warlockId,
                kind = CharacterClassOptionKind.PACT_CHOICE,
                name = "Pacto del Tomo",
                source = "Brujo",
                costText = "Elección de pacto",
                effectSummary = "Referencia persistente",
                notes = "Sin duplicar conjuros",
                active = true,
                pinned = true,
                sortOrder = 0,
            )
            val invocation = CharacterClassOption(
                id = Uuid.random(),
                linkedClassId = warlockId,
                kind = CharacterClassOptionKind.INVOCATION,
                name = "Invocación",
                source = "Brujo",
                costText = null,
                effectSummary = "Referencia",
                notes = null,
                active = true,
                sortOrder = 1,
            )

            val saved = characters.saveCharacter(
                created.copy(
                    classes = listOf(
                        CharacterClassLevel(
                            id = warlockId,
                            name = "Brujo",
                            level = 5,
                            hitDieSides = 8,
                            hitDiceRemaining = 5,
                            sortOrder = 0,
                            rulesFamily = CharacterRulesFamily.DND_5_5E,
                            catalogKey = "warlock-2024",
                        ),
                    ),
                    classOptions = listOf(pactChoice, invocation),
                ),
            )
            val reopened = characters.character(saved.id)!!

            assertEquals(listOf(CharacterClassOptionKind.PACT_CHOICE, CharacterClassOptionKind.INVOCATION), reopened.classOptions.map { it.kind })
            val reopenedChoice = reopened.classOptions.single { it.id == pactChoice.id }
            assertEquals("Pacto del Tomo", reopenedChoice.name)
            assertEquals("Elección de pacto", reopenedChoice.costText)
            assertEquals("Referencia persistente", reopenedChoice.effectSummary)
            assertEquals("Sin duplicar conjuros", reopenedChoice.notes)
            assertTrue(reopenedChoice.pinned)
        } finally {
            driver.close()
        }
    }
}
