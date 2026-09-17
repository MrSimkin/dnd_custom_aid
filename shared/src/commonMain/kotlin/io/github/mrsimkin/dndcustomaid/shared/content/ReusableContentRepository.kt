package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.CopyProvenance
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import io.github.mrsimkin.dndcustomaid.shared.spine.ScopedObjectIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.SyncMetadata
import io.github.mrsimkin.dndcustomaid.shared.spine.checkMutation
import kotlin.uuid.Uuid

class ReusableContentRepository(
    private val database: AppDatabase,
    private val spine: IntegratedSpineRepository = IntegratedSpineRepository(database),
) {
    fun createPersonal(
        ownerAccountId: Uuid,
        family: ReusableContentFamily,
        rawDisplayName: String,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): ReusableContentItem {
        require(spine.account(ownerAccountId) != null) {
            "Personal reusable content owner must already exist locally."
        }
        return insertNew(
            identity = ScopedObjectIdentity(
                id = id,
                scope = ContentScope.Personal(ownerAccountId),
            ),
            family = family,
            rawDisplayName = rawDisplayName,
            createdAtEpochSeconds = nowEpochSeconds,
        )
    }

    fun createCampaign(
        campaignId: Uuid,
        family: ReusableContentFamily,
        rawDisplayName: String,
        nowEpochSeconds: Long,
        id: Uuid = Uuid.random(),
    ): ReusableContentItem {
        requireCampaignExists(campaignId)
        return insertNew(
            identity = ScopedObjectIdentity(
                id = id,
                scope = ContentScope.Campaign(campaignId),
            ),
            family = family,
            rawDisplayName = rawDisplayName,
            createdAtEpochSeconds = nowEpochSeconds,
        )
    }

    fun content(id: Uuid, includeDeleted: Boolean = false): ReusableContentItem? {
        val item = database.reusableContentQueries.selectReusableContentById(id.toString(), ::mapItem)
            .executeAsOneOrNull()
        return item?.takeIf { includeDeleted || !it.isDeleted }
    }

    fun listPersonal(
        ownerAccountId: Uuid,
        family: ReusableContentFamily? = null,
    ): List<ReusableContentItem> = listByScope(
        scope = ContentScope.Personal(ownerAccountId),
        family = family,
    )

    fun listCampaign(
        campaignId: Uuid,
        family: ReusableContentFamily? = null,
    ): List<ReusableContentItem> = listByScope(
        scope = ContentScope.Campaign(campaignId),
        family = family,
    )

    fun copyPersonalToCampaign(
        sourceId: Uuid,
        campaignId: Uuid,
        copiedAtEpochSeconds: Long,
        newId: Uuid = Uuid.random(),
    ): ReusableContentItem {
        require(copiedAtEpochSeconds >= 0) { "Copy timestamp must not be negative." }
        val source = requireNotNull(content(sourceId)) { "Reusable content source must exist and be active." }
        require(source.identity.scope is ContentScope.Personal) {
            "Only Personal reusable content can use the Personal-to-Campaign copy operation."
        }
        require(copiedAtEpochSeconds >= source.createdAtEpochSeconds) {
            "Copy timestamp must not precede source creation."
        }
        requireCampaignExists(campaignId)

        return insertNew(
            identity = source.identity.independentCampaignCopy(
                newId = newId,
                campaignId = campaignId,
                copiedAtEpochSeconds = copiedAtEpochSeconds,
            ),
            family = source.family,
            rawDisplayName = source.displayName,
            createdAtEpochSeconds = copiedAtEpochSeconds,
        )
    }

    fun rename(
        id: Uuid,
        expectedRevision: Revision,
        rawDisplayName: String,
        updatedAtEpochSeconds: Long,
    ): RevisionDecision {
        val displayName = normalizeName(rawDisplayName)
        var result: RevisionDecision? = null

        database.transaction {
            val current = requireNotNull(content(id, includeDeleted = true)) {
                "Reusable content must exist before it can be renamed."
            }
            require(updatedAtEpochSeconds >= current.updatedAtEpochSeconds) {
                "Updated timestamp must not move backwards."
            }

            val metadata = SyncMetadata(
                revision = current.identity.revision,
                deletedAtEpochSeconds = current.deletedAtEpochSeconds,
            )
            val decision = metadata.checkMutation(expectedRevision)
            if (decision is RevisionDecision.Accepted) {
                database.reusableContentQueries.updateReusableContentName(
                    display_name = displayName,
                    updated_at_epoch_seconds = updatedAtEpochSeconds,
                    id = id.toString(),
                )
                spine.putSyncMetadata(
                    objectType = OBJECT_TYPE,
                    objectId = id,
                    metadata = metadata.copy(revision = decision.nextRevision),
                )
            }
            result = decision
        }

        return checkNotNull(result)
    }

    fun tombstone(
        id: Uuid,
        expectedRevision: Revision,
        deletedAtEpochSeconds: Long,
    ): RevisionDecision {
        var result: RevisionDecision? = null

        database.transaction {
            val current = requireNotNull(content(id, includeDeleted = true)) {
                "Reusable content must exist before it can be deleted."
            }
            require(deletedAtEpochSeconds >= current.updatedAtEpochSeconds) {
                "Deletion timestamp must not precede the latest update."
            }

            val metadata = SyncMetadata(
                revision = current.identity.revision,
                deletedAtEpochSeconds = current.deletedAtEpochSeconds,
            )
            val decision = metadata.checkMutation(expectedRevision)
            if (decision is RevisionDecision.Accepted) {
                database.reusableContentQueries.touchReusableContent(
                    updated_at_epoch_seconds = deletedAtEpochSeconds,
                    id = id.toString(),
                )
                spine.putSyncMetadata(
                    objectType = OBJECT_TYPE,
                    objectId = id,
                    metadata = SyncMetadata(
                        revision = decision.nextRevision,
                        deletedAtEpochSeconds = deletedAtEpochSeconds,
                    ),
                )
            }
            result = decision
        }

        return checkNotNull(result)
    }

    private fun insertNew(
        identity: ScopedObjectIdentity,
        family: ReusableContentFamily,
        rawDisplayName: String,
        createdAtEpochSeconds: Long,
    ): ReusableContentItem {
        require(identity.revision == Revision()) { "New reusable content must start at revision 0." }
        require(createdAtEpochSeconds >= 0) { "Created timestamp must not be negative." }
        require(content(identity.id, includeDeleted = true) == null) {
            "Reusable content id already exists."
        }
        val displayName = normalizeName(rawDisplayName)
        val scope = encodeScope(identity.scope, authoredOnly = true)
        val provenance = identity.provenance
        val sourceScope = provenance?.let { encodeScope(it.sourceScope, authoredOnly = false) }

        database.transaction {
            database.reusableContentQueries.insertReusableContent(
                id = identity.id.toString(),
                family = family.name,
                display_name = displayName,
                scope_kind = scope.kind,
                scope_ref = requireNotNull(scope.ref),
                source_object_id = provenance?.sourceObjectId?.toString(),
                source_scope_kind = sourceScope?.kind,
                source_scope_ref = sourceScope?.ref,
                copied_at_epoch_seconds = provenance?.copiedAtEpochSeconds,
                created_at_epoch_seconds = createdAtEpochSeconds,
                updated_at_epoch_seconds = createdAtEpochSeconds,
            )
            spine.putSyncMetadata(
                objectType = OBJECT_TYPE,
                objectId = identity.id,
                metadata = SyncMetadata(revision = identity.revision),
            )
        }

        return requireNotNull(content(identity.id, includeDeleted = true))
    }

    private fun listByScope(
        scope: ContentScope,
        family: ReusableContentFamily?,
    ): List<ReusableContentItem> {
        val encoded = encodeScope(scope, authoredOnly = true)
        return database.reusableContentQueries.selectActiveReusableContentByScope(
            scope_kind = encoded.kind,
            scope_ref = requireNotNull(encoded.ref),
            family = family?.name,
            mapper = ::mapItem,
        ).executeAsList()
    }

    private fun mapItem(
        id: String,
        family: String,
        displayName: String,
        scopeKind: String,
        scopeRef: String,
        sourceObjectId: String?,
        sourceScopeKind: String?,
        sourceScopeRef: String?,
        copiedAtEpochSeconds: Long?,
        createdAtEpochSeconds: Long,
        updatedAtEpochSeconds: Long,
        revision: Long,
        deletedAtEpochSeconds: Long?,
    ): ReusableContentItem {
        val provenance = sourceObjectId?.let {
            CopyProvenance(
                sourceObjectId = Uuid.parse(it),
                sourceScope = decodeScope(
                    kind = requireNotNull(sourceScopeKind),
                    ref = sourceScopeRef,
                ),
                copiedAtEpochSeconds = copiedAtEpochSeconds,
            )
        }

        return ReusableContentItem(
            identity = ScopedObjectIdentity(
                id = Uuid.parse(id),
                scope = decodeScope(scopeKind, scopeRef),
                revision = Revision(revision),
                provenance = provenance,
            ),
            family = ReusableContentFamily.valueOf(family),
            displayName = displayName,
            createdAtEpochSeconds = createdAtEpochSeconds,
            updatedAtEpochSeconds = updatedAtEpochSeconds,
            deletedAtEpochSeconds = deletedAtEpochSeconds,
        )
    }

    private fun encodeScope(scope: ContentScope, authoredOnly: Boolean): EncodedScope = when (scope) {
        is ContentScope.Personal -> EncodedScope(PERSONAL, scope.ownerAccountId.toString())
        is ContentScope.Campaign -> EncodedScope(CAMPAIGN, scope.campaignId.toString())
        is ContentScope.Official -> {
            require(!authoredOnly) { "Reusable authored content cannot be Official scoped." }
            EncodedScope(OFFICIAL, scope.sourceKey)
        }
        ContentScope.System -> {
            require(!authoredOnly) { "Reusable authored content cannot be System scoped." }
            EncodedScope(SYSTEM, null)
        }
    }

    private fun decodeScope(kind: String, ref: String?): ContentScope = when (kind) {
        PERSONAL -> ContentScope.Personal(Uuid.parse(requireNotNull(ref)))
        CAMPAIGN -> ContentScope.Campaign(Uuid.parse(requireNotNull(ref)))
        OFFICIAL -> ContentScope.Official(requireNotNull(ref))
        SYSTEM -> {
            require(ref == null) { "System scope must not carry a reference." }
            ContentScope.System
        }
        else -> error("Unknown reusable content scope kind: $kind")
    }

    private fun requireCampaignExists(campaignId: Uuid) {
        require(database.campaignQueries.selectCampaignById(campaignId.toString()).executeAsOneOrNull() != null) {
            "Campaign reusable content target must already exist locally."
        }
    }

    private fun normalizeName(rawDisplayName: String): String = rawDisplayName.trim().also {
        require(it.isNotEmpty()) { "Reusable content display name must not be blank." }
    }

    private data class EncodedScope(
        val kind: String,
        val ref: String?,
    )

    private companion object {
        const val OBJECT_TYPE = "REUSABLE_CONTENT"
        const val PERSONAL = "PERSONAL"
        const val CAMPAIGN = "CAMPAIGN"
        const val SYSTEM = "SYSTEM"
        const val OFFICIAL = "OFFICIAL"
    }
}
