package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class SceneContentRepository(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
    private val payloadJson: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    },
) {
    fun createPersonal(
        ownerAccountId: Uuid,
        rawDisplayName: String,
        payload: ScenePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): SceneContent {
        var result: SceneContent? = null
        database.transaction {
            val item = reusableContent.createPersonal(
                ownerAccountId = ownerAccountId,
                family = ReusableContentFamily.SCENE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = SceneContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun createCampaign(
        campaignId: Uuid,
        rawDisplayName: String,
        payload: ScenePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): SceneContent {
        var result: SceneContent? = null
        database.transaction {
            val item = reusableContent.createCampaign(
                campaignId = campaignId,
                family = ReusableContentFamily.SCENE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = SceneContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun scene(id: Uuid, includeDeleted: Boolean = false): SceneContent? {
        val item = reusableContent.content(id, includeDeleted = includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.SCENE) return null
        val payload = requireNotNull(payload(id)) {
            "Scene reusable content is missing its persisted payload."
        }
        return SceneContent(item, payload)
    }

    fun copyPersonalToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
        newId: Uuid = Uuid.random(),
    ): SceneContent {
        var result: SceneContent? = null
        database.transaction {
            val source = requireNotNull(scene(sourceId)) {
                "Scene source must exist and be active before it can be copied."
            }
            val copiedItem = reusableContent.copyPersonalToCampaign(
                sourceId = sourceId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
                newId = newId,
            )
            require(copiedItem.family == ReusableContentFamily.SCENE) {
                "Scene copy must retain the SCENE family."
            }
            insertPayload(copiedItem.identity.id, source.payload)
            result = SceneContent(copiedItem, source.payload)
        }
        return checkNotNull(result)
    }

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        rawDisplayName: String,
        payload: ScenePayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision {
        val displayName = rawDisplayName.trim()
        require(displayName.isNotEmpty()) { "Scene display name must not be blank." }

        return reusableContent.mutateContent(
            id = id,
            expectedRevision = expectedRevision,
            updatedAtEpochSeconds = updatedAtEpochSeconds,
        ) { current ->
            require(current.family == ReusableContentFamily.SCENE) {
                "Scene updates require SCENE reusable content."
            }
            require(this.payload(id) != null) {
                "Scene reusable content is missing its persisted payload."
            }
            database.reusableContentQueries.updateReusableContentName(
                display_name = displayName,
                updated_at_epoch_seconds = updatedAtEpochSeconds,
                id = id.toString(),
            )
            updatePayloadRow(id, payload)
        }
    }

    fun updatePayload(
        id: Uuid,
        expectedRevision: Revision,
        payload: ScenePayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision = reusableContent.mutateContent(
        id = id,
        expectedRevision = expectedRevision,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
    ) { current ->
        require(current.family == ReusableContentFamily.SCENE) {
            "Scene payload updates require SCENE reusable content."
        }
        require(this.payload(id) != null) {
            "Scene reusable content is missing its persisted payload."
        }
        updatePayloadRow(id, payload)
    }

    fun tombstone(
        id: Uuid,
        expectedRevision: Revision,
        deletedAtEpochSeconds: Long,
    ): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) {
            "Scene reusable content must exist before it can be deleted."
        }
        require(current.family == ReusableContentFamily.SCENE) {
            "Scene deletion requires SCENE reusable content."
        }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    private fun payload(id: Uuid): ScenePayload? =
        database.scenePayloadQueries.selectScenePayloadByContentId(
            content_id = id.toString(),
            mapper = ::mapPayload,
        ).executeAsOneOrNull()

    private fun insertPayload(id: Uuid, payload: ScenePayload) {
        database.scenePayloadQueries.insertScenePayload(
            content_id = id.toString(),
            purpose = payload.purpose,
            possible_next_scenes_json = encodeStringList(payload.possibleNextScenes),
            references_json = encodeStringList(payload.references),
            notes = payload.notes,
        )
    }

    private fun updatePayloadRow(id: Uuid, payload: ScenePayload) {
        database.scenePayloadQueries.updateScenePayload(
            purpose = payload.purpose,
            possible_next_scenes_json = encodeStringList(payload.possibleNextScenes),
            references_json = encodeStringList(payload.references),
            notes = payload.notes,
            content_id = id.toString(),
        )
    }

    private fun mapPayload(
        contentId: String,
        purpose: String,
        possibleNextScenesJson: String,
        referencesJson: String,
        notes: String,
    ): ScenePayload {
        require(contentId.isNotBlank()) { "Scene payload content id must not be blank." }
        return ScenePayload(
            purpose = purpose,
            possibleNextScenes = decodeStringList(possibleNextScenesJson),
            references = decodeStringList(referencesJson),
            notes = notes,
        )
    }

    private fun encodeStringList(values: List<String>): String = payloadJson.encodeToString(values)

    private fun decodeStringList(raw: String): List<String> = payloadJson.decodeFromString<List<String>>(raw)
}
