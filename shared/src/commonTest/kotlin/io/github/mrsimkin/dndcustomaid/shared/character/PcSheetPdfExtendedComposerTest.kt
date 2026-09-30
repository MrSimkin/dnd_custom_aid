package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PcSheetPdfExtendedComposerTest {
    @Test
    fun combinesCompatibleActiveModulesInsteadOfGivingEachRoleItsOwnPage() {
        val layouts = listOf(
            PcSheetExtendedLayoutTemplate(
                id = "traits-only",
                slots = listOf(
                    slot("traits", PcSheetSemanticModule.TRAITS to 4),
                ),
                priority = 10,
            ),
            PcSheetExtendedLayoutTemplate(
                id = "equipment-only",
                slots = listOf(
                    slot("equipment", PcSheetSemanticModule.ORDINARY_EQUIPMENT to 3),
                ),
                priority = 10,
            ),
            PcSheetExtendedLayoutTemplate(
                id = "mixed-native-layout",
                slots = listOf(
                    slot("left", PcSheetSemanticModule.TRAITS to 4),
                    slot("right", PcSheetSemanticModule.ORDINARY_EQUIPMENT to 3),
                    slot("bottom", PcSheetSemanticModule.RESOURCES to 1),
                ),
                priority = 0,
            ),
        )

        val step = requireNotNull(
            PcSheetExtendedPageComposer.composeNextPage(
                demands = listOf(
                    PcSheetModuleDemand(PcSheetSemanticModule.TRAITS, 4),
                    PcSheetModuleDemand(PcSheetSemanticModule.ORDINARY_EQUIPMENT, 3),
                    PcSheetModuleDemand(PcSheetSemanticModule.RESOURCES, 1),
                ),
                layouts = layouts,
            ),
        )

        assertEquals("mixed-native-layout", step.page.layoutId)
        assertEquals(
            setOf(
                PcSheetSemanticModule.TRAITS,
                PcSheetSemanticModule.ORDINARY_EQUIPMENT,
                PcSheetSemanticModule.RESOURCES,
            ),
            step.page.placements.map { it.module }.toSet(),
        )
        assertTrue(step.remainingDemands.isEmpty())
    }

    @Test
    fun exhaustedModulesDoNotReserveTheirOldSlots() {
        val step = requireNotNull(
            PcSheetExtendedPageComposer.composeNextPage(
                demands = listOf(
                    PcSheetModuleDemand(PcSheetSemanticModule.ORDINARY_EQUIPMENT, 2),
                ),
                layouts = listOf(
                    PcSheetExtendedLayoutTemplate(
                        id = "traits-equipment",
                        slots = listOf(
                            slot(
                                "left",
                                PcSheetSemanticModule.TRAITS to 4,
                                PcSheetSemanticModule.ORDINARY_EQUIPMENT to 2,
                            ),
                            slot(
                                "right",
                                PcSheetSemanticModule.ORDINARY_EQUIPMENT to 2,
                            ),
                        ),
                    ),
                ),
            ),
        )

        assertEquals("traits-equipment", step.page.layoutId)
        assertTrue(step.page.placements.none { it.module == PcSheetSemanticModule.TRAITS })
        assertTrue(step.remainingDemands.isEmpty())
    }

    @Test
    fun notesTakeTheirFullNativePageBeforeOtherActiveModules() {
        val layouts = listOf(
            PcSheetExtendedLayoutTemplate(
                id = "notes-native-page",
                slots = listOf(
                    slot("notes", PcSheetSemanticModule.NOTES to 10),
                ),
            ),
            PcSheetExtendedLayoutTemplate(
                id = "equipment-page",
                slots = listOf(
                    slot("equipment", PcSheetSemanticModule.ORDINARY_EQUIPMENT to 4),
                ),
            ),
        )

        val step = requireNotNull(
            PcSheetExtendedPageComposer.composeNextPage(
                demands = listOf(
                    PcSheetModuleDemand(PcSheetSemanticModule.NOTES, 6),
                    PcSheetModuleDemand(PcSheetSemanticModule.ORDINARY_EQUIPMENT, 4),
                ),
                layouts = layouts,
            ),
        )

        assertEquals("notes-native-page", step.page.layoutId)
        assertEquals(listOf(PcSheetSemanticModule.NOTES), step.page.placements.map { it.module })
        assertEquals(
            listOf(PcSheetModuleDemand(PcSheetSemanticModule.ORDINARY_EQUIPMENT, 4)),
            step.remainingDemands,
        )
    }

    @Test
    fun fullPageExclusiveNotesCannotBeCombinedWithOtherSlotsInOneTemplate() {
        assertFailsWith<IllegalArgumentException> {
            PcSheetExtendedLayoutTemplate(
                id = "invalid-notes-mix",
                slots = listOf(
                    slot("notes", PcSheetSemanticModule.NOTES to 10),
                    slot("equipment", PcSheetSemanticModule.ORDINARY_EQUIPMENT to 4),
                ),
            )
        }
    }

    @Test
    fun failsWhenNoFamilyApprovedLayoutCanConsumeRemainingContent() {
        assertFailsWith<IllegalStateException> {
            PcSheetExtendedPageComposer.composeNextPage(
                demands = listOf(
                    PcSheetModuleDemand(PcSheetSemanticModule.COMBAT_ACTIONS, 2),
                ),
                layouts = listOf(
                    PcSheetExtendedLayoutTemplate(
                        id = "equipment-only",
                        slots = listOf(
                            slot("equipment", PcSheetSemanticModule.ORDINARY_EQUIPMENT to 4),
                        ),
                    ),
                ),
            )
        }
    }

    private fun slot(
        id: String,
        vararg capacities: Pair<PcSheetSemanticModule, Int>,
    ): PcSheetExtendedLayoutSlot =
        PcSheetExtendedLayoutSlot(
            id = id,
            capacityByModule = capacities.toMap(),
        )
}
