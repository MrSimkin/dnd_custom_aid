package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/**
 * Owner's server plan A (2026-11-18): changes travel in groups instead of one request each, so the
 * first upload of a whole package takes seconds and spends a hundred times fewer requests of the
 * free plan. A service that does not know the group route yet keeps working, one by one.
 */
class HostedDmContentGroupSendTest {
    private val dmAccount = Uuid.parse("10000000-0000-4000-8000-000000000901")
    private val campaignId = Uuid.parse("20000000-0000-4000-8000-000000000901")

    @Test
    fun twoHundredAndFiftyChangesTravelInThreeRequests() = scenario { server, device ->
        val creatures = CreatureContentRepository(device.database)
        repeat(250) { creatures.createCampaign(campaignId, "Goblin $it", goblinPayload(), 100) }

        val result = device.sync()

        assertEquals(250, result.pushedIds.size)
        assertEquals(250, server.objects.size)
        assertEquals(0, result.pendingCount)
        assertEquals(listOf(100, 100, 50), server.groupSizes, "one request per group of a hundred")
        assertEquals(0, server.oneByOneCalls, "nothing fell back to one request per object")
    }

    @Test
    fun theProgressStillCountsEveryChangeThatWasSent() = scenario { _, device ->
        val creatures = CreatureContentRepository(device.database)
        repeat(120) { creatures.createCampaign(campaignId, "Goblin $it", goblinPayload(), 100) }

        val steps = mutableListOf<DmContentSyncProgress>()
        device.sync { steps += it }

        val sending = steps.filter { it.step == DmContentSyncStep.SENDING }
        assertEquals(DmContentSyncProgress(DmContentSyncStep.SENDING, 0, 120), sending.first())
        assertEquals(DmContentSyncProgress(DmContentSyncStep.SENDING, 120, 120), sending.last())
    }

    @Test
    fun oneRefusedChangeOfAGroupBlocksOnlyThatObject() = scenario { server, device ->
        val creatures = CreatureContentRepository(device.database)
        val goblins = (0 until 5).map { creatures.createCampaign(campaignId, "Goblin $it", goblinPayload(), 100) }
        server.refuse += goblins[2].item.identity.id

        val result = device.sync()

        assertEquals(4, result.pushedIds.size)
        assertEquals(1, result.conflicts.size)
        assertEquals(goblins[2].item.identity.id, result.conflicts.single().contentId)
        assertEquals(DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED, result.conflicts.single().reason)
        assertEquals(4, server.objects.size)
    }

    @Test
    fun aConnectionThatStopsMidGroupLeavesTheRestInTheQueue() = scenario { server, device ->
        val creatures = CreatureContentRepository(device.database)
        repeat(10) { creatures.createCampaign(campaignId, "Goblin $it", goblinPayload(), 100) }
        server.stopAfter = 4

        val first = device.sync()
        assertTrue(first.offline, "the pass stops and waits")
        assertEquals(4, first.pushedIds.size)
        assertEquals(6, first.pendingCount, "nothing is lost")

        server.stopAfter = null
        val second = device.sync()
        assertEquals(6, second.pushedIds.size)
        assertEquals(0, second.pendingCount)
        assertEquals(10, server.objects.size)
    }

    @Test
    fun aServiceWithoutTheGroupRouteStillReceivesEveryChange() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { AppDatabase.Schema.create(it) }
        try {
            val database = AppDatabase(driver)
            IntegratedSpineRepository(database).upsertAccount(AccountIdentity(dmAccount, displayName = "DM"))
            CampaignRepository(database).upsertCampaign(campaignId, "Terramore")
            val creatures = CreatureContentRepository(database)
            repeat(3) { creatures.createCampaign(campaignId, "Goblin $it", goblinPayload(), 100) }
            // This stand-in only knows the old routes, exactly like the service in production today.
            val server = OneByOneServer()
            val result = runBlocking {
                HostedDmContentSyncService(database, server).synchronize(
                    listOf(DmContentSyncScope.Personal(dmAccount), DmContentSyncScope.Campaign(campaignId)),
                    1_000,
                )
            }
            assertEquals(3, result.pushedIds.size)
            assertEquals(3, server.puts)
        } finally {
            driver.close()
        }
    }

    private fun scenario(block: (GroupingServer, Device) -> Unit) {
        val server = GroupingServer()
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { AppDatabase.Schema.create(it) }
        try {
            val database = AppDatabase(driver)
            IntegratedSpineRepository(database).upsertAccount(AccountIdentity(dmAccount, displayName = "DM"))
            CampaignRepository(database).upsertCampaign(campaignId, "Terramore")
            block(server, Device(database, server, listOf(DmContentSyncScope.Personal(dmAccount), DmContentSyncScope.Campaign(campaignId))))
        } finally {
            driver.close()
        }
    }

    private class Device(val database: AppDatabase, private val server: HostedDmContentTransport, private val scopes: List<DmContentSyncScope>) {
        private var clock = 1_000L
        fun sync(onProgress: (DmContentSyncProgress) -> Unit = {}): DmContentSyncResult = runBlocking {
            HostedDmContentSyncService(database, server).synchronize(scopes, clock++, onProgress)
        }
    }

    private fun goblinPayload(): CreaturePayload = CreaturePayload(armorClass = 15, hitPoints = 7)
}

