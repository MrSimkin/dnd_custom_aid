package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PcSheetPdfContinuationFlowTest {
    private val history = PcSheetSemanticRecordRef(
        module = PcSheetSemanticModule.BACKGROUND_STORY,
        stableKey = "history",
        displayName = "Historia",
    )

    @Test
    fun contentThatFitsNormalSectionNeedsNoNavigation() {
        val segments = planPcSheetBidirectionalTextContinuation(
            record = history,
            sectionName = "Historia",
            wrappedLines = listOf("uno", "dos"),
            normalCapacity = 3,
            extendedCapacity = 5,
        )

        assertEquals(1, segments.size)
        assertEquals(listOf("uno", "dos"), segments.single().contentLines)
        assertNull(segments.single().leadingMarker)
        assertNull(segments.single().trailingMarker)
    }

    @Test
    fun firstOverflowUsesOwnerDefinedNormalToExtendedMarkers() {
        val segments = planPcSheetBidirectionalTextContinuation(
            record = history,
            sectionName = "Historia",
            wrappedLines = listOf("uno", "dos", "tres", "cuatro"),
            normalCapacity = 3,
            extendedCapacity = 5,
        )

        assertEquals(2, segments.size)
        assertEquals(listOf("uno", "dos"), segments[0].contentLines)
        assertEquals(
            "[continúa en sección extendida HISTORIA 01]",
            segments[0].trailingMarker,
        )
        assertEquals(
            "[proviene de sección normal HISTORIA]",
            segments[1].leadingMarker,
        )
        assertEquals(listOf("tres", "cuatro"), segments[1].contentLines)
    }

    @Test
    fun multipleExtendedSegmentsNavigateForwardAndBackward() {
        val segments = planPcSheetBidirectionalTextContinuation(
            record = history,
            sectionName = "Historia",
            wrappedLines = (1..10).map { "línea $it" },
            normalCapacity = 3,
            extendedCapacity = 4,
        )

        assertEquals(5, segments.size)

        assertEquals(
            "[continúa en sección extendida HISTORIA 01]",
            segments[0].trailingMarker,
        )

        assertEquals(
            "[proviene de sección normal HISTORIA]",
            segments[1].leadingMarker,
        )
        assertEquals(
            "[continúa en sección extendida HISTORIA 02]",
            segments[1].trailingMarker,
        )

        assertEquals(
            "[proviene de sección extendida HISTORIA 01]",
            segments[2].leadingMarker,
        )
        assertEquals(
            "[continúa en sección extendida HISTORIA 03]",
            segments[2].trailingMarker,
        )

        assertEquals(
            "[proviene de sección extendida HISTORIA 02]",
            segments[3].leadingMarker,
        )
        assertEquals(
            "[continúa en sección extendida HISTORIA 04]",
            segments[3].trailingMarker,
        )

        assertEquals(
            "[proviene de sección extendida HISTORIA 03]",
            segments[4].leadingMarker,
        )
        assertNull(segments[4].trailingMarker)
        assertEquals((1..10).map { "línea $it" }, segments.flatMap { it.contentLines })
    }

    @Test
    fun noteIdentityTravelsWithBothNavigationDirections() {
        val note = PcSheetSemanticRecordRef(
            module = PcSheetSemanticModule.NOTES,
            stableKey = "note-8",
            displayName = "Nota 8",
        )

        val segments = planPcSheetBidirectionalTextContinuation(
            record = note,
            sectionName = "Notas",
            wrappedLines = listOf("a", "b", "c", "d"),
            normalCapacity = 3,
            extendedCapacity = 5,
            recordLabel = "Nota 8",
        )

        assertEquals(
            "[continúa en sección extendida NOTAS 01 / Nota 8]",
            segments[0].trailingMarker,
        )
        assertEquals(
            "[proviene de sección normal NOTAS / Nota 8]",
            segments[1].leadingMarker,
        )
    }
}
