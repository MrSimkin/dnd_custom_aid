package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutPayload
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePayload
import io.github.mrsimkin.dndcustomaid.shared.content.sha256Hex
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRuleContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRulePayload
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.DungeonContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.DungeonPayload
import io.github.mrsimkin.dndcustomaid.shared.content.NotePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentItem
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ScenePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZonePayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.SyncMetadata
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.uuid.Uuid

/*
 * Base para DM, package 1: DM content travels App de PC -> hosted API -> tablet.
 *
 * Rules are the same as for PCs: stable ids, hosted revisions, stale-write rejection, durable
 * outbox with stable mutation ids, tombstones that never resurrect, and conflicts that never
 * overwrite local work. The local revision (object_sync_state) stays the local editing counter;
 * the last hosted revision and snapshot live in dm_content_sync_baseline.
 */

const val DM_CONTENT_SNAPSHOT_FORMAT = "dnd-custom-aid.dm-content"
const val DM_CONTENT_SNAPSHOT_VERSION = 1

private const val REUSABLE_CONTENT_OBJECT_TYPE = "REUSABLE_CONTENT"
private const val SCOPE_PERSONAL = "PERSONAL"
private const val SCOPE_CAMPAIGN = "CAMPAIGN"

@Serializable
data class DmContentScopeDocument(
    val kind: String,
    val ref: Uuid,
) {
    init {
        require(kind == SCOPE_PERSONAL || kind == SCOPE_CAMPAIGN) { "DM content scope must be PERSONAL or CAMPAIGN." }
    }
}

@Serializable
data class DmContentProvenanceDocument(
    val sourceObjectId: Uuid,
    val sourceScopeKind: String,
    val sourceScopeRef: String? = null,
    val copiedAtEpochSeconds: Long? = null,
)

@Serializable
data class DmContentHeaderDocument(
    val id: Uuid,
    val family: String,
    val displayName: String,
    val scope: DmContentScopeDocument,
    val provenance: DmContentProvenanceDocument? = null,
    val createdAtEpochSeconds: Long,
    val updatedAtEpochSeconds: Long,
) {
    init {
        require(displayName.isNotBlank()) { "DM content display name must not be blank." }
        require(createdAtEpochSeconds >= 0) { "DM content creation time must not be negative." }
        require(updatedAtEpochSeconds >= 0) { "DM content update time must not be negative." }
    }
}

@Serializable
data class DmContentSnapshotDocument(
    val format: String = DM_CONTENT_SNAPSHOT_FORMAT,
    val version: Int = DM_CONTENT_SNAPSHOT_VERSION,
    val content: DmContentHeaderDocument,
    val payload: JsonObject,
) {
    init {
        require(format == DM_CONTENT_SNAPSHOT_FORMAT) { "DM content snapshot format is unsupported." }
        require(version in 1..DM_CONTENT_SNAPSHOT_VERSION) { "DM content snapshot version is unsupported." }
    }
}

/** Hosted state of one DM content object, as returned by the Worker. */
@Serializable
data class HostedDmContent(
    val id: Uuid,
    val scopeKind: String,
    val scopeRef: Uuid,
    val family: String,
    val displayName: String,
    val revision: Long,
    val deletedAtEpochSeconds: Long? = null,
    val snapshotFormat: String,
    val snapshotVersion: Int,
    val snapshot: JsonObject,
) {
    init {
        require(revision >= 0) { "Hosted DM content revision must not be negative." }
        require(deletedAtEpochSeconds == null || deletedAtEpochSeconds >= 0) {
            "Hosted DM content deletion time must not be negative."
        }
    }
}

data class HostedDmContentMutation(
    val content: HostedDmContent,
    val applied: Boolean,
)

/** The hosted calls DM content sync needs; HostedApiClient implements them over HTTP. */
interface HostedDmContentTransport {
    suspend fun campaignDmContent(campaignId: Uuid): List<HostedDmContent>
    suspend fun personalDmContent(): List<HostedDmContent>
    suspend fun putDmContent(
        mutationId: Uuid,
        contentId: Uuid,
        expectedRevision: Long,
        snapshot: DmContentSnapshotDocument,
    ): HostedDmContentMutation
    suspend fun deleteDmContent(
        mutationId: Uuid,
        contentId: Uuid,
        expectedRevision: Long,
    ): HostedDmContentMutation

    /** Uploads an image file by checksum (package 4). Uploading an existing checksum is a no-op. */
    suspend fun putMediaContent(sha256: String, mediaType: String, bytes: ByteArray)

    /** Downloads the image of a handout the account may read. */
    suspend fun mediaContent(sha256: String, contentId: Uuid): ByteArray
}

private const val LINKS_KEY = "links"

internal fun dmContentJson(): Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}

/**
 * Reads local DM content into snapshot documents and writes hosted documents back into the
 * local tables. Unknown families (from a newer client) are reported, never guessed.
 */
