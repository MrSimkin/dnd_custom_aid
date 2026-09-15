package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.SyncMetadata
import kotlin.uuid.Uuid

private const val CAMPAIGN_SYNC_OBJECT_TYPE = "CAMPAIGN"

enum class HostedCampaignBootstrapConflictReason {
    LOCAL_REVISION_AHEAD,
    LOCAL_TOMBSTONE,
    SAME_REVISION_STATE_MISMATCH,
}

data class HostedCampaignBootstrapConflict(
    val campaignId: Uuid,
    val reason: HostedCampaignBootstrapConflictReason,
    val localRevision: Revision,
    val hostedRevision: Revision,
)

data class HostedCampaignBootstrapResult(
    val account: HostedAccount,
    val hostedCampaignCount: Int,
    val appliedCampaignIds: List<Uuid>,
    val conflicts: List<HostedCampaignBootstrapConflict>,
) {
    init {
        require(hostedCampaignCount >= 0)
        require(appliedCampaignIds.size + conflicts.size == hostedCampaignCount) {
            "Every hosted campaign membership state must be applied or reported as a conflict."
        }
    }
}

class HostedCampaignBootstrapService(
    private val database: AppDatabase,
    private val localCampaigns: CampaignRepository = CampaignRepository(database),
    private val spine: IntegratedSpineRepository = IntegratedSpineRepository(database),
    private val accountProvider: suspend () -> HostedAccount,
    private val membershipsProvider: suspend () -> List<HostedCampaignMembershipState>,
) {
    constructor(
        database: AppDatabase,
        api: HostedApiClient,
    ) : this(
        database = database,
        accountProvider = api::currentAccount,
        membershipsProvider = api::campaignMemberships,
    )

    suspend fun refresh(): HostedCampaignBootstrapResult {
        // Complete authenticated reads before mutating local state so an API failure cannot
        // leave a half-refreshed account/campaign snapshot.
        val hostedAccount = accountProvider()
        val hostedMemberships = membershipsProvider()
        require(hostedMemberships.map { it.campaignId }.distinct().size == hostedMemberships.size) {
            "Hosted membership response contains duplicate campaign identities."
        }

        val applied = mutableListOf<Uuid>()
        val conflicts = mutableListOf<HostedCampaignBootstrapConflict>()

        database.transaction {
            val existingAccount = spine.account(hostedAccount.id)
            spine.upsertAccount(
                AccountIdentity(
                    id = hostedAccount.id,
                    // The API intentionally does not expose the provider subject. Preserve one
                    // already known locally instead of erasing it during bootstrap.
                    externalSubject = existingAccount?.externalSubject,
                    displayName = hostedAccount.displayName,
                ),
            )

            hostedMemberships
                .sortedBy { it.campaignId.toString() }
                .forEach { hosted ->
                    val hostedRevision = Revision(hosted.revision)
                    val hostedMetadata = SyncMetadata(
                        revision = hostedRevision,
                        deletedAtEpochSeconds = hosted.deletedAtEpochSeconds,
                    )
                    val localCampaign = localCampaigns.campaign(hosted.campaignId)
                    val localMetadata = spine.syncMetadata(
                        CAMPAIGN_SYNC_OBJECT_TYPE,
                        hosted.campaignId,
                    )

                    val conflictReason = when {
                        localMetadata.isDeleted && !hostedMetadata.isDeleted ->
                            HostedCampaignBootstrapConflictReason.LOCAL_TOMBSTONE
                        localMetadata.revision > hostedRevision ->
                            HostedCampaignBootstrapConflictReason.LOCAL_REVISION_AHEAD
                        localMetadata.revision == hostedRevision &&
                            localMetadata.isDeleted &&
                            hostedMetadata.isDeleted &&
                            localMetadata.deletedAtEpochSeconds != hostedMetadata.deletedAtEpochSeconds ->
                            HostedCampaignBootstrapConflictReason.SAME_REVISION_STATE_MISMATCH
                        localCampaign != null &&
                            !hostedMetadata.isDeleted &&
                            localMetadata.revision == hostedRevision &&
                            localCampaign.name != hosted.name ->
                            HostedCampaignBootstrapConflictReason.SAME_REVISION_STATE_MISMATCH
                        else -> null
                    }

                    if (conflictReason != null) {
                        // Membership lifecycle is independently authoritative. Refresh it when the
                        // campaign row exists even if campaign object state needs resolution.
                        if (localCampaign != null) {
                            spine.upsertMembership(
                                CampaignMembership(
                                    campaignId = hosted.campaignId,
                                    accountId = hostedAccount.id,
                                    role = hosted.role,
                                    status = hosted.status,
                                ),
                            )
                        }
                        conflicts += HostedCampaignBootstrapConflict(
                            campaignId = hosted.campaignId,
                            reason = conflictReason,
                            localRevision = localMetadata.revision,
                            hostedRevision = hostedRevision,
                        )
                        return@forEach
                    }

                    // Keep the last hosted campaign identity/name locally even when membership is
                    // inactive or the hosted campaign is tombstoned; local data is not destroyed by
                    // losing hosted access.
                    localCampaigns.upsertCampaign(
                        id = hosted.campaignId,
                        rawName = hosted.name,
                    )
                    spine.upsertMembership(
                        CampaignMembership(
                            campaignId = hosted.campaignId,
                            accountId = hostedAccount.id,
                            role = hosted.role,
                            status = hosted.status,
                        ),
                    )
                    spine.putSyncMetadata(
                        objectType = CAMPAIGN_SYNC_OBJECT_TYPE,
                        objectId = hosted.campaignId,
                        metadata = hostedMetadata,
                    )
                    applied += hosted.campaignId
                }
        }

        // Absence remains deliberately non-semantic. A membership is only changed when the server
        // explicitly returns its lifecycle state.
        return HostedCampaignBootstrapResult(
            account = hostedAccount,
            hostedCampaignCount = hostedMemberships.size,
            appliedCampaignIds = applied,
            conflicts = conflicts,
        )
    }
}
