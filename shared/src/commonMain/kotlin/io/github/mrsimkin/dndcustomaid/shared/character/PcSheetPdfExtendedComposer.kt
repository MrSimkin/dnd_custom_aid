package io.github.mrsimkin.dndcustomaid.shared.character

data class PcSheetCompositionScoreTrace(
    val totalUtilization: Double,
    val filledSlots: Int,
    val distinctModules: Int,
)

data class PcSheetPaginationStreamTrace(
    val streamId: String,
    val module: PcSheetSemanticModule,
    val remainingBefore: Int,
    val consumedUnits: Int,
    val nativeCapacity: Int,
    val remainingAfter: Int,
) {
    init {
        require(streamId.isNotBlank()) { "Pagination stream id is required." }
        require(remainingBefore > 0) { "Active pagination stream must begin with positive demand." }
        require(consumedUnits > 0) { "Pagination stream must consume at least one unit." }
        require(nativeCapacity > 0) { "Pagination stream native capacity must be positive." }
        require(consumedUnits <= nativeCapacity) {
            "Pagination stream cannot consume more than its native capacity."
        }
        require(remainingAfter == remainingBefore - consumedUnits) {
            "Pagination stream remaining demand must match the consumed units."
        }
    }
}

data class PcSheetPhysicalPaginationTrace(
    val metric: String,
    val used: Double,
    val capacity: Double,
    val nextAtomicUnitSize: Double? = null,
    val nextAtomicUnitFits: Boolean? = null,
    val rationale: String,
) {
    init {
        require(metric.isNotBlank()) { "Physical pagination metric is required." }
        require(used >= 0.0) { "Physical pagination used capacity cannot be negative." }
        require(capacity > 0.0) { "Physical pagination capacity must be positive." }
        require(rationale.isNotBlank()) { "Physical pagination rationale is required." }
    }
}

data class PcSheetExtendedCompositionTrace(
    val candidateLayoutIds: List<String>,
    val eligibleLayoutIds: List<String>,
    val demandsBefore: List<PcSheetModuleDemand>,
    val chosenLayoutId: String,
    val placements: List<PcSheetExtendedPlacement>,
    val demandsAfter: List<PcSheetModuleDemand>,
    val score: PcSheetCompositionScoreTrace,
    val streamTraces: List<PcSheetPaginationStreamTrace> = emptyList(),
    val physical: PcSheetPhysicalPaginationTrace? = null,
    val eligibleNativeAreaScores: Map<String, Double> = emptyMap(),
)

data class PcSheetPaginationTraceEntry(
    val family: PcSheetVisualFamily,
    val frontId: String,
    val decisionOrdinal: Int,
    val composition: PcSheetExtendedCompositionTrace,
)

