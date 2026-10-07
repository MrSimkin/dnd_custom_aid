package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * One family-approved global Extended composition front.
 *
 * A front is a compatibility partition, not a physical layout: every active semantic module must
 * belong to exactly one front before any Extended page is emitted. The renderer may then use the
 * existing [PcSheetExtendedPageComposer] inside that front to choose among family-native layouts.
 *
 * This keeps global coordination separate from measured geometry and prevents a renderer from
 * silently scheduling the same semantic stream through two unrelated role loops.
 */
data class PcSheetExtendedGlobalFront(
    val id: String,
    val modules: Set<PcSheetSemanticModule>,
    val priority: Int,
) {
    init {
        require(id.isNotBlank()) { "Global Extended front id is required." }
        require(modules.isNotEmpty()) { "Global Extended front must own at least one semantic module." }

        val exclusive = modules.filter {
            PcSheetNativeModuleContracts.forModule(it).pageOccupancy ==
                PcSheetModulePageOccupancy.FULL_NATIVE_PAGE_EXCLUSIVE
        }
        if (exclusive.isNotEmpty()) {
            require(exclusive.size == 1 && modules.size == 1) {
                "A full-page-exclusive semantic module must own its global front."
            }
        }
    }
}

data class PcSheetExtendedGlobalFrontSelection(
    val id: String,
    val activeModules: Set<PcSheetSemanticModule>,
    val priority: Int,
)

/**
 * Family-global coordinator for Extended semantic streams.
 *
 * The coordinator deliberately does not invent dimensions or combine arbitrary roles. Families
 * declare the compatibility fronts they actually support; this coordinator verifies that every
 * active stream is owned exactly once before page emission and returns the selected fronts in a
 * stable owner-approved order. Physical packing within each front remains the responsibility of
 * [PcSheetExtendedPageComposer] and the renderer's measured native layouts.
 */
object PcSheetExtendedGlobalCoordinator {
    fun plan(
        activeModules: Set<PcSheetSemanticModule>,
        fronts: List<PcSheetExtendedGlobalFront>,
    ): List<PcSheetExtendedGlobalFrontSelection> {
        if (activeModules.isEmpty()) return emptyList()

        require(fronts.isNotEmpty()) {
            "Active Extended semantic streams require at least one family-global front."
        }
        require(fronts.map { it.id }.distinct().size == fronts.size) {
            "Family-global Extended front ids must be unique."
        }

        val owners = mutableMapOf<PcSheetSemanticModule, PcSheetExtendedGlobalFront>()
        fronts.forEach { front ->
            front.modules.forEach { module ->
                val previous = owners.put(module, front)
                require(previous == null) {
                    "Semantic module $module is owned by more than one global Extended front: " +
                        "${previous?.id} and ${front.id}."
                }
            }
        }

        activeModules.forEach { module ->
            require(owners.containsKey(module)) {
                "Active semantic module $module is missing from the family-global Extended plan."
            }
        }

        return fronts
            .mapNotNull { front ->
                val selected = front.modules intersect activeModules
                if (selected.isEmpty()) {
                    null
                } else {
                    PcSheetExtendedGlobalFrontSelection(
                        id = front.id,
                        activeModules = selected,
                        priority = front.priority,
                    )
                }
            }
            .sortedWith(compareBy<PcSheetExtendedGlobalFrontSelection> { it.priority }.thenBy { it.id })
    }
}
