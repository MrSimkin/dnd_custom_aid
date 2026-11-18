package io.github.mrsimkin.dndcustomaid.shared.content

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/** Owner, 2026-11-18: deleting must be possible, and a mistake must be recoverable. */
class ContentTrashTest {
    private fun scenario(block: (AppDatabase, Uuid) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        try { block(database, campaign.id) } finally { driver.close() }
    }

    @Test
    fun aDeletedNpcLeavesTheListAndWaitsInTheTrash() = scenario { database, campaignId ->
        val npcs = NpcContentRepository(database)
        val barek = npcs.createCampaign(campaignId, "Barek Surn", NpcPayload(conceptRole = "Capataz"), 100)
        val trash = ContentTrash(database)

        assertEquals(1, ReusableContentRepository(database).listCampaign(campaignId, ReusableContentFamily.NPC).size)
        assertTrue(trash.list(ContentScope.Campaign(campaignId)).isEmpty())

        npcs.tombstone(barek.item.identity.id, barek.item.identity.revision, 200)

        assertTrue(ReusableContentRepository(database).listCampaign(campaignId, ReusableContentFamily.NPC).isEmpty(), "it leaves the list")
        val waiting = trash.list(ContentScope.Campaign(campaignId)).single()
        assertEquals("Barek Surn", waiting.item.displayName)
        assertEquals(200, waiting.deletedAtEpochSeconds)
    }

    @Test
    fun restoringBringsBackTheContentAndWhatPointedAtIt() = scenario { database, campaignId ->
        val npcs = NpcContentRepository(database)
        val places = PlaceContentRepository(database)
        val links = ContentLinksRepository(database)
        val barek = npcs.createCampaign(
            campaignId,
            "Barek Surn",
            NpcPayload(conceptRole = "Capataz", secrets = "Falsea el conteo."),
            100,
        )
        val shop = places.createCampaign(campaignId, "Punta y Compás", PlacePayload(summary = "Tienda"), 100)
        val deletedId = barek.item.identity.id
        links.save(shop.item.identity.id, ContentLinks(npcIds = listOf(deletedId)))

        npcs.tombstone(deletedId, barek.item.identity.revision, 200)
        val restored = ContentTrash(database).restore(deletedId, nowEpochSeconds = 300)

        assertNotEquals(deletedId, restored.identity.id, "the tombstone is never resurrected")
        assertEquals("Barek Surn", restored.displayName)
        val payload = npcs.payload(restored.identity.id)
        assertEquals("Capataz", payload?.conceptRole)
        assertEquals("Falsea el conteo.", payload?.secrets)
        assertEquals(listOf(restored.identity.id), links.links(shop.item.identity.id).npcIds, "the shop now points at the copy")
        assertEquals(1, ReusableContentRepository(database).listCampaign(campaignId, ReusableContentFamily.NPC).size)
        assertTrue(ContentTrash(database).list(ContentScope.Campaign(campaignId)).single().item.identity.id == deletedId)
    }

    @Test
    fun theTrashOnlyAnswersForWhatWasDeleted() = scenario { database, campaignId ->
        val creatures = CreatureContentRepository(database)
        val goblin = creatures.createCampaign(campaignId, "Goblin", CreaturePayload(armorClass = 15, hitPoints = 7), 100)
        val trash = ContentTrash(database)

        val alive = runCatching { trash.restore(goblin.item.identity.id, 200) }
        assertTrue(alive.isFailure, "what is not deleted cannot be restored")

        creatures.tombstone(goblin.item.identity.id, goblin.item.identity.revision, 200)
        val restored = trash.restore(goblin.item.identity.id, 300)
        assertEquals(15, creatures.payload(restored.identity.id)?.armorClass)
        assertEquals(ReusableContentFamily.CREATURE, restored.family)
    }
}
