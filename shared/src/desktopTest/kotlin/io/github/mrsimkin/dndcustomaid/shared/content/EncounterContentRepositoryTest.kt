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
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class EncounterContentRepositoryTest {
    @Test
    fun encounterRoundTripPreservesPreparedParticipantsAndGuidance() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random(), externalSubject = "owner")
        spine.upsertAccount(owner)
        val creature = CreatureContentRepository(database).createPersonal(
            ownerAccountId = owner.id,
            rawDisplayName = "Canal Ghoul",
            payload = sampleCreaturePayload(),
            nowEpochSeconds = 100,
        )
        val npc = NpcContentRepository(database).createPersonal(
            ownerAccountId = owner.id,
            rawDisplayName = "Archivist Mara",
            payload = sampleNpcPayload(),
            nowEpochSeconds = 100,
        )
        val repository = EncounterContentRepository(database)
        val payload = sampleEncounterPayload(creature.item.identity.id, npc.item.identity.id)

        val encounter = repository.createPersonal(owner.id, "Archive Ambush", payload, 150)
        val reopened = repository.encounter(encounter.item.identity.id)

        assertEquals(payload, reopened?.payload)
        assertEquals(ContentScope.Personal(owner.id), reopened?.item?.identity?.scope)
        assertEquals(ReusableContentFamily.ENCOUNTER, reopened?.item?.family)
    }

    @Test
    fun campaignCopyCopiesAndRemapsUniqueCreatureAndNpcDependencies() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val creatures = CreatureContentRepository(database)
        val npcs = NpcContentRepository(database)
        val creaturePayload = sampleCreaturePayload()
        val creature = creatures.createPersonal(owner.id, "Canal Ghoul", creaturePayload, 100)
        val npc = npcs.createPersonal(owner.id, "Archivist Mara", sampleNpcPayload(), 100)
        val encounterRepository = EncounterContentRepository(database)
        val payload = sampleEncounterPayload(creature.item.identity.id, npc.item.identity.id)
        val personal = encounterRepository.createPersonal(owner.id, "Archive Ambush", payload, 150)

        val copy = encounterRepository.copyPersonalToCampaign(
            sourceId = personal.item.identity.id,
            campaignId = campaign.id,
            copiedAtEpochSeconds = 200,
        )

        assertNotEquals(personal.item.identity.id, copy.item.identity.id)
        assertEquals(personal.item.identity.id, copy.item.identity.provenance?.sourceObjectId)
        assertEquals(ContentScope.Personal(owner.id), copy.item.identity.provenance?.sourceScope)
        assertEquals(200, copy.item.identity.provenance?.copiedAtEpochSeconds)
        assertEquals(ContentScope.Campaign(campaign.id), copy.item.identity.scope)
        assertEquals(Revision(0), copy.item.identity.revision)

        val copiedCreatureIdA = copy.payload.participants[0].sourceContentId
        val copiedCreatureIdB = copy.payload.participants[1].sourceContentId
        val copiedNpcId = copy.payload.participants[2].sourceContentId
        assertEquals(copiedCreatureIdA, copiedCreatureIdB)
        assertNotEquals(creature.item.identity.id, copiedCreatureIdA)
        assertNotEquals(npc.item.identity.id, copiedNpcId)
        assertNull(copy.payload.participants[3].sourceContentId)

        val copiedCreature = requireNotNull(creatures.creature(requireNotNull(copiedCreatureIdA)))
        assertEquals(ContentScope.Campaign(campaign.id), copiedCreature.item.identity.scope)
        assertEquals(creature.item.identity.id, copiedCreature.item.identity.provenance?.sourceObjectId)
        assertEquals(creaturePayload, copiedCreature.payload)

        val copiedNpc = requireNotNull(npcs.npc(requireNotNull(copiedNpcId)))
        assertEquals(ContentScope.Campaign(campaign.id), copiedNpc.item.identity.scope)
        assertEquals(npc.item.identity.id, copiedNpc.item.identity.provenance?.sourceObjectId)

        val reusable = ReusableContentRepository(database)
        assertEquals(1, reusable.listCampaign(campaign.id, ReusableContentFamily.CREATURE).size)
        assertEquals(1, reusable.listCampaign(campaign.id, ReusableContentFamily.NPC).size)

        val changedCreature = creaturePayload.copy(notes = "Source changed after Encounter copy")
        assertIs<RevisionDecision.Accepted>(
            creatures.updatePayload(
                id = creature.item.identity.id,
                expectedRevision = Revision(0),
                payload = changedCreature,
                updatedAtEpochSeconds = 300,
            ),
        )
        assertEquals(changedCreature, creatures.creature(creature.item.identity.id)?.payload)
        assertEquals(creaturePayload, creatures.creature(copiedCreature.item.identity.id)?.payload)
        assertEquals(payload, encounterRepository.encounter(personal.item.identity.id)?.payload)
        assertNotEquals(payload.participants[0].sourceContentId, copy.payload.participants[0].sourceContentId)
    }

    @Test
    fun encounterParticipantDependenciesMustMatchEncounterScope() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val firstOwner = AccountIdentity(Uuid.random())
        val secondOwner = AccountIdentity(Uuid.random())
        spine.upsertAccount(firstOwner)
        spine.upsertAccount(secondOwner)
        val creatures = CreatureContentRepository(database)
        val personalCreature = creatures.createPersonal(
            firstOwner.id,
            "Wrong-owner Ghoul",
            sampleCreaturePayload(),
            100,
        )
        val repository = EncounterContentRepository(database)

        assertFailsWith<IllegalArgumentException> {
            repository.createPersonal(
                ownerAccountId = secondOwner.id,
                rawDisplayName = "Invalid Personal Encounter",
                payload = EncounterPayload(
                    participants = listOf(
                        EncounterParticipant(sourceContentId = personalCreature.item.identity.id),
                    ),
                ),
                nowEpochSeconds = 150,
            )
        }

        val campaignRepository = CampaignRepository(database)
        val firstCampaign = campaignRepository.createCampaign("First Campaign")
        val secondCampaign = campaignRepository.createCampaign("Second Campaign")
        val campaignCreature = creatures.createCampaign(
            firstCampaign.id,
            "Wrong-campaign Ghoul",
            sampleCreaturePayload(),
            200,
        )

        assertFailsWith<IllegalArgumentException> {
            repository.createCampaign(
                campaignId = secondCampaign.id,
                rawDisplayName = "Invalid Campaign Encounter",
                payload = EncounterPayload(
                    participants = listOf(
                        EncounterParticipant(sourceContentId = campaignCreature.item.identity.id),
                    ),
                ),
                nowEpochSeconds = 250,
            )
        }
    }

    @Test
    fun staleAndDeletedEncounterMutationsCannotOverwriteOrResurrect() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val repository = EncounterContentRepository(database)
        val originalPayload = EncounterPayload(
            summary = "Freeform checkpoint encounter",
            participants = listOf(EncounterParticipant(label = "Dock thugs", quantity = 3)),
        )
        val original = repository.createPersonal(owner.id, "Dockside Checkpoint", originalPayload, 100)
        val changed = original.payload.copy(dmGuidance = "First accepted change")

        assertIs<RevisionDecision.Accepted>(
            repository.updatePayload(original.item.identity.id, Revision(0), changed, 200),
        )
        val stale = assertIs<RevisionDecision.Stale>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(0),
                original.payload.copy(dmGuidance = "Stale overwrite"),
                250,
            ),
        )
        assertEquals(Revision(1), stale.actual)
        assertEquals(changed, repository.encounter(original.item.identity.id)?.payload)

        val deleted = assertIs<RevisionDecision.Accepted>(
            repository.tombstone(original.item.identity.id, Revision(1), 300),
        )
        assertEquals(Revision(2), deleted.nextRevision)
        assertNull(repository.encounter(original.item.identity.id))

        assertIs<RevisionDecision.Deleted>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(2),
                original.payload.copy(dmGuidance = "Resurrection attempt"),
                400,
            ),
        )
        val tombstoned = repository.encounter(original.item.identity.id, includeDeleted = true)
        assertEquals(changed, tombstoned?.payload)
        assertEquals(Revision(2), tombstoned?.item?.identity?.revision)
        assertEquals(300, tombstoned?.item?.deletedAtEpochSeconds)
    }

    @Test
    fun migration24PreservesExistingCampaignAndBackfillsMetadataOnlyEncounter() {
        val file = File.createTempFile("dnd-custom-aid-encounter-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000001201"
        val encounterId = "00000000-0000-0000-0000-000000001202"

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
                            '$encounterId', 'ENCOUNTER', 'Legacy Canal Ambush', 'CAMPAIGN', '$campaignId',
                            NULL, NULL, NULL, NULL, 100, 100
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate(
                        "INSERT INTO object_sync_state(object_type, object_id, revision) VALUES ('REUSABLE_CONTENT', '$encounterId', 0)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 24, newVersion = 25)
            val database = AppDatabase(driver)

            assertEquals(
                "Legacy Campaign",
                database.campaignQueries.selectCampaignById(campaignId) { _, name -> name }.executeAsOne(),
            )
            val migrated = EncounterContentRepository(database).encounter(Uuid.parse(encounterId))
            assertEquals("Legacy Canal Ambush", migrated?.item?.displayName)
            assertEquals(EncounterPayload(), migrated?.payload)
            driver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun encounterPayloadSurvivesDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-encounter-reopen", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val ownerId = Uuid.random()
        val encounterId = Uuid.random()
        val payload = EncounterPayload(
            summary = "Persistent encounter",
            environment = "Rain-soaked canal bridge",
            participants = listOf(
                EncounterParticipant(
                    label = "Dock thugs",
                    quantity = 4,
                    readiness = EncounterParticipantReadiness.RESERVE,
                    notes = "Arrive from the east stairs",
                ),
            ),
            tags = listOf("canal", "ambush"),
        )

        try {
            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val database = AppDatabase(driver)
                IntegratedSpineRepository(database).upsertAccount(AccountIdentity(ownerId))
                EncounterContentRepository(database).createPersonal(
                    ownerAccountId = ownerId,
                    rawDisplayName = "Persistent Canal Ambush",
                    payload = payload,
                    nowEpochSeconds = 100,
                    id = encounterId,
                )
            }

            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val reopened = EncounterContentRepository(AppDatabase(driver)).encounter(encounterId)
                assertEquals("Persistent Canal Ambush", reopened?.item?.displayName)
                assertEquals(payload, reopened?.payload)
            }
        } finally {
            file.delete()
        }
    }

    private fun sampleCreaturePayload(): CreaturePayload = CreaturePayload(
        creatureType = "undead",
        armorClass = 12,
        hitPoints = 22,
        speed = "30 ft., swim 20 ft.",
        challengeRating = "1",
        tactics = "Drag isolated targets toward the flooded stacks.",
        notes = "Prepared reusable creature dependency.",
    )

    private fun sampleNpcPayload(): NpcPayload = NpcPayload(
        conceptRole = "Archive guide under duress",
        personalityManner = "Precise, frightened, and impatient with improvisation",
        wantsFearsNeeds = "Wants the stolen ledger recovered; fears the canal ghouls",
        dmGuidance = "Mara bargains for safety before offering hidden-route knowledge.",
    )

    private fun sampleEncounterPayload(creatureId: Uuid, npcId: Uuid): EncounterPayload = EncounterPayload(
        summary = "A canal-ghoul ambush around a frightened archivist and a flooded catalogue balcony.",
        environment = "Half-submerged archive stacks, narrow balcony, west sluice wheel, waist-deep water.",
        context = "The ghouls want prey, while Mara is trying to reach the maintenance stairs without being seen.",
        dmGuidance = "Let noise and positioning drive escalation; do not force combat if the party creates another solution.",
        participants = listOf(
            EncounterParticipant(
                sourceContentId = creatureId,
                label = "Canal ghouls",
                quantity = 2,
                readiness = EncounterParticipantReadiness.EXPECTED,
                overrides = "One begins in the flooded stacks with half cover.",
            ),
            EncounterParticipant(
                sourceContentId = creatureId,
                label = "Reserve ghoul",
                quantity = 1,
                readiness = EncounterParticipantReadiness.RESERVE,
                notes = "Use only if sustained noise draws it from the cistern.",
            ),
            EncounterParticipant(
                sourceContentId = npcId,
                label = "Archivist Mara",
                quantity = 1,
                readiness = EncounterParticipantReadiness.CONDITIONAL,
                condition = "Present if the party reached the archive before Mara escaped through the maintenance stairs.",
                overrides = "Starts frightened and unwilling to fight.",
            ),
            EncounterParticipant(
                label = "Dock thugs",
                quantity = 3,
                readiness = EncounterParticipantReadiness.RESERVE,
                notes = "Freeform group with no reusable-content dependency.",
            ),
        ),
        tags = listOf("archive", "ambush", "canal"),
        notes = "Prepared Encounter only; not live combat state.",
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
