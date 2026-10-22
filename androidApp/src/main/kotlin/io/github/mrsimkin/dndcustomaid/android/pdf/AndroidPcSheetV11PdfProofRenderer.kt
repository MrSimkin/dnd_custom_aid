package io.github.mrsimkin.dndcustomaid.android.pdf

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowItem
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableComposer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedAmountRouting
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTablePlaced
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11MeasuredFlowPlacer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TextFlow
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TextColumn
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSourceMapper
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ColumnGrid
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11CompactFieldRouter
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11RectPt
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticData
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Typeface
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetV11PrintableText
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
            val headerNameRoute =
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11HeaderNameRouter.route(
                    sourceName = source.name,
                    availableWidthPt = 406f, // Header text x154..560, inside its 422pt band.
                    width = measurer.resolve(PcSheetV11Typeface.LABEL, 12f).width,
                )
            val texts = PcSheetV11FlowSourceMapper.from(data)
            val pageArea = PcSheetV11RectPt(42f, 76f, 528f, 626f)
            // Physical page roles are registered exactly when a PDFBox page is
            // created. Footers and C2 backlinks are resolved only when the final
            // document length is known: no section-local page guesses.
            val physicalSectionKeys = mutableListOf<String>()
            // A mixed C2 can now carry real P2 trait fragments alongside P1
            // sources. Track actual physical pages; never infer a P2 backlink
            // from a generic C2 heading or a section-local ordinal.
            val mixedC2TraitPages = mutableSetOf<Int>()
            // P1 must link to mixed C2 ONLY when that physical section
            // actually contains P1-owned source lines, not P2-only traits.
            val mixedC2P1Pages = mutableSetOf<Int>()

            fun draw(stream: PDPageContentStream, text: String, x: Float, y: Float, label: Boolean) {
                if (text.isEmpty()) return
                stream.beginText()
                stream.setFont(if (label) headerPdfFont else bodyPdfFont, if (label) 12f else 9f)
                stream.newLineAtOffset(x, y)
                stream.showText(pcSheetV11PrintableText(text))
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
                // Exterior V11 double frame: exact real PDF vectors from ALL
                // EIGHT immutable approved US-Letter golden documents. These
                // are stable on every physical page (including C2 and NOTAS).
                // PyMuPDF round-trip bounds: outer x26.362..585.638,
                // y24.945..767.055; inner x30.614..581.386,
                // y28.913..763.087. Preserve all content placements.
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.95f)
                stream.addRect(26.3622f, 24.9449f, 559.2756f, 742.1102f)
                stream.stroke()
                stream.setStrokingColor(Color(191, 211, 222))
                stream.setLineWidth(0.38f)
                stream.addRect(30.6142f, 28.9134f, 550.7716f, 734.1732f)
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

                draw(stream, headerNameRoute.paperName, 154f, 747f, true)
                draw(stream, title, 42f, 724f, true)
                if (!lastHandwrittenPage) {
                    draw(stream, "PRUEBA INTERNA V11 - NO APROBADA", 42f, 46f, false)
                }
            }

            // For B only, the golden's 6+2/6+3 uses compact 25pt
            // real-attribute rows above a separate skill panel. A has its own
            // measured skill-under-ability heights and is never force-shrunk.
            val compactBAttributes = data.request.variant ==
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.B &&
                data.attributes.size > 6
            // Source-backed A skills cannot force the SIX mandatory
            // attribute rows beneath P1's approved printable floor. Preserve
            // the original geometry while all rows fit; when they do not,
            // the shared cap defers only excess source skills to mixed C2.
            val aSkillRowCaps = if (data.request.variant ==
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A
            ) {
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11AttributeSkillBudget
                    .plan(
                        data.attributes.take(6).map { attribute ->
                            data.skills.count { it.ability == attribute.reference }
                        },
                        areaTopPt = 577f,
                        safeBottomPt = 110f,
                    ).mapIndexed { index, cap ->
                        data.attributes[index].reference to cap
                    }.toMap()
            } else emptyMap()
            fun p1AttributeHeight(ability: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Attribute): Float {
                val a=data.request.variant ==
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A
                return if(a) io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11AttributeSkillBudget
                    .heightPt(
                        aSkillRowCaps[ability.reference] ?: minOf(6,
                            data.skills.count { it.ability == ability.reference }),
                    )
                    else if(compactBAttributes) 25f else 38f
            }
            // Variant A NEVER uses B's fixed 25pt compression: a skill group
            // is only printed if the entire genuine attribute-height fits.
            // The first unfit attribute and all following ones remain in C2.
            val p1PrintedAttributeCount = when {
                compactBAttributes -> minOf(9, data.attributes.size)
                data.request.variant ==
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A -> {
                    var count = 6
                    var remainingBottom = 577f - data.attributes.take(6)
                        .sumOf { p1AttributeHeight(it).toDouble() }.toFloat()
                    while (count < minOf(9, data.attributes.size)) {
                        val extraHeight = p1AttributeHeight(data.attributes[count])
                        if (remainingBottom - extraHeight <= 110f) break
                        remainingBottom -= extraHeight
                        count++
                    }
                    count
                }
                else -> 6
            }
            fun p1AttributeTop(i:Int):Float = 577f -
                data.attributes.take(i).sumOf { p1AttributeHeight(it).toDouble() }.toFloat()

            // Standard enum IDs stay English in the model; only owner-approved
            // V11 P1 PAPER labels are translated. Custom names remain literal.
            fun approvedAbilityName(name: String): String = when (name) {
                "STRENGTH" -> "FUERZA"
                "DEXTERITY" -> "DESTREZA"
                "CONSTITUTION" -> "CONSTITUCIÓN"
                "INTELLIGENCE" -> "INTELIGENCIA"
                "WISDOM" -> "SABIDURÍA"
                "CHARISMA" -> "CARISMA"
                else -> name
            }
            // C218: the approved V11 paper displays true positive/zero
            // ability and skill modifiers with an explicit plus, as P1 does.
            // This is a display-only formatting of the persisted Int.
            fun signedC2(value: Int): String =
                if (value >= 0) "+" + value else value.toString()

            fun approvedAbilityAbbreviation(raw: String): String = when (raw.uppercase()) {
                "STR" -> "FUE"
                "DEX" -> "DES"
                "CON" -> "CON"
                "INT" -> "INT"
                "WIS" -> "SAB"
                "CHA" -> "CAR"
                else -> raw.uppercase().take(3)
            }
            fun approvedSkillName(skill:
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Skill
            ): String = when (skill.builtInKey?.name) {
                "ACROBATICS" -> "Acrobacias"
                "ANIMAL_HANDLING" -> "Trato con Animales"
                "ARCANA" -> "Conoc. Arcano"
                "ATHLETICS" -> "Atletismo"
                "DECEPTION" -> "Engaño"
                "HISTORY" -> "Historia"
                "INSIGHT" -> "Perspicacia"
                "INTIMIDATION" -> "Intimidación"
                "INVESTIGATION" -> "Investigación"
                "MEDICINE" -> "Medicina"
                "NATURE" -> "Naturaleza"
                "PERCEPTION" -> "Percepción"
                "PERFORMANCE" -> "Interpretación"
                "PERSUASION" -> "Persuasión"
                "RELIGION" -> "Religión"
                "SLEIGHT_OF_HAND" -> "Juego de Manos"
                "STEALTH" -> "Sigilo"
                "SURVIVAL" -> "Supervivencia"
                else -> skill.name
            }
            fun drawP1Attributes(stream:PDPageContentStream) {
                val standards=data.attributes.take(p1PrintedAttributeCount)
                require(standards.size>=6 && p1AttributeTop(p1PrintedAttributeCount) >
                    (if (compactBAttributes) 328f else 110f))
                // Approved V11 P1 C2c: shared A/B Attributes blue header.
                // It fits the genuine 23pt strip BELOW portrait (PDF y605)
                // and ABOVE first source-backed ability row (PDF y577).
                // Decoration cannot move any ability, save or skill row.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.55f)
                stream.moveTo(46f, 579f)
                stream.lineTo(296f, 579f)
                stream.lineTo(300f, 583f)
                stream.lineTo(300f, 598f)
                stream.lineTo(296f, 602f)
                stream.lineTo(46f, 602f)
                stream.lineTo(42f, 598f)
                stream.lineTo(42f, 583f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                draw(stream,if(data.request.variant ==
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A)
                    "ATRIBUTOS · A" else "ATRIBUTOS · B",51f,586f,true)
                val k=0.5522848f
                standards.forEachIndexed { i,ability ->
                    val top=p1AttributeTop(i)
                    val bottom=p1AttributeTop(i+1)
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130,165,186))
                    stream.setLineWidth(0.6f)
                    stream.setNonStrokingColor(Color(238,247,251))
                    stream.addRect(42f,bottom,258f,top-bottom)
                    stream.fill()
                    stream.addRect(42f,bottom,258f,top-bottom)
                    stream.stroke()
                    stream.addRect(46f,top-(if(compactBAttributes) 22f else 27f),
                        47f,if(compactBAttributes) 18f else 22f)
                    stream.stroke()
                    val cx=116f
                    val cy=top-(if(compactBAttributes) 13f else 16f)
                    val rx=23f
                    val ry=if(compactBAttributes) 9f else 11f
                    stream.moveTo(cx+rx,cy)
                    stream.curveTo(cx+rx,cy+ry*k,cx+rx*k,cy+ry,cx,cy+ry)
                    stream.curveTo(cx-rx*k,cy+ry,cx-rx,cy+ry*k,cx-rx,cy)
                    stream.curveTo(cx-rx,cy-ry*k,cx-rx*k,cy-ry,cx,cy-ry)
                    stream.curveTo(cx+rx*k,cy-ry,cx+rx,cy-ry*k,cx+rx,cy)
                    stream.closePath()
                    stream.stroke()
                    stream.restoreGraphicsState()
                    val score=ability.score.toString()
                    val mod=if(ability.modifier>=0)"+"+ability.modifier else ability.modifier.toString()
                    val valueY=top-(if(compactBAttributes) 17f else 20f)
                    draw(stream,score,69.5f-body.width.measurePt(score)/2f,valueY,false)
                    draw(stream,mod,cx-body.width.measurePt(mod)/2f,valueY,false)
                    draw(stream,approvedAbilityName(ability.name),145f,
                        top-(if(compactBAttributes) 11f else 14f),false)
                    stream.beginText()
                    stream.setFont(bodyPdfFont,7f)
                    // Golden B: label-to-box gap 17.85pt; previous real App gap 20.18pt.
                    // Move the label only 2.3pt right and 2.1pt upward relative
                    // to its attribute name; leave the 7pt box, value and row intact.
                    val saveY=top-(if(compactBAttributes) 23f else 25f)
                    // Position only the label; saveY remains the untouched
                    // checkbox/value baseline from the verified App PDF.
                    val savingLabelY=saveY+2.1f
                    stream.newLineAtOffset(217.3f,savingLabelY)
                    stream.showText("SALVACIÓN")
                    stream.endText()
                    // Approved V11 paper saving-throw mark. Always blank:
                    // proficiency/state cannot be inferred from the save total.
                    // Genuine vector 7×7pt square stays between the label
                    // (ending before x250) and its actual value (starts x287+).
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(80, 122, 151))
                    stream.setLineWidth(0.6f)
                    stream.addRect(270f, saveY - 1f, 7f, 7f)
                    stream.stroke()
                    stream.restoreGraphicsState()
                    ability.savingThrowTotal?.let {
                        val value=if(it>=0)"+"+it else it.toString()
                        draw(stream,value,296f-body.width.measurePt(value),saveY,false)
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
                data.attributes.take(p1PrintedAttributeCount).forEachIndexed { attrIndex, attr ->
                    val fitsHere = mutableListOf<Pair<Int, io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Skill>>()
                    data.skills.forEachIndexed { skillIndex, skill ->
                        if (skill.ability == attr.reference && skillIndex !in printedSkillIndices &&
                            fitsHere.size < (aSkillRowCaps[attr.reference] ?: 6) &&
                            skill.name.isNotBlank()
                        ) {
                            val total = skill.total?.let { if (it >= 0) "+" + it else it.toString() } ?: ""
                            val remaining = 258f - 34f - skillFont.width.measurePt(total) - 9f
                            if (skillFont.width.measurePt(approvedSkillName(skill)) <= remaining) {
                                fitsHere += skillIndex to skill
                                printedSkillIndices += skillIndex
                            }
                        }
                    }
                    groupedSkills[attrIndex] = fitsHere
                }
            }

            fun drawP1GroupedSkills(stream:PDPageContentStream) {
                if(!variantA)return
                groupedSkills.forEach { (attrIndex,rows) ->
                    val top=p1AttributeTop(attrIndex)
                    rows.forEachIndexed { idx,(_,skill) ->
                        val y=top-36f-idx*10.5f
                        require(y>p1AttributeTop(attrIndex+1)+1f)
                        stream.saveGraphicsState()
                        stream.setStrokingColor(Color(130,165,186))
                        stream.addRect(47f,y-2f,7f,7f)
                        stream.stroke()
                        stream.restoreGraphicsState()
                        fun put(value:String,x:Float){
                            if(value.isNotEmpty()){
                                stream.beginText()
                                stream.setFont(bodyPdfFont,7f)
                                stream.newLineAtOffset(x,y)
                                stream.showText(pcSheetV11PrintableText(value))
                                stream.endText()
                            }
                        }
                        put(when(skill.training) {
                            io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.NONE -> ""
                            io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> "P"
                            io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> "E"
                        },48f)
                        put(approvedSkillName(skill),60f)
                        skill.total?.let {
                            val v=if(it>=0)"+"+it else it.toString()
                            put(v,294f-skillFont.width.measurePt(v))
                        }
                    }
                }
                draw(stream,"HABILIDADES BAJO ATRIBUTO",44f,92f,false)
            }

            // Variant B has a physically separate skills area, never a prose
            // list inside the identity field. Source offsets stay attached to
            // their original skills so any measured overflow survives in C2.
            val bSkillEntries = if (!variantA) data.skills.withIndex()
                .filter { it.value.builtInKey != null }
                .map { (index, skill) ->
                    val abbreviation = data.attributes.firstOrNull {
                        it.reference == skill.ability
                    }?.abbreviation?.let { approvedAbilityAbbreviation(it) }
                    val training = when (skill.training) {
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.NONE -> ""
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> " P"
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> " E"
                    }
                    PcSheetV11FlowItem(
                        "skill:" + index,
                        approvedSkillName(skill) + (abbreviation?.let { " (" + it + ")" } ?: "") +
                            (skill.total?.let { " " + (if (it >= 0) "+" else "") + it } ?: "") +
                            training,
                        keepTogetherIfFits = true,
                    )
                }
            else emptyList()
            // Reserve 11pt for each approved B paper skill checkbox. Both
            // physical columns are genuinely measured: x60..167 and x186..293
            // (19pt between them houses the separator and second checkbox).
            // This never adds a fake skill or clips an overlong source label.
            val bSkillsArea = PcSheetV11RectPt(60f,220f,233f,95f)
            val bSkillsPlaced = if(!variantA) PcSheetV11MeasuredFlowPlacer.place(
                PcSheetV11FlowSection.TRAITS,bSkillEntries,bSkillsArea,2,19f,
                skillFont.metrics.lineHeightPt,skillFont.width,
            ) else null
            fun drawP1BIndependentSkills(stream: PDPageContentStream) {
                if (variantA) return
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.55f)
                stream.moveTo(46f, 330f)
                stream.lineTo(296f, 330f)
                stream.lineTo(300f, 334f)
                stream.lineTo(300f, 344f)
                stream.lineTo(296f, 348f)
                stream.lineTo(46f, 348f)
                stream.lineTo(42f, 344f)
                stream.lineTo(42f, 334f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                draw(stream,"HABILIDADES  P=COMPETENCIA  E=PERICIA",51f,335f,true)
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130,165,186))
                stream.setLineWidth(0.6f)
                stream.addRect(42f,105f,258f,223f)
                stream.stroke()
                stream.moveTo(171f,105f)
                stream.lineTo(171f,319f)
                stream.stroke()
                stream.restoreGraphicsState()
                // The approved B skills cartela remains useful on paper
                // BELOW all real skill records. Its layout placer is confined
                // to y=220..315; this lower region y=125..218 cannot erase,
                // crowd or duplicate any source skill even with long entries.
                // Two separate ruled columns mirror the frozen golden; no
                // unchecked state or digital NOTAS record is invented.
                val blankPaperTop = bSkillsArea.bottomPt - 15f
                val blankPaperFloor = 105f + 20f
                val blankPaperStep = 16f
                var ruleY = blankPaperTop
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130,165,186))
                stream.setLineWidth(0.45f)
                stream.setNonStrokingColor(Color(238,247,251))
                while (ruleY >= blankPaperFloor) {
                    for ((xStart,xEnd) in listOf(47f to 167f,176f to 295f)) {
                        // Alternate pale-blue usable handwriting bands, never
                        // outside the skills frame or across its divider.
                        stream.addRect(xStart,ruleY-1f,xEnd-xStart,14f)
                        stream.fill()
                        stream.moveTo(xStart,ruleY)
                        stream.lineTo(xEnd,ruleY)
                        stream.stroke()
                    }
                    ruleY -= blankPaperStep
                }
                stream.restoreGraphicsState()
                bSkillsPlaced?.cells?.filter{it.localPageIndex==1}?.forEach{cell->
                    if(cell.drawableText.isNotEmpty()){
                        val baseline = skillFont.metrics.baselineIn(cell.cellPt)
                        if (cell.sourceStartOffset == 0) {
                            // One genuine blank 7×7pt square per SOURCE skill,
                            // never one per wrapped fragment. P/E text is only
                            // a source-backed training label, not a checked state.
                            stream.saveGraphicsState()
                            stream.setStrokingColor(Color(130,165,186))
                            stream.setLineWidth(0.6f)
                            stream.addRect(cell.cellPt.leftPt - 11f,
                                baseline - 2f, 7f, 7f)
                            stream.stroke()
                            stream.restoreGraphicsState()
                        }
                        stream.beginText()
                        stream.setFont(bodyPdfFont,7f)
                        stream.newLineAtOffset(cell.cellPt.leftPt, baseline)
                        stream.showText(pcSheetV11PrintableText(cell.drawableText))
                        stream.endText()
                    }
                }
            }
            val bSkillOverflow = bSkillEntries.mapNotNull { item ->
                val consumed = bSkillsPlaced?.cells?.asSequence()
                    ?.filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    ?.maxOfOrNull { it.sourceEndExclusive } ?: 0
                require(consumed in 0..item.text.length)
                if (consumed == item.text.length) null
                else item.copy(text = item.text.substring(consumed))
            }

            // Custom-attribute skills belong directly to their persisted
            // attribute in mixed C2, in either A or B. Other detached skills
            // retain their conventional three-letter reference.
            val customAttributeReferences = data.attributes.drop(6)
                .map { it.reference }.toSet()
            val detachedSkills = if (variantA) data.skills.withIndex()
                .filter { it.index !in printedSkillIndices &&
                    it.value.ability !in customAttributeReferences }
                .map { (index, skill) ->
                    val abbreviation = data.attributes.firstOrNull {
                        it.reference == skill.ability
                    }?.abbreviation?.let { approvedAbilityAbbreviation(it) }
                    PcSheetV11FlowItem(
                        "detached-skill:" + index,
                        approvedSkillName(skill) + (abbreviation?.let { " (" + it + ")" } ?: "") +
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
            val attackLabelWidth = 121f
            val attackBonusWidth = 36f
            val attackDamageWidth = 258f - attackLabelWidth - attackBonusWidth
            fun p1AttackLabel(entry:io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry):String {
                val complete=entry.name+(entry.rangeText?.takeIf{it.isNotBlank()}
                    ?.let{" / "+it}?:"")
                return if(body.width.measurePt(complete)<=attackLabelWidth-10f)
                    complete else entry.name
            }
            // Fill the six genuine P1 attack rows with the first SIX source-order
            // entries which physically FIT, not only matches among the first
            // six source entries. All other UUIDs remain deferred to C2.
            val p1Attacks=attackRows.filter{entry->
                // P1 cells are single physical rows. A measured "width" cannot
                // prove that a source with CR/LF/TAB is safe to print there.
                // Defer the COMPLETE UUID-backed attack to measured C2 rather
                // than flattening, clipping, or dropping a source segment.
                val hasPhysicalBreak = listOfNotNull(
                    entry.name, entry.rangeText, entry.damageEffect,
                ).any { field -> field.any { ch ->
                    ch == '\r' || ch == '\n' || ch == '\t'
                } }
                val bonus=entry.attackModifier?.let{if(it>=0)"+"+it else it.toString()}?:""
                !hasPhysicalBreak &&
                    body.width.measurePt(p1AttackLabel(entry))<=attackLabelWidth-10f &&
                    body.width.measurePt(bonus)<=attackBonusWidth-8f &&
                    body.width.measurePt(entry.damageEffect)<=attackDamageWidth-10f
            }.take(6)
            val p1AttackIds = p1Attacks.map { it.id }.toSet()

            fun drawP1Attacks(stream: PDPageContentStream) {
                val left = 312f
                val top = 491f
                val bottom = 392f
                val b1 = left + attackLabelWidth
                val b2 = b1 + attackBonusWidth
                // Frozen V11 attacks use a clipped blue heading. This narrow
                // source-neutral adaptation fills ONLY the existing 12pt
                // three-column header row (x312..570, top-origin y301..313).
                // There is no 23pt clearance for a separate title cartela;
                // never move real attacks or the P1 traits to fake one.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(217, 236, 245))
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.55f)
                val headerBottom = top - 12f
                val cut = 3f
                stream.moveTo(left + cut, headerBottom)
                stream.lineTo(left + 258f - cut, headerBottom)
                stream.lineTo(left + 258f, headerBottom + cut)
                stream.lineTo(left + 258f, top - cut)
                stream.lineTo(left + 258f - cut, top)
                stream.lineTo(left + cut, top)
                stream.lineTo(left, top - cut)
                stream.lineTo(left, headerBottom + cut)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // V11 approved blue-mist table grammar: alternate four VERY light
                // attack-body rows. Keep existing coordinates, table borders,
                // source-backed labels/bonuses/damage and all page breaks intact.
                // Tint only inside the original 7 x 12pt table body, not header.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(244, 250, 253))
                for (rowIndex in 0..6 step 2) {
                    val rowTop = headerBottom - rowIndex * 12f
                    stream.addRect(left + 1f, rowTop - 12f, 256f, 12f)
                    stream.fill()
                }
                stream.restoreGraphicsState()
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.7f)
                stream.addRect(left, bottom, 258f, top - bottom)
                stream.stroke()
                for (x in listOf(b1, b2)) {
                    stream.moveTo(x, bottom)
                    stream.lineTo(x, top)
                    stream.stroke()
                }
                for (row in 1..7) {
                    val y = top - row * 12f
                    stream.moveTo(left, y)
                    stream.lineTo(left + 258f, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
                draw(stream, "ATAQUE / RANGO", left + 4f, top - 10f, false)
                draw(stream, "BONO", b1 + 3f, top - 10f, false)
                draw(stream, "DAÑO / EFECTO", b2 + 3f, top - 10f, false)
                p1Attacks.forEachIndexed { index, entry ->
                    val y = top - 22f - index * 12f
                    draw(stream,p1AttackLabel(entry),left+4f,y,false)
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

            // P1 spell-slot cells have fixed physical widths. Preserve every
            // source value without shrinking or overprinting adjacent levels:
            // route outlier counters to the existing measured P1-owned C2.
            val overflowP1SpellSlots = data.spellSlots.filter { slots ->
                slots.level in 1..9 &&
                    (body.width.measurePt(slots.totalSlots.toString()) > 22f ||
                        body.width.measurePt(slots.spentSlots.toString()) > 29f)
            }
            val overflowP1SpellLevels = overflowP1SpellSlots.map { it.level }.toSet()

            fun drawP1Spellcasting(stream:PDPageContentStream) {
                // Approved frozen B P1: LANZAMIENTO uses a filled/stroked
                // 8-segment clipped cartela (302x23pt in the golden).
                // Genuine App P1 has only a 20pt free strip between the
                // traits bottom (top-origin y491) and spell panel (y511).
                // Keep 1pt clearance on each side with a 258x18pt blue cartela at
                // PDF x312..570, y282..300; do not move any source data,
                // spell counters, panels, or page breaks.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231,242,247))
                stream.setStrokingColor(Color(38,75,104))
                stream.setLineWidth(0.55f)
                stream.moveTo(316f,282f)
                stream.lineTo(566f,282f)
                stream.lineTo(570f,286f)
                stream.lineTo(570f,296f)
                stream.lineTo(566f,300f)
                stream.lineTo(316f,300f)
                stream.lineTo(312f,296f)
                stream.lineTo(312f,286f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // Real rendered P1 review found the first letter touching the
                // left/top stroke. Inset the title 9pt and lower its ink
                // 2pt within the 18pt strip, no change to spell data.
                draw(stream,"LANZAMIENTO DE CONJUROS",321f,286f,true)
                val actual=data.spellSlots.associateBy{it.level}
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130,165,186))
                stream.setLineWidth(0.55f)
                stream.setNonStrokingColor(Color(238,247,251))
                stream.addRect(312f,154f,258f,127f)
                stream.fill()
                stream.addRect(312f,154f,258f,127f)
                stream.stroke()
                stream.restoreGraphicsState()
                val magical=when(source.spellcastingAbility){
                    SpellcastingAbility.STRENGTH->"FUE"
                    SpellcastingAbility.DEXTERITY->"DES"
                    SpellcastingAbility.CONSTITUTION->"CON"
                    SpellcastingAbility.INTELLIGENCE->"INT"
                    SpellcastingAbility.WISDOM->"SAB"
                    SpellcastingAbility.CHARISMA->"CAR"
                    SpellcastingAbility.OTHER,SpellcastingAbility.NONE->""
                }
                listOf("APTITUD" to magical,
                    "CD SALVACIÓN" to(source.spellSaveDc?.toString()?:""),
                    "ATAQUE MÁGICO" to (source.spellAttackModifier?.let{
                        if(it>=0)"+"+it else it.toString()
                    }?:"")).forEachIndexed{idx,(label,value)->
                        val x=314f+idx*85f
                        stream.beginText()
                        stream.setFont(bodyPdfFont,7f)
                        stream.newLineAtOffset(x,270f)
                        stream.showText(label)
                        stream.endText()
                        if(value.isNotEmpty())draw(stream,value,x,255f,false)
                    }
                draw(stream,"ESPACIOS",315f,243f,false)
                draw(stream,"GASTADOS",407f,243f,false)
                (1..9).forEach{level->
                    val x=315f+((level-1)%3)*85f
                    val y=222f-((level-1)/3)*23f
                    draw(stream,"N"+level,x,y,false)
                    actual[level]?.let{v->
                        if (level in overflowP1SpellLevels) {
                            draw(stream,"VER C2",x+19f,y,false)
                        } else {
                        draw(stream,v.totalSlots.toString(),x+19f,y,false)
                        draw(stream,v.spentSlots.toString(),x+45f,y,false)
                        if(v.totalSlots in 1..3){
                            repeat(v.totalSlots){n->
                                val px=x+19f+n*12f
                                stream.addRect(px,y-12f,7f,7f)
                                stream.stroke()
                                if(n<v.spentSlots)draw(stream,"X",px+1f,y-10f,false)
                            }
                        }
                        }
                    }?:run{
                        stream.moveTo(x+19f,y-7f)
                        stream.lineTo(x+75f,y-7f)
                        stream.stroke()
                    }
                }
            }

            // Eight real combat cells are frozen at 121pt of usable value width.
            // Extreme multiclass hit dice (or other source numbers) move WHOLE to
            // the existing measured, bidirectionally referenced P1-owned C2.
            fun signedCombat(v: Int) = if (v >= 0) "+" + v else v.toString()
            val p1HitDice = source.classes.joinToString(" / ") {
                it.hitDiceRemaining.toString() + "d" + it.hitDieSides
            }
            val p1CombatSourceFields = listOf(
                "CA" to source.armorClass.toString(),
                "INICIATIVA" to signedCombat(source.initiativeModifier),
                "VELOCIDAD" to source.speed.toString(),
                "COMPETENCIA" to signedCombat(source.finalProficiencyBonus),
                "PV ACTUALES" to source.currentHp.toString(),
                "PV MÁXIMOS" to source.maxHp.toString(),
                "PV TEMP" to source.tempHp.toString(),
                "DADOS" to p1HitDice,
            )
            val p1CombatRoutes = p1CombatSourceFields.mapIndexed { index, (label, value) ->
                // Six narrow genuine source cards and two wider hit-point
                // cards. Oversized values retain their real source in C2.
                val cardWidth = if (index == 4 || index == 5) 129f else 86f
                PcSheetV11CompactFieldRouter.route(
                    "p1-combat:" + label, label, value,
                    cardWidth - 12f, body.width,
                )
            }
            val extraCombatData = buildList {
                addAll(p1CombatRoutes.mapNotNull { it.continuation })
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
                        if(attack.rangeText?.isNotBlank()==true &&
                            p1AttackLabel(attack)==attack.name) {
                            add(PcSheetV11FlowItem("combat:range:"+attack.id,
                                "ALCANCE "+attack.name+": "+attack.rangeText,
                                keepTogetherIfFits=true))
                        }
                        attack.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                            add(PcSheetV11FlowItem(
                                "combat:notes:" + attack.id,
                                "NOTAS DEL ATAQUE " + attack.name + ": " + notes,
                                keepTogetherIfFits = true,
                            ))
                        }
                    }
                }
                // Values too wide for the frozen 3x3 P1 cells remain complete
                // on C2 and are never mistaken for absent paper slots.
                overflowP1SpellSlots.forEach { slots ->
                    add(PcSheetV11FlowItem(
                        "spellcasting:slots:" + slots.level,
                        "ESPACIOS REALES NIVEL " + slots.level +
                            ": TOTAL " + slots.totalSlots +
                            ", GASTADOS " + slots.spentSlots,
                        keepTogetherIfFits = true,
                    ))
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



            // P1 is the FIRST physical occurrence of each Rasgo:
            // print ONLY name/category/source, never its description/notes.
            // P2/C2 continue the remaining original IDs without repeats.
            val p1Font = measurer.resolve(PcSheetV11Typeface.BODY, 7f)
            fun p1DrawSmall(stream: PDPageContentStream, content: String, x: Float, y: Float) {
                if (content.isEmpty()) return
                stream.beginText()
                stream.setFont(bodyPdfFont, 7f)
                stream.newLineAtOffset(x, y)
                stream.showText(pcSheetV11PrintableText(content))
                stream.endText()
            }
            // Fit complete identities in 12 genuine 10pt paper rows
            // (six per column). A long entry can use multiple rows; if it
            // cannot fit, start the P2 continuation with that SAME source ID.
            // Always select a source-order PREFIX to avoid reordering Rasgos.
            fun p1TraitWrap(label: String): List<String> {
                if (label.isBlank()) return emptyList()
                val lines = mutableListOf<String>()
                var current = ""
                for (word in label.split(' ').filter { it.isNotEmpty() }) {
                    if (p1Font.width.measurePt(word) > 121f) return emptyList()
                    val candidate = if (current.isEmpty()) word else "${current} $word"
                    if (p1Font.width.measurePt(candidate) <= 121f) {
                        current = candidate
                    } else {
                        if (current.isNotEmpty()) lines += current
                        current = word
                    }
                }
                if (current.isNotEmpty()) lines += current
                return lines
            }
            val p1TraitEntries = buildList<Triple<
                io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
                List<String>, Int>>() {
                var slot = 0
                for (trait in data.traits) {
                    val lines = p1TraitWrap(
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TraitSummary.text(trait)
                    )
                    if (lines.isEmpty() || lines.size > 6) break
                    if (slot < 6 && slot + lines.size > 6) slot = 6
                    if (slot + lines.size > 12) break
                    add(Triple(trait, lines, slot))
                    slot += lines.size
                }
            }
            val p1PrintedTraitIds = p1TraitEntries.map { it.first.id }.toSet()
            fun drawP1Traits(stream: PDPageContentStream) {
                val left = 312f
                val width = 258f
                val top = 382f
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.5f)
                stream.addRect(left, 301f, width, top - 301f)
                stream.stroke()
                stream.moveTo(441f, 301f)
                stream.lineTo(441f, top - 15f)
                stream.stroke()
                repeat(6) { row ->
                    val y = top - 21f - row * 10f
                    for (col in 0..1) {
                        val x = left + col * 129f
                        stream.moveTo(x + 3f, y - 3f)
                        stream.lineTo(x + 126f, y - 3f)
                        stream.stroke()
                    }
                }
                stream.restoreGraphicsState()
                // Owner V11 C2c: Rasgos heading has a clipped pale-blue band,
                // not floating text. Exactly the existing 14pt P1 title strip:
                // PDF x312..570/y368..382; row-1 begins at y361 untouched.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231,242,247))
                stream.setStrokingColor(Color(38,75,104))
                stream.setLineWidth(0.55f)
                stream.moveTo(315f,368f)
                stream.lineTo(567f,368f)
                stream.lineTo(570f,371f)
                stream.lineTo(570f,379f)
                stream.lineTo(567f,382f)
                stream.lineTo(315f,382f)
                stream.lineTo(312f,379f)
                stream.lineTo(312f,371f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // Golden heading font role: authentic embedded Barlow Condensed,
                // distinct from 7pt Fira source-value rows below.
                stream.beginText()
                stream.setFont(headerPdfFont,9f)
                stream.newLineAtOffset(left + 6f,top - 11f)
                stream.showText("RASGOS Y ATRIBUTOS")
                stream.endText()
                p1TraitEntries.forEach { (_, lines, firstSlot) ->
                    lines.forEachIndexed { offset, line ->
                        val slot = firstSlot + offset
                        p1DrawSmall(stream, line, left + 4f + (slot / 6) * 129f,
                            top - 21f - (slot % 6) * 10f)
                    }
                }
            }

            // Explicit AMMUNITION usage is required; names do not imply ammo.
            // Draw up to two P1 rows with groups of five paper-only marks.
            // Overflow and full source details stay in C2; never count marks twice.
            val p1AmmunitionIds = data.source.closure.inventoryUsage
                .filter { it.kind ==
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.AMMUNITION }
                .map { it.itemId }.toSet()
            val p1AmmoItems = data.source.sheet.inventoryItems
                // The fixed paper mark row is SINGLE-LINE. A source ammo
                // name containing CR/LF/TAB cannot be printed in this slot even
                // when a font-width measurement appears to fit. Its genuine
                // UUID/name/quantity remain in the existing measured C2 route.
                .filter { item ->
                    item.id in p1AmmunitionIds && item.quantity >= 0 &&
                        item.name.none { ch -> ch == '\r' || ch == '\n' || ch == '\t' } &&
                        p1Font.width.measurePt(item.name) <= 68f
                }
                .sortedWith(compareBy<
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem> {
                    it.sortOrder
                }.thenBy { it.id.toString() }).take(2)
            val p1AmmoIds = p1AmmoItems.map { it.id }.toSet()
            fun drawP1Ammunition(stream: PDPageContentStream) {
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.55f)
                stream.addRect(312f, 102f, 258f, 46f)
                stream.stroke()
                for (y in listOf(131f, 115f)) {
                    stream.moveTo(312f, y)
                    stream.lineTo(570f, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
                p1DrawSmall(stream, "MUNICIONES / MARCAS:", 316f, 137f)
                // D-0076 §3.39: no recorded/displayable ammunition still
                // reserves ONE logical paper entry with a blank name and
                // exactly 20 unmarked squares, grouped 5+5 in two subrows.
                // Do not invent a type, a quantity or a second ammunition ID.
                if (p1AmmoItems.isEmpty()) {
                    // Empty ammunition NAME is a writable PAPER baseline,
                    // not 12 black underscore glyphs impersonating a source.
                    // Draw it beside (never into) the 20 original grouped marks.
                    // Real ammunition titles and the contract's "____ / X"
                    // over-20 counter remain untouched.
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130, 165, 186))
                    stream.setLineWidth(0.55f)
                    stream.moveTo(316f, 120f)
                    stream.lineTo(377f, 120f)
                    stream.stroke()
                    repeat(20) { index ->
                        val column = index % 10
                        val x = 392f + column * 8f + (column / 5) * 4f
                        val y = if (index < 10) 119f else 103f
                        require(x + 6f <= 570f)
                        stream.addRect(x, y, 6f, 6f)
                        stream.stroke()
                    }
                    stream.restoreGraphicsState()
                }
                p1AmmoItems.forEachIndexed { row, item ->
                    val baseline = 122f - row * 16f
                    p1DrawSmall(stream, item.name, 316f, baseline)
                    when {
                        item.quantity == 0 -> p1DrawSmall(stream, "0", 392f, baseline)
                        item.quantity <= 20 -> repeat(item.quantity) { index ->
                            val x = 392f + index * 8f + (index / 5) * 4f
                            require(x + 6f <= 570f)
                            stream.addRect(x, baseline - 3f, 6f, 6f)
                            stream.stroke()
                        }
                        else -> p1DrawSmall(stream, "MARCAS: ____ / " + item.quantity, 392f, baseline)
                    }
                }
            }

            fun drawP1Portrait(stream: PDPageContentStream) {
                val frameX = 42f
                val frameY = 605f
                val frameWidth = 104f
                val frameHeight = 100f
                val imageX = frameX + 7f
                val imageY = frameY + 7f
                val imageWidth = frameWidth - 14f
                val imageHeight = frameHeight - 28f
                // Frozen V11 golden uses a chamfered portrait frame, not
                // the provisional rectangular proof. Preserve image viewport.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color.WHITE)
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.65f)
                val corner = 7f
                stream.moveTo(frameX + corner, frameY)
                stream.lineTo(frameX + frameWidth - corner, frameY)
                stream.lineTo(frameX + frameWidth, frameY + corner)
                stream.lineTo(frameX + frameWidth, frameY + frameHeight - corner)
                stream.lineTo(frameX + frameWidth - corner, frameY + frameHeight)
                stream.lineTo(frameX + corner, frameY + frameHeight)
                stream.lineTo(frameX, frameY + frameHeight - corner)
                stream.lineTo(frameX, frameY + corner)
                stream.closePath()
                stream.fillAndStroke()
                stream.setStrokingColor(Color(191, 211, 222))
                stream.setLineWidth(0.35f)
                stream.moveTo(frameX + 12f, frameY + frameHeight - 4.5f)
                stream.lineTo(frameX + frameWidth - 12f, frameY + frameHeight - 4.5f)
                stream.stroke()
                stream.restoreGraphicsState()

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
                    // The original approved paper's absent-portrait symbol is a
                    // pair of PALE FILLED ellipses, never an outline-only avatar.
                    // Normalize its original bounds to the existing V11 viewport.
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(231, 242, 247))
                    stream.setStrokingColor(Color(191, 211, 222))
                    stream.setLineWidth(0.35f)
                    val k = 0.5522848f
                    fun filledOval(cx: Float, cy: Float, rx: Float, ry: Float) {
                        stream.moveTo(cx + rx, cy)
                        stream.curveTo(cx + rx, cy + k * ry, cx + k * rx, cy + ry, cx, cy + ry)
                        stream.curveTo(cx - k * rx, cy + ry, cx - rx, cy + k * ry, cx - rx, cy)
                        stream.curveTo(cx - rx, cy - k * ry, cx - k * rx, cy - ry, cx, cy - ry)
                        stream.curveTo(cx + k * rx, cy - ry, cx + rx, cy - k * ry, cx + rx, cy)
                        stream.closePath()
                        stream.fillAndStroke()
                    }
                    val cx = frameX + frameWidth / 2f
                    filledOval(cx, frameY + 73f, 14.85f, 14.85f)
                    filledOval(cx, frameY + 32.3f, 27.85f, 19.65f)
                    stream.restoreGraphicsState()
                }
                // Golden's caption is beneath the figure and never covers its head.
                // Keep the label when a real local image is shown as well.
                val portraitCaption = "RETRATO"
                val captionSize = 7f
                val captionWidth = headerPdfFont.getStringWidth(portraitCaption) /
                    1000f * captionSize
                stream.saveGraphicsState()
                stream.beginText()
                stream.setFont(headerPdfFont, captionSize)
                stream.setNonStrokingColor(Color(80, 122, 151))
                stream.newLineAtOffset(frameX + (frameWidth - captionWidth) / 2f, frameY + 6f)
                stream.showText(portraitCaption)
                stream.endText()
                stream.restoreGraphicsState()
            }

            // V11 P1 uses genuine physical identity fields. Alignment is not a
            // persisted CharacterSheet property, so its box stays manuscript-only.
            // The same measured body font decides which source suffix must flow to C2.
            val identityTop = 705f
            val classIdentity = PcSheetV11FlowItem(
                "identity:classes",
                source.classes.joinToString(", ") { it.name + " " + it.level },
            )
            val raceIdentity = PcSheetV11FlowItem("identity:race", source.background.race)
            val identityFieldFrames = listOf(
                Triple("CLASE / NIVEL", classIdentity,
                    PcSheetV11RectPt(158f, identityTop - 28f, 412f, 28f)),
                Triple("RAZA", raceIdentity,
                    PcSheetV11RectPt(295f, identityTop - 58f, 126f, 28f)),
            )
            val identityFields = identityFieldFrames.map { (label, item, frame) ->
                val writingArea = PcSheetV11RectPt(
                    frame.leftPt + 6f, frame.bottomPt + 1f,
                    frame.widthPt - 12f, body.metrics.lineHeightPt + 2f,
                )
                val placed = item.text.takeIf { it.isNotBlank() }?.let {
                    body.place(PcSheetV11FlowSection.TRAITS, listOf(item),
                        writingArea, 1, 0f)
                }
                Triple(label to item, frame, placed)
            }
            fun drawP1IdentityFields(stream: PDPageContentStream) {
                val alignment = PcSheetV11RectPt(432f, identityTop - 58f, 138f, 28f)
                val player = PcSheetV11RectPt(158f, identityTop - 58f, 126f, 28f)
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.7f)
                (identityFieldFrames.map { it.third } + listOf(player, alignment)).forEach { frame ->
                    // P1 C2c golden uses four INDIVIDUAL chamfered identity
                    // outlines. Stay strictly inside the old 28pt rectangles;
                    // labels, values, source offsets and writing lines remain
                    // at the exact previously verified PDF coordinates.
                    val cut = 4f
                    require(frame.widthPt > cut * 2f && frame.heightPt > cut * 2f)
                    val l = frame.leftPt
                    val r = frame.rightPt
                    val b = frame.bottomPt
                    val t = frame.topPt
                    stream.moveTo(l + cut, b)
                    stream.lineTo(r - cut, b)
                    stream.lineTo(r, b + cut)
                    stream.lineTo(r, t - cut)
                    stream.lineTo(r - cut, t)
                    stream.lineTo(l + cut, t)
                    stream.lineTo(l, t - cut)
                    stream.lineTo(l, b + cut)
                    stream.closePath()
                    stream.stroke()
                }
                stream.restoreGraphicsState()
                identityFields.forEach { (descriptor, _, placed) ->
                    val (label, _) = descriptor
                    val frame = identityFieldFrames.first { it.first == label }.third
                    draw(stream, label, frame.leftPt + 6f, frame.topPt - 11f, true)
                    placed?.cells?.filter { it.localPageIndex == 1 }?.forEach { cell ->
                        if (cell.drawableText.isNotEmpty()) {
                            draw(stream, cell.drawableText, cell.cellPt.leftPt,
                                body.metrics.baselineIn(cell.cellPt), false)
                        }
                    }
                }
                draw(stream, "JUGADOR", player.leftPt + 6f, player.topPt - 11f, true)
                stream.moveTo(player.leftPt + 6f, player.bottomPt + 5f)
                stream.lineTo(player.rightPt - 6f, player.bottomPt + 5f)
                stream.stroke()
                draw(stream, "ALINEAMIENTO", alignment.leftPt + 6f,
                    alignment.topPt - 11f, true)
                // No persisted alignment exists; a ruled entry is deliberately blank.
                stream.moveTo(alignment.leftPt + 6f, alignment.bottomPt + 5f)
                stream.lineTo(alignment.rightPt - 6f, alignment.bottomPt + 5f)
                stream.stroke()
            }
            val identityFieldOverflow = identityFields.mapNotNull { (descriptor, _, placed) ->
                val (_, item) = descriptor
                val consumed = placed?.cells?.asSequence()
                    ?.filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    ?.maxOfOrNull { it.sourceEndExclusive } ?: 0
                require(consumed in 0..item.text.length)
                if (consumed == item.text.length) null
                else item.copy(text = item.text.substring(consumed))
            }

            // A separate physical combat dashboard replaces the old prose
            // identity row; values are taken ONLY from the saved character.
            fun drawP1CombatReference(stream: PDPageContentStream) {
                val values = p1CombatSourceFields.mapIndexed { index, field ->
                    field.first to p1CombatRoutes[index].paperValue
                }
                val left = 312f
                val paperTop = 591f
                // Golden P1 has 3+3+2 real cards. This App persists PV TEMP,
                // not INSPIRACIÓN: never invent an absent golden state.
                // First six cards are 86pt wide, bottom two 129pt each.
                val slots = listOf(
                    Triple(0, 0, 0), Triple(1, 1, 0), Triple(2, 2, 0),
                    Triple(3, 0, 1), Triple(6, 1, 1), Triple(7, 2, 1),
                    Triple(4, 0, 2), Triple(5, 1, 2),
                )
                val title = "COMBATE Y SUPERVIVENCIA"
                require(headerPdfFont.getStringWidth(title) * 11f / 1000f <= 240f)
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(217, 236, 245))
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.6f)
                stream.moveTo(317f, 594f)
                stream.lineTo(565f, 594f)
                stream.lineTo(570f, 599f)
                stream.lineTo(570f, 611f)
                stream.lineTo(565f, 616f)
                stream.lineTo(317f, 616f)
                stream.lineTo(312f, 611f)
                stream.lineTo(312f, 599f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                stream.beginText()
                stream.setFont(headerPdfFont, 11f)
                stream.newLineAtOffset(321f, 600f)
                stream.showText(title)
                stream.endText()
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.55f)
                slots.forEach { (_, col, row) ->
                    val narrow = row < 2
                    val rawWidth = if (narrow) 86f else 129f
                    val rawHeight = if (narrow) 28f else 32f
                    // 2.4pt gutters, all inside the OLD source paper bounds
                    // x312..570 and y503..591: do not touch attack tables.
                    val l = left + col * rawWidth + 1.2f
                    val r = left + (col + 1) * rawWidth - 1.2f
                    val t = paperTop - row * 28f - 1.2f
                    val b = paperTop - row * 28f - rawHeight + 1.2f
                    require(l >= 312f && r <= 570f && b >= 503f && t <= 591f)
                    val cut = 3f
                    stream.setNonStrokingColor(if (row == 1)
                        Color(244, 250, 253) else Color(238, 247, 251))
                    stream.moveTo(l + cut, b)
                    stream.lineTo(r - cut, b)
                    stream.lineTo(r, b + cut)
                    stream.lineTo(r, t - cut)
                    stream.lineTo(r - cut, t)
                    stream.lineTo(l + cut, t)
                    stream.lineTo(l, t - cut)
                    stream.lineTo(l, b + cut)
                    stream.closePath()
                    stream.fillAndStroke()
                }
                stream.restoreGraphicsState()
                slots.forEach { (sourceIndex, col, row) ->
                    val (label, value) = values[sourceIndex]
                    val narrow = row < 2
                    val width = if (narrow) 86f else 129f
                    val top = paperTop - row * 28f
                    val x = left + col * width
                    val labelPt = 9f
                    require(headerPdfFont.getStringWidth(label) *
                        labelPt / 1000f <= width - 12f) {
                        "P1 true source combat label exceeds its real card: " + label
                    }
                    stream.beginText()
                    stream.setFont(headerPdfFont, labelPt)
                    stream.newLineAtOffset(x + 5f, top - 10f)
                    stream.showText(label)
                    stream.endText()
                    if (value.isNotEmpty()) {
                        val measured = body.width.measurePt(value)
                        require(measured <= width - 12f) {
                            "P1 combat value must flow into source-backed C2: " + label
                        }
                        draw(stream, value, x + (width - measured) / 2f,
                            top - (if (narrow) 22f else 24f), false)
                    }
                }
            }

            // Approved golden P4: source-backed magical aptitude, real save DC,
            // real spell attack; blank manuscript fields when values are absent.
            // This is a physical banner above the three-column level ledger,
            // NOT additional fake spell slots or a generic body-text section.
            fun drawP4MagicReference(stream: PDPageContentStream) {
                // C221: for contradictory source-specific spellcasting
                // profiles never print one global number as a universal
                // value. Per-spell P4 rows continue to print each genuine
                // source identity and independent prepared state.
                // Exact per-source numeric card geometry is still an OWNER
                // decision under D-0076, so do not fabricate it here.
                val labels = io.github.mrsimkin.dndcustomaid.shared.character
                    .PcSheetV11SpellSourceLabels
                val varying = labels.conflictingReferenceFields(data.spells) +
                    labels.globalReferenceDisagreements(
                        data.spells, source.spellSaveDc, source.spellAttackModifier,
                    )
                val ability = when (source.spellcastingAbility) {
                    SpellcastingAbility.STRENGTH -> "FUE"
                    SpellcastingAbility.DEXTERITY -> "DES"
                    SpellcastingAbility.CONSTITUTION -> "CON"
                    SpellcastingAbility.INTELLIGENCE -> "INT"
                    SpellcastingAbility.WISDOM -> "SAB"
                    SpellcastingAbility.CHARISMA -> "CAR"
                    SpellcastingAbility.OTHER, SpellcastingAbility.NONE -> null
                }
                val attack = source.spellAttackModifier?.let {
                    if (it >= 0) "+" + it else it.toString()
                }
                val fields: List<Pair<String, String?>> = listOf(
                    "APTITUD" to if ("APTITUD" in varying) "SEGÚN FUENTE" else ability,
                    "CD SALVACIÓN" to if ("CD SALVACIÓN" in varying)
                        "SEGÚN FUENTE" else source.spellSaveDc?.toString(),
                    "ATAQUE MÁGICO" to if ("ATAQUE MÁGICO" in varying)
                        "SEGÚN FUENTE" else attack,
                )
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.7f)
                stream.setNonStrokingColor(Color(231, 241, 247))
                // Frozen V11 P4: a genuinely clipped, stroked blue cartela,
                // confined to the original 528x27pt source-free header strip.
                val x0 = 42f
                val x1 = 570f
                val y0 = 667f
                val y1 = 694f
                val corner = 6f
                stream.moveTo(x0 + corner, y0)
                stream.lineTo(x1 - corner, y0)
                stream.lineTo(x1, y0 + corner)
                stream.lineTo(x1, y1 - corner)
                stream.lineTo(x1 - corner, y1)
                stream.lineTo(x0 + corner, y1)
                stream.lineTo(x0, y1 - corner)
                stream.lineTo(x0, y0 + corner)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                draw(stream, "REFERENCIA MÁGICA", 49f, 676f, true)
                fields.forEachIndexed { index, (label, value) ->
                    val x = 42f + index * 180f
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(80, 122, 151))
                    stream.setLineWidth(0.7f)
                    // Golden uses clipped corners on each source-backed card,
                    // with identical 168x44pt measured source rectangles.
                    val cardCut = 6f
                    val cardRight = x + 168f
                    val cardBottom = 617f
                    val cardTop = 661f
                    stream.moveTo(x + cardCut, cardBottom)
                    stream.lineTo(cardRight - cardCut, cardBottom)
                    stream.lineTo(cardRight, cardBottom + cardCut)
                    stream.lineTo(cardRight, cardTop - cardCut)
                    stream.lineTo(cardRight - cardCut, cardTop)
                    stream.lineTo(x + cardCut, cardTop)
                    stream.lineTo(x, cardTop - cardCut)
                    stream.lineTo(x, cardBottom + cardCut)
                    stream.closePath()
                    stream.stroke()
                    stream.restoreGraphicsState()
                    val fieldLabelPt = 9f
                    require(headerPdfFont.getStringWidth(label) *
                        fieldLabelPt / 1000f <= 154f) {
                        "P4 spellcasting card label overflows frozen width: " + label
                    }
                    stream.beginText()
                    stream.setFont(headerPdfFont, fieldLabelPt)
                    stream.newLineAtOffset(x + 7f, 645f)
                    stream.showText(label)
                    stream.endText()
                    if (value == null) {
                        stream.moveTo(x + 7f, 629f)
                        stream.lineTo(x + 160f, 629f)
                        stream.stroke()
                    } else {
                        draw(stream, value, x + 8f, 625f, true)
                    }
                }
            }

            // The frozen C2 golden frames each writable NOTES block with a
            // pale-blue clipped cartela, not a floating black heading.
            // Use only the 18pt strip that is already reserved above the
            // complete measured grid; never consume a source-text cell.
            fun drawC2ManuscriptCartela(
                stream: PDPageContentStream, x: Float, width: Float, gridTop: Float,
            ) {
                require(width >= 32f)
                val bottom = gridTop + 1f
                val top = gridTop + 17f
                val cut = 4f
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(217, 236, 245))
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.55f)
                stream.moveTo(x + cut, bottom)
                stream.lineTo(x + width - cut, bottom)
                stream.lineTo(x + width, bottom + cut)
                stream.lineTo(x + width, top - cut)
                stream.lineTo(x + width - cut, top)
                stream.lineTo(x + cut, top)
                stream.lineTo(x, top - cut)
                stream.lineTo(x, bottom + cut)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                draw(stream, "NOTAS", x + 8f, gridTop + 7f, true)
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
                drawC2ManuscriptCartela(stream, 42f, 528f, gridTop)
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
                // C2 is two independent vertical paper columns. A nearly
                // occupied LEFT column cannot veto useful blank RIGHT paper.
                val columns = PcSheetV11ColumnGrid.split(pageArea, 2, 12f)
                columns.forEachIndexed { index, column ->
                    val lowestTextBottom = cells.filter { it.columnIndex == index + 1 }
                        .minOfOrNull { it.cellPt.bottomPt } ?: column.topPt
                    val gridBottom = 78f
                    val gridSize = 16f
                    val maxGridTop = lowestTextBottom - 28f
                    val completeRows = ((maxGridTop - gridBottom) / gridSize).toInt()
                    if (completeRows < 3) return@forEachIndexed
                    val gridTop = gridBottom + completeRows * gridSize
                    require(gridTop + 18f < lowestTextBottom) {
                        "Independent C2 manuscript notes may not overlap actual source cells."
                    }
                    val completeColumns = (column.widthPt / gridSize).toInt()
                    require(completeColumns >= 2)
                    val gridWidth = completeColumns * gridSize
                    val gridLeft = column.leftPt + (column.widthPt - gridWidth) / 2f
                    drawC2ManuscriptCartela(stream, gridLeft, gridWidth, gridTop)
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130, 165, 186))
                    stream.setLineWidth(0.5f)
                    for (x in 0..completeColumns) {
                        val px = gridLeft + x * gridSize
                        stream.moveTo(px, gridBottom)
                        stream.lineTo(px, gridTop)
                        stream.stroke()
                    }
                    for (y in 0..completeRows) {
                        val py = gridBottom + y * gridSize
                        stream.moveTo(gridLeft, py)
                        stream.lineTo(gridLeft + gridWidth, py)
                        stream.stroke()
                    }
                    stream.restoreGraphicsState()
                }
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


            // P3 is the approved physical paired narrative sheet, not generic
            // unlabelled full-width paragraphs. Source strings retain their IDs
            // and unprinted suffixes for measured two-column P3 continuations.
            val p3Narrative = texts.narrative.associateBy { it.sourceKey }
            val p3FieldSpecs = listOf(
                "TRASFONDO" to listOf("background-name", "background-summary"),
                "RELIGIÓN / FE" to listOf("background-faith"),
                "RASGOS DE PERSONALIDAD" to listOf("personality"),
                "IDEALES" to listOf("ideals"),
                "VÍNCULOS" to listOf("bonds"),
                "DEFECTOS" to listOf("flaws"),
            )
            val p3FieldLayouts = p3FieldSpecs.mapIndexed { index, (heading, keys) ->
                val x = if (index % 2 == 0) 42f else 312f
                val top = 672f - (index / 2) * 63f
                val bounds = PcSheetV11RectPt(x, top - 63f, 258f, 63f)
                val bodyArea = PcSheetV11RectPt(x + 5f, top - 57f, 248f, 42f)
                val entries = keys.mapNotNull { p3Narrative["narrative:" + it] }
                val measured = body.place(
                    PcSheetV11FlowSection.NARRATIVE, entries, bodyArea, 1, 0f,
                )
                Triple(heading, bounds, measured)
            }
            val p3StoryItems = listOfNotNull(p3Narrative["narrative:story"])
            val p3StoryArea = PcSheetV11RectPt(47f, 88f, 518f, 361f)
            val p3Story = body.place(
                PcSheetV11FlowSection.NARRATIVE, p3StoryItems, p3StoryArea, 2, 12f,
            )
            fun remainingNarrative(
                original: List<PcSheetV11FlowItem>,
                placed: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PlacedFlow,
            ): List<PcSheetV11FlowItem> = original.mapNotNull { item ->
                val consumed = placed.cells.asSequence()
                    .filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    .maxOfOrNull { it.sourceEndExclusive } ?: 0
                require(consumed in 0..item.text.length)
                if (consumed == item.text.length) null
                else item.copy(text = item.text.substring(consumed))
            }
            val p3Overflow = buildList {
                p3FieldLayouts.forEachIndexed { index, (heading, _, flow) ->
                    val keys = p3FieldSpecs[index].second
                    val originals = keys.mapNotNull { p3Narrative["narrative:" + it] }
                    val remaining = remainingNarrative(originals, flow)
                    if (remaining.isNotEmpty()) {
                        add(PcSheetV11FlowItem(
                            "p3-field-heading:" + index, heading + " / CONTINUACIÓN",
                            keepTogetherIfFits = true,
                        ))
                        addAll(remaining)
                    }
                }
                val remainingStory = remainingNarrative(p3StoryItems, p3Story)
                if (remainingStory.isNotEmpty()) {
                    add(PcSheetV11FlowItem(
                        "p3-story-heading", "HISTORIA Y CRÓNICA / CONTINUACIÓN",
                        keepTogetherIfFits = true,
                    ))
                    addAll(remainingStory)
                }
            }
            fun drawP3Band(
                stream: PDPageContentStream, label: String, y: Float,
            ) {
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.65f)
                stream.setNonStrokingColor(Color(217, 236, 245))
                // Frozen P3 golden: clipped blue narrative cartela
                // within the SAME 528×20pt physical reservation.
                stream.moveTo(48f, y)
                stream.lineTo(564f, y)
                stream.lineTo(570f, y + 6f)
                stream.lineTo(570f, y + 14f)
                stream.lineTo(564f, y + 20f)
                stream.lineTo(48f, y + 20f)
                stream.lineTo(42f, y + 14f)
                stream.lineTo(42f, y + 6f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                draw(stream, label, 49f, y + 6f, true)
            }
            fun p3RuledCells(
                stream: PDPageContentStream,
                rect: PcSheetV11RectPt,
                lineHeight: Float,
            ) {
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(238, 247, 251))
                stream.addRect(rect.leftPt, rect.bottomPt,
                    rect.widthPt, rect.heightPt)
                stream.fill()
                // Golden P3 alternating blue-mist handwriting ROWS.
                // Paper-only fills precede existing measured ruled baselines
                // and source glyphs; no added text/rows/pagination.
                val rows = (rect.heightPt / lineHeight).toInt()
                stream.setNonStrokingColor(Color(231, 242, 247))
                for (row in 0 until rows step 2) {
                    val top = rect.topPt - row * lineHeight
                    val bottom = maxOf(rect.bottomPt, top - lineHeight)
                    if (top > bottom) {
                        stream.addRect(rect.leftPt, bottom, rect.widthPt, top-bottom)
                        stream.fill()
                    }
                }
                stream.setStrokingColor(Color(174, 203, 220))
                stream.setLineWidth(0.35f)
                for (row in 1..rows) {
                    val y = rect.topPt - row * lineHeight
                    if (y >= rect.bottomPt) {
                        stream.moveTo(rect.leftPt, y)
                        stream.lineTo(rect.rightPt, y)
                        stream.stroke()
                    }
                }
                stream.restoreGraphicsState()
            }
            fun drawP3FirstPage(stream: PDPageContentStream) {
                drawP3Band(stream, "TRASFONDO Y PERFIL NARRATIVO", 682f)
                p3FieldLayouts.forEachIndexed { index, (heading, rect, placed) ->
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130, 165, 186))
                    stream.setLineWidth(0.55f)
                    stream.addRect(rect.leftPt, rect.bottomPt,
                        rect.widthPt, rect.heightPt)
                    stream.stroke()
                    stream.restoreGraphicsState()
                    p3RuledCells(stream,
                        PcSheetV11RectPt(rect.leftPt + 1f, rect.bottomPt + 1f,
                            rect.widthPt - 2f, rect.heightPt - 18f),
                        body.metrics.lineHeightPt)
                    // Restore the frozen golden blue field-title band
                    // INSIDE the pre-existing 17pt label reservation.
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(231, 242, 247))
                    stream.addRect(rect.leftPt + 1f, rect.topPt - 17f,
                        rect.widthPt - 2f, 16f)
                    stream.fill()
                    stream.restoreGraphicsState()
                    // Section labels are genuine Barlow Condensed, not
                    // Fira Sans values. Enforce width with the embedded
                    // PDF font before changing their physical typography.
                    val labelPt = 9f
                    val labelWidth = headerPdfFont.getStringWidth(heading) *
                        labelPt / 1000f
                    require(labelWidth <= rect.widthPt - 10f) {
                        "P3 narrative cartela label cannot fit without clipping."
                    }
                    stream.beginText()
                    stream.setFont(headerPdfFont, labelPt)
                    stream.newLineAtOffset(rect.leftPt + 5f, rect.topPt - 11f)
                    stream.showText(heading)
                    stream.endText()
                    placed.cells.filter { it.localPageIndex == 1 }.forEach { cell ->
                        if (cell.drawableText.isNotEmpty()) {
                            draw(stream, cell.drawableText, cell.cellPt.leftPt,
                                body.metrics.baselineIn(cell.cellPt), false)
                        }
                    }
                    require(p3FieldSpecs[index].first == heading)
                }
                drawP3Band(stream, "HISTORIA Y CRÓNICA", 461f)
                val columns = PcSheetV11ColumnGrid.split(p3StoryArea, 2, 12f)
                columns.forEach { p3RuledCells(stream, it, body.metrics.lineHeightPt) }
                p3Story.cells.filter { it.localPageIndex == 1 }.forEach { cell ->
                    if (cell.drawableText.isNotEmpty()) {
                        draw(stream, cell.drawableText, cell.cellPt.leftPt,
                            body.metrics.baselineIn(cell.cellPt), false)
                    }
                }
            }
            fun drawP3ContinuationPaper(stream: PDPageContentStream) {
                drawP3Band(stream, "TRASFONDO, PERFIL E HISTORIA / CONTINUACIÓN", 680f)
                val columns = PcSheetV11ColumnGrid.split(
                    PcSheetV11RectPt(42f, 76f, 528f, 597f), 2, 12f,
                )
                columns.forEach { p3RuledCells(stream, it, body.metrics.lineHeightPt) }
            }

            // The approved P4 golden places a short, genuine spell origin at
            // the far right of its own row. Long associations MUST continue
            // through the existing measured text flow, never be abbreviated.
            val p4SourceFont = measurer.resolve(PcSheetV11Typeface.BODY, 7f)
            val sourceNames =
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSourceLabels
                    .forSpells(data.spells)
            val p4SourceLabels = data.spells.associate { entry ->
                "quick-spell:" + entry.spell.id to
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSourceLabels
                        .perSpell(entry, sourceNames)
            }
            val p4RightSources = data.spells.mapNotNull { entry ->
                val key = "quick-spell:" + entry.spell.id
                val sourceInfo = p4SourceLabels.getValue(key)
                val fullName = "     " + entry.spell.name
                // 168pt true 3-column text band with a 12pt glyph gutter;
                // no fake slots and no shrinking long spell/source records.
                if (sourceInfo.isNotBlank() &&
                    // Persisted source names may contain actual CR/LF/TAB. A
                    // fixed physical right lane is one row only; measuring
                    // its multi-line content with PDFBox rejects control
                    // glyphs. Route the entire UUID-backed source through
                    // the existing measured/lossless P4 row instead.
                    // Both the spell NAME and its casting SOURCE must be
                    // physically single-line before entering the fixed P4
                    // right lane. Multiline persisted names belong to the
                    // existing lossless three-column text flow, never a
                    // one-line PDFBox width call (CR/LF have no font glyph).
                    fullName.none { it == '\r' || it == '\n' || it == '\t' } &&
                    sourceInfo.none { it == '\r' || it == '\n' || it == '\t' } &&
                    body.width.measurePt(fullName) +
                    p4SourceFont.width.measurePt(sourceInfo) + 12f <= 168f
                ) key to sourceInfo else null
            }.toMap()

                fun drawC2ContinuationCartela(
                    stream: PDPageContentStream,
                    column: PcSheetV11RectPt,
                ) {
                    // Golden C2c: a genuine column-continuation identity is a
                    // small clipped BLUE band, not loose ink above the source.
                    // The 703..716pt header gap is already empty; source rows
                    // start below 702pt. No pagination/content change.
                    val bottom = 703f
                    val top = 716f
                    require(column.topPt <= 702f && top < 717f && bottom > column.topPt)
                    val cut = 2.5f
                    val left = column.leftPt
                    val right = column.rightPt
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(217, 236, 245))
                    stream.setStrokingColor(Color(80, 122, 151))
                    stream.setLineWidth(0.45f)
                    stream.moveTo(left + cut, bottom)
                    stream.lineTo(right - cut, bottom)
                    stream.lineTo(right, bottom + cut)
                    stream.lineTo(right, top - cut)
                    stream.lineTo(right - cut, top)
                    stream.lineTo(left + cut, top)
                    stream.lineTo(left, top - cut)
                    stream.lineTo(left, bottom + cut)
                    stream.closePath()
                    stream.fillAndStroke()
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
                    val pageCells = flow.cells.filter {
                        it.localPageIndex == index + 1
                    }
                    // A note card can span physical C-N3 pages. Keep the
                    // persisted title (or stable source ID) visible on its
                    // NEXT page, instead of leaving owner-visible paragraphs
                    // without a note identity. No source text is duplicated.
                    val basicTitle = title + " / " + (index + 1)
                    // P3 stories and profile fields can span multiple C2-style
                    // flow pages. The first page carries its original heading;
                    // later physical pages must retain that SAME source-backed
                    // narrative identity in the free header, not merely say P3.
                    // No paragraph, cell, source offset or page boundary changes.
                    val pageTitle = if (title == "P3 - Narrativa / continuación" &&
                        index > 0
                    ) {
                        val firstCell = pageCells.minWithOrNull(
                            compareBy({ it.columnIndex }, { it.rowIndex })
                        )
                        val sourcePosition = firstCell?.let { first ->
                            entries.indexOfFirst { it.sourceKey == first.sourceKey }
                        } ?: -1
                        val heading = if (sourcePosition >= 0 &&
                            firstCell?.sourceKey?.startsWith("narrative:") == true
                        ) {
                            entries.take(sourcePosition).lastOrNull {
                                it.sourceKey == "p3-story-heading" ||
                                    it.sourceKey.startsWith("p3-field-heading:")
                            }?.text
                        } else null
                        if (heading == null) basicTitle else {
                            fun fits(value: String): Boolean =
                                headerPdfFont.getStringWidth(
                                    pcSheetV11PrintableText(value)
                                ) * 12f / 1000f <= 524f
                            val full = basicTitle + " / " + heading
                            val short = "P3 / " + (index + 1) + " / " + heading
                            when {
                                fits(full) -> full
                                fits(short) -> short
                                else -> error("P3 continuation must retain its real source heading")
                            }
                        }
                    } else if (title == "C-N3 - Notas digitales" &&
                        pageCells.firstOrNull()?.sourceStartOffset?.let { it > 0 } == true
                    ) {
                        val sourceKey = requireNotNull(pageCells.firstOrNull()).sourceKey
                        val note = data.digitalNotes.firstOrNull {
                            "digital-note:" + (it.sourceId?.toString() ?: "general") ==
                                sourceKey
                        }
                        io.github.mrsimkin.dndcustomaid.shared.character
                            .PcSheetV11NoteContinuationHeader.choose(
                                baseTitle = basicTitle,
                                sourceTitle = note?.title ?: (note?.sourceId?.toString()
                                    ?: "NOTAS GENERALES"),
                                stableId = note?.sourceId?.toString() ?: "GENERAL",
                                fits = { value ->
                                    headerPdfFont.getStringWidth(
                                        pcSheetV11PrintableText(value)
                                    ) * 12f / 1000f <= 524f
                                },
                            )
                    } else basicTitle
                    val page = PDPage(PDRectangle.LETTER)
                    pdf.addPage(page)
                    physicalSectionKeys += title
                    PDPageContentStream(pdf, page).use { stream ->
                        drawFrameAndHeader(stream, pageTitle)
                        if (title.startsWith("P4 - ")) drawP4MagicReference(stream)
                        if (title.startsWith("P3 - Narrativa / continuación")) {
                            drawP3ContinuationPaper(stream)
                        }
                        if (title.startsWith("P1 - ") && index == 0) {
                            drawP1Attributes(stream)
                            drawP1Attacks(stream)
                            drawP1Spellcasting(stream)
                            drawP1Traits(stream)
                            drawP1Ammunition(stream)
                            drawP1Portrait(stream)
                            drawP1IdentityFields(stream)
                            drawP1CombatReference(stream)
                            drawP1GroupedSkills(stream)
                            drawP1BIndependentSkills(stream)
                        }
                        // A level heading is printed once in the source stream.
                        // When the SAME level crosses a P4 physical COLUMN
                        // (including a new page), each continued column gets
                        // source-backed context in the free 10pt header gap:
                        // above the ledger (top 607) and below cards (bottom 617).
                        // Never move/duplicate real rows or invent level headings.
                        if (title.startsWith("P4 - ")) {
                            flow.columnsPt.forEachIndexed { columnZero, column ->
                                val firstCell = pageCells
                                    .filter { it.columnIndex == columnZero + 1 }
                                    .minByOrNull { it.rowIndex }
                                if (firstCell != null &&
                                    !firstCell.sourceKey.startsWith("spell-level:")) {
                                    val firstIndex = entries.indexOfFirst {
                                        it.sourceKey == firstCell.sourceKey
                                    }
                                    require(firstIndex >= 0)
                                    val level = entries.take(firstIndex)
                                        .lastOrNull {
                                            it.sourceKey.startsWith("spell-level:")
                                        }?.sourceKey?.removePrefix("spell-level:")
                                        ?.toIntOrNull()
                                    if (level != null) {
                                        val context = if (level == 0)
                                            "TRUCOS / NIVEL 0 / CONTINUACIÓN"
                                        else "NIVEL " + level + " / CONTINUACIÓN"
                                        require(p4SourceFont.width.measurePt(context) <=
                                            column.widthPt - 4f) {
                                            "P4 physical column continuation label overflows."
                                        }
                                        // True level continues into this physical
                                        // column. The approved P4 grammar uses
                                        // BLUE LEVEL BANDS, not floating labels.
                                        // Reserve only the EXISTING empty 10pt
                                        // gap below cards and above printed ledger;
                                        // no source cell/row/page is relocated.
                                        val bottom = 607.3f
                                        val top = 616.3f
                                        val left = column.leftPt
                                        val right = left + column.widthPt
                                        val cut = 2f
                                        require(column.topPt == 607f &&
                                            bottom > column.topPt && top < 617f)
                                        stream.saveGraphicsState()
                                        stream.setNonStrokingColor(Color(217, 236, 245))
                                        stream.setStrokingColor(Color(80, 122, 151))
                                        stream.setLineWidth(0.45f)
                                        stream.moveTo(left + cut, bottom)
                                        stream.lineTo(right - cut, bottom)
                                        stream.lineTo(right, bottom + cut)
                                        stream.lineTo(right, top - cut)
                                        stream.lineTo(right - cut, top)
                                        stream.lineTo(left + cut, top)
                                        stream.lineTo(left, top - cut)
                                        stream.lineTo(left, bottom + cut)
                                        stream.closePath()
                                        stream.fillAndStroke()
                                        stream.restoreGraphicsState()
                                        stream.beginText()
                                        stream.setFont(bodyPdfFont, 7f)
                                        stream.newLineAtOffset(column.leftPt + 2f, 610f)
                                        stream.showText(pcSheetV11PrintableText(context))
                                        stream.endText()
                                    }
                                }
                            }
                        }
                        if (title == "Libro de conjuros") {
                            // D-0076 §3.48 / BK05: a taller-than-column card
                            // may continue losslessly, but each NEW physical
                            // column must identify the SAME persisted spell.
                            // All fragments start at line 0 in the next column.
                            // Put the source-free sign within the real reserved
                            // 15pt band BELOW header rule y717 and ABOVE flow
                            // top y702: never spend a description line.
                            val namesByKey = entries.associate {
                                it.sourceKey to it.text.substringBefore('\n')
                            }
                            val continuationParts = flow.sourceFlow.fragments.filter {
                                it.localPageIndex == index + 1 &&
                                    it.continuedFromPrevious
                            }
                            val actualColumns = flow.columnsPt
                            continuationParts.forEach { fragment ->
                                require(fragment.startLineIndexInColumn == 0) {
                                    "Book continuation must begin at a genuine column top."
                                }
                                val col = actualColumns[fragment.columnIndex - 1]
                                require(col.topPt == 702f) {
                                    "V11 book continuation label needs the source-free header band."
                                }
                                val realName = namesByKey.getValue(fragment.sourceKey)
                                val titleText = "CONTINUACIÓN: " + realName
                                val fontSize = 7f
                                val width = bodyPdfFont.getStringWidth(pcSheetV11PrintableText(titleText)) *
                                    fontSize / 1000f
                                val printed = if (width <= col.widthPt - 6f) titleText
                                    else "CONTINUACIÓN (VER ÍNDICE)"
                                require(bodyPdfFont.getStringWidth(pcSheetV11PrintableText(printed)) *
                                    fontSize / 1000f <= col.widthPt - 6f)
                                stream.saveGraphicsState()
                                stream.setNonStrokingColor(Color(80, 122, 151))
                                stream.beginText()
                                stream.setFont(bodyPdfFont, fontSize)
                                stream.newLineAtOffset(col.leftPt + 2f, col.topPt + 5f)
                                stream.showText(pcSheetV11PrintableText(printed))
                                stream.endText()
                                stream.restoreGraphicsState()
                            }
                        }
                        if (title == "C2 - Habilidades, acciones y recursos") {
                            // When a GENUINE custom attribute and linked skills
                            // cannot fit a whole column, source-backed context
                            // accompanies each following physical column.
                            // It lives in the reserved 702..717pt header gap:
                            // no duplicated attribute score/ID, no row moved,
                            // no invented state, no new page or reduced font.
                            flow.columnsPt.forEachIndexed { columnZero, column ->
                                val firstCell = pageCells
                                    .filter { it.columnIndex == columnZero + 1 }
                                    .minByOrNull { it.rowIndex }
                                val group = entries.firstOrNull {
                                    it.sourceKey == firstCell?.sourceKey
                                }?.groupKey
                                val attrIndex = group
                                    ?.removePrefix("custom-attribute-group:")
                                    ?.toIntOrNull()
                                // A source group can cross a PHYSICAL C2 column after its blue
                                // cartela was printed in the preceding column. Repeat only
                                // its true group identity in the existing free header band,
                                // never a source row, UUID, count or invented game state.
                                // Custom attributes have their own source-specific caption.
                                if (attrIndex == null && firstCell != null &&
                                    !firstCell.sourceKey.startsWith("c2-heading:")) {
                                    val sourcePosition = entries.indexOfFirst {
                                        it.sourceKey == firstCell.sourceKey
                                    }
                                    val precedingHeading = if (sourcePosition > 0)
                                        entries.take(sourcePosition).lastOrNull {
                                            it.sourceKey.startsWith("c2-heading:")
                                        } else null
                                    if (precedingHeading != null) {
                                        val fontPt = 7f
                                        val shortName = precedingHeading.text
                                            .substringBefore(" / ").substringBefore(",")
                                        val context = listOf(
                                            "CONTINÚA: " + precedingHeading.text,
                                            "CONTINÚA: " + shortName,
                                        ).firstOrNull { candidate ->
                                            bodyPdfFont.getStringWidth(
                                                pcSheetV11PrintableText(candidate)
                                            ) * fontPt / 1000f <= column.widthPt - 4f
                                        }
                                        requireNotNull(context) {
                                            "C2 source group continuation cannot fit the existing header band."
                                        }
                                        drawC2ContinuationCartela(stream, column)
                                        stream.saveGraphicsState()
                                        stream.setNonStrokingColor(Color(80, 122, 151))
                                        stream.beginText()
                                        stream.setFont(bodyPdfFont, fontPt)
                                        stream.newLineAtOffset(column.leftPt + 2f, 707f)
                                        stream.showText(pcSheetV11PrintableText(context))
                                        stream.endText()
                                        stream.restoreGraphicsState()
                                    }
                                }
                                if (attrIndex != null) {
                                    val original = flow.cells.firstOrNull {
                                        it.sourceKey == "attribute:" + attrIndex
                                    }
                                    if (original != null &&
                                        (original.localPageIndex != index + 1 ||
                                            original.columnIndex != columnZero + 1)
                                    ) {
                                        val attribute = requireNotNull(
                                            data.attributes.drop(6).getOrNull(attrIndex)
                                        ) { "C2 continuation lost its source attribute." }
                                        val fontPt = 7f
                                        fun fits(text: String): Boolean =
                                            bodyPdfFont.getStringWidth(
                                                pcSheetV11PrintableText(text)
                                            ) * fontPt / 1000f <= column.widthPt - 4f
                                        val full = "CONTINÚA ATRIBUTO: " + attribute.name
                                        val short = attribute.abbreviation
                                            .takeIf { it.isNotBlank() }
                                            ?.let {
                                                "CONTINÚA ATRIBUTO: " +
                                                    approvedAbilityAbbreviation(it)
                                            }
                                        val sourceId = attribute.reference.customAttributeId
                                            ?.toString()?.let { "CONTINÚA ID: " + it }
                                        val context = listOfNotNull(full, short, sourceId)
                                            .firstOrNull { fits(it) }
                                        requireNotNull(context) {
                                            "C2 source attribute continuation cannot fit the " +
                                                "existing physical header band."
                                        }
                                        drawC2ContinuationCartela(stream, column)
                                        stream.saveGraphicsState()
                                        stream.setNonStrokingColor(Color(80, 122, 151))
                                        stream.beginText()
                                        stream.setFont(bodyPdfFont, fontPt)
                                        stream.newLineAtOffset(column.leftPt + 2f, 707f)
                                        stream.showText(pcSheetV11PrintableText(context))
                                        stream.endText()
                                        stream.restoreGraphicsState()
                                    }
                                }
                            }
                            val fromP2 = pageCells.any {
                                it.sourceKey.startsWith("p2-trait:") ||
                                    it.sourceKey == "c2-heading:traits"
                            }
                            val fromP1 = pageCells.any {
                                !it.sourceKey.startsWith("p2-trait:") &&
                                    it.sourceKey != "c2-heading:traits"
                            }
                            if (fromP2) mixedC2TraitPages += pdf.numberOfPages
                            if (fromP1) mixedC2P1Pages += pdf.numberOfPages
                        }
                        // Golden P4 uses quiet blue level bands and ruled writing
                        // rows, not free-floating glyphs over a blank page.
                        if (title.startsWith("P4 - ")) {
                            stream.saveGraphicsState()
                            stream.setStrokingColor(Color(176, 204, 220))
                            stream.setLineWidth(0.35f)
                            pageCells.forEach { cell ->
                                if (cell.sourceKey.startsWith("spell-level:") ||
                                    cell.sourceKey.startsWith("spell-handwritten:") ||
                                    cell.sourceKey.startsWith("quick-spell:")) {
                                    val fill = if (cell.sourceKey.startsWith("spell-level:"))
                                        Color(217, 236, 245)
                                    else Color(238, 247, 251)
                                    stream.setNonStrokingColor(fill)
                                    stream.addRect(cell.cellPt.leftPt, cell.cellPt.bottomPt,
                                        cell.cellPt.widthPt, cell.cellPt.heightPt)
                                    stream.fill()
                                    stream.moveTo(cell.cellPt.leftPt, cell.cellPt.bottomPt)
                                    stream.lineTo(cell.cellPt.rightPt, cell.cellPt.bottomPt)
                                    stream.stroke()
                                }
                            }
                            stream.restoreGraphicsState()
                        }
                        // C-N3 source cards in the genuine Mara PDF were
                        // visibly glued together: the last text line of one
                        // record touched the next card title. A thin blue-mist
                        // START band and top rule give each persisted note its
                        // own physical anchor, without inserting fake text,
                        // changing word coordinates, or consuming extra pages.
                        // A continued record has no new start band: its real
                        // source identity is instead in this page's header.
                        if (title == "C-N3 - Notas digitales") {
                            val starts = pageCells.filter {
                                it.sourceKey.startsWith("digital-note:") &&
                                    it.sourceStartOffset == 0
                            }
                            stream.saveGraphicsState()
                            stream.setNonStrokingColor(Color(217, 236, 245))
                            stream.setStrokingColor(Color(130, 165, 186))
                            stream.setLineWidth(0.45f)
                            starts.forEach { cell ->
                                val rect = cell.cellPt
                                stream.addRect(rect.leftPt, rect.bottomPt,
                                    rect.widthPt, rect.heightPt)
                                stream.fill()
                                stream.moveTo(rect.leftPt, rect.topPt)
                                stream.lineTo(rect.rightPt, rect.topPt)
                                stream.stroke()
                            }
                            stream.restoreGraphicsState()
                        }
                        // CN304: a digital C-N3 record is never replaced by paper
                        // notes. Only unused floor below the LAST measured source
                        // line can carry secondary handwriting rules. No extra
                        // page, caption, fictitious record or pagination offset.
                        if (title == "C-N3 - Notas digitales" && pageCells.isNotEmpty()) {
                            val lastSourceBottom = pageCells.minOf { it.cellPt.bottomPt }
                            val ruleBottom = 92f // above the real page footer
                            val rulePitch = 16f
                            val ruleTopLimit = lastSourceBottom - 24f
                            val fullRules = ((ruleTopLimit - ruleBottom) / rulePitch).toInt() + 1
                            if (fullRules >= 4) {
                                require(ruleBottom + (fullRules - 1) * rulePitch <=
                                    lastSourceBottom - 24f) {
                                    "C-N3 manual reserve overlaps genuine note content."
                                }
                                stream.saveGraphicsState()
                                stream.setStrokingColor(Color(174, 203, 220))
                                stream.setLineWidth(0.35f)
                                repeat(fullRules) { row ->
                                    val y = ruleBottom + row * rulePitch
                                    stream.moveTo(42f, y)
                                    stream.lineTo(570f, y)
                                    stream.stroke()
                                }
                                stream.restoreGraphicsState()
                            }
                        }
                        // Mixed C2 must preserve the approved blue interior
                        // cartela language for each genuine source-group heading.
                        // Paint ONLY within its already-measured cell: this
                        // cannot consume paper, move source text, or affect the
                        // two-column paginator and manuscript NOTAS geometry.
                        if (title == "C2 - Habilidades, acciones y recursos") {
                            val groupHeadings = pageCells.filter {
                                it.sourceKey.startsWith("c2-heading:")
                            }
                            stream.saveGraphicsState()
                            stream.setNonStrokingColor(Color(217, 236, 245))
                            stream.setStrokingColor(Color(80, 122, 151))
                            stream.setLineWidth(0.55f)
                            groupHeadings.forEach { heading ->
                                val cell = heading.cellPt
                                val cut = minOf(3f, cell.heightPt / 4f)
                                stream.moveTo(cell.leftPt + cut, cell.bottomPt)
                                stream.lineTo(cell.rightPt - cut, cell.bottomPt)
                                stream.lineTo(cell.rightPt, cell.bottomPt + cut)
                                stream.lineTo(cell.rightPt, cell.topPt - cut)
                                stream.lineTo(cell.rightPt - cut, cell.topPt)
                                stream.lineTo(cell.leftPt + cut, cell.topPt)
                                stream.lineTo(cell.leftPt, cell.topPt - cut)
                                stream.lineTo(cell.leftPt, cell.bottomPt + cut)
                                stream.closePath()
                                stream.fillAndStroke()
                            }
                            stream.restoreGraphicsState()
                        }
                        // C213: actual persisted custom attribute/skill grouping.
                        // Paint only inside measured source cells; never alter
                        // text, IDs, wrapping, source order or physical pages.
                        if (title == "C2 - Habilidades, acciones y recursos") {
                            val c2GroupKeys = entries.associate { it.sourceKey to it.groupKey }
                            val c2CustomGroups = pageCells.mapNotNull { cell ->
                                val sourceGroup = c2GroupKeys[cell.sourceKey]
                                if (sourceGroup?.startsWith("custom-attribute-group:") == true)
                                    sourceGroup to cell else null
                            }.groupBy { (group, cell) -> group to cell.columnIndex }
                            c2CustomGroups.forEach { (identity, grouped) ->
                                val column = flow.columnsPt[identity.second - 1]
                                val realCells = grouped.map { it.second }
                                require(realCells.all { column.contains(it.cellPt) })
                                val groupTop = realCells.maxOf { it.cellPt.topPt }
                                val groupBottom = realCells.minOf { it.cellPt.bottomPt }
                                require(groupTop - groupBottom >= body.metrics.lineHeightPt - 0.1f)
                                val linkedKeys = entries.filter {
                                    it.groupKey == identity.first &&
                                        it.sourceKey.startsWith("attribute-skill:")
                                }.map { it.sourceKey }
                                stream.saveGraphicsState()
                                // Alternate rows by real skill identity: wrapped
                                // fragments retain their parent band's parity.
                                stream.setNonStrokingColor(Color(231, 242, 247))
                                realCells.filter {
                                    it.sourceKey.startsWith("attribute-skill:") &&
                                        linkedKeys.indexOf(it.sourceKey) % 2 == 0
                                }.forEach { row ->
                                    stream.addRect(row.cellPt.leftPt, row.cellPt.bottomPt,
                                        row.cellPt.widthPt, row.cellPt.heightPt)
                                    stream.fill()
                                }
                                val headings = realCells.filter {
                                    it.sourceKey.startsWith("attribute:")
                                }
                                if (headings.isNotEmpty()) {
                                    val top = headings.maxOf { it.cellPt.topPt }
                                    val bottom = headings.minOf { it.cellPt.bottomPt }
                                    val left = column.leftPt
                                    val right = column.rightPt
                                    val cut = minOf(2.5f, (top - bottom) / 4f)
                                    stream.setNonStrokingColor(Color(217, 236, 245))
                                    stream.setStrokingColor(Color(80, 122, 151))
                                    stream.setLineWidth(0.55f)
                                    stream.moveTo(left + cut, bottom)
                                    stream.lineTo(right - cut, bottom)
                                    stream.lineTo(right, bottom + cut)
                                    stream.lineTo(right, top - cut)
                                    stream.lineTo(right - cut, top)
                                    stream.lineTo(left + cut, top)
                                    stream.lineTo(left, top - cut)
                                    stream.lineTo(left, bottom + cut)
                                    stream.closePath()
                                    stream.fillAndStroke()
                                }
                                // Narrow gutter rail persists when the header
                                // was printed in a previous physical column.
                                val railX = column.leftPt - 3f
                                require(railX >= 38f && railX + 1.5f < column.leftPt)
                                stream.setNonStrokingColor(Color(80, 122, 151))
                                stream.addRect(railX, groupBottom + 1f, 1.5f,
                                    groupTop - groupBottom - 2f)
                                stream.fill()
                                stream.restoreGraphicsState()
                            }
                        }
                        // C214: bounded source-anchored V11 score/modifier
                        // outlines. Existing printed numbers remain the ONLY
                        // source values; no duplicate score or inferred save
                        // proficiency. Metrics and fragment offsets are the
                        // same as the immutable source text renderer below.
                        if (title == "C2 - Habilidades, acciones y recursos") {
                            pageCells.filter {
                                it.sourceKey.startsWith("attribute:")
                            }.forEach { cell ->
                                val index = cell.sourceKey.removePrefix("attribute:").toIntOrNull()
                                val ability = index?.let { data.attributes.drop(6).getOrNull(it) }
                                requireNotNull(ability) {
                                    "C2 custom ability paper card has no persisted source."
                                }
                                // A heading row, a genuinely measured EMPTY spacer line
                                // and a numerical-value row form a two-level
                                // paper card; no synthetic ability values.
                                val prefix = ability.name + "\n\n"
                                val score = ability.score.toString()
                                val modifier = signedC2(ability.modifier)
                                val saving = ability.savingThrowTotal?.let {
                                    signedC2(it)
                                } ?: "sin valor"
                                val scoreStart = prefix.length
                                val modifierStart = scoreStart + score.length + " / mod ".length
                                val savingStart = modifierStart + modifier.length +
                                    " / salvación ".length
                                val exact = prefix + score + " / mod " + modifier +
                                    " / salvación " + saving
                                require(entries.single { it.sourceKey == cell.sourceKey }.text ==
                                    exact) { "C2 ability ink must match its source, not a guess." }
                                fun printedToken(start: Int, value: String): Pair<Float, Float>? {
                                    val end = start + value.length
                                    if (start < cell.sourceStartOffset ||
                                        end > cell.sourceEndExclusive) return null
                                    val before = cell.sourceText.substring(
                                        0, start - cell.sourceStartOffset)
                                    if (before.any { it == '\r' || it == '\n' || it == '\t' } ||
                                        value.any { it == '\r' || it == '\n' || it == '\t' })
                                        return null
                                    val x = cell.cellPt.leftPt + body.width.measurePt(before)
                                    val right = x + body.width.measurePt(value)
                                    if (x < cell.cellPt.leftPt ||
                                        right > cell.cellPt.rightPt - 1f) return null
                                    return x to right
                                }
                                val scoreInk = printedToken(scoreStart, score)
                                val modifierInk = printedToken(modifierStart, modifier)
                                val savingInk = printedToken(savingStart, saving)
                                if (scoreInk != null || modifierInk != null || savingInk != null) {
                                    val baseline = body.metrics.baselineIn(cell.cellPt)
                                    // The EMPTY line immediately before the
                                    // numeric row is a real measured reservation,
                                    // not a guessed blank. It lets full-height
                                    // score/oval ink extend into that RESERVED
                                    // row without touching the title above.
                                    // If a pathological wrapped item continues
                                    // across a physical column here, retain the
                                    // source and use the prior compact outline.
                                    val reserved = pageCells.firstOrNull { prior ->
                                        prior.sourceKey == cell.sourceKey &&
                                            prior.columnIndex == cell.columnIndex &&
                                            prior.rowIndex + 1 == cell.rowIndex &&
                                            prior.drawableText.isEmpty() &&
                                            prior.sourceEndExclusive == scoreStart &&
                                            kotlin.math.abs(prior.cellPt.bottomPt - cell.cellPt.topPt) <= 0.02f
                                    }
                                    val bottom = baseline - 2.7f
                                    val top = baseline + if (reserved != null) 17f else 8.9f
                                    require(bottom > cell.cellPt.bottomPt &&
                                        top < (reserved?.cellPt?.topPt ?: cell.cellPt.topPt)) {
                                        "C2 custom card must stay within its measured rows."
                                    }
                                    stream.saveGraphicsState()
                                    stream.setStrokingColor(Color(80, 122, 151))
                                    stream.setLineWidth(0.45f)
                                    scoreInk?.let { (left, right) ->
                                        // The tight measured box avoids the real
                                        // attribute name and following slash.
                                        stream.addRect(left - 1.25f, bottom,
                                            right - left + 2.5f, top - bottom)
                                        stream.stroke()
                                    }
                                    modifierInk?.let { (left, right) ->
                                        val cx = (left + right) / 2f
                                        val cy = (top + bottom) / 2f
                                        val rx = (right - left) / 2f + 1.25f
                                        val ry = (top - bottom) / 2f
                                        val k = 0.5522848f
                                        stream.moveTo(cx + rx, cy)
                                        stream.curveTo(cx + rx, cy + ry*k,
                                            cx + rx*k, cy + ry, cx, cy + ry)
                                        stream.curveTo(cx - rx*k, cy + ry,
                                            cx - rx, cy + ry*k, cx - rx, cy)
                                        stream.curveTo(cx - rx, cy - ry*k,
                                            cx - rx*k, cy - ry, cx, cy - ry)
                                        stream.curveTo(cx + rx*k, cy - ry,
                                            cx + rx, cy - ry*k, cx + rx, cy)
                                        stream.closePath()
                                        stream.stroke()
                                    }
                                    savingInk?.let { (_, right) ->
                                        val x = right + 4f
                                        if (x + 7f < cell.cellPt.rightPt - 1f) {
                                            // Blank handwriting/proficiency
                                            // square, never an invented state.
                                            stream.addRect(x, baseline - 1.5f, 7f, 7f)
                                            stream.stroke()
                                        }
                                    }
                                    stream.restoreGraphicsState()
                                }
                            }
                        }
                        // BK07: approved V11 spellbook cards are distinct
                        // clipped-corner physical panels, not unframed prose.
                        // The existing measured cell envelopes and source text
                        // remain IMMUTABLE: this paints behind them only and
                        // never changes a page/column, entry offset or font.
                        // A split spell receives one panel per actual fragment,
                        // without repeating its title or description.
                        if (title == "Libro de conjuros") {
                            val originals = entries.associate {
                                it.sourceKey to it.text
                            }
                            val bookParts = pageCells.filter {
                                it.sourceKey.startsWith("book:")
                            }.groupBy { it.columnIndex to it.sourceKey }
                            stream.saveGraphicsState()
                            bookParts.forEach { (identity, partCells) ->
                                val column = flow.columnsPt[identity.first - 1]
                                val left = column.leftPt - 4f
                                val right = column.rightPt + 4f
                                val top = partCells.maxOf { it.cellPt.topPt }
                                val bottom = partCells.minOf { it.cellPt.bottomPt }
                                require(top - bottom >= body.metrics.lineHeightPt - 0.1f)
                                require(left >= 35f && right <= 577f &&
                                    top <= 703f && bottom >= 75.5f) {
                                    "V11 book card cannot escape the approved Letter paper area."
                                }
                                val cut = minOf(4f, (top - bottom) / 4f)
                                stream.setNonStrokingColor(Color.WHITE)
                                stream.setStrokingColor(Color(38, 75, 104))
                                stream.setLineWidth(0.55f)
                                stream.moveTo(left + cut, bottom)
                                stream.lineTo(right - cut, bottom)
                                stream.lineTo(right, bottom + cut)
                                stream.lineTo(right, top - cut)
                                stream.lineTo(right - cut, top)
                                stream.lineTo(left + cut, top)
                                stream.lineTo(left, top - cut)
                                stream.lineTo(left, bottom + cut)
                                stream.closePath()
                                stream.fillAndStroke()
                                // The real title and level occupy the source
                                // prefix before DESCRIPCIÓN, not a fabricated
                                // school or technical spell field. Shade only
                                // those EXISTING measured lines; no duplicate
                                // text and no invented per-card header row.
                                val raw = originals.getValue(identity.second)
                                val descriptionStart = raw.indexOf("DESCRIPCIÓN:")
                                require(descriptionStart >= 0) {
                                    "V11 essential book card lost its source section label."
                                }
                                stream.setNonStrokingColor(Color(231, 242, 247))
                                partCells.filter {
                                    it.sourceStartOffset < descriptionStart
                                }.forEach { headingCell ->
                                    stream.addRect(left + 1f, headingCell.cellPt.bottomPt,
                                        right - left - 2f, headingCell.cellPt.heightPt)
                                    stream.fill()
                                }
                            }
                            stream.restoreGraphicsState()
                        }
                        pageCells.forEach { cell ->
                            // C219: repeat genuinely persisted P1 total/spent
                            // spell slots on P4's physical SAME-LINE level
                            // meter, as approved D-0076 §§3.25–3.26.
                            // Three separate blank squares remain PAPER ONLY:
                            // their number and state never claim digital slots.
                            // For pathological oversized values retain the
                            // prior blank paper meter rather than clipping real
                            // digits or inferring smaller numbers.
                            if (title.startsWith("P4 - ") &&
                                cell.sourceKey.startsWith("spell-level:") &&
                                cell.sourceStartOffset == 0) {
                                val level = cell.sourceKey.removePrefix("spell-level:")
                                    .toIntOrNull()
                                if (level != null && level in 1..9) {
                                    val baseline = body.metrics.baselineIn(cell.cellPt)
                                    val labelPt = 7f
                                    val regularX = cell.cellPt.rightPt - 83f
                                    val firstSquareX = cell.cellPt.rightPt - 33f
                                    val actualSlots = data.spellSlots.firstOrNull {
                                        it.level == level
                                    }
                                    val fullActualLabel = actualSlots?.let {
                                        "ESP.${it.totalSlots} G.${it.spentSlots}"
                                    }
                                    // C220: when genuine integers do not fit
                                    // the 45pt meter, reuse the actual FREE
                                    // baseline between "NIVEL X" and its three
                                    // 7pt paper squares. NEVER shrink any font,
                                    // split the approved one-line level header,
                                    // change a number or cover the title.
                                    val trueHeading = entries.single {
                                        it.sourceKey == cell.sourceKey
                                    }.text
                                    val headingInkWidth = bodyPdfFont.getStringWidth(
                                        pcSheetV11PrintableText(trueHeading)
                                    ) * 9f / 1000f
                                    val expandedX = cell.cellPt.leftPt +
                                        headingInkWidth + 8f
                                    val chosen = fullActualLabel?.let { sourceLabel ->
                                        val width = headerPdfFont.getStringWidth(
                                            sourceLabel
                                        ) * labelPt / 1000f
                                        when {
                                            width <= firstSquareX - regularX - 5f ->
                                                regularX to sourceLabel
                                            expandedX < regularX &&
                                                width <= firstSquareX - expandedX - 5f ->
                                                expandedX to sourceLabel
                                            else -> null
                                        }
                                    }
                                    val labelX = chosen?.first ?: regularX
                                    val label = chosen?.second ?: "ESP."
                                    require(headerPdfFont.getStringWidth(label) *
                                        labelPt / 1000f <= firstSquareX - labelX - 5f) {
                                        "P4 true slot label exceeds available paper after safe fitting."
                                    }
                                    require(labelX >= cell.cellPt.leftPt +
                                        headingInkWidth + 7f) {
                                        "P4 true slot label overlaps the real level heading."
                                    }
                                    require(firstSquareX + 2 * 11f + 7f <=
                                        cell.cellPt.rightPt - 2f) {
                                        "P4 handwritten level slots exceed measured column."
                                    }
                                    stream.beginText()
                                    stream.setFont(headerPdfFont, labelPt)
                                    stream.newLineAtOffset(labelX, baseline)
                                    stream.showText(label)
                                    stream.endText()
                                    stream.saveGraphicsState()
                                    stream.setStrokingColor(Color(130, 165, 186))
                                    stream.setLineWidth(0.6f)
                                    // Paper-only meter: a GENUINE blue ruled line,
                                    // NEVER literal underscores printed as fake ink.
                                    if (label == "ESP.") {
                                        stream.moveTo(labelX + 24f, baseline - 1f)
                                        stream.lineTo(firstSquareX - 7f, baseline - 1f)
                                        stream.stroke()
                                    }
                                    repeat(3) { slot ->
                                        stream.addRect(firstSquareX + slot * 11f,
                                            baseline - 1f, 7f, 7f)
                                        stream.stroke()
                                    }
                                    stream.restoreGraphicsState()
                                }
                            }
                            // Golden P4 paper-only rows use REAL square marks.
                            // Persisted spell preparation still uses source-specific
                            // text state; blank handwriting has no magical state.
                            if (title.startsWith("P4 - ") &&
                                cell.sourceKey.startsWith("spell-handwritten:") &&
                                cell.sourceStartOffset == 0 &&
                                cell.drawableText.startsWith("[ ] ")) {
                                val baseline = body.metrics.baselineIn(cell.cellPt)
                                stream.saveGraphicsState()
                                stream.setStrokingColor(Color(130, 165, 186))
                                stream.setLineWidth(0.65f)
                                stream.addRect(cell.cellPt.leftPt + 2f, baseline - 1f, 7f, 7f)
                                stream.stroke()
                                stream.restoreGraphicsState()
                                // Approved V11 P4 uses *blank ruled paper*, not
                                // dark printed underscore glyphs. The measured
                                // source placeholder remains in the flow model
                                // to reserve one genuine row per level. Its
                                // physical vector rule was already drawn above,
                                // and the preparation square was drawn here.
                                // Never replace the paper line with digital text.
                            } else if (title.startsWith("P4 - ") &&
                                cell.sourceKey.startsWith("quick-spell:") &&
                                cell.sourceStartOffset == 0) {
                                // Every real spell row reserves its own 7pt PAPER
                                // preparation box. Text still contains the exact
                                // persisted per-source prepared status; a paper
                                // square NEVER asserts a digital prepared value.
                                val baseline = body.metrics.baselineIn(cell.cellPt)
                                stream.saveGraphicsState()
                                stream.setStrokingColor(Color(130, 165, 186))
                                stream.setLineWidth(0.65f)
                                stream.addRect(cell.cellPt.leftPt + 2f, baseline - 1f, 7f, 7f)
                                stream.stroke()
                                stream.restoreGraphicsState()
                                if (cell.drawableText.isNotEmpty()) {
                                    draw(stream, cell.drawableText, cell.cellPt.leftPt,
                                        baseline, false)
                                }
                                p4RightSources[cell.sourceKey]?.let { realSource ->
                                    val width = p4SourceFont.width.measurePt(realSource)
                                    val x = cell.cellPt.rightPt - width - 2f
                                    require(x >= cell.cellPt.leftPt +
                                        body.width.measurePt(cell.drawableText) + 10f) {
                                        "Source-backed P4 right label overlaps the spell name."
                                    }
                                    stream.beginText()
                                    stream.setFont(bodyPdfFont, 7f)
                                    stream.newLineAtOffset(x, baseline)
                                    stream.showText(pcSheetV11PrintableText(realSource))
                                    stream.endText()
                                }
                            } else if (
                                title == "C2 - Habilidades, acciones y recursos" &&
                                cell.sourceKey.startsWith("c2-heading:") &&
                                cell.drawableText.isNotEmpty()
                            ) {
                                // Group cartelas use the G2-approved Barlow
                                // label font, NOT the Fira body-value font.
                                // Their literal bytes and reserved cell remain
                                // unchanged. Strict actual embedded-font width
                                // verification prevents clipping or overflow.
                                val fontSize = 9f
                                val inset = 5f
                                val inkWidth = headerPdfFont.getStringWidth(
                                    cell.drawableText,
                                ) * fontSize / 1000f
                                require(inkWidth <= cell.cellPt.widthPt - inset - 1f) {
                                    "C2 source heading cannot fit its real Barlow cartela."
                                }
                                stream.beginText()
                                stream.setFont(headerPdfFont, fontSize)
                                stream.newLineAtOffset(
                                    cell.cellPt.leftPt + inset,
                                    body.metrics.baselineIn(cell.cellPt),
                                )
                                stream.showText(pcSheetV11PrintableText(cell.drawableText))
                                stream.endText()
                            } else if (title == "C2 - Habilidades, acciones y recursos" &&
                                cell.sourceKey.startsWith("attribute-skill:") &&
                                cell.sourceStartOffset == 0) {
                                // C217: reposition the unique REAL skill total
                                // into the golden's right-value edge. Full
                                // source identity and training stay printed.
                                // Long/wrapped skills keep their prior safe ink.
                                val wholeSource = entries.single {
                                    it.sourceKey == cell.sourceKey
                                }.text
                                val trainingSep = wholeSource.lastIndexOf(" / ")
                                val valueSep = if (trainingSep >= 0)
                                    wholeSource.lastIndexOf(": ", trainingSep) else -1
                                if (cell.sourceEndExclusive == wholeSource.length &&
                                    valueSep > 0 && trainingSep > valueSep + 2) {
                                    val realTotal = wholeSource.substring(
                                        valueSep + 2, trainingSep)
                                    val realTraining = wholeSource.substring(trainingSep + 3)
                                    val leftInk = wholeSource.substring(0, valueSep) +
                                        " (" + realTraining + ")"
                                    val rightX = cell.cellPt.rightPt -
                                        body.width.measurePt(realTotal) - 4f
                                    val labelEnd = cell.cellPt.leftPt +
                                        body.width.measurePt(leftInk)
                                    if (realTotal.isNotBlank() && realTraining.isNotBlank() &&
                                        labelEnd + 9f <= rightX) {
                                        val baseline = body.metrics.baselineIn(cell.cellPt)
                                        draw(stream, leftInk, cell.cellPt.leftPt, baseline, false)
                                        draw(stream, realTotal, rightX, baseline, false)
                                    } else {
                                        draw(stream, cell.drawableText, cell.cellPt.leftPt,
                                            body.metrics.baselineIn(cell.cellPt), false)
                                    }
                                } else if (cell.drawableText.isNotEmpty()) {
                                    draw(stream, cell.drawableText, cell.cellPt.leftPt,
                                        body.metrics.baselineIn(cell.cellPt), false)
                                }
                            } else if (cell.drawableText.isNotEmpty()) {
                                draw(stream, cell.drawableText,
                                    cell.cellPt.leftPt,
                                    body.metrics.baselineIn(cell.cellPt),
                                    false)
                            }
                            // C216: the approved C2 paper uses an EMPTY
                            // manual checkbox for each real linked custom skill,
                            // not a literal "[ ]" glyph or inferred training.
                            // The source flow reserves six measured spaces so
                            // this 7pt square cannot obscure the printed name.
                            // Wrapped continuations never gain a second box.
                            if (title == "C2 - Habilidades, acciones y recursos" &&
                                cell.sourceKey.startsWith("attribute-skill:") &&
                                cell.sourceStartOffset == 0) {
                                require(cell.sourceText.startsWith("      Habilidad ")) {
                                    "C2 linked skill square lacks its measured source inset."
                                }
                                require(body.width.measurePt("      ") >= 11f) {
                                    "C2 paper skill checkbox needs genuine font-measured clearance."
                                }
                                val base = body.metrics.baselineIn(cell.cellPt)
                                stream.saveGraphicsState()
                                stream.setStrokingColor(Color(130, 165, 186))
                                stream.setLineWidth(0.65f)
                                stream.addRect(cell.cellPt.leftPt + 2f, base - 1f, 7f, 7f)
                                stream.stroke()
                                stream.restoreGraphicsState()
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
                        // P3 now draws its own two-column ruled writing grammar,
                        // before the source text, including continuation pages.
                    }
                }
            }

            val identity = buildList {
                data.attributes.drop(6).forEachIndexed { index, attribute ->
                    val alreadyPrintedOnP1 = index + 6 < p1PrintedAttributeCount
                    val customC2Group = if (alreadyPrintedOnP1) null else
                        "custom-attribute-group:" + index
                    if (!alreadyPrintedOnP1) {
                        add(PcSheetV11FlowItem(
                            "attribute:" + index,
                            attribute.name + "\n\n" + attribute.score +
                                " / mod " + signedC2(attribute.modifier) +
                                " / salvación " +
                                (attribute.savingThrowTotal?.let {
                                    signedC2(it)
                                } ?: "sin valor"),
                            keepTogetherIfFits = true,
                            groupKey = customC2Group,
                        ))
                    }
                    data.skills.withIndex()
                        .filter { (skillIndex, skill) -> skill.customSkillId != null &&
                            skill.ability == attribute.reference &&
                            skillIndex !in printedSkillIndices }
                        .forEach { (_, skill) ->
                            add(PcSheetV11FlowItem(
                                "attribute-skill:" + skill.customSkillId,
                                // Six spaces are measurable handwriting
                                // clearance for the genuine 7pt paper box.
                                "      Habilidad " + skill.name +
                                    // The source ability is still explicit if
                                    // the full attribute was already placed P1.
                                    (if (alreadyPrintedOnP1) attribute.abbreviation
                                        .takeIf { it.isNotBlank() }
                                        ?.let { " (" + approvedAbilityAbbreviation(it) + ")" }
                                        ?: "" else "") +
                                    ": " + (skill.total?.let {
                                        signedC2(it)
                                    } ?: "sin resolver") +
                                    " / " + skill.training.name,
                                keepTogetherIfFits = true,
                                groupKey = customC2Group,
                            ))
                        }
                }
                if (!variantA) {
                    data.skills.forEachIndexed { index, skill ->
                        if (skill.builtInKey == null &&
                            skill.ability !in customAttributeReferences) {
                            val abbreviation = data.attributes.firstOrNull {
                                it.reference == skill.ability
                            }?.abbreviation?.let { approvedAbilityAbbreviation(it) }
                            add(PcSheetV11FlowItem("skill:" + index,
                                "Habilidad " + skill.name +
                                    (abbreviation?.let { " (" + it + ")" } ?: "") +
                                    ": " + (skill.total?.let {
                                        signedC2(it)
                                    } ?: "sin resolver") +
                                    " / " + skill.training.name))
                        }
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
            // Keep variable-height mechanics beneath the fixed approved identity
            // cartelas, never underneath their labels or beside the portrait.
            val p1Unprinted=listOfNotNull(headerNameRoute.continuation)+
                identityFieldOverflow+bSkillOverflow+identity
            addSection("P1 - Personaje / combate / habilidades",
                PcSheetV11FlowSection.TRAITS,emptyList(),2,alwaysPage=true,
                area=PcSheetV11RectPt(42f,90f,258f,500f),maxPhysicalPages=1)

            // Approved golden P2 has the EQUIPO/TESORO stack on the LEFT,
            // mechanical RASGOS on the RIGHT, and full-width EQUIPO ESPECIAL
            // below them. The source-backed composers retain measured overflow
            // and forward it to C2, never to narrative P3.
            val equipmentArea = PcSheetV11RectPt(42f, 464f, 258f, 220f)
            val treasureArea = PcSheetV11RectPt(42f, 330f, 258f, 118f)
            val traitsArea = PcSheetV11RectPt(312f, 330f, 258f, 354f)
            val specialArea = PcSheetV11RectPt(42f, 88f, 528f, 225f)
            require(equipmentArea.rightPt + 12f == traitsArea.leftPt)
            require(equipmentArea.bottomPt > treasureArea.topPt)
            require(traitsArea.bottomPt > specialArea.topPt)
            require(specialArea.widthPt == 528f)
            val completeEquipment = PcSheetV11PairedTableSources.equipment(data)
            val completeTreasure = PcSheetV11PairedTableSources.treasure(data)
            // P2 has narrow golden-approved paired subcolumns. A name whose
            // real glyphs cannot fit one P2 label row must move WHOLE to the
            // wider C2 table, not split its identity at a page boundary.
            // C2 prints its amount exactly once; P2 retains blank paper rows.
            fun rowsForP2(
                input: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData,
                area: PcSheetV11RectPt,
            ): io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData =
                PcSheetV11PairedAmountRouting.p2Eligible(input, area, 12f, body.width)
            val inventoryPlaced = PcSheetV11PairedTableComposer.place(
                rowsForP2(completeEquipment, equipmentArea),
                equipmentArea, 12f, 36f, 6f, body.metrics.lineHeightPt, body.width,
            )
            val treasurePlaced = PcSheetV11PairedTableComposer.place(
                rowsForP2(completeTreasure, treasureArea),
                treasureArea, 12f, 36f, 6f, body.metrics.lineHeightPt, body.width,
            )
            // P2 contains ONLY source IDs not already printed on P1.
            // G2 flow and mixed C2 extend these summary records losslessly;
            // description/notes still exist in the App source, but NEVER print
            // in Rasgos y Atributos on the V11 paper sheet.
            val traitLines = data.traits
                .filterNot { it.id in p1PrintedTraitIds }
                .map { trait ->
                    PcSheetV11FlowItem("p2-trait:" + trait.id,
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TraitSummary.text(trait),
                        keepTogetherIfFits = true)
                }
            val traitsPlaced = body.place(PcSheetV11FlowSection.TRAITS, traitLines,
                traitsArea, 2, 12f)
            // V11 golden P2: location / name / description / ACTIVO belong
            // to ONE physical row per special item, not four paragraphs.
            // A row is published on P2 ONLY when all of its REAL source text
            // fits legibly in two measured lines per cell; the complete
            // remaining source suffix goes to full-width C2 untruncated.
            val specialColumns = listOf(105f, 136f, 241f, 46f)
            require(specialColumns.sum() == specialArea.widthPt)
            val specialHeaderHeight = 18f
            val specialRowHeight = 25f
            val specialCapacity = ((specialArea.heightPt - specialHeaderHeight) /
                specialRowHeight).toInt()
            fun specialCellLines(raw: String?, widthPt: Float): List<String>? {
                if (raw.isNullOrBlank()) return emptyList()
                val text = raw
                val width = widthPt - 10f
                // The verified text-flow uses the exact PDF's Fira font width.
                val printable = PcSheetV11TextFlow.paginate(
                    PcSheetV11FlowSection.SPECIAL_EQUIPMENT,
                    listOf(PcSheetV11FlowItem("special-cell", text,
                        keepTogetherIfFits = true)),
                    1,
                    PcSheetV11TextColumn(width, body.metrics.lineHeightPt * 2f,
                        body.metrics.lineHeightPt),
                    body.width,
                )
                if (printable.localPageCount != 1) return null
                val ranges = printable.fragments.flatMap { it.lineRanges }
                if (ranges.size > 2) return null
                return ranges.map { text.substring(it.startOffset, it.endExclusive)
                    .trimEnd('\r', '\n') }
            }
            val specialP2Rows = mutableListOf<Pair<
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Equipment,
                List<List<String>>>>()
            for (item in data.specialEquipment) {
                if (specialP2Rows.size >= specialCapacity) break
                val details = listOfNotNull(
                    item.description?.takeIf { it.isNotBlank() },
                    item.notes?.takeIf { it.isNotBlank() }?.let { "NOTAS: " + it },
                ).joinToString(" / ")
                val fields = listOf(item.location, item.name, details)
                val lines = fields.mapIndexed { index, field ->
                    specialCellLines(field, specialColumns[index])
                }
                if (lines.any { it == null }) break
                specialP2Rows += item to lines.map { requireNotNull(it) }
            }
            val specialDeferredToC2 = data.specialEquipment.drop(specialP2Rows.size)
            require(specialP2Rows.size + specialDeferredToC2.size ==
                data.specialEquipment.size)

            fun grid(
                stream: PDPageContentStream,
                rect: PcSheetV11RectPt,
                twoColumns: Boolean,
                manuscriptRowHeightPt: Float = body.metrics.lineHeightPt,
                alternateP2Rows: Boolean = false,
            ) {
                require(manuscriptRowHeightPt.isFinite() && manuscriptRowHeightPt >=
                    body.metrics.lineHeightPt) { "Writing rows cannot be compressed." }
                // Blue mist writing bands and fine rules match the approved V11
                // paper grammar; black default strokes belong only to PREQA.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(238, 247, 251))
                stream.addRect(rect.leftPt, rect.bottomPt, rect.widthPt, rect.heightPt)
                stream.fill()
                // Golden P2 writes against alternating blue-mist rows, not
                // an undifferentiated blue table. Opt in on P2 ONLY: this
                // shared function also serves C2, whose proven geometry and
                // complete manuscript grids must remain unchanged.
                if (alternateP2Rows) {
                    val complete = (rect.heightPt / manuscriptRowHeightPt).toInt()
                    stream.setNonStrokingColor(Color(231, 242, 247))
                    for (row in 0 until complete step 2) {
                        val top = rect.topPt - row * manuscriptRowHeightPt
                        val bottom = maxOf(rect.bottomPt, top - manuscriptRowHeightPt)
                        if (top > bottom) {
                            stream.addRect(rect.leftPt, bottom, rect.widthPt, top-bottom)
                            stream.fill()
                        }
                    }
                }
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.45f)
                stream.addRect(rect.leftPt, rect.bottomPt, rect.widthPt, rect.heightPt)
                stream.stroke()
                if (twoColumns) {
                    val center = rect.leftPt + rect.widthPt / 2f
                    stream.moveTo(center, rect.bottomPt)
                    stream.lineTo(center, rect.topPt)
                    stream.stroke()
                }
                val cells = (rect.heightPt / manuscriptRowHeightPt).toInt()
                for (line in 1 until cells) {
                    val y = rect.topPt - line * manuscriptRowHeightPt
                    stream.moveTo(rect.leftPt, y)
                    stream.lineTo(rect.rightPt, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
            }

            // P2 paper grammar in the approved golden: all four source-backed
            // module titles are clipped blue cartelas, not floating headings.
            // The fixed 14.5pt heading strip is already outside every measured
            // equipment/treasure/traits/special table, so no row, ID or page
            // allocation moves to accommodate decoration.
            fun drawP2Cartela(
                stream: PDPageContentStream,
                title: String,
                area: PcSheetV11RectPt,
                columnLegend: String? = null,
            ) {
                val left = area.leftPt
                val right = area.rightPt
                val bottom = area.topPt
                val top = bottom + 14.5f
                val corner = 5f
                require(top < 717f) { "P2 cartela cannot hit the physical page header" }
                require(headerPdfFont.getStringWidth(title) * 11f / 1000f <=
                    area.widthPt - 18f) {
                    "P2 title cannot fit inside its approved source-free cartela: " + title
                }
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(217, 236, 245))
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.6f)
                stream.moveTo(left + corner, bottom)
                stream.lineTo(right - corner, bottom)
                stream.lineTo(right, bottom + corner)
                stream.lineTo(right, top - corner)
                stream.lineTo(right - corner, top)
                stream.lineTo(left + corner, top)
                stream.lineTo(left, top - corner)
                stream.lineTo(left, bottom + corner)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                stream.beginText()
                stream.setFont(headerPdfFont, 11f)
                stream.newLineAtOffset(left + 9f, bottom + 3f)
                stream.showText(title)
                stream.endText()
                // Golden P2 requires an explicit meaning for BOTH small
                // internal columns: OBJETO/CANTIDAD or DENOMINACIÓN/CANTIDAD.
                // Occupy the existing blue source-free cartela, never the table.
                columnLegend?.let { legend ->
                    val legendSize = 7f
                    val width = headerPdfFont.getStringWidth(legend) *
                        legendSize / 1000f
                    val titleWidth = headerPdfFont.getStringWidth(title) *
                        11f / 1000f
                    val x = right - 9f - width
                    require(x > left + 9f + titleWidth + 8f) {
                        "P2 paired-table legend overlaps its approved heading."
                    }
                    stream.beginText()
                    stream.setFont(headerPdfFont, legendSize)
                    stream.newLineAtOffset(x, bottom + 4f)
                    stream.showText(legend)
                    stream.endText()
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
                writeC2Notes: Boolean = true,
            ) {
                if (!compactC2) {
                    val ordinary = placed.data.kind ==
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableKind.EQUIPMENT
                    drawP2Cartela(
                        stream, if (ordinary) "EQUIPO" else "TESORO", area,
                        if (ordinary) "OBJETO / CANTIDAD" else "DENOMINACIÓN / CANTIDAD",
                    )
                }
                val pageLabels = placed.labels.filter { it.localPageIndex == localPage }
                val lowestPrintedBottom = pageLabels.minOfOrNull { it.rectPt.bottomPt }
                // Unlike P2, C2's last table page must free genuine unused paper
                // for handwritten NOTES, without drawing empty object/quantity rows
                // underneath. These bounds come from measured source cells.
                val notesUsable = compactC2 && writeC2Notes &&
                    lowestPrintedBottom != null &&
                    ((lowestPrintedBottom - 28f - 78f) / 16f).toInt() >= 3
                val tableBottom = if (notesUsable) requireNotNull(lowestPrintedBottom)
                    else area.bottomPt
                val visibleTable = PcSheetV11RectPt(
                    area.leftPt, tableBottom, area.widthPt, area.topPt - tableBottom,
                )
                // P203/P206: a source-free P2 table is handwriting paper, not
                // a compressed digital-data table. The golden has broader
                // writing lines (Equipo ~16.5pt, Tesoro ~19.5pt). Retain
                // the original real-font line rhythm whenever any data is printed,
                // and never alter C2 pagination or original source identities.
                val manuscriptRowHeight = if (!compactC2 && pageLabels.isEmpty()) {
                    when (placed.data.kind) {
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableKind.EQUIPMENT -> 16.5f
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableKind.TREASURE -> 19.5f
                    }
                } else body.metrics.lineHeightPt
                grid(stream, visibleTable, true, manuscriptRowHeight,
                    alternateP2Rows = !compactC2)
                if (compactC2) {
                    // Approved C2 paper grammar: a real pale-blue cartela
                    // with clipped corners, within the EXISTING 14.5pt
                    // strip between the table and fixed header rule.
                    // The title previously overlapped that rule by 3pt.
                    // Font glyph extents at 11pt stay inside this measured
                    // strip, while all source table cells remain unchanged.
                    val left = area.leftPt
                    val right = area.rightPt
                    val bottom = area.topPt
                    val top = bottom + 14.5f
                    val corner = 5f
                    require(top < 717f) {
                        "C2 cartela cannot cross the fixed physical header rule."
                    }
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(217, 236, 245))
                    stream.setStrokingColor(Color(80, 122, 151))
                    stream.setLineWidth(0.6f)
                    stream.moveTo(left + corner, bottom)
                    stream.lineTo(right - corner, bottom)
                    stream.lineTo(right, bottom + corner)
                    stream.lineTo(right, top - corner)
                    stream.lineTo(right - corner, top)
                    stream.lineTo(left + corner, top)
                    stream.lineTo(left, top - corner)
                    stream.lineTo(left, bottom + corner)
                    stream.closePath()
                    stream.fillAndStroke()
                    stream.restoreGraphicsState()
                    stream.beginText()
                    stream.setFont(headerPdfFont, 11f)
                    stream.newLineAtOffset(left + 9f, bottom + 3f)
                    stream.showText(title)
                    stream.endText()
                }
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.45f)
                placed.pairs.forEach { pair ->
                    // The quantity separator belongs to its ACTUAL measured column.
                    // Preserve the original -42pt separator for normal 36pt rows.
                    val amountWidth = placed.quantities.firstOrNull()?.rectPt?.widthPt
                        ?: PcSheetV11PairedAmountRouting.BASE_AMOUNT_WIDTH_PT
                    val quantitySeparator = pair.rightPt -
                        amountWidth - PcSheetV11PairedAmountRouting.AMOUNT_GAP_PT
                    stream.moveTo(quantitySeparator, visibleTable.bottomPt)
                    stream.lineTo(quantitySeparator, pair.topPt)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
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
                // C207: on a FULL-HEIGHT C2 paired-table page, one real
                // object/amount pair may end hundreds of points above the
                // other. That blank pair is handwriting paper, not dozens
                // of source-free (and unusable) EQUIPO/TESORO rows. A full
                // bottom NOTES grid already takes priority when it fits.
                if (compactC2 && !notesUsable) {
                    val printedBottoms = (1..2).map { pair ->
                        pageLabels.filter { it.pairIndex == pair }
                            .minOfOrNull { it.rectPt.bottomPt }
                    }
                    for (pairZero in 0..1) {
                        val availableFrom = printedBottoms[pairZero] ?: continue
                        val longerSideBottom = printedBottoms[1 - pairZero] ?: continue
                        // Require a genuine asymmetric source stream; never
                        // replace cells on the longer, still occupied side.
                        if (availableFrom - longerSideBottom < 128f) continue
                        val pair = placed.pairs[pairZero]
                        val gridPitch = 16f
                        val gridBottom = maxOf(area.bottomPt + 2f, 78f)
                        val completeRows =
                            ((availableFrom - 28f - gridBottom) / gridPitch).toInt()
                        if (completeRows < 4) continue
                        val gridTop = gridBottom + completeRows * gridPitch
                        require(gridTop + 18f <= availableFrom - 10f) {
                            "C2 paired-table side notes would overlap source cells."
                        }
                        val completeColumns = (pair.widthPt / gridPitch).toInt()
                        require(completeColumns >= 4)
                        val gridWidth = completeColumns * gridPitch
                        val gridLeft = pair.leftPt + (pair.widthPt - gridWidth) / 2f
                        // The original table covers the entire physical
                        // height. Clear ONLY the shorter pair's unused cells
                        // before drawing a complete source-free NOTES grid.
                        stream.saveGraphicsState()
                        stream.setNonStrokingColor(Color.WHITE)
                        stream.addRect(pair.leftPt + 0.6f, area.bottomPt + 0.6f,
                            pair.widthPt - 1.2f, gridTop + 18f - area.bottomPt - 0.6f)
                        stream.fill()
                        stream.restoreGraphicsState()
                        drawC2ManuscriptCartela(stream, gridLeft, gridWidth, gridTop)
                        stream.saveGraphicsState()
                        stream.setStrokingColor(Color(130, 165, 186))
                        stream.setLineWidth(0.5f)
                        for (column in 0..completeColumns) {
                            val x = gridLeft + column * gridPitch
                            stream.moveTo(x, gridBottom)
                            stream.lineTo(x, gridTop)
                            stream.stroke()
                        }
                        for (row in 0..completeRows) {
                            val y = gridBottom + row * gridPitch
                            stream.moveTo(gridLeft, y)
                            stream.lineTo(gridLeft + gridWidth, y)
                            stream.stroke()
                        }
                        stream.restoreGraphicsState()
                    }
                }
                if (notesUsable) drawUnusedC2NotesGrid(stream, tableBottom)
            }

            fun drawP2Modules(stream: PDPageContentStream, localPage: Int) {
                drawPaired(stream, "EQUIPO   /   CANTIDAD", equipmentArea, inventoryPlaced, localPage)
                drawPaired(stream, "TESORO   /   CANTIDAD", treasureArea, treasurePlaced, localPage)
                drawP2Cartela(stream, "RASGOS Y ATRIBUTOS", traitsArea)
                grid(stream, traitsArea, true, alternateP2Rows = true)
                traitsPlaced.cells.filter { it.localPageIndex == localPage }.forEach { cell ->
                    if (cell.drawableText.isNotEmpty()) {
                        draw(stream, cell.drawableText, cell.cellPt.leftPt + 2f,
                            body.metrics.baselineIn(cell.cellPt), false)
                    }
                }
                drawP2Cartela(stream, "EQUIPO ESPECIAL", specialArea)
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.55f)
                stream.setNonStrokingColor(Color(217, 236, 245))
                stream.addRect(specialArea.leftPt,
                    specialArea.topPt - specialHeaderHeight,
                    specialArea.widthPt, specialHeaderHeight)
                stream.fill()
                stream.setNonStrokingColor(Color(238, 247, 251))
                stream.addRect(specialArea.leftPt, specialArea.bottomPt,
                    specialArea.widthPt,
                    specialArea.heightPt - specialHeaderHeight)
                stream.fill()
                // Source-backed EQUIPO ESPECIAL must share golden P2's
                // alternating writing-row grammar. Paper-only fill is
                // behind genuine location/name/description/ACTIVO values
                // and their original vector marks; no new digital rows.
                stream.setNonStrokingColor(Color(231, 242, 247))
                for (row in 0 until specialCapacity step 2) {
                    val top = specialArea.topPt - specialHeaderHeight -
                        row * specialRowHeight
                    val bottom = maxOf(specialArea.bottomPt, top - specialRowHeight)
                    if (top > bottom) {
                        stream.addRect(specialArea.leftPt, bottom,
                            specialArea.widthPt, top-bottom)
                        stream.fill()
                    }
                }
                stream.addRect(specialArea.leftPt, specialArea.bottomPt,
                    specialArea.widthPt, specialArea.heightPt)
                stream.stroke()
                var boundary = specialArea.leftPt
                specialColumns.dropLast(1).forEach { width ->
                    boundary += width
                    stream.moveTo(boundary, specialArea.bottomPt)
                    stream.lineTo(boundary, specialArea.topPt)
                    stream.stroke()
                }
                for (index in 0..specialCapacity) {
                    val lineY = specialArea.topPt - specialHeaderHeight -
                        index * specialRowHeight
                    stream.moveTo(specialArea.leftPt, lineY)
                    stream.lineTo(specialArea.rightPt, lineY)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
                val headings = listOf("UBICACIÓN", "NOMBRE", "DESCRIPCIÓN", "ACTIVO")
                var headerX = specialArea.leftPt
                headings.forEachIndexed { index, label ->
                    draw(stream, label, headerX + 5f, specialArea.topPt - 13f, false)
                    headerX += specialColumns[index]
                }
                val fieldX = listOf(
                    specialArea.leftPt,
                    specialArea.leftPt + specialColumns[0],
                    specialArea.leftPt + specialColumns[0] + specialColumns[1],
                )
                val activeX = specialArea.rightPt - specialColumns.last() / 2f - 4.5f
                specialP2Rows.forEachIndexed { rowIndex, (_, columns) ->
                    val rowTop = specialArea.topPt - specialHeaderHeight -
                        rowIndex * specialRowHeight
                    columns.forEachIndexed { column, values ->
                        values.forEachIndexed { line, value ->
                            if (value.isNotEmpty()) {
                                draw(stream, value, fieldX[column] + 5f,
                                    rowTop - 10f - line * body.metrics.lineHeightPt,
                                    false)
                            }
                        }
                    }
                }
                // D-0076 §3.42 / P210: only for zero recorded special items,
                // preprint locations used by real QA character fixtures.
                // Leave free rows for arbitrary locations; do not create objects.
                val manualSpecialLocations = if (data.specialEquipment.isEmpty()) {
                    listOf("Cuello", "Cuerpo", "Mano izquierda", "Mano derecha")
                } else emptyList()
                require(manualSpecialLocations.size < specialCapacity)
                repeat(specialCapacity) { rowIndex ->
                    val rowTop = specialArea.topPt - specialHeaderHeight -
                        rowIndex * specialRowHeight
                    if (data.specialEquipment.isEmpty()) {
                        manualSpecialLocations.getOrNull(rowIndex)?.let { location ->
                            draw(stream, location, fieldX[0] + 5f, rowTop - 10f, false)
                        }
                    }
                    // Each writable row requires a genuine unmarked ACTIVO square.
                    val activeY = rowTop - specialRowHeight / 2f - 4.5f
                    stream.addRect(activeX, activeY, 9f, 9f)
                    stream.stroke()
                    if (specialP2Rows.getOrNull(rowIndex)?.first?.active == true) {
                        draw(stream, "X", activeX, activeY + 2f, false)
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

            PDPage(PDRectangle.LETTER).also { page ->
                pdf.addPage(page)
                physicalSectionKeys += "P3 - Trasfondo / Perfil / Historia"
                PDPageContentStream(pdf, page).use { stream ->
                    drawFrameAndHeader(stream, "P3 - Trasfondo / Perfil / Historia")
                    drawP3FirstPage(stream)
                }
            }
            addSection("P3 - Narrativa / continuación", PcSheetV11FlowSection.NARRATIVE,
                p3Overflow, 2, area = PcSheetV11RectPt(42f, 76f, 528f, 597f))


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
            val ammunitionTracks = ammunitionItems.filter {
                it.quantity in 0..20 && it.id !in p1AmmoIds
            }
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
                    if (item.id !in p1AmmoIds) {
                        add(PcSheetV11FlowItem(
                            sourceKey = "ammo-track:" + item.id,
                            text = if (item.quantity in 0..20) "MARCAS:" else
                                "MARCAS: ____ / " + item.quantity,
                            keepTogetherIfFits = true,
                        ))
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
                if (remainingTraits.isNotEmpty()) {
                    // Source-accurate P2 continuation follows the P1 streams
                    // inside the same TWO-column measured C2. Extra pages are
                    // generated only if actual text requires them.
                    add(PcSheetV11FlowItem("c2-heading:traits",
                        "RASGOS / CONTINUACIÓN P2", keepTogetherIfFits = true))
                    addAll(remainingTraits)
                }
            }
            addSection("C2 - Habilidades, acciones y recursos",
                PcSheetV11FlowSection.TRAITS,
                mixedC2, columns = 2, ammunitionTracks = ammunitionTracks)

            // Reflow the exact P2-unprinted equipment/treasure suffixes into
            // FULL-HEIGHT C2 paired tables. Never redraw quantities already
            // printed on P2; keep every original label code point and source ID.
            fun remainingPairedRows(
                complete: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData,
                placed: PcSheetV11PairedTablePlaced,
            ): io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData {
                val p2Eligible = placed.data.rows.map { it.sourceKey }.toSet()
                val rows = complete.rows.mapNotNull { row ->
                    if (row.sourceKey !in p2Eligible) return@mapNotNull row
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
                return complete.copy(rows = rows)
            }

            val fullC2PairedArea = PcSheetV11RectPt(42f, 76f, 528f, 626f)
            val pairedRemainders = listOf(
                Triple("EQUIPO", completeEquipment, inventoryPlaced),
                Triple("TESORO", completeTreasure, treasurePlaced),
            ).map { (label, complete, original) ->
                Triple(label, remainingPairedRows(complete, original), original)
            }
            fun placeC2Paired(
                remaining: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData,
                area: PcSheetV11RectPt,
            ): PcSheetV11PairedTablePlaced = PcSheetV11PairedTableComposer.place(
                remaining, area, 12f,
                PcSheetV11PairedAmountRouting.c2AmountWidth(remaining, body.width),
                PcSheetV11PairedAmountRouting.AMOUNT_GAP_PT,
                body.metrics.lineHeightPt, body.width,
                balanceSinglePageC2 = true,
            )
            data class CompactPairedC2(
                val topArea: PcSheetV11RectPt,
                val bottomArea: PcSheetV11RectPt,
                val equipment: PcSheetV11PairedTablePlaced,
                val treasure: PcSheetV11PairedTablePlaced,
            )
            // C203/C205: if both genuine continuations fit on one physical
            // sheet, keep their separate PAIR grammars but stack the two
            // modules. Formerly even five short equipment items plus three
            // currencies forced two wasteful C2 pages. Never squeeze font,
            // move P2 data, truncate labels, or join separate source IDs.
            // Oversized quantities and dense/multipage tables retain their
            // existing dedicated full-height C2 path without changes.
            val compactPaired = if (pairedRemainders.all { it.second.rows.isNotEmpty() } &&
                pairedRemainders.all { (_, remaining, _) ->
                    PcSheetV11PairedAmountRouting.c2AmountWidth(remaining, body.width) <=
                        PcSheetV11PairedAmountRouting.BASE_AMOUNT_WIDTH_PT
                }
            ) {
                val equipmentRemainder = pairedRemainders[0].second
                val treasureRemainder = pairedRemainders[1].second
                val fullEquipment = placeC2Paired(equipmentRemainder, fullC2PairedArea)
                val fullTreasure = placeC2Paired(treasureRemainder, fullC2PairedArea)
                if (fullEquipment.localPageCount == 1 && fullTreasure.localPageCount == 1) {
                    val occupiedEquipment = (fullEquipment.labels.maxOfOrNull {
                        it.rowIndex + 1
                    } ?: 0) * body.metrics.lineHeightPt
                    // 14.5pt blue cartela ABOVE each module, plus a 5.5pt
                    // physical gap. The two content rectangles never overlap.
                    val upperHeight = maxOf(70f, occupiedEquipment + 12f)
                    val lowerTop = fullC2PairedArea.topPt - upperHeight - 20f
                    if (lowerTop > fullC2PairedArea.bottomPt + 55f) {
                        val topArea = PcSheetV11RectPt(
                            42f, fullC2PairedArea.topPt - upperHeight, 528f, upperHeight,
                        )
                        val bottomArea = PcSheetV11RectPt(
                            42f, fullC2PairedArea.bottomPt, 528f,
                            lowerTop - fullC2PairedArea.bottomPt,
                        )
                        val top = placeC2Paired(equipmentRemainder, topArea)
                        val bottom = placeC2Paired(treasureRemainder, bottomArea)
                        if (top.localPageCount == 1 && bottom.localPageCount == 1) {
                            CompactPairedC2(topArea, bottomArea, top, bottom)
                        } else null
                    } else null
                } else null
            } else null
            data class DeferredOrdinaryForSpecial(
                val label: String,
                val original: PcSheetV11PairedTablePlaced,
                val expanded: PcSheetV11PairedTablePlaced,
                val upperHeight: Float,
            )
            var deferredOrdinaryForSpecial: DeferredOrdinaryForSpecial? = null
            fun drawSeparatePairedC2(
                label: String,
                original: PcSheetV11PairedTablePlaced,
                expanded: PcSheetV11PairedTablePlaced,
            ) {
                val amountsAlreadyOnP2 = original.quantities.asSequence()
                    .filter { it.localPageIndex == 1 }
                    .map { it.sourceKey }.toSet()
                for (localPage in 1..expanded.localPageCount) {
                    val page = PDPage(PDRectangle.LETTER)
                    pdf.addPage(page)
                    physicalSectionKeys += "C2 - Equipo y tesoro / " + label
                    PDPageContentStream(pdf, page).use { stream ->
                        drawFrameAndHeader(stream,
                            "C2 - Equipo y tesoro / " + label + " / " + localPage)
                        drawPaired(stream, label + " / CANTIDAD",
                            fullC2PairedArea, expanded, localPage, amountsAlreadyOnP2,
                            compactC2 = true)
                    }
                }
            }
            if (compactPaired != null) {
                val page = PDPage(PDRectangle.LETTER)
                pdf.addPage(page)
                physicalSectionKeys += "C2 - Equipo y tesoro / EQUIPO Y TESORO"
                PDPageContentStream(pdf, page).use { stream ->
                    drawFrameAndHeader(stream, "C2 - Equipo y tesoro / EQUIPO Y TESORO")
                    val equipoAmounts = pairedRemainders[0].third.quantities
                        .filter { it.localPageIndex == 1 }
                        .map { it.sourceKey }.toSet()
                    val tesoroAmounts = pairedRemainders[1].third.quantities
                        .filter { it.localPageIndex == 1 }
                        .map { it.sourceKey }.toSet()
                    drawPaired(stream, "EQUIPO / CANTIDAD", compactPaired.topArea,
                        compactPaired.equipment, 1, equipoAmounts,
                        compactC2 = true, writeC2Notes = false)
                    drawPaired(stream, "TESORO / CANTIDAD", compactPaired.bottomArea,
                        compactPaired.treasure, 1, tesoroAmounts, compactC2 = true)
                }
            } else {
                // Original independent page path remains authoritative when the
                // measured physical envelopes do not fit together.
                for ((label, remaining, original) in pairedRemainders) {
                    if (remaining.rows.isEmpty()) continue
                    val expanded = placeC2Paired(remaining, fullC2PairedArea)
                    val occupied = (expanded.labels.maxOfOrNull { it.rowIndex + 1 }
                        ?: 0) * body.metrics.lineHeightPt
                    val upperHeight = maxOf(90f, occupied + 18f)
                    // Only ONE short, standard-width ordinary pair may join
                    // ONE deferred special record. The real full-width special
                    // table is measured below before this reservation commits.
                    // Large, multi-page or currency cases remain unchanged.
                    val reserveForSpecial = label == "EQUIPO" &&
                        pairedRemainders.count { it.second.rows.isNotEmpty() } == 1 &&
                        specialDeferredToC2.size == 1 &&
                        PcSheetV11PairedAmountRouting.c2AmountWidth(
                            remaining, body.width,
                        ) <= PcSheetV11PairedAmountRouting.BASE_AMOUNT_WIDTH_PT &&
                        expanded.localPageCount == 1 && upperHeight <= 180f
                    if (reserveForSpecial) {
                        deferredOrdinaryForSpecial =
                            DeferredOrdinaryForSpecial(label, original, expanded, upperHeight)
                    } else {
                        drawSeparatePairedC2(label, original, expanded)
                    }
                }
            }

            // The golden C2 extends the SAME full-width four-field grammar as
            // P2. True source-backed rows may span physical pages or become
            // taller for long descriptions, never compress or truncate text.
            // The shared text flow independently verifies all source offsets.
            fun specialC2Lines(raw: String?, widthPt: Float): List<String> {
                if (raw.isNullOrBlank()) return emptyList()
                val measured = PcSheetV11TextFlow.paginate(
                    PcSheetV11FlowSection.SPECIAL_EQUIPMENT,
                    listOf(PcSheetV11FlowItem("c2-source", raw,
                        keepTogetherIfFits = true)),
                    1, PcSheetV11TextColumn(widthPt - 10f, 626f,
                        body.metrics.lineHeightPt),
                    body.width,
                )
                return measured.fragments.flatMap { it.lineRanges }.map { range ->
                    raw.substring(range.startOffset, range.endExclusive)
                        .trimEnd('\r', '\n')
                }
            }
            val specialC2Rows = specialDeferredToC2.map { item ->
                val description = listOfNotNull(
                    item.description?.takeIf { it.isNotBlank() },
                    item.notes?.takeIf { it.isNotBlank() }?.let { "NOTAS: " + it },
                ).joinToString(" / ")
                item to listOf(item.location, item.name, description).mapIndexed {
                    column, value -> specialC2Lines(value, specialColumns[column])
                }
            }
            data class SpecialC2Fragment(
                val rowIndex: Int,
                val startLine: Int,
                val endLine: Int,
                val heightPt: Float,
            )
            val c2SpecialArea = PcSheetV11RectPt(42f, 76f, 528f, 626f)
            val c2SpecialPages = mutableListOf<List<SpecialC2Fragment>>()
            var currentC2 = mutableListOf<SpecialC2Fragment>()
            var availableC2 = c2SpecialArea.heightPt - specialHeaderHeight
            fun flushC2Special() {
                if (currentC2.isNotEmpty()) c2SpecialPages += currentC2.toList()
                currentC2 = mutableListOf()
                availableC2 = c2SpecialArea.heightPt - specialHeaderHeight
            }
            specialC2Rows.forEachIndexed { rowIndex, (_, fields) ->
                val totalLines = maxOf(1, fields.maxOf { it.size })
                var consumed = 0
                while (consumed < totalLines) {
                    var lines = minOf(totalLines - consumed,
                        ((availableC2 - 8f) / body.metrics.lineHeightPt).toInt())
                    var height = maxOf(specialRowHeight,
                        lines * body.metrics.lineHeightPt + 6f)
                    while (lines > 1 && height > availableC2) {
                        lines--
                        height = maxOf(specialRowHeight,
                            lines * body.metrics.lineHeightPt + 6f)
                    }
                    if (lines <= 0 || height > availableC2) {
                        require(currentC2.isNotEmpty()) {
                            "A single real special-equipment line cannot fit C2."
                        }
                        flushC2Special()
                        continue
                    }
                    currentC2 += SpecialC2Fragment(
                        rowIndex, consumed, consumed + lines, height,
                    )
                    availableC2 -= height
                    consumed += lines
                }
            }
            flushC2Special()
            require(c2SpecialPages.flatten().groupBy { it.rowIndex }.size ==
                specialC2Rows.size) {
                "Every deferred special item must occupy at least one physical C2 row."
            }
            // Join a genuinely short special record with a genuinely
            // short ordinary pair only if the already-measured complete
            // special row fits in its own lower full-width rectangle.
            // If not, flush the deferred ordinary page unchanged before the
            // original special continuation pages; never attempt compression.
            val sharedC2 = deferredOrdinaryForSpecial?.takeIf { pending ->
                c2SpecialPages.size == 1 && c2SpecialPages.single().size == 1 &&
                    c2SpecialPages.single().single().startLine == 0 &&
                    specialHeaderHeight +
                        c2SpecialPages.single().sumOf { it.heightPt.toDouble() } <
                        fullC2PairedArea.heightPt - pending.upperHeight - 22f - 10f
            }
            if (deferredOrdinaryForSpecial != null && sharedC2 == null) {
                val separate = requireNotNull(deferredOrdinaryForSpecial)
                drawSeparatePairedC2(separate.label, separate.original, separate.expanded)
            }
            c2SpecialPages.forEachIndexed { pageIndex, fragments ->
                val shared = sharedC2?.takeIf { pageIndex == 0 }
                val drawingArea = if (shared != null) PcSheetV11RectPt(
                    42f, c2SpecialArea.bottomPt, 528f,
                    c2SpecialArea.heightPt - shared.upperHeight - 22f,
                ) else c2SpecialArea
                val page = PDPage(PDRectangle.LETTER)
                pdf.addPage(page)
                physicalSectionKeys += if (shared != null)
                    "C2 - Equipo y tesoro / EQUIPO + EQUIPO ESPECIAL"
                    else "C2 - Equipo especial / continuación"
                PDPageContentStream(pdf, page).use { stream ->
                    if (shared != null) {
                        drawFrameAndHeader(stream,
                            "C2 - Equipo y tesoro / EQUIPO + EQUIPO ESPECIAL")
                        val topArea = PcSheetV11RectPt(
                            42f, c2SpecialArea.topPt - shared.upperHeight,
                            528f, shared.upperHeight,
                        )
                        val topPlaced = placeC2Paired(
                            pairedRemainders.first().second, topArea,
                        )
                        require(topPlaced.localPageCount == 1) {
                            "Measured shared C2 ordinary equipment cannot fit its top panel."
                        }
                        val alreadyP2 = shared.original.quantities
                            .filter { it.localPageIndex == 1 }
                            .map { it.sourceKey }.toSet()
                        drawPaired(stream, "EQUIPO / CANTIDAD", topArea,
                            topPlaced, 1, alreadyP2, compactC2 = true,
                            writeC2Notes = false)
                    } else {
                        drawFrameAndHeader(stream,
                            "C2 - Equipo especial / continuación / " + (pageIndex + 1))
                    }
                    draw(stream, "EQUIPO ESPECIAL", 42f,
                        drawingArea.topPt + 6f, true)
                    // An oversized source row can continue with ONLY its
                    // description: name/location were drawn on the prior
                    // physical page. Identify the SAME record in the free
                    // C2 header strip instead of repeating its data fields.
                    // This uses the approved header grammar and consumes no
                    // row or additional page.
                    val continuedPart = fragments.firstOrNull()
                        ?.takeIf { it.startLine > 0 }
                    if (continuedPart != null) {
                        val item = specialC2Rows[continuedPart.rowIndex].first
                        val x = 192f
                        val fontPt = 7f
                        val available = drawingArea.rightPt - x - 3f
                        fun fits(text: String) =
                            bodyPdfFont.getStringWidth(pcSheetV11PrintableText(text)) *
                                fontPt / 1000f <= available
                        val full = "CONTINÚA EQUIPO: " + item.name
                        val sourceId = "CONTINÚA ID: " + item.sourceId
                        // The full name is drawn losslessly in its real
                        // measured table cells. Its PAPER navigation label is
                        // single-line: explicit source CR/LF must never be sent
                        // to PDFBox getStringWidth/showText, which reject those
                        // control glyphs. Use the actual persisted UUID instead.
                        val canUseNameInHeader = item.name.none {
                            it == '\n' || it == '\r'
                        }
                        val label = when {
                            canUseNameInHeader && fits(full) -> full
                            fits(sourceId) -> sourceId
                            else -> error("Real C2 special equipment continuation ID " +
                                "does not fit the existing physical header.")
                        }
                        require(headerPdfFont.getStringWidth("EQUIPO ESPECIAL") *
                            12f / 1000f < x - 42f - 10f) {
                            "Special equipment continuation must not overlap the heading."
                        }
                        stream.saveGraphicsState()
                        stream.setNonStrokingColor(Color(80, 122, 151))
                        stream.beginText()
                        stream.setFont(bodyPdfFont, fontPt)
                        stream.newLineAtOffset(x, drawingArea.topPt + 6f)
                        stream.showText(pcSheetV11PrintableText(label))
                        stream.endText()
                        stream.restoreGraphicsState()
                    }
                    val printedHeight = specialHeaderHeight +
                        fragments.sumOf { it.heightPt.toDouble() }.toFloat()
                    val tableBottom = drawingArea.topPt - printedHeight
                    require(tableBottom >= drawingArea.bottomPt - 0.1f)
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130, 165, 186))
                    stream.setLineWidth(0.55f)
                    stream.setNonStrokingColor(Color(217, 236, 245))
                    stream.addRect(42f, drawingArea.topPt -
                        specialHeaderHeight, 528f, specialHeaderHeight)
                    stream.fill()
                    stream.setNonStrokingColor(Color(238, 247, 251))
                    stream.addRect(42f, tableBottom, 528f,
                        printedHeight - specialHeaderHeight)
                    stream.fill()
                    stream.addRect(42f, tableBottom, 528f, printedHeight)
                    stream.stroke()
                    var separator = 42f
                    specialColumns.dropLast(1).forEach { width ->
                        separator += width
                        stream.moveTo(separator, tableBottom)
                        stream.lineTo(separator, drawingArea.topPt)
                        stream.stroke()
                    }
                    stream.moveTo(42f, drawingArea.topPt - specialHeaderHeight)
                    stream.lineTo(570f, drawingArea.topPt - specialHeaderHeight)
                    stream.stroke()
                    stream.restoreGraphicsState()
                    var headerX = 42f
                    listOf("UBICACIÓN", "NOMBRE", "DESCRIPCIÓN", "ACTIVO")
                        .forEachIndexed { index, label ->
                            draw(stream, label, headerX + 5f,
                                drawingArea.topPt - 13f, false)
                            headerX += specialColumns[index]
                        }
                    val positions = listOf(42f, 42f + specialColumns[0],
                        42f + specialColumns[0] + specialColumns[1])
                    var rowTop = drawingArea.topPt - specialHeaderHeight
                    fragments.forEach { part ->
                        val (item, fields) = specialC2Rows[part.rowIndex]
                        fields.forEachIndexed { column, allLines ->
                            val shown = allLines.drop(part.startLine)
                                .take(part.endLine - part.startLine)
                            shown.forEachIndexed { index, line ->
                                if (line.isNotEmpty()) {
                                    draw(stream, line, positions[column] + 5f,
                                        rowTop - 10f -
                                            index * body.metrics.lineHeightPt,
                                        false)
                                }
                            }
                        }
                        if (part.startLine == 0) {
                            val markerX = drawingArea.rightPt -
                                specialColumns.last() / 2f - 4.5f
                            val markerY = rowTop - 16f
                            stream.addRect(markerX, markerY, 9f, 9f)
                            stream.stroke()
                            if (item.active == true) {
                                draw(stream, "X", markerX, markerY + 2f, false)
                            }
                        }
                        rowTop -= part.heightPt
                        stream.saveGraphicsState()
                        stream.setStrokingColor(Color(130, 165, 186))
                        stream.setLineWidth(0.45f)
                        stream.moveTo(42f, rowTop)
                        stream.lineTo(570f, rowTop)
                        stream.stroke()
                        stream.restoreGraphicsState()
                    }
                    require(kotlin.math.abs(rowTop - tableBottom) <= 0.5f)
                    drawUnusedC2NotesGrid(stream, tableBottom)
                }
            }

            // C209: frozen owner-approved V11 extreme A/B places source-backed
            // C2 continuation pages after P3 and BEFORE P4. D-0076 §3.30
            // likewise prefers C2 adjacent to its originating base modules.
            // P4's own measured spell text and 3-column layout are untouched;
            // footer/backlinks are resolved from FINAL physical pages.
            if (data.hasQuickSpellList) {
                // Contract: a spell page, when present, reserves visible levels 0–9
                // even if a given level has no real spells; 10–12 require real
                // epic rules data and are not invented. Sources/preparation stay
                // linked to the specific persisted casting-source association.
                fun quickRows(
                    manuscriptRowsPerLevel: Int,
                    extraLastLevel9PaperRows: Int = 0,
                ): List<PcSheetV11FlowItem> = buildList {
                    require(manuscriptRowsPerLevel >= 1 && extraLastLevel9PaperRows >= 0)
                    val levels = (0..9).toList() +
                        data.spells.map { it.spell.level }.filter { it >= 10 }.distinct().sorted()
                    levels.forEach { level ->
                        add(PcSheetV11FlowItem("spell-level:" + level,
                            if (level == 0) "TRUCOS / NIVEL 0" else "NIVEL " + level,
                            keepTogetherIfFits = true))
                        val spellsHere = data.spells.filter { it.spell.level == level }
                        spellsHere.forEach { entry ->
                            val key = "quick-spell:" + entry.spell.id
                            val sourceInfo = p4SourceLabels.getValue(key)
                            add(PcSheetV11FlowItem(key,
                                // Five measured spaces separate the actual first
                                // spell glyph from the 7pt vector box (verified original
                                // 3-space result overlapped its border by ~1.8pt).
                                // No source name or status is abbreviated or trimmed.
                                "     " + entry.spell.name +
                                    (if (sourceInfo.isBlank() || key in p4RightSources) ""
                                    else " | " + sourceInfo),
                                keepTogetherIfFits = true))
                        }
                        // Rows are PAPER-ONLY: no fabricated spell, source, preparation
                        // or spell slot. Every level is writable, even when populated.
                        val paperRows = (if (level in 0..9)
                            manuscriptRowsPerLevel else 1) +
                            (if (level == 9) extraLastLevel9PaperRows else 0)
                        repeat(paperRows) { row ->
                            add(PcSheetV11FlowItem(
                                "spell-handwritten:" + level + ":" + row,
                                "[ ] ______________________",
                                keepTogetherIfFits = true,
                            ))
                        }
                    }
                }
                // G2/G3: measure the TRUE three-column page before reserving
                // manuscript lines. The original internal proof shortened the
                // page area and stranded most of a Letter sheet as blank paper.
                // Expand paper-only entries, not digital spell data, as far as
                // one physical page permits. Never create a second filler page.
                val p4WritingArea = PcSheetV11RectPt(42f, 76f, 528f, 531f)
                val minimalQuick = quickRows(1)
                val minimalPlacement = body.place(
                    PcSheetV11FlowSection.QUICK_SPELLS,
                    minimalQuick, p4WritingArea, 3, 12f,
                )
                val quick = if (minimalPlacement.sourceFlow.localPageCount == 1) {
                    var lowest = 1
                    var highest = 20
                    while (lowest < highest) {
                        val candidateRows = (lowest + highest + 1) / 2
                        val candidate = body.place(
                            PcSheetV11FlowSection.QUICK_SPELLS,
                            quickRows(candidateRows), p4WritingArea, 3, 12f,
                        )
                        if (candidate.sourceFlow.localPageCount == 1) {
                            lowest = candidateRows
                        } else {
                            highest = candidateRows - 1
                        }
                    }
                    val baseRows = quickRows(lowest)
                    val basePlacement = body.place(
                        PcSheetV11FlowSection.QUICK_SPELLS,
                        baseRows, p4WritingArea, 3, 12f,
                    )
                    require(basePlacement.sourceFlow.localPageCount == 1)
                    // Original golden P4 fills each column with handwriting
                    // paper. Measured genuine App sheets often leave the LAST
                    // column 7-8 writable rows short even when all source
                    // levels are present. Grow ONLY the tail of the final
                    // non-epic level 9, after every real spell/heading.
                    // Never add a P4 filler page or give fake epic levels.
                    val finalLevel9InLastColumn =
                        data.spells.none { it.spell.level >= 10 } &&
                            basePlacement.cells.any { cell ->
                                cell.sourceKey == "spell-handwritten:9:" + (lowest - 1) &&
                                    cell.localPageIndex == 1 && cell.columnIndex == 3
                            }
                    if (finalLevel9InLastColumn) {
                        var extraLow = 0
                        var extraHigh = 42
                        while (extraLow < extraHigh) {
                            val extraCandidate = (extraLow + extraHigh + 1) / 2
                            val candidate = body.place(
                                PcSheetV11FlowSection.QUICK_SPELLS,
                                quickRows(lowest, extraCandidate),
                                p4WritingArea, 3, 12f,
                            )
                            if (candidate.sourceFlow.localPageCount == 1) {
                                extraLow = extraCandidate
                            } else {
                                extraHigh = extraCandidate - 1
                            }
                        }
                        // Only adds genuine vector WRITING ROWS at the end.
                        // Existing source cells keep identical coordinates,
                        // font, value and physical page number.
                        quickRows(lowest, extraLow)
                    } else baseRows
                } else minimalQuick
                addSection("P4 - Conjuros", PcSheetV11FlowSection.QUICK_SPELLS,
                    quick, 3, area = p4WritingArea)
            }

            addSection("C-N3 - Notas digitales", PcSheetV11FlowSection.DIGITAL_NOTES,
                texts.digitalNotes, 1)
            data.spellbookEntries?.takeIf { it.isNotEmpty() }?.let { spells ->
                // The only optional annex, with real two-column cards. All spell
                // content remains source-backed; no fabricated school or rules.
                val cards = spells.map { entry ->
                    val spell = entry.spell
                    // D-0076 §3.47 / BK06: the OWNER approved the essential
                    // spellbook card, not the expanded technical alternative.
                    // A card contains source-backed NAME, LEVEL, SCHOOL (only
                    // when actually available) and the ENTIRE DESCRIPTION.
                    // CharacterSpell currently has no school field. Never
                    // guess it from class, source, names or description.
                    // Keep time/range/components/duration/preparation/notes
                    // in the App source and other independently contracted
                    // views; they are NOT printed in this optional card.
                    val lines = buildList {
                        add(spell.name)
                        add("NIVEL " + spell.level)
                        add("DESCRIPCIÓN:")
                        add(spell.description)
                    }
                    PcSheetV11FlowItem(
                        sourceKey = "book:" + spell.id,
                        text = lines.joinToString("\n"),
                        keepTogetherIfFits = true,
                        leadingSeparationLines = 1, // BK08: one REAL measured blank row between cards.
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
                // Frozen owner-approved V11 golden (7 final NOTAS examples):
                // 17 softly alternating ruled rows per column, followed by a
                // pale blue 34 x 20 COMPLETE grid. All dimensions are physical
                // US-Letter points, measured on the original golden PDF vectors.
                // Do not use inherited black / 1pt PDFBox graphics defaults.
                stream.saveGraphicsState()
                try {
                    val noteLeft = 39.685f
                    val noteRight = 310.787f
                    val ruledWidth = 261.528f
                    for (columnLeft in listOf(noteLeft, noteRight)) {
                        for (row in 0 until 17) {
                            val base = 676.551f - row * 18.993f
                            stream.setNonStrokingColor(
                                if (row % 2 == 0) Color(231, 242, 247)
                                else Color(240, 247, 250),
                            )
                            stream.addRect(columnLeft, base, ruledWidth, 18.993f)
                            stream.fill()
                            stream.setStrokingColor(Color(148, 176, 193))
                            stream.setLineWidth(0.32f)
                            stream.moveTo(columnLeft, base + 1f)
                            stream.lineTo(columnLeft + ruledWidth, base + 1f)
                            stream.stroke()
                        }
                    }
                    val gridLeft = 39.685f
                    val gridBottom = 57.827f
                    val gridWidth = 532.63f
                    val gridHeight = 305.008f
                    stream.setStrokingColor(Color(148, 176, 193))
                    stream.setLineWidth(0.45f)
                    stream.addRect(gridLeft, gridBottom, gridWidth, gridHeight)
                    stream.stroke()
                    stream.setStrokingColor(Color(191, 211, 222))
                    stream.setLineWidth(0.27f)
                    for (column in 1 until 34) {
                        val x = gridLeft + column * gridWidth / 34f
                        stream.moveTo(x, gridBottom)
                        stream.lineTo(x, gridBottom + gridHeight)
                        stream.stroke()
                    }
                    for (row in 1 until 20) {
                        val y = gridBottom + row * gridHeight / 20f
                        stream.moveTo(gridLeft, y)
                        stream.lineTo(gridLeft + gridWidth, y)
                        stream.stroke()
                    }
                } finally {
                    stream.restoreGraphicsState()
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
            val c2FromP1 = mixedC2P1Pages.minOrNull()
            fun isFromP2(key: String): Boolean =
                key.startsWith("C2 - Equipo y tesoro / ") ||
                    key == "C2 - Rasgos / continuación" ||
                    key == "C2 - Equipo especial / continuación"
            val dedicatedC2FromP2 = physicalSectionKeys.indexOfFirst { isFromP2(it) }
                .takeIf { it >= 0 }?.plus(1)
            val c2FromP2 = listOfNotNull(
                dedicatedC2FromP2, mixedC2TraitPages.minOrNull(),
            ).minOrNull()
            // C206: P2 can have MULTIPLE independent C2 descendants (traits,
            // ordinary equipment, treasure, special equipment). Its existing
            // bottom cue points only to the first. Preserve that established
            // reference and add explicit per-module destinations in the unused
            // footer band, with actual FINAL physical page numbers.
            val p2C2Destinations = buildList {
                mixedC2TraitPages.minOrNull()?.let { add("RASGOS" to it) }
                physicalSectionKeys.forEachIndexed { physicalIndex, section ->
                    val sourceModule = when {
                        section == "C2 - Rasgos / continuación" -> "RASGOS"
                        section == "C2 - Equipo y tesoro / EQUIPO Y TESORO" ->
                            "EQUIPO/TESORO"
                        section.startsWith("C2 - Equipo y tesoro / EQUIPO") -> "EQUIPO"
                        section.startsWith("C2 - Equipo y tesoro / TESORO") -> "TESORO"
                        section == "C2 - Equipo especial / continuación" -> "ESPECIAL"
                        else -> null
                    }
                    if (sourceModule != null) add(sourceModule to (physicalIndex + 1))
                }
            }.distinctBy { it.first }.sortedBy { it.second }
            require(p2C2Destinations.minOfOrNull { it.second } == c2FromP2) {
                "The P2 C2 source modules must agree with the primary backlink."
            }
            val otherP2C2Links = p2C2Destinations
                .filter { it.second != c2FromP2 }
                .joinToString(" | ") { (module, physical) ->
                    module + " PÁG. " + physical
                }.takeIf { it.isNotEmpty() }?.let { "OTROS C2: " + it }
            if (otherP2C2Links != null) {
                require(body.width.measurePt(otherP2C2Links) <= 528f) {
                    "All genuine P2 module destinations must fit the reserved footer."
                }
            }
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
                val link = when {
                    key.startsWith("P1 - ") && c2FromP1 != null ->
                        "CONTINÚA EN C2 PÁG. " + c2FromP1
                    key.startsWith("P2 - ") && c2FromP2 != null ->
                        "CONTINÚA EN C2 PÁG. " + c2FromP2
                    key == mixedC2Key -> {
                        // A single mixed C2 physical page may contain P1
                        // continuations, P2 trait continuations, both or neither.
                        // Link each base page to its FIRST genuine continuation,
                        // and announce provenance only when it begins there.
                        val origins = listOfNotNull(
                            if (physicalPage == c2FromP1)
                                "P1 PÁG. " + physicalP1 else null,
                            if (physicalPage == mixedC2TraitPages.minOrNull())
                                "P2 PÁG. " + physicalP2 else null,
                        )
                        listOfNotNull(
                            origins.takeIf { it.isNotEmpty() }?.let {
                                "VIENE DE " + it.joinToString(" / ")
                            },
                            prev?.let { "ANT. " + it },
                            next?.let { "SIG. " + it },
                        ).joinToString(" | ")
                    }
                    isFromP2(key) && prev == null ->
                        "VIENE DE P2 PÁG. " + physicalP2 +
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
                    if (key == "P2 - Equipo y tesoro" &&
                        otherP2C2Links != null
                    ) {
                        // PDF y=63: below source body (>=76), above the original
                        // watermark y=46 and navigation y=35. No page/layout
                        // mutation; never replace the first legacy cue.
                        draw(stream, otherP2C2Links, 42f, 63f, false)
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