fun PcSheetPaginationTraceEntry.toStableJsonLine(): String = buildString {
    fun quoted(value: String): String = buildString {
        append('"')
        value.forEach { ch ->
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(ch)
            }
        }
        append('"')
    }

    fun appendDemands(values: List<PcSheetModuleDemand>) {
        append('[')
        values.forEachIndexed { index, demand ->
            if (index > 0) append(',')
            append("{\"module\":")
            append(quoted(demand.module.name))
            append(",\"remainingUnits\":")
            append(demand.remainingUnits)
            append(",\"atomicUnitSizes\":[")
            demand.atomicUnitSizes.forEachIndexed { atomIndex, rows ->
                if (atomIndex > 0) append(',')
                append(rows)
            }
            append("]}")

        }
        append(']')
    }

    fun appendPlacements(values: List<PcSheetExtendedPlacement>) {
        append('[')
        values.forEachIndexed { index, placement ->
            if (index > 0) append(',')
            append("{\"slotId\":")
            append(quoted(placement.slotId))
            append(",\"module\":")
            append(quoted(placement.module.name))
            append(",\"consumedUnits\":")
            append(placement.consumedUnits)
            append(",\"nativeCapacity\":")
            append(placement.nativeCapacity)
            append('}')
        }
        append(']')
    }

    fun appendStreams(values: List<PcSheetPaginationStreamTrace>) {
        append('[')
        values.forEachIndexed { index, stream ->
            if (index > 0) append(',')
            append("{\"streamId\":")
            append(quoted(stream.streamId))
            append(",\"module\":")
            append(quoted(stream.module.name))
            append(",\"remainingBefore\":")
            append(stream.remainingBefore)
            append(",\"consumedUnits\":")
            append(stream.consumedUnits)
            append(",\"nativeCapacity\":")
            append(stream.nativeCapacity)
            append(",\"remainingAfter\":")
            append(stream.remainingAfter)
            append('}')
        }
        append(']')
    }

    append("{\"family\":")
    append(quoted(family.name))
    append(",\"frontId\":")
    append(quoted(frontId))
    append(",\"decisionOrdinal\":")
    append(decisionOrdinal)
    append(",\"candidateLayoutIds\":[")
    composition.candidateLayoutIds.forEachIndexed { index, id ->
        if (index > 0) append(',')
        append(quoted(id))
    }
    append("],\"eligibleLayoutIds\":[")
    composition.eligibleLayoutIds.forEachIndexed { index, id ->
        if (index > 0) append(',')
        append(quoted(id))
    }
    append("],\"eligibleNativeAreaScores\":{")
    composition.eligibleNativeAreaScores.entries.forEachIndexed { index, (id, area) ->
        if (index > 0) append(',')
        append(quoted(id))
        append(':')
        append(area)
    }
    append('}')
    append(",\"demandsBefore\":")
    appendDemands(composition.demandsBefore)
    append(",\"chosenLayoutId\":")
    append(quoted(composition.chosenLayoutId))
    append(",\"placements\":")
    appendPlacements(composition.placements)
    append(",\"demandsAfter\":")
    appendDemands(composition.demandsAfter)
    append(",\"score\":{\"totalUtilization\":")
    append(composition.score.totalUtilization)
    append(",\"filledSlots\":")
    append(composition.score.filledSlots)
    append(",\"distinctModules\":")
    append(composition.score.distinctModules)
    append("}")
    append(",\"streamTraces\":")
    appendStreams(composition.streamTraces)
    append(",\"physical\":")
    val physical = composition.physical
    if (physical == null) {
        append("null")
    } else {
        append("{\"metric\":")
        append(quoted(physical.metric))
        append(",\"used\":")
        append(physical.used)
        append(",\"capacity\":")
        append(physical.capacity)
        append(",\"nextAtomicUnitSize\":")
        physical.nextAtomicUnitSize?.let(::append) ?: append("null")
        append(",\"nextAtomicUnitFits\":")
        physical.nextAtomicUnitFits?.let(::append) ?: append("null")
        append(",\"rationale\":")
        append(quoted(physical.rationale))
        append('}')
    }
    append('}')
}

fun pcSheetDirectCompositionTrace(
    layoutId: String,
    streams: List<PcSheetPaginationStreamTrace>,
    physical: PcSheetPhysicalPaginationTrace? = null,
): PcSheetExtendedCompositionTrace {
    require(layoutId.isNotBlank()) { "Direct pagination layout id is required." }
    require(streams.isNotEmpty()) { "Direct pagination trace requires at least one active stream." }

    fun groupedDemands(after: Boolean): List<PcSheetModuleDemand> =
        streams
            .groupBy { it.module }
            .mapNotNull { (module, values) ->
                val remaining = values.sumOf {
                    if (after) it.remainingAfter else it.remainingBefore
                }
                remaining.takeIf { it > 0 }?.let { PcSheetModuleDemand(module, it) }
            }
            .sortedBy { it.module.name }

    val placements = streams.map { stream ->
        PcSheetExtendedPlacement(
            slotId = stream.streamId,
            module = stream.module,
            consumedUnits = stream.consumedUnits,
            nativeCapacity = stream.nativeCapacity,
        )
    }

    return PcSheetExtendedCompositionTrace(
        candidateLayoutIds = listOf(layoutId),
        eligibleLayoutIds = listOf(layoutId),
        demandsBefore = groupedDemands(after = false),
        chosenLayoutId = layoutId,
        placements = placements,
        demandsAfter = groupedDemands(after = true),
        score = PcSheetCompositionScoreTrace(
            totalUtilization = placements.sumOf { it.utilization },
            filledSlots = placements.size,
            distinctModules = placements.map { it.module }.distinct().size,
        ),
        streamTraces = streams,
        physical = physical,
    )
}

fun PcSheetExtendedCompositionTrace.streamsWithUnusedCapacityAndRemainingDemand(): Set<String> =
    streamTraces
        .filter { stream ->
            stream.remainingAfter > 0 &&
                stream.consumedUnits < stream.nativeCapacity
        }
        .mapTo(mutableSetOf()) { it.streamId }

