package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ScenePayload
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Owner's J (2026-11-20): «imprimir varias fichas de una vez», in the format they chose on
 * 2026-11-18: the table card, one per sheet, with the stat block where it applies.
 */
class DesktopTableCardBundleTest {
    @Test
    fun aScenePrintsItselfAndEverythingThatRunsWithIt() {
        val dir = Files.createTempDirectory("dnd-custom-aid-bundle-")
        val handle = DesktopDatabaseFactory(dir.resolve("bundle.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")

        val scene = SceneContentRepository(database).createCampaign(
            campaign.id, "Turno de noche", ScenePayload(purpose = "El grupo entra al taller."), 10,
        )
        val taller = PlaceContentRepository(database)
            .createCampaign(campaign.id, "Taller de la Serpiente", PlacePayload(summary = "Humo y poleas."), 10)
        val barek = NpcContentRepository(database)
            .createCampaign(campaign.id, "Barek Surn", NpcPayload(conceptRole = "Capataz"), 10)
        val obrero = CreatureContentRepository(database).createCampaign(
            campaign.id, "Obrero alterado", CreaturePayload(armorClass = 14, hitPoints = 31, challengeRating = "1/2"), 10,
        )
        val patrulla = EncounterContentRepository(database).createCampaign(
            campaign.id,
            "Patrulla de turno",
            EncounterPayload(participants = listOf(EncounterParticipant(sourceContentId = obrero.item.identity.id, quantity = 2))),
            10,
        )
        val links = ContentLinksRepository(database)
        links.save(
            scene.item.identity.id,
            ContentLinks(placeIds = listOf(taller.item.identity.id), encounterIds = listOf(patrulla.item.identity.id)),
        )
        // Barek does not say he is in the scene; the scene is written on his own sheet.
        links.save(barek.item.identity.id, ContentLinks(sceneIds = listOf(scene.item.identity.id)))

        val cards = TableCardBundle(database).forScene(scene.item.identity.id)

        assertEquals(
            listOf("Turno de noche", "Taller de la Serpiente", "Barek Surn", "Obrero alterado"),
            cards.map { it.title },
            "the scene first, then where it happens, then who is there, then what fights",
        )
        assertTrue(cards.last().stat != null, "a creature carries its stat block")

        val file = dir.resolve("fichas.pdf").toFile()
        writeTableCardsPdf(cards, file)
        Loader.loadPDF(file).use { pdf ->
            assertEquals(4, pdf.numberOfPages, "one card per sheet")
            val first = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
            assertTrue(first.contains("Turno de noche"), first)
            assertTrue(!first.contains("Barek Surn"), "nothing of the next card leaks onto this sheet")
            val last = PDFTextStripper().apply { startPage = 4; endPage = 4 }.getText(pdf)
            assertTrue(last.contains("BLOQUE DE COMBATE"), last)
            assertTrue(last.contains("Puntos de golpe"), last)
        }
        handle.close()
    }

    @Test
    fun aPlaceTakesTheOnesWhoAreThereAndNotItsOwnCardTwice() {
        val dir = Files.createTempDirectory("dnd-custom-aid-bundle-place-")
        val handle = DesktopDatabaseFactory(dir.resolve("bundle.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val taller = PlaceContentRepository(database)
            .createCampaign(campaign.id, "Taller de la Serpiente", PlacePayload(summary = "Humo y poleas."), 10)
        val barek = NpcContentRepository(database)
            .createCampaign(campaign.id, "Barek Surn", NpcPayload(conceptRole = "Capataz"), 10)
        ContentLinksRepository(database)
            .save(barek.item.identity.id, ContentLinks(placeIds = listOf(taller.item.identity.id)))

        val cards = TableCardBundle(database).forPlace(taller.item.identity.id)

        assertEquals(listOf("Taller de la Serpiente", "Barek Surn"), cards.map { it.title })
        handle.close()
    }
}
