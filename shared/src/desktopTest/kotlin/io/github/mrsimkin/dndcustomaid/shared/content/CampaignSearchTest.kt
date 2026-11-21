package io.github.mrsimkin.dndcustomaid.shared.content

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/**
 * Owner, 2026-11-18 («K»): one search for the whole campaign, with each result saying what it is,
 * what it says and where it lives, and with a way to leave families out — «excluir reglas».
 */
class CampaignSearchTest {
    private fun scenario(block: (AppDatabase, Uuid) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        try { block(database, campaign.id) } finally { driver.close() }
    }

    @Test
    fun aNameFoundAnywhereSaysWhatItIsAndWhereItLives() = scenario { database, campaignId ->
        val places = PlaceContentRepository(database)
        val npcs = NpcContentRepository(database)
        val shop = places.createCampaign(campaignId, "Punta y Compás", PlacePayload(summary = "Tienda de instrumentos"), 100)
        val barek = npcs.createCampaign(campaignId, "Barek Surn", NpcPayload(conceptRole = "Capataz del turno de noche"), 100)
        ContentLinksRepository(database).save(barek.item.identity.id, ContentLinks(placeIds = listOf(shop.item.identity.id)))

        val results = CampaignSearch(database).search(campaignId, "barek")

        val found = results.single()
        assertEquals(ReusableContentFamily.NPC, found.family)
        assertEquals("Barek Surn", found.name)
        assertEquals("Capataz del turno de noche", found.summary)
        assertEquals("Punta y Compás", found.origin, "it says where it lives")
    }

    @Test
    fun whatSomethingSaysAlsoCounts() = scenario { database, campaignId ->
        SceneContentRepository(database).createCampaign(
            campaignId,
            "Patio de campanas",
            ScenePayload(purpose = "El grupo oye la campana rota desde el pórtico."),
            100,
        )

        val byName = CampaignSearch(database).search(campaignId, "patio")
        val byText = CampaignSearch(database).search(campaignId, "campana rota")

        assertEquals(1, byName.size)
        assertEquals(1, byText.size)
        assertEquals(ReusableContentFamily.SCENE, byText.single().family)
    }

    @Test
    fun theRulesStayOutUnlessTheDmAsksForThem() = scenario { database, campaignId ->
        HomebrewRuleContentRepository(database).createCampaign(
            campaignId,
            "Beber pociones",
            HomebrewRulePayload(summary = "Beber una poción es acción adicional."),
            100,
        )
        NpcContentRepository(database).createCampaign(campaignId, "Pocionera Vane", NpcPayload(conceptRole = "Vende pociones"), 100)

        val withoutRules = CampaignSearch(database).search(campaignId, "poci")
        val withRules = CampaignSearch(database).search(campaignId, "poci", families = SEARCHABLE_FAMILIES.toSet())

        assertEquals(listOf(ReusableContentFamily.NPC), withoutRules.map { it.family })
        assertTrue(withRules.any { it.family == ReusableContentFamily.HOMEBREW_RULE }, withRules.toString())
    }

    @Test
    fun oneLetterIsNotASearch() = scenario { database, campaignId ->
        NpcContentRepository(database).createCampaign(campaignId, "Barek Surn", NpcPayload(), 100)
        assertTrue(CampaignSearch(database).search(campaignId, "b").isEmpty())
        assertTrue(CampaignSearch(database).search(campaignId, "  ").isEmpty())
    }

    @Test
    fun resultsKeepTheOrderADmThinksIn() = scenario { database, campaignId ->
        PlaceContentRepository(database).createCampaign(campaignId, "Taller de campanas", PlacePayload(), 100)
        NpcContentRepository(database).createCampaign(campaignId, "Campanero Ruso", NpcPayload(), 100)
        CreatureContentRepository(database).createCampaign(campaignId, "Campana viva", CreaturePayload(armorClass = 17, hitPoints = 30), 100)

        val families = CampaignSearch(database).search(campaignId, "campan").map { it.family }

        assertEquals(
            listOf(ReusableContentFamily.NPC, ReusableContentFamily.PLACE, ReusableContentFamily.CREATURE),
            families,
        )
    }
}
