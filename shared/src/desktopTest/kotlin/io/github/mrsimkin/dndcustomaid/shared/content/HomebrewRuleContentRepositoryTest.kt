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

class HomebrewRuleContentRepositoryTest {
    @Test
    fun lightweightRuleRoundTripPreservesStructuredListsAndDraftLifecycle() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random(), externalSubject = "owner")
        spine.upsertAccount(owner)
        val repository = HomebrewRuleContentRepository(database)
        val payload = samplePayload()

        val rule = repository.createPersonal(owner.id, "Heroic Inspiration Variant", payload, 100)
        val reopened = repository.rule(rule.item.identity.id)

        assertEquals(payload, reopened?.payload)
        assertEquals(HomebrewRuleLifecycle.DRAFT, reopened?.payload?.lifecycle)
        assertEquals(ContentScope.Personal(owner.id), reopened?.item?.identity?.scope)
        assertEquals(ReusableContentFamily.HOMEBREW_RULE, reopened?.item?.family)
    }

    @Test
    fun campaignCopyIsIndependentAndRetainsProvenance() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = HomebrewRuleContentRepository(database)
        val originalPayload = samplePayload()
        val personal = repository.createPersonal(owner.id, "Heroic Inspiration Variant", originalPayload, 100)

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
            lifecycle = HomebrewRuleLifecycle.ACTIVE,
            summary = "Award inspiration for costly choices that materially complicate the character's situation.",
            tags = originalPayload.tags + "active-campaign-rule",
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
        assertEquals(changedSource, repository.rule(personal.item.identity.id)?.payload)
        assertEquals(originalPayload, repository.rule(copy.item.identity.id)?.payload)
        assertEquals(Revision(0), repository.rule(copy.item.identity.id)?.item?.identity?.revision)
    }

    @Test
    fun staleAndDeletedRuleMutationsCannotOverwriteOrResurrect() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val repository = HomebrewRuleContentRepository(database)
        val original = repository.createPersonal(owner.id, "Heroic Inspiration Variant", samplePayload(), 100)
        val changed = original.payload.copy(notes = "First accepted change")

        assertIs<RevisionDecision.Accepted>(
            repository.updatePayload(original.item.identity.id, Revision(0), changed, 200),
        )
        val stale = assertIs<RevisionDecision.Stale>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(0),
                original.payload.copy(notes = "Stale overwrite"),
                250,
            ),
        )
        assertEquals(Revision(1), stale.actual)
        assertEquals(changed, repository.rule(original.item.identity.id)?.payload)

        val deleted = assertIs<RevisionDecision.Accepted>(
            repository.tombstone(original.item.identity.id, Revision(1), 300),
        )
        assertEquals(Revision(2), deleted.nextRevision)
        assertNull(repository.rule(original.item.identity.id))

        assertIs<RevisionDecision.Deleted>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(2),
                original.payload.copy(notes = "Resurrection attempt"),
                400,
            ),
        )
        val tombstoned = repository.rule(original.item.identity.id, includeDeleted = true)
        assertEquals(changed, tombstoned?.payload)
        assertEquals(Revision(2), tombstoned?.item?.identity?.revision)
        assertEquals(300, tombstoned?.item?.deletedAtEpochSeconds)
    }

    @Test
    fun migration21PreservesExistingCampaignAndBackfillsMetadataOnlyRule() {
        val file = File.createTempFile("dnd-custom-aid-homebrew-rule-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000000901"
        val ruleId = "00000000-0000-0000-0000-000000000902"

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
                            '$ruleId', 'HOMEBREW_RULE', 'Legacy Rest Variant', 'CAMPAIGN', '$campaignId',
                            NULL, NULL, NULL, NULL, 100, 100
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate(
                        "INSERT INTO object_sync_state(object_type, object_id, revision) VALUES ('REUSABLE_CONTENT', '$ruleId', 0)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 21, newVersion = 22)
            val database = AppDatabase(driver)

            assertEquals(
                "Legacy Campaign",
                database.campaignQueries.selectCampaignById(campaignId) { _, name -> name }.executeAsOne(),
            )
            val migrated = HomebrewRuleContentRepository(database).rule(Uuid.parse(ruleId))
            assertEquals("Legacy Rest Variant", migrated?.item?.displayName)
            assertEquals(HomebrewRulePayload(), migrated?.payload)
            driver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun homebrewRulePayloadSurvivesDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-homebrew-rule-reopen", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val ownerId = Uuid.random()
        val ruleId = Uuid.random()
        val payload = samplePayload().copy(
            lifecycle = HomebrewRuleLifecycle.ACTIVE,
            notes = "Persist across reopen",
        )

        try {
            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val database = AppDatabase(driver)
                IntegratedSpineRepository(database).upsertAccount(AccountIdentity(ownerId))
                HomebrewRuleContentRepository(database).createPersonal(
                    ownerAccountId = ownerId,
                    rawDisplayName = "Persistent Rest Variant",
                    payload = payload,
                    nowEpochSeconds = 100,
                    id = ruleId,
                )
            }

            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val reopened = HomebrewRuleContentRepository(AppDatabase(driver)).rule(ruleId)
                assertEquals("Persistent Rest Variant", reopened?.item?.displayName)
                assertEquals(payload, reopened?.payload)
            }
        } finally {
            file.delete()
        }
    }

    private fun samplePayload(): HomebrewRulePayload = HomebrewRulePayload(
        summary = "Inspiration rewards costly character-driven choices instead of generic good play.",
        body = "When a character voluntarily accepts a meaningful complication consistent with an established ideal, bond, flaw, or personal goal, the DM may award Inspiration.",
        category = "Character / Inspiration",
        rationale = "Keeps Inspiration tied to visible characterization and meaningful table consequences.",
        examples = listOf(
            "The paladin refuses an expedient lie and loses access to an informant.",
            "The rogue protects a rival because of an established debt and gives up an escape route.",
        ),
        relatedReferences = listOf("PHB: Inspiration", "Campaign tone notes"),
        tags = listOf("inspiration", "character", "table-rule"),
        lifecycle = HomebrewRuleLifecycle.DRAFT,
        notes = "Human-readable rule text remains authoritative; no automatic rules execution is implied.",
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
