package io.github.mrsimkin.dndcustomaid.shared.dm

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ScenePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ShopItem
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZonePayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SceneIndexTest {
    @Test
    fun theDmFindsNpcsByIdeaAndSeesWhoIsHere() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val market = ZoneContentRepository(database).createCampaign(campaign.id, "Mercado viejo", ZonePayload(summary = "Plaza de tenderetes"), 100)
        val forge = PlaceContentRepository(database).createCampaign(
            campaign.id, "La Fragua Roja", PlacePayload(kind = PlaceKind.SHOP, function = "Herrería"), 100,
        )
        val inn = PlaceContentRepository(database).createCampaign(campaign.id, "El Pato Cojo", PlacePayload(function = "Posada", area = "Mercado viejo"), 100)
        val bruna = NpcContentRepository(database).createCampaign(
            campaign.id, "Bruna Hierro", NpcPayload(conceptRole = "Herrera gruñona", appearanceFirstImpression = "Brazos tatuados. Huele a carbón."), 100,
        )
        val tom = NpcContentRepository(database).createCampaign(campaign.id, "Tom", NpcPayload(conceptRole = "Posadero", places = "El Pato Cojo"), 100)
        val arrival = SceneContentRepository(database).createCampaign(campaign.id, "Llegada", ScenePayload(purpose = "Entrar a la ciudad"), 100)
        val ambush = SceneContentRepository(database).createCampaign(campaign.id, "Emboscada", ScenePayload(), 100)
        val detour = SceneContentRepository(database).createCampaign(campaign.id, "Ladrón en el mercado", ScenePayload(), 100)

        val links = ContentLinksRepository(database)
        links.save(forge.item.identity.id, ContentLinks(zoneIds = listOf(market.item.identity.id), shopItems = listOf(ShopItem("Escudo", "10 po", highlighted = true))))
        links.save(bruna.item.identity.id, ContentLinks(placeIds = listOf(forge.item.identity.id), groups = listOf("Gremio de herreros")))
        links.save(arrival.item.identity.id, ContentLinks(adventure = "La ciudad", order = 1, sceneIds = listOf(ambush.item.identity.id)))
        links.save(ambush.item.identity.id, ContentLinks(adventure = "La ciudad", order = 2))
        links.save(detour.item.identity.id, ContentLinks(adventure = "La ciudad", order = 2, detour = true))

        val index = SceneIndex.load(database, campaign.id)

        // By idea, not by name: «herrero tatuado» finds Bruna without knowing who she is.
        assertEquals(listOf("Bruna Hierro"), index.search("herrera tatuados", SceneFilter.NPC).map { it.name })
        assertEquals(listOf("Bruna Hierro"), index.search("gremio").map { it.name }, "groups are searchable")
        assertEquals("La Fragua Roja", index.search("escudo", SceneFilter.SHOP).single().name, "shops are found by what they sell")
        assertEquals(listOf("La Fragua Roja", "El Pato Cojo").sorted(), index.search("", SceneFilter.PLACE).filter { it.kind != SceneEntryKind.ZONE }.map { it.name }.sorted())

        val bruna0 = index.search("bruna").first()
        assertEquals("Herrera gruñona", bruna0.headline, "the role comes first")
        assertEquals("Brazos tatuados", bruna0.detail)
        assertEquals("Mercado viejo › La Fragua Roja", bruna0.where)

        // «PNJ aquí»: linked from the NPC's side, and the zone also shows its places' NPCs.
        assertEquals(listOf("Bruna Hierro"), index.npcsHere(forge.item.identity.id).map { it.item.displayName })
        assertEquals(listOf("Bruna Hierro"), index.npcsHere(market.item.identity.id).map { it.item.displayName })
        assertEquals("Mercado viejo › El Pato Cojo", index.pathOf(inn.item.identity.id), "a place's «área» text still finds its zone")
        assertEquals(listOf("El Pato Cojo"), index.whereIs(tom), "without links, the free text is shown")

        val adventure = index.adventures().first()
        assertEquals("La ciudad", adventure.first)
        assertEquals(listOf("Llegada", "Emboscada", "Ladrón en el mercado"), adventure.second.map { it.item.displayName })
        assertTrue(index.isDetour(detour))
        assertEquals(listOf("Emboscada"), index.nextScenes(arrival).map { it.item.displayName })

        val byGroup = index.groupNpcs(NpcGrouping.GROUP)
        assertEquals(listOf("Gremio de herreros", "Sin grupo"), byGroup.map { it.first })
        assertEquals(listOf("Herrería", "Posada"), index.byFunction().map { it.first })
    }

    private fun db(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        driver.execute(null, "PRAGMA foreign_keys = ON", 0)
        try {
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}