class DmContentSnapshotCodec(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
    private val spine: IntegratedSpineRepository = IntegratedSpineRepository(database),
) {
    private val json = dmContentJson()
    private val creatures = CreatureContentRepository(database, reusableContent)
    private val npcs = NpcContentRepository(database, reusableContent)
    private val rules = HomebrewRuleContentRepository(database, reusableContent)
    private val places = PlaceContentRepository(database, reusableContent)
    private val zones = ZoneContentRepository(database, reusableContent)
    private val encounters = EncounterContentRepository(database, reusableContent)
    private val scenes = SceneContentRepository(database, reusableContent)
    private val handouts = HandoutContentRepository(database, reusableContent)
    private val packages = PackageContentRepository(database, reusableContent)
    private val links = ContentLinksRepository(database)
    private val notes = NoteContentRepository(database, reusableContent)
    private val dungeons = DungeonContentRepository(database, reusableContent)
    private val rulesEntries = io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryContentRepository(database, reusableContent)

    fun supportsFamily(family: String): Boolean = ReusableContentFamily.entries.any { it.name == family }

    fun item(id: Uuid): ReusableContentItem? = reusableContent.content(id, includeDeleted = true)

    /** The image description of a handout, or null for other families. */
    fun handoutPayload(id: Uuid): HandoutPayload? = handouts.payload(id)

    /** Current local document, or null when the object does not exist locally. */
    fun export(id: Uuid): DmContentSnapshotDocument? {
        val item = item(id) ?: return null
        val payload = when (item.family) {
            ReusableContentFamily.CREATURE -> encode(CreaturePayload.serializer(), creatures.payload(id))
            ReusableContentFamily.NPC -> encode(NpcPayload.serializer(), npcs.payload(id))
            ReusableContentFamily.HOMEBREW_RULE -> encode(HomebrewRulePayload.serializer(), rules.payload(id))
            ReusableContentFamily.PLACE -> encode(PlacePayload.serializer(), places.payload(id))
            ReusableContentFamily.ZONE -> encode(ZonePayload.serializer(), zones.payload(id))
            ReusableContentFamily.ENCOUNTER -> encode(EncounterPayload.serializer(), encounters.payload(id))
            ReusableContentFamily.SCENE -> encode(ScenePayload.serializer(), scenes.payload(id))
            ReusableContentFamily.HANDOUT -> encode(HandoutPayload.serializer(), handouts.payload(id))
            ReusableContentFamily.PACKAGE -> encode(PackagePayload.serializer(), packages.payload(id))
            ReusableContentFamily.NOTE -> encode(NotePayload.serializer(), notes.payload(id))
            ReusableContentFamily.DUNGEON -> encode(DungeonPayload.serializer(), dungeons.payload(id) ?: DungeonPayload())
            ReusableContentFamily.RULES_ENTRY -> encode(io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryPayload.serializer(), rulesEntries.payload(id))
        }.withLinks(links.links(id))
        val provenance = item.identity.provenance?.let {
            val (kind, ref) = encodeScope(it.sourceScope)
            DmContentProvenanceDocument(
                sourceObjectId = it.sourceObjectId,
                sourceScopeKind = kind,
                sourceScopeRef = ref,
                copiedAtEpochSeconds = it.copiedAtEpochSeconds,
            )
        }
        val (scopeKind, scopeRef) = encodeScope(item.identity.scope)
        return DmContentSnapshotDocument(
            content = DmContentHeaderDocument(
                id = item.identity.id,
                family = item.family.name,
                displayName = item.displayName,
                scope = DmContentScopeDocument(scopeKind, Uuid.parse(requireNotNull(scopeRef))),
                provenance = provenance,
                createdAtEpochSeconds = item.createdAtEpochSeconds,
                updatedAtEpochSeconds = item.updatedAtEpochSeconds,
            ),
            payload = payload,
        )
    }

    /**
     * Canonical comparison form: the payload is decoded and re-encoded with every default, and
     * the update timestamp is ignored so a save without changes does not count as an edit.
     */
    fun normalizedJson(document: DmContentSnapshotDocument): String {
        val family = requireNotNull(ReusableContentFamily.entries.firstOrNull { it.name == document.content.family }) {
            "DM content family ${document.content.family} is not supported by this App."
        }
        val normalized = document.copy(
            content = document.content.copy(updatedAtEpochSeconds = 0),
            payload = canonicalPayload(family, document.payload),
        )
        return json.encodeToString(DmContentSnapshotDocument.serializer(), normalized)
    }

    /**
     * Writes a hosted document as local current state. The local revision advances so any editor
     * still holding the previous local revision is rejected as stale instead of overwriting it.
     */
    fun applyHostedState(document: DmContentSnapshotDocument, hostedDeletedAtEpochSeconds: Long?) {
        val header = document.content
        val family = requireNotNull(ReusableContentFamily.entries.firstOrNull { it.name == header.family }) {
            "DM content family ${header.family} is not supported by this App."
        }
        val payload = canonicalPayload(family, document.payload)
        database.transaction {
            val existing = item(header.id)
            if (existing != null) {
                val (kind, ref) = encodeScope(existing.identity.scope)
                require(existing.family == family && kind == header.scope.kind && ref == header.scope.ref.toString()) {
                    "Hosted DM content changed family or scope."
                }
            }
            val createdAt = header.createdAtEpochSeconds
            val updatedAt = maxOf(header.updatedAtEpochSeconds, createdAt)
            val provenance = header.provenance
            if (existing == null) {
                database.reusableContentQueries.insertReusableContent(
                    id = header.id.toString(),
                    family = family.name,
                    display_name = header.displayName.trim(),
                    scope_kind = header.scope.kind,
                    scope_ref = header.scope.ref.toString(),
                    source_object_id = provenance?.sourceObjectId?.toString(),
                    source_scope_kind = provenance?.sourceScopeKind,
                    source_scope_ref = provenance?.sourceScopeRef,
                    copied_at_epoch_seconds = provenance?.copiedAtEpochSeconds,
                    created_at_epoch_seconds = createdAt,
                    updated_at_epoch_seconds = updatedAt,
                )
            } else {
                database.dmContentSyncQueries.updateHostedReusableContent(
                    display_name = header.displayName.trim(),
                    source_object_id = provenance?.sourceObjectId?.toString(),
                    source_scope_kind = provenance?.sourceScopeKind,
                    source_scope_ref = provenance?.sourceScopeRef,
                    copied_at_epoch_seconds = provenance?.copiedAtEpochSeconds,
                    created_at_epoch_seconds = createdAt,
                    updated_at_epoch_seconds = updatedAt,
                    id = header.id.toString(),
                )
            }
            writePayload(family, header.id, payload)
            val localRevision = if (existing == null) Revision() else existing.identity.revision.next()
            spine.putSyncMetadata(
                objectType = REUSABLE_CONTENT_OBJECT_TYPE,
                objectId = header.id,
                metadata = SyncMetadata(
                    revision = localRevision,
                    deletedAtEpochSeconds = hostedDeletedAtEpochSeconds?.let { maxOf(it, updatedAt) },
                ),
            )
        }
    }

    /** Package 6: structured links travel inside the payload under "links"; none means no key. */
    private fun JsonObject.withLinks(value: ContentLinks): JsonObject {
        val clean = value.normalized()
        if (clean.isEmpty) return JsonObject(this - LINKS_KEY)
        return JsonObject(this + (LINKS_KEY to json.encodeToJsonElement(ContentLinks.serializer(), clean)))
    }

    private fun linksOf(payload: JsonObject): ContentLinks =
        (payload[LINKS_KEY] as? JsonObject)?.let { runCatching { json.decodeFromJsonElement(ContentLinks.serializer(), it) }.getOrNull() }
            ?: ContentLinks()

    private fun canonicalPayload(family: ReusableContentFamily, payload: JsonObject): JsonObject =
        familyCanonicalPayload(family, payload).withLinks(linksOf(payload))

    private fun familyCanonicalPayload(family: ReusableContentFamily, payload: JsonObject): JsonObject = when (family) {
        ReusableContentFamily.CREATURE -> roundTrip(CreaturePayload.serializer(), payload)
        ReusableContentFamily.NPC -> roundTrip(NpcPayload.serializer(), payload)
        ReusableContentFamily.HOMEBREW_RULE -> roundTrip(HomebrewRulePayload.serializer(), payload)
        ReusableContentFamily.PLACE -> roundTrip(PlacePayload.serializer(), payload)
        ReusableContentFamily.ZONE -> roundTrip(ZonePayload.serializer(), payload)
        ReusableContentFamily.ENCOUNTER -> roundTrip(EncounterPayload.serializer(), payload)
        ReusableContentFamily.SCENE -> roundTrip(ScenePayload.serializer(), payload)
        ReusableContentFamily.HANDOUT -> roundTrip(HandoutPayload.serializer(), payload)
        ReusableContentFamily.PACKAGE -> roundTrip(PackagePayload.serializer(), payload)
        ReusableContentFamily.NOTE -> roundTrip(NotePayload.serializer(), payload)
        ReusableContentFamily.DUNGEON -> roundTrip(DungeonPayload.serializer(), payload)
        ReusableContentFamily.RULES_ENTRY -> roundTrip(io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryPayload.serializer(), payload)
    }

    private fun writePayload(family: ReusableContentFamily, id: Uuid, payload: JsonObject) {
        links.save(id, linksOf(payload))
        when (family) {
            ReusableContentFamily.CREATURE -> json.decodeFromJsonElement(CreaturePayload.serializer(), payload).let {
                if (creatures.payload(id) == null) creatures.insertPayload(id, it) else creatures.updatePayloadRow(id, it)
            }
            ReusableContentFamily.NPC -> json.decodeFromJsonElement(NpcPayload.serializer(), payload).let {
                if (npcs.payload(id) == null) npcs.insertPayload(id, it) else npcs.updatePayloadRow(id, it)
            }
            ReusableContentFamily.HOMEBREW_RULE -> json.decodeFromJsonElement(HomebrewRulePayload.serializer(), payload).let {
                if (rules.payload(id) == null) rules.insertPayload(id, it) else rules.updatePayloadRow(id, it)
            }
            ReusableContentFamily.PLACE -> json.decodeFromJsonElement(PlacePayload.serializer(), payload).let {
                if (places.payload(id) == null) places.insertPayload(id, it) else places.updatePayloadRow(id, it)
            }
            ReusableContentFamily.ZONE -> json.decodeFromJsonElement(ZonePayload.serializer(), payload).let {
                if (zones.payload(id) == null) zones.insertPayload(id, it) else zones.updatePayloadRow(id, it)
            }
            ReusableContentFamily.ENCOUNTER -> json.decodeFromJsonElement(EncounterPayload.serializer(), payload).let {
                if (encounters.payload(id) == null) encounters.insertPayload(id, it) else encounters.updatePayloadRow(id, it)
            }
            ReusableContentFamily.SCENE -> json.decodeFromJsonElement(ScenePayload.serializer(), payload).let {
                if (scenes.payload(id) == null) scenes.insertPayload(id, it) else scenes.updatePayloadRow(id, it)
            }
            ReusableContentFamily.HANDOUT -> json.decodeFromJsonElement(HandoutPayload.serializer(), payload).let {
                if (handouts.payload(id) == null) handouts.insertPayload(id, it) else handouts.updatePayloadRow(id, it)
            }
            ReusableContentFamily.PACKAGE -> json.decodeFromJsonElement(PackagePayload.serializer(), payload).let {
                if (packages.payload(id) == null) packages.insertPayload(id, it) else packages.updatePayloadRow(id, it)
            }
            ReusableContentFamily.NOTE -> json.decodeFromJsonElement(NotePayload.serializer(), payload).let {
                if (notes.payload(id) == null) notes.insertPayload(id, it) else notes.updatePayloadRow(id, it)
            }
            ReusableContentFamily.DUNGEON -> json.decodeFromJsonElement(DungeonPayload.serializer(), payload).let {
                dungeons.updatePayloadRow(id, it)
            }
            ReusableContentFamily.RULES_ENTRY -> json.decodeFromJsonElement(io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryPayload.serializer(), payload).let {
                rulesEntries.updatePayloadRow(id, it)
            }
        }
    }

    private fun <T> encode(serializer: KSerializer<T>, value: T?): JsonObject =
        json.encodeToJsonElement(serializer, requireNotNull(value) { "DM content is missing its payload." }).jsonObject

    private fun <T> roundTrip(serializer: KSerializer<T>, payload: JsonObject): JsonObject =
        json.encodeToJsonElement(serializer, json.decodeFromJsonElement(serializer, payload)).jsonObject

    private fun encodeScope(scope: ContentScope): Pair<String, String?> = when (scope) {
        is ContentScope.Personal -> SCOPE_PERSONAL to scope.ownerAccountId.toString()
        is ContentScope.Campaign -> SCOPE_CAMPAIGN to scope.campaignId.toString()
        is ContentScope.Official -> "OFFICIAL" to scope.sourceKey
        ContentScope.System -> "SYSTEM" to null
    }
}

