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
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Owner, 2026-11-18: the step-by-step new PC must exist on Windows too, not only on the tablet.
 * This drives the real Personajes screen: «＋ Nuevo personaje», pick from the SRD, create, and the
 * sheet comes out filled.
 */
@OptIn(ExperimentalTestApi::class)
class DesktopNewCharacterWizardTest {
    @Test
    fun windowsCreatesAPcStepByStepFromTheSrd() = runDesktopComposeUiTest(width = 1500, height = 950) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-new-pc-")
        val handle = DesktopDatabaseFactory(dir.resolve("pcs.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val controller = DesktopPcManagerController(database)
        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))

        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.DARK)) {
                DesktopPcManagerScreen(
                    controller = controller,
                    hostedController = DesktopHostedCampaignAdministrationController(database, accessTokens = DesktopHostedAuthController(vault = InMemoryCredentialVault())),
                    activeCampaign = campaign,
                    onQaEvent = {},
                    database = database,
                    platform = platform,
                    accountId = null,
                )
            }
        }
        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }

        onNodeWithText("＋ Nuevo personaje").performClick()
        waitForIdle()
        onNodeWithText("1. Nombre y campaña").assertExists()
        onAllNodesWithText("Nombre del personaje")[0].performTextInput("Arruk")

        // Class from the SRD: the picker is the same one the tablet uses.
        onNodeWithText("📖 Elegir clase").performClick()
        waitForIdle()
        onAllNodesWithText("Buscar")[0].performTextInput("Bárbaro")
        waitUntil(timeoutMillis = 10_000) { onAllNodesWithText("Bárbaro").fetchSemanticsNodes().size > 1 }
        // [0] is the search box, [1] the result of the SRD.
        onAllNodesWithText("Bárbaro")[1].performClick()
        onNodeWithText("Usar").performClick()
        waitForIdle()
        shot("p1_asistente_windows")
        onNodeWithText("Nivel total: 1").assertExists()

        onNodeWithText("Crear personaje").performClick()
        waitForIdle()
        shot("p2_personaje_creado")

        val created = CharacterRepository(database).listCharacters(campaign.id).single()
        assertEquals("Arruk", created.name)
        assertTrue(created.classes.any { it.name == "Bárbaro" }, created.classes.toString())
        assertTrue(created.maxHp > 0, "the sheet comes out with hit points: ${created.maxHp}")
        handle.close()
    }
}
