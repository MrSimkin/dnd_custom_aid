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
import java.io.InputStream
import java.io.OutputStream
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode

/**
 * Development renderer used to prove measured overlays against the owner's authoritative Custom
 * v1/v2 PDF pages before the complete export renderer is wired into product UI.
 *
 * It deliberately renders only the selected MAIN page. Other base pages, Extended pages, portraits
 * and App Modified geometry remain outside this visual-proof slice, so this type must not be used as
 * a complete PC export implementation.
 */
private val CUSTOM_APPROVED_TYPOGRAPHY_THEME = PdfTypographyTheme(
    id = "custom-owner-approved-fill",
    resourcesByRole = mapOf(
        PdfTypographyRole.CHARACTER_NAME to "fonts/pdf/text/Kalam-Bold.ttf",
        PdfTypographyRole.HANDWRITTEN_NAME to "fonts/pdf/text/Kalam-Bold.ttf",
        PdfTypographyRole.PRIMARY_VALUE to "fonts/pdf/text/FiraSans-SemiBold.ttf",
        PdfTypographyRole.SECONDARY_VALUE to "fonts/pdf/text/FiraSans-SemiBold.ttf",
        PdfTypographyRole.BODY to "fonts/pdf/text/FiraSans-Regular.ttf",
        PdfTypographyRole.COMPACT_TABLE to "fonts/pdf/text/FiraSans-Regular.ttf",
        PdfTypographyRole.NUMERIC_COMPACT to "fonts/pdf/text/FiraSans-SemiBold.ttf",
        PdfTypographyRole.NOTE_TEXT to "fonts/pdf/text/FiraSans-Regular.ttf",
        PdfTypographyRole.SPELL_NAME to "fonts/pdf/text/FiraSans-Regular.ttf",
        PdfTypographyRole.OPTIONAL_DECORATIVE to "fonts/pdf/text/FiraSans-SemiBold.ttf",
    ),
)

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
                    DesktopPdfFontRegistry(document, CUSTOM_APPROVED_TYPOGRAPHY_THEME, resourceLoader),
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

        fillOnRulePx(stream, primitives, 620f, 260f, 106f, classSummary(plan), 9.5f)
        fillOnRulePx(stream, primitives, 620f, 260f, 146f, sheet.background.race, 9.5f)
        if (closure.progressMode == CharacterProgressMode.EXPERIENCE) {
            fillOnRulePx(stream, primitives, 275f, 185f, 225f, closure.experiencePoints.toString(), 9.5f)
        }
        fillCenteredTextPx(
            stream, primitives, 1042f, 410f, sheet.name,
            13f, 250f, PdfTypographyRole.HANDWRITTEN_NAME,
        )

        fillCenteredTextPx(stream, primitives, 115f, 335f, sheet.armorClass.toString(), 22f, 105f, PdfTypographyRole.PRIMARY_VALUE)
        fillCenteredTextPx(stream, primitives, 399.5f, 276f, signed(sheet.initiativeModifier), 17f, 105f, PdfTypographyRole.PRIMARY_VALUE)
        fillCenteredTextPx(stream, primitives, 541.5f, 276f, signed(sheet.finalProficiencyBonus), 17f, 105f, PdfTypographyRole.PRIMARY_VALUE)
        fillCenteredTextPx(stream, primitives, 682.5f, 276f, sheet.maxHp.toString(), 18f, 105f, PdfTypographyRole.PRIMARY_VALUE)
        fillCenteredTextPx(stream, primitives, 824.5f, 347f, sheet.currentHp.toString(), 23f, 105f, PdfTypographyRole.PRIMARY_VALUE)
        fillCenteredTextPx(stream, primitives, 399.5f, 412f, sheet.speed.toString(), 17f, 105f, PdfTypographyRole.PRIMARY_VALUE)
        if (sheet.inspiration) {
            markerPx(stream, primitives, 541.5f, 412f, 30f, PdfMarkerKind.STAR_FILLED, PdfSymbolFamily.V1_DERIVED)
        }
        fillCenteredTextPx(stream, primitives, 682.5f, 412f, hitDiceSummary(plan), 12f, 108f, PdfTypographyRole.SECONDARY_VALUE)

        val abilityPlacements = listOf(
            Triple(CharacterAbility.STRENGTH, 116f, 170f),
            Triple(CharacterAbility.DEXTERITY, 308.5f, 368.5f),
            Triple(CharacterAbility.CONSTITUTION, 501.5f, 561f),
            Triple(CharacterAbility.INTELLIGENCE, 694.5f, 753.5f),
            Triple(CharacterAbility.WISDOM, 891f, 946.5f),
            Triple(CharacterAbility.CHARISMA, 1079.5f, 1139f),
        )
        abilityPlacements.forEach { (ability, scoreX, modifierX) ->
            fillCenteredTextPx(
                stream, primitives, scoreX, 566.5f,
                sheet.abilityScore(ability).toString(), 19f, 90f, PdfTypographyRole.PRIMARY_VALUE,
            )
            fillCenteredTextPx(
                stream, primitives, modifierX, 586f,
                signed(sheet.abilityModifier(ability)), 11f, 52f, PdfTypographyRole.SECONDARY_VALUE,
            )
        }

        drawCustomV1ChecksAndTotals(stream, primitives, plan)
        drawCustomV1Spellcasting(stream, primitives, plan)
        drawCustomV1Attacks(stream, primitives, plan)
        drawCustomV1Traits(stream, primitives, plan)
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
                markerPx(stream, primitives, column.checkboxX, 632f, 14f, PdfMarkerKind.CHECK, PdfSymbolFamily.V1_DERIVED)
            }
            fillTextPx(stream, primitives, column.valueX, 622f, signed(sheet.savingThrowTotal(column.ability)), 7.8f, 45f)
            column.skills.forEachIndexed { index, skill ->
                val y = 652f + index * 30f
                val state = sheet.skill(skill)
                skillMarkerKind(state.training)?.let { kind ->
                    markerPx(stream, primitives, column.checkboxX, y + 9f, 14f, kind, PdfSymbolFamily.V1_DERIVED)
                }
                fillTextPx(stream, primitives, column.valueX, y, signed(sheet.skillTotal(skill)), 7.8f, 45f)
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
        saveDc?.let {
            fillCenteredTextPx(stream, primitives, 348.5f, 880.5f, it.toString(), 16f, 105f, PdfTypographyRole.PRIMARY_VALUE)
        }
        attack?.let {
            fillCenteredTextPx(stream, primitives, 349.5f, 1031.5f, signed(it), 16f, 105f, PdfTypographyRole.PRIMARY_VALUE)
        }
        ability.takeIf { it.isNotBlank() }?.let {
            fillCenteredTextPx(stream, primitives, 348.5f, 1175f, it, 14f, 105f, PdfTypographyRole.SECONDARY_VALUE)
        }

        val slots = sheet.spellSlots.associateBy { it.level }
        for (level in 1..9) {
            val slot = slots[level] ?: continue
            val rowCenterY = V1_SLOT_CENTER_Y[level - 1]
            fillCenteredTextPx(
                stream, primitives, 125f, rowCenterY,
                slot.totalSlots.toString(), 10.5f, 50f, PdfTypographyRole.NUMERIC_COMPACT,
            )
            // Owner semantic: ESPACIOS GASTADOS remains empty for paper tracking.
        }
    }

    private fun drawCustomV1Attacks(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        plan.snapshot.aggregate.sheet.combatEntries
            .sortedBy { it.sortOrder }
            .take(V1_ATTACK_RULE_Y.size)
            .forEachIndexed { index, entry ->
                val ruleY = V1_ATTACK_RULE_Y[index]
                fillOnRulePx(stream, primitives, 440f, 260f, ruleY, entry.name, 7.8f)
                fillOnRulePx(stream, primitives, 718f, 105f, ruleY, entry.rangeText.orEmpty(), 7.4f)
                fillOnRulePx(stream, primitives, 840f, 90f, ruleY, entry.attackModifier?.let(::signed).orEmpty(), 7.8f)
                fillOnRulePx(stream, primitives, 950f, 205f, ruleY, entry.damageEffect, 7.4f)
            }
    }

    private fun drawCustomV1Traits(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        plan.snapshot.aggregate.sheet.traits
            .sortedBy { it.sortOrder }
            .take(V1_TRAIT_RULE_Y.size * 3)
            .forEachIndexed { index, trait ->
                val column = index % 3
                val row = index / 3
                fillOnRulePx(
                    stream, primitives,
                    52f + column * 373f,
                    325f,
                    V1_TRAIT_RULE_Y[row],
                    trait.name,
                    7.4f,
                )
            }
    }

