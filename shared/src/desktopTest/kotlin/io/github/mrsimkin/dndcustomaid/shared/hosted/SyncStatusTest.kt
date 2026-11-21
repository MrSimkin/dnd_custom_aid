package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/**
 * Owner's D (2026-11-20): one «Sincronizar», and a status that says what is still waiting instead
 * of leaving the DM guessing.
 */
class SyncStatusTest {
    private val account = Uuid.parse("10000000-0000-4000-8000-000000000d01")

    private fun scenario(block: (AppDatabase, Uuid) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        IntegratedSpineRepository(database).upsertAccount(AccountIdentity(account, displayName = "DM"))
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        try { block(database, campaign.id) } finally { driver.close() }
    }

    @Test
    fun withNothingWaitingItSaysSoAndWhenItLastRan() = scenario { database, _ ->
        val status = SyncStatusReader(database).status(lastSyncEpochSeconds = 1_000)

        assertTrue(status.nothingToDo)
        assertEquals("Todo al día. Última sincronización: hace 5 min.", status.spanishSummary(nowEpochSeconds = 1_300))
        assertEquals("Todo al día en este aparato. Aún no has sincronizado.", SyncStatusReader(database).status(null).spanishSummary())
    }

    @Test
    fun itCountsWhatIsWaitingOfEachKind() = scenario { database, campaignId ->
        // A PC change of this device.
        val characters = CharacterRepository(database)
        val pc = characters.createCharacter(campaignId, "Arruk")
        HostedPcSnapshotQueueService(
            database = database,
            backups = CharacterBackupRepository(database),
            spine = IntegratedSpineRepository(database),
            outbox = HostedOutboxRepository(database),
        ).queueCurrentSnapshot(pc.id, createdAtEpochSeconds = 100)

        // Prepared content that has not travelled yet.
        val npc = NpcContentRepository(database).createCampaign(campaignId, "Barek Surn", NpcPayload(), 100)
        HostedDmContentSyncService(database, NoServer()).queueLocalChanges(DmContentSyncScope.Campaign(campaignId), 200)

        // A difference waiting for the DM (owner's B).
        database.dmContentConflictQueries.upsertDmContentConflict(
            content_id = npc.item.identity.id.toString(),
            reason = DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED.name,
            hosted_revision = 2,
            hosted_snapshot = "{}",
            hosted_deleted = 0,
            seen_at_epoch_seconds = 300,
        )

        val status = SyncStatusReader(database).status(lastSyncEpochSeconds = 400, missingImages = 2)

        assertEquals(1, status.pendingPcChanges)
        assertTrue(status.pendingDmChanges >= 1, "the prepared content is queued: ${status.pendingDmChanges}")
        assertEquals(1, status.differencesToDecide)
        assertEquals(2, status.missingImages)
        assertTrue(!status.nothingToDo)
        val summary = status.spanishSummary(nowEpochSeconds = 500)
        assertTrue(summary.contains("personajes esperando"), summary)
        assertTrue(summary.contains("diferencia"), summary)
        assertTrue(summary.contains("imagen"), summary)
    }
}

/** Nothing of this test talks to a server. */
private class NoServer : HostedDmContentTransport {
    override suspend fun campaignDmContent(campaignId: Uuid): List<HostedDmContent> = emptyList()
    override suspend fun personalDmContent(): List<HostedDmContent> = emptyList()
    override suspend fun putDmContent(
        mutationId: Uuid,
        contentId: Uuid,
        expectedRevision: Long,
        snapshot: DmContentSnapshotDocument,
    ): HostedDmContentMutation = throw HostedApiException(503, HostedApiErrorCode.TRANSIENT_FAILURE, "offline")
    override suspend fun deleteDmContent(mutationId: Uuid, contentId: Uuid, expectedRevision: Long): HostedDmContentMutation =
        throw HostedApiException(503, HostedApiErrorCode.TRANSIENT_FAILURE, "offline")
    override suspend fun putMediaContent(sha256: String, mediaType: String, bytes: ByteArray) = Unit
    override suspend fun mediaContent(sha256: String, contentId: Uuid): ByteArray =
        throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing")
}
