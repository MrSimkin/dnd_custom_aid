package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOptionKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.spellSaveDc
import io.github.mrsimkin.dndcustomaid.shared.character.spellAttackModifier
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterMovementType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDefenseType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryCadence
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryAmountMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomAttributeProjection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomSkillProjection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedPageKind
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetNotePhysicalLine
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetNotePhysicalLineKind
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPackedNotes
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetWrappedNoteRecord
import io.github.mrsimkin.dndcustomaid.shared.character.packPcSheetNoteColumns
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetNoteRecords
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.StandardCurrencyKind
import io.github.mrsimkin.dndcustomaid.shared.character.standardCurrencyKindOrNull
import io.github.mrsimkin.dndcustomaid.shared.character.pdfCompactEquipmentLabel
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetWritableUsesTrackerOrNull
import io.github.mrsimkin.dndcustomaid.shared.character.pdfCampaignNoteParagraphs
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetIntegratedAttributeTitle
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetContextualSkillIdentity
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetCompactAttributeKey
import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.roundToInt
import org.apache.pdfbox.multipdf.LayerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDFormContentStream
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.util.Matrix

/**
 * Production promotion, pass 1, for the owner-approved Custom-v2 Extended Run-7 baseline.
 *
 * This class promotes only the mandatory Custom Statistics Extended page. The remaining shared
 * Extended roles are intentionally left for subsequent isolated promotion passes.
 *
 * Visual authority:
 * docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V2_EXTENDED_RUN7_OWNER_APPROVED.md
 */
