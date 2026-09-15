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

class HostedPcOfflineReconnectTest {
    @Test
    fun offlineLocalEditDeliversAfterReconnectWhenHostedRevisionDidNotAdvance() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        val local = characters.createCharacter(campaign.id, "Original")
        val backups = CharacterBackupRepository(database)
        val spine = IntegratedSpineRepository(database)
        val baselines = HostedPcSyncBaselineRepository(database)
        val outbox = HostedOutboxRepository(database)

        val hostedAtDisconnect = backups.exportCharacter(local.id, 100)
        spine.putSyncMetadata("PC", local.id, SyncMetadata(revision = Revision(1)))
        baselines.put(local.id, 1, hostedAtDisconnect)

        // The device is conceptually offline here: only the authoritative local repository changes.
        characters.saveCharacter(local.copy(name = "Offline Local Edit"))

        // Reconnect first observes the same hosted revision. Equal-revision pull must not overwrite
        // the local edit, while retaining the known hosted baseline for change detection.
        val pull = HostedPcSnapshotPullService(
            database = database,
            snapshotsProvider = {
                listOf(
                    HostedPcSnapshot(
                        id = local.id,
                        campaignId = campaign.id,
                        ownerUserId = null,
                        controllerUserId = null,
                        name = hostedAtDisconnect.character.name,
                        revision = 1,
                        snapshotFormat = hostedAtDisconnect.format,
                        snapshotVersion = hostedAtDisconnect.version,
                        snapshot = hostedAtDisconnect,
                    ),
                )
            },
        )
        val pullResult = runBlocking { pull.refreshCampaign(campaign.id) }

        assertEquals(listOf(local.id), pullResult.unchangedPcIds)
        assertEquals("Offline Local Edit", assertNotNull(characters.character(local.id)).name)

        val queued = HostedPcSnapshotQueueService(database, outbox = outbox).queueCurrentSnapshot(
            characterId = local.id,
            createdAtEpochSeconds = 200,
        )
        val delivery = HostedOutboxDeliveryService(
            outbox = outbox,
            campaignCreator = { _, _, _ -> error("Campaign creation not expected") },
            pcSnapshotPutter = { mutationId, campaignId, pcId, expectedRevision, snapshot ->
                assertEquals(queued.mutation.mutationId, mutationId)
                assertEquals(campaign.id, campaignId)
                assertEquals(local.id, pcId)
                assertEquals(1L, expectedRevision)
                assertEquals("Offline Local Edit", snapshot.character.name)
                HostedPcSnapshotPut(
                    pc = HostedPcSnapshot(
                        id = pcId,
                        campaignId = campaignId,
                        ownerUserId = null,
                        controllerUserId = null,
                        name = snapshot.character.name,
                        revision = 2,
                        snapshotFormat = snapshot.format,
                        snapshotVersion = snapshot.version,
                        snapshot = snapshot,
                    ),
                    applied = true,
                )
            },
        )

        val deliveryResult = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 210) }

        assertEquals(HostedOutboxDeliveryReport(1, 1, 0, 0), deliveryResult)
        assertNull(outbox.mutation(queued.mutation.mutationId))
        assertEquals(Revision(2), spine.syncMetadata("PC", local.id).revision)
        val newBaseline = assertNotNull(baselines.baseline(local.id))
        assertEquals(2L, newBaseline.revision)
        assertEquals("Offline Local Edit", newBaseline.snapshot.character.name)
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
