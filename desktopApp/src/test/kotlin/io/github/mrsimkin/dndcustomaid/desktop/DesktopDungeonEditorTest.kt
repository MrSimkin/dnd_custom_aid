package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.DungeonContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Redesign R3 (owner observation P5, approved mockup D5): create a dungeon, add zones, drag a passage. */
@OptIn(ExperimentalTestApi::class)
class DesktopDungeonEditorTest {
    @Test
    fun zonesAreAddedOnThePlanAndDraggingFromTheDotMakesAPassage() = runDesktopComposeUiTest(width = 1500, height = 900) {
        val out = File(System.getProperty("pcSheetProofDir") ?: "build/pc-sheet-proofs").parentFile.resolve("dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-dungeon-editor-")
        val handle = DesktopDatabaseFactory(dir.resolve("editor.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val dungeons = DungeonContentRepository(database)
        val site = dungeons.create(campaign.id, "Sitio 021", io.github.mrsimkin.dndcustomaid.shared.content.DungeonPayload(), 100)
        val controller = DesktopDungeonEditorController(database, InMemoryMediaBlobStore())

        setContent {
            MaterialTheme(colors = darkColors()) {
                androidx.compose.material.Surface {
                    DesktopDungeonEditorScreen(controller, campaign, onOpenZone = {}, onQaEvent = {})
                }
            }
        }

        onNodeWithTag("add-zone").performClick()
        onNodeWithTag("add-zone").performClick()
        waitForIdle()
        val zones = dungeons.zonesOf(campaign.id, dungeons.dungeon(site.item.identity.id)!!)
        assertEquals(listOf("Zona 1", "Zona 2"), zones.map { it.zone.displayName })

        // Drag from Zona 1's dot to the middle of Zona 2.
        val from = onNodeWithTag("link-Zona 1", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val to = onNodeWithTag("zone-Zona 2", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        onNodeWithTag("link-Zona 1", useUnmergedTree = true).performMouseInput {
            moveTo(center)
            press()
            val total = Offset(to.center.x - from.center.x, to.center.y - from.center.y)
            repeat(10) { moveBy(total / 10f, delayMillis = 16) }
            release()
        }
        waitForIdle()
        val links = ContentLinksRepository(database)
        val stored = zones.flatMap { links.links(it.zone.identity.id).passages }
        assertEquals(1, stored.size, "one passage between the two zones")

        onAllNodesWithText("Zona 1")[0].performClick()
        waitForIdle()
        assertTrue(onAllNodesWithText("→ Zona 2").fetchSemanticsNodes().isNotEmpty(), "the zone panel lists the passage")
        val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
        ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("r3-dungeon-editor.png"))
        handle.close()
    }
}
