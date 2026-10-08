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
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetWritableTrackerState
import io.github.mrsimkin.dndcustomaid.shared.character.spellAttackModifier
import io.github.mrsimkin.dndcustomaid.shared.character.spellSaveDc
import io.github.mrsimkin.dndcustomaid.shared.character.pdfCampaignNoteParagraphs
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetIntegratedAttributeTitle
import io.github.mrsimkin.dndcustomaid.shared.character.pdfCompactEquipmentLabel
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetWritableUsesTrackerOrNull
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomSkillProjection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetSemanticModule
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBidirectionalContinuation
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetContinuationEndpoint
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetContinuationSurface
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetSemanticRecordRef
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetModuleDemand
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedLayoutSlot
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedLayoutTemplate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedPageComposer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedCompositionStep
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedCompositionTrace
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPhysicalPaginationTrace
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPaginationStreamTrace
import io.github.mrsimkin.dndcustomaid.shared.character.traceWithNativeSlotUtilization
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetDirectCompositionTrace
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPaginationTraceEntry
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedGlobalCoordinator
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedGlobalFront
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
import io.github.mrsimkin.dndcustomaid.shared.character.standardCurrencyKindOrNull
import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.InputStream
import kotlin.math.max
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
    private val paginationTraceSink: (PcSheetPaginationTraceEntry) -> Unit = {},
) {
    private val resources by lazy {
        Resources.load(document, sourceTemplate, resourceLoader)
    }
    private var layerSerial = 0
    private val isolatedSourceCropCache = mutableMapOf<SourceCropKey, PDImageXObject>()

    fun appendExtendedPages(plan: PcSheetPdfRenderPlan) {
        require(plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V1)
        PcSheetExtendedGlobalCoordinator.plan(
            activeModules = activeGlobalExtendedModules(plan),
            fronts = customV1GlobalFronts(),
        ).forEach { front ->
            when (front.id) {
                V1_GLOBAL_STATS_FRONT_ID -> appendCustomStatisticsPages(plan)
                V1_GLOBAL_NARRATIVE_TRAITS_FRONT_ID -> appendNarrativeAndTraitsExtendedPages(plan)
                V1_GLOBAL_COMBAT_FRONT_ID -> appendCombatExtendedPages(plan)
                V1_GLOBAL_RESOURCES_FRONT_ID -> appendResourcesExtendedPages(plan)
                V1_GLOBAL_INVENTORY_FRONT_ID -> appendInventoryExtendedPages(plan)
                V1_GLOBAL_SPELLS_FRONT_ID -> appendSpellExtendedPages(plan)
                V1_GLOBAL_NOTES_FRONT_ID -> appendNotesExtendedPages(plan)
                else -> error("Unknown Custom-v1 global Extended front: ${front.id}")
            }
        }
    }

    private var paginationTraceDecisionOrdinal = 0

    private fun recordPaginationTrace(
        frontId: String,
        trace: PcSheetExtendedCompositionTrace,
    ) {
        paginationTraceSink(
            PcSheetPaginationTraceEntry(
                family = PcSheetVisualFamily.CUSTOM_V1,
                frontId = frontId,
                decisionOrdinal = paginationTraceDecisionOrdinal++,
                composition = trace,
            ),
        )
    }

    private fun recordPaginationTrace(
        frontId: String,
        step: PcSheetExtendedCompositionStep,
    ) = recordPaginationTrace(frontId, step.trace)

    private fun activeGlobalExtendedModules(
        plan: PcSheetPdfRenderPlan,
    ): Set<PcSheetSemanticModule> = buildSet {
        val aggregate = plan.snapshot.aggregate
        val stats = plan.snapshot.customStatistics

        if (
            PcSheetExtendedPageKind.CUSTOM_STATISTICS in plan.mandatoryExtendedPages &&
            !stats.isEmpty
        ) {
            add(PcSheetSemanticModule.CUSTOM_STATISTICS)
        }
        if (narrativeContinuationModules(plan).isNotEmpty()) {
            add(PcSheetSemanticModule.BACKGROUND_STORY)
        }
        if (packV1TraitFlow(traitFlowBlocks(plan)).isNotEmpty()) {
            add(PcSheetSemanticModule.TRAITS)
        }
        if (combatReferenceRows(plan).isNotEmpty()) {
            add(PcSheetSemanticModule.COMBAT_ACTIONS)
        }
        if (aggregate.sheet.resources.isNotEmpty() || aggregate.successor.customMarkers.isNotEmpty()) {
            add(PcSheetSemanticModule.RESOURCES)
        }
        if (aggregate.sheet.classOptions.isNotEmpty()) {
            add(PcSheetSemanticModule.CLASS_CHOICES)
        }
        addAll(inventoryExtendedModules(plan))
        if (spellContinuationPageCount(plan) > 0) {
            add(PcSheetSemanticModule.SPELLS)
        }
        if (packedNotes(plan).extendedPageCount > 0) {
            add(PcSheetSemanticModule.NOTES)
        }
    }

    private fun customV1GlobalFronts(): List<PcSheetExtendedGlobalFront> = listOf(
        PcSheetExtendedGlobalFront(
            id = V1_GLOBAL_STATS_FRONT_ID,
            modules = setOf(PcSheetSemanticModule.CUSTOM_STATISTICS),
            priority = 10,
        ),
        PcSheetExtendedGlobalFront(
            id = V1_GLOBAL_NARRATIVE_TRAITS_FRONT_ID,
            modules = setOf(
                PcSheetSemanticModule.BACKGROUND_STORY,
                PcSheetSemanticModule.TRAITS,
            ),
            priority = 20,
        ),
        PcSheetExtendedGlobalFront(
            id = V1_GLOBAL_COMBAT_FRONT_ID,
            modules = setOf(PcSheetSemanticModule.COMBAT_ACTIONS),
            priority = 30,
        ),
        PcSheetExtendedGlobalFront(
            id = V1_GLOBAL_RESOURCES_FRONT_ID,
            modules = setOf(
                PcSheetSemanticModule.RESOURCES,
                PcSheetSemanticModule.CLASS_CHOICES,
            ),
            priority = 40,
        ),
        PcSheetExtendedGlobalFront(
            id = V1_GLOBAL_INVENTORY_FRONT_ID,
            modules = setOf(
                PcSheetSemanticModule.ORDINARY_EQUIPMENT,
                PcSheetSemanticModule.SPECIAL_EQUIPMENT,
            ),
            priority = 50,
        ),
        PcSheetExtendedGlobalFront(
            id = V1_GLOBAL_SPELLS_FRONT_ID,
            modules = setOf(PcSheetSemanticModule.SPELLS),
            priority = 60,
        ),
        PcSheetExtendedGlobalFront(
            id = V1_GLOBAL_NOTES_FRONT_ID,
            modules = setOf(PcSheetSemanticModule.NOTES),
            priority = 90,
        ),
    )

    private fun appendCustomStatisticsPages(plan: PcSheetPdfRenderPlan) {
        val stats = plan.snapshot.customStatistics
        val modules = buildModules(plan)
        val definitionLines = stats.attributes.flatMap { projection ->
            val note = projection.attribute.notes.orEmpty().trim()
            if (note.isEmpty()) {
                emptyList()
            } else {
                wrapByWidth(
                    pcSheetIntegratedAttributeTitle(projection.attribute.name, projection.attribute.abbreviation) + ": " + note,
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

        if (modules.isEmpty() && definitionLines.isEmpty() && noteLines.isEmpty()) return

        var moduleOffset = 0
        var definitionOffset = 0
        var noteOffset = 0
        var pageIndex = 0

        while (
            moduleOffset < modules.size ||
            definitionOffset < definitionLines.size ||
            noteOffset < noteLines.size
        ) {
            val pageModules = modules
                .drop(moduleOffset)
                .take(MODULES_PER_PAGE)
            val pageDefinitions = definitionLines
                .drop(definitionOffset)
                .take(DEFINITION_LINES_PER_PAGE)
            val definitionRowsPerColumn =
                if (pageDefinitions.isEmpty()) 0
                else (pageDefinitions.size + STAT_SECTION_COLUMNS.size - 1) /
                    STAT_SECTION_COLUMNS.size
            check(definitionRowsPerColumn <= STAT_DEFINITION_ROWS_PER_COLUMN) {
                "Custom-v1 Custom Statistics definitions exceed native split capacity."
            }
            val reclaimedDefinitionRows =
                STAT_DEFINITION_ROWS_PER_COLUMN - definitionRowsPerColumn
            val noteRowsPerColumn =
                STAT_NOTE_ROWS_PER_COLUMN + reclaimedDefinitionRows
            val noteCapacity = noteRowsPerColumn * STAT_SECTION_COLUMNS.size
            val pageNotes = noteLines
                .drop(noteOffset)
                .take(noteCapacity)

            val statsStreams = buildList {
                val moduleBefore = modules.size - moduleOffset
                if (moduleBefore > 0 && pageModules.isNotEmpty()) {
                    add(
                        PcSheetPaginationStreamTrace(
                            "custom-stat-modules",
                            PcSheetSemanticModule.CUSTOM_STATISTICS,
                            moduleBefore,
                            pageModules.size,
                            MODULES_PER_PAGE,
                            moduleBefore - pageModules.size,
                        ),
                    )
                }
                val definitionBefore = definitionLines.size - definitionOffset
                if (definitionBefore > 0 && pageDefinitions.isNotEmpty()) {
                    add(
                        PcSheetPaginationStreamTrace(
                            "custom-stat-definitions",
                            PcSheetSemanticModule.CUSTOM_STATISTICS,
                            definitionBefore,
                            pageDefinitions.size,
                            maxOf(pageDefinitions.size, definitionRowsPerColumn * STAT_SECTION_COLUMNS.size),
                            definitionBefore - pageDefinitions.size,
                        ),
                    )
                }
                val noteBefore = noteLines.size - noteOffset
                if (noteBefore > 0 && pageNotes.isNotEmpty()) {
                    add(
                        PcSheetPaginationStreamTrace(
                            "custom-stat-notes",
                            PcSheetSemanticModule.CUSTOM_STATISTICS,
                            noteBefore,
                            pageNotes.size,
                            noteCapacity,
                            noteBefore - pageNotes.size,
                        ),
                    )
                }
            }
            check(statsStreams.isNotEmpty()) {
                "Custom-v1 Custom Statistics compositor produced a page with no active stream."
            }
            recordPaginationTrace(
                V1_GLOBAL_STATS_FRONT_ID,
                pcSheetDirectCompositionTrace(
                    layoutId = "v1-custom-statistics-adaptive-native",
                    streams = statsStreams,
                    physical = PcSheetPhysicalPaginationTrace(
                        metric = "native-stat-stream-units",
                        used = statsStreams.sumOf { it.consumedUnits }.toDouble(),
                        capacity = statsStreams.sumOf { it.nativeCapacity }.toDouble(),
                        rationale = if (statsStreams.any { it.remainingAfter > 0 }) {
                            "remaining-custom-statistics-demand"
                        } else {
                            "front-exhausted"
                        },
                    ),
                ),
            )

            val notesShift = reclaimedDefinitionRows * STAT_SECTION_STEP
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderCustomStatisticsPage(
                page = page,
                modules = pageModules,
                definitions = pageDefinitions,
                notes = pageNotes,
                definitionRowsPerColumn = definitionRowsPerColumn,
                noteRowsPerColumn = noteRowsPerColumn,
                notesHeadingTop = NOTES_HEADING_TOP - notesShift,
                notesFirstRuleTop = NOTES_FIRST_RULE_TOP - notesShift,
                pageIndex = pageIndex,
            )

            moduleOffset += pageModules.size
            definitionOffset += pageDefinitions.size
            noteOffset += pageNotes.size
            pageIndex += 1
        }

        check(moduleOffset == modules.size) {
            "Custom-v1 Custom Statistics did not consume every attribute module."
        }
        check(definitionOffset == definitionLines.size) {
            "Custom-v1 Custom Statistics did not consume every definition line."
        }
        check(noteOffset == noteLines.size) {
            "Custom-v1 Custom Statistics did not consume every note line."
        }
    }

    private fun appendNarrativeAndTraitsExtendedPages(plan: PcSheetPdfRenderPlan) {
        val narrativeModules = narrativeContinuationModules(plan)
        val traitFlow = packV1TraitFlow(traitFlowBlocks(plan))

        if (narrativeModules.size != 1 || traitFlow.isEmpty()) {
            appendNarrativeExtendedPages(plan)
            appendTraitsExtendedPages(plan)
            return
        }

        val step = requireNotNull(
            PcSheetExtendedPageComposer.composeNextPage(
                demands = listOf(
                    PcSheetModuleDemand(
                        module = PcSheetSemanticModule.BACKGROUND_STORY,
                        remainingUnits = 1,
                    ),
                    PcSheetModuleDemand(
                        module = PcSheetSemanticModule.TRAITS,
                        remainingUnits = traitFlow.size,
                    ),
                ),
                layouts = listOf(customV1NarrativeTraitsLayout()),
            ),
        )
        recordPaginationTrace(V1_GLOBAL_NARRATIVE_TRAITS_FRONT_ID, step)
        val narrativeConsumed = step.page.placements
            .filter { it.module == PcSheetSemanticModule.BACKGROUND_STORY }
            .sumOf { it.consumedUnits }
        val traitConsumed = step.page.placements
            .filter { it.module == PcSheetSemanticModule.TRAITS }
            .sumOf { it.consumedUnits }

        check(narrativeConsumed == 1) {
            "Custom-v1 mixed Narrative/Traits layout must consume the single narrative module."
        }
        check(traitConsumed > 0) {
            "Custom-v1 mixed Narrative/Traits layout must reclaim the lower native Traits module."
        }

        val page = PDPage(PDRectangle(W, H))
        document.addPage(page)
        renderNarrativeContinuationPage(
            page = page,
            modules = narrativeModules,
            pageIndex = 0,
        )
        appendTraitModuleToExistingPage(
            page = page,
            lines = traitFlow.take(traitConsumed),
            targetTop = V1_TRAIT_TARGET_TOPS[1],
            prefix = "V1X NARRATIVE+TRAITS P1",
        )

        val remainingTraitFlow = traitFlow.drop(traitConsumed)
        if (remainingTraitFlow.isNotEmpty()) {
            appendTraitsExtendedPages(remainingTraitFlow)
        }
    }

    private fun customV1NarrativeTraitsLayout(): PcSheetExtendedLayoutTemplate =
        PcSheetExtendedLayoutTemplate(
            id = "v1-native-narrative-top-traits-bottom",
            slots = listOf(
                PcSheetExtendedLayoutSlot(
                    id = "v1-narrative-top",
                    capacityByModule = mapOf(PcSheetSemanticModule.BACKGROUND_STORY to 1),
                ),
                PcSheetExtendedLayoutSlot(
                    id = "v1-traits-bottom",
                    capacityByModule = mapOf(PcSheetSemanticModule.TRAITS to V1_TRAIT_ROWS_PER_MODULE),
                ),
            ),
        )

    private fun appendNarrativeExtendedPages(plan: PcSheetPdfRenderPlan) {
        val modules = narrativeContinuationModules(plan)
        if (modules.isEmpty()) return

        var demands = listOf(
            PcSheetModuleDemand(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                remainingUnits = modules.size,
            ),
        )
        var moduleOffset = 0
        var pageIndex = 0

        while (demands.isNotEmpty()) {
            val step = requireNotNull(
                PcSheetExtendedPageComposer.composeNextPage(
                    demands = demands,
                    layouts = listOf(customV1NarrativeLayout()),
                ),
            )
            recordPaginationTrace(V1_GLOBAL_NARRATIVE_TRAITS_FRONT_ID, step)
            val consumed = step.page.placements
                .filter { it.module == PcSheetSemanticModule.BACKGROUND_STORY }
                .sumOf { it.consumedUnits }
            val pageModules = modules.drop(moduleOffset).take(consumed)
            check(pageModules.isNotEmpty()) {
                "Custom-v1 narrative compositor produced an empty BACKGROUND_STORY page."
            }

            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderNarrativeContinuationPage(
                page = page,
                modules = pageModules,
                pageIndex = pageIndex,
            )

            moduleOffset += consumed
            demands = step.remainingDemands
            pageIndex += 1
        }

        check(moduleOffset == modules.size) {
            "Custom-v1 narrative compositor did not consume every native narrative module."
        }
    }

    private fun customV1NarrativeLayout(): PcSheetExtendedLayoutTemplate =
        PcSheetExtendedLayoutTemplate(
            id = V1_NARRATIVE_LAYOUT_ID,
            slots = (0 until V1_NARRATIVE_MODULES_PER_PAGE).map { index ->
                PcSheetExtendedLayoutSlot(
                    id = "v1-narrative-$index",
                    capacityByModule = mapOf(PcSheetSemanticModule.BACKGROUND_STORY to 1),
                )
            },
        )

    private fun narrativeContinuationModules(
        plan: PcSheetPdfRenderPlan,
    ): List<V1NarrativeModule> {
        val background = plan.snapshot.aggregate.sheet.background
        val modules = mutableListOf<V1NarrativeModule>()

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

        fun addOverflow(
            stableKey: String,
            heading: String,
            sectionName: String,
            text: String,
            baseWidth: Float,
            baseRows: Int,
        ) {
            val clean = text.trim()
            if (clean.isEmpty()) return
            val baseLines = wrapByWidth(
                clean,
                resources.fira,
                V1_NARRATIVE_BASE_SIZE,
                baseWidth,
            )
            if (baseLines.size <= baseRows) return

            val record = PcSheetSemanticRecordRef(
                module = PcSheetSemanticModule.BACKGROUND_STORY,
                stableKey = "background:$stableKey",
                displayName = sectionName,
            )
            var remaining = wrapByWidth(
                baseLines.drop((baseRows - 1).coerceAtLeast(0)).joinToString(" "),
                resources.fira,
                V1_NARRATIVE_BODY_SIZE,
                V1_NARRATIVE_TEXT_WIDTH,
            )
            var segmentIndex = 1

            while (remaining.isNotEmpty()) {
                val inbound = PcSheetBidirectionalContinuation(
                    record = record,
                    source = if (segmentIndex == 1) {
                        normalEndpoint(sectionName)
                    } else {
                        extendedEndpoint(sectionName, segmentIndex - 1)
                    },
                    target = extendedEndpoint(sectionName, segmentIndex),
                ).targetMarker()
                val capacityWithoutOutbound = V1_NARRATIVE_ROWS_PER_MODULE - 1
                val hasMore = remaining.size > capacityWithoutOutbound
                val bodyCapacity = capacityWithoutOutbound - if (hasMore) 1 else 0
                require(bodyCapacity > 0) {
                    "Custom-v1 native narrative module leaves no room for semantic content."
                }
                val body = remaining.take(bodyCapacity)
                remaining = remaining.drop(body.size)
                val lines = buildList {
                    add(inbound)
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
                modules += V1NarrativeModule(
                    heading = if (segmentIndex == 1) heading else "$heading · CONT.",
                    lines = lines,
                )
                segmentIndex += 1
            }
        }

        addOverflow(
            stableKey = "background",
            heading = "Trasfondo",
            sectionName = "TRASFONDO",
            text = background.name,
            baseWidth = V1_NARRATIVE_NARROW_BASE_WIDTH,
            baseRows = 6,
        )
        addOverflow(
            stableKey = "personality",
            heading = "Rasgos de Personalidad",
            sectionName = "PERSONALIDAD",
            text = background.personalityTraits,
            baseWidth = V1_NARRATIVE_NARROW_BASE_WIDTH,
            baseRows = 6,
        )
        addOverflow(
            stableKey = "ideals",
            heading = "Ideales",
            sectionName = "IDEALES",
            text = background.ideals,
            baseWidth = V1_NARRATIVE_NARROW_BASE_WIDTH,
            baseRows = 6,
        )
        addOverflow(
            stableKey = "bonds",
            heading = "Vínculos",
            sectionName = "VÍNCULOS",
            text = background.bonds,
            baseWidth = V1_NARRATIVE_NARROW_BASE_WIDTH,
            baseRows = 6,
        )
        addOverflow(
            stableKey = "flaws",
            heading = "Defectos",
            sectionName = "DEFECTOS",
            text = background.flaws,
            baseWidth = V1_NARRATIVE_NARROW_BASE_WIDTH,
            baseRows = 6,
        )
        addOverflow(
            stableKey = "story",
            heading = "Historia del Personaje",
            sectionName = "HISTORIA",
            text = background.story,
            baseWidth = V1_NARRATIVE_TEXT_WIDTH,
            baseRows = 4,
        )

        return modules
    }

    private fun renderNarrativeContinuationPage(
        page: PDPage,
        modules: List<V1NarrativeModule>,
        pageIndex: Int,
    ) {
        require(modules.size <= V1_NARRATIVE_MODULES_PER_PAGE) {
            "Custom-v1 narrative page exceeds native repeated-module capacity."
        }
        val prefix = "V1X NARRATIVE P${pageIndex + 1}"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            drawIsolatedSourceCrop(s, 2, 20f, 18f, 150f, 74f)
            modules.indices.forEach { index ->
                drawIsolatedSourceCrop(
                    s = s,
                    sourcePageIndex = 2,
                    sourceX = V1_NARRATIVE_SOURCE_X,
                    sourceTop = V1_NARRATIVE_SOURCE_TOP,
                    width = V1_NARRATIVE_MODULE_WIDTH,
                    height = V1_NARRATIVE_MODULE_HEIGHT,
                    targetX = V1_NARRATIVE_TARGET_X,
                    targetTop = V1_NARRATIVE_TARGET_TOPS[index],
                )
            }
        }

        appendLayer(page, "$prefix - CLEANUP") { s ->
            modules.indices.forEach { index ->
                headingInteriorMask(
                    s = s,
                    x = V1_NARRATIVE_TARGET_X,
                    top = V1_NARRATIVE_TARGET_TOPS[index],
                    width = V1_NARRATIVE_MODULE_WIDTH,
                    height = V1_NARRATIVE_HEADING_HEIGHT,
                )
            }
        }

        appendLayer(page, "$prefix - LABELS") { s ->
            modules.forEachIndexed { index, module ->
                centeredText(
                    s = s,
                    font = resources.heading,
                    x = V1_NARRATIVE_TARGET_X,
                    top = V1_NARRATIVE_TARGET_TOPS[index],
                    width = V1_NARRATIVE_MODULE_WIDTH,
                    height = V1_NARRATIVE_HEADING_HEIGHT,
                    value = module.heading,
                    size = V1_NARRATIVE_HEADING_SIZE,
                )
            }
        }

        appendLayer(page, "$prefix - VALUES") { s ->
            modules.forEachIndexed { moduleIndex, module ->
                val targetTop = V1_NARRATIVE_TARGET_TOPS[moduleIndex]
                module.lines.forEachIndexed { lineIndex, line ->
                    ruleText(
                        s = s,
                        font = resources.fira,
                        rule = Rule(
                            V1_NARRATIVE_TARGET_X,
                            V1_NARRATIVE_TARGET_X + V1_NARRATIVE_MODULE_WIDTH,
                            targetTop + V1_NARRATIVE_RULE_OFFSETS[lineIndex],
                        ),
                        value = line,
                        size = V1_NARRATIVE_BODY_SIZE,
                    )
                }
            }
        }

        appendLayer(page, "$prefix - MARKERS") { }
    }

    private fun appendTraitsExtendedPages(plan: PcSheetPdfRenderPlan) =
        appendTraitsExtendedPages(packV1TraitFlow(traitFlowBlocks(plan)))

    private fun appendTraitsExtendedPages(flow: List<V1TraitFlowLine>) {
        if (flow.isEmpty()) return

        var demands = listOf(
            PcSheetModuleDemand(
                module = PcSheetSemanticModule.TRAITS,
                remainingUnits = flow.size,
            ),
        )
        var lineOffset = 0
        var pageIndex = 0

        while (demands.isNotEmpty()) {
            val remaining = demands.single().remainingUnits
            val step = requireNotNull(
                PcSheetExtendedPageComposer.composeNextPage(
                    demands = demands,
                    layouts = v1TraitsCompositionLayouts(remaining),
                ),
            )
            recordPaginationTrace(V1_GLOBAL_NARRATIVE_TRAITS_FRONT_ID, step)
            val placementBySlot = step.page.placements.associateBy { it.slotId }

            var cursor = lineOffset
            val topCount = placementBySlot[V1_TRAIT_TOP_SLOT_ID]?.consumedUnits ?: 0
            val topLines = flow.drop(cursor).take(topCount)
            cursor += topCount

            val bottomCount = placementBySlot[V1_TRAIT_BOTTOM_SLOT_ID]?.consumedUnits ?: 0
            val bottomLines = flow.drop(cursor).take(bottomCount)
            cursor += bottomCount

            check(topLines.isNotEmpty()) {
                "Custom-v1 Traits compositor must occupy the top native module first."
            }

            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderTraitsPage(
                page = page,
                topLines = topLines,
                bottomLines = bottomLines,
                pageIndex = pageIndex,
            )

            lineOffset = cursor
            demands = step.remainingDemands
            pageIndex += 1
        }

        check(lineOffset == flow.size) {
            "Custom-v1 Traits compositor did not consume every packed native row."
        }
    }

    private fun v1TraitsCompositionLayouts(
        remainingUnits: Int,
    ): List<PcSheetExtendedLayoutTemplate> =
        if (remainingUnits <= V1_TRAIT_ROWS_PER_MODULE) {
            listOf(
                PcSheetExtendedLayoutTemplate(
                    id = V1_TRAIT_SINGLE_LAYOUT_ID,
                    slots = listOf(
                        PcSheetExtendedLayoutSlot(
                            id = V1_TRAIT_TOP_SLOT_ID,
                            capacityByModule = mapOf(
                                PcSheetSemanticModule.TRAITS to V1_TRAIT_ROWS_PER_MODULE,
                            ),
                        ),
                    ),
                ),
            )
        } else {
            listOf(
                PcSheetExtendedLayoutTemplate(
                    id = V1_TRAIT_DOUBLE_LAYOUT_ID,
                    slots = listOf(
                        PcSheetExtendedLayoutSlot(
                            id = V1_TRAIT_TOP_SLOT_ID,
                            capacityByModule = mapOf(
                                PcSheetSemanticModule.TRAITS to V1_TRAIT_ROWS_PER_MODULE,
                            ),
                        ),
                        PcSheetExtendedLayoutSlot(
                            id = V1_TRAIT_BOTTOM_SLOT_ID,
                            capacityByModule = mapOf(
                                PcSheetSemanticModule.TRAITS to V1_TRAIT_ROWS_PER_MODULE,
                            ),
                        ),
                    ),
                ),
            )
        }

    private fun renderTraitsPage(
        page: PDPage,
        topLines: List<V1TraitFlowLine>,
        bottomLines: List<V1TraitFlowLine>,
        pageIndex: Int,
    ) {
        val prefix = "V1X TRAITS P${pageIndex + 1}"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            drawIsolatedSourceCrop(
                s = s,
                sourcePageIndex = 2,
                sourceX = V1_TRAIT_SOURCE_X,
                sourceTop = V1_TRAIT_SOURCE_TOP,
                width = V1_TRAIT_MODULE_WIDTH,
                height = V1_TRAIT_MODULE_HEIGHT,
                targetX = V1_TRAIT_TARGET_X,
                targetTop = V1_TRAIT_TARGET_TOPS[0],
            )
            if (bottomLines.isNotEmpty()) {
                drawIsolatedSourceCrop(
                    s = s,
                    sourcePageIndex = 2,
                    sourceX = V1_TRAIT_SOURCE_X,
                    sourceTop = V1_TRAIT_SOURCE_TOP,
                    width = V1_TRAIT_MODULE_WIDTH,
                    height = V1_TRAIT_MODULE_HEIGHT,
                    targetX = V1_TRAIT_TARGET_X,
                    targetTop = V1_TRAIT_TARGET_TOPS[1],
                )
            }
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { }
        appendLayer(page, "$prefix - VALUES") { s ->
            drawV1TraitModuleLines(s, topLines, V1_TRAIT_TARGET_TOPS[0])
            if (bottomLines.isNotEmpty()) {
                drawV1TraitModuleLines(s, bottomLines, V1_TRAIT_TARGET_TOPS[1])
            }
        }
        appendLayer(page, "$prefix - MARKERS") { }
    }

    private fun appendTraitModuleToExistingPage(
        page: PDPage,
        lines: List<V1TraitFlowLine>,
        targetTop: Float,
        prefix: String,
    ) {
        require(lines.isNotEmpty()) {
            "Custom-v1 mixed page cannot append an empty Traits module."
        }
        require(lines.size <= V1_TRAIT_ROWS_PER_MODULE) {
            "Custom-v1 mixed page exceeds one native Traits module."
        }
        appendLayer(page, "$prefix - STRUCTURE") { s ->
            drawIsolatedSourceCrop(
                s = s,
                sourcePageIndex = 2,
                sourceX = V1_TRAIT_SOURCE_X,
                sourceTop = V1_TRAIT_SOURCE_TOP,
                width = V1_TRAIT_MODULE_WIDTH,
                height = V1_TRAIT_MODULE_HEIGHT,
                targetX = V1_TRAIT_TARGET_X,
                targetTop = targetTop,
            )
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { }
        appendLayer(page, "$prefix - VALUES") { s ->
            drawV1TraitModuleLines(s, lines, targetTop)
        }
        appendLayer(page, "$prefix - MARKERS") { }
    }

    private fun drawV1TraitModuleLines(
        s: PDFormContentStream,
        lines: List<V1TraitFlowLine>,
        targetTop: Float,
    ) {
        require(lines.size <= V1_TRAIT_ROWS_PER_MODULE) {
            "Custom-v1 native Traits module exceeds its physical row capacity."
        }
        lines.forEachIndexed { index, line ->
            if (line.kind == V1TraitFlowLineKind.BLANK || line.text.isBlank()) {
                return@forEachIndexed
            }
            val columnIndex = index / V1_TRAIT_ROWS_PER_COLUMN
            val rowIndex = index % V1_TRAIT_ROWS_PER_COLUMN
            val (relativeStartX, relativeEndX) = V1_TRAIT_COLUMN_RANGES[columnIndex]
            val rule = Rule(
                startX = V1_TRAIT_TARGET_X + relativeStartX,
                endX = V1_TRAIT_TARGET_X + relativeEndX,
                topY = targetTop + V1_TRAIT_RULE_OFFSETS[rowIndex],
            )
            when (line.kind) {
                V1TraitFlowLineKind.GROUP_HEADING ->
                    ruleText(s, resources.firaSemibold, rule, line.text, V1_TRAIT_GROUP_SIZE)

                V1TraitFlowLineKind.NAME ->
                    ruleText(s, resources.firaSemibold, rule, line.text, V1_TRAIT_NAME_SIZE)

                V1TraitFlowLineKind.DETAIL ->
                    ruleText(s, resources.fira, rule, line.text, V1_TRAIT_DETAIL_SIZE)

                V1TraitFlowLineKind.PROFICIENCY,
                V1TraitFlowLineKind.SUPPLEMENT,
                -> ruleText(s, resources.fira, rule, line.text, V1_TRAIT_META_SIZE)

                V1TraitFlowLineKind.BLANK -> Unit
            }
        }
    }

    private fun traitFlowBlocks(plan: PcSheetPdfRenderPlan): List<V1TraitFlowBlock> {
        val sheet = plan.snapshot.aggregate.sheet
        val orderedTraits = sheet.traits.sortedBy { it.sortOrder }
        val baseVisibleTraitIds = orderedTraits
            .take(BASE_V1_TRAIT_NAME_CAPACITY)
            .mapTo(mutableSetOf()) { it.id }
        val resourceNames = sheet.resources
            .map { it.name.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .toSet()
        val actionNames = sheet.combatEntries
            .filter { it.type != CharacterCombatEntryType.ATTACK }
            .map { it.name.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .toSet()

        fun traitBlock(
            trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
        ): V1TraitFlowBlock? {
            val normalizedName = trait.name.trim().lowercase()
            val hasDedicatedActionOrResource =
                normalizedName in resourceNames || normalizedName in actionNames
            val nameNeedsContinuation = trait.id !in baseVisibleTraitIds
            val writableTracker = trait.pcSheetWritableUsesTrackerOrNull()
            val hasContinuationMetadata =
                trait.notes?.trim()?.isNotEmpty() == true ||
                    writableTracker != null ||
                    trait.activation?.takeIf { it != CharacterActivationType.PASSIVE } != null
            val detailParts = if (hasDedicatedActionOrResource) {
                emptyList()
            } else {
                buildList {
                    trait.description.trim().takeIf { it.isNotEmpty() }?.let {
                        add(V1TraitFlowLineKind.DETAIL to it)
                    }
                    trait.source.trim().takeIf { it.isNotEmpty() }?.let {
                        if (nameNeedsContinuation || hasContinuationMetadata) {
                            add(V1TraitFlowLineKind.DETAIL to "Fuente: $it")
                        }
                    }
                    trait.notes?.trim()?.takeIf { it.isNotEmpty() }?.let {
                        add(V1TraitFlowLineKind.DETAIL to "Notas: $it")
                    }
                    writableTracker?.let { tracker ->
                        add(V1TraitFlowLineKind.DETAIL to ("Usos " + tracker.compactEditableLabel()))
                        trait.recovery?.trim()?.takeIf { it.isNotEmpty() }?.let {
                            add(V1TraitFlowLineKind.DETAIL to "Recuperación: $it")
                        }
                    }
                    trait.activation
                        ?.takeIf { it != CharacterActivationType.PASSIVE }
                        ?.let {
                            add(V1TraitFlowLineKind.DETAIL to "Activación: " + activationLabel(it))
                        }
                }.distinct()
            }

            if (!nameNeedsContinuation && detailParts.isEmpty()) return null

            val lines = mutableListOf<V1TraitFlowLine>()
            wrapByWidth(
                trait.name,
                resources.firaSemibold,
                V1_TRAIT_NAME_SIZE,
                V1_TRAIT_NATIVE_TEXT_WIDTH,
            ).forEach { line ->
                lines += V1TraitFlowLine(V1TraitFlowLineKind.NAME, line)
            }
            detailParts.forEach { (kind, value) ->
                wrapByWidth(
                    value,
                    resources.fira,
                    V1_TRAIT_DETAIL_SIZE,
                    V1_TRAIT_NATIVE_TEXT_WIDTH,
                ).forEach { line ->
                    lines += V1TraitFlowLine(kind, line)
                }
            }
            require(lines.size <= V1_TRAIT_ROWS_PER_MODULE) {
                "Custom-v1 trait record '${trait.name}' exceeds one native Traits module; " +
                    "explicit record continuation is required before owner handoff."
            }
            return V1TraitFlowBlock(lines)
        }

        val blocks = mutableListOf<V1TraitFlowBlock>()
        val categories = listOf(
            "CLASE" to setOf(CharacterTraitType.CLASS),
            "DOTES" to setOf(CharacterTraitType.FEAT),
            "RAZA" to setOf(CharacterTraitType.SPECIES_RACE),
            "TRASFONDO / DON / OTRO" to setOf(
                CharacterTraitType.BACKGROUND,
                CharacterTraitType.GIFT_BLESSING,
                CharacterTraitType.OTHER,
            ),
        )

        categories.forEach { (heading, types) ->
            val categoryBlocks = orderedTraits
                .filter { it.type in types }
                .mapNotNull(::traitBlock)
            if (categoryBlocks.isNotEmpty()) {
                blocks += categoryBlocks.first().copy(
                    lines = listOf(
                        V1TraitFlowLine(V1TraitFlowLineKind.GROUP_HEADING, heading),
                    ) + categoryBlocks.first().lines,
                )
                blocks += categoryBlocks.drop(1)
            }
        }

        fun appendReferenceGroup(
            heading: String,
            kind: V1TraitFlowLineKind,
            rawLines: List<String>,
        ) {
            val physical = rawLines.flatMap { value ->
                wrapByWidth(
                    value,
                    resources.fira,
                    V1_TRAIT_META_SIZE,
                    V1_TRAIT_NATIVE_TEXT_WIDTH,
                )
            }
            if (physical.isEmpty()) return
            blocks += V1TraitFlowBlock(
                lines = listOf(
                    V1TraitFlowLine(V1TraitFlowLineKind.GROUP_HEADING, heading),
                    V1TraitFlowLine(kind, physical.first()),
                ),
            )
            physical.drop(1).forEach { line ->
                blocks += V1TraitFlowBlock(
                    listOf(V1TraitFlowLine(kind, line)),
                )
            }
        }

        appendReferenceGroup(
            heading = "COMPETENCIAS",
            kind = V1TraitFlowLineKind.PROFICIENCY,
            rawLines = proficiencyLines(
                sheet.proficiencies.filter { it.type != CharacterProficiencyType.LANGUAGE },
            ),
        )
        appendReferenceGroup(
            heading = "IDIOMAS",
            kind = V1TraitFlowLineKind.PROFICIENCY,
            rawLines = proficiencyLines(
                sheet.proficiencies.filter { it.type == CharacterProficiencyType.LANGUAGE },
            ),
        )
        appendReferenceGroup(
            heading = "REFERENCIAS",
            kind = V1TraitFlowLineKind.SUPPLEMENT,
            rawLines = traitSupplementLines(plan),
        )

        return blocks
    }

    private fun packV1TraitFlow(
        blocks: List<V1TraitFlowBlock>,
    ): List<V1TraitFlowLine> {
        if (blocks.isEmpty()) return emptyList()

        val packed = mutableListOf<V1TraitFlowLine>()

        fun pad(count: Int) {
            repeat(count) {
                packed += V1TraitFlowLine(V1TraitFlowLineKind.BLANK, "")
            }
        }

        blocks.forEach { block ->
            if (block.lines.isEmpty()) return@forEach
            require(block.lines.size <= V1_TRAIT_ROWS_PER_MODULE) {
                "Custom-v1 Traits block exceeds one native module."
            }

            val moduleOffset = packed.size % V1_TRAIT_ROWS_PER_MODULE
            val columnOffset = moduleOffset % V1_TRAIT_ROWS_PER_COLUMN

            if (block.lines.size <= V1_TRAIT_ROWS_PER_COLUMN) {
                val rowsLeftInColumn = V1_TRAIT_ROWS_PER_COLUMN - columnOffset
                if (block.lines.size > rowsLeftInColumn) {
                    pad(rowsLeftInColumn)
                }
            } else if (moduleOffset != 0) {
                pad(V1_TRAIT_ROWS_PER_MODULE - moduleOffset)
            }

            packed += block.lines
        }

        return packed
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
        val rows = combatReferenceRows(plan).flatMap(::expandCombatReferenceRow)
        if (rows.isEmpty()) return

        val pages = pageCount(rows.size, COMBAT_ROWS_PER_PAGE)
        repeat(pages) { pageIndex ->
            val pageRows = rows.pageSlice(pageIndex, COMBAT_ROWS_PER_PAGE)
            val remainingBefore = rows.size - pageIndex * COMBAT_ROWS_PER_PAGE
            val remainingAfter = remainingBefore - pageRows.size
            recordPaginationTrace(
                V1_GLOBAL_COMBAT_FRONT_ID,
                pcSheetDirectCompositionTrace(
                    layoutId = "v1-combat-fixed-native-rows",
                    streams = listOf(
                        PcSheetPaginationStreamTrace(
                            "combat-physical-rows",
                            PcSheetSemanticModule.COMBAT_ACTIONS,
                            remainingBefore,
                            pageRows.size,
                            COMBAT_ROWS_PER_PAGE,
                            remainingAfter,
                        ),
                    ),
                    physical = PcSheetPhysicalPaginationTrace(
                        metric = "native-rows",
                        used = pageRows.size.toDouble(),
                        capacity = COMBAT_ROWS_PER_PAGE.toDouble(),
                        nextAtomicUnitSize = 1.0.takeIf { remainingAfter > 0 },
                        nextAtomicUnitFits = (pageRows.size < COMBAT_ROWS_PER_PAGE)
                            .takeIf { remainingAfter > 0 },
                        rationale = if (remainingAfter > 0) "native-row-capacity-exhausted" else "front-exhausted",
                    ),
                ),
            )
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderCombatPage(page, pageRows, pageIndex)
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
                    index >= BASE_V1_COMBAT_CAPACITY ||
                        entry.type != CharacterCombatEntryType.ATTACK ||
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

    private fun expandCombatReferenceRow(row: CombatReferenceRow): List<CombatReferenceRow> {
        val nameLines = combatCellLines(resources.fira, Rule(27.5f, 201f, 0f), row.name)
        val rangeLines = combatCellLines(resources.fira, Rule(207f, 281f, 0f), row.range)
        val bonusLines = combatCellLines(resources.firaSemibold, Rule(287f, 331f, 0f), row.bonus)
        val effectLines = combatCellLines(resources.fira, Rule(337f, 456f, 0f), row.effect)
        val noteLines = combatCellLines(resources.fira, Rule(462f, 583.795f, 0f), row.notes)
        val physicalRows = maxOf(
            1,
            nameLines.size,
            rangeLines.size,
            bonusLines.size,
            effectLines.size,
            noteLines.size,
        )

        return (0 until physicalRows).map { index ->
            CombatReferenceRow(
                name = nameLines.getOrElse(index) { "" },
                range = rangeLines.getOrElse(index) { "" },
                bonus = bonusLines.getOrElse(index) { "" },
                effect = effectLines.getOrElse(index) { "" },
                notes = noteLines.getOrElse(index) { "" },
            )
        }
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
            clean,
            font,
            MINIMUM_BODY_SIZE,
            maximumRawWidthAtReadableScale,
        )
    }

    private fun renderCombatPage(
        page: PDPage,
        rows: List<CombatReferenceRow>,
        pageIndex: Int,
    ) {
        val prefix = "V1X COMBAT P${pageIndex + 1}"
        appendLayer(page, "$prefix - STRUCTURE") { s ->
            drawIsolatedSourceCrop(s, 1, 20f, 18f, 150f, 74f)
            sourceBands(
                s,
                25f,
                585f,
                COMBAT_FIRST_RULE_TOP,
                COMBAT_ROWS_PER_PAGE,
                COMBAT_ROW_STEP,
            )
            listOf(205f, 285f, 335f, 460f).forEach { x ->
                verticalRule(s, x, 112f, COMBAT_FIRST_RULE_TOP + COMBAT_ROWS_PER_PAGE * COMBAT_ROW_STEP, 0.45f)
            }
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { s ->
            centeredText(s, resources.heading, 24f, 66f, 564f, 30f, "Combate / Acciones", 18f)
            centeredText(s, resources.fira, 27f, 96f, 176f, 14f, "TIPO / NOMBRE", 7.5f)
            centeredText(s, resources.fira, 207f, 96f, 76f, 14f, "RANGO", 7.5f)
            centeredText(s, resources.fira, 287f, 96f, 46f, 14f, "BONIF.", 7.5f)
            centeredText(s, resources.fira, 337f, 96f, 121f, 14f, "DAÑO / EFECTO", 7.5f)
            centeredText(s, resources.fira, 462f, 96f, 121f, 14f, "NOTAS", 7.5f)
        }
        appendLayer(page, "$prefix - VALUES") { s ->
            rows.forEachIndexed { index, row ->
                val top = COMBAT_FIRST_RULE_TOP + index * COMBAT_ROW_STEP
                combatCellText(s, resources.fira, Rule(27.5f, 201f, top), row.name, 8.0f)
                combatCellText(s, resources.fira, Rule(207f, 281f, top), row.range, 7.8f)
                combatCellText(s, resources.firaSemibold, Rule(287f, 331f, top), row.bonus, 8.0f)
                combatCellText(s, resources.fira, Rule(337f, 456f, top), row.effect, 7.8f)
                combatCellText(s, resources.fira, Rule(462f, 583.795f, top), row.notes, 7.6f)
            }
        }
        appendLayer(page, "$prefix - MARKERS") { }
    }

    private fun verticalRule(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        bottomTop: Float,
        width: Float,
    ) {
        s.saveGraphicsState()
        s.setLineWidth(width)
        s.moveTo(x, H - top)
        s.lineTo(x, H - bottomTop)
        s.stroke()
        s.restoreGraphicsState()
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
        val options = optionRenderLines(
            plan.snapshot.aggregate.sheet.classOptions.sortedBy { it.sortOrder },
        )
        var demands = buildList {
            if (rows.isNotEmpty()) {
                add(PcSheetModuleDemand(PcSheetSemanticModule.RESOURCES, rows.size))
            }
            if (options.isNotEmpty()) {
                add(PcSheetModuleDemand(PcSheetSemanticModule.CLASS_CHOICES, options.size))
            }
        }
        if (demands.isEmpty()) return

        var resourceOffset = 0
        var optionOffset = 0
        var pageIndex = 0

        while (demands.isNotEmpty()) {
            val layouts = v1ResourceCompositionLayouts(
                demands.mapTo(mutableSetOf()) { it.module },
            )
            val step = requireNotNull(
                PcSheetExtendedPageComposer.composeNextPage(
                    demands = demands,
                    layouts = layouts,
                ),
            )
            recordPaginationTrace(
                V1_GLOBAL_RESOURCES_FRONT_ID,
                step.traceWithNativeSlotUtilization(
                    layouts.single { it.id == step.page.layoutId },
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

            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderResourcesPage(
                page = page,
                rows = rows.drop(resourceOffset).take(resourceCount),
                options = options.drop(optionOffset).take(optionCount),
                pageIndex = pageIndex,
                layoutId = step.page.layoutId,
            )

            resourceOffset += resourceCount
            optionOffset += optionCount
            demands = step.remainingDemands
            pageIndex += 1
        }

        check(resourceOffset == rows.size) {
            "Custom-v1 compositor did not consume every Resource line."
        }
        check(optionOffset == options.size) {
            "Custom-v1 compositor did not consume every Class Choice line."
        }
    }

    private fun v1ResourceCompositionLayouts(
        activeModules: Set<PcSheetSemanticModule>,
    ): List<PcSheetExtendedLayoutTemplate> =
        when (activeModules) {
            setOf(PcSheetSemanticModule.RESOURCES, PcSheetSemanticModule.CLASS_CHOICES) ->
                listOf(
                    PcSheetExtendedLayoutTemplate(
                        id = RESOURCE_OPTIONS_SPLIT_LAYOUT_ID,
                        slots = listOf(
                            PcSheetExtendedLayoutSlot(
                                id = "resources",
                                capacityByModule = mapOf(
                                    PcSheetSemanticModule.RESOURCES to RESOURCE_ROWS_PER_PAGE,
                                ),
                            ),
                            PcSheetExtendedLayoutSlot(
                                id = "class-choices",
                                capacityByModule = mapOf(
                                    PcSheetSemanticModule.CLASS_CHOICES to OPTION_ROWS_PER_PAGE,
                                ),
                            ),
                        ),
                    ),
                )

            setOf(PcSheetSemanticModule.RESOURCES) ->
                listOf(
                    PcSheetExtendedLayoutTemplate(
                        id = RESOURCE_FULL_LAYOUT_ID,
                        slots = listOf(
                            PcSheetExtendedLayoutSlot(
                                id = "resources-full",
                                capacityByModule = mapOf(
                                    PcSheetSemanticModule.RESOURCES to RESOURCE_FULL_ROWS_PER_PAGE,
                                ),
                            ),
                        ),
                    ),
                )

            setOf(PcSheetSemanticModule.CLASS_CHOICES) ->
                listOf(
                    PcSheetExtendedLayoutTemplate(
                        id = OPTION_FULL_LAYOUT_ID,
                        slots = listOf(
                            PcSheetExtendedLayoutSlot(
                                id = "class-choices-full",
                                capacityByModule = mapOf(
                                    PcSheetSemanticModule.CLASS_CHOICES to OPTION_FULL_ROWS_PER_PAGE,
                                ),
                            ),
                        ),
                    ),
                )

            else -> error(
                "Unsupported Custom-v1 Resources/Options compositor demand: " +
                    activeModules.joinToString(),
            )
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
                    recoveryAndDetail = buildList {
                        val recoveryText = structuredRecovery
                            .takeIf { it.isNotEmpty() }
                            ?.joinToString(" · ")
                            ?: resource.recovery.orEmpty().trim()
                        recoveryText.takeIf { it.isNotEmpty() }?.let(::add)
                        resource.source.orEmpty().trim().takeIf { it.isNotEmpty() }?.let(::add)
                        resource.notes.orEmpty().trim().takeIf { it.isNotEmpty() }?.let(::add)
                    }.distinct().joinToString(" · "),
                    sortOrder = resource.sortOrder,
                    sourceRank = 0,
                )
            }

        val markers = aggregate.successor.customMarkers
            .sortedBy { it.sortOrder }
            .map { marker ->
                val maximum = when (marker.valueKind) {
                    CharacterTrackableValueKind.BINARY -> 1
                    CharacterTrackableValueKind.COUNTER,
                    CharacterTrackableValueKind.CURRENT_MAX -> marker.maxValue
                }
                val oneUse = maximum == 1
                ResourceRenderRow(
                    name = marker.name,
                    currentValue = marker.currentValue,
                    maximum = maximum,
                    recoveryAndDetail = buildList {
                        recoveryLabel(marker.recovery.cadence).takeIf { it.isNotEmpty() }?.let(::add)
                        if (!oneUse || marker.recovery.amountMode != CharacterRecoveryAmountMode.TO_MAX) {
                            recoveryAmountLabel(marker.recovery.amountMode, marker.recovery.fixedAmount)
                                .takeIf { it.isNotEmpty() }
                                ?.let(::add)
                        }
                        marker.notes.orEmpty().trim().takeIf { it.isNotEmpty() }?.let(::add)
                    }.joinToString(" · "),
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
                ResourceRenderLine(
                    name = nameLines.getOrNull(index).orEmpty(),
                    currentValue = row.currentValue.takeIf { firstLine },
                    maximum = row.maximum.takeIf { firstLine },
                    numericValue = resourceTrackerLabel(row.currentValue, row.maximum)
                        .takeIf { firstLine },
                    recoveryAndDetail = detailLines.getOrNull(index).orEmpty(),
                )
            }
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
        layoutId: String,
    ) {
        val prefix = "V1X RESOURCES P${pageIndex + 1}"
        val splitLayout = layoutId == RESOURCE_OPTIONS_SPLIT_LAYOUT_ID
        val resourcesFullLayout = layoutId == RESOURCE_FULL_LAYOUT_ID
        val optionsFullLayout = layoutId == OPTION_FULL_LAYOUT_ID
        require(splitLayout || resourcesFullLayout || optionsFullLayout) {
            "Unknown Custom-v1 Resources/Options layout: $layoutId"
        }
        require(!resourcesFullLayout || options.isEmpty()) {
            "Custom-v1 Resources-full layout cannot reserve Options."
        }
        require(!optionsFullLayout || rows.isEmpty()) {
            "Custom-v1 Options-full layout cannot reserve Resources."
        }

        val resourceCapacity =
            if (splitLayout) RESOURCE_ROWS_PER_PAGE else RESOURCE_FULL_ROWS_PER_PAGE
        val optionCapacity =
            if (splitLayout) OPTION_ROWS_PER_PAGE else OPTION_FULL_ROWS_PER_PAGE
        val optionHeadingTop =
            if (splitLayout) OPTION_HEADING_TOP else RESOURCE_HEADING_TOP
        val optionHeaderTop =
            if (splitLayout) OPTION_HEADER_TOP else RESOURCE_HEADER_TOP
        val optionFirstRuleTop =
            if (splitLayout) OPTION_FIRST_RULE_TOP else RESOURCE_FIRST_RULE_TOP

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            drawIsolatedSourceCrop(s, 1, 20f, 18f, 150f, 74f)
            if (rows.isNotEmpty()) {
                sourceBands(
                    s,
                    25f,
                    585f,
                    RESOURCE_FIRST_RULE_TOP,
                    resourceCapacity,
                    RESOURCE_STEP,
                )
            }
            if (options.isNotEmpty()) {
                sourceBands(
                    s,
                    25f,
                    585f,
                    optionFirstRuleTop,
                    optionCapacity,
                    OPTION_STEP,
                )
            }
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { s ->
            if (rows.isNotEmpty()) {
                centeredText(
                    s,
                    resources.heading,
                    24f,
                    RESOURCE_HEADING_TOP,
                    564f,
                    30f,
                    "Recursos",
                    18f,
                )
                centeredText(s, resources.fira, 27f, RESOURCE_HEADER_TOP, 175f, 14f, "RECURSO", 7.5f)
                centeredText(s, resources.fira, 205f, RESOURCE_HEADER_TOP, 180f, 14f, "USOS", 7.5f)
                centeredText(s, resources.fira, 400f, RESOURCE_HEADER_TOP, 183f, 14f, "RESTABLECE", 7.5f)
            }

            if (options.isNotEmpty()) {
                centeredText(
                    s,
                    resources.heading,
                    24f,
                    optionHeadingTop,
                    564f,
                    30f,
                    "Opciones",
                    18f,
                )
                centeredText(s, resources.fira, 35f, optionHeaderTop, 74f, 14f, "TIPO", 7.5f)
                centeredText(s, resources.fira, 126f, optionHeaderTop, 112f, 14f, "OPCIÓN", 7.5f)
                centeredText(
                    s,
                    resources.fira,
                    240.803f,
                    optionHeaderTop,
                    342.992f,
                    14f,
                    "DESCRIPCIÓN",
                    7.5f,
                )
            }
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
                val ruleTop = optionFirstRuleTop + index * OPTION_STEP
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
            // Resource capacity remains handwriting-editable. Runtime current/max is rendered
            // compactly in VALUES as ____(current)/max; do not consume the paper tracker marks.
            options.forEachIndexed { index, option ->
                option.active?.let { active ->
                    val ruleTop = optionFirstRuleTop + index * OPTION_STEP
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

    private fun inventoryExtendedModules(
        plan: PcSheetPdfRenderPlan,
    ): Set<PcSheetSemanticModule> {
        val aggregate = plan.snapshot.aggregate
        val usageByItem = aggregate.closure.inventoryUsage.associateBy { it.itemId }
        val ordered = aggregate.sheet.inventoryItems.sortedBy { it.sortOrder }

        val ordinaryActive = ordered
            .filterNot { it.special }
            .withIndex()
            .any { (index, item) -> ordinaryInventoryNeedsContinuation(index, item) }

        val specialActive = ordered
            .filter { it.special }
            .withIndex()
            .any { (index, item) ->
                specialInventoryNeedsContinuation(
                    index = index,
                    item = item,
                    usage = usageByItem[item.id],
                )
            }

        val treasureActive = treasureContinuationEntries(plan).isNotEmpty()

        return buildSet {
            if (ordinaryActive || treasureActive) {
                // Treasure continuation shares the family-native Inventory surface.
                add(PcSheetSemanticModule.ORDINARY_EQUIPMENT)
            }
            if (specialActive) {
                add(PcSheetSemanticModule.SPECIAL_EQUIPMENT)
            }
        }
    }

    private fun ordinaryInventoryNeedsContinuation(
        index: Int,
        item: CharacterInventoryItem,
    ): Boolean =
        index >= BASE_V1_EQUIPMENT_CAPACITY ||
            wrapByWidth(
                inventoryBaseLabel(item),
                resources.condensed,
                7.0f,
                INVENTORY_ORDINARY_TEXT_WIDTH,
            ).size > 1

    private fun specialInventoryNeedsContinuation(
        index: Int,
        item: CharacterInventoryItem,
        usage: CharacterInventoryUsage?,
    ): Boolean {
        val baseDetail = buildList {
            if (item.quantity != 1) add("Cant. " + item.quantity)
            item.weightLb?.let { add(formatInventoryWeight(it)) }
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

        return index >= BASE_V1_SPECIAL_CAPACITY ||
            usageMeaningful(usage) ||
            baseNameOverflows ||
            baseDetailOverflows ||
            specialLocationNeedsText(item.location)
    }

    private fun treasureContinuationEntries(
        plan: PcSheetPdfRenderPlan,
    ): List<TreasureEntry> {
        val aggregate = plan.snapshot.aggregate
        return buildList {
            aggregate.sheet.currencies
                .filter { !it.isDefault && it.standardCurrencyKindOrNull() == null }
                .sortedBy { it.sortOrder }
                .drop(BASE_V1_CUSTOM_CURRENCY_CAPACITY)
                .forEach { currency ->
                    add(TreasureEntry("${currency.name}: ${currency.amount}", null))
                }
            aggregate.successor.preferences.valuablesText
                .split(';')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .drop(BASE_V1_VALUABLE_CAPACITY)
                .map(::parseValuable)
                .forEach(::add)
        }
    }

    private fun appendInventoryExtendedPages(plan: PcSheetPdfRenderPlan) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val usageByItem = aggregate.closure.inventoryUsage.associateBy { it.itemId }
        val ordered = sheet.inventoryItems.sortedBy { it.sortOrder }
        val ordinary = ordered.filterNot { it.special }
        val ordinaryLines = ordinary.flatMapIndexed { index, item ->
            if (ordinaryInventoryNeedsContinuation(index, item)) {
                inventoryContinuationLines(item)
            } else {
                emptyList()
            }
        }

        val special = ordered.filter { it.special }
        val specialContinuation = special.mapIndexedNotNull { index, item ->
            item.takeIf {
                specialInventoryNeedsContinuation(
                    index = index,
                    item = item,
                    usage = usageByItem[item.id],
                )
            }
        }
        val specialRowGroups = specialContinuation.map { item ->
            specialInventoryFlowRows(item, usageByItem[item.id])
        }
        val specialPages = packSpecialInventoryRowGroups(specialRowGroups)

        val treasure = treasureContinuationEntries(plan)

        if (ordinaryLines.isEmpty() && specialPages.isEmpty() && treasure.isEmpty()) return

        val pages = maxOf(
            pageCount(ordinaryLines.size, INVENTORY_ORDINARY_CAPACITY),
            pageCount(treasure.size, INVENTORY_TREASURE_CAPACITY),
            specialPages.size,
        )
        repeat(pages) { pageIndex ->
            val pageOrdinary = ordinaryLines.pageSlice(pageIndex, INVENTORY_ORDINARY_CAPACITY)
            val pageTreasure = treasure.pageSlice(pageIndex, INVENTORY_TREASURE_CAPACITY)
            val pageSpecial = specialPages.getOrNull(pageIndex).orEmpty()
            val inventoryStreams = buildList {
                val ordinaryBefore = (ordinaryLines.size - pageIndex * INVENTORY_ORDINARY_CAPACITY).coerceAtLeast(0)
                if (ordinaryBefore > 0 && pageOrdinary.isNotEmpty()) add(
                    PcSheetPaginationStreamTrace(
                        "ordinary-equipment-lines",
                        PcSheetSemanticModule.ORDINARY_EQUIPMENT,
                        ordinaryBefore,
                        pageOrdinary.size,
                        INVENTORY_ORDINARY_CAPACITY,
                        ordinaryBefore - pageOrdinary.size,
                    ),
                )
                val treasureBefore = (treasure.size - pageIndex * INVENTORY_TREASURE_CAPACITY).coerceAtLeast(0)
                if (treasureBefore > 0 && pageTreasure.isNotEmpty()) add(
                    PcSheetPaginationStreamTrace(
                        "treasure-entries",
                        PcSheetSemanticModule.ORDINARY_EQUIPMENT,
                        treasureBefore,
                        pageTreasure.size,
                        INVENTORY_TREASURE_CAPACITY,
                        treasureBefore - pageTreasure.size,
                    ),
                )
                val specialBefore = (specialPages.size - pageIndex).coerceAtLeast(0)
                if (specialBefore > 0 && pageSpecial.isNotEmpty()) add(
                    PcSheetPaginationStreamTrace(
                        "special-equipment-modules",
                        PcSheetSemanticModule.SPECIAL_EQUIPMENT,
                        specialBefore,
                        1,
                        1,
                        specialBefore - 1,
                    ),
                )
            }
            recordPaginationTrace(
                V1_GLOBAL_INVENTORY_FRONT_ID,
                pcSheetDirectCompositionTrace(
                    layoutId = "v1-inventory-native-page",
                    streams = inventoryStreams,
                    physical = PcSheetPhysicalPaginationTrace(
                        metric = "native-stream-slots",
                        used = inventoryStreams.sumOf { it.consumedUnits }.toDouble(),
                        capacity = inventoryStreams.sumOf { it.nativeCapacity }.toDouble(),
                        rationale = if (inventoryStreams.any { it.remainingAfter > 0 }) {
                            "remaining-inventory-stream-demand"
                        } else {
                            "front-exhausted"
                        },
                    ),
                ),
            )
            val page = PDPage(PDRectangle(W, H))
            document.addPage(page)
            renderInventoryPage(page, pageOrdinary, pageTreasure, pageSpecial, pageIndex)
        }
    }

    private fun renderInventoryPage(
        page: PDPage,
        ordinary: List<String>,
        treasure: List<TreasureEntry>,
        special: List<SpecialInventoryFlowRow>,
        pageIndex: Int,
    ) {
        val prefix = "V1X INVENTORY P${pageIndex + 1}"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            s.drawForm(resources.forms[1])
        }
        appendLayer(page, "$prefix - CLEANUP") { s ->
            special.indices.forEach { rowIndex ->
                drawBlankSpecialLocationCell(s, rowIndex)
            }
        }
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

            special.forEachIndexed { rowIndex, row ->
                val y = INVENTORY_SPECIAL_RULES[rowIndex]
                if (row.location.isNotEmpty()) {
                    ruleText(
                        s,
                        resources.fira,
                        Rule(25f, 110f, y),
                        row.location,
                        8.0f,
                    )
                }
                if (row.name.isNotEmpty()) {
                    ruleText(
                        s,
                        resources.fira,
                        Rule(126f, 238f, y),
                        row.name,
                        8.2f,
                    )
                }
                if (row.detail.isNotEmpty()) {
                    ruleText(
                        s,
                        resources.fira,
                        Rule(240.803f, 583.795f, y),
                        row.detail,
                        8.0f,
                    )
                }
            }
        }
        appendLayer(page, "$prefix - MARKERS") { s ->
            special.forEachIndexed { rowIndex, row ->
                if (row.marker) {
                    // The imported v1 module already contains the empty checkbox. Overlay only
                    // the approved v8 mark at the exact source-box optical center.
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

    private fun inventoryBaseLabel(item: CharacterInventoryItem): String =
        item.pdfCompactEquipmentLabel()

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

        return lines
    }

    private fun inventoryContinuationLines(
        item: CharacterInventoryItem,
    ): List<String> {
        // Ordinary Equipment is an identity/list surface only. Preserve the complete compact
        // identity across native writing rows; operational state, weight and prose metadata do not
        // migrate into this module or into Notes.
        val wrapped = wrapByWidth(
            item.pdfCompactEquipmentLabel(),
            resources.condensed,
            8.4f,
            INVENTORY_ORDINARY_TEXT_WIDTH - INVENTORY_ORDINARY_CONTINUATION_INDENT_WIDTH,
        ).ifEmpty { listOf(item.pdfCompactEquipmentLabel()) }

        return wrapped.mapIndexed { index, line ->
            if (index == 0) line else INVENTORY_ORDINARY_CONTINUATION_PREFIX + line
        }
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

    private fun specialInventoryFlowRows(
        item: CharacterInventoryItem,
        usage: CharacterInventoryUsage?,
    ): List<SpecialInventoryFlowRow> {
        val locationLines = item.location
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                wrapByWidth(
                    it,
                    resources.fira,
                    8.0f,
                    INVENTORY_SPECIAL_LOCATION_TEXT_WIDTH,
                )
            }
            .orEmpty()

        val nameLines = wrapByWidth(
            inventoryContinuationLabel(item),
            resources.fira,
            8.2f,
            INVENTORY_SPECIAL_NAME_TEXT_WIDTH,
        ).ifEmpty { listOf(inventoryContinuationLabel(item)) }

        val detailLines = wrapByWidth(
            specialInventoryDetail(item, usage),
            resources.fira,
            8.0f,
            INVENTORY_SPECIAL_DETAIL_TEXT_WIDTH,
        )

        val rows = maxOf(1, locationLines.size, nameLines.size, detailLines.size)
        require(rows <= INVENTORY_SPECIAL_CAPACITY) {
            "Custom-v1 special Equipment record exceeds one fixed native module: " +
                inventoryContinuationLabel(item)
        }

        return (0 until rows).map { index ->
            SpecialInventoryFlowRow(
                location = locationLines.getOrNull(index).orEmpty(),
                name = nameLines.getOrNull(index).orEmpty().let { line ->
                    if (index == 0 || line.isEmpty()) line else SPECIAL_CONTINUATION_PREFIX + line
                },
                detail = detailLines.getOrNull(index).orEmpty(),
                marker = index == 0 && (item.equipped || item.attuned),
            )
        }
    }

    private fun packSpecialInventoryRowGroups(
        groups: List<List<SpecialInventoryFlowRow>>,
    ): List<List<SpecialInventoryFlowRow>> {
        val pages = mutableListOf<List<SpecialInventoryFlowRow>>()
        var current = mutableListOf<SpecialInventoryFlowRow>()

        groups.forEach { group ->
            require(group.size <= INVENTORY_SPECIAL_CAPACITY) {
                "Custom-v1 special Equipment record exceeds one fixed native module."
            }
            if (current.isNotEmpty() && current.size + group.size > INVENTORY_SPECIAL_CAPACITY) {
                pages += current.toList()
                current = mutableListOf()
            }
            current.addAll(group)
        }
        if (current.isNotEmpty()) pages += current.toList()
        return pages
    }

    private fun drawBlankSpecialLocationCell(
        s: PDFormContentStream,
        rowIndex: Int,
    ) {
        val donorRow = if (rowIndex % 2 == 0) {
            SPECIAL_BLANK_LOCATION_DONOR_EVEN
        } else {
            SPECIAL_BLANK_LOCATION_DONOR_ODD
        }
        val sourceTop = INVENTORY_SPECIAL_CHECK_TOPS[donorRow] - SPECIAL_LOCATION_CELL_TOP_PAD
        val targetTop = INVENTORY_SPECIAL_CHECK_TOPS[rowIndex] - SPECIAL_LOCATION_CELL_TOP_PAD
        drawIsolatedSourceCrop(
            s = s,
            sourcePageIndex = 1,
            sourceX = SPECIAL_LOCATION_CELL_X,
            sourceTop = sourceTop,
            width = SPECIAL_LOCATION_CELL_WIDTH,
            height = SPECIAL_LOCATION_CELL_HEIGHT,
            targetX = SPECIAL_LOCATION_CELL_X,
            targetTop = targetTop,
        )
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

    private fun spellContinuationPageCount(plan: PcSheetPdfRenderPlan): Int {
        val sheet = plan.snapshot.aggregate.sheet
        require(sheet.spells.all { it.level in 0..9 }) {
            "Custom-v1 spell continuation supports spell levels 0 through 9."
        }
        val byLevel = sheet.spells
            .groupBy { it.level }
            .mapValues { (_, entries) ->
                entries.sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.name.lowercase() })
            }
        return SPELL_CONTINUATION_BLOCKS.maxOf { block ->
            pageCount(
                byLevel[block.level].orEmpty().drop(block.baseCapacity).size,
                block.rules.size,
            )
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
        val pages = spellContinuationPageCount(plan)
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
        val packed = packedNotes(plan)
        if (packed.extendedPageCount == 0) return

        val extendedLineCounts = (1..packed.extendedPageCount).associateWith { pageIndex ->
            packed.columns.mapNotNull { segments ->
                val address = segments.firstOrNull()?.address ?: return@mapNotNull null
                if (address.extendedPageIndex != pageIndex) null else segments.flatMap { it.lines }
            }.sumOf { it.size }
        }
        var remainingExtendedLines = extendedLineCounts.values.sum()

        for (extendedPageIndex in 1..packed.extendedPageCount) {
            val columns = packed.columns
                .mapNotNull { segments ->
                    val address = segments.firstOrNull()?.address ?: return@mapNotNull null
                    if (address.extendedPageIndex != extendedPageIndex) null
                    else address.columnIndex to segments.flatMap { it.lines }
                }
                .toMap()
            val consumedLines = columns.values.sumOf { it.size }
            val remainingAfter = remainingExtendedLines - consumedLines
            val pageAdvance = packed.columnAdvances.firstOrNull { advance ->
                advance.from.extendedPageIndex == extendedPageIndex &&
                    advance.to.extendedPageIndex > extendedPageIndex
            }
            recordPaginationTrace(
                V1_GLOBAL_NOTES_FRONT_ID,
                pcSheetDirectCompositionTrace(
                    layoutId = "v1-notes-full-native-page",
                    streams = listOf(
                        PcSheetPaginationStreamTrace(
                            "notes-physical-lines",
                            PcSheetSemanticModule.NOTES,
                            remainingExtendedLines,
                            consumedLines,
                            NOTES_COLUMN_CAPACITY * 2,
                            remainingAfter,
                        ),
                    ),
                    physical = PcSheetPhysicalPaginationTrace(
                        metric = "native-note-rows",
                        used = consumedLines.toDouble(),
                        capacity = (NOTES_COLUMN_CAPACITY * 2).toDouble(),
                        nextAtomicUnitSize = pageAdvance?.atomicRowsRequired?.toDouble(),
                        nextAtomicUnitFits = pageAdvance?.atomicUnitFitsRemainder,
                        rationale = when {
                            remainingAfter <= 0 -> "front-exhausted"
                            pageAdvance != null ->
                                "note-page-advance-" + pageAdvance.reason.name.lowercase()
                            else -> "packed-note-columns-advance-without-recorded-boundary"
                        },
                    ),
                ),
            )
            remainingExtendedLines = remainingAfter
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
                    record.heading,
                    resources.firaSemibold,
                    NOTES_HEADING_SIZE,
                    narrowestWidth,
                ).ifEmpty { listOf(record.heading) },
                bodyLines = wrapByWidth(
                    record.body,
                    resources.fira,
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
        val prefix = "V1X NOTES P$pageIndex"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            // Notes is the full-page native exception. Reuse the complete source Notes page.
            s.drawForm(resources.forms[4])
        }
        appendLayer(page, "$prefix - CLEANUP") { }
        appendLayer(page, "$prefix - LABELS") { }
        appendLayer(page, "$prefix - VALUES") { s ->
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
        appendLayer(page, "$prefix - MARKERS") { }
    }

    private fun drawNotesColumn(
        s: PDFormContentStream,
        lines: List<PcSheetNotePhysicalLine>,
        startX: Float,
        endX: Float,
    ) {
        require(lines.size <= NOTES_COLUMN_CAPACITY) {
            "Packed Custom-v1 Extended Notes column exceeds native row capacity."
        }
        lines.forEachIndexed { index, line ->
            if (line.kind == PcSheetNotePhysicalLineKind.SEPARATOR || line.text.isBlank()) {
                return@forEachIndexed
            }
            val (font, preferredSize, minimumSize) = when (line.kind) {
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
                rule = Rule(startX, endX, NOTES_RULES[index]),
                value = line.text,
                preferredSize = preferredSize,
                minimumSize = minimumSize,
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
        var actual = preferredSize
        while (actual > minimumSize && textWidth(font, value, actual) > available) {
            actual -= 0.2f
        }
        require(textWidth(font, value, actual) <= available + 0.05f) {
            "Custom-v1 native Notes line does not fit without semantic truncation: '$value'"
        }
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, actual)
        s.newLineAtOffset(rule.startX + 1.5f, H - rule.topY + NOTES_BASELINE_CLEARANCE)
        s.showText(value)
        s.endText()
    }

    private fun narrativeNotesText(plan: PcSheetPdfRenderPlan): String {
        val sheet = plan.snapshot.aggregate.sheet
        return buildList {
            addAll(sheet.pdfCampaignNoteParagraphs())
            sheet.background.summary.trim().takeIf { it.isNotEmpty() }?.let {
                add("Resumen de trasfondo: $it")
            }
            sheet.background.religionFaith.trim().takeIf { it.isNotEmpty() }?.let {
                add("Fe / religión: $it")
            }
            sheet.classes.sortedBy { it.sortOrder }.forEach { classLevel ->
                classLevel.subclassName?.trim()?.takeIf { it.isNotEmpty() }?.let { subclass ->
                    add("Subclase: " + classLevel.name + " - " + subclass)
                }
            }
        }.joinToString(" ")
    }

    private fun notesText(plan: PcSheetPdfRenderPlan): String {
        val sheet = plan.snapshot.aggregate.sheet
        return buildList {
            val narrativeOverflow = wrapForRulesByChars(
                narrativeNotesText(plan),
                V1_NARRATIVE_NOTE_APPROX_CHARS,
            ).drop(BASE_V1_NARRATIVE_NOTE_CAPACITY).joinToString(" ")
            narrativeOverflow.takeIf { it.isNotBlank() }?.let(::add)
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
                .flatMap(::skillLines)
                .chunked(SKILLS_PER_MODULE)
                .ifEmpty { listOf(emptyList()) }
                .map { skills ->
                    ModuleSlice(
                        title = pcSheetIntegratedAttributeTitle(
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
                        skills = skills,
                    )
                }
        }

        val standardModules = CharacterAbility.entries.flatMap { ability ->
            val physicalSkillLines = stats.skills
                .filter { it.ability.builtIn == ability }
                .flatMap(::skillLines)
            physicalSkillLines.chunked(SKILLS_PER_MODULE).map { skills ->
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
                    skills = skills,
                )
            }
        }

        return customModules + standardModules
    }

    private fun skillLines(projection: PcSheetCustomSkillProjection): List<SkillLine> {
        val narrowestLabelWidth = COLUMNS.minOf { it.width - 39f }
        val rawWidthAtApprovedScale =
            narrowestLabelWidth / (SOURCE_LABEL_HORIZONTAL_SCALE / 100f)
        val wrapped = wrapByWidth(
            projection.skill.name,
            resources.fira,
            10f,
            rawWidthAtApprovedScale,
        ).ifEmpty { listOf(projection.skill.name) }

        return wrapped.mapIndexed { index, line ->
            SkillLine(
                name = line,
                total = projection.total?.let(::signed).orEmpty().takeIf { index == 0 }.orEmpty(),
                training = if (index == 0) training(projection.skill.training) else Training.NONE,
                marker = index == 0,
                horizontalScale = SOURCE_LABEL_HORIZONTAL_SCALE,
            )
        }
    }

    private fun renderCustomStatisticsPage(
        page: PDPage,
        modules: List<ModuleSlice>,
        definitions: List<String>,
        notes: List<String>,
        definitionRowsPerColumn: Int,
        noteRowsPerColumn: Int,
        notesHeadingTop: Float,
        notesFirstRuleTop: Float,
        pageIndex: Int,
    ) {
        val prefix = "V1X STATS P${pageIndex + 1}"

        appendLayer(page, "$prefix - STRUCTURE") { s ->
            drawIsolatedSourceCrop(s, 0, 20f, 18f, 170f, 74f)
            COLUMNS.take(modules.size).forEach { column ->
                drawIsolatedSourceCrop(
                    s = s,
                    sourcePageIndex = 0,
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
            if (definitions.isNotEmpty()) {
                STAT_SECTION_COLUMNS.forEach { (a, b) ->
                    sourceBands(
                        s, a, b,
                        DEFINITIONS_FIRST_RULE_TOP,
                        definitionRowsPerColumn,
                        STAT_SECTION_STEP,
                    )
                }
            }
            if (notes.isNotEmpty()) {
                STAT_SECTION_COLUMNS.forEach { (a, b) ->
                    sourceBands(
                        s, a, b,
                        notesFirstRuleTop,
                        noteRowsPerColumn,
                        STAT_SECTION_STEP,
                    )
                }
            }
        }

        appendLayer(page, "$prefix - CLEANUP") { s ->
            COLUMNS.take(modules.size).forEachIndexed { index, _ ->
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
            if (definitions.isNotEmpty()) {
                centeredText(
                    s, resources.heading,
                    24f, DEFINITIONS_HEADING_TOP, 564f, 26f,
                    "Definiciones", 17f,
                )
            }
            if (notes.isNotEmpty()) {
                centeredText(
                    s, resources.heading,
                    24f, notesHeadingTop, 564f, 26f,
                    "Notas de Estadísticas Personalizadas", 17f,
                )
            }
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
                            fixedHorizontalScale = skill.horizontalScale,
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

            drawBottomLines(
                s, definitions, DEFINITIONS_FIRST_RULE_TOP, definitionRowsPerColumn,
            )
            drawBottomLines(
                s, notes, notesFirstRuleTop, noteRowsPerColumn,
            )
        }

        appendLayer(page, "$prefix - MARKERS") { s ->
            COLUMNS.take(modules.size).forEachIndexed { index, column ->
                val module = modules[index]
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
                        module?.skills?.getOrNull(rowIndex)?.takeIf { it.marker }?.training
                            ?: Training.NONE,
                    )
                }
            }
        }
    }

    private fun drawBottomLines(
        s: PDFormContentStream,
        lines: List<String>,
        firstRuleTop: Float,
        rowsPerColumn: Int,
    ) {
        STAT_SECTION_COLUMNS.forEachIndexed { columnIndex, (a, b) ->
            lines
                .drop(columnIndex * rowsPerColumn)
                .take(rowsPerColumn)
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
        fixedHorizontalScale: Float? = null,
    ) {
        if (value.isBlank()) return
        val size = 10f
        val rawWidth = textWidth(font, value, size)
        val scale = fixedHorizontalScale ?: minOf(
            SOURCE_LABEL_HORIZONTAL_SCALE,
            maximumWidth / rawWidth * 100f,
        )
        require(scale >= MINIMUM_LABEL_HORIZONTAL_SCALE) {
            "Custom-v1 source-matched label requires excessive compression: '$value' ($scale%)"
        }
        require(rawWidth * scale / 100f <= maximumWidth + 0.05f) {
            "Custom-v1 wrapped source-matched label does not fit at uniform scale: '$value' ($scale%)"
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
        while (size > MINIMUM_BODY_SIZE && textWidth(font, value, size) > available) {
            size -= 0.2f
        }
        val rawWidth = textWidth(font, value, size)
        val horizontalScale = if (rawWidth <= available) {
            100f
        } else {
            (available / rawWidth * 100f).coerceAtMost(100f)
        }
        require(horizontalScale >= COMBAT_MINIMUM_HORIZONTAL_SCALE) {
            "Custom-v1 combat cell requires excessive compression: '$value' ($horizontalScale%)"
        }
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, size)
        s.setHorizontalScaling(horizontalScale)
        s.newLineAtOffset(rule.startX + leftPadding, H - rule.topY + 3.2f)
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

    private fun drawIsolatedSourceCrop(
        s: PDFormContentStream,
        sourcePageIndex: Int,
        sourceX: Float,
        sourceTop: Float,
        width: Float,
        height: Float,
        targetX: Float = sourceX,
        targetTop: Float = sourceTop,
    ) {
        val key = SourceCropKey(
            sourcePageIndex = sourcePageIndex,
            sourceX = sourceX,
            sourceTop = sourceTop,
            width = width,
            height = height,
        )
        val image = isolatedSourceCropCache.getOrPut(key) {
            val scale = SOURCE_FRAGMENT_DPI / 72f
            val sourceImage = PDFRenderer(sourceTemplate).renderImageWithDPI(
                sourcePageIndex,
                SOURCE_FRAGMENT_DPI,
                ImageType.RGB,
            )
            val sourcePixelX = (sourceX * scale).roundToInt()
            val sourcePixelY = (sourceTop * scale).roundToInt()
            val pixelWidth = (width * scale).roundToInt().coerceAtLeast(1)
            val pixelHeight = (height * scale).roundToInt().coerceAtLeast(1)
            require(
                sourcePixelX >= 0 &&
                    sourcePixelY >= 0 &&
                    sourcePixelX + pixelWidth <= sourceImage.width &&
                    sourcePixelY + pixelHeight <= sourceImage.height
            ) {
                "Custom-v1 isolated source crop exceeds source page bounds."
            }

            val transparent = newArgbImage(pixelWidth, pixelHeight)
            repeat(pixelHeight) { y ->
                repeat(pixelWidth) { x ->
                    transparent.setRGB(
                        x,
                        y,
                        sourceImage.getRGB(sourcePixelX + x, sourcePixelY + y),
                    )
                }
            }
            LosslessFactory.createFromImage(document, transparent)
        }
        s.drawImage(
            image,
            targetX,
            H - targetTop - height,
            width,
            height,
        )
    }

    private fun newArgbImage(width: Int, height: Int) =
        BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

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

    private data class CombatReferenceRow(
        val name: String,
        val range: String,
        val bonus: String,
        val effect: String,
        val notes: String,
    )

    private data class SpecialInventoryFlowRow(
        val location: String,
        val name: String,
        val detail: String,
        val marker: Boolean,
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

    private data class SourceCropKey(
        val sourcePageIndex: Int,
        val sourceX: Float,
        val sourceTop: Float,
        val width: Float,
        val height: Float,
    )

    private data class V1NarrativeModule(
        val heading: String,
        val lines: List<String>,
    )

    private enum class V1TraitFlowLineKind {
        GROUP_HEADING,
        NAME,
        DETAIL,
        PROFICIENCY,
        SUPPLEMENT,
        BLANK,
    }

    private data class V1TraitFlowLine(
        val kind: V1TraitFlowLineKind,
        val text: String,
    )

    private data class V1TraitFlowBlock(
        val lines: List<V1TraitFlowLine>,
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
        val marker: Boolean,
        val horizontalScale: Float,
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

        const val SOURCE_FRAGMENT_DPI = 288f
        const val MODULES_PER_PAGE = 6
        const val SKILLS_PER_MODULE = 5
        const val DEFINITION_LINES_PER_PAGE = 15
        const val NOTE_LINES_PER_PAGE = 27
        const val BOTTOM_TEXT_WIDTH = 150f

        // Source page 3 provides three compatible full-width right-hand slots:
        // Otros Rasgos (top), Historia del Personaje (middle), and Notas (lower).
        // Reuse the fixed Historia module at those source-derived positions without resizing.
        const val V1_GLOBAL_STATS_FRONT_ID = "v1-global-custom-statistics"
        const val V1_GLOBAL_NARRATIVE_TRAITS_FRONT_ID = "v1-global-narrative-traits"
        const val V1_GLOBAL_COMBAT_FRONT_ID = "v1-global-combat"
        const val V1_GLOBAL_RESOURCES_FRONT_ID = "v1-global-resources-options"
        const val V1_GLOBAL_INVENTORY_FRONT_ID = "v1-global-inventory"
        const val V1_GLOBAL_SPELLS_FRONT_ID = "v1-global-spells"
        const val V1_GLOBAL_NOTES_FRONT_ID = "v1-global-notes"
        const val V1_NARRATIVE_LAYOUT_ID = "v1-native-narrative-modules"
        const val V1_NARRATIVE_MODULES_PER_PAGE = 3
        const val V1_NARRATIVE_ROWS_PER_MODULE = 4
        const val V1_NARRATIVE_SOURCE_X = 215.291f
        const val V1_NARRATIVE_SOURCE_TOP = 344f
        const val V1_NARRATIVE_MODULE_WIDTH = 368.504f
        const val V1_NARRATIVE_MODULE_HEIGHT = 124f
        const val V1_NARRATIVE_HEADING_HEIGHT = 35f
        const val V1_NARRATIVE_TARGET_X = 215.291f
        const val V1_NARRATIVE_TEXT_WIDTH = 365f
        const val V1_NARRATIVE_NARROW_BASE_WIDTH = 153f
        const val V1_NARRATIVE_BASE_SIZE = 9.25f
        const val V1_NARRATIVE_BODY_SIZE = 8.5f
        const val V1_NARRATIVE_HEADING_SIZE = 16f
        val V1_NARRATIVE_TARGET_TOPS = listOf(66f, 344f, 566f)
        val V1_NARRATIVE_RULE_OFFSETS = listOf(43.996f, 63.839f, 83.681f, 103.524f)

        // Custom-v1 base renders six names on page 1 and another 24 in the native
        // "Otros Rasgos y Atributos" module on page 3.
        const val BASE_V1_TRAIT_NAME_CAPACITY = 30

        // Native page-3 "Otros Rasgos y Atributos" module: two columns x 12 ruled rows.
        // Repeat/copy the exact module without resizing; coordinates may translate on blank
        // Extended pages while preserving its source geometry and paper rhythm.
        const val V1_TRAIT_SINGLE_LAYOUT_ID = "v1-native-traits-single"
        const val V1_TRAIT_DOUBLE_LAYOUT_ID = "v1-native-traits-double"
        const val V1_TRAIT_TOP_SLOT_ID = "v1-traits-top"
        const val V1_TRAIT_BOTTOM_SLOT_ID = "v1-traits-bottom"
        const val V1_TRAIT_SOURCE_X = 215.291f
        const val V1_TRAIT_SOURCE_TOP = 66f
        const val V1_TRAIT_MODULE_WIDTH = 368.504f
        const val V1_TRAIT_MODULE_HEIGHT = 278f
        const val V1_TRAIT_TARGET_X = 121.748f
        const val V1_TRAIT_ROWS_PER_COLUMN = 12
        const val V1_TRAIT_COLUMNS_PER_MODULE = 2
        const val V1_TRAIT_ROWS_PER_MODULE =
            V1_TRAIT_ROWS_PER_COLUMN * V1_TRAIT_COLUMNS_PER_MODULE
        const val V1_TRAIT_NATIVE_TEXT_WIDTH = 177f
        const val V1_TRAIT_GROUP_SIZE = 7.7f
        const val V1_TRAIT_NAME_SIZE = 8.2f
        const val V1_TRAIT_DETAIL_SIZE = 7.5f
        const val V1_TRAIT_META_SIZE = 7.3f
        val V1_TRAIT_TARGET_TOPS = listOf(66f, 390f)
        val V1_TRAIT_RULE_OFFSETS = listOf(
            43.5f, 63.5f, 83.5f, 103f, 123f, 143f,
            163f, 182.5f, 202.5f, 222.5f, 242f, 262f,
        )
        val V1_TRAIT_COLUMN_RANGES = listOf(
            0f to 181.417f,
            187.087f to 368.504f,
        )

        const val TRAIT_LEFT_ROWS = 3
        const val TRAIT_OTHER_CAPACITY = 12
        const val TRAIT_DETAIL_ROWS = 4
        const val TRAIT_NOTE_ROWS = 5
        const val TRAIT_RIGHT_CAPACITY = TRAIT_DETAIL_ROWS + TRAIT_NOTE_ROWS
        const val TRAIT_LEFT_TEXT_WIDTH = 154f
        const val TRAIT_RIGHT_TEXT_WIDTH = 365f

        const val RESOURCE_HEADING_TOP = 66f
        const val RESOURCE_HEADER_TOP = 96f
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
        // Run-6 establishes one 20 pt native row grammar across both Resource and Option
        // sections, with the lower split section ending at rule y=726.5. A single surviving
        // stream may therefore reclaim the page using 30 whole native rows from y=128.5 to
        // y=708.5 without crossing the approved lower content boundary.
        const val RESOURCE_FULL_ROWS_PER_PAGE = 30
        const val OPTION_FULL_ROWS_PER_PAGE = 30
        const val RESOURCE_OPTIONS_SPLIT_LAYOUT_ID = "v1-resources-options-split"
        const val RESOURCE_FULL_LAYOUT_ID = "v1-resources-full"
        const val OPTION_FULL_LAYOUT_ID = "v1-options-full"
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

        const val V1_NARRATIVE_NOTE_APPROX_CHARS = 48
        const val NOTES_HEADING_SIZE = 8.8f
        const val NOTES_HEADING_MINIMUM_SIZE = 6.4f
        const val NOTES_BODY_SIZE = 8.4f
        const val NOTES_BODY_MINIMUM_SIZE = 6.2f
        const val NOTES_CONTINUITY_SIZE = 7.2f
        const val NOTES_CONTINUITY_MINIMUM_SIZE = 5.8f
        const val NOTES_HORIZONTAL_PADDING = 4f
        const val NOTES_BASELINE_CLEARANCE = 3.2f
        const val NOTES_LEFT_START_X = 25f
        const val NOTES_LEFT_END_X = 267.5f
        const val NOTES_RIGHT_START_X = 311.669f
        const val NOTES_RIGHT_END_X = 583.795f
        const val BASE_V1_NARRATIVE_NOTE_CAPACITY = 9
        const val BASE_V1_NOTES_WRAP_CHARS = 68
        const val NOTES_COLUMN_CAPACITY = 17
        const val BASE_V1_NOTES_CAPACITY = NOTES_COLUMN_CAPACITY * 2
        const val NOTES_CONTINUATION_CAPACITY = NOTES_COLUMN_CAPACITY * 2
        val NOTES_RULES = listOf(
            109.5f, 129.5f, 149.5f, 169f, 189f, 209f, 229f, 248.5f, 268.5f,
            288.5f, 308f, 328f, 348f, 367.5f, 387.5f, 407.5f, 427f,
        )

        const val BASE_V1_COMBAT_CAPACITY = 5
        const val COMBAT_ROWS_PER_PAGE = 14
        const val COMBAT_FIRST_RULE_TOP = 128f
        const val COMBAT_ROW_STEP = 42f
        const val COMBAT_TEXT_WIDTH = 556f
        const val BASE_V1_EQUIPMENT_CAPACITY = 54
        const val BASE_V1_SPECIAL_CAPACITY = 12
        const val BASE_V1_VALUABLE_CAPACITY = 4
        const val BASE_V1_CUSTOM_CURRENCY_CAPACITY = 2
        const val INVENTORY_ORDINARY_CAPACITY = 54
        const val INVENTORY_TREASURE_CAPACITY = 4
        const val INVENTORY_SPECIAL_CAPACITY = 13
        const val INVENTORY_ORDINARY_TEXT_WIDTH = 106f
        const val INVENTORY_ORDINARY_CONTINUATION_INDENT_WIDTH = 10f
        const val INVENTORY_ORDINARY_CONTINUATION_PREFIX = "  "
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
        const val INVENTORY_SPECIAL_LOCATION_TEXT_WIDTH = 82f
        const val INVENTORY_SPECIAL_NAME_TEXT_WIDTH = 108f
        const val INVENTORY_SPECIAL_DETAIL_TEXT_WIDTH = 338f
        const val SPECIAL_CONTINUATION_PREFIX = "  "
        const val SPECIAL_BLANK_LOCATION_DONOR_EVEN = 10
        const val SPECIAL_BLANK_LOCATION_DONOR_ODD = 11
        const val SPECIAL_LOCATION_CELL_X = 25f
        const val SPECIAL_LOCATION_CELL_WIDTH = 85f
        const val SPECIAL_LOCATION_CELL_TOP_PAD = 1f
        const val SPECIAL_LOCATION_CELL_HEIGHT = 13.5f
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
        const val STAT_DEFINITION_ROWS_PER_COLUMN = 5
        // The approved page leaves native writing space below the old five-row Notes band.
        // Continue the same 22 pt ruled rhythm through y=711 without crossing the footer zone.
        const val STAT_NOTE_ROWS_PER_COLUMN = 9
        const val STAT_SECTION_STEP = 22f

        const val SOURCE_LABEL_HORIZONTAL_SCALE = 60f
        const val MINIMUM_LABEL_HORIZONTAL_SCALE = 50f
        const val SOURCE_LABEL_BASELINE_OFFSET = 3.0f
        const val MINIMUM_BODY_SIZE = 5.8f
        const val COMBAT_MINIMUM_HORIZONTAL_SCALE = 55f

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
