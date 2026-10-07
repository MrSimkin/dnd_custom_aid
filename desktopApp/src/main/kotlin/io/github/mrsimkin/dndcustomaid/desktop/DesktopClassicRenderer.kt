package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOptionKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDefenseType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterMovementType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryAmountMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRecoveryCadence
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetWritableTrackerState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBaseLayoutMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBasePageRole
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedPageKind
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetSemanticModule
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBidirectionalContinuation
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetContinuationEndpoint
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetContinuationSurface
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetSemanticRecordRef
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetSemanticRecordRef
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetModuleDemand
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedLayoutSlot
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedLayoutTemplate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedPageComposer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetNotePhysicalLine
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetNotePhysicalLineKind
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPackedNotes
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetWrappedNoteRecord
import io.github.mrsimkin.dndcustomaid.shared.character.packPcSheetNoteColumns
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetNoteRecords
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.StandardCurrencyKind
import io.github.mrsimkin.dndcustomaid.shared.character.standardCurrency
import io.github.mrsimkin.dndcustomaid.shared.character.standardCurrencyKindOrNull
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
import io.github.mrsimkin.dndcustomaid.shared.character.spellAttackModifier
import io.github.mrsimkin.dndcustomaid.shared.character.spellSaveDc
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetWritableUsesTrackerOrNull
import java.awt.Color
import java.io.OutputStream
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle

/**
 * Production renderer for the owner-approved/frozen Classic Run-2 complete family.
 *
 * The three normal pages and all six D-0074 continuation roles are driven by the canonical
 * PcSheetPdfRenderPlan. The frozen visual grammar remains authoritative; bounded continuation
 * preserves canonical/current-state data that the normal pages cannot express without redesign.
 */
internal class DesktopClassicRenderer {
    private val overflowDiagnostics = mutableListOf<String>()
    private val baseClippedTraitIds = mutableSetOf<kotlin.uuid.Uuid>()

    fun renderBase(
        plan: PcSheetPdfRenderPlan,
        output: OutputStream,
    ) {
        require(plan.request.visualFamily == PcSheetVisualFamily.CLASSIC_DND_STYLE) {
            "DesktopClassicRenderer supports Fantasy Sheet (legacy technical id CLASSIC_DND_STYLE)."
        }
        require(plan.baseLayoutMode == PcSheetBaseLayoutMode.FAITHFUL) {
            "Fantasy Sheet production supports the faithful base layout only."
        }
        require(
            plan.mandatoryExtendedPages.all { it == PcSheetExtendedPageKind.CUSTOM_STATISTICS },
        ) {
            "Fantasy Sheet planner-mandated extensions are limited to Custom Statistics; other promoted continuations are data-driven."
        }

        overflowDiagnostics.clear()
        baseClippedTraitIds.clear()
        PDDocument().use { doc ->
            val fonts = DesktopPdfFontRegistry(doc, CLASSIC_THEME)
            val p = DesktopPdfRenderingPrimitives(fonts)

            drawMain(doc, p, plan)
            drawCharacterAndEquipment(doc, p, plan)
            if (plan.basePages.any { it.role == PcSheetBasePageRole.SPELL_LIST }) {
                drawSpells(doc, p, plan)
            }
            appendCustomStatisticsPages(doc, p, plan)
            appendNarrativePages(doc, p, plan)
            appendTraitsPages(doc, p, plan)
            appendCombatPages(doc, p, plan)
            appendResourcesPages(doc, p, plan)
            appendInventoryPages(doc, p, plan)
            appendSpellContinuationPages(doc, p, plan)
            appendNotesPages(doc, p, plan)
            appendReferencePages(doc, p, plan)

            check(overflowDiagnostics.isEmpty()) {
                "Fantasy Sheet production encountered content outside its bounded base/continuation routing:\n" +
                    overflowDiagnostics.joinToString("\n")
            }
            doc.save(output)
        }
    }

    private fun appendCustomStatisticsPages(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val stats = plan.snapshot.customStatistics
        if (
            PcSheetExtendedPageKind.CUSTOM_STATISTICS !in plan.mandatoryExtendedPages ||
            stats.isEmpty
        ) {
            return
        }

        val attributesById = stats.attributes.associateBy { it.attribute.id }
        val customSkillGroups = stats.skills
            .filter { projection ->
                projection.ability.customAttributeId?.let(attributesById::containsKey) == true
            }
            .groupBy { requireNotNull(it.ability.customAttributeId) }

        val customSlices = stats.attributes.flatMap { projection ->
            val skillChunks = customSkillGroups[projection.attribute.id]
                .orEmpty()
                .chunked(CLASSIC_CUSTOM_SKILLS_PER_ATTRIBUTE_PANEL)
                .ifEmpty { listOf(emptyList()) }
            val noteChunks = wrapForChars(projection.attribute.notes.orEmpty(), 40)
                .chunked(CLASSIC_CUSTOM_ATTRIBUTE_NOTE_LINES)
                .map { it.joinToString("\n") }
                .ifEmpty { listOf("") }
            val slices = maxOf(skillChunks.size, noteChunks.size)
            (0 until slices).map { index ->
                CustomAttributeSlice(
                    projection = projection,
                    skills = skillChunks.getOrElse(index) { emptyList() },
                    note = noteChunks.getOrElse(index) { "" },
                )
            }
        }

        val standardSlices = buildList {
            CharacterAbility.entries.forEach { ability ->
                stats.skills
                    .filter { it.ability.builtIn == ability }
                    .chunked(CLASSIC_STANDARD_CUSTOM_SKILLS_PER_GROUP)
                    .forEach { skills ->
                        add(
                            StandardCustomGroupSlice(
                                title = abilityLabel(ability) + " (" + abilityAbbreviation(ability) + ")",
                                skills = skills,
                            ),
                        )
                    }
            }
            val unlinked = stats.skills.filter { projection ->
                projection.ability.builtIn == null &&
                    projection.ability.customAttributeId?.let(attributesById::containsKey) != true
            }
            unlinked.chunked(CLASSIC_STANDARD_CUSTOM_SKILLS_PER_GROUP).forEach { skills ->
                add(StandardCustomGroupSlice(title = "SIN ATRIBUTO", skills = skills))
            }
        }

        val pages = buildList {
            var customOffset = 0
            var standardOffset = 0

            while (customOffset < customSlices.size) {
                val customRemaining = customSlices.size - customOffset
                if (customRemaining > CLASSIC_CUSTOM_ATTRIBUTE_PANELS_PER_ROW) {
                    val count = minOf(
                        CLASSIC_CUSTOM_ATTRIBUTE_PANELS_PER_ATTRIBUTE_ONLY_PAGE,
                        customRemaining,
                    )
                    add(
                        ClassicCustomStatisticsPage(
                            customSlices = customSlices.drop(customOffset).take(count),
                            standardSlices = emptyList(),
                        ),
                    )
                    customOffset += count
                } else {
                    val customPage = customSlices.drop(customOffset)
                    val standardPage = standardSlices
                        .drop(standardOffset)
                        .take(CLASSIC_STANDARD_GROUPS_PER_PAGE)
                    add(
                        ClassicCustomStatisticsPage(
                            customSlices = customPage,
                            standardSlices = standardPage,
                        ),
                    )
                    customOffset = customSlices.size
                    standardOffset += standardPage.size
                }
            }

            while (standardOffset < standardSlices.size) {
                val standardPage = standardSlices
                    .drop(standardOffset)
                    .take(CLASSIC_STANDARD_GROUPS_PER_PAGE)
                add(
                    ClassicCustomStatisticsPage(
                        customSlices = emptyList(),
                        standardSlices = standardPage,
                    ),
                )
                standardOffset += standardPage.size
            }
        }
        check(pages.isNotEmpty()) {
            "Fantasy Custom Statistics requires at least one rendered page."
        }

        pages.forEachIndexed { pageIndex, pageSpec ->
            val page = addPage(doc)
            PDPageContentStream(doc, page).use { s ->
                extendedHeader(s, p, plan.snapshot.aggregate.sheet.name, "ESTADÍSTICAS PERSONALIZADAS")

                val xPositions = listOf(24f, 212f, 400f)
                pageSpec.customSlices.forEachIndexed { index, slice ->
                    val attr = slice.projection.attribute
                    val columnIndex = index % CLASSIC_CUSTOM_ATTRIBUTE_PANELS_PER_ROW
                    val rowIndex = index / CLASSIC_CUSTOM_ATTRIBUTE_PANELS_PER_ROW
                    check(rowIndex < 2) {
                        "Fantasy Custom Statistics page exceeds six native attribute panels."
                    }
                    customAttributePanel(
                        s = s,
                        p = p,
                        x = xPositions[columnIndex],
                        top = 112f + rowIndex * CLASSIC_CUSTOM_ATTRIBUTE_SECOND_ROW_OFFSET,
                        width = 176f,
                        height = 286f,
                        title = attr.name,
                        abbreviation = attr.abbreviation,
                        score = attr.score.toString(),
                        modifier = signed(attr.modifier),
                        save = if (attr.savingThrowEnabled) {
                            slice.projection.savingThrowTotal?.let(::signed).orEmpty()
                        } else {
                            ""
                        },
                        saveTraining = if (
                            attr.savingThrowEnabled && attr.savingThrowProficient
                        ) {
                            Training.PROFICIENT
                        } else {
                            Training.NONE
                        },
                        skills = slice.skills.map { skill ->
                            SkillRow(
                                name = skill.skill.name,
                                total = skill.total?.let(::signed).orEmpty(),
                                training = training(skill.skill.training),
                            )
                        },
                        note = slice.note,
                    )
                }

                if (pageSpec.standardSlices.isNotEmpty()) {
                    check(pageSpec.customSlices.size <= CLASSIC_CUSTOM_ATTRIBUTE_PANELS_PER_ROW) {
                        "Fantasy standard custom-skill groups cannot overlap a second attribute row."
                    }
                    val frameTop = if (pageSpec.customSlices.isEmpty()) 112f else 414f
                    titledFrame(
                        s, p, 24f, frameTop, 564f, 304f,
                        "HABILIDADES PERSONALIZADAS VINCULADAS A ATRIBUTOS ESTÁNDAR",
                    )
                    val groupX = listOf(36f, 224f, 412f)
                    pageSpec.standardSlices.forEachIndexed { index, group ->
                        standardLinkedCustomGroup(
                            s = s,
                            p = p,
                            x = groupX[index],
                            top = frameTop + 36f,
                            width = 164f,
                            title = group.title,
                            skills = group.skills.map { skill ->
                                SkillRow(
                                    name = skill.skill.name,
                                    total = skill.total?.let(::signed).orEmpty(),
                                    training = training(skill.skill.training),
                                )
                            },
                        )
                    }
                    text(
                        s, p, 36f, frameTop + 236f, 540f, 52f,
                        "Cada habilidad conserva visible su atributo gobernante. Las habilidades de un atributo personalizado se agrupan dentro de ese atributo; las vinculadas a un atributo estándar aparecen bajo su nombre y abreviatura.",
                        PdfTypographyRole.NOTE_TEXT, 8.2f, 7.2f, wrap = true, maxLines = 4,
                        vertical = PdfVerticalAlignment.TOP,
                    )
                }

                footer(
                    s, p, 4 + pageIndex,
                    "EXTENSIÓN / ESTADÍSTICAS PERSONALIZADAS",
                )
            }
        }
    }

    private fun pageCount(size: Int, capacity: Int): Int =
        if (size <= 0) 0 else (size + capacity - 1) / capacity

    private fun wrapForChars(text: String, maxChars: Int): List<String> {
        val clean = text.trim()
        if (clean.isEmpty()) return emptyList()
        return clean
            .split(Regex("\\n+"))
            .flatMap { paragraph ->
                val words = paragraph.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
                if (words.isEmpty()) {
                    emptyList()
                } else {
                    buildList {
                        var current = ""
                        words.forEach { word ->
                            if (current.isEmpty()) {
                                current = word
                            } else if (current.length + 1 + word.length <= maxChars) {
                                current += " " + word
                            } else {
                                add(current)
                                current = word
                            }
                        }
                        if (current.isNotEmpty()) add(current)
                    }
                }
            }
    }

    private fun classicBaseExcerpt(value: String, maxChars: Int, maxLines: Int): String =
        wrapForChars(value, maxChars).take(maxLines).joinToString("\n")

    private fun classicNarrativeContinuation(
        stableKey: String,
        sectionName: String,
        targetIndex: Int = 1,
    ): PcSheetBidirectionalContinuation =
        PcSheetBidirectionalContinuation(
            record = PcSheetSemanticRecordRef(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                stableKey = "background:$stableKey",
                displayName = sectionName,
            ),
            source = PcSheetContinuationEndpoint(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                sectionName = sectionName,
                surface = PcSheetContinuationSurface.NORMAL,
            ),
            target = PcSheetContinuationEndpoint(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                sectionName = sectionName,
                surface = PcSheetContinuationSurface.EXTENDED,
                extendedIndex = targetIndex,
            ),
        )

    private fun classicBaseNarrativeExcerpt(
        value: String,
        maxChars: Int,
        maxLines: Int,
        stableKey: String,
        sectionName: String,
    ): String {
        val lines = wrapForChars(value, maxChars)
        if (lines.size <= maxLines) return lines.joinToString("\n")
        val visible = lines.take((maxLines - 1).coerceAtLeast(0))
        return (visible + classicNarrativeContinuation(stableKey, sectionName).sourceMarker())
            .joinToString("\n")
    }

    private fun appendNarrativePages(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val modules = classicNarrativeModules(plan)
        if (modules.isEmpty()) return

        modules.chunked(CLASSIC_NARRATIVE_MODULES_PER_PAGE).forEach { pageModules ->
            val page = addPage(doc)
            PDPageContentStream(doc, page).use { s ->
                extendedHeader(s, p, plan.snapshot.aggregate.sheet.name, "HISTORIA Y PERSONALIDAD")
                pageModules.forEachIndexed { index, module ->
                    val placement = CLASSIC_NARRATIVE_MODULE_PLACEMENTS[index]
                    titledFrame(
                        s, p,
                        placement.first, placement.second,
                        CLASSIC_NARRATIVE_MODULE_WIDTH,
                        CLASSIC_NARRATIVE_MODULE_HEIGHT,
                        module.heading,
                    )
                    ruledTextArea(
                        s = s,
                        p = p,
                        x = placement.first + 10f,
                        top = placement.second + 32f,
                        width = CLASSIC_NARRATIVE_MODULE_WIDTH - 20f,
                        height = CLASSIC_NARRATIVE_TEXT_HEIGHT,
                        content = module.lines,
                        fontSize = CLASSIC_NARRATIVE_BODY_SIZE,
                        lineGap = CLASSIC_NARRATIVE_ROW_STEP,
                    )
                }
                footer(s, p, doc.numberOfPages, "EXTENSIÓN / HISTORIA Y PERSONALIDAD")
            }
        }
    }

    private fun classicNarrativeModules(plan: PcSheetPdfRenderPlan): List<ClassicNarrativeModule> {
        val background = plan.snapshot.aggregate.sheet.background
        val modules = mutableListOf<ClassicNarrativeModule>()

        fun normalEndpoint(sectionName: String) =
            PcSheetContinuationEndpoint(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                sectionName = sectionName,
                surface = PcSheetContinuationSurface.NORMAL,
            )

        fun extendedEndpoint(sectionName: String, index: Int) =
            PcSheetContinuationEndpoint(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                sectionName = sectionName,
                surface = PcSheetContinuationSurface.EXTENDED,
                extendedIndex = index,
            )

        fun addFlow(
            stableKey: String,
            heading: String,
            sectionName: String,
            value: String,
            baseMaxChars: Int?,
            baseLines: Int?,
        ) {
            val clean = value.trim()
            if (clean.isEmpty()) return
            val record = PcSheetSemanticRecordRef(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                stableKey = "background:$stableKey",
                displayName = sectionName,
            )
            val fromNormal = baseMaxChars != null && baseLines != null
            val remainingText = if (fromNormal) {
                val baseWrapped = wrapForChars(clean, requireNotNull(baseMaxChars))
                if (baseWrapped.size <= requireNotNull(baseLines)) return
                baseWrapped.drop((baseLines - 1).coerceAtLeast(0)).joinToString(" ")
            } else {
                clean
            }
            var remaining = wrapForChars(remainingText, CLASSIC_NARRATIVE_CHARS_PER_LINE)
            var segmentIndex = 1
            while (remaining.isNotEmpty()) {
                val inbound = when {
                    segmentIndex == 1 && fromNormal ->
                        PcSheetBidirectionalContinuation(
                            record = record,
                            source = normalEndpoint(sectionName),
                            target = extendedEndpoint(sectionName, segmentIndex),
                        ).targetMarker()
                    segmentIndex > 1 ->
                        PcSheetBidirectionalContinuation(
                            record = record,
                            source = extendedEndpoint(sectionName, segmentIndex - 1),
                            target = extendedEndpoint(sectionName, segmentIndex),
                        ).targetMarker()
                    else -> null
                }
                val inboundRows = if (inbound == null) 0 else 1
                val capacityWithoutOutbound = CLASSIC_NARRATIVE_ROWS_PER_MODULE - inboundRows
                val hasMore = remaining.size > capacityWithoutOutbound
                val bodyCapacity = capacityWithoutOutbound - if (hasMore) 1 else 0
                require(bodyCapacity > 0) {
                    "Fantasy native narrative module leaves no room for semantic content."
                }
                val body = remaining.take(bodyCapacity)
                remaining = remaining.drop(body.size)
                val lines = buildList {
                    inbound?.let(::add)
                    addAll(body)
                    if (remaining.isNotEmpty()) {
                        add(
                            PcSheetBidirectionalContinuation(
                                record = record,
                                source = extendedEndpoint(sectionName, segmentIndex),
                                target = extendedEndpoint(sectionName, segmentIndex + 1),
                            ).sourceMarker(),
                        )
                    }
                }
                modules += ClassicNarrativeModule(
                    heading = if (segmentIndex == 1) heading else "$heading · CONT.",
                    lines = lines,
                )
                segmentIndex += 1
            }
        }

        val narrative = listOf(background.name, background.summary, background.story)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
        addFlow(
            "story", "HISTORIA / TRASFONDO", "HISTORIA",
            narrative, CLASSIC_BACKGROUND_NARRATIVE_CHARS, CLASSIC_BACKGROUND_NARRATIVE_LINES,
        )
        addFlow(
            "personality", "RASGO DE PERSONALIDAD", "PERSONALIDAD",
            background.personalityTraits.takeIf { it.isNotBlank() }?.let { "Rasgo: $it" }.orEmpty(),
            CLASSIC_BACKGROUND_DETAIL_CHARS, CLASSIC_BACKGROUND_DETAIL_LINES,
        )
        addFlow(
            "ideals", "IDEAL", "IDEALES",
            background.ideals.takeIf { it.isNotBlank() }?.let { "Ideal: $it" }.orEmpty(),
            CLASSIC_BACKGROUND_DETAIL_CHARS, CLASSIC_BACKGROUND_DETAIL_LINES,
        )
        addFlow(
            "bonds", "VÍNCULO", "VÍNCULOS",
            background.bonds.takeIf { it.isNotBlank() }?.let { "Vínculo: $it" }.orEmpty(),
            CLASSIC_BACKGROUND_DETAIL_CHARS, CLASSIC_BACKGROUND_DETAIL_LINES,
        )
        addFlow(
            "flaws", "DEFECTO", "DEFECTOS",
            background.flaws.takeIf { it.isNotBlank() }?.let { "Defecto: $it" }.orEmpty(),
            CLASSIC_BACKGROUND_DETAIL_CHARS, CLASSIC_BACKGROUND_DETAIL_LINES,
        )
        addFlow(
            "religion", "FE / RELIGIÓN", "FE / RELIGIÓN",
            background.religionFaith, null, null,
        )

        return modules
    }

