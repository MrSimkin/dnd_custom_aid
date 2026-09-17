package io.github.mrsimkin.dndcustomaid.shared.content

import kotlinx.serialization.Serializable

@Serializable
data class ScenePayload(
    val purpose: String = "",
    val possibleNextScenes: List<String> = emptyList(),
    val references: List<String> = emptyList(),
    val notes: String = "",
)

@Serializable
data class SceneContent(
    val item: ReusableContentItem,
    val payload: ScenePayload,
) {
    init {
        require(item.family == ReusableContentFamily.SCENE) {
            "SceneContent must wrap SCENE reusable content."
        }
    }
}
