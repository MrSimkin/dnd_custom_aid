package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
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
 * Owner's B (2026-11-20), the rule chosen from the 2026-11-14 proposal: changed on one device, that
 * change wins; changed on both, nothing is overwritten and the DM decides.
 */
class DmContentConflictResolutionTest {
    private val dmAccount = Uuid.parse("10000000-0000-4000-8000-000000000b01")
    private val campaignId = Uuid.parse("20000000-0000-4000-8000-000000000b01")

    @Test
    fun oneDeviceChangingItNeedsNoQuestion() = twoDevices { server, pc, tablet ->
        val id = pc.createNpc("Barek Surn", "Capataz")
        pc.sync()
        tablet.sync()

        pc.editNpc(id, "Capataz del turno de noche")
        pc.sync()
        val received = tablet.sync()

        assertEquals(listOf(id), received.appliedIds)
        assertTrue(received.conflicts.isEmpty())
        assertEquals("Capataz del turno de noche", tablet.npc(id)?.payload?.conceptRole)
        assertEquals(0, DmContentConflictResolution(tablet.database).count())
        assertEquals(1, server.objects.size)
    }

    @Test
    fun changedOnBothNothingIsOverwrittenAndTheTwoVersionsAreThere() = twoDevices { _, pc, tablet ->
        val id = pc.createNpc("Barek Surn", "Capataz")
        pc.sync()
        tablet.sync()

        pc.editNpc(id, "Capataz del turno de noche")
        pc.sync()
        tablet.editNpc(id, "Capataz de la Caseta")
        val result = tablet.sync()

        assertEquals(1, result.conflicts.size)
        assertEquals(DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED, result.conflicts.single().reason)
        assertEquals("Capataz de la Caseta", tablet.npc(id)?.payload?.conceptRole, "nothing was overwritten")

        val difference = DmContentConflictResolution(tablet.database).differences().single()
        assertEquals(id, difference.contentId)
        assertEquals("Barek Surn", difference.theirsName)
        assertTrue(difference.differingFields.contains("Concepto"), difference.differingFields.toString())
        assertEquals(
            "Capataz del turno de noche",
            difference.theirsLines.toMap()["Concepto"],
            "the other device's version is kept to compare",
        )
    }

    @Test
    fun keepingMineSendsItOverTheOtherOne() = twoDevices { server, pc, tablet ->
        val id = pc.createNpc("Barek Surn", "Capataz")
        pc.sync(); tablet.sync()
        pc.editNpc(id, "Capataz del turno de noche"); pc.sync()
        tablet.editNpc(id, "Capataz de la Caseta"); tablet.sync()

        DmContentConflictResolution(tablet.database).resolve(id, DmContentChoice.MINE, nowEpochSeconds = 900)
        val after = tablet.sync()

        assertEquals(0, DmContentConflictResolution(tablet.database).count())
        assertTrue(after.conflicts.isEmpty(), after.conflicts.toString())
        assertEquals("Capataz de la Caseta", tablet.npc(id)?.payload?.conceptRole)
        assertEquals(id, after.pushedIds.singleOrNull(), "mine travelled to the server")
        pc.sync()
        assertEquals("Capataz de la Caseta", pc.npc(id)?.payload?.conceptRole, "and reached the other device")
        assertEquals(1, server.objects.size)
    }

    @Test
    fun keepingTheirsReplacesWhatWasHere() = twoDevices { _, pc, tablet ->
        val id = pc.createNpc("Barek Surn", "Capataz")
        pc.sync(); tablet.sync()
        pc.editNpc(id, "Capataz del turno de noche"); pc.sync()
        tablet.editNpc(id, "Capataz de la Caseta"); tablet.sync()

        DmContentConflictResolution(tablet.database).resolve(id, DmContentChoice.THEIRS, nowEpochSeconds = 900)
        val after = tablet.sync()

        assertEquals("Capataz del turno de noche", tablet.npc(id)?.payload?.conceptRole)
        assertEquals(0, DmContentConflictResolution(tablet.database).count())
        assertTrue(after.conflicts.isEmpty())
        assertTrue(after.pushedIds.isEmpty(), "taking theirs sends nothing")
    }

