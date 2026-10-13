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
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.harmony.awt.AWTColor as Color

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
    fun render(
        data: PcSheetV11SemanticData,
        target: OutputStream,
        portraitBytes: ByteArray? = null,
    ) {
        PDDocument().use { pdf ->
            val measurer = AndroidPcSheetV11FontMeasurer(pdf)
            val body = measurer.resolve(PcSheetV11Typeface.BODY, 9f)
            measurer.resolve(PcSheetV11Typeface.LABEL, 12f)
            val bodyPdfFont = measurer.font(PcSheetV11Typeface.BODY)
            val headerPdfFont = measurer.font(PcSheetV11Typeface.LABEL)
            val source = data.source.sheet
            val texts = PcSheetV11FlowSourceMapper.from(data)
            val pageArea = PcSheetV11RectPt(42f, 76f, 528f, 626f)
            // Physical page roles are registered exactly when a PDFBox page is
            // created. Footers and C2 backlinks are resolved only when the final
            // document length is known: no section-local page guesses.
            val physicalSectionKeys = mutableListOf<String>()

            fun draw(stream: PDPageContentStream, text: String, x: Float, y: Float, label: Boolean) {
                if (text.isEmpty()) return
                stream.beginText()
                stream.setFont(if (label) headerPdfFont else bodyPdfFont, if (label) 12f else 9f)
                stream.newLineAtOffset(x, y)
                stream.showText(text)
                stream.endText()
            }

            // Owner's original D&D sheet includes its genuine logotype in its top-left.
            // Embed that real graphic from the already-versioned asset; clip to LOGO only,
            // so no original-sheet boxes or lettering leak into this V11 page.
            val originalSheetPixels = requireNotNull(AndroidPcSheetAssetLoader.open(
                "character-sheets/templates/renders/original/page-1.png",
            )?.use { it.readBytes() }) {
                "The approved owner-logo source image is missing; never substitute text."
            }
            val ownerLogo = PDImageXObject.createFromByteArray(
                pdf, originalSheetPixels, "owner-dnd-logo-original",
            )
            require(ownerLogo.width == 1224 && ownerLogo.height == 1584) {
                "Unexpected original-logo raster dimensions; refuse an incorrect crop."
            }

            fun drawFrameAndHeader(
                stream: PDPageContentStream,
                title: String,
                lastHandwrittenPage: Boolean = false,
            ) {
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(1.2f)
                stream.addRect(24f, 24f, 564f, 744f)
                stream.stroke()
                stream.setLineWidth(0.65f)
                stream.addRect(29f, 29f, 554f, 734f)
                stream.stroke()
                stream.setNonStrokingColor(Color(231, 241, 247))
                stream.addRect(148f, 736f, 422f, 26f)
                stream.fill()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.65f)
                stream.moveTo(42f, 717f)
                stream.lineTo(570f, 717f)
                stream.stroke()
                stream.restoreGraphicsState()

                stream.saveGraphicsState()
                // Native template coordinates: logo x≈50–400, y≈52–181 in
                // the 1224×1584 pixel original. Clip only the logo region.
                stream.addRect(42f, 732f, 99f, 35f)
                stream.clip()
                // PDFBox exposes no endPath(); stroke closes the clipped path.
                stream.stroke()
                val scale = 0.28f
                stream.drawImage(
                    ownerLogo,
                    42f - 49f * scale,
                    767f + 52f * scale - ownerLogo.height * scale,
                    ownerLogo.width * scale,
                    ownerLogo.height * scale,
                )
                stream.restoreGraphicsState()

                draw(stream, source.name, 154f, 747f, true)
                draw(stream, title, 42f, 724f, true)
                if (!lastHandwrittenPage) {
                    draw(stream, "PRUEBA INTERNA V11 - NO APROBADA", 42f, 46f, false)
                }
            }

            /**
             * P1 approved attributes: rectangular score, overlapping oval modifier,
             * a visually anchored saving throw, in the page's physical coordinates.
             * Additional custom attributes remain losslessly available in the
             * measured text section until their C2 visual relationship is finalized.
             */
            fun drawP1Attributes(stream: PDPageContentStream) {
                val standards = data.attributes.take(6)
                require(standards.size == 6) {
                    "A playable D&D sheet must supply the six standard abilities."
                }
                val left = 42f
                val width = 528f / 6f
                val kappa = 0.5522848f
                standards.forEachIndexed { index, ability ->
                    val x = left + index * width
                    val centerX = x + width / 2f
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(80, 122, 151))
                    stream.setLineWidth(0.75f)
                    stream.addRect(x + 2f, 593f, width - 4f, 95f)
                    stream.stroke()
                    stream.addRect(x + 19f, 628f, width - 38f, 34f)
                    stream.stroke()
                    // Oval centered over the bottom edge of the score rectangle.
                    val rx = 20f
                    val ry = 11f
                    val cy = 627f
                    stream.moveTo(centerX + rx, cy)
                    stream.curveTo(centerX + rx, cy + ry * kappa,
                        centerX + rx * kappa, cy + ry, centerX, cy + ry)
                    stream.curveTo(centerX - rx * kappa, cy + ry,
                        centerX - rx, cy + ry * kappa, centerX - rx, cy)
                    stream.curveTo(centerX - rx, cy - ry * kappa,
                        centerX - rx * kappa, cy - ry, centerX, cy - ry)
                    stream.curveTo(centerX + rx * kappa, cy - ry,
                        centerX + rx, cy - ry * kappa, centerX + rx, cy)
                    stream.closePath()
                    stream.stroke()
                    stream.restoreGraphicsState()

                    val title = ability.name
                    require(body.width.measurePt(title) <= width - 6f) {
                        "Ability title does not fit the approved P1 cell: " + title
                    }
                    val titleWidth = body.width.measurePt(title)
                    draw(stream, title, centerX - titleWidth / 2f, 672f, false)
                    val score = ability.score.toString()
                    draw(stream, score, centerX - body.width.measurePt(score) / 2f, 642f, false)
                    val mod = if (ability.modifier >= 0) "+" + ability.modifier
                        else ability.modifier.toString()
                    draw(stream, mod, centerX - body.width.measurePt(mod) / 2f, 623f, false)
                    draw(stream, "SALV.", x + 7f, 602f, false)
                    ability.savingThrowTotal?.let { save ->
                        val saving = if (save >= 0) "+" + save else save.toString()
                        draw(stream, saving,
                            x + width - 8f - body.width.measurePt(saving), 602f, false)
                    }
                }
            }



            // Variant A fixes built-in skills below their matching ability box.
            // Any overlong or custom skill is preserved whole in C2, with the
            // three-character ability suffix required outside its attribute.
            val variantA = data.request.variant ==
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A
            val skillFont = measurer.resolve(PcSheetV11Typeface.BODY, 7f)
            val groupedSkills = mutableMapOf<Int, MutableList<Pair<Int, io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Skill>>>()
            val printedSkillIndices = mutableSetOf<Int>()
            if (variantA) {
                data.attributes.take(6).forEachIndexed { attrIndex, attr ->
                    val fitsHere = mutableListOf<Pair<Int, io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Skill>>()
                    data.skills.forEachIndexed { skillIndex, skill ->
                        if (skill.ability == attr.reference && skillIndex !in printedSkillIndices &&
                            fitsHere.size < 6 && skill.name.isNotBlank()
                        ) {
                            val total = skill.total?.let { if (it >= 0) "+" + it else it.toString() } ?: ""
                            val remaining = 88f - 11f - skillFont.width.measurePt(total) - 7f
                            if (skillFont.width.measurePt(skill.name) <= remaining) {
                                fitsHere += skillIndex to skill
                                printedSkillIndices += skillIndex
                            }
                        }
                    }
                    groupedSkills[attrIndex] = fitsHere
                }
            }

            fun drawP1GroupedSkills(stream: PDPageContentStream) {
                if (!variantA) return
                stream.beginText()
                stream.setFont(bodyPdfFont, 7f)
                stream.newLineAtOffset(42f, 583f)
                stream.showText("HABILIDADES BAJO ATRIBUTO  P=COMPETENCIA  E=PERICIA")
                stream.endText()
                groupedSkills.forEach { (attrIndex, rows) ->
                    val left = 42f + attrIndex * 88f
                    rows.forEachIndexed { rowIndex, indexed ->
                        val skill = indexed.second
                        val y = 571f - rowIndex * 10.5f
                        fun put(text: String, x: Float) {
                            if (text.isBlank()) return
                            stream.beginText()
                            stream.setFont(bodyPdfFont, 7f)
                            stream.newLineAtOffset(x, y)
                            stream.showText(text)
                            stream.endText()
                        }
                        val indicator = when (skill.training) {
                            io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.NONE -> ""
                            io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> "P"
                            io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> "E"
                        }
                        put(indicator, left + 2f)
                        put(skill.name, left + 11f)
                        skill.total?.let { total ->
                            val number = if (total >= 0) "+" + total else total.toString()
                            put(number, left + 84f - skillFont.width.measurePt(number))
                        }
                    }
                }
            }

            val detachedSkills = if (variantA) data.skills.withIndex()
                .filter { it.index !in printedSkillIndices }
                .map { (index, skill) ->
                    val abbreviation = data.attributes.firstOrNull {
                        it.reference == skill.ability
                    }?.abbreviation?.uppercase()?.take(3)
                    PcSheetV11FlowItem(
                        "detached-skill:" + index,
                        skill.name + (abbreviation?.let { " (" + it + ")" } ?: "") +
                            " / " + (skill.total?.toString() ?: "sin resolver") +
                            " / " + skill.training.name,
                    )
                }
            else emptyList()

            // P1 mandatory mechanics: no invented values for absent spell slots.
            // Real attacks receive three fields (attack/range, narrow bonus, damage).
            // Rows whose actual measured text will not fit move intact into C2.
            val attackRows = data.combatEntries.filter {
                it.type == io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK
            }
            val attackLabelWidth = 239f
            val attackBonusWidth = 49f
            val attackDamageWidth = 528f - attackLabelWidth - attackBonusWidth
            val p1Attacks = attackRows.takeWhile { entry ->
                val nameAndRange = entry.name +
                    (entry.rangeText?.takeIf { it.isNotBlank() }?.let { " / " + it } ?: "")
                val bonus = entry.attackModifier?.let { if (it >= 0) "+" + it else it.toString() } ?: ""
                val labelFits = body.width.measurePt(nameAndRange) <= attackLabelWidth - 10f
                val bonusFits = body.width.measurePt(bonus) <= attackBonusWidth - 8f
                val damageFits = body.width.measurePt(entry.damageEffect) <= attackDamageWidth - 10f
                labelFits && bonusFits && damageFits &&
                    attackRows.indexOf(entry) < 6
            }
            val p1AttackIds = p1Attacks.map { it.id }.toSet()

            fun drawP1Attacks(stream: PDPageContentStream) {
                val left = 42f
                val top = 303f
                val bottom = 210f
                val b1 = left + attackLabelWidth
                val b2 = b1 + attackBonusWidth
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.7f)
                stream.addRect(left, bottom, 528f, top - bottom)
                stream.stroke()
                for (x in listOf(b1, b2)) {
                    stream.moveTo(x, bottom)
                    stream.lineTo(x, top)
                    stream.stroke()
                }
                for (row in 1..7) {
                    val y = top - row * 12f
                    stream.moveTo(left, y)
                    stream.lineTo(left + 528f, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
                draw(stream, "ATAQUE / RANGO", left + 4f, top - 10f, false)
                draw(stream, "BONO", b1 + 3f, top - 10f, false)
                draw(stream, "DAÑO / EFECTO", b2 + 3f, top - 10f, false)
                p1Attacks.forEachIndexed { index, entry ->
                    val y = top - 22f - index * 12f
                    draw(stream, entry.name +
                        (entry.rangeText?.takeIf { it.isNotBlank() }?.let { " / " + it } ?: ""),
                        left + 4f, y, false)
                    entry.attackModifier?.let { value ->
                        draw(stream, if (value >= 0) "+" + value else value.toString(),
                            b1 + 3f, y, false)
                    }
                    draw(stream, entry.damageEffect, b2 + 3f, y, false)
                }
                if (attackRows.isEmpty()) {
                    draw(stream, "________________", left + 5f, top - 25f, false)
                }
            }

            fun drawP1Spellcasting(stream: PDPageContentStream) {
                draw(stream, "LANZAMIENTO DE CONJUROS", 42f, 198f, true)
                val actual = data.spellSlots.associateBy { it.level }
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.65f)
                stream.addRect(42f, 78f, 528f, 108f)
                stream.stroke()
                stream.restoreGraphicsState()
                draw(stream, "NIVEL", 47f, 175f, false)
                draw(stream, "ESPACIOS", 104f, 175f, false)
                draw(stream, "GASTADOS", 176f, 175f, false)
                for (level in 1..9) {
                    val y = 163f - (level - 1) * 9.4f
                    draw(stream, level.toString(), 48f, y, false)
                    val slots = actual[level]
                    if (slots != null) {
                        // Show stored total/spent even for zero; no assumed progression.
                        draw(stream, slots.totalSlots.toString(), 110f, y, false)
                        draw(stream, slots.spentSlots.toString(), 180f, y, false)
                        if (slots.totalSlots in 1..20) {
                            repeat(slots.totalSlots) { n ->
                                val x = 230f + n * 13f + (n / 5) * 4f
                                stream.addRect(x, y - 2f, 8f, 8f)
                                stream.stroke()
                                if (n < slots.spentSlots) draw(stream, "X", x + 1f, y, false)
                            }
                        }
                    } else {
                        draw(stream, "______", 107f, y, false)
                        draw(stream, "______", 180f, y, false)
                    }
                }
            }

            val extraCombatData = buildList {
                attackRows.forEach { attack ->
                    if (attack.id !in p1AttackIds) {
                        add(PcSheetV11FlowItem(
                            "combat:deferred:" + attack.id,
                            "ATAQUE " + attack.name +
                                (attack.rangeText?.let { " | ALCANCE " + it } ?: "") +
                                (attack.attackModifier?.let { " | BONO " + it } ?: "") +
                                " | DAÑO " + attack.damageEffect +
                                (attack.notes?.let { " | NOTAS " + it } ?: ""),
                            keepTogetherIfFits = true,
                        ))
                    } else {
                        attack.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                            add(PcSheetV11FlowItem(
                                "combat:notes:" + attack.id,
                                "NOTAS DEL ATAQUE " + attack.name + ": " + notes,
                                keepTogetherIfFits = true,
                            ))
                        }
                    }
                }
                // High-level slot rows only exist when explicitly persisted.
                data.spellSlots.filter { it.level >= 10 }.forEach { slots ->
                    add(PcSheetV11FlowItem(
                        "spellcasting:epic:" + slots.level,
                        "ESPACIOS REALES NIVEL " + slots.level +
                            ": TOTAL " + slots.totalSlots + ", GASTADOS " + slots.spentSlots,
                    ))
                }
            }


            fun drawP1Portrait(stream: PDPageContentStream) {
                val frameX = 429f
                val frameY = 320f
                val frameWidth = 141f
                val frameHeight = 175f
                val imageX = frameX + 7f
                val imageY = frameY + 7f
                val imageWidth = frameWidth - 14f
                val imageHeight = frameHeight - 28f
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.9f)
                stream.addRect(frameX, frameY, frameWidth, frameHeight)
                stream.stroke()
                stream.restoreGraphicsState()
                draw(stream, "RETRATO", frameX + 7f, frameY + frameHeight - 12f, false)

                if (portraitBytes != null) {
                    // Same data the existing App export services load locally.
                    // FIT preserves the full image; CROP clips its centre without
                    // stretching. This path is embedded in the actual PDF file.
                    val picture = PDImageXObject.createFromByteArray(
                        pdf, portraitBytes, "pc-v11-local-portrait",
                    )
                    require(picture.width > 0 && picture.height > 0)
                    val xRatio = imageWidth / picture.width
                    val yRatio = imageHeight / picture.height
                    val scale = if (data.portrait.fitMode ==
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPortraitFitMode.CROP_TO_FILL
                    ) maxOf(xRatio, yRatio) else minOf(xRatio, yRatio)
                    val drawWidth = picture.width * scale
                    val drawHeight = picture.height * scale
                    val px = imageX + (imageWidth - drawWidth) / 2f
                    val py = imageY + (imageHeight - drawHeight) / 2f
                    stream.saveGraphicsState()
                    stream.addRect(imageX, imageY, imageWidth, imageHeight)
                    stream.clip()
                    stream.stroke()
                    stream.drawImage(picture, px, py, drawWidth, drawHeight)
                    stream.restoreGraphicsState()
                } else {
                    // No usable local portrait: contract requires a reserved frame
                    // with a neutral silhouette, not an invented character image.
                    val cx = imageX + imageWidth / 2f
                    val cy = imageY + imageHeight * 0.58f
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130, 165, 186))
                    stream.setLineWidth(1f)
                    val radius = 19f
                    val k = radius * 0.5522848f
                    stream.moveTo(cx + radius, cy)
                    stream.curveTo(cx + radius, cy + k, cx + k, cy + radius, cx, cy + radius)
                    stream.curveTo(cx - k, cy + radius, cx - radius, cy + k, cx - radius, cy)
                    stream.curveTo(cx - radius, cy - k, cx - k, cy - radius, cx, cy - radius)
                    stream.curveTo(cx + k, cy - radius, cx + radius, cy - k, cx + radius, cy)
                    stream.closePath()
                    stream.stroke()
                    stream.moveTo(imageX + 12f, imageY + 16f)
                    stream.curveTo(imageX + 17f, imageY + 68f,
                        imageX + imageWidth - 17f, imageY + 68f,
                        imageX + imageWidth - 12f, imageY + 16f)
                    stream.stroke()
                    stream.restoreGraphicsState()
                }
            }

            // C2 leftover paper is handwriting space, not a new digital-notes section.
            // Occupied PDF line rectangles govern whether a complete square grid fits.
            fun drawUnusedC2NotesGrid(
                stream: PDPageContentStream,
                lowestTextBottom: Float,
            ) {
                val gridBottom = 78f
                val maxGridTop = lowestTextBottom - 28f
                val completeRows = ((maxGridTop - gridBottom) / 16f).toInt()
                if (completeRows < 3) return
                val gridTop = gridBottom + completeRows * 16f
                require(gridTop + 18f < lowestTextBottom) {
                    "C2 notes grid must never overlap character content."
                }
                draw(stream, "NOTAS", 42f, gridTop + 7f, true)
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.5f)
                repeat(34) { column ->
                    val x = 42f + column * 16f
                    stream.moveTo(x, gridBottom)
                    stream.lineTo(x, gridTop)
                    stream.stroke()
                }
                repeat(completeRows + 1) { row ->
                    val y = gridBottom + row * 16f
                    stream.moveTo(42f, y)
                    stream.lineTo(570f, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
            }

            fun fillUnusedC2WithNotes(
                stream: PDPageContentStream,
                cells: List<io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PlacedLineCell>,
            ) {
                val lowestTextBottom = cells.minOfOrNull { it.cellPt.bottomPt } ?: return
                drawUnusedC2NotesGrid(stream, lowestTextBottom)
            }

            // The approved P3 has ruled handwriting space beneath narrative,
            // never across paragraphs and never on a mechanically extended C2.
            // It reuses ACTUAL placed font baselines, not guessed character counts.
            fun drawP3HandwritingBelowContent(
                stream: PDPageContentStream,
                cells: List<io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PlacedLineCell>,
            ) {
                val usedBottom = cells.minOfOrNull { it.cellPt.bottomPt } ?: 684f
                val writingBottom = 84f
                val availableTop = minOf(usedBottom - 20f, 668f)
                val completeRows = ((availableTop - writingBottom) / 16f).toInt()
                if (completeRows < 3) return
                val writingTop = writingBottom + completeRows * 16f
                require(writingTop + 18f <= usedBottom) {
                    "P3 handwriting lines cannot overlap real printed narrative."
                }
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.5f)
                for (row in 0..completeRows) {
                    val y = writingBottom + row * 16f
                    stream.moveTo(42f, y)
                    stream.lineTo(570f, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
            }

            fun addSection(
                title: String,
                section: PcSheetV11FlowSection,
                entries: List<PcSheetV11FlowItem>,
                columns: Int,
                alwaysPage: Boolean = false,
                area: PcSheetV11RectPt = pageArea,
                specialMarks: Map<String, Boolean?> = emptyMap(),
                ammunitionTracks: Map<String, Int> = emptyMap(),
                maxPhysicalPages: Int? = null,
            ) {
                require(maxPhysicalPages == null || maxPhysicalPages > 0)
                if (!alwaysPage && entries.isEmpty()) return
                val flow = body.place(section, entries, area, columns, if (columns == 1) 0f else 12f)
                val pageCount = maxOf(if (alwaysPage) 1 else 0, flow.sourceFlow.localPageCount)
                repeat(minOf(pageCount, maxPhysicalPages ?: pageCount)) { index ->
                    val page = PDPage(PDRectangle.LETTER)
                    pdf.addPage(page)
                    physicalSectionKeys += title
                    PDPageContentStream(pdf, page).use { stream ->
                        drawFrameAndHeader(stream, title + " / " + (index + 1))
                        if (title.startsWith("P1 - ") && index == 0) {
                            drawP1Attributes(stream)
                            drawP1Attacks(stream)
                            drawP1Spellcasting(stream)
                            drawP1Portrait(stream)
                            drawP1GroupedSkills(stream)
                        }
                        val pageCells = flow.cells.filter {
                            it.localPageIndex == index + 1
                        }
                        pageCells.forEach { cell ->
                            if (cell.drawableText.isNotEmpty()) {
                                draw(stream, cell.drawableText,
                                    cell.cellPt.leftPt,
                                    body.metrics.baselineIn(cell.cellPt),
                                    false)
                            }
                            if (cell.sourceKey in ammunitionTracks &&
                                cell.sourceStartOffset == 0) {
                                val count = ammunitionTracks.getValue(cell.sourceKey)
                                require(count in 0..20)
                                if (count <= 20) {
                                    repeat(count) { mark ->
                                        val px = cell.cellPt.leftPt + 54f + mark * 8f +
                                            (mark / 5) * 4f
                                        require(px + 6f <= cell.cellPt.rightPt) {
                                            "Ammunition track overflows its physical PDF column."
                                        }
                                        stream.addRect(px, cell.cellPt.bottomPt + 2f, 6f, 6f)
                                        stream.stroke()
                                    }
                                }
                            }
                            if (cell.sourceKey in specialMarks && cell.sourceStartOffset == 0) {
                                stream.addRect(557f, cell.cellPt.bottomPt + 1f, 9f, 9f)
                                stream.stroke()
                                if (specialMarks[cell.sourceKey] == true) {
                                    draw(stream, "X", 557f,
                                        body.metrics.baselineIn(cell.cellPt), false)
                                }
                            }
                        }
                        if (title.startsWith("C2 - ")) {
                            fillUnusedC2WithNotes(stream, pageCells)
                        }
                        if (title.startsWith("P3 - ")) {
                            drawP3HandwritingBelowContent(stream, pageCells)
                        }
                    }
                }
            }

            val identity = buildList {
                add(PcSheetV11FlowItem("identity:variant", "Variante: " + data.request.variant))
                add(PcSheetV11FlowItem("identity:state", "Estado seleccionado: " + data.selectedState))
                add(PcSheetV11FlowItem("identity:classes",
                    "Clase/nivel: " + source.classes.joinToString(", ") { it.name + " " + it.level }))
                add(PcSheetV11FlowItem("identity:combat",
                    "CA " + source.armorClass + " | PG " + source.currentHp + "/" + source.maxHp +
                        " | PG temp " + source.tempHp + " | Velocidad " + source.speed))
                data.attributes.drop(6).forEachIndexed { index, it ->
                    add(PcSheetV11FlowItem("attribute:" + index,
                        it.name + " " + it.score + " / mod " + it.modifier +
                            " / salvación " + (it.savingThrowTotal?.toString() ?: "sin valor")))
                }
                if (!variantA) {
                    data.skills.forEachIndexed { index, skill ->
                        val abbreviation = data.attributes.firstOrNull {
                            it.reference == skill.ability
                        }?.abbreviation?.uppercase()?.take(3)
                        add(PcSheetV11FlowItem("skill:" + index,
                            "Habilidad " + skill.name +
                                (abbreviation?.let { " (" + it + ")" } ?: "") +
                                ": " + (skill.total?.toString() ?: "sin resolver") +
                                " / " + skill.training.name))
                    }
                }
                data.combatEntries
                    .filter { it.id !in p1AttackIds && it.type !=
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK }
                    .forEach { entry ->
                        add(PcSheetV11FlowItem("action:" + entry.id,
                            entry.name + " | " + entry.type +
                                (entry.rangeText?.let { " | ALCANCE " + it } ?: "") +
                                (entry.attackModifier?.let { " | BONO " + it } ?: "") +
                                " | " + entry.damageEffect +
                                (entry.notes?.let { " | NOTAS " + it } ?: "")))
                    }
            }
            // P1 is one physical sheet, not a repeating P1 sub-document.
            // Measure the complete source first and put EVERY unprinted source
            // suffix into C2; the same real font metrics drive both placements.
            val p1IdentityArea = PcSheetV11RectPt(
                42f, 320f, 378f, if (variantA) 175f else 252f,
            )
            val p1Measured = body.place(
                PcSheetV11FlowSection.TRAITS, identity, p1IdentityArea, 2, 12f,
            )
            val p1Unprinted = identity.mapNotNull { item ->
                val printedEnd = p1Measured.cells.asSequence()
                    .filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    .maxOfOrNull { it.sourceEndExclusive } ?: 0
                require(printedEnd in 0..item.text.length) {
                    "P1 placement cannot consume a non-source character offset."
                }
                if (printedEnd == item.text.length) null
                else item.copy(text = item.text.substring(printedEnd))
            }
            addSection("P1 - Personaje / combate / habilidades",
                PcSheetV11FlowSection.TRAITS, identity, 2, alwaysPage = true,
                area = p1IdentityArea, maxPhysicalPages = 1)

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
                suppressAmounts: Set<String> = emptySet(),
                compactC2: Boolean = false,
            ) {
                draw(stream, title, area.leftPt, area.topPt + 6f, true)
                val pageLabels = placed.labels.filter { it.localPageIndex == localPage }
                val lowestPrintedBottom = pageLabels.minOfOrNull { it.rectPt.bottomPt }
                // Unlike P2, C2's last table page must free genuine unused paper
                // for handwritten NOTES, without drawing empty object/quantity rows
                // underneath. These bounds come from measured source cells.
                val notesUsable = compactC2 && lowestPrintedBottom != null &&
                    ((lowestPrintedBottom - 28f - 78f) / 16f).toInt() >= 3
                val tableBottom = if (notesUsable) requireNotNull(lowestPrintedBottom)
                    else area.bottomPt
                val visibleTable = PcSheetV11RectPt(
                    area.leftPt, tableBottom, area.widthPt, area.topPt - tableBottom,
                )
                grid(stream, visibleTable, true)
                placed.pairs.forEach { pair ->
                    val quantitySeparator = pair.rightPt - 42f
                    stream.moveTo(quantitySeparator, visibleTable.bottomPt)
                    stream.lineTo(quantitySeparator, pair.topPt)
                    stream.stroke()
                }
                pageLabels.forEach { cell ->
                    if (cell.printableText.isNotEmpty()) {
                        draw(stream, cell.printableText, cell.rectPt.leftPt + 2f,
                            body.metrics.baselineIn(cell.rectPt), false)
                    }
                }
                placed.quantities.filter {
                    it.localPageIndex == localPage && it.sourceKey !in suppressAmounts
                }.forEach { cell ->
                    draw(stream, cell.printableText, cell.rectPt.leftPt + 2f,
                        body.metrics.baselineIn(cell.rectPt), false)
                }
                if (notesUsable) drawUnusedC2NotesGrid(stream, tableBottom)
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
                physicalSectionKeys += "P2 - Equipo y tesoro"
                PDPageContentStream(pdf, page).use { stream ->
                    drawFrameAndHeader(stream, "P2 - Equipo y tesoro")
                    drawP2Modules(stream, localPage = 1)
                }
            }

            addSection("P3 - Trasfondo / Perfil / Historia", PcSheetV11FlowSection.NARRATIVE,
                texts.narrative, 1, alwaysPage = true)

            if (data.hasQuickSpellList) {
                // Contract: a spell page, when present, reserves visible levels 0–9
                // even if a given level has no real spells; 10–12 require real
                // epic rules data and are not invented. Sources/preparation stay
                // linked to the specific persisted casting-source association.
                val quick = buildList {
                    val levels = (0..9).toList() +
                        data.spells.map { it.spell.level }.filter { it >= 10 }.distinct().sorted()
                    levels.forEach { level ->
                        add(PcSheetV11FlowItem("spell-level:" + level,
                            if (level == 0) "TRUCOS / NIVEL 0" else "NIVEL " + level,
                            keepTogetherIfFits = true))
                        val spellsHere = data.spells.filter { it.spell.level == level }
                        if (spellsHere.isEmpty()) {
                            // Explicitly blank PAPER row with a preparation checkbox,
                            // not a fabricated spell or slot.
                            add(PcSheetV11FlowItem("spell-handwritten:" + level,
                                "[ ] ______________________", keepTogetherIfFits = true))
                        }
                        spellsHere.forEach { entry ->
                            val sourceInfo = entry.sources.joinToString(" / ") { association ->
                                val sourceLabel = association.sourceName
                                    ?.takeIf { it.isNotBlank() }
                                    ?: association.sourceId.toString()
                                "[" + (if (association.prepared) "X" else " ") +
                                    "] " + sourceLabel
                            }
                            add(PcSheetV11FlowItem("quick-spell:" + entry.spell.id,
                                entry.spell.name +
                                    (if (sourceInfo.isBlank()) "" else " | " + sourceInfo),
                                keepTogetherIfFits = true))
                        }
                    }
                }
                // Share actual measured rows roughly across three columns rather
                // than leaving two physical columns completely blank.
                val initial = body.place(PcSheetV11FlowSection.QUICK_SPELLS,
                    quick, pageArea, 3, 12f)
                val balancedRows = maxOf(10,
                    (initial.cells.size + 2) / 3 + 2)
                val usedHeightPt = minOf(pageArea.heightPt,
                    balancedRows * body.metrics.lineHeightPt)
                val balancedArea = PcSheetV11RectPt(
                    pageArea.leftPt, pageArea.topPt - usedHeightPt,
                    pageArea.widthPt, usedHeightPt,
                )
                addSection("P4 - Conjuros", PcSheetV11FlowSection.QUICK_SPELLS,
                    quick, 3, area = balancedArea)
            }

            // Resource balances and ammunition are drawn only from persisted source
            // records. AMMUNITION is the explicit inventory-usage classification;
            // item names never serve as a guess or as fabricated state.
            val allResources = data.source.sheet.resources.sortedWith(
                compareBy<io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource> {
                    it.sortOrder
                }.thenBy { it.id.toString() },
            )
            val ammunitionIds = data.source.closure.inventoryUsage
                .filter {
                    it.kind ==
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.AMMUNITION
                }.map { it.itemId }.toSet()
            val ammunitionItems = data.source.sheet.inventoryItems
                .filter { it.id in ammunitionIds }
                .sortedWith(
                    compareBy<io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem> {
                        it.sortOrder
                    }.thenBy { it.id.toString() },
                )
            val ammunitionTracks = ammunitionItems.filter { it.quantity in 0..20 }
                .associate { item -> "ammo-track:" + item.id to item.quantity }
            val resourceLines = buildList {
                allResources.forEach { resource ->
                    val maximum = resource.maxValue?.let { "/" + it } ?: ""
                    val extras = buildList {
                        resource.recovery?.takeIf { it.isNotBlank() }?.let {
                            add("RECUPERA " + it)
                        }
                        resource.source?.takeIf { it.isNotBlank() }?.let {
                            add("FUENTE " + it)
                        }
                        resource.notes?.takeIf { it.isNotBlank() }?.let {
                            add("NOTAS " + it)
                        }
                    }
                    add(PcSheetV11FlowItem(
                        sourceKey = "resource:" + resource.id,
                        text = "RECURSO " + resource.name + " | RESTANTE " +
                            resource.currentValue + maximum +
                            (if (extras.isEmpty()) "" else " | " + extras.joinToString(" | ")),
                        keepTogetherIfFits = true,
                    ))
                }
                ammunitionItems.forEach { item ->
                    add(PcSheetV11FlowItem(
                        sourceKey = "ammo:" + item.id,
                        text = "MUNICIÓN " + item.name + " | CANTIDAD " + item.quantity +
                            (item.notes?.takeIf { it.isNotBlank() }?.let {
                                " | NOTAS " + it
                            } ?: ""),
                        keepTogetherIfFits = true,
                    ))
                    add(PcSheetV11FlowItem(
                        sourceKey = "ammo-track:" + item.id,
                        text = if (item.quantity in 0..20) "MARCAS:" else
                            "MARCAS: ____ / " + item.quantity,
                        keepTogetherIfFits = true,
                    ))
                }
            }
            // Compact MIXED C2: categories share the same measured two-column
            // page(s). Distinct headings are real rows, not dedicated blank pages.
            // UUID-backed records keep stable independent source keys and order.
            val mixedC2 = buildList {
                if (p1Unprinted.isNotEmpty()) {
                    add(PcSheetV11FlowItem("c2-heading:p1-overflow",
                        "PERSONAJE, HABILIDADES Y ACCIONES / CONTINUACIÓN",
                        keepTogetherIfFits = true))
                    addAll(p1Unprinted)
                }
                if (detachedSkills.isNotEmpty()) {
                    add(PcSheetV11FlowItem("c2-heading:skills",
                        "HABILIDADES Y REFERENCIAS", keepTogetherIfFits = true))
                    addAll(detachedSkills)
                }
                if (extraCombatData.isNotEmpty()) {
                    add(PcSheetV11FlowItem("c2-heading:combat",
                        "ATAQUES, NOTAS Y ESPACIOS REALES", keepTogetherIfFits = true))
                    addAll(extraCombatData)
                }
                if (resourceLines.isNotEmpty()) {
                    add(PcSheetV11FlowItem("c2-heading:resources",
                        "RECURSOS Y MUNICIONES", keepTogetherIfFits = true))
                    addAll(resourceLines)
                }
            }
            addSection("C2 - Habilidades, acciones y recursos",
                PcSheetV11FlowSection.TRAITS,
                mixedC2, columns = 2, ammunitionTracks = ammunitionTracks)

            // Reflow the exact P2-unprinted equipment/treasure suffixes into
            // FULL-HEIGHT C2 paired tables. Never redraw quantities already
            // printed on P2; keep every original label code point and source ID.
            fun remainingPairedRows(
                placed: PcSheetV11PairedTablePlaced,
            ): io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData {
                val rows = placed.data.rows.mapNotNull { row ->
                    val prefixParts = placed.labelFlow.fragments.filter {
                        it.localPageIndex == 1 && it.sourceKey == row.sourceKey
                    }.sortedWith(compareBy({ it.columnIndex }, { it.startOffset }))
                    val printedPrefix = prefixParts.joinToString("") { it.text }
                    require(row.label.startsWith(printedPrefix)) {
                        "P2 equipment must preserve each source label prefix exactly."
                    }
                    if (printedPrefix.length == row.label.length) null
                    else row.copy(label = row.label.substring(printedPrefix.length))
                }
                return placed.data.copy(rows = rows)
            }

            val fullC2PairedArea = PcSheetV11RectPt(42f, 76f, 528f, 626f)
            for ((label, original) in listOf(
                "EQUIPO" to inventoryPlaced,
                "TESORO" to treasurePlaced,
            )) {
                val remaining = remainingPairedRows(original)
                if (remaining.rows.isEmpty()) continue
                val expanded = PcSheetV11PairedTableComposer.place(
                    remaining, fullC2PairedArea, 12f, 36f, 6f,
                    body.metrics.lineHeightPt, body.width,
                )
                val amountsAlreadyOnP2 = original.quantities.asSequence()
                    .filter { it.localPageIndex == 1 }
                    .map { it.sourceKey }.toSet()
                for (localPage in 1..expanded.localPageCount) {
                    val page = PDPage(PDRectangle.LETTER)
                    pdf.addPage(page)
                    physicalSectionKeys += "C2 - Equipo y tesoro"
                    PDPageContentStream(pdf, page).use { stream ->
                        drawFrameAndHeader(stream,
                            "C2 - Equipo y tesoro / " + label + " / " + localPage)
                        drawPaired(stream, label + " / CANTIDAD",
                            fullC2PairedArea, expanded, localPage, amountsAlreadyOnP2,
                            compactC2 = true)
                    }
                }
            }

            fun unprintedSuffix(
                inputs: List<PcSheetV11FlowItem>,
                printedOnP2: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PlacedFlow,
            ): List<PcSheetV11FlowItem> = inputs.mapNotNull { item ->
                val consumed = printedOnP2.cells.asSequence()
                    .filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    .maxOfOrNull { it.sourceEndExclusive } ?: 0
                val originalPrefix = printedOnP2.cells
                    .filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    .joinToString("") { it.sourceText }
                require(originalPrefix == item.text.substring(0, consumed)) {
                    "P2 must preserve the exact prefix of " + item.sourceKey
                }
                if (consumed == item.text.length) null
                else item.copy(text = item.text.substring(consumed))
            }

            val remainingTraits = unprintedSuffix(traitLines, traitsPlaced)
            addSection(
                "C2 - Rasgos / continuación", PcSheetV11FlowSection.TRAITS,
                remainingTraits, columns = 2,
            )

            val allSpecialFields = specialPlaced.sources.map {
                PcSheetV11FlowItem(it.sourceKey, it.text, keepTogetherIfFits = true)
            }
            val remainingSpecials = unprintedSuffix(allSpecialFields, specialPlaced.body)
            val marksNeeded = data.specialEquipment.filter { item ->
                specialPlaced.body.cells.none { cell ->
                    cell.localPageIndex == 1 &&
                        cell.sourceKey == "special:" + item.sourceId + ":name"
                }
            }.associate { item ->
                ("special:" + item.sourceId + ":name") to item.active
            }
            addSection(
                "C2 - Equipo especial / continuación",
                PcSheetV11FlowSection.SPECIAL_EQUIPMENT,
                remainingSpecials, columns = 1,
                area = PcSheetV11RectPt(42f, 76f, 507f, 626f),
                specialMarks = marksNeeded,
            )

            addSection("C-N3 - Notas digitales", PcSheetV11FlowSection.DIGITAL_NOTES,
                texts.digitalNotes, 1)
            data.spellbookEntries?.takeIf { it.isNotEmpty() }?.let { spells ->
                // The only optional annex, with real two-column cards. All spell
                // content remains source-backed; no fabricated school or rules.
                val cards = spells.map { entry ->
                    val spell = entry.spell
                    val components = buildList {
                        if (spell.verbal) add("V")
                        if (spell.somatic) add("S")
                        if (spell.material) add("M")
                    }.joinToString("/")
                    val sourceLines = entry.sources.map { association ->
                        "FUENTE " + (association.sourceName?.takeIf { it.isNotBlank() }
                            ?: association.sourceId.toString()) +
                            " | " + (if (association.prepared) "PREPARADO" else "SIN PREPARAR") +
                            (association.saveDc?.let { " | CD " + it } ?: "") +
                            (association.spellAttackModifier?.let { " | ATAQUE " + it } ?: "")
                    }
                    val lines = buildList {
                        add("NIVEL " + spell.level + " | " + spell.name)
                        add("LANZAMIENTO " + spell.castingTime + " | ALCANCE " + spell.rangeText)
                        add("COMPONENTES " + components +
                            (spell.materialText?.takeIf { it.isNotBlank() }?.let {
                                " | MATERIAL " + it
                            } ?: ""))
                        add("DURACIÓN " + spell.duration +
                            (if (spell.concentration) " | CONCENTRACIÓN" else "") +
                            (if (spell.ritual) " | RITUAL" else ""))
                        addAll(sourceLines)
                        add("DESCRIPCIÓN:")
                        add(spell.description)
                        spell.notes?.takeIf { it.isNotBlank() }?.let { add("NOTAS: " + it) }
                    }
                    PcSheetV11FlowItem(
                        sourceKey = "book:" + spell.id,
                        text = lines.joinToString("\n"),
                        keepTogetherIfFits = true,
                    )
                }
                val contentFlow = body.place(
                    PcSheetV11FlowSection.SPELLBOOK, cards, pageArea, 2, 12f,
                )
                require(contentFlow.sourceFlow.localPageCount > 0)
                val firstPagesByKey = contentFlow.cells.groupBy { it.sourceKey }
                    .mapValues { (_, cells) -> cells.minOf { it.localPageIndex } }
                require(firstPagesByKey.size == cards.size) {
                    "Every real spell must have a distinct physical book entry."
                }
                val pagesBeforeIndex = pdf.numberOfPages
                fun buildIndex(indexPageCount: Int): List<PcSheetV11FlowItem> =
                    spells.map { entry ->
                        val key = "book:" + entry.spell.id
                        val relativePage = firstPagesByKey.getValue(key)
                        PcSheetV11FlowItem(
                            "index:" + entry.spell.id,
                            "NIVEL " + entry.spell.level + " | " + entry.spell.name +
                                " | PÁGINA " +
                                (pagesBeforeIndex + indexPageCount + relativePage),
                            keepTogetherIfFits = true,
                        )
                    }

                // Page numbers are global PHYSICAL pages. If a long index spills
                // onto multiple pages, converge on its measured page count, never
                // guess the next physical page from a fixed section number.
                var indexPageCount = 1
                repeat(8) {
                    val measured = body.place(
                        PcSheetV11FlowSection.SPELLBOOK,
                        buildIndex(indexPageCount),
                        pageArea, 2, 12f,
                    ).sourceFlow.localPageCount
                    require(measured > 0)
                    if (measured == indexPageCount) return@repeat
                    indexPageCount = measured
                }
                val indexEntries = buildIndex(indexPageCount)
                val finalIndexFlow = body.place(
                    PcSheetV11FlowSection.SPELLBOOK, indexEntries, pageArea, 2, 12f,
                )
                require(finalIndexFlow.sourceFlow.localPageCount == indexPageCount) {
                    "Physical spellbook index pagination did not converge."
                }
                require(indexEntries.size == spells.size)
                addSection("Índice del Libro de Conjuros",
                    PcSheetV11FlowSection.SPELLBOOK, indexEntries, 2)
                require(pdf.numberOfPages == pagesBeforeIndex + indexPageCount)
                addSection("Libro de conjuros",
                    PcSheetV11FlowSection.SPELLBOOK, cards, 2)
                require(pdf.numberOfPages ==
                    pagesBeforeIndex + indexPageCount + contentFlow.sourceFlow.localPageCount)
            }

            val finalPage = PDPage(PDRectangle.LETTER)
            pdf.addPage(finalPage)
            physicalSectionKeys += "NOTAS-FINAL"
            PDPageContentStream(pdf, finalPage).use { stream ->
                drawFrameAndHeader(stream, "NOTAS", lastHandwrittenPage = true)
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
            require(physicalSectionKeys.size == pdf.numberOfPages) {
                "Every real PDF page must have exactly one recorded V11 role."
            }
            require(physicalSectionKeys.last() == "NOTAS-FINAL")
            val totalPages = pdf.numberOfPages
            val physicalP1 = physicalSectionKeys.indexOfFirst {
                it.startsWith("P1 - ")
            } + 1
            val physicalP2 = physicalSectionKeys.indexOfFirst {
                it == "P2 - Equipo y tesoro"
            } + 1
            require(physicalP1 > 0 && physicalP2 > 0)
            val mixedC2Key = "C2 - Habilidades, acciones y recursos"
            val c2FromP1 = physicalSectionKeys.indexOfFirst { it == mixedC2Key }
                .takeIf { it >= 0 }?.plus(1)
            fun isFromP2(key: String): Boolean =
                key == "C2 - Equipo y tesoro" ||
                    key == "C2 - Rasgos / continuación" ||
                    key == "C2 - Equipo especial / continuación"
            val c2FromP2 = physicalSectionKeys.indexOfFirst { isFromP2(it) }
                .takeIf { it >= 0 }?.plus(1)
            // A C2 page never points at P2 merely because it happens to be a
            // continuation: derive the correct base-page role from its module.
            for (index in 0 until totalPages - 1) {
                val key = physicalSectionKeys[index]
                val physicalPage = index + 1
                val sectionPages = physicalSectionKeys.indices
                    .filter { physicalSectionKeys[it] == key }
                    .map { it + 1 }
                val position = sectionPages.indexOf(physicalPage)
                require(position >= 0)
                val prev = sectionPages.getOrNull(position - 1)
                val next = sectionPages.getOrNull(position + 1)
                val parent = when {
                    key == mixedC2Key -> "P1" to physicalP1
                    isFromP2(key) -> "P2" to physicalP2
                    else -> null
                }
                val link = when {
                    key.startsWith("P1 - ") && c2FromP1 != null ->
                        "CONTINÚA EN C2 PÁG. " + c2FromP1
                    key.startsWith("P2 - ") && c2FromP2 != null ->
                        "CONTINÚA EN C2 PÁG. " + c2FromP2
                    parent != null && prev == null ->
                        "VIENE DE " + parent.first + " PÁG. " + parent.second +
                            (next?.let { " | SIG. " + it } ?: "")
                    else -> listOfNotNull(
                        prev?.let { "ANT. " + it },
                        next?.let { "SIG. " + it },
                    ).joinToString(" | ")
                }
                PDPageContentStream(
                    pdf, pdf.getPage(index), PDPageContentStream.AppendMode.APPEND,
                    true, true,
                ).use { stream ->
                    if (link.isNotEmpty()) {
                        draw(stream, link, 42f, 35f, false)
                    }
                    val label = "HOJA " + physicalPage + " / " + totalPages
                    val x = 570f - body.width.measurePt(label)
                    require(x >= 292f)
                    draw(stream, label, x, 35f, false)
                }
            }
            pdf.save(target)
        }
    }
}