fun PcSheetExtendedCompositionTrace.exhaustedStreamsWithUnusedCapacityWhileSiblingRemains(): Set<String> {
    if (streamTraces.none { it.remainingAfter > 0 }) return emptySet()
    return streamTraces
        .filter { stream ->
            stream.remainingAfter == 0 &&
                stream.consumedUnits < stream.nativeCapacity
        }
        .mapTo(mutableSetOf()) { it.streamId }
}

fun PcSheetExtendedCompositionTrace.hasPhysicallyAvoidableNextPage(): Boolean =
    demandsAfter.isNotEmpty() && physical?.nextAtomicUnitFits == true

fun PcSheetExtendedCompositionTrace.reclaimableExhaustedModules(): Set<PcSheetSemanticModule> {
    if (demandsAfter.isEmpty()) return emptySet()
    val remainingModules = demandsAfter.mapTo(mutableSetOf()) { it.module }
    return placements
        .filter { placement ->
            placement.module !in remainingModules &&
                placement.consumedUnits < placement.nativeCapacity
        }
        .mapTo(mutableSetOf()) { it.module }
}

data class PcSheetModuleDemand(
    val module: PcSheetSemanticModule,
    val remainingUnits: Int,
    /** Ordered whole-record sizes measured in this module's native physical row units. */
    val atomicUnitSizes: List<Int> = emptyList(),
) {
    init {
        require(remainingUnits > 0) { "Active module demand must be positive." }
        require(atomicUnitSizes.all { it > 0 }) { "Native atomic records must occupy positive rows." }
        require(atomicUnitSizes.isEmpty() || atomicUnitSizes.sum() == remainingUnits) {
            "Ordered native atomic record sizes must exactly cover the remaining physical rows."
        }
    }

    fun afterConsuming(rows: Int): PcSheetModuleDemand? {
        require(rows in 0..remainingUnits)
        if (rows == remainingUnits) return null
        if (atomicUnitSizes.isEmpty()) return copy(remainingUnits = remainingUnits - rows)
        var consumed = rows
        val remainingAtoms = atomicUnitSizes.dropWhile { atom ->
            if (consumed >= atom) {
                consumed -= atom
                true
            } else false
        }
        require(consumed == 0) { "Physical pagination must never split a native atomic record." }
        return copy(remainingUnits = remainingUnits - rows, atomicUnitSizes = remainingAtoms)
    }

    /** Number of complete next atomic native records that fit; never split a record. */
    fun wholeRowsFitting(alreadyConsumed: Int, capacity: Int): Int {
        require(capacity >= 0 && alreadyConsumed in 0..remainingUnits)
        if (atomicUnitSizes.isEmpty()) return minOf(capacity, remainingUnits - alreadyConsumed)
        val pending = requireNotNull(afterConsuming(alreadyConsumed)) {
            "Cannot fit records after a completely exhausted demand."
        }
        var count = 0
        pending.atomicUnitSizes.forEach { size ->
            if (count + size > capacity) return count
            count += size
        }
        return count
    }
}

/**
 * One source/native slot in a family-approved Extended-page layout.
 *
 * Capacity is expressed in that module's own native record units. The composer never changes the
 * slot geometry; it only decides which compatible active module occupies the slot.
 */
data class PcSheetExtendedLayoutSlot(
    val id: String,
    val capacityByModule: Map<PcSheetSemanticModule, Int>,
    /** Measured source-native occupied area (pt squared) for one atomic unit of each module. */
    val measuredNativeAreaPerUnit: Map<PcSheetSemanticModule, Double> = emptyMap(),
) {
    init {
        require(id.isNotBlank()) { "Extended layout slot id is required." }
        require(capacityByModule.isNotEmpty()) { "Extended layout slot must accept at least one module." }
        capacityByModule.forEach { (module, capacity) ->
            require(capacity > 0) { "Slot $id capacity for $module must be positive." }
        }
        measuredNativeAreaPerUnit.forEach { (module, area) ->
            require(module in capacityByModule && area > 0.0 && area.isFinite()) {
                "Measured native area must be positive and belong to a compatible module."
            }
        }
    }
}

/**
 * A layout template is already valid for the visual family before it reaches the compositor.
 *
 * [priority] is only a deterministic tie-breaker between equally suitable valid layouts. It is not
 * a page-count target and never authorizes resizing a module.
 */
