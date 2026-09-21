package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOptionKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryCadence
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomAttributeProjection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomSkillProjection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedPageKind
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
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

            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            when (plan.request.visualFamily) {
                PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE ->
                    renderPerAttribute(page, stats.attributes, stats.skills)
                PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY ->
                    renderPerAbility(page, stats.attributes, stats.skills)
                else -> error("Unreachable Custom-v2 family branch.")
            }
        }

        if (needsTraitsExtendedPage(plan)) {
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderTraits(page, plan)
        }

        if (needsResourcesExtendedPage(plan)) {
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderResources(page, plan)
        }
    }

    private fun needsTraitsExtendedPage(plan: PcSheetPdfRenderPlan): Boolean {
        val sheet = plan.snapshot.aggregate.sheet
        return sheet.traits.size > BASE_V2_TRAIT_CAPACITY ||
            sheet.traits.any { trait ->
                trait.maxUses != null ||
                    !trait.recovery.isNullOrBlank() ||
                    !trait.notes.isNullOrBlank()
            } ||
            sheet.proficiencies.isNotEmpty()
    }

    private fun needsResourcesExtendedPage(plan: PcSheetPdfRenderPlan): Boolean {
        val sheet = plan.snapshot.aggregate.sheet
        return sheet.resources.isNotEmpty() || sheet.classOptions.isNotEmpty()
    }

    private fun renderPerAttribute(
        page: PDPage,
        attributes: List<PcSheetCustomAttributeProjection>,
        skills: List<PcSheetCustomSkillProjection>,
    ) {
        require(attributes.size <= 3) {
            "Production pass 1 supports up to three custom attributes on the per-Attribute Extended page; overflow pagination is the next promotion pass."
        }

        val customById = attributes.associateBy { it.attribute.id }
        val linkedByCustom = skills
            .filter { it.ability.customAttributeId != null }
            .groupBy { it.ability.customAttributeId }
        linkedByCustom.values.forEach { linked ->
            require(linked.size <= 6) {
                "Production pass 1 supports up to six custom skills linked to one custom attribute; overflow pagination is pending."
            }
        }

        val standardGroups = skills
            .filter { it.ability.builtIn != null }
            .groupBy { requireNotNull(it.ability.builtIn) }
            .toList()
        require(standardGroups.size <= 3) {
            "Production pass 1 supports custom skills linked to up to three built-in attributes on this page; overflow pagination is pending."
        }
        standardGroups.forEach { (_, group) ->
            require(group.size <= 4) {
                "Production pass 1 supports up to four custom skills for one built-in attribute in the standard-anchor section."
            }
        }

        appendLayer(page, "V2X ATTR - STRUCTURE") { s ->
            pageHeaderStructure(s, resources.forms[2])
            val columns = listOf(14f, 207f, 400f)
            columns.forEach { x ->
                fill(s, x, 104f, 184f, 244f, SOURCE_GRAY_LIGHT)
                attributeBandStructure(s, x, 104f, 184f, 6)
            }

            drawRule(s, 14f, 598f, 365f, 0.8f)
            columns.forEachIndexed { index, x ->
                fill(s, x, 392f, 184f, 110f, if (index % 2 == 0) SOURCE_GRAY_LIGHT else SOURCE_GRAY_DARK)
                drawRule(s, x + 16f, x + 174f, 430f, 0.55f)
                repeat(4) { row -> drawRule(s, x + 16f, x + 174f, 447f + row * 17f, 0.55f) }
            }

            drawRule(s, 14f, 598f, 522f, 0.8f)
            columns.forEachIndexed { col, x ->
                bandedRows(s, x, x + 184f, 562f, 10, 17f, col)
            }
        }
        appendLayer(page, "V2X ATTR - CLEANUP") { }
        appendLayer(page, "V2X ATTR - LABELS") { s ->
            pageTitle(s, "ESTADÍSTICAS PERSONALIZADAS")
            attributes.forEachIndexed { index, projection ->
                textTopSource(
                    s, resources.corbelBold, resources.firaSemibold,
                    18f + index * 193f, 111f,
                    keyedName(projection.attribute.name, projection.attribute.abbreviation),
                    12.12f, SOURCE_CORBEL_ATTRIBUTE_SCALE,
                )
                textAboveRuleSource(
                    s, resources.corbel, resources.fira,
                    Rule(108f + index * 193f, 159f + index * 193f, 151f),
                    "Tirada de Salvación", 7.75f, 2.0f, SOURCE_CORBEL_COMPACT_SCALE,
                )
            }
            centeredSource(
                s, resources.corbelBold, resources.firaSemibold,
                TopRect(14f, 367f, 584f, 22f),
                "HABILIDADES VINCULADAS A ATRIBUTOS ESTÁNDAR", 10.2f, SOURCE_CORBEL_HEADING_SCALE,
            )
            standardGroups.forEachIndexed { index, group ->
                centeredSource(
                    s, resources.corbelBold, resources.firaSemibold,
                    TopRect(14f + index * 193f, 397f, 184f, 22f),
                    builtInKeyedName(group.first), 12.12f, SOURCE_CORBEL_ATTRIBUTE_SCALE,
                )
            }
            centeredSource(
                s, resources.corbelBold, resources.firaSemibold,
                TopRect(14f, 524f, 584f, 22f),
                "DEFINICIONES / NOTAS", 12.12f, SOURCE_CORBEL_HEADING_SCALE,
            )
        }
        appendLayer(page, "V2X ATTR - VALUES") { s ->
            attributes.forEachIndexed { index, projection ->
                val attr = projection.attribute
                val linked = linkedByCustom[attr.id].orEmpty()
                val sample = AttributeValues(
                    score = attr.score.toString(),
                    modifier = signed(attr.modifier),
                    save = projection.savingThrowTotal?.let(::signed).orEmpty(),
                    skills = linked.map { it.skill.name to it.total?.let(::signed).orEmpty() },
                )
                drawAttributeBandValues(s, 14f + index * 193f, 104f, sample)
            }

            standardGroups.forEachIndexed { col, group ->
                group.second.forEachIndexed { row, item ->
                    val y = 447f + row * 17f
                    textAboveRuleSource(
                        s, resources.corbel, resources.fira,
                        Rule(38f + col * 193f, 164f + col * 193f, y),
                        item.skill.name, 7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE,
                    )
                    item.total?.let {
                        centeredAboveRule(
                            s, resources.firaSemibold,
                            Rule(164f + col * 193f, 196f + col * 193f, y),
                            signed(it), 8.8f, 2.2f,
                        )
                    }
                }
            }

            attributes.forEachIndexed { col, projection ->
                val rawNote = projection.attribute.notes.orEmpty().trim()
                if (rawNote.isNotEmpty()) {
                    val key = projection.attribute.abbreviation.trim().uppercase().take(3)
                    val note = if (key.isNotEmpty() && !rawNote.startsWith("$key:", ignoreCase = true)) {
                        "$key: $rawNote"
                    } else {
                        rawNote
                    }
                    val lines = wrapByWidth(resources.fira, note, 8.8f, 174f)
                    require(lines.size <= 10) {
                        "Custom attribute notes exceed the approved Run-7 notes region; overflow continuation is pending."
                    }
                    lines.forEachIndexed { row, line ->
                        textAboveRule(
                            s, resources.fira,
                            Rule(18f + col * 193f, 194f + col * 193f, 562f + row * 17f),
                            line, 8.8f, 8.0f, 2.2f,
                        )
                    }
                }
            }
        }
        appendLayer(page, "V2X ATTR - MARKERS") { s ->
            repeat(3) { col ->
                val projection = attributes.getOrNull(col)
                val saveTraining = if (
                    projection?.attribute?.savingThrowEnabled == true &&
                    projection.attribute.savingThrowProficient
                ) {
                    Training.PROFICIENT
                } else {
                    Training.NONE
                }
                drawV2TrainingBox(s, TopRect(98.5f + col * 193f, 141.5f, 8.5f, 9f), saveTraining)

                val linked = projection
                    ?.let { linkedByCustom[it.attribute.id].orEmpty() }
                    .orEmpty()
                repeat(6) { row ->
                    drawV2TrainingBox(
                        s,
                        TopRect(98.5f + col * 193f, 157f + row * 17f, 8.5f, 9f),
                        linked.getOrNull(row)?.let { training(it.skill.training) } ?: Training.NONE,
                    )
                }
            }

            repeat(3) { col ->
                val group = standardGroups.getOrNull(col)?.second.orEmpty()
                repeat(4) { row ->
                    drawV2TrainingBox(
                        s,
                        TopRect(18f + col * 193f, 434f + row * 17f, 8.5f, 9f),
                        group.getOrNull(row)?.let { training(it.skill.training) } ?: Training.NONE,
                    )
                }
            }
        }

        require(customById.size == attributes.size)
    }

    private fun renderPerAbility(
        page: PDPage,
        attributes: List<PcSheetCustomAttributeProjection>,
        skills: List<PcSheetCustomSkillProjection>,
    ) {
        val saves = attributes.filter { it.attribute.savingThrowEnabled }
        require(attributes.size <= 6) {
            "Production pass 1 supports up to six custom attributes on the per-Ability Extended page; overflow pagination is the next promotion pass."
        }
        require(saves.size <= 30) {
            "Production pass 1 supports up to thirty custom saving throws on the per-Ability Extended page."
        }
        require(skills.size <= 34) {
            "Production pass 1 supports up to thirty-four custom skills on the per-Ability Extended page."
        }

        appendLayer(page, "V2X ABILITY - STRUCTURE") { s ->
            pageHeaderStructure(s, resources.forms[2])
            fill(s, 14f, 104f, 174f, 30f, SOURCE_GRAY_LIGHT)
            fill(s, 202f, 104f, 150f, 30f, SOURCE_GRAY_LIGHT)
            fill(s, 366f, 104f, 232f, 30f, SOURCE_GRAY_LIGHT)

            repeat(6) { index ->
                val top = 136f + index * 96f
                fill(s, 14f, top, 174f, 94f, if (index % 2 == 0) SOURCE_GRAY_LIGHT else SOURCE_GRAY_DARK)
                drawAttributeOrnament(s, 14.3f, top + 24f)
                drawRule(s, 22f, 180f, top + 94f, 0.55f)
            }
            bandedRows(s, 202f, 352f, 154f, 30, 17f, 1)
            bandedRows(s, 366f, 598f, 154f, 34, 17f, 0)
        }
        appendLayer(page, "V2X ABILITY - CLEANUP") { }
        appendLayer(page, "V2X ABILITY - LABELS") { s ->
            pageTitle(s, "ESTADÍSTICAS PERSONALIZADAS")
            centeredSource(s, resources.corbelBold, resources.firaSemibold, TopRect(14f, 108f, 174f, 22f), "ATRIBUTOS", 7.8f, SOURCE_CORBEL_HEADING_SCALE)
            centeredSource(s, resources.corbelBold, resources.firaSemibold, TopRect(202f, 108f, 150f, 22f), "TIRADAS DE SALVACIÓN", 7.8f, SOURCE_CORBEL_HEADING_SCALE)
            centeredSource(s, resources.corbelBold, resources.firaSemibold, TopRect(366f, 108f, 232f, 22f), "HABILIDADES", 7.8f, SOURCE_CORBEL_HEADING_SCALE)
            attributes.forEachIndexed { index, projection ->
                textTopSource(
                    s, resources.corbelBold, resources.firaSemibold,
                    14f, 142f + index * 96f,
                    keyedName(projection.attribute.name, projection.attribute.abbreviation),
                    12.12f, SOURCE_CORBEL_ATTRIBUTE_SCALE,
                )
            }
        }
        appendLayer(page, "V2X ABILITY - VALUES") { s ->
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
                    keyedName(projection.attribute.name, projection.attribute.abbreviation),
                    7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE,
                )
                projection.savingThrowTotal?.let {
                    centeredAboveRule(s, resources.firaSemibold, Rule(308f, 342f, y), signed(it), 8.8f, 2.2f)
                }
            }

            skills.forEachIndexed { index, projection ->
                val y = 154f + index * 17f
                val label = projection.skill.name + " (" + abilityKey(projection.ability, attributes) + ")"
                textAboveRuleSource(
                    s, resources.corbel, resources.fira,
                    Rule(399f, 548f, y), label, 7.75f, 2.2f, SOURCE_CORBEL_COMPACT_SCALE,
                )
                projection.total?.let {
                    centeredAboveRule(s, resources.firaSemibold, Rule(548f, 588f, y), signed(it), 8.8f, 2.2f)
                }
            }
        }
        appendLayer(page, "V2X ABILITY - MARKERS") { s ->
            repeat(30) { row ->
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
            repeat(34) { row ->
                drawV2TrainingBox(
                    s,
                    TopRect(384f, 142f + row * 17f, 8.5f, 9f),
                    skills.getOrNull(row)?.let { training(it.skill.training) } ?: Training.NONE,
                )
            }
        }
    }

    private fun renderTraits(page: PDPage, plan: PcSheetPdfRenderPlan) {
        val sheet = plan.snapshot.aggregate.sheet
        val traits = sheet.traits.sortedBy { it.sortOrder }
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
        val proficiencies = sheet.proficiencies.sortedBy { it.sortOrder }

        require(remaining.size <= 10) {
            "Traits production pass 2 supports up to ten continuation trait names after four featured entries."
        }
        require(proficiencies.size <= 8) {
            "Traits production pass 2 supports up to eight proficiency/language continuation rows."
        }

        appendLayer(page, "V2X TRAITS - STRUCTURE") { s ->
            pageHeaderStructure(s, resources.forms[2])
            fill(s, 14f, 96f, 277f, 24f, SOURCE_GRAY_LIGHT)
            fill(s, 307f, 96f, 291f, 24f, SOURCE_GRAY_LIGHT)
            bandedRows(s, 14f, 291f, 137f, 35, 17f, 0)
            bandedRows(s, 307f, 598f, 137f, 35, 17f, 1)
            drawRule(s, 14f, 291f, 358f, 0.8f)
            drawRule(s, 307f, 598f, 358f, 0.8f)
            drawRule(s, 14f, 291f, 579f, 0.8f)
            drawRule(s, 307f, 598f, 579f, 0.8f)
        }
        appendLayer(page, "V2X TRAITS - CLEANUP") { }
        appendLayer(page, "V2X TRAITS - LABELS") { s ->
            pageTitle(s, "RASGOS Y ATRIBUTOS")
            centeredSource(s, resources.corbelBold, resources.firaSemibold, TopRect(14f, 98f, 277f, 20f), "CLASE / DOTES", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            centeredSource(s, resources.corbelBold, resources.firaSemibold, TopRect(307f, 98f, 291f, 20f), "RAZA / TRASFONDO / OTROS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            textTopSource(s, resources.corbelBold, resources.firaSemibold, 18f, 365f, "OTROS RASGOS", 9.5f, SOURCE_CORBEL_HEADING_SCALE)
            textTopSource(s, resources.corbelBold, resources.firaSemibold, 311f, 365f, "DETALLES / NOTAS", 9.5f, SOURCE_CORBEL_HEADING_SCALE)
            textTopSource(s, resources.corbelBold, resources.firaSemibold, 18f, 586f, "COMPETENCIAS / IDIOMAS", 9.5f, SOURCE_CORBEL_HEADING_SCALE)
            textTopSource(s, resources.corbelBold, resources.firaSemibold, 311f, 586f, "CONTINUACIÓN", 9.5f, SOURCE_CORBEL_HEADING_SCALE)
        }
        appendLayer(page, "V2X TRAITS - VALUES") { s ->
            featuredLeft.forEachIndexed { index, trait ->
                featureEntry(s, 14f, 137f + index * 102f, 277f, trait)
            }
            featuredRight.forEachIndexed { index, trait ->
                featureEntry(s, 307f, 137f + index * 102f, 291f, trait)
            }

            remaining.forEachIndexed { index, trait ->
                textAboveRule(s, resources.fira, Rule(18f, 287f, 392f + index * 17f), trait.name, 8.1f, 6.5f, 2.2f)
            }

            val detailLines = remaining.flatMap { trait ->
                val detail = listOf(
                    trait.description.trim(),
                    trait.notes.orEmpty().trim(),
                ).filter { it.isNotEmpty() }.joinToString(" · ")
                if (detail.isBlank()) emptyList()
                else wrapByWidth(resources.fira, trait.name + ": " + detail, 7.7f, 281f)
            }
            require(detailLines.size <= 10) {
                "Trait detail continuation exceeds the approved Run-7 detail region."
            }
            detailLines.forEachIndexed { index, line ->
                textAboveRule(s, resources.fira, Rule(311f, 594f, 392f + index * 17f), line, 7.7f, 6.2f, 2.2f)
            }

            proficiencies.forEachIndexed { index, proficiency ->
                val label = buildString {
                    append(proficiency.name)
                    proficiency.source?.takeIf { it.isNotBlank() }?.let { append(" · ").append(it) }
                }
                textAboveRule(s, resources.fira, Rule(18f, 287f, 613f + index * 17f), label, 8.0f, 6.4f, 2.2f)
            }

            val continuation = (featuredLeft + featuredRight).flatMap { trait ->
                trait.notes.orEmpty().trim().takeIf { it.isNotEmpty() }
                    ?.let { wrapByWidth(resources.fira, trait.name + ": " + it, 8.2f, 281f) }
                    .orEmpty()
            }
            require(continuation.size <= 8) {
                "Featured trait notes exceed the approved Run-7 continuation region."
            }
            continuation.forEachIndexed { index, line ->
                textAboveRule(s, resources.fira, Rule(311f, 594f, 613f + index * 17f), line, 8.2f, 7.2f, 2.3f)
            }
        }
        appendLayer(page, "V2X TRAITS - MARKERS") { }
    }

    private fun renderResources(page: PDPage, plan: PcSheetPdfRenderPlan) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val rows = sheet.resources.sortedBy { it.sortOrder }
        val options = sheet.classOptions.sortedBy { it.sortOrder }
        val recoveries = aggregate.closure.resourceRecovery.associateBy { it.resourceId }

        require(rows.size <= 10) {
            "Resources production pass 2 supports up to ten resource rows; multi-page continuation is pending."
        }
        require(options.size <= 18) {
            "Resources production pass 2 supports up to eighteen option rows; multi-page continuation is pending."
        }

        appendLayer(page, "V2X RESOURCES - STRUCTURE") { s ->
            pageHeaderStructure(s, resources.forms[2])
            fill(s, 14f, 96f, 584f, 22f, SOURCE_GRAY_LIGHT)
            bandedRows(s, 14f, 598f, 150f, 10, 17f, 0)
            listOf(222f, 352f, 475f).forEach { x -> verticalRule(s, x, 120f, 303f, 0.45f) }

            drawRule(s, 14f, 598f, 329f, 0.8f)
            fill(s, 14f, 337f, 584f, 22f, SOURCE_GRAY_LIGHT)
            bandedRows(s, 14f, 598f, 398f, 18, 17f, 1)
            listOf(30f, 118f, 258f).forEach { x -> verticalRule(s, x, 362f, 704f, 0.45f) }
        }
        appendLayer(page, "V2X RESOURCES - CLEANUP") { }
        appendLayer(page, "V2X RESOURCES - LABELS") { s ->
            pageTitle(s, "RECURSOS Y OPCIONES")
            centeredSource(s, resources.corbelBold, resources.firaSemibold, TopRect(14f, 97f, 584f, 20f), "RECURSOS", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            tableLabel(s, 14f, 121f, 208f, "RECURSO")
            tableLabel(s, 222f, 121f, 130f, "ACTUAL / MÁX.")
            tableLabel(s, 352f, 121f, 123f, "RESTABLECE")
            tableLabel(s, 475f, 121f, 123f, "ORIGEN")

            centeredSource(s, resources.corbelBold, resources.firaSemibold, TopRect(14f, 338f, 584f, 20f), "OPCIONES", 12.12f, SOURCE_CORBEL_HEADING_SCALE)
            tableLabel(s, 30f, 364f, 88f, "TIPO")
            tableLabel(s, 118f, 364f, 140f, "OPCIÓN")
            tableLabel(s, 258f, 364f, 340f, "DESCRIPCIÓN / COSTE")
        }
        appendLayer(page, "V2X RESOURCES - VALUES") { s ->
            rows.forEachIndexed { index, row ->
                val y = 150f + index * 17f
                textAboveRule(s, resources.fira, Rule(18f, 218f, y), row.name, 9.0f, 8.2f, 2.3f)

                val maximum = row.maxValue
                when {
                    maximum == null ->
                        centeredAboveRule(s, resources.firaSemibold, Rule(226f, 348f, y), row.currentValue.toString(), 8.5f, 2.2f)
                    maximum >= 10 ->
                        centeredAboveRule(s, resources.firaSemibold, Rule(226f, 348f, y), row.currentValue.toString() + "/" + maximum, 8.5f, 2.2f)
                }

                val recovery = row.recovery.orEmpty().trim().ifEmpty {
                    recoveries[row.id]?.cadence?.let(::recoveryLabel).orEmpty()
                }
                if (recovery.isNotEmpty()) {
                    textAboveRule(s, resources.fira, Rule(356f, 471f, y), recovery, 8.5f, 7.5f, 2.3f)
                }
                row.source?.takeIf { it.isNotBlank() }?.let { source ->
                    textAboveRule(s, resources.fira, Rule(479f, 594f, y), source, 8.5f, 7.5f, 2.3f)
                }
            }

            options.forEachIndexed { index, option ->
                val y = 398f + index * 17f
                textAboveRule(s, resources.fira, Rule(34f, 114f, y), optionKindLabel(option.kind), 8.5f, 7.5f, 2.3f)
                textAboveRuleScaled(s, resources.fira, Rule(122f, 254f, y), option.name, 8.5f, 7.5f, 2.3f, 72f)

                val detail = listOf(
                    option.effectSummary.trim(),
                    option.costText.orEmpty().trim(),
                    option.notes.orEmpty().trim(),
                ).filter { it.isNotEmpty() }.joinToString(" · ")
                if (detail.isNotEmpty()) {
                    textAboveRule(s, resources.fira, Rule(262f, 594f, y), detail, 8.5f, 7.0f, 2.3f)
                }
            }
        }
        appendLayer(page, "V2X RESOURCES - MARKERS") { s ->
            rows.forEachIndexed { index, row ->
                val maximum = row.maxValue
                if (maximum != null && maximum in 1..9) {
                    drawSquareCounter(
                        s,
                        236f,
                        141.5f + index * 17f,
                        row.currentValue.coerceIn(0, maximum),
                        maximum,
                    )
                }
            }

            repeat(18) { row ->
                val option = options.getOrNull(row)
                drawV2TrainingBox(
                    s,
                    TopRect(16f, 386f + row * 17f, 8.5f, 9f),
                    if (option?.active == true) Training.PROFICIENT else Training.NONE,
                )
            }
        }
    }

    private fun featureEntry(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        width: Float,
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
    ) {
        textAboveRule(s, resources.firaSemibold, Rule(x + 4f, x + width - 4f, top), trait.name, 9.0f, 7.4f, 2.7f)

        val meta = listOf(
            traitTypeLabel(trait.type),
            trait.source.trim(),
            trait.activation?.let(::activationLabel).orEmpty(),
        ).filter { it.isNotEmpty() }.joinToString(" · ")
        if (meta.isNotEmpty()) {
            textAboveRule(s, resources.fira, Rule(x + 4f, x + width - 4f, top + 17f), meta, 7.3f, 6.2f, 2.5f)
        }

        val uses = trait.maxUses?.let { max ->
            val current = (max - trait.spentUses).coerceIn(0, max)
            buildString {
                append("Usos: ").append(current).append(" / ").append(max)
                trait.recovery?.takeIf { it.isNotBlank() }?.let { append(" · ").append(it) }
            }
        } ?: trait.recovery?.takeIf { it.isNotBlank() }

        val descriptionTop = if (uses != null) top + 51f else top + 34f
        uses?.let {
            textAboveRule(s, resources.fira, Rule(x + 4f, x + width - 4f, top + 34f), it, 7.3f, 6.2f, 2.5f)
        }

        val description = trait.description.trim()
        if (description.isNotEmpty()) {
            wrapByWidth(resources.fira, description, 7.4f, width - 8f)
                .take(3)
                .forEachIndexed { index, line ->
                    textAboveRule(s, resources.fira, Rule(x + 4f, x + width - 4f, descriptionTop + index * 17f), line, 7.4f, 6.2f, 2.5f)
                }
        }
    }

    private fun tableLabel(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        width: Float,
        label: String,
    ) {
        if (label.isNotBlank()) {
            centeredSource(s, resources.corbel, resources.fira, TopRect(x, top, width, 18f), label, 7.79f, SOURCE_CORBEL_TABLE_SCALE)
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
        require(scale >= minimumHorizontalScale) {
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

    private fun pageHeaderStructure(s: PDFormContentStream, logoSource: PDFormXObject) {
        drawSourceCrop(s, logoSource, 14f, 16f, 105f, 60f)
        drawRule(s, 126f, 598f, 79f, 0.6f)
    }

    private fun pageTitle(s: PDFormContentStream, title: String) {
        centeredSource(
            s, resources.corbelBold, resources.firaSemibold,
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
        val scaledWidth = textWidth(font, text, size) * horizontalScale / 100f
        require(scaledWidth <= available + 0.05f) {
            "Source-matched text does not fit: $text ($scaledWidth > $available)"
        }
        val descent = (font.fontDescriptor?.descent ?: -250f) / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.setHorizontalScaling(horizontalScale)
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
        text.trim().split(Regex("\\s+")).forEach { word ->
            val candidate = if (current.isBlank()) word else "$current $word"
            if (textWidth(font, candidate, size) <= maxWidth) current = candidate
            else {
                if (current.isNotBlank()) out += current
                current = word
            }
        }
        if (current.isNotBlank()) out += current
        return out
    }

    private fun textWidth(font: PDFont, text: String, size: Float): Float =
        font.getStringWidth(text) / 1000f * size

    private fun keyedName(name: String, abbreviation: String): String {
        val cleanName = name.trim()
        val cleanKey = abbreviation.trim().uppercase().take(3)
        if (cleanKey.isEmpty()) return cleanName
        return if (cleanName.length >= cleanKey.length &&
            cleanName.substring(0, cleanKey.length).equals(cleanKey, ignoreCase = true)
        ) {
            cleanKey + cleanName.substring(cleanKey.length)
        } else {
            "$cleanKey · $cleanName"
        }
    }

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

    private enum class Training { NONE, PROFICIENT, EXPERTISE }

    private data class Resources(
        val forms: List<PDFormXObject>,
        val attributeOrnament: PDImageXObject,
        val corbel: PDFont,
        val corbelBold: PDFont,
        val fira: PDFont,
        val firaSemibold: PDFont,
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
                    attributeOrnament = buildTransparentAttributeOrnament(doc, source),
                    corbel = corbelRegular,
                    corbelBold = corbelBold,
                    fira = resourceFont(doc, resourceLoader, FIRA_REGULAR),
                    firaSemibold = resourceFont(doc, resourceLoader, FIRA_SEMIBOLD),
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
        const val SYMBOL_V8 = "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf"
        const val ATTRIBUTE_ORNAMENT_SOURCE_X = 14.32f
        const val ATTRIBUTE_ORNAMENT_SOURCE_TOP = 164.68f
        const val ATTRIBUTE_ORNAMENT_WIDTH = 80.40f
        const val ATTRIBUTE_ORNAMENT_HEIGHT = 47.76f
        const val SOURCE_CORBEL_ATTRIBUTE_SCALE = 79f
        const val SOURCE_CORBEL_COMPACT_SCALE = 78f
        const val SOURCE_CORBEL_HEADING_SCALE = 81f
        const val SOURCE_CORBEL_TABLE_SCALE = 86f
        const val BASE_V2_TRAIT_CAPACITY = 18
        val SOURCE_GRAY_DARK: Color = Color(200, 199, 199)
        val SOURCE_GRAY_LIGHT: Color = Color(227, 227, 227)
    }
}
