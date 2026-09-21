package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomSkillProjection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedPageKind
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import java.awt.Color
import java.awt.geom.AffineTransform
import java.io.InputStream
import kotlin.math.max
import org.apache.pdfbox.multipdf.LayerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDFormContentStream
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.util.Matrix

/**
 * Production promotion of the owner-approved Custom-v1 Extended Run-6 family.
 *
 * Pass 1 intentionally promotes only Extended — Custom Statistics. The geometry, typography,
 * source crops and independent layer model come from the frozen Run-6 owner-approved proof; the
 * values come exclusively from [PcSheetPdfRenderPlan].
 */
internal class DesktopCustomV1ExtendedRenderer(
    private val document: PDDocument,
    private val sourceTemplate: PDDocument,
    private val resourceLoader: (String) -> InputStream?,
) {
    private val resources by lazy {
        Resources.load(document, sourceTemplate, resourceLoader)
    }
    private var layerSerial = 0

    fun appendExtendedPages(plan: PcSheetPdfRenderPlan) {
        require(plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V1)
        val stats = plan.snapshot.customStatistics
        if (
            PcSheetExtendedPageKind.CUSTOM_STATISTICS in plan.mandatoryExtendedPages &&
            !stats.isEmpty
        ) {
            appendCustomStatisticsPages(plan)
        }
        if (needsTraitsExtendedPage(plan)) {
            appendTraitsExtendedPages(plan)
        }
    }

    private fun appendCustomStatisticsPages(plan: PcSheetPdfRenderPlan) {
        val stats = plan.snapshot.customStatistics
        val modules = buildModules(plan)
        val definitionLines = stats.attributes.flatMap { projection ->
            val note = projection.attribute.notes.orEmpty().trim()
            if (note.isEmpty()) {
                emptyList()
            } else {
                wrapByWidth(
                    keyedName(projection.attribute.name, projection.attribute.abbreviation) + ": " + note,
                    resources.fira,
                    8.1f,
                    BOTTOM_TEXT_WIDTH,
                )
            }
        }
        val noteLines = stats.skills.flatMap { projection ->
            val metadata = listOf(
                projection.skill.source.orEmpty().trim(),
                projection.skill.notes.orEmpty().trim(),
            ).filter { it.isNotEmpty() }
            if (metadata.isEmpty()) {
                emptyList()
            } else {
                wrapByWidth(
                    projection.skill.name + ": " + metadata.joinToString(" · "),
                    resources.fira,
                    8.1f,
                    BOTTOM_TEXT_WIDTH,
                )
            }
        }

        val pages = maxOf(
            1,
            pageCount(modules.size, MODULES_PER_PAGE),
            pageCount(definitionLines.size, BOTTOM_LINES_PER_PAGE),
            pageCount(noteLines.size, BOTTOM_LINES_PER_PAGE),
        )

        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderCustomStatisticsPage(
                page = page,
                modules = modules
                    .drop(pageIndex * MODULES_PER_PAGE)
                    .take(MODULES_PER_PAGE),
                definitions = definitionLines
                    .drop(pageIndex * BOTTOM_LINES_PER_PAGE)
                    .take(BOTTOM_LINES_PER_PAGE),
                notes = noteLines
                    .drop(pageIndex * BOTTOM_LINES_PER_PAGE)
                    .take(BOTTOM_LINES_PER_PAGE),
                pageIndex = pageIndex,
            )
        }
    }

    private fun needsTraitsExtendedPage(plan: PcSheetPdfRenderPlan): Boolean {
        val sheet = plan.snapshot.aggregate.sheet
        val orderedTraits = sheet.traits.sortedBy { it.sortOrder }
        val overflowNames = orderedTraits.drop(BASE_V1_TRAIT_NAME_CAPACITY)
        val detailOverflow = traitDescriptionOverflowLines(orderedTraits)
        val metadata = traitMetadataLines(orderedTraits)
        return overflowNames.isNotEmpty() ||
            detailOverflow.isNotEmpty() ||
            metadata.isNotEmpty() ||
            sheet.proficiencies.isNotEmpty()
    }

    private fun appendTraitsExtendedPages(plan: PcSheetPdfRenderPlan) {
        val sheet = plan.snapshot.aggregate.sheet
        val orderedTraits = sheet.traits.sortedBy { it.sortOrder }
        val overflowTraits = orderedTraits.drop(BASE_V1_TRAIT_NAME_CAPACITY)

        val classNames = overflowTraits
            .filter { it.type == CharacterTraitType.CLASS }
            .map { it.name }
        val raceNames = overflowTraits
            .filter { it.type == CharacterTraitType.SPECIES_RACE }
            .map { it.name }
        val featNames = overflowTraits
            .filter { it.type == CharacterTraitType.FEAT }
            .map { it.name }
        val otherNames = overflowTraits
            .filter {
                it.type == CharacterTraitType.BACKGROUND ||
                    it.type == CharacterTraitType.GIFT_BLESSING ||
                    it.type == CharacterTraitType.OTHER
            }
            .map { it.name }

        val proficiencies = sheet.proficiencies
            .filter { it.type != CharacterProficiencyType.LANGUAGE }
            .sortedBy { it.sortOrder }
            .map { proficiency ->
                listOf(proficiency.name, proficiency.source.orEmpty())
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
            }
        val languages = sheet.proficiencies
            .filter { it.type == CharacterProficiencyType.LANGUAGE }
            .sortedBy { it.sortOrder }
            .map { proficiency ->
                listOf(proficiency.name, proficiency.source.orEmpty())
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
            }

        val detailLines = traitDescriptionOverflowLines(orderedTraits)
        val metadataLines = traitMetadataLines(orderedTraits)

        val pages = maxOf(
            1,
            pageCount(classNames.size, TRAIT_LEFT_ROWS),
            pageCount(raceNames.size, TRAIT_LEFT_ROWS),
            pageCount(featNames.size, TRAIT_LEFT_ROWS),
            pageCount(proficiencies.size, TRAIT_LEFT_ROWS),
            pageCount(languages.size, TRAIT_LEFT_ROWS),
            pageCount(otherNames.size, TRAIT_OTHER_CAPACITY),
            pageCount(detailLines.size, TRAIT_DETAIL_ROWS),
            pageCount(metadataLines.size, TRAIT_NOTE_ROWS),
        )

        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderTraitsPage(
                page = page,
                classNames = classNames.pageSlice(pageIndex, TRAIT_LEFT_ROWS),
                raceNames = raceNames.pageSlice(pageIndex, TRAIT_LEFT_ROWS),
                featNames = featNames.pageSlice(pageIndex, TRAIT_LEFT_ROWS),
                proficiencies = proficiencies.pageSlice(pageIndex, TRAIT_LEFT_ROWS),
                languages = languages.pageSlice(pageIndex, TRAIT_LEFT_ROWS),
                otherNames = otherNames.pageSlice(pageIndex, TRAIT_OTHER_CAPACITY),
                detailLines = detailLines.pageSlice(pageIndex, TRAIT_DETAIL_ROWS),
                noteLines = metadataLines.pageSlice(pageIndex, TRAIT_NOTE_ROWS),
                pageIndex = pageIndex,
            )
        }
    }

    private fun renderTraitsPage(
        page: PDPage,
        classNames: List<String>,
        raceNames: List<String>,
        featNames: List<String>,
        proficiencies: List<String>,
        languages: List<String>,
        otherNames: List<String>,
        detailLines: List<String>,
        noteLines: List<String>,
        pageIndex: Int,
    ) {
        val prefix = "V1X TRAITS P${pageIndex + 1}"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            s.drawForm(resources.forms[2])
        }
        appendLayer(page, "$prefix - CLEANUP") { s ->
            headingInteriorMask(s, 24f, 66f, 156f, 35f)
            headingInteriorMask(s, 24f, 205f, 156f, 35f)
            headingInteriorMask(s, 24f, 344f, 156f, 35f)
            headingInteriorMask(s, 24f, 483f, 156f, 35f)
            headingInteriorMask(s, 24f, 621f, 156f, 35f)
            headingInteriorMask(s, 215f, 344f, 369f, 35f)
        }
        appendLayer(page, "$prefix - LABELS") { s ->
            centeredText(s, resources.heading, 24f, 66f, 156f, 35f, "Rasgos de Clase", 18f)
            centeredText(s, resources.heading, 24f, 205f, 156f, 35f, "Rasgos de Raza", 18f)
            centeredText(s, resources.heading, 24f, 344f, 156f, 35f, "Dotes", 18f)
            centeredText(s, resources.heading, 24f, 483f, 156f, 35f, "Competencias", 18f)
            centeredText(s, resources.heading, 24f, 621f, 156f, 35f, "Idiomas", 18f)
            centeredText(s, resources.heading, 215f, 344f, 369f, 35f, "Detalles de Rasgos", 18f)
        }
        appendLayer(page, "$prefix - VALUES") { s ->
            drawRuledValues(s, 25f, 181f, TRAIT_CLASS_RULES, classNames, 8.6f)
            drawRuledValues(s, 25f, 181f, TRAIT_RACE_RULES, raceNames, 8.6f)
            drawRuledValues(s, 25f, 181f, TRAIT_FEAT_RULES, featNames, 8.6f)
            drawRuledValues(s, 25f, 181f, TRAIT_PROF_RULES, proficiencies, 8.4f)
            drawRuledValues(s, 25f, 181f, TRAIT_LANGUAGE_RULES, languages, 8.6f)

            TRAIT_OTHER_COLUMNS.forEachIndexed { columnIndex, (startX, endX) ->
                drawRuledValues(
                    s,
                    startX,
                    endX,
                    TRAIT_OTHER_RULES,
                    otherNames
                        .drop(columnIndex * TRAIT_OTHER_RULES.size)
                        .take(TRAIT_OTHER_RULES.size),
                    8.6f,
                )
            }

            drawRuledValues(
                s,
                215.291f,
                583.795f,
                TRAIT_DETAIL_RULES,
                detailLines,
                8.5f,
            )
            drawRuledValues(
                s,
                215.291f,
                583.795f,
                TRAIT_NOTE_RULES,
                noteLines,
                8.4f,
            )
        }
        appendLayer(page, "$prefix - MARKERS") { }
    }

    private fun traitDescriptionOverflowLines(
        traits: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>,
    ): List<String> {
        val traitText = traits.joinToString(" · ") { trait ->
            trait.name + ": " + trait.description
        }
        return wrapForRulesByChars(traitText, BASE_V1_TRAIT_DETAIL_MAX_CHARS)
            .drop(BASE_V1_TRAIT_DETAIL_RULES)
    }

    private fun traitMetadataLines(
        traits: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>,
    ): List<String> = traits.flatMap { trait ->
        val meaningful = trait.maxUses != null ||
            !trait.recovery.isNullOrBlank() ||
            trait.activation != null ||
            !trait.notes.isNullOrBlank()
        if (!meaningful) {
            emptyList()
        } else {
            val metadata = buildList {
                trait.source.trim().takeIf { it.isNotEmpty() }?.let(::add)
                trait.activation?.let { add(activationLabel(it)) }
                trait.maxUses?.let { maximum ->
                    val remaining = (maximum - trait.spentUses).coerceIn(0, maximum)
                    add("Usos $remaining / $maximum")
                }
                trait.recovery?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
                trait.notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            }
            wrapByWidth(
                trait.name + ": " + metadata.joinToString(" · "),
                resources.fira,
                8.4f,
                TRAIT_RIGHT_TEXT_WIDTH,
            )
        }
    }

    private fun activationLabel(type: CharacterActivationType): String = when (type) {
        CharacterActivationType.PASSIVE -> "Pasivo"
        CharacterActivationType.ACTION -> "Acción"
        CharacterActivationType.BONUS_ACTION -> "Acción adicional"
        CharacterActivationType.REACTION -> "Reacción"
        CharacterActivationType.OTHER -> "Otro"
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
                val candidate = if (current.isEmpty()) word else current + " " + word
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

    private fun drawRuledValues(
        s: PDFormContentStream,
        startX: Float,
        endX: Float,
        rules: List<Float>,
        values: List<String>,
        size: Float,
    ) {
        rules.forEachIndexed { index, y ->
            values.getOrNull(index)?.takeIf { it.isNotBlank() }?.let { value ->
                ruleText(s, resources.fira, Rule(startX, endX, y), value, size)
            }
        }
    }

    private fun headingInteriorMask(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
    ) {
        fill(s, x + 3f, top + 5f, width - 6f, height - 10f, Color.WHITE)
    }

    private fun <T> List<T>.pageSlice(pageIndex: Int, capacity: Int): List<T> =
        drop(pageIndex * capacity).take(capacity)

    private fun buildModules(plan: PcSheetPdfRenderPlan): List<ModuleSlice> {
        val stats = plan.snapshot.customStatistics
        val sheet = plan.snapshot.aggregate.sheet
        val linkedByCustom = stats.skills
            .filter { it.ability.customAttributeId != null }
            .groupBy { it.ability.customAttributeId }

        val customModules = stats.attributes.flatMap { projection ->
            linkedByCustom[projection.attribute.id].orEmpty()
                .chunked(SKILLS_PER_MODULE)
                .ifEmpty { listOf(emptyList()) }
                .map { skills ->
                    ModuleSlice(
                        title = keyedName(
                            projection.attribute.name,
                            projection.attribute.abbreviation,
                        ),
                        score = projection.attribute.score.toString(),
                        modifier = signed(projection.attribute.modifier),
                        save = if (projection.attribute.savingThrowEnabled) {
                            projection.savingThrowTotal?.let(::signed).orEmpty()
                        } else {
                            ""
                        },
                        saveTraining = if (
                            projection.attribute.savingThrowEnabled &&
                            projection.attribute.savingThrowProficient
                        ) {
                            Training.PROFICIENT
                        } else {
                            Training.NONE
                        },
                        skills = skills.map(::skillLine),
                    )
                }
        }

        val standardModules = CharacterAbility.entries.flatMap { ability ->
            val grouped = stats.skills.filter { it.ability.builtIn == ability }
            grouped.chunked(SKILLS_PER_MODULE).map { skills ->
                ModuleSlice(
                    title = builtInKeyedName(ability),
                    score = sheet.abilityScore(ability).toString(),
                    modifier = signed(sheet.abilityModifier(ability)),
                    save = signed(sheet.savingThrowTotal(ability)),
                    saveTraining = if (sheet.savingThrow(ability).proficient) {
                        Training.PROFICIENT
                    } else {
                        Training.NONE
                    },
                    skills = skills.map(::skillLine),
                )
            }
        }

        return customModules + standardModules
    }

    private fun skillLine(projection: PcSheetCustomSkillProjection): SkillLine =
        SkillLine(
            name = projection.skill.name,
            total = projection.total?.let(::signed).orEmpty(),
            training = training(projection.skill.training),
        )

    private fun renderCustomStatisticsPage(
        page: PDPage,
        modules: List<ModuleSlice>,
        definitions: List<String>,
        notes: List<String>,
        pageIndex: Int,
    ) {
        val prefix = "V1X STATS P${pageIndex + 1}"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            drawSourceCrop(s, resources.forms[0], 20f, 18f, 170f, 74f)
            COLUMNS.forEach { column ->
                drawTranslatedSourceCrop(
                    s = s,
                    form = resources.forms[0],
                    sourceX = SOURCE_WHITE_ATTRIBUTE_X,
                    sourceTop = SOURCE_SCORE_FRAGMENT_TOP,
                    width = column.width,
                    height = SOURCE_SCORE_FRAGMENT_HEIGHT,
                    targetX = column.x,
                    targetTop = STAT_SCORE_FRAGMENT_TARGET_TOP,
                )
                drawRule(s, column.x + 16f, column.x + column.width - 5f, STAT_SAVE_RULE_TOP)
                STAT_SKILL_RULE_TOPS.forEach { top ->
                    drawRule(s, column.x + 16f, column.x + column.width - 5f, top)
                }
            }
            STAT_SECTION_COLUMNS.forEach { (a, b) ->
                sourceBands(s, a, b, DEFINITIONS_FIRST_RULE_TOP, STAT_SECTION_ROWS, STAT_SECTION_STEP)
                sourceBands(s, a, b, NOTES_FIRST_RULE_TOP, STAT_SECTION_ROWS, STAT_SECTION_STEP)
            }
        }

        appendLayer(page, "$prefix - CLEANUP") { s ->
            COLUMNS.forEachIndexed { index, _ ->
                fill(s, SCORE_X[index] - 18f, STAT_SCORE_VALUE_TOP, 36f, 17f, Color.WHITE)
                fill(s, MOD_X[index] - 10f, STAT_MOD_VALUE_TOP, 20f, 10f, Color.WHITE)
            }
        }

        appendLayer(page, "$prefix - LABELS") { s ->
            centeredText(
                s, resources.heading,
                215f, 48f, 365f, 26f,
                "Estadísticas Personalizadas", 18f,
            )
            centeredText(
                s, resources.fira,
                215f, 76f, 365f, 13f,
                "ATRIBUTOS PERSONALIZADOS Y HABILIDADES VINCULADAS", 7.5f,
            )
            modules.forEachIndexed { index, module ->
                val column = COLUMNS[index]
                centeredGeneratedHeading(
                    s = s,
                    x = column.x + 1f,
                    top = STAT_ATTRIBUTE_TITLE_TOP,
                    width = column.width - 2f,
                    height = 20f,
                    value = module.title,
                    preferredSize = 16.5f,
                )
            }
            centeredText(
                s, resources.heading,
                24f, DEFINITIONS_HEADING_TOP, 564f, 26f,
                "Definiciones", 17f,
            )
            centeredText(
                s, resources.heading,
                24f, NOTES_HEADING_TOP, 564f, 26f,
                "Notas de Estadísticas Personalizadas", 17f,
            )
        }

        appendLayer(page, "$prefix - VALUES") { s ->
            modules.forEachIndexed { index, module ->
                val column = COLUMNS[index]
                centeredText(
                    s, resources.firaSemibold,
                    SCORE_X[index] - 18f, STAT_SCORE_VALUE_TOP, 36f, 17f,
                    module.score, 17f,
                )
                centeredText(
                    s, resources.firaSemibold,
                    MOD_X[index] - 10f, STAT_MOD_VALUE_TOP, 20f, 10f,
                    module.modifier, 10.6f,
                )

                sourceMatchedSkillLabel(
                    s, resources.fira,
                    column.x + 16f,
                    STAT_SAVE_RULE_TOP - SOURCE_LABEL_BASELINE_OFFSET,
                    "Tirada de Salvación",
                    column.width - 39f,
                )
                if (module.save.isNotEmpty()) {
                    centeredText(
                        s, resources.firaSemibold,
                        column.x + column.width - 22f,
                        STAT_SAVE_RULE_TOP - 11.6f,
                        17f, 11f,
                        module.save, 7.7f,
                    )
                }

                STAT_SKILL_RULE_TOPS.forEachIndexed { rowIndex, ruleTop ->
                    module.skills.getOrNull(rowIndex)?.let { skill ->
                        sourceMatchedSkillLabel(
                            s, resources.fira,
                            column.x + 16f,
                            ruleTop - SOURCE_LABEL_BASELINE_OFFSET,
                            skill.name,
                            column.width - 39f,
                        )
                        if (skill.total.isNotEmpty()) {
                            centeredText(
                                s, resources.firaSemibold,
                                column.x + column.width - 22f,
                                ruleTop - 11.6f,
                                17f, 11f,
                                skill.total, 7.5f,
                            )
                        }
                    }
                }
            }

            drawBottomLines(s, definitions, DEFINITIONS_FIRST_RULE_TOP)
            drawBottomLines(s, notes, NOTES_FIRST_RULE_TOP)
        }

        appendLayer(page, "$prefix - MARKERS") { s ->
            COLUMNS.forEachIndexed { index, column ->
                val module = modules.getOrNull(index)
                drawV1TrainingBox(
                    s, resources.symbol,
                    column.x + 6.2f,
                    STAT_SAVE_RULE_TOP - 6.2f,
                    module?.saveTraining ?: Training.NONE,
                )
                STAT_SKILL_RULE_TOPS.forEachIndexed { rowIndex, ruleTop ->
                    drawV1TrainingBox(
                        s, resources.symbol,
                        column.x + 6.2f,
                        ruleTop - 6.2f,
                        module?.skills?.getOrNull(rowIndex)?.training ?: Training.NONE,
                    )
                }
            }
        }
    }

    private fun drawBottomLines(
        s: PDFormContentStream,
        lines: List<String>,
        firstRuleTop: Float,
    ) {
        STAT_SECTION_COLUMNS.forEachIndexed { columnIndex, (a, b) ->
            lines
                .drop(columnIndex * STAT_SECTION_ROWS)
                .take(STAT_SECTION_ROWS)
                .forEachIndexed { rowIndex, value ->
                    ruleText(
                        s, resources.fira,
                        Rule(a, b, firstRuleTop + rowIndex * STAT_SECTION_STEP),
                        value, 8.1f,
                    )
                }
        }
    }

    private fun appendLayer(
        page: PDPage,
        name: String,
        draw: (PDFormContentStream) -> Unit,
    ) {
        val form = PDFormXObject(document).apply {
            resources = PDResources()
            setBBox(PDRectangle(W, H))
        }
        PDFormContentStream(form).use { stream ->
            stream.setNonStrokingColor(Color.BLACK)
            draw(stream)
        }
        LayerUtility(document).appendFormAsLayer(
            page,
            form,
            AffineTransform(),
            "$name #${++layerSerial}",
        )
    }

    private fun drawV1TrainingBox(
        s: PDFormContentStream,
        font: PDFont,
        centerX: Float,
        centerTop: Float,
        training: Training,
    ) {
        val codePoint = when (training) {
            Training.NONE -> 0xE203
            Training.PROFICIENT -> 0xE213
            Training.EXPERTISE -> 0xE214
        }
        centeredText(
            s, font,
            centerX - 5.5f, centerTop - 5.5f, 11f, 11f,
            String(Character.toChars(codePoint)), 8.4f,
        )
    }

    private fun sourceMatchedSkillLabel(
        s: PDFormContentStream,
        font: PDFont,
        x: Float,
        baselineTop: Float,
        value: String,
        maximumWidth: Float,
    ) {
        if (value.isBlank()) return
        val size = 10f
        val rawWidth = textWidth(font, value, size)
        val scale = minOf(SOURCE_LABEL_HORIZONTAL_SCALE, maximumWidth / rawWidth * 100f)
        require(scale >= MINIMUM_LABEL_HORIZONTAL_SCALE) {
            "Custom-v1 source-matched label requires excessive compression: '$value' ($scale%)"
        }
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, size)
        s.setHorizontalScaling(scale)
        s.newLineAtOffset(x, H - baselineTop)
        s.showText(value)
        s.setHorizontalScaling(100f)
        s.endText()
    }

    private fun ruleText(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        value: String,
        size: Float,
        leftPadding: Float = 2f,
    ) {
        if (value.isBlank()) return
        val available = rule.endX - rule.startX - leftPadding - 1f
        var actual = size
        while (actual > MINIMUM_BODY_SIZE && textWidth(font, value, actual) > available) {
            actual -= 0.2f
        }
        require(textWidth(font, value, actual) <= available + 0.05f) {
            "Custom-v1 Extended text does not fit: $value"
        }
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, actual)
        s.newLineAtOffset(rule.startX + leftPadding, H - rule.topY + 3.2f)
        s.showText(value)
        s.endText()
    }

    private fun centeredGeneratedHeading(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        preferredSize: Float,
    ) {
        val useSource = supports(resources.heading, value)
        centeredText(
            s = s,
            font = if (useSource) resources.heading else resources.firaSemibold,
            x = x,
            top = top,
            width = width,
            height = height,
            value = value,
            size = if (useSource) preferredSize else minOf(preferredSize, 12.5f),
        )
    }

    private fun supports(font: PDFont, text: String): Boolean =
        runCatching {
            font.encode(text)
            true
        }.getOrDefault(false)

    private fun centeredText(
        s: PDFormContentStream,
        font: PDFont,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        if (value.isBlank()) return
        var actual = size
        while (actual > MINIMUM_BODY_SIZE && textWidth(font, value, actual) > width - 2f) {
            actual -= 0.2f
        }
        require(textWidth(font, value, actual) <= width + 0.05f) {
            "Custom-v1 Extended centered text does not fit: $value"
        }
        val widthText = textWidth(font, value, actual)
        val descriptor = requireNotNull(font.fontDescriptor)
        val ascent = descriptor.ascent / 1000f * actual
        val descent = descriptor.descent / 1000f * actual
        val boxY = H - top - height
        val baseline = boxY + (height - (ascent - descent)) / 2f - descent
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, actual)
        s.newLineAtOffset(x + (width - widthText) / 2f, baseline)
        s.showText(value)
        s.endText()
    }

    private fun wrapByWidth(
        text: String,
        font: PDFont,
        size: Float,
        maximumWidth: Float,
    ): List<String> {
        val clean = text.trim()
        if (clean.isEmpty()) return emptyList()
        val output = mutableListOf<String>()
        var current = ""
        clean.split(Regex("\\s+")).forEach { word ->
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (textWidth(font, candidate, size) <= maximumWidth || current.isEmpty()) {
                current = candidate
            } else {
                output += current
                current = word
            }
        }
        if (current.isNotEmpty()) output += current
        return output
    }

    private fun sourceBands(
        s: PDFormContentStream,
        x1: Float,
        x2: Float,
        top: Float,
        rows: Int,
        step: Float,
    ) {
        repeat(rows) { index ->
            val rowTop = top + index * step
            if (index % 2 == 0) {
                fill(s, x1, rowTop - step + 1f, x2 - x1, step - 1f, SOURCE_GRAY)
            }
            drawRule(s, x1, x2, rowTop)
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

    private fun drawTranslatedSourceCrop(
        s: PDFormContentStream,
        form: PDFormXObject,
        sourceX: Float,
        sourceTop: Float,
        width: Float,
        height: Float,
        targetX: Float,
        targetTop: Float,
    ) {
        s.saveGraphicsState()
        s.addRect(targetX, H - targetTop - height, width, height)
        s.clip()
        s.transform(Matrix.getTranslateInstance(targetX - sourceX, sourceTop - targetTop))
        s.drawForm(form)
        s.restoreGraphicsState()
    }

    private fun fill(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        color: Color,
    ) {
        s.saveGraphicsState()
        s.setNonStrokingColor(color)
        s.addRect(x, H - top - height, width, height)
        s.fill()
        s.restoreGraphicsState()
    }

    private fun drawRule(
        s: PDFormContentStream,
        startX: Float,
        endX: Float,
        top: Float,
    ) {
        s.saveGraphicsState()
        s.setStrokingColor(Color.BLACK)
        s.setLineWidth(0.65f)
        s.moveTo(startX, H - top)
        s.lineTo(endX, H - top)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun keyedName(name: String, abbreviation: String): String {
        val cleanName = name.trim()
        val cleanKey = abbreviation.trim().uppercase().take(3)
        if (cleanKey.isEmpty()) return cleanName
        return if (
            cleanName.length >= cleanKey.length &&
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

    private fun training(value: SkillTraining): Training = when (value) {
        SkillTraining.NONE -> Training.NONE
        SkillTraining.PROFICIENT -> Training.PROFICIENT
        SkillTraining.EXPERTISE -> Training.EXPERTISE
    }

    private fun signed(value: Int): String = if (value >= 0) "+$value" else value.toString()

    private fun pageCount(size: Int, capacity: Int): Int =
        if (size <= 0) 0 else (size + capacity - 1) / capacity

    private fun textWidth(font: PDFont, text: String, size: Float): Float =
        font.getStringWidth(text) / 1000f * size

    private data class ModuleSlice(
        val title: String,
        val score: String,
        val modifier: String,
        val save: String,
        val saveTraining: Training,
        val skills: List<SkillLine>,
    )

    private data class SkillLine(
        val name: String,
        val total: String,
        val training: Training,
    )

    private data class ColumnGeometry(
        val x: Float,
        val width: Float,
    )

    private data class Rule(
        val startX: Float,
        val endX: Float,
        val topY: Float,
    )

    private enum class Training {
        NONE,
        PROFICIENT,
        EXPERTISE,
    }

    private data class Resources(
        val forms: List<PDFormXObject>,
        val heading: PDFont,
        val fira: PDFont,
        val firaSemibold: PDFont,
        val symbol: PDFont,
    ) {
        companion object {
            fun load(
                document: PDDocument,
                source: PDDocument,
                resourceLoader: (String) -> InputStream?,
            ): Resources {
                val utility = LayerUtility(document)
                val forms = (0 until 5).map { utility.importPageAsForm(source, it) }
                val heading = findImportedFont(forms) { name ->
                    name.contains("EnchantedLand", ignoreCase = true)
                }
                return Resources(
                    forms = forms,
                    heading = heading,
                    fira = resourceFont(document, resourceLoader, FIRA_RESOURCE),
                    firaSemibold = resourceFont(document, resourceLoader, FIRA_SEMIBOLD_RESOURCE),
                    symbol = resourceFont(document, resourceLoader, SYMBOL_RESOURCE),
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
                error("Requested imported Custom-v1 source font not found.")
            }

            private fun resourceFont(
                document: PDDocument,
                resourceLoader: (String) -> InputStream?,
                path: String,
            ): PDFont = requireNotNull(resourceLoader(path)) {
                "Missing renderer resource: $path"
            }.use { PDType0Font.load(document, it, false) }
        }
    }

    private companion object {
        const val W = 612f
        const val H = 792f

        const val FIRA_RESOURCE = "fonts/pdf/text/FiraSans-Regular.ttf"
        const val FIRA_SEMIBOLD_RESOURCE = "fonts/pdf/text/FiraSans-SemiBold.ttf"
        const val SYMBOL_RESOURCE = "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf"

        const val MODULES_PER_PAGE = 6
        const val SKILLS_PER_MODULE = 5
        const val BOTTOM_LINES_PER_PAGE = 15
        const val BOTTOM_TEXT_WIDTH = 150f

        const val BASE_V1_TRAIT_NAME_CAPACITY = 6
        const val BASE_V1_TRAIT_DETAIL_RULES = 12
        const val BASE_V1_TRAIT_DETAIL_MAX_CHARS = 92
        const val TRAIT_LEFT_ROWS = 3
        const val TRAIT_OTHER_CAPACITY = 12
        const val TRAIT_DETAIL_ROWS = 4
        const val TRAIT_NOTE_ROWS = 5
        const val TRAIT_RIGHT_TEXT_WIDTH = 365f

        const val SOURCE_WHITE_ATTRIBUTE_X = 408f
        const val SOURCE_SCORE_FRAGMENT_TOP = 268.5f
        const val SOURCE_SCORE_FRAGMENT_HEIGHT = 41.5f
        const val STAT_SCORE_FRAGMENT_TARGET_TOP = 136.5f
        const val STAT_ATTRIBUTE_TITLE_TOP = 100f
        const val STAT_SCORE_VALUE_TOP = 139f
        const val STAT_MOD_VALUE_TOP = 157f
        const val STAT_SAVE_RULE_TOP = 209.0f

        const val DEFINITIONS_HEADING_TOP = 325f
        const val DEFINITIONS_FIRST_RULE_TOP = 370f
        const val NOTES_HEADING_TOP = 490f
        const val NOTES_FIRST_RULE_TOP = 535f
        const val STAT_SECTION_ROWS = 5
        const val STAT_SECTION_STEP = 22f

        const val SOURCE_LABEL_HORIZONTAL_SCALE = 60f
        const val MINIMUM_LABEL_HORIZONTAL_SCALE = 50f
        const val SOURCE_LABEL_BASELINE_OFFSET = 3.0f
        const val MINIMUM_BODY_SIZE = 5.8f

        val SOURCE_GRAY = Color(211, 210, 210)

        val TRAIT_CLASS_RULES = listOf(109.5f, 129.5f, 149.5f)
        val TRAIT_RACE_RULES = listOf(248.5f, 268.5f, 287.5f)
        val TRAIT_FEAT_RULES = listOf(387.5f, 407.5f, 426f)
        val TRAIT_PROF_RULES = listOf(526.5f, 546f, 565f)
        val TRAIT_LANGUAGE_RULES = listOf(665.5f, 685f, 704f)
        val TRAIT_OTHER_RULES = listOf(109.5f, 129.5f, 149.5f, 169f, 189f, 209f)
        val TRAIT_OTHER_COLUMNS = listOf(
            215.291f to 396.708f,
            402.378f to 583.795f,
        )
        val TRAIT_DETAIL_RULES = listOf(387.996f, 407.839f, 427.681f, 447.524f)
        val TRAIT_NOTE_RULES = listOf(606f, 625.5f, 645.5f, 665.5f, 685f)

        val SCORE_X = listOf(58f, 154.25f, 250.75f, 347.25f, 445.5f, 539.75f)
        val MOD_X = listOf(85f, 184.25f, 280.5f, 376.75f, 473.25f, 569.5f)
        val STAT_SKILL_RULE_TOPS = listOf(223.173f, 237.346f, 251.519f, 265.692f, 279.865f)
        val STAT_SECTION_COLUMNS = listOf(
            25f to 181f,
            215.291f to 396.708f,
            402.378f to 583.795f,
        )
        val COLUMNS = listOf(
            ColumnGeometry(22.5f, 96.4f),
            ColumnGeometry(118.9f, 96.4f),
            ColumnGeometry(215.3f, 96.4f),
            ColumnGeometry(311.7f, 96.4f),
            ColumnGeometry(408.0f, 96.4f),
            ColumnGeometry(504.4f, 85.5f),
        )
    }
}