data class PcSheetExtendedLayoutTemplate(
    val id: String,
    val slots: List<PcSheetExtendedLayoutSlot>,
    val priority: Int = 0,
) {
    init {
        require(id.isNotBlank()) { "Extended layout id is required." }
        require(slots.isNotEmpty()) { "Extended layout must contain at least one native slot." }
        require(slots.map { it.id }.distinct().size == slots.size) {
            "Extended layout slot ids must be unique."
        }

        val exclusiveModules = slots
            .flatMap { it.capacityByModule.keys }
            .filter {
                PcSheetNativeModuleContracts.forModule(it).pageOccupancy ==
                    PcSheetModulePageOccupancy.FULL_NATIVE_PAGE_EXCLUSIVE
            }
            .toSet()

        if (exclusiveModules.isNotEmpty()) {
            require(exclusiveModules.size == 1) {
                "A valid layout cannot combine multiple full-page-exclusive modules."
            }
            val exclusiveModule = exclusiveModules.single()
            require(slots.size == 1 && slots.single().capacityByModule.keys == setOf(exclusiveModule)) {
                "A full-page-exclusive module must own its complete native layout."
            }
        }
    }
}

data class PcSheetExtendedPlacement(
    val slotId: String,
    val module: PcSheetSemanticModule,
    val consumedUnits: Int,
    val nativeCapacity: Int,
) {
    init {
        require(slotId.isNotBlank()) { "Placement slot id is required." }
        require(consumedUnits > 0) { "Placement must consume at least one unit." }
        require(nativeCapacity > 0) { "Placement native capacity must be positive." }
        require(consumedUnits <= nativeCapacity) {
            "Placement cannot consume more than the native slot capacity."
        }
    }

    val utilization: Double
        get() = consumedUnits.toDouble() / nativeCapacity.toDouble()
}

data class PcSheetComposedExtendedPage(
    val layoutId: String,
    val placements: List<PcSheetExtendedPlacement>,
)

data class PcSheetExtendedCompositionStep(
    val page: PcSheetComposedExtendedPage,
    val remainingDemands: List<PcSheetModuleDemand>,
    val trace: PcSheetExtendedCompositionTrace,
)


/**
 * Evidence for each actually chosen native layout. Native slot fractions are normalized;
 * per-stream rows/entries retain their distinct real capacities and cannot be added as
 * interchangeable physical heights. This does not prove omitted alternative layouts illegal.
 */
fun PcSheetExtendedCompositionStep.traceWithNativeSlotUtilization(
    selectedLayout: PcSheetExtendedLayoutTemplate,
): PcSheetExtendedCompositionTrace {
    require(page.layoutId == selectedLayout.id) {
        "Physical native-slot trace must use the selected layout."
    }
    val slotsById = selectedLayout.slots.associateBy { it.id }
    val before = trace.demandsBefore.associate { it.module to it.remainingUnits }
    val after = trace.demandsAfter.associate { it.module to it.remainingUnits }
    val afterDemands = trace.demandsAfter.associateBy { it.module }
    fun nextAtomicRows(module: PcSheetSemanticModule): Int =
        afterDemands[module]?.atomicUnitSizes?.firstOrNull() ?: 1
    val placedBySlot = page.placements.associateBy { it.slotId }
    require(placedBySlot.size == page.placements.size) {
        "Only one placement is permitted per physical native slot."
    }
    val alreadyConsumed = mutableMapOf<PcSheetSemanticModule, Int>()
    val streams = page.placements.map { placement ->
        val slot = requireNotNull(slotsById[placement.slotId]) {
            "Placed native slot is missing from the selected physical layout."
        }
        require(slot.capacityByModule[placement.module] == placement.nativeCapacity) {
            "Physical trace capacity differs from the native layout contract."
        }
        val remainingBefore =
            requireNotNull(before[placement.module]) -
                alreadyConsumed.getOrDefault(placement.module, 0)
        alreadyConsumed[placement.module] =
            alreadyConsumed.getOrDefault(placement.module, 0) + placement.consumedUnits
        PcSheetPaginationStreamTrace(
            streamId = placement.slotId,
            module = placement.module,
            remainingBefore = remainingBefore,
            consumedUnits = placement.consumedUnits,
            nativeCapacity = placement.nativeCapacity,
            remainingAfter = remainingBefore - placement.consumedUnits,
        )
    }
    val pending = after.isNotEmpty()
    val compatibleRemaining = selectedLayout.slots.firstNotNullOfOrNull { slot ->
        val occupied = placedBySlot[slot.id]
        when {
            occupied != null &&
                after.getOrDefault(occupied.module, 0) > 0 &&
                occupied.nativeCapacity - occupied.consumedUnits >= nextAtomicRows(occupied.module) ->
                occupied.module to occupied.nativeCapacity
            occupied == null ->
                slot.capacityByModule.entries.firstNotNullOfOrNull { (module, cap) ->
                    (module to cap).takeIf {
                        after.getOrDefault(module, 0) > 0 && nextAtomicRows(module) <= cap
                    }
                }
            else -> null
        }
    }
    val nextCandidate = if (pending) {
        compatibleRemaining ?: selectedLayout.slots.firstNotNullOfOrNull { slot ->
            slot.capacityByModule.entries.firstNotNullOfOrNull { (module, cap) ->
                (module to cap).takeIf { after.getOrDefault(module, 0) > 0 }
            }
        }
    } else null
    return trace.copy(
        streamTraces = streams,
        physical = PcSheetPhysicalPaginationTrace(
            metric = "native-slot-utilization",
            used = page.placements.sumOf { it.utilization },
            capacity = selectedLayout.slots.size.toDouble(),
            nextAtomicUnitSize = nextCandidate?.let { (module, cap) ->
                nextAtomicRows(module).toDouble() / cap
            },
            nextAtomicUnitFits = if (pending) compatibleRemaining != null else null,
            rationale = when {
                !pending -> "resource-option-streams-exhausted"
                compatibleRemaining != null -> "next-whole-native-record-fits-selected-slot"
                else -> "next-whole-native-record-does-not-fit-selected-slot;alternative-layouts-unproven"
            },
        ),
    )
}

