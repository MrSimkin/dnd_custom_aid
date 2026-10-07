package io.github.mrsimkin.dndcustomaid.shared.character

data class PcSheetCompositionScoreTrace(
    val totalUtilization: Double,
    val filledSlots: Int,
    val distinctModules: Int,
)

data class PcSheetExtendedCompositionTrace(
    val candidateLayoutIds: List<String>,
    val eligibleLayoutIds: List<String>,
    val demandsBefore: List<PcSheetModuleDemand>,
    val chosenLayoutId: String,
    val placements: List<PcSheetExtendedPlacement>,
    val demandsAfter: List<PcSheetModuleDemand>,
    val score: PcSheetCompositionScoreTrace,
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
            append('}')
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
    append("],\"demandsBefore\":")
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
    append("}}")
}

data class PcSheetModuleDemand(
    val module: PcSheetSemanticModule,
    val remainingUnits: Int,
) {
    init {
        require(remainingUnits > 0) { "Active module demand must be positive." }
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
) {
    init {
        require(id.isNotBlank()) { "Extended layout slot id is required." }
        require(capacityByModule.isNotEmpty()) { "Extended layout slot must accept at least one module." }
        capacityByModule.forEach { (module, capacity) ->
            require(capacity > 0) { "Slot $id capacity for $module must be positive." }
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

        val demandByModule = demands.associate { it.module to it.remainingUnits }
        val evaluated = candidateLayouts.mapNotNull { layout ->
            bestAssignment(layout, demandByModule)?.let { assignment ->
                EvaluatedLayout(
                    layout = layout,
                    placements = assignment,
                )
            }
        }

        val best = evaluated.maxWithOrNull(
            compareBy<EvaluatedLayout> { it.totalUtilization }
                .thenBy { it.filledSlots }
                .thenBy { it.distinctModules }
                .thenBy { -it.layout.priority }
                .thenBy { it.layout.id },
        ) ?: error(
            "No valid Extended layout can consume the active modules: " +
                demands.joinToString { it.module.name },
        )

        val consumedByModule = best.placements
            .groupBy { it.module }
            .mapValues { (_, placements) -> placements.sumOf { it.consumedUnits } }

        val remaining = demands.mapNotNull { demand ->
            val left = demand.remainingUnits - consumedByModule.getOrDefault(demand.module, 0)
            if (left > 0) PcSheetModuleDemand(demand.module, left) else null
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
        demandByModule: Map<PcSheetSemanticModule, Int>,
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

                val consumed = minOf(demand, nativeCapacity)
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
            remaining = demandByModule,
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
