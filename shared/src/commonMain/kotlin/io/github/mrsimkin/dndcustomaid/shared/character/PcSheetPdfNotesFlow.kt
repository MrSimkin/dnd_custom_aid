package io.github.mrsimkin.dndcustomaid.shared.character

enum class PcSheetNoteKind {
    GENERAL,
    CARD,
}

data class PcSheetNoteRecord(
    val ref: PcSheetSemanticRecordRef,
    val kind: PcSheetNoteKind,
    val heading: String,
    val body: String,
) {
    init {
        require(ref.module == PcSheetSemanticModule.NOTES) {
            "PC-sheet note records must belong to the Notes semantic module."
        }
        require(heading.isNotBlank()) { "PC-sheet note heading is required." }
        require(body.isNotBlank()) { "PC-sheet note body is required." }
    }
}

/**
 * Notes-only semantic source.
 *
 * Background/personality/history content is intentionally excluded. Those values belong to their
 * own native narrative module and must never be preserved by silently rerouting them into Notes.
 */
fun CharacterSheet.pcSheetNoteRecords(): List<PcSheetNoteRecord> = buildList {
    generalNotes.trim().takeIf { it.isNotEmpty() }?.let { body ->
        add(
            PcSheetNoteRecord(
                ref = PcSheetSemanticRecordRef(
                    module = PcSheetSemanticModule.NOTES,
                    stableKey = "general-notes",
                    displayName = "Notas generales",
                ),
                kind = PcSheetNoteKind.GENERAL,
                heading = "Notas generales",
                body = body,
            ),
        )
    }

    noteCards
        .sortedBy { it.sortOrder }
        .forEachIndexed { index, note ->
            val rawTitle = note.title.trim()
            val body = note.content.trim()
            if (body.isEmpty() && rawTitle.isEmpty()) return@forEachIndexed

            val ordinal = index + 1
            val heading = normalizedNoteHeading(rawTitle, ordinal)
            val semanticBody = body.ifEmpty { rawTitle }
            add(
                PcSheetNoteRecord(
                    ref = PcSheetSemanticRecordRef(
                        module = PcSheetSemanticModule.NOTES,
                        stableKey = note.id.toString(),
                        displayName = heading,
                    ),
                    kind = PcSheetNoteKind.CARD,
                    heading = heading,
                    body = semanticBody,
                ),
            )
        }
}

private val NOTE_HEADING_PREFIX = Regex(
    pattern = """^nota\s+\d+\b""",
    option = RegexOption.IGNORE_CASE,
)

private fun normalizedNoteHeading(
    rawTitle: String,
    ordinal: Int,
): String {
    val clean = rawTitle.trim()
    if (clean.isEmpty()) return "Nota $ordinal"
    return if (NOTE_HEADING_PREFIX.containsMatchIn(clean)) {
        clean
    } else {
        "Nota $ordinal — $clean"
    }
}

enum class PcSheetNotePhysicalLineKind {
    HEADING,
    BODY,
    CONTINUITY,
    SEPARATOR,
}

data class PcSheetNotePhysicalLine(
    val kind: PcSheetNotePhysicalLineKind,
    val text: String,
)

data class PcSheetWrappedNoteRecord(
    val record: PcSheetNoteRecord,
    val headingLines: List<String>,
    val bodyLines: List<String>,
) {
    init {
        require(headingLines.isNotEmpty()) { "Wrapped note heading requires at least one line." }
        require(bodyLines.isNotEmpty()) { "Wrapped note body requires at least one line." }
    }
}

data class PcSheetNoteColumnAddress(
    /**
     * Zero means the base/native Notes page. One and above are numbered Extended Notes pages.
     */
    val extendedPageIndex: Int,
    val columnIndex: Int,
) {
    init {
        require(extendedPageIndex >= 0) { "Notes page index cannot be negative." }
        require(columnIndex > 0) { "Notes column index is one-based." }
    }

    fun endpoint(
        record: PcSheetSemanticRecordRef,
    ): PcSheetContinuationEndpoint =
        PcSheetContinuationEndpoint(
            module = PcSheetSemanticModule.NOTES,
            sectionName = "Notas",
            surface = if (extendedPageIndex == 0) {
                PcSheetContinuationSurface.NORMAL
            } else {
                PcSheetContinuationSurface.EXTENDED
            },
            extendedIndex = extendedPageIndex.takeIf { it > 0 },
            recordLabel = record.displayName,
            columnIndex = columnIndex,
        )
}

data class PcSheetPackedNoteSegment(
    val record: PcSheetNoteRecord,
    val address: PcSheetNoteColumnAddress,
    val lines: List<PcSheetNotePhysicalLine>,
    val firstSegment: Boolean,
    val lastSegment: Boolean,
)

data class PcSheetPackedNotes(
    val columns: List<List<PcSheetPackedNoteSegment>>,
) {
    val extendedPageCount: Int
        get() = columns
            .flatMap { it }
            .maxOfOrNull { it.address.extendedPageIndex }
            ?: 0
}

/**
 * Pack already-wrapped note records through native Notes columns.
 *
 * Whole records move to a fresh column when they fit there. A record is split only when it cannot
 * fit in an otherwise-empty native column. Navigation consumes real rows. A blank separator row is
 * retained between complete records whenever capacity allows.
 */
