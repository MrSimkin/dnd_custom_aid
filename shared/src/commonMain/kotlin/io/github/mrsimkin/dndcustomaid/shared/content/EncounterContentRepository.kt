package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class EncounterContentRepository(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
    private val creatures: CreatureContentRepository = CreatureContentRepository(database),
    private val npcs: NpcContentRepository = NpcContentRepository(database),
    private val payloadJson: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    },
) {
    fun createPersonal(
        ownerAccountId: Uuid,
        rawDisplayName: String,
        payload: EncounterPayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): EncounterContent {
        var result: EncounterContent? = null
        database.transaction {
            validateParticipantDependencies(payload.participants, ContentScope.Personal(ownerAccountId))
            val item = reusableContent.createPersonal(
                ownerAccountId = ownerAccountId,
                family = ReusableContentFamily.ENCOUNTER,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = EncounterContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun createCampaign(
        campaignId: Uuid,
        rawDisplayName: String,
        payload: EncounterPayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): EncounterContent {
        var result: EncounterContent? = null
        database.transaction {
            validateParticipantDependencies(payload.participants, ContentScope.Campaign(campaignId))
            val item = reusableContent.createCampaign(
                campaignId = campaignId,
                family = ReusableContentFamily.ENCOUNTER,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = EncounterContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun encounter(id: Uuid, includeDeleted: Boolean = false): EncounterContent? {
        val item = reusableContent.content(id, includeDeleted = includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.ENCOUNTER) return null
        val payload = requireNotNull(payload(id)) {
            "Encounter reusable content is missing its persisted payload."
        }
        return EncounterContent(item, payload)
    }

    fun copyPersonalToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
        newId: Uuid = Uuid.random(),
    ): EncounterContent {
        var result: EncounterContent? = null
        database.transaction {
            val source = requireNotNull(encounter(sourceId)) {
                "Encounter source must exist and be active before it can be copied."
            }
            val sourceScope = source.item.identity.scope
            require(sourceScope is ContentScope.Personal) {
                "Only Personal Encounters can use the Personal-to-Campaign copy operation."
            }
            validateParticipantDependencies(source.payload.participants, sourceScope)

            val copiedDependencyIds = mutableMapOf<Uuid, Uuid>()
            val copiedParticipants = source.payload.participants.map { participant ->
                val sourceContentId = participant.sourceContentId ?: return@map participant
                val copiedDependencyId = copiedDependencyIds.getOrPut(sourceContentId) {
                    copyDependencyToCampaign(
                        sourceId = sourceContentId,
                        campaignId = campaignId,
                        copiedAtEpochSeconds = copiedAtEpochSeconds,
                    )
                }
                participant.copy(sourceContentId = copiedDependencyId)
            }

            val copiedItem = reusableContent.copyPersonalToCampaign(
                sourceId = sourceId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
                newId = newId,
            )
            require(copiedItem.family == ReusableContentFamily.ENCOUNTER) {
                "Encounter copy must retain the ENCOUNTER family."
            }
            val copiedPayload = source.payload.copy(participants = copiedParticipants)
            insertPayload(copiedItem.identity.id, copiedPayload)
            result = EncounterContent(copiedItem, copiedPayload)
        }
        return checkNotNull(result)
    }

    fun updatePayload(
        id: Uuid,
        expectedRevision: Revision,
        payload: EncounterPayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision = reusableContent.mutateContent(
        id = id,
        expectedRevision = expectedRevision,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
    ) { current ->
        require(current.family == ReusableContentFamily.ENCOUNTER) {
            "Encounter payload updates require ENCOUNTER reusable content."
        }
        require(this.payload(id) != null) {
            "Encounter reusable content is missing its persisted payload."
        }
        validateParticipantDependencies(payload.participants, current.identity.scope)
        updatePayloadRow(id, payload)
    }

    fun tombstone(
        id: Uuid,
        expectedRevision: Revision,
        deletedAtEpochSeconds: Long,
    ): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) {
            "Encounter reusable content must exist before it can be deleted."
        }
        require(current.family == ReusableContentFamily.ENCOUNTER) {
            "Encounter deletion requires ENCOUNTER reusable content."
        }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    private fun validateParticipantDependencies(
        participants: List<EncounterParticipant>,
        expectedScope: ContentScope,
    ) {
        participants.mapNotNull { it.sourceContentId }.distinct().forEach { sourceId ->
            val dependency = requireNotNull(reusableContent.content(sourceId)) {
                "Encounter participant dependency must exist and be active."
            }
            require(
                dependency.family == ReusableContentFamily.CREATURE ||
                    dependency.family == ReusableContentFamily.NPC,
            ) {
                "Encounter participant dependencies must be CREATURE or NPC reusable content."
            }
            require(dependency.identity.scope == expectedScope) {
                "Encounter participant dependency scope must match the Encounter scope."
            }
        }
    }

    private fun copyDependencyToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
    ): Uuid {
        val dependency = requireNotNull(reusableContent.content(sourceId)) {
            "Encounter participant dependency must exist and be active before copy."
        }
        require(dependency.identity.scope is ContentScope.Personal) {
            "Encounter dependency copy requires Personal source content."
        }
        return when (dependency.family) {
            ReusableContentFamily.CREATURE -> creatures.copyPersonalToCampaign(
                sourceId = sourceId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
            ).item.identity.id
            ReusableContentFamily.NPC -> npcs.copyPersonalToCampaign(
                sourceId = sourceId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
            ).item.identity.id
            else -> error("Encounter dependencies support only CREATURE and NPC reusable content.")
        }
    }

    private fun payload(id: Uuid): EncounterPayload? =
        database.encounterPayloadQueries.selectEncounterPayloadByContentId(
            content_id = id.toString(),
            mapper = ::mapPayload,
        ).executeAsOneOrNull()

    private fun insertPayload(id: Uuid, payload: EncounterPayload) {
        database.encounterPayloadQueries.insertEncounterPayload(
            content_id = id.toString(),
            summary = payload.summary,
            environment = payload.environment,
            encounter_context = payload.context,
            dm_guidance = payload.dmGuidance,
            participants_json = payloadJson.encodeToString(payload.participants),
            tags_json = payloadJson.encodeToString(payload.tags),
            notes = payload.notes,
        )
    }

    private fun updatePayloadRow(id: Uuid, payload: EncounterPayload) {
        database.encounterPayloadQueries.updateEncounterPayload(
            summary = payload.summary,
            environment = payload.environment,
            encounter_context = payload.context,
            dm_guidance = payload.dmGuidance,
            participants_json = payloadJson.encodeToString(payload.participants),
            tags_json = payloadJson.encodeToString(payload.tags),
            notes = payload.notes,
            content_id = id.toString(),
        )
    }

    private fun mapPayload(
        contentId: String,
        summary: String,
        environment: String,
        encounterContext: String,
        dmGuidance: String,
        participantsJson: String,
        tagsJson: String,
        notes: String,
    ): EncounterPayload {
        require(contentId.isNotBlank()) { "Encounter payload content id must not be blank." }
        return EncounterPayload(
            summary = summary,
            environment = environment,
            context = encounterContext,
            dmGuidance = dmGuidance,
            participants = payloadJson.decodeFromString<List<EncounterParticipant>>(participantsJson),
            tags = payloadJson.decodeFromString<List<String>>(tagsJson),
            notes = notes,
        )
    }
}
