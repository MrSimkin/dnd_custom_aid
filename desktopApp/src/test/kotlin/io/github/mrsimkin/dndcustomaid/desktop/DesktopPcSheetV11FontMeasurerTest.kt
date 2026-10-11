package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDecodeResult
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportAggregate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowItem
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSourceMapper
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11RectPt
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticMapper
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Typeface
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant
import java.io.File
import org.apache.pdfbox.pdmodel.PDDocument
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class DesktopPcSheetV11FontMeasurerTest {
    @Test
    fun embeddedFiraAndBarlowProvideTruePdfMetrics() {
        PDDocument().use { pdf ->
            val measurer = DesktopPcSheetV11FontMeasurer(pdf)
            val body = measurer.resolve(PcSheetV11Typeface.BODY, 9f)
            val label = measurer.resolve(PcSheetV11Typeface.LABEL, 9f)
            assertEquals("fonts/pdf/text/FiraSans-Regular.ttf", body.typeface.assetPath)
            assertEquals("fonts/pdf/text/BarlowCondensed-Bold.ttf", label.typeface.assetPath)
            assertTrue(body.metrics.ascentPt > 0f && body.metrics.descentPt > 0f)
            assertTrue(body.metrics.lineHeightPt > body.metrics.ascentPt + body.metrics.descentPt)
            assertEquals(0f, body.width.measurePt(""))
            assertTrue(body.width.measurePt("MMM") > body.width.measurePt("iii"))
            assertTrue(body.width.measurePt("ñáéíóú —") > 0f)
            assertTrue(label.width.measurePt("Mara de los Siete Umbrales") > 0f)
            assertTrue(body.width.measurePt("Aster") != label.width.measurePt("Aster"))
            val larger = measurer.resolve(PcSheetV11Typeface.BODY, 18f)
            assertEquals(body.width.measurePt("Aster") * 2f, larger.width.measurePt("Aster"), 0.01f)
            val area = PcSheetV11RectPt(45f, 65f, 520f, 660f)
            val texts = listOf(PcSheetV11FlowItem(
                "long-story", ("ñáéíóú — Historia extensa. ").repeat(280),
            ))
            val placed = body.place(PcSheetV11FlowSection.NARRATIVE, texts, area, 2, 12f)
            assertTrue(placed.sourceFlow.localPageCount >= 2)
            placed.sourceFlow.verifyLosslessAgainst(texts)
            placed.verifyGeometry()
            placed.cells.forEach { cell ->
                assertTrue(body.width.measurePt(cell.drawableText) <= cell.cellPt.widthPt + 0.01f)
                val baseline = body.metrics.baselineIn(cell.cellPt)
                assertTrue(baseline >= cell.cellPt.bottomPt && baseline <= cell.cellPt.topPt)
            }
        }
    }

    @Test
    fun realMaraBackupFlowsLosslesslyWithTheBundledBodyFont() {
        val current = File(System.getProperty("user.dir")).absoluteFile
        val fixtures = generateSequence(current) { it.parentFile }
            .map { File(it, "qa/pc-sheet/fixtures") }
            .firstOrNull { it.isDirectory }
            ?: error("Real QA fixtures unavailable")
        val raw = File(fixtures, "03_mara_siete_umbrales_custom_extended.json").readText()
        val original = (CharacterBackupCodec.decode(raw) as CharacterBackupDecodeResult.Success).document
        val data = PcSheetV11SemanticMapper.map(
            PcSheetV11ExportRequest(PcSheetV11Variant.B, PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true),
            PcSheetExportSources(PcSheetExportAggregate(
                sheet = original.character,
                closure = original.closureState,
                successor = original.successorState,
            )),
        )
        val sources = PcSheetV11FlowSourceMapper.from(data)
        PDDocument().use { pdf ->
            val font = DesktopPcSheetV11FontMeasurer(pdf).resolve(PcSheetV11Typeface.BODY, 9f)
            val area = PcSheetV11RectPt(45f, 70f, 520f, 640f)
            val sections = listOf(
                PcSheetV11FlowSection.NARRATIVE,
                PcSheetV11FlowSection.DIGITAL_NOTES,
                PcSheetV11FlowSection.TRAITS,
                PcSheetV11FlowSection.SPECIAL_EQUIPMENT,
                PcSheetV11FlowSection.SPELLBOOK,
            )
            sections.forEach { section ->
                val items = sources.forSection(section)
                val placed = font.place(section, items, area, 2, 12f)
                placed.sourceFlow.verifyLosslessAgainst(items)
                placed.verifyGeometry()
                placed.cells.forEach { cell ->
                    assertTrue(
                        font.width.measurePt(cell.drawableText) <= cell.cellPt.widthPt + 0.01f,
                        "Real font overflow in " + section + " / " + cell.sourceKey,
                    )
                    assertTrue(area.contains(cell.cellPt))
                }
            }
            val equipment = io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableSources.equipment(data)
            val treasure = io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableSources.treasure(data)
            assertEquals(29, equipment.rows.size)
            assertEquals(5, treasure.rows.size)
            assertEquals(data.ordinaryEquipment.map { it.sourceId }, equipment.rows.map { it.sourceId })
            assertEquals(data.currencies.map { it.amount }, treasure.rows.map { it.amount })
            listOf(equipment, treasure).forEach { table ->
                val paired = io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableComposer.place(
                    data = table,
                    areaPt = area,
                    gutterPt = 12f,
                    quantityWidthPt = 36f,
                    labelQuantityGapPt = 6f,
                    lineHeightPt = font.metrics.lineHeightPt,
                    measuredWidth = font.width,
                )
                paired.verify()
                assertEquals(table.rows.size, paired.quantities.size)
                assertEquals(table.rows.map { it.label },
                    table.rows.map { row ->
                        paired.labels.filter { it.sourceKey == row.sourceKey }
                            .joinToString("") { it.sourceText }
                    })
                paired.labels.forEach { cell ->
                    assertTrue(font.width.measurePt(cell.printableText) <= cell.rectPt.widthPt + 0.01f)
                }
                paired.quantities.forEach { cell ->
                    assertTrue(font.width.measurePt(cell.printableText) <= cell.rectPt.widthPt + 0.01f)
                    assertTrue(area.contains(cell.rectPt))
                }
            }
        }
    }

    @Test
    fun missingAssetsOrInvalidTypeSizesCannotTriggerFontSubstitutions() {
        PDDocument().use { pdf ->
            val missing = DesktopPcSheetV11FontMeasurer(pdf) { null }
            assertFails { missing.resolve(PcSheetV11Typeface.BODY, 9f) }
            val real = DesktopPcSheetV11FontMeasurer(pdf)
            assertFails { real.resolve(PcSheetV11Typeface.BODY, Float.NaN) }
            assertFails { real.resolve(PcSheetV11Typeface.BODY, 9f, 0.5f) }
        }
    }
}
