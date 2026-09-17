package io.github.mrsimkin.dndcustomaid.shared.content

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class CreatureContentRepositoryTest {
    @Test
    fun personalAndCampaignCreatureCreateReadRoundTripPreservesPayload() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random(), externalSubject = "owner")
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = CreatureContentRepository(database)
        val payload = samplePayload()

        val personal = repository.createPersonal(owner.id, "Goblin", payload, 100)
        val campaignCreature = repository.createCampaign(campaign.id, "Owlbear", payload, 110)

        assertEquals(payload, repository.creature(personal.item.identity.id)?.payload)
        assertEquals(ContentScope.Personal(owner.id), personal.item.identity.scope)
        assertEquals(ReusableContentFamily.CREATURE, personal.item.family)
        assertEquals(payload, repository.creature(campaignCreature.item.identity.id)?.payload)
        assertEquals(ContentScope.Campaign(campaign.id), campaignCreature.item.identity.scope)
    }

    @Test
    fun personalToCampaignCopyIsIndependentAndRetainsPayloadProvenance() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = CreatureContentRepository(database)
        val originalPayload = samplePayload()
        val personal = repository.createPersonal(owner.id, "Goblin", originalPayload, 100)

        val copy = repository.copyPersonalToCampaign(
            sourceId = personal.item.identity.id,
            campaignId = campaign.id,
            copiedAtEpochSeconds = 200,
        )

        assertNotEquals(personal.item.identity.id, copy.item.identity.id)
        assertEquals(originalPayload, copy.payload)
        assertEquals(personal.item.identity.id, copy.item.identity.provenance?.sourceObjectId)
        assertEquals(ContentScope.Personal(owner.id), copy.item.identity.provenance?.sourceScope)
        assertEquals(200, copy.item.identity.provenance?.copiedAtEpochSeconds)
        assertEquals(Revision(0), copy.item.identity.revision)

        val changedSource = originalPayload.copy(actions = "Scimitar. +4 to hit.", tactics = "Uses cover.")
        val accepted = assertIs<RevisionDecision.Accepted>(
            repository.updatePayload(
                id = personal.item.identity.id,
                expectedRevision = Revision(0),
                payload = changedSource,
                updatedAtEpochSeconds = 300,
            ),
        )
        assertEquals(Revision(1), accepted.nextRevision)
        assertEquals(changedSource, repository.creature(personal.item.identity.id)?.payload)
        assertEquals(originalPayload, repository.creature(copy.item.identity.id)?.payload)
        assertEquals(Revision(0), repository.creature(copy.item.identity.id)?.item?.identity?.revision)
    }

    @Test
    fun staleAndDeletedCreaturePayloadMutationsCannotOverwriteOrResurrect() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val repository = CreatureContentRepository(database)
        val original = repository.createPersonal(owner.id, "Goblin", samplePayload(), 100)
        val changed = original.payload.copy(notes = "First accepted change")

        assertIs<RevisionDecision.Accepted>(
            repository.updatePayload(original.item.identity.id, Revision(0), changed, 200),
        )
        val stale = assertIs<RevisionDecision.Stale>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(0),
                original.payload.copy(notes = "Stale overwrite"),
                250,
            ),
        )
        assertEquals(Revision(1), stale.actual)
        assertEquals(changed, repository.creature(original.item.identity.id)?.payload)

        val deleted = assertIs<RevisionDecision.Accepted>(
            repository.tombstone(original.item.identity.id, Revision(1), 300),
        )
        assertEquals(Revision(2), deleted.nextRevision)
        assertNull(repository.creature(original.item.identity.id))

        assertIs<RevisionDecision.Deleted>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(2),
                original.payload.copy(notes = "Resurrection attempt"),
                400,
            ),
        )
        val tombstoned = repository.creature(original.item.identity.id, includeDeleted = true)
        assertEquals(changed, tombstoned?.payload)
        assertEquals(Revision(2), tombstoned?.item?.identity?.revision)
        assertEquals(300, tombstoned?.item?.deletedAtEpochSeconds)
    }

    @Test
    fun migration19PreservesExistingCampaignAndBackfillsMetadataOnlyCreature() {
        val file = File.createTempFile("dnd-custom-aid-creature-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000000701"
        val creatureId = "00000000-0000-0000-0000-000000000702"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    statement.executeUpdate(
                        """
                        CREATE TABLE object_sync_state (
                            object_type TEXT NOT NULL,
                            object_id TEXT NOT NULL,
                            revision INTEGER NOT NULL DEFAULT 0 CHECK (revision >= 0),
                            deleted_at_epoch_seconds INTEGER,
                            PRIMARY KEY(object_type, object_id)
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate(
                        """
                        CREATE TABLE reusable_content (
                            id TEXT NOT NULL PRIMARY KEY,
                            family TEXT NOT NULL,
                            display_name TEXT NOT NULL,
                            scope_kind TEXT NOT NULL,
                            scope_ref TEXT NOT NULL,
                            source_object_id TEXT,
                            source_scope_kind TEXT,
                            source_scope_ref TEXT,
                            copied_at_epoch_seconds INTEGER,
                            created_at_epoch_seconds INTEGER NOT NULL,
                            updated_at_epoch_seconds INTEGER NOT NULL
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate("INSERT INTO campaign(id, name) VALUES ('$campaignId', 'Legacy Campaign')")
                    statement.executeUpdate(
                        """
                        INSERT INTO reusable_content(
                            id, family, display_name, scope_kind, scope_ref,
                            source_object_id, source_scope_kind, source_scope_ref, copied_at_epoch_seconds,
                            created_at_epoch_seconds, updated_at_epoch_seconds
                        ) VALUES (
                            '$creatureId', 'CREATURE', 'Legacy Goblin', 'CAMPAIGN', '$campaignId',
                            NULL, NULL, NULL, NULL, 100, 100
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate(
                        "INSERT INTO object_sync_state(object_type, object_id, revision) VALUES ('REUSABLE_CONTENT', '$creatureId', 0)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 19, newVersion = 20)
            val database = AppDatabase(driver)

            assertEquals(
                "Legacy Campaign",
                database.campaignQueries.selectCampaignById(campaignId) { _, name -> name }.executeAsOne(),
            )
            val migrated = CreatureContentRepository(database).creature(Uuid.parse(creatureId))
            assertEquals("Legacy Goblin", migrated?.item?.displayName)
            assertEquals(CreaturePayload(), migrated?.payload)
            driver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun creaturePayloadSurvivesDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-creature-reopen", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val ownerId = Uuid.random()
        val creatureId = Uuid.random()
        val payload = samplePayload().copy(notes = "Persist across reopen")

        try {
            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val database = AppDatabase(driver)
                IntegratedSpineRepository(database).upsertAccount(AccountIdentity(ownerId))
                CreatureContentRepository(database).createPersonal(
                    ownerAccountId = ownerId,
                    rawDisplayName = "Persistent Goblin",
                    payload = payload,
                    nowEpochSeconds = 100,
                    id = creatureId,
                )
            }

            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val reopened = CreatureContentRepository(AppDatabase(driver)).creature(creatureId)
                assertEquals("Persistent Goblin", reopened?.item?.displayName)
                assertEquals(payload, reopened?.payload)
            }
        } finally {
            file.delete()
        }
    }

    private fun samplePayload(): CreaturePayload = CreaturePayload(
        size = "Small",
        creatureType = "Humanoid (goblinoid)",
        alignment = "Neutral Evil",
        armorClass = 15,
        armorClassDetails = "leather armor, shield",
        hitPoints = 7,
        hitDice = "2d6",
        speed = "30 ft.",
        abilityScores = CreatureAbilityScores(8, 14, 10, 10, 8, 8),
        skills = "Stealth +6",
        senses = "darkvision 60 ft., passive Perception 9",
        languages = "Common, Goblin",
        challengeRating = "1/4",
        proficiencyBonus = 2,
        traits = "Nimble Escape. The goblin can take the Disengage or Hide action as a bonus action.",
        actions = "Scimitar. Melee Weapon Attack.",
        bonusActions = "Nimble Escape.",
        tactics = "Skirmishes from cover and retreats when isolated.",
        notes = "Reusable test creature.",
    )

    private fun withDatabase(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}
