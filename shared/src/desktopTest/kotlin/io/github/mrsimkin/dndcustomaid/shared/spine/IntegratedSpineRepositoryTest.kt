package io.github.mrsimkin.dndcustomaid.shared.spine

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class IntegratedSpineRepositoryTest {
    @Test
    fun membershipAndPcAuthorityRoundTripKeepsRoleOwnershipAndControlDistinct() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val character = CharacterRepository(database).createCharacter(campaign.id, "Vanya")
        val repository = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random(), externalSubject = "descope-owner", displayName = "Player")
        val controller = AccountIdentity(Uuid.random(), externalSubject = "descope-dm", displayName = "DM")

        repository.upsertAccount(owner)
        repository.upsertAccount(controller)
        repository.upsertMembership(CampaignMembership(campaign.id, owner.id, CampaignRole.PLAYER))
        repository.upsertMembership(CampaignMembership(campaign.id, controller.id, CampaignRole.DM))
        repository.setPcAuthority(PcAuthority(character.id, owner.id, controller.id))

        assertEquals(CampaignRole.PLAYER, repository.membership(campaign.id, owner.id)?.role)
        assertEquals(CampaignRole.DM, repository.membership(campaign.id, controller.id)?.role)
        assertEquals(PcAuthority(character.id, owner.id, controller.id), repository.pcAuthority(character.id))
    }

    @Test
    fun inactiveMemberCannotBeNewPcOwnerOrController() = withDatabase { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val character = CharacterRepository(database).createCharacter(campaign.id, "Vanya")
        val repository = IntegratedSpineRepository(database)
        val kicked = AccountIdentity(Uuid.random(), externalSubject = "kicked")

        repository.upsertAccount(kicked)
        repository.upsertMembership(
            CampaignMembership(
                campaignId = campaign.id,
                accountId = kicked.id,
                role = CampaignRole.PLAYER,
                status = CampaignMembershipStatus.KICKED,
            ),
        )

        assertFailsWith<IllegalArgumentException> {
            repository.setPcAuthority(PcAuthority(character.id, ownerAccountId = kicked.id))
        }
        assertNull(repository.pcAuthority(character.id))
    }

    @Test
    fun pcAuthorityMustUseMembershipFromThePcCampaign() = withDatabase { database ->
        val campaigns = CampaignRepository(database)
        val first = campaigns.createCampaign("First")
        val second = campaigns.createCampaign("Second")
        val character = CharacterRepository(database).createCharacter(first.id, "Vanya")
        val repository = IntegratedSpineRepository(database)
        val account = AccountIdentity(Uuid.random())

        repository.upsertAccount(account)
        repository.upsertMembership(CampaignMembership(second.id, account.id, CampaignRole.PLAYER))

        assertFailsWith<IllegalArgumentException> {
            repository.setPcAuthority(PcAuthority(character.id, ownerAccountId = account.id))
        }
    }

    @Test
    fun revisionAdvanceRejectsStaleWritesAndTombstoneBlocksResurrection() = withDatabase { database ->
        val repository = IntegratedSpineRepository(database)
        val objectId = Uuid.random()

        val first = assertIs<RevisionDecision.Accepted>(
            repository.advanceRevision("TEST", objectId, Revision(0)),
        )
        assertEquals(Revision(1), first.nextRevision)

        val stale = assertIs<RevisionDecision.Stale>(
            repository.advanceRevision("TEST", objectId, Revision(0)),
        )
        assertEquals(Revision(1), stale.actual)

        val deleted = assertIs<RevisionDecision.Accepted>(
            repository.tombstone("TEST", objectId, Revision(1), deletedAtEpochSeconds = 1000),
        )
        assertEquals(Revision(2), deleted.nextRevision)

        assertIs<RevisionDecision.Deleted>(
            repository.advanceRevision("TEST", objectId, Revision(2)),
        )
        assertEquals(Revision(2), repository.syncMetadata("TEST", objectId).revision)
        assertEquals(1000, repository.syncMetadata("TEST", objectId).deletedAtEpochSeconds)
    }

    @Test
    fun migration15BackfillsExistingCampaignAndPcSyncMetadata() {
        val file = File.createTempFile("dnd-custom-aid-spine-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000000101"
        val characterId = "00000000-0000-0000-0000-000000000102"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    statement.executeUpdate(
                        "CREATE TABLE character (id TEXT NOT NULL PRIMARY KEY, campaign_id TEXT NOT NULL REFERENCES campaign(id) ON DELETE CASCADE)",
                    )
                    statement.executeUpdate("INSERT INTO campaign(id, name) VALUES ('$campaignId', 'Legacy')")
                    statement.executeUpdate("INSERT INTO character(id, campaign_id) VALUES ('$characterId', '$campaignId')")
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 15, newVersion = 16)
            val database = AppDatabase(driver)
            val repository = IntegratedSpineRepository(database)

            assertEquals(Revision(0), repository.syncMetadata("CAMPAIGN", Uuid.parse(campaignId)).revision)
            assertEquals(Revision(0), repository.syncMetadata("PC", Uuid.parse(characterId)).revision)
            driver.close()
        } finally {
            file.delete()
        }
    }

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
