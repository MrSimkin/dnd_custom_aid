package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowItem
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSourceMapper
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11RectPt
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticData
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Typeface
import java.io.OutputStream
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle

/**
 * FIRST actual app-code V11 PDF output path: a bounded INTERNAL proof renderer.
 * Never expose as a production/owner-approved V11 layout: the full P1/P2/P3/C2/CN3
 * visual composition, marks, logo, paper rules and portrait are not yet implemented.
 * This PDF is deliberately stamped as an internal layout test.
 *
 * G3 acceptance is separate from proving that local Android/Desktop code writes PDF bytes.
 * The four existing exported sheet families continue to use their original renderers.
 */
internal class DesktopPcSheetV11PdfProofRenderer {
    fun render(data: PcSheetV11SemanticData, target: OutputStream) {
        PDDocument().use { pdf ->
            val measurer = DesktopPcSheetV11FontMeasurer(pdf)
            val body = measurer.resolve(PcSheetV11Typeface.BODY, 9f)
            measurer.resolve(PcSheetV11Typeface.LABEL, 12f)
            val bodyPdfFont = measurer.font(PcSheetV11Typeface.BODY)
            val headerPdfFont = measurer.font(PcSheetV11Typeface.LABEL)
            val source = data.source.sheet
            val texts = PcSheetV11FlowSourceMapper.from(data)
            val pageArea = PcSheetV11RectPt(42f, 76f, 528f, 626f)

            fun draw(stream: PDPageContentStream, text: String, x: Float, y: Float, label: Boolean) {
                if (text.isEmpty()) return
                stream.beginText()
                stream.setFont(if (label) headerPdfFont else bodyPdfFont, if (label) 12f else 9f)
                stream.newLineAtOffset(x, y)
                stream.showText(text)
                stream.endText()
            }

            fun addSection(
                title: String,
                section: PcSheetV11FlowSection,
                entries: List<PcSheetV11FlowItem>,
                columns: Int,
                alwaysPage: Boolean = false,
            ) {
                if (!alwaysPage && entries.isEmpty()) return
                val flow = body.place(section, entries, pageArea, columns, if (columns == 1) 0f else 12f)
                val pageCount = maxOf(if (alwaysPage) 1 else 0, flow.sourceFlow.localPageCount)
                repeat(pageCount) { index ->
                    val page = PDPage(PDRectangle.LETTER)
                    pdf.addPage(page)
                    PDPageContentStream(pdf, page).use { stream ->
                        draw(stream, source.name, 42f, 757f, true)
                        draw(stream, title + " / " + (index + 1), 42f, 726f, true)
                        draw(stream, "PRUEBA INTERNA V11 - NO APROBADA", 42f, 46f, false)
                        flow.cells.filter { it.localPageIndex == index + 1 }.forEach { cell ->
                            if (cell.drawableText.isNotEmpty()) {
                                draw(stream, cell.drawableText,
                                    cell.cellPt.leftPt,
                                    body.metrics.baselineIn(cell.cellPt),
                                    false)
                            }
                        }
                    }
                }
            }

            val identity = buildList {
                add(PcSheetV11FlowItem("identity:name", "Personaje: " + source.name))
                add(PcSheetV11FlowItem("identity:variant", "Variante: " + data.request.variant))
                add(PcSheetV11FlowItem("identity:state", "Estado seleccionado: " + data.selectedState))
                add(PcSheetV11FlowItem("identity:classes",
                    "Clase/nivel: " + source.classes.joinToString(", ") { it.name + " " + it.level }))
                add(PcSheetV11FlowItem("identity:combat",
                    "CA " + source.armorClass + " | PG " + source.currentHp + "/" + source.maxHp +
                        " | PG temp " + source.tempHp + " | Velocidad " + source.speed))
                data.attributes.forEachIndexed { index, it ->
                    add(PcSheetV11FlowItem("attribute:" + index,
                        it.name + " " + it.score + " / mod " + it.modifier +
                            " / salvación " + (it.savingThrowTotal?.toString() ?: "sin valor")))
                }
                data.skills.forEachIndexed { index, it ->
                    add(PcSheetV11FlowItem("skill:" + index, "Habilidad " + it.name +
                        ": " + (it.total?.toString() ?: "sin resolver")))
                }
                data.combatEntries.forEach { it ->
                    add(PcSheetV11FlowItem("attack:" + it.id,
                        "Ataque " + it.name + " | " + it.type + " | " +
                            (it.attackModifier?.toString() ?: "") + " | " + it.damageEffect))
                }
                data.spellSlots.forEach { it ->
                    add(PcSheetV11FlowItem("slot:" + it.level,
                        "Ranuras nivel " + it.level + " " + it.totalSlots + " total, " +
                            it.spentSlots + " gastadas"))
                }
            }
            addSection("P1 - Personaje / combate / habilidades", PcSheetV11FlowSection.TRAITS,
                identity, 2, alwaysPage = true)

            val inventory = buildList {
                data.ordinaryEquipment.forEach { it ->
                    add(PcSheetV11FlowItem("ordinary:" + it.sourceId,
                        "Equipo: " + it.name + " | cantidad " + it.quantity))
                }
                data.currencies.forEach { it ->
                    add(PcSheetV11FlowItem("currency:" + it.key, "Tesoro: " + it.name +
                        " | cantidad " + it.amount))
                }
            }
            addSection("P2 - Equipo y tesoro", PcSheetV11FlowSection.ORDINARY_EQUIPMENT,
                inventory, 2, alwaysPage = true)

            addSection("P3 - Trasfondo / Perfil / Historia", PcSheetV11FlowSection.NARRATIVE,
                texts.narrative, 1, alwaysPage = true)

            if (data.hasQuickSpellList) {
                val quick = data.spells.map {
                    PcSheetV11FlowItem("quick-spell:" + it.spell.id,
                        "Nv " + it.spell.level + " " + it.spell.name)
                }
                addSection("P4 - Conjuros", PcSheetV11FlowSection.QUICK_SPELLS, quick, 3)
            }

            val extended = buildList {
                data.traits.forEach { it ->
                    add(PcSheetV11FlowItem("trait:" + it.id, "Rasgo " + it.name +
                        " (" + it.source + "): " + it.description))
                }
                data.specialEquipment.forEach { it ->
                    add(PcSheetV11FlowItem("special:" + it.sourceId,
                        "Equipo especial " + it.name + " | Ubicación: " + (it.location ?: "") +
                            " | ACTIVO: " + (it.active?.toString() ?: "sin verificar") +
                            " | " + (it.description ?: "") + " | " + (it.notes ?: "")))
                }
            }
            addSection("C2 - Rasgos y equipo especial", PcSheetV11FlowSection.TRAITS, extended, 1)

            addSection("C-N3 - Notas digitales", PcSheetV11FlowSection.DIGITAL_NOTES,
                texts.digitalNotes, 1)
            data.spellbookEntries?.let { spells ->
                addSection("Libro de conjuros", PcSheetV11FlowSection.SPELLBOOK,
                    spells.map {
                        PcSheetV11FlowItem("book:" + it.spell.id,
                            "Nivel " + it.spell.level + " " + it.spell.name + ": " +
                                it.spell.description)
                    }, 2)
            }

            val finalPage = PDPage(PDRectangle.LETTER)
            pdf.addPage(finalPage)
            PDPageContentStream(pdf, finalPage).use { stream ->
                draw(stream, source.name, 42f, 757f, true)
                draw(stream, "NOTAS - escritura manual", 42f, 726f, true)
                // No digital data ever appears on the final handwritten-only page.
                for (row in 0..27) {
                    val y = 697f - row * 21f
                    stream.moveTo(42f, y)
                    stream.lineTo(570f, y)
                    stream.stroke()
                }
            }
            require(pdf.numberOfPages >= 4) { "A V11 proof must include P1/P2/P3 and final Notes." }
            pdf.save(target)
        }
    }
}
