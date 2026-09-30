package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PcSheetPdfNativeModuleContractsTest {
    @Test
    fun everySemanticModuleHasAnExplicitNativeContract() {
        assertEquals(
            PcSheetSemanticModule.entries.toSet(),
            PcSheetNativeModuleContracts.all().map { it.module }.toSet(),
        )
    }

    @Test
    fun notesOwnAFullNativePageAndAreNeverArbitrarilyResized() {
        val notes = PcSheetNativeModuleContracts.forModule(PcSheetSemanticModule.NOTES)

        assertEquals(PcSheetNativeRepeatUnit.NONE, notes.repeatUnit)
        assertEquals(PcSheetModulePageOccupancy.FULL_NATIVE_PAGE_EXCLUSIVE, notes.pageOccupancy)
        assertFalse(notes.arbitraryResizeAllowed)
    }

    @Test
    fun specialEquipmentRepeatsTheWholeNativeModule() {
        val special = PcSheetNativeModuleContracts.forModule(PcSheetSemanticModule.SPECIAL_EQUIPMENT)

        assertEquals(PcSheetNativeRepeatUnit.WHOLE_MODULE, special.repeatUnit)
        assertEquals(
            PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
            special.pageOccupancy,
        )
        assertFalse(special.arbitraryResizeAllowed)
    }

    @Test
    fun ordinaryEquipmentExtendsThroughNativeRowsOrColumns() {
        val equipment = PcSheetNativeModuleContracts.forModule(PcSheetSemanticModule.ORDINARY_EQUIPMENT)

        assertEquals(PcSheetNativeRepeatUnit.NATIVE_ROWS_OR_COLUMNS, equipment.repeatUnit)
        assertTrue(equipment.releasesSpaceWhenExhausted)
        assertFalse(equipment.arbitraryResizeAllowed)
    }

    @Test
    fun combatUsesContentDrivenLogicalRows() {
        val combat = PcSheetNativeModuleContracts.forModule(PcSheetSemanticModule.COMBAT_ACTIONS)

        assertEquals(PcSheetNativeRepeatUnit.LOGICAL_ROWS, combat.repeatUnit)
        assertTrue(combat.contentDrivenLogicalRowHeight)
        assertFalse(combat.arbitraryResizeAllowed)
    }

    @Test
    fun exhaustedShareableModulesReleaseTheirSpace() {
        listOf(
            PcSheetSemanticModule.TRAITS,
            PcSheetSemanticModule.BACKGROUND_STORY,
            PcSheetSemanticModule.RESOURCES,
            PcSheetSemanticModule.CLASS_CHOICES,
            PcSheetSemanticModule.ORDINARY_EQUIPMENT,
            PcSheetSemanticModule.SPECIAL_EQUIPMENT,
        ).forEach { module ->
            val contract = PcSheetNativeModuleContracts.forModule(module)
            assertEquals(
                PcSheetModulePageOccupancy.SHAREABLE_WHEN_LAYOUT_ALLOWS,
                contract.pageOccupancy,
            )
            assertTrue(contract.releasesSpaceWhenExhausted)
        }
    }
}