object PcSheetExtendedPageComposer {
    fun composeNextPage(
        demands: List<PcSheetModuleDemand>,
        layouts: List<PcSheetExtendedLayoutTemplate>,
    ): PcSheetExtendedCompositionStep? {
        if (demands.isEmpty()) return null
        require(layouts.isNotEmpty()) { "At least one valid Extended layout is required." }
        require(demands.map { it.module }.distinct().size == demands.size) {
            "Active module demand must contain at most one entry per semantic module."
        }

        val activeExclusiveModules = demands
            .map { it.module }
            .filter {
                PcSheetNativeModuleContracts.forModule(it).pageOccupancy ==
                    PcSheetModulePageOccupancy.FULL_NATIVE_PAGE_EXCLUSIVE
            }

        val candidateLayouts = if (activeExclusiveModules.isNotEmpty()) {
            val exclusive = activeExclusiveModules.first()
            layouts.filter { layout ->
                layout.slots.size == 1 &&
                    layout.slots.single().capacityByModule.keys == setOf(exclusive)
            }
        } else {
            layouts.filter { layout ->
                layout.slots.none { slot ->
                    slot.capacityByModule.keys.any {
                        PcSheetNativeModuleContracts.forModule(it).pageOccupancy ==
                            PcSheetModulePageOccupancy.FULL_NATIVE_PAGE_EXCLUSIVE
                    }
                }
            }
        }

        val demandByModule = demands.associateBy { it.module }
        val evaluated = candidateLayouts.mapNotNull { layout ->
            bestAssignment(layout, demandByModule)?.let { assignment ->
                EvaluatedLayout(
                    layout = layout,
                    placements = assignment,
                )
            }
        }

        // Do not reward an extra filled slot over a physically dominant native layout.
        // Compare measured area only when EVERY eligible layout supplies comparable measures.
        val allMeasured = evaluated.isNotEmpty() && evaluated.all { it.measuredNativeArea != null }
        val scoreOrder = if (allMeasured) {
            compareBy<EvaluatedLayout> { requireNotNull(it.measuredNativeArea) }
                .thenBy { it.totalUtilization }
                .thenBy { it.filledSlots }
                .thenBy { it.distinctModules }
                .thenBy { -it.layout.priority }
                .thenBy { it.layout.id }
        } else {
            compareBy<EvaluatedLayout> { it.totalUtilization }
                .thenBy { it.filledSlots }
                .thenBy { it.distinctModules }
                .thenBy { -it.layout.priority }
                .thenBy { it.layout.id }
        }
        val best = evaluated.maxWithOrNull(scoreOrder) ?: error(
            "No valid Extended layout can consume the active modules: " +
                demands.joinToString { it.module.name },
        )

        val consumedByModule = best.placements
            .groupBy { it.module }
            .mapValues { (_, placements) -> placements.sumOf { it.consumedUnits } }

        val remaining = demands.mapNotNull { demand ->
            demand.afterConsuming(consumedByModule.getOrDefault(demand.module, 0))
        }

        return PcSheetExtendedCompositionStep(
            page = PcSheetComposedExtendedPage(
                layoutId = best.layout.id,
                placements = best.placements,
            ),
            remainingDemands = remaining,
            trace = PcSheetExtendedCompositionTrace(
                candidateLayoutIds = candidateLayouts.map { it.id },
                eligibleLayoutIds = evaluated.map { it.layout.id },
                eligibleNativeAreaScores = evaluated.mapNotNull { candidate ->
                    candidate.measuredNativeArea?.let { candidate.layout.id to it }
                }.toMap(),
                demandsBefore = demands,
                chosenLayoutId = best.layout.id,
                placements = best.placements,
                demandsAfter = remaining,
                score = PcSheetCompositionScoreTrace(
                    totalUtilization = best.totalUtilization,
                    filledSlots = best.filledSlots,
                    distinctModules = best.distinctModules,
                ),
            ),
        )
    }

