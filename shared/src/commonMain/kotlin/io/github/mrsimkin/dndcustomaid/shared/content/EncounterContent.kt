package io.github.mrsimkin.dndcustomaid.shared.content

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
enum class EncounterParticipantReadiness {
    EXPECTED,
    RESERVE,
    CONDITIONAL,
}

@Serializable
data class EncounterParticipant(
    val sourceContentId: Uuid? = null,
    val label: String = "",
    val quantity: Int = 1,
    val readiness: EncounterParticipantReadiness = EncounterParticipantReadiness.EXPECTED,
    val condition: String = "",
    val overrides: String = "",
    val notes: String = "",
) {
    init {
        require(quantity > 0) { "Encounter participant quantity must be positive." }
        require(sourceContentId != null || label.isNotBlank()) {
            "Encounter participant must reference reusable content or provide a label."
        }
    }
}

@Serializable
data class EncounterPayload(
    val summary: String = "",
    val environment: String = "",
    val context: String = "",
    val dmGuidance: String = "",
    val participants: List<EncounterParticipant> = emptyList(),
    val tags: List<String> = emptyList(),
    val notes: String = "",
)

@Serializable
data class EncounterContent(
    val item: ReusableContentItem,
    val payload: EncounterPayload,
) {
    init {
        require(item.family == ReusableContentFamily.ENCOUNTER) {
            "EncounterContent must wrap ENCOUNTER reusable content."
        }
    }
}
