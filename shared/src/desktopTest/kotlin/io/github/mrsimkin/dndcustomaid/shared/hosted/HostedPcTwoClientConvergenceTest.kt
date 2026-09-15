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

class HostedPcTwoClientConvergenceTest {
    @Test
    fun secondClientPreservesOfflineEditWhenFirstClientAdvancesHostedRevision() {
        val firstDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        val secondDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(firstDriver)
        AppDatabase.Schema.create(secondDriver)
        try {
            val clientA = AppDatabase(firstDriver)
            val clientB = AppDatabase(secondDriver)
            val campaignsA = CampaignRepository(clientA)
            val campaignsB = CampaignRepository(clientB)
            val charactersA = CharacterRepository(clientA)
            val charactersB = CharacterRepository(clientB)
            val backupsA = CharacterBackupRepository(clientA)
            val spineA = IntegratedSpineRepository(clientA)

            val campaign = campaignsA.createCampaign("Two Client Test")
            campaignsB.upsertCampaign(campaign.id, campaign.name)
            val pcA = charactersA.createCharacter(campaign.id, "Initial Hosted Name")
            val initialSnapshot = backupsA.exportCharacter(pcA.id, 100)
            spineA.putSyncMetadata("PC", pcA.id, SyncMetadata(revision = Revision(1)))
            HostedPcSyncBaselineRepository(clientA).put(pcA.id, 1, initialSnapshot)

            var hosted = hostedPc(initialSnapshot, revision = 1)

            // Client B starts with no local PC and observes the exact same stable hosted identity.
            val initialBPull = runBlocking {
                HostedPcSnapshotPullService(
                    database = clientB,
                    snapshotsProvider = { listOf(hosted) },
                ).refreshCampaign(campaign.id)
            }
            assertEquals(listOf(pcA.id), initialBPull.appliedPcIds)
            val pcB = assertNotNull(charactersB.character(pcA.id))
            assertEquals("Initial Hosted Name", pcB.name)
            assertEquals(1L, assertNotNull(HostedPcSyncBaselineRepository(clientB).baseline(pcA.id)).revision)

            // Client B changes locally and remains unsynchronized/offline.
            charactersB.saveCharacter(pcB.copy(name = "Client B Offline Edit"))

            // Client A independently edits and advances the hosted revision to 2.
            charactersA.saveCharacter(pcA.copy(name = "Client A Hosted Edit"))
            val outboxA = HostedOutboxRepository(clientA)
            val queuedA = HostedPcSnapshotQueueService(clientA, outbox = outboxA).queueCurrentSnapshot(
                characterId = pcA.id,
                createdAtEpochSeconds = 200,
            )
            val deliveryA = HostedOutboxDeliveryService(
                outbox = outboxA,
                campaignCreator = { _, _, _ -> error("Campaign creation not expected") },
                pcSnapshotPutter = { _, campaignId, pcId, expectedRevision, snapshot ->
                    assertEquals(1L, expectedRevision)
                    hosted = hostedPc(snapshot, revision = 2)
                    HostedPcSnapshotPut(pc = hosted, applied = true)
                },
            )
            val deliveryReport = runBlocking { deliveryA.deliverReady(attemptedAtEpochSeconds = 210) }
            assertEquals(HostedOutboxDeliveryReport(1, 1, 0, 0), deliveryReport)
            assertNull(outboxA.mutation(queuedA.mutation.mutationId))
            assertEquals(Revision(2), spineA.syncMetadata("PC", pcA.id).revision)
            assertEquals("Client A Hosted Edit", hosted.snapshot.character.name)

            // Client B reconnects after the server moved. Its old baseline proves that B also
            // changed locally, so hosted state must be reported as a conflict rather than applied.
            val reconnectB = runBlocking {
                HostedPcSnapshotPullService(
                    database = clientB,
                    snapshotsProvider = { listOf(hosted) },
                ).refreshCampaign(campaign.id)
            }

            assertEquals(1, reconnectB.conflicts.size)
            assertEquals(HostedPcPullConflictReason.LOCAL_UNSENT_CHANGES, reconnectB.conflicts.single().reason)
            assertEquals("Client B Offline Edit", assertNotNull(charactersB.character(pcA.id)).name)
            assertEquals(Revision(1), IntegratedSpineRepository(clientB).syncMetadata("PC", pcA.id).revision)
            assertEquals(1L, assertNotNull(HostedPcSyncBaselineRepository(clientB).baseline(pcA.id)).revision)
        } finally {
            firstDriver.close()
            secondDriver.close()
        }
    }

    private fun hostedPc(
        document: io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument,
        revision: Long,
    ): HostedPcSnapshot = HostedPcSnapshot(
        id = document.character.id,
        campaignId = document.character.campaignId,
        ownerUserId = null,
        controllerUserId = null,
        name = document.character.name,
        revision = revision,
        snapshotFormat = document.format,
        snapshotVersion = document.version,
        snapshot = document,
    )
}
