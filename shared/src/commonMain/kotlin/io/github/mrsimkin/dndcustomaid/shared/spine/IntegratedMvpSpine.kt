package io.github.mrsimkin.dndcustomaid.shared.spine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AccountIdentity(
    val id: Uuid,
    val externalSubject: String? = null,
    val displayName: String? = null,
)

@Serializable
enum class CampaignRole {
    DM,
    PLAYER,
}

@Serializable
enum class CampaignMembershipStatus {
    ACTIVE,
    KICKED,
    BANNED,
}

@Serializable
data class CampaignMembership(
    val campaignId: Uuid,
    val accountId: Uuid,
    val role: CampaignRole,
    val status: CampaignMembershipStatus = CampaignMembershipStatus.ACTIVE,
) {
    val canUseHostedCampaign: Boolean
        get() = status == CampaignMembershipStatus.ACTIVE
}

@Serializable
data class PcAuthority(
    val characterId: Uuid,
    val ownerAccountId: Uuid? = null,
    val controllerAccountId: Uuid? = null,
)

@Serializable
data class Revision(val value: Long = 0) : Comparable<Revision> {
    init {
        require(value >= 0) { "Revision must not be negative." }
    }

    fun next(): Revision {
        check(value < Long.MAX_VALUE) { "Revision cannot advance beyond Long.MAX_VALUE." }
        return Revision(value + 1)
    }

    override fun compareTo(other: Revision): Int = value.compareTo(other.value)
}

@Serializable
data class SyncMetadata(
    val revision: Revision = Revision(),
    val deletedAtEpochSeconds: Long? = null,
) {
    val isDeleted: Boolean
        get() = deletedAtEpochSeconds != null

    init {
        require(deletedAtEpochSeconds == null || deletedAtEpochSeconds >= 0) {
            "Deletion timestamp must not be negative."
        }
    }
}

sealed interface RevisionDecision {
    data class Accepted(val nextRevision: Revision) : RevisionDecision
    data class Stale(val expected: Revision, val actual: Revision) : RevisionDecision
    data class Deleted(val actual: Revision) : RevisionDecision
}

fun SyncMetadata.checkMutation(expectedRevision: Revision): RevisionDecision = when {
    isDeleted -> RevisionDecision.Deleted(revision)
    expectedRevision != revision -> RevisionDecision.Stale(expectedRevision, revision)
    else -> RevisionDecision.Accepted(revision.next())
}

@Serializable
sealed interface ContentScope {
    @Serializable
    @SerialName("PERSONAL")
    data class Personal(val ownerAccountId: Uuid) : ContentScope

    @Serializable
    @SerialName("CAMPAIGN")
    data class Campaign(val campaignId: Uuid) : ContentScope

    @Serializable
    @SerialName("SYSTEM")
    data object System : ContentScope

    @Serializable
    @SerialName("OFFICIAL")
    data class Official(val sourceKey: String) : ContentScope {
        init {
            require(sourceKey.isNotBlank()) { "Official source key must not be blank." }
        }
    }
}

@Serializable
data class CopyProvenance(
    val sourceObjectId: Uuid,
    val sourceScope: ContentScope,
    val copiedAtEpochSeconds: Long? = null,
) {
    init {
        require(copiedAtEpochSeconds == null || copiedAtEpochSeconds >= 0) {
            "Copy timestamp must not be negative."
        }
    }
}

@Serializable
data class ScopedObjectIdentity(
    val id: Uuid,
    val scope: ContentScope,
    val revision: Revision = Revision(),
    val provenance: CopyProvenance? = null,
) {
    fun independentCampaignCopy(
        newId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long? = null,
    ): ScopedObjectIdentity {
        require(newId != id) { "Independent copies must receive a new object identity." }
        return ScopedObjectIdentity(
            id = newId,
            scope = ContentScope.Campaign(campaignId),
            revision = Revision(),
            provenance = CopyProvenance(
                sourceObjectId = id,
                sourceScope = scope,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
            ),
        )
    }
}
