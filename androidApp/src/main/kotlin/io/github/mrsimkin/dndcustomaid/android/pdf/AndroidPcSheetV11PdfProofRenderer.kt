package io.github.mrsimkin.dndcustomaid.android.pdf

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowItem
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableComposer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTablePlaced
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpecialEquipmentComposer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSourceMapper
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11RectPt
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticData
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Typeface
import java.io.OutputStream
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle

/**
 * FIRST actual app-code V11 PDF output path: a bounded INTERNAL proof renderer.
 * Never expose as a production/owner-approved V11 layout: the full P1/P2/P3/C2/CN3
 * visual composition, marks, logo, paper rules and portrait are not yet implemented.
 * This PDF is deliberately stamped as an internal layout test.
 *
 * G3 acceptance is separate from proving that local Android/Desktop code writes PDF bytes.
 * The four existing exported sheet families continue to use their original renderers.
 */
internal class AndroidPcSheetV11PdfProofRenderer {
    fun render(data: PcSheetV11SemanticData, target: OutputStream) {
        PDDocument().use { pdf ->
            val measurer = AndroidPcSheetV11FontMeasurer(pdf)
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

            // P2 has four separate print modules. This drawing path uses the
            // same measured source-ID rows that G2 verified; it is not a plain-text mock.
            // Areas remain INTERNAL proof geometry, not golden-approved coordinates.
            val equipmentArea = PcSheetV11RectPt(42f, 435f, 528f, 255f)
            val treasureArea = PcSheetV11RectPt(42f, 342f, 528f, 80f)
            val traitsArea = PcSheetV11RectPt(42f, 215f, 528f, 113f)
            val specialArea = PcSheetV11RectPt(42f, 90f, 528f, 110f)
            val inventoryPlaced = PcSheetV11PairedTableComposer.place(
                PcSheetV11PairedTableSources.equipment(data),
                equipmentArea, 12f, 36f, 6f, body.metrics.lineHeightPt, body.width,
            )
            val treasurePlaced = PcSheetV11PairedTableComposer.place(
                PcSheetV11PairedTableSources.treasure(data),
                treasureArea, 12f, 36f, 6f, body.metrics.lineHeightPt, body.width,
            )
            val traitLines = data.traits.map { trait ->
                PcSheetV11FlowItem("p2-trait:" + trait.id,
                    trait.name + " (" + trait.source + "): " + trait.description +
                        (trait.notes?.let { " / " + it } ?: ""),
                    keepTogetherIfFits = true)
            }
            val traitsPlaced = body.place(PcSheetV11FlowSection.TRAITS, traitLines,
                traitsArea, 2, 12f)
            val specialPlaced = PcSheetV11SpecialEquipmentComposer.place(
                data.specialEquipment, specialArea, 14f, 7f,
                body.metrics.lineHeightPt, body.width,
            )

            fun grid(stream: PDPageContentStream, rect: PcSheetV11RectPt, twoColumns: Boolean) {
                stream.addRect(rect.leftPt, rect.bottomPt, rect.widthPt, rect.heightPt)
                stream.stroke()
                if (twoColumns) {
                    val center = rect.leftPt + rect.widthPt / 2f
                    stream.moveTo(center, rect.bottomPt)
                    stream.lineTo(center, rect.topPt)
                    stream.stroke()
                }
                val cells = (rect.heightPt / body.metrics.lineHeightPt).toInt()
                for (line in 1 until cells) {
                    val y = rect.topPt - line * body.metrics.lineHeightPt
                    stream.moveTo(rect.leftPt, y)
                    stream.lineTo(rect.rightPt, y)
                    stream.stroke()
                }
            }

            fun drawPaired(
                stream: PDPageContentStream,
                title: String,
                area: PcSheetV11RectPt,
                placed: PcSheetV11PairedTablePlaced,
                localPage: Int,
            ) {
                draw(stream, title, area.leftPt, area.topPt + 6f, true)
                grid(stream, area, true)
                placed.pairs.forEach { pair ->
                    val quantitySeparator = pair.rightPt - 42f
                    stream.moveTo(quantitySeparator, pair.bottomPt)
                    stream.lineTo(quantitySeparator, pair.topPt)
                    stream.stroke()
                }
                placed.labels.filter { it.localPageIndex == localPage }.forEach { cell ->
                    if (cell.printableText.isNotEmpty()) {
                        draw(stream, cell.printableText, cell.rectPt.leftPt + 2f,
                            body.metrics.baselineIn(cell.rectPt), false)
                    }
                }
                placed.quantities.filter { it.localPageIndex == localPage }.forEach { cell ->
                    draw(stream, cell.printableText, cell.rectPt.leftPt + 2f,
                        body.metrics.baselineIn(cell.rectPt), false)
                }
            }

            fun drawP2Modules(stream: PDPageContentStream, localPage: Int) {
                drawPaired(stream, "EQUIPO   /   CANTIDAD", equipmentArea, inventoryPlaced, localPage)
                drawPaired(stream, "TESORO   /   CANTIDAD", treasureArea, treasurePlaced, localPage)
                draw(stream, "RASGOS", traitsArea.leftPt, traitsArea.topPt + 6f, true)
                grid(stream, traitsArea, true)
                traitsPlaced.cells.filter { it.localPageIndex == localPage }.forEach { cell ->
                    if (cell.drawableText.isNotEmpty()) {
                        draw(stream, cell.drawableText, cell.cellPt.leftPt + 2f,
                            body.metrics.baselineIn(cell.cellPt), false)
                    }
                }
                draw(stream, "EQUIPO ESPECIAL   /   ACTIVO", specialArea.leftPt,
                    specialArea.topPt + 6f, true)
                grid(stream, specialArea, false)
                val markColumn = specialArea.rightPt - 14f
                stream.moveTo(markColumn - 7f, specialArea.bottomPt)
                stream.lineTo(markColumn - 7f, specialArea.topPt)
                stream.stroke()
                specialPlaced.body.cells.filter { it.localPageIndex == localPage }.forEach { cell ->
                    if (cell.drawableText.isNotEmpty()) {
                        draw(stream, cell.drawableText, cell.cellPt.leftPt + 2f,
                            body.metrics.baselineIn(cell.cellPt), false)
                    }
                }
                specialPlaced.activeMarks.filter { it.localPageIndex == localPage }.forEach { mark ->
                    stream.addRect(mark.rectPt.leftPt + 2f, mark.rectPt.bottomPt + 1f, 9f, 9f)
                    stream.stroke()
                    if (mark.active == true) {
                        draw(stream, "X", mark.rectPt.leftPt + 2f,
                            body.metrics.baselineIn(mark.rectPt), false)
                    }
                }
            }

            PDPage(PDRectangle.LETTER).also { page ->
                pdf.addPage(page)
                PDPageContentStream(pdf, page).use { stream ->
                    draw(stream, source.name, 42f, 757f, true)
                    draw(stream, "P2 - Equipo y tesoro", 42f, 726f, true)
                    draw(stream, "PRUEBA INTERNA V11 - NO APROBADA", 42f, 46f, false)
                    drawP2Modules(stream, localPage = 1)
                }
            }

            addSection("P3 - Trasfondo / Perfil / Historia", PcSheetV11FlowSection.NARRATIVE,
                texts.narrative, 1, alwaysPage = true)

            if (data.hasQuickSpellList) {
                val quick = data.spells.map {
                    PcSheetV11FlowItem("quick-spell:" + it.spell.id,
                        "Nv " + it.spell.level + " " + it.spell.name)
                }
                addSection("P4 - Conjuros", PcSheetV11FlowSection.QUICK_SPELLS, quick, 3)
            }

            // Physical C2 comes AFTER P3 / conditional P4, never mixed into P3.
            // G2 submodule continuations carry the original source offsets and IDs.
            val continuationPages = maxOf(
                inventoryPlaced.localPageCount,
                treasurePlaced.localPageCount,
                traitsPlaced.sourceFlow.localPageCount,
                specialPlaced.localPageCount,
                1,
            )
            for (localPage in 2..continuationPages) {
                val page = PDPage(PDRectangle.LETTER)
                pdf.addPage(page)
                PDPageContentStream(pdf, page).use { stream ->
                    draw(stream, source.name, 42f, 757f, true)
                    draw(stream, "C2 - Equipo / rasgos / especial (" + (localPage - 1) + ")",
                        42f, 726f, true)
                    draw(stream, "PRUEBA INTERNA V11 - NO APROBADA", 42f, 46f, false)
                    drawP2Modules(stream, localPage)
                }
            }

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
                // Final physical page: NO digital content. Two handwriting
                // columns at the top, complete aligned square grid below.
                // Technical proof geometry; final acceptance compares to golden.
                for (column in 0..1) {
                    val left = if (column == 0) 42f else 316f
                    val right = if (column == 0) 296f else 570f
                    for (row in 0 until 15) {
                        val y = 694f - row * 16f
                        stream.moveTo(left, y)
                        stream.lineTo(right, y)
                        stream.stroke()
                    }
                }
                // 528pt = 33 exact 16pt cells; 352pt = 22 exact rows.
                // No clipped or partial square at the outer edge.
                for (column in 0..33) {
                    val x = 42f + column * 16f
                    stream.moveTo(x, 96f)
                    stream.lineTo(x, 448f)
                    stream.stroke()
                }
                for (row in 0..22) {
                    val y = 96f + row * 16f
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
