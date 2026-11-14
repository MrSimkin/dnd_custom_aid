package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContextKind
import io.github.mrsimkin.dndcustomaid.shared.content.NotePayload
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ShopItem
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutKind
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutPayload
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutVisibility
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRuleContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.sha256Hex
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRulePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class HostedDmContentSyncTest {
    private val dmAccount = Uuid.parse("10000000-0000-4000-8000-000000000801")
    private val campaignId = Uuid.parse("20000000-0000-4000-8000-000000000801")

    @Test
    fun appDePcContentReachesTheTabletWithPayloadScopeAndProvenance() = scenario { server, pc, tablet ->
        val creatures = CreatureContentRepository(pc.database)
        val goblin = creatures.createPersonal(dmAccount, "Goblin", goblinPayload(), 100)
        val campaignGoblin = creatures.copyPersonalToCampaign(goblin.item.identity.id, campaignId, 110)
        val rule = HomebrewRuleContentRepository(pc.database).createCampaign(
            campaignId, "Pociones como acción adicional", HomebrewRulePayload(summary = "Beber es acción adicional."), 120,
        )
        val ambush = EncounterContentRepository(pc.database).createCampaign(
            campaignId,
            "Emboscada",
            EncounterPayload(participants = listOf(EncounterParticipant(sourceContentId = campaignGoblin.item.identity.id, quantity = 4))),
            130,
        )

        val pushed = pc.sync()
        assertEquals(4, pushed.pushedIds.size)
        assertEquals(0, pushed.pendingCount)
        assertEquals(4, server.objects.size)

        val received = tablet.sync()
        assertEquals(4, received.appliedIds.size)
        assertTrue(received.conflicts.isEmpty())

        val tabletCreatures = CreatureContentRepository(tablet.database)
        val tabletCopy = assertNotNull(tabletCreatures.creature(campaignGoblin.item.identity.id))
        assertEquals(goblinPayload(), tabletCopy.payload)
        assertEquals(ContentScope.Campaign(campaignId), tabletCopy.item.identity.scope)
        assertEquals(goblin.item.identity.id, tabletCopy.item.identity.provenance?.sourceObjectId)
        assertEquals(ContentScope.Personal(dmAccount), tabletCopy.item.identity.provenance?.sourceScope)
        assertNotNull(tabletCreatures.creature(goblin.item.identity.id))
        assertEquals(
            "Beber es acción adicional.",
            HomebrewRuleContentRepository(tablet.database).rule(rule.item.identity.id)?.payload?.summary,
        )
        assertEquals(
            campaignGoblin.item.identity.id,
            EncounterContentRepository(tablet.database).encounter(ambush.item.identity.id)?.payload?.participants?.single()?.sourceContentId,
        )

        // A second pass with no edits sends and changes nothing.
        val quietPc = pc.sync()
        val quietTablet = tablet.sync()
        assertTrue(quietPc.pushedIds.isEmpty() && quietPc.appliedIds.isEmpty())
        assertTrue(quietTablet.pushedIds.isEmpty() && quietTablet.appliedIds.isEmpty())
        assertEquals(4, quietTablet.unchangedCount)
    }

    @Test
    fun structuredLinksTravelWithTheirContentAndALinkEditIsAnEdit() = scenario { _, pc, tablet ->
        val shop = PlaceContentRepository(pc.database).createCampaign(campaignId, "Herrería", PlacePayload(kind = PlaceKind.SHOP), 100)
        val smith = NpcContentRepository(pc.database).createCampaign(campaignId, "Bruna", NpcPayload(conceptRole = "Herrera"), 100)
        val shopId = shop.item.identity.id
        val smithId = smith.item.identity.id
        val pcLinks = ContentLinksRepository(pc.database)
        pcLinks.save(smithId, ContentLinks(placeIds = listOf(shopId), groups = listOf(" Gremio ", "gremio")))
        pcLinks.save(shopId, ContentLinks(npcIds = listOf(smithId), shopItems = listOf(ShopItem("Cota de malla", "75 po", highlighted = true))))

        assertEquals(2, pc.sync().pushedIds.size)
        assertEquals(2, tablet.sync().appliedIds.size)
        val tabletLinks = ContentLinksRepository(tablet.database)
        assertEquals(ContentLinks(placeIds = listOf(shopId), groups = listOf("Gremio")), tabletLinks.links(smithId), "groups arrive cleaned")
        assertEquals("75 po", tabletLinks.links(shopId).shopItems.single().price)

        pcLinks.save(shopId, pcLinks.links(shopId).copy(shopItems = listOf(ShopItem("Cota de malla", "70 po"))))
        assertEquals(listOf(shopId), pc.sync().pushedIds, "changing only a link sends the place again")
        tablet.sync()
        assertEquals("70 po", tabletLinks.links(shopId).shopItems.single().price)
        assertTrue(tablet.sync().appliedIds.isEmpty() && pc.sync().pushedIds.isEmpty(), "nothing moves without edits")
    }

    @Test
    fun aDungeonDrawnOnTheAppDePcReachesTheTabletWithItsZonesAndPassages() = scenario { _, pc, tablet ->
        val dungeons = io.github.mrsimkin.dndcustomaid.shared.content.DungeonContentRepository(pc.database)
        val site = dungeons.create(campaignId, "Sitio 021", io.github.mrsimkin.dndcustomaid.shared.content.DungeonPayload(summary = "Complejo de extracción"), 500)
        val a1 = dungeons.addZone(campaignId, site, "A1 Explanada", 0.1, 0.1, 500)
        val a2 = dungeons.addZone(campaignId, site, "A2 Caseta", 0.5, 0.1, 500)
        dungeons.connect(a1.item.identity.id, a2.item.identity.id, secret = true)
        pc.sync()
        tablet.sync()
        val onTablet = io.github.mrsimkin.dndcustomaid.shared.content.DungeonContentRepository(tablet.database)
        val received = onTablet.listCampaign(campaignId).single()
        assertEquals("Sitio 021", received.item.displayName)
        assertEquals("Complejo de extracción", received.payload.summary)
        val zones = onTablet.zonesOf(campaignId, received)
        assertEquals(listOf("A1 Explanada", "A2 Caseta"), zones.map { it.zone.displayName })
        val passage = zones.first().links.passages.single()
        assertEquals(a2.item.identity.id, passage.toZoneId)
        assertTrue(passage.secret, "the secret passage keeps its mark")
        assertEquals("Sitio 021", zones.first().links.dungeon, "the tablet's Mesa de Dungeon still finds it by name")
    }

    @Test
    fun aLiveNoteWrittenOnTheTabletReachesTheAppDePcWithItsContext() = scenario { _, pc, tablet ->
        val zoneId = Uuid.random()
        val note = NoteContentRepository(tablet.database).create(
            campaignId,
            NotePayload("La puerta norte quedó atrancada.\nVolver con palanca.", NoteContextKind.ZONE, zoneId, "Antesala"),
            500,
        )
        assertEquals("La puerta norte quedó atrancada.", note.item.displayName, "a note is named by its first line")
        assertEquals(listOf(note.item.identity.id), tablet.sync().pushedIds)
        pc.sync()
        val onPc = NoteContentRepository(pc.database).listCampaign(campaignId).single()
        assertEquals(NoteContextKind.ZONE, onPc.payload.contextKind)
        assertEquals("Antesala", onPc.payload.contextLabel)
        assertEquals(zoneId, onPc.payload.contextId)
    }

    @Test
    fun editsAndDeletesTravelAndDeletedContentNeverComesBack() = scenario { server, pc, tablet ->
        val creatures = CreatureContentRepository(pc.database)
        val owlbear = creatures.createCampaign(campaignId, "Oso lechuza", goblinPayload(), 100)
        val id = owlbear.item.identity.id
        pc.sync()
        tablet.sync()
        val tabletRevisionBefore = CreatureContentRepository(tablet.database).creature(id)!!.item.identity.revision

        assertIs<RevisionDecision.Accepted>(
            creatures.update(id, Revision(0), "Oso lechuza viejo", goblinPayload().copy(hitPoints = 59), 200),
        )
        assertEquals(listOf(id), pc.sync().pushedIds)
        assertEquals(1L, server.objects.getValue(id).revision)

        assertEquals(listOf(id), tablet.sync().appliedIds)
        val tabletOwlbear = CreatureContentRepository(tablet.database).creature(id)!!
        assertEquals("Oso lechuza viejo", tabletOwlbear.item.displayName)
        assertEquals(59, tabletOwlbear.payload.hitPoints)
        // Any editor still holding the old local revision is now stale instead of overwriting.
        assertTrue(tabletOwlbear.item.identity.revision > tabletRevisionBefore)

        assertIs<RevisionDecision.Accepted>(creatures.tombstone(id, Revision(1), 300))
        pc.sync()
        assertNotNull(server.objects.getValue(id).deletedAtEpochSeconds)
        assertEquals(listOf(id), tablet.sync().tombstonedIds)

        val tabletContent = ReusableContentRepository(tablet.database)
        assertEquals(null, tabletContent.content(id))
        assertTrue(tabletContent.content(id, includeDeleted = true)!!.isDeleted, "tombstone keeps the data for recovery")
        assertTrue(tabletContent.listCampaign(campaignId).none { it.identity.id == id })

        val again = tablet.sync()
        assertTrue(again.appliedIds.isEmpty() && again.pushedIds.isEmpty())
        assertTrue(tabletContent.content(id, includeDeleted = true)!!.isDeleted)
    }

    @Test
    fun editsOnBothSidesBecomeAConflictAndNothingIsOverwritten() = scenario { server, pc, tablet ->
        val pcCreatures = CreatureContentRepository(pc.database)
        val id = pcCreatures.createCampaign(campaignId, "Goblin", goblinPayload(), 100).item.identity.id
        pc.sync()
        tablet.sync()

        val tabletCreatures = CreatureContentRepository(tablet.database)
        val tabletRevision = tabletCreatures.creature(id)!!.item.identity.revision
        pcCreatures.updatePayload(id, Revision(0), goblinPayload().copy(hitPoints = 10), 200)
        tabletCreatures.updatePayload(id, tabletRevision, goblinPayload().copy(hitPoints = 12), 210)

        pc.sync()
        val result = tablet.sync()

        assertEquals(
            listOf(DmContentSyncConflict(id, DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED)),
            result.conflicts,
        )
        assertEquals(12, tabletCreatures.creature(id)!!.payload.hitPoints, "tablet work is kept")
        assertEquals(1L, server.objects.getValue(id).revision, "the server keeps the App de PC version")
        val blocked = HostedDmContentOutboxRepository(tablet.database).mutationsFor(id).single()
        assertEquals(HostedRetryState.BLOCKED, blocked.retryState)
        assertEquals(HostedApiErrorCode.CONFLICT_STALE_REVISION.name, blocked.lastErrorCode)

        // The conflict stays visible on later passes and still overwrites nothing.
        assertEquals(DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED, tablet.sync().conflicts.single().reason)
        assertEquals(12, tabletCreatures.creature(id)!!.payload.hitPoints)
    }

    @Test
    fun offlineKeepsTheQueueAndALostAnswerIsNotAppliedTwice() = scenario { server, pc, _ ->
        val creatures = CreatureContentRepository(pc.database)
        val id = creatures.createCampaign(campaignId, "Goblin", goblinPayload(), 100).item.identity.id

        server.offline = true
        val offline = pc.sync()
        assertTrue(offline.offline)
        assertEquals(1, offline.pendingCount)
        val queued = HostedDmContentOutboxRepository(pc.database).mutationsFor(id).single()
        assertEquals(HostedRetryState.READY, queued.retryState)

        // The server applies the change but the answer is lost on the way back.
        server.offline = false
        server.loseNextAnswer = true
        assertTrue(pc.sync().offline)
        assertEquals(0L, server.objects.getValue(id).revision)

        // The retry reuses the same mutation id, so it is a replay, not a second change.
        val retried = pc.sync()
        assertEquals(listOf(id), retried.pushedIds)
        assertEquals(0L, server.objects.getValue(id).revision)
        assertEquals(listOf(queued.mutationId, queued.mutationId), server.receivedMutationIds)
        assertEquals(0, retried.pendingCount)
    }

    @Test
    fun aServerWithoutDmContentRoutesKeepsTheQueueReady() = scenario { server, pc, _ ->
        val id = CreatureContentRepository(pc.database).createCampaign(campaignId, "Goblin", goblinPayload(), 100).item.identity.id
        server.routesMissing = true
        val result = pc.sync()
        assertTrue(result.offline)
        assertEquals(HostedRetryState.READY, HostedDmContentOutboxRepository(pc.database).mutationsFor(id).single().retryState)
        server.routesMissing = false
        assertEquals(listOf(id), pc.sync().pushedIds)
    }

    @Test
    fun aSaveWithoutChangesSendsNothing() = scenario { server, pc, _ ->
        val creatures = CreatureContentRepository(pc.database)
        val id = creatures.createCampaign(campaignId, "Goblin", goblinPayload(), 100).item.identity.id
        pc.sync()
        assertIs<RevisionDecision.Accepted>(creatures.update(id, Revision(0), "Goblin", goblinPayload(), 500))
        assertTrue(pc.sync().pushedIds.isEmpty())
        assertEquals(0L, server.objects.getValue(id).revision)
    }

    @Test
    fun aFamilyFromANewerAppIsReportedAndLeftAlone() = scenario { server, _, tablet ->
        val id = Uuid.random()
        server.objects[id] = HostedDmContent(
            id = id,
            scopeKind = "CAMPAIGN",
            scopeRef = campaignId,
            family = "DUNGEON_ACTIVITY",
            displayName = "Exploración normal",
            revision = 0,
            snapshotFormat = DM_CONTENT_SNAPSHOT_FORMAT,
            snapshotVersion = 1,
            snapshot = JsonObject(mapOf("format" to JsonPrimitive(DM_CONTENT_SNAPSHOT_FORMAT))),
        )
        val result = tablet.sync()
        assertEquals(listOf(id), result.unsupportedFamilyIds)
        assertEquals(null, ReusableContentRepository(tablet.database).content(id, includeDeleted = true))
    }

    @Test
    fun snapshotDocumentKeepsTheWireEnvelope() = scenario { _, pc, _ ->
        val id = CreatureContentRepository(pc.database).createCampaign(campaignId, "Goblin", goblinPayload(), 100).item.identity.id
        val document = assertNotNull(DmContentSnapshotCodec(pc.database).export(id))
        val wire = hostedWireJson().encodeToJsonElement(DmContentSnapshotDocument.serializer(), document).jsonObject
        assertEquals(JsonPrimitive(DM_CONTENT_SNAPSHOT_FORMAT), wire["format"])
        assertEquals(JsonPrimitive(1), wire["version"])
        assertEquals(JsonPrimitive("CAMPAIGN"), wire["content"]!!.jsonObject["scope"]!!.jsonObject["kind"])
    }

    private class Device(val database: AppDatabase, private val server: FakeDmContentServer, private val scopes: List<DmContentSyncScope>) {
        private var clock = 1_000L
        val blobs = InMemoryMediaBlobStore()
        fun sync(onProgress: (DmContentSyncProgress) -> Unit = {}): DmContentSyncResult = runBlocking {
            HostedDmContentSyncService(database, server, blobs = blobs).synchronize(scopes, clock++, onProgress)
        }
    }

    @Test
    fun aLongSyncReportsHowMuchIsSentAndReceived() = scenario { _, pc, tablet ->
        val creatures = CreatureContentRepository(pc.database)
        repeat(3) { creatures.createCampaign(campaignId, "Goblin $it", goblinPayload(), 100) }
        val sent = mutableListOf<DmContentSyncProgress>()
        pc.sync { sent += it }
        assertEquals(DmContentSyncProgress(DmContentSyncStep.SENDING, 0, 3), sent.first())
        assertEquals((1..3).map { DmContentSyncProgress(DmContentSyncStep.SENDING, it, 3) }, sent.filter { it.step == DmContentSyncStep.SENDING }.drop(1))

        val received = mutableListOf<DmContentSyncProgress>()
        tablet.sync { received += it }
        assertEquals(DmContentSyncProgress(DmContentSyncStep.RECEIVING, 3, 3), received.last { it.step == DmContentSyncStep.RECEIVING })
    }

    @Test
    fun theProgressLineSaysHowMuchTimeIsLeftOnceThePaceIsKnown() {
        var now = 0L
        val text = DmContentSyncProgressText { now }
        assertEquals("Enviando 0 de 1.090", text.text(DmContentSyncProgress(DmContentSyncStep.SENDING, 0, 1090)))
        now = 5_000
        assertEquals("Enviando 5 de 1.090", text.text(DmContentSyncProgress(DmContentSyncStep.SENDING, 5, 1090)))
        now = 400_000
        // 535 sent in 400 s: the other 555 take about 415 s, so about 7 minutes.
        assertEquals("Enviando 535 de 1.090 · faltan unos 7 min", text.text(DmContentSyncProgress(DmContentSyncStep.SENDING, 535, 1090)))
        now = 810_000
        assertEquals("Enviando 1.080 de 1.090 · falta menos de un minuto", text.text(DmContentSyncProgress(DmContentSyncStep.SENDING, 1080, 1090)))
        assertEquals("Bajando imágenes: 0 de 22", text.text(DmContentSyncProgress(DmContentSyncStep.IMAGES, 0, 22)))
    }

    @Test
    fun aHandoutImageTravelsWithItsDescriptionAndIsCheckedOnArrival() = scenario { server, pc, tablet ->
        val image = ByteArray(2048) { (it % 251).toByte() }
        val sha = pc.blobs.write(image)
        val handout = HandoutContentRepository(pc.database).createCampaign(
            campaignId,
            "Mapa de la cripta",
            HandoutPayload(kind = HandoutKind.MAP, mediaType = "image/png", sizeBytes = image.size.toLong(), sha256 = sha,
                width = 800, height = 600, visibility = HandoutVisibility.PLAYER_SAFE, caption = "Nivel 1"),
            100,
        )

        val pushed = pc.sync()
        assertEquals(1, pushed.mediaTransferredCount, "the image goes up with its description")
        assertTrue(server.media.containsKey(sha))

        val received = tablet.sync()
        assertEquals(listOf(handout.item.identity.id), received.appliedIds)
        assertEquals(1, received.mediaTransferredCount)
        assertEquals(0, received.mediaMissingCount)
        assertTrue(tablet.blobs.read(sha)!!.contentEquals(image))
        val onTablet = HandoutContentRepository(tablet.database).handout(handout.item.identity.id)!!
        assertEquals(HandoutKind.MAP, onTablet.payload.kind)
        assertEquals(HandoutVisibility.PLAYER_SAFE, onTablet.payload.visibility)

        // A tampered file on the server is never stored: the handout stays «imagen pendiente».
        val other = ByteArray(10) { 7 }
        val otherSha = sha256Hex(other)
        val second = HandoutContentRepository(pc.database).createCampaign(
            campaignId, "Carta", HandoutPayload(mediaType = "image/jpeg", sizeBytes = 10, sha256 = otherSha), 110,
        )
        pc.blobs.write(other)
        pc.sync()
        server.media[otherSha] = ByteArray(10) { 9 }
        val tampered = tablet.sync()
        assertEquals(1, tampered.mediaMissingCount)
        assertTrue(!tablet.blobs.has(otherSha))
        assertTrue(HandoutContentRepository(tablet.database).handout(second.item.identity.id) != null)
    }

    private fun scenario(block: (FakeDmContentServer, Device, Device) -> Unit) {
        val server = FakeDmContentServer()
        val drivers = List(2) { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { AppDatabase.Schema.create(it) } }
        try {
            val scopes = listOf(DmContentSyncScope.Personal(dmAccount), DmContentSyncScope.Campaign(campaignId))
            val devices = drivers.map { driver ->
                val database = AppDatabase(driver)
                IntegratedSpineRepository(database).upsertAccount(AccountIdentity(dmAccount, displayName = "DM"))
                CampaignRepository(database).upsertCampaign(campaignId, "Terramore")
                Device(database, server, scopes)
            }
            block(server, devices[0], devices[1])
        } finally {
            drivers.forEach { it.close() }
        }
    }

    private fun goblinPayload(): CreaturePayload = CreaturePayload(
        size = "Small",
        creatureType = "Humanoid (goblinoid)",
        alignment = "Neutral Evil",
        armorClass = 15,
        armorClassDetails = "leather armor, shield",
        hitPoints = 7,
        hitDice = "2d6",
        speed = "30 ft.",
        abilityScores = CreatureAbilityScores(8, 14, 10, 10, 8, 8),
        challengeRating = "1/4",
        proficiencyBonus = 2,
        actions = "Scimitar. Melee Weapon Attack.",
    )
}

