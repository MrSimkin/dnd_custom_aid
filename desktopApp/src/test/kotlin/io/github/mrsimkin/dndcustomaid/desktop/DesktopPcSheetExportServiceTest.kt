package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorRepository
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportAggregate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportPlanner
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper

class DesktopPcSheetExportServiceTest {
    @Test
    fun saveAndShareDeliveryUseTheExactFrozenProductionRendererOutput() {
        val tempDir = Files.createTempDirectory("dnd-custom-aid-pdf-delivery-")
        val handle = DesktopDatabaseFactory(tempDir.resolve("delivery.db").toFile()).create()
        try {
            val database = handle.database
            val campaign = CampaignRepository(database).createCampaign("PDF Delivery")
            val pc = CharacterRepository(database).createCharacter(campaign.id, "Hero: Export / Test")
            val aggregate = PcSheetExportAggregate(
                sheet = pc,
                closure = CharacterClosureRepository(database).state(pc.id),
                successor = CharacterSuccessorRepository(database).state(pc.id),
            )
            val sources = PcSheetExportSources(permanent = aggregate)
            val request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            )

            val service = DesktopPcSheetExportService()
            val generated = service.generate(request, sources)
            val directPlan = PcSheetPdfExportPlanner.plan(request, sources)
            val directBytes = ByteArrayOutputStream().use { buffer ->
                DesktopPcSheetWholeDraftRenderer().renderDraft(directPlan, buffer)
                buffer.toByteArray()
            }

            assertEquals(directPlan, generated.plan)
            Loader.loadPDF(directBytes).use { direct ->
                Loader.loadPDF(generated.bytes).use { delivered ->
                    assertEquals(direct.numberOfPages, delivered.numberOfPages)
                    assertEquals(PDFTextStripper().getText(direct), PDFTextStripper().getText(delivered))

                    val directRenderer = PDFRenderer(direct)
                    val deliveredRenderer = PDFRenderer(delivered)
                    repeat(direct.numberOfPages) { pageIndex ->
                        val expected = directRenderer.renderImageWithDPI(pageIndex, 72f, ImageType.RGB)
                        val actual = deliveredRenderer.renderImageWithDPI(pageIndex, 72f, ImageType.RGB)
                        assertEquals(expected.width, actual.width)
                        assertEquals(expected.height, actual.height)
                        assertContentEquals(
                            expected.getRGB(0, 0, expected.width, expected.height, null, 0, expected.width),
                            actual.getRGB(0, 0, actual.width, actual.height, null, 0, actual.width),
                        )
                    }
                }
            }

            val saved = service.save(generated, tempDir.resolve("saved-sheet").toFile())
            assertTrue(saved.name.endsWith(".pdf"))
            assertContentEquals(generated.bytes, saved.readBytes())

            val shared = service.createShareFile(
                generated = generated,
                characterName = pc.name,
                directory = tempDir.resolve("share").toFile(),
            )
            assertTrue(shared.isFile)
            assertContentEquals(generated.bytes, shared.readBytes())
            assertEquals(
                "Hero_ Export _ Test - Hoja de PJ - Fantasy Sheet.pdf",
                shared.name,
            )
        } finally {
            handle.close()
            tempDir.toFile().deleteRecursively()
        }
    }
}