fun packPcSheetNoteColumns(
    records: List<PcSheetWrappedNoteRecord>,
    rowsPerColumn: Int,
    columnsPerPage: Int = 2,
): PcSheetPackedNotes {
    require(rowsPerColumn >= 4) {
        "Native Notes column must have room for heading, body and continuity."
    }
    require(columnsPerPage > 0) { "Native Notes page must contain at least one column." }

    if (records.isEmpty()) return PcSheetPackedNotes(emptyList())

    val columns = mutableListOf<MutableList<PcSheetPackedNoteSegment>>()
    var columnIndex = 0
    var usedRows = 0

    fun ensureColumn(index: Int) {
        while (columns.size <= index) columns.add(mutableListOf())
    }

    fun address(index: Int): PcSheetNoteColumnAddress =
        PcSheetNoteColumnAddress(
            extendedPageIndex = index / columnsPerPage,
            columnIndex = index % columnsPerPage + 1,
        )

    fun remainingRows(): Int = rowsPerColumn - usedRows

    fun advanceColumn() {
        columnIndex += 1
        usedRows = 0
        ensureColumn(columnIndex)
    }

    ensureColumn(0)

    records.forEach { wrapped ->
        val fullLines = buildList {
            wrapped.headingLines.forEach {
                add(PcSheetNotePhysicalLine(PcSheetNotePhysicalLineKind.HEADING, it))
            }
            wrapped.bodyLines.forEach {
                add(PcSheetNotePhysicalLine(PcSheetNotePhysicalLineKind.BODY, it))
            }
        }

        if (
            usedRows > 0 &&
            fullLines.size <= rowsPerColumn &&
            fullLines.size > remainingRows()
        ) {
            advanceColumn()
        }

        if (fullLines.size <= remainingRows()) {
            val separatorFits = fullLines.size + 1 <= remainingRows()
            val lines = if (separatorFits) {
                fullLines + PcSheetNotePhysicalLine(
                    PcSheetNotePhysicalLineKind.SEPARATOR,
                    "",
                )
            } else {
                fullLines
            }
            columns[columnIndex] += PcSheetPackedNoteSegment(
                record = wrapped.record,
                address = address(columnIndex),
                lines = lines,
                firstSegment = true,
                lastSegment = true,
            )
            usedRows += lines.size
            return@forEach
        }

        // Oversized note: split only because the record itself cannot fit a fresh native column.
        if (usedRows > 0) advanceColumn()

        var remaining = fullLines
        var segmentIndex = 0
        var previousAddress: PcSheetNoteColumnAddress? = null

        while (remaining.isNotEmpty()) {
            val currentAddress = address(columnIndex)
            val inboundRows = if (segmentIndex == 0) 0 else 1
            val minimumOutboundRows = if (remaining.size > rowsPerColumn - inboundRows) 1 else 0
            val contentCapacity = rowsPerColumn - inboundRows - minimumOutboundRows
            require(contentCapacity > 0) {
                "Native Notes column leaves no room for semantic content."
            }

            val content = remaining.take(contentCapacity)
            remaining = remaining.drop(content.size)
            val hasMore = remaining.isNotEmpty()

            val currentEndpoint = currentAddress.endpoint(wrapped.record.ref)
            val inbound = previousAddress?.let { previous ->
                PcSheetBidirectionalContinuation(
                    record = wrapped.record.ref,
                    source = previous.endpoint(wrapped.record.ref),
                    target = currentEndpoint,
                ).targetMarker()
            }
            val outbound = if (hasMore) {
                val nextAddress = PcSheetNoteColumnAddress(
                    extendedPageIndex = (columnIndex + 1) / columnsPerPage,
                    columnIndex = (columnIndex + 1) % columnsPerPage + 1,
                )
                PcSheetBidirectionalContinuation(
                    record = wrapped.record.ref,
                    source = currentEndpoint,
                    target = nextAddress.endpoint(wrapped.record.ref),
                ).sourceMarker()
            } else {
                null
            }

            val lines = buildList {
                inbound?.let {
                    add(PcSheetNotePhysicalLine(PcSheetNotePhysicalLineKind.CONTINUITY, it))
                }
                addAll(content)
                outbound?.let {
                    add(PcSheetNotePhysicalLine(PcSheetNotePhysicalLineKind.CONTINUITY, it))
                }
            }

            columns[columnIndex] += PcSheetPackedNoteSegment(
                record = wrapped.record,
                address = currentAddress,
                lines = lines,
                firstSegment = segmentIndex == 0,
                lastSegment = !hasMore,
            )
            usedRows = lines.size

            if (hasMore) {
                previousAddress = currentAddress
                advanceColumn()
                segmentIndex += 1
            }
        }

        if (usedRows < rowsPerColumn) {
            // Preserve one visible row boundary before the next note when practical.
            columns[columnIndex] += PcSheetPackedNoteSegment(
                record = wrapped.record,
                address = address(columnIndex),
                lines = listOf(
                    PcSheetNotePhysicalLine(
                        PcSheetNotePhysicalLineKind.SEPARATOR,
                        "",
                    ),
                ),
                firstSegment = false,
                lastSegment = true,
            )
            usedRows += 1
        }
    }

    return PcSheetPackedNotes(columns.map { it.toList() })
}