/** Stand-in for the updated Worker: it answers a whole group in one call. */
private class GroupingServer : HostedDmContentTransport {
    val objects = linkedMapOf<Uuid, HostedDmContent>()
    val groupSizes = mutableListOf<Int>()
    var oneByOneCalls = 0
    val refuse = mutableSetOf<Uuid>()

    /** How many items of the next group reach the service before the connection stops. */
    var stopAfter: Int? = null

    override suspend fun sendDmContent(items: List<HostedDmContentSendItem>): List<HostedDmContentSendResult> {
        groupSizes += items.size
        val limit = stopAfter ?: items.size
        return items.take(limit).map { item ->
            if (item.contentId in refuse) {
                HostedDmContentSendResult.Rejected(HostedApiErrorCode.CONFLICT_STALE_REVISION, "stale")
            } else {
                val snapshot = requireNotNull(item.snapshot)
                objects[item.contentId] = HostedDmContent(
                    id = item.contentId,
                    scopeKind = snapshot.content.scope.kind,
                    scopeRef = snapshot.content.scope.ref,
                    family = snapshot.content.family,
                    displayName = snapshot.content.displayName,
                    revision = item.expectedRevision + 1,
                    snapshotFormat = snapshot.format,
                    snapshotVersion = snapshot.version,
                    snapshot = hostedWireJson().encodeToJsonElement(DmContentSnapshotDocument.serializer(), snapshot).jsonObject,
                )
                HostedDmContentSendResult.Sent(item.expectedRevision + 1)
            }
        }
    }

    override suspend fun campaignDmContent(campaignId: Uuid): List<HostedDmContent> =
        objects.values.filter { it.scopeKind == "CAMPAIGN" && it.scopeRef == campaignId }

    override suspend fun personalDmContent(): List<HostedDmContent> = objects.values.filter { it.scopeKind == "PERSONAL" }

    override suspend fun putDmContent(
        mutationId: Uuid,
        contentId: Uuid,
        expectedRevision: Long,
        snapshot: DmContentSnapshotDocument,
    ): HostedDmContentMutation {
        oneByOneCalls += 1
        error("This stand-in groups every change.")
    }

    override suspend fun deleteDmContent(mutationId: Uuid, contentId: Uuid, expectedRevision: Long): HostedDmContentMutation {
        oneByOneCalls += 1
        error("This stand-in groups every change.")
    }

    override suspend fun putMediaContent(sha256: String, mediaType: String, bytes: ByteArray) = Unit

    override suspend fun mediaContent(sha256: String, contentId: Uuid): ByteArray =
        throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing media")
}

/** Stand-in for a service that only knows the one-object routes. */
private class OneByOneServer : HostedDmContentTransport {
    var puts = 0
    private val objects = linkedMapOf<Uuid, HostedDmContent>()

    override suspend fun campaignDmContent(campaignId: Uuid): List<HostedDmContent> =
        objects.values.filter { it.scopeKind == "CAMPAIGN" && it.scopeRef == campaignId }

    override suspend fun personalDmContent(): List<HostedDmContent> = objects.values.filter { it.scopeKind == "PERSONAL" }

    override suspend fun putDmContent(
        mutationId: Uuid,
        contentId: Uuid,
        expectedRevision: Long,
        snapshot: DmContentSnapshotDocument,
    ): HostedDmContentMutation {
        puts += 1
        val stored = HostedDmContent(
            id = contentId,
            scopeKind = snapshot.content.scope.kind,
            scopeRef = snapshot.content.scope.ref,
            family = snapshot.content.family,
            displayName = snapshot.content.displayName,
            revision = expectedRevision + 1,
            snapshotFormat = snapshot.format,
            snapshotVersion = snapshot.version,
            snapshot = hostedWireJson().encodeToJsonElement(DmContentSnapshotDocument.serializer(), snapshot).jsonObject,
        )
        objects[contentId] = stored
        return HostedDmContentMutation(stored, applied = true)
    }

    override suspend fun deleteDmContent(mutationId: Uuid, contentId: Uuid, expectedRevision: Long): HostedDmContentMutation =
        throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing")

    override suspend fun putMediaContent(sha256: String, mediaType: String, bytes: ByteArray) = Unit

    override suspend fun mediaContent(sha256: String, contentId: Uuid): ByteArray =
        throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing media")
}
