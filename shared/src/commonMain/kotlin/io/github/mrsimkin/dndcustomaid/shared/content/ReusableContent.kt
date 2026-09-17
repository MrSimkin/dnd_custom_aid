package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.ScopedObjectIdentity
import kotlinx.serialization.Serializable

@Serializable
enum class ReusableContentFamily {
    CREATURE,
    NPC,
    HOMEBREW_RULE,
    PLACE,
    ZONE,
    ENCOUNTER,
    SCENE,
}

@Serializable
data class ReusableContentItem(
    val identity: ScopedObjectIdentity,
    val family: ReusableContentFamily,
    val displayName: String,
    val createdAtEpochSeconds: Long,
    val updatedAtEpochSeconds: Long,
    val deletedAtEpochSeconds: Long? = null,
) {
    val isDeleted: Boolean
        get() = deletedAtEpochSeconds != null

    init {
        require(identity.scope is ContentScope.Personal || identity.scope is ContentScope.Campaign) {
            "Reusable authored content must be Personal or Campaign scoped."
        }
        require(displayName.isNotBlank()) { "Reusable content display name must not be blank." }
        require(createdAtEpochSeconds >= 0) { "Created timestamp must not be negative." }
        require(updatedAtEpochSeconds >= createdAtEpochSeconds) {
            "Updated timestamp must not precede creation."
        }
        require(deletedAtEpochSeconds == null || deletedAtEpochSeconds >= createdAtEpochSeconds) {
            "Deletion timestamp must not precede creation."
        }
    }
}
