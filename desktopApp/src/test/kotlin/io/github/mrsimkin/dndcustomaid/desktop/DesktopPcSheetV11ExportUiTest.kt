package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignAdministrationClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcAuthorityAdministrationClient
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper

/**
 * C439 / IN01, IN06: drives the real PC Manager screen and presses its real buttons. Only the two
 * operating-system dialogs are replaced (file chooser, clipboard), so the test runs with no display.
 * Every hosted endpoint points at a closed local port: an export that needed the cloud would fail.
 */
@OptIn(ExperimentalTestApi::class)
class DesktopPcSheetV11ExportUiTest {
    private fun text(bytes: ByteArray): String =
        Loader.loadPDF(bytes).use { PDFTextStripper().getText(it) }

    @Test
    fun c439RealButtonsSaveAndShareV11AAndBOfflineAndLeaveTheOldFamiliesAlone() {
        val dir = Files.createTempDirectory("dnd-v11-ui-").toFile()
        val handle = DesktopDatabaseFactory(File(dir, "ui.db")).create()
        try {
            val database = handle.database
            val campaign = CampaignRepository(database).createCampaign("Campaña UI")
            val pc = CharacterRepository(database).createCharacter(campaign.id, "Prueba UI")
            val controller = DesktopPcManagerController(database)
            val closedPort = "http://127.0.0.1:9"
            val auth = DesktopHostedAuthController(descopeBaseUrl = closedPort)
            val hosted = DesktopHostedCampaignAdministrationController(
                database, auth,
                HostedApiClient(baseUrl = closedPort, accessTokens = auth),
                HostedCampaignAdministrationClient(baseUrl = closedPort, accessTokens = auth),
                HostedPcAuthorityAdministrationClient(baseUrl = closedPort, accessTokens = auth),
            )
            val out = File(dir, "out").apply { mkdirs() }
            val events = mutableListOf<String>()
            var shared: File? = null

            runDesktopComposeUiTest(width = 1600, height = 1200) {
                setContent {
                    MaterialTheme {
                        DesktopPcManagerScreen(
                            controller = controller,
                            hostedController = hosted,
                            activeCampaign = campaign,
                            onQaEvent = { events += it },
                            chooseSaveTarget = { name -> File(out, name) },
                            copyFileToClipboard = { shared = it },
                        )
                    }
                }
                onNodeWithText("Prueba UI").performClick()
                waitForIdle()

                fun press(label: String, vararg alsoRequired: androidx.compose.ui.test.SemanticsMatcher) {
                    onAllNodes(hasScrollToNodeAction())[1].performScrollToNode(hasText(label))
                    var matcher = hasText(label) and hasClickAction()
                    alsoRequired.forEach { matcher = matcher and it }
                    onNode(matcher).performClick()
                }
                fun awaitEvent(fragment: String, count: Int) = waitUntil(timeoutMillis = 120_000) {
                    events.count { it.contains(fragment) } >= count
                }

                // V11 is its own opt-in: variant A is selected and saved through its own button.
                press("Guardar PDF PREQA")
                awaitEvent("PDF V11 interno guardado", 1)
                onNodeWithText("PDF PREQA guardado", substring = true).assertExists()

                // B is reachable from the same panel and goes through Save.
                press("B")
                waitForIdle()
                press("Guardar PDF PREQA")
                awaitEvent("PDF V11 interno guardado", 2)

                // Share delivers the same document as a file.
                press("Compartir PDF PREQA")
                awaitEvent("PDF V11 interno compartido", 1)

                // The old family keeps its own button and its own file name.
                press("Guardar PDF…")
                waitUntil(timeoutMillis = 120_000) {
                    File(out, "Prueba UI - Hoja de PJ - Fantasy Sheet.pdf").isFile
                }
                waitForIdle()
            }

            val sources = desktopPcSheetExportSources(assertNotNull(controller.details(pc.id)))
            val service = DesktopPcSheetExportService()
            fun expected(variant: PcSheetV11Variant) = text(service.generateV11Proof(
                DesktopPcSheetExportOptions().let {
                    PcSheetV11ExportRequest(variant, it.stateSelection, portraitFitMode = it.portraitFitMode,
                        includeSpellDescriptions = it.includeSpellDescriptions)
                }, sources).bytes)

            val savedA = File(out, "Prueba UI - Hoja de PJ - PREQA V11 A.pdf")
            val savedB = File(out, "Prueba UI - Hoja de PJ - PREQA V11 B.pdf")
            assertTrue(savedA.isFile && savedB.isFile, "Both variants were saved: ${out.list()?.toList()}")
            assertEquals(expected(PcSheetV11Variant.A), text(savedA.readBytes()), "Save A wrote variant A.")
            assertEquals(expected(PcSheetV11Variant.B), text(savedB.readBytes()), "Save B wrote variant B.")
            assertNotEquals(text(savedA.readBytes()), text(savedB.readBytes()), "A and B are different sheets.")

            val shareFile = assertNotNull(shared, "Share delivered a file.")
            assertEquals(savedB.name, shareFile.name, "Share keeps the selected variant.")
            assertEquals(text(savedB.readBytes()), text(shareFile.readBytes()), "Share and Save give the same sheet.")

            val old = text(File(out, "Prueba UI - Hoja de PJ - Fantasy Sheet.pdf").readBytes())
            assertNotEquals(text(savedA.readBytes()), old, "The old family is still a different document.")
        } finally {
            handle.close()
            dir.deleteRecursively()
        }
    }
}
