package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
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
            assertEquals("Elfo", raceResolution.label)
            assertEquals("Sabio", backgroundResolution.label)
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
