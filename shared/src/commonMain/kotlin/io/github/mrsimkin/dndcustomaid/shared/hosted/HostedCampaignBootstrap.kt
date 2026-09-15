package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembership
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
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
            "Every hosted campaign must be applied or reported as a conflict."
        }
    }
}

class HostedCampaignBootstrapService(
    private val database: AppDatabase,
    private val localCampaigns: CampaignRepository = CampaignRepository(database),
    private val spine: IntegratedSpineRepository = IntegratedSpineRepository(database),
    private val accountProvider: suspend () -> HostedAccount,
    private val campaignsProvider: suspend () -> List<HostedCampaign>,
) {
    constructor(
        database: AppDatabase,
        api: HostedApiClient,
    ) : this(
        database = database,
        accountProvider = api::currentAccount,
        campaignsProvider = api::campaigns,
    )

    suspend fun refresh(): HostedCampaignBootstrapResult {
        // Complete the authenticated reads before mutating local state so an API failure cannot
        // leave a half-refreshed account/campaign snapshot.
        val hostedAccount = accountProvider()
        val hostedCampaigns = campaignsProvider()
        require(hostedCampaigns.map { it.id }.distinct().size == hostedCampaigns.size) {
            "Hosted campaign response contains duplicate campaign identities."
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

            hostedCampaigns
                .sortedBy { it.id.toString() }
                .forEach { hostedCampaign ->
                    val hostedRevision = Revision(hostedCampaign.revision)
                    val localCampaign = localCampaigns.campaign(hostedCampaign.id)
                    val localMetadata = spine.syncMetadata(
                        CAMPAIGN_SYNC_OBJECT_TYPE,
                        hostedCampaign.id,
                    )

                    val conflictReason = when {
                        localMetadata.isDeleted -> HostedCampaignBootstrapConflictReason.LOCAL_TOMBSTONE
                        localMetadata.revision > hostedRevision -> HostedCampaignBootstrapConflictReason.LOCAL_REVISION_AHEAD
                        localCampaign != null &&
                            localMetadata.revision == hostedRevision &&
                            localCampaign.name != hostedCampaign.name ->
                            HostedCampaignBootstrapConflictReason.SAME_REVISION_STATE_MISMATCH
                        else -> null
                    }

                    if (conflictReason != null) {
                        // If the campaign row still exists, hosted membership/role is independently
                        // authoritative and safe to refresh even while object state needs resolution.
                        if (localCampaign != null) {
                            spine.upsertMembership(
                                CampaignMembership(
                                    campaignId = hostedCampaign.id,
                                    accountId = hostedAccount.id,
                                    role = hostedCampaign.role,
                                    status = CampaignMembershipStatus.ACTIVE,
                                ),
                            )
                        }
                        conflicts += HostedCampaignBootstrapConflict(
                            campaignId = hostedCampaign.id,
                            reason = conflictReason,
                            localRevision = localMetadata.revision,
                            hostedRevision = hostedRevision,
                        )
                        return@forEach
                    }

                    localCampaigns.upsertCampaign(
                        id = hostedCampaign.id,
                        rawName = hostedCampaign.name,
                    )
                    spine.upsertMembership(
                        CampaignMembership(
                            campaignId = hostedCampaign.id,
                            accountId = hostedAccount.id,
                            role = hostedCampaign.role,
                            status = CampaignMembershipStatus.ACTIVE,
                        ),
                    )
                    spine.putSyncMetadata(
                        objectType = CAMPAIGN_SYNC_OBJECT_TYPE,
                        objectId = hostedCampaign.id,
                        metadata = SyncMetadata(revision = hostedRevision),
                    )
                    applied += hostedCampaign.id
                }
        }

        // Absence from GET /v1/campaigns is deliberately not converted into KICKED/BANNED.
        // Those states need explicit server-side change semantics rather than client inference.
        return HostedCampaignBootstrapResult(
            account = hostedAccount,
            hostedCampaignCount = hostedCampaigns.size,
            appliedCampaignIds = applied,
            conflicts = conflicts,
        )
    }
}