    @Test
    fun keepingBothLeavesTheirsAndMineSideBySide() = twoDevices { _, pc, tablet ->
        val id = pc.createNpc("Barek Surn", "Capataz")
        pc.sync(); tablet.sync()
        pc.editNpc(id, "Capataz del turno de noche"); pc.sync()
        tablet.editNpc(id, "Capataz de la Caseta"); tablet.sync()

        DmContentConflictResolution(tablet.database).resolve(id, DmContentChoice.BOTH, nowEpochSeconds = 900)

        val npcs = ReusableContentRepository(tablet.database).listCampaign(campaignId, ReusableContentFamily.NPC)
        assertEquals(2, npcs.size)
        assertEquals("Capataz del turno de noche", tablet.npc(id)?.payload?.conceptRole)
        val copy = npcs.single { it.identity.id != id }
        assertEquals("Barek Surn (mi versión)", copy.displayName)
        assertEquals("Capataz de la Caseta", tablet.npc(copy.identity.id)?.payload?.conceptRole)
        assertEquals(0, DmContentConflictResolution(tablet.database).count())
    }

    // ------------------------------------------------------------------ two devices, one server

    private class Device(val database: AppDatabase, private val server: FakeConflictServer, private val campaignId: Uuid) {
        private var clock = 1_000L
        private val npcs = NpcContentRepository(database)

        fun createNpc(name: String, concept: String): Uuid =
            npcs.createCampaign(campaignId, name, NpcPayload(conceptRole = concept), clock++).item.identity.id

        fun editNpc(id: Uuid, concept: String) {
            val current = requireNotNull(npcs.npc(id))
            npcs.update(id, current.item.identity.revision, current.item.displayName, current.payload.copy(conceptRole = concept), clock++)
        }

        fun npc(id: Uuid) = npcs.npc(id)

        fun sync(): DmContentSyncResult = runBlocking {
            HostedDmContentSyncService(database, server).synchronize(listOf(DmContentSyncScope.Campaign(campaignId)), clock++)
        }
    }

    private fun twoDevices(block: (FakeConflictServer, Device, Device) -> Unit) {
        val server = FakeConflictServer()
        val drivers = List(2) { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { AppDatabase.Schema.create(it) } }
        try {
            val devices = drivers.map { driver ->
                val database = AppDatabase(driver)
                IntegratedSpineRepository(database).upsertAccount(AccountIdentity(dmAccount, displayName = "DM"))
                CampaignRepository(database).upsertCampaign(campaignId, "Serpiente Vieja")
                Device(database, server, campaignId)
            }
            block(server, devices[0], devices[1])
        } finally {
            drivers.forEach { it.close() }
        }
    }
}

/** A server that keeps one revision per object and refuses a change made on top of an old one. */
private class FakeConflictServer : HostedDmContentTransport {
    val objects = linkedMapOf<Uuid, HostedDmContent>()

    override suspend fun campaignDmContent(campaignId: Uuid): List<HostedDmContent> =
        objects.values.filter { it.scopeKind == "CAMPAIGN" && it.scopeRef == campaignId }

    override suspend fun personalDmContent(): List<HostedDmContent> = emptyList()

    override suspend fun putDmContent(
        mutationId: Uuid,
        contentId: Uuid,
        expectedRevision: Long,
        snapshot: DmContentSnapshotDocument,
    ): HostedDmContentMutation {
        val existing = objects[contentId]
        val revision = when {
            existing == null && expectedRevision == 0L -> 0L
            existing == null -> throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing")
            existing.revision != expectedRevision ->
                throw HostedApiException(409, HostedApiErrorCode.CONFLICT_STALE_REVISION, "stale")
            else -> existing.revision + 1
        }
        val stored = HostedDmContent(
            id = contentId,
            scopeKind = snapshot.content.scope.kind,
            scopeRef = snapshot.content.scope.ref,
            family = snapshot.content.family,
            displayName = snapshot.content.displayName,
            revision = revision,
            snapshotFormat = snapshot.format,
            snapshotVersion = snapshot.version,
            snapshot = hostedWireJson().encodeToJsonElement(DmContentSnapshotDocument.serializer(), snapshot).jsonObject,
        )
        objects[contentId] = stored
        return HostedDmContentMutation(stored, applied = true)
    }

    override suspend fun deleteDmContent(mutationId: Uuid, contentId: Uuid, expectedRevision: Long): HostedDmContentMutation {
        val existing = objects[contentId] ?: throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing")
        if (existing.revision != expectedRevision) throw HostedApiException(409, HostedApiErrorCode.CONFLICT_STALE_REVISION, "stale")
        val stored = existing.copy(revision = existing.revision + 1, deletedAtEpochSeconds = 5_000)
        objects[contentId] = stored
        return HostedDmContentMutation(stored, applied = true)
    }

    override suspend fun putMediaContent(sha256: String, mediaType: String, bytes: ByteArray) = Unit

    override suspend fun mediaContent(sha256: String, contentId: Uuid): ByteArray =
        throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing media")
}