enum class HostedDmContentMutationType {
    CONTENT_PUT,
    CONTENT_DELETE,
}

@Serializable
data class HostedDmContentPutPayload(
    val mutationId: Uuid,
    val contentId: Uuid,
    val expectedRevision: Long,
    val snapshot: DmContentSnapshotDocument,
) {
    init {
        require(expectedRevision >= 0) { "Expected revision must not be negative." }
        require(snapshot.content.id == contentId) { "DM content snapshot identity must match its queued object." }
    }
}

@Serializable
data class HostedDmContentDeletePayload(
    val mutationId: Uuid,
    val contentId: Uuid,
    val expectedRevision: Long,
) {
    init {
        require(expectedRevision >= 0) { "Expected revision must not be negative." }
    }
}

data class HostedDmContentOutboxMutation(
    val mutationId: Uuid,
    val contentId: Uuid,
    val type: HostedDmContentMutationType,
    val expectedRevision: Long,
    val payloadJson: String,
    val retryState: HostedRetryState,
    val attemptCount: Long,
    val createdAtEpochSeconds: Long,
    val lastErrorCode: String?,
)

data class DmContentSyncBaseline(
    val contentId: Uuid,
    val hostedRevision: Long,
    val hostedDeleted: Boolean,
    val snapshotJson: String,
)

