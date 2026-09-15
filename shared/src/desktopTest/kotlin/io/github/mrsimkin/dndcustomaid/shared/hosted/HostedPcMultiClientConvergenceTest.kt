package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HostedPcMultiClientConvergenceTest {
    @Test
    fun twoClientsConvergeOfflineThenPreserveConcurrentLocalEdit() {
        val firstDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        val secondDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(firstDriver)
        AppDatabase.Schema.create(secondDriver)

        try {
            val firstDb = AppDatabase(firstDriver)
            val secondDb = AppDatabase(secondDriver)
            val firstCampaigns = CampaignRepository(firstDb)
            val secondCampaigns = CampaignRepository(secondDb)
            val firstCharacters = CharacterRepository(firstDb)
            val secondCharacters = CharacterRepository(secondDb)
            val firstBackups = CharacterBackupRepository(firstDb)
            val secondBackups = CharacterBackupRepository(secondDb)
            val firstSpine = IntegratedSpineRepository(firstDb)
            val secondSpine = IntegratedSpineRepository(secondDb)
            val firstBaselines = HostedPcSyncBaselineRepository(firstDb)
            val secondBaselines = HostedPcSyncBaselineRepository(secondDb)

            val campaign = firstCampaigns.createCampaign("Terramore")
            secondCampaigns.upsertCampaign(campaign.id, campaign.name)
            val firstPc = firstCharacters.createCharacter(campaign.id, "Shared PC")

            var hosted = hostedPc(
                document = firstBackups.exportCharacter(firstPc.id, 100),
                revision = 0,
            )

            // Client A already has the local PC. An equal-revision pull establishes its durable
            // last-synchronized baseline without replacing the local aggregate.
            val firstBaselinePull = pull(firstDb, campaign.id) { hosted }
            assertEquals(listOf(firstPc.id), firstBaselinePull.unchangedPcIds)
            assertEquals(Revision(0), assertNotNull(firstBaselines.baseline(firstPc.id)).revision)

            // D: a fresh second client observes the same hosted campaign/PC identity.
            val secondInitialPull = pull(secondDb, campaign.id) { hosted }
            val secondPc = assertNotNull(secondCharacters.character(firstPc.id))
            assertEquals(listOf(firstPc.id), secondInitialPull.appliedPcIds)
            assertEquals(firstPc.id, secondPc.id)
            assertEquals(campaign.id, secondPc.campaignId)
            assertEquals(Revision(0), assertNotNull(secondBaselines.baseline(firstPc.id)).revision)

            // C: client B edits offline while the server remains unchanged. Pull preserves the
            // local edit at equal revision, then normal outbox delivery succeeds on reconnect.
            secondCharacters.saveCharacter(secondPc.copy(name = "Client B offline edit"))
            val secondEqualPull = pull(secondDb, campaign.id) { hosted }
            assertEquals(listOf(firstPc.id), secondEqualPull.unchangedPcIds)
            assertEquals("Client B offline edit", assertNotNull(secondCharacters.character(firstPc.id)).name)

            hosted = deliverCurrentPc(
                database = secondDb,
                characterId = firstPc.id,
                createdAtEpochSeconds = 200,
                currentHosted = hosted,
            )
            assertEquals(1L, hosted.revision)
            assertEquals("Client B offline edit", hosted.snapshot.character.name)
            assertEquals(Revision(1), secondSpine.syncMetadata("PC", firstPc.id).revision)
            assertEquals(Revision(1), assertNotNull(secondBaselines.baseline(firstPc.id)).revision)

            // A: client A has not changed since its revision-0 baseline, so the newer hosted
            // revision is safe to apply automatically.
            val firstConvergence = pull(firstDb, campaign.id) { hosted }
            assertEquals(listOf(firstPc.id), firstConvergence.appliedPcIds)
            assertEquals("Client B offline edit", assertNotNull(firstCharacters.character(firstPc.id)).name)
            assertEquals(Revision(1), firstSpine.syncMetadata("PC", firstPc.id).revision)
            assertEquals(Revision(1), assertNotNull(firstBaselines.baseline(firstPc.id)).revision)

            // B: both clients now diverge from revision 1. Client B reaches the server first.
            val firstBeforeConflict = assertNotNull(firstCharacters.character(firstPc.id))
            firstCharacters.saveCharacter(firstBeforeConflict.copy(name = "Client A unsent edit"))

            val secondBeforeRemoteAdvance = assertNotNull(secondCharacters.character(firstPc.id))
            secondCharacters.saveCharacter(secondBeforeRemoteAdvance.copy(name = "Client B remote edit"))
            hosted = deliverCurrentPc(
                database = secondDb,
                characterId = firstPc.id,
                createdAtEpochSeconds = 300,
                currentHosted = hosted,
            )
            assertEquals(2L, hosted.revision)
            assertEquals("Client B remote edit", hosted.snapshot.character.name)

            // Client A now sees server revision 2 while its local aggregate differs from the
            // revision-1 baseline. The local edit must survive and the conflict must be explicit.
            val conflictPull = pull(firstDb, campaign.id) { hosted }
            val conflict = conflictPull.conflicts.single()
            assertEquals(HostedPcPullConflictReason.LOCAL_AND_HOSTED_CHANGED, conflict.reason)
            assertEquals(Revision(1), conflict.localRevision)
            assertEquals(Revision(2), conflict.hostedRevision)
            assertEquals("Client A unsent edit", assertNotNull(firstCharacters.character(firstPc.id)).name)
            assertEquals(Revision(1), firstSpine.syncMetadata("PC", firstPc.id).revision)
            assertEquals(Revision(1), assertNotNull(firstBaselines.baseline(firstPc.id)).revision)
        } finally {
            firstDriver.close()
            secondDriver.close()
        }
    }

    private fun pull(
        database: AppDatabase,
        campaignId: kotlin.uuid.Uuid,
        hosted: () -> HostedPcSnapshot,
    ): HostedPcPullResult = runBlocking {
        HostedPcSnapshotPullService(
            database = database,
            snapshotsProvider = { listOf(hosted()) },
        ).refreshCampaign(campaignId)
    }

    private fun deliverCurrentPc(
        database: AppDatabase,
        characterId: kotlin.uuid.Uuid,
        createdAtEpochSeconds: Long,
        currentHosted: HostedPcSnapshot,
    ): HostedPcSnapshot {
        val outbox = HostedOutboxRepository(database)
        val queued = HostedPcSnapshotQueueService(database, outbox = outbox).queueCurrentSnapshot(
            characterId = characterId,
            createdAtEpochSeconds = createdAtEpochSeconds,
            exportedAtEpochSeconds = createdAtEpochSeconds,
        )
        var resultingHosted: HostedPcSnapshot? = null
        val delivery = HostedOutboxDeliveryService(
            outbox = outbox,
            pcSnapshotPutter = { mutationId, campaignId, pcId, expectedRevision, snapshot ->
                assertEquals(queued.mutation.mutationId, mutationId)
                assertEquals(currentHosted.campaignId, campaignId)
                assertEquals(currentHosted.id, pcId)
                assertEquals(currentHosted.revision, expectedRevision)

                val next = hostedPc(snapshot, currentHosted.revision + 1)
                resultingHosted = next
                HostedPcSnapshotPut(pc = next, applied = true)
            },
            campaignCreator = { _, _, _ -> error("Campaign creation is not part of this test.") },
        )

        val report = runBlocking {
            delivery.deliverReady(attemptedAtEpochSeconds = createdAtEpochSeconds + 1)
        }
        assertEquals(HostedOutboxDeliveryReport(1, 1, 0, 0), report)
        return assertNotNull(resultingHosted)
    }

    private fun hostedPc(
        document: CharacterBackupDocument,
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
