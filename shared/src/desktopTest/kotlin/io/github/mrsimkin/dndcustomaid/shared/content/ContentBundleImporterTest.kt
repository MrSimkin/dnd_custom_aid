package io.github.mrsimkin.dndcustomaid.shared.content

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPlayerRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Redesign R7: a content bundle loads a prepared campaign once, with its links, on any device. */
class ContentBundleImporterTest {
    private val bundle = parseContentBundle(
        """
        {
          "format": "dnd-custom-aid.content-bundle", "version": 1, "title": "Ciudad de prueba",
          "places": [
            {"key": "distrito:baja", "name": "Ciudad Baja", "payload": {"kind": "PLACE", "summary": "Mercado y tabernas"}},
            {"key": "tienda:fragua", "name": "La Fragua", "payload": {"kind": "SHOP"}, "parentKey": "distrito:baja",
             "shopItems": [{"name": "Espada larga", "price": "15 po"}], "npcKeys": ["pnj:olga"]}
          ],
          "npcs": [{"key": "pnj:olga", "name": "Olga", "payload": {"conceptRole": "Herrera"}, "groups": ["Gremio"], "placeKeys": ["tienda:fragua"]}],
          "encounters": [{"key": "enc:ratas", "name": "Ratas", "payload": {"summary": "Un enjambre"}}],
          "dungeons": [{"key": "dungeon:cueva", "name": "La Cueva", "zones": [
            {"key": "z:1", "name": "Entrada", "x": 0.1, "y": 0.1, "encounterKeys": ["enc:ratas"]},
            {"key": "z:2", "name": "Galería"}], "passages": [{"from": "z:1", "to": "z:2", "secret": true}]}],
          "scenes": [
            {"key": "s:1", "name": "Llegada", "adventure": "La Cueva", "order": 1, "placeKeys": ["distrito:baja"], "npcKeys": ["pnj:olga"], "nextKeys": ["s:2"]},
            {"key": "s:2", "name": "Bajada", "adventure": "La Cueva", "order": 2}
          ],
          "handouts": [{"key": "img:mapa", "name": "Mapa", "file": "mapa.png", "kind": "MAP", "visibility": "PLAYER_SAFE"}],
          "notes": [{"key": "gancho:1", "text": "Gancho 1\nAlguien vio luces en la cueva."}],
          "rules": [{"key": "dote:x", "name": "Dote de prueba", "payload": {"kind": "FEAT", "summary": "Una dote"}}],
          "characters": [{"key": "pj:ana", "name": "Ana", "player": "Daniela", "species": "Gnomo",
            "classes": [{"name": "Pícaro", "level": 7, "subclass": "Embaucador arcano", "hitDie": 8, "edition": "5.5e"}]}]
        }
        """.trimIndent(),
    )

    @Test
    fun aBundleLoadsEveryFamilyWithItsLinksAndASecondLoadAddsNothing() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Prueba")
        val blobs = InMemoryMediaBlobStore()
        val importer = ContentBundleImporter(database, blobs) { 100 }
        val png = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 1, 2, 3)

        val report = importer.load(bundle, campaign.id, ContentBundleParts.ALL, ownerAccountId = null) { if (it == "mapa.png") png else null }
        assertTrue(report.problems.isEmpty(), report.problems.toString())
        assertEquals(mapOf("lugares" to 2, "PNJ" to 1, "encuentros" to 1, "escenas" to 2, "dungeons" to 1, "zonas" to 2, "handouts" to 1, "notas" to 1, "reglas homebrew" to 1, "PJ" to 1), report.created)

        val links = ContentLinksRepository(database)
        val shop = stableContentId(campaign.id, "tienda:fragua")
        assertEquals(listOf(stableContentId(campaign.id, "distrito:baja")), links.links(shop).placeIds)
        assertEquals("15 po", links.links(shop).shopItems.single().price)
        assertEquals(listOf("Gremio"), links.links(stableContentId(campaign.id, "pnj:olga")).groups)
        val first = links.links(stableContentId(campaign.id, "s:1"))
        assertEquals("La Cueva", first.adventure)
        assertEquals(listOf(stableContentId(campaign.id, "s:2")), first.sceneIds)

        val dungeons = DungeonContentRepository(database)
        val cave = dungeons.listCampaign(campaign.id).single()
        val zones = dungeons.zonesOf(campaign.id, cave)
        assertEquals(2, zones.size)
        assertTrue(zones.first { it.zone.displayName == "Entrada" }.links.passages.single().secret)
        assertEquals(listOf(stableContentId(campaign.id, "enc:ratas")), zones.first { it.zone.displayName == "Entrada" }.links.encounterIds)

        val pc = CharacterRepository(database).listCharacters(campaign.id).single()
        assertEquals("Pícaro", pc.classes.single().name)
        assertEquals("Embaucador arcano", pc.classes.single().subclassName)
        assertEquals("Gnomo", pc.background.race)
        assertEquals("Daniela", CharacterPlayerRepository(database).player(pc.id))

        val again = importer.load(bundle, campaign.id, ContentBundleParts.ALL, ownerAccountId = null) { png }
        assertTrue(again.created.isEmpty(), "nothing new the second time: ${again.created}")
        assertEquals(1, CharacterRepository(database).listCharacters(campaign.id).size)
    }

    @Test
    fun theTabletLoadsOnlyTheParty() = db { database ->
        val campaign = CampaignRepository(database).createCampaign("Prueba")
        val report = ContentBundleImporter(database, null) { 100 }.load(bundle, campaign.id, ContentBundleParts.CHARACTERS, null) { null }
        assertEquals(mapOf("PJ" to 1), report.created)
        assertTrue(ReusableContentRepository(database).listCampaign(campaign.id, ReusableContentFamily.PLACE).isEmpty())
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