/** Durable queue of DM content changes waiting for the hosted API. */
class HostedDmContentOutboxRepository(
    private val database: AppDatabase,
) {
    private val json = dmContentJson()

    fun enqueuePut(
        snapshot: DmContentSnapshotDocument,
        expectedRevision: Long,
        createdAtEpochSeconds: Long,
        mutationId: Uuid = Uuid.random(),
    ): HostedDmContentOutboxMutation {
        val contentId = snapshot.content.id
        val payload = HostedDmContentPutPayload(mutationId, contentId, expectedRevision, snapshot)
        insert(mutationId, contentId, HostedDmContentMutationType.CONTENT_PUT, expectedRevision,
            json.encodeToString(HostedDmContentPutPayload.serializer(), payload), createdAtEpochSeconds)
        return requireNotNull(mutation(mutationId))
    }

    fun enqueueDelete(
        contentId: Uuid,
        expectedRevision: Long,
        createdAtEpochSeconds: Long,
        mutationId: Uuid = Uuid.random(),
    ): HostedDmContentOutboxMutation {
        val payload = HostedDmContentDeletePayload(mutationId, contentId, expectedRevision)
        insert(mutationId, contentId, HostedDmContentMutationType.CONTENT_DELETE, expectedRevision,
            json.encodeToString(HostedDmContentDeletePayload.serializer(), payload), createdAtEpochSeconds)
        return requireNotNull(mutation(mutationId))
    }

    fun mutation(mutationId: Uuid): HostedDmContentOutboxMutation? =
        allMutations().firstOrNull { it.mutationId == mutationId }

    fun allMutations(): List<HostedDmContentOutboxMutation> =
        database.dmContentSyncQueries.selectAllMutations(mapper = ::mapMutation).executeAsList()

    fun mutationsFor(contentId: Uuid): List<HostedDmContentOutboxMutation> =
        database.dmContentSyncQueries.selectMutationsForContent(contentId.toString(), mapper = ::mapMutation)
            .executeAsList()

    fun putPayload(mutation: HostedDmContentOutboxMutation): HostedDmContentPutPayload {
        require(mutation.type == HostedDmContentMutationType.CONTENT_PUT) { "Mutation is not a DM content put." }
        val payload = json.decodeFromString(HostedDmContentPutPayload.serializer(), mutation.payloadJson)
        require(payload.mutationId == mutation.mutationId && payload.contentId == mutation.contentId) {
            "Queued DM content payload changed identity."
        }
        return payload
    }

    fun baseline(contentId: Uuid): DmContentSyncBaseline? =
        database.dmContentSyncQueries.selectBaseline(contentId.toString()) { id, revision, deleted, snapshot ->
            DmContentSyncBaseline(Uuid.parse(id), revision, deleted != 0L, snapshot)
        }.executeAsOneOrNull()

    fun recordBaseline(contentId: Uuid, hostedRevision: Long, hostedDeleted: Boolean, normalizedSnapshotJson: String) {
        database.dmContentSyncQueries.upsertBaseline(
            content_id = contentId.toString(),
            hosted_revision = hostedRevision,
            hosted_deleted = if (hostedDeleted) 1L else 0L,
            snapshot_json = normalizedSnapshotJson,
        )
    }

    fun recordFailure(mutationId: Uuid, attemptedAtEpochSeconds: Long, retryable: Boolean, errorCode: String?, errorMessage: String?) {
        database.dmContentSyncQueries.recordAttemptFailure(
            retry_state = if (retryable) HostedRetryState.READY.name else HostedRetryState.BLOCKED.name,
            last_attempt_at_epoch_seconds = attemptedAtEpochSeconds,
            last_error_code = errorCode?.takeIf { it.isNotBlank() },
            last_error_message = errorMessage?.takeIf { it.isNotBlank() }?.take(500),
            mutation_id = mutationId.toString(),
        )
    }

    fun markReady(mutationId: Uuid) {
        database.dmContentSyncQueries.markReady(mutationId.toString())
    }

    /** Acknowledges a hosted success: the new baseline and the removal happen together. */
    fun acknowledge(mutation: HostedDmContentOutboxMutation, hostedRevision: Long, hostedDeleted: Boolean, normalizedSnapshotJson: String) {
        database.transaction {
            recordBaseline(mutation.contentId, hostedRevision, hostedDeleted, normalizedSnapshotJson)
            database.dmContentSyncQueries.acknowledgeMutation(mutation.mutationId.toString())
        }
    }

    private fun insert(
        mutationId: Uuid,
        contentId: Uuid,
        type: HostedDmContentMutationType,
        expectedRevision: Long,
        payloadJson: String,
        createdAtEpochSeconds: Long,
    ) {
        require(createdAtEpochSeconds >= 0) { "Creation timestamp must not be negative." }
        require(mutationsFor(contentId).isEmpty()) { "A hosted DM content change is already queued for this object." }
        database.dmContentSyncQueries.insertMutation(
            mutation_id = mutationId.toString(),
            content_id = contentId.toString(),
            mutation_type = type.name,
            expected_revision = expectedRevision,
            payload_json = payloadJson,
            created_at_epoch_seconds = createdAtEpochSeconds,
        )
    }

    private fun mapMutation(
        mutation_id: String,
        content_id: String,
        mutation_type: String,
        expected_revision: Long,
        payload_json: String,
        retry_state: String,
        attempt_count: Long,
        created_at_epoch_seconds: Long,
        @Suppress("UNUSED_PARAMETER") last_attempt_at_epoch_seconds: Long?,
        last_error_code: String?,
        @Suppress("UNUSED_PARAMETER") last_error_message: String?,
    ): HostedDmContentOutboxMutation = HostedDmContentOutboxMutation(
        mutationId = Uuid.parse(mutation_id),
        contentId = Uuid.parse(content_id),
        type = HostedDmContentMutationType.valueOf(mutation_type),
        expectedRevision = expected_revision,
        payloadJson = payload_json,
        retryState = HostedRetryState.valueOf(retry_state),
        attemptCount = attempt_count,
        createdAtEpochSeconds = created_at_epoch_seconds,
        lastErrorCode = last_error_code,
    )
}

