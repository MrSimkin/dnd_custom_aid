package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.ui.test.ExperimentalTestApi
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Owner, 2026-11-18 («D»): the campaign is saved to a file from the real «Campañas» screen and
 * comes back on another computer with its content and its links.
 */
@OptIn(ExperimentalTestApi::class)
class DesktopCampaignBackupTest {
    @Test
    fun theCampaignIsSavedToAFileAndRecoveredOnAnotherComputer() = runDesktopComposeUiTest(width = 1200, height = 800) {
        val dir = Files.createTempDirectory("dnd-custom-aid-backup-")
        val target = dir.resolve("copia.json").toFile()
        val handle = DesktopDatabaseFactory(dir.resolve("source.db").toFile()).create()
        val database = handle.database
        val campaign = CampaignRepository(database).createCampaign("Serpiente Vieja")
        val npcs = NpcContentRepository(database)
        val barek = npcs.createCampaign(campaign.id, "Barek Surn", NpcPayload(conceptRole = "Capataz"), 100)
        val shop = PlaceContentRepository(database).createCampaign(campaign.id, "Punta y Compás", PlacePayload(summary = "Tienda"), 100)
        ContentLinksRepository(database).save(shop.item.identity.id, ContentLinks(npcIds = listOf(barek.item.identity.id)))

        val messages = mutableListOf<String>()
        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.DARK)) {
                DesktopCampaignBackupCard(
                    database = database,
                    campaign = campaign,
                    onMessage = { messages += it },
                    chooseFile = { _, _ -> target },
                    nowEpochSeconds = { 500 },
                )
            }
        }

        onNodeWithText("Guardar copia de seguridad…").performClick()
        waitForIdle()
        assertTrue(target.isFile, "the file was written")
        assertTrue(messages.last().contains("1 PNJ"), messages.toString())
        handle.close()

        // Another computer: an empty App, the open campaign, and the copy brings everything back.
        val otherDir = Files.createTempDirectory("dnd-custom-aid-backup-other-")
        val other = DesktopDatabaseFactory(otherDir.resolve("other.db").toFile()).create()
        val freshCampaign = CampaignRepository(other.database).createCampaign("Serpiente Vieja")
        val report = io.github.mrsimkin.dndcustomaid.shared.content.CampaignArchiveService(other.database).restoreInto(
            io.github.mrsimkin.dndcustomaid.shared.content.parseCampaignArchive(target.readText()),
            freshCampaign.id,
            900,
        )

        assertEquals(2, report.restored)
        val content = ReusableContentRepository(other.database)
        val npc = content.listCampaign(freshCampaign.id, ReusableContentFamily.NPC).single()
        val place = content.listCampaign(freshCampaign.id, ReusableContentFamily.PLACE).single()
        assertEquals("Barek Surn", npc.displayName)
        assertEquals(listOf(npc.identity.id), ContentLinksRepository(other.database).links(place.identity.id).npcIds)
        other.close()
        File(target.toURI()).delete()
    }
}
