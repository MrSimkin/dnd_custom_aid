package io.github.mrsimkin.dndcustomaid.shared.content

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSnapshotCodec
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/**
 * Owner, 2026-11-18 («M»: «espero que haya más material»). The Campus Celeste set is already 1083
 * objects; this builds a campaign about twice that and measures what the DM waits for, so a change
 * that makes it crawl is noticed here and not at the table.
 *
 * The limits are generous on purpose: they catch something going quadratic, not a slow machine.
 */
class LargeCampaignPerformanceTest {
    private val places = 400
    private val npcs = 900
    private val scenes = 300
    private val creatures = 400

    @Test
    fun aCampaignOfTwoThousandObjectsStaysWorkable() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        val campaign = CampaignRepository(database).createCampaign("Campaña grande")
        val content = ReusableContentRepository(database)
        val links = ContentLinksRepository(database)

        val built = measureTimeMillis { fill(database, campaign.id, links) }
        val total = content.listCampaign(campaign.id).size
        assertTrue(total >= 2_000, "the campaign must be big enough to be a measurement: $total")

        val listed: List<ReusableContentItem>
        val listing = measureTimeMillis { listed = content.listCampaign(campaign.id, ReusableContentFamily.NPC) }
        val found: List<CampaignSearchResult>
        val searching = measureTimeMillis { found = CampaignSearch(database).search(campaign.id, "turno 42") }
        val exported: Int
        val exporting = measureTimeMillis {
            val codec = DmContentSnapshotCodec(database)
            exported = listed.take(200).count { codec.export(it.identity.id) != null }
        }

        // The worst case: results that have no links of their own, so each one looks for whoever
        // mentions it. This is what turns quadratic if the links are read per result.
        val orphans: List<CampaignSearchResult>
        val searchingOrphans = measureTimeMillis { orphans = CampaignSearch(database).search(campaign.id, "criatura") }

        println(
            "Campaña grande: $total objetos · construir ${built} ms · " +
                "listar PNJ ${listing} ms · buscar ${searching} ms · buscar sin enlaces ${searchingOrphans} ms · " +
                "exportar 200 ${exporting} ms",
        )

        assertTrue(listed.size == npcs, "every PNJ is listed: ${listed.size}")
        assertTrue(found.isNotEmpty(), "the search finds the one that was asked for")
        assertTrue(exported == 200)
        assertTrue(listing < 3_000, "listing the PNJ took $listing ms")
        assertTrue(searching < 20_000, "searching the whole campaign took $searching ms")
        assertTrue(exporting < 20_000, "exporting 200 objects took $exporting ms")
        assertTrue(orphans.size >= 50, "the worst case must really match many objects: ${orphans.size}")
        assertTrue(searchingOrphans < 20_000, "searching without links took $searchingOrphans ms")
        driver.close()
    }

    private fun fill(database: AppDatabase, campaignId: Uuid, links: ContentLinksRepository) {
        val placeRepository = PlaceContentRepository(database)
        val npcRepository = NpcContentRepository(database)
        val sceneRepository = SceneContentRepository(database)
        val creatureRepository = CreatureContentRepository(database)
        val placeIds = mutableListOf<Uuid>()

        database.transaction {
            repeat(places) { index ->
                val place = placeRepository.createCampaign(
                    campaignId,
                    "Lugar $index",
                    PlacePayload(summary = "Un lugar del campus, el número $index."),
                    100,
                )
                placeIds += place.item.identity.id
            }
            repeat(npcs) { index ->
                val npc = npcRepository.createCampaign(
                    campaignId,
                    "PNJ $index",
                    NpcPayload(conceptRole = "Atiende el turno $index", secrets = "Secreto $index"),
                    100,
                )
                links.save(npc.item.identity.id, ContentLinks(placeIds = listOf(placeIds[index % placeIds.size])))
            }
            repeat(scenes) { index ->
                sceneRepository.createCampaign(campaignId, "Escena $index", ScenePayload(purpose = "Pasa algo, el número $index."), 100)
            }
            repeat(creatures) { index ->
                creatureRepository.createCampaign(
                    campaignId,
                    "Criatura $index",
                    CreaturePayload(armorClass = 12, hitPoints = 20 + index % 30),
                    100,
                )
            }
        }
    }
}
