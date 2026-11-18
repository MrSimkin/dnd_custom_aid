package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasText
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
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.content.InMemoryMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.DmCombatRepository
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Owner observation E1 on 0.7.0-preqa.4: «La seleccion de encuentros preparados no puede ser una
 * ventana poptup, debe haber una ventana intermedia selector, para poder improvisar y modificar una,
 * o crear un encuentro nuevo». Drives the real desk: a new combat opens on «Elegir encuentro».
 */
@OptIn(ExperimentalTestApi::class)
class DesktopEncounterSelectorTest {
    @Test
    fun aNewCombatChoosesChangesUsesAndSavesEncounters() = runDesktopComposeUiTest(width = 1500, height = 950) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-encounter-selector-")
        val handle = DesktopDatabaseFactory(dir.resolve("selector.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val characters = CharacterRepository(database)
        characters.createCharacter(campaign.id, "Arruk")
        val obrero = CreatureContentRepository(database).createCampaign(campaign.id, "Obrero alterado", CreaturePayload(armorClass = 14, hitPoints = 31), 10)
        val patrulla = EncounterContentRepository(database).createCampaign(
            campaign.id, "Patrulla de turno",
            EncounterPayload(dmGuidance = "Empujan hacia el borde.", participants = listOf(EncounterParticipant(sourceContentId = obrero.item.identity.id, quantity = 2))),
            10,
        )
        // What «＋ Nueva mesa → Nuevo combate» creates: the PCs and nothing else.
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
        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }

        onAllNodesWithText("⚔ Combate 1")[0].performClick()
        shot("e1_elegir_encuentro")
        onAllNodesWithText("Elegir encuentro")[0].assertExists()
        // Choose the prepared encounter and change it before using it.
        onAllNodesWithText("Patrulla de turno")[0].performClick()
        onNodeWithText("Tal como está guardado en la campaña.").assertExists()
        onAllNodesWithText("＋")[0].performClick()
        onNodeWithText("×3").assertExists()
        shot("e2_cambiado")
        onAllNodesWithText("Usar en este combate ▸")[0].performClick()
        waitForIdle()
        onAllNodesWithText("Paso 1 · Preparar")[0].assertExists()
        val inCombat = DmCombatRepository(database).combat(combat.id)!!
        assertEquals(3, inCombat.combatants.count { it.sourceContentId == obrero.item.identity.id })
        assertEquals(patrulla.item.identity.id, inCombat.encounterId)
        assertEquals(2, EncounterContentRepository(database).encounter(patrulla.item.identity.id)!!.payload.participants.single().quantity,
            "using it in the combat leaves the saved encounter as it was")

        // From the preparation step the same screen opens again (no pop-up) to make a new encounter.
        onAllNodesWithText("Elegir o armar encuentro")[0].performClick()
        onAllNodesWithText("＋ Encuentro nuevo")[0].performClick()
        onAllNodesWithText("Nombre del encuentro")[0].performTextInput("Ratas del sótano")
        onAllNodesWithText("Nombre")[0].performTextInput("Rata gigante")
        onAllNodesWithText("CA")[0].performTextInput("12")
        onAllNodesWithText("PV")[0].performTextInput("7")
        // Tablet QA: the box started with «1», so typing 4 left ×14. Touching it must empty it.
        onAllNodesWithText("Cantidad")[0].performClick()
        onAllNodesWithText("Cantidad")[0].performTextInput("4")
        onAllNodesWithText("Agregar")[0].performClick()
        shot("e3_nuevo")
        // Each box keeps what was typed into it: name, CA, PV and quantity do not mix.
        onNodeWithText("Rata gigante (improvisada)").assertExists()
        onNodeWithText("CA 12 · PV 7").assertExists()
        onNodeWithText("×4").assertExists()
        onAllNodesWithText("Guardar encuentro")[0].performClick()
        waitForIdle()
        shot("e4_guardado")
        val saved = ReusableContentRepository(database).listCampaign(campaign.id, ReusableContentFamily.ENCOUNTER).map { it.displayName }
        assertTrue("Ratas del sótano" in saved, saved.toString())
        val rats = EncounterContentRepository(database).encounter(
            ReusableContentRepository(database).listCampaign(campaign.id, ReusableContentFamily.ENCOUNTER).first { it.displayName == "Ratas del sótano" }.identity.id,
        )!!
        val rat = rats.payload.participants.single()
        assertEquals(4, rat.quantity)
        val ratCreature = CreatureContentRepository(database).creature(rat.sourceContentId!!)!!
        assertEquals("Rata gigante", ratCreature.item.displayName)
        assertEquals(12, ratCreature.payload.armorClass)
        assertEquals(7, ratCreature.payload.hitPoints)
        handle.close()
    }
}
