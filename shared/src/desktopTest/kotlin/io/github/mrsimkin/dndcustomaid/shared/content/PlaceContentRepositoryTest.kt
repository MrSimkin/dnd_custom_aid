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

class PlaceContentRepositoryTest {
    @Test
    fun shopRoundTripPreservesSpecializedPlaceData() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random(), externalSubject = "owner")
        spine.upsertAccount(owner)
        val repository = PlaceContentRepository(database)
        val payload = samplePayload()

        val shop = repository.createPersonal(owner.id, "The Brass Lantern", payload, 100)
        val reopened = repository.place(shop.item.identity.id)

        assertEquals(payload, reopened?.payload)
        assertEquals(PlaceKind.SHOP, reopened?.payload?.kind)
        assertEquals(ContentScope.Personal(owner.id), reopened?.item?.identity?.scope)
        assertEquals(ReusableContentFamily.PLACE, reopened?.item?.family)
    }

    @Test
    fun campaignCopyIsIndependentAndRetainsProvenance() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = PlaceContentRepository(database)
        val originalPayload = samplePayload()
        val personal = repository.createPersonal(owner.id, "The Brass Lantern", originalPayload, 100)

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
            summary = "A quieter version of the shop after the guild embargo.",
            services = originalPayload.services + "Discrete courier arrangements",
            dmNotes = "The proprietor now hides contraband in the cellar.",
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
        assertEquals(changedSource, repository.place(personal.item.identity.id)?.payload)
        assertEquals(originalPayload, repository.place(copy.item.identity.id)?.payload)
        assertEquals(Revision(0), repository.place(copy.item.identity.id)?.item?.identity?.revision)
    }

    @Test
    fun staleAndDeletedPlaceMutationsCannotOverwriteOrResurrect() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val repository = PlaceContentRepository(database)
        val original = repository.createPersonal(owner.id, "The Brass Lantern", samplePayload(), 100)
        val changed = original.payload.copy(dmNotes = "First accepted change")

        assertIs<RevisionDecision.Accepted>(
            repository.updatePayload(original.item.identity.id, Revision(0), changed, 200),
        )
        val stale = assertIs<RevisionDecision.Stale>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(0),
                original.payload.copy(dmNotes = "Stale overwrite"),
                250,
            ),
        )
        assertEquals(Revision(1), stale.actual)
        assertEquals(changed, repository.place(original.item.identity.id)?.payload)

        val deleted = assertIs<RevisionDecision.Accepted>(
            repository.tombstone(original.item.identity.id, Revision(1), 300),
        )
        assertEquals(Revision(2), deleted.nextRevision)
        assertNull(repository.place(original.item.identity.id))

        assertIs<RevisionDecision.Deleted>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(2),
                original.payload.copy(dmNotes = "Resurrection attempt"),
                400,
            ),
        )
        val tombstoned = repository.place(original.item.identity.id, includeDeleted = true)
        assertEquals(changed, tombstoned?.payload)
        assertEquals(Revision(2), tombstoned?.item?.identity?.revision)
        assertEquals(300, tombstoned?.item?.deletedAtEpochSeconds)
    }

    @Test
    fun migration22PreservesExistingCampaignAndBackfillsMetadataOnlyPlace() {
        val file = File.createTempFile("dnd-custom-aid-place-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000001001"
        val placeId = "00000000-0000-0000-0000-000000001002"

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
                            '$placeId', 'PLACE', 'Legacy Watchtower', 'CAMPAIGN', '$campaignId',
                            NULL, NULL, NULL, NULL, 100, 100
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate(
                        "INSERT INTO object_sync_state(object_type, object_id, revision) VALUES ('REUSABLE_CONTENT', '$placeId', 0)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 22, newVersion = 23)
            val database = AppDatabase(driver)

            assertEquals(
                "Legacy Campaign",
                database.campaignQueries.selectCampaignById(campaignId) { _, name -> name }.executeAsOne(),
            )
            val migrated = PlaceContentRepository(database).place(Uuid.parse(placeId))
            assertEquals("Legacy Watchtower", migrated?.item?.displayName)
            assertEquals(PlacePayload(), migrated?.payload)
            driver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun placePayloadSurvivesDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-place-reopen", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val ownerId = Uuid.random()
        val placeId = Uuid.random()
        val payload = samplePayload().copy(dmNotes = "Persist across reopen")

        try {
            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val database = AppDatabase(driver)
                IntegratedSpineRepository(database).upsertAccount(AccountIdentity(ownerId))
                PlaceContentRepository(database).createPersonal(
                    ownerAccountId = ownerId,
                    rawDisplayName = "Persistent Brass Lantern",
                    payload = payload,
                    nowEpochSeconds = 100,
                    id = placeId,
                )
            }

            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val reopened = PlaceContentRepository(AppDatabase(driver)).place(placeId)
                assertEquals("Persistent Brass Lantern", reopened?.item?.displayName)
                assertEquals(payload, reopened?.payload)
            }
        } finally {
            file.delete()
        }
    }

    private fun samplePayload(): PlacePayload = PlacePayload(
        kind = PlaceKind.SHOP,
        summary = "A brass-and-glass lamp shop that doubles as a neighborhood information hub.",
        area = "Old Market / Lantern Row",
        function = "Shop, rumor exchange, and safe meeting point",
        presentation = "Warm amber light spills through crowded windows; tiny bells chime whenever the front door opens.",
        services = listOf("Lamps and oil", "Minor metal repairs", "Local delivery"),
        interactives = listOf("Browse coded lamp designs", "Ask about recent night traffic"),
        hooks = listOf("A customer never returned for a locked brass case", "The rear alley lamp is extinguished every third night"),
        playerSafeText = "The Brass Lantern sells lamps, oil, and repair work to most of Lantern Row.",
        dmNotes = "The proprietor quietly trades neighborhood information but refuses violence on the premises.",
        paperReferences = listOf("Campaign binder p. 42", "Old Market sketch"),
        tags = listOf("shop", "old-market", "rumors"),
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
