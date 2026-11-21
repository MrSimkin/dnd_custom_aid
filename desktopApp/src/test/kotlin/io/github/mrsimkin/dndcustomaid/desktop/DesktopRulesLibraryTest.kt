package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.mrsimkin.dndcustomaid.dmui.LocalDmPlatform
import io.github.mrsimkin.dndcustomaid.dmui.RulesLibraryScreen
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Redesign R4 (owner observation G4): read the SRD by family and keep homebrew apart. */
@OptIn(ExperimentalTestApi::class)
class DesktopRulesLibraryTest {
    @Test
    fun theSrdIsReadByFamilyAndHomebrewHasItsPlaceEvenWhenEmpty() = runDesktopComposeUiTest(width = 1500, height = 950) {
        val out = File(System.getProperty("pcSheetProofDir") ?: "build/pc-sheet-proofs").parentFile.resolve("dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-rules-")
        val handle = DesktopDatabaseFactory(dir.resolve("rules.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                CompositionLocalProvider(LocalDmPlatform provides platform) {
                    Surface {
                        RulesLibraryScreen(platform = platform, database = database, ownerAccountId = null, campaignId = campaign.id)
                    }
                }
            }
        }
        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }

        waitUntil(timeoutMillis = 20_000) { onAllNodesWithText("Clases (12)").fetchSemanticsNodes().isNotEmpty() }
        onAllNodesWithText("Bárbaro")[0].performClick()
        waitForIdle()
        assertTrue(onAllNodesWithText("Dado de golpe: d12").fetchSemanticsNodes().isNotEmpty(), "the class shows its hit die")
        shot("r4-reglas-srd")

        onAllNodesWithText("Homebrew")[0].performClick()
        waitForIdle()
        assertTrue(onAllNodesWithText("Conjuros · 0").fetchSemanticsNodes().isNotEmpty())
        shot("r4-reglas-homebrew")
        onAllNodesWithText("Conjuros · 0")[0].performClick()
        onAllNodesWithText("＋ Nuevo")[0].performClick()
        waitForIdle()
        onAllNodes(hasSetTextAction())[0].performTextInput("Rayo de ceniza")
        onAllNodesWithText("Guardar")[0].performClick()
        waitForIdle()
        val saved = RulesEntryContentRepository(database).listCampaign(campaign.id, RulesEntryKind.SPELL)
        assertEquals(listOf("Rayo de ceniza"), saved.map { it.item.displayName })
        handle.close()
    }
}
