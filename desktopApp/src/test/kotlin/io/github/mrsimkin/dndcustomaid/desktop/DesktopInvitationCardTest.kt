package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignInvitation
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

/**
 * Owner, 2026-11-18 («A»): the DM shows a code to dictate, a link to send and a QR to scan, all
 * three with the same six letters and the same expiry.
 */
@OptIn(ExperimentalTestApi::class)
class DesktopInvitationCardTest {
    private val campaign = Campaign(
        id = Uuid.parse("20000000-0000-4000-8000-000000000002"),
        name = "Serpiente Vieja",
    )

    @Test
    fun theCardCreatesACodeAndShowsTheLinkAndTheQr() = runDesktopComposeUiTest(width = 1100, height = 760) {
        val out = File("build/dm-desk-proofs").apply { mkdirs() }
        var created = 0
        val copied = mutableListOf<String>()

        setContent {
            DesktopAppTheme(DesktopPreferences(themeChoice = DesktopThemeChoice.DARK)) {
                DesktopInvitationSection(
                    campaign = campaign,
                    load = { null },
                    create = { days ->
                        created += 1
                        HostedCampaignInvitation(
                            code = "MARFIL",
                            campaignId = campaign.id,
                            campaignName = campaign.name,
                            expiresAtEpochSeconds = 1_800_000_000,
                        ).also { assertEquals(7, days) }
                    },
                    revoke = { true },
                    copy = { copied += it },
                    linkBaseUrl = "https://dnd-custom-aid-api.mrsimkin-dev.workers.dev",
                )
            }
        }
        fun shot(name: String) {
            waitForIdle()
            val roots = onAllNodes(isRoot()).fetchSemanticsNodes().size
            ImageIO.write(onAllNodes(isRoot())[roots - 1].captureToImage().toAwtImage(), "png", out.resolve("$name.png"))
        }

        waitForIdle()
        onNodeWithText("Ahora mismo no hay ninguna invitación viva.").assertExists()
        onNodeWithText("Crear invitación (7 días)").performClick()
        waitForIdle()
        shot("i1_invitacion")

        assertEquals(1, created)
        onNodeWithText("M A R F I L").assertExists()
        onAllNodesWithText("https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/unirme/MARFIL")[0].assertExists()
        onNodeWithText("Copiar el código").performClick()
        onNodeWithText("Copiar el enlace").performClick()
        waitForIdle()
        assertEquals(listOf("MARFIL", "https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/unirme/MARFIL"), copied)
        assertTrue(out.resolve("i1_invitacion.png").isFile)
    }
}
