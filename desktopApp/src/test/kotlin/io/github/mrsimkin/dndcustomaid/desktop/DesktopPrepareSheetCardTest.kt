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
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Owner points 6 and 10 of the critical review: what you prepare opens as the card you read at the
 * table, «Editar» changes one block, and the complete preparation is there under «Ver todo».
 */
@OptIn(ExperimentalTestApi::class)
class DesktopPrepareSheetCardTest {
    @Test
    fun anNpcOpensAsItsTableCardAndOneBlockIsEdited() = runDesktopComposeUiTest(width = 1500, height = 950) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-prepare-card-")
        val handle = DesktopDatabaseFactory(dir.resolve("prepare.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val npcs = NpcContentRepository(database)
        npcs.createCampaign(
            campaign.id,
            "Barek Surn",
            NpcPayload(
                conceptRole = "Capataz del turno de noche",
                wantsFearsNeeds = "Que nadie mire la planilla de lote.",
                secrets = "Falsea el conteo desde hace dos meses.",
                history = "Trabajó doce años en la Caseta.",
            ),
            100,
        )
        val controller = DesktopNpcManagerController(database)

        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.DARK)) {
                NpcManagerScreen(controller = controller, activeCampaign = campaign, onQaEvent = {})
            }
        }
        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }

        onAllNodesWithText("Barek Surn")[0].performClick()
        waitForIdle()
        shot("f1_ficha_de_mesa")
        // The card reads; the long form is not in the way.
        onNodeWithText("Quién es").assertExists()
        onNodeWithText("Qué quiere en la mesa").assertExists()
        onAllNodesWithText("Capataz del turno de noche")[0].assertExists()
        onAllNodesWithText("Falsea el conteo desde hace dos meses.")[0].assertExists()
        assertEquals(0, onAllNodesWithText("PNJ rápido").fetchSemanticsNodes().size, "the complete preparation stays closed")

        // «Editar» turns one block into its fields and what is typed is kept.
        onAllNodesWithText("Editar")[0].performClick()
        waitForIdle()
        onAllNodesWithText("Concepto / rol")[0].performTextInput(" y de la Caseta")
        onAllNodesWithText("Listo")[0].performClick()
        waitForIdle()
        shot("f2_bloque_editado")
        onAllNodesWithText("y de la Caseta", substring = true)[0].assertExists()

        // «Ver todo» is the complete preparation, with every field as before.
        onNodeWithText("▸ Ver todo (preparación completa)").performClick()
        waitForIdle()
        onAllNodesWithText("PNJ rápido")[0].assertExists()
        shot("f3_preparacion_completa")
        handle.close()
    }
}
