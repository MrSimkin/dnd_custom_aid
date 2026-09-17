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

class ZoneContentRepositoryTest {
    @Test
    fun zoneBriefRoundTripPreservesPreparedAreaData() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random(), externalSubject = "owner")
        spine.upsertAccount(owner)
        val repository = ZoneContentRepository(database)
        val payload = samplePayload()

        val zone = repository.createPersonal(owner.id, "Flooded Archive", payload, 100)
        val reopened = repository.zone(zone.item.identity.id)

        assertEquals(payload, reopened?.payload)
        assertEquals(ContentScope.Personal(owner.id), reopened?.item?.identity?.scope)
        assertEquals(ReusableContentFamily.ZONE, reopened?.item?.family)
    }

    @Test
    fun campaignCopyIsIndependentAndRetainsProvenance() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = ZoneContentRepository(database)
        val originalPayload = samplePayload()
        val personal = repository.createPersonal(owner.id, "Flooded Archive", originalPayload, 100)

        val copy = repository.copyPersonalToCampaign(
            sourceId = personal.item.identity.id,
            campaignId = campaign.id,
            copiedAtEpochSeconds = 200,
        )

        assertNotEquals(personal.item.identity.id, copy.item.identity.id)
        assertEquals(originalPayload, copy.payload)
        assertEquals(personal.item.identity.id, copy.item.identity.provenance?.sourceObjectId)
        assertEquals(ContentScope.Personal(owner.id), copy.item.identity.provenance?.sourceScope)
        assertEquals(200, copy.item.identity.provenance?.copiedAtEpochSeconds)
        assertEquals(ContentScope.Campaign(campaign.id), copy.item.identity.scope)
        assertEquals(Revision(0), copy.item.identity.revision)

        val changedSource = originalPayload.copy(
            exploration = "Water rises slowly while the west sluice remains closed.",
            clues = originalPayload.clues + "Fresh boot prints end at the collapsed catalogue room.",
            dmGuidance = "Use the rising water only as pressure, not as an automatic failure clock.",
        )
        val accepted = assertIs<RevisionDecision.Accepted>(
            repository.updatePayload(
                id = personal.item.identity.id,
                expectedRevision = Revision(0),
                payload = changedSource,
                updatedAtEpochSeconds = 300,
            ),
        )
        assertEquals(Revision(1), accepted.nextRevision)
        assertEquals(changedSource, repository.zone(personal.item.identity.id)?.payload)
        assertEquals(originalPayload, repository.zone(copy.item.identity.id)?.payload)
        assertEquals(Revision(0), repository.zone(copy.item.identity.id)?.item?.identity?.revision)
    }

    @Test
    fun staleAndDeletedZoneMutationsCannotOverwriteOrResurrect() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val repository = ZoneContentRepository(database)
        val original = repository.createPersonal(owner.id, "Flooded Archive", samplePayload(), 100)
        val changed = original.payload.copy(dmGuidance = "First accepted change")

        assertIs<RevisionDecision.Accepted>(
            repository.updatePayload(original.item.identity.id, Revision(0), changed, 200),
        )
        val stale = assertIs<RevisionDecision.Stale>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(0),
                original.payload.copy(dmGuidance = "Stale overwrite"),
                250,
            ),
        )
        assertEquals(Revision(1), stale.actual)
        assertEquals(changed, repository.zone(original.item.identity.id)?.payload)

        val deleted = assertIs<RevisionDecision.Accepted>(
            repository.tombstone(original.item.identity.id, Revision(1), 300),
        )
        assertEquals(Revision(2), deleted.nextRevision)
        assertNull(repository.zone(original.item.identity.id))

        assertIs<RevisionDecision.Deleted>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(2),
                original.payload.copy(dmGuidance = "Resurrection attempt"),
                400,
            ),
        )
        val tombstoned = repository.zone(original.item.identity.id, includeDeleted = true)
        assertEquals(changed, tombstoned?.payload)
        assertEquals(Revision(2), tombstoned?.item?.identity?.revision)
        assertEquals(300, tombstoned?.item?.deletedAtEpochSeconds)
    }

    @Test
    fun migration23PreservesExistingCampaignAndBackfillsMetadataOnlyZone() {
        val file = File.createTempFile("dnd-custom-aid-zone-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000001101"
        val zoneId = "00000000-0000-0000-0000-000000001102"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    statement.executeUpdate(
                        """
                        CREATE TABLE object_sync_state (
                            object_type TEXT NOT NULL,
                            object_id TEXT NOT NULL,
                            revision INTEGER NOT NULL DEFAULT 0 CHECK (revision >= 0),
                            deleted_at_epoch_seconds INTEGER,
                            PRIMARY KEY(object_type, object_id)
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate(
                        """
                        CREATE TABLE reusable_content (
                            id TEXT NOT NULL PRIMARY KEY,
                            family TEXT NOT NULL,
                            display_name TEXT NOT NULL,
                            scope_kind TEXT NOT NULL,
                            scope_ref TEXT NOT NULL,
                            source_object_id TEXT,
                            source_scope_kind TEXT,
                            source_scope_ref TEXT,
                            copied_at_epoch_seconds INTEGER,
                            created_at_epoch_seconds INTEGER NOT NULL,
                            updated_at_epoch_seconds INTEGER NOT NULL
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate("INSERT INTO campaign(id, name) VALUES ('$campaignId', 'Legacy Campaign')")
                    statement.executeUpdate(
                        """
                        INSERT INTO reusable_content(
                            id, family, display_name, scope_kind, scope_ref,
                            source_object_id, source_scope_kind, source_scope_ref, copied_at_epoch_seconds,
                            created_at_epoch_seconds, updated_at_epoch_seconds
                        ) VALUES (
                            '$zoneId', 'ZONE', 'Legacy Flooded Archive', 'CAMPAIGN', '$campaignId',
                            NULL, NULL, NULL, NULL, 100, 100
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate(
                        "INSERT INTO object_sync_state(object_type, object_id, revision) VALUES ('REUSABLE_CONTENT', '$zoneId', 0)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 23, newVersion = 24)
            val database = AppDatabase(driver)

            assertEquals(
                "Legacy Campaign",
                database.campaignQueries.selectCampaignById(campaignId) { _, name -> name }.executeAsOne(),
            )
            val migrated = ZoneContentRepository(database).zone(Uuid.parse(zoneId))
            assertEquals("Legacy Flooded Archive", migrated?.item?.displayName)
            assertEquals(ZonePayload(), migrated?.payload)
            driver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun zonePayloadSurvivesDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-zone-reopen", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val ownerId = Uuid.random()
        val zoneId = Uuid.random()
        val payload = samplePayload().copy(dmGuidance = "Persist across reopen")

        try {
            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val database = AppDatabase(driver)
                IntegratedSpineRepository(database).upsertAccount(AccountIdentity(ownerId))
                ZoneContentRepository(database).createPersonal(
                    ownerAccountId = ownerId,
                    rawDisplayName = "Persistent Flooded Archive",
                    payload = payload,
                    nowEpochSeconds = 100,
                    id = zoneId,
                )
            }

            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val reopened = ZoneContentRepository(AppDatabase(driver)).zone(zoneId)
                assertEquals("Persistent Flooded Archive", reopened?.item?.displayName)
                assertEquals(payload, reopened?.payload)
            }
        } finally {
            file.delete()
        }
    }

    private fun samplePayload(): ZonePayload = ZonePayload(
        summary = "A half-submerged municipal archive whose lower stacks hide the route into an older cistern complex.",
        area = "Lower Ward / East drainage district",
        presentation = "Cold water reflects rows of leaning shelves while paper scraps cling to the stone like pale leaves.",
        space = "Entry gallery, raised catalogue balcony, flooded stacks, collapsed records room, west sluice gate.",
        exploration = "Movement through the stacks is slow and noisy; the balcony offers a dry overview of most routes.",
        interactives = listOf("Operate the west sluice wheel", "Search the surviving catalogue drawers", "Cross the hanging shelf bridge"),
        clues = listOf("Recent wax drips mark a route toward the cistern stairs", "A waterlogged ledger names a sealed maintenance door"),
        checks = listOf("Athletics to force the corroded sluice", "Investigation to reconstruct the damaged catalogue index"),
        consequences = listOf("Forcing the sluice loudly alerts nearby scavengers", "A failed shelf crossing drops carried loose papers into the water"),
        encounterBrief = "Scavengers may arrive from the cistern if the party makes sustained noise; they prefer to bargain before fighting.",
        dmGuidance = "Emphasize navigation and information recovery. Keep the scavenger arrival advisory rather than automatic.",
        playerSafeText = "The old archive is flooded but still partly accessible from the Lower Ward canal walk.",
        paperReferences = listOf("Dungeon binder p. 18", "Lower Ward drainage sketch"),
        tags = listOf("archive", "flooded", "exploration"),
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
