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

class NpcContentRepositoryTest {
    @Test
    fun quickNpcRoundTripIsValidWithoutCombatMechanics() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random(), externalSubject = "owner")
        spine.upsertAccount(owner)
        val repository = NpcContentRepository(database)
        val payload = quickPayload()

        val npc = repository.createPersonal(owner.id, "Mara Venn", payload, 100)
        val reopened = repository.npc(npc.item.identity.id)

        assertEquals(payload, reopened?.payload)
        assertNull(reopened?.payload?.combatMechanics)
        assertEquals(ContentScope.Personal(owner.id), reopened?.item?.identity?.scope)
        assertEquals(ReusableContentFamily.NPC, reopened?.item?.family)
    }

    @Test
    fun developedNpcCopyIsIndependentAndRetainsOptionalCreatureMechanics() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val repository = NpcContentRepository(database)
        val originalPayload = developedPayload()
        val personal = repository.createPersonal(owner.id, "Captain Ilyra", originalPayload, 100)

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
        assertEquals(Revision(0), copy.item.identity.revision)
        assertEquals(ContentScope.Campaign(campaign.id), copy.item.identity.scope)

        val changedSource = originalPayload.copy(
            motivations = "Protect the district even if the council objects.",
            combatMechanics = originalPayload.combatMechanics?.copy(tactics = "Controls chokepoints and protects civilians."),
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
        assertEquals(changedSource, repository.npc(personal.item.identity.id)?.payload)
        assertEquals(originalPayload, repository.npc(copy.item.identity.id)?.payload)
        assertEquals(Revision(0), repository.npc(copy.item.identity.id)?.item?.identity?.revision)
    }

    @Test
    fun staleAndDeletedNpcPayloadMutationsCannotOverwriteOrResurrect() = withDatabase { database ->
        val spine = IntegratedSpineRepository(database)
        val owner = AccountIdentity(Uuid.random())
        spine.upsertAccount(owner)
        val repository = NpcContentRepository(database)
        val original = repository.createPersonal(owner.id, "Mara Venn", quickPayload(), 100)
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
        assertEquals(changed, repository.npc(original.item.identity.id)?.payload)

        val deleted = assertIs<RevisionDecision.Accepted>(
            repository.tombstone(original.item.identity.id, Revision(1), 300),
        )
        assertEquals(Revision(2), deleted.nextRevision)
        assertNull(repository.npc(original.item.identity.id))

        assertIs<RevisionDecision.Deleted>(
            repository.updatePayload(
                original.item.identity.id,
                Revision(2),
                original.payload.copy(notes = "Resurrection attempt"),
                400,
            ),
        )
        val tombstoned = repository.npc(original.item.identity.id, includeDeleted = true)
        assertEquals(changed, tombstoned?.payload)
        assertEquals(Revision(2), tombstoned?.item?.identity?.revision)
        assertEquals(300, tombstoned?.item?.deletedAtEpochSeconds)
    }

    @Test
    fun migration20PreservesExistingCampaignAndBackfillsMetadataOnlyNpc() {
        val file = File.createTempFile("dnd-custom-aid-npc-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000000801"
        val npcId = "00000000-0000-0000-0000-000000000802"

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
                            '$npcId', 'NPC', 'Legacy Archivist', 'CAMPAIGN', '$campaignId',
                            NULL, NULL, NULL, NULL, 100, 100
                        )
                        """.trimIndent(),
                    )
                    statement.executeUpdate(
                        "INSERT INTO object_sync_state(object_type, object_id, revision) VALUES ('REUSABLE_CONTENT', '$npcId', 0)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 20, newVersion = 21)
            val database = AppDatabase(driver)

            assertEquals(
                "Legacy Campaign",
                database.campaignQueries.selectCampaignById(campaignId) { _, name -> name }.executeAsOne(),
            )
            val migrated = NpcContentRepository(database).npc(Uuid.parse(npcId))
            assertEquals("Legacy Archivist", migrated?.item?.displayName)
            assertEquals(NpcPayload(), migrated?.payload)
            driver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun npcPayloadSurvivesDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-npc-reopen", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val ownerId = Uuid.random()
        val npcId = Uuid.random()
        val payload = developedPayload().copy(notes = "Persist across reopen")

        try {
            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val database = AppDatabase(driver)
                IntegratedSpineRepository(database).upsertAccount(AccountIdentity(ownerId))
                NpcContentRepository(database).createPersonal(
                    ownerAccountId = ownerId,
                    rawDisplayName = "Persistent Captain",
                    payload = payload,
                    nowEpochSeconds = 100,
                    id = npcId,
                )
            }

            JdbcSqliteDriver(jdbcUrl, schema = AppDatabase.Schema).use { driver ->
                val reopened = NpcContentRepository(AppDatabase(driver)).npc(npcId)
                assertEquals("Persistent Captain", reopened?.item?.displayName)
                assertEquals(payload, reopened?.payload)
            }
        } finally {
            file.delete()
        }
    }

    private fun quickPayload(): NpcPayload = NpcPayload(
        conceptRole = "Night-shift archivist who knows which records are missing.",
        appearanceFirstImpression = "Ink-stained fingers, immaculate coat, exhausted eyes.",
        personalityManner = "Dry, precise, and visibly impatient with vague questions.",
        wantsFearsNeeds = "Wants the archive protected; fears public scandal.",
        canOffer = "Access to old permits and a quiet introduction to the registrar.",
        limitsRefusals = "Will not falsify a public record.",
        relationshipContext = "Trusted by the district clerk, disliked by smugglers.",
        notes = "A valid quick NPC with no combat block.",
    )

    private fun developedPayload(): NpcPayload = NpcPayload(
        conceptRole = "Watch captain balancing civic duty and political pressure.",
        appearanceFirstImpression = "Weathered uniform, silver braid, deliberate posture.",
        personalityManner = "Measured voice; pauses before making promises.",
        wantsFearsNeeds = "Needs stability, fears becoming the council's enforcer.",
        canOffer = "Watch access, local intelligence, emergency manpower.",
        limitsRefusals = "Will not target civilians to satisfy political orders.",
        relationshipContext = "Commands the south watch and owes the healer a life-debt.",
        identityDetails = "Human woman; veteran officer; known publicly as Captain Ilyra.",
        voiceMannerisms = "Low voice, repeats the last word of a difficult question before answering.",
        motivations = "Keep the district safe without surrendering the watch to the council.",
        valuesBeliefs = "Duty must serve people rather than officeholders.",
        relationships = "Protective of junior officers; distrusts Councilor Veiss.",
        history = "Rose through the watch during the river riots.",
        secrets = "Quietly keeps evidence against a council faction.",
        knowledge = "Knows patrol gaps, old tunnels, and which officials can be trusted.",
        goals = "Expose corruption without destabilizing the district.",
        resources = "Watch patrols, holding cells, informants, modest emergency funds.",
        affiliations = "South Watch; informal ties to the temple clinic.",
        places = "South Watch House; River Gate; old customs office.",
        adventureSceneLinks = "River Gate ambush; council hearing; missing-ledger investigation.",
        dmGuidance = "Useful ally, but asks what lawful authority the party actually has.",
        combatMechanics = sampleCreatureMechanics(),
        notes = "Developed NPC with optional Creature mechanics.",
    )

    private fun sampleCreatureMechanics(): CreaturePayload = CreaturePayload(
        size = "Medium",
        creatureType = "Humanoid (human)",
        alignment = "Lawful Neutral",
        armorClass = 16,
        armorClassDetails = "breastplate",
        hitPoints = 45,
        hitDice = "6d8+18",
        speed = "30 ft.",
        abilityScores = CreatureAbilityScores(14, 12, 16, 11, 14, 13),
        savingThrows = "Con +5, Wis +4",
        skills = "Insight +4, Perception +4",
        senses = "passive Perception 14",
        languages = "Common",
        challengeRating = "3",
        proficiencyBonus = 2,
        traits = "Commanding Presence. Allies nearby steady themselves under pressure.",
        actions = "Longsword. Melee Weapon Attack.",
        reactions = "Protective Interpose.",
        tactics = "Holds a defensive line and avoids unnecessary pursuit.",
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
