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
import io.github.mrsimkin.dndcustomaid.dmui.DmSyncResult
import io.github.mrsimkin.dndcustomaid.dmui.DmWorkspaceScreen
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CampaignSearchDetail
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRuleContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRulePayload
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Owner's I (2026-11-20): «buscar también en la tablet». The desk is in `dmUi`, so this drives the
 * very screen the tablet shows. It also checks the two things the owner asked for on 2026-11-18:
 * each result says where it lives, and the house rules stay out until the DM asks for them.
 */
@OptIn(ExperimentalTestApi::class)
class DmTabletSearchTest {
    @Test
    fun theTabletSearchesTheWholeCampaignAndReadsWhatItFinds() = runDesktopComposeUiTest(width = 1500, height = 950) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-tablet-search-")
        val handle = DesktopDatabaseFactory(dir.resolve("search.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        CharacterRepository(database).createCharacter(campaign.id, "Arruk")

        val taller = PlaceContentRepository(database)
            .createCampaign(campaign.id, "Taller de la Serpiente", PlacePayload(summary = "Donde se arma el engaño."), 10)
        val barek = NpcContentRepository(database).createCampaign(
            campaign.id,
            "Barek Surn",
            NpcPayload(conceptRole = "Capataz de la serpiente", secrets = "Falsea el conteo del turno."),
            10,
        )
        ContentLinksRepository(database).save(barek.item.identity.id, ContentLinks(placeIds = listOf(taller.item.identity.id)))
        CreatureContentRepository(database).createCampaign(
            campaign.id,
            "Obrero alterado de la serpiente",
            CreaturePayload(armorClass = 14, hitPoints = 31, actions = "Golpe: 1d8 contundente."),
            10,
        )
        HomebrewRuleContentRepository(database).createCampaign(
            campaign.id,
            "Serpiente: descanso corto",
            HomebrewRulePayload(summary = "El descanso corto dura 10 minutos.", body = "Se aplica solo bajo tierra."),
            10,
        )

        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))
        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.DARK)) {
                DmWorkspaceScreen(
                    campaign = campaign, database = database, mediaBlobs = InMemoryMediaBlobStore(), platform = platform,
                    accountId = null, onSync = { DmSyncResult(synced = true) }, onExitToCampaigns = {}, onOpenAppSettings = {},
                )
            }
        }
        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }

        // The desk is reachable from the workspace itself, not only from the Mesa de Escena.
        onAllNodesWithText("🔎 Buscar")[0].performClick()
        waitForIdle()
        onAllNodesWithText("Buscar en toda la campaña")[0].performTextInput("serpiente")
        shot("i1_buscar_tablet")

        // Each family answers, and each result says where it lives.
        onAllNodesWithText("Barek Surn")[0].assertExists()
        onNodeWithText("· en Taller de la Serpiente").assertExists()
        onAllNodesWithText("Obrero alterado de la serpiente")[0].assertExists()
        onAllNodesWithText("Taller de la Serpiente")[0].assertExists()
        // The house rules are out until the DM switches them on.
        assertTrue(onAllNodesWithText("Serpiente: descanso corto").fetchSemanticsNodes().isEmpty(), "las reglas de la casa quedan fuera")
        onAllNodesWithText("▸ Qué se busca")[0].performClick()
        onAllNodesWithText("Regla de la casa")[0].performClick()
        waitForIdle()
        onAllNodesWithText("Serpiente: descanso corto")[0].assertExists()
        shot("i2_con_reglas")

        // A result is read right there: the tablet has no «Preparar» to jump to.
        onAllNodesWithText("Obrero alterado de la serpiente")[0].performClick()
        waitForIdle()
        onNodeWithText("Golpe: 1d8 contundente.").assertExists()
        // Twice: the one line of the result, and the reading sheet beside it.
        onAllNodesWithText("CA 14 · PV 31")[1].assertExists()
        shot("i3_ficha_criatura")

        // What the Mesa de Escena can hold opens there, on that sheet.
        onAllNodesWithText("Barek Surn")[0].performClick()
        waitForIdle()
        onAllNodesWithText("Abrir en la Mesa de Escena")[0].performClick()
        waitForIdle()
        onAllNodesWithText("Mesa de Escena")[0].assertExists()
        onAllNodesWithText("Barek Surn")[0].assertExists()
        shot("i4_en_la_mesa_de_escena")

        // The reading sheet itself, without a screen: nothing empty, and in the DM's order.
        val lines = CampaignSearchDetail(database).lines(barek.item.identity.id, ReusableContentFamily.NPC)
        assertEquals(listOf("Papel", "Secretos (solo DM)"), lines.map { it.label })
        assertEquals("Capataz de la serpiente", lines.first().text)
        handle.close()
    }
}