/** In-memory stand-in for the Worker with the same revision, receipt and tombstone rules. */
private class FakeDmContentServer : HostedDmContentTransport {
    val objects = linkedMapOf<Uuid, HostedDmContent>()
    private val receipts = mutableMapOf<Uuid, Uuid>()
    val receivedMutationIds = mutableListOf<Uuid>()
    var offline = false
    var loseNextAnswer = false
    var routesMissing = false

    override suspend fun campaignDmContent(campaignId: Uuid): List<HostedDmContent> {
        checkOnline()
        return objects.values.filter { it.scopeKind == "CAMPAIGN" && it.scopeRef == campaignId }
    }

    override suspend fun personalDmContent(): List<HostedDmContent> {
        checkOnline()
        return objects.values.filter { it.scopeKind == "PERSONAL" }
    }

    override suspend fun putDmContent(
        mutationId: Uuid,
        contentId: Uuid,
        expectedRevision: Long,
        snapshot: DmContentSnapshotDocument,
    ): HostedDmContentMutation {
        checkOnline()
        receivedMutationIds += mutationId
        receipts[mutationId]?.let { objectId ->
            check(objectId == contentId)
            return HostedDmContentMutation(objects.getValue(contentId), applied = false)
        }
        val existing = objects[contentId]
        val revision = when {
            existing == null && expectedRevision == 0L -> 0L
            existing == null -> throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing")
            existing.deletedAtEpochSeconds != null -> throw HostedApiException(410, HostedApiErrorCode.GONE, "gone")
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
        receipts[mutationId] = contentId
        return answer(HostedDmContentMutation(stored, applied = true))
    }

    override suspend fun deleteDmContent(mutationId: Uuid, contentId: Uuid, expectedRevision: Long): HostedDmContentMutation {
        checkOnline()
        receivedMutationIds += mutationId
        receipts[mutationId]?.let { return HostedDmContentMutation(objects.getValue(it), applied = false) }
        val existing = objects[contentId] ?: throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing")
        if (existing.deletedAtEpochSeconds != null) throw HostedApiException(410, HostedApiErrorCode.GONE, "gone")
        if (existing.revision != expectedRevision) {
            throw HostedApiException(409, HostedApiErrorCode.CONFLICT_STALE_REVISION, "stale")
        }
        val stored = existing.copy(revision = existing.revision + 1, deletedAtEpochSeconds = 5_000)
        objects[contentId] = stored
        receipts[mutationId] = contentId
        return answer(HostedDmContentMutation(stored, applied = true))
    }

    val media = linkedMapOf<String, ByteArray>()

    override suspend fun putMediaContent(sha256: String, mediaType: String, bytes: ByteArray) {
        checkOnline()
        media.getOrPut(sha256) { bytes.copyOf() }
    }

    override suspend fun mediaContent(sha256: String, contentId: Uuid): ByteArray {
        checkOnline()
        return media[sha256]?.copyOf() ?: throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "missing media")
    }

    private fun answer(result: HostedDmContentMutation): HostedDmContentMutation {
        if (loseNextAnswer) {
            loseNextAnswer = false
            throw HostedApiException(503, HostedApiErrorCode.TRANSIENT_FAILURE, "answer lost")
        }
        return result
    }

    private fun checkOnline() {
        if (routesMissing) throw HostedApiException(404, HostedApiErrorCode.NOT_FOUND, "Route not found.")
        if (offline) throw HostedApiException(503, HostedApiErrorCode.TRANSIENT_FAILURE, "offline")
    }
}
