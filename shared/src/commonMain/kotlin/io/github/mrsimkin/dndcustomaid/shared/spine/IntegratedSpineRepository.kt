package io.github.mrsimkin.dndcustomaid.shared.spine

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

class IntegratedSpineRepository(
    private val database: AppDatabase,
) {
    fun upsertAccount(account: AccountIdentity) {
        require(account.externalSubject == null || account.externalSubject.isNotBlank()) {
            "External subject must be null or non-blank."
        }
        val displayName = account.displayName?.trim()?.takeIf { it.isNotEmpty() }
        database.transaction {
            if (this@IntegratedSpineRepository.account(account.id) == null) {
                database.integratedSpineQueries.insertAccount(
                    id = account.id.toString(),
                    external_subject = account.externalSubject,
                    display_name = displayName,
                )
            } else {
                database.integratedSpineQueries.updateAccount(
                    external_subject = account.externalSubject,
                    display_name = displayName,
                    id = account.id.toString(),
                )
            }
        }
    }

    fun account(id: Uuid): AccountIdentity? =
        database.integratedSpineQueries.selectAccountById(id.toString()) { storedId, subject, displayName ->
            AccountIdentity(
                id = Uuid.parse(storedId),
                externalSubject = subject,
                displayName = displayName,
            )
        }.executeAsOneOrNull()

    fun upsertMembership(membership: CampaignMembership) {
        requireCampaignExists(membership.campaignId)
        requireAccountExists(membership.accountId)
        database.integratedSpineQueries.upsertCampaignMembership(
            campaign_id = membership.campaignId.toString(),
            account_id = membership.accountId.toString(),
            role = membership.role.name,
            status = membership.status.name,
        )
    }

    fun membership(campaignId: Uuid, accountId: Uuid): CampaignMembership? =
        database.integratedSpineQueries.selectCampaignMembership(
            campaign_id = campaignId.toString(),
            account_id = accountId.toString(),
        ) { storedCampaignId, storedAccountId, role, status ->
            CampaignMembership(
                campaignId = Uuid.parse(storedCampaignId),
                accountId = Uuid.parse(storedAccountId),
                role = CampaignRole.valueOf(role),
                status = CampaignMembershipStatus.valueOf(status),
            )
        }.executeAsOneOrNull()

    fun memberships(campaignId: Uuid): List<CampaignMembership> =
        database.integratedSpineQueries.selectCampaignMemberships(campaignId.toString()) {
                storedCampaignId, storedAccountId, role, status ->
            CampaignMembership(
                campaignId = Uuid.parse(storedCampaignId),
                accountId = Uuid.parse(storedAccountId),
                role = CampaignRole.valueOf(role),
                status = CampaignMembershipStatus.valueOf(status),
            )
        }.executeAsList()

    fun setPcAuthority(authority: PcAuthority) {
        val campaignId = characterCampaignId(authority.characterId)
        authority.ownerAccountId?.let { requireActiveMembership(campaignId, it, "PC owner") }
        authority.controllerAccountId?.let { requireActiveMembership(campaignId, it, "PC controller") }

        database.integratedSpineQueries.upsertPcAuthority(
            character_id = authority.characterId.toString(),
            owner_account_id = authority.ownerAccountId?.toString(),
            controller_account_id = authority.controllerAccountId?.toString(),
        )
    }

    fun pcAuthority(characterId: Uuid): PcAuthority? =
        database.integratedSpineQueries.selectPcAuthority(characterId.toString()) {
                storedCharacterId, ownerAccountId, controllerAccountId ->
            PcAuthority(
                characterId = Uuid.parse(storedCharacterId),
                ownerAccountId = ownerAccountId?.let(Uuid::parse),
                controllerAccountId = controllerAccountId?.let(Uuid::parse),
            )
        }.executeAsOneOrNull()

    fun syncMetadata(objectType: String, objectId: Uuid): SyncMetadata {
        require(objectType.isNotBlank()) { "Sync object type must not be blank." }
        return database.integratedSpineQueries.selectObjectSyncState(objectType, objectId.toString()) {
                _, _, revision, deletedAt ->
            SyncMetadata(
                revision = Revision(revision),
                deletedAtEpochSeconds = deletedAt,
            )
        }.executeAsOneOrNull() ?: SyncMetadata()
    }

    fun putSyncMetadata(objectType: String, objectId: Uuid, metadata: SyncMetadata) {
        require(objectType.isNotBlank()) { "Sync object type must not be blank." }
        database.integratedSpineQueries.upsertObjectSyncState(
            object_type = objectType,
            object_id = objectId.toString(),
            revision = metadata.revision.value,
            deleted_at_epoch_seconds = metadata.deletedAtEpochSeconds,
        )
    }

    fun advanceRevision(
        objectType: String,
        objectId: Uuid,
        expectedRevision: Revision,
    ): RevisionDecision {
        val current = syncMetadata(objectType, objectId)
        val decision = current.checkMutation(expectedRevision)
        if (decision is RevisionDecision.Accepted) {
            putSyncMetadata(
                objectType = objectType,
                objectId = objectId,
                metadata = current.copy(revision = decision.nextRevision),
            )
        }
        return decision
    }

    fun tombstone(
        objectType: String,
        objectId: Uuid,
        expectedRevision: Revision,
        deletedAtEpochSeconds: Long,
    ): RevisionDecision {
        require(deletedAtEpochSeconds >= 0) { "Deletion timestamp must not be negative." }
        val current = syncMetadata(objectType, objectId)
        val decision = current.checkMutation(expectedRevision)
        if (decision is RevisionDecision.Accepted) {
            putSyncMetadata(
                objectType = objectType,
                objectId = objectId,
                metadata = SyncMetadata(
                    revision = decision.nextRevision,
                    deletedAtEpochSeconds = deletedAtEpochSeconds,
                ),
            )
        }
        return decision
    }

    private fun characterCampaignId(characterId: Uuid): Uuid {
        val stored = database.integratedSpineQueries
            .selectCharacterCampaignId(characterId.toString())
            .executeAsOneOrNull()
        require(stored != null) { "PC must already exist locally before authority can be assigned." }
        return Uuid.parse(stored)
    }

    private fun requireCampaignExists(campaignId: Uuid) {
        val stored = database.campaignQueries.selectCampaignById(campaignId.toString()).executeAsOneOrNull()
        require(stored != null) { "Campaign must already exist locally." }
    }

    private fun requireAccountExists(accountId: Uuid) {
        require(account(accountId) != null) { "Account must already exist locally." }
    }

    private fun requireActiveMembership(campaignId: Uuid, accountId: Uuid, label: String) {
        val membership = membership(campaignId, accountId)
        require(membership?.status == CampaignMembershipStatus.ACTIVE) {
            "$label must be an active member of the PC campaign."
        }
    }
}
