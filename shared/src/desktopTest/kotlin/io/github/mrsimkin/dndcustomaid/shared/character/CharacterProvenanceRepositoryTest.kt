package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterProvenanceRepositoryTest {
    @Test
    fun canonicalRaceAndBackgroundRenamesKeepIdentityWhileDeletionLeavesTraitsUnresolved() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val provenance = CharacterProvenanceRepository(database)
            val campaign = campaigns.createCampaign("P7")
            val base = characters.createCharacter(campaign.id, "Identidades")
            val raceTraitId = Uuid.random()
            val backgroundTraitId = Uuid.random()
            val saved = characters.saveCharacter(
                base.copy(
                    background = CharacterBackground(name = "Sabio", race = "Elfo"),
                    traits = listOf(
                        trait(raceTraitId, "Linaje feérico", "Elfo", CharacterTraitType.SPECIES_RACE, 0),
                        trait(backgroundTraitId, "Investigador", "Sabio", CharacterTraitType.BACKGROUND, 1),
                    ),
                ),
            )

            val initial = provenance.state(saved.id)
            val speciesId = requireNotNull(initial.speciesIdentity).id
            val backgroundId = requireNotNull(initial.backgroundIdentity).id
            assertEquals(speciesId, initial.traitProvenance.single { it.traitId == raceTraitId }.targetId)
            assertEquals(backgroundId, initial.traitProvenance.single { it.traitId == backgroundTraitId }.targetId)

            characters.saveCharacter(
                requireNotNull(characters.character(saved.id)).copy(
                    background = CharacterBackground(name = "Erudito", race = "Alto elfo"),
                ),
            )
            val renamed = provenance.state(saved.id)
            assertEquals(speciesId, requireNotNull(renamed.speciesIdentity).id)
            assertEquals("Alto elfo", renamed.speciesIdentity?.name)
            assertEquals(backgroundId, requireNotNull(renamed.backgroundIdentity).id)
            assertEquals("Erudito", renamed.backgroundIdentity?.name)
            assertEquals(
                "Alto elfo",
                resolveCharacterTraitProvenance(
                    renamed.traitProvenance.single { it.traitId == raceTraitId },
                    requireNotNull(characters.character(saved.id)),
                    renamed,
                ).label,
            )

            characters.saveCharacter(
                requireNotNull(characters.character(saved.id)).copy(background = CharacterBackground()),
            )
            val deleted = provenance.state(saved.id)
            assertNull(deleted.speciesIdentity)
            assertNull(deleted.backgroundIdentity)
            val sheetAfterDeletion = requireNotNull(characters.character(saved.id))
            val raceResolution = resolveCharacterTraitProvenance(
                deleted.traitProvenance.single { it.traitId == raceTraitId },
                sheetAfterDeletion,
                deleted,
            )
            val backgroundResolution = resolveCharacterTraitProvenance(
                deleted.traitProvenance.single { it.traitId == backgroundTraitId },
                sheetAfterDeletion,
                deleted,
            )
            assertEquals(CharacterProvenanceResolutionStatus.UNRESOLVED, raceResolution.status)
            assertEquals(CharacterProvenanceResolutionStatus.UNRESOLVED, backgroundResolution.status)
            assertEquals("Alto elfo", raceResolution.label)
            assertEquals("Erudito", backgroundResolution.label)
        }
    }

    @Test
    fun ambiguousLegacyClassTextIsPreservedInsteadOfGuessed() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val provenance = CharacterProvenanceRepository(database)
            val campaign = campaigns.createCampaign("Ambigüedad")
            val base = characters.createCharacter(campaign.id, "Multiclase")
            val traitId = Uuid.random()
            val saved = characters.saveCharacter(
                base.copy(
                    classes = listOf(
                        classLevel(Uuid.random(), "Mago", 0),
                        classLevel(Uuid.random(), "Mago", 1),
                    ),
                    traits = listOf(trait(traitId, "Origen dudoso", "Mago", CharacterTraitType.CLASS, 0)),
                ),
            )

            val state = provenance.state(saved.id)
            val migrated = state.traitProvenance.single()
            assertNull(migrated.targetId)
            assertEquals("Mago", migrated.legacyText)
            val resolution = resolveCharacterTraitProvenance(
                migrated,
                requireNotNull(characters.character(saved.id)),
                state,
            )
            assertEquals(CharacterProvenanceResolutionStatus.UNRESOLVED, resolution.status)
            assertTrue(characterProvenanceOptions(requireNotNull(characters.character(saved.id)), state, CharacterProvenanceKind.CLASS).size == 2)
        }
    }

    @Test
    fun subraceIsOwnedChildOfCurrentRaceAndUsesSameCanonicalSchemaForCustomDefinitions() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val provenance = CharacterProvenanceRepository(database)
            val campaign = campaigns.createCampaign("Subraza")
            val base = characters.createCharacter(campaign.id, "Linaje")
            val saved = characters.saveCharacter(
                base.copy(background = CharacterBackground(race = "Elfo")),
            )

            val initial = provenance.state(saved.id)
            val species = requireNotNull(initial.speciesIdentity)
            val child = CharacterOwnedSubraceIdentity(
                id = Uuid.random(),
                parentSpeciesId = species.id,
                name = "Elfo lunar",
                definitionKey = "custom:elfo-lunar",
            )
            val withSubrace = provenance.saveState(
                saved.id,
                initial.copy(subraceIdentity = child),
            )
            val option = characterProvenanceOptions(
                requireNotNull(characters.character(saved.id)),
                withSubrace,
                CharacterProvenanceKind.SUBRACE,
            ).single()
            assertEquals(child.id, option.targetId)
            assertEquals("Elfo lunar", option.label)
            assertEquals("Elfo", option.parentLabel)
            assertEquals("custom:elfo-lunar", withSubrace.subraceIdentity?.definitionKey)

            assertFailsWith<IllegalArgumentException> {
                provenance.saveState(
                    saved.id,
                    withSubrace.copy(speciesIdentity = null),
                )
            }
        }
    }

    @Test
    fun structuredOriginsRejectFreeTextAndSeparateTraitInstancesKeepSeparateSources() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val provenance = CharacterProvenanceRepository(database)
            val campaign = campaigns.createCampaign("Instancias")
            val firstClassId = Uuid.random()
            val secondClassId = Uuid.random()
            val firstTraitId = Uuid.random()
            val secondTraitId = Uuid.random()
            val saved = characters.saveCharacter(
                characters.createCharacter(campaign.id, "Dos fuentes").copy(
                    classes = listOf(
                        classLevel(firstClassId, "Guerrero", 0),
                        classLevel(secondClassId, "Mago", 1),
                    ),
                    traits = listOf(
                        trait(firstTraitId, "Entrenamiento", "Guerrero", CharacterTraitType.CLASS, 0),
                        trait(secondTraitId, "Entrenamiento", "Mago", CharacterTraitType.CLASS, 1),
                    ),
                ),
            )

            val state = provenance.state(saved.id)
            val first = state.traitProvenance.single { it.traitId == firstTraitId }
            val second = state.traitProvenance.single { it.traitId == secondTraitId }
            assertNotEquals(first.traitId, second.traitId)
            assertEquals(firstClassId, first.targetId)
            assertEquals(secondClassId, second.targetId)
            assertNotEquals(first.targetId, second.targetId)

            assertFailsWith<IllegalArgumentException> {
                provenance.saveState(
                    saved.id,
                    state.copy(
                        traitProvenance = state.traitProvenance.map { item ->
                            if (item.traitId == firstTraitId) item.copy(freeText = "atajo manual") else item
                        },
                    ),
                )
            }
        }
    }

    @Test
    fun subclassOptionsRemainAttachedToParentClassForDisambiguation() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val provenance = CharacterProvenanceRepository(database)
            val campaign = campaigns.createCampaign("Subclases")
            val firstClassId = Uuid.random()
            val secondClassId = Uuid.random()
            val saved = characters.saveCharacter(
                characters.createCharacter(campaign.id, "Dos tradiciones").copy(
                    classes = listOf(
                        classLevel(firstClassId, "Mago", 0).copy(subclassName = "Tradición"),
                        classLevel(secondClassId, "Bardo", 1).copy(subclassName = "Tradición"),
                    ),
                ),
            )

            val state = provenance.state(saved.id)
            val options = characterProvenanceOptions(
                requireNotNull(characters.character(saved.id)),
                state,
                CharacterProvenanceKind.SUBCLASS,
            )
            assertEquals(2, options.size)
            assertEquals(setOf("Tradición (Mago)", "Tradición (Bardo)"), options.map { it.disambiguatedLabel }.toSet())
            assertEquals(setOf(firstClassId, secondClassId), state.subclassIdentities.map { it.parentClassId }.toSet())
        }
    }

    private fun classLevel(id: Uuid, name: String, order: Int) = CharacterClassLevel(
        id = id,
        name = name,
        level = 1,
        hitDieSides = 6,
        hitDiceRemaining = 1,
        sortOrder = order,
    )

    private fun trait(
        id: Uuid,
        name: String,
        source: String,
        type: CharacterTraitType,
        order: Int,
    ) = CharacterTrait(
        id = id,
        name = name,
        source = source,
        type = type,
        description = "",
        notes = null,
        maxUses = null,
        spentUses = 0,
        recovery = null,
        activation = null,
        sortOrder = order,
    )

    private fun withDatabase(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}