    private fun bestAssignment(
        layout: PcSheetExtendedLayoutTemplate,
        demandByModule: Map<PcSheetSemanticModule, PcSheetModuleDemand>,
    ): List<PcSheetExtendedPlacement>? {
        var best: AssignmentScore? = null

        fun search(
            slotIndex: Int,
            remaining: Map<PcSheetSemanticModule, Int>,
            placements: List<PcSheetExtendedPlacement>,
        ) {
            if (slotIndex >= layout.slots.size) {
                if (placements.isEmpty()) return
                val candidate = AssignmentScore(placements)
                if (best == null || candidate > requireNotNull(best)) {
                    best = candidate
                }
                return
            }

            val slot = layout.slots[slotIndex]

            // A valid layout may intentionally leave one slot unused when its semantic stream is
            // exhausted and another active module uses the rest of the page.
            search(slotIndex + 1, remaining, placements)

            slot.capacityByModule.forEach { (module, nativeCapacity) ->
                val demand = remaining[module] ?: return@forEach
                if (demand <= 0) return@forEach

                val descriptor = requireNotNull(demandByModule[module])
                val alreadyConsumed = descriptor.remainingUnits - demand
                val consumed = descriptor.wholeRowsFitting(alreadyConsumed, nativeCapacity)
                if (consumed == 0) return@forEach
                val updatedRemaining = remaining.toMutableMap().apply {
                    this[module] = demand - consumed
                }
                search(
                    slotIndex = slotIndex + 1,
                    remaining = updatedRemaining,
                    placements = placements + PcSheetExtendedPlacement(
                        slotId = slot.id,
                        module = module,
                        consumedUnits = consumed,
                        nativeCapacity = nativeCapacity,
                    ),
                )
            }
        }

        search(
            slotIndex = 0,
            remaining = demandByModule.mapValues { it.value.remainingUnits },
            placements = emptyList(),
        )
        return best?.placements
    }

    private data class EvaluatedLayout(
        val layout: PcSheetExtendedLayoutTemplate,
        val placements: List<PcSheetExtendedPlacement>,
    ) {
        val totalUtilization: Double
            get() = placements.sumOf { it.utilization }
        val filledSlots: Int
            get() = placements.size
        val distinctModules: Int
            get() = placements.map { it.module }.distinct().size
        val measuredNativeArea: Double?
            get() {
                val slots = layout.slots.associateBy { it.id }
                val areas = placements.mapNotNull { placement ->
                    slots[placement.slotId]?.measuredNativeAreaPerUnit?.get(placement.module)
                }
                if (areas.size != placements.size) return null
                return placements.indices.sumOf { index ->
                    placements[index].consumedUnits * areas[index]
                }
            }
    }

    private data class AssignmentScore(
        val placements: List<PcSheetExtendedPlacement>,
    ) : Comparable<AssignmentScore> {
        private val totalUtilization: Double = placements.sumOf { it.utilization }
        private val filledSlots: Int = placements.size
        private val distinctModules: Int = placements.map { it.module }.distinct().size

        override fun compareTo(other: AssignmentScore): Int =
            compareValuesBy(
                this,
                other,
                { it.totalUtilization },
                { it.filledSlots },
                { it.distinctModules },
            )
    }
}
