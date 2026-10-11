package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Assign real multiline payloads to their V11-only logical modules before measuring.
 * This is NOT the final PDF planner: renderers still need to handle item labels,
 * table rows, handwriting space and source-native artwork at their approved locations.
 *
 * The arrays deliberately exclude mechanical traits from NARRATIVE, preserving P3's
 * owner-approved prohibition against mechanically formatted trait sections.
 */
data class PcSheetV11FlowSources(
    val narrative: List<PcSheetV11FlowItem>,
    val digitalNotes: List<PcSheetV11FlowItem>,
    val traitDescriptions: List<PcSheetV11FlowItem>,
    val specialEquipmentDescriptions: List<PcSheetV11FlowItem>,
    val spellbookDescriptions: List<PcSheetV11FlowItem>,
) {
    fun forSection(section: PcSheetV11FlowSection): List<PcSheetV11FlowItem> = when (section) {
        PcSheetV11FlowSection.NARRATIVE -> narrative
        PcSheetV11FlowSection.DIGITAL_NOTES -> digitalNotes
        PcSheetV11FlowSection.TRAITS -> traitDescriptions
        PcSheetV11FlowSection.SPECIAL_EQUIPMENT -> specialEquipmentDescriptions
        PcSheetV11FlowSection.SPELLBOOK -> spellbookDescriptions
        // These require real row/table composition, not generic paragraph flow.
        PcSheetV11FlowSection.ORDINARY_EQUIPMENT,
        PcSheetV11FlowSection.QUICK_SPELLS,
        -> emptyList()
    }
}

object PcSheetV11FlowSourceMapper {
    fun from(data: PcSheetV11SemanticData): PcSheetV11FlowSources {
        val background = data.background
        val narrativeFields = listOf(
            "background-name" to background.name,
            "background-summary" to background.summary,
            "background-faith" to background.religionFaith,
            "personality" to background.personalityTraits,
            "ideals" to background.ideals,
            "bonds" to background.bonds,
            "flaws" to background.flaws,
            "story" to background.story,
        )
        return PcSheetV11FlowSources(
            narrative = narrativeFields.filter { (_, text) -> text.isNotBlank() }
                .map { (field, text) ->
                    PcSheetV11FlowItem(sourceKey = "narrative:$field", text = text)
                },
            digitalNotes = data.digitalNotes.map { note ->
                val key = note.sourceId?.toString() ?: "general"
                val title = note.title?.takeIf { it.isNotBlank() }
                // The heading remains traceable in semantic data; the newline simply provides
                // a paper-native boundary between the titled card and its unaltered body.
                val display = if (title != null && note.content.isNotEmpty()) {
                    "$title\n${note.content}"
                } else {
                    title ?: note.content
                }
                PcSheetV11FlowItem(
                    sourceKey = "digital-note:$key",
                    text = display,
                    keepTogetherIfFits = true,
                )
            },
            traitDescriptions = data.traits.filter { it.description.isNotBlank() }.map { trait ->
                PcSheetV11FlowItem(
                    sourceKey = "trait:${trait.id}",
                    text = trait.description,
                    keepTogetherIfFits = true,
                )
            },
            specialEquipmentDescriptions = data.specialEquipment
                .filter { !it.description.isNullOrBlank() }
                .map { item ->
                    PcSheetV11FlowItem(
                        sourceKey = "special-equipment:${item.sourceId}",
                        text = checkNotNull(item.description),
                        keepTogetherIfFits = true,
                    )
                },
            spellbookDescriptions = data.spellbookEntries.orEmpty().map { entry ->
                PcSheetV11FlowItem(
                    sourceKey = "spellbook:${entry.spell.id}",
                    text = entry.spell.description,
                    keepTogetherIfFits = true,
                )
            },
        )
    }
}
