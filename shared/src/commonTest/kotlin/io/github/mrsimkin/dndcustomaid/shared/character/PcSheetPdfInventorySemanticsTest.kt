package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class PcSheetPdfInventorySemanticsTest {
    @Test
    fun ordinaryEquipmentIdentityIsQuantityNameAndWeightOnly() {
        val item = CharacterInventoryItem(
            id = Uuid.random(),
            name = "Virotes",
            quantity = 20,
            weightLb = 0.075,
            special = false,
            description = "Munición de prueba",
            location = "Carcaj",
            attuned = false,
            equipped = false,
            notes = "No debe ocupar otra línea de Equipo",
            sortOrder = 0,
        )

        val compact = item.pdfCompactEquipmentLabel()
        assertEquals("20 x Virotes · 0.075 lb", compact)
        assertFalse(compact.contains("Carcaj"))
        assertFalse(compact.contains("Munición"))
        assertFalse(compact.contains("otra línea"))

        val detail = requireNotNull(item.pdfOrdinaryEquipmentDetailOrNull())
        assertTrue(detail.contains("Ubicación: Carcaj"))
        assertTrue(detail.contains("Munición de prueba"))
        assertTrue(detail.contains("No debe ocupar otra línea de Equipo"))
    }

    @Test
    fun specialEquipmentKeepsItsOwnDedicatedSemantics() {
        val item = CharacterInventoryItem(
            id = Uuid.random(),
            name = "Espada larga",
            quantity = 1,
            weightLb = 3.0,
            special = true,
            description = "1d8 cortante",
            location = "Mano derecha",
            attuned = false,
            equipped = true,
            notes = "Arma marcial",
            sortOrder = 0,
        )

        assertEquals("Espada larga · 3 lb", item.pdfCompactEquipmentLabel())
        assertEquals(null, item.pdfOrdinaryEquipmentDetailOrNull())
    }
}
