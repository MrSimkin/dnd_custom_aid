package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOptionKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDefenseType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterMovementType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryAmountMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryCadence
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
import io.github.mrsimkin.dndcustomaid.shared.character.spellAttackModifier
import io.github.mrsimkin.dndcustomaid.shared.character.spellSaveDc
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
 * Production promotion advances one frozen role at a time. Custom Statistics, Traits & Features,
 * Resources & Options, Inventory / Equipment, Spells, and Notes now use the Run-6
 * owner-approved geometry, typography, source structure and independent layer model, while all
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
        appendCombatExtendedPages(plan)
        if (needsResourcesExtendedPage(plan)) {
            appendResourcesExtendedPages(plan)
        }
        appendInventoryExtendedPages(plan)
        appendSpellExtendedPages(plan)
        appendNotesExtendedPages(plan)
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
        val detailLines = traitDescriptionLines(orderedTraits)
        val metadata = traitMetadataLines(orderedTraits)
        val supplements = traitSupplementLines(plan)
        return overflowNames.isNotEmpty() ||
            detailLines.isNotEmpty() ||
            metadata.isNotEmpty() ||
            supplements.isNotEmpty() ||
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

        val proficiencies = proficiencyLines(
            sheet.proficiencies.filter { it.type != CharacterProficiencyType.LANGUAGE },
        )
        val languages = proficiencyLines(
            sheet.proficiencies.filter { it.type == CharacterProficiencyType.LANGUAGE },
        )

        val detailLines = traitDescriptionLines(orderedTraits)
        val metadataLines = traitMetadataLines(orderedTraits) + traitSupplementLines(plan)

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

    private fun traitDescriptionLines(
        traits: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>,
    ): List<String> = traits.flatMap { trait ->
        val description = trait.description.trim()
        if (description.isEmpty()) {
            emptyList()
        } else {
            wrapByWidth(
                trait.name + ": " + description,
                resources.fira,
                8.5f,
                TRAIT_RIGHT_TEXT_WIDTH,
            )
        }
    }

    private fun traitMetadataLines(
        traits: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>,
    ): List<String> = traits.flatMap { trait ->
        val meaningful = trait.source.trim().isNotEmpty() ||
            trait.maxUses != null ||
            !trait.recovery.isNullOrBlank() ||
            trait.activation != null ||
            !trait.notes.isNullOrBlank()
        if (!meaningful) {
            emptyList()
        } else {
            val metadata = buildList {
                add(traitTypeLabel(trait.type))
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
                lines += wrapByWidth(
                    "$label: $clean",
                    resources.fira,
                    8.4f,
                    TRAIT_RIGHT_TEXT_WIDTH,
                )
            }
        }

        fun addCharOverflow(label: String, value: String, maxChars: Int, baseLines: Int) {
            val overflow = wrapForRulesByChars(value, maxChars).drop(baseLines)
            if (overflow.isNotEmpty()) {
                addFull("$label (cont.)", overflow.joinToString(" "))
            }
        }

        fun addWidthOverflow(
            label: String,
            value: String,
            size: Float,
            width: Float,
            baseLines: Int,
        ) {
            val clean = value.trim()
            if (clean.isEmpty()) return
            val overflow = wrapByWidth(clean, resources.fira, size, width).drop(baseLines)
            if (overflow.isNotEmpty()) {
                addFull("$label (cont.)", overflow.joinToString(" "))
            }
        }

        // Base v1 page 3 has no dedicated summary/religion fields.
        addFull("Resumen de trasfondo", background.summary)
        addFull("Fe / religión", background.religionFaith)

        // Preserve only narrative overflow beyond the exact frozen v1 base capacities.
        addCharOverflow("Rasgos de personalidad", background.personalityTraits, 42, 6)
        addWidthOverflow("Ideales", background.ideals, 9.25f, 153f, 6)
        addWidthOverflow("Vínculos", background.bonds, 9.25f, 153f, 6)
        addWidthOverflow("Defectos", background.flaws, 9.25f, 153f, 6)
        addWidthOverflow("Historia", background.story, 9.25f, 365f, 4)

        successor.speciesIdentity?.name?.trim()?.takeIf {
            it.isNotEmpty() && !it.equals(background.race.trim(), ignoreCase = true)
        }?.let { addFull("Raza canónica", it) }
        successor.subraceIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }?.let {
            addFull("Subraza", it)
        }
        successor.backgroundIdentity?.name?.trim()?.takeIf {
            it.isNotEmpty() && !it.equals(background.name.trim(), ignoreCase = true)
        }?.let { addFull("Trasfondo canónico", it) }

        val orderedClasses = sheet.classes.sortedBy { it.sortOrder }
        val classSummary = orderedClasses.joinToString(" / ") { classLevel ->
            classLevel.name + " " + classLevel.level
        }
        if (classSummary.length > 32) addFull("Clases", classSummary)
        orderedClasses.forEach { classLevel ->
            classLevel.subclassName?.trim()?.takeIf { it.isNotEmpty() }?.let { subclass ->
                addFull("Subclase", classLevel.name + " - " + subclass)
            }
        }

        if (sheet.name.length > 36) addFull("Nombre", sheet.name)
        if (sheet.status != io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.ACTIVE) {
            addFull("Estado", characterStatusLabel(sheet.status))
        }

        if (closure.progressMode == CharacterProgressMode.MILESTONE) {
            addFull("Progreso", closure.milestoneProgress)
        }

        if (sheet.inspiration) addFull("Inspiración", "Sí")
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
        successor.spellcastingProfiles.drop(1).forEach { profile ->
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
                    add(
                        companion.name +
                            companion.kind.takeIf { it.isNotBlank() }?.let { " ($it)" }.orEmpty(),
                    )
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
        val lines = combatReferenceLines(plan)
        if (lines.isEmpty()) return

        val pages = pageCount(lines.size, COMBAT_LINES_PER_PAGE)
        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderCombatPage(
                page = page,
                lines = lines.pageSlice(pageIndex, COMBAT_LINES_PER_PAGE),
                pageIndex = pageIndex,
            )
        }
    }

    private fun combatReferenceLines(plan: PcSheetPdfRenderPlan): List<String> {
        val aggregate = plan.snapshot.aggregate
        val damageByCombatId = aggregate.successor.combatDamage.associateBy { it.combatEntryId }

        return aggregate.sheet.combatEntries
            .sortedBy { it.sortOrder }
            .mapIndexedNotNull { index, entry ->
                val damage = damageByCombatId[entry.id]
                val structuredDamage = damage?.components
                    ?.joinToString(" + ") { component ->
                        component.expression +
                            component.typeText?.takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()
                    }
                    .orEmpty()
                val needsReference =
                    index >= BASE_V1_COMBAT_CAPACITY ||
                        entry.type != CharacterCombatEntryType.ATTACK ||
                        !entry.notes.isNullOrBlank() ||
                        structuredDamage.isNotBlank()
                if (!needsReference) {
                    null
                } else {
                    buildList {
                        add(combatTypeLabel(entry.type) + " — " + entry.name)
                        entry.attackModifier?.let { add("Ataque " + signed(it)) }
                        entry.damageEffect.takeIf { it.isNotBlank() }?.let { add("Efecto / daño: $it") }
                        entry.rangeText?.takeIf { it.isNotBlank() }?.let { add("Alcance: $it") }
                        entry.notes?.takeIf { it.isNotBlank() }?.let { add("Notas: $it") }
                        structuredDamage.takeIf { it.isNotBlank() }?.let { add("Daño estructurado: $it") }
                    }.joinToString(" · ")
                }
            }
            .flatMap { value ->
                wrapByWidth(
                    value,
                    resources.fira,
                    8.4f,
                    COMBAT_TEXT_WIDTH,
                )
            }
    }

    private fun renderCombatPage(
        page: PDPage,
        lines: List<String>,
        pageIndex: Int,
    ) {
        val prefix = "V1X COMBAT P${pageIndex + 1}"
        appendLayer(page, "$prefix - STRUCTURE") { s ->
            drawSourceCrop(s, resources.forms[1], 20f, 18f, 150f, 74f)
            sourceBands(
                s,
                25f,
                585f,
                COMBAT_FIRST_RULE_TOP,
                COMBAT_LINES_PER_PAGE,
                COMBAT_STEP,
            )
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { s ->
            centeredText(s, resources.heading, 24f, 66f, 564f, 30f, "Combate / Acciones", 18f)
            centeredText(
                s,
                resources.fira,
                27f,
                96f,
                556f,
                14f,
                "REFERENCIA DE COMBATE / ACCIÓN / DAÑO",
                7.5f,
            )
        }
        appendLayer(page, "$prefix - VALUES") { s ->
            lines.forEachIndexed { index, line ->
                ruleText(
                    s,
                    resources.fira,
                    Rule(27.5f, 583.795f, COMBAT_FIRST_RULE_TOP + index * COMBAT_STEP),
                    line,
                    8.4f,
                )
            }
        }
        appendLayer(page, "$prefix - MARKERS") { }
    }

    private fun characterStatusLabel(
        status: io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus,
    ): String = when (status) {
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.ACTIVE -> "Activo"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.INACTIVE -> "Inactivo"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.RETIRED -> "Retirado"
        io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus.DEAD -> "Muerto"
    }

    private fun combatTypeLabel(type: CharacterCombatEntryType): String = when (type) {
        CharacterCombatEntryType.ATTACK -> "Ataque"
        CharacterCombatEntryType.ACTION -> "Acción"
        CharacterCombatEntryType.BONUS_ACTION -> "Acción adicional"
        CharacterCombatEntryType.REACTION -> "Reacción"
        CharacterCombatEntryType.OTHER -> "Otro"
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

    private fun traitTypeLabel(type: CharacterTraitType): String = when (type) {
        CharacterTraitType.CLASS -> "Clase"
        CharacterTraitType.SPECIES_RACE -> "Raza"
        CharacterTraitType.BACKGROUND -> "Trasfondo"
        CharacterTraitType.FEAT -> "Dote"
        CharacterTraitType.GIFT_BLESSING -> "Don / Bendición"
        CharacterTraitType.OTHER -> "Otro"
    }

    private fun activationLabel(type: CharacterActivationType): String = when (type) {
        CharacterActivationType.PASSIVE -> "Pasivo"
        CharacterActivationType.ACTION -> "Acción"
        CharacterActivationType.BONUS_ACTION -> "Acción adicional"
        CharacterActivationType.REACTION -> "Reacción"
        CharacterActivationType.OTHER -> "Otro"
    }

    private fun proficiencyLines(
        proficiencies: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency>,
    ): List<String> = proficiencies
        .sortedBy { it.sortOrder }
        .flatMap { proficiency ->
            val value = listOf(
                proficiency.name,
                proficiency.source.orEmpty().trim(),
                proficiency.notes.orEmpty().trim(),
            )
                .filter { it.isNotEmpty() }
                .joinToString(" · ")
            if (value.isEmpty()) {
                emptyList()
            } else {
                wrapByWidth(value, resources.fira, 8.4f, TRAIT_LEFT_TEXT_WIDTH)
            }
        }

    private fun needsResourcesExtendedPage(plan: PcSheetPdfRenderPlan): Boolean {
        val aggregate = plan.snapshot.aggregate
        return aggregate.sheet.resources.isNotEmpty() ||
            aggregate.sheet.classOptions.isNotEmpty() ||
            aggregate.successor.customMarkers.isNotEmpty()
    }

    private fun appendResourcesExtendedPages(plan: PcSheetPdfRenderPlan) {
        val rows = resourceRenderLines(resourceRenderRows(plan))
        val options = optionRenderLines(plan.snapshot.aggregate.sheet.classOptions.sortedBy { it.sortOrder })
        val pages = maxOf(
            1,
            pageCount(rows.size, RESOURCE_ROWS_PER_PAGE),
            pageCount(options.size, OPTION_ROWS_PER_PAGE),
        )

        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderResourcesPage(
                page = page,
                rows = rows.pageSlice(pageIndex, RESOURCE_ROWS_PER_PAGE),
                options = options.pageSlice(pageIndex, OPTION_ROWS_PER_PAGE),
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
                ResourceRenderRow(
                    name = resource.name,
                    currentValue = resource.currentValue,
                    maximum = when (kind) {
                        CharacterTrackableValueKind.BINARY -> 1
                        CharacterTrackableValueKind.COUNTER,
                        CharacterTrackableValueKind.CURRENT_MAX -> resource.maxValue
                    },
                    recoveryAndDetail = listOf(
                        resource.recovery.orEmpty().trim(),
                        recovery?.cadence?.let(::recoveryLabel).orEmpty(),
                        recovery?.amountMode?.let { recoveryAmountLabel(it, recovery.fixedAmount) }.orEmpty(),
                        recovery?.notes.orEmpty().trim(),
                        resource.source.orEmpty().trim(),
                        resource.notes.orEmpty().trim(),
                    ).filter { it.isNotEmpty() }.distinct().joinToString(" · "),
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
                    recoveryAndDetail = listOf(
                        recoveryLabel(marker.recovery.cadence),
                        recoveryAmountLabel(marker.recovery.amountMode, marker.recovery.fixedAmount),
                        marker.notes.orEmpty().trim(),
                    ).filter { it.isNotEmpty() }.joinToString(" · "),
                    sortOrder = marker.sortOrder,
                    sourceRank = 1,
                )
            }

        return (ordinary + markers)
            .sortedWith(
                compareBy<ResourceRenderRow> { it.sortOrder }
                    .thenBy { it.sourceRank }
                    .thenBy { it.name.lowercase() },
            )
    }

    private fun resourceRenderLines(rows: List<ResourceRenderRow>): List<ResourceRenderLine> =
        rows.flatMap { row ->
            val nameLines = wrapByWidth(
                row.name,
                resources.fira,
                8.4f,
                RESOURCE_NAME_TEXT_WIDTH,
            ).ifEmpty { listOf("") }
            val detailLines = wrapByWidth(
                row.recoveryAndDetail,
                resources.fira,
                8.0f,
                RESOURCE_DETAIL_TEXT_WIDTH,
            ).ifEmpty { listOf("") }
            val count = maxOf(nameLines.size, detailLines.size, 1)
            (0 until count).map { index ->
                val firstLine = index == 0
                val symbolic = firstLine &&
                    row.maximum != null &&
                    row.maximum in 1..RESOURCE_SYMBOL_MAXIMUM &&
                    row.currentValue in 0..row.maximum
                ResourceRenderLine(
                    name = nameLines.getOrNull(index).orEmpty(),
                    currentValue = row.currentValue.takeIf { firstLine },
                    maximum = row.maximum.takeIf { firstLine },
                    numericValue = if (firstLine && !symbolic) {
                        row.maximum?.let { row.currentValue.toString() + "/" + it }
                            ?: row.currentValue.toString()
                    } else {
                        null
                    },
                    recoveryAndDetail = detailLines.getOrNull(index).orEmpty(),
                )
            }
        }

    private fun optionRenderLines(
        options: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOption>,
    ): List<OptionRenderLine> = options.flatMap { option ->
        val nameLines = wrapByWidth(
            option.name,
            resources.fira,
            8.2f,
            OPTION_NAME_TEXT_WIDTH,
        ).ifEmpty { listOf("") }
        val detail = listOf(
            option.effectSummary.trim(),
            option.costText.orEmpty().trim(),
            option.source.orEmpty().trim(),
            option.notes.orEmpty().trim(),
        ).filter { it.isNotEmpty() }.joinToString(" · ")
        val detailLines = wrapByWidth(
            detail,
            resources.fira,
            8.2f,
            OPTION_DETAIL_TEXT_WIDTH,
        ).ifEmpty { listOf("") }
        val count = maxOf(nameLines.size, detailLines.size, 1)
        (0 until count).map { index ->
            OptionRenderLine(
                kind = optionKindLabel(option.kind).takeIf { index == 0 }.orEmpty(),
                name = nameLines.getOrNull(index).orEmpty(),
                detail = detailLines.getOrNull(index).orEmpty(),
                active = option.active.takeIf { index == 0 },
            )
        }
    }

    private fun renderResourcesPage(
        page: PDPage,
        rows: List<ResourceRenderLine>,
        options: List<OptionRenderLine>,
        pageIndex: Int,
    ) {
        val prefix = "V1X RESOURCES P${pageIndex + 1}"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            drawSourceCrop(s, resources.forms[1], 20f, 18f, 150f, 74f)
            sourceBands(s, 25f, 585f, RESOURCE_FIRST_RULE_TOP, RESOURCE_ROWS_PER_PAGE, RESOURCE_STEP)
            sourceBands(s, 25f, 585f, OPTION_FIRST_RULE_TOP, OPTION_ROWS_PER_PAGE, OPTION_STEP)
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { s ->
            centeredText(s, resources.heading, 24f, 66f, 564f, 30f, "Recursos", 18f)
            centeredText(s, resources.fira, 27f, 96f, 175f, 14f, "RECURSO", 7.5f)
            centeredText(s, resources.fira, 205f, 96f, 180f, 14f, "USOS", 7.5f)
            centeredText(s, resources.fira, 400f, 96f, 183f, 14f, "RESTABLECE", 7.5f)

            centeredText(s, resources.heading, 24f, OPTION_HEADING_TOP, 564f, 30f, "Opciones", 18f)
            centeredText(s, resources.fira, 35f, OPTION_HEADER_TOP, 74f, 14f, "TIPO", 7.5f)
            centeredText(s, resources.fira, 126f, OPTION_HEADER_TOP, 112f, 14f, "OPCIÓN", 7.5f)
            centeredText(s, resources.fira, 240.803f, OPTION_HEADER_TOP, 342.992f, 14f, "DESCRIPCIÓN", 7.5f)
        }
        appendLayer(page, "$prefix - VALUES") { s ->
            rows.forEachIndexed { index, row ->
                val ruleTop = RESOURCE_FIRST_RULE_TOP + index * RESOURCE_STEP
                if (row.name.isNotEmpty()) {
                    ruleText(s, resources.fira, Rule(27.5f, 202f, ruleTop), row.name, 8.4f)
                }
                row.numericValue?.let { value ->
                    centeredText(
                        s,
                        resources.firaSemibold,
                        205f,
                        ruleTop - RESOURCE_STEP,
                        180f,
                        RESOURCE_STEP,
                        value,
                        8.6f,
                    )
                }
                if (row.recoveryAndDetail.isNotEmpty()) {
                    ruleText(
                        s,
                        resources.fira,
                        Rule(400f, 583.795f, ruleTop),
                        row.recoveryAndDetail,
                        8.0f,
                    )
                }
            }

            options.forEachIndexed { index, option ->
                val ruleTop = OPTION_FIRST_RULE_TOP + index * OPTION_STEP
                if (option.kind.isNotEmpty()) {
                    centeredText(
                        s,
                        resources.heading,
                        35f,
                        ruleTop - 18f,
                        74f,
                        18f,
                        option.kind,
                        12.5f,
                    )
                }
                if (option.name.isNotEmpty()) {
                    ruleText(s, resources.fira, Rule(126f, 238f, ruleTop), option.name, 8.2f)
                }
                if (option.detail.isNotEmpty()) {
                    ruleText(
                        s,
                        resources.fira,
                        Rule(240.803f, 583.795f, ruleTop),
                        option.detail,
                        8.2f,
                    )
                }
            }
        }
        appendLayer(page, "$prefix - MARKERS") { s ->
            rows.forEachIndexed { index, row ->
                val current = row.currentValue
                val maximum = row.maximum
                if (
                    current != null &&
                    maximum != null &&
                    maximum in 1..RESOURCE_SYMBOL_MAXIMUM &&
                    current in 0..maximum
                ) {
                    drawResourceCounter(
                        s = s,
                        font = resources.symbol,
                        startX = 222f,
                        centerTop = RESOURCE_FIRST_RULE_TOP + index * RESOURCE_STEP - RESOURCE_STEP / 2f,
                        current = current,
                        maximum = maximum,
                    )
                }
            }

            options.forEachIndexed { index, option ->
                option.active?.let { active ->
                    val ruleTop = OPTION_FIRST_RULE_TOP + index * OPTION_STEP
                    drawV1TrainingBox(
                        s = s,
                        font = resources.symbol,
                        centerX = 116f,
                        centerTop = ruleTop - OPTION_STEP / 2f,
                        training = if (active) Training.PROFICIENT else Training.NONE,
                    )
                }
            }
        }
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
            val baseLabel = inventoryBaseLabel(item)
            val needsFullContinuation =
                index >= BASE_V1_EQUIPMENT_CAPACITY ||
                    wrapByWidth(
                        baseLabel,
                        resources.condensed,
                        7.0f,
                        INVENTORY_ORDINARY_TEXT_WIDTH,
                    ).size > 1
            if (needsFullContinuation) {
                inventoryContinuationLines(item, usage)
            } else {
                inventoryDetailContinuationLines(item, usage)
            }
        }

        val special = ordered.filter { it.special }
        val specialContinuation = special.mapIndexedNotNull { index, item ->
            val usage = usageByItem[item.id]
            val baseDetail = buildList {
                if (item.attuned) add("Sintonizado")
                item.location?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
                item.description?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
                item.notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            }.joinToString(" · ")
            val baseDetailOverflows =
                baseDetail.isNotBlank() &&
                    textWidth(resources.fira, baseDetail, 8.5f) > V1_BASE_SPECIAL_DETAIL_WIDTH
            val baseNameOverflows =
                textWidth(resources.fira, item.name, 8.5f) > V1_BASE_SPECIAL_NAME_WIDTH
            item.takeIf {
                index >= BASE_V1_SPECIAL_CAPACITY ||
                    item.quantity != 1 ||
                    item.weightLb != null ||
                    usageMeaningful(usage) ||
                    baseNameOverflows ||
                    baseDetailOverflows
            }
        }

        val treasure = buildList {
            sheet.currencies
                .filter { !it.isDefault }
                .sortedBy { it.sortOrder }
                .forEach { currency ->
                    add(TreasureEntry("${currency.name}: ${currency.amount}", null))
                }
            aggregate.successor.preferences.valuablesText
                .split(';')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map(::parseValuable)
                .forEach(::add)
        }.drop(BASE_V1_VALUABLE_CAPACITY)

        if (ordinaryLines.isEmpty() && specialContinuation.isEmpty() && treasure.isEmpty()) return

        val pages = maxOf(
            pageCount(ordinaryLines.size, INVENTORY_ORDINARY_CAPACITY),
            pageCount(treasure.size, INVENTORY_TREASURE_CAPACITY),
            pageCount(specialContinuation.size, INVENTORY_SPECIAL_CAPACITY),
        )
        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderInventoryPage(
                page = page,
                ordinary = ordinaryLines.pageSlice(pageIndex, INVENTORY_ORDINARY_CAPACITY),
                treasure = treasure.pageSlice(pageIndex, INVENTORY_TREASURE_CAPACITY),
                special = specialContinuation.pageSlice(pageIndex, INVENTORY_SPECIAL_CAPACITY),
                usageByItem = usageByItem,
                pageIndex = pageIndex,
            )
        }
    }

    private fun renderInventoryPage(
        page: PDPage,
        ordinary: List<String>,
        treasure: List<TreasureEntry>,
        special: List<CharacterInventoryItem>,
        usageByItem: Map<kotlin.uuid.Uuid, CharacterInventoryUsage>,
        pageIndex: Int,
    ) {
        val prefix = "V1X INVENTORY P${pageIndex + 1}"
        val positionedSpecial = positionedSpecialItems(special, INVENTORY_SPECIAL_CAPACITY)

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            s.drawForm(resources.forms[1])
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        // The source sheet already identifies this as the Equipment page. Do not add a second
        // heading/subtitle: it collided with the approved "Equipo" title and made the continuation
        // look like a different layout instead of another native page.
        appendLayer(page, "$prefix - LABELS") { }
        appendLayer(page, "$prefix - VALUES") { s ->
            ordinary.forEachIndexed { index, value ->
                // Preserve natural paper reading order: fill one column top-to-bottom before
                // continuing in the next column, so wrapped metadata for one item stays together.
                val row = index % INVENTORY_ORDINARY_RULES.size
                val column = index / INVENTORY_ORDINARY_RULES.size
                val (startX, endX) = INVENTORY_ORDINARY_COLUMNS[column]
                ruleText(
                    s,
                    resources.condensed,
                    Rule(startX, endX, INVENTORY_ORDINARY_RULES[row]),
                    value,
                    8.4f,
                )
            }

            treasure.forEachIndexed { index, entry ->
                val y = INVENTORY_TREASURE_RULES[index]
                ruleText(
                    s,
                    resources.fira,
                    Rule(453.402f, 546.945f, y),
                    entry.label,
                    8.2f,
                )
                entry.value?.let { value ->
                    ruleText(
                        s,
                        resources.fira,
                        Rule(549.779f, 583.795f, y),
                        value,
                        8.2f,
                    )
                }
            }

            positionedSpecial.forEach { (rowIndex, item) ->
                val y = INVENTORY_SPECIAL_RULES[rowIndex]
                val expectedLocationRow = specialLocationRow(item.location)
                if (expectedLocationRow != rowIndex) {
                    item.location?.trim()?.takeIf { it.isNotEmpty() }?.let { location ->
                        ruleText(
                            s,
                            resources.fira,
                            Rule(25f, 120f, y),
                            location,
                            8.0f,
                        )
                    }
                }
                ruleText(
                    s,
                    resources.fira,
                    Rule(126f, 238f, y),
                    inventoryContinuationLabel(item),
                    8.2f,
                )
                val detail = specialInventoryDetail(item, usageByItem[item.id])
                if (detail.isNotEmpty()) {
                    ruleText(
                        s,
                        resources.fira,
                        Rule(240.803f, 583.795f, y),
                        detail,
                        8.0f,
                    )
                }
            }
        }
        appendLayer(page, "$prefix - MARKERS") { s ->
            positionedSpecial.forEach { (rowIndex, item) ->
                if (item.equipped || item.attuned) {
                    val y = INVENTORY_SPECIAL_RULES[rowIndex]
                    // The imported v1 equipment template already contains the empty checkbox.
                    // Overlay only the approved v8 check glyph; drawing another square creates a
                    // visually double-boxed marker and shifts the perceived center.
                    approvedV8Marker(
                        s = s,
                        font = resources.symbol,
                        centerX = SPECIAL_CHECK_X + SPECIAL_CHECK_WIDTH / 2f,
                        centerTop = INVENTORY_SPECIAL_CHECK_TOPS[rowIndex] + SPECIAL_CHECK_HEIGHT / 2f,
                        size = 5.6f,
                    )
                }
            }
        }
    }

    private fun inventoryContinuationLabel(item: CharacterInventoryItem): String = buildString {
        if (item.quantity > 1) append(item.quantity).append(" x ")
        append(item.name)
    }

    private fun inventoryBaseLabel(item: CharacterInventoryItem): String = buildList {
        add(inventoryContinuationLabel(item))
        item.location?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        item.weightLb?.let { weight ->
            add(if (weight % 1.0 == 0.0) weight.toInt().toString() + " lb" else weight.toString() + " lb")
        }
    }.joinToString(" · ")

    private fun inventoryDetailContinuationLines(
        item: CharacterInventoryItem,
        usage: CharacterInventoryUsage?,
    ): List<String> {
        val lines = mutableListOf<String>()
        val operationalStatus = buildList {
            if (item.equipped) add("Equipado")
            if (item.attuned) add("Sintonizado")
            addAll(inventoryUsageLabels(usage))
        }.joinToString(" · ")
        if (operationalStatus.isNotEmpty()) {
            lines += wrapByWidth(
                item.name + " — Estado: " + operationalStatus,
                resources.condensed,
                8.2f,
                INVENTORY_ORDINARY_TEXT_WIDTH,
            )
        }

        val detail = listOfNotNull(
            item.description?.trim()?.takeIf { it.isNotEmpty() },
            item.notes?.trim()?.takeIf { it.isNotEmpty() },
        ).joinToString(" · ")
        if (detail.isNotEmpty()) {
            lines += wrapByWidth(
                item.name + " — Nota: " + detail,
                resources.condensed,
                8.2f,
                INVENTORY_ORDINARY_TEXT_WIDTH,
            )
        }
        return lines
    }

    private fun inventoryContinuationLines(
        item: CharacterInventoryItem,
        usage: CharacterInventoryUsage?,
    ): List<String> {
        val status = buildList {
            item.location?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            item.weightLb?.let { weight ->
                add(
                    if (weight % 1.0 == 0.0) weight.toInt().toString() + " lb"
                    else weight.toString() + " lb",
                )
            }
            if (item.equipped) add("Equipado")
            if (item.attuned) add("Sintonizado")
            addAll(inventoryUsageLabels(usage))
        }.joinToString(" · ")

        val lines = mutableListOf<String>()
        // Keep the logical item identity on one ruled line. ruleText() may reduce the condensed
        // type slightly to fit, but it must not turn "5 lb" into an orphaned second item-looking row.
        val primary = buildList {
            add(inventoryContinuationLabel(item))
            item.location?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            item.weightLb?.let { weight ->
                add(if (weight % 1.0 == 0.0) weight.toInt().toString() + " lb" else weight.toString() + " lb")
            }
        }.joinToString(" · ")
        lines += primary

        val operationalStatus = buildList {
            if (item.equipped) add("Equipado")
            if (item.attuned) add("Sintonizado")
            addAll(inventoryUsageLabels(usage))
        }.joinToString(" · ")
        if (operationalStatus.isNotEmpty()) {
            lines += wrapByWidth(
                "Estado: $operationalStatus",
                resources.condensed,
                8.2f,
                INVENTORY_ORDINARY_TEXT_WIDTH,
            )
        }

        val description = listOfNotNull(
            item.description?.trim()?.takeIf { it.isNotEmpty() },
            item.notes?.trim()?.takeIf { it.isNotEmpty() },
        ).joinToString(" · ")
        if (description.isNotEmpty()) {
            lines += wrapByWidth(
                "Nota: $description",
                resources.condensed,
                8.2f,
                INVENTORY_ORDINARY_TEXT_WIDTH,
            )
        }
        return lines
    }

    private fun specialInventoryDetail(
        item: CharacterInventoryItem,
        usage: CharacterInventoryUsage?,
    ): String = buildList {
        item.weightLb?.let { add(formatInventoryWeight(it)) }
        if (item.equipped) add("Equipado")
        if (item.attuned) add("Sintonizado")
        addAll(inventoryUsageLabels(usage))
        item.description?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        item.notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
    }.joinToString(" · ")

    private fun positionedSpecialItems(
        items: List<CharacterInventoryItem>,
        rowCount: Int,
    ): List<Pair<Int, CharacterInventoryItem>> {
        val available = (0 until rowCount).toMutableSet()
        val positioned = mutableListOf<Pair<Int, CharacterInventoryItem>>()
        items.take(rowCount).forEach { item ->
            val preferred = specialLocationRow(item.location)?.takeIf { it in available }
            val fallback = available
                .filter { it >= SPECIAL_LOCATION_LABELS.size }
                .minOrNull()
                ?: available.minOrNull()
            val row = preferred ?: fallback ?: return@forEach
            available.remove(row)
            positioned += row to item
        }
        return positioned.sortedBy { it.first }
    }

    private fun specialLocationRow(location: String?): Int? {
        val normalized = normalizedInventoryLocation(location)
        return SPECIAL_LOCATION_LABELS.indexOf(normalized).takeIf { it >= 0 }
    }

    private fun specialLocationNeedsText(location: String?): Boolean {
        val normalized = normalizedInventoryLocation(location)
        return normalized.isNotEmpty() && normalized !in SPECIAL_LOCATION_LABELS
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

    private fun parseValuable(raw: String): TreasureEntry {
        val match = Regex("""^(.*?)\s*\((\d+)\s*po\)\s*$""", RegexOption.IGNORE_CASE)
            .matchEntire(raw)
        return if (match != null) {
            TreasureEntry(
                label = match.groupValues[1].trim(),
                value = match.groupValues[2],
            )
        } else {
            TreasureEntry(label = raw.trim(), value = null)
        }
    }

    private fun appendSpellExtendedPages(plan: PcSheetPdfRenderPlan) {
        val sheet = plan.snapshot.aggregate.sheet
        require(sheet.spells.all { it.level in 0..9 }) {
            "Custom-v1 spell continuation supports spell levels 0 through 9."
        }
        val byLevel = sheet.spells
            .groupBy { it.level }
            .mapValues { (_, entries) ->
                entries.sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.name.lowercase() })
            }
        val overflowByLevel = SPELL_CONTINUATION_BLOCKS.associate { block ->
            block.level to byLevel[block.level].orEmpty().drop(block.baseCapacity)
        }
        val pages = SPELL_CONTINUATION_BLOCKS.maxOf { block ->
            pageCount(overflowByLevel[block.level].orEmpty().size, block.rules.size)
        }
        if (pages == 0) return

        val slotsByLevel = sheet.spellSlots.associateBy { it.level }
        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            val spellsByLevel = SPELL_CONTINUATION_BLOCKS.associate { block ->
                block.level to overflowByLevel[block.level].orEmpty()
                    .drop(pageIndex * block.rules.size)
                    .take(block.rules.size)
            }
            renderSpellContinuationPage(
                page = page,
                spellsByLevel = spellsByLevel,
                slotsByLevel = slotsByLevel.mapValues { it.value.totalSlots },
                pageIndex = pageIndex,
            )
        }
    }

    private fun renderSpellContinuationPage(
        page: PDPage,
        spellsByLevel: Map<Int, List<CharacterSpell>>,
        slotsByLevel: Map<Int, Int>,
        pageIndex: Int,
    ) {
        val prefix = "V1X SPELLS P${pageIndex + 1}"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            s.drawForm(resources.forms[3])
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { }
        appendLayer(page, "$prefix - VALUES") { s ->
            SPELL_CONTINUATION_BLOCKS.forEach { block ->
                val spells = spellsByLevel[block.level].orEmpty()
                spells.forEachIndexed { index, spell ->
                    ruleText(
                        s,
                        resources.fira,
                        block.rules[index],
                        spell.name,
                        8.7f,
                        11.5f,
                    )
                }
                block.slotRule?.let { rule ->
                    slotsByLevel[block.level]?.let { total ->
                        ruleText(s, resources.fira, rule, total.toString(), 9f)
                    }
                }
            }
        }
        appendLayer(page, "$prefix - MARKERS") { s ->
            SPELL_CONTINUATION_BLOCKS.forEach { block ->
                spellsByLevel[block.level].orEmpty().forEachIndexed { index, spell ->
                    if (spell.sourceAssociations.any { it.prepared }) {
                        val center = block.checkboxCenters[index]
                        approvedV8Marker(
                            s = s,
                            font = resources.symbol,
                            centerX = center.first,
                            centerTop = center.second,
                            size = 7f,
                        )
                    }
                }
            }
        }
    }

    private fun approvedV8Marker(
        s: PDFormContentStream,
        font: PDFont,
        centerX: Float,
        centerTop: Float,
        size: Float,
    ) {
        val glyph = V8Glyph(
            codePoint = 0xE211,
            xMin = 285,
            yMin = 400,
            xMax = 1350,
            yMax = 1306,
        )
        val designWidth = (glyph.xMax - glyph.xMin).toFloat()
        val designHeight = (glyph.yMax - glyph.yMin).toFloat()
        val fontSize = size * SYMBOL_UNITS_PER_EM / max(designWidth, designHeight)
        val centerY = H - centerTop
        val originX = centerX - ((glyph.xMin + glyph.xMax) / 2f / SYMBOL_UNITS_PER_EM) * fontSize
        val originY = centerY - ((glyph.yMin + glyph.yMax) / 2f / SYMBOL_UNITS_PER_EM) * fontSize
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, fontSize)
        s.newLineAtOffset(originX, originY)
        s.showText(String(Character.toChars(glyph.codePoint)))
        s.endText()
    }

    private fun appendNotesExtendedPages(plan: PcSheetPdfRenderPlan) {
        val lines = wrapForRulesByChars(notesText(plan), BASE_V1_NOTES_WRAP_CHARS)
        val overflow = lines.drop(BASE_V1_NOTES_CAPACITY)
        if (overflow.isEmpty()) return

        val pages = pageCount(overflow.size, NOTES_CONTINUATION_CAPACITY)
        repeat(pages) { pageIndex ->
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderNotesContinuationPage(
                page = page,
                lines = overflow.pageSlice(pageIndex, NOTES_CONTINUATION_CAPACITY),
                pageIndex = pageIndex,
            )
        }
    }

    private fun renderNotesContinuationPage(
        page: PDPage,
        lines: List<String>,
        pageIndex: Int,
    ) {
        val prefix = "V1X NOTES P${pageIndex + 1}"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            s.drawForm(resources.forms[4])
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { }
        appendLayer(page, "$prefix - VALUES") { s ->
            lines.take(NOTES_COLUMN_CAPACITY).forEachIndexed { index, line ->
                ruleText(
                    s,
                    resources.fira,
                    Rule(25f, 267.5f, NOTES_RULES[index]),
                    line,
                    8.4f,
                )
            }
            lines.drop(NOTES_COLUMN_CAPACITY)
                .take(NOTES_COLUMN_CAPACITY)
                .forEachIndexed { index, line ->
                    ruleText(
                        s,
                        resources.fira,
                        Rule(311.669f, 583.795f, NOTES_RULES[index]),
                        line,
                        8.4f,
                    )
                }
        }
        appendLayer(page, "$prefix - MARKERS") { }
    }

    private fun notesText(plan: PcSheetPdfRenderPlan): String {
        val sheet = plan.snapshot.aggregate.sheet
        return buildList {
            sheet.generalNotes.trim().takeIf { it.isNotEmpty() }?.let(::add)
            sheet.noteCards.sortedBy { it.sortOrder }.forEach { card ->
                val body = card.content.trim()
                if (body.isNotEmpty()) add("${card.title}: $body")
            }
        }.joinToString("\n\n")
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

    private fun drawResourceCounter(
        s: PDFormContentStream,
        font: PDFont,
        startX: Float,
        centerTop: Float,
        current: Int,
        maximum: Int,
    ) {
        require(current in 0..maximum)
        require(maximum in 1..RESOURCE_SYMBOL_MAXIMUM)
        repeat(maximum) { index ->
            // Para Hoja de PJ v8 semantics: outline = available, filled = spent.
            val available = index < current
            val codePoint = if (available) 0xE200 else 0xE201
            centeredText(
                s,
                font,
                startX + index * 22f,
                centerTop - 8f,
                17f,
                16f,
                String(Character.toChars(codePoint)),
                12.8f,
            )
        }
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

    private data class V8Glyph(
        val codePoint: Int,
        val xMin: Int,
        val yMin: Int,
        val xMax: Int,
        val yMax: Int,
    )

    private data class SpellContinuationBlock(
        val level: Int,
        val baseCapacity: Int,
        val rules: List<Rule>,
        val checkboxCenters: List<Pair<Float, Float>>,
        val slotRule: Rule?,
    )

    private data class TreasureEntry(
        val label: String,
        val value: String?,
    )

    private data class ResourceRenderRow(
        val name: String,
        val currentValue: Int,
        val maximum: Int?,
        val recoveryAndDetail: String,
        val sortOrder: Int,
        val sourceRank: Int,
    )

    private data class ResourceRenderLine(
        val name: String,
        val currentValue: Int?,
        val maximum: Int?,
        val numericValue: String?,
        val recoveryAndDetail: String,
    )

    private data class OptionRenderLine(
        val kind: String,
        val name: String,
        val detail: String,
        val active: Boolean?,
    )

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
        val condensed: PDFont,
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
                    condensed = resourceFont(document, resourceLoader, BARLOW_CONDENSED_RESOURCE),
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
        const val BARLOW_CONDENSED_RESOURCE = "fonts/pdf/text/BarlowCondensed-Bold.ttf"
        const val SYMBOL_RESOURCE = "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf"

        const val MODULES_PER_PAGE = 6
        const val SKILLS_PER_MODULE = 5
        const val BOTTOM_LINES_PER_PAGE = 15
        const val BOTTOM_TEXT_WIDTH = 150f

        const val BASE_V1_TRAIT_NAME_CAPACITY = 6
        const val TRAIT_LEFT_ROWS = 3
        const val TRAIT_OTHER_CAPACITY = 12
        const val TRAIT_DETAIL_ROWS = 4
        const val TRAIT_NOTE_ROWS = 5
        const val TRAIT_LEFT_TEXT_WIDTH = 154f
        const val TRAIT_RIGHT_TEXT_WIDTH = 365f

        const val RESOURCE_FIRST_RULE_TOP = 128.5f
        const val RESOURCE_ROWS_PER_PAGE = 10
        const val RESOURCE_STEP = 20f
        const val RESOURCE_SYMBOL_MAXIMUM = 6
        const val RESOURCE_NAME_TEXT_WIDTH = 171f
        const val RESOURCE_DETAIL_TEXT_WIDTH = 180f
        const val OPTION_HEADING_TOP = 354f
        const val OPTION_HEADER_TOP = 389f
        const val OPTION_FIRST_RULE_TOP = 426.5f
        const val OPTION_ROWS_PER_PAGE = 16
        const val OPTION_STEP = 20f
        const val OPTION_NAME_TEXT_WIDTH = 108f
        const val OPTION_DETAIL_TEXT_WIDTH = 339f

        const val SYMBOL_UNITS_PER_EM = 2048f

        val SPELL_CONTINUATION_BLOCKS = listOf(
            spellContinuationBlock(0, 8, 28.2f, 117.4f, 4, null),
            spellContinuationBlock(1, 10, 28.2f, 327.2f, 10, 317f),
            spellContinuationBlock(2, 9, 28.2f, 573.8f, 9, 563.7f),
            spellContinuationBlock(3, 10, 215.3f, 117.4f, 10, 107.3f),
            spellContinuationBlock(4, 10, 215.3f, 356.5f, 10, 346.5f),
            spellContinuationBlock(5, 8, 215.3f, 592.6f, 8, 582.6f),
            spellContinuationBlock(6, 8, 408.1f, 117.4f, 8, 107.3f),
            spellContinuationBlock(7, 6, 408.1f, 315.8f, 7, 305.7f),
            spellContinuationBlock(8, 6, 408.1f, 494.4f, 6, 484.3f),
            spellContinuationBlock(9, 5, 408.1f, 653.2f, 5, 643.1f),
        )

        private fun spellContinuationBlock(
            level: Int,
            baseCapacity: Int,
            x: Float,
            firstTop: Float,
            count: Int,
            slotY: Float?,
        ): SpellContinuationBlock = SpellContinuationBlock(
            level = level,
            baseCapacity = baseCapacity,
            rules = (0 until count).map { index ->
                Rule(x + 11.3f, x + 174f, firstTop + 12.6f + index * 19.84f)
            },
            checkboxCenters = (0 until count).map { index ->
                (x + 4.9f) to (firstTop + 6.1f + index * 19.84f)
            },
            slotRule = slotY?.let { Rule(x + 39f, x + 79f, it) },
        )

        const val BASE_V1_NOTES_WRAP_CHARS = 68
        const val NOTES_COLUMN_CAPACITY = 17
        const val BASE_V1_NOTES_CAPACITY = NOTES_COLUMN_CAPACITY * 2
        const val NOTES_CONTINUATION_CAPACITY = NOTES_COLUMN_CAPACITY * 2
        val NOTES_RULES = listOf(
            109.5f, 129.5f, 149.5f, 169f, 189f, 209f, 229f, 248.5f, 268.5f,
            288.5f, 308f, 328f, 348f, 367.5f, 387.5f, 407.5f, 427f,
        )

        const val BASE_V1_COMBAT_CAPACITY = 5
        const val COMBAT_LINES_PER_PAGE = 29
        const val COMBAT_FIRST_RULE_TOP = 128f
        const val COMBAT_STEP = 21f
        const val COMBAT_TEXT_WIDTH = 556f
        const val BASE_V1_EQUIPMENT_CAPACITY = 54
        const val BASE_V1_SPECIAL_CAPACITY = 13
        const val BASE_V1_VALUABLE_CAPACITY = 4
        const val INVENTORY_ORDINARY_CAPACITY = 54
        const val INVENTORY_TREASURE_CAPACITY = 4
        const val INVENTORY_SPECIAL_CAPACITY = 13
        const val INVENTORY_ORDINARY_TEXT_WIDTH = 106f
        const val V1_BASE_SPECIAL_NAME_WIDTH = 82.5f
        const val V1_BASE_SPECIAL_DETAIL_WIDTH = 343f
        val INVENTORY_ORDINARY_COLUMNS = listOf(
            27.5f to 137.5f,
            169.937f to 300.331f,
            311.669f to 442.063f,
        )
        // Owner correction 2026-09-22: use every physical writing row. Run-6 QA deliberately
        // sampled alternating rules, but production continuation must not waste every other line.
        val INVENTORY_ORDINARY_RULES = listOf(
            108.5f, 128.5f, 148.5f, 168f, 188f, 208f, 228f, 247.5f, 267.5f,
            287.5f, 307f, 327f, 347f, 366.5f, 386.5f, 406.5f, 426f, 446f,
        )
        val INVENTORY_TREASURE_RULES = listOf(307f, 347f, 386.5f, 426.5f)
        val INVENTORY_SPECIAL_RULES = listOf(
            522.5f, 542.5f, 562f, 582f, 602f, 622f, 641.5f,
            661.5f, 681.5f, 701f, 721f, 741f, 763.5f,
        )
        val INVENTORY_SPECIAL_CHECK_TOPS = listOf(
            508.770f, 528.612f, 548.455f, 568.297f, 588.140f, 607.982f,
            627.825f, 647.667f, 667.510f, 687.352f, 707.195f, 727.037f,
            746.880f,
        )
        const val SPECIAL_CHECK_X = 113.244f
        const val SPECIAL_CHECK_WIDTH = 9.669f
        const val SPECIAL_CHECK_HEIGHT = 12.287f
        val SPECIAL_LOCATION_LABELS = listOf(
            "cabeza",
            "rostro",
            "cuello",
            "mano izquierda",
            "mano derecha",
            "brazo izquierdo",
            "brazo derecho",
            "pecho",
            "piernas",
            "pies",
        )

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
