package io.github.mrsimkin.dndcustomaid.shared.content

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.SceneIndex
import io.github.mrsimkin.dndcustomaid.shared.dm.dungeonMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/** Redesign R3 (owner observation P5, decision D5): a dungeon is created, drawn and connected. */
class DungeonContentTest {
    @Test
    fun aDungeonIsCreatedAndItsZonesAreAddedOnThePlan() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val dungeons = DungeonContentRepository(database)
        val site = dungeons.create(campaign.id, "Sitio 021", DungeonPayload(summary = "Extracción y ensayo"), 100)
        val a1 = dungeons.addZone(campaign.id, site, "A1 Explanada", 0.1, 0.2, 100)
        val a2 = dungeons.addZone(campaign.id, site, "A2 Caseta", 0.5, 0.2, 100)
        dungeons.connect(a1.item.identity.id, a2.item.identity.id)

        val zones = dungeons.zonesOf(campaign.id, dungeons.listCampaign(campaign.id).single())
        assertEquals(listOf("A1 Explanada", "A2 Caseta"), zones.map { it.zone.displayName })
        assertEquals(0.1, zones.first().links.diagramX)
        assertEquals(listOf(a2.item.identity.id), zones.first().links.passages.map { it.toZoneId })

        // The tablet's Mesa de Dungeon reads the same plan by name, passages both ways.
        val index = SceneIndex.load(database, campaign.id)
        val map = index.dungeonMap("Sitio 021")
        assertEquals(2, map.size)
        assertTrue(map.all { it.passages.size == 1 }, "a passage shows from both zones")
    }

    @Test
    fun connectingTwiceKeepsOnePassageAndSecretAndRemovalWorkFromEitherSide() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val dungeons = DungeonContentRepository(database)
        val links = ContentLinksRepository(database)
        val tower = dungeons.create(campaign.id, "La Torre", DungeonPayload(), 100)
        val atrio = dungeons.addZone(campaign.id, tower, "Atrio", 0.5, 0.1, 100).item.identity.id
        val patio = dungeons.addZone(campaign.id, tower, "Patio", 0.5, 0.3, 100).item.identity.id

        dungeons.connect(atrio, patio)
        dungeons.connect(patio, atrio)
        val stored = links.links(atrio).passages + links.links(patio).passages
        assertEquals(1, stored.size, "one passage, stored once")

        dungeons.setSecret(patio, atrio, true)
        assertTrue(links.links(atrio).passages.single().secret)

        dungeons.disconnect(patio, atrio)
        assertTrue(links.links(atrio).passages.isEmpty() && links.links(patio).passages.isEmpty())
    }

    @Test
    fun renamingTheDungeonRenamesWhatItsZonesCarry() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val dungeons = DungeonContentRepository(database)
        val site = dungeons.create(campaign.id, "Sitio", DungeonPayload(), 100)
        val zone = dungeons.addZone(campaign.id, site, "Explanada", 0.1, 0.1, 100).item.identity.id
        dungeons.update(site.item.identity.id, site.item.identity.revision, "Sitio 021", DungeonPayload(summary = "Nuevo"), 200)
        assertEquals("Sitio 021", ContentLinksRepository(database).links(zone).dungeon)
        assertEquals("Nuevo", dungeons.dungeon(site.item.identity.id)!!.payload.summary)
    }

    @Test
    fun namesTypedInOlderZonesBecomeOneDungeonWithTheSameIdOnEveryDevice() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val zones = ZoneContentRepository(database)
        val links = ContentLinksRepository(database)
        val hall = zones.createCampaign(campaign.id, "Antesala", ZonePayload(), 100).item.identity.id
        val crypt = zones.createCampaign(campaign.id, "Cripta", ZonePayload(), 100).item.identity.id
        links.save(hall, ContentLinks(dungeon = "Capilla del Eco"))
        links.save(crypt, ContentLinks(dungeon = "capilla del eco"))

        val dungeons = DungeonContentRepository(database)
        val created = dungeons.adoptLegacyNames(campaign.id, 200)
        assertEquals(1, created.size, "same name, different case: one dungeon")
        assertEquals(legacyDungeonId(campaign.id, "Capilla del Eco"), created.single().item.identity.id)
        assertEquals(created.single().item.identity.id, links.links(crypt).dungeonId)
        assertTrue(dungeons.adoptLegacyNames(campaign.id, 300).isEmpty(), "running it again changes nothing")
        assertEquals(2, dungeons.zonesOf(campaign.id, created.single()).size)
    }

    @Test
    fun theLegacyIdDependsOnCampaignAndNameOnly() {
        val campaign = Uuid.parse("20000000-0000-4000-8000-000000000801")
        assertEquals(legacyDungeonId(campaign, "La Torre"), legacyDungeonId(campaign, " la torre "))
        assertFalse(legacyDungeonId(campaign, "La Torre") == legacyDungeonId(campaign, "Sitio 021"))
        assertEquals(4, legacyDungeonId(campaign, "La Torre").toString()[14].digitToInt(), "a version-4 shaped id")
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
