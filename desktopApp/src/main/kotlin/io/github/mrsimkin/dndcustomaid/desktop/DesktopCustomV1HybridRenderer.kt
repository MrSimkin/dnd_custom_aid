package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBasePageRole
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.spellAttackModifier
import io.github.mrsimkin.dndcustomaid.shared.character.spellSaveDc
import java.awt.Color
import java.awt.geom.AffineTransform
import java.io.InputStream
import kotlin.math.abs
import org.apache.pdfbox.multipdf.LayerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDFormContentStream
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.util.Matrix

/**
 * Production-candidate Custom v1 Hybrid overlay.
 *
 * This promotes the owner-approved Run-6 visual mechanics out of QA fixtures. It intentionally
 * renders only fields whose product semantics are available in PcSheetPdfRenderPlan and whose
 * geometry was proven by the Run-6 baseline. Unsupported values stay blank rather than being
 * inferred (for example alignment, AC component breakdown, or next-level XP threshold).
 */
internal class DesktopCustomV1HybridRenderer(
    private val document: PDDocument,
    private val resourceLoader: (String) -> InputStream?,
) {
    private val fonts = Fonts(document, resourceLoader)
    private val layers = LayerUtility(document)

    fun render(
        page: PDPage,
        role: PcSheetBasePageRole,
        plan: PcSheetPdfRenderPlan,
    ) {
        when (role) {
            PcSheetBasePageRole.MAIN -> renderMain(page, plan)
            PcSheetBasePageRole.NARRATIVE -> renderNarrative(page, plan)
            PcSheetBasePageRole.SPELL_LIST -> renderApprovedSpellSlice(page, plan)
            else -> Unit
        }
    }

    private fun renderMain(page: PDPage, plan: PcSheetPdfRenderPlan) {
        appendSection(page, "CustomV1 MAIN - Identification") { drawIdentification(it, plan) }
        appendSection(page, "CustomV1 MAIN - Defense") { drawDefense(it, plan) }
        appendSection(page, "CustomV1 MAIN - Core Stats") { drawCoreStats(it, plan) }
        appendSection(page, "CustomV1 MAIN - Abilities") { drawAbilities(it, plan) }

        SKILL_SECTIONS.forEach { section ->
            appendSection(page, "CustomV1 MAIN - Skills ${section.name}") {
                drawSkillSection(it, plan, section)
            }
        }

        appendSection(page, "CustomV1 MAIN - Spellcasting Summary") { drawSpellcastingSummary(it, plan) }
        appendSection(page, "CustomV1 MAIN - Attacks") { drawAttacks(it, plan) }
        appendSection(page, "CustomV1 MAIN - Traits rows 1-2") { drawTraits(it, plan) }
    }

    private fun renderNarrative(page: PDPage, plan: PcSheetPdfRenderPlan) {
        appendSection(page, "CustomV1 PAGE3 - Background fields") { drawBackgroundFields(it, plan) }
        appendSection(page, "CustomV1 PAGE3 - Story") { drawStory(it, plan) }
    }

    private fun renderApprovedSpellSlice(page: PDPage, plan: PcSheetPdfRenderPlan) {
        appendSection(page, "CustomV1 SPELLS - Cantrips") { drawCantrips(it, plan) }
        appendSection(page, "CustomV1 SPELLS - Level 1") { drawLevelOneSpells(it, plan) }
    }

    private fun appendSection(
        page: PDPage,
        layerName: String,
        draw: (PDFormContentStream) -> Unit,
    ) {
        val form = org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject(document).apply {
            resources = PDResources()
            setBBox(PDRectangle(page.cropBox.width, page.cropBox.height))
        }
        PDFormContentStream(form).use { stream ->
            stream.setNonStrokingColor(Color.BLACK)
            draw(stream)
        }
        layers.appendFormAsLayer(page, form, AffineTransform(), layerName)
    }

    private fun drawIdentification(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet

        val classes = sheet.classes.sortedBy { it.sortOrder }
            .joinToString(" / ") { "${it.name} ${it.level}" }
        if (classes.isNotBlank()) {
            textAboveRule(s, fonts.handwritten, IDENT_CLASS_RULE, classes, 10.5f, 9f, 2.2f, 2f)
        }
        sheet.background.race.trim().takeIf { it.isNotEmpty() }?.let {
            textAboveRule(s, fonts.handwritten, IDENT_RACE_RULE, it, 10.5f, 9f, 2.2f, 2f)
        }

        if (aggregate.closure.progressMode == CharacterProgressMode.EXPERIENCE) {
            centeredAboveRule(
                s,
                fonts.handwritten,
                IDENT_XP_RULE,
                formatInteger(aggregate.closure.experiencePoints),
                10.5f,
                2.2f,
            )
        }

        // Alignment and next-level XP threshold are not represented by the current product model.
        centered(s, fonts.handwritten, PORTRAIT_NAME_RECT, sheet.name, 14f, 0f)
    }

    private fun drawDefense(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val sheet = plan.snapshot.aggregate.sheet
        centered(s, fonts.semibold, DEFENSE_AC_RECT, sheet.armorClass.toString(), 22f, -1f)
        centeredAboveRule(
            s,
            fonts.semibold,
            DEFENSE_DEX_RULE,
            signed(sheet.abilityModifier(CharacterAbility.DEXTERITY)),
            10.5f,
            2.5f,
        )
        // Armor / shield / misc AC decomposition is not currently stored; those boxes remain blank.
    }

    private fun drawCoreStats(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val sheet = plan.snapshot.aggregate.sheet
        centered(s, fonts.semibold, CORE_INIT_RECT, signed(sheet.initiativeModifier), 16f, -0.4f)
        centered(s, fonts.semibold, CORE_PROF_RECT, signed(sheet.finalProficiencyBonus), 16f, -0.4f)
        centered(s, fonts.semibold, CORE_MAX_HP_RECT, sheet.maxHp.toString(), 17f, -0.4f)
        centered(s, fonts.semibold, CORE_CURRENT_HP_RECT, sheet.currentHp.toString(), 21f, -0.7f)
        centered(s, fonts.semibold, CORE_SPEED_RECT, sheet.speed.toString(), 16f, -0.4f)

        val hitDice = sheet.classes.sortedBy { it.sortOrder }
            .joinToString(" / ") { "${it.hitDiceRemaining}d${it.hitDieSides}" }
        if (hitDice.isNotBlank()) {
            centered(s, fonts.semibold, CORE_HIT_DICE_RECT, hitDice, 10.5f, -0.2f)
        }
        // Inspiration is intentionally deferred: it was not included in the approved Run-6 baseline.
    }

    private fun drawAbilities(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val sheet = plan.snapshot.aggregate.sheet
        ABILITIES.forEach { placement ->
            centered(
                s,
                fonts.semibold,
                TopRect(placement.scoreX - 28f, 265.5f, 56f, 31f),
                sheet.abilityScore(placement.ability).toString(),
                18f,
                -0.5f,
            )
            centered(
                s,
                fonts.semibold,
                TopRect(placement.modX - 16f, 285f, 32f, 20f),
                signed(sheet.abilityModifier(placement.ability)),
                11.5f,
                -0.2f,
            )
        }
    }

    private fun drawSkillSection(
        s: PDFormContentStream,
        plan: PcSheetPdfRenderPlan,
        section: SkillSection,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        section.rows.forEach { row ->
            val state = sheet.skill(row.skill)
            markerCodePoint(state)?.let { cp ->
                glyphInRect(s, fonts.symbol, cp, row.checkbox, 0.6f, 0.6f, opticalX = 0.65f)
            }
            centeredAboveRule(
                s,
                fonts.semibold,
                row.valueRule,
                signed(sheet.skillTotal(row.skill)),
                8.8f,
                2.0f,
            )
        }
    }

    private fun drawSpellcastingSummary(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val (saveDc, attack, ability) = spellcastingSummary(plan)
        saveDc?.let { centered(s, fonts.semibold, SPELL_SAVE_RECT, it.toString(), 16f, -0.5f) }
        attack?.let { centered(s, fonts.semibold, SPELL_ATTACK_RECT, signed(it), 16f, -0.5f) }
        ability.takeIf { it.isNotBlank() }?.let {
            centered(s, fonts.semibold, SPELL_ABILITY_RECT, it, 14f, -0.4f)
        }
    }

    private fun drawAttacks(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        plan.snapshot.aggregate.sheet.combatEntries
            .sortedBy { it.sortOrder }
            .take(ATTACK_RULE_Y.size)
            .forEachIndexed { index, entry ->
                val y = ATTACK_RULE_Y[index]
                textAboveRule(s, fonts.regular, Rule(215.291f, 354.189f, y), entry.name, 9.25f, 8.5f, 2.5f, 2.2f)
                textAboveRule(s, fonts.regular, Rule(357.024f, 413.717f, y), entry.rangeText.orEmpty(), 9f, 8.5f, 2.5f, 1.5f)
                textAboveRule(s, fonts.regular, Rule(416.551f, 461.905f, y), entry.attackModifier?.let(::signed).orEmpty(), 9f, 8.5f, 2.5f, 1.5f)
                textAboveRule(s, fonts.regular, Rule(460.968f, 583.803f, y), entry.damageEffect, 9f, 8.5f, 2.5f, 2f)
            }
    }

    private fun drawTraits(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val traits = plan.snapshot.aggregate.sheet.traits.sortedBy { it.sortOrder }.take(6)
        traits.forEachIndexed { index, trait ->
            val row = index / 3
            val column = index % 3
            val rule = TRAIT_COLUMNS[column].copy(topY = TRAIT_ROW_Y[row])
            textAboveRule(s, fonts.regular, rule, trait.name, 9.25f, 8.5f, 2.5f, 2f)
        }
    }

    private fun drawBackgroundFields(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val background = plan.snapshot.aggregate.sheet.background
        listOf(
            BACKGROUND_RULE to background.name,
            IDEALS_RULE to background.ideals,
            BONDS_RULE to background.bonds,
            FLAWS_RULE to background.flaws,
        ).forEach { (rule, value) ->
            value.trim().takeIf { it.isNotEmpty() }?.let {
                textAboveRule(s, fonts.regular, rule, it, 9.25f, 8.5f, 2.6f, 2f)
            }
        }
    }

    private fun drawStory(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val story = plan.snapshot.aggregate.sheet.background.story.trim()
        if (story.isEmpty()) return
        val lines = wrapByWidth(fonts.regular, story, 9.25f, 365f)
        STORY_RULE_Y.zip(lines.take(STORY_RULE_Y.size)).forEach { (y, line) ->
            textAboveRule(s, fonts.regular, Rule(215.291f, 583.795f, y), line, 9.25f, 9f, 2.8f, 2f)
        }
    }

    private fun drawCantrips(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val spells = spellsAtLevel(plan, 0).take(CANTRIP_RULE_Y.size)
        CANTRIP_RULE_Y.zip(spells).forEach { (ruleY, spell) ->
            // Owner semantic: cantrips are always prepared, therefore no preparation mark is drawn.
            textAboveRule(s, fonts.regular, Rule(39.5f, 203.95f, ruleY), spell.name, 9.25f, 8.75f, 3.2f, 2f)
        }
    }

    private fun drawLevelOneSpells(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val sheet = plan.snapshot.aggregate.sheet
        sheet.spellSlots.firstOrNull { it.level == 1 }?.let {
            centered(s, fonts.semibold, LEVEL1_SLOT_TOTAL_RECT, it.totalSlots.toString(), 15f, -0.35f)
        }

        val spells = spellsAtLevel(plan, 1).take(LEVEL1_SQUARES.size)
        LEVEL1_SQUARES.zip(spells).forEach { (square, spell) ->
            if (spell.sourceAssociations.any { it.prepared }) {
                glyphInRect(s, fonts.symbol, CHECK_CP, square, 0.6f, 0.6f, opticalX = 0.65f)
            }
            textAboveRule(
                s,
                fonts.regular,
                Rule(39.543f, 203.952f, square.top + square.height),
                spell.name,
                9.25f,
                8.75f,
                3.4f,
                2f,
            )
        }
        // Owner semantic: ESPACIOS GASTADOS remains empty.
    }

    private fun spellsAtLevel(plan: PcSheetPdfRenderPlan, level: Int): List<CharacterSpell> =
        plan.snapshot.aggregate.sheet.spells
            .filter { it.level == level }
            .sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.name.lowercase() })

    private fun spellcastingSummary(plan: PcSheetPdfRenderPlan): Triple<Int?, Int?, String> {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val successor = aggregate.successor
        val profile = successor.spellcastingProfiles.firstOrNull()
        if (profile != null) {
            return Triple(
                sheet.spellSaveDc(profile, successor),
                sheet.spellAttackModifier(profile, successor),
                abilityLabel(profile.ability, plan),
            )
        }

        val legacy = when (sheet.spellcastingAbility.name) {
            "STRENGTH" -> "FUE"
            "DEXTERITY" -> "DES"
            "CONSTITUTION" -> "CON"
            "INTELLIGENCE" -> "INT"
            "WISDOM" -> "SAB"
            "CHARISMA" -> "CAR"
            else -> ""
        }
        return Triple(sheet.spellSaveDc, sheet.spellAttackModifier, legacy)
    }

    private fun abilityLabel(reference: CharacterAbilityReference, plan: PcSheetPdfRenderPlan): String =
        when (reference.builtIn) {
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

    private fun markerCodePoint(skill: CharacterSkill): Int? = when (skill.training) {
        SkillTraining.NONE -> null
        SkillTraining.PROFICIENT -> CHECK_CP
        SkillTraining.EXPERTISE -> DOUBLE_CHECK_CP
    }

    private fun textAboveRule(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        preferredSize: Float,
        minimumSize: Float,
        clearance: Float,
        leftPadding: Float = 0f,
    ) {
        if (text.isBlank()) return
        val available = rule.endX - rule.startX - leftPadding - 1f
        var size = preferredSize
        while (size > minimumSize && textWidth(font, text, size) > available) size -= 0.25f
        if (textWidth(font, text, size) > available + 0.05f) return

        val descent = requireNotNull(font.fontDescriptor).descent / 1000f * size
        val baseline = PAGE_HEIGHT - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rule.startX + leftPadding, baseline)
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
        if (text.isBlank()) return
        val width = textWidth(font, text, size)
        val descent = requireNotNull(font.fontDescriptor).descent / 1000f * size
        val baseline = PAGE_HEIGHT - rule.topY + clearance - descent
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
        opticalY: Float,
    ) {
        if (text.isBlank()) return
        val width = textWidth(font, text, size)
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        val descent = abs(font.fontDescriptor?.descent?.takeIf { it < 0 } ?: -250f) / 1000f * size
        val bottom = PAGE_HEIGHT - (rect.top + rect.height)
        val baseline = bottom + (rect.height - ascent - descent) / 2f + descent + opticalY

        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rect.x + (rect.width - width) / 2f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun glyphInRect(
        s: PDFormContentStream,
        font: PDFont,
        codePoint: Int,
        rect: TopRect,
        insetX: Float,
        insetY: Float,
        opticalX: Float = 0f,
        opticalY: Float = 0f,
    ) {
        val glyph = String(Character.toChars(codePoint))
        val normalizedWidth = font.getStringWidth(glyph) / 1000f
        val descriptor = requireNotNull(font.fontDescriptor)
        val normalizedAscent = descriptor.ascent / 1000f
        val normalizedDescent = descriptor.descent / 1000f
        val normalizedHeight = normalizedAscent - normalizedDescent
        if (normalizedWidth <= 0f || normalizedHeight <= 0f) return

        val scaleX = (rect.width - insetX * 2f) / normalizedWidth
        val scaleY = (rect.height - insetY * 2f) / normalizedHeight
        val left = rect.x + insetX + opticalX
        val targetBottom = PAGE_HEIGHT - (rect.top + rect.height) + insetY + opticalY
        val baseline = targetBottom - normalizedDescent * scaleY

        s.beginText()
        s.setFont(font, 1f)
        s.setTextMatrix(Matrix(scaleX, 0f, 0f, scaleY, left, baseline))
        s.showText(glyph)
        s.endText()
    }

    private fun wrapByWidth(font: PDFont, text: String, size: Float, maxWidth: Float): List<String> {
        val output = mutableListOf<String>()
        var current = ""
        text.trim().split(Regex("\\s+")).forEach { word ->
            val candidate = if (current.isBlank()) word else "$current $word"
            if (textWidth(font, candidate, size) <= maxWidth) {
                current = candidate
            } else {
                if (current.isNotBlank()) output += current
                current = word
            }
        }
        if (current.isNotBlank()) output += current
        return output
    }

    private fun textWidth(font: PDFont, text: String, size: Float): Float =
        font.getStringWidth(text) / 1000f * size

    private fun signed(value: Int): String = if (value >= 0) "+$value" else value.toString()

    private fun formatInteger(value: Int): String {
        val raw = value.toString()
        return raw.reversed().chunked(3).joinToString(".").reversed()
    }

    private class Fonts(
        document: PDDocument,
        resourceLoader: (String) -> InputStream?,
    ) {
        val regular = load(document, resourceLoader, "fonts/pdf/text/FiraSans-Regular.ttf")
        val semibold = load(document, resourceLoader, "fonts/pdf/text/FiraSans-SemiBold.ttf")
        val handwritten = load(document, resourceLoader, "fonts/pdf/text/Kalam-Bold.ttf")
        val symbol = load(document, resourceLoader, SYMBOL_FONT)

        companion object {
            private fun load(
                document: PDDocument,
                resourceLoader: (String) -> InputStream?,
                path: String,
            ): PDFont {
                val input = resourceLoader(path) ?: error("Required PDF font unavailable: $path")
                return input.use { PDType0Font.load(document, it, false) }
            }
        }
    }

    private data class Rule(val startX: Float, val endX: Float, val topY: Float)
    private data class TopRect(val x: Float, val top: Float, val width: Float, val height: Float)
    private data class AbilityPlacement(val ability: CharacterAbility, val scoreX: Float, val modX: Float)
    private data class SkillRow(val skill: SkillKey, val checkbox: TopRect, val valueRule: Rule)
    private data class SkillSection(val name: String, val rows: List<SkillRow>)

    private companion object {
        const val PAGE_HEIGHT = 792f
        const val CHECK_CP = 0xE211
        const val DOUBLE_CHECK_CP = 0xE212
        const val SYMBOL_FONT = "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf"

        val IDENT_CLASS_RULE = Rule(307.417f, 442.063f, 53.508f)
        val IDENT_RACE_RULE = Rule(307.417f, 442.063f, 73.350f)
        val IDENT_XP_RULE = Rule(134.504f, 233.717f, 113.035f)
        val PORTRAIT_NAME_RECT = TopRect(455f, 190f, 132f, 28f)

        val DEFENSE_AC_RECT = TopRect(30f, 135f, 55f, 65f)
        val DEFENSE_DEX_RULE = Rule(98.5f, 158f, 138.5f)

        val CORE_INIT_RECT = TopRect(171f, 119f, 57.5f, 38f)
        val CORE_PROF_RECT = TopRect(242f, 119f, 57.5f, 38f)
        val CORE_MAX_HP_RECT = TopRect(312.5f, 119f, 57.5f, 38f)
        val CORE_CURRENT_HP_RECT = TopRect(383.5f, 151f, 57.5f, 45f)
        val CORE_SPEED_RECT = TopRect(171f, 188f, 57.5f, 37f)
        val CORE_HIT_DICE_RECT = TopRect(312f, 188f, 59f, 37f)

        val SPELL_SAVE_RECT = TopRect(147f, 421f, 55f, 39f)
        val SPELL_ATTACK_RECT = TopRect(147f, 496f, 55f, 39f)
        val SPELL_ABILITY_RECT = TopRect(147f, 568f, 55f, 39f)

        val ABILITIES = listOf(
            AbilityPlacement(CharacterAbility.STRENGTH, 58f, 85f),
            AbilityPlacement(CharacterAbility.DEXTERITY, 154.25f, 184.25f),
            AbilityPlacement(CharacterAbility.CONSTITUTION, 250.75f, 280.5f),
            AbilityPlacement(CharacterAbility.INTELLIGENCE, 347.25f, 376.75f),
            AbilityPlacement(CharacterAbility.WISDOM, 445.5f, 473.25f),
            AbilityPlacement(CharacterAbility.CHARISMA, 539.75f, 569.5f),
        )

        val SKILL_SECTIONS = listOf(
            SkillSection("STR", listOf(
                SkillRow(SkillKey.ATHLETICS, TopRect(23.035f, 324.386f, 9.669f, 12.287f), Rule(85.398f, 108.075f, 336.972f)),
            )),
            SkillSection("DEX", listOf(
                SkillRow(SkillKey.ACROBATICS, TopRect(119.413f, 324.353f, 9.669f, 12.286f), Rule(181.776f, 204.453f, 336.972f)),
                SkillRow(SkillKey.SLEIGHT_OF_HAND, TopRect(119.413f, 338.526f, 9.669f, 12.287f), Rule(181.776f, 204.453f, 351.146f)),
                SkillRow(SkillKey.STEALTH, TopRect(119.413f, 352.699f, 9.669f, 12.287f), Rule(181.776f, 204.453f, 365.319f)),
            )),
            SkillSection("INT", listOf(
                SkillRow(SkillKey.ARCANA, TopRect(312.169f, 324.353f, 9.669f, 12.286f), Rule(374.531f, 397.208f, 336.972f)),
                SkillRow(SkillKey.HISTORY, TopRect(312.169f, 338.526f, 9.669f, 12.287f), Rule(374.531f, 397.208f, 351.146f)),
                SkillRow(SkillKey.INVESTIGATION, TopRect(312.169f, 352.699f, 9.669f, 12.287f), Rule(374.531f, 397.208f, 365.319f)),
                SkillRow(SkillKey.NATURE, TopRect(312.169f, 366.872f, 9.669f, 12.287f), Rule(374.531f, 397.208f, 379.492f)),
                SkillRow(SkillKey.RELIGION, TopRect(312.169f, 381.045f, 9.669f, 12.287f), Rule(374.531f, 397.208f, 393.665f)),
            )),
            SkillSection("WIS", listOf(
                SkillRow(SkillKey.MEDICINE, TopRect(408.547f, 324.353f, 9.669f, 12.286f), Rule(470.909f, 493.586f, 336.972f)),
                SkillRow(SkillKey.PERCEPTION, TopRect(408.547f, 338.526f, 9.669f, 12.287f), Rule(470.909f, 493.586f, 351.146f)),
                SkillRow(SkillKey.INSIGHT, TopRect(408.547f, 352.699f, 9.669f, 12.287f), Rule(470.909f, 493.586f, 365.319f)),
                SkillRow(SkillKey.SURVIVAL, TopRect(408.547f, 366.872f, 9.669f, 12.287f), Rule(470.909f, 493.586f, 379.492f)),
                SkillRow(SkillKey.ANIMAL_HANDLING, TopRect(408.547f, 381.045f, 9.669f, 12.287f), Rule(470.909f, 493.586f, 393.665f)),
            )),
            SkillSection("CHA", listOf(
                SkillRow(SkillKey.DECEPTION, TopRect(504.925f, 324.353f, 9.669f, 12.286f), Rule(567.287f, 589.964f, 336.972f)),
                SkillRow(SkillKey.PERFORMANCE, TopRect(504.925f, 338.526f, 9.669f, 12.287f), Rule(567.287f, 589.964f, 351.146f)),
                SkillRow(SkillKey.INTIMIDATION, TopRect(504.925f, 352.699f, 9.669f, 12.287f), Rule(567.287f, 589.964f, 365.319f)),
                SkillRow(SkillKey.PERSUASION, TopRect(504.925f, 366.872f, 9.669f, 12.287f), Rule(567.287f, 589.964f, 379.492f)),
            )),
        )

        val ATTACK_RULE_Y = listOf(444.689f, 464.531f, 484.374f, 504.217f, 524.059f)
        val TRAIT_COLUMNS = listOf(
            Rule(26f, 188.5f, 0f),
            Rule(212.5f, 375f, 0f),
            Rule(399f, 561.5f, 0f),
        )
        val TRAIT_ROW_Y = listOf(661.5f, 681.5f)

        val BACKGROUND_RULE = Rule(25f, 181f, 109.5f)
        val IDEALS_RULE = Rule(25f, 181f, 387.5f)
        val BONDS_RULE = Rule(25f, 181f, 526.5f)
        val FLAWS_RULE = Rule(25f, 181f, 665.5f)
        val STORY_RULE_Y = listOf(387.996f, 407.839f, 427.681f, 447.524f)

        val CANTRIP_RULE_Y = listOf(129.8f, 148.7f, 167.5f, 186.4f)
        val LEVEL1_SLOT_TOTAL_RECT = TopRect(56.98f, 291.55f, 27.49f, 27.49f)
        val LEVEL1_SQUARES = listOf(
            TopRect(28.205f, 327.187f, 9.669f, 12.287f),
            TopRect(28.205f, 347.030f, 9.669f, 12.287f),
            TopRect(28.205f, 366.872f, 9.669f, 12.287f),
            TopRect(28.205f, 386.715f, 9.669f, 12.287f),
            TopRect(28.205f, 406.557f, 9.669f, 12.287f),
        )
    }
}
