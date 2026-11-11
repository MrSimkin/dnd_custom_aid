package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlin.uuid.Uuid

/** Handout descriptions (package 4). The image bytes live in a [MediaBlobStore]. */
class HandoutContentRepository(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
) {
    fun createPersonal(
        ownerAccountId: Uuid,
        rawDisplayName: String,
        payload: HandoutPayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): HandoutContent = create(payload) {
        reusableContent.createPersonal(ownerAccountId, ReusableContentFamily.HANDOUT, rawDisplayName, nowEpochSeconds, id)
    }

    fun createCampaign(
        campaignId: Uuid,
        rawDisplayName: String,
        payload: HandoutPayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): HandoutContent = create(payload) {
        reusableContent.createCampaign(campaignId, ReusableContentFamily.HANDOUT, rawDisplayName, nowEpochSeconds, id)
    }

    fun handout(id: Uuid, includeDeleted: Boolean = false): HandoutContent? {
        val item = reusableContent.content(id, includeDeleted = includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.HANDOUT) return null
        val payload = requireNotNull(payload(id)) { "Handout reusable content is missing its persisted payload." }
        return HandoutContent(item, payload)
    }

    fun listCampaign(campaignId: Uuid): List<HandoutContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.HANDOUT).mapNotNull { handout(it.identity.id) }

    fun listPersonal(ownerAccountId: Uuid): List<HandoutContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.HANDOUT).mapNotNull { handout(it.identity.id) }

    fun copyPersonalToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
        newId: Uuid = Uuid.random(),
    ): HandoutContent {
        var result: HandoutContent? = null
        database.transaction {
            val source = requireNotNull(handout(sourceId)) { "Handout source must exist and be active before it can be copied." }
            val copied = reusableContent.copyPersonalToCampaign(sourceId, campaignId, copiedAtEpochSeconds, newId)
            insertPayload(copied.identity.id, source.payload)
            result = HandoutContent(copied, source.payload)
        }
        return checkNotNull(result)
    }

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        rawDisplayName: String,
        payload: HandoutPayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision {
        val displayName = rawDisplayName.trim()
        require(displayName.isNotEmpty()) { "Handout display name must not be blank." }
        return reusableContent.mutateContent(id, expectedRevision, updatedAtEpochSeconds) { current ->
            require(current.family == ReusableContentFamily.HANDOUT) { "Handout updates require HANDOUT reusable content." }
            require(this.payload(id) != null) { "Handout reusable content is missing its persisted payload." }
            database.reusableContentQueries.updateReusableContentName(displayName, updatedAtEpochSeconds, id.toString())
            updatePayloadRow(id, payload)
        }
    }

    fun tombstone(id: Uuid, expectedRevision: Revision, deletedAtEpochSeconds: Long): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) {
            "Handout reusable content must exist before it can be deleted."
        }
        require(current.family == ReusableContentFamily.HANDOUT) { "Handout deletion requires HANDOUT reusable content." }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    /** Every image checksum any local handout refers to (active or deleted). */
    fun referencedHashes(): Set<String> = database.handoutPayloadQueries.selectAllHandoutHashes().executeAsList().toSet()

    internal fun payload(id: Uuid): HandoutPayload? =
        database.handoutPayloadQueries.selectHandoutPayloadByContentId(id.toString()) {
                _, kind, mediaType, sizeBytes, sha256, width, height, visibility, caption, notes ->
            HandoutPayload(
                kind = HandoutKind.valueOf(kind),
                mediaType = mediaType,
                sizeBytes = sizeBytes,
                sha256 = sha256,
                width = width?.toInt(),
                height = height?.toInt(),
                visibility = HandoutVisibility.valueOf(visibility),
                caption = caption,
                notes = notes,
            )
        }.executeAsOneOrNull()

    internal fun insertPayload(id: Uuid, payload: HandoutPayload) {
        database.handoutPayloadQueries.insertHandoutPayload(
            content_id = id.toString(),
            kind = payload.kind.name,
            media_type = payload.mediaType,
            size_bytes = payload.sizeBytes,
            sha256 = payload.sha256,
            width = payload.width?.toLong(),
            height = payload.height?.toLong(),
            visibility = payload.visibility.name,
            caption = payload.caption,
            notes = payload.notes,
        )
    }

    internal fun updatePayloadRow(id: Uuid, payload: HandoutPayload) {
        database.handoutPayloadQueries.updateHandoutPayload(
            kind = payload.kind.name,
            media_type = payload.mediaType,
            size_bytes = payload.sizeBytes,
            sha256 = payload.sha256,
            width = payload.width?.toLong(),
            height = payload.height?.toLong(),
            visibility = payload.visibility.name,
            caption = payload.caption,
            notes = payload.notes,
            content_id = id.toString(),
        )
    }

    private fun create(payload: HandoutPayload, createItem: () -> ReusableContentItem): HandoutContent {
        var result: HandoutContent? = null
        database.transaction {
            val item = createItem()
            insertPayload(item.identity.id, payload)
            result = HandoutContent(item, payload)
        }
        return checkNotNull(result)
    }
}
