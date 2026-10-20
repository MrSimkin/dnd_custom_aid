package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * G2 page-order contract. This is a logical page sequence, NOT generated PDF geometry.
 * The physical composer must first measure content and supply honest continuation counts.
 * No continuation page is fabricated simply to fill empty space.
 */
enum class PcSheetV11PageRole {
    CHARACTER_P1,
    INVENTORY_P2,
    NARRATIVE_P3,
    QUICK_SPELLS_P4,
    CONTINUATION_C2,
    DIGITAL_NOTES_CN3,
    SPELLBOOK,
    FINAL_HANDWRITTEN_NOTES,
}

data class PcSheetV11OrderedPage(
    /** Final 1-based sequence number once measured page counts have been supplied. */
    val pageNumber: Int,
    val role: PcSheetV11PageRole,
    /** 1-based index of this role when its role occupies multiple pages. */
    val rolePageIndex: Int,
)

data class PcSheetV11PageSequence(
    val pages: List<PcSheetV11OrderedPage>,
) {
    init {
        require(pages.isNotEmpty())
        require(pages.map { it.pageNumber } == (1..pages.size).toList()) {
            "Physical page references must be contiguous and 1-based."
        }
        require(pages.first().role == PcSheetV11PageRole.CHARACTER_P1)
        require(pages.last().role == PcSheetV11PageRole.FINAL_HANDWRITTEN_NOTES)
        require(pages.count { it.role == PcSheetV11PageRole.FINAL_HANDWRITTEN_NOTES } == 1)
    }

    fun pagesFor(role: PcSheetV11PageRole): List<PcSheetV11OrderedPage> =
        pages.filter { it.role == role }

    fun page(role: PcSheetV11PageRole, localIndex: Int = 1): Int? =
        pages.firstOrNull { it.role == role && it.rolePageIndex == localIndex }?.pageNumber
}

/** Counts come from measured page composition, not guessed from source item counts. */
data class PcSheetV11PageRequirements(
    val continuationPages: Int = 0,
    val digitalNotePages: Int = 0,
    val spellbookPages: Int = 0,
) {
    init {
        require(continuationPages >= 0 && digitalNotePages >= 0 && spellbookPages >= 0) {
            "Page counts cannot be negative."
        }
    }
}

object PcSheetV11PageSequenceBuilder {
    fun build(
        data: PcSheetV11SemanticData,
        requirements: PcSheetV11PageRequirements,
    ): PcSheetV11PageSequence {
        val hasDigitalNotes = data.digitalNotes.isNotEmpty()
        require((requirements.digitalNotePages > 0) == hasDigitalNotes) {
            "C-N3 exists exactly when genuine digital notes need dedicated pages."
        }
        val hasBook = data.spellbookEntries != null
        require((requirements.spellbookPages > 0) == hasBook) {
            "The spellbook must be both requested and backed by real spells."
        }
        require(!hasBook || data.spells.isNotEmpty())

        val roles = buildList {
            add(PcSheetV11PageRole.CHARACTER_P1)
            add(PcSheetV11PageRole.INVENTORY_P2)
            add(PcSheetV11PageRole.NARRATIVE_P3)
            // Approved V11 extreme A/B: C2 continuation immediately after P3,
            // before the optional quick-spell P4. Never invent C2 pages.
            repeat(requirements.continuationPages) { add(PcSheetV11PageRole.CONTINUATION_C2) }
            if (data.hasQuickSpellList) add(PcSheetV11PageRole.QUICK_SPELLS_P4)
            repeat(requirements.digitalNotePages) { add(PcSheetV11PageRole.DIGITAL_NOTES_CN3) }
            repeat(requirements.spellbookPages) { add(PcSheetV11PageRole.SPELLBOOK) }
            add(PcSheetV11PageRole.FINAL_HANDWRITTEN_NOTES)
        }
        val indices = mutableMapOf<PcSheetV11PageRole, Int>()
        val sequence = roles.mapIndexed { index, role ->
            val roleIndex = (indices[role] ?: 0) + 1
            indices[role] = roleIndex
            PcSheetV11OrderedPage(index + 1, role, roleIndex)
        }
        return PcSheetV11PageSequence(sequence)
    }
}
