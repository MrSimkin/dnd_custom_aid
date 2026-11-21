package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRuleContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRulePayload
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Owner, 2026-11-18 («K»): a search that does not lose the context — every result says what it is
 * and where it lives, the house rules stay out unless asked for, and opening one lands on it.
 */
@OptIn(ExperimentalTestApi::class)
class DesktopCampaignSearchTest {
    @Test
    fun theSearchSaysWhatAndWhereAndOpensTheResult() = runDesktopComposeUiTest(width = 1200, height = 850) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-search-")
        val handle = DesktopDatabaseFactory(dir.resolve("search.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val shop = PlaceContentRepository(database).createCampaign(campaign.id, "Punta y Compás", PlacePayload(summary = "Tienda"), 100)
        val barek = NpcContentRepository(database).createCampaign(
            campaign.id,
            "Barek Surn",
            NpcPayload(conceptRole = "Capataz del turno de noche"),
            100,
        )
        ContentLinksRepository(database).save(barek.item.identity.id, ContentLinks(placeIds = listOf(shop.item.identity.id)))
        HomebrewRuleContentRepository(database).createCampaign(
            campaign.id,
            "Turnos de guardia",
            HomebrewRulePayload(summary = "Un turno dura cuatro horas."),
            100,
        )

        val opened = mutableListOf<String>()
        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.DARK)) {
                DesktopCampaignSearchBar(database = database, campaign = campaign) { opened += it.name }
            }
        }
        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }

        onNodeWithText("Buscar en toda la campaña").performTextInput("turno")
        waitForIdle()
        shot("k1_busqueda")

        // The PNJ says what it is and where it lives; the house rule stays out.
        onNodeWithText("Barek Surn").assertExists()
        onNodeWithText("· en Punta y Compás").assertExists()
        onAllNodesWithText("PNJ")[0].assertExists()
        assertEquals(0, onAllNodesWithText("Turnos de guardia").fetchSemanticsNodes().size, "the rules stay out")

        // Asking for the rules brings them in.
        onNodeWithText("▸ Qué se busca").performClick()
        waitForIdle()
        onNodeWithText("Regla de la casa").performClick()
        waitForIdle()
        onNodeWithText("Turnos de guardia").assertExists()
        shot("k2_con_reglas")

        onNodeWithText("Barek Surn").performClick()
        waitForIdle()
        assertTrue(opened == listOf("Barek Surn"), opened.toString())
        handle.close()
    }
}
