package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class ZoneContentRepository(
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
        payload: ZonePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): ZoneContent {
        var result: ZoneContent? = null
        database.transaction {
            val item = reusableContent.createPersonal(
                ownerAccountId = ownerAccountId,
                family = ReusableContentFamily.ZONE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = ZoneContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun createCampaign(
        campaignId: Uuid,
        rawDisplayName: String,
        payload: ZonePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): ZoneContent {
        var result: ZoneContent? = null
        database.transaction {
            val item = reusableContent.createCampaign(
                campaignId = campaignId,
                family = ReusableContentFamily.ZONE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = ZoneContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun zone(id: Uuid, includeDeleted: Boolean = false): ZoneContent? {
        val item = reusableContent.content(id, includeDeleted = includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.ZONE) return null
        val payload = requireNotNull(payload(id)) {
            "Zone reusable content is missing its persisted payload."
        }
        return ZoneContent(item, payload)
    }

    fun copyPersonalToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
        newId: Uuid = Uuid.random(),
    ): ZoneContent {
        var result: ZoneContent? = null
        database.transaction {
            val source = requireNotNull(zone(sourceId)) {
                "Zone source must exist and be active before it can be copied."
            }
            val copiedItem = reusableContent.copyPersonalToCampaign(
                sourceId = sourceId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
                newId = newId,
            )
            require(copiedItem.family == ReusableContentFamily.ZONE) {
                "Zone copy must retain the ZONE family."
            }
            insertPayload(copiedItem.identity.id, source.payload)
            result = ZoneContent(copiedItem, source.payload)
        }
        return checkNotNull(result)
    }

    fun updatePayload(
        id: Uuid,
        expectedRevision: Revision,
        payload: ZonePayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision = reusableContent.mutateContent(
        id = id,
        expectedRevision = expectedRevision,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
    ) { current ->
        require(current.family == ReusableContentFamily.ZONE) {
            "Zone payload updates require ZONE reusable content."
        }
        require(this.payload(id) != null) {
            "Zone reusable content is missing its persisted payload."
        }
        updatePayloadRow(id, payload)
    }

    fun tombstone(
        id: Uuid,
        expectedRevision: Revision,
        deletedAtEpochSeconds: Long,
    ): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) {
            "Zone reusable content must exist before it can be deleted."
        }
        require(current.family == ReusableContentFamily.ZONE) {
            "Zone deletion requires ZONE reusable content."
        }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    private fun payload(id: Uuid): ZonePayload? =
        database.zonePayloadQueries.selectZonePayloadByContentId(
            content_id = id.toString(),
            mapper = ::mapPayload,
        ).executeAsOneOrNull()

    private fun insertPayload(id: Uuid, payload: ZonePayload) {
        database.zonePayloadQueries.insertZonePayload(
            content_id = id.toString(),
            summary = payload.summary,
            area = payload.area,
            presentation = payload.presentation,
            space = payload.space,
            exploration = payload.exploration,
            interactives_json = encodeStringList(payload.interactives),
            clues_json = encodeStringList(payload.clues),
            checks_json = encodeStringList(payload.checks),
            consequences_json = encodeStringList(payload.consequences),
            encounter_brief = payload.encounterBrief,
            dm_guidance = payload.dmGuidance,
            player_safe_text = payload.playerSafeText,
            paper_references_json = encodeStringList(payload.paperReferences),
            tags_json = encodeStringList(payload.tags),
        )
    }

    private fun updatePayloadRow(id: Uuid, payload: ZonePayload) {
        database.zonePayloadQueries.updateZonePayload(
            summary = payload.summary,
            area = payload.area,
            presentation = payload.presentation,
            space = payload.space,
            exploration = payload.exploration,
            interactives_json = encodeStringList(payload.interactives),
            clues_json = encodeStringList(payload.clues),
            checks_json = encodeStringList(payload.checks),
            consequences_json = encodeStringList(payload.consequences),
            encounter_brief = payload.encounterBrief,
            dm_guidance = payload.dmGuidance,
            player_safe_text = payload.playerSafeText,
            paper_references_json = encodeStringList(payload.paperReferences),
            tags_json = encodeStringList(payload.tags),
            content_id = id.toString(),
        )
    }

    private fun mapPayload(
        contentId: String,
        summary: String,
        area: String,
        presentation: String,
        space: String,
        exploration: String,
        interactivesJson: String,
        cluesJson: String,
        checksJson: String,
        consequencesJson: String,
        encounterBrief: String,
        dmGuidance: String,
        playerSafeText: String,
        paperReferencesJson: String,
        tagsJson: String,
    ): ZonePayload {
        require(contentId.isNotBlank()) { "Zone payload content id must not be blank." }
        return ZonePayload(
            summary = summary,
            area = area,
            presentation = presentation,
            space = space,
            exploration = exploration,
            interactives = decodeStringList(interactivesJson),
            clues = decodeStringList(cluesJson),
            checks = decodeStringList(checksJson),
            consequences = decodeStringList(consequencesJson),
            encounterBrief = encounterBrief,
            dmGuidance = dmGuidance,
            playerSafeText = playerSafeText,
            paperReferences = decodeStringList(paperReferencesJson),
            tags = decodeStringList(tagsJson),
        )
    }

    private fun encodeStringList(values: List<String>): String = payloadJson.encodeToString(values)

    private fun decodeStringList(raw: String): List<String> = payloadJson.decodeFromString<List<String>>(raw)
}
