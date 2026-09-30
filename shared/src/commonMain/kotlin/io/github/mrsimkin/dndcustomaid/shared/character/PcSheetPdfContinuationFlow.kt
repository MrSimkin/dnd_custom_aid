package io.github.mrsimkin.dndcustomaid.shared.character

data class PcSheetTextContinuationSegment(
    val endpoint: PcSheetContinuationEndpoint,
    val leadingMarker: String? = null,
    val contentLines: List<String>,
    val trailingMarker: String? = null,
) {
    val physicalLines: List<String>
        get() = buildList {
            leadingMarker?.let(::add)
            addAll(contentLines)
            trailingMarker?.let(::add)
        }
}

/**
 * Split already-wrapped semantic text into one normal section plus as many numbered Extended
 * sections as necessary.
 *
 * Navigation markers consume real physical rows. The planner therefore cannot "claim" all writing
 * rows for content and then overprint the continuity cue afterward.
 */
fun planPcSheetBidirectionalTextContinuation(
    record: PcSheetSemanticRecordRef,
    sectionName: String,
    wrappedLines: List<String>,
    normalCapacity: Int,
    extendedCapacity: Int,
    recordLabel: String? = null,
): List<PcSheetTextContinuationSegment> {
    require(sectionName.isNotBlank()) { "Continuation section name is required." }
    require(normalCapacity > 0) { "Normal continuation capacity must be positive." }
    require(extendedCapacity >= 2) {
        "Extended continuation capacity must leave room for navigation plus content."
    }

    if (wrappedLines.isEmpty()) return emptyList()

    val normalEndpoint = PcSheetContinuationEndpoint(
        module = record.module,
        sectionName = sectionName,
        surface = PcSheetContinuationSurface.NORMAL,
        recordLabel = recordLabel,
    )

    if (wrappedLines.size <= normalCapacity) {
        return listOf(
            PcSheetTextContinuationSegment(
                endpoint = normalEndpoint,
                contentLines = wrappedLines,
            ),
        )
    }

    require(normalCapacity >= 2) {
        "Overflowing normal section needs at least one content row and one continuation row."
    }

    val segments = mutableListOf<PcSheetTextContinuationSegment>()
    var remaining = wrappedLines
    var sourceEndpoint = normalEndpoint
    var extendedIndex = 1

    val firstTarget = PcSheetContinuationEndpoint(
        module = record.module,
        sectionName = sectionName,
        surface = PcSheetContinuationSurface.EXTENDED,
        extendedIndex = extendedIndex,
        recordLabel = recordLabel,
    )
    val firstLink = PcSheetBidirectionalContinuation(
        record = record,
        source = normalEndpoint,
        target = firstTarget,
    )

    val normalContentCount = normalCapacity - 1
    segments += PcSheetTextContinuationSegment(
        endpoint = normalEndpoint,
        contentLines = remaining.take(normalContentCount),
        trailingMarker = firstLink.sourceMarker(),
    )
    remaining = remaining.drop(normalContentCount)
    sourceEndpoint = normalEndpoint

    while (remaining.isNotEmpty()) {
        val endpoint = PcSheetContinuationEndpoint(
            module = record.module,
            sectionName = sectionName,
            surface = PcSheetContinuationSurface.EXTENDED,
            extendedIndex = extendedIndex,
            recordLabel = recordLabel,
        )
        val inbound = PcSheetBidirectionalContinuation(
            record = record,
            source = sourceEndpoint,
            target = endpoint,
        )

        val contentCapacityIfFinal = extendedCapacity - 1 // inbound marker only
        val isFinal = remaining.size <= contentCapacityIfFinal

        if (isFinal) {
            segments += PcSheetTextContinuationSegment(
                endpoint = endpoint,
                leadingMarker = inbound.targetMarker(),
                contentLines = remaining,
            )
            remaining = emptyList()
        } else {
            require(extendedCapacity >= 3) {
                "Multi-page Extended continuation needs inbound + content + outbound rows."
            }
            val nextEndpoint = PcSheetContinuationEndpoint(
                module = record.module,
                sectionName = sectionName,
                surface = PcSheetContinuationSurface.EXTENDED,
                extendedIndex = extendedIndex + 1,
                recordLabel = recordLabel,
            )
            val outbound = PcSheetBidirectionalContinuation(
                record = record,
                source = endpoint,
                target = nextEndpoint,
            )
            val contentCount = extendedCapacity - 2
            segments += PcSheetTextContinuationSegment(
                endpoint = endpoint,
                leadingMarker = inbound.targetMarker(),
                contentLines = remaining.take(contentCount),
                trailingMarker = outbound.sourceMarker(),
            )
            remaining = remaining.drop(contentCount)
        }

        sourceEndpoint = endpoint
        extendedIndex += 1
    }

    return segments
}
