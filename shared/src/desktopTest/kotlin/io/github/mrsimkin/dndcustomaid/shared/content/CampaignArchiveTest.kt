package io.github.mrsimkin.dndcustomaid.shared.content

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/**
 * Owner, 2026-11-18 («D»): a campaign can be saved to a file and brought back, on this computer or
 * on another one, with its content, its links and its PCs.
 */
class CampaignArchiveTest {
    private fun database(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try { block(AppDatabase(driver)) } finally { driver.close() }
    }

    private fun fill(database: AppDatabase): Uuid {
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val npcs = NpcContentRepository(database)
        val places = PlaceContentRepository(database)
        val barek = npcs.createCampaign(campaign.id, "Barek Surn", NpcPayload(conceptRole = "Capataz", secrets = "Falsea el conteo."), 100)
        val shop = places.createCampaign(campaign.id, "Punta y Compás", PlacePayload(summary = "Tienda de instrumentos"), 100)
        ContentLinksRepository(database).save(shop.item.identity.id, ContentLinks(npcIds = listOf(barek.item.identity.id)))
        CharacterRepository(database).createCharacter(campaign.id, "Arruk")
        return campaign.id
    }

    @Test
    fun aCopyHoldsWhatTheCampaignHas() = database { database ->
        val campaignId = fill(database)

        val archive = CampaignArchiveService(database).save(campaignId, nowEpochSeconds = 500)

        assertEquals("Serpiente Vieja", archive.campaignName)
        assertEquals(2, archive.content.size)
        assertEquals(1, archive.characters.size)
        assertTrue(archive.spanishSummary().contains("1 PNJ"), archive.spanishSummary())
        val text = writeCampaignArchive(archive)
        assertEquals(archive, parseCampaignArchive(text), "the file reads back the same")
    }

    @Test
    fun anotherComputerGetsTheCampaignWithItsLinks() = database { source ->
        val campaignId = fill(source)
        val archive = CampaignArchiveService(source).save(campaignId, nowEpochSeconds = 500)
        val text = writeCampaignArchive(archive)

        database { target ->
            val fresh = CampaignRepository(target).createCampaign("Serpiente Vieja (copia)")
            val report = CampaignArchiveService(target).restoreInto(parseCampaignArchive(text), fresh.id, 900)

            assertEquals(2, report.restored)
            assertEquals(1, report.characters)
            assertTrue(report.problems.isEmpty(), report.problems.toString())

            val content = ReusableContentRepository(target)
            val npc = content.listCampaign(fresh.id, ReusableContentFamily.NPC).single()
            val place = content.listCampaign(fresh.id, ReusableContentFamily.PLACE).single()
            assertEquals("Barek Surn", npc.displayName)
            assertEquals("Falsea el conteo.", NpcContentRepository(target).npc(npc.identity.id)?.payload?.secrets)
            assertEquals(
                listOf(npc.identity.id),
                ContentLinksRepository(target).links(place.identity.id).npcIds,
                "the place points at the PNJ of this computer",
            )
            assertEquals("Arruk", CharacterRepository(target).listCharacters(fresh.id).single().name)
        }
    }

    @Test
    fun recoveringTwiceNeverOverwritesWhatIsAlreadyThere() = database { database ->
        val campaignId = fill(database)
        val archive = CampaignArchiveService(database).save(campaignId, nowEpochSeconds = 500)
        val service = CampaignArchiveService(database)

        service.restoreInto(archive, campaignId, 900)
        val afterFirst = ReusableContentRepository(database).listCampaign(campaignId, ReusableContentFamily.NPC)
        service.restoreInto(archive, campaignId, 1_000)
        val afterSecond = ReusableContentRepository(database).listCampaign(campaignId, ReusableContentFamily.NPC)

        assertEquals(2, afterFirst.size, "the copy is added, the original stays")
        assertEquals(3, afterSecond.size)
        assertNotEquals(afterFirst.map { it.identity.id }.toSet(), afterSecond.map { it.identity.id }.toSet())
    }

    @Test
    fun aFileThatIsNotACopyIsRefusedWithPlainWords() = database { _ ->
        val problem = runCatching { parseCampaignArchive("""{"format":"otra-cosa","version":1,"campaignName":"x","savedAtEpochSeconds":1}""") }
        assertTrue(problem.isFailure)
        assertTrue(problem.exceptionOrNull()?.message?.contains("no es una copia") == true, problem.exceptionOrNull()?.message.orEmpty())
    }
}
