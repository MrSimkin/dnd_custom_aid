package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PcSheetPdfGlobalCoordinatorTest {
    @Test
    fun plansEveryActiveModuleExactlyOnceAcrossFamilyFronts() {
        val fronts = listOf(
            PcSheetExtendedGlobalFront(
                id = "narrative-traits",
                modules = setOf(
                    PcSheetSemanticModule.BACKGROUND_STORY,
                    PcSheetSemanticModule.TRAITS,
                ),
                priority = 10,
            ),
            PcSheetExtendedGlobalFront(
                id = "resources-options",
                modules = setOf(
                    PcSheetSemanticModule.RESOURCES,
                    PcSheetSemanticModule.CLASS_CHOICES,
                ),
                priority = 20,
            ),
            PcSheetExtendedGlobalFront(
                id = "notes",
                modules = setOf(PcSheetSemanticModule.NOTES),
                priority = 90,
            ),
        )

        val plan = PcSheetExtendedGlobalCoordinator.plan(
            activeModules = setOf(
                PcSheetSemanticModule.TRAITS,
                PcSheetSemanticModule.RESOURCES,
                PcSheetSemanticModule.NOTES,
            ),
            fronts = fronts,
        )

        assertEquals(listOf("narrative-traits", "resources-options", "notes"), plan.map { it.id })
        assertEquals(setOf(PcSheetSemanticModule.TRAITS), plan[0].activeModules)
        assertEquals(setOf(PcSheetSemanticModule.RESOURCES), plan[1].activeModules)
        assertEquals(setOf(PcSheetSemanticModule.NOTES), plan[2].activeModules)
    }

    @Test
    fun rejectsMissingOrMultiplyOwnedActiveModules() {
        assertFailsWith<IllegalArgumentException> {
            PcSheetExtendedGlobalCoordinator.plan(
                activeModules = setOf(PcSheetSemanticModule.COMBAT_ACTIONS),
                fronts = listOf(
                    PcSheetExtendedGlobalFront(
                        id = "traits",
                        modules = setOf(PcSheetSemanticModule.TRAITS),
                        priority = 10,
                    ),
                ),
            )
        }

        assertFailsWith<IllegalArgumentException> {
            PcSheetExtendedGlobalCoordinator.plan(
                activeModules = setOf(PcSheetSemanticModule.TRAITS),
                fronts = listOf(
                    PcSheetExtendedGlobalFront(
                        id = "traits-a",
                        modules = setOf(PcSheetSemanticModule.TRAITS),
                        priority = 10,
                    ),
                    PcSheetExtendedGlobalFront(
                        id = "traits-b",
                        modules = setOf(PcSheetSemanticModule.TRAITS),
                        priority = 20,
                    ),
                ),
            )
        }
    }

    @Test
    fun fullPageExclusiveNotesCannotShareGlobalFront() {
        assertFailsWith<IllegalArgumentException> {
            PcSheetExtendedGlobalFront(
                id = "invalid-notes-share",
                modules = setOf(PcSheetSemanticModule.NOTES, PcSheetSemanticModule.TRAITS),
                priority = 10,
            )
        }
    }
}
