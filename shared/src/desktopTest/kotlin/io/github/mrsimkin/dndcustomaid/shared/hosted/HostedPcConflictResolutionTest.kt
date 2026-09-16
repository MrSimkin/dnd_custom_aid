package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
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

class HostedPcConflictResolutionTest {
    @Test
    fun explicitKeepLocalQueuesReviewedHostedRevisionAndAcknowledgesResultingRevision() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val character = CharacterRepository(database).createCharacter(campaign.id, "Local Edited PC")
        val spine = IntegratedSpineRepository(database)
        spine.putSyncMetadata("PC", character.id, SyncMetadata(revision = Revision(7)))
        val outbox = HostedOutboxRepository(database)

        val queued = HostedPcConflictResolutionQueueService(
            database = database,
            outbox = outbox,
        ).queueLocalSnapshotAgainstHostedRevision(
            characterId = character.id,
            expectedHostedRevision = Revision(8),
            createdAtEpochSeconds = 100,
            exportedAtEpochSeconds = 90,
        )
        val payload = outbox.pcSnapshotPayload(queued.mutation)

        assertEquals(8L, payload.expectedRevision)
        assertEquals(Revision(7), spine.syncMetadata("PC", character.id).revision)

        val delivery = HostedOutboxDeliveryService(
            outbox = outbox,
            pcSnapshotPutter = { mutationId, campaignId, pcId, expectedRevision, snapshot ->
                assertEquals(queued.mutation.mutationId, mutationId)
                assertEquals(campaign.id, campaignId)
                assertEquals(character.id, pcId)
                assertEquals(8L, expectedRevision)
                HostedPcSnapshotPut(
                    pc = HostedPcSnapshot(
                        id = pcId,
                        campaignId = campaignId,
                        ownerUserId = null,
                        controllerUserId = null,
                        name = snapshot.character.name,
                        revision = 9,
                        snapshotFormat = snapshot.format,
                        snapshotVersion = snapshot.version,
                        snapshot = snapshot,
                    ),
                    applied = true,
                )
            },
            campaignCreator = { _, _, _ -> error("Campaign creation not expected") },
        )

        val report = runBlocking { delivery.deliverReady(attemptedAtEpochSeconds = 110) }
        val baseline = assertNotNull(HostedPcSyncBaselineRepository(database).baseline(character.id))

        assertEquals(HostedOutboxDeliveryReport(1, 1, 0, 0), report)
        assertNull(outbox.mutation(queued.mutation.mutationId))
        assertEquals(Revision(9), spine.syncMetadata("PC", character.id).revision)
        assertEquals(Revision(9), baseline.revision)
        assertEquals(normalizePcSyncSnapshot(payload.snapshot), baseline.snapshot)
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