private fun drawCustomV2PerAttribute(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        drawCustomV2Common(stream, primitives, plan, nameCenterY = 291f, pageTwoVariant = false)
        val sheet = plan.snapshot.aggregate.sheet

        val scoreRows = listOf(
            Triple(CharacterAbility.STRENGTH, 271f, 300.5f),
            Triple(CharacterAbility.DEXTERITY, 431f, 460.5f),
            Triple(CharacterAbility.CONSTITUTION, 591f, 620.5f),
            Triple(CharacterAbility.INTELLIGENCE, 750f, 779.5f),
            Triple(CharacterAbility.WISDOM, 910f, 939.5f),
            Triple(CharacterAbility.CHARISMA, 1069f, 1099.5f),
        )
        scoreRows.forEach { (ability, scoreY, modifierY) ->
            fillCenteredTextPx(stream, primitives, 86.5f, scoreY, sheet.abilityScore(ability).toString(), 17f, 117f, PdfTypographyRole.PRIMARY_VALUE, -1f)
            fillCenteredTextPx(stream, primitives, 143f, modifierY, signed(sheet.abilityModifier(ability)), 15.5f, 88f, PdfTypographyRole.SECONDARY_VALUE, -0.4f)
        }

        val rows = listOf(
            V2ExactRow(CharacterAbility.STRENGTH, null, 98.5f, 125f, 8.5f, 9f, 159f, 178.5f, 134.5f),
            V2ExactRow(CharacterAbility.STRENGTH, SkillKey.ATHLETICS, 98.5f, 137f, 8.5f, 9f, 159f, 178.5f, 146f),

            V2ExactRow(CharacterAbility.DEXTERITY, null, 98.5f, 193.5f, 8.5f, 9f, 159f, 178.5f, 203f),
            V2ExactRow(CharacterAbility.DEXTERITY, SkillKey.ACROBATICS, 98.5f, 205f, 8.5f, 9f, 159f, 178.5f, 214.5f),
            V2ExactRow(CharacterAbility.DEXTERITY, SkillKey.SLEIGHT_OF_HAND, 98.5f, 216.5f, 8.5f, 9f, 159f, 178.5f, 226f),
            V2ExactRow(CharacterAbility.DEXTERITY, SkillKey.STEALTH, 98.5f, 228f, 8.5f, 9f, 159f, 178.5f, 237.5f),

            V2ExactRow(CharacterAbility.CONSTITUTION, null, 98.5f, 290.5f, 8.5f, 9f, 159f, 178.5f, 300f),

            V2ExactRow(CharacterAbility.INTELLIGENCE, null, 98.5f, 342f, 8.5f, 9f, 159f, 178.5f, 351.5f),
            V2ExactRow(CharacterAbility.INTELLIGENCE, SkillKey.ARCANA, 98.5f, 353.5f, 8.5f, 9f, 159f, 178.5f, 363f),
            V2ExactRow(CharacterAbility.INTELLIGENCE, SkillKey.HISTORY, 98.5f, 365f, 8.5f, 9f, 159f, 178.5f, 374f),
            V2ExactRow(CharacterAbility.INTELLIGENCE, SkillKey.INVESTIGATION, 98.5f, 376f, 8.5f, 9f, 159f, 178.5f, 385.5f),
            V2ExactRow(CharacterAbility.INTELLIGENCE, SkillKey.NATURE, 98.5f, 387.5f, 8.5f, 9f, 159f, 178.5f, 397f),
            V2ExactRow(CharacterAbility.INTELLIGENCE, SkillKey.RELIGION, 98.5f, 399f, 8.5f, 9f, 159f, 178.5f, 408.5f),

            V2ExactRow(CharacterAbility.WISDOM, null, 98.5f, 422f, 8.5f, 9f, 159f, 178.5f, 431.5f),
            V2ExactRow(CharacterAbility.WISDOM, SkillKey.MEDICINE, 98.5f, 434f, 8.5f, 9f, 159f, 178.5f, 442.5f),
            V2ExactRow(CharacterAbility.WISDOM, SkillKey.PERCEPTION, 98.5f, 444.5f, 8.5f, 9f, 159f, 178.5f, 454f),
            V2ExactRow(CharacterAbility.WISDOM, SkillKey.INSIGHT, 98.5f, 456f, 8.5f, 9f, 159f, 178.5f, 465.5f),
            V2ExactRow(CharacterAbility.WISDOM, SkillKey.SURVIVAL, 98.5f, 468f, 8.5f, 9f, 159f, 178.5f, 477f),
            V2ExactRow(CharacterAbility.WISDOM, SkillKey.ANIMAL_HANDLING, 98.5f, 479f, 8.5f, 9f, 159f, 178.5f, 488.5f),

            V2ExactRow(CharacterAbility.CHARISMA, null, 98.5f, 507.5f, 8.5f, 9f, 159f, 178.5f, 517f),
            V2ExactRow(CharacterAbility.CHARISMA, SkillKey.DECEPTION, 98.5f, 519f, 8.5f, 9f, 159f, 178.5f, 528f),
            V2ExactRow(CharacterAbility.CHARISMA, SkillKey.PERFORMANCE, 98.5f, 530f, 8.5f, 9f, 159f, 178.5f, 539.5f),
            V2ExactRow(CharacterAbility.CHARISMA, SkillKey.INTIMIDATION, 98.5f, 541.5f, 8.5f, 9f, 159f, 178.5f, 551f),
            V2ExactRow(CharacterAbility.CHARISMA, SkillKey.PERSUASION, 98.5f, 553f, 8.5f, 9f, 159f, 178.5f, 562.5f),
        )
        rows.forEach { row ->
            val value = if (row.skill == null) {
                val save = sheet.savingThrow(row.ability)
                if (save.proficient) {
                    markerTopRectPt(stream, primitives, row.checkX, row.checkTop, row.checkW, row.checkH, PdfMarkerKind.CHECK, 1.7f, -0.6f)
                }
                signed(sheet.savingThrowTotal(row.ability))
            } else {
                val skill = sheet.skill(row.skill)
                skillMarkerKind(skill.training)?.let { kind ->
                    markerTopRectPt(stream, primitives, row.checkX, row.checkTop, row.checkW, row.checkH, kind, if (kind == PdfMarkerKind.DOUBLE_CHECK) 2f else 1.7f, -0.6f)
                }
                signed(sheet.skillTotal(row.skill))
            }
            fillCenteredAboveRulePt(stream, primitives, row.ruleStartX, row.ruleEndX, row.ruleTop, value, 8.8f)
        }
    }

