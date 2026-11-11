package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.test.runComposeUiTest
import io.github.mrsimkin.dndcustomaid.dmui.DmSyncResult
import io.github.mrsimkin.dndcustomaid.dmui.DmWorkspaceScreen
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.DmCombatRepository
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The Escritorio DM on Windows: the same shared desks as the tablet, with the desktop's own platform. */
@OptIn(ExperimentalTestApi::class)
class DesktopDmWorkspaceTest {
    @Test
    fun desktopPlatformKeepsSettingsAndReadsTheBundledSrd() {
        val dir = Files.createTempDirectory("dnd-custom-aid-dm-store-")
        val file = dir.resolve("dm_workspace.properties").toFile()
        DesktopDmKeyValueStore(file).putString("nav_style", "TOP_TABS")
        assertEquals("TOP_TABS", DesktopDmKeyValueStore(file).getString("nav_style"), "settings survive a restart")

        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(file))
        assertTrue(platform.readSrd("quick_reference_es.json").contains("\"cards\""), "the SRD ships inside the Windows app")
    }

    @Test
    fun theEscritorioOpensTheDmScreenAndACombatOnWindows() = runComposeUiTest {
        val dir = Files.createTempDirectory("dnd-custom-aid-dm-desk-")
        val handle = DesktopDatabaseFactory(dir.resolve("desk.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val goblin = CreatureContentRepository(database).createCampaign(
            campaign.id,
            "Goblin",
            CreaturePayload(armorClass = 15, hitPoints = 7, hitDice = "2d6", abilityScores = CreatureAbilityScores(dexterity = 14)),
            100,
        )
        val combat = CombatEngine.addCreature(CombatEngine.new(campaign.id, "Puente", 200), goblin, 3)
        DmCombatRepository(database).save(combat, 200)
        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                DmWorkspaceScreen(
                    campaign = campaign,
                    database = database,
                    mediaBlobs = InMemoryMediaBlobStore(),
                    platform = platform,
                    accountId = null,
                    onSync = { DmSyncResult(synced = true) },
                    onExitToCampaigns = {},
                    onOpenAppSettings = {},
                )
            }
        }

        onAllNodesWithText("Pantalla del DM")[0].performClick()
        onNodeWithText("Referencia rápida").assertExists()
        onAllNodesWithText("⚔ Puente")[0].performClick()
        onNodeWithText("Paso 1 · Preparar").assertExists()
        onAllNodesWithText("Goblin ×3", useUnmergedTree = true)[0].performClick()
        onAllNodesWithText("Goblin ×3", useUnmergedTree = true)[0].assertExists()
        onAllNodesWithText("Editar completa")[0].assertExists()
        handle.close()
    }

    /** Renders the Windows desks at a laptop size into build/dm-desk-proofs for visual review. */
    @Test
    fun theWindowsDesksRenderForVisualReview() = runDesktopComposeUiTest(width = 1600, height = 960) {
        val out = File(System.getProperty("pcSheetProofDir") ?: "build/pc-sheet-proofs").parentFile.resolve("dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-dm-proof-")
        val handle = DesktopDatabaseFactory(dir.resolve("proof.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Terramore")
        val characters = CharacterRepository(database)
        listOf(Triple("Aria", 16, 14), Triple("Borin", 18, 10), Triple("Cael", 13, 16)).forEach { (name, ac, wis) ->
            val pc = characters.createCharacter(campaign.id, name)
            characters.saveCharacter(pc.copy(armorClass = ac, maxHp = ac * 2, currentHp = ac * 2, wisdom = wis, speed = 30))
        }
        val goblin = CreatureContentRepository(database).createCampaign(
            campaign.id,
            "Goblin jefe",
            CreaturePayload(
                armorClass = 15, hitPoints = 22, hitDice = "5d6+5", speed = "9 m", challengeRating = "1",
                abilityScores = CreatureAbilityScores(dexterity = 14),
                actions = "Cimitarra. +4 al ataque, 1d6+2 cortante.", tactics = "Ataca desde la cobertura.",
            ),
            100,
        )
        val combat = CombatEngine.addPcs(CombatEngine.addCreature(CombatEngine.new(campaign.id, "Puente", 200), goblin, 3), characters.listCharacters(campaign.id))
        DmCombatRepository(database).save(combat, 200)
        val platform = DesktopDmPlatform(DesktopDmKeyValueStore(dir.resolve("dm.properties").toFile()))

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                DmWorkspaceScreen(
                    campaign = campaign,
                    database = database,
                    mediaBlobs = InMemoryMediaBlobStore(),
                    platform = platform,
                    accountId = null,
                    onSync = { DmSyncResult(synced = true) },
                    onExitToCampaigns = {},
                    onOpenAppSettings = {},
                )
            }
        }

        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }
        shot("w1_escritorio")
        onAllNodesWithText("Pantalla del DM")[0].performClick()
        onAllNodesWithText("Borin", useUnmergedTree = true)[0].performClick()
        shot("w2_pantalla_dm")
        onAllNodesWithText("⚔ Puente")[0].performClick()
        onAllNodesWithText("Goblin jefe ×3", useUnmergedTree = true)[0].performClick()
        shot("w3_preparar")
        onAllNodesWithText("Empezar combate ▸")[0].performClick()
        shot("w4_iniciativa")
        handle.close()
    }
}
