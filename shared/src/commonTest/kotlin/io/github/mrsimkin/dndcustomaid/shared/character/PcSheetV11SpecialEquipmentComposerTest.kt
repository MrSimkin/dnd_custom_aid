package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class PcSheetV11SpecialEquipmentComposerTest {
    private val width = PcSheetV11TextWidth { it.length * 4f }
    private val area = PcSheetV11RectPt(40f, 90f, 510f, 300f)
    private val ringId = Uuid.parse("70000000-0000-0000-0000-000000000001")
    private val staffId = Uuid.parse("70000000-0000-0000-0000-000000000002")

    private fun item(
        id: Uuid,
        name: String,
        description: String? = null,
    ): PcSheetV11Equipment = PcSheetV11Equipment(
        sourceId = id,
        name = name,
        quantity = 1,
        location = "Bolsa",
        description = description,
        notes = "Anotación íntegra.",
        special = true,
        active = null,
    )

    private fun place(items: List<PcSheetV11Equipment>): PcSheetV11SpecialEquipmentPlaced =
        PcSheetV11SpecialEquipmentComposer.place(
            equipment = items,
            areaPt = area,
            activeMarkWidthPt = 12f,
            activeMarkGapPt = 6f,
            lineHeightPt = 12f,
            measuredWidth = width,
        )

    @Test
    fun separateFullWidthEntriesPreserveEveryFieldAndUnknownActivoMark() {
        val ring = item(ringId, "Anillo desconocido", "Protección no verificada")
        val staff = item(staffId, "Bastón en tránsito", "Sin portador")
        val output = place(listOf(ring, staff))
        assertEquals(1, output.body.columnsPt.size)
        assertEquals(listOf(ringId, staffId), output.inputIds)
        assertEquals(2, output.activeMarks.size)
        assertTrue(output.activeMarks.all { it.active == null })
        assertTrue(output.activeMarks.all {
            it.rectPt.leftPt > output.body.columnsPt.single().rightPt
        })
        val keys = output.sources.map { it.sourceKey }
        assertEquals(8, keys.size) // Name, location, description, notes for each.
        output.sources.forEach { source ->
            val actual = output.body.cells.filter { it.sourceKey == source.sourceKey }
                .joinToString("") { it.sourceText }
            assertEquals(source.text, actual)
        }
        output.verify()
    }

    @Test
    fun aHugeDescriptionCanCrossFullWidthPagesWithoutDuplicatingActivo() {
        val description = ("Mapa de fracturas — detalle extenso. ").repeat(180)
        val input = item(ringId, "Anillo de siete umbrales", description)
        val result = PcSheetV11SpecialEquipmentComposer.place(
            listOf(input),
            PcSheetV11RectPt(40f, 90f, 240f, 96f),
            12f, 6f, 12f, width,
        )
        assertTrue(result.localPageCount >= 2)
        assertEquals(1, result.activeMarks.size)
        assertNull(result.activeMarks.single().active)
        assertEquals(description, result.body.cells
            .filter { it.sourceKey.endsWith(":description") }
            .joinToString("") { it.sourceText })
        result.verify()
    }

    @Test
    fun missingAndMismatchedSourcesFailWithoutFabricatingSpecialObjects() {
        val empty = place(emptyList())
        assertEquals(0, empty.localPageCount)
        assertTrue(empty.body.cells.isEmpty() && empty.activeMarks.isEmpty())
        assertFailsWith<IllegalArgumentException> {
            place(listOf(item(ringId, "Anillo"), item(ringId, "Otro anillo")))
        }
        assertFailsWith<IllegalArgumentException> {
            place(listOf(item(ringId, " ")))
        }
        assertFailsWith<IllegalArgumentException> {
            val ordinary = item(ringId, "Ordinario").copy(special = false)
            place(listOf(ordinary))
        }
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11SpecialEquipmentComposer.place(
                listOf(item(ringId, "Anillo")),
                area, 1000f, 6f, 12f, width,
            )
        }
    }
}
