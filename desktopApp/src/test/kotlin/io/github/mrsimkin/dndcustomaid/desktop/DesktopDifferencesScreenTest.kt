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
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentConflictResolution
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSnapshotCodec
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSyncConflictReason
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Owner's B (2026-11-20): the two versions side by side, and what the DM presses decides. The
 * engine's own rules are covered in shared; this drives the screen the DM uses.
 */
@OptIn(ExperimentalTestApi::class)
class DesktopDifferencesScreenTest {
    @Test
    fun theTwoVersionsAreShownAndTheChoiceIsApplied() = runDesktopComposeUiTest(width = 1400, height = 900) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        val dir = Files.createTempDirectory("dnd-custom-aid-differences-")
        val handle = DesktopDatabaseFactory(dir.resolve("differences.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val npcs = NpcContentRepository(database)
        val mine = npcs.createCampaign(campaign.id, "Barek Surn", NpcPayload(conceptRole = "Capataz de la Caseta"), 100)
        val id = mine.item.identity.id

        // What the other device had, as a sync would have left it waiting.
        val theirs = DmContentSnapshotCodec(database).export(id)!!
        val theirDocument = theirs.copy(
            payload = kotlinx.serialization.json.JsonObject(
                theirs.payload + ("conceptRole" to kotlinx.serialization.json.JsonPrimitive("Capataz del turno de noche")),
            ),
        )
        database.dmContentConflictQueries.upsertDmContentConflict(
            content_id = id.toString(),
            reason = DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED.name,
            hosted_revision = 3,
            hosted_snapshot = kotlinx.serialization.json.Json.encodeToString(
                io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSnapshotDocument.serializer(),
                theirDocument,
            ),
            hosted_deleted = 0,
            seen_at_epoch_seconds = 200,
        )

        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.DARK)) {
                DesktopDifferencesScreen(database = database, onQaEvent = {}, nowEpochSeconds = { 900 })
            }
        }
        waitForIdle()
        val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
        ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("d1_diferencias.png"))

        onNodeWithText("En este equipo").assertExists()
        onNodeWithText("En el otro aparato").assertExists()
        onAllNodesWithText("Capataz de la Caseta")[0].assertExists()
        onAllNodesWithText("Capataz del turno de noche")[0].assertExists()

        onNodeWithText("Guardar las dos").performClick()
        waitForIdle()

        val all = ReusableContentRepository(database).listCampaign(campaign.id, ReusableContentFamily.NPC)
        assertEquals(2, all.size, "keeping both leaves the two")
        assertEquals("Capataz del turno de noche", npcs.npc(id)?.payload?.conceptRole)
        assertEquals("Barek Surn (mi versión)", all.single { it.identity.id != id }.displayName)
        assertEquals(0, DmContentConflictResolution(database).count())
        onNodeWithText("No hay nada que decidir.").assertExists()
        handle.close()
    }
}