internal class DesktopCustomV2ExtendedRenderer(
    private val document: PDDocument,
    sourceTemplate: PDDocument,
    private val resourceLoader: (String) -> InputStream?,
) {
    private val layers = LayerUtility(document)
    private val resources = Resources.load(document, sourceTemplate, resourceLoader)

    fun appendExtendedPages(plan: PcSheetPdfRenderPlan) {
        require(plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE ||
            plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY) {
            "Custom-v2 Extended renderer received a non-v2 visual family."
        }

        if (PcSheetExtendedPageKind.CUSTOM_STATISTICS in plan.mandatoryExtendedPages) {
            val stats = plan.snapshot.customStatistics
            require(!stats.isEmpty) { "Mandatory Custom Statistics page requires custom statistics." }

            when (plan.request.visualFamily) {
                PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE ->
                    appendPerAttributePages(stats.attributes, stats.skills)
                PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY ->
                    appendPerAbilityPages(stats.attributes, stats.skills)
                else -> error("Unreachable Custom-v2 family branch.")
            }
        }

        appendTraitsExtendedPages(plan)
        appendCombatExtendedPages(plan)

        appendResourcesExtendedPages(plan)

        appendInventoryExtendedPages(plan)
        appendSpellExtendedPages(plan)
        appendNotesExtendedPages(plan)
    }

    private fun needsTraitsExtendedPage(plan: PcSheetPdfRenderPlan): Boolean {
        val sheet = plan.snapshot.aggregate.sheet
        val semanticTraits = sheet.traits.any {
            !isSpeciesIdentityTrait(it, plan) && !traitHasDedicatedActionOrResource(it, plan)
        }
        return semanticTraits ||
            sheet.proficiencies.isNotEmpty() ||
            traitSupplementLines(plan).isNotEmpty()
    }

    private fun needsResourcesExtendedPage(plan: PcSheetPdfRenderPlan): Boolean {
        val aggregate = plan.snapshot.aggregate
        return aggregate.sheet.resources.isNotEmpty() ||
            aggregate.sheet.classOptions.isNotEmpty() ||
            aggregate.successor.customMarkers.isNotEmpty()
    }

    private fun appendPerAttributePages(
        attributes: List<PcSheetCustomAttributeProjection>,
        skills: List<PcSheetCustomSkillProjection>,
    ) {
        val linkedByCustom = skills
            .filter { it.ability.customAttributeId != null }
            .groupBy { it.ability.customAttributeId }

        val attributeRows = attributes.flatMap { projection ->
            val linked = linkedByCustom[projection.attribute.id].orEmpty()
                .sortedBy { it.skill.sortOrder }
            val noteLines = projection.attribute.notes
                .orEmpty()
                .trim()
                .takeIf { it.isNotEmpty() }
                ?.let {
                    wrapByWidth(
                        resources.fira,
                        it,
                        7.7f,
                        PER_ATTRIBUTE_NOTE_TEXT_WIDTH,
                    )
                }
                .orEmpty()

            val slices = maxOf(
                1,
                pageCount(linked.size, PER_ATTRIBUTE_LINKED_SKILLS_PER_ROW),
                pageCount(noteLines.size, PER_ATTRIBUTE_NOTE_LINES_PER_ROW),
            )

            (0 until slices).map { sliceIndex ->
                PerAttributePageRow(
                    attribute = AttributeColumnSlice(
                        projection = projection,
                        skills = linked
                            .drop(sliceIndex * PER_ATTRIBUTE_LINKED_SKILLS_PER_ROW)
                            .take(PER_ATTRIBUTE_LINKED_SKILLS_PER_ROW),
                        noteLines = noteLines
                            .drop(sliceIndex * PER_ATTRIBUTE_NOTE_LINES_PER_ROW)
                            .take(PER_ATTRIBUTE_NOTE_LINES_PER_ROW),
                    ),
                    continuation = sliceIndex > 0,
                )
            }
        }

        val standardRows = skills
            .filter { it.ability.builtIn != null }
            .sortedBy { it.skill.sortOrder }
            .chunked(PER_ATTRIBUTE_STANDARD_SKILLS_PER_ROW)
            .map { group ->
                PerAttributePageRow(
                    standardSkills = group,
                )
            }

        val rows = attributeRows + standardRows
        val pages = rows.chunked(PER_ATTRIBUTE_ROWS_PER_PAGE).ifEmpty { listOf(emptyList()) }

        pages.forEachIndexed { pageIndex, pageRows ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderPerAttributePage(
                page = page,
                rows = pageRows,
                allAttributes = attributes,
                pageIndex = pageIndex,
            )
        }
    }

    private fun renderPerAttributePage(
        page: PDPage,
        rows: List<PerAttributePageRow>,
        allAttributes: List<PcSheetCustomAttributeProjection>,
        pageIndex: Int,
    ) {
        val layerPrefix = if (pageIndex == 0) "V2X ATTR" else "V2X ATTR ${pageIndex + 1}"

        appendLayer(page, "$layerPrefix - STRUCTURE") { s ->
            pageHeaderStructure(s)
            rows.forEachIndexed { index, row ->
                val top = PER_ATTRIBUTE_FIRST_ROW_TOP + index * PER_ATTRIBUTE_ROW_HEIGHT
                if (row.attribute != null) {
                    drawAttributeOrnament(s, 14.3f, top + 18f)
                    drawRule(s, 108f, 192f, top + 52f, 0.6f)
                    repeat(PER_ATTRIBUTE_LINKED_SKILLS_PER_ROW) { skillRow ->
                        val y = top + 32f + skillRow * PER_ATTRIBUTE_ROW_STEP
                        drawRule(s, 220f, 392f, y, 0.55f)
                    }
                    repeat(PER_ATTRIBUTE_NOTE_LINES_PER_ROW) { noteRow ->
                        val y = top + 32f + noteRow * PER_ATTRIBUTE_ROW_STEP
                        drawRule(s, 405f, 590f, y, 0.55f)
                    }
                } else {
                    fill(
                        s,
                        14f,
                        top + 8f,
                        584f,
                        72f,
                        if (index % 2 == 0) SOURCE_GRAY_LIGHT else SOURCE_GRAY_DARK,
                    )
                    repeat(PER_ATTRIBUTE_STANDARD_SKILLS_PER_ROW) { skillRow ->
                        val y = top + 34f + skillRow * PER_ATTRIBUTE_ROW_STEP
                        drawRule(s, 32f, 586f, y, 0.55f)
                    }
                }
                drawRule(s, 14f, 598f, top + 88f, 0.7f)
            }
        }

        appendLayer(page, "$layerPrefix - CLEANUP") { }

        appendLayer(page, "$layerPrefix - LABELS") { s ->
            pageTitle(s, "ESTADÍSTICAS PERSONALIZADAS")
            rows.forEachIndexed { index, row ->
                val top = PER_ATTRIBUTE_FIRST_ROW_TOP + index * PER_ATTRIBUTE_ROW_HEIGHT
                row.attribute?.let { slice ->
                    val projection = slice.projection
                    textTopSource(
                        s,
                        resources.corbelBold,
                        resources.firaSemibold,
                        18f,
                        top + 2f,
                        pcSheetIntegratedAttributeTitle(
                            projection.attribute.name,
                            projection.attribute.abbreviation,
                        ) + if (row.continuation) " · CONT." else "",
                        12.12f,
                        SOURCE_CORBEL_ATTRIBUTE_SCALE,
                    )
                    textAboveRuleSource(
                        s,
                        resources.corbel,
                        resources.fira,
                        Rule(108f, 162f, top + 52f),
                        "Tirada de Salvación",
                        7.75f,
                        2.0f,
                        SOURCE_CORBEL_COMPACT_SCALE,
                    )
                    centeredSource(
                        s,
                        resources.corbelBold,
                        resources.firaSemibold,
                        TopRect(220f, top + 8f, 172f, 18f),
                        "HABILIDADES",
                        7.8f,
                        SOURCE_CORBEL_HEADING_SCALE,
                    )
                    centeredSource(
                        s,
                        resources.corbelBold,
                        resources.firaSemibold,
                        TopRect(405f, top + 8f, 185f, 18f),
                        "NOTAS",
                        7.8f,
                        SOURCE_CORBEL_HEADING_SCALE,
                    )
                } ?: centeredSource(
                    s,
                    resources.corbelBold,
                    resources.firaSemibold,
                    TopRect(18f, top + 8f, 570f, 18f),
                    "HABILIDADES ADICIONALES",
                    8.4f,
                    SOURCE_CORBEL_HEADING_SCALE,
                )
            }
        }

        appendLayer(page, "$layerPrefix - VALUES") { s ->
            rows.forEachIndexed { index, row ->
                val top = PER_ATTRIBUTE_FIRST_ROW_TOP + index * PER_ATTRIBUTE_ROW_HEIGHT
                row.attribute?.let { slice ->
                    val attr = slice.projection.attribute
                    centered(
                        s,
                        resources.firaSemibold,
                        TopRect(31.8f, top + 26f, 25.5f, 18f),
                        attr.score.toString(),
                        17f,
                    )
                    centered(
                        s,
                        resources.firaSemibold,
                        TopRect(62.3f, top + 45f, 23f, 16.5f),
                        signed(attr.modifier),
                        15.5f,
                    )
                    slice.projection.savingThrowTotal?.let { total ->
                        centeredAboveRule(
                            s,
                            resources.firaSemibold,
                            Rule(162f, 192f, top + 52f),
                            signed(total),
                            8.8f,
                            2.0f,
                        )
                    }

                    slice.skills.forEachIndexed { skillRow, projection ->
                        val y = top + 32f + skillRow * PER_ATTRIBUTE_ROW_STEP
                        textAboveRuleSource(
                            s,
                            resources.corbel,
                            resources.fira,
                            Rule(232f, 354f, y),
                            pcSheetContextualSkillIdentity(
                                skillName = projection.skill.name,
                                attributeKey = pcSheetCompactAttributeKey(attr.abbreviation),
                                ownerAttributeStructurallyVisible = true,
                            ),
                            7.75f,
                            2.2f,
                            SOURCE_CORBEL_COMPACT_SCALE,
                        )
                        projection.total?.let { total ->
                            centeredAboveRule(
                                s,
                                resources.firaSemibold,
                                Rule(354f, 392f, y),
                                signed(total),
                                8.8f,
                                2.2f,
                            )
                        }
                    }

                    slice.noteLines.forEachIndexed { noteRow, note ->
                        val y = top + 32f + noteRow * PER_ATTRIBUTE_ROW_STEP
                        textAboveRule(
                            s,
                            resources.fira,
                            Rule(405f, 590f, y),
                            note,
                            7.7f,
                            7.0f,
                            2.2f,
                        )
                    }
                } ?: row.standardSkills.forEachIndexed { skillRow, projection ->
                    val y = top + 34f + skillRow * PER_ATTRIBUTE_ROW_STEP
                    val label = pcSheetContextualSkillIdentity(
                        skillName = projection.skill.name,
                        attributeKey = abilityKey(projection.ability, allAttributes),
                        ownerAttributeStructurallyVisible = false,
                    )
                    textAboveRuleSource(
                        s,
                        resources.corbel,
                        resources.fira,
                        Rule(44f, 522f, y),
                        label,
                        7.75f,
                        2.2f,
                        SOURCE_CORBEL_COMPACT_SCALE,
                    )
                    projection.total?.let { total ->
                        centeredAboveRule(
                            s,
                            resources.firaSemibold,
                            Rule(522f, 586f, y),
                            signed(total),
                            8.8f,
                            2.2f,
                        )
                    }
                }
            }
        }

        appendLayer(page, "$layerPrefix - MARKERS") { s ->
            rows.forEachIndexed { index, row ->
                val top = PER_ATTRIBUTE_FIRST_ROW_TOP + index * PER_ATTRIBUTE_ROW_HEIGHT
                row.attribute?.let { slice ->
                    val projection = slice.projection
                    val saveTraining = if (
                        projection.attribute.savingThrowEnabled &&
                        projection.attribute.savingThrowProficient
                    ) {
                        Training.PROFICIENT
                    } else {
                        Training.NONE
                    }
                    drawV2TrainingBox(
                        s,
                        TopRect(98.5f, top + 42.5f, 8.5f, 9f),
                        saveTraining,
                    )
                    slice.skills.forEachIndexed { skillRow, skill ->
                        drawV2TrainingBox(
                            s,
                            TopRect(
                                208f,
                                top + 22.5f + skillRow * PER_ATTRIBUTE_ROW_STEP,
                                8.5f,
                                9f,
                            ),
                            training(skill.skill.training),
                        )
                    }
                } ?: row.standardSkills.forEachIndexed { skillRow, skill ->
                    drawV2TrainingBox(
                        s,
                        TopRect(
                            20f,
                            top + 24.5f + skillRow * PER_ATTRIBUTE_ROW_STEP,
                            8.5f,
                            9f,
                        ),
                        training(skill.skill.training),
                    )
                }
            }
        }
    }

    private fun appendPerAbilityPages(
        attributes: List<PcSheetCustomAttributeProjection>,
        skills: List<PcSheetCustomSkillProjection>,
    ) {
        val saves = attributes.filter { it.attribute.savingThrowEnabled }
        val pages = maxOf(
            1,
            pageCount(attributes.size, ABILITY_ATTRIBUTES_PER_PAGE),
            pageCount(saves.size, ABILITY_SAVES_PER_PAGE),
            pageCount(skills.size, ABILITY_SKILLS_PER_PAGE),
        )

        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderPerAbilityPage(
                page = page,
                attributes = attributes
                    .drop(pageIndex * ABILITY_ATTRIBUTES_PER_PAGE)
                    .take(ABILITY_ATTRIBUTES_PER_PAGE),
                saves = saves
                    .drop(pageIndex * ABILITY_SAVES_PER_PAGE)
                    .take(ABILITY_SAVES_PER_PAGE),
                skills = skills
                    .drop(pageIndex * ABILITY_SKILLS_PER_PAGE)
                    .take(ABILITY_SKILLS_PER_PAGE),
                allAttributes = attributes,
                pageIndex = pageIndex,
            )
        }
    }

    private fun renderPerAbilityPage(
        page: PDPage,
        attributes: List<PcSheetCustomAttributeProjection>,
        saves: List<PcSheetCustomAttributeProjection>,
        skills: List<PcSheetCustomSkillProjection>,
        allAttributes: List<PcSheetCustomAttributeProjection>,
        pageIndex: Int,
    ) {
        val layerPrefix = if (pageIndex == 0) "V2X ABILITY" else "V2X ABILITY ${pageIndex + 1}"

        appendLayer(page, "$layerPrefix - STRUCTURE") { s ->
            pageHeaderStructure(s)
            fill(s, 14f, 104f, 174f, 30f, SOURCE_GRAY_LIGHT)
            fill(s, 202f, 104f, 150f, 30f, SOURCE_GRAY_LIGHT)
            fill(s, 366f, 104f, 232f, 30f, SOURCE_GRAY_LIGHT)

            repeat(ABILITY_ATTRIBUTES_PER_PAGE) { index ->
                val top = 136f + index * 96f
                fill(s, 14f, top, 174f, 94f, if (index % 2 == 0) SOURCE_GRAY_LIGHT else SOURCE_GRAY_DARK)
                drawAttributeOrnament(s, 14.3f, top + 24f)
                drawRule(s, 22f, 180f, top + 94f, 0.55f)
            }
            bandedRows(s, 202f, 352f, 154f, ABILITY_SAVES_PER_PAGE, 17f, 1)
            bandedRows(s, 366f, 598f, 154f, ABILITY_SKILLS_PER_PAGE, 17f, 0)
        }
        appendLayer(page, "$layerPrefix - CLEANUP") { }
        appendLayer(page, "$layerPrefix - LABELS") { s ->
            pageTitle(s, "ESTADÍSTICAS PERSONALIZADAS")
            centeredFixedScale(s, resources.corbelBold, TopRect(14f, 108f, 174f, 22f), "ATRIBUTOS", 7.8f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, resources.corbelBold, TopRect(202f, 108f, 150f, 22f), "TIRADAS DE SALVACIÓN", 7.8f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, resources.corbelBold, TopRect(366f, 108f, 232f, 22f), "HABILIDADES", 7.8f, SOURCE_CORBEL_HEADING_SCALE)
            attributes.forEachIndexed { index, projection ->
                textTopSource(
                    s, resources.corbelBold, resources.firaSemibold,
                    14f, 142f + index * 96f,
                    pcSheetIntegratedAttributeTitle(projection.attribute.name, projection.attribute.abbreviation),
                    12.12f, SOURCE_CORBEL_ATTRIBUTE_SCALE,
                )
            }
        }
        appendLayer(page, "$layerPrefix - VALUES") { s ->
            attributes.forEachIndexed { index, projection ->
                val ornamentTop = 160f + index * 96f
                centered(s, resources.firaSemibold, TopRect(31.8f, ornamentTop + 8f, 25.5f, 18f), projection.attribute.score.toString(), 17f)
                centered(s, resources.firaSemibold, TopRect(62.3f, ornamentTop + 27f, 23f, 16.5f), signed(projection.attribute.modifier), 15.5f)
            }

            saves.forEachIndexed { index, projection ->
                val y = 154f + index * 17f
                textAboveRuleSource(
                    s, resources.corbel, resources.fira,
                    Rule(235f, 308f, y),
                    pcSheetIntegratedAttributeTitle(projection.attribute.name, projection.attribute.abbreviation),
                    7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE,
                )
                projection.savingThrowTotal?.let {
                    centeredAboveRule(s, resources.firaSemibold, Rule(308f, 342f, y), signed(it), 8.8f, 2.2f)
                }
            }

            skills.forEachIndexed { index, projection ->
                val y = 154f + index * 17f
                val label = pcSheetContextualSkillIdentity(
                    skillName = projection.skill.name,
                    attributeKey = abilityKey(projection.ability, allAttributes),
                    ownerAttributeStructurallyVisible = false,
                )
                textAboveRuleSource(
                    s, resources.corbel, resources.fira,
                    Rule(399f, 548f, y), label, 7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE,
                )
                projection.total?.let {
                    centeredAboveRule(s, resources.firaSemibold, Rule(548f, 588f, y), signed(it), 8.8f, 2.2f)
                }
            }
        }
        appendLayer(page, "$layerPrefix - MARKERS") { s ->
            repeat(ABILITY_SAVES_PER_PAGE) { row ->
                val projection = saves.getOrNull(row)
                drawV2TrainingBox(
                    s,
                    TopRect(220f, 142f + row * 17f, 8.5f, 9f),
                    if (projection?.attribute?.savingThrowProficient == true) {
                        Training.PROFICIENT
                    } else {
                        Training.NONE
                    },
                )
            }
            repeat(ABILITY_SKILLS_PER_PAGE) { row ->
                drawV2TrainingBox(
                    s,
                    TopRect(384f, 142f + row * 17f, 8.5f, 9f),
                    skills.getOrNull(row)?.let { training(it.skill.training) } ?: Training.NONE,
                )
            }
        }
    }

    private fun appendTraitsExtendedPages(plan: PcSheetPdfRenderPlan) {
        if (!needsTraitsExtendedPage(plan)) return

        val sheet = plan.snapshot.aggregate.sheet
        val traits = sheet.traits
            .sortedBy { it.sortOrder }
            .filterNot { isSpeciesIdentityTrait(it, plan) || traitHasDedicatedActionOrResource(it, plan) }
        val leftTraits = traits.filter {
            it.type == CharacterTraitType.CLASS ||
                it.type == CharacterTraitType.FEAT ||
                it.type == CharacterTraitType.GIFT_BLESSING
        }
        val rightTraits = traits.filterNot { it in leftTraits }

        val featuredLeft = leftTraits.take(2)
        val featuredRight = rightTraits.take(2)
        val featuredIds = (featuredLeft + featuredRight).map { it.id }.toSet()
        val remaining = traits.filterNot { it.id in featuredIds }

        val detailLines = buildList {
            addAll(traitSupplementLines(plan))
            featuredLeft.forEach { trait ->
                addAll(featureOverflowLines(trait, 269f))
            }
            featuredRight.forEach { trait ->
                addAll(featureOverflowLines(trait, 283f))
            }
            remaining.forEach { trait ->
                addAll(fullTraitDetailLines(trait))
            }
        }

        val proficiencies = sheet.proficiencies.sortedBy { it.sortOrder }
        val pages = maxOf(
            1,
            pageCount(remaining.size, TRAIT_NAME_INDEX_PER_PAGE),
            pageCount(detailLines.size, TRAIT_DETAIL_LINES_PER_PAGE),
            pageCount(proficiencies.size, TRAIT_PROFICIENCIES_PER_PAGE),
        )

        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderTraitsPage(
                page = page,
                featuredLeft = if (pageIndex == 0) featuredLeft else emptyList(),
                featuredRight = if (pageIndex == 0) featuredRight else emptyList(),
                nameIndex = remaining
                    .drop(pageIndex * TRAIT_NAME_INDEX_PER_PAGE)
                    .take(TRAIT_NAME_INDEX_PER_PAGE),
                detailLines = detailLines
                    .drop(pageIndex * TRAIT_DETAIL_LINES_PER_PAGE)
                    .take(TRAIT_DETAIL_LINES_PER_PAGE),
                proficiencies = proficiencies
                    .drop(pageIndex * TRAIT_PROFICIENCIES_PER_PAGE)
                    .take(TRAIT_PROFICIENCIES_PER_PAGE),
                pageIndex = pageIndex,
            )
        }
    }

    private fun renderTraitsPage(
        page: PDPage,
        featuredLeft: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>,
        featuredRight: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>,
        nameIndex: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>,
        detailLines: List<String>,
        proficiencies: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency>,
        pageIndex: Int,
    ) {
        val layerPrefix = if (pageIndex == 0) "V2X TRAITS" else "V2X TRAITS ${pageIndex + 1}"

        appendLayer(page, "$layerPrefix - STRUCTURE") { s ->
            pageHeaderStructure(s)
            bandedRows(s, 14f, 291f, 137f, 36, 17f, 0)
            bandedRows(s, 307f, 598f, 137f, 36, 17f, 1)
            fill(s, 14f, 96f, 277f, 34f, Color.WHITE)
            fill(s, 307f, 96f, 291f, 34f, Color.WHITE)
            fill(s, 14f, 358f, 277f, 34f, Color.WHITE)
            fill(s, 307f, 358f, 291f, 34f, Color.WHITE)
            fill(s, 14f, 579f, 277f, 34f, Color.WHITE)
            fill(s, 307f, 579f, 291f, 34f, Color.WHITE)
            drawRule(s, 14f, 291f, 358f, 0.8f)
            drawRule(s, 307f, 598f, 358f, 0.8f)
            drawRule(s, 14f, 291f, 579f, 0.8f)
            drawRule(s, 307f, 598f, 579f, 0.8f)
        }
        appendLayer(page, "$layerPrefix - CLEANUP") { }
        appendLayer(page, "$layerPrefix - LABELS") { s ->
            pageTitle(s, "RASGOS Y ATRIBUTOS")
            centeredFixedScale(s, resources.corbelBold, TopRect(14f, 99f, 277f, 22f), "CLASE / DOTES", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, resources.corbelBold, TopRect(307f, 99f, 291f, 22f), "RAZA / TRASFONDO / OTROS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, resources.corbelBold, TopRect(14f, 363f, 277f, 22f), "OTROS RASGOS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, resources.corbelBold, TopRect(307f, 363f, 291f, 22f), "DETALLES / NOTAS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, resources.corbelBold, TopRect(14f, 584f, 277f, 22f), "COMPETENCIAS / IDIOMAS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(s, resources.corbelBold, TopRect(307f, 584f, 291f, 22f), "CONTINUACIÓN", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
        }
        appendLayer(page, "$layerPrefix - VALUES") { s ->
            var leftFeatureRule = 137f
            featuredLeft.forEach { trait ->
                leftFeatureRule = featureEntry(s, 14f, leftFeatureRule, 277f, trait)
            }
            var rightFeatureRule = 137f
            featuredRight.forEach { trait ->
                rightFeatureRule = featureEntry(s, 307f, rightFeatureRule, 291f, trait)
            }

            nameIndex.forEachIndexed { index, trait ->
                textAboveRule(s, resources.fira, Rule(18f, 287f, 392f + index * 17f), trait.name, 8.1f, 6.5f, 2.2f)
            }

            detailLines.take(10).forEachIndexed { index, line ->
                textAboveRule(s, resources.fira, Rule(311f, 594f, 392f + index * 17f), line, 7.7f, 6.2f, 2.2f)
            }

            proficiencies.forEachIndexed { index, proficiency ->
                val label = buildString {
                    append(proficiency.name)
                    proficiency.source?.takeIf { it.isNotBlank() }?.let { append(" · ").append(it) }
                    proficiency.notes?.takeIf { it.isNotBlank() }?.let { append(" · ").append(it) }
                }
                textAboveRule(s, resources.fira, Rule(18f, 287f, 613f + index * 17f), label, 8.0f, 6.2f, 2.2f)
            }

            detailLines.drop(10).take(8).forEachIndexed { index, line ->
                textAboveRule(s, resources.fira, Rule(311f, 594f, 613f + index * 17f), line, 7.7f, 6.2f, 2.2f)
            }
        }
        appendLayer(page, "$layerPrefix - MARKERS") { }
    }

    private fun featureOverflowLines(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
        width: Float,
    ): List<String> {
        val lines = featureDescriptionLines(trait, width)
        val overflow = lines.drop(FEATURE_DESCRIPTION_LINES)
        if (overflow.isEmpty()) return emptyList()

        val continuationText = trait.name + ": " + overflow.joinToString(" ")
        return wrapByWidth(
            resources.fira,
            continuationText,
            7.7f,
            TRAIT_CONTINUATION_TEXT_WIDTH,
        )
    }

    private fun traitSupplementLines(plan: PcSheetPdfRenderPlan): List<String> {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val closure = aggregate.closure
        val successor = aggregate.successor
        val background = sheet.background
        val lines = mutableListOf<String>()

        fun addFull(label: String, value: String) {
            val clean = value.trim()
            if (clean.isNotEmpty()) {
                lines += wrapByWidth(resources.fira, "$label: $clean", 7.7f, 281f)
            }
        }

        fun addOverflow(label: String, value: String, maxChars: Int, baseLines: Int) {
            val clean = value.trim()
            if (clean.isEmpty()) return
            val overflow = wrapForRulesByChars(clean, maxChars).drop(baseLines)
            if (overflow.isNotEmpty()) {
                lines += wrapByWidth(
                    resources.fira,
                    "$label (cont.): " + overflow.joinToString(" "),
                    7.7f,
                    281f,
                )
            }
        }

        val backgroundSummary = listOf(background.name, background.summary)
            .filter { it.isNotBlank() }
            .joinToString(" - ")
        addOverflow("Trasfondo", backgroundSummary, 76, 3)
        addOverflow("Vínculos", background.bonds, 76, 3)
        addOverflow("Ideales", background.ideals, 76, 3)
        addOverflow("Historia", background.story, 76, 12)
        val orderedClasses = sheet.classes.sortedBy { it.sortOrder }
        val classSummary = orderedClasses.joinToString(" / ") { classLevel ->
            classLevel.name + " " + classLevel.level
        }
        if (classSummary.length > 32) addFull("Clases", classSummary)
        if (sheet.name.length > 36) addFull("Nombre", sheet.name)
        if (sheet.status != io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.ACTIVE) {
            addFull("Estado", characterStatusLabel(sheet.status))
        }

        when (closure.progressMode) {
            CharacterProgressMode.EXPERIENCE -> addFull("Experiencia", closure.experiencePoints.toString())
            CharacterProgressMode.MILESTONE -> addFull("Progreso", closure.milestoneProgress)
        }

        if (sheet.tempHp != 0) addFull("PG temporales", sheet.tempHp.toString())
        if (sheet.deathSaveSuccesses != 0 || sheet.deathSaveFailures != 0) {
            addFull(
                "Salvaciones de muerte",
                sheet.deathSaveSuccesses.toString() + " éxitos / " +
                    sheet.deathSaveFailures.toString() + " fallos",
            )
        }
        if (sheet.passivePerceptionAdjustment != 0) {
            addFull("Percepción pasiva", sheet.passivePerception.toString())
        }

        sheet.weaponMasteries.sortedBy { it.sortOrder }.forEach { mastery ->
            addFull(
                "Maestría",
                listOf(
                    mastery.weaponName + " - " + mastery.masteryName,
                    mastery.source.orEmpty(),
                    mastery.notes.orEmpty(),
                ).filter { it.isNotBlank() }.joinToString(" · "),
            )
        }

        if (closure.exhaustionLevel > 0) addFull("Agotamiento", closure.exhaustionLevel.toString())
        closure.concentration?.let { concentration ->
            addFull(
                "Concentración",
                listOf(concentration.name, concentration.notes.orEmpty())
                    .filter { it.isNotBlank() }.joinToString(" · "),
            )
        }
        closure.conditions.sortedBy { it.sortOrder }.forEach { condition ->
            addFull(
                "Condición",
                listOf(condition.name, condition.source.orEmpty(), condition.notes.orEmpty())
                    .filter { it.isNotBlank() }.joinToString(" · "),
            )
        }
        closure.defenses.sortedBy { it.sortOrder }.forEach { defense ->
            addFull(
                defenseTypeLabel(defense.type),
                listOf(defense.name, defense.source.orEmpty(), defense.notes.orEmpty())
                    .filter { it.isNotBlank() }.joinToString(" · "),
            )
        }
        closure.movements.sortedBy { it.sortOrder }.forEach { movement ->
            addFull(
                "Movimiento",
                buildList {
                    add(movementTypeLabel(movement.type) + " - " + movement.name)
                    movement.speedFeet?.let { add("$it ft") }
                    movement.notes?.takeIf { it.isNotBlank() }?.let(::add)
                }.joinToString(" · "),
            )
        }
        closure.senses.sortedBy { it.sortOrder }.forEach { sense ->
            addFull(
                "Sentido",
                buildList {
                    add(sense.name)
                    sense.rangeFeet?.let { add("$it ft") }
                    sense.notes?.takeIf { it.isNotBlank() }?.let(::add)
                }.joinToString(" · "),
            )
        }
        closure.temporaryEffects
            .filter { it.active }
            .sortedBy { it.sortOrder }
            .forEach { effect ->
                addFull(
                    "Efecto temporal",
                    listOf(
                        effect.name,
                        effect.summary,
                        effect.durationText.orEmpty(),
                        effect.source.orEmpty(),
                        effect.notes.orEmpty(),
                    ).filter { it.isNotBlank() }.joinToString(" · "),
                )
            }

        val spellSources = sheet.spellcastingSources.associateBy { it.id }
        successor.spellcastingProfiles.forEach { profile ->
            val sourceName = spellSources[profile.sourceId]?.name ?: "Fuente mágica"
            val ability = when {
                profile.ability.builtIn != null -> when (profile.ability.builtIn) {
                    CharacterAbility.STRENGTH -> "FUE"
                    CharacterAbility.DEXTERITY -> "DES"
                    CharacterAbility.CONSTITUTION -> "CON"
                    CharacterAbility.INTELLIGENCE -> "INT"
                    CharacterAbility.WISDOM -> "SAB"
                    CharacterAbility.CHARISMA -> "CAR"
                    null -> ""
                }
                profile.ability.customAttributeId != null -> successor.customAttributes
                    .firstOrNull { it.id == profile.ability.customAttributeId }
                    ?.abbreviation
                    .orEmpty()
                else -> ""
            }
            addFull(
                "Lanzamiento",
                buildList {
                    add(sourceName)
                    if (ability.isNotBlank()) add(ability)
                    sheet.spellSaveDc(profile, successor)?.let { add("CD $it") }
                    sheet.spellAttackModifier(profile, successor)?.let { add("Ataque " + signed(it)) }
                }.joinToString(" · "),
            )
        }

        sheet.forms.sortedBy { it.sortOrder }.forEach { form ->
            addFull(
                "Forma",
                buildList {
                    add(form.name)
                    form.source?.takeIf { it.isNotBlank() }?.let(::add)
                    form.challengeRatingText?.takeIf { it.isNotBlank() }?.let { add("VD $it") }
                    form.armorClass?.let { add("CA $it") }
                    form.hitPoints?.let { add("PG $it") }
                    form.movement?.takeIf { it.isNotBlank() }?.let(::add)
                    form.senses?.takeIf { it.isNotBlank() }?.let(::add)
                    form.actionSummary.takeIf { it.isNotBlank() }?.let(::add)
                    form.notes?.takeIf { it.isNotBlank() }?.let(::add)
                }.joinToString(" · "),
            )
        }

        sheet.companions.sortedBy { it.sortOrder }.forEach { companion ->
            addFull(
                "Compañero",
                buildList {
                    add(companion.name + companion.kind.takeIf { it.isNotBlank() }?.let { " ($it)" }.orEmpty())
                    companion.source?.takeIf { it.isNotBlank() }?.let(::add)
                    companion.armorClass?.let { add("CA $it") }
                    companion.maxHp?.let { max ->
                        add("PG " + (companion.currentHp ?: max) + "/" + max)
                    }
                    if (companion.tempHp > 0) add("PG temp. " + companion.tempHp)
                    companion.speed?.takeIf { it.isNotBlank() }?.let(::add)
                    companion.abilitySummary?.takeIf { it.isNotBlank() }?.let(::add)
                    companion.sensesProficiencies?.takeIf { it.isNotBlank() }?.let(::add)
                    companion.traitsActions.takeIf { it.isNotBlank() }?.let(::add)
                    companion.notes?.takeIf { it.isNotBlank() }?.let(::add)
                }.joinToString(" · "),
            )
        }

        return lines
    }

    private fun appendCombatExtendedPages(plan: PcSheetPdfRenderPlan) {
        val logicalRows = combatReferenceRows(plan).map(::layoutCombatReferenceRow)
        if (logicalRows.isEmpty()) return

        packCombatRows(logicalRows).forEachIndexed { pageIndex, rows ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderCombatPage(
                page = page,
                rows = rows,
                pageIndex = pageIndex,
            )
        }
    }

    private fun combatReferenceRows(plan: PcSheetPdfRenderPlan): List<CombatReferenceRow> {
        val aggregate = plan.snapshot.aggregate
        val damageByCombatId = aggregate.successor.combatDamage.associateBy { it.combatEntryId }

        return aggregate.sheet.combatEntries
            .sortedBy { it.sortOrder }
            .mapIndexedNotNull { index, entry ->
                val structuredDamage = damageByCombatId[entry.id]?.components
                    ?.joinToString(" + ") { component ->
                        component.expression +
                            component.typeText?.takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()
                    }
                    .orEmpty()
                val needsReference =
                    index >= BASE_V2_COMBAT_CAPACITY ||
                        entry.type != io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK ||
                        !entry.notes.isNullOrBlank() ||
                        structuredDamage.isNotBlank()
                if (!needsReference) {
                    null
                } else {
                    val extraDamage = structuredDamage.takeIf {
                        it.isNotBlank() && !it.equals(entry.damageEffect.trim(), ignoreCase = true)
                    }
                    CombatReferenceRow(
                        name = combatTypeLabel(entry.type) + " — " + entry.name,
                        range = entry.rangeText.orEmpty().trim(),
                        bonus = entry.attackModifier?.let(::signed).orEmpty(),
                        effect = entry.damageEffect.trim(),
                        notes = listOf(
                            entry.notes.orEmpty().trim(),
                            extraDamage?.let { "Daño: $it" }.orEmpty(),
                        ).filter { it.isNotEmpty() }.joinToString(" · "),
                    )
                }
            }
    }

    private fun layoutCombatReferenceRow(row: CombatReferenceRow): CombatLayoutRow {
        val nameLines = combatCellLines(resources.fira, Rule(18f, 166f, 0f), row.name)
        val rangeLines = combatCellLines(resources.fira, Rule(174f, 230f, 0f), row.range)
        val bonusLines = combatCellLines(resources.firaSemibold, Rule(236f, 276f, 0f), row.bonus)
        val effectLines = combatCellLines(resources.fira, Rule(282f, 430f, 0f), row.effect)
        val noteLines = combatCellLines(resources.fira, Rule(436f, 594f, 0f), row.notes)
        val maximumLines = maxOf(
            1,
            nameLines.size,
            rangeLines.size,
            bonusLines.size,
            effectLines.size,
            noteLines.size,
        )
        val height = maxOf(
            COMBAT_MIN_LOGICAL_ROW_HEIGHT,
            COMBAT_CELL_VERTICAL_PADDING * 2f + maximumLines * COMBAT_CELL_LINE_HEIGHT,
        )
        require(height <= COMBAT_AVAILABLE_DATA_HEIGHT + 0.05f) {
            "Custom-v2 combat logical row exceeds one full Extended page: ${row.name}"
        }
        return CombatLayoutRow(
            row = row,
            nameLines = nameLines,
            rangeLines = rangeLines,
            bonusLines = bonusLines,
            effectLines = effectLines,
            noteLines = noteLines,
            height = height,
        )
    }

    private fun packCombatRows(rows: List<CombatLayoutRow>): List<List<CombatLayoutRow>> {
        val pages = mutableListOf<MutableList<CombatLayoutRow>>()
        var current = mutableListOf<CombatLayoutRow>()
        var usedHeight = 0f

        rows.forEach { row ->
            if (
                current.isNotEmpty() &&
                usedHeight + row.height > COMBAT_AVAILABLE_DATA_HEIGHT + 0.05f
            ) {
                pages += current
                current = mutableListOf()
                usedHeight = 0f
            }
            current += row
            usedHeight += row.height
        }
        if (current.isNotEmpty()) pages += current
        return pages
    }

    private fun combatCellLines(
        font: PDFont,
        rule: Rule,
        value: String,
        leftPadding: Float = 2f,
    ): List<String> {
        val clean = value.trim()
        if (clean.isEmpty()) return emptyList()

        val available = rule.endX - rule.startX - leftPadding - 1f
        val maximumRawWidthAtReadableScale =
            available / (COMBAT_MINIMUM_HORIZONTAL_SCALE / 100f)
        return wrapByWidth(
            font,
            clean,
            COMBAT_MINIMUM_BODY_SIZE,
            maximumRawWidthAtReadableScale,
        )
    }

    private fun renderCombatPage(
        page: PDPage,
        rows: List<CombatLayoutRow>,
        pageIndex: Int,
    ) {
        val layerPrefix = if (pageIndex == 0) "V2X COMBAT" else "V2X COMBAT ${pageIndex + 1}"
        val rowTops = mutableListOf<Float>()
        var cursorTop = COMBAT_FIRST_LOGICAL_ROW_TOP
        rows.forEach { row ->
            rowTops += cursorTop
            cursorTop += row.height
        }

        appendLayer(page, "$layerPrefix - STRUCTURE") { s ->
            pageHeaderStructure(s)
            fill(s, 14f, COMBAT_HEADER_TOP, 584f, COMBAT_HEADER_HEIGHT, SOURCE_GRAY_LIGHT)

            rows.forEachIndexed { index, row ->
                val top = rowTops[index]
                val tone = if (index % 2 == 0) SOURCE_GRAY_LIGHT else SOURCE_GRAY_DARK
                fill(s, 14f, top, 584f, row.height, tone)
                drawRule(s, 14f, 598f, top + row.height, 0.55f)
            }

            val tableBottom = rowTops.lastOrNull()
                ?.let { it + rows.last().height }
                ?: COMBAT_FIRST_LOGICAL_ROW_TOP
            listOf(170f, 232f, 278f, 432f).forEach { x ->
                verticalRule(s, x, COMBAT_HEADER_TOP, tableBottom, 0.45f)
            }
            drawRule(s, 14f, 598f, COMBAT_FIRST_LOGICAL_ROW_TOP, 0.7f)
        }

        appendLayer(page, "$layerPrefix - CLEANUP") { }

        appendLayer(page, "$layerPrefix - LABELS") { s ->
            pageTitle(s, "COMBATE / ACCIONES")
            tableLabel(s, 18f, COMBAT_HEADER_LABEL_TOP, 148f, "TIPO / NOMBRE")
            tableLabel(s, 174f, COMBAT_HEADER_LABEL_TOP, 56f, "RANGO")
            tableLabel(s, 236f, COMBAT_HEADER_LABEL_TOP, 40f, "BONIF.")
            tableLabel(s, 282f, COMBAT_HEADER_LABEL_TOP, 148f, "DAÑO / EFECTO")
            tableLabel(s, 436f, COMBAT_HEADER_LABEL_TOP, 158f, "NOTAS")
        }

        appendLayer(page, "$layerPrefix - VALUES") { s ->
            rows.forEachIndexed { index, row ->
                val top = rowTops[index]
                drawCombatCellLines(s, resources.fira, 18f, 166f, top, row.nameLines, 7.8f)
                drawCombatCellLines(s, resources.fira, 174f, 230f, top, row.rangeLines, 7.6f)
                drawCombatCellLines(s, resources.firaSemibold, 236f, 276f, top, row.bonusLines, 7.8f)
                drawCombatCellLines(s, resources.fira, 282f, 430f, top, row.effectLines, 7.6f)
                drawCombatCellLines(s, resources.fira, 436f, 594f, top, row.noteLines, 7.4f)
            }
        }

        appendLayer(page, "$layerPrefix - MARKERS") { }
    }

    private fun drawCombatCellLines(
        s: PDFormContentStream,
        font: PDFont,
        startX: Float,
        endX: Float,
        rowTop: Float,
        lines: List<String>,
        preferredSize: Float,
    ) {
        lines.forEachIndexed { index, line ->
            val baselineRuleTop =
                rowTop + COMBAT_CELL_VERTICAL_PADDING +
                    (index + 1) * COMBAT_CELL_LINE_HEIGHT - 2f
            combatCellText(
                s,
                font,
                Rule(startX, endX, baselineRuleTop),
                line,
                preferredSize,
            )
        }
    }

    private fun characterStatusLabel(
        status: io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus,
    ): String = when (status) {
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.ACTIVE -> "Activo"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.INACTIVE -> "Inactivo"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.RETIRED -> "Retirado"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.DEAD -> "Muerto"
    }

    private fun combatTypeLabel(
        type: io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType,
    ): String = when (type) {
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK -> "Ataque"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ACTION -> "Acción"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.BONUS_ACTION -> "Acción adicional"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.REACTION -> "Reacción"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.OTHER -> "Otro"
    }

    private fun defenseTypeLabel(type: CharacterDefenseType): String = when (type) {
        CharacterDefenseType.RESISTANCE -> "Resistencia"
        CharacterDefenseType.IMMUNITY -> "Inmunidad"
        CharacterDefenseType.VULNERABILITY -> "Vulnerabilidad"
    }

    private fun movementTypeLabel(type: CharacterMovementType): String = when (type) {
        CharacterMovementType.FLY -> "Volar"
        CharacterMovementType.SWIM -> "Nadar"
        CharacterMovementType.CLIMB -> "Trepar"
        CharacterMovementType.BURROW -> "Excavar"
        CharacterMovementType.OTHER -> "Otro"
    }

    private fun isSpeciesIdentityTrait(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
        plan: PcSheetPdfRenderPlan,
    ): Boolean {
        if (trait.type != CharacterTraitType.SPECIES_RACE) return false
        val aggregate = plan.snapshot.aggregate
        val identityNames = listOfNotNull(
            aggregate.sheet.background.race.trim().takeIf { it.isNotEmpty() },
            aggregate.successor.speciesIdentity?.name?.trim()?.takeIf { it.isNotEmpty() },
            aggregate.successor.subraceIdentity?.name?.trim()?.takeIf { it.isNotEmpty() },
        )
        return identityNames.any { it.equals(trait.name.trim(), ignoreCase = true) }
    }

    private fun traitHasDedicatedActionOrResource(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
        plan: PcSheetPdfRenderPlan,
    ): Boolean {
        val name = trait.name.trim()
        if (name.isEmpty()) return false
        val sheet = plan.snapshot.aggregate.sheet
        return sheet.resources.any { it.name.trim().equals(name, ignoreCase = true) } ||
            sheet.combatEntries.any {
                it.type != CharacterCombatEntryType.ATTACK &&
                    it.name.trim().equals(name, ignoreCase = true)
            }
    }

    private fun fullTraitDetailLines(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
    ): List<String> {
        val uses = trait.pcSheetWritableUsesTrackerOrNull()?.let { tracker ->
            buildString {
                append("Usos ").append(tracker.compactEditableLabel())
                trait.recovery?.takeIf { it.isNotBlank() }?.let { append(" · ").append(it) }
            }
        } ?: trait.recovery?.takeIf { it.isNotBlank() }

        val metadata = listOf(
            traitTypeLabel(trait.type),
            trait.source.trim(),
            trait.activation?.let(::activationLabel).orEmpty(),
            uses.orEmpty(),
            trait.description.trim(),
            trait.notes.orEmpty().trim(),
        ).filter { it.isNotEmpty() }.joinToString(" · ")

        if (metadata.isEmpty()) return listOf(trait.name)
        return wrapByWidth(resources.fira, trait.name + ": " + metadata, 7.7f, 281f)
    }

    private fun appendResourcesExtendedPages(plan: PcSheetPdfRenderPlan) {
        if (!needsResourcesExtendedPage(plan)) return

        val rows = resourceRenderLines(resourceRenderRows(plan))
        val options = optionRenderLines(plan.snapshot.aggregate.sheet.classOptions.sortedBy { it.sortOrder })
        val pages = maxOf(
            pageCount(rows.size, RESOURCE_ROWS_PER_PAGE),
            pageCount(options.size, RESOURCE_OPTIONS_PER_PAGE),
        )
        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderResources(
                page = page,
                rows = rows.drop(pageIndex * RESOURCE_ROWS_PER_PAGE).take(RESOURCE_ROWS_PER_PAGE),
                options = options.drop(pageIndex * RESOURCE_OPTIONS_PER_PAGE).take(RESOURCE_OPTIONS_PER_PAGE),
                pageIndex = pageIndex,
            )
        }
    }

    private fun resourceRenderRows(plan: PcSheetPdfRenderPlan): List<ResourceRenderRow> {
        val aggregate = plan.snapshot.aggregate
        val recoveryByResource = aggregate.closure.resourceRecovery.associateBy { it.resourceId }
        val configurationByResource = aggregate.successor.resourceConfigurations.associateBy { it.resourceId }

        val ordinary = aggregate.sheet.resources
            .sortedBy { it.sortOrder }
            .map { resource ->
                val recovery = recoveryByResource[resource.id]
                val kind = configurationByResource[resource.id]?.valueKind ?: CharacterTrackableValueKind.CURRENT_MAX
                val maximum = when (kind) {
                    CharacterTrackableValueKind.BINARY -> 1
                    CharacterTrackableValueKind.COUNTER,
                    CharacterTrackableValueKind.CURRENT_MAX -> resource.maxValue
                }
                val oneUse = maximum == 1
                val structuredRecovery = buildList {
                    recovery?.cadence?.let(::recoveryLabel)?.takeIf { it.isNotEmpty() }?.let(::add)
                    if (!oneUse || recovery?.amountMode != CharacterRecoveryAmountMode.TO_MAX) {
                        recovery?.amountMode
                            ?.let { recoveryAmountLabel(it, recovery.fixedAmount) }
                            ?.takeIf { it.isNotEmpty() }
                            ?.let(::add)
                    }
                    recovery?.notes.orEmpty().trim().takeIf { it.isNotEmpty() }?.let(::add)
                }
                ResourceRenderRow(
                    name = resource.name,
                    currentValue = resource.currentValue,
                    maximum = maximum,
                    valueKind = kind,
                    recovery = structuredRecovery
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(" · ")
                        ?: resource.recovery.orEmpty().trim(),
                    detail = listOf(
                        resource.source.orEmpty().trim(),
                        resource.notes.orEmpty().trim(),
                    ).filter { it.isNotEmpty() }.joinToString(" · "),
                    sortOrder = resource.sortOrder,
                    sourceRank = 0,
                )
            }

        val markers = aggregate.successor.customMarkers
            .sortedBy { it.sortOrder }
            .map { marker ->
                ResourceRenderRow(
                    name = marker.name,
                    currentValue = marker.currentValue,
                    maximum = when (marker.valueKind) {
                        CharacterTrackableValueKind.BINARY -> 1
                        CharacterTrackableValueKind.COUNTER,
                        CharacterTrackableValueKind.CURRENT_MAX -> marker.maxValue
                    },
                    valueKind = marker.valueKind,
                    recovery = listOf(
                        recoveryLabel(marker.recovery.cadence),
                        recoveryAmountLabel(marker.recovery.amountMode, marker.recovery.fixedAmount),
                    ).filter { it.isNotEmpty() }.joinToString(" · "),
                    detail = marker.notes.orEmpty().trim(),
                    sortOrder = marker.sortOrder,
                    sourceRank = 1,
                )
            }

        return (ordinary + markers)
            .sortedWith(compareBy<ResourceRenderRow> { it.sortOrder }.thenBy { it.sourceRank }.thenBy { it.name.lowercase() })
    }

    private fun resourceRenderLines(rows: List<ResourceRenderRow>): List<ResourceRenderLine> =
        rows.flatMap { row ->
            val nameLines = wrapByWidth(resources.fira, row.name, 7.6f, 196f).ifEmpty { listOf("") }
            val recoveryLines = wrapByWidth(resources.fira, row.recovery, 7.0f, 111f).ifEmpty { listOf("") }
            val detailLines = wrapByWidth(resources.fira, row.detail, 7.0f, 111f).ifEmpty { listOf("") }
            val count = maxOf(nameLines.size, recoveryLines.size, detailLines.size, 1)
            (0 until count).map { index ->
                ResourceRenderLine(
                    name = nameLines.getOrNull(index).orEmpty(),
                    currentValue = row.currentValue.takeIf { index == 0 },
                    maximum = row.maximum.takeIf { index == 0 },
                    oneUse = row.maximum == 1 && index == 0,
                    recovery = recoveryLines.getOrNull(index).orEmpty(),
                    detail = detailLines.getOrNull(index).orEmpty(),
                )
            }
        }

    private fun optionRenderLines(
        options: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOption>,
    ): List<OptionRenderLine> = options.flatMap { option ->
        val kindLines = wrapByWidth(resources.fira, optionKindLabel(option.kind), 7.0f, 76f).ifEmpty { listOf("") }
        val nameLines = wrapByWidth(resources.fira, option.name, 7.2f, 128f).ifEmpty { listOf("") }
        val detail = listOf(
            option.effectSummary.trim(),
            option.costText.orEmpty().trim(),
            option.source.orEmpty().trim(),
            option.notes.orEmpty().trim(),
        ).filter { it.isNotEmpty() }.joinToString(" · ")
        val detailLines = wrapByWidth(resources.fira, detail, 7.0f, 328f).ifEmpty { listOf("") }
        val count = maxOf(kindLines.size, nameLines.size, detailLines.size, 1)
        (0 until count).map { index ->
            OptionRenderLine(
                kind = kindLines.getOrNull(index).orEmpty(),
                name = nameLines.getOrNull(index).orEmpty(),
                detail = detailLines.getOrNull(index).orEmpty(),
                active = option.active && index == 0,
            )
        }
    }

    private fun renderResources(
        page: PDPage,
        rows: List<ResourceRenderLine>,
        options: List<OptionRenderLine>,
        pageIndex: Int,
    ) {
        val layerPrefix = if (pageIndex == 0) "V2X RESOURCES" else "V2X RESOURCES ${pageIndex + 1}"

        appendLayer(page, "$layerPrefix - STRUCTURE") { s ->
            pageHeaderStructure(s)
            fill(s, 14f, 96f, 584f, 22f, SOURCE_GRAY_LIGHT)
            bandedRows(s, 14f, 598f, 150f, RESOURCE_ROWS_PER_PAGE, 17f, 0)
            listOf(222f, 352f, 475f).forEach { x -> verticalRule(s, x, 120f, 303f, 0.45f) }

            drawRule(s, 14f, 598f, 329f, 0.8f)
            fill(s, 14f, 337f, 584f, 22f, SOURCE_GRAY_LIGHT)
            bandedRows(s, 14f, 598f, 398f, RESOURCE_OPTIONS_PER_PAGE, 17f, 1)
            listOf(30f, 118f, 258f).forEach { x -> verticalRule(s, x, 362f, 704f, 0.45f) }
        }
        appendLayer(page, "$layerPrefix - CLEANUP") { }
        appendLayer(page, "$layerPrefix - LABELS") { s ->
            pageTitle(s, "RECURSOS Y OPCIONES")
            centeredFixedScale(s, resources.corbelBold, TopRect(14f, 97f, 584f, 20f), "RECURSOS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            tableLabel(s, 14f, 121f, 208f, "RECURSO")
            tableLabel(s, 222f, 121f, 130f, "ACTUAL / MÁX.")
            tableLabel(s, 352f, 121f, 123f, "RESTABLECE")
            tableLabel(s, 475f, 121f, 123f, "ORIGEN / NOTAS")

            centeredFixedScale(s, resources.corbelBold, TopRect(14f, 338f, 584f, 20f), "OPCIONES", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            tableLabel(s, 30f, 364f, 88f, "TIPO")
            tableLabel(s, 118f, 364f, 140f, "OPCIÓN")
            tableLabel(s, 258f, 364f, 340f, "DESCRIPCIÓN / COSTE / ORIGEN")
        }
        appendLayer(page, "$layerPrefix - VALUES") { s ->
            rows.forEachIndexed { index, row ->
                val y = 150f + index * 17f
                if (row.name.isNotEmpty()) {
                    textAboveRule(s, resources.fira, Rule(18f, 218f, y), row.name, 7.6f, 6.6f, 2.3f)
                }

                val current = row.currentValue
                val maximum = row.maximum
                if (current != null) {
                    if (row.oneUse) {
                        centeredAboveRule(
                            s,
                            resources.firaSemibold,
                            Rule(226f, 348f, y),
                            current.coerceIn(0, 1).toString() + " / 1",
                            8.0f,
                            2.2f,
                        )
                    } else {
                        val canUseSymbols = maximum != null &&
                            maximum in 1..9 &&
                            current in 0..maximum
                        if (!canUseSymbols) {
                            val value = if (maximum == null) current.toString() else current.toString() + "/" + maximum
                            centeredAboveRule(s, resources.firaSemibold, Rule(226f, 348f, y), value, 8.5f, 2.2f)
                        }
                    }
                }

                if (row.recovery.isNotEmpty()) {
                    textAboveRule(s, resources.fira, Rule(356f, 471f, y), row.recovery, 7.0f, 6.2f, 2.3f)
                }
                if (row.detail.isNotEmpty()) {
                    textAboveRule(s, resources.fira, Rule(479f, 594f, y), row.detail, 7.0f, 6.2f, 2.3f)
                }
            }

            options.forEachIndexed { index, option ->
                val y = 398f + index * 17f
                if (option.kind.isNotEmpty()) {
                    textAboveRule(s, resources.fira, Rule(34f, 114f, y), option.kind, 7.0f, 6.2f, 2.3f)
                }
                if (option.name.isNotEmpty()) {
                    textAboveRule(s, resources.fira, Rule(122f, 254f, y), option.name, 7.2f, 6.2f, 2.3f)
                }
                if (option.detail.isNotEmpty()) {
                    textAboveRule(s, resources.fira, Rule(262f, 594f, y), option.detail, 7.0f, 6.2f, 2.3f)
                }
            }
        }
        appendLayer(page, "$layerPrefix - MARKERS") { s ->
            rows.forEachIndexed { index, row ->
                val current = row.currentValue
                val maximum = row.maximum
                if (
                    !row.oneUse &&
                    current != null &&
                    maximum != null &&
                    maximum in 1..9 &&
                    current in 0..maximum
                ) {
                    drawSquareCounter(
                        s,
                        236f,
                        141.5f + index * 17f,
                        current,
                        maximum,
                    )
                }
            }

            repeat(RESOURCE_OPTIONS_PER_PAGE) { row ->
                val option = options.getOrNull(row)
                drawV2TrainingBox(
                    s,
                    TopRect(16f, 386f + row * 17f, 8.5f, 9f),
                    if (option?.active == true) Training.PROFICIENT else Training.NONE,
                )
            }
        }
    }

    private fun recoveryAmountLabel(
        mode: CharacterRecoveryAmountMode,
        fixedAmount: Int?,
    ): String = when (mode) {
        CharacterRecoveryAmountMode.NONE -> ""
        CharacterRecoveryAmountMode.TO_MAX -> "A máximo"
        CharacterRecoveryAmountMode.FIXED -> fixedAmount?.let { "+$it" } ?: "Cantidad fija"
    }

    private fun appendInventoryExtendedPages(plan: PcSheetPdfRenderPlan) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val usageByItem = aggregate.closure.inventoryUsage.associateBy { it.itemId }
        val ordered = sheet.inventoryItems.sortedBy { it.sortOrder }
        val ordinary = ordered.filterNot { it.special }
        val ordinaryLines = ordinary.flatMapIndexed { index, item ->
            val usage = usageByItem[item.id]
            val needsFullContinuation =
                index >= BASE_V2_EQUIPMENT_CAPACITY ||
                    wrapByWidth(
                        resources.condensed,
                        inventoryBaseLabel(item),
                        7.0f,
                        V2_BASE_EQUIPMENT_TEXT_WIDTH,
                    ).size > 1
            if (needsFullContinuation) {
                inventoryContinuationLines(item, usage)
            } else {
                emptyList()
            }
        }

        val special = ordered.filter { it.special }
        val specialContinuation = special.mapIndexedNotNull { index, item ->
            val usage = usageByItem[item.id]
            val baseDetail = buildList {
                if (item.quantity != 1) add("Cant. " + item.quantity)
                item.weightLb?.let { add(formatInventoryWeight(it)) }
                if (item.attuned) add("Sintonizado")
                item.description?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
                item.notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            }.joinToString(" · ")
            val baseDetailOverflows =
                baseDetail.isNotBlank() &&
                    textWidth(resources.fira, baseDetail, 8.5f) > V2_BASE_SPECIAL_DETAIL_WIDTH
            val baseNameOverflows =
                textWidth(resources.fira, item.name, 8.5f) > V2_BASE_SPECIAL_NAME_WIDTH
            val locationOverflows = item.location?.trim()?.takeIf { it.isNotEmpty() }?.let {
                textWidth(resources.fira, it, 7.5f) > V2_BASE_SPECIAL_LOCATION_WIDTH
            } ?: false
            item.takeIf {
                index >= BASE_V2_SPECIAL_CAPACITY ||
                    usageMeaningful(usage) ||
                    baseNameOverflows ||
                    baseDetailOverflows ||
                    locationOverflows
            }
        }

        val specialDetailLines = specialContinuation.flatMap(::inventoryDetailContinuationLines)
        val equipmentContinuationLines = ordinaryLines + specialDetailLines

        val nativeV2Kinds = setOf(
            StandardCurrencyKind.PLATINUM,
            StandardCurrencyKind.GOLD,
            StandardCurrencyKind.SILVER,
            StandardCurrencyKind.COPPER,
        )
        val nonNativeCurrencies = sheet.currencies
            .filter { currency ->
                val kind = currency.standardCurrencyKindOrNull()
                (kind != null && kind !in nativeV2Kinds) ||
                    (!currency.isDefault && kind == null)
            }
            .sortedBy { it.sortOrder }

        val treasureLines = buildList {
            // The first adjacent OTROS rows are already consumed on the base page.
            nonNativeCurrencies
                .drop(BASE_V2_OTHER_CURRENCY_CAPACITY)
                .forEach { currency ->
                    add(currency.name + ": " + currency.amount)
                }
            addAll(
                plan.snapshot.aggregate.successor.preferences.valuablesText
                    .split(Regex("[;\\n]+"))
                    .map { it.trim() }
                    .filter { it.isNotEmpty() },
            )
        }.flatMap { value ->
            wrapByWidth(resources.fira, value, 8.0f, V2_TREASURE_COLUMN_WIDTH)
        }

        if (equipmentContinuationLines.isEmpty() && specialContinuation.isEmpty() && treasureLines.isEmpty()) return

        val ordinaryCapacity =
            if (treasureLines.isEmpty()) INVENTORY_CONTINUATION_CAPACITY
            else INVENTORY_EQUIPMENT_WITH_TREASURE_CAPACITY
        val pages = maxOf(
            pageCount(equipmentContinuationLines.size, ordinaryCapacity),
            pageCount(treasureLines.size, INVENTORY_TREASURE_CAPACITY),
            pageCount(specialContinuation.size, INVENTORY_SPECIAL_CAPACITY),
        )
        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderInventory(
                page = page,
                ordinary = equipmentContinuationLines
                    .drop(pageIndex * ordinaryCapacity)
                    .take(ordinaryCapacity),
                treasure = treasureLines
                    .drop(pageIndex * INVENTORY_TREASURE_CAPACITY)
                    .take(INVENTORY_TREASURE_CAPACITY),
                special = specialContinuation
                    .drop(pageIndex * INVENTORY_SPECIAL_CAPACITY)
                    .take(INVENTORY_SPECIAL_CAPACITY),
                usageByItem = usageByItem,
                pageIndex = pageIndex,
            )
        }
    }

    private fun renderInventory(
        page: PDPage,
        ordinary: List<String>,
        treasure: List<String>,
        special: List<CharacterInventoryItem>,
        usageByItem: Map<kotlin.uuid.Uuid, CharacterInventoryUsage>,
        pageIndex: Int,
    ) {
        // PDFBox OCG names are document-global, not page-local. Keep the first-page names exactly
        // compatible with the existing production proof/tests, and page-scope subsequent inventory
        // continuation layers so legitimate multi-page overflow cannot collide.
        val prefix = if (pageIndex == 0) "V2X INVENTORY" else "V2X INVENTORY P${pageIndex + 1}"

        val positionedSpecial = positionedSpecialItems(special, INVENTORY_SPECIAL_CAPACITY)

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            pageHeaderStructure(s)
            listOf(14f, 307f).forEach { blockX ->
                bandedRows(s, blockX, blockX + 277f, 139f, 19, 17f, 0)
                verticalRule(s, blockX + 139f, 122f, 462f, 0.45f)
            }
            drawRule(s, 14f, 598f, 480f, 0.8f)

            bandedRows(s, 14f, 598f, 548f, 12, 17f, 0)
            listOf(30f, 99f, 303f).forEach { x -> verticalRule(s, x, 512f, 752f, 0.45f) }
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { s ->
            pageTitle(s, "INVENTARIO / EQUIPO")
            centeredFixedScale(s, resources.corbelBold, TopRect(14f, 99f, 277f, 22f), "EQUIPO", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            centeredFixedScale(
                s,
                resources.corbelBold,
                TopRect(307f, 99f, 291f, 22f),
                if (treasure.isEmpty()) "EQUIPO" else "TESORO / MONEDAS",
                12.12f,
                SOURCE_CORBEL_HEADING_SCALE,
            )

            centeredFixedScale(s, resources.corbelBold, TopRect(14f, 489f, 584f, 22f), "EQUIPO ESPECIAL", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            tableLabel(s, 30f, 514f, 69f, "UBICACIÓN")
            tableLabel(s, 99f, 514f, 204f, "NOMBRE")
            tableLabel(s, 303f, 514f, 295f, "DESCRIPCIÓN / ESTADO")
            SPECIAL_LOCATION_LABELS_DISPLAY.forEachIndexed { row, label ->
                textAboveRule(s, resources.fira, Rule(34f, 95f, 548f + row * 17f), label, 7.4f, 6.6f, 2.2f)
            }
        }
        appendLayer(page, "$prefix - VALUES") { s ->
            ordinary.forEachIndexed { index, line ->
                // When treasure exists, preserve the right block for its own semantic domain.
                val block = index / INVENTORY_BLOCK_CAPACITY
                val withinBlock = index % INVENTORY_BLOCK_CAPACITY
                val column = withinBlock / INVENTORY_ROWS_PER_COLUMN
                val row = withinBlock % INVENTORY_ROWS_PER_COLUMN
                val blockX = if (block == 0) 14f else 307f
                val x1 = blockX + if (column == 0) 4f else 143f
                val x2 = blockX + if (column == 0) 135f else 273f
                if (line.contains("— Nota:") || line.contains("— Estado:") ||
                    line.startsWith("Nota:") || line.startsWith("Estado:")
                ) {
                    textAboveRule(s, resources.condensed, Rule(x1, x2, 139f + row * 17f), line, 8.0f, 6.6f, 2.3f)
                } else {
                    textAboveRuleScaled(
                        s,
                        resources.condensed,
                        Rule(x1, x2, 139f + row * 17f),
                        line,
                        preferredSize = 8.4f,
                        minimumSize = 7.0f,
                        clearance = 2.3f,
                        minimumHorizontalScale = 78f,
                    )
                }
            }

            treasure.forEachIndexed { index, value ->
                textAboveRule(
                    s,
                    resources.fira,
                    Rule(311f, 594f, 139f + index * 17f),
                    value,
                    8.0f,
                    6.8f,
                    2.3f,
                )
            }

            positionedSpecial.forEach { (row, item) ->
                val y = 548f + row * 17f
                if (specialLocationRow(item.location) != row) {
                    item.location?.trim()?.takeIf { it.isNotEmpty() }?.let {
                        textAboveRule(s, resources.fira, Rule(34f, 95f, y), it, 7.4f, 6.6f, 2.2f)
                    }
                }
                textAboveRule(s, resources.fira, Rule(103f, 297f, y), inventoryContinuationLabel(item), 8.8f, 7.2f, 2.3f)
                val detail = buildList {
                    item.weightLb?.let { add(formatInventoryWeight(it)) }
                    if (item.attuned) add("Sintonizado")
                    addAll(inventoryUsageLabels(usageByItem[item.id]))
                }.joinToString(" · ")
                if (detail.isNotEmpty()) {
                    textAboveRule(s, resources.fira, Rule(307f, 594f, y), detail, 8.5f, 7.0f, 2.3f)
                }
            }
        }
        appendLayer(page, "$prefix - MARKERS") { s ->
            positionedSpecial.forEach { (row, item) ->
                drawV2TrainingBox(
                    s,
                    TopRect(16f, 536f + row * 17f, 8.5f, 9f),
                    if (item.equipped || item.attuned) Training.PROFICIENT else Training.NONE,
                )
            }
        }
    }

    private fun inventoryContinuationLabel(item: CharacterInventoryItem): String = buildString {
        if (item.quantity > 1) append(item.quantity).append(" x ")
        append(item.name)
    }

    private fun inventoryBaseLabel(item: CharacterInventoryItem): String =
        item.pdfCompactEquipmentLabel()

    private fun inventoryDetailContinuationLines(
        item: CharacterInventoryItem,
    ): List<String> {
        val descriptiveDetail = buildList {
            item.description?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            item.notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        }.joinToString(" · ")
        if (descriptiveDetail.isEmpty()) return emptyList()

        return wrapByWidth(
            resources.condensed,
            inventoryContinuationLabel(item) + " — Detalle: " + descriptiveDetail,
            8.2f,
            V2_EQUIPMENT_COLUMN_WIDTH,
        )
    }

    private fun inventoryContinuationLines(
        item: CharacterInventoryItem,
        usage: CharacterInventoryUsage?,
    ): List<String> {
        val lines = mutableListOf<String>()
        // True Equipment overflow carries only compact identity; descriptive metadata is routed
        // to Notes instead of becoming apparent duplicate equipment rows. Long identity labels
        // may consume multiple physical continuation rows rather than dropping below the compact
        // readability target.
        lines += wrapByWidth(
            resources.condensed,
            item.pdfCompactEquipmentLabel(),
            7.0f,
            INVENTORY_COMPACT_IDENTITY_WRAP_WIDTH,
        )

        val operationalStatus = buildList {
            if (item.equipped) add("Equipado")
            addAll(inventoryUsageLabels(usage))
        }.joinToString(" · ")
        if (operationalStatus.isNotEmpty()) {
            lines += wrapByWidth(resources.condensed, "Estado: $operationalStatus", 8.2f, V2_EQUIPMENT_COLUMN_WIDTH)
        }

        return lines
    }

    private fun positionedSpecialItems(
        items: List<CharacterInventoryItem>,
        rowCount: Int,
    ): List<Pair<Int, CharacterInventoryItem>> {
        val available = (0 until rowCount).toMutableSet()
        val positioned = mutableListOf<Pair<Int, CharacterInventoryItem>>()
        items.take(rowCount).forEach { item ->
            val preferred = specialLocationRow(item.location)?.takeIf { it in available }
            val fallback = available.filter { it >= SPECIAL_LOCATION_LABELS.size }.minOrNull() ?: available.minOrNull()
            val row = preferred ?: fallback ?: return@forEach
            available.remove(row)
            positioned += row to item
        }
        return positioned.sortedBy { it.first }
    }

    private fun normalizedInventoryLocation(location: String?): String =
        location
            ?.lowercase()
            ?.replace('á', 'a')
            ?.replace('é', 'e')
            ?.replace('í', 'i')
            ?.replace('ó', 'o')
            ?.replace('ú', 'u')
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            .orEmpty()

    private fun specialLocationRow(location: String?): Int? =
        SPECIAL_LOCATION_LABELS.indexOf(normalizedInventoryLocation(location)).takeIf { it >= 0 }

    private fun specialLocationNeedsText(location: String?): Boolean {
        val normalized = normalizedInventoryLocation(location)
        return normalized.isNotEmpty() && normalized !in SPECIAL_LOCATION_LABELS
    }

    private fun usageMeaningful(usage: CharacterInventoryUsage?): Boolean =
        usage != null && (
            usage.kind != CharacterConsumableKind.NONE ||
                usage.quickUseAmount != 1 ||
                usage.carryState != CharacterInventoryCarryState.CARRIED
            )

    private fun inventoryUsageLabels(usage: CharacterInventoryUsage?): List<String> {
        if (!usageMeaningful(usage)) return emptyList()
        requireNotNull(usage)
        return buildList {
            when (usage.kind) {
                CharacterConsumableKind.NONE -> Unit
                CharacterConsumableKind.CONSUMABLE -> add("Consumible")
                CharacterConsumableKind.AMMUNITION -> add("Munición")
            }
            if (usage.quickUseAmount != 1) add("Uso rápido " + usage.quickUseAmount)
            if (usage.carryState == CharacterInventoryCarryState.STORED) add("Almacenado")
        }
    }

    private fun formatInventoryWeight(weightLb: Double): String =
        "Peso " + if (weightLb % 1.0 == 0.0) {
            weightLb.toInt().toString() + " lb"
        } else {
            weightLb.toString() + " lb"
        }

    private fun pageCount(size: Int, capacity: Int): Int =
        if (size <= 0) 0 else (size + capacity - 1) / capacity

    private fun appendSpellExtendedPages(plan: PcSheetPdfRenderPlan) {
        val spells = plan.snapshot.aggregate.sheet.spells
        require(spells.all { it.level in 0..9 }) {
            "Custom-v2 spell continuation supports spell levels 0 through 9."
        }
        val byLevel = spells
            .groupBy { it.level }
            .mapValues { (_, entries) ->
                entries.sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.name.lowercase() })
            }

        val overflowByLevel = SPELL_CONTINUATION_BLOCKS.associate { block ->
            block.level to byLevel[block.level].orEmpty().drop(block.maxRows)
        }
        val pages = SPELL_CONTINUATION_BLOCKS.maxOf { block ->
            pageCount(overflowByLevel[block.level].orEmpty().size, block.maxRows)
        }
        if (pages == 0) return

        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            val pageSpells = SPELL_CONTINUATION_BLOCKS.associate { block ->
                block.level to overflowByLevel[block.level].orEmpty()
                    .drop(pageIndex * block.maxRows)
                    .take(block.maxRows)
            }
            renderSpellContinuationPage(page, pageSpells, pageIndex)
        }
    }

    private fun renderSpellContinuationPage(
        page: PDPage,
        spellsByLevel: Map<Int, List<CharacterSpell>>,
        pageIndex: Int,
    ) {
        val layerPrefix = if (pageIndex == 0) "V2X SPELLS" else "V2X SPELLS ${pageIndex + 1}"
        appendLayer(page, "$layerPrefix - STRUCTURE") { s ->
            s.drawForm(resources.forms[3])
        }
        appendLayer(page, "$layerPrefix - CLEANUP") { s ->
            continuationSpellHeaderMasks().forEach { region ->
                fill(s, region.x, region.top, region.width, region.height, Color.WHITE)
            }
        }
        appendLayer(page, "$layerPrefix - LABELS") { }
        appendLayer(page, "$layerPrefix - VALUES") { s ->
            SPELL_CONTINUATION_BLOCKS.forEach { block ->
                spellsByLevel[block.level].orEmpty().forEachIndexed { row, spell ->
                    val ruleTop = block.firstRuleTop + row * 17f
                    textAboveRule(
                        s,
                        resources.fira,
                        Rule(block.textStartX, block.textEndX, ruleTop),
                        spell.name,
                        9.25f,
                        8.0f,
                        2.8f,
                    )
                }
            }
        }
        appendLayer(page, "$layerPrefix - MARKERS") { s ->
            SPELL_CONTINUATION_BLOCKS.forEach { block ->
                spellsByLevel[block.level].orEmpty().forEachIndexed { row, spell ->
                    if (spell.sourceAssociations.any { it.prepared }) {
                        glyphInRect(
                            s,
                            resources.symbol,
                            0xE211,
                            TopRect(block.markerX, block.firstRuleTop - 12f + row * 17f, 8.5f, 8.5f),
                            0.5f,
                            0.5f,
                        )
                    }
                }
            }
        }
    }

    private fun continuationSpellHeaderMasks(): List<TopRect> = listOf(
        TopRect(76f, 304f, 129f, 43f),
        TopRect(76f, 536f, 129f, 43f),
        TopRect(271f, 71f, 129f, 43f),
        TopRect(271f, 304f, 129f, 43f),
        TopRect(271f, 536f, 129f, 43f),
        TopRect(466f, 71f, 132f, 43f),
        TopRect(466f, 256f, 132f, 43f),
        TopRect(466f, 437f, 132f, 43f),
        TopRect(466f, 604f, 132f, 43f),
    )

    private fun appendNotesExtendedPages(plan: PcSheetPdfRenderPlan) {
        val packed = packedNotes(plan)
        if (packed.extendedPageCount == 0) return

        for (extendedPageIndex in 1..packed.extendedPageCount) {
            val columns = packed.columns
                .mapNotNull { segments ->
                    val address = segments.firstOrNull()?.address ?: return@mapNotNull null
                    if (address.extendedPageIndex != extendedPageIndex) {
                        null
                    } else {
                        address.columnIndex to segments.flatMap { it.lines }
                    }
                }
                .toMap()

            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderNotesContinuationPage(
                page = page,
                columns = columns,
                pageIndex = extendedPageIndex,
            )
        }
    }

    private fun packedNotes(plan: PcSheetPdfRenderPlan): PcSheetPackedNotes {
        val narrowestWidth = minOf(
            NOTES_LEFT_END_X - NOTES_LEFT_START_X,
            NOTES_RIGHT_END_X - NOTES_RIGHT_START_X,
        ) - NOTES_HORIZONTAL_PADDING
        val wrapped = plan.snapshot.aggregate.sheet.pcSheetNoteRecords().map { record ->
            PcSheetWrappedNoteRecord(
                record = record,
                headingLines = wrapByWidth(
                    resources.firaSemibold,
                    record.heading,
                    NOTES_HEADING_SIZE,
                    narrowestWidth,
                ).ifEmpty { listOf(record.heading) },
                bodyLines = wrapByWidth(
                    resources.fira,
                    record.body,
                    NOTES_BODY_SIZE,
                    narrowestWidth,
                ).ifEmpty { listOf(record.body) },
            )
        }
        return packPcSheetNoteColumns(
            records = wrapped,
            rowsPerColumn = NOTES_COLUMN_CAPACITY,
            columnsPerPage = 2,
        )
    }

    private fun renderNotesContinuationPage(
        page: PDPage,
        columns: Map<Int, List<PcSheetNotePhysicalLine>>,
        pageIndex: Int,
    ) {
        val layerPrefix = "V2X NOTES $pageIndex"
        appendLayer(page, "$layerPrefix - STRUCTURE") { s ->
            // Notes overflow is a whole-page exception: copy the complete native Notes page.
            s.drawForm(resources.forms[4])
        }
        appendLayer(page, "$layerPrefix - CLEANUP") { }
        appendLayer(page, "$layerPrefix - LABELS") { }
        appendLayer(page, "$layerPrefix - VALUES") { s ->
            drawNotesColumn(
                s = s,
                lines = columns[1].orEmpty(),
                startX = NOTES_LEFT_START_X,
                endX = NOTES_LEFT_END_X,
            )
            drawNotesColumn(
                s = s,
                lines = columns[2].orEmpty(),
                startX = NOTES_RIGHT_START_X,
                endX = NOTES_RIGHT_END_X,
            )
        }
        appendLayer(page, "$layerPrefix - MARKERS") { }
    }

    private fun drawNotesColumn(
        s: PDFormContentStream,
        lines: List<PcSheetNotePhysicalLine>,
        startX: Float,
        endX: Float,
    ) {
        require(lines.size <= NOTES_COLUMN_CAPACITY) {
            "Packed Custom-v2 Extended Notes column exceeds native row capacity."
        }
        lines.forEachIndexed { row, line ->
            if (line.kind == PcSheetNotePhysicalLineKind.SEPARATOR || line.text.isBlank()) {
                return@forEachIndexed
            }
            val (font, preferred, minimum) = when (line.kind) {
                PcSheetNotePhysicalLineKind.HEADING ->
                    Triple(resources.firaSemibold, NOTES_HEADING_SIZE, NOTES_HEADING_MINIMUM_SIZE)
                PcSheetNotePhysicalLineKind.BODY ->
                    Triple(resources.fira, NOTES_BODY_SIZE, NOTES_BODY_MINIMUM_SIZE)
                PcSheetNotePhysicalLineKind.CONTINUITY ->
                    Triple(resources.firaSemibold, NOTES_CONTINUITY_SIZE, NOTES_CONTINUITY_MINIMUM_SIZE)
                PcSheetNotePhysicalLineKind.SEPARATOR -> return@forEachIndexed
            }
            noteRuleText(
                s = s,
                font = font,
                rule = Rule(startX, endX, NOTES_FIRST_RULE_TOP + row * NOTES_ROW_STEP),
                value = line.text,
                preferredSize = preferred,
                minimumSize = minimum,
            )
        }
    }

    private fun noteRuleText(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        value: String,
        preferredSize: Float,
        minimumSize: Float,
    ) {
        val available = rule.endX - rule.startX - NOTES_HORIZONTAL_PADDING
        var size = preferredSize
        while (size > minimumSize && textWidth(font, value, size) > available) size -= 0.2f
        require(textWidth(font, value, size) <= available + 0.05f) {
            "Custom-v2 native Notes line does not fit without semantic truncation: '$value'"
        }
        val descent = requireNotNull(font.fontDescriptor).descent / 1000f * size
        val baseline = H - rule.topY + NOTES_BASELINE_CLEARANCE - descent
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, size)
        s.newLineAtOffset(rule.startX + 1.5f, baseline)
        s.showText(value)
        s.endText()
    }

    private fun wrapForRulesByChars(text: String, maxChars: Int): List<String> {
        val paragraphs = text
            .replace("\r\n", "\n")
            .split(Regex("\\n+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val result = mutableListOf<String>()
        paragraphs.forEach { paragraph ->
            var current = ""
            paragraph.split(Regex("\\s+")).forEach { word ->
                val candidate = if (current.isEmpty()) word else "$current $word"
                if (candidate.length <= maxChars || current.isEmpty()) {
                    current = candidate
                } else {
                    result += current
                    current = word
                }
            }
            if (current.isNotEmpty()) result += current
        }
        return result
    }

        private fun featureEntry(
        s: PDFormContentStream,
        x: Float,
        firstRuleTop: Float,
        width: Float,
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
    ): Float {
        var ruleTop = firstRuleTop
        fun nextRule(): Float = ruleTop.also { ruleTop += 17f }

        textAboveRule(
            s, resources.firaSemibold,
            Rule(x + 4f, x + width - 4f, nextRule()),
            trait.name, 9.0f, 7.4f, 2.7f,
        )

        val meta = listOf(
            traitTypeLabel(trait.type),
            trait.source.trim(),
            trait.activation?.let(::activationLabel).orEmpty(),
        ).filter { it.isNotEmpty() }.joinToString(" · ")
        if (meta.isNotEmpty()) {
            textAboveRule(
                s, resources.fira,
                Rule(x + 4f, x + width - 4f, nextRule()),
                meta, 7.3f, 6.2f, 2.5f,
            )
        }

        val uses = trait.pcSheetWritableUsesTrackerOrNull()?.let { tracker ->
            buildString {
                append("Usos: ").append(tracker.compactEditableLabel())
                trait.recovery?.takeIf { it.isNotBlank() }?.let { append(" · ").append(it) }
            }
        } ?: trait.recovery?.takeIf { it.isNotBlank() }

        uses?.let {
            textAboveRule(
                s, resources.fira,
                Rule(x + 4f, x + width - 4f, nextRule()),
                it, 7.3f, 6.2f, 2.5f,
            )
        }

        featureDescriptionLines(trait, width - 8f)
            .take(FEATURE_DESCRIPTION_LINES)
            .forEach { line ->
                textAboveRule(
                    s, resources.fira,
                    Rule(x + 4f, x + width - 4f, nextRule()),
                    line, 7.4f, 6.2f, 2.5f,
                )
            }
        return ruleTop
    }

    private fun featureDescriptionLines(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
        width: Float,
    ): List<String> {
        val description = listOf(
            trait.description.trim(),
            trait.notes.orEmpty().trim(),
        ).filter { it.isNotEmpty() }.joinToString(" · ")
        return if (description.isEmpty()) emptyList()
        else wrapByWidth(resources.fira, description, 7.4f, width)
    }

        private fun tableLabel(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        width: Float,
        label: String,
    ) {
        if (label.isNotBlank()) {
            centeredFixedScale(
                s, resources.corbel,
                TopRect(x, top, width, 18f), label, 7.79f, SOURCE_CORBEL_TABLE_SCALE,
            )
        }
    }

    private fun verticalRule(s: PDFormContentStream, x: Float, top: Float, bottomTop: Float, width: Float) {
        s.saveGraphicsState()
        s.setLineWidth(width)
        s.moveTo(x, H - top)
        s.lineTo(x, H - bottomTop)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun combatCellText(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        value: String,
        preferredSize: Float,
        leftPadding: Float = 2f,
    ) {
        if (value.isBlank()) return
        val available = rule.endX - rule.startX - leftPadding - 1f
        var size = preferredSize
        while (size > COMBAT_MINIMUM_BODY_SIZE && textWidth(font, value, size) > available) {
            size -= 0.2f
        }
        val rawWidth = textWidth(font, value, size)
        val horizontalScale = if (rawWidth <= available) {
            100f
        } else {
            (available / rawWidth * 100f).coerceAtMost(100f)
        }
        require(horizontalScale >= COMBAT_MINIMUM_HORIZONTAL_SCALE) {
            "Custom-v2 combat cell requires excessive compression: '$value' ($horizontalScale%)"
        }
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + 2.3f - descent
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(horizontalScale)
        s.newLineAtOffset(rule.startX + leftPadding, baseline)
        s.showText(value)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun textAboveRuleScaled(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        preferredSize: Float,
        minimumSize: Float,
        clearance: Float,
        minimumHorizontalScale: Float,
    ) {
        var size = preferredSize
        val available = rule.endX - rule.startX - 2f
        while (size > minimumSize && textWidth(font, text, size) > available / (minimumHorizontalScale / 100f)) {
            size -= 0.25f
        }
        val rawWidth = textWidth(font, text, size)
        val scale = minOf(100f, available / rawWidth * 100f)
        require(scale >= minimumHorizontalScale - COMPACT_LABEL_MICRO_FIT_DELTA) {
            "Compact v2 label requires excessive compression: $text ($scale%)"
        }
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(scale)
        s.newLineAtOffset(rule.startX + 1f, baseline)
        s.showText(text)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun drawSquareCounter(
        s: PDFormContentStream,
        startX: Float,
        centerTop: Float,
        current: Int,
        maximum: Int,
    ) {
        require(maximum in 1..9)
        require(current in 0..maximum)
        repeat(maximum) { index ->
            val cp = if (index < current) 0xE304 else 0xE303
            glyphInRect(s, resources.symbol, cp, TopRect(startX + index * 13f, centerTop - 5f, 10f, 10f), 0.7f, 0.7f)
        }
    }

    private fun traitTypeLabel(type: CharacterTraitType): String = when (type) {
        CharacterTraitType.CLASS -> "Clase"
        CharacterTraitType.SPECIES_RACE -> "Raza"
        CharacterTraitType.BACKGROUND -> "Trasfondo"
        CharacterTraitType.FEAT -> "Dote"
        CharacterTraitType.GIFT_BLESSING -> "Don/Bendición"
        CharacterTraitType.OTHER -> "Otro"
    }

    private fun activationLabel(type: CharacterActivationType): String = when (type) {
        CharacterActivationType.PASSIVE -> "Pasivo"
        CharacterActivationType.ACTION -> "Acción"
        CharacterActivationType.BONUS_ACTION -> "Acción adicional"
        CharacterActivationType.REACTION -> "Reacción"
        CharacterActivationType.OTHER -> "Otro"
    }

    private fun optionKindLabel(kind: CharacterClassOptionKind): String = when (kind) {
        CharacterClassOptionKind.ARTIFICER_PLAN -> "Plan"
        CharacterClassOptionKind.ARTIFICER_DEVICE -> "Dispositivo"
        CharacterClassOptionKind.SUBCLASS_STATE -> "Subclase"
        CharacterClassOptionKind.TECHNIQUE -> "Técnica"
        CharacterClassOptionKind.METAMAGIC -> "Metamagia"
        CharacterClassOptionKind.INVOCATION -> "Invocación"
        CharacterClassOptionKind.PACT_CHOICE -> "Pacto"
        CharacterClassOptionKind.OTHER -> "Otro"
    }

    private fun recoveryLabel(cadence: CharacterRecoveryCadence): String = when (cadence) {
        CharacterRecoveryCadence.NONE -> ""
        CharacterRecoveryCadence.SHORT_REST -> "Descanso corto"
        CharacterRecoveryCadence.LONG_REST -> "Descanso largo"
        CharacterRecoveryCadence.SHORT_OR_LONG_REST -> "Descanso corto/largo"
        CharacterRecoveryCadence.MANUAL -> "Manual"
    }

    private fun appendLayer(page: PDPage, name: String, draw: (PDFormContentStream) -> Unit) {
        val form = PDFormXObject(document).apply {
            resources = PDResources()
            setBBox(PDRectangle(page.cropBox.width, page.cropBox.height))
        }
        PDFormContentStream(form).use { stream ->
            stream.setNonStrokingColor(Color.BLACK)
            stream.setStrokingColor(Color.BLACK)
            draw(stream)
        }
        layers.appendFormAsLayer(page, form, AffineTransform(), name)
    }

    private fun pageHeaderStructure(s: PDFormContentStream) {
        // Draw the exact imported source logo image directly. A clipped whole-page form looks
        // correct but still exposes hidden source-template text to PDF extraction/copy/search.
        s.drawImage(
            resources.logo,
            V2_LOGO_X,
            H - V2_LOGO_TOP - V2_LOGO_HEIGHT,
            V2_LOGO_WIDTH,
            V2_LOGO_HEIGHT,
        )
        drawRule(s, 126f, 598f, 79f, 0.6f)
    }

    private fun pageTitle(s: PDFormContentStream, title: String) {
        centeredFixedScale(
            s, resources.corbelBold,
            TopRect(126f, 28f, 472f, 34f), title, 12.12f, SOURCE_CORBEL_HEADING_SCALE,
        )
    }

    private fun attributeBandStructure(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        width: Float,
        rows: Int,
    ) {
        drawAttributeOrnament(s, x + 0.3f, top + 34f)
        drawRule(s, x + 94f, x + width - 9f, top + 47f, 0.65f)
        repeat(rows) { row -> drawRule(s, x + 94f, x + width - 9f, top + 64f + row * 17f, 0.55f) }
    }

    private fun drawAttributeBandValues(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        values: AttributeValues,
    ) {
        centered(s, resources.firaSemibold, TopRect(x + 17.8f, top + 42f, 25.5f, 18f), values.score, 17f)
        centered(s, resources.firaSemibold, TopRect(x + 48.3f, top + 61f, 23f, 16.5f), values.modifier, 15.5f)
        if (values.save.isNotEmpty()) {
            centeredAboveRule(s, resources.firaSemibold, Rule(x + 145f, x + 174f, top + 47f), values.save, 8.8f, 2.0f)
        }
        values.skills.forEachIndexed { row, item ->
            val y = top + 64f + row * 17f
            textAboveRuleSource(
                s, resources.corbel, resources.fira,
                Rule(x + 94f, x + 145f, y), item.first, 7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE,
            )
            if (item.second.isNotEmpty()) {
                centeredAboveRule(s, resources.firaSemibold, Rule(x + 145f, x + 174f, y), item.second, 8.8f, 2.2f)
            }
        }
    }

    private fun drawAttributeOrnament(s: PDFormContentStream, targetX: Float, targetTop: Float) {
        s.drawImage(
            resources.attributeOrnament,
            targetX,
            H - targetTop - ATTRIBUTE_ORNAMENT_HEIGHT,
            ATTRIBUTE_ORNAMENT_WIDTH,
            ATTRIBUTE_ORNAMENT_HEIGHT,
        )
    }

    private fun drawV2TrainingBox(s: PDFormContentStream, rect: TopRect, training: Training) {
        val cp = when (training) {
            Training.NONE -> 0xE303
            Training.PROFICIENT -> 0xE313
            Training.EXPERTISE -> 0xE314
        }
        glyphInRect(s, resources.symbol, cp, rect, 0.7f, 0.7f)
    }

    private fun training(value: SkillTraining): Training = when (value) {
        SkillTraining.NONE -> Training.NONE
        SkillTraining.PROFICIENT -> Training.PROFICIENT
        SkillTraining.EXPERTISE -> Training.EXPERTISE
    }

    private fun bandedRows(
        s: PDFormContentStream,
        x1: Float,
        x2: Float,
        firstRuleTop: Float,
        rows: Int,
        step: Float,
        phase: Int,
    ) {
        repeat(rows) { row ->
            val ruleTop = firstRuleTop + row * step
            val tone = if ((row + phase) % 2 == 0) SOURCE_GRAY_DARK else SOURCE_GRAY_LIGHT
            fill(s, x1, ruleTop - step + 0.5f, x2 - x1, step - 0.5f, tone)
            drawRule(s, x1, x2, ruleTop, 0.55f)
        }
    }

    private fun drawSourceCrop(
        s: PDFormContentStream,
        form: PDFormXObject,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
    ) {
        s.saveGraphicsState()
        s.addRect(x, H - top - height, width, height)
        s.clip()
        s.drawForm(form)
        s.restoreGraphicsState()
    }

    private fun fill(s: PDFormContentStream, x: Float, top: Float, width: Float, height: Float, color: Color) {
        s.saveGraphicsState()
        s.setNonStrokingColor(color)
        s.addRect(x, H - top - height, width, height)
        s.fill()
        s.restoreGraphicsState()
    }

    private fun drawRule(s: PDFormContentStream, x1: Float, x2: Float, top: Float, width: Float) {
        s.saveGraphicsState()
        s.setLineWidth(width)
        s.moveTo(x1, H - top)
        s.lineTo(x2, H - top)
        s.stroke()
        s.restoreGraphicsState()
    }

        private fun textTopFixedScale(
        s: PDFormContentStream,
        font: PDFont,
        x: Float,
        top: Float,
        text: String,
        size: Float,
        horizontalScale: Float,
    ) {
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(horizontalScale)
        s.newLineAtOffset(x, H - top - ascent)
        s.showText(text)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun textTopSource(
        s: PDFormContentStream,
        sourceFont: PDFont,
        fallbackFont: PDFont,
        x: Float,
        top: Float,
        text: String,
        size: Float,
        horizontalScale: Float,
    ) {
        val font = sourceFont.takeIf { supports(it, text) } ?: fallbackFont
        val scale = if (font === sourceFont) horizontalScale else 100f
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(scale)
        s.newLineAtOffset(x, H - top - ascent)
        s.showText(text)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun textAboveRuleSource(
        s: PDFormContentStream,
        sourceFont: PDFont,
        fallbackFont: PDFont,
        rule: Rule,
        text: String,
        size: Float,
        clearance: Float,
        horizontalScale: Float,
    ) {
        val font = sourceFont.takeIf { supports(it, text) } ?: fallbackFont
        if (font === sourceFont) {
            textAboveRuleFixedScale(s, font, rule, text, size, clearance, horizontalScale)
        } else {
            textAboveRule(s, font, rule, text, size, 6.2f, clearance)
        }
    }

    private fun centeredSource(
        s: PDFormContentStream,
        sourceFont: PDFont,
        fallbackFont: PDFont,
        rect: TopRect,
        text: String,
        size: Float,
        horizontalScale: Float,
    ) {
        val font = sourceFont.takeIf { supports(it, text) } ?: fallbackFont
        if (font === sourceFont) {
            centeredFixedScale(s, font, rect, text, size, horizontalScale)
        } else {
            centered(s, font, rect, text, size)
        }
    }

    private fun supports(font: PDFont, text: String): Boolean =
        runCatching {
            font.encode(text)
            true
        }.getOrDefault(false)

    private fun textAboveRuleFixedScale(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        size: Float,
        clearance: Float,
        horizontalScale: Float,
    ) {
        val available = rule.endX - rule.startX - 2f
        val rawWidth = textWidth(font, text, size)
        val nominalWidth = rawWidth * horizontalScale / 100f
        val effectiveScale = if (nominalWidth <= available + 0.05f || rawWidth <= 0f) {
            horizontalScale
        } else {
            (available / rawWidth * 100f).coerceAtMost(horizontalScale)
        }
        require(effectiveScale >= horizontalScale - SOURCE_MATCHED_MICRO_FIT_DELTA) {
            "Source-matched text does not fit: $text ($nominalWidth > $available; requiredScale=$effectiveScale)"
        }
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(effectiveScale)
        s.newLineAtOffset(rule.startX + 1f, baseline)
        s.showText(text)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun centeredFixedScale(
        s: PDFormContentStream,
        font: PDFont,
        rect: TopRect,
        text: String,
        size: Float,
        horizontalScale: Float,
    ) {
        val width = textWidth(font, text, size) * horizontalScale / 100f
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        val descent = abs(font.fontDescriptor?.descent?.takeIf { it < 0 } ?: -250f) / 1000f * size
        val bottom = H - (rect.top + rect.height)
        val baseline = bottom + (rect.height - ascent - descent) / 2f + descent
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(horizontalScale)
        s.newLineAtOffset(rect.x + (rect.width - width) / 2f, baseline)
        s.showText(text)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun textAboveRule(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        preferredSize: Float,
        minimumSize: Float,
        clearance: Float,
    ) {
        var size = preferredSize
        val available = rule.endX - rule.startX - 2f
        while (size > minimumSize && textWidth(font, text, size) > available) size -= 0.25f
        require(textWidth(font, text, size) <= available + 0.05f) { "Text does not fit: $text" }
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rule.startX + 1f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun centeredAboveRule(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        size: Float,
        clearance: Float,
    ) {
        val width = textWidth(font, text, size)
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rule.startX + (rule.endX - rule.startX - width) / 2f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun centered(
        s: PDFormContentStream,
        font: PDFont,
        rect: TopRect,
        text: String,
        size: Float,
    ) {
        val width = textWidth(font, text, size)
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        val descent = abs(font.fontDescriptor?.descent?.takeIf { it < 0 } ?: -250f) / 1000f * size
        val bottom = H - (rect.top + rect.height)
        val baseline = bottom + (rect.height - ascent - descent) / 2f + descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rect.x + (rect.width - width) / 2f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun glyphInRect(
        s: PDFormContentStream,
        font: PDFont,
        cp: Int,
        rect: TopRect,
        insetX: Float,
        insetY: Float,
    ) {
        val glyph = String(Character.toChars(cp))
        val normalizedWidth = font.getStringWidth(glyph) / 1000f
        val descriptor = requireNotNull(font.fontDescriptor)
        val normalizedAscent = descriptor.ascent / 1000f
        val normalizedDescent = descriptor.descent / 1000f
        val normalizedHeight = normalizedAscent - normalizedDescent
        val targetWidth = rect.width - insetX * 2f
        val targetHeight = rect.height - insetY * 2f
        val scaleX = targetWidth / normalizedWidth
        val scaleY = targetHeight / normalizedHeight
        val left = rect.x + insetX
        val targetBottom = H - (rect.top + rect.height) + insetY
        val baseline = targetBottom - normalizedDescent * scaleY
        s.beginText()
        s.setFont(font, 1f)
        s.setTextMatrix(Matrix(scaleX, 0f, 0f, scaleY, left, baseline))
        s.showText(glyph)
        s.endText()
    }

    private fun wrapByWidth(font: PDFont, text: String, size: Float, maxWidth: Float): List<String> {
        val out = mutableListOf<String>()
        var current = ""

        fun splitOnlyWhenTokenCannotFit(word: String): List<String> {
            if (textWidth(font, word, size) <= maxWidth) return listOf(word)
            val pieces = mutableListOf<String>()
            var piece = ""
            word.forEach { char ->
                val candidate = piece + char
                if (piece.isNotEmpty() && textWidth(font, candidate, size) > maxWidth) {
                    pieces += piece
                    piece = char.toString()
                } else {
                    piece = candidate
                }
            }
            if (piece.isNotEmpty()) pieces += piece
            return pieces
        }

        text.trim().split(Regex("\\s+")).forEach { word ->
            splitOnlyWhenTokenCannotFit(word).forEach { piece ->
                val candidate = if (current.isBlank()) piece else "$current $piece"
                if (textWidth(font, candidate, size) <= maxWidth) {
                    current = candidate
                } else {
                    if (current.isNotBlank()) out += current
                    current = piece
                }
            }
        }
        if (current.isNotBlank()) out += current
        return out
    }

    private fun textWidth(font: PDFont, text: String, size: Float): Float =
        font.getStringWidth(text) / 1000f * size

    private fun builtInKeyedName(ability: CharacterAbility): String = when (ability) {
        CharacterAbility.STRENGTH -> "FUErza"
        CharacterAbility.DEXTERITY -> "DEStreza"
        CharacterAbility.CONSTITUTION -> "CONstitución"
        CharacterAbility.INTELLIGENCE -> "INTeligencia"
        CharacterAbility.WISDOM -> "SABiduría"
        CharacterAbility.CHARISMA -> "CARisma"
    }

    private fun abilityKey(
        reference: CharacterAbilityReference,
        attributes: List<PcSheetCustomAttributeProjection>,
    ): String = when {
        reference.builtIn != null -> when (val builtIn = reference.builtIn) {
            CharacterAbility.STRENGTH -> "FUE"
            CharacterAbility.DEXTERITY -> "DES"
            CharacterAbility.CONSTITUTION -> "CON"
            CharacterAbility.INTELLIGENCE -> "INT"
            CharacterAbility.WISDOM -> "SAB"
            CharacterAbility.CHARISMA -> "CAR"
            null -> error("Built-in ability branch lost its value.")
        }
        reference.customAttributeId != null -> attributes
            .firstOrNull { it.attribute.id == reference.customAttributeId }
            ?.attribute
            ?.abbreviation
            ?.trim()
            ?.uppercase()
            ?.take(3)
            .orEmpty()
        else -> ""
    }

    private fun signed(value: Int): String = if (value >= 0) "+$value" else value.toString()

    private data class Rule(val startX: Float, val endX: Float, val topY: Float)
    private data class TopRect(val x: Float, val top: Float, val width: Float, val height: Float)
    private data class AttributeValues(
        val score: String,
        val modifier: String,
        val save: String,
        val skills: List<Pair<String, String>>,
    )

    private data class AttributeColumnSlice(
        val projection: PcSheetCustomAttributeProjection,
        val skills: List<PcSheetCustomSkillProjection>,
        val noteLines: List<String>,
    )

    private data class StandardSkillSlice(
        val skills: List<PcSheetCustomSkillProjection>,
    )

    private data class PerAttributePageRow(
        val attribute: AttributeColumnSlice? = null,
        val standardSkills: List<PcSheetCustomSkillProjection> = emptyList(),
        val continuation: Boolean = false,
    ) {
        init {
            require((attribute != null) xor standardSkills.isNotEmpty()) {
                "Per-Attribute native row must represent exactly one semantic row kind."
            }
        }
    }

    private data class ResourceRenderLine(
        val name: String,
        val currentValue: Int?,
        val maximum: Int?,
        val oneUse: Boolean,
        val recovery: String,
        val detail: String,
    )

    private data class OptionRenderLine(
        val kind: String,
        val name: String,
        val detail: String,
        val active: Boolean,
    )

    private data class CombatReferenceRow(
        val name: String,
        val range: String,
        val bonus: String,
        val effect: String,
        val notes: String,
    )

    private data class CombatLayoutRow(
        val row: CombatReferenceRow,
        val nameLines: List<String>,
        val rangeLines: List<String>,
        val bonusLines: List<String>,
        val effectLines: List<String>,
        val noteLines: List<String>,
        val height: Float,
    )

    private data class ResourceRenderRow(
        val name: String,
        val currentValue: Int,
        val maximum: Int?,
        val valueKind: CharacterTrackableValueKind,
        val recovery: String,
        val detail: String,
        val sortOrder: Int,
        val sourceRank: Int,
    )

    private data class SpellContinuationBlock(
        val level: Int,
        val textStartX: Float,
        val textEndX: Float,
        val markerX: Float,
        val firstRuleTop: Float,
        val maxRows: Int,
    )

    private enum class Training { NONE, PROFICIENT, EXPERTISE }

    private data class Resources(
        val forms: List<PDFormXObject>,
        val logo: PDImageXObject,
        val attributeOrnament: PDImageXObject,
        val corbel: PDFont,
        val corbelBold: PDFont,
        val fira: PDFont,
        val firaSemibold: PDFont,
        val condensed: PDFont,
        val symbol: PDFont,
    ) {
        companion object {
            fun load(
                doc: PDDocument,
                source: PDDocument,
                resourceLoader: (String) -> InputStream?,
            ): Resources {
                val utility = LayerUtility(doc)
                val forms = (0 until 5).map { utility.importPageAsForm(source, it) }
                val corbelRegular = findImportedFont(forms) { name ->
                    name.contains("Corbel", ignoreCase = true) && !name.contains("Bold", ignoreCase = true)
                }
                val corbelBold = findImportedFont(forms) { name ->
                    name.contains("Corbel", ignoreCase = true) && name.contains("Bold", ignoreCase = true)
                }
                return Resources(
                    forms = forms,
                    logo = findImportedImage(forms) { image ->
                        image.width == V2_LOGO_SOURCE_WIDTH && image.height == V2_LOGO_SOURCE_HEIGHT
                    },
                    attributeOrnament = buildTransparentAttributeOrnament(doc, source),
                    corbel = corbelRegular,
                    corbelBold = corbelBold,
                    fira = resourceFont(doc, resourceLoader, FIRA_REGULAR),
                    firaSemibold = resourceFont(doc, resourceLoader, FIRA_SEMIBOLD),
                    condensed = resourceFont(doc, resourceLoader, BARLOW_CONDENSED),
                    symbol = resourceFont(doc, resourceLoader, SYMBOL_V8),
                )
            }

            private fun findImportedFont(
                forms: List<PDFormXObject>,
                predicate: (String) -> Boolean,
            ): PDFont {
                val visited = mutableSetOf<Int>()

                fun scan(resources: PDResources?): PDFont? {
                    if (resources == null) return null
                    val identity = System.identityHashCode(resources.cosObject)
                    if (!visited.add(identity)) return null

                    resources.fontNames.forEach { key ->
                        val font = resources.getFont(key)
                        if (predicate(font.name)) return font
                    }
                    resources.xObjectNames.forEach { key ->
                        val child = resources.getXObject(key)
                        if (child is PDFormXObject) {
                            scan(child.resources)?.let { return it }
                        }
                    }
                    return null
                }

                forms.forEach { form ->
                    scan(form.resources)?.let { return it }
                }
                error("Requested imported source font not found.")
            }

            private fun findImportedImage(
                forms: List<PDFormXObject>,
                predicate: (PDImageXObject) -> Boolean,
            ): PDImageXObject {
                val visited = mutableSetOf<Int>()

                fun scan(resources: PDResources?): PDImageXObject? {
                    if (resources == null) return null
                    if (!visited.add(System.identityHashCode(resources.cosObject))) return null
                    resources.xObjectNames.forEach { key ->
                        when (val child = resources.getXObject(key)) {
                            is PDImageXObject -> if (predicate(child)) return child
                            is PDFormXObject -> scan(child.resources)?.let { return it }
                        }
                    }
                    return null
                }

                forms.forEach { form -> scan(form.resources)?.let { return it } }
                error("Requested imported Custom-v2 source image not found.")
            }

            private fun buildTransparentAttributeOrnament(
                doc: PDDocument,
                source: PDDocument,
            ): PDImageXObject {
                val dpi = 288f
                val scale = dpi / 72f
                val sourceImage = PDFRenderer(source).renderImageWithDPI(1, dpi, ImageType.RGB)
                val x0 = (ATTRIBUTE_ORNAMENT_SOURCE_X * scale).roundToInt()
                val y0 = (ATTRIBUTE_ORNAMENT_SOURCE_TOP * scale).roundToInt()
                val width = (ATTRIBUTE_ORNAMENT_WIDTH * scale).roundToInt()
                val height = (ATTRIBUTE_ORNAMENT_HEIGHT * scale).roundToInt()
                val transparent = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

                fun insideValueMask(px: Int, py: Int): Boolean {
                    val x = px / scale
                    val y = py / scale
                    val score = x in 17.5f..43.0f && y in 8.0f..26.0f
                    val modifier = x in 48.0f..71.0f && y in 27.0f..43.5f
                    return score || modifier
                }

                for (y in 0 until height) {
                    for (x in 0 until width) {
                        if (insideValueMask(x, y)) {
                            transparent.setRGB(x, y, 0)
                            continue
                        }
                        val rgb = sourceImage.getRGB(x0 + x, y0 + y)
                        val red = rgb shr 16 and 0xFF
                        val green = rgb shr 8 and 0xFF
                        val blue = rgb and 0xFF
                        val luma = (red * 299 + green * 587 + blue * 114) / 1000
                        val alpha = when {
                            luma >= 190 -> 0
                            luma <= 120 -> 255
                            else -> ((190 - luma) * 255 / 70).coerceIn(0, 255)
                        }
                        transparent.setRGB(x, y, alpha shl 24)
                    }
                }
                return LosslessFactory.createFromImage(doc, transparent)
            }

            private fun resourceFont(
                doc: PDDocument,
                resourceLoader: (String) -> InputStream?,
                path: String,
            ): PDFont = requireNotNull(resourceLoader(path)) {
                "Missing renderer resource: $path"
            }.use { PDType0Font.load(doc, it, false) }
        }
    }

    private companion object {
        const val W = 612f
        const val H = 792f
        const val FIRA_REGULAR = "fonts/pdf/text/FiraSans-Regular.ttf"
        const val FIRA_SEMIBOLD = "fonts/pdf/text/FiraSans-SemiBold.ttf"
        const val BARLOW_CONDENSED = "fonts/pdf/text/BarlowCondensed-Bold.ttf"
        const val SYMBOL_V8 = "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf"
        const val V2_LOGO_SOURCE_WIDTH = 453
        const val V2_LOGO_SOURCE_HEIGHT = 171
        const val V2_LOGO_X = 14.03f
        const val V2_LOGO_TOP = 28.77f
        const val V2_LOGO_WIDTH = 108.68f
        const val V2_LOGO_HEIGHT = 40.89f
        const val ATTRIBUTE_ORNAMENT_SOURCE_X = 14.32f
        const val ATTRIBUTE_ORNAMENT_SOURCE_TOP = 164.68f
        const val ATTRIBUTE_ORNAMENT_WIDTH = 80.40f
        const val ATTRIBUTE_ORNAMENT_HEIGHT = 47.76f
        const val SOURCE_CORBEL_ATTRIBUTE_SCALE = 79f
        const val SOURCE_CORBEL_COMPACT_SCALE = 78f
        const val SOURCE_CORBEL_HEADING_SCALE = 81f
        const val SOURCE_CORBEL_TABLE_SCALE = 86f
        const val SOURCE_MATCHED_MICRO_FIT_DELTA = 2f
        const val COMPACT_LABEL_MICRO_FIT_DELTA = 2f
        const val BASE_V2_TRAIT_CAPACITY = 18
        const val ATTRIBUTE_COLUMNS_PER_PAGE = 3
        const val ATTRIBUTE_LINKED_SKILLS_PER_COLUMN = 6
        const val ATTRIBUTE_NOTE_LINES_PER_COLUMN = 10
        const val STANDARD_COLUMNS_PER_PAGE = 3
        const val STANDARD_SKILLS_PER_COLUMN = 4
        const val PER_ATTRIBUTE_ROWS_PER_PAGE = 6
        const val PER_ATTRIBUTE_FIRST_ROW_TOP = 104f
        const val PER_ATTRIBUTE_ROW_HEIGHT = 96f
        const val PER_ATTRIBUTE_ROW_STEP = 19.84f
        const val PER_ATTRIBUTE_LINKED_SKILLS_PER_ROW = 3
        const val PER_ATTRIBUTE_NOTE_LINES_PER_ROW = 3
        const val PER_ATTRIBUTE_STANDARD_SKILLS_PER_ROW = 3
        const val PER_ATTRIBUTE_NOTE_TEXT_WIDTH = 180f
        const val ABILITY_ATTRIBUTES_PER_PAGE = 6
        const val ABILITY_SAVES_PER_PAGE = 30
        const val ABILITY_SKILLS_PER_PAGE = 34
        const val FEATURE_DESCRIPTION_LINES = 3
        const val TRAIT_NAME_INDEX_PER_PAGE = 10
        const val TRAIT_DETAIL_LINES_PER_PAGE = 18
        const val TRAIT_CONTINUATION_TEXT_WIDTH = 281f
        const val TRAIT_PROFICIENCIES_PER_PAGE = 8
        const val BASE_V2_COMBAT_CAPACITY = 8
        const val COMBAT_MINIMUM_BODY_SIZE = 6.0f
        const val COMBAT_MINIMUM_HORIZONTAL_SCALE = 72f
        const val COMBAT_HEADER_TOP = 96f
        const val COMBAT_HEADER_HEIGHT = 30f
        const val COMBAT_HEADER_LABEL_TOP = 113f
        const val COMBAT_FIRST_LOGICAL_ROW_TOP = 134f
        const val COMBAT_PAGE_BOTTOM = 758f
        const val COMBAT_AVAILABLE_DATA_HEIGHT = COMBAT_PAGE_BOTTOM - COMBAT_FIRST_LOGICAL_ROW_TOP
        const val COMBAT_MIN_LOGICAL_ROW_HEIGHT = 19.84f
        const val COMBAT_CELL_LINE_HEIGHT = 12f
        const val COMBAT_CELL_VERTICAL_PADDING = 4f
        const val COMBAT_TEXT_WIDTH = 576f
        const val BASE_V2_EQUIPMENT_CAPACITY = 46
        const val V2_EQUIPMENT_COLUMN_WIDTH = 125f
        // Conservative raw-width ceiling at the 7 pt / 78% compact target for the narrowest
        // continuation equipment rule (~128 pt usable width).
        const val INVENTORY_COMPACT_IDENTITY_WRAP_WIDTH = 164f
        const val V2_BASE_EQUIPMENT_TEXT_WIDTH = 132f
        const val V2_BASE_SPECIAL_LOCATION_WIDTH = 79f
        const val V2_BASE_SPECIAL_NAME_WIDTH = 196f
        const val V2_BASE_SPECIAL_DETAIL_WIDTH = 291f
        const val BASE_V2_SPECIAL_CAPACITY = 14
        const val INVENTORY_ROWS_PER_COLUMN = 19
        const val INVENTORY_BLOCK_CAPACITY = 38
        const val INVENTORY_CONTINUATION_CAPACITY = 76
        const val INVENTORY_EQUIPMENT_WITH_TREASURE_CAPACITY = INVENTORY_BLOCK_CAPACITY
        const val INVENTORY_TREASURE_CAPACITY = INVENTORY_ROWS_PER_COLUMN
        const val BASE_V2_OTHER_CURRENCY_CAPACITY = 4
        const val V2_TREASURE_COLUMN_WIDTH = 283f
        const val INVENTORY_SPECIAL_CAPACITY = 12
        val SPECIAL_LOCATION_LABELS = listOf(
            "cabeza", "rostro", "cuello", "mano izquierda", "mano derecha",
            "brazo izquierdo", "brazo derecho", "pecho", "piernas", "pies",
        )
        val SPECIAL_LOCATION_LABELS_DISPLAY = listOf(
            "Cabeza", "Rostro", "Cuello", "Mano izquierda", "Mano derecha",
            "Brazo izquierdo", "Brazo derecho", "Pecho", "Piernas", "Pies",
        )
        const val RESOURCE_ROWS_PER_PAGE = 10
        const val RESOURCE_OPTIONS_PER_PAGE = 18
        const val NOTES_HEADING_SIZE = 8.8f
        const val NOTES_HEADING_MINIMUM_SIZE = 6.4f
        const val NOTES_BODY_SIZE = 8.4f
        const val NOTES_BODY_MINIMUM_SIZE = 6.2f
        const val NOTES_CONTINUITY_SIZE = 7.2f
        const val NOTES_CONTINUITY_MINIMUM_SIZE = 5.8f
        const val NOTES_HORIZONTAL_PADDING = 4f
        const val NOTES_BASELINE_CLEARANCE = 2.8f
        const val NOTES_LEFT_START_X = 14f
        const val NOTES_LEFT_END_X = 302.5f
        const val NOTES_RIGHT_START_X = 309f
        const val NOTES_RIGHT_END_X = 597.5f
        const val NOTES_FIRST_RULE_TOP = 104f
        const val NOTES_ROW_STEP = 17f
        const val BASE_V2_NOTES_CAPACITY = 40
        const val NOTES_COLUMN_CAPACITY = 20
        const val NOTES_CONTINUATION_CAPACITY = 40

        val SPELL_CONTINUATION_BLOCKS = listOf(
            SpellContinuationBlock(0, 25.5f, 203.5f, 14f, 127.21f, 8),
            SpellContinuationBlock(1, 25.5f, 203.5f, 14f, 358.65f, 10),
            SpellContinuationBlock(2, 25.5f, 203.5f, 14f, 591.09f, 9),
            SpellContinuationBlock(3, 221f, 399f, 209.5f, 126.21f, 10),
            SpellContinuationBlock(4, 221f, 399f, 209.5f, 358.65f, 10),
            SpellContinuationBlock(5, 221f, 399f, 209.5f, 591.09f, 8),
            SpellContinuationBlock(6, 416.5f, 594.5f, 405f, 126.21f, 8),
            SpellContinuationBlock(7, 416.5f, 594.5f, 405f, 310.46f, 6),
            SpellContinuationBlock(8, 416.5f, 594.5f, 405f, 491.88f, 6),
            SpellContinuationBlock(9, 416.5f, 594.5f, 405f, 659.12f, 5),
        )

        val SOURCE_GRAY_DARK: Color = Color(200, 199, 199)
        val SOURCE_GRAY_LIGHT: Color = Color(227, 227, 227)
    }
}
