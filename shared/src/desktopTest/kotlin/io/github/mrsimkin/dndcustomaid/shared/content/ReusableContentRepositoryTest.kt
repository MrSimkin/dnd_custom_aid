package io.github.mrsimkin.dndcustomaid.shared.content

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class ReusableContentRepositoryTest {
    @Test
    fun personalToCampaignCopyCreatesIndependentIdentityAndRetainsProvenance() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random(), externalSubject = "owner")
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = ReusableContentRepository(database, spine)

        val personal = repository.createPersonal(
            ownerAccountId = owner.id,
            family = ReusableContentFamily.CREATURE,
            rawDisplayName = " Goblin ",
            nowEpochSeconds = 100,
        )
        val campaignCopy = repository.copyPersonalToCampaign(
            sourceId = personal.identity.id,
            campaignId = campaign.id,
            copiedAtEpochSeconds = 200,
        )

        assertNotEquals(personal.identity.id, campaignCopy.identity.id)
        assertEquals(ContentScope.Campaign(campaign.id), campaignCopy.identity.scope)
        assertEquals(personal.identity.id, campaignCopy.identity.provenance?.sourceObjectId)
        assertEquals(ContentScope.Personal(owner.id), campaignCopy.identity.provenance?.sourceScope)
        assertEquals(Revision(0), campaignCopy.identity.revision)
        assertEquals("Goblin", campaignCopy.displayName)

        assertIs<RevisionDecision.Accepted>(
            repository.rename(
                id = personal.identity.id,
                expectedRevision = Revision(0),
                rawDisplayName = "Goblin Chief",
                updatedAtEpochSeconds = 300,
            ),
        )

        assertEquals("Goblin Chief", repository.content(personal.identity.id)?.displayName)
        assertEquals(Revision(1), repository.content(personal.identity.id)?.identity?.revision)
        assertEquals("Goblin", repository.content(campaignCopy.identity.id)?.displayName)
        assertEquals(Revision(0), repository.content(campaignCopy.identity.id)?.identity?.revision)
    }

    @Test
    fun staleMutationAndTombstonePreventSilentOverwriteAndResurrection() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val repository = ReusableContentRepository(database, spine)
        val item = repository.createPersonal(
            ownerAccountId = owner.id,
            family = ReusableContentFamily.NPC,
            rawDisplayName = "Archivist",
            nowEpochSeconds = 100,
        )

        val accepted = assertIs<RevisionDecision.Accepted>(
            repository.rename(item.identity.id, Revision(0), "Senior Archivist", 200),
        )
        assertEquals(Revision(1), accepted.nextRevision)

        val stale = assertIs<RevisionDecision.Stale>(
            repository.rename(item.identity.id, Revision(0), "Stale Name", 250),
        )
        assertEquals(Revision(1), stale.actual)
        assertEquals("Senior Archivist", repository.content(item.identity.id)?.displayName)

        val deleted = assertIs<RevisionDecision.Accepted>(
            repository.tombstone(item.identity.id, Revision(1), 300),
        )
        assertEquals(Revision(2), deleted.nextRevision)
        assertNull(repository.content(item.identity.id))
        assertEquals(emptyList(), repository.listPersonal(owner.id))

        assertIs<RevisionDecision.Deleted>(
            repository.rename(item.identity.id, Revision(2), "Resurrected", 400),
        )
        val tombstoned = repository.content(item.identity.id, includeDeleted = true)
        assertEquals("Senior Archivist", tombstoned?.displayName)
        assertEquals(Revision(2), tombstoned?.identity?.revision)
        assertEquals(300, tombstoned?.deletedAtEpochSeconds)
    }

    @Test
    fun listFiltersByScopeAndFamily() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = ReusableContentRepository(database, spine)

        repository.createPersonal(owner.id, ReusableContentFamily.CREATURE, "Owlbear", 100)
        repository.createPersonal(owner.id, ReusableContentFamily.NPC, "Guide", 101)
        repository.createCampaign(campaign.id, ReusableContentFamily.CREATURE, "Dragon", 102)

        assertEquals(
            listOf("Owlbear"),
            repository.listPersonal(owner.id, ReusableContentFamily.CREATURE).map { it.displayName },
        )
        assertEquals(
            listOf("Guide", "Owlbear"),
            repository.listPersonal(owner.id).map { it.displayName },
        )
        assertEquals(
            listOf("Dragon"),
            repository.listCampaign(campaign.id).map { it.displayName },
        )
    }

    @Test
    fun migration18AddsReusableCatalogWithoutTouchingExistingCampaignData() {
        val file = File.createTempFile("dnd-custom-aid-content-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000000601"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    statement.executeUpdate("INSERT INTO campaign(id, name) VALUES ('$campaignId', 'Legacy Campaign')")
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 18, newVersion = 19)
            val database = AppDatabase(driver)

            assertEquals(
                "Legacy Campaign",
                database.campaignQueries.selectCampaignById(campaignId) { _, name -> name }.executeAsOne(),
            )
            assertEquals(
                emptyList(),
                database.reusableContentQueries.selectActiveReusableContentByScope(
                    scope_kind = "CAMPAIGN",
                    scope_ref = campaignId,
                    family = null,
                ) { _, _, displayName, _, _, _, _, _, _, _, _, _, _ -> displayName }.executeAsList(),
            )
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