/** Where DM content lives: the DM's own library, or one campaign the account runs as DM. */
sealed interface DmContentSyncScope {
    data class Personal(val accountId: Uuid) : DmContentSyncScope
    data class Campaign(val campaignId: Uuid) : DmContentSyncScope
}

enum class DmContentSyncConflictReason {
    /** Changed here and on the server since the last sync; nothing was overwritten. */
    LOCAL_AND_HOSTED_CHANGED,
    /** Changed here, but another device deleted it on the server. */
    CHANGED_LOCALLY_DELETED_HOSTED,
    /** Local and hosted object disagree on family or scope. */
    IDENTITY_MISMATCH,
    /** The server refused the change (permission or validation). */
    REJECTED,
    /** The hosted document could not be read by this App. */
    INVALID_HOSTED_DOCUMENT,
}

data class DmContentSyncConflict(
    val contentId: Uuid,
    val reason: DmContentSyncConflictReason,
)

data class DmContentSyncResult(
    val pushedIds: List<Uuid>,
    val appliedIds: List<Uuid>,
    val tombstonedIds: List<Uuid>,
    val unchangedCount: Int,
    val conflicts: List<DmContentSyncConflict>,
    val unsupportedFamilyIds: List<Uuid>,
    val pendingCount: Int,
    val offline: Boolean,
    /** Images uploaded or downloaded in this pass (package 4). */
    val mediaTransferredCount: Int = 0,
    /** Handouts whose image is still missing on this device (retried on the next pass). */
    val mediaMissingCount: Int = 0,
)

