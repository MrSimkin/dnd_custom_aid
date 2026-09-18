package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBaseLayoutMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBasePageRole
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.spellAttackModifier
import io.github.mrsimkin.dndcustomaid.shared.character.spellSaveDc
import java.awt.Color
import java.io.InputStream
import java.io.OutputStream
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts

/**
 * Development renderer used to prove measured overlays against the owner's authoritative Custom
 * v1/v2 PDF pages before the complete export renderer is wired into product UI.
 *
 * It deliberately renders only the selected MAIN page. Other base pages, Extended pages, portraits
 * and App Modified geometry remain outside this visual-proof slice, so this type must not be used as
 * a complete PC export implementation.
 */
internal class DesktopPcSheetTemplateProofRenderer(
    private val resourceLoader: (String) -> InputStream? = { resourcePath ->
        DesktopPcSheetTemplateProofRenderer::class.java.classLoader.getResourceAsStream(resourcePath)
    },
) {
    fun renderMainPage(
        plan: PcSheetPdfRenderPlan,
        output: OutputStream,
    ) {
        require(plan.baseLayoutMode == PcSheetBaseLayoutMode.FAITHFUL) {
            "Template proof rendering currently supports faithful Custom layouts only."
        }
        require(
            plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V1 ||
                plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE ||
                plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
        ) {
            "Template proof rendering is only defined for the owner's Custom v1/v2 families."
        }

        val mainPage = plan.basePages.single { it.role == PcSheetBasePageRole.MAIN }
        val templatePath = requireNotNull(mainPage.sourceTemplatePath)
        val sourcePageNumber = requireNotNull(mainPage.sourcePageNumber)
        val classpathPath = templatePath.removePrefix("assets/")
        val template = resourceLoader(classpathPath)
            ?: error("PC sheet template resource is unavailable: $classpathPath")

        template.use { input ->
            Loader.loadPDF(input.readBytes()).use { document ->
                val sourceIndex = sourcePageNumber - 1
                require(sourceIndex in 0 until document.numberOfPages) {
                    "Template page $sourcePageNumber does not exist in $templatePath."
                }

                for (index in document.numberOfPages - 1 downTo 0) {
                    if (index != sourceIndex) {
                        document.removePage(index)
                    }
                }

                val page = document.getPage(0)
                val primitives = DesktopPdfRenderingPrimitives(
                    DesktopPdfFontRegistry(document, resourceLoader = resourceLoader),
                )
                PDPageContentStream(document, page, AppendMode.APPEND, true, true).use { stream ->
                    when (plan.request.visualFamily) {
                        PcSheetVisualFamily.CUSTOM_V1 -> drawCustomV1(stream, primitives, plan)
                        PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE -> drawCustomV2PerAttribute(stream, primitives, plan)
                        PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY -> drawCustomV2PerAbility(stream, primitives, plan)
                        PcSheetVisualFamily.CLASSIC_DND_STYLE -> error("Classic is not an owner-template proof.")
                    }
                }
                document.save(output)
            }
        }
    }

    private fun drawCustomV1(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val closure = aggregate.closure

        stream.textPx(620f, 72f, classSummary(plan), 9f, 280f)
        stream.textPx(620f, 112f, sheet.background.race, 9f, 280f)
        if (closure.progressMode == CharacterProgressMode.EXPERIENCE) {
            stream.textPx(250f, 190f, closure.experiencePoints.toString(), 8f, 180f)
        }
        stream.centerTextPx(1005f, 410f, sheet.name, 10f, 250f, bold = true)

        stream.centerTextPx(115f, 332f, sheet.armorClass.toString(), 17f, 105f, bold = true)
        stream.centerTextPx(395f, 270f, signed(sheet.initiativeModifier), 12f, 100f, bold = true)
        stream.centerTextPx(540f, 270f, signed(sheet.finalProficiencyBonus), 12f, 100f, bold = true)
        stream.centerTextPx(685f, 270f, sheet.maxHp.toString(), 12f, 100f, bold = true)
        stream.centerTextPx(827f, 334f, sheet.currentHp.toString(), 13f, 100f, bold = true)
        stream.centerTextPx(395f, 405f, sheet.speed.toString(), 12f, 100f, bold = true)
        if (sheet.inspiration) {
            markerPx(stream, primitives, 540f, 405f, 21f, PdfMarkerKind.STAR_FILLED, PdfSymbolFamily.V1_DERIVED)
        }
        stream.centerTextPx(685f, 405f, hitDiceSummary(plan), 9f, 105f, bold = true)

        val abilityColumns = listOf(
            CharacterAbility.STRENGTH to 115f,
            CharacterAbility.DEXTERITY to 305f,
            CharacterAbility.CONSTITUTION to 495f,
            CharacterAbility.INTELLIGENCE to 685f,
            CharacterAbility.WISDOM to 875f,
            CharacterAbility.CHARISMA to 1065f,
        )
        abilityColumns.forEach { (ability, x) ->
            stream.centerTextPx(x, 570f, sheet.abilityScore(ability).toString(), 12f, 85f, bold = true)
            stream.centerTextPx(x + 55f, 590f, signed(sheet.abilityModifier(ability)), 8.5f, 55f, bold = true)
        }

        drawCustomV1ChecksAndTotals(stream, primitives, plan)
        drawCustomV1Spellcasting(stream, primitives, plan)
        drawCustomV1Attacks(stream, plan)
        drawCustomV1Traits(stream, plan)
    }

    private fun drawCustomV1ChecksAndTotals(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val columns = listOf(
            V1AbilityColumn(CharacterAbility.STRENGTH, 55f, 175f, listOf(SkillKey.ATHLETICS)),
            V1AbilityColumn(
                CharacterAbility.DEXTERITY,
                245f,
                365f,
                listOf(SkillKey.ACROBATICS, SkillKey.SLEIGHT_OF_HAND, SkillKey.STEALTH),
            ),
            V1AbilityColumn(CharacterAbility.CONSTITUTION, 438f, 555f, emptyList()),
            V1AbilityColumn(
                CharacterAbility.INTELLIGENCE,
                628f,
                745f,
                listOf(
                    SkillKey.ARCANA,
                    SkillKey.HISTORY,
                    SkillKey.INVESTIGATION,
                    SkillKey.NATURE,
                    SkillKey.RELIGION,
                ),
            ),
            V1AbilityColumn(
                CharacterAbility.WISDOM,
                818f,
                935f,
                listOf(
                    SkillKey.MEDICINE,
                    SkillKey.PERCEPTION,
                    SkillKey.INSIGHT,
                    SkillKey.SURVIVAL,
                    SkillKey.ANIMAL_HANDLING,
                ),
            ),
            V1AbilityColumn(
                CharacterAbility.CHARISMA,
                1010f,
                1170f,
                listOf(
                    SkillKey.DECEPTION,
                    SkillKey.PERFORMANCE,
                    SkillKey.INTIMIDATION,
                    SkillKey.PERSUASION,
                ),
            ),
        )
        columns.forEach { column ->
            val save = sheet.savingThrow(column.ability)
            if (save.proficient) {
                markerPx(stream, primitives, column.checkboxX, 632f, 13f, PdfMarkerKind.CHECK, PdfSymbolFamily.V1_DERIVED)
            }
            stream.textPx(column.valueX, 622f, signed(sheet.savingThrowTotal(column.ability)), 6.5f, 45f)
            column.skills.forEachIndexed { index, skill ->
                val y = 652f + index * 30f
                val state = sheet.skill(skill)
                skillMarkerKind(state.training)?.let { kind ->
                    markerPx(stream, primitives, column.checkboxX, y + 9f, 13f, kind, PdfSymbolFamily.V1_DERIVED)
                }
                stream.textPx(column.valueX, y, signed(sheet.skillTotal(skill)), 6.5f, 45f)
            }
        }
    }

    private fun drawCustomV1Spellcasting(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val (saveDc, attack, ability) = spellcastingSummary(plan)
        saveDc?.let { stream.centerTextPx(300f, 858f, it.toString(), 9f, 95f, bold = true) }
        attack?.let { stream.centerTextPx(300f, 1012f, signed(it), 9f, 95f, bold = true) }
        ability.takeIf { it.isNotBlank() }?.let {
            stream.centerTextPx(300f, 1152f, it, 8f, 95f, bold = true)
        }

        val slots = sheet.spellSlots.associateBy { it.level }
        for (level in 1..9) {
            val slot = slots[level] ?: continue
            val y = 898f + (level - 1) * 39f
            stream.centerTextPx(112f, y, slot.totalSlots.toString(), 7.5f, 45f, bold = true)
            val spentCenters = listOf(174f, 202f, 230f, 258f)
            repeat(slot.spentSlots.coerceAtMost(spentCenters.size)) { index ->
                markerPx(
                    stream, primitives, spentCenters[index], y + 2f, 12f,
                    PdfMarkerKind.CIRCLE_FILLED, PdfSymbolFamily.V1_DERIVED,
                )
            }
        }
    }

    private fun drawCustomV1Attacks(
        stream: PDPageContentStream,
        plan: PcSheetPdfRenderPlan,
    ) {
        plan.snapshot.aggregate.sheet.combatEntries
            .sortedBy { it.sortOrder }
            .take(5)
            .forEachIndexed { index, entry ->
                val y = 897f + index * 71f
                stream.textPx(440f, y, entry.name, 7.5f, 265f)
                stream.textPx(720f, y, entry.rangeText.orEmpty(), 7f, 105f)
                stream.textPx(835f, y, entry.attackModifier?.let(::signed).orEmpty(), 7.5f, 105f)
                stream.textPx(1010f, y, entry.damageEffect, 7f, 150f)
            }
    }

    private fun drawCustomV1Traits(
        stream: PDPageContentStream,
        plan: PcSheetPdfRenderPlan,
    ) {
        plan.snapshot.aggregate.sheet.traits
            .sortedBy { it.sortOrder }
            .take(9)
            .forEachIndexed { index, trait ->
                val column = index % 3
                val row = index / 3
                stream.textPx(
                    xPx = 65f + column * 375f,
                    yPx = 1328f + row * 76f,
                    text = trait.name,
                    fontSizePt = 7f,
                    maxWidthPx = 330f,
                )
            }
    }

    private fun drawCustomV2PerAttribute(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        drawCustomV2Common(stream, primitives, plan, nameCenterY = 315f, pageTwoVariant = false)
        val sheet = plan.snapshot.aggregate.sheet

        val abilityRows = listOf(
            CharacterAbility.STRENGTH to 270f,
            CharacterAbility.DEXTERITY to 420f,
            CharacterAbility.CONSTITUTION to 585f,
            CharacterAbility.INTELLIGENCE to 740f,
            CharacterAbility.WISDOM to 895f,
            CharacterAbility.CHARISMA to 1050f,
        )
        abilityRows.forEach { (ability, y) ->
            stream.centerTextPx(80f, y, sheet.abilityScore(ability).toString(), 12f, 85f, bold = true)
            stream.centerTextPx(140f, y + 15f, signed(sheet.abilityModifier(ability)), 8.5f, 60f, bold = true)
        }

        val rows = listOf(
            V2InlineRow(CharacterAbility.STRENGTH, 255f, listOf(SkillKey.ATHLETICS to 282f)),
            V2InlineRow(
                CharacterAbility.DEXTERITY,
                397f,
                listOf(
                    SkillKey.ACROBATICS to 422f,
                    SkillKey.SLEIGHT_OF_HAND to 446f,
                    SkillKey.STEALTH to 470f,
                ),
            ),
            V2InlineRow(CharacterAbility.CONSTITUTION, 583f, emptyList()),
            V2InlineRow(
                CharacterAbility.INTELLIGENCE,
                688f,
                listOf(
                    SkillKey.ARCANA to 714f,
                    SkillKey.HISTORY to 738f,
                    SkillKey.INVESTIGATION to 762f,
                    SkillKey.NATURE to 786f,
                    SkillKey.RELIGION to 810f,
                ),
            ),
            V2InlineRow(
                CharacterAbility.WISDOM,
                850f,
                listOf(
                    SkillKey.MEDICINE to 875f,
                    SkillKey.PERCEPTION to 899f,
                    SkillKey.INSIGHT to 923f,
                    SkillKey.SURVIVAL to 947f,
                    SkillKey.ANIMAL_HANDLING to 971f,
                ),
            ),
            V2InlineRow(
                CharacterAbility.CHARISMA,
                1047f,
                listOf(
                    SkillKey.DECEPTION to 1071f,
                    SkillKey.PERFORMANCE to 1095f,
                    SkillKey.INTIMIDATION to 1119f,
                    SkillKey.PERSUASION to 1143f,
                ),
            ),
        )
        rows.forEach { row ->
            val save = sheet.savingThrow(row.ability)
            if (save.proficient) {
                markerPx(stream, primitives, 204f, row.saveY + 7f, 12f, PdfMarkerKind.CHECK, PdfSymbolFamily.V3_DERIVED)
            }
            stream.textPx(315f, row.saveY, signed(sheet.savingThrowTotal(row.ability)), 6.2f, 45f)
            row.skills.forEach { (skill, y) ->
                skillMarkerKind(sheet.skill(skill).training)?.let { kind ->
                    markerPx(stream, primitives, 204f, y + 7f, 12f, kind, PdfSymbolFamily.V3_DERIVED)
                }
                stream.textPx(315f, y, signed(sheet.skillTotal(skill)), 6.2f, 45f)
            }
        }
    }

    private fun drawCustomV2PerAbility(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        drawCustomV2Common(stream, primitives, plan, nameCenterY = 365f, pageTwoVariant = true)
        val sheet = plan.snapshot.aggregate.sheet
        val abilityRows = listOf(
            CharacterAbility.STRENGTH to 345f,
            CharacterAbility.DEXTERITY to 485f,
            CharacterAbility.CONSTITUTION to 625f,
            CharacterAbility.INTELLIGENCE to 770f,
            CharacterAbility.WISDOM to 925f,
            CharacterAbility.CHARISMA to 1070f,
        )
        abilityRows.forEach { (ability, y) ->
            stream.centerTextPx(80f, y, sheet.abilityScore(ability).toString(), 12f, 85f, bold = true)
            stream.centerTextPx(140f, y + 20f, signed(sheet.abilityModifier(ability)), 8.5f, 60f, bold = true)
        }

        CharacterAbility.entries.forEachIndexed { index, ability ->
            val y = 347f + index * 31f
            val save = sheet.savingThrow(ability)
            if (save.proficient) {
                markerPx(stream, primitives, 204f, y + 7f, 12f, PdfMarkerKind.CHECK, PdfSymbolFamily.V3_DERIVED)
            }
            stream.textPx(320f, y, signed(sheet.savingThrowTotal(ability)), 6.2f, 42f)
        }

        V2_PER_ABILITY_SKILL_ORDER.forEachIndexed { index, skill ->
            val y = 590f + index * 29f
            skillMarkerKind(sheet.skill(skill).training)?.let { kind ->
                markerPx(stream, primitives, 204f, y + 7f, 12f, kind, PdfSymbolFamily.V3_DERIVED)
            }
            stream.textPx(320f, y, signed(sheet.skillTotal(skill)), 6.1f, 42f)
        }
    }

    private fun drawCustomV2Common(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
        nameCenterY: Float,
        pageTwoVariant: Boolean,
    ) {
        val sheet = plan.snapshot.aggregate.sheet

        stream.textPx(890f, 64f, classSummary(plan), 9f, 300f)
        stream.textPx(800f, 110f, sheet.background.race, 9f, 390f)
        stream.centerTextPx(520f, nameCenterY, sheet.name, 10f, 245f, bold = true)

        stream.centerTextPx(850f, 220f, sheet.armorClass.toString(), 11f, 105f, bold = true)
        stream.centerTextPx(970f, 220f, hitDiceSummary(plan), 8.5f, 125f, bold = true)
        stream.centerTextPx(1120f, 220f, sheet.maxHp.toString(), 11f, 105f, bold = true)
        stream.centerTextPx(850f, 315f, signed(sheet.initiativeModifier), 11f, 105f, bold = true)
        stream.centerTextPx(1035f, 325f, sheet.currentHp.toString(), 12f, 200f, bold = true)
        stream.centerTextPx(850f, 420f, sheet.speed.toString(), 11f, 105f, bold = true)

        if (pageTwoVariant) {
            stream.centerTextPx(100f, 225f, signed(sheet.finalProficiencyBonus), 10f, 135f, bold = true)
            if (sheet.inspiration) {
                markerPx(stream, primitives, 260f, 225f, 20f, PdfMarkerKind.STAR_FILLED, PdfSymbolFamily.V3_DERIVED)
            }
        } else {
            stream.centerTextPx(505f, 420f, signed(sheet.finalProficiencyBonus), 10f, 135f, bold = true)
            if (sheet.inspiration) {
                markerPx(stream, primitives, 650f, 420f, 20f, PdfMarkerKind.STAR_FILLED, PdfSymbolFamily.V3_DERIVED)
            }
        }

        sheet.combatEntries
            .sortedBy { it.sortOrder }
            .take(4)
            .forEachIndexed { index, entry ->
                val y = 518f + index * 55f
                val name = buildString {
                    append(entry.name)
                    entry.rangeText?.takeIf { it.isNotBlank() }?.let { append(" (").append(it).append(")") }
                }
                stream.textPx(385f, y, name, 7f, 420f)
                stream.textPx(820f, y, entry.attackModifier?.let(::signed).orEmpty(), 7.2f, 120f)
                stream.textPx(980f, y, entry.damageEffect, 7f, 205f)
            }

        sheet.traits
            .sortedBy { it.sortOrder }
            .take(8)
            .forEachIndexed { index, trait ->
                val column = index % 2
                val row = index / 2
                stream.textPx(
                    390f + column * 420f,
                    870f + row * 62f,
                    trait.name,
                    7f,
                    360f,
                )
            }

        val (saveDc, attack, ability) = spellcastingSummary(plan)
        saveDc?.let { stream.centerTextPx(265f, 1245f, it.toString(), 8.5f, 100f, bold = true) }
        attack?.let { stream.centerTextPx(265f, 1370f, signed(it), 8.5f, 100f, bold = true) }
        ability.takeIf { it.isNotBlank() }?.let {
            stream.centerTextPx(265f, 1495f, it, 7.5f, 100f, bold = true)
        }

        val slots = sheet.spellSlots.associateBy { it.level }
        for (level in 1..9) {
            val slot = slots[level] ?: continue
            val y = 1235f + (level - 1) * 32f
            stream.centerTextPx(101f, y, slot.totalSlots.toString(), 7f, 42f, bold = true)
            val spentCenters = listOf(128f, 154f, 180f, 206f)
            repeat(slot.spentSlots.coerceAtMost(spentCenters.size)) { index ->
                markerPx(
                    stream, primitives, spentCenters[index], y + 2f, 11.5f,
                    PdfMarkerKind.CIRCLE_FILLED, PdfSymbolFamily.V3_DERIVED,
                )
            }
        }
    }

    private fun classSummary(plan: PcSheetPdfRenderPlan): String =
        plan.snapshot.aggregate.sheet.classes
            .sortedBy { it.sortOrder }
            .joinToString(" / ") { classLevel -> "${classLevel.name} ${classLevel.level}" }

    private fun hitDiceSummary(plan: PcSheetPdfRenderPlan): String =
        plan.snapshot.aggregate.sheet.classes
            .sortedBy { it.sortOrder }
            .joinToString(" / ") { classLevel -> "${classLevel.hitDiceRemaining}d${classLevel.hitDieSides}" }

    private fun spellcastingSummary(plan: PcSheetPdfRenderPlan): Triple<Int?, Int?, String> {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val successor = aggregate.successor
        val profile = successor.spellcastingProfiles.firstOrNull()
        if (profile != null) {
            val abilityLabel = abilityLabel(profile.ability, plan)
            return Triple(
                sheet.spellSaveDc(profile, successor),
                sheet.spellAttackModifier(profile, successor),
                abilityLabel,
            )
        }
        val legacyAbility = when (sheet.spellcastingAbility.name) {
            "STRENGTH" -> "FUE"
            "DEXTERITY" -> "DES"
            "CONSTITUTION" -> "CON"
            "INTELLIGENCE" -> "INT"
            "WISDOM" -> "SAB"
            "CHARISMA" -> "CAR"
            else -> ""
        }
        return Triple(sheet.spellSaveDc, sheet.spellAttackModifier, legacyAbility)
    }

    private fun abilityLabel(
        reference: CharacterAbilityReference,
        plan: PcSheetPdfRenderPlan,
    ): String = when (reference.builtIn) {
        CharacterAbility.STRENGTH -> "FUE"
        CharacterAbility.DEXTERITY -> "DES"
        CharacterAbility.CONSTITUTION -> "CON"
        CharacterAbility.INTELLIGENCE -> "INT"
        CharacterAbility.WISDOM -> "SAB"
        CharacterAbility.CHARISMA -> "CAR"
        null -> reference.customAttributeId?.let { id ->
            plan.snapshot.aggregate.successor.customAttributes
                .firstOrNull { it.id == id }
                ?.abbreviation
                .orEmpty()
        }.orEmpty()
    }

    private fun skillMarkerKind(training: SkillTraining): PdfMarkerKind? = when (training) {
        SkillTraining.NONE -> null
        SkillTraining.PROFICIENT -> PdfMarkerKind.CHECK
        SkillTraining.EXPERTISE -> PdfMarkerKind.DOUBLE_CHECK
    }

    private fun markerPx(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        centerXPx: Float,
        centerYPx: Float,
        sizePx: Float,
        kind: PdfMarkerKind,
        family: PdfSymbolFamily,
    ) {
        primitives.drawMarker(
            stream = stream,
            centerX = centerXPx * PAGE_WIDTH_PT / REFERENCE_WIDTH_PX,
            centerY = PAGE_HEIGHT_PT - centerYPx * PAGE_HEIGHT_PT / REFERENCE_HEIGHT_PX,
            sizePt = sizePx * PAGE_WIDTH_PT / REFERENCE_WIDTH_PX,
            kind = kind,
            family = family,
        )
    }

    private fun signed(value: Int): String = if (value >= 0) "+$value" else value.toString()

    private data class V1AbilityColumn(
        val ability: CharacterAbility,
        val checkboxX: Float,
        val valueX: Float,
        val skills: List<SkillKey>,
    )

    private data class V2InlineRow(
        val ability: CharacterAbility,
        val saveY: Float,
        val skills: List<Pair<SkillKey, Float>>,
    )

    private companion object {
        const val REFERENCE_WIDTH_PX = 1224f
        const val REFERENCE_HEIGHT_PX = 1584f
        const val PAGE_WIDTH_PT = 612f
        const val PAGE_HEIGHT_PT = 792f

        val V2_PER_ABILITY_SKILL_ORDER = listOf(
            SkillKey.ACROBATICS,
            SkillKey.ATHLETICS,
            SkillKey.ARCANA,
            SkillKey.DECEPTION,
            SkillKey.PERFORMANCE,
            SkillKey.INTIMIDATION,
            SkillKey.INVESTIGATION,
            SkillKey.SLEIGHT_OF_HAND,
            SkillKey.HISTORY,
            SkillKey.MEDICINE,
            SkillKey.NATURE,
            SkillKey.PERCEPTION,
            SkillKey.INSIGHT,
            SkillKey.PERSUASION,
            SkillKey.RELIGION,
            SkillKey.STEALTH,
            SkillKey.SURVIVAL,
            SkillKey.ANIMAL_HANDLING,
        )
    }

    private fun PDPageContentStream.textPx(
        xPx: Float,
        yPx: Float,
        text: String,
        fontSizePt: Float,
        maxWidthPx: Float,
        bold: Boolean = false,
    ) {
        val cleaned = text.trim()
        if (cleaned.isEmpty()) return
        val font = if (bold) {
            PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
        } else {
            PDType1Font(Standard14Fonts.FontName.HELVETICA)
        }
        val maxWidthPt = maxWidthPx * PAGE_WIDTH_PT / REFERENCE_WIDTH_PX
        val fittedSize = fitFontSize(font, cleaned, fontSizePt, maxWidthPt)
        val xPt = xPx * PAGE_WIDTH_PT / REFERENCE_WIDTH_PX
        val yPt = PAGE_HEIGHT_PT - (yPx * PAGE_HEIGHT_PT / REFERENCE_HEIGHT_PX) - fittedSize * 0.82f
        beginText()
        setNonStrokingColor(Color.BLACK)
        setFont(font, fittedSize)
        newLineAtOffset(xPt, yPt)
        showText(cleaned)
        endText()
    }

    private fun PDPageContentStream.centerTextPx(
        centerXPx: Float,
        centerYPx: Float,
        text: String,
        fontSizePt: Float,
        maxWidthPx: Float,
        bold: Boolean = false,
    ) {
        val cleaned = text.trim()
        if (cleaned.isEmpty()) return
        val font = if (bold) {
            PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
        } else {
            PDType1Font(Standard14Fonts.FontName.HELVETICA)
        }
        val maxWidthPt = maxWidthPx * PAGE_WIDTH_PT / REFERENCE_WIDTH_PX
        val fittedSize = fitFontSize(font, cleaned, fontSizePt, maxWidthPt)
        val widthPt = font.getStringWidth(cleaned) / 1000f * fittedSize
        val centerXPt = centerXPx * PAGE_WIDTH_PT / REFERENCE_WIDTH_PX
        val centerYPt = PAGE_HEIGHT_PT - (centerYPx * PAGE_HEIGHT_PT / REFERENCE_HEIGHT_PX)
        beginText()
        setNonStrokingColor(Color.BLACK)
        setFont(font, fittedSize)
        newLineAtOffset(centerXPt - widthPt / 2f, centerYPt - fittedSize * 0.34f)
        showText(cleaned)
        endText()
    }

    private fun fitFontSize(
        font: PDFont,
        text: String,
        preferredSize: Float,
        maxWidthPt: Float,
    ): Float {
        val unitWidth = font.getStringWidth(text) / 1000f
        if (unitWidth <= 0f) return preferredSize
        val fitted = maxWidthPt / unitWidth
        return minOf(preferredSize, fitted).coerceAtLeast(5.2f)
    }
}