    private fun appendTraitsPages(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val orderedTraits = sheet.traits
            .sortedBy { it.sortOrder }
            .filterNot { isSpeciesIdentityTrait(it, plan) }
        val classBase = orderedTraits.filter { it.type == CharacterTraitType.CLASS }
            .take(BASE_CLASS_TRAIT_CAPACITY)
        val speciesBase = orderedTraits.filter { it.type == CharacterTraitType.SPECIES_RACE }
            .take(BASE_SPECIES_TRAIT_CAPACITY)
        val featBase = orderedTraits.filter { it.type == CharacterTraitType.FEAT }
            .take(BASE_FEAT_CAPACITY)
        val firstPageIds = (classBase + speciesBase + featBase).mapTo(mutableSetOf()) { it.id }
        val additionalBase = orderedTraits.filter { it.id !in firstPageIds }
            .take(BASE_ADDITIONAL_TRAIT_CAPACITY)
        val baseDisplayed = classBase + speciesBase + featBase + additionalBase
        val baseDisplayedIds = baseDisplayed.mapTo(mutableSetOf()) { it.id }

        val overflowTraits = orderedTraits.filter {
            it.id !in baseDisplayedIds && !traitHasDedicatedActionOrResource(it, plan)
        }
        val clippedClassTraitIds = classicBaseClassProjection(classBase).clippedTraitIds
        val referenceTraits = baseDisplayed.filter { trait ->
            !traitHasDedicatedActionOrResource(trait, plan) &&
                (
                    trait.id in clippedClassTraitIds ||
                    trait.id in baseClippedTraitIds ||
                    traitNeedsReferenceContinuation(trait)
                )
        }
        val continuationIds = (overflowTraits + referenceTraits)
            .mapTo(mutableSetOf()) { it.id }
        val traitEntries = orderedTraits
            .filter { it.id in continuationIds }
            .flatMap(::traitFeatureSlices)

        val leftEntries = traitEntries.filter {
            it.type == CharacterTraitType.CLASS || it.type == CharacterTraitType.FEAT
        }
        val rightEntries = traitEntries.filter {
            it.type != CharacterTraitType.CLASS && it.type != CharacterTraitType.FEAT
        }

        val columns =
            packClassicTraitColumns(
                entries = leftEntries,
                preferredSlotId = CLASSIC_TRAIT_LEFT_SLOT_ID,
                heading = "RASGOS Y CARACTERÍSTICAS - CONTINUACIÓN",
            ) +
                packClassicTraitColumns(
                    entries = rightEntries,
                    preferredSlotId = CLASSIC_TRAIT_RIGHT_SLOT_ID,
                    heading = "RASGOS DE RAZA / TRASFONDO / OTROS",
                )
        if (columns.isEmpty()) return

        var demands = listOf(
            PcSheetModuleDemand(
                module = PcSheetSemanticModule.TRAITS,
                remainingUnits = columns.size,
            ),
        )
        val remainingColumns = columns.toMutableList()

        while (demands.isNotEmpty()) {
            val step = requireNotNull(
                PcSheetExtendedPageComposer.composeNextPage(
                    demands = demands,
                    layouts = classicTraitCompositionLayouts(),
                ),
            )
            val placements = step.page.placements
                .filter { it.module == PcSheetSemanticModule.TRAITS }
                .sortedBy { placement ->
                    when (placement.slotId) {
                        CLASSIC_TRAIT_LEFT_SLOT_ID -> 0
                        CLASSIC_TRAIT_RIGHT_SLOT_ID -> 1
                        else -> error("Unknown Fantasy Traits slot: ${placement.slotId}")
                    }
                }
            val pageColumns = placements.map { placement ->
                check(placement.consumedUnits == 1) {
                    "Fantasy Traits native-frame placement must consume exactly one packed column."
                }
                val preferredIndex = remainingColumns.indexOfFirst { column ->
                    column.preferredSlotId == placement.slotId
                }
                val selectedIndex = if (preferredIndex >= 0) preferredIndex else 0
                placement.slotId to remainingColumns.removeAt(selectedIndex)
            }

            val page = addPage(doc)
            PDPageContentStream(doc, page).use { s ->
                extendedHeader(
                    s,
                    p,
                    sheet.name,
                    "RASGOS Y CARACTERÍSTICAS",
                )

                pageColumns.forEach { (slotId, column) ->
                    val frameX = when (slotId) {
                        CLASSIC_TRAIT_LEFT_SLOT_ID -> 24f
                        CLASSIC_TRAIT_RIGHT_SLOT_ID -> 312f
                        else -> error("Unknown Fantasy Traits slot: $slotId")
                    }
                    val contentX = frameX + 12f
                    titledFrame(
                        s,
                        p,
                        frameX,
                        CLASSIC_TRAIT_FRAME_TOP,
                        CLASSIC_TRAIT_FRAME_WIDTH,
                        CLASSIC_TRAIT_FRAME_HEIGHT,
                        column.heading,
                    )
                    continuousFeatureEntries(
                        s = s,
                        p = p,
                        x = contentX,
                        top = CLASSIC_TRAIT_CONTENT_TOP,
                        width = CLASSIC_TRAIT_CONTENT_WIDTH,
                        height = CLASSIC_TRAIT_CONTENT_HEIGHT,
                        entries = column.entries,
                    )
                }

                footer(
                    s,
                    p,
                    doc.numberOfPages,
                    "EXTENSIÓN / RASGOS Y CARACTERÍSTICAS",
                )
            }

            demands = step.remainingDemands
        }

        check(remainingColumns.isEmpty()) {
            "Fantasy Traits compositor did not consume every packed native frame."
        }
    }

    private fun classicTraitCompositionLayouts(): List<PcSheetExtendedLayoutTemplate> =
        listOf(
            PcSheetExtendedLayoutTemplate(
                id = CLASSIC_TRAIT_SINGLE_LAYOUT_ID,
                slots = listOf(
                    PcSheetExtendedLayoutSlot(
                        id = CLASSIC_TRAIT_LEFT_SLOT_ID,
                        capacityByModule = mapOf(PcSheetSemanticModule.TRAITS to 1),
                    ),
                ),
                priority = 0,
            ),
            PcSheetExtendedLayoutTemplate(
                id = CLASSIC_TRAIT_TWO_COLUMN_LAYOUT_ID,
                slots = listOf(
                    PcSheetExtendedLayoutSlot(
                        id = CLASSIC_TRAIT_LEFT_SLOT_ID,
                        capacityByModule = mapOf(PcSheetSemanticModule.TRAITS to 1),
                    ),
                    PcSheetExtendedLayoutSlot(
                        id = CLASSIC_TRAIT_RIGHT_SLOT_ID,
                        capacityByModule = mapOf(PcSheetSemanticModule.TRAITS to 1),
                    ),
                ),
                priority = 1,
            ),
        )

    private fun packClassicTraitColumns(
        entries: List<ClassicFeature>,
        preferredSlotId: String,
        heading: String,
    ): List<ClassicTraitColumn> {
        if (entries.isEmpty()) return emptyList()

        val columns = mutableListOf<ClassicTraitColumn>()
        var current = mutableListOf<ClassicFeature>()
        var usedRows = 0

        fun flush() {
            if (current.isNotEmpty()) {
                columns += ClassicTraitColumn(
                    preferredSlotId = preferredSlotId,
                    heading = heading,
                    entries = current.toList(),
                )
                current = mutableListOf()
                usedRows = 0
            }
        }

        entries.forEach { entry ->
            val rows = classicFeaturePhysicalRows(entry)
            require(rows <= CLASSIC_TRAIT_PHYSICAL_ROWS_PER_FRAME) {
                "Fantasy Trait slice exceeds one native continuation frame: ${entry.name}"
            }
            if (
                current.isNotEmpty() &&
                usedRows + rows > CLASSIC_TRAIT_PHYSICAL_ROWS_PER_FRAME
            ) {
                flush()
            }
            current += entry
            usedRows += rows
            if (usedRows == CLASSIC_TRAIT_PHYSICAL_ROWS_PER_FRAME) {
                flush()
            }
        }

        flush()
        return columns
    }

