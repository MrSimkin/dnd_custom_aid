package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Owner, 2026-11-18 («C: borrar con papelera»): until now nothing prepared could be deleted from
 * the App. This drives the real screens: delete a PNJ, find it in «Papelera», bring it back with
 * its content and with the place that mentioned it still pointing at it.
 */
@OptIn(ExperimentalTestApi::class)
class DesktopDeleteAndTrashTest {
    @Test
    fun anNpcIsDeletedAndBroughtBackFromTheTrash() = runDesktopComposeUiTest(width = 1400, height = 900) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-trash-")
        val handle = DesktopDatabaseFactory(dir.resolve("trash.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val npcs = NpcContentRepository(database)
        val barek = npcs.createCampaign(campaign.id, "Barek Surn", NpcPayload(conceptRole = "Capataz", secrets = "Falsea el conteo."), 100)
        val shop = PlaceContentRepository(database).createCampaign(campaign.id, "Punta y Compás", PlacePayload(summary = "Tienda"), 100)
        ContentLinksRepository(database).save(shop.item.identity.id, ContentLinks(npcIds = listOf(barek.item.identity.id)))
        val content = ReusableContentRepository(database)

        val showTrash = androidx.compose.runtime.mutableStateOf(false)
        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.DARK)) {
                if (showTrash.value) {
                    DesktopTrashScreen(database = database, activeCampaign = campaign, onQaEvent = {}, nowEpochSeconds = { 300 })
                } else {
                    NpcManagerScreen(
                        controller = DesktopNpcManagerController(database) { 200 },
                        activeCampaign = campaign,
                        onQaEvent = {},
                    )
                }
            }
        }
        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }

        onAllNodesWithText("Barek Surn")[0].performClick()
        waitForIdle()
        onNodeWithText("Borrar").performClick()
        waitForIdle()
        shot("b1_confirmar_borrado")
        onNodeWithText("¿Borrar este PNJ?").assertExists()
        onNodeWithText("Sí, borrar").performClick()
        waitForIdle()
        shot("b2_borrado")

        assertTrue(content.listCampaign(campaign.id, ReusableContentFamily.NPC).isEmpty(), "it left the list")
        onNodeWithText("Aún no hay PNJ en esta campaña.").assertExists()

        showTrash.value = true
        waitForIdle()
        onNodeWithText("Barek Surn").assertExists()
        shot("b3_papelera")
        onNodeWithText("Recuperar").performClick()
        waitForIdle()
        shot("b4_recuperado")

        val restored = content.listCampaign(campaign.id, ReusableContentFamily.NPC).single()
        assertEquals("Barek Surn", restored.displayName)
        assertEquals("Falsea el conteo.", NpcContentRepository(database).npc(restored.identity.id)?.payload?.secrets)
        assertEquals(
            listOf(restored.identity.id),
            ContentLinksRepository(database).links(shop.item.identity.id).npcIds,
            "the place keeps pointing at it",
        )
        handle.close()
    }
}
