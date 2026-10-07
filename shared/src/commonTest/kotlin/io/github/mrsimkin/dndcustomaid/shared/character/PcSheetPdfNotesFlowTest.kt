package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class PcSheetPdfNotesFlowTest {
    @Test
    fun noteRecordsExcludeBackgroundNarrativeAndNormalizeCardIdentity() {
        val sheet = minimalSheet().copy(
            generalNotes = "Nota general real.",
            background = CharacterBackground(
                story = "HISTORIA NO ES NOTAS",
                personalityTraits = "PERSONALIDAD NO ES NOTAS",
                religionFaith = "RELIGIÓN NO ES NOTAS",
            ),
            noteCards = listOf(
                CharacterNote(
                    id = Uuid.random(),
                    title = "Hipótesis",
                    content = "Contenido uno.",
                    sortOrder = 1,
                ),
                CharacterNote(
                    id = Uuid.random(),
                    title = "Nota 8 — Lugar",
                    content = "Contenido ocho.",
                    sortOrder = 2,
                ),
            ),
        )

        val records = sheet.pcSheetNoteRecords()

        assertEquals(
            listOf("Notas generales", "Nota 1 — Hipótesis", "Nota 8 — Lugar"),
            records.map { it.heading },
        )
        val combined = records.joinToString(" ") { it.heading + " " + it.body }
        assertTrue(!combined.contains("HISTORIA NO ES NOTAS"))
        assertTrue(!combined.contains("PERSONALIDAD NO ES NOTAS"))
        assertTrue(!combined.contains("RELIGIÓN NO ES NOTAS"))
    }

    @Test
    fun wholeNoteMovesToFreshColumnInsteadOfSplittingToFillRemainder() {
        val first = wrapped("note-1", "Nota 1 — A", bodyLines = 4)
        val second = wrapped("note-2", "Nota 2 — B", bodyLines = 3)

        val packed = packPcSheetNoteColumns(
            records = listOf(first, second),
            rowsPerColumn = 6,
            columnsPerPage = 2,
        )

        assertEquals(2, packed.columns.size)
        assertTrue(packed.columns[0].any { it.record.ref.stableKey == "note-1" })
        assertTrue(packed.columns[0].none { it.record.ref.stableKey == "note-2" })
        assertTrue(packed.columns[1].any { it.record.ref.stableKey == "note-2" })
    }

    @Test
    fun noteThatFitsRemainderDoesNotMoveSolelyForOptionalSeparator() {
        val first = wrapped("note-1", "Nota 1 — A", bodyLines = 2)
        val second = wrapped("note-2", "Nota 2 — B", bodyLines = 1)

        val packed = packPcSheetNoteColumns(
            records = listOf(first, second),
            rowsPerColumn = 6,
            columnsPerPage = 2,
        )

        assertEquals(1, packed.columns.size)
        assertTrue(packed.columns.single().any { it.record.ref.stableKey == "note-1" })
        assertTrue(packed.columns.single().any { it.record.ref.stableKey == "note-2" })
        assertEquals(
            6,
            packed.columns.single().flatMap { it.lines }.size,
            "The second note must use the exact remaining rows without demanding an optional separator.",
        )
    }

    @Test
    fun wholeRecordColumnAdvanceTraceProvesTheRecordDidNotFitTheRemainder() {
        val first = wrapped("note-1", "Nota 1 — A", bodyLines = 2)
        val second = wrapped("note-2", "Nota 2 — B", bodyLines = 2)

        val packed = packPcSheetNoteColumns(
            records = listOf(first, second),
            rowsPerColumn = 6,
            columnsPerPage = 2,
        )

        val advance = packed.columnAdvances.single()
        assertEquals(
            PcSheetNoteColumnAdvanceReason.WHOLE_RECORD_DOES_NOT_FIT_REMAINDER,
            advance.reason,
        )
        assertEquals(2, advance.remainingRowsBeforeAdvance)
        assertEquals(3, advance.atomicRowsRequired)
        assertTrue(!advance.atomicUnitFitsRemainder)
        assertEquals(0, advance.from.extendedPageIndex)
        assertEquals(0, advance.to.extendedPageIndex)
        assertEquals(1, advance.from.columnIndex)
        assertEquals(2, advance.to.columnIndex)
    }

    @Test
    fun oversizedNoteSplitsWithBidirectionalColumnAwareNavigation() {
        val large = wrapped(
            stableKey = "note-8",
            heading = "Nota 8 — Lugar",
            bodyLines = 9,
        )

        val packed = packPcSheetNoteColumns(
            records = listOf(large),
            rowsPerColumn = 6,
            columnsPerPage = 2,
        )

        val segments = packed.columns.flatten()
            .filter { segment -> segment.lines.any { it.kind != PcSheetNotePhysicalLineKind.SEPARATOR } }

        assertTrue(segments.size >= 2)
        val firstLines = segments.first().lines.map { it.text }
        val secondLines = segments[1].lines.map { it.text }

        assertTrue(
            firstLines.any {
                it.contains("[continúa en sección normal NOTAS · columna 2 / Nota 8 — Lugar]")
            },
        )
        assertTrue(
            secondLines.any {
                it.contains("[proviene de sección normal NOTAS · columna 1 / Nota 8 — Lugar]")
            },
        )
    }

    @Test
    fun notesConsumeBothNativeColumnsBeforeOpeningExtendedPage() {
        val records = (1..4).map { index ->
            wrapped(
                stableKey = "note-$index",
                heading = "Nota $index",
                bodyLines = 1,
            )
        }

        val packed = packPcSheetNoteColumns(
            records = records,
            rowsPerColumn = 4,
            columnsPerPage = 2,
        )

        val firstExtended = packed.columns.flatten()
            .firstOrNull { it.address.extendedPageIndex > 0 }
        assertTrue(firstExtended == null || packed.columns.take(2).all { it.isNotEmpty() })
    }

    private fun wrapped(
        stableKey: String,
        heading: String,
        bodyLines: Int,
    ): PcSheetWrappedNoteRecord {
        val record = PcSheetNoteRecord(
            ref = PcSheetSemanticRecordRef(
                module = PcSheetSemanticModule.NOTES,
                stableKey = stableKey,
                displayName = heading,
            ),
            kind = PcSheetNoteKind.CARD,
            heading = heading,
            body = (1..bodyLines).joinToString(" ") { "línea$it" },
        )
        return PcSheetWrappedNoteRecord(
            record = record,
            headingLines = listOf(heading),
            bodyLines = (1..bodyLines).map { "cuerpo $it" },
        )
    }

    private fun minimalSheet(): CharacterSheet {
        val id = Uuid.random()
        return CharacterSheet(
            id = id,
            campaignId = Uuid.random(),
            name = "QA",
            status = CharacterStatus.ACTIVE,
            updatedAtEpochSeconds = 0,
            strength = 10,
            dexterity = 10,
            constitution = 10,
            intelligence = 10,
            wisdom = 10,
            charisma = 10,
            armorClass = 10,
            maxHp = 1,
            currentHp = 1,
            tempHp = 0,
            initiativeAdjustment = 0,
            speed = 30,
            proficiencyBonus = 2,
            savingThrows = CharacterAbility.entries.map {
                CharacterSavingThrow(it, proficient = false, adjustment = 0)
            },
            passivePerceptionAdjustment = 0,
            spellSaveDc = null,
            classes = emptyList(),
            skills = SkillKey.entries.map {
                CharacterSkill(it, adjustment = 0, training = SkillTraining.NONE)
            },
        )
    }
}