    private fun classicReferenceFeatures(plan: PcSheetPdfRenderPlan): List<ClassicFeature> {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val closure = aggregate.closure
        val successor = aggregate.successor
        val entries = mutableListOf<Pair<String, String>>()

        fun add(label: String, value: String) {
            val clean = value.trim()
            if (clean.isNotEmpty()) entries += label to clean
        }

        fun addOverflow(label: String, value: String, maxChars: Int, baseLines: Int) {
            val overflow = wrapForChars(value, maxChars).drop(baseLines)
            if (overflow.isNotEmpty()) add("$label (cont.)", overflow.joinToString(" "))
        }

        val background = sheet.background
        val backgroundName = successor.backgroundIdentity?.name
            ?.trim()?.takeIf { it.isNotEmpty() }
            ?: background.name.trim()
        val speciesName = successor.subraceIdentity?.name
            ?.trim()?.takeIf { it.isNotEmpty() }
            ?: successor.speciesIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
            ?: background.race.trim()
        val orderedClasses = sheet.classes.sortedBy { it.sortOrder }
        val classSummary = orderedClasses.joinToString(" / ") { "${it.name} ${it.level}" }
        val subclassSummary = orderedClasses.mapNotNull { classLevel ->
            classLevel.subclassName?.trim()?.takeIf { it.isNotEmpty() }
        }.joinToString(" / ")

        if (sheet.name.trim().length > CLASSIC_HEADER_NAME_CHARS) add("Nombre", sheet.name)
        if (backgroundName.length > CLASSIC_IDENTITY_VALUE_CHARS) add("Trasfondo", backgroundName)
        if (classSummary.length > CLASSIC_IDENTITY_VALUE_CHARS) add("Clases", classSummary)
        if (speciesName.length > CLASSIC_IDENTITY_VALUE_CHARS) add("Raza", speciesName)
        if (subclassSummary.length > CLASSIC_IDENTITY_VALUE_CHARS) add("Subclases", subclassSummary)

        plan.snapshot.customStatistics.attributes.forEach { projection ->
            val attribute = projection.attribute
            val heading = "${attribute.name} (${attribute.abbreviation})"
            if (heading.length > CLASSIC_CUSTOM_ATTRIBUTE_TITLE_CHARS) {
                add("Atributo personalizado", heading)
            }
        }
        plan.snapshot.customStatistics.skills.forEach { projection ->
            if (projection.skill.name.length > CLASSIC_CUSTOM_SKILL_NAME_CHARS) {
                add("Habilidad personalizada", projection.skill.name)
            }
        }
        sheet.spells
            .filter { it.name.length > CLASSIC_SPELL_NAME_CHARS }
            .forEach { spell ->
                add("Conjuro", "Nivel ${spell.level} · ${spell.name}")
            }

        if (
            successor.subraceIdentity != null &&
            successor.speciesIdentity?.name?.isNotBlank() == true
        ) {
            add("Raza", requireNotNull(successor.speciesIdentity).name)
        }
        if (sheet.status != CharacterStatus.ACTIVE) {
            add("Estado", characterStatusLabel(sheet.status))
        }

        if (closure.progressMode == CharacterProgressMode.MILESTONE) {
            add("Progreso", closure.milestoneProgress)
        }
        if (sheet.tempHp != 0) add("PG temporales", sheet.tempHp.toString())

        sheet.spellSlots.filter { it.totalSlots > CLASSIC_BASE_SLOT_MARKERS }.forEach { slot ->
            add(
                "Espacios de conjuro",
                "Nivel ${slot.level}: ${slot.totalSlots} totales · ${slot.spentSlots} gastados",
            )
        }

        sheet.weaponMasteries.sortedBy { it.sortOrder }.forEach { mastery ->
            add(
                "Maestría",
                listOf(
                    mastery.weaponName + " - " + mastery.masteryName,
                    mastery.source.orEmpty(),
                    mastery.notes.orEmpty(),
                ).filter { it.isNotBlank() }.joinToString(" · "),
            )
        }

        if (closure.exhaustionLevel > 0) add("Agotamiento", closure.exhaustionLevel.toString())
        closure.concentration?.let { concentration ->
            add(
                "Concentración",
                listOf(concentration.name, concentration.notes.orEmpty())
                    .filter { it.isNotBlank() }.joinToString(" · "),
            )
        }
        closure.conditions.sortedBy { it.sortOrder }.forEach { condition ->
            add(
                "Condición",
                listOf(condition.name, condition.source.orEmpty(), condition.notes.orEmpty())
                    .filter { it.isNotBlank() }.joinToString(" · "),
            )
        }
        closure.defenses.sortedBy { it.sortOrder }.forEach { defense ->
            add(
                defenseTypeLabel(defense.type),
                listOf(defense.name, defense.source.orEmpty(), defense.notes.orEmpty())
                    .filter { it.isNotBlank() }.joinToString(" · "),
            )
        }
        closure.movements.sortedBy { it.sortOrder }.forEach { movement ->
            add(
                "Movimiento",
                buildList {
                    add(movementTypeLabel(movement.type) + " - " + movement.name)
                    movement.speedFeet?.let { add("$it ft") }
                    movement.notes?.takeIf { it.isNotBlank() }?.let(::add)
                }.joinToString(" · "),
            )
        }
        closure.senses.sortedBy { it.sortOrder }.forEach { sense ->
            add(
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
                add(
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

        val spellSourceOrder = sheet.spellcastingSources.associate { it.id to it.sortOrder }
        val spellSources = sheet.spellcastingSources.associateBy { it.id }
        successor.spellcastingProfiles
            .sortedBy { spellSourceOrder[it.sourceId] ?: Int.MAX_VALUE }
            .drop(1)
            .forEach { profile ->
                val sourceName = spellSources[profile.sourceId]?.name ?: "Fuente mágica"
                val ability = when {
                    profile.ability.builtIn != null ->
                        abilityAbbreviation(requireNotNull(profile.ability.builtIn))
                    profile.ability.customAttributeId != null -> successor.customAttributes
                        .firstOrNull { it.id == profile.ability.customAttributeId }
                        ?.abbreviation.orEmpty()
                    else -> ""
                }
                add(
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
            add(
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

        sheet.companions.sortedBy { it.sortOrder }.forEachIndexed { index, companion ->
            val displayedCompanion = companion.name +
                companion.kind.takeIf { it.isNotBlank() }?.let { " ($it)" }.orEmpty()
            val hasReferenceDetail =
                displayedCompanion.length > CLASSIC_BASE_ALLY_VALUE_CHARS ||
                !companion.source.isNullOrBlank() ||
                    companion.armorClass != null ||
                    companion.maxHp != null ||
                    companion.currentHp != null ||
                    companion.tempHp != 0 ||
                    !companion.speed.isNullOrBlank() ||
                    !companion.abilitySummary.isNullOrBlank() ||
                    !companion.sensesProficiencies.isNullOrBlank() ||
                    companion.traitsActions.isNotBlank() ||
                    !companion.notes.isNullOrBlank()
            if (index >= BASE_COMPANION_CAPACITY || hasReferenceDetail) {
                add(
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
        }

        return entries.flatMap { (label, value) ->
            wrapForChars(value, CLASSIC_TRAIT_BODY_CHARS)
                .chunked(CLASSIC_TRAIT_BODY_LINES)
                .map { it.joinToString("\n") }
                .ifEmpty { listOf("") }
                .mapIndexed { index, chunk ->
                    ClassicFeature(
                        name = if (index == 0) label else "$label (cont.)",
                        source = "Referencia",
                        description = chunk,
                        type = CharacterTraitType.OTHER,
                    )
                }
        }
    }

    private fun appendCombatPages(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val rows = classicCombatReferenceRows(plan).map(::layoutClassicCombatRow)
        if (rows.isEmpty()) return

        packClassicCombatRows(rows).forEach { pageRows ->
            val page = addPage(doc)
            PDPageContentStream(doc, page).use { s ->
                extendedHeader(s, p, plan.snapshot.aggregate.sheet.name, "COMBATE / ACCIONES")
                titledFrame(
                    s,
                    p,
                    24f,
                    112f,
                    564f,
                    606f,
                    "ARMAS Y ACCIONES — CONTINUACIÓN",
                )
                tableHeader(
                    s,
                    p,
                    CLASSIC_COMBAT_TABLE_X,
                    CLASSIC_COMBAT_HEADER_TOP,
                    listOf(
                        CLASSIC_COMBAT_NAME_WIDTH to "Nombre",
                        CLASSIC_COMBAT_BONUS_WIDTH to "Bonif.",
                        CLASSIC_COMBAT_DETAIL_WIDTH to "Daño / notas",
                    ),
                )

                var top = CLASSIC_COMBAT_FIRST_ROW_TOP
                pageRows.forEach { row ->
                    text(
                        s,
                        p,
                        CLASSIC_COMBAT_TABLE_X + 3f,
                        top + 3f,
                        CLASSIC_COMBAT_NAME_WIDTH - 6f,
                        row.height - 6f,
                        row.nameLines.joinToString("\n"),
                        PdfTypographyRole.BODY,
                        8.5f,
                        7.2f,
                        wrap = true,
                        maxLines = row.nameLines.size.coerceAtLeast(1),
                        vertical = PdfVerticalAlignment.TOP,
                    )
                    text(
                        s,
                        p,
                        CLASSIC_COMBAT_TABLE_X + CLASSIC_COMBAT_NAME_WIDTH + 2f,
                        top + 3f,
                        CLASSIC_COMBAT_BONUS_WIDTH - 4f,
                        row.height - 6f,
                        row.bonus,
                        PdfTypographyRole.NUMERIC_COMPACT,
                        9f,
                        8f,
                        align = PdfHorizontalAlignment.CENTER,
                        vertical = PdfVerticalAlignment.TOP,
                    )
                    text(
                        s,
                        p,
                        CLASSIC_COMBAT_TABLE_X + CLASSIC_COMBAT_NAME_WIDTH +
                            CLASSIC_COMBAT_BONUS_WIDTH + 3f,
                        top + 3f,
                        CLASSIC_COMBAT_DETAIL_WIDTH - 6f,
                        row.height - 6f,
                        row.detailLines.joinToString("\n"),
                        PdfTypographyRole.BODY,
                        8.2f,
                        7f,
                        wrap = true,
                        maxLines = row.detailLines.size.coerceAtLeast(1),
                        vertical = PdfVerticalAlignment.TOP,
                    )
                    top += row.height
                    hairline(
                        s,
                        CLASSIC_COMBAT_TABLE_X,
                        top,
                        CLASSIC_COMBAT_TABLE_X + CLASSIC_COMBAT_TABLE_WIDTH,
                        top,
                    )
                }

                val tableBottom = top
                listOf(
                    CLASSIC_COMBAT_TABLE_X + CLASSIC_COMBAT_NAME_WIDTH,
                    CLASSIC_COMBAT_TABLE_X + CLASSIC_COMBAT_NAME_WIDTH + CLASSIC_COMBAT_BONUS_WIDTH,
                ).forEach { x ->
                    hairline(
                        s,
                        x,
                        CLASSIC_COMBAT_HEADER_TOP,
                        x,
                        tableBottom,
                    )
                }
                footer(s, p, doc.numberOfPages, "EXTENSIÓN / COMBATE Y ACCIONES")
            }
        }
    }

    private fun classicCombatReferenceRows(plan: PcSheetPdfRenderPlan): List<ClassicCombatReferenceRow> {
        val aggregate = plan.snapshot.aggregate
        val damageByCombatId = aggregate.successor.combatDamage.associateBy { it.combatEntryId }

        return aggregate.sheet.combatEntries
            .sortedBy { it.sortOrder }
            .mapIndexedNotNull { index, entry ->
                val baseDetail = listOfNotNull(
                    entry.damageEffect.takeIf { it.isNotBlank() },
                    entry.rangeText?.takeIf { it.isNotBlank() },
                    entry.notes?.takeIf { it.isNotBlank() },
                ).joinToString(" · ")
                val structuredDamage = damageByCombatId[entry.id]?.components
                    ?.joinToString(" + ") { component ->
                        component.expression +
                            component.typeText?.takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()
                    }
                    .orEmpty()
                val needsReference =
                    index >= BASE_COMBAT_CAPACITY ||
                        entry.type != CharacterCombatEntryType.ATTACK ||
                        !entry.notes.isNullOrBlank() ||
                        entry.name.length > CLASSIC_COMBAT_NAME_CHARS ||
                        baseDetail.length > CLASSIC_COMBAT_DETAIL_CHARS ||
                        structuredDamage.isNotBlank()

                if (!needsReference) {
                    null
                } else {
                    val structuredExtra = structuredDamage.takeIf {
                        it.isNotBlank() && !it.equals(entry.damageEffect.trim(), ignoreCase = true)
                    }
                    val reverseMarker = if (index < BASE_COMBAT_CAPACITY) {
                        PcSheetBidirectionalContinuation(
                            record = entry.pcSheetSemanticRecordRef(),
                            source = PcSheetContinuationEndpoint(
                                module = PcSheetSemanticModule.COMBAT_ACTIONS,
                                sectionName = "COMBATE / ACCIONES",
                                surface = PcSheetContinuationSurface.NORMAL,
                            ),
                            target = PcSheetContinuationEndpoint(
                                module = PcSheetSemanticModule.COMBAT_ACTIONS,
                                sectionName = "COMBATE / ACCIONES",
                                surface = PcSheetContinuationSurface.EXTENDED,
                                extendedIndex = 1,
                            ),
                        ).targetMarker()
                    } else {
                        null
                    }
                    ClassicCombatReferenceRow(
                        name = combatTypeLabel(entry.type) + " — " + entry.name,
                        bonus = entry.attackModifier?.let(::signed).orEmpty(),
                        detail = buildList {
                            reverseMarker?.let(::add)
                            entry.damageEffect.takeIf { it.isNotBlank() }?.let(::add)
                            entry.rangeText?.takeIf { it.isNotBlank() }?.let { add("Alcance: $it") }
                            entry.notes?.takeIf { it.isNotBlank() }?.let(::add)
                            structuredExtra?.let { add("Daño: $it") }
                        }.joinToString(" · "),
                    )
                }
            }
    }

    private fun layoutClassicCombatRow(row: ClassicCombatReferenceRow): ClassicCombatLayoutRow {
        val nameLines = wrapForChars(row.name, CLASSIC_COMBAT_CONTINUATION_NAME_CHARS)
        val detailLines = wrapForChars(row.detail, CLASSIC_COMBAT_CONTINUATION_DETAIL_CHARS)
        val lineCount = maxOf(1, nameLines.size, detailLines.size)
        val height = maxOf(
            CLASSIC_COMBAT_MIN_ROW_HEIGHT,
            CLASSIC_COMBAT_ROW_VERTICAL_PADDING * 2f +
                lineCount * CLASSIC_COMBAT_ROW_LINE_HEIGHT,
        )
        require(height <= CLASSIC_COMBAT_AVAILABLE_HEIGHT + 0.05f) {
            "Fantasy combat logical row exceeds one full continuation page: ${row.name}"
        }
        return ClassicCombatLayoutRow(
            nameLines = nameLines.ifEmpty { listOf(row.name) },
            bonus = row.bonus,
            detailLines = detailLines.ifEmpty { listOf(row.detail) },
            height = height,
        )
    }

    private fun packClassicCombatRows(
        rows: List<ClassicCombatLayoutRow>,
    ): List<List<ClassicCombatLayoutRow>> {
        val pages = mutableListOf<MutableList<ClassicCombatLayoutRow>>()
        var current = mutableListOf<ClassicCombatLayoutRow>()
        var used = 0f

        rows.forEach { row ->
            if (current.isNotEmpty() && used + row.height > CLASSIC_COMBAT_AVAILABLE_HEIGHT + 0.05f) {
                pages += current
                current = mutableListOf()
                used = 0f
            }
            current += row
            used += row.height
        }
        if (current.isNotEmpty()) pages += current
        return pages
    }

    private fun characterStatusLabel(status: CharacterStatus): String = when (status) {
        CharacterStatus.ACTIVE -> "Activo"
        CharacterStatus.INACTIVE -> "Inactivo"
        CharacterStatus.RETIRED -> "Retirado"
        CharacterStatus.DEAD -> "Muerto"
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

    private fun traitNeedsReferenceContinuation(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
    ): Boolean =
        wrapForChars(traitSummary(trait), CLASSIC_RULED_ENTRY_CHARS).size >
            CLASSIC_RULED_ENTRY_LINES ||
            trait.name.length > CLASSIC_SPECIES_NAME_CHARS ||
            !trait.notes.isNullOrBlank() ||
            trait.maxUses != null ||
            trait.spentUses != 0 ||
            !trait.recovery.isNullOrBlank() ||
            trait.activation?.let { it != CharacterActivationType.PASSIVE } == true

    private fun traitFeatureSlices(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
    ): List<ClassicFeature> {
        val body = buildList {
            trait.description.trim().takeIf { it.isNotEmpty() }?.let(::add)
            trait.source.trim().takeIf { it.isNotEmpty() }?.let { add("Fuente: $it") }
            trait.pcSheetWritableUsesTrackerOrNull()?.let { tracker ->
                add("Usos " + tracker.compactEditableLabel())
            } ?: trait.spentUses.takeIf { it != 0 }?.let { add("Usos gastados: $it") }
            trait.recovery?.trim()?.takeIf { it.isNotEmpty() }?.let { add("Recuperación: $it") }
            trait.activation
                ?.takeIf { it != CharacterActivationType.PASSIVE }
                ?.let { add("Activación: " + activationLabel(it)) }
            trait.notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        }.joinToString(" · ")
        val chunks = wrapForChars(body, CLASSIC_TRAIT_BODY_CHARS)
            .chunked(CLASSIC_TRAIT_BODY_LINES)
            .map { it.joinToString("\n") }
            .ifEmpty { listOf("") }
        return chunks.mapIndexed { index, chunk ->
            ClassicFeature(
                name = if (index == 0) trait.name else trait.name + " (cont.)",
                source = traitTypeLabel(trait.type),
                description = chunk,
                type = trait.type,
            )
        }
    }

    private fun proficiencyFeatureSlices(
        proficiency: io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency,
    ): List<ClassicFeature> {
        val body = buildList {
            proficiency.source.orEmpty().trim().takeIf { it.isNotEmpty() }?.let(::add)
            proficiency.notes.orEmpty().trim().takeIf { it.isNotEmpty() }?.let(::add)
        }.joinToString(" · ")
        val chunks = wrapForChars(body, CLASSIC_TRAIT_BODY_CHARS)
            .chunked(CLASSIC_TRAIT_BODY_LINES)
            .map { it.joinToString("\n") }
            .ifEmpty { listOf("") }
        return chunks.mapIndexed { index, chunk ->
            ClassicFeature(
                name = if (index == 0) proficiency.name else proficiency.name + " (cont.)",
                source = proficiencyTypeLabel(proficiency.type),
                description = chunk,
                type = CharacterTraitType.OTHER,
            )
        }
    }

    private fun traitTypeLabel(type: CharacterTraitType): String = when (type) {
        CharacterTraitType.CLASS -> "Clase"
        CharacterTraitType.SPECIES_RACE -> "Raza"
        CharacterTraitType.BACKGROUND -> "Trasfondo"
        CharacterTraitType.FEAT -> "Dote"
        CharacterTraitType.GIFT_BLESSING -> "Don / bendición"
        CharacterTraitType.OTHER -> "Otro"
    }

    private fun proficiencyTypeLabel(
        type: CharacterProficiencyType,
    ): String = when (type) {
        CharacterProficiencyType.LANGUAGE -> "Idioma"
        CharacterProficiencyType.TOOL -> "Herramienta"
        CharacterProficiencyType.ARMOR -> "Armadura"
        CharacterProficiencyType.WEAPON -> "Arma"
        CharacterProficiencyType.OTHER -> "Competencia"
    }

    private fun activationLabel(type: CharacterActivationType): String = when (type) {
        CharacterActivationType.PASSIVE -> "Pasiva"
        CharacterActivationType.ACTION -> "Acción"
        CharacterActivationType.BONUS_ACTION -> "Acción adicional"
        CharacterActivationType.REACTION -> "Reacción"
        CharacterActivationType.OTHER -> "Otra"
    }

    private fun appendResourcesPages(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val resources = classicResourceRows(plan)
        val options = classicOptionRows(plan)
        var demands = buildList {
            if (resources.isNotEmpty()) {
                add(PcSheetModuleDemand(PcSheetSemanticModule.RESOURCES, resources.size))
            }
            if (options.isNotEmpty()) {
                add(PcSheetModuleDemand(PcSheetSemanticModule.CLASS_CHOICES, options.size))
            }
        }
        if (demands.isEmpty()) return

        var resourceOffset = 0
        var optionOffset = 0

        while (demands.isNotEmpty()) {
            val layouts = classicResourceCompositionLayouts(
                demands.mapTo(mutableSetOf()) { it.module },
            )
            val step = requireNotNull(
                PcSheetExtendedPageComposer.composeNextPage(
                    demands = demands,
                    layouts = layouts,
                ),
            )
            val resourceCount = step.page.placements
                .firstOrNull { it.module == PcSheetSemanticModule.RESOURCES }
                ?.consumedUnits
                ?: 0
            val optionCount = step.page.placements
                .firstOrNull { it.module == PcSheetSemanticModule.CLASS_CHOICES }
                ?.consumedUnits
                ?: 0
            val pageResources = resources.drop(resourceOffset).take(resourceCount)
            val pageOptions = options.drop(optionOffset).take(optionCount)

            val splitLayout = step.page.layoutId == CLASSIC_RESOURCE_OPTIONS_SPLIT_LAYOUT_ID
            val resourcesFullLayout = step.page.layoutId == CLASSIC_RESOURCE_FULL_LAYOUT_ID
            val optionsFullLayout = step.page.layoutId == CLASSIC_OPTION_FULL_LAYOUT_ID
            require(splitLayout || resourcesFullLayout || optionsFullLayout) {
                "Unknown Fantasy Resources/Options layout: ${step.page.layoutId}"
            }

            val page = addPage(doc)
            PDPageContentStream(doc, page).use { s ->
                extendedHeader(
                    s,
                    p,
                    plan.snapshot.aggregate.sheet.name,
                    when {
                        splitLayout -> "RECURSOS Y OPCIONES"
                        resourcesFullLayout -> "RECURSOS"
                        else -> "OPCIONES"
                    },
                )

                if (pageResources.isNotEmpty()) {
                    val frameHeight =
                        if (splitLayout) CLASSIC_RESOURCE_SPLIT_FRAME_HEIGHT
                        else CLASSIC_RESOURCE_FULL_FRAME_HEIGHT
                    titledFrame(s, p, 24f, 112f, 564f, frameHeight, "RECURSOS")
                    resourceTableHeader(s, p, 36f, 148f)
                    val capacity =
                        if (splitLayout) CLASSIC_RESOURCE_ROWS_PER_PAGE
                        else CLASSIC_RESOURCE_FULL_ROWS_PER_PAGE
                    repeat(capacity) { index ->
                        val top = CLASSIC_RESOURCE_FIRST_ROW_TOP + index * CLASSIC_RESOURCE_ROW_STEP
                        pageResources.getOrNull(index)?.let { row ->
                            resourceTableRow(s, p, 36f, top, row)
                        } ?: hairline(s, 36f, top + 42f, 576f, top + 42f)
                    }
                }

                if (pageOptions.isNotEmpty()) {
                    val frameTop =
                        if (splitLayout) CLASSIC_OPTION_SPLIT_FRAME_TOP
                        else CLASSIC_FULL_FRAME_TOP
                    val frameHeight =
                        if (splitLayout) CLASSIC_OPTION_SPLIT_FRAME_HEIGHT
                        else CLASSIC_RESOURCE_FULL_FRAME_HEIGHT
                    titledFrame(
                        s,
                        p,
                        24f,
                        frameTop,
                        564f,
                        frameHeight,
                        "OPCIONES Y ESTADOS RELEVANTES",
                    )
                    val firstTop =
                        if (splitLayout) CLASSIC_OPTION_SPLIT_FIRST_ROW_TOP
                        else CLASSIC_OPTION_FULL_FIRST_ROW_TOP
                    pageOptions.forEachIndexed { index, row ->
                        optionEntry(
                            s,
                            p,
                            36f,
                            firstTop + index * CLASSIC_OPTION_ROW_STEP,
                            540f,
                            row.name,
                            row.source,
                            row.description,
                        )
                    }
                    if (splitLayout) {
                        ruledLines(s, 36f, 682f, 540f, 24f, 1)
                    }
                }

                footer(
                    s,
                    p,
                    doc.numberOfPages,
                    when {
                        splitLayout -> "EXTENSIÓN / RECURSOS Y OPCIONES"
                        resourcesFullLayout -> "EXTENSIÓN / RECURSOS"
                        else -> "EXTENSIÓN / OPCIONES"
                    },
                )
            }

            resourceOffset += resourceCount
            optionOffset += optionCount
            demands = step.remainingDemands
        }

        check(resourceOffset == resources.size) {
            "Fantasy compositor did not consume every Resource row."
        }
        check(optionOffset == options.size) {
            "Fantasy compositor did not consume every Class Choice row."
        }
    }

    private fun classicResourceCompositionLayouts(
        activeModules: Set<PcSheetSemanticModule>,
    ): List<PcSheetExtendedLayoutTemplate> =
        when (activeModules) {
            setOf(PcSheetSemanticModule.RESOURCES, PcSheetSemanticModule.CLASS_CHOICES) ->
                listOf(
                    PcSheetExtendedLayoutTemplate(
                        id = CLASSIC_RESOURCE_OPTIONS_SPLIT_LAYOUT_ID,
                        slots = listOf(
                            PcSheetExtendedLayoutSlot(
                                id = "resources",
                                capacityByModule = mapOf(
                                    PcSheetSemanticModule.RESOURCES to CLASSIC_RESOURCE_ROWS_PER_PAGE,
                                ),
                            ),
                            PcSheetExtendedLayoutSlot(
                                id = "class-choices",
                                capacityByModule = mapOf(
                                    PcSheetSemanticModule.CLASS_CHOICES to CLASSIC_OPTION_ROWS_PER_PAGE,
                                ),
                            ),
                        ),
                    ),
                )

            setOf(PcSheetSemanticModule.RESOURCES) ->
                listOf(
                    PcSheetExtendedLayoutTemplate(
                        id = CLASSIC_RESOURCE_FULL_LAYOUT_ID,
                        slots = listOf(
                            PcSheetExtendedLayoutSlot(
                                id = "resources-full",
                                capacityByModule = mapOf(
                                    PcSheetSemanticModule.RESOURCES to CLASSIC_RESOURCE_FULL_ROWS_PER_PAGE,
                                ),
                            ),
                        ),
                    ),
                )

            setOf(PcSheetSemanticModule.CLASS_CHOICES) ->
                listOf(
                    PcSheetExtendedLayoutTemplate(
                        id = CLASSIC_OPTION_FULL_LAYOUT_ID,
                        slots = listOf(
                            PcSheetExtendedLayoutSlot(
                                id = "class-choices-full",
                                capacityByModule = mapOf(
                                    PcSheetSemanticModule.CLASS_CHOICES to CLASSIC_OPTION_FULL_ROWS_PER_PAGE,
                                ),
                            ),
                        ),
                    ),
                )

            else -> error(
                "Unsupported Fantasy Resources/Options compositor demand: " +
                    activeModules.joinToString(),
            )
        }


    private fun resourceTrackerLabel(
        current: Int,
        maximum: Int?,
    ): String {
        val boundedMaximum = maximum?.takeIf { it > 0 && current in 0..it }
        return boundedMaximum?.let {
            PcSheetWritableTrackerState(
                currentAvailable = current,
                maximum = it,
            ).compactEditableLabel()
        } ?: maximum?.let { "$current/$it" } ?: current.toString()
    }

    private fun classicResourceRows(plan: PcSheetPdfRenderPlan): List<ClassicResourceRow> {
        val aggregate = plan.snapshot.aggregate
        val recoveryById = aggregate.closure.resourceRecovery.associateBy { it.resourceId }
        val configById = aggregate.successor.resourceConfigurations.associateBy { it.resourceId }

        val ordinary = aggregate.sheet.resources.sortedBy { it.sortOrder }.flatMap { resource ->
            val recovery = recoveryById[resource.id]
            val kind = configById[resource.id]?.valueKind ?: CharacterTrackableValueKind.CURRENT_MAX
            val maximum = when (kind) {
                CharacterTrackableValueKind.BINARY -> 1
                CharacterTrackableValueKind.COUNTER,
                CharacterTrackableValueKind.CURRENT_MAX,
                -> resource.maxValue
            }
            val oneUse = maximum == 1
            val legacyRecoveryText = resource.recovery.orEmpty().trim()
            val structuredRecovery = buildList {
                recovery?.cadence?.let(::recoveryLabel)?.takeIf { it.isNotEmpty() }?.let(::add)
                if (!oneUse || recovery?.amountMode != CharacterRecoveryAmountMode.TO_MAX) {
                    recovery?.amountMode
                        ?.let { recoveryAmountLabel(it, recovery.fixedAmount) }
                        ?.takeIf { it.isNotEmpty() }
                        ?.let(::add)
                }
            }
            val recoveryText = structuredRecovery
                .takeIf { it.isNotEmpty() }
                ?.joinToString(" · ")
                ?: legacyRecoveryText
            val notes = listOf(
                recovery?.notes.orEmpty().trim(),
                resource.notes.orEmpty().trim(),
            ).filter { it.isNotEmpty() }.distinct().joinToString(" · ")
            splitClassicResourceRow(
                name = resource.name,
                value = resourceTrackerLabel(resource.currentValue, maximum),
                oneUseAvailable = null,
                recovery = recoveryText,
                source = resource.source.orEmpty().trim(),
                notes = notes,
            )
        }

        val markers = aggregate.successor.customMarkers.sortedBy { it.sortOrder }.flatMap { marker ->
            val maximum = when (marker.valueKind) {
                CharacterTrackableValueKind.BINARY -> 1
                CharacterTrackableValueKind.COUNTER,
                CharacterTrackableValueKind.CURRENT_MAX,
                -> marker.maxValue
            }
            val oneUse = maximum == 1
            splitClassicResourceRow(
                name = marker.name,
                value = resourceTrackerLabel(marker.currentValue, maximum),
                oneUseAvailable = null,
                recovery = buildList {
                    recoveryLabel(marker.recovery.cadence).takeIf { it.isNotEmpty() }?.let(::add)
                    if (!oneUse || marker.recovery.amountMode != CharacterRecoveryAmountMode.TO_MAX) {
                        recoveryAmountLabel(marker.recovery.amountMode, marker.recovery.fixedAmount)
                            .takeIf { it.isNotEmpty() }
                            ?.let(::add)
                    }
                }.joinToString(" · "),
                source = "",
                notes = marker.notes.orEmpty().trim(),
            )
        }

        return ordinary + markers
    }

    private fun splitClassicResourceRow(
        name: String,
        value: String,
        oneUseAvailable: Boolean?,
        recovery: String,
        source: String,
        notes: String,
    ): List<ClassicResourceRow> {
        val cleanName = name.trim()
        val cleanRecovery = recovery.trim()
        val cleanSource = source.trim()
        val nameChunks = wrapForChars(cleanName, CLASSIC_RESOURCE_NAME_CHARS)
            .chunked(CLASSIC_RESOURCE_NAME_LINES_PER_ROW)
            .map { it.joinToString("\n") }
            .ifEmpty { listOf("") }
        val sourceChunks = wrapForChars(cleanSource, CLASSIC_RESOURCE_SOURCE_CHARS)
            .chunked(CLASSIC_RESOURCE_SOURCE_LINES_PER_ROW)
            .map { it.joinToString("\n") }
            .ifEmpty { listOf("") }
        val projectedRecovery = classicBaseExcerpt(
            cleanRecovery,
            CLASSIC_RESOURCE_RECOVERY_CHARS,
            CLASSIC_RESOURCE_NOTE_LINES,
        )
        val detailText = buildList {
            notes.trim().takeIf { it.isNotEmpty() }?.let(::add)
            if (
                wrapForChars(cleanRecovery, CLASSIC_RESOURCE_RECOVERY_CHARS).size >
                CLASSIC_RESOURCE_NOTE_LINES
            ) {
                add("Recuperación completa: $cleanRecovery")
            }
        }.joinToString(" · ")
        val noteChunks = wrapForChars(detailText, CLASSIC_RESOURCE_NOTE_CHARS)
            .chunked(CLASSIC_RESOURCE_NOTE_LINES)
            .map { it.joinToString("\n") }
            .ifEmpty { listOf("") }
        val physicalRows = maxOf(nameChunks.size, sourceChunks.size, noteChunks.size, 1)

        return (0 until physicalRows).map { index ->
            ClassicResourceRow(
                name = nameChunks.getOrNull(index).orEmpty().let { chunk ->
                    if (index == 0 || chunk.isEmpty()) chunk else "  $chunk"
                },
                value = value.takeIf { index == 0 }.orEmpty(),
                oneUseAvailable = oneUseAvailable.takeIf { index == 0 },
                recovery = projectedRecovery.takeIf { index == 0 }.orEmpty(),
                source = sourceChunks.getOrNull(index).orEmpty(),
                notes = noteChunks.getOrNull(index).orEmpty(),
            )
        }
    }

    private fun classicOptionRows(plan: PcSheetPdfRenderPlan): List<ClassicOptionRow> =
        plan.snapshot.aggregate.sheet.classOptions
            .sortedBy { it.sortOrder }
            .flatMap { option ->
                val cleanName = option.name.trim()
                val cleanSource = option.source.orEmpty().trim()
                val nameChunks = wrapForChars(cleanName, CLASSIC_OPTION_NAME_CHARS)
                    .chunked(CLASSIC_OPTION_NAME_LINES_PER_ROW)
                    .map { it.joinToString("\n") }
                    .ifEmpty { listOf("") }
                val sourceChunks = wrapForChars(cleanSource, CLASSIC_OPTION_SOURCE_CHARS)
                    .chunked(CLASSIC_OPTION_SOURCE_LINES_PER_ROW)
                    .map { it.joinToString("\n") }
                    .ifEmpty { listOf("") }
                val description = buildList {
                    add("Tipo: " + optionKindLabel(option.kind))
                    option.effectSummary.trim().takeIf { it.isNotEmpty() }?.let(::add)
                    option.costText?.trim()?.takeIf { it.isNotEmpty() }?.let { add("Coste: $it") }
                    if (!option.active) add("Inactiva")
                    option.notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
                }.joinToString(" · ")
                val detailChunks = wrapForChars(description, CLASSIC_OPTION_DETAIL_CHARS)
                    .chunked(CLASSIC_OPTION_DETAIL_LINES)
                    .map { it.joinToString("\n") }
                    .ifEmpty { listOf("") }
                val physicalRows = maxOf(
                    nameChunks.size,
                    sourceChunks.size,
                    detailChunks.size,
                    1,
                )

                (0 until physicalRows).map { index ->
                    ClassicOptionRow(
                        name = nameChunks.getOrNull(index).orEmpty().let { chunk ->
                            if (index == 0 || chunk.isEmpty()) chunk else "  $chunk"
                        },
                        source = sourceChunks.getOrNull(index).orEmpty(),
                        description = detailChunks.getOrNull(index).orEmpty(),
                    )
                }
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

    private fun appendInventoryPages(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ): Int {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val usageByItem = aggregate.closure.inventoryUsage.associateBy { it.itemId }
        val ordered = sheet.inventoryItems.sortedBy { it.sortOrder }
        val baseIds = ordered.take(BASE_EQUIPMENT_CAPACITY).mapTo(mutableSetOf()) { it.id }

        val specialItems = ordered.filter { item ->
            item.special || item.attuned
        }
        val specialIds = specialItems.mapTo(mutableSetOf()) { it.id }

        val ordinaryRows = ordered
            .filter { it.id !in specialIds }
            .filter { item ->
                item.id !in baseIds ||
                    item.name.length > CLASSIC_BASE_INVENTORY_NAME_CHARS
            }
            .flatMap { item -> classicInventoryRows(item, usageByItem[item.id]) }

        // Special equipment details belong in the dedicated special-equipment block, never in
        // the Treasure/Notes block. This may repeat the compact item identity intentionally, but
        // not the item's data across unrelated semantic destinations.
        val specialRows = specialItems
            .flatMap { item ->
                classicSpecialItemRows(
                    item = item,
                    usage = usageByItem[item.id],
                    representedInBase = item.id in baseIds,
                )
            }

        val noteEntries = buildList {
            sheet.currencies
                .filter { !it.isDefault && it.standardCurrencyKindOrNull() == null }
                .sortedBy { it.sortOrder }
                .forEach { currency ->
                    add("${currency.name}: ${currency.amount}")
                }
            val valuables = aggregate.successor.preferences.valuablesText
                .split(';')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            valuables.take(CLASSIC_BASE_VALUABLE_CAPACITY)
                .filter { it.length > CLASSIC_BASE_ALLY_VALUE_CHARS }
                .forEach { add("Tesoro / valor: $it") }
            valuables.drop(CLASSIC_BASE_VALUABLE_CAPACITY).forEach(::add)
        }.flatMap { note ->
            wrapForChars(note, CLASSIC_INVENTORY_NOTE_CHARS)
                .chunked(CLASSIC_INVENTORY_NOTE_LINES)
                .map { it.joinToString("\n") }
        }

        if (ordinaryRows.isEmpty() && specialRows.isEmpty() && noteEntries.isEmpty()) return 0

        val pages = maxOf(
            1,
            pageCount(ordinaryRows.size, CLASSIC_INVENTORY_ROWS_PER_PAGE),
            pageCount(specialRows.size, CLASSIC_SPECIAL_ITEMS_PER_PAGE),
            pageCount(noteEntries.size, CLASSIC_INVENTORY_NOTES_PER_PAGE),
        )
        repeat(pages) { pageIndex ->
            val pageRows = ordinaryRows
                .drop(pageIndex * CLASSIC_INVENTORY_ROWS_PER_PAGE)
                .take(CLASSIC_INVENTORY_ROWS_PER_PAGE)
            val pageSpecial = specialRows
                .drop(pageIndex * CLASSIC_SPECIAL_ITEMS_PER_PAGE)
                .take(CLASSIC_SPECIAL_ITEMS_PER_PAGE)
            val pageNotes = noteEntries
                .drop(pageIndex * CLASSIC_INVENTORY_NOTES_PER_PAGE)
                .take(CLASSIC_INVENTORY_NOTES_PER_PAGE)
            check(pageRows.isNotEmpty() || pageSpecial.isNotEmpty() || pageNotes.isNotEmpty()) {
                "Fantasy Inventory compositor produced a page with no active module."
            }

            val page = addPage(doc)
            PDPageContentStream(doc, page).use { s ->
                extendedHeader(s, p, sheet.name, "INVENTARIO / EQUIPO")

                if (pageRows.isNotEmpty()) {
                    titledFrame(s, p, 24f, 112f, 564f, 402f, "INVENTARIO - CONTINUACIÓN")
                    inventoryHeader(s, p, 36f, 148f)
                    pageRows.forEachIndexed { index, row ->
                        inventoryRow(s, p, 36f, 176f + index * 27f, row)
                    }
                    repeat((CLASSIC_INVENTORY_ROWS_PER_PAGE - pageRows.size).coerceAtLeast(0)) { index ->
                        inventoryBlankRow(s, 36f, 176f + (pageRows.size + index) * 27f)
                    }
                }

                // Keep each native secondary module at its established geometry, but remove
                // exhausted siblings. When ordinary Equipment is exhausted, the remaining
                // modules reclaim the vacated top band instead of carrying an empty table.
                val secondaryTop = if (pageRows.isNotEmpty()) 528f else 112f
                if (pageSpecial.isNotEmpty()) {
                    titledFrame(
                        s,
                        p,
                        24f,
                        secondaryTop,
                        276f,
                        190f,
                        "OBJETOS ESPECIALES / SINTONIZADOS",
                    )
                    pageSpecial.forEachIndexed { index, item ->
                        specialItem(
                            s,
                            p,
                            36f,
                            secondaryTop + 30f + index * 39f,
                            252f,
                            item.name,
                            item.attuned,
                            item.note,
                        )
                    }
                }

                if (pageNotes.isNotEmpty()) {
                    val notesX = if (pageSpecial.isNotEmpty()) 312f else 24f
                    titledFrame(s, p, notesX, secondaryTop, 276f, 190f, "TESORO / VALORES")
                    ruledTextArea(
                        s,
                        p,
                        notesX + 12f,
                        secondaryTop + 36f,
                        252f,
                        140f,
                        pageNotes,
                        8.3f,
                    )
                }

                footer(s, p, doc.numberOfPages, "EXTENSIÓN / INVENTARIO Y EQUIPO")
            }
        }
        return 0
    }

    private fun classicSpecialItemRows(
        item: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem,
        usage: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage?,
        representedInBase: Boolean,
    ): List<ClassicSpecialItem> {
        val cleanName = item.name.trim()
        val projectedName = cleanName
        val detail = buildList {
            if (cleanName.length > CLASSIC_SPECIAL_ITEM_NAME_CHARS) {
                add("Nombre completo: $cleanName")
            }
            if (!representedInBase) {
                if (item.quantity != 1) add("Cant. ${item.quantity}")
                item.weightLb?.let { add("Peso " + formatWeight(it) + " lb") }
            }
            inventoryState(item, usage).takeIf { it.isNotBlank() }?.let(::add)
            item.location?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            item.description?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            item.notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        }.joinToString(" · ")
        val chunks = wrapForChars(detail, CLASSIC_SPECIAL_ITEM_NOTE_CHARS)
            .chunked(CLASSIC_SPECIAL_ITEM_NOTE_LINES)
            .map { it.joinToString("\n") }
            .ifEmpty { listOf("") }
        return chunks.mapIndexed { index, chunk ->
            ClassicSpecialItem(
                name = if (index == 0) {
                    projectedName
                } else {
                    "$projectedName (cont.)"
                },
                attuned = item.attuned && index == 0,
                note = chunk,
            )
        }
    }

private fun classicInventoryRows(
        item: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem,
        usage: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage?,
    ): List<InventoryRow> =
        listOf(
            InventoryRow(
                quantity = item.quantity.toString(),
                name = item.name.trim(),
                // Owner-confirmed ordinary Equipment content is compact identity only.
                // Preserve the Fantasy table grammar, but do not project weight/state/detail.
                weight = "",
                state = "",
                notes = "",
            ),
        )

    private fun inventoryState(
        item: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem,
        usage: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage?,
    ): String = buildList {
        when {
            item.attuned -> add("Sintonizado")
            item.equipped -> add("Equipado")
        }
        when (usage?.kind) {
            CharacterConsumableKind.CONSUMABLE -> add("Consumible")
            CharacterConsumableKind.AMMUNITION -> add("Munición")
            CharacterConsumableKind.NONE,
            null,
            -> Unit
        }
        if (usage?.quickUseAmount != null && usage.quickUseAmount != 1) {
            add("Uso ${usage.quickUseAmount}")
        }
        if (usage?.carryState == CharacterInventoryCarryState.STORED) add("Almacenado")
        if (usage?.carryState == CharacterInventoryCarryState.CARRIED && !item.equipped && !item.attuned) {
            add("Llevado")
        }
    }.joinToString(" · ")

    private fun formatWeight(value: Double): String {
        val rounded = kotlin.math.round(value * 10.0) / 10.0
        val text = if (rounded % 1.0 == 0.0) {
            rounded.toInt().toString()
        } else {
            rounded.toString()
        }
        return text.replace('.', ',')
    }

private fun appendSpellContinuationPages(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val slots = sheet.spellSlots.associateBy { it.level }
        val spellsByLevel = sheet.spells
            .sortedWith(
                compareBy<io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell> { it.sortOrder }
                    .thenBy { it.name.lowercase() },
            )
            .groupBy { it.level }

        val cantripOverflow = spellsByLevel[0].orEmpty().drop(CLASSIC_BASE_CANTRIP_CAPACITY)
        val baseCapacities = mapOf(
            1 to CLASSIC_BASE_LEVEL1_CAPACITY,
            2 to CLASSIC_BASE_LEVEL2_CAPACITY,
            3 to CLASSIC_BASE_LEVEL3_CAPACITY,
            4 to CLASSIC_BASE_LEVEL4_CAPACITY,
            5 to CLASSIC_BASE_LEVEL5_CAPACITY,
        )
        val overflowByLevel = mutableMapOf<Int, List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell>>()
        (1..5).forEach { level ->
            overflowByLevel[level] = spellsByLevel[level].orEmpty().drop(baseCapacities.getValue(level))
        }

        // The legacy/base Classic sheet has one compact 6+ area. Preserve which exact spells were
        // already represented there, but never collapse their continuation into another 6+ block.
        val consumedHighIds = (6..9)
            .flatMap { level -> spellsByLevel[level].orEmpty() }
            .take(CLASSIC_BASE_HIGH_LEVEL_CAPACITY)
            .mapTo(mutableSetOf()) { it.id }
        (6..9).forEach { level ->
            overflowByLevel[level] = spellsByLevel[level].orEmpty().filter { it.id !in consumedHighIds }
        }

        if (cantripOverflow.isEmpty() && overflowByLevel.values.all { it.isEmpty() }) return

        val cantripPages = pageCount(cantripOverflow.size, CLASSIC_EXT_TOP_ROWS)
        repeat(cantripPages) { pageIndex ->
            val page = addPage(doc)
            PDPageContentStream(doc, page).use { s ->
                extendedHeader(s, p, sheet.name, "CONJUROS")
                text(
                    s, p, 36f, 104f, 540f, 28f,
                    "Continuación de trucos. Los espacios gastados permanecen escribibles para uso en mesa.",
                    PdfTypographyRole.NOTE_TEXT, 8.2f, 7.2f,
                    align = PdfHorizontalAlignment.CENTER,
                )
                spellLevelBlock(
                    s, p, 24f, 146f, 176f, 268f, "TRUCOS - CONT.", "",
                    classicSpellRows(cantripOverflow.pageSlice(pageIndex, CLASSIC_EXT_TOP_ROWS)),
                )
                footer(s, p, doc.numberOfPages, "EXTENSIÓN / CONJUROS")
            }
        }

        data class Placement(
            val level: Int,
            val x: Float,
            val top: Float,
            val height: Float,
            val capacity: Int,
        )

        fun appendLevelGroup(placements: List<Placement>) {
            val pages = placements.maxOfOrNull { placement ->
                pageCount(overflowByLevel[placement.level].orEmpty().size, placement.capacity)
            } ?: 0
            repeat(pages) { pageIndex ->
                val page = addPage(doc)
                PDPageContentStream(doc, page).use { s ->
                    extendedHeader(s, p, sheet.name, "CONJUROS")
                    text(
                        s, p, 36f, 104f, 540f, 28f,
                        "Continuación por nivel. Cada nivel conserva su bloque propio; si no cabe, continúa en otra página.",
                        PdfTypographyRole.NOTE_TEXT, 8.2f, 7.2f,
                        align = PdfHorizontalAlignment.CENTER,
                    )
                    placements.forEach { placement ->
                        spellLevelBlock(
                            s,
                            p,
                            placement.x,
                            placement.top,
                            176f,
                            placement.height,
                            "NIVEL ${placement.level} - CONT.",
                            slots[placement.level]?.totalSlots?.toString().orEmpty(),
                            classicSpellRows(
                                overflowByLevel[placement.level].orEmpty()
                                    .pageSlice(pageIndex, placement.capacity),
                            ),
                        )
                    }
                    footer(s, p, doc.numberOfPages, "EXTENSIÓN / CONJUROS")
                }
            }
        }

        // Levels 1-6 preserve six distinct family-native blocks on their own continuation set.
        appendLevelGroup(
            listOf(
                Placement(1, 24f, 146f, 268f, CLASSIC_EXT_TOP_ROWS),
                Placement(2, 24f, 426f, 292f, CLASSIC_EXT_BOTTOM_ROWS),
                Placement(3, 212f, 146f, 268f, CLASSIC_EXT_TOP_ROWS),
                Placement(4, 212f, 426f, 292f, CLASSIC_EXT_BOTTOM_ROWS),
                Placement(5, 400f, 146f, 268f, CLASSIC_EXT_TOP_ROWS),
                Placement(6, 400f, 426f, 292f, CLASSIC_EXT_BOTTOM_ROWS),
            ),
        )

        // Levels 7-9 get a separate continuation set instead of being compressed into "6+".
        appendLevelGroup(
            listOf(
                Placement(7, 24f, 146f, 268f, CLASSIC_EXT_TOP_ROWS),
                Placement(8, 24f, 426f, 292f, CLASSIC_EXT_BOTTOM_ROWS),
                Placement(9, 212f, 146f, 268f, CLASSIC_EXT_TOP_ROWS),
            ),
        )
    }


    private fun appendNotesPages(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val packed = packedClassicNotes(p, plan)
        if (packed.columns.isEmpty()) return

        val pages = packed.columns
            .flatMap { segments -> segments.map { it.address.extendedPageIndex } }
            .distinct()
            .sorted()

        pages.forEach { pageIndex ->
            val columns = packed.columns
                .mapNotNull { segments ->
                    val address = segments.firstOrNull()?.address ?: return@mapNotNull null
                    if (address.extendedPageIndex != pageIndex) {
                        null
                    } else {
                        address.columnIndex to segments.flatMap { it.lines }
                    }
                }
                .toMap()

            val page = addPage(doc)
            PDPageContentStream(doc, page).use { s ->
                extendedHeader(s, p, sheet.name, "NOTAS")
                titledFrame(
                    s,
                    p,
                    CLASSIC_NOTES_FRAME_X,
                    CLASSIC_NOTES_FRAME_TOP,
                    CLASSIC_NOTES_FRAME_WIDTH,
                    CLASSIC_NOTES_FRAME_HEIGHT,
                    "NOTAS DE CAMPAÑA",
                )
                drawClassicNotesColumn(
                    s = s,
                    p = p,
                    lines = columns[1].orEmpty(),
                    x = CLASSIC_NOTES_LEFT_X,
                )

                // A Notes overflow page is the complete native Fantasy Notes page. Keep the
                // native companion panels as structure, but do not route unrelated semantic
                // streams into them.
                titledFrame(s, p, 398f, 112f, 190f, 292f, "CROQUIS / MAPA")
                grid(s, 410f, 148f, 166f, 240f, 10, 14)
                titledFrame(s, p, 398f, 418f, 190f, 300f, "REFERENCIAS Y RECORDATORIOS")
                ruledTextArea(
                    s, p, 410f, 454f, 166f, 246f,
                    emptyList(),
                    8.1f,
                )

                footer(s, p, doc.numberOfPages, "EXTENSIÓN / NOTAS")
            }
        }
    }

    private fun packedClassicNotes(
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ): PcSheetPackedNotes {
        val records = plan.snapshot.aggregate.sheet.pcSheetNoteRecords()
        val wrapped = records.map { record ->
            PcSheetWrappedNoteRecord(
                record = record,
                headingLines = classicNoteWrappedLines(
                    p = p,
                    text = record.heading,
                    role = PdfTypographyRole.OPTIONAL_DECORATIVE,
                    size = CLASSIC_NOTES_HEADING_SIZE,
                ),
                bodyLines = classicNoteWrappedLines(
                    p = p,
                    text = record.body,
                    role = PdfTypographyRole.NOTE_TEXT,
                    size = CLASSIC_NOTES_BODY_SIZE,
                ),
            )
        }
        return packPcSheetNoteColumns(
            records = wrapped,
            rowsPerColumn = CLASSIC_NOTES_ROWS_PER_COLUMN,
            columnsPerPage = 1,
        )
    }

    private fun classicNoteWrappedLines(
        p: DesktopPdfRenderingPrimitives,
        text: String,
        role: PdfTypographyRole,
        size: Float,
    ): List<String> {
        if (text.isBlank()) return emptyList()
        val layout = p.layoutTextBox(
            PdfTextBoxSpec(
                rect = PdfRect(
                    0f,
                    0f,
                    CLASSIC_NOTES_COLUMN_WIDTH - CLASSIC_NOTES_HORIZONTAL_PADDING * 2f,
                    2_000f,
                ),
                text = text.trim(),
                role = role,
                preferredSizePt = size,
                minimumSizePt = size,
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.TOP,
                wrapPolicy = PdfWrapPolicy.WORD_WRAP,
                maximumLines = 100,
                horizontalPaddingPt = 0f,
                verticalPaddingPt = 0f,
                lineHeightMultiplier = 1f,
                fontSizeMode = PdfFontSizeMode.FIXED,
            ),
        )
        require(!layout.hasOverflow) {
            "Fantasy Notes semantic record cannot be wrapped without data loss: '$text'"
        }
        return layout.renderedLines.ifEmpty { listOf(text.trim()) }
    }

    private fun drawClassicNotesColumn(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        lines: List<PcSheetNotePhysicalLine>,
        x: Float,
    ) {
        require(lines.size <= CLASSIC_NOTES_ROWS_PER_COLUMN) {
            "Packed Fantasy Notes column exceeds native family capacity."
        }

        repeat(CLASSIC_NOTES_ROWS_PER_COLUMN) { row ->
            val ruleTop = CLASSIC_NOTES_FIRST_ROW_TOP + (row + 1) * CLASSIC_NOTES_ROW_STEP
            hairline(s, x, ruleTop, x + CLASSIC_NOTES_COLUMN_WIDTH, ruleTop)
        }

        lines.forEachIndexed { row, line ->
            if (line.kind == PcSheetNotePhysicalLineKind.SEPARATOR || line.text.isBlank()) {
                return@forEachIndexed
            }
            val role = when (line.kind) {
                PcSheetNotePhysicalLineKind.HEADING,
                PcSheetNotePhysicalLineKind.CONTINUITY -> PdfTypographyRole.OPTIONAL_DECORATIVE
                PcSheetNotePhysicalLineKind.BODY -> PdfTypographyRole.NOTE_TEXT
                PcSheetNotePhysicalLineKind.SEPARATOR -> return@forEachIndexed
            }
            val preferred = when (line.kind) {
                PcSheetNotePhysicalLineKind.HEADING -> CLASSIC_NOTES_HEADING_SIZE
                PcSheetNotePhysicalLineKind.BODY -> CLASSIC_NOTES_BODY_SIZE
                PcSheetNotePhysicalLineKind.CONTINUITY -> CLASSIC_NOTES_CONTINUITY_SIZE
                PcSheetNotePhysicalLineKind.SEPARATOR -> CLASSIC_NOTES_BODY_SIZE
            }
            val minimum = when (line.kind) {
                PcSheetNotePhysicalLineKind.HEADING -> CLASSIC_NOTES_HEADING_MINIMUM
                PcSheetNotePhysicalLineKind.BODY -> CLASSIC_NOTES_BODY_MINIMUM
                PcSheetNotePhysicalLineKind.CONTINUITY -> CLASSIC_NOTES_CONTINUITY_MINIMUM
                PcSheetNotePhysicalLineKind.SEPARATOR -> CLASSIC_NOTES_BODY_MINIMUM
            }
            text(
                s = s,
                p = p,
                x = x + CLASSIC_NOTES_HORIZONTAL_PADDING,
                top = CLASSIC_NOTES_FIRST_ROW_TOP + row * CLASSIC_NOTES_ROW_STEP,
                width = CLASSIC_NOTES_COLUMN_WIDTH - CLASSIC_NOTES_HORIZONTAL_PADDING * 2f,
                height = CLASSIC_NOTES_ROW_STEP,
                value = line.text,
                role = role,
                preferred = preferred,
                minimum = minimum,
                vertical = PdfVerticalAlignment.BOTTOM,
            )
        }
    }

    private fun appendReferencePages(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val references = classicReferenceNoteLines(plan)
        if (references.isEmpty()) return

        references.chunked(CLASSIC_REFERENCE_FULL_PAGE_LINES).forEach { pageLines ->
            val page = addPage(doc)
            PDPageContentStream(doc, page).use { s ->
                extendedHeader(s, p, plan.snapshot.aggregate.sheet.name, "REFERENCIAS")
                titledFrame(s, p, 24f, 112f, 564f, 606f, "REFERENCIAS Y RECORDATORIOS")
                ruledTextArea(
                    s = s,
                    p = p,
                    x = 36f,
                    top = 148f,
                    width = 540f,
                    height = 552f,
                    content = pageLines,
                    fontSize = 8.3f,
                    lineGap = 20f,
                )
                footer(s, p, doc.numberOfPages, "EXTENSIÓN / REFERENCIAS")
            }
        }
    }

    private fun classicReferenceNoteLines(plan: PcSheetPdfRenderPlan): List<String> {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val lines = mutableListOf<String>()

        fun addWrapped(label: String, value: String) {
            val clean = value.trim()
            if (clean.isEmpty()) return
            val wrapped = wrapForChars("$label: $clean", CLASSIC_REFERENCE_CHARS_PER_LINE)
            if (wrapped.isEmpty()) return
            val usedOnPage = lines.size % CLASSIC_REFERENCE_LINES_PER_PAGE
            if (
                usedOnPage != 0 &&
                wrapped.size <= CLASSIC_REFERENCE_LINES_PER_PAGE &&
                usedOnPage + wrapped.size > CLASSIC_REFERENCE_LINES_PER_PAGE
            ) {
                repeat(CLASSIC_REFERENCE_LINES_PER_PAGE - usedOnPage) { lines += "" }
            }
            lines += wrapped
        }

        val baseLanguageIds = sheet.proficiencies
            .filter { it.type == CharacterProficiencyType.LANGUAGE }
            .sortedBy { it.sortOrder }
            .take(BASE_LANGUAGE_CAPACITY)
            .mapTo(mutableSetOf()) { it.id }

        sheet.proficiencies
            .sortedBy { it.sortOrder }
            .filter { proficiency ->
                proficiency.type != CharacterProficiencyType.LANGUAGE ||
                    proficiency.id !in baseLanguageIds ||
                    proficiency.name.length > CLASSIC_BASE_LANGUAGE_NAME_CHARS ||
                    !proficiency.notes.isNullOrBlank()
            }
            .forEach { proficiency ->
                addWrapped(
                    proficiencyTypeLabel(proficiency.type),
                    listOf(
                        proficiency.name,
                        proficiency.source.orEmpty().trim(),
                        proficiency.notes.orEmpty().trim(),
                    ).filter { it.isNotEmpty() }.joinToString(" · "),
                )
            }

        sheet.traits
            .sortedBy { it.sortOrder }
            .filter { isSpeciesIdentityTrait(it, plan) }
            .forEach { trait ->
                addWrapped(
                    "Raza",
                    listOf(
                        trait.name,
                        trait.description.trim(),
                        trait.notes.orEmpty().trim(),
                    ).filter { it.isNotEmpty() }.joinToString(" · "),
                )
            }

        classicReferenceFeatures(plan).forEach { feature ->
            addWrapped(
                feature.name,
                listOf(feature.source, feature.description)
                    .filter { it.isNotBlank() }
                    .joinToString(" · "),
            )
        }

        return lines
    }

    private fun classicNoteEntries(plan: PcSheetPdfRenderPlan): List<String> {
        val sheet = plan.snapshot.aggregate.sheet
        return buildList {
            sheet.generalNotes.trim().takeIf { it.isNotEmpty() }?.let(::add)
            sheet.noteCards.sortedBy { it.sortOrder }.forEach { card ->
                val title = card.title.trim()
                val body = card.content.trim()
                val value = when {
                    title.isNotEmpty() && body.isNotEmpty() -> "$title: $body"
                    title.isNotEmpty() -> title
                    else -> body
                }
                if (value.isNotEmpty()) add(value)
            }
        }.flatMap { note ->
            wrapForChars(note, CLASSIC_NOTES_CHARS_PER_LINE)
                .chunked(CLASSIC_NOTES_LINES_PER_ENTRY)
                .map { it.joinToString("\n") }
        }
    }

    private fun <T> List<T>.pageSlice(pageIndex: Int, capacity: Int): List<T> =
        drop(pageIndex * capacity).take(capacity)

    private fun drawMain(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val closure = aggregate.closure
        val successor = aggregate.successor
        val classes = sheet.classes.sortedBy { it.sortOrder }
        val backgroundName = successor.backgroundIdentity?.name
            ?.trim()?.takeIf { it.isNotEmpty() }
            ?: sheet.background.name.trim()
        val speciesName = successor.subraceIdentity?.name
            ?.trim()?.takeIf { it.isNotEmpty() }
            ?: successor.speciesIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
            ?: sheet.background.race.trim()
        val classSummary = classes.joinToString(" / ") { "${it.name} ${it.level}" }
        val subclassSummary = classes.mapNotNull { classLevel ->
            classLevel.subclassName?.trim()?.takeIf { it.isNotEmpty() }
        }.joinToString(" / ")
        val xp = if (closure.progressMode == CharacterProgressMode.EXPERIENCE) {
            formatInteger(closure.experiencePoints)
        } else {
            ""
        }

        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            identityHeader(
                s = s,
                p = p,
                name = sheet.name,
                details = listOf(
                    "TRASFONDO" to backgroundName,
                    "CLASE" to classSummary,
                    "RAZA" to speciesName,
                    "SUBCLASE" to subclassSummary,
                ),
                level = sheet.totalLevel.toString(),
                xp = xp,
            )

            val leftX = 24f
            val leftW = 232f
            decorativeStat(
                s, p, leftX, 108f, leftW, 43f,
                "BONIFICADOR POR COMPETENCIA", signed(sheet.finalProficiencyBonus),
            )

            val panelW = 112f
            val gap = 8f
            val placements = listOf(
                Triple(CharacterAbility.STRENGTH, leftX, 160f),
                Triple(CharacterAbility.INTELLIGENCE, leftX + panelW + gap, 160f),
                Triple(CharacterAbility.DEXTERITY, leftX, 344f),
                Triple(CharacterAbility.WISDOM, leftX + panelW + gap, 344f),
                Triple(CharacterAbility.CONSTITUTION, leftX, 528f),
                Triple(CharacterAbility.CHARISMA, leftX + panelW + gap, 528f),
            )
            placements.forEach { (ability, x, top) ->
                val save = sheet.savingThrow(ability)
                val skills = SkillKey.entries
                    .filter { it.ability == ability }
                    .map { key ->
                        val state = sheet.skill(key)
                        SkillRow(
                            name = skillLabel(key),
                            total = signed(sheet.skillTotal(key)),
                            training = training(state.training),
                        )
                    }
                attributePanel(
                    s = s,
                    p = p,
                    x = x,
                    top = top,
                    width = panelW,
                    title = abilityLabel(ability),
                    abbreviation = abilityAbbreviation(ability),
                    score = sheet.abilityScore(ability).toString(),
                    modifier = signed(sheet.abilityModifier(ability)),
                    saveTotal = signed(sheet.savingThrowTotal(ability)),
                    saveTraining = if (save.proficient) Training.PROFICIENT else Training.NONE,
                    skills = skills,
                )
            }

            val rightX = 270f
            val rightW = 318f
            combatOverview(
                s = s,
                p = p,
                x = rightX,
                top = 108f,
                width = rightW,
                armorClass = sheet.armorClass,
                currentHp = sheet.currentHp,
                maxHp = sheet.maxHp,
                hitDice = classes.joinToString(" / ") { "${it.hitDiceRemaining}d${it.hitDieSides}" },
                deathSaveSuccesses = sheet.deathSaveSuccesses,
                deathSaveFailures = sheet.deathSaveFailures,
            )
            quickReferenceRow(
                s = s,
                p = p,
                x = rightX,
                top = 194f,
                width = rightW,
                initiative = signed(sheet.initiativeModifier),
                speed = sheet.speed.toString(),
                passivePerception = sheet.passivePerception.toString(),
                inspiration = sheet.inspiration,
            )

            titledFrame(s, p, rightX, 248f, rightW, 150f, "ARMAS Y ACCIONES")
            tableHeader(
                s, p, rightX + 10f, 278f,
                listOf(148f to "Nombre", 50f to "Bonif.", 86f to "Daño / notas"),
            )
            val combatEntries = sheet.combatEntries.sortedBy { it.sortOrder }
            val damageByCombatId = aggregate.successor.combatDamage.associateBy { it.combatEntryId }
            combatEntries.take(BASE_COMBAT_CAPACITY).forEachIndexed { index, entry ->
                val top = 300f + index * 23f
                text(
                    s, p, rightX + 10f, top, 148f, 18f,
                    entry.name,
                    PdfTypographyRole.BODY, 8.5f, 6.6f,
                    wrap = true,
                    maxLines = 2,
                )
                text(
                    s, p, rightX + 162f, top, 45f, 18f,
                    entry.attackModifier?.let(::signed).orEmpty(),
                    PdfTypographyRole.NUMERIC_COMPACT, 9f, 8f,
                    align = PdfHorizontalAlignment.CENTER,
                )
                val detail = listOfNotNull(
                    entry.damageEffect.takeIf { it.isNotBlank() },
                    entry.rangeText?.takeIf { it.isNotBlank() },
                    entry.notes?.takeIf { it.isNotBlank() },
                ).joinToString(" · ")
                val structuredDamage = damageByCombatId[entry.id]?.components
                    ?.joinToString(" + ") { component ->
                        component.expression +
                            component.typeText?.takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()
                    }
                    .orEmpty()
                val needsExtendedCombatReference =
                    entry.type != CharacterCombatEntryType.ATTACK ||
                        !entry.notes.isNullOrBlank() ||
                        entry.name.length > CLASSIC_COMBAT_NAME_CHARS ||
                        detail.length > CLASSIC_COMBAT_DETAIL_CHARS ||
                        structuredDamage.isNotBlank()
                val baseDetail = if (needsExtendedCombatReference) {
                    "[continúa en COMBATE / ACCIONES]"
                } else {
                    detail
                }
                text(
                    s, p, rightX + 211f, top, 97f, 18f,
                    baseDetail,
                    PdfTypographyRole.BODY, 8.2f, 5.8f,
                    wrap = true,
                    maxLines = 2,
                )
                hairline(s, rightX + 10f, top + 20f, rightX + rightW - 10f, top + 20f)
            }

            val orderedTraits = sheet.traits.sortedBy { it.sortOrder }
            val classTraits = orderedTraits.filter { it.type == CharacterTraitType.CLASS }
            val speciesTraits = orderedTraits.filter {
                it.type == CharacterTraitType.SPECIES_RACE && !isSpeciesIdentityTrait(it, plan)
            }
            val feats = orderedTraits.filter { it.type == CharacterTraitType.FEAT }

            titledFrame(s, p, rightX, 410f, rightW, 184f, "RASGOS DE CLASE")
            val baseClassProjection = classicBaseClassProjection(classTraits)
            ruledTextArea(
                s, p, rightX + 10f, 442f, rightW - 20f, 140f,
                baseClassProjection.content,
                8.4f,
            )

            titledFrame(s, p, rightX, 606f, 154f, 112f, "ATRIBUTOS DE RAZA")
            speciesTraits.take(BASE_SPECIES_TRAIT_CAPACITY).forEachIndexed { index, trait ->
                val rowTop = 636f + index * 22f
                text(
                    s, p, rightX + 10f, rowTop, 134f, 17f,
                    trait.name,
                    PdfTypographyRole.BODY, 7.8f, 6.2f,
                    wrap = true,
                    maxLines = 2,
                )
                hairline(s, rightX + 10f, rowTop + 21f, rightX + 144f, rowTop + 21f)
            }

            titledFrame(s, p, rightX + 164f, 606f, 154f, 112f, "DOTES")
            boundedBaseTraitPreview(
                s = s,
                p = p,
                x = rightX + 173f,
                top = 638f,
                width = 136f,
                height = 70f,
                traits = feats.take(BASE_FEAT_CAPACITY),
                fontSize = 8.2f,
            )

            footer(s, p, 1, "RESUMEN / COMBATE / ATRIBUTOS Y HABILIDADES")
        }
    }

    private fun drawCharacterAndEquipment(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val background = sheet.background
        val usageByItem = aggregate.closure.inventoryUsage.associateBy { it.itemId }
        val inventory = sheet.inventoryItems.sortedBy { it.sortOrder }

        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            secondaryHeader(s, p, sheet.name, "PERSONAJE / HISTORIA / EQUIPO")

            titledFrame(s, p, 24f, 104f, 226f, 196f, "ASPECTO")
            // D-0074: when no real locally-resolved portrait bytes are supplied, leave the
            // portrait area blank. The border/frame remains part of the approved Classic grammar.
            fantasyFrame(s, 36f, 136f, 202f, 150f, 0.65f)

            titledFrame(s, p, 24f, 314f, 226f, 108f, "DESCRIPCIÓN")
            ruledBackground(s, 34f, 346f, 206f, 66f, firstRuleOffset = 28f, lineGap = 22f)

            titledFrame(s, p, 24f, 436f, 226f, 282f, "HISTORIA Y PERSONALIDAD")
            val historyAndPersonality = listOf(
                classicBaseNarrativeExcerpt(
                    listOf(background.name, background.summary, background.story)
                        .filter { it.isNotBlank() }.joinToString(" · "),
                    CLASSIC_BACKGROUND_NARRATIVE_CHARS,
                    CLASSIC_BACKGROUND_NARRATIVE_LINES,
                    "story",
                    "HISTORIA",
                ),
                classicBaseNarrativeExcerpt(
                    background.personalityTraits.takeIf { it.isNotBlank() }?.let { "Rasgo: $it" }.orEmpty(),
                    CLASSIC_BACKGROUND_DETAIL_CHARS,
                    CLASSIC_BACKGROUND_DETAIL_LINES,
                    "personality",
                    "PERSONALIDAD",
                ),
                classicBaseNarrativeExcerpt(
                    background.ideals.takeIf { it.isNotBlank() }?.let { "Ideal: $it" }.orEmpty(),
                    CLASSIC_BACKGROUND_DETAIL_CHARS,
                    CLASSIC_BACKGROUND_DETAIL_LINES,
                    "ideals",
                    "IDEALES",
                ),
                classicBaseNarrativeExcerpt(
                    background.bonds.takeIf { it.isNotBlank() }?.let { "Vínculo: $it" }.orEmpty(),
                    CLASSIC_BACKGROUND_DETAIL_CHARS,
                    CLASSIC_BACKGROUND_DETAIL_LINES,
                    "bonds",
                    "VÍNCULOS",
                ),
                classicBaseNarrativeExcerpt(
                    background.flaws.takeIf { it.isNotBlank() }?.let { "Defecto: $it" }.orEmpty(),
                    CLASSIC_BACKGROUND_DETAIL_CHARS,
                    CLASSIC_BACKGROUND_DETAIL_LINES,
                    "flaws",
                    "DEFECTOS",
                ),
            )
            ruledTextArea(
                s, p, 34f, 468f, 206f, 238f,
                historyAndPersonality,
                7.9f,
            )

titledFrame(s, p, 264f, 104f, 324f, 316f, "EQUIPO")
            coinStrip(s, p, 276f, 136f, sheet.currencies)
            tableHeader(
                s, p, 276f, 180f,
                listOf(36f to "Cant.", 146f to "Objeto", 112f to "Peso"),
            )
            inventory.take(BASE_EQUIPMENT_CAPACITY).forEachIndexed { index, item ->
                val top = 202f + index * 27f
                text(
                    s, p, 278f, top, 32f, 19f,
                    item.quantity.toString(), PdfTypographyRole.NUMERIC_COMPACT, 8.6f, 7.8f,
                    align = PdfHorizontalAlignment.CENTER,
                )
                text(
                    s, p, 316f, top, 142f, 19f,
                    item.name,
                    PdfTypographyRole.BODY, 8.5f, 6.4f,
                    wrap = true,
                    maxLines = 2,
                )
                text(
                    s, p, 464f, top, 112f, 19f,
                    inventoryBaseNote(item, usageByItem[item.id]),
                    PdfTypographyRole.BODY, 8f, 6.4f,
                    wrap = true,
                    maxLines = 2,
                )
                hairline(s, 276f, top + 21f, 576f, top + 21f)
            }
            val baseEquipmentCount = inventory.size.coerceAtMost(BASE_EQUIPMENT_CAPACITY)
            repeat((BASE_EQUIPMENT_CAPACITY - baseEquipmentCount).coerceAtLeast(0)) { index ->
                val row = baseEquipmentCount + index
                val y = 202f + row * 27f + 21f
                hairline(s, 276f, y, 576f, y)
            }
            repeat(2) { index ->
                val y = 391f + index * 18f
                hairline(s, 276f, y, 576f, y)
            }

            val orderedTraits = sheet.traits.sortedBy { it.sortOrder }
            val semanticTraits = orderedTraits.filterNot { isSpeciesIdentityTrait(it, plan) }
            val usedTraitIds = buildSet {
                semanticTraits.filter { it.type == CharacterTraitType.CLASS }
                    .take(BASE_CLASS_TRAIT_CAPACITY).forEach { add(it.id) }
                semanticTraits.filter { it.type == CharacterTraitType.SPECIES_RACE }
                    .take(BASE_SPECIES_TRAIT_CAPACITY).forEach { add(it.id) }
                semanticTraits.filter { it.type == CharacterTraitType.FEAT }
                    .take(BASE_FEAT_CAPACITY).forEach { add(it.id) }
            }
            val additionalTraits = semanticTraits.filter { it.id !in usedTraitIds }
            titledFrame(s, p, 264f, 434f, 324f, 132f, "RASGOS ADICIONALES")
            ruledTextArea(
                s, p, 276f, 466f, 300f, 88f,
                additionalTraits.take(BASE_ADDITIONAL_TRAIT_CAPACITY).map(::classicBaseTraitSummary),
                8.4f,
            )

            val languages = sheet.proficiencies
                .filter { it.type == CharacterProficiencyType.LANGUAGE }
                .sortedBy { it.sortOrder }
            titledFrame(s, p, 264f, 580f, 156f, 138f, "IDIOMAS")
            ruledTextArea(
                s, p, 276f, 612f, 132f, 92f,
                languages.take(BASE_LANGUAGE_CAPACITY).map { it.name },
                8.8f,
            )

            val alliesAndTreasure = buildList {
                sheet.companions.sortedBy { it.sortOrder }.take(BASE_COMPANION_CAPACITY).forEach { companion ->
                    add(
                        companion.name +
                            companion.kind.takeIf { it.isNotBlank() }?.let { " ($it)" }.orEmpty(),
                    )
                }
                aggregate.successor.preferences.valuablesText
                    .split(';').map { it.trim() }.filter { it.isNotEmpty() }
                    .take(CLASSIC_BASE_VALUABLE_CAPACITY)
                    .forEach(::add)
            }
            titledFrame(s, p, 432f, 580f, 156f, 138f, "ALIADOS Y TESORO")
            ruledTextArea(s, p, 444f, 612f, 132f, 92f, alliesAndTreasure, 8.1f)

            footer(s, p, 2, "PERSONAJE / EQUIPO / HISTORIA")
        }
    }

    private fun drawSpells(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val sourceOrder = sheet.spellcastingSources.associate { it.id to it.sortOrder }
        val profile = aggregate.successor.spellcastingProfiles
            .minByOrNull { sourceOrder[it.sourceId] ?: Int.MAX_VALUE }
        val profileAbility = profile?.ability
        val abilityName = when {
            profileAbility?.builtIn != null -> abilityLabel(requireNotNull(profileAbility.builtIn))
            profileAbility?.customAttributeId != null -> aggregate.successor.customAttributes
                .firstOrNull { it.id == profileAbility.customAttributeId }?.name.orEmpty()
            sheet.spellcastingAbility != SpellcastingAbility.NONE -> spellcastingAbilityLabel(sheet.spellcastingAbility)
            else -> ""
        }
        val saveDc = profile?.let { sheet.spellSaveDc(it, aggregate.successor) } ?: sheet.spellSaveDc
        val attack = profile?.let { sheet.spellAttackModifier(it, aggregate.successor) } ?: sheet.spellAttackModifier
        val slots = sheet.spellSlots.associateBy { it.level }
        val spells = sheet.spells
            .sortedWith(compareBy<io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell> { it.sortOrder }
                .thenBy { it.name.lowercase() })
            .groupBy { it.level }

        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            spellHeader(
                s = s,
                p = p,
                name = sheet.name,
                ability = abilityName,
                modifier = when {
                    profileAbility?.builtIn != null ->
                        signed(sheet.abilityModifier(requireNotNull(profileAbility.builtIn)))
                    profileAbility?.customAttributeId != null -> aggregate.successor.customAttributes
                        .firstOrNull { it.id == profileAbility.customAttributeId }
                        ?.modifier?.let(::signed).orEmpty()
                    else -> legacySpellcastingModifier(sheet.spellcastingAbility, sheet)
                },
                saveDc = saveDc?.toString().orEmpty(),
                attack = attack?.let(::signed).orEmpty(),
            )
            spellSlotBand(s, p, 24f, 112f, 564f, slots)

            val colW = 176f
            val gap = 12f
            val x1 = 24f
            val x2 = x1 + colW + gap
            val x3 = x2 + colW + gap

            spellLevelBlock(s, p, x1, 190f, colW, 160f, "TRUCOS", "",
                classicSpellRows(spells[0].orEmpty()))
            spellLevelBlock(s, p, x1, 362f, colW, 356f, "NIVEL 1", slots[1]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[1].orEmpty()))

            spellLevelBlock(s, p, x2, 190f, colW, 252f, "NIVEL 2", slots[2]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[2].orEmpty()))
            spellLevelBlock(s, p, x2, 454f, colW, 264f, "NIVEL 3", slots[3]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[3].orEmpty()))

            spellLevelBlock(s, p, x3, 190f, colW, 168f, "NIVEL 4", slots[4]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[4].orEmpty()))
            spellLevelBlock(s, p, x3, 370f, colW, 168f, "NIVEL 5", slots[5]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[5].orEmpty()))
            val highLevel = (6..9).flatMap { level ->
                spells[level].orEmpty().map { spell ->
                    ClassicSpellRow("N$level ${spell.name}", spell.sourceAssociations.any { it.prepared })
                }
            }
            spellLevelBlock(s, p, x3, 550f, colW, 168f, "NIVEL 6+", "", highLevel)

            footer(s, p, 3, "CONJUROS / ESPACIOS / PREPARACIÓN")
        }
    }

    private fun combatOverview(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        armorClass: Int,
        currentHp: Int,
        maxHp: Int,
        hitDice: String,
        deathSaveSuccesses: Int,
        deathSaveFailures: Int,
    ) {
        fantasyFrame(s, x, top, width, 76f, 0.9f)
        shieldStat(s, p, x + 10f, top + 10f, 56f, 55f, "CA", armorClass.toString())
        miniRunicStat(s, p, x + 74f, top + 10f, 88f, 55f, "PUNTOS DE GOLPE", "$currentHp / $maxHp")
        miniRunicStat(s, p, x + 170f, top + 10f, 60f, 55f, "DADOS DE GOLPE", hitDice)
        text(s, p, x + 238f, top + 10f, 68f, 12f, "SALV. MUERTE", PdfTypographyRole.OPTIONAL_DECORATIVE, 6.3f, 5.4f,
            align = PdfHorizontalAlignment.CENTER)
        repeat(3) { index ->
            marker(
                s, p, x + 250f + index * 17f, top + 36f, 9f,
                if (index < deathSaveSuccesses.coerceIn(0, 3)) PdfMarkerKind.DIAMOND_FILLED else PdfMarkerKind.DIAMOND_OUTLINE,
            )
        }
        repeat(3) { index ->
            marker(
                s, p, x + 250f + index * 17f, top + 55f, 9f,
                if (index < deathSaveFailures.coerceIn(0, 3)) PdfMarkerKind.DIAMOND_FILLED else PdfMarkerKind.DIAMOND_OUTLINE,
            )
        }
        text(s, p, x + 298f, top + 29f, 8f, 10f, "E", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.4f, 4.8f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 298f, top + 48f, 8f, 10f, "F", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.4f, 4.8f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun quickReferenceRow(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        initiative: String,
        speed: String,
        passivePerception: String,
        inspiration: Boolean,
    ) {
        val labels = listOf(
            Triple("INICIATIVA", initiative, 58f),
            Triple("VELOCIDAD", speed, 58f),
            Triple("TAMAÑO", "", 60f),
            Triple("PERCEPCIÓN PASIVA", passivePerception, 82f),
        )
        var cursor = x
        labels.forEach { (label, value, w) ->
            miniRunicStat(s, p, cursor, top, w, 42f, label, value)
            cursor += w + 4f
        }
        val inspirationWidth = 44f
        fantasyFrame(s, cursor, top, inspirationWidth, 42f, 0.65f)
        text(s, p, cursor + 2f, top + 4f, inspirationWidth - 4f, 11f, "INSPIRACIÓN",
            PdfTypographyRole.OPTIONAL_DECORATIVE, 5.7f, 4.9f, align = PdfHorizontalAlignment.CENTER)
        marker(
            s, p, cursor + inspirationWidth / 2f, top + 27f, 11f,
            if (inspiration) PdfMarkerKind.STAR_FILLED else PdfMarkerKind.STAR_OUTLINE,
        )
    }

    private fun coinStrip(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        currencies: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency>,
    ) {
        val coins = listOf(
            "PC" to StandardCurrencyKind.COPPER,
            "PP" to StandardCurrencyKind.SILVER,
            "PE" to StandardCurrencyKind.ELECTRUM,
            "PO" to StandardCurrencyKind.GOLD,
            "PPT" to StandardCurrencyKind.PLATINUM,
        )
        coins.forEachIndexed { index, (label, kind) ->
            miniRunicStat(
                s, p, x + index * 58f, top, 52f, 34f,
                label, currencies.standardCurrency(kind)?.amount?.toString().orEmpty(),
            )
        }
    }

    private fun spellHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        name: String,
        ability: String,
        modifier: String,
        saveDc: String,
        attack: String,
    ) {
        fantasyFrame(s, 24f, 24f, 564f, 72f, 1.05f, fill = PAPER_TINT)
        text(
            s, p, 36f, 31f, 195f, 31f,
            name,
            PdfTypographyRole.CHARACTER_NAME, 18f, 8.8f,
            wrap = true,
            maxLines = 2,
        )
        text(s, p, 36f, 66f, 195f, 12f, "APTITUD MÁGICA", PdfTypographyRole.OPTIONAL_DECORATIVE, 6.8f, 6f)
        text(
            s, p, 239f, 27f, 112f, 15f,
            ability,
            PdfTypographyRole.OPTIONAL_DECORATIVE, 9f, 7.5f,
            align = PdfHorizontalAlignment.CENTER)
        miniRunicStat(s, p, 239f, 45f, 112f, 38f, "MODIFICADOR", modifier)
        miniRunicStat(s, p, 363f, 30f, 101f, 52f, "CD DE SALVACIÓN", saveDc)
        miniRunicStat(s, p, 476f, 30f, 100f, 52f, "ATAQUE", attack)
    }

    private fun spellSlotBand(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        slots: Map<Int, CharacterSpellSlot>,
    ) {
        titledFrame(s, p, x, top, width, 66f, "ESPACIOS DE CONJURO")
        (1..9).forEachIndexed { index, level ->
            val slot = slots[level]
            val total = slot?.totalSlots ?: 0
            val cellX = x + 10f + index * 60.2f
            text(s, p, cellX, top + 31f, 18f, 16f, level.toString(), PdfTypographyRole.NUMERIC_COMPACT, 8f, 7f,
                align = PdfHorizontalAlignment.CENTER)
            repeat(total.coerceAtMost(CLASSIC_BASE_SLOT_MARKERS)) { markerIndex ->
                marker(
                    s, p, cellX + 26f + markerIndex * 8.6f, top + 40f, 6.5f,
                    PdfMarkerKind.DIAMOND_OUTLINE,
                )
            }
        }
    }

    private fun spellLevelBlock(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        title: String,
        slots: String,
        spells: List<ClassicSpellRow>,
    ) {
        fantasyFrame(s, x, top, width, height, 0.75f)
        text(s, p, x + 10f, top + 8f, width - 58f, 18f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 9f, 7.8f)
        if (slots.isNotBlank()) {
            fantasyFrame(s, x + width - 47f, top + 6f, 37f, 24f, 0.55f)
            text(s, p, x + width - 44f, top + 9f, 31f, 16f, slots, PdfTypographyRole.NUMERIC_COMPACT, 9f, 8f,
                align = PdfHorizontalAlignment.CENTER)
        }
        val usableTop = top + 36f
        val rowH = 23f
        val maxRows = ((height - 44f) / rowH).toInt().coerceAtLeast(1)
        repeat(maxRows) { index ->
            val rowTop = usableTop + index * rowH
            val spell = spells.getOrNull(index)
            marker(
                s, p, x + 11f, rowTop + 8f, 7f,
                if (spell?.prepared == true) PdfMarkerKind.CIRCLE_FILLED else PdfMarkerKind.CIRCLE_OUTLINE,
            )
            text(
                s, p, x + 21f, rowTop, width - 31f, 18f,
                spell?.name.orEmpty(),
                PdfTypographyRole.SPELL_NAME, 8.1f, 6.2f,
                wrap = true,
                maxLines = 2,
            )
            hairline(s, x + 21f, rowTop + 20f, x + width - 10f, rowTop + 20f)
        }
    }

    private fun classicSpellRows(
        spells: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell>,
    ): List<ClassicSpellRow> = spells.map { spell ->
        ClassicSpellRow(
            name = spell.name,
            prepared = spell.sourceAssociations.any { it.prepared },
        )
    }

    private fun inventoryBaseNote(
        item: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem,
        usage: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage?,
    ): String = ""

    private fun traitSummary(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
    ): String = if (trait.description.isBlank()) trait.name else "${trait.name}: ${trait.description}"

    private fun classicBaseTraitSummary(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
    ): String = classicBaseExcerpt(
        traitSummary(trait),
        CLASSIC_RULED_ENTRY_CHARS,
        CLASSIC_RULED_ENTRY_LINES,
    )

    private fun classicBaseClassProjection(
        traits: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>,
    ): ClassicBaseRuledProjection {
        val selected = traits.take(BASE_CLASS_TRAIT_CAPACITY)
        var rowsLeft = CLASSIC_BASE_CLASS_RULE_ROWS
        val content = mutableListOf<String>()
        val clipped = mutableSetOf<kotlin.uuid.Uuid>()

        selected.forEachIndexed { index, trait ->
            val lines = wrapForChars(traitSummary(trait), CLASSIC_RULED_ENTRY_CHARS)
            val remainingTraits = selected.size - index - 1
            val maxRowsForThis = (rowsLeft - remainingTraits)
                .coerceAtLeast(1)
                .coerceAtMost(CLASSIC_RULED_ENTRY_LINES)
            val shown = lines.take(maxRowsForThis)
            if (shown.isNotEmpty()) content += shown.joinToString("\n")
            if (lines.size > shown.size) clipped += trait.id
            rowsLeft = (rowsLeft - shown.size).coerceAtLeast(0)
        }

        return ClassicBaseRuledProjection(
            content = content,
            clippedTraitIds = clipped,
        )
    }

    private fun abilityLabel(ability: CharacterAbility): String = when (ability) {
        CharacterAbility.STRENGTH -> "FUERZA"
        CharacterAbility.DEXTERITY -> "DESTREZA"
        CharacterAbility.CONSTITUTION -> "CONSTITUCIÓN"
        CharacterAbility.INTELLIGENCE -> "INTELIGENCIA"
        CharacterAbility.WISDOM -> "SABIDURÍA"
        CharacterAbility.CHARISMA -> "CARISMA"
    }

    private fun abilityAbbreviation(ability: CharacterAbility): String = when (ability) {
        CharacterAbility.STRENGTH -> "FUE"
        CharacterAbility.DEXTERITY -> "DES"
        CharacterAbility.CONSTITUTION -> "CON"
        CharacterAbility.INTELLIGENCE -> "INT"
        CharacterAbility.WISDOM -> "SAB"
        CharacterAbility.CHARISMA -> "CAR"
    }

    private fun legacySpellcastingModifier(
        ability: SpellcastingAbility,
        sheet: io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet,
    ): String = when (ability) {
        SpellcastingAbility.STRENGTH -> signed(sheet.abilityModifier(CharacterAbility.STRENGTH))
        SpellcastingAbility.DEXTERITY -> signed(sheet.abilityModifier(CharacterAbility.DEXTERITY))
        SpellcastingAbility.CONSTITUTION -> signed(sheet.abilityModifier(CharacterAbility.CONSTITUTION))
        SpellcastingAbility.INTELLIGENCE -> signed(sheet.abilityModifier(CharacterAbility.INTELLIGENCE))
        SpellcastingAbility.WISDOM -> signed(sheet.abilityModifier(CharacterAbility.WISDOM))
        SpellcastingAbility.CHARISMA -> signed(sheet.abilityModifier(CharacterAbility.CHARISMA))
        SpellcastingAbility.OTHER,
        SpellcastingAbility.NONE,
        -> ""
    }

    private fun spellcastingAbilityLabel(ability: SpellcastingAbility): String = when (ability) {
        SpellcastingAbility.STRENGTH -> "FUERZA"
        SpellcastingAbility.DEXTERITY -> "DESTREZA"
        SpellcastingAbility.CONSTITUTION -> "CONSTITUCIÓN"
        SpellcastingAbility.INTELLIGENCE -> "INTELIGENCIA"
        SpellcastingAbility.WISDOM -> "SABIDURÍA"
        SpellcastingAbility.CHARISMA -> "CARISMA"
        SpellcastingAbility.OTHER -> "OTRA"
        SpellcastingAbility.NONE -> ""
    }

    private fun skillLabel(key: SkillKey): String = when (key) {
        SkillKey.ACROBATICS -> "Acrobacias"
        SkillKey.ANIMAL_HANDLING -> "Trato con animales"
        SkillKey.ARCANA -> "Conocimiento arcano"
        SkillKey.ATHLETICS -> "Atletismo"
        SkillKey.DECEPTION -> "Engaño"
        SkillKey.HISTORY -> "Historia"
        SkillKey.INSIGHT -> "Perspicacia"
        SkillKey.INTIMIDATION -> "Intimidación"
        SkillKey.INVESTIGATION -> "Investigación"
        SkillKey.MEDICINE -> "Medicina"
        SkillKey.NATURE -> "Naturaleza"
        SkillKey.PERCEPTION -> "Percepción"
        SkillKey.PERFORMANCE -> "Interpretación"
        SkillKey.PERSUASION -> "Persuasión"
        SkillKey.RELIGION -> "Religión"
        SkillKey.SLEIGHT_OF_HAND -> "Juego de manos"
        SkillKey.STEALTH -> "Sigilo"
        SkillKey.SURVIVAL -> "Supervivencia"
    }

    private fun training(value: SkillTraining): Training = when (value) {
        SkillTraining.NONE -> Training.NONE
        SkillTraining.PROFICIENT -> Training.PROFICIENT
        SkillTraining.EXPERTISE -> Training.EXPERTISE
    }

    private fun signed(value: Int): String = if (value >= 0) "+$value" else value.toString()

    private fun formatInteger(value: Int): String =
        value.toString().reversed().chunked(3).joinToString(".").reversed()

    private fun identityHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        name: String,
        details: List<Pair<String, String>>,
        level: String,
        xp: String,
    ) {
        fantasyFrame(s, 24f, 24f, 564f, 72f, 1.15f, fill = PAPER_TINT)
        text(
            s, p, 36f, 31f, 214f, 37f,
            name,
            PdfTypographyRole.CHARACTER_NAME, 20f, 8.8f,
            wrap = true,
            maxLines = 2,
            align = PdfHorizontalAlignment.CENTER,
        )
        hairline(s, 36f, 68f, 250f, 68f)
        text(s, p, 36f, 70f, 214f, 15f, "NOMBRE DEL PERSONAJE", PdfTypographyRole.OPTIONAL_DECORATIVE, 7.2f, 6.5f)

        val detailX = 264f
        val detailW = 226f
        details.forEachIndexed { i, (label, value) ->
            val col = i % 2
            val row = i / 2
            val x = detailX + col * 113f
            val top = 31f + row * 27f
            text(
                s, p, x, top, 106f, 14f,
                value,
                PdfTypographyRole.BODY, 8.5f, 5.0f,
                wrap = true,
                maxLines = 2,
            )
            hairline(s, x, top + 14f, x + 106f, top + 14f)
            text(s, p, x, top + 15f, 106f, 9f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 5.8f, 5.2f)
        }

        fantasyFrame(s, 500f, 31f, 76f, 52f, 0.8f)
        text(s, p, 505f, 34f, 31f, 25f, level, PdfTypographyRole.PRIMARY_VALUE, 17f, 14f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, 505f, 60f, 31f, 10f, "NIVEL", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.8f, 5f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, 540f, 34f, 31f, 25f, xp, PdfTypographyRole.NUMERIC_COMPACT, 9f, 7f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, 540f, 60f, 31f, 10f, "PX", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.8f, 5f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun secondaryHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        name: String,
        title: String,
    ) {
        fantasyFrame(s, 24f, 24f, 564f, 64f, 1.05f, fill = PAPER_TINT)
        text(
            s, p, 36f, 30f, 230f, 33f,
            name,
            PdfTypographyRole.CHARACTER_NAME, 18f, 8.8f,
            wrap = true,
            maxLines = 2,
        )
        hairline(s, 36f, 66f, 266f, 66f)
        text(s, p, 278f, 32f, 298f, 22f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 12f, 10f,
            align = PdfHorizontalAlignment.RIGHT)
        text(s, p, 278f, 57f, 298f, 14f, "HOJA DE PERSONAJE", PdfTypographyRole.BODY, 7.3f, 6.3f,
            align = PdfHorizontalAlignment.RIGHT)
    }

    private fun attributePanel(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        title: String,
        abbreviation: String,
        score: String,
        modifier: String,
        saveTotal: String,
        saveTraining: Training,
        skills: List<SkillRow>,
    ) {
        fantasyFrame(s, x, top, width, 174f, 0.85f)
        text(s, p, x + 6f, top + 6f, width - 12f, 14f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 8.6f, 7.2f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 8f, top + 22f, 56f, 40f, modifier, PdfTypographyRole.PRIMARY_VALUE, 23f, 19f,
            align = PdfHorizontalAlignment.CENTER)
        circleOutline(s, x + 36f, top + 42f, 25f)
        text(s, p, x + 68f, top + 23f, width - 76f, 18f, score, PdfTypographyRole.SECONDARY_VALUE, 13f, 11f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 68f, top + 42f, width - 76f, 10f, "PUNT.", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.6f, 5f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 68f, top + 53f, width - 76f, 10f, abbreviation, PdfTypographyRole.OPTIONAL_DECORATIVE, 6.2f, 5.5f,
            align = PdfHorizontalAlignment.CENTER)

        val saveTop = top + 69f
        trainingMarker(s, p, x + 10f, saveTop + 8f, saveTraining)
        text(s, p, x + 21f, saveTop, width - 49f, 16f, "Tirada de salvación", PdfTypographyRole.BODY, 7.2f, 6.2f)
        text(s, p, x + width - 26f, saveTop, 20f, 16f, saveTotal, PdfTypographyRole.NUMERIC_COMPACT, 8f, 7f,
            align = PdfHorizontalAlignment.RIGHT)
        hairline(s, x + 8f, saveTop + 18f, x + width - 8f, saveTop + 18f)

        skills.forEachIndexed { i, row ->
            val rowTop = saveTop + 20f + i * 16.2f
            trainingMarker(s, p, x + 10f, rowTop + 7f, row.training)
            text(s, p, x + 21f, rowTop, width - 49f, 14f, row.name, PdfTypographyRole.BODY, 6.9f, 6.0f)
            text(s, p, x + width - 26f, rowTop, 20f, 14f, row.total, PdfTypographyRole.NUMERIC_COMPACT, 7.6f, 6.6f,
                align = PdfHorizontalAlignment.RIGHT)
            if (i < skills.lastIndex) hairline(s, x + 21f, rowTop + 15f, x + width - 8f, rowTop + 15f)
        }
    }

    private fun extendedHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        name: String,
        title: String,
    ) {
        fantasyFrame(s, 24f, 24f, 564f, 64f, 1.05f, fill = PAPER_TINT)
        text(
            s, p, 36f, 30f, 220f, 33f,
            name,
            PdfTypographyRole.CHARACTER_NAME, 18f, 8.8f,
            wrap = true,
            maxLines = 2,
        )
        hairline(s, 36f, 66f, 256f, 66f)
        text(
            s, p, 268f, 28f, 308f, 20f,
            "EXTENSIÓN", PdfTypographyRole.OPTIONAL_DECORATIVE, 8f, 7f,
            align = PdfHorizontalAlignment.RIGHT,
        )
        text(
            s, p, 268f, 48f, 308f, 22f,
            title, PdfTypographyRole.OPTIONAL_DECORATIVE, 13f, 10.5f,
            align = PdfHorizontalAlignment.RIGHT,
        )
    }

    private fun customAttributePanel(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        title: String,
        abbreviation: String,
        score: String,
        modifier: String,
        save: String,
        saveTraining: Training,
        skills: List<SkillRow>,
        note: String,
    ) {
        fantasyFrame(s, x, top, width, height, 0.9f)
        text(
            s, p, x + 8f, top + 7f, width - 16f, 18f,
            "$title ($abbreviation)",
            PdfTypographyRole.OPTIONAL_DECORATIVE,
            10f, 7.2f,
            wrap = true,
            maxLines = 2,
            align = PdfHorizontalAlignment.CENTER,
        )
        circleOutline(s, x + 38f, top + 58f, 28f)
        text(
            s, p, x + 12f, top + 35f, 52f, 42f,
            modifier, PdfTypographyRole.PRIMARY_VALUE, 24f, 20f,
            align = PdfHorizontalAlignment.CENTER,
        )
        miniRunicStat(s, p, x + 78f, top + 36f, 42f, 42f, "PUNT.", score)
        miniRunicStat(s, p, x + 126f, top + 36f, 40f, 42f, "SALV.", save)
        trainingMarker(s, p, x + 12f, top + 88f, saveTraining)
        text(
            s, p, x + 23f, top + 80f, width - 31f, 15f,
            "Competencia en salvación", PdfTypographyRole.BODY, 6.6f, 5.8f,
        )
        text(
            s, p, x + 10f, top + 101f, width - 20f, 15f,
            "Habilidades gobernadas por $abbreviation",
            PdfTypographyRole.OPTIONAL_DECORATIVE, 6.7f, 5.8f,
            align = PdfHorizontalAlignment.CENTER,
        )
        skills.forEachIndexed { index, row ->
            val rowTop = top + 122f + index * 23f
            trainingMarker(s, p, x + 12f, rowTop + 8f, row.training)
            text(
                s, p, x + 24f, rowTop, width - 56f, 18f,
                row.name,
                PdfTypographyRole.BODY, 7.8f, 6.0f,
                wrap = true,
                maxLines = 2,
            )
            text(
                s, p, x + width - 30f, rowTop, 22f, 18f,
                row.total, PdfTypographyRole.NUMERIC_COMPACT, 8.5f, 7.2f,
                align = PdfHorizontalAlignment.RIGHT,
            )
            hairline(s, x + 24f, rowTop + 20f, x + width - 8f, rowTop + 20f)
        }
        val blanksStart = top + 122f + skills.size * 23f
        repeat((CLASSIC_CUSTOM_SKILLS_PER_ATTRIBUTE_PANEL - skills.size).coerceAtLeast(0)) { index ->
            val ruleTop = blanksStart + index * 23f + 20f
            hairline(s, x + 24f, ruleTop, x + width - 8f, ruleTop)
        }
        text(
            s, p, x + 10f, top + height - 48f, width - 20f, 36f,
            note, PdfTypographyRole.NOTE_TEXT, 7.4f, 6.4f,
            wrap = true, maxLines = CLASSIC_CUSTOM_ATTRIBUTE_NOTE_LINES,
            vertical = PdfVerticalAlignment.TOP,
        )
    }

    private fun standardLinkedCustomGroup(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        title: String,
        skills: List<SkillRow>,
    ) {
        val height = 168f
        fantasyFrame(s, x, top, width, height, 0.65f)
        text(
            s, p, x + 7f, top + 7f, width - 14f, 17f,
            title, PdfTypographyRole.OPTIONAL_DECORATIVE, 8.2f, 7f,
            align = PdfHorizontalAlignment.CENTER,
        )
        skills.forEachIndexed { index, row ->
            val rowTop = top + 36f + index * 27f
            trainingMarker(s, p, x + 12f, rowTop + 8f, row.training)
            text(
                s, p, x + 25f, rowTop, width - 58f, 19f,
                row.name,
                PdfTypographyRole.BODY, 7.8f, 6.0f,
                wrap = true,
                maxLines = 2,
            )
            text(
                s, p, x + width - 29f, rowTop, 20f, 19f,
                row.total, PdfTypographyRole.NUMERIC_COMPACT, 8.4f, 7.2f,
                align = PdfHorizontalAlignment.RIGHT,
            )
            hairline(s, x + 25f, rowTop + 21f, x + width - 9f, rowTop + 21f)
        }
        var ruleTop = top + 36f + skills.size * 27f + 20f
        while (ruleTop <= top + height - 8f) {
            hairline(s, x + 25f, ruleTop, x + width - 9f, ruleTop)
            ruleTop += 25f
        }
    }

    private fun shieldStat(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
    ) {
        val cut = 8f
        s.saveGraphicsState()
        s.setStrokingColor(INk)
        s.setLineWidth(0.85f)
        val yTop = H - top
        val yBottom = H - top - height
        s.moveTo(x + cut, yTop)
        s.lineTo(x + width - cut, yTop)
        s.lineTo(x + width, yTop - cut)
        s.lineTo(x + width - 6f, yBottom + 11f)
        s.lineTo(x + width / 2f, yBottom)
        s.lineTo(x + 6f, yBottom + 11f)
        s.lineTo(x, yTop - cut)
        s.closePath()
        s.stroke()
        s.restoreGraphicsState()
        text(s, p, x + 5f, top + 6f, width - 10f, 12f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 7f, 6f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 6f, top + 20f, width - 12f, 28f, value, PdfTypographyRole.PRIMARY_VALUE, 18f, 15f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun miniRunicStat(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
    ) {
        fantasyFrame(s, x, top, width, height, 0.65f)
        text(s, p, x + 4f, top + 4f, width - 8f, 11f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 6.5f, 5.4f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 5f, top + 16f, width - 10f, height - 20f, value, PdfTypographyRole.PRIMARY_VALUE, 13.5f, 9.5f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun decorativeStat(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
    ) {
        fantasyFrame(s, x, top, width, height, 0.8f)
        text(s, p, x + 8f, top + 7f, width - 58f, height - 14f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 7.8f, 6.5f)
        circleOutline(s, x + width - 28f, top + height / 2f, 16f)
        text(s, p, x + width - 44f, top + 6f, 32f, height - 12f, value, PdfTypographyRole.PRIMARY_VALUE, 13f, 11f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun continuousFeatureEntries(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        entries: List<ClassicFeature>,
    ) {
        val physicalRows = (height / CLASSIC_TRAIT_ROW_STEP).toInt().coerceAtLeast(1)

        repeat(physicalRows) { row ->
            hairline(
                s,
                x,
                top + (row + 1) * CLASSIC_TRAIT_ROW_STEP,
                x + width,
                top + (row + 1) * CLASSIC_TRAIT_ROW_STEP,
            )
        }

        var rowIndex = 0
        entries.forEach { entry ->
            val nameLines = classicFeatureNameLines(entry)
            val sourceLines = classicFeatureSourceLines(entry)
            val headerRows = maxOf(nameLines.size, sourceLines.size, 1)
            val bodyLines = classicFeatureBodyLines(entry)

            check(rowIndex + headerRows + bodyLines.size <= physicalRows) {
                "Fantasy Trait column packing drifted after layout: ${entry.name}"
            }

            repeat(headerRows) { headerLineIndex ->
                val headerTop = top + rowIndex * CLASSIC_TRAIT_ROW_STEP
                nameLines.getOrNull(headerLineIndex)?.let { line ->
                    text(
                        s,
                        p,
                        x + 1f,
                        headerTop + 0.5f,
                        width * 0.68f - 1f,
                        CLASSIC_TRAIT_ROW_STEP - 1.5f,
                        line,
                        PdfTypographyRole.SPELL_NAME,
                        9f,
                        7.8f,
                        vertical = PdfVerticalAlignment.BOTTOM,
                    )
                }
                val sourceLineIndex =
                    headerLineIndex - (headerRows - sourceLines.size).coerceAtLeast(0)
                sourceLines.getOrNull(sourceLineIndex)?.let { line ->
                    text(
                        s,
                        p,
                        x + width * 0.68f,
                        headerTop + 0.5f,
                        width * 0.32f - 1f,
                        CLASSIC_TRAIT_ROW_STEP - 1.5f,
                        line,
                        PdfTypographyRole.OPTIONAL_DECORATIVE,
                        7f,
                        6f,
                        align = PdfHorizontalAlignment.RIGHT,
                        vertical = PdfVerticalAlignment.BOTTOM,
                    )
                }
                rowIndex += 1
            }

            bodyLines.forEach { line ->
                val rowTop = top + rowIndex * CLASSIC_TRAIT_ROW_STEP
                val result = p.drawTextBox(
                    s,
                    PdfTextBoxSpec(
                        rect = rect(
                            x + 2f,
                            rowTop + 0.5f,
                            width - 4f,
                            CLASSIC_TRAIT_ROW_STEP - 1.5f,
                        ),
                        text = line,
                        role = PdfTypographyRole.BODY,
                        preferredSizePt = 8.1f,
                        minimumSizePt = 8.1f,
                        horizontalAlignment = PdfHorizontalAlignment.LEFT,
                        verticalAlignment = PdfVerticalAlignment.BOTTOM,
                        wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                        maximumLines = 1,
                        horizontalPaddingPt = 0f,
                        verticalPaddingPt = 0.45f,
                        lineHeightMultiplier = 1f,
                        fontSizeMode = PdfFontSizeMode.FIXED,
                    ),
                )
                check(!result.hasOverflow) {
                    "Fantasy Trait semantic line overflow after pre-wrap: ${entry.name}"
                }
                rowIndex += 1
            }
        }
    }

    private fun classicFeatureNameLines(entry: ClassicFeature): List<String> =
        wrapForChars(entry.name, CLASSIC_FEATURE_NAME_CHARS)
            .ifEmpty { listOf("") }

    private fun classicFeatureSourceLines(entry: ClassicFeature): List<String> =
        wrapForChars(entry.source, CLASSIC_FEATURE_SOURCE_CHARS)
            .ifEmpty { listOf("") }

    private fun classicFeatureBodyLines(entry: ClassicFeature): List<String> =
        entry.description
            .replace("\r\n", "\n")
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .flatMap { wrapForChars(it, CLASSIC_TRAIT_BODY_CHARS) }

    private fun classicFeaturePhysicalRows(entry: ClassicFeature): Int =
        maxOf(
            classicFeatureNameLines(entry).size,
            classicFeatureSourceLines(entry).size,
            1,
        ) + classicFeatureBodyLines(entry).size

    private fun boundedBaseTraitPreview(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        traits: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>,
        fontSize: Float,
        lineGap: Float = 20f,
    ) {
        val physicalRows = (height / lineGap).toInt().coerceAtLeast(1)
        repeat(physicalRows) { row ->
            val ruleTop = top + (row + 1) * lineGap
            if (ruleTop <= top + height + 0.05f) {
                hairline(s, x, ruleTop, x + width, ruleTop)
            }
        }

        var rowIndex = 0
        traits.forEachIndexed { traitIndex, trait ->
            val remainingTraits = traits.size - traitIndex - 1
            val availableForTrait = (physicalRows - rowIndex - remainingTraits)
                .coerceAtLeast(0)
                .coerceAtMost(CLASSIC_RULED_ENTRY_LINES)
            var remaining = traitSummary(trait).trim()
            var rowsUsed = 0

            while (
                remaining.isNotBlank() &&
                rowsUsed < availableForTrait &&
                rowIndex < physicalRows
            ) {
                val rowTop = top + rowIndex * lineGap
                val result = p.drawTextBox(
                    s,
                    PdfTextBoxSpec(
                        rect = rect(x + 2f, rowTop + 0.5f, width - 4f, lineGap - 1.5f),
                        text = remaining,
                        role = PdfTypographyRole.NOTE_TEXT,
                        preferredSizePt = fontSize,
                        minimumSizePt = fontSize,
                        horizontalAlignment = PdfHorizontalAlignment.LEFT,
                        verticalAlignment = PdfVerticalAlignment.BOTTOM,
                        wrapPolicy = PdfWrapPolicy.WORD_WRAP,
                        maximumLines = 1,
                        horizontalPaddingPt = 0f,
                        verticalPaddingPt = 0.45f,
                        lineHeightMultiplier = 1f,
                        fontSizeMode = PdfFontSizeMode.FIXED,
                    ),
                )
                remaining = result.overflowText?.trim().orEmpty()
                rowIndex += 1
                rowsUsed += 1
            }

            if (remaining.isNotBlank()) {
                baseClippedTraitIds += trait.id
            }
        }
    }

    private fun ruledLines(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        count: Int,
    ) {
        val gap = height / count
        repeat(count) { index ->
            hairline(s, x, top + (index + 1) * gap, x + width, top + (index + 1) * gap)
        }
    }

    private fun inventoryHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
    ) {
        tableHeader(
            s, p, x, top,
            listOf(
                44f to "Cant.",
                196f to "Objeto",
                55f to "Peso",
                92f to "Estado",
                153f to "Ubicación / notas",
            ),
        )
    }

    private fun inventoryRow(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        row: InventoryRow,
    ) {
        val widths = listOf(44f, 196f, 55f, 92f, 153f)
        val values = listOf(row.quantity, row.name, row.weight, row.state, row.notes)
        var cursor = x
        values.forEachIndexed { index, value ->
            text(
                s, p, cursor + 3f, top, widths[index] - 6f, 20f, value,
                if (index == 1) PdfTypographyRole.SPELL_NAME else PdfTypographyRole.BODY,
                if (index == 1) 8.3f else 7.6f, 6.2f,
                wrap = index == 1,
                maxLines = if (index == 1) 2 else 1,
                align = if (index == 0 || index == 2) {
                    PdfHorizontalAlignment.CENTER
                } else {
                    PdfHorizontalAlignment.LEFT
                },
            )
            cursor += widths[index]
        }
        hairline(s, x, top + 22f, x + widths.sum(), top + 22f)
    }

    private fun inventoryBlankRow(
        s: PDPageContentStream,
        x: Float,
        top: Float,
    ) {
        hairline(s, x, top + 20f, x + 540f, top + 20f)
    }

    private fun specialItem(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        name: String,
        attuned: Boolean,
        note: String,
    ) {
        marker(
            s, p, x + 7f, top + 7f, 8f,
            if (attuned) PdfMarkerKind.DIAMOND_FILLED else PdfMarkerKind.DIAMOND_OUTLINE,
        )
        text(
            s, p, x + 18f, top, width - 18f, 15f,
            name,
            PdfTypographyRole.SPELL_NAME, 8.3f, 6.0f,
            wrap = true,
            maxLines = 2,
        )
        text(
            s, p, x + 18f, top + 15f, width - 18f, 18f,
            note, PdfTypographyRole.BODY, 7.2f, 6.2f,
            wrap = true,
            maxLines = CLASSIC_SPECIAL_ITEM_NOTE_LINES,
            vertical = PdfVerticalAlignment.TOP,
        )
        hairline(s, x + 18f, top + 36f, x + width, top + 36f)
    }

    private fun optionEntry(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        name: String,
        source: String,
        description: String,
    ) {
        text(
            s, p, x, top, 196f, 48f,
            name,
            PdfTypographyRole.SPELL_NAME, 9f, 7.8f,
            wrap = true,
            maxLines = CLASSIC_OPTION_NAME_LINES_PER_ROW,
            vertical = PdfVerticalAlignment.TOP,
        )
        text(
            s, p, x + 202f, top, 118f, 48f,
            source,
            PdfTypographyRole.OPTIONAL_DECORATIVE, 7.2f, 6.2f,
            wrap = true,
            maxLines = CLASSIC_OPTION_SOURCE_LINES_PER_ROW,
            align = PdfHorizontalAlignment.CENTER,
            vertical = PdfVerticalAlignment.TOP,
        )
        text(
            s, p, x + 326f, top, width - 326f, 48f,
            description, PdfTypographyRole.BODY, 8f, 6.8f,
            wrap = true, maxLines = CLASSIC_OPTION_DETAIL_LINES,
            vertical = PdfVerticalAlignment.TOP,
        )
        hairline(s, x, top + 56f, x + width, top + 56f)
    }

    private fun resourceTableHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
    ) {
        tableHeader(
            s, p, x, top,
            listOf(
                162f to "Recurso",
                68f to "Actual / máx.",
                104f to "Recuperación",
                72f to "Fuente",
                134f to "Notas",
            ),
        )
    }

    private fun resourceTableRow(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        row: ClassicResourceRow,
    ) {
        val values = listOf(
            row.name,
            row.value,
            classicBaseExcerpt(
                row.recovery,
                CLASSIC_RESOURCE_RECOVERY_CHARS,
                CLASSIC_RESOURCE_NOTE_LINES,
            ),
            row.source,
            row.notes,
        )
        val widths = listOf(162f, 68f, 104f, 72f, 134f)
        var cursor = x
        values.forEachIndexed { index, value ->
            val semanticIdentityColumn = index == 0 || index == 3
            text(
                s, p, cursor + 3f, top, widths[index] - 6f, 40f, value,
                if (index == 0) PdfTypographyRole.SPELL_NAME else PdfTypographyRole.BODY,
                if (index == 0) 8.2f else 7.6f, 6.5f,
                wrap = semanticIdentityColumn || index == 2 || index == 4,
                maxLines = when (index) {
                    0 -> CLASSIC_RESOURCE_NAME_LINES_PER_ROW
                    3 -> CLASSIC_RESOURCE_SOURCE_LINES_PER_ROW
                    2, 4 -> CLASSIC_RESOURCE_NOTE_LINES
                    else -> 1
                },
                align = if (index == 1) PdfHorizontalAlignment.CENTER else PdfHorizontalAlignment.LEFT,
                vertical = PdfVerticalAlignment.TOP,
            )
            cursor += widths[index]
        }
        row.oneUseAvailable?.let { available ->
            marker(
                s,
                p,
                x + 162f + 34f,
                top + 19f,
                8f,
                if (available) PdfMarkerKind.CIRCLE_OUTLINE else PdfMarkerKind.CIRCLE_FILLED,
            )
        }
        hairline(s, x, top + 42f, x + widths.sum(), top + 42f)
    }

    private fun resourceBlankRow(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
    ) {
        hairline(s, x, top + 20f, x + 540f, top + 20f)
        marker(s, p, x + 6f, top + 10f, 6f, PdfMarkerKind.CIRCLE_OUTLINE)
    }

    private fun titledFrame(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        title: String,
    ) {
        fantasyFrame(s, x, top, width, height, 0.8f)
        val labelWidth = (title.length * 5.3f + 26f).coerceIn(88f, width - 24f)
        fillRect(s, x + (width - labelWidth) / 2f, top - 1f, labelWidth, 18f, Color.WHITE)
        text(
            s, p, x + (width - labelWidth) / 2f + 6f, top + 1f,
            labelWidth - 12f, 14f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 8.6f, 7.2f,
            align = PdfHorizontalAlignment.CENTER,
        )
    }

    private fun tableHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        columns: List<Pair<Float, String>>,
    ) {
        var cursor = x
        columns.forEach { (width, label) ->
            text(s, p, cursor + 2f, top, width - 4f, 16f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 6.8f, 5.8f,
                align = PdfHorizontalAlignment.CENTER)
            cursor += width
        }
        hairline(s, x, top + 17f, cursor, top + 17f)
    }

private fun ruledTextArea(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        content: List<String>,
        fontSize: Float,
        lineGap: Float = 20f,
        role: PdfTypographyRole = PdfTypographyRole.NOTE_TEXT,
    ) {
        val physicalRows = (height / lineGap).toInt().coerceAtLeast(1)
        repeat(physicalRows) { row ->
            val ruleTop = top + (row + 1) * lineGap
            if (ruleTop <= top + height + 0.05f) {
                hairline(s, x, ruleTop, x + width, ruleTop)
            }
        }

        var rowIndex = 0
        content
            .flatMap { entry ->
                entry.replace("\r\n", "\n")
                    .split("\n")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            }
            .forEach { physicalLine ->
                var remaining = physicalLine
                while (remaining.isNotBlank() && rowIndex < physicalRows) {
                    val rowTop = top + rowIndex * lineGap
                    val result = p.drawTextBox(
                        s,
                        PdfTextBoxSpec(
                            rect = rect(x + 2f, rowTop + 0.5f, width - 4f, lineGap - 1.5f),
                            text = remaining,
                            role = role,
                            preferredSizePt = fontSize,
                            minimumSizePt = fontSize,
                            horizontalAlignment = PdfHorizontalAlignment.LEFT,
                            verticalAlignment = PdfVerticalAlignment.BOTTOM,
                            wrapPolicy = PdfWrapPolicy.WORD_WRAP,
                            maximumLines = 1,
                            horizontalPaddingPt = 0f,
                            verticalPaddingPt = 0.45f,
                            lineHeightMultiplier = 1f,
                            fontSizeMode = PdfFontSizeMode.FIXED,
                        ),
                    )
                    remaining = result.overflowText?.trim().orEmpty()
                    rowIndex += 1
                }
                if (remaining.isNotBlank()) {
                    overflowDiagnostics += "ruled-area value='${physicalLine}' overflow='${remaining}'"
                }
            }
    }

    private fun ruledBackground(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        firstRuleOffset: Float = 23f,
        lineGap: Float = 20f,
    ) {
        var ruleTop = top + firstRuleOffset
        while (ruleTop <= top + height) {
            hairline(s, x, ruleTop, x + width, ruleTop)
            ruleTop += lineGap
        }
    }

    private fun trainingMarker(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        centerX: Float,
        centerTop: Float,
        training: Training,
    ) {
        val kind = when (training) {
            Training.NONE -> PdfMarkerKind.CIRCLE_OUTLINE
            Training.PROFICIENT -> PdfMarkerKind.CIRCLE_FILLED
            Training.EXPERTISE -> PdfMarkerKind.DOUBLE_CIRCLE
        }
        marker(s, p, centerX, centerTop, 8f, kind)
    }

    private fun marker(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        centerX: Float,
        centerTop: Float,
        size: Float,
        kind: PdfMarkerKind,
    ) {
        p.drawMarker(
            s,
            centerX = centerX,
            centerY = H - centerTop,
            sizePt = size,
            kind = kind,
            family = PdfSymbolFamily.V1_DERIVED,
        )
    }

    private fun fantasyFrame(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        lineWidth: Float,
        fill: Color? = null,
    ) {
        val c = 8f.coerceAtMost(width / 5f).coerceAtMost(height / 5f)
        val yTop = H - top
        val yBottom = H - top - height

        s.saveGraphicsState()
        fill?.let { s.setNonStrokingColor(it) }
        s.setStrokingColor(INk)
        s.setLineWidth(lineWidth)
        s.moveTo(x + c, yTop)
        s.lineTo(x + width - c, yTop)
        s.lineTo(x + width, yTop - c)
        s.lineTo(x + width, yBottom + c)
        s.lineTo(x + width - c, yBottom)
        s.lineTo(x + c, yBottom)
        s.lineTo(x, yBottom + c)
        s.lineTo(x, yTop - c)
        s.closePath()
        if (fill != null) s.fillAndStroke() else s.stroke()

        s.setLineWidth(0.35f)
        val d = 4f
        s.moveTo(x + c + d, yTop - 3f)
        s.lineTo(x + width - c - d, yTop - 3f)
        s.moveTo(x + c + d, yBottom + 3f)
        s.lineTo(x + width - c - d, yBottom + 3f)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun circleOutline(s: PDPageContentStream, centerX: Float, centerTop: Float, radius: Float) {
        s.saveGraphicsState()
        s.setStrokingColor(Color(125, 125, 125))
        s.setLineWidth(0.75f)
        circlePath(s, centerX, H - centerTop, radius)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun circlePath(s: PDPageContentStream, cx: Float, cy: Float, r: Float) {
        val c = r * 0.552284749831f
        s.moveTo(cx + r, cy)
        s.curveTo(cx + r, cy + c, cx + c, cy + r, cx, cy + r)
        s.curveTo(cx - c, cy + r, cx - r, cy + c, cx - r, cy)
        s.curveTo(cx - r, cy - c, cx - c, cy - r, cx, cy - r)
        s.curveTo(cx + c, cy - r, cx + r, cy - c, cx + r, cy)
        s.closePath()
    }


    private fun grid(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        columns: Int,
        rows: Int,
    ) {
        val y = H - top - height
        s.saveGraphicsState()
        s.setStrokingColor(GRID_COLOR)
        s.setLineWidth(0.3f)
        s.addRect(x, y, width, height)
        s.stroke()
        repeat(columns - 1) { index ->
            val xx = x + width * (index + 1) / columns
            s.moveTo(xx, y)
            s.lineTo(xx, y + height)
        }
        repeat(rows - 1) { index ->
            val yy = y + height * (index + 1) / rows
            s.moveTo(x, yy)
            s.lineTo(x + width, yy)
        }
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun fillRect(
        s: PDPageContentStream,
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

    private fun hairline(
        s: PDPageContentStream,
        x1: Float,
        top1: Float,
        x2: Float,
        top2: Float,
    ) {
        s.saveGraphicsState()
        s.setStrokingColor(LINE_COLOR)
        s.setLineWidth(0.34f)
        s.moveTo(x1, H - top1)
        s.lineTo(x2, H - top2)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun text(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        role: PdfTypographyRole,
        preferred: Float,
        minimum: Float,
        wrap: Boolean = false,
        maxLines: Int = 1,
        align: PdfHorizontalAlignment = PdfHorizontalAlignment.LEFT,
        vertical: PdfVerticalAlignment = PdfVerticalAlignment.CENTER,
    ) {
        val result = p.drawTextBox(
            s,
            PdfTextBoxSpec(
                rect = rect(x, top, width, height),
                text = value,
                role = role,
                preferredSizePt = preferred,
                minimumSizePt = minimum,
                horizontalAlignment = align,
                verticalAlignment = vertical,
                wrapPolicy = if (wrap) PdfWrapPolicy.WORD_WRAP else PdfWrapPolicy.SINGLE_LINE,
                maximumLines = maxLines,
                horizontalPaddingPt = 0.6f,
                verticalPaddingPt = 0.35f,
            ),
        )
        if (result.hasOverflow) {
            overflowDiagnostics += "value='$value' overflow='${result.overflowText}'"
        }
    }

    private fun footer(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        pageNumber: Int,
        label: String,
    ) {
        hairline(s, 24f, 744f, 588f, 744f)
        text(s, p, 24f, 750f, 430f, 14f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 7.2f, 6.2f)
        text(s, p, 506f, 750f, 82f, 14f, "PÁGINA $pageNumber", PdfTypographyRole.OPTIONAL_DECORATIVE, 7.2f, 6.2f,
            align = PdfHorizontalAlignment.RIGHT)
    }

    private fun rect(x: Float, top: Float, width: Float, height: Float) =
        PdfRect(x, H - top - height, width, height)

    private fun addPage(doc: PDDocument): PDPage =
        PDPage(PDRectangle(W, H)).also(doc::addPage)

    private data class SkillRow(
        val name: String,
        val total: String,
        val training: Training,
    )

    private data class ClassicSpellRow(
        val name: String,
        val prepared: Boolean,
    )

    private data class InventoryRow(
        val quantity: String,
        val name: String,
        val weight: String,
        val state: String,
        val notes: String,
    )

    private data class ClassicSpecialItem(
        val name: String,
        val attuned: Boolean,
        val note: String,
    )

    private data class ClassicResourceRow(
        val name: String,
        val value: String,
        val oneUseAvailable: Boolean?,
        val recovery: String,
        val source: String,
        val notes: String,
    )

    private data class ClassicCombatReferenceRow(
        val name: String,
        val bonus: String,
        val detail: String,
    )

    private data class ClassicNarrativeModule(
        val heading: String,
        val lines: List<String>,
    )

    private data class ClassicCombatLayoutRow(
        val nameLines: List<String>,
        val bonus: String,
        val detailLines: List<String>,
        val height: Float,
    )

    private data class ClassicOptionRow(
        val name: String,
        val source: String,
        val description: String,
    )

    private data class ClassicFeature(
        val name: String,
        val source: String,
        val description: String,
        val type: CharacterTraitType,
    )

    private data class ClassicTraitColumn(
        val preferredSlotId: String,
        val heading: String,
        val entries: List<ClassicFeature>,
    )

    private data class ClassicBaseRuledProjection(
        val content: List<String>,
        val clippedTraitIds: Set<kotlin.uuid.Uuid>,
    )

    private data class ClassicCustomStatisticsPage(
        val customSlices: List<CustomAttributeSlice>,
        val standardSlices: List<StandardCustomGroupSlice>,
    )

    private data class CustomAttributeSlice(
        val projection: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomAttributeProjection,
        val skills: List<io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomSkillProjection>,
        val note: String,
    )

    private data class StandardCustomGroupSlice(
        val title: String,
        val skills: List<io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomSkillProjection>,
    )

    private enum class Training {
        NONE,
        PROFICIENT,
        EXPERTISE,
    }

    private companion object {
        const val W = 612f
        const val H = 792f
        const val BASE_COMBAT_CAPACITY = 4
        const val CLASSIC_NARRATIVE_MODULES_PER_PAGE = 4
        const val CLASSIC_NARRATIVE_MODULE_WIDTH = 226f
        const val CLASSIC_NARRATIVE_MODULE_HEIGHT = 282f
        const val CLASSIC_NARRATIVE_TEXT_HEIGHT = 238f
        const val CLASSIC_NARRATIVE_ROW_STEP = 20f
        const val CLASSIC_NARRATIVE_ROWS_PER_MODULE = 11
        const val CLASSIC_NARRATIVE_BODY_SIZE = 7.6f
        const val CLASSIC_NARRATIVE_CHARS_PER_LINE = 48
        val CLASSIC_NARRATIVE_MODULE_PLACEMENTS = listOf(
            24f to 112f,
            362f to 112f,
            24f to 436f,
            362f to 436f,
        )
        const val CLASSIC_COMBAT_LINES_PER_PAGE = 27
        const val CLASSIC_COMBAT_REFERENCE_CHARS = 86
        const val CLASSIC_COMBAT_TABLE_X = 36f
        const val CLASSIC_COMBAT_NAME_WIDTH = 250f
        const val CLASSIC_COMBAT_BONUS_WIDTH = 60f
        const val CLASSIC_COMBAT_DETAIL_WIDTH = 230f
        const val CLASSIC_COMBAT_TABLE_WIDTH =
            CLASSIC_COMBAT_NAME_WIDTH + CLASSIC_COMBAT_BONUS_WIDTH + CLASSIC_COMBAT_DETAIL_WIDTH
        const val CLASSIC_COMBAT_HEADER_TOP = 148f
        const val CLASSIC_COMBAT_FIRST_ROW_TOP = 174f
        const val CLASSIC_COMBAT_PAGE_BOTTOM = 700f
        const val CLASSIC_COMBAT_AVAILABLE_HEIGHT =
            CLASSIC_COMBAT_PAGE_BOTTOM - CLASSIC_COMBAT_FIRST_ROW_TOP
        const val CLASSIC_COMBAT_MIN_ROW_HEIGHT = 23f
        const val CLASSIC_COMBAT_ROW_LINE_HEIGHT = 13f
        const val CLASSIC_COMBAT_ROW_VERTICAL_PADDING = 4f
        const val CLASSIC_COMBAT_CONTINUATION_NAME_CHARS = 42
        const val CLASSIC_COMBAT_CONTINUATION_DETAIL_CHARS = 38
        const val BASE_CLASS_TRAIT_CAPACITY = 4
        const val CLASSIC_BASE_CLASS_RULE_ROWS = 7
        const val BASE_SPECIES_TRAIT_CAPACITY = 3
        const val BASE_FEAT_CAPACITY = 1
        const val BASE_EQUIPMENT_CAPACITY = 7
        const val BASE_ADDITIONAL_TRAIT_CAPACITY = 2
        const val BASE_LANGUAGE_CAPACITY = 4
        const val BASE_COMPANION_CAPACITY = 1
        const val CLASSIC_CUSTOM_ATTRIBUTE_PANELS_PER_ROW = 3
        const val CLASSIC_CUSTOM_ATTRIBUTE_PANELS_PER_ATTRIBUTE_ONLY_PAGE = 6
        const val CLASSIC_CUSTOM_ATTRIBUTE_SECOND_ROW_OFFSET = 302f
        const val CLASSIC_CUSTOM_SKILLS_PER_ATTRIBUTE_PANEL = 4
        const val CLASSIC_CUSTOM_ATTRIBUTE_NOTE_LINES = 3
        const val CLASSIC_STANDARD_GROUPS_PER_PAGE = 3
        const val CLASSIC_STANDARD_CUSTOM_SKILLS_PER_GROUP = 4
        const val CLASSIC_TRAIT_FRAME_TOP = 112f
        const val CLASSIC_TRAIT_FRAME_WIDTH = 276f
        const val CLASSIC_TRAIT_FRAME_HEIGHT = 606f
        const val CLASSIC_TRAIT_CONTENT_TOP = 148f
        const val CLASSIC_TRAIT_CONTENT_WIDTH = 252f
        const val CLASSIC_TRAIT_CONTENT_HEIGHT = 552f
        const val CLASSIC_TRAIT_ROW_STEP = 20f
        const val CLASSIC_TRAIT_PHYSICAL_ROWS_PER_FRAME = 27
        const val CLASSIC_TRAIT_LEFT_SLOT_ID = "fantasy-traits-left"
        const val CLASSIC_TRAIT_RIGHT_SLOT_ID = "fantasy-traits-right"
        const val CLASSIC_TRAIT_SINGLE_LAYOUT_ID = "fantasy-traits-single-native-frame"
        const val CLASSIC_TRAIT_TWO_COLUMN_LAYOUT_ID = "fantasy-traits-two-native-frames"
        const val CLASSIC_TRAIT_BODY_CHARS = 58
        const val CLASSIC_TRAIT_BODY_LINES = 4
        const val CLASSIC_FEATURE_NAME_CHARS = 26
        const val CLASSIC_FEATURE_SOURCE_CHARS = 16
        const val CLASSIC_HEADER_NAME_CHARS = 20
        const val CLASSIC_IDENTITY_VALUE_CHARS = 22
        const val CLASSIC_BACKGROUND_NARRATIVE_CHARS = 50
        const val CLASSIC_BACKGROUND_NARRATIVE_LINES = 3
        const val CLASSIC_BACKGROUND_DETAIL_CHARS = 46
        const val CLASSIC_BACKGROUND_DETAIL_LINES = 2
        const val CLASSIC_RULED_ENTRY_CHARS = 64
        const val CLASSIC_RULED_ENTRY_LINES = 2
        const val CLASSIC_SPECIES_NAME_CHARS = 28
        const val CLASSIC_COMBAT_NAME_CHARS = 30
        const val CLASSIC_COMBAT_DETAIL_CHARS = 34
        const val CLASSIC_COMBAT_PREVIEW_CHARS = 20
        const val CLASSIC_BASE_SLOT_MARKERS = 4
        const val CLASSIC_RESOURCE_ROWS_PER_PAGE = 4
        // The approved Fantasy continuation page already occupies one 564 x 606 native frame
        // from y=112 to y=718. Resources use 48 pt row rhythm; 11 complete rows end at y=698.
        // Options use 68 pt entry rhythm; 8 complete entries end at y=680. These full layouts
        // reclaim only the sibling frame that has been released; no row/entry geometry changes.
        const val CLASSIC_RESOURCE_FULL_ROWS_PER_PAGE = 11
        const val CLASSIC_OPTION_FULL_ROWS_PER_PAGE = 8
        const val CLASSIC_FULL_FRAME_TOP = 112f
        const val CLASSIC_RESOURCE_FULL_FRAME_HEIGHT = 606f
        const val CLASSIC_RESOURCE_SPLIT_FRAME_HEIGHT = 316f
        const val CLASSIC_OPTION_SPLIT_FRAME_TOP = 442f
        const val CLASSIC_OPTION_SPLIT_FRAME_HEIGHT = 276f
        const val CLASSIC_RESOURCE_FIRST_ROW_TOP = 176f
        const val CLASSIC_RESOURCE_ROW_STEP = 48f
        const val CLASSIC_OPTION_SPLIT_FIRST_ROW_TOP = 478f
        const val CLASSIC_OPTION_FULL_FIRST_ROW_TOP = 148f
        const val CLASSIC_OPTION_ROW_STEP = 68f
        const val CLASSIC_RESOURCE_OPTIONS_SPLIT_LAYOUT_ID = "fantasy-resources-options-split"
        const val CLASSIC_RESOURCE_FULL_LAYOUT_ID = "fantasy-resources-full"
        const val CLASSIC_OPTION_FULL_LAYOUT_ID = "fantasy-options-full"
        const val CLASSIC_RESOURCE_NAME_CHARS = 24
        const val CLASSIC_RESOURCE_NAME_LINES_PER_ROW = 2
        const val CLASSIC_RESOURCE_RECOVERY_CHARS = 18
        const val CLASSIC_RESOURCE_SOURCE_CHARS = 12
        const val CLASSIC_RESOURCE_SOURCE_LINES_PER_ROW = 2
        const val CLASSIC_RESOURCE_NOTE_CHARS = 30
        const val CLASSIC_RESOURCE_NOTE_LINES = 2
        const val CLASSIC_OPTION_ROWS_PER_PAGE = 3
        const val CLASSIC_OPTION_NAME_CHARS = 28
        const val CLASSIC_OPTION_NAME_LINES_PER_ROW = 2
        const val CLASSIC_OPTION_SOURCE_CHARS = 18
        const val CLASSIC_OPTION_SOURCE_LINES_PER_ROW = 2
        const val CLASSIC_OPTION_DETAIL_CHARS = 48
        const val CLASSIC_OPTION_DETAIL_LINES = 3
        const val CLASSIC_INVENTORY_ROWS_PER_PAGE = 12
        const val CLASSIC_SPECIAL_ITEMS_PER_PAGE = 4
        const val CLASSIC_SPECIAL_ITEM_NAME_CHARS = 30
        const val CLASSIC_SPECIAL_ITEM_NOTE_CHARS = 42
        const val CLASSIC_SPECIAL_ITEM_NOTE_LINES = 2
        const val CLASSIC_INVENTORY_NOTES_PER_PAGE = 3
        const val CLASSIC_BASE_INVENTORY_NAME_CHARS = 30
        const val CLASSIC_BASE_INVENTORY_NOTE_CHARS = 24
        const val CLASSIC_INVENTORY_ROW_NAME_CHARS = 34
        const val CLASSIC_INVENTORY_ROW_STATE_CHARS = 26
        const val CLASSIC_INVENTORY_ROW_NOTE_CHARS = 26
        const val CLASSIC_INVENTORY_NOTE_CHARS = 50
        const val CLASSIC_INVENTORY_NOTE_LINES = 2
        const val CLASSIC_BASE_VALUABLE_CAPACITY = 1
        const val CLASSIC_BASE_LANGUAGE_NAME_CHARS = 20
        const val CLASSIC_BASE_ALLY_VALUE_CHARS = 20
        const val CLASSIC_SPELL_NAME_CHARS = 24
        const val CLASSIC_SPELLCASTING_ABILITY_CHARS = 18
        const val CLASSIC_CUSTOM_ATTRIBUTE_TITLE_CHARS = 26
        const val CLASSIC_CUSTOM_ABBREVIATION_CHARS = 8
        const val CLASSIC_CUSTOM_SKILL_NAME_CHARS = 22
        const val CLASSIC_BASE_CANTRIP_CAPACITY = 5
        const val CLASSIC_BASE_LEVEL1_CAPACITY = 13
        const val CLASSIC_BASE_LEVEL2_CAPACITY = 9
        const val CLASSIC_BASE_LEVEL3_CAPACITY = 9
        const val CLASSIC_BASE_LEVEL4_CAPACITY = 5
        const val CLASSIC_BASE_LEVEL5_CAPACITY = 5
        const val CLASSIC_BASE_HIGH_LEVEL_CAPACITY = 5
        const val CLASSIC_EXT_TOP_ROWS = 9
        const val CLASSIC_EXT_BOTTOM_ROWS = 10
        const val CLASSIC_NOTES_FRAME_X = 24f
        const val CLASSIC_NOTES_FRAME_TOP = 112f
        const val CLASSIC_NOTES_FRAME_WIDTH = 360f
        const val CLASSIC_NOTES_FRAME_HEIGHT = 606f
        const val CLASSIC_NOTES_LEFT_X = 36f
        const val CLASSIC_NOTES_COLUMN_WIDTH = 336f
        const val CLASSIC_NOTES_FIRST_ROW_TOP = 148f
        const val CLASSIC_NOTES_ROW_STEP = 20f
        const val CLASSIC_NOTES_ROWS_PER_COLUMN = 27
        const val CLASSIC_NOTES_HORIZONTAL_PADDING = 3f
        const val CLASSIC_NOTES_HEADING_SIZE = 8.8f
        const val CLASSIC_NOTES_HEADING_MINIMUM = 6.5f
        const val CLASSIC_NOTES_BODY_SIZE = 8.4f
        const val CLASSIC_NOTES_BODY_MINIMUM = 6.3f
        const val CLASSIC_NOTES_CONTINUITY_SIZE = 7.2f
        const val CLASSIC_NOTES_CONTINUITY_MINIMUM = 5.8f
        const val CLASSIC_REFERENCE_FULL_PAGE_LINES = 27
        const val CLASSIC_NOTES_ENTRIES_PER_PAGE = 13
        const val CLASSIC_NOTES_CHARS_PER_LINE = 58
        const val CLASSIC_NOTES_LINES_PER_ENTRY = 2
        const val CLASSIC_REFERENCE_LINES_PER_PAGE = 12
        const val CLASSIC_REFERENCE_CHARS_PER_LINE = 32

        val INk = Color(42, 42, 42)
        val PAPER_TINT = Color(248, 247, 243)
        val LINE_COLOR = Color(146, 146, 142)
        val GRID_COLOR = Color(210, 210, 205)

        val CLASSIC_THEME = PdfTypographyTheme(
            id = "classic-dnd-style-run2-production",
            resourcesByRole = mapOf(
                PdfTypographyRole.CHARACTER_NAME to "fonts/pdf/text/Kalam-Bold.ttf",
                PdfTypographyRole.HANDWRITTEN_NAME to "fonts/pdf/text/Kalam-Bold.ttf",
                PdfTypographyRole.PRIMARY_VALUE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
                PdfTypographyRole.SECONDARY_VALUE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
                PdfTypographyRole.BODY to "fonts/pdf/text/FiraSans-Regular.ttf",
                PdfTypographyRole.COMPACT_TABLE to "fonts/pdf/text/FiraSans-Regular.ttf",
                PdfTypographyRole.NUMERIC_COMPACT to "fonts/pdf/text/FiraSans-SemiBold.ttf",
                PdfTypographyRole.NOTE_TEXT to "fonts/pdf/text/FiraSans-Regular.ttf",
                PdfTypographyRole.SPELL_NAME to "fonts/pdf/text/FiraSans-SemiBold.ttf",
                PdfTypographyRole.OPTIONAL_DECORATIVE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
            ),
        )
    }
}
