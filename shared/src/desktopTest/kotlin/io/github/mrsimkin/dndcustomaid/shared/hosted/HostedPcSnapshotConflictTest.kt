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
import kotlin.test.assertTrue

class HostedPcSnapshotConflictTest {
    @Test
    fun localRevisionAheadIsReportedWithoutOverwritingLocalPc() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        val local = characters.createCharacter(campaign.id, "Local Newer")
        val backups = CharacterBackupRepository(database)
        val remoteDocument = backups.exportCharacter(local.id, 100).copy(
            character = local.copy(name = "Hosted Older"),
        )
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata("PC", local.id, SyncMetadata(revision = Revision(6)))
        val remote = HostedPcSnapshot(
            id = local.id,
            campaignId = campaign.id,
            name = remoteDocument.character.name,
            revision = 5,
            snapshotFormat = remoteDocument.format,
            snapshotVersion = remoteDocument.version,
            snapshot = remoteDocument,
        )
        val service = HostedPcSnapshotPullService(
            database = database,
            snapshotsProvider = { listOf(remote) },
        )

        val result = runBlocking { service.refreshCampaign(campaign.id) }

        assertEquals(HostedPcPullConflictReason.LOCAL_REVISION_AHEAD, result.conflicts.single().reason)
        assertEquals("Local Newer", assertNotNull(characters.character(local.id)).name)
        assertEquals(Revision(6), spine.syncMetadata("PC", local.id).revision)
    }

    @Test
    fun concurrentLocalAndHostedEditsAreExplicitConflictAndPreserveLocalData() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        val original = characters.createCharacter(campaign.id, "Original")
        val backups = CharacterBackupRepository(database)
        val baselineDocument = backups.exportCharacter(original.id, 100)
        val spine = IntegratedSpineRepository(database)
        val baselines = HostedPcSyncBaselineRepository(database)
        spine.putSyncMetadata("PC", original.id, SyncMetadata(revision = Revision(3)))
        baselines.record(original.id, Revision(3), baselineDocument)

        characters.saveCharacter(original.copy(name = "Local Offline Edit"))
        val hostedDocument = baselineDocument.copy(
            character = baselineDocument.character.copy(name = "Other Client Edit"),
            exportedAtEpochSeconds = 110,
        )
        val remote = HostedPcSnapshot(
            id = original.id,
            campaignId = campaign.id,
            name = hostedDocument.character.name,
            revision = 4,
            snapshotFormat = hostedDocument.format,
            snapshotVersion = hostedDocument.version,
            snapshot = hostedDocument,
        )
        val service = HostedPcSnapshotPullService(
            database = database,
            snapshotsProvider = { listOf(remote) },
        )

        val result = runBlocking { service.refreshCampaign(campaign.id) }

        assertEquals(HostedPcPullConflictReason.LOCAL_AND_HOSTED_CHANGED, result.conflicts.single().reason)
        assertEquals("Local Offline Edit", assertNotNull(characters.character(original.id)).name)
        assertEquals(Revision(3), spine.syncMetadata("PC", original.id).revision)
        assertEquals(Revision(3), assertNotNull(baselines.baseline(original.id)).revision)
    }

    @Test
    fun serverNewerWithUnknownLegacyBaselineRefusesDestructiveGuess() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        val local = characters.createCharacter(campaign.id, "Possibly Edited Local")
        val backups = CharacterBackupRepository(database)
        val hostedDocument = backups.exportCharacter(local.id, 100).copy(
            character = local.copy(name = "Hosted Newer"),
        )
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata("PC", local.id, SyncMetadata(revision = Revision(2)))
        val remote = HostedPcSnapshot(
            id = local.id,
            campaignId = campaign.id,
            name = hostedDocument.character.name,
            revision = 3,
            snapshotFormat = hostedDocument.format,
            snapshotVersion = hostedDocument.version,
            snapshot = hostedDocument,
        )

        val result = runBlocking {
            HostedPcSnapshotPullService(
                database = database,
                snapshotsProvider = { listOf(remote) },
            ).refreshCampaign(campaign.id)
        }

        assertEquals(HostedPcPullConflictReason.SYNC_BASELINE_MISSING, result.conflicts.single().reason)
        assertEquals("Possibly Edited Local", assertNotNull(characters.character(local.id)).name)
        assertEquals(Revision(2), spine.syncMetadata("PC", local.id).revision)
    }

    @Test
    fun staleServerConflictBlocksAutomaticRetryWithoutAdvancingLocalRevision() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val character = CharacterRepository(database).createCharacter(campaign.id, "Simkin")
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata("PC", character.id, SyncMetadata(revision = Revision(2)))
        val outbox = HostedOutboxRepository(database)
        val queued = HostedPcSnapshotQueueService(database, outbox = outbox).queueCurrentSnapshot(
            characterId = character.id,
            createdAtEpochSeconds = 100,
        )
        val delivery = HostedOutboxDeliveryService(
            outbox = outbox,
            pcSnapshotPutter = { _, _, _, _, _ ->
                throw HostedApiException(
                    statusCode = 409,
                    code = HostedApiErrorCode.CONFLICT_STALE_REVISION,
                    message = "Hosted revision advanced.",
                )
            },
            campaignCreator = { _, _, _ -> error("Campaign creation not expected") },
        )

        val report = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 120) }
        val stored = assertNotNull(outbox.mutation(queued.mutation.mutationId))

        assertEquals(HostedOutboxDeliveryReport(1, 0, 0, 1), report)
        assertEquals(HostedRetryState.BLOCKED, stored.retryState)
        assertEquals(HostedApiErrorCode.CONFLICT_STALE_REVISION.name, stored.lastErrorCode)
        assertEquals(Revision(2), spine.syncMetadata("PC", character.id).revision)
        assertTrue(outbox.readyMutations().isEmpty())
    }

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
