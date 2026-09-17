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

class SceneContentRepositoryTest {
    @Test
    fun sceneRoundTripPreservesOrientationFields() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random(), externalSubject = "owner")
        spine.upsertAccount(owner)
        val repository = SceneContentRepository(database)
        val payload = samplePayload()

        val scene = repository.createPersonal(owner.id, "The Sealed Observatory", payload, 100)
        val reopened = repository.scene(scene.item.identity.id)

        assertEquals(payload, reopened?.payload)
        assertEquals(ContentScope.Personal(owner.id), reopened?.item?.identity?.scope)
        assertEquals(ReusableContentFamily.SCENE, reopened?.item?.family)
    }

    @Test
    fun campaignCopyIsIndependentAndRetainsProvenance() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = SceneContentRepository(database)
        val originalPayload = samplePayload()
        val personal = repository.createPersonal(owner.id, "The Sealed Observatory", originalPayload, 100)

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
            purpose = "Reveal who has been using the sealed observatory and why.",
            possibleNextScenes = originalPayload.possibleNextScenes + "Confront the astronomer at the north gate",
        )
        assertIs<RevisionDecision.Accepted>(
            repository.updatePayload(
                id = personal.item.identity.id,
                expectedRevision = Revision(0),
                payload = changedSource,
                updatedAtEpochSeconds = 300,
            ),
        )

        assertEquals(changedSource, repository.scene(personal.item.identity.id)?.payload)
        assertEquals(originalPayload, repository.scene(copy.item.identity.id)?.payload)
        assertEquals(Revision(0), repository.scene(copy.item.identity.id)?.item?.identity?.revision)
    }

    @Test
    fun atomicRenameStaleAndDeletedMutationsBehaveLikeOtherReusableContent() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val repository = SceneContentRepository(database)
        val original = repository.createPersonal(owner.id, "Observatory", samplePayload(), 100)
        val changed = original.payload.copy(notes = "First accepted change")

        val accepted = assertIs<RevisionDecision.Accepted>(
            repository.update(
                id = original.item.identity.id,
                expectedRevision = Revision(0),
                rawDisplayName = "The Sealed Observatory",
                payload = changed,
                updatedAtEpochSeconds = 200,
            ),
        )
        assertEquals(Revision(1), accepted.nextRevision)
        assertEquals("The Sealed Observatory", repository.scene(original.item.identity.id)?.item?.displayName)
        assertEquals(changed, repository.scene(original.item.identity.id)?.payload)

        val stale = assertIs<RevisionDecision.Stale>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(0),
                original.payload.copy(notes = "Stale overwrite"),
                250,
            ),
        )
        assertEquals(Revision(1), stale.actual)
        assertEquals(changed, repository.scene(original.item.identity.id)?.payload)

        val deleted = assertIs<RevisionDecision.Accepted>(
            repository.tombstone(original.item.identity.id, Revision(1), 300),
        )
        assertEquals(Revision(2), deleted.nextRevision)
        assertNull(repository.scene(original.item.identity.id))

        assertIs<RevisionDecision.Deleted>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(2),
                original.payload.copy(notes = "Resurrection attempt"),
                400,
            ),
        )
        val tombstoned = repository.scene(original.item.identity.id, includeDeleted = true)
        assertEquals(changed, tombstoned?.payload)
        assertEquals(Revision(2), tombstoned?.item?.identity?.revision)
        assertEquals(300, tombstoned?.item?.deletedAtEpochSeconds)
    }

    @Test
    fun migration25BackfillsExistingMetadataOnlyScene() {
        val file = File.createTempFile("dnd-custom-aid-scene-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000001001"
        val sceneId = "00000000-0000-0000-0000-000000001002"

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
                            '$sceneId', 'SCENE', 'Legacy Crossroads', 'CAMPAIGN', '$campaignId',
                            NULL, NULL, NULL, NULL, 100, 100
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate(
                        "INSERT INTO object_sync_state(object_type, object_id, revision) VALUES ('REUSABLE_CONTENT', '$sceneId', 0)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 25, newVersion = 26)
            val database = AppDatabase(driver)

            assertEquals(
                "Legacy Campaign",
                database.campaignQueries.selectCampaignById(campaignId) { _, name -> name }.executeAsOne(),
            )
            val migrated = SceneContentRepository(database).scene(Uuid.parse(sceneId))
            assertEquals("Legacy Crossroads", migrated?.item?.displayName)
            assertEquals(ScenePayload(), migrated?.payload)
            driver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun scenePayloadSurvivesDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-scene-reopen", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val ownerId = Uuid.random()
        val sceneId = Uuid.random()
        val payload = samplePayload().copy(notes = "Persist across reopen")

        try {
            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val database = AppDatabase(driver)
                IntegratedSpineRepository(database).upsertAccount(AccountIdentity(ownerId))
                SceneContentRepository(database).createPersonal(
                    ownerAccountId = ownerId,
                    rawDisplayName = "Persistent Observatory",
                    payload = payload,
                    nowEpochSeconds = 100,
                    id = sceneId,
                )
            }

            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val reopened = SceneContentRepository(AppDatabase(driver)).scene(sceneId)
                assertEquals("Persistent Observatory", reopened?.item?.displayName)
                assertEquals(payload, reopened?.payload)
            }
        } finally {
            file.delete()
        }
    }

    private fun samplePayload(): ScenePayload = ScenePayload(
        purpose = "Discover why the observatory was sealed and choose what lead to follow next.",
        possibleNextScenes = listOf(
            "Follow the star-map trail into the old market",
            "Question the night watch captain",
        ),
        references = listOf("Place: Sealed Observatory", "NPC: Night Watch Captain", "Notebook p. 14"),
        notes = "Orientation only: these are preparation cues, not executable quest-state transitions.",
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
