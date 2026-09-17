package io.github.mrsimkin.dndcustomaid.shared.content

import kotlinx.serialization.Serializable

@Serializable
data class ZonePayload(
    val summary: String = "",
    val area: String = "",
    val presentation: String = "",
    val space: String = "",
    val exploration: String = "",
    val interactives: List<String> = emptyList(),
    val clues: List<String> = emptyList(),
    val checks: List<String> = emptyList(),
    val consequences: List<String> = emptyList(),
    val encounterBrief: String = "",
    val dmGuidance: String = "",
    val playerSafeText: String = "",
    val paperReferences: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
)

@Serializable
data class ZoneContent(
    val item: ReusableContentItem,
    val payload: ZonePayload,
) {
    init {
        require(item.family == ReusableContentFamily.ZONE) {
            "ZoneContent must wrap ZONE reusable content."
        }
    }
}