private fun drawCustomV2PerAbility(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        drawCustomV2Common(stream, primitives, plan, nameCenterY = 376f, pageTwoVariant = true)
        val sheet = plan.snapshot.aggregate.sheet

        val scoreRows = listOf(
            Triple(CharacterAbility.STRENGTH, 362f, 395.5f),
            Triple(CharacterAbility.DEXTERITY, 505f, 538.5f),
            Triple(CharacterAbility.CONSTITUTION, 650f, 683f),
            Triple(CharacterAbility.INTELLIGENCE, 794f, 828f),
            Triple(CharacterAbility.WISDOM, 939f, 972.5f),
            Triple(CharacterAbility.CHARISMA, 1083.5f, 1117f),
        )
        scoreRows.forEach { (ability, scoreY, modifierY) ->
            fillCenteredTextPx(stream, primitives, 86.5f, scoreY, sheet.abilityScore(ability).toString(), 17f, 117f, PdfTypographyRole.PRIMARY_VALUE, -1f)
            fillCenteredTextPx(stream, primitives, 143f, modifierY, signed(sheet.abilityModifier(ability)), 15.5f, 88f, PdfTypographyRole.SECONDARY_VALUE, -0.4f)
        }

        val saveTops = listOf(171.5f, 186.5f, 201f, 216f, 230.5f, 245.5f)
        val saveRules = listOf(183f, 198f, 212.5f, 227.5f, 242f, 257f)
        CharacterAbility.entries.forEachIndexed { index, ability ->
            val save = sheet.savingThrow(ability)
            if (save.proficient) {
                markerTopRectPt(stream, primitives, 98f, saveTops[index], 8.5f, 9f, PdfMarkerKind.CHECK, 1.7f, -0.6f)
            }
            fillCenteredAboveRulePt(stream, primitives, 161.5f, 178f, saveRules[index], signed(sheet.savingThrowTotal(ability)), 8.8f)
        }

        val skillTops = listOf(
            302f, 317f, 332f, 347f, 362f, 377f, 392f, 407f, 421.5f,
            436.5f, 451.5f, 466.5f, 481.5f, 496.5f, 511.5f, 526.5f, 541.5f, 556.5f,
        )
        val skillRules = listOf(
            313.5f, 328.5f, 343.5f, 358.5f, 373.5f, 388.5f, 403.5f, 418.5f, 433f,
            448f, 463f, 478f, 493f, 508f, 523f, 538f, 553f, 568f,
        )
        V2_PER_ABILITY_SKILL_ORDER.forEachIndexed { index, skillKey ->
            val skill = sheet.skill(skillKey)
            skillMarkerKind(skill.training)?.let { kind ->
                markerTopRectPt(stream, primitives, 98f, skillTops[index], 8.5f, 9f, kind, if (kind == PdfMarkerKind.DOUBLE_CHECK) 2f else 1.7f, -0.6f)
            }
            fillCenteredAboveRulePt(stream, primitives, 161.5f, 178f, skillRules[index], signed(sheet.skillTotal(skillKey)), 8.8f)
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

        fillOnRulePx(stream, primitives, 885f, 311f, 93f, classSummary(plan), 10.5f)
        fillOnRulePx(stream, primitives, 777f, 419f, 135f, sheet.background.race, 10.5f)
        fillCenteredTextPx(
            stream, primitives, 534f, nameCenterY, sheet.name,
            13.5f, 248f, PdfTypographyRole.HANDWRITTEN_NAME, -0.4f,
        )

        fillCenteredTextPx(stream, primitives, 771f, 237f, sheet.armorClass.toString(), 20f, 106f, PdfTypographyRole.PRIMARY_VALUE, -1.6f)
        fillCenteredTextPx(stream, primitives, 958f, 225f, hitDiceSummary(plan), 10.5f, 136f, PdfTypographyRole.SECONDARY_VALUE, -0.4f)
        fillCenteredTextPx(stream, primitives, 1123f, 225f, sheet.maxHp.toString(), 16f, 126f, PdfTypographyRole.PRIMARY_VALUE, -0.8f)
        fillCenteredTextPx(stream, primitives, 771f, 322f, signed(sheet.initiativeModifier), 15f, 106f, PdfTypographyRole.PRIMARY_VALUE, -0.8f)
        fillCenteredTextPx(stream, primitives, 1037f, 352f, sheet.currentHp.toString(), 22f, 298f, PdfTypographyRole.PRIMARY_VALUE, -1.4f)
        fillCenteredTextPx(stream, primitives, 771f, 399f, sheet.speed.toString(), 15f, 106f, PdfTypographyRole.PRIMARY_VALUE, -0.8f)

        if (pageTwoVariant) {
            fillCenteredTextPx(stream, primitives, 97f, 223f, signed(sheet.finalProficiencyBonus), 14f, 156f, PdfTypographyRole.PRIMARY_VALUE, -0.6f)
            if (sheet.inspiration) {
                markerTopRectPt(stream, primitives, 111f, 102.5f, 18f, 18f, PdfMarkerKind.STAR_FILLED, 0f, 0f)
            }
        } else {
            fillCenteredTextPx(stream, primitives, 437f, 410f, signed(sheet.finalProficiencyBonus), 14f, 118f, PdfTypographyRole.PRIMARY_VALUE, -0.6f)
            if (sheet.inspiration) {
                markerTopRectPt(stream, primitives, 281f, 196f, 18f, 18f, PdfMarkerKind.STAR_FILLED, 0f, 0f)
            }
        }

        sheet.combatEntries
            .sortedBy { it.sortOrder }
            .take(V2_ATTACK_RULE_Y.size)
            .forEachIndexed { index, entry ->
                val ruleY = V2_ATTACK_RULE_Y[index]
                val name = buildString {
                    append(entry.name)
                    entry.rangeText?.takeIf { it.isNotBlank() }?.let { append(" (").append(it).append(")") }
                }
                fillOnRulePx(stream, primitives, 369f, 447f, ruleY, name, 9.25f)
                fillCenteredAboveRulePt(stream, primitives, 411.5f, 453.5f, ruleY / 2f, entry.attackModifier?.let(::signed).orEmpty(), 9f)
                fillOnRulePx(stream, primitives, 913f, 283f, ruleY, entry.damageEffect, 9f)
            }

        sheet.traits
            .sortedBy { it.sortOrder }
            .take(V2_TRAIT_RULE_Y.size * 2)
            .forEachIndexed { index, trait ->
                val column = index % 2
                val row = index / 2
                fillOnRulePx(
                    stream, primitives,
                    if (column == 0) 369f else 789f,
                    407f,
                    V2_TRAIT_RULE_Y[row],
                    trait.name,
                    9.25f,
                )
            }

        val (saveDc, attack, ability) = spellcastingSummary(plan)
        saveDc?.let {
            fillCenteredTextPx(stream, primitives, 280f, 1229f, it.toString(), 15f, 88f, PdfTypographyRole.PRIMARY_VALUE, -0.6f)
        }
        attack?.let {
            fillCenteredTextPx(stream, primitives, 280f, 1349f, signed(it), 15f, 88f, PdfTypographyRole.PRIMARY_VALUE, -0.6f)
        }
        ability.takeIf { it.isNotBlank() }?.let {
            fillCenteredTextPx(stream, primitives, 280f, 1467f, it, 13.5f, 88f, PdfTypographyRole.SECONDARY_VALUE, -0.6f)
        }

        val slots = sheet.spellSlots.associateBy { it.level }
        for (level in 1..9) {
            val slot = slots[level] ?: continue
            fillCenteredAboveRulePt(
                stream, primitives, 28.5f, 56.5f,
                627.5f + (level - 1) * 17f,
                slot.totalSlots.toString(), 12f,
            )
        }

        drawCustomV2TreasureAndResources(stream, primitives, plan)
    }

    private fun drawCustomV2TreasureAndResources(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val currencies = sheet.currencies.associateBy { it.key.lowercase() }
        val treasureKeys = listOf("pt", "po", "pp", "pc")
        treasureKeys.forEachIndexed { index, key ->
            currencies[key]?.let { currency ->
                fillCenteredTextPx(
                    stream, primitives,
                    579f, V2_TREASURE_RULE_Y[index] - 17f,
                    currency.amount.toString(), 9.5f, 105f, PdfTypographyRole.NUMERIC_COMPACT,
                )
            }
        }

        sheet.inventoryItems.filterNot { it.special }
            .take(V2_OBJECT_RULE_Y.size)
            .forEachIndexed { index, item ->
                fillOnRulePx(stream, primitives, 370f, 300f, V2_OBJECT_RULE_Y[index], item.name, 7.2f)
            }

        // Preserve the source ammunition grid blank for paper use. Real ammunition records are
        // character data and are carried through the Inventory continuation when configured.
        sheet.inventoryItems.filter { it.special }
            .take(V2_OTHER_RULE_Y.size)
            .forEachIndexed { index, item ->
                fillOnRulePx(stream, primitives, 700f, 355f, V2_OTHER_RULE_Y[index], item.name, 7.2f)
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


    private fun fillCenteredAboveRulePt(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        startXPt: Float,
        endXPt: Float,
        ruleTopPt: Float,
        text: String,
        fontSizePt: Float,
    ) {
        if (text.isBlank()) return
        fillCenteredTextPx(
            stream = stream,
            primitives = primitives,
            centerXPx = (startXPt + endXPt),
            centerYPx = ruleTopPt * 2f - 13f,
            text = text,
            fontSizePt = fontSizePt,
            maxWidthPx = (endXPt - startXPt) * 2f,
            role = PdfTypographyRole.SECONDARY_VALUE,
        )
    }

    private fun markerTopRectPt(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        xPt: Float,
        topPt: Float,
        widthPt: Float,
        heightPt: Float,
        kind: PdfMarkerKind,
        opticalXPt: Float,
        opticalYPt: Float,
    ) {
        markerPx(
            stream,
            primitives,
            (xPt + widthPt / 2f + opticalXPt) * 2f,
            (topPt + heightPt / 2f - opticalYPt) * 2f,
            minOf(widthPt, heightPt) * 2f,
            kind,
            PdfSymbolFamily.V3_DERIVED,
        )
    }

    private fun signed(value: Int): String = if (value >= 0) "+$value" else value.toString()

    private data class V1AbilityColumn(
        val ability: CharacterAbility,
        val checkboxX: Float,
        val valueX: Float,
        val skills: List<SkillKey>,
    )


    private data class V2ExactRow(
        val ability: CharacterAbility,
        val skill: SkillKey?,
        val checkX: Float,
        val checkTop: Float,
        val checkW: Float,
        val checkH: Float,
        val ruleStartX: Float,
        val ruleEndX: Float,
        val ruleTop: Float,
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

        val V1_ATTACK_RULE_Y = listOf(888f, 928f, 968f, 1007f, 1047f, 1087f, 1126f, 1166f)
        val V1_SLOT_CENTER_Y = listOf(901.5f, 941.5f, 980.5f, 1020.5f, 1060.5f, 1099.5f, 1139.5f, 1179.5f, 1218.5f)
        val V1_SLOT_SPENT_CENTER_X = listOf(175.5f, 205f, 234f, 263.5f)
        val V1_TRAIT_RULE_Y = listOf(1323f, 1363f, 1402f, 1442f, 1482f, 1521f)

        val V2_ATTACK_RULE_Y = listOf(529f, 563f, 597f, 631f, 665f, 699f, 733f, 767f)
        const val V2_SLOT_FIRST_CENTER_Y = 1239.5f
        const val V2_SLOT_ROW_STEP = 34f
        val V2_SLOT_SPENT_CENTER_X = listOf(127.5f, 153.5f, 180f, 206.5f)
        val V2_TRAIT_RULE_Y = listOf(875f, 909f, 943f, 977f, 1011f, 1045f, 1079f, 1113f, 1147f)
        val V2_TREASURE_RULE_Y = listOf(1255f, 1289f, 1323f, 1357f)
        val V2_OBJECT_RULE_Y = listOf(1425f, 1459f, 1493f, 1527f)
        val V2_OTHER_RULE_Y = listOf(1425f, 1459f, 1493f, 1527f)

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

    private fun fillTextPx(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        xPx: Float,
        yPx: Float,
        text: String,
        fontSizePt: Float,
        maxWidthPx: Float,
        role: PdfTypographyRole = PdfTypographyRole.BODY,
    ) {
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = rectPx(xPx, yPx - 5f, maxWidthPx, 30f),
                text = text,
                role = role,
                preferredSizePt = fontSizePt,
                minimumSizePt = 5.8f,
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.CENTER,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
                horizontalPaddingPt = 1f,
                verticalPaddingPt = 0f,
            ),
        )
    }

    private fun fillCenteredTextPx(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        centerXPx: Float,
        centerYPx: Float,
        text: String,
        fontSizePt: Float,
        maxWidthPx: Float,
        role: PdfTypographyRole,
        opticalYOffsetPx: Float = 0f,
    ) {
        val heightPx = maxOf(34f, fontSizePt * 3.1f)
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = rectPx(
                    centerXPx - maxWidthPx / 2f,
                    centerYPx + opticalYOffsetPx - heightPx / 2f,
                    maxWidthPx,
                    heightPx,
                ),
                text = text,
                role = role,
                preferredSizePt = fontSizePt,
                minimumSizePt = 6f,
                horizontalAlignment = PdfHorizontalAlignment.CENTER,
                verticalAlignment = PdfVerticalAlignment.CENTER,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
                horizontalPaddingPt = 1f,
                verticalPaddingPt = 0f,
            ),
        )
    }

    private fun fillOnRulePx(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        xPx: Float,
        widthPx: Float,
        ruleYPx: Float,
        text: String,
        fontSizePt: Float,
    ) {
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = rectPx(xPx, ruleYPx - 29f, widthPx, 26f),
                text = text,
                role = PdfTypographyRole.COMPACT_TABLE,
                preferredSizePt = fontSizePt,
                minimumSizePt = 5.8f,
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.BOTTOM,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
                horizontalPaddingPt = 1f,
                verticalPaddingPt = 0.5f,
            ),
        )
    }

    private fun rectPx(
        xPx: Float,
        topYPx: Float,
        widthPx: Float,
        heightPx: Float,
    ): PdfRect = PdfRect(
        x = xPx * PAGE_WIDTH_PT / REFERENCE_WIDTH_PX,
        y = PAGE_HEIGHT_PT - (topYPx + heightPx) * PAGE_HEIGHT_PT / REFERENCE_HEIGHT_PX,
        width = widthPx * PAGE_WIDTH_PT / REFERENCE_WIDTH_PX,
        height = heightPx * PAGE_HEIGHT_PT / REFERENCE_HEIGHT_PX,
    )

}
