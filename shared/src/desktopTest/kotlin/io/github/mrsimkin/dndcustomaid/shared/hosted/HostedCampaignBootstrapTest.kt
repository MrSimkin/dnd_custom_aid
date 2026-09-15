package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.SyncMetadata
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class HostedCampaignBootstrapTest {
    @Test
    fun refreshImportsAccountCampaignMembershipAndRevision() = withDatabase { database ->
        val accountId = Uuid.random()
        val campaignId = Uuid.random()
        val account = HostedAccount(accountId, "Gustavo")
        val hosted = HostedCampaign(campaignId, "Terramore", CampaignRole.DM, revision = 3)
        val service = service(database, account, listOf(hosted))

        val result = runBlocking { service.refresh() }
        val campaigns = CampaignRepository(database)
        val spine = IntegratedSpineRepository(database)

        assertEquals(account, result.account)
        assertEquals(1, result.hostedCampaignCount)
        assertEquals(listOf(campaignId), result.appliedCampaignIds)
        assertEquals(emptyList(), result.conflicts)
        assertEquals(Campaign(campaignId, "Terramore"), campaigns.campaign(campaignId))
        assertEquals(
            AccountIdentity(accountId, externalSubject = null, displayName = "Gustavo"),
            spine.account(accountId),
        )
        assertEquals(CampaignRole.DM, spine.membership(campaignId, accountId)?.role)
        assertEquals(CampaignMembershipStatus.ACTIVE, spine.membership(campaignId, accountId)?.status)
        assertEquals(Revision(3), spine.syncMetadata("CAMPAIGN", campaignId).revision)
    }

    @Test
    fun newerHostedRevisionUpdatesLocalCampaignAndRole() = withDatabase { database ->
        val accountId = Uuid.random()
        val campaignId = Uuid.random()
        val campaigns = CampaignRepository(database)
        val spine = IntegratedSpineRepository(database)
        campaigns.upsertCampaign(campaignId, "Old Name")
        spine.putSyncMetadata("CAMPAIGN", campaignId, SyncMetadata(revision = Revision(1)))

        val result = runBlocking {
            service(
                database,
                HostedAccount(accountId, "Gustavo"),
                listOf(HostedCampaign(campaignId, "New Name", CampaignRole.PLAYER, revision = 2)),
            ).refresh()
        }

        assertEquals(listOf(campaignId), result.appliedCampaignIds)
        assertEquals(Campaign(campaignId, "New Name"), campaigns.campaign(campaignId))
        assertEquals(Revision(2), spine.syncMetadata("CAMPAIGN", campaignId).revision)
        assertEquals(CampaignRole.PLAYER, spine.membership(campaignId, accountId)?.role)
    }

    @Test
    fun localRevisionAheadIsReportedAndNeverOverwritten() = withDatabase { database ->
        val accountId = Uuid.random()
        val campaignId = Uuid.random()
        val campaigns = CampaignRepository(database)
        val spine = IntegratedSpineRepository(database)
        campaigns.upsertCampaign(campaignId, "Local Newer")
        spine.putSyncMetadata("CAMPAIGN", campaignId, SyncMetadata(revision = Revision(5)))

        val result = runBlocking {
            service(
                database,
                HostedAccount(accountId),
                listOf(HostedCampaign(campaignId, "Hosted Older", CampaignRole.PLAYER, revision = 4)),
            ).refresh()
        }

        assertEquals(emptyList(), result.appliedCampaignIds)
        assertEquals(
            listOf(
                HostedCampaignBootstrapConflict(
                    campaignId,
                    HostedCampaignBootstrapConflictReason.LOCAL_REVISION_AHEAD,
                    Revision(5),
                    Revision(4),
                ),
            ),
            result.conflicts,
        )
        assertEquals(Campaign(campaignId, "Local Newer"), campaigns.campaign(campaignId))
        assertEquals(Revision(5), spine.syncMetadata("CAMPAIGN", campaignId).revision)
        assertEquals(CampaignRole.PLAYER, spine.membership(campaignId, accountId)?.role)
    }

    @Test
    fun localTombstoneIsNeverResurrectedByBootstrap() = withDatabase { database ->
        val accountId = Uuid.random()
        val campaignId = Uuid.random()
        val campaigns = CampaignRepository(database)
        val spine = IntegratedSpineRepository(database)
        campaigns.upsertCampaign(campaignId, "Locally Deleted")
        spine.putSyncMetadata(
            "CAMPAIGN",
            campaignId,
            SyncMetadata(revision = Revision(3), deletedAtEpochSeconds = 100),
        )

        val result = runBlocking {
            service(
                database,
                HostedAccount(accountId),
                listOf(HostedCampaign(campaignId, "Hosted Alive", CampaignRole.DM, revision = 4)),
            ).refresh()
        }

        assertEquals(HostedCampaignBootstrapConflictReason.LOCAL_TOMBSTONE, result.conflicts.single().reason)
        assertEquals(Campaign(campaignId, "Locally Deleted"), campaigns.campaign(campaignId))
        assertEquals(
            SyncMetadata(revision = Revision(3), deletedAtEpochSeconds = 100),
            spine.syncMetadata("CAMPAIGN", campaignId),
        )
    }

    @Test
    fun sameRevisionStateMismatchIsReportedInsteadOfSilentlyOverwritten() = withDatabase { database ->
        val accountId = Uuid.random()
        val campaignId = Uuid.random()
        val campaigns = CampaignRepository(database)
        val spine = IntegratedSpineRepository(database)
        campaigns.upsertCampaign(campaignId, "Local State")
        spine.putSyncMetadata("CAMPAIGN", campaignId, SyncMetadata(revision = Revision(2)))

        val result = runBlocking {
            service(
                database,
                HostedAccount(accountId),
                listOf(HostedCampaign(campaignId, "Hosted State", CampaignRole.DM, revision = 2)),
            ).refresh()
        }

        assertEquals(
            HostedCampaignBootstrapConflictReason.SAME_REVISION_STATE_MISMATCH,
            result.conflicts.single().reason,
        )
        assertEquals(Campaign(campaignId, "Local State"), campaigns.campaign(campaignId))
    }

    @Test
    fun refreshPreservesAProviderSubjectAlreadyKnownLocally() = withDatabase { database ->
        val accountId = Uuid.random()
        val spine = IntegratedSpineRepository(database)
        spine.upsertAccount(
            AccountIdentity(
                id = accountId,
                externalSubject = "descope-user-123",
                displayName = "Old Name",
            ),
        )

        runBlocking {
            service(database, HostedAccount(accountId, "New Name"), emptyList()).refresh()
        }

        assertEquals(
            AccountIdentity(accountId, externalSubject = "descope-user-123", displayName = "New Name"),
            spine.account(accountId),
        )
    }

    @Test
    fun duplicateHostedCampaignIdentityIsRejectedBeforeLocalWrites() = withDatabase { database ->
        val accountId = Uuid.random()
        val campaignId = Uuid.random()
        val service = service(
            database,
            HostedAccount(accountId),
            listOf(
                HostedCampaign(campaignId, "One", CampaignRole.DM, revision = 0),
                HostedCampaign(campaignId, "Two", CampaignRole.DM, revision = 1),
            ),
        )

        assertFailsWith<IllegalArgumentException> {
            runBlocking { service.refresh() }
        }

        assertNull(IntegratedSpineRepository(database).account(accountId))
        assertNull(CampaignRepository(database).campaign(campaignId))
    }

    @Test
    fun failedCampaignReadLeavesLocalStateUntouched() = withDatabase { database ->
        val accountId = Uuid.random()
        val service = HostedCampaignBootstrapService(
            database = database,
            accountProvider = { HostedAccount(accountId, "Gustavo") },
            campaignsProvider = { error("offline") },
        )

        assertFailsWith<IllegalStateException> {
            runBlocking { service.refresh() }
        }

        assertNull(IntegratedSpineRepository(database).account(accountId))
        assertEquals(emptyList(), CampaignRepository(database).listCampaigns())
    }

    private fun service(
        database: AppDatabase,
        account: HostedAccount,
        campaigns: List<HostedCampaign>,
    ): HostedCampaignBootstrapService = HostedCampaignBootstrapService(
        database = database,
        accountProvider = { account },
        campaignsProvider = { campaigns },
    )

    private fun withDatabase(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}
