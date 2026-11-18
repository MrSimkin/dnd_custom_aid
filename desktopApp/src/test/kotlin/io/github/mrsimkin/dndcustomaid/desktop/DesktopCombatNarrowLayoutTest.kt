package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.mrsimkin.dndcustomaid.dmui.DmSyncResult
import io.github.mrsimkin.dndcustomaid.dmui.DmWorkspaceScreen
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.DmCombatRepository
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tablet QA of E1 (0.7.0-preqa.5): on the tablet's narrow column the creature's name was drawn one
 * letter per line, and the step's title broke the same way beside the message. The buttons of the
 * row must leave the name room to read.
 */
@OptIn(ExperimentalTestApi::class)
class DesktopCombatNarrowLayoutTest {
    @Test
    fun aGroupNameStaysReadableInANarrowColumn() = runDesktopComposeUiTest(width = 760, height = 1000) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-narrow-combat-")
        val handle = DesktopDatabaseFactory(dir.resolve("narrow.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val characters = CharacterRepository(database)
        characters.createCharacter(campaign.id, "Arruk")
        val combat = CombatEngine.addCustom(
            CombatEngine.addPcs(CombatEngine.new(campaign.id, "Combate 1", 20), characters.listCharacters(campaign.id)),
            "Rata de muelle", 9, 12, 3,
        )
        DmCombatRepository(database).save(combat, 20)
        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))
        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.DARK)) {
                DmWorkspaceScreen(
                    campaign = campaign, database = database, mediaBlobs = InMemoryMediaBlobStore(), platform = platform,
                    accountId = null, onSync = { DmSyncResult(synced = true) }, onExitToCampaigns = {}, onOpenAppSettings = {},
                )
            }
        }
        onAllNodesWithText("⚔ Combate 1")[0].performClick()
        waitForIdle()
        val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
        ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("n1_columna_angosta.png"))

        val name = onNodeWithText("Rata de muelle ×3").fetchSemanticsNode().boundsInRoot
        assertTrue(name.width > 110f, "the name had only ${name.width}px of width")
        assertTrue(name.height < 90f, "the name piled up over ${name.height}px of height")
        val title = onNodeWithText("Paso 1 · Preparar").fetchSemanticsNode().boundsInRoot
        assertTrue(title.height < 60f, "the title piled up over ${title.height}px of height")
        handle.close()
    }
}
