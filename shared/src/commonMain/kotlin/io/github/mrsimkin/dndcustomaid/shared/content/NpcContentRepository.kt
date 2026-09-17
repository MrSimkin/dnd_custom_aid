package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class NpcContentRepository(
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
        payload: NpcPayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): NpcContent {
        var result: NpcContent? = null
        database.transaction {
            val item = reusableContent.createPersonal(
                ownerAccountId = ownerAccountId,
                family = ReusableContentFamily.NPC,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = NpcContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun createCampaign(
        campaignId: Uuid,
        rawDisplayName: String,
        payload: NpcPayload,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): NpcContent {
        var result: NpcContent? = null
        database.transaction {
            val item = reusableContent.createCampaign(
                campaignId = campaignId,
                family = ReusableContentFamily.NPC,
                rawDisplayName = rawDisplayName,
                nowEpochSeconds = nowEpochSeconds,
                id = id,
            )
            insertPayload(item.identity.id, payload)
            result = NpcContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun npc(id: Uuid, includeDeleted: Boolean = false): NpcContent? {
        val item = reusableContent.content(id, includeDeleted = includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.NPC) return null
        val payload = requireNotNull(payload(id)) {
            "NPC reusable content is missing its persisted NPC payload."
        }
        return NpcContent(item, payload)
    }

    fun copyPersonalToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
        newId: Uuid = Uuid.random(),
    ): NpcContent {
        var result: NpcContent? = null
        database.transaction {
            val source = requireNotNull(npc(sourceId)) {
                "NPC source must exist and be active before it can be copied."
            }
            val copiedItem = reusableContent.copyPersonalToCampaign(
                sourceId = sourceId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
                newId = newId,
            )
            require(copiedItem.family == ReusableContentFamily.NPC) {
                "NPC copy must retain the NPC family."
            }
            insertPayload(copiedItem.identity.id, source.payload)
            result = NpcContent(copiedItem, source.payload)
        }
        return checkNotNull(result)
    }

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        rawDisplayName: String,
        payload: NpcPayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision {
        val displayName = rawDisplayName.trim()
        require(displayName.isNotEmpty()) { "NPC display name must not be blank." }

        return reusableContent.mutateContent(
            id = id,
            expectedRevision = expectedRevision,
            updatedAtEpochSeconds = updatedAtEpochSeconds,
        ) { current ->
            require(current.family == ReusableContentFamily.NPC) {
                "NPC updates require NPC reusable content."
            }
            require(this.payload(id) != null) {
                "NPC reusable content is missing its persisted NPC payload."
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
        payload: NpcPayload,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision = reusableContent.mutateContent(
        id = id,
        expectedRevision = expectedRevision,
        updatedAtEpochSeconds = updatedAtEpochSeconds,
    ) { current ->
        require(current.family == ReusableContentFamily.NPC) {
            "NPC payload updates require NPC reusable content."
        }
        require(this.payload(id) != null) {
            "NPC reusable content is missing its persisted NPC payload."
        }
        updatePayloadRow(id, payload)
    }

    fun tombstone(
        id: Uuid,
        expectedRevision: Revision,
        deletedAtEpochSeconds: Long,
    ): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) {
            "NPC reusable content must exist before it can be deleted."
        }
        require(current.family == ReusableContentFamily.NPC) {
            "NPC deletion requires NPC reusable content."
        }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    private fun payload(id: Uuid): NpcPayload? =
        database.npcPayloadQueries.selectNpcPayloadByContentId(
            content_id = id.toString(),
            mapper = ::mapPayload,
        ).executeAsOneOrNull()

    private fun insertPayload(id: Uuid, payload: NpcPayload) {
        database.npcPayloadQueries.insertNpcPayload(
            content_id = id.toString(),
            concept_role = payload.conceptRole,
            appearance_first_impression = payload.appearanceFirstImpression,
            personality_manner = payload.personalityManner,
            wants_fears_needs = payload.wantsFearsNeeds,
            can_offer = payload.canOffer,
            limits_refusals = payload.limitsRefusals,
            relationship_context = payload.relationshipContext,
            identity_details = payload.identityDetails,
            voice_mannerisms = payload.voiceMannerisms,
            motivations = payload.motivations,
            values_beliefs = payload.valuesBeliefs,
            relationships = payload.relationships,
            history = payload.history,
            secrets = payload.secrets,
            knowledge = payload.knowledge,
            goals = payload.goals,
            resources = payload.resources,
            affiliations = payload.affiliations,
            places = payload.places,
            adventure_scene_links = payload.adventureSceneLinks,
            dm_guidance = payload.dmGuidance,
            combat_payload_json = encodeCombatMechanics(payload.combatMechanics),
            notes = payload.notes,
        )
    }

    private fun updatePayloadRow(id: Uuid, payload: NpcPayload) {
        database.npcPayloadQueries.updateNpcPayload(
            concept_role = payload.conceptRole,
            appearance_first_impression = payload.appearanceFirstImpression,
            personality_manner = payload.personalityManner,
            wants_fears_needs = payload.wantsFearsNeeds,
            can_offer = payload.canOffer,
            limits_refusals = payload.limitsRefusals,
            relationship_context = payload.relationshipContext,
            identity_details = payload.identityDetails,
            voice_mannerisms = payload.voiceMannerisms,
            motivations = payload.motivations,
            values_beliefs = payload.valuesBeliefs,
            relationships = payload.relationships,
            history = payload.history,
            secrets = payload.secrets,
            knowledge = payload.knowledge,
            goals = payload.goals,
            resources = payload.resources,
            affiliations = payload.affiliations,
            places = payload.places,
            adventure_scene_links = payload.adventureSceneLinks,
            dm_guidance = payload.dmGuidance,
            combat_payload_json = encodeCombatMechanics(payload.combatMechanics),
            notes = payload.notes,
            content_id = id.toString(),
        )
    }

    private fun mapPayload(
        contentId: String,
        conceptRole: String,
        appearanceFirstImpression: String,
        personalityManner: String,
        wantsFearsNeeds: String,
        canOffer: String,
        limitsRefusals: String,
        relationshipContext: String,
        identityDetails: String,
        voiceMannerisms: String,
        motivations: String,
        valuesBeliefs: String,
        relationships: String,
        history: String,
        secrets: String,
        knowledge: String,
        goals: String,
        resources: String,
        affiliations: String,
        places: String,
        adventureSceneLinks: String,
        dmGuidance: String,
        combatPayloadJson: String?,
        notes: String,
    ): NpcPayload {
        require(contentId.isNotBlank()) { "NPC payload content id must not be blank." }
        return NpcPayload(
            conceptRole = conceptRole,
            appearanceFirstImpression = appearanceFirstImpression,
            personalityManner = personalityManner,
            wantsFearsNeeds = wantsFearsNeeds,
            canOffer = canOffer,
            limitsRefusals = limitsRefusals,
            relationshipContext = relationshipContext,
            identityDetails = identityDetails,
            voiceMannerisms = voiceMannerisms,
            motivations = motivations,
            valuesBeliefs = valuesBeliefs,
            relationships = relationships,
            history = history,
            secrets = secrets,
            knowledge = knowledge,
            goals = goals,
            resources = resources,
            affiliations = affiliations,
            places = places,
            adventureSceneLinks = adventureSceneLinks,
            dmGuidance = dmGuidance,
            combatMechanics = decodeCombatMechanics(combatPayloadJson),
            notes = notes,
        )
    }

    private fun encodeCombatMechanics(payload: CreaturePayload?): String? =
        payload?.let { payloadJson.encodeToString(it) }

    private fun decodeCombatMechanics(raw: String?): CreaturePayload? =
        raw?.let { payloadJson.decodeFromString<CreaturePayload>(it) }
}
