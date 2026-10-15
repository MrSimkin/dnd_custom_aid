package io.github.mrsimkin.dndcustomaid.android.pdf

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowItem
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableComposer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTablePlaced
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11MeasuredFlowPlacer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TextFlow
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TextColumn
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSourceMapper
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ColumnGrid
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11RectPt
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticData
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Typeface
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
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

            // V11 frozen two-side P1: left vertical abilities, not six
            // horizontal cells over the combat side of the page.
            fun p1AttributeHeight(ability: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Attribute): Float {
                val a=data.request.variant ==
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A
                return if(a) maxOf(38f,29f +
                    minOf(6,data.skills.count{it.ability==ability.reference})*10.5f)
                    else 38f
            }
            fun p1AttributeTop(i:Int):Float = 577f -
                data.attributes.take(i).sumOf { p1AttributeHeight(it).toDouble() }.toFloat()
            fun drawP1Attributes(stream:PDPageContentStream) {
                val standards=data.attributes.take(6)
                require(standards.size==6 && p1AttributeTop(6)>110f)
                draw(stream,if(data.request.variant ==
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A)
                    "ATRIBUTOS · A" else "ATRIBUTOS · B",43f,588f,true)
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
                    stream.addRect(46f,top-27f,47f,22f)
                    stream.stroke()
                    val cx=116f
                    val cy=top-16f
                    val rx=23f
                    val ry=11f
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
                    draw(stream,score,69.5f-body.width.measurePt(score)/2f,top-20f,false)
                    draw(stream,mod,cx-body.width.measurePt(mod)/2f,top-20f,false)
                    draw(stream,ability.name,145f,top-14f,false)
                    stream.beginText()
                    stream.setFont(bodyPdfFont,7f)
                    stream.newLineAtOffset(215f,top-25f)
                    stream.showText("SALVACIÓN")
                    stream.endText()
                    ability.savingThrowTotal?.let {
                        val value=if(it>=0)"+"+it else it.toString()
                        draw(stream,value,296f-body.width.measurePt(value),top-25f,false)
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
                            val remaining = 258f - 34f - skillFont.width.measurePt(total) - 9f
                            if (skillFont.width.measurePt(skill.name) <= remaining) {
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
                                stream.showText(value)
                                stream.endText()
                            }
                        }
                        put(when(skill.training) {
                            io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.NONE -> ""
                            io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> "P"
                            io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> "E"
                        },48f)
                        put(skill.name,60f)
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
                    }?.abbreviation?.uppercase()?.take(3)
                    val training = when (skill.training) {
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.NONE -> ""
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> " P"
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> " E"
                    }
                    PcSheetV11FlowItem(
                        "skill:" + index,
                        skill.name + (abbreviation?.let { " (" + it + ")" } ?: "") +
                            (skill.total?.let { " " + (if (it >= 0) "+" else "") + it } ?: "") +
                            training,
                        keepTogetherIfFits = true,
                    )
                }
            else emptyList()
            val bSkillsArea = PcSheetV11RectPt(49f,220f,244f,95f)
            val bSkillsPlaced = if(!variantA) PcSheetV11MeasuredFlowPlacer.place(
                PcSheetV11FlowSection.TRAITS,bSkillEntries,bSkillsArea,2,8f,
                skillFont.metrics.lineHeightPt,skillFont.width,
            ) else null
            fun drawP1BIndependentSkills(stream: PDPageContentStream) {
                if (variantA) return
                draw(stream,"HABILIDADES  P=COMPETENCIA  E=PERICIA",43f,338f,true)
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130,165,186))
                stream.setLineWidth(0.6f)
                stream.addRect(42f,105f,258f,223f)
                stream.stroke()
                stream.moveTo(171f,105f)
                stream.lineTo(171f,319f)
                stream.stroke()
                stream.restoreGraphicsState()
                bSkillsPlaced?.cells?.filter{it.localPageIndex==1}?.forEach{cell->
                    if(cell.drawableText.isNotEmpty()){
                        stream.beginText()
                        stream.setFont(bodyPdfFont,7f)
                        stream.newLineAtOffset(cell.cellPt.leftPt,
                            skillFont.metrics.baselineIn(cell.cellPt))
                        stream.showText(cell.drawableText)
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
            val attackLabelWidth = 121f
            val attackBonusWidth = 36f
            val attackDamageWidth = 258f - attackLabelWidth - attackBonusWidth
            fun p1AttackLabel(entry:io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry):String {
                val complete=entry.name+(entry.rangeText?.takeIf{it.isNotBlank()}
                    ?.let{" / "+it}?:"")
                return if(body.width.measurePt(complete)<=attackLabelWidth-10f)
                    complete else entry.name
            }
            val p1Attacks=attackRows.take(6).filter{entry->
                val bonus=entry.attackModifier?.let{if(it>=0)"+"+it else it.toString()}?:""
                body.width.measurePt(p1AttackLabel(entry))<=attackLabelWidth-10f &&
                body.width.measurePt(bonus)<=attackBonusWidth-8f &&
                body.width.measurePt(entry.damageEffect)<=attackDamageWidth-10f
            }
            val p1AttackIds = p1Attacks.map { it.id }.toSet()

            fun drawP1Attacks(stream: PDPageContentStream) {
                val left = 312f
                val top = 491f
                val bottom = 392f
                val b1 = left + attackLabelWidth
                val b2 = b1 + attackBonusWidth
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

            fun drawP1Spellcasting(stream:PDPageContentStream) {
                draw(stream,"LANZAMIENTO DE CONJUROS",312f,291f,true)
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
                    }?:run{
                        stream.moveTo(x+19f,y-7f)
                        stream.lineTo(x+75f,y-7f)
                        stream.stroke()
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
                // High-level slot rows only exist when explicitly persisted.
                data.spellSlots.filter { it.level >= 10 }.forEach { slots ->
                    add(PcSheetV11FlowItem(
                        "spellcasting:epic:" + slots.level,
                        "ESPACIOS REALES NIVEL " + slots.level +
                            ": TOTAL " + slots.totalSlots + ", GASTADOS " + slots.spentSlots,
                    ))
                }
            }



            // P1 has an independent, measured right-side mechanical summary.
            // Full trait descriptions remain source-backed on P2/C2.
            val p1Font = measurer.resolve(PcSheetV11Typeface.BODY, 7f)
            fun p1DrawSmall(stream: PDPageContentStream, content: String, x: Float, y: Float) {
                if (content.isEmpty()) return
                stream.beginText()
                stream.setFont(bodyPdfFont, 7f)
                stream.newLineAtOffset(x, y)
                stream.showText(content)
                stream.endText()
            }
            val p1TraitNames = data.traits.asSequence().map { it.name }
                .filter { it.isNotBlank() && p1Font.width.measurePt(it) <= 121f }
                .take(12).toList()
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
                p1DrawSmall(stream, "RASGOS Y ATRIBUTOS", left + 4f, top - 11f)
                p1TraitNames.forEachIndexed { index, name ->
                    p1DrawSmall(stream, name, left + 4f + (index / 6) * 129f,
                        top - 21f - (index % 6) * 10f)
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
                .filter { it.id in p1AmmunitionIds && it.quantity >= 0 &&
                    p1Font.width.measurePt(it.name) <= 68f }
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
                    stream.addRect(frame.leftPt, frame.bottomPt, frame.widthPt, frame.heightPt)
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
                fun plus(v: Int) = if (v >= 0) "+" + v else v.toString()
                val dice = source.classes.joinToString(" / ") {
                    it.hitDiceRemaining.toString() + "d" + it.hitDieSides
                }
                val values = listOf(
                    "CA" to source.armorClass.toString(),
                    "INICIATIVA" to plus(source.initiativeModifier),
                    "VELOCIDAD" to source.speed.toString(),
                    "COMPETENCIA" to plus(source.finalProficiencyBonus),
                    "PV ACTUALES" to source.currentHp.toString(),
                    "PV MÁXIMOS" to source.maxHp.toString(),
                    "PV TEMP" to source.tempHp.toString(),
                    "DADOS" to dice,
                )
                val left = 312f
                val bottom = 503f
                val colWidth = 129f
                val height = 22f
                draw(stream, "COMBATE Y SUPERVIVENCIA", left, 604f, false)
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.55f)
                stream.setNonStrokingColor(Color(238, 247, 251))
                stream.addRect(left, bottom, 258f, height * 4f)
                stream.fill()
                stream.addRect(left, bottom, 258f, height * 4f)
                stream.stroke()
                stream.moveTo(left + colWidth, bottom)
                stream.lineTo(left + colWidth, bottom + height * 4f)
                stream.stroke()
                for (line in 1..3) {
                    val y = bottom + line * height
                    stream.moveTo(left, y)
                    stream.lineTo(left + colWidth * 2f, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
                values.forEachIndexed { index, (label, value) ->
                    val x = left + (index % 2) * colWidth + 4f
                    val top = bottom + (4 - index / 2) * height
                    require(body.width.measurePt(label) <= colWidth - 8f) {
                        "P1 combat label too wide: " + label
                    }
                    require(body.width.measurePt(value) <= colWidth - 8f) {
                        "P1 source combat value cannot fit: " + label
                    }
                    draw(stream, label, x, top - 9f, false)
                    if (value.isNotEmpty()) draw(stream, value, x, top - 19f, false)
                }
            }

            // Approved golden P4: source-backed magical aptitude, real save DC,
            // real spell attack; blank manuscript fields when values are absent.
            // This is a physical banner above the three-column level ledger,
            // NOT additional fake spell slots or a generic body-text section.
            fun drawP4MagicReference(stream: PDPageContentStream) {
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
                    "APTITUD" to ability,
                    "CD SALVACIÓN" to source.spellSaveDc?.toString(),
                    "ATAQUE MÁGICO" to attack,
                )
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.7f)
                stream.setNonStrokingColor(Color(231, 241, 247))
                stream.addRect(42f, 667f, 528f, 27f)
                stream.fill()
                stream.addRect(42f, 667f, 528f, 27f)
                stream.stroke()
                stream.restoreGraphicsState()
                draw(stream, "REFERENCIA MÁGICA", 49f, 676f, true)
                fields.forEachIndexed { index, (label, value) ->
                    val x = 42f + index * 180f
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(80, 122, 151))
                    stream.setLineWidth(0.7f)
                    stream.addRect(x, 617f, 168f, 44f)
                    stream.stroke()
                    stream.restoreGraphicsState()
                    draw(stream, label, x + 7f, 645f, false)
                    if (value == null) {
                        stream.moveTo(x + 7f, 629f)
                        stream.lineTo(x + 160f, 629f)
                        stream.stroke()
                    } else {
                        draw(stream, value, x + 8f, 625f, true)
                    }
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
                stream.addRect(42f, y, 528f, 20f)
                stream.fill()
                stream.addRect(42f, y, 528f, 20f)
                stream.stroke()
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
                stream.setStrokingColor(Color(174, 203, 220))
                stream.setLineWidth(0.35f)
                val rows = (rect.heightPt / lineHeight).toInt()
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
                    draw(stream, heading, rect.leftPt + 5f, rect.topPt - 11f, false)
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
                        val pageCells = flow.cells.filter {
                            it.localPageIndex == index + 1
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
                        // P3 now draws its own two-column ruled writing grammar,
                        // before the source text, including continuation pages.
                    }
                }
            }

            val identity = buildList {
                data.attributes.drop(6).forEachIndexed { index, attribute ->
                    add(PcSheetV11FlowItem(
                        "attribute:" + index,
                        attribute.name + " " + attribute.score +
                            " / mod " + attribute.modifier +
                            " / salvación " +
                            (attribute.savingThrowTotal?.toString() ?: "sin valor"),
                        keepTogetherIfFits = true,
                    ))
                    data.skills.withIndex()
                        .filter { (_, skill) -> skill.customSkillId != null &&
                            skill.ability == attribute.reference }
                        .forEach { (_, skill) ->
                            add(PcSheetV11FlowItem(
                                "attribute-skill:" + skill.customSkillId,
                                "Habilidad " + skill.name + ": " +
                                    (skill.total?.toString() ?: "sin resolver") +
                                    " / " + skill.training.name,
                                keepTogetherIfFits = true,
                            ))
                        }
                }
                if (!variantA) {
                    data.skills.forEachIndexed { index, skill ->
                        if (skill.builtInKey == null &&
                            skill.ability !in customAttributeReferences) {
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
            val p1Unprinted=identityFieldOverflow+bSkillOverflow+identity
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
            ): io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData {
                val labelWidth = (area.widthPt - 12f) / 2f - 36f - 6f
                require(labelWidth > 0f)
                return input.copy(rows = input.rows.filter { row ->
                    !row.label.contains('\n') &&
                        body.width.measurePt(row.label) <= labelWidth
                })
            }
            val inventoryPlaced = PcSheetV11PairedTableComposer.place(
                rowsForP2(completeEquipment, equipmentArea),
                equipmentArea, 12f, 36f, 6f, body.metrics.lineHeightPt, body.width,
            )
            val treasurePlaced = PcSheetV11PairedTableComposer.place(
                rowsForP2(completeTreasure, treasureArea),
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

            fun grid(stream: PDPageContentStream, rect: PcSheetV11RectPt, twoColumns: Boolean) {
                // Blue mist writing bands and fine rules match the approved V11
                // paper grammar; black default strokes belong only to PREQA.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(238, 247, 251))
                stream.addRect(rect.leftPt, rect.bottomPt, rect.widthPt, rect.heightPt)
                stream.fill()
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
                val cells = (rect.heightPt / body.metrics.lineHeightPt).toInt()
                for (line in 1 until cells) {
                    val y = rect.topPt - line * body.metrics.lineHeightPt
                    stream.moveTo(rect.leftPt, y)
                    stream.lineTo(rect.rightPt, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
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
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.45f)
                placed.pairs.forEach { pair ->
                    val quantitySeparator = pair.rightPt - 42f
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
                draw(stream, "EQUIPO ESPECIAL", specialArea.leftPt,
                    specialArea.topPt + 6f, true)
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
                specialP2Rows.forEachIndexed { rowIndex, (item, columns) ->
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
                    val activeY = rowTop - specialRowHeight / 2f - 4.5f
                    stream.addRect(activeX, activeY, 9f, 9f)
                    stream.stroke()
                    if (item.active == true) {
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

            if (data.hasQuickSpellList) {
                // Contract: a spell page, when present, reserves visible levels 0–9
                // even if a given level has no real spells; 10–12 require real
                // epic rules data and are not invented. Sources/preparation stay
                // linked to the specific persisted casting-source association.
                fun quickRows(manuscriptRowsPerLevel: Int): List<PcSheetV11FlowItem> = buildList {
                    require(manuscriptRowsPerLevel >= 1)
                    val levels = (0..9).toList() +
                        data.spells.map { it.spell.level }.filter { it >= 10 }.distinct().sorted()
                    levels.forEach { level ->
                        add(PcSheetV11FlowItem("spell-level:" + level,
                            if (level == 0) "TRUCOS / NIVEL 0" else "NIVEL " + level,
                            keepTogetherIfFits = true))
                        val spellsHere = data.spells.filter { it.spell.level == level }
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
                        // Rows are PAPER-ONLY: no fabricated spell, source, preparation
                        // or spell slot. Every level is writable, even when populated.
                        repeat(if (level in 0..9) manuscriptRowsPerLevel else 1) { row ->
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
                    quickRows(lowest)
                } else minimalQuick
                addSection("P4 - Conjuros", PcSheetV11FlowSection.QUICK_SPELLS,
                    quick, 3, area = p4WritingArea)
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
            for ((label, complete, original) in listOf(
                Triple("EQUIPO", completeEquipment, inventoryPlaced),
                Triple("TESORO", completeTreasure, treasurePlaced),
            )) {
                val remaining = remainingPairedRows(complete, original)
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
            c2SpecialPages.forEachIndexed { pageIndex, fragments ->
                val page = PDPage(PDRectangle.LETTER)
                pdf.addPage(page)
                physicalSectionKeys += "C2 - Equipo especial / continuación"
                PDPageContentStream(pdf, page).use { stream ->
                    drawFrameAndHeader(stream,
                        "C2 - Equipo especial / continuación / " + (pageIndex + 1))
                    draw(stream, "EQUIPO ESPECIAL", 42f,
                        c2SpecialArea.topPt + 6f, true)
                    val printedHeight = specialHeaderHeight +
                        fragments.sumOf { it.heightPt.toDouble() }.toFloat()
                    val tableBottom = c2SpecialArea.topPt - printedHeight
                    require(tableBottom >= c2SpecialArea.bottomPt - 0.1f)
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130, 165, 186))
                    stream.setLineWidth(0.55f)
                    stream.setNonStrokingColor(Color(217, 236, 245))
                    stream.addRect(42f, c2SpecialArea.topPt -
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
                        stream.lineTo(separator, c2SpecialArea.topPt)
                        stream.stroke()
                    }
                    stream.moveTo(42f, c2SpecialArea.topPt - specialHeaderHeight)
                    stream.lineTo(570f, c2SpecialArea.topPt - specialHeaderHeight)
                    stream.stroke()
                    stream.restoreGraphicsState()
                    var headerX = 42f
                    listOf("UBICACIÓN", "NOMBRE", "DESCRIPCIÓN", "ACTIVO")
                        .forEachIndexed { index, label ->
                            draw(stream, label, headerX + 5f,
                                c2SpecialArea.topPt - 13f, false)
                            headerX += specialColumns[index]
                        }
                    val positions = listOf(42f, 42f + specialColumns[0],
                        42f + specialColumns[0] + specialColumns[1])
                    var rowTop = c2SpecialArea.topPt - specialHeaderHeight
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
                            val markerX = c2SpecialArea.rightPt -
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
