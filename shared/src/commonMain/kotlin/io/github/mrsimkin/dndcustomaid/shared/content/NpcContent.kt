package io.github.mrsimkin.dndcustomaid.shared.content

import kotlinx.serialization.Serializable

@Serializable
data class NpcPayload(
    val conceptRole: String = "",
    val appearanceFirstImpression: String = "",
    val personalityManner: String = "",
    val wantsFearsNeeds: String = "",
    val canOffer: String = "",
    val limitsRefusals: String = "",
    val relationshipContext: String = "",
    val identityDetails: String = "",
    val voiceMannerisms: String = "",
    val motivations: String = "",
    val valuesBeliefs: String = "",
    val relationships: String = "",
    val history: String = "",
    val secrets: String = "",
    val knowledge: String = "",
    val goals: String = "",
    val resources: String = "",
    val affiliations: String = "",
    val places: String = "",
    val adventureSceneLinks: String = "",
    val dmGuidance: String = "",
    val combatMechanics: CreaturePayload? = null,
    val notes: String = "",
)

@Serializable
data class NpcContent(
    val item: ReusableContentItem,
    val payload: NpcPayload,
) {
    init {
        require(item.family == ReusableContentFamily.NPC) {
            "NpcContent must wrap NPC reusable content."
        }
    }
}
