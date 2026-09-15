package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.SyncMetadata
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class HostedPcSnapshotSyncTest {
    @Test
    fun queueUsesVersionedCharacterBackupAndCurrentPcRevision() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val character = CharacterRepository(database).createCharacter(campaign.id, "Simkin")
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata("PC", character.id, SyncMetadata(revision = Revision(4)))
        val outbox = HostedOutboxRepository(database)

        val queued = HostedPcSnapshotQueueService(database, outbox = outbox).queueCurrentSnapshot(
            characterId = character.id,
            createdAtEpochSeconds = 100,
            exportedAtEpochSeconds = 90,
        )
        val payload = outbox.pcSnapshotPayload(queued.mutation)

        assertEquals(character.id, payload.pcId)
        assertEquals(campaign.id, payload.campaignId)
        assertEquals(4L, payload.expectedRevision)
        assertEquals(character.id, payload.snapshot.character.id)
        assertEquals(campaign.id, payload.snapshot.character.campaignId)
        assertEquals(90L, payload.snapshot.exportedAtEpochSeconds)
    }

    @Test
    fun successfulPcDeliveryAcknowledgesOutboxAndAdvancesLocalRevision() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val character = CharacterRepository(database).createCharacter(campaign.id, "Simkin")
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata("PC", character.id, SyncMetadata(revision = Revision(2)))
        val outbox = HostedOutboxRepository(database)
        val queued = HostedPcSnapshotQueueService(database, outbox = outbox).queueCurrentSnapshot(
            characterId = character.id,
            createdAtEpochSeconds = 100,
        )
        val payload = outbox.pcSnapshotPayload(queued.mutation)

        val delivery = HostedOutboxDeliveryService(
            outbox = outbox,
            pcSnapshotPutter = { mutationId, campaignId, pcId, expectedRevision, snapshot ->
                assertEquals(queued.mutation.mutationId, mutationId)
                assertEquals(campaign.id, campaignId)
                assertEquals(character.id, pcId)
                assertEquals(2L, expectedRevision)
                HostedPcSnapshotPut(
                    pc = HostedPcSnapshot(
                        id = pcId,
                        campaignId = campaignId,
                        ownerUserId = null,
                        controllerUserId = null,
                        name = snapshot.character.name,
                        revision = 3,
                        snapshotFormat = snapshot.format,
                        snapshotVersion = snapshot.version,
                        snapshot = snapshot,
                    ),
                    applied = true,
                )
            },
            campaignCreator = { _, _, _ -> error("Campaign creation not expected") },
        )

        val report = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 120) }

        assertEquals(HostedOutboxDeliveryReport(1, 1, 0, 0), report)
        assertNull(outbox.mutation(payload.mutationId))
        assertEquals(Revision(3), spine.syncMetadata("PC", character.id).revision)
    }

    @Test
    fun newerHostedSnapshotReplacesCurrentStateWithoutChangingPcIdentity() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        val local = characters.createCharacter(campaign.id, "Local Name")
        val backups = CharacterBackupRepository(database)
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata("PC", local.id, SyncMetadata(revision = Revision(1)))
        val baseDocument = backups.exportCharacter(local.id, 100)
        val remoteDocument = baseDocument.copy(
            character = baseDocument.character.copy(name = "Hosted Name"),
            exportedAtEpochSeconds = 110,
        )
        val remote = hostedPc(remoteDocument, revision = 2)
        val service = HostedPcSnapshotPullService(
            database = database,
            snapshotsProvider = { listOf(remote) },
        )

        val result = runBlocking { service.refreshCampaign(campaign.id) }

        assertEquals(listOf(local.id), result.appliedPcIds)
        assertEquals("Hosted Name", assertNotNull(characters.character(local.id)).name)
        assertEquals(local.id, characters.character(local.id)?.id)
        assertEquals(Revision(2), spine.syncMetadata("PC", local.id).revision)
    }

    @Test
    fun equalRevisionPreservesPotentiallyUnsentLocalEdits() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        val base = characters.createCharacter(campaign.id, "Original")
        val backups = CharacterBackupRepository(database)
        val remoteDocument = backups.exportCharacter(base.id, 100)
        characters.saveCharacter(base.copy(name = "Local Unsent Edit"))
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata("PC", base.id, SyncMetadata(revision = Revision(3)))
        val service = HostedPcSnapshotPullService(
            database = database,
            snapshotsProvider = { listOf(hostedPc(remoteDocument, revision = 3)) },
        )

        val result = runBlocking { service.refreshCampaign(campaign.id) }

        assertEquals(listOf(base.id), result.unchangedPcIds)
        assertEquals("Local Unsent Edit", assertNotNull(characters.character(base.id)).name)
        assertEquals(Revision(3), spine.syncMetadata("PC", base.id).revision)
    }

    @Test
    fun localTombstoneBlocksHostedResurrection() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        val local = characters.createCharacter(campaign.id, "Keep Local")
        val backups = CharacterBackupRepository(database)
        val document = backups.exportCharacter(local.id, 100).copy(
            character = local.copy(name = "Hosted Active"),
        )
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata(
            "PC",
            local.id,
            SyncMetadata(revision = Revision(4), deletedAtEpochSeconds = 200),
        )
        val service = HostedPcSnapshotPullService(
            database = database,
            snapshotsProvider = { listOf(hostedPc(document, revision = 5)) },
        )

        val result = runBlocking { service.refreshCampaign(campaign.id) }

        assertEquals(HostedPcPullConflictReason.LOCAL_TOMBSTONE, result.conflicts.single().reason)
        assertEquals("Keep Local", assertNotNull(characters.character(local.id)).name)
        assertEquals(
            SyncMetadata(revision = Revision(4), deletedAtEpochSeconds = 200),
            spine.syncMetadata("PC", local.id),
        )
    }

    @Test
    fun hostedDeletionTombstonesSyncStateWithoutDeletingLocalCharacter() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        val local = characters.createCharacter(campaign.id, "Recoverable Local Copy")
        val backups = CharacterBackupRepository(database)
        val document = backups.exportCharacter(local.id, 100)
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata("PC", local.id, SyncMetadata(revision = Revision(2)))
        val remote = hostedPc(document, revision = 3, deletedAtEpochSeconds = 300)
        val service = HostedPcSnapshotPullService(
            database = database,
            snapshotsProvider = { listOf(remote) },
        )

        val result = runBlocking { service.refreshCampaign(campaign.id) }

        assertEquals(listOf(local.id), result.tombstonedPcIds)
        assertNotNull(characters.character(local.id))
        assertEquals(
            SyncMetadata(revision = Revision(3), deletedAtEpochSeconds = 300),
            spine.syncMetadata("PC", local.id),
        )
    }

    @Test
    fun hostedSnapshotCanCreateSameStablePcIdentityLocally() {
        val sourceDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        val targetDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(sourceDriver)
        AppDatabase.Schema.create(targetDriver)
        try {
            val sourceDb = AppDatabase(sourceDriver)
            val targetDb = AppDatabase(targetDriver)
            val campaign = CampaignRepository(sourceDb).createCampaign("Terramore")
            val sourceCharacter = CharacterRepository(sourceDb).createCharacter(campaign.id, "Remote PC")
            val document = CharacterBackupRepository(sourceDb).exportCharacter(sourceCharacter.id, 100)

            CampaignRepository(targetDb).upsertCampaign(campaign.id, campaign.name)
            val service = HostedPcSnapshotPullService(
                database = targetDb,
                snapshotsProvider = { listOf(hostedPc(document, revision = 0)) },
            )
            val result = runBlocking { service.refreshCampaign(campaign.id) }
            val imported = CharacterRepository(targetDb).character(sourceCharacter.id)

            assertEquals(listOf(sourceCharacter.id), result.appliedPcIds)
            assertNotNull(imported)
            assertEquals(sourceCharacter.id, imported.id)
            assertEquals(campaign.id, imported.campaignId)
            assertEquals("Remote PC", imported.name)
        } finally {
            sourceDriver.close()
            targetDriver.close()
        }
    }

    private fun hostedPc(
        document: io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument,
        revision: Long,
        deletedAtEpochSeconds: Long? = null,
    ): HostedPcSnapshot = HostedPcSnapshot(
        id = document.character.id,
        campaignId = document.character.campaignId,
        ownerUserId = null,
        controllerUserId = null,
        name = document.character.name,
        revision = revision,
        deletedAtEpochSeconds = deletedAtEpochSeconds,
        snapshotFormat = document.format,
        snapshotVersion = document.version,
        snapshot = document,
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