/**
 * One sync pass for the DM: queue local changes, send the queue, then receive hosted changes.
 *
 * Conflicts block only the object involved and are reported; local work is never overwritten.
 * A network failure stops sending, keeps the queue, and skips receiving for this pass.
 */
class HostedDmContentSyncService(
    private val database: AppDatabase,
    private val transport: HostedDmContentTransport,
    private val codec: DmContentSnapshotCodec = DmContentSnapshotCodec(database),
    private val outbox: HostedDmContentOutboxRepository = HostedDmContentOutboxRepository(database),
    private val blobs: MediaBlobStore? = null,
) {
    private var mediaTransferred = 0

    suspend fun synchronize(scopes: List<DmContentSyncScope>, nowEpochSeconds: Long): DmContentSyncResult {
        require(nowEpochSeconds >= 0) { "Sync time must not be negative." }
        val conflicts = linkedMapOf<Uuid, DmContentSyncConflictReason>()
        val pushed = mutableListOf<Uuid>()
        val applied = mutableListOf<Uuid>()
        val tombstoned = mutableListOf<Uuid>()
        val unsupported = mutableListOf<Uuid>()
        var unchanged = 0

        mediaTransferred = 0
        scopes.distinct().forEach { queueLocalChanges(it, nowEpochSeconds, conflicts) }

        var offline = false
        for (mutation in outbox.allMutations().filter { it.retryState == HostedRetryState.READY }) {
            when (val outcome = send(mutation, nowEpochSeconds)) {
                SendOutcome.Sent -> pushed += mutation.contentId
                SendOutcome.Offline -> {
                    offline = true
                    break
                }
                is SendOutcome.Blocked -> conflicts[mutation.contentId] = outcome.reason
            }
        }

        if (!offline) {
            for (scope in scopes.distinct()) {
                val hosted = try {
                    when (scope) {
                        is DmContentSyncScope.Campaign -> transport.campaignDmContent(scope.campaignId)
                            .filter { it.scopeKind == SCOPE_CAMPAIGN && it.scopeRef == scope.campaignId }
                        is DmContentSyncScope.Personal -> transport.personalDmContent()
                            .filter { it.scopeKind == SCOPE_PERSONAL && it.scopeRef == scope.accountId }
                    }
                } catch (error: Exception) {
                    if (isOffline(error)) {
                        offline = true
                        break
                    }
                    throw error
                }
                hosted.sortedBy { it.id.toString() }.forEach { remote ->
                    when (val outcome = receive(remote, conflicts)) {
                        ReceiveOutcome.Applied -> applied += remote.id
                        ReceiveOutcome.Tombstoned -> tombstoned += remote.id
                        ReceiveOutcome.Unchanged -> unchanged += 1
                        ReceiveOutcome.Unsupported -> unsupported += remote.id
                        is ReceiveOutcome.Conflict -> conflicts[remote.id] = outcome.reason
                    }
                }
            }
        }

        val mediaMissing = if (offline) countMissingMedia(scopes) else downloadMissingMedia(scopes)

        return DmContentSyncResult(
            mediaTransferredCount = mediaTransferred,
            mediaMissingCount = mediaMissing,
            pushedIds = pushed,
            appliedIds = applied,
            tombstonedIds = tombstoned,
            unchangedCount = unchanged,
            conflicts = conflicts.map { DmContentSyncConflict(it.key, it.value) },
            unsupportedFamilyIds = unsupported,
            pendingCount = outbox.allMutations().size,
            offline = offline,
        )
    }

    /** Puts every locally changed object of the scope in the queue (one queued change per object). */
    fun queueLocalChanges(
        scope: DmContentSyncScope,
        nowEpochSeconds: Long,
        conflicts: MutableMap<Uuid, DmContentSyncConflictReason> = linkedMapOf(),
    ) {
        val (kind, ref) = when (scope) {
            is DmContentSyncScope.Personal -> SCOPE_PERSONAL to scope.accountId.toString()
            is DmContentSyncScope.Campaign -> SCOPE_CAMPAIGN to scope.campaignId.toString()
        }
        val ids = database.dmContentSyncQueries.selectContentIdsByScope(kind, ref).executeAsList().map(Uuid::parse)
        database.transaction {
            ids.forEach { id ->
                if (outbox.mutationsFor(id).isNotEmpty()) return@forEach
                val item = codec.item(id) ?: return@forEach
                val baseline = outbox.baseline(id)
                if (item.isDeleted) {
                    if (baseline != null && !baseline.hostedDeleted) {
                        outbox.enqueueDelete(id, baseline.hostedRevision, nowEpochSeconds)
                    }
                    return@forEach
                }
                val document = codec.export(id) ?: return@forEach
                when {
                    baseline == null -> outbox.enqueuePut(document, 0, nowEpochSeconds)
                    baseline.hostedDeleted -> conflicts[id] = DmContentSyncConflictReason.CHANGED_LOCALLY_DELETED_HOSTED
                    codec.normalizedJson(document) != baseline.snapshotJson ->
                        outbox.enqueuePut(document, baseline.hostedRevision, nowEpochSeconds)
                }
            }
        }
    }

    private suspend fun send(mutation: HostedDmContentOutboxMutation, nowEpochSeconds: Long): SendOutcome {
        return try {
            when (mutation.type) {
                HostedDmContentMutationType.CONTENT_PUT -> {
                    val payload = outbox.putPayload(mutation)
                    uploadHandoutImage(payload.snapshot)
                    val result = transport.putDmContent(mutation.mutationId, mutation.contentId, mutation.expectedRevision, payload.snapshot)
                    outbox.acknowledge(mutation, result.content.revision, false, codec.normalizedJson(payload.snapshot))
                }
                HostedDmContentMutationType.CONTENT_DELETE -> {
                    val result = transport.deleteDmContent(mutation.mutationId, mutation.contentId, mutation.expectedRevision)
                    val previous = outbox.baseline(mutation.contentId)?.snapshotJson
                        ?: codec.export(mutation.contentId)?.let(codec::normalizedJson)
                        ?: "{}"
                    outbox.acknowledge(mutation, result.content.revision, true, previous)
                }
            }
            SendOutcome.Sent
        } catch (error: Exception) {
            if (isOffline(error)) {
                outbox.recordFailure(mutation.mutationId, nowEpochSeconds, retryable = true, errorCode = "OFFLINE", errorMessage = error.message)
                return SendOutcome.Offline
            }
            val code = (error as? HostedApiException)?.code
            outbox.recordFailure(mutation.mutationId, nowEpochSeconds, retryable = false, errorCode = code?.name ?: "LOCAL_ERROR", errorMessage = error.message)
            SendOutcome.Blocked(
                when (code) {
                    HostedApiErrorCode.CONFLICT_STALE_REVISION -> DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED
                    HostedApiErrorCode.GONE -> DmContentSyncConflictReason.CHANGED_LOCALLY_DELETED_HOSTED
                    else -> DmContentSyncConflictReason.REJECTED
                },
            )
        }
    }

    private fun receive(
        remote: HostedDmContent,
        conflicts: Map<Uuid, DmContentSyncConflictReason>,
    ): ReceiveOutcome {
        if (!codec.supportsFamily(remote.family)) return ReceiveOutcome.Unsupported
        conflicts[remote.id]?.let { return ReceiveOutcome.Conflict(it) }
        val pending = outbox.mutationsFor(remote.id)
        if (pending.isNotEmpty()) {
            return if (pending.any { it.retryState == HostedRetryState.BLOCKED }) {
                ReceiveOutcome.Conflict(DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED)
            } else {
                ReceiveOutcome.Unchanged
            }
        }

        val document = try {
            dmContentJson().decodeFromJsonElement(DmContentSnapshotDocument.serializer(), remote.snapshot)
                .also {
                    require(it.content.id == remote.id && it.content.family == remote.family)
                    require(it.content.scope.kind == remote.scopeKind && it.content.scope.ref == remote.scopeRef)
                    codec.normalizedJson(it)
                }
        } catch (_: Exception) {
            return ReceiveOutcome.Conflict(DmContentSyncConflictReason.INVALID_HOSTED_DOCUMENT)
        }
        val remoteJson = codec.normalizedJson(document)
        val remoteDeleted = remote.deletedAtEpochSeconds != null
        val baseline = outbox.baseline(remote.id)
        val local = codec.item(remote.id)

        if (local != null && local.family.name != remote.family) {
            return ReceiveOutcome.Conflict(DmContentSyncConflictReason.IDENTITY_MISMATCH)
        }
        if (baseline != null && remote.revision <= baseline.hostedRevision && remoteDeleted == baseline.hostedDeleted) {
            return ReceiveOutcome.Unchanged
        }

        if (local != null) {
            val localJson = if (local.isDeleted) null else codec.export(remote.id)?.let(codec::normalizedJson)
            val untouchedSinceBaseline = baseline != null && !baseline.hostedDeleted && localJson == baseline.snapshotJson
            val alreadyEqual = !local.isDeleted && localJson == remoteJson && !remoteDeleted
            when {
                local.isDeleted && remoteDeleted -> {
                    outbox.recordBaseline(remote.id, remote.revision, true, remoteJson)
                    return ReceiveOutcome.Tombstoned
                }
                alreadyEqual -> {
                    outbox.recordBaseline(remote.id, remote.revision, false, remoteJson)
                    return ReceiveOutcome.Unchanged
                }
                !untouchedSinceBaseline -> return ReceiveOutcome.Conflict(
                    if (remoteDeleted) DmContentSyncConflictReason.CHANGED_LOCALLY_DELETED_HOSTED
                    else DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED,
                )
            }
        }

        return try {
            database.transaction {
                codec.applyHostedState(document, remote.deletedAtEpochSeconds)
                outbox.recordBaseline(remote.id, remote.revision, remoteDeleted, remoteJson)
            }
            if (remoteDeleted) ReceiveOutcome.Tombstoned else ReceiveOutcome.Applied
        } catch (_: IllegalArgumentException) {
            ReceiveOutcome.Conflict(DmContentSyncConflictReason.IDENTITY_MISMATCH)
        }
    }

    /** A handout's image goes up before its description, so no device receives a dangling reference. */
    private suspend fun uploadHandoutImage(snapshot: DmContentSnapshotDocument) {
        val store = blobs ?: return
        if (snapshot.content.family != ReusableContentFamily.HANDOUT.name) return
        val payload = dmContentJson().decodeFromJsonElement(HandoutPayload.serializer(), snapshot.payload)
        val bytes = store.read(payload.sha256) ?: return
        transport.putMediaContent(payload.sha256, payload.mediaType, bytes)
        mediaTransferred += 1
    }

    private fun activeHandoutsMissingImage(scopes: List<DmContentSyncScope>): List<Pair<Uuid, HandoutPayload>> {
        val store = blobs ?: return emptyList()
        return scopes.distinct().flatMap { scope ->
            val (kind, ref) = when (scope) {
                is DmContentSyncScope.Personal -> SCOPE_PERSONAL to scope.accountId.toString()
                is DmContentSyncScope.Campaign -> SCOPE_CAMPAIGN to scope.campaignId.toString()
            }
            database.dmContentSyncQueries.selectContentIdsByScope(kind, ref).executeAsList().map(Uuid::parse)
        }.mapNotNull { id ->
            val item = codec.item(id) ?: return@mapNotNull null
            if (item.isDeleted || item.family != ReusableContentFamily.HANDOUT) return@mapNotNull null
            val payload = codec.handoutPayload(id) ?: return@mapNotNull null
            if (store.has(payload.sha256)) null else id to payload
        }
    }

    private fun countMissingMedia(scopes: List<DmContentSyncScope>): Int = activeHandoutsMissingImage(scopes).size

    private suspend fun downloadMissingMedia(scopes: List<DmContentSyncScope>): Int {
        val store = blobs ?: return 0
        var missing = 0
        for ((id, payload) in activeHandoutsMissingImage(scopes)) {
            if (store.has(payload.sha256)) continue
            val bytes = try {
                transport.mediaContent(payload.sha256, id)
            } catch (error: Exception) {
                if (error is kotlinx.coroutines.CancellationException) throw error
                missing += 1
                continue
            }
            // Never keep a file whose content does not match the checksum the DM published.
            if (bytes.isEmpty() || sha256Hex(bytes) != payload.sha256) {
                missing += 1
                continue
            }
            store.write(bytes)
            mediaTransferred += 1
        }
        return missing
    }

    private fun isOffline(error: Exception): Boolean = when (error) {
        // NOT_FOUND also counts as "try later": a server that does not have the DM content
        // routes yet must not block the DM's queued changes. Nothing is lost while it waits.
        is HostedApiException -> error.isTransient || error.code == HostedApiErrorCode.NOT_FOUND
        is HostedAuthenticationUnavailableException -> true
        else -> error::class.simpleName?.let { name ->
            name.contains("Timeout") || name.contains("Connect") || name.contains("UnknownHost") ||
                name.contains("Unresolved") || name.contains("Socket") || name.contains("IOException")
        } == true
    }

    private sealed interface SendOutcome {
        data object Sent : SendOutcome
        data object Offline : SendOutcome
        data class Blocked(val reason: DmContentSyncConflictReason) : SendOutcome
    }

    private sealed interface ReceiveOutcome {
        data object Applied : ReceiveOutcome
        data object Tombstoned : ReceiveOutcome
        data object Unchanged : ReceiveOutcome
        data object Unsupported : ReceiveOutcome
        data class Conflict(val reason: DmContentSyncConflictReason) : ReceiveOutcome
    }
}

