package io.github.mrsimkin.dndcustomaid.shared.content

import kotlinx.serialization.Serializable

@Serializable
enum class PlaceKind {
    PLACE,
    SHOP,
}

@Serializable
data class PlacePayload(
    val kind: PlaceKind = PlaceKind.PLACE,
    val summary: String = "",
    val area: String = "",
    val function: String = "",
    val presentation: String = "",
    val services: List<String> = emptyList(),
    val interactives: List<String> = emptyList(),
    val hooks: List<String> = emptyList(),
    val playerSafeText: String = "",
    val dmNotes: String = "",
    val paperReferences: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
)

@Serializable
data class PlaceContent(
    val item: ReusableContentItem,
    val payload: PlacePayload,
) {
    init {
        require(item.family == ReusableContentFamily.PLACE) {
            "PlaceContent must wrap PLACE reusable content."
        }
    }
}
