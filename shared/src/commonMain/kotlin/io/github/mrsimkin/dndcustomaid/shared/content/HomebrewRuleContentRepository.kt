package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class HomebrewRuleContentRepository(
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
        payload: HomebrewRulePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): HomebrewRuleContent {
        var result: HomebrewRuleContent? = null
        database.transaction {
            val item = reusableContent.createPersonal(
                ownerAccountId = ownerAccountId,
                family = ReusableContentFamily.HOMEBREW_RULE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = HomebrewRuleContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun createCampaign(
        campaignId: Uuid,
        rawDisplayName: String,
        payload: HomebrewRulePayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): HomebrewRuleContent {
        var result: HomebrewRuleContent? = null
        database.transaction {
            val item = reusableContent.createCampaign(
                campaignId = campaignId,
                family = ReusableContentFamily.HOMEBREW_RULE,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = HomebrewRuleContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun rule(id: Uuid, includeDeleted: Boolean = false): HomebrewRuleContent? {
        val item = reusableContent.content(id, includeDeleted = includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.HOMEBREW_RULE) return null
        val payload = requireNotNull(payload(id)) {
            "Homebrew/Rule reusable content is missing its persisted payload."
        }
        return HomebrewRuleContent(item, payload)
    }

    fun copyPersonalToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
        newId: Uuid = Uuid.random(),
    ): HomebrewRuleContent {
        var result: HomebrewRuleContent? = null
        database.transaction {
            val source = requireNotNull(rule(sourceId)) {
                "Homebrew/Rule source must exist and be active before it can be copied."
            }
            val copiedItem = reusableContent.copyPersonalToCampaign(
                sourceId = sourceId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
                newId = newId,
            )
            require(copiedItem.family == ReusableContentFamily.HOMEBREW_RULE) {
                "Homebrew/Rule copy must retain the HOMEBREW_RULE family."
            }
            insertPayload(copiedItem.identity.id, source.payload)
            result = HomebrewRuleContent(copiedItem, source.payload)
        }
        return checkNotNull(result)
    }

    fun updatePayload(
        id: Uuid,
        expectedRevision: Revision,
        payload: HomebrewRulePayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision = reusableContent.mutateContent(
        id = id,
        expectedRevision = expectedRevision,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
    ) { current ->
        require(current.family == ReusableContentFamily.HOMEBREW_RULE) {
            "Homebrew/Rule payload updates require HOMEBREW_RULE reusable content."
        }
        require(this.payload(id) != null) {
            "Homebrew/Rule reusable content is missing its persisted payload."
        }
        updatePayloadRow(id, payload)
    }

    fun tombstone(
        id: Uuid,
        expectedRevision: Revision,
        deletedAtEpochSeconds: Long,
    ): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) {
            "Homebrew/Rule reusable content must exist before it can be deleted."
        }
        require(current.family == ReusableContentFamily.HOMEBREW_RULE) {
            "Homebrew/Rule deletion requires HOMEBREW_RULE reusable content."
        }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    private fun payload(id: Uuid): HomebrewRulePayload? =
        database.homebrewRulePayloadQueries.selectHomebrewRulePayloadByContentId(
            content_id = id.toString(),
            mapper = ::mapPayload,
        ).executeAsOneOrNull()

    private fun insertPayload(id: Uuid, payload: HomebrewRulePayload) {
        database.homebrewRulePayloadQueries.insertHomebrewRulePayload(
            content_id = id.toString(),
            summary = payload.summary,
            body = payload.body,
            category = payload.category,
            rationale = payload.rationale,
            examples_json = encodeStringList(payload.examples),
            related_references_json = encodeStringList(payload.relatedReferences),
            tags_json = encodeStringList(payload.tags),
            lifecycle = payload.lifecycle.name,
            notes = payload.notes,
        )
    }

    private fun updatePayloadRow(id: Uuid, payload: HomebrewRulePayload) {
        database.homebrewRulePayloadQueries.updateHomebrewRulePayload(
            summary = payload.summary,
            body = payload.body,
            category = payload.category,
            rationale = payload.rationale,
            examples_json = encodeStringList(payload.examples),
            related_references_json = encodeStringList(payload.relatedReferences),
            tags_json = encodeStringList(payload.tags),
            lifecycle = payload.lifecycle.name,
            notes = payload.notes,
            content_id = id.toString(),
        )
    }

    private fun mapPayload(
        contentId: String,
        summary: String,
        body: String,
        category: String,
        rationale: String,
        examplesJson: String,
        relatedReferencesJson: String,
        tagsJson: String,
        lifecycle: String,
        notes: String,
    ): HomebrewRulePayload {
        require(contentId.isNotBlank()) { "Homebrew/Rule payload content id must not be blank." }
        return HomebrewRulePayload(
            summary = summary,
            body = body,
            category = category,
            rationale = rationale,
            examples = decodeStringList(examplesJson),
            relatedReferences = decodeStringList(relatedReferencesJson),
            tags = decodeStringList(tagsJson),
            lifecycle = HomebrewRuleLifecycle.valueOf(lifecycle),
            notes = notes,
        )
    }

    private fun encodeStringList(values: List<String>): String = payloadJson.encodeToString(values)

    private fun decodeStringList(raw: String): List<String> = payloadJson.decodeFromString<List<String>>(raw)
}