/**
 * The scopes a DM syncs: their personal library plus every local campaign where the account is
 * an ACTIVE DM. Player campaigns are never included (players do not read DM content).
 */
fun dmContentSyncScopes(database: AppDatabase, accountId: Uuid): List<DmContentSyncScope> {
    val spine = IntegratedSpineRepository(database)
    val campaigns = io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository(database).listCampaigns()
        .filter { campaign ->
            spine.membership(campaign.id, accountId)?.let {
                it.role == io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole.DM && it.canUseHostedCampaign
            } == true
        }
        .map { DmContentSyncScope.Campaign(it.id) }
    return listOf(DmContentSyncScope.Personal(accountId)) + campaigns
}

/** Short Spanish summary for the owner-facing sync status line. */
fun DmContentSyncResult.spanishSummary(): String = buildString {
    if (offline) {
        append("Sin conexión: ${pendingCount} cambio(s) quedan en cola y se enviarán después.")
        return@buildString
    }
    append("Contenido del DM sincronizado · enviados: ${pushedIds.size} · recibidos: ${appliedIds.size}")
    if (tombstonedIds.isNotEmpty()) append(" · borrados: ${tombstonedIds.size}")
    if (conflicts.isNotEmpty()) append(" · en conflicto: ${conflicts.size} (no se pisó nada)")
    if (unsupportedFamilyIds.isNotEmpty()) append(" · de una versión más nueva: ${unsupportedFamilyIds.size}")
    if (mediaTransferredCount > 0) append(" · imágenes: $mediaTransferredCount")
    if (mediaMissingCount > 0) append(" · imágenes pendientes: $mediaMissingCount")
    append('.')
}
