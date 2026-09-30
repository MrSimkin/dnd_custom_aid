package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * What kind of native unit may be repeated when a semantic module needs more capacity.
 *
 * This deliberately does not invent new widths/heights. Platform renderers still provide the
 * measured/source geometry for each family.
 */
enum class PcSheetNativeRepeatUnit {
    NATIVE_COMPONENTS,
    NATIVE_ROWS_OR_COLUMNS,
    LOGICAL_ROWS,
    WHOLE_MODULE,
    NONE,
}

enum class PcSheetModulePageOccupancy {
    SHAREABLE_WHEN_LAYOUT_ALLOWS,
    FULL_NATIVE_PAGE_EXCLUSIVE,
}

/**
 * Family-independent capability contract for one semantic module.
 *
 * Exact source coordinates remain a renderer/family concern. These flags encode the owner-approved
 * constraints that the global compositor must respect before it considers a physical layout.
 */
data class PcSheetNativeModuleContract(
    val module: PcSheetSemanticModule,
    val repeatUnit: PcSheetNativeRepeatUnit,
    val pageOccupancy: PcSheetModulePageOccupancy,
    val contentDrivenLogicalRowHeight: Boolean = false,
    val releasesSpaceWhenExhausted: Boolean = true,
    val arbitraryResizeAllowed: Boolean = false,
) {
    init {
        if (pageOccupancy == PcSheetModulePageOccupancy.FULL_NATIVE_PAGE_EXCLUSIVE) {
            require(repeatUnit == PcSheetNativeRepeatUnit.NONE) {
                "A full-native-page module cannot also expose a smaller repeat unit."
            }
        }
        if (contentDrivenLogicalRowHeight) {
            require(repeatUnit == PcSheetNativeRepeatUnit.LOGICAL_ROWS) {
                "Content-driven row height is only valid for logical-row modules."
            }
        }
        require(!arbitraryResizeAllowed) {
            "Phase-3 native module contracts do not permit arbitrary geometric resizing."
        }
    }
}

object PcSheetNativeModuleContracts {
    private val contracts: Map<PcSheetSemanticModule, PcSheetNativeModuleContract> = listOf(
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.CUSTOM_STATISTICS,
            repeatUnit = PcSheetNativeRepeatUnit.NATIVE_COMPONENTS,
            pageOccupancy = PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
        ),
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.TRAITS,
            repeatUnit = PcSheetNativeRepeatUnit.NATIVE_ROWS_OR_COLUMNS,
            pageOccupancy = PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
        ),
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.BACKGROUND_STORY,
            repeatUnit = PcSheetNativeRepeatUnit.NATIVE_ROWS_OR_COLUMNS,
            pageOccupancy = PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
        ),
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.COMBAT_ACTIONS,
            repeatUnit = PcSheetNativeRepeatUnit.LOGICAL_ROWS,
            pageOccupancy = PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
            contentDrivenLogicalRowHeight = true,
        ),
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.RESOURCES,
            repeatUnit = PcSheetNativeRepeatUnit.NATIVE_ROWS_OR_COLUMNS,
            pageOccupancy = PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
        ),
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.CLASS_CHOICES,
            repeatUnit = PcSheetNativeRepeatUnit.NATIVE_ROWS_OR_COLUMNS,
            pageOccupancy = PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
        ),
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.ORDINARY_EQUIPMENT,
            repeatUnit = PcSheetNativeRepeatUnit.NATIVE_ROWS_OR_COLUMNS,
            pageOccupancy = PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
        ),
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.SPECIAL_EQUIPMENT,
            repeatUnit = PcSheetNativeRepeatUnit.WHOLE_MODULE,
            pageOccupancy = PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
        ),
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.NOTES,
            repeatUnit = PcSheetNativeRepeatUnit.NONE,
            pageOccupancy = PcSheetModulePageOccupancy.FULL_NATIVE_PAGE_EXCLUSIVE,
        ),
        PcSheetNativeModuleContract(
            module = PcSheetSemanticModule.SPELLS,
            repeatUnit = PcSheetNativeRepeatUnit.NATIVE_ROWS_OR_COLUMNS,
            pageOccupancy = PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
        ),
    ).associateBy { it.module }

    fun forModule(module: PcSheetSemanticModule): PcSheetNativeModuleContract =
        requireNotNull(contracts[module]) {
            "Missing native module contract for $module."
        }

    fun all(): List<PcSheetNativeModuleContract> =
        PcSheetSemanticModule.entries.map(::forModule)
}
