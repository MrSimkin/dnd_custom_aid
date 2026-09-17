package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class PlaceContentRepository(
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
        payload: PlacePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): PlaceContent {
        var result: PlaceContent? = null
        database.transaction {
            val item = reusableContent.createPersonal(
                ownerAccountId = ownerAccountId,
                family = ReusableContentFamily.PLACE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = PlaceContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun createCampaign(
        campaignId: Uuid,
        rawDisplayName: String,
        payload: PlacePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): PlaceContent {
        var result: PlaceContent? = null
        database.transaction {
            val item = reusableContent.createCampaign(
                campaignId = campaignId,
                family = ReusableContentFamily.PLACE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = PlaceContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun place(id: Uuid, includeDeleted: Boolean = false): PlaceContent? {
        val item = reusableContent.content(id, includeDeleted = includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.PLACE) return null
        val payload = requireNotNull(payload(id)) {
            "Place reusable content is missing its persisted payload."
        }
        return PlaceContent(item, payload)
    }

    fun copyPersonalToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
        newId: Uuid = Uuid.random(),
    ): PlaceContent {
        var result: PlaceContent? = null
        database.transaction {
            val source = requireNotNull(place(sourceId)) {
                "Place source must exist and be active before it can be copied."
            }
            val copiedItem = reusableContent.copyPersonalToCampaign(
                sourceId = sourceId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
                newId = newId,
            )
            require(copiedItem.family == ReusableContentFamily.PLACE) {
                "Place copy must retain the PLACE family."
            }
            insertPayload(copiedItem.identity.id, source.payload)
            result = PlaceContent(copiedItem, source.payload)
        }
        return checkNotNull(result)
    }

    fun updatePayload(
        id: Uuid,
        expectedRevision: Revision,
        payload: PlacePayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision = reusableContent.mutateContent(
        id = id,
        expectedRevision = expectedRevision,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
    ) { current ->
        require(current.family == ReusableContentFamily.PLACE) {
            "Place payload updates require PLACE reusable content."
        }
        require(this.payload(id) != null) {
            "Place reusable content is missing its persisted payload."
        }
        updatePayloadRow(id, payload)
    }

    fun tombstone(
        id: Uuid,
        expectedRevision: Revision,
        deletedAtEpochSeconds: Long,
    ): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) {
            "Place reusable content must exist before it can be deleted."
        }
        require(current.family == ReusableContentFamily.PLACE) {
            "Place deletion requires PLACE reusable content."
        }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    private fun payload(id: Uuid): PlacePayload? =
        database.placePayloadQueries.selectPlacePayloadByContentId(
            content_id = id.toString(),
            mapper = ::mapPayload,
        ).executeAsOneOrNull()

    private fun insertPayload(id: Uuid, payload: PlacePayload) {
        database.placePayloadQueries.insertPlacePayload(
            content_id = id.toString(),
            kind = payload.kind.name,
            summary = payload.summary,
            area = payload.area,
            function = payload.function,
            presentation = payload.presentation,
            services_json = encodeStringList(payload.services),
            interactives_json = encodeStringList(payload.interactives),
            hooks_json = encodeStringList(payload.hooks),
            player_safe_text = payload.playerSafeText,
            dm_notes = payload.dmNotes,
            paper_references_json = encodeStringList(payload.paperReferences),
            tags_json = encodeStringList(payload.tags),
        )
    }

    private fun updatePayloadRow(id: Uuid, payload: PlacePayload) {
        database.placePayloadQueries.updatePlacePayload(
            kind = payload.kind.name,
            summary = payload.summary,
            area = payload.area,
            function = payload.function,
            presentation = payload.presentation,
            services_json = encodeStringList(payload.services),
            interactives_json = encodeStringList(payload.interactives),
            hooks_json = encodeStringList(payload.hooks),
            player_safe_text = payload.playerSafeText,
            dm_notes = payload.dmNotes,
            paper_references_json = encodeStringList(payload.paperReferences),
            tags_json = encodeStringList(payload.tags),
            content_id = id.toString(),
        )
    }

    private fun mapPayload(
        contentId: String,
        kind: String,
        summary: String,
        area: String,
        function: String,
        presentation: String,
        servicesJson: String,
        interactivesJson: String,
        hooksJson: String,
        playerSafeText: String,
        dmNotes: String,
        paperReferencesJson: String,
        tagsJson: String,
    ): PlacePayload {
        require(contentId.isNotBlank()) { "Place payload content id must not be blank." }
        return PlacePayload(
            kind = PlaceKind.valueOf(kind),
            summary = summary,
            area = area,
            function = function,
            presentation = presentation,
            services = decodeStringList(servicesJson),
            interactives = decodeStringList(interactivesJson),
            hooks = decodeStringList(hooksJson),
            playerSafeText = playerSafeText,
            dmNotes = dmNotes,
            paperReferences = decodeStringList(paperReferencesJson),
            tags = decodeStringList(tagsJson),
        )
    }

    private fun encodeStringList(values: List<String>): String = payloadJson.encodeToString(values)

    private fun decodeStringList(raw: String): List<String> = payloadJson.decodeFromString<List<String>>(raw)
}
