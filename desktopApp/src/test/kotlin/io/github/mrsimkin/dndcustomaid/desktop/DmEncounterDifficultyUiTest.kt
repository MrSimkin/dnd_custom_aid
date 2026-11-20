package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.mrsimkin.dndcustomaid.dmui.DmSyncResult
import io.github.mrsimkin.dndcustomaid.dmui.DmWorkspaceScreen
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.DmCombatRepository
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.uuid.Uuid

/**
 * Owner's G (2026-11-20): «dificultad del encuentro según el grupo». The verdict is on the screen
 * where the DM chooses an encounter, which is the same screen on the tablet and on the PC.
 */
@OptIn(ExperimentalTestApi::class)
class DmEncounterDifficultyUiTest {
    @Test
    fun choosingAnEncounterSaysHowHardItLooksForThisParty() = runDesktopComposeUiTest(width = 1500, height = 950) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-difficulty-")
        val handle = DesktopDatabaseFactory(dir.resolve("difficulty.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val characters = CharacterRepository(database)
        // Four characters of level 3: they can take 300 / 600 / 900 / 1600 experience.
        listOf("Arruk", "Nera", "Tolm", "Yx").forEachIndexed { index, name ->
            val pc = characters.createCharacter(campaign.id, name)
            characters.saveCharacter(
                pc.copy(
                    classes = listOf(
                        CharacterClassLevel(
                            id = Uuid.random(),
                            name = "Guerrero",
                            level = 3,
                            hitDieSides = 10,
                            hitDiceRemaining = 3,
                            sortOrder = index,
                        ),
                    ),
                ),
            )
        }
        val ogro = CreatureContentRepository(database)
            .createCampaign(campaign.id, "Ogro del taller", CreaturePayload(armorClass = 11, hitPoints = 59, challengeRating = "2"), 10)
        EncounterContentRepository(database).createCampaign(
            campaign.id,
            "Cuatro ogros",
            EncounterPayload(participants = listOf(EncounterParticipant(sourceContentId = ogro.item.identity.id, quantity = 4))),
            10,
        )
        EncounterContentRepository(database).createCampaign(
            campaign.id,
            "Un ogro solo",
            EncounterPayload(participants = listOf(EncounterParticipant(sourceContentId = ogro.item.identity.id, quantity = 1))),
            10,
        )
        val combat = CombatEngine.addPcs(CombatEngine.new(campaign.id, "Combate 1", 20), characters.listCharacters(campaign.id))
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

        // Four CR 2 are 1800 experience, and four at once count double: 3600, far over the 1600
        // that four level 3 can take.
        onAllNodesWithText("Mortal para 4 PJ · 3600 PX ajustados")[0].assertExists()
        // The same ogre on its own is 450: an easy afternoon.
        onAllNodesWithText("Fácil para 4 PJ · 450 PX ajustados")[0].assertExists()
        val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
        ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("g1_dificultad.png"))

        // And it follows the draft: taking ogres out of the encounter calms it down.
        onAllNodesWithText("Cuatro ogros")[0].performClick()
        waitForIdle()
        onAllNodesWithText("−")[0].performClick()
        onAllNodesWithText("−")[0].performClick()
        waitForIdle()
        onAllNodesWithText("Difícil para 4 PJ · 1350 PX ajustados")[0].assertExists()
        val roots2 = onAllNodes(isRoot()).fetchSemanticsNodes().size
        ImageIO.write(onAllNodes(isRoot())[roots2 - 1].captureToImage().toAwtImage(), "png", out.resolve("g2_dificultad_cambiada.png"))
        handle.close()
    }
}
