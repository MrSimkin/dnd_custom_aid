package io.github.mrsimkin.dndcustomaid.shared.content

import kotlinx.serialization.Serializable

@Serializable
enum class HomebrewRuleLifecycle {
    DRAFT,
    ACTIVE,
    RETIRED,
}

@Serializable
data class HomebrewRulePayload(
    val summary: String = "",
    val body: String = "",
    val category: String = "",
    val rationale: String = "",
    val examples: List<String> = emptyList(),
    val relatedReferences: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val lifecycle: HomebrewRuleLifecycle = HomebrewRuleLifecycle.DRAFT,
    val notes: String = "",
)

@Serializable
data class HomebrewRuleContent(
    val item: ReusableContentItem,
    val payload: HomebrewRulePayload,
) {
    init {
        require(item.family == ReusableContentFamily.HOMEBREW_RULE) {
            "HomebrewRuleContent must wrap HOMEBREW_RULE reusable content."
        }
    }
}
