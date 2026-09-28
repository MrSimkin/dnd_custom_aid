package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Compact ordinary-equipment identity used by every PDF family.
 *
 * Owner 50800 contract: ordinary Equipment is the existing sheet's compact Equipment list, not a
 * metadata/detail surface. Quantity belongs to the identity; weight, usage state, location,
 * description and notes do not render as ordinary Equipment PDF content.
 */
fun CharacterInventoryItem.pdfCompactEquipmentLabel(): String =
    buildString {
        if (quantity > 1) append(quantity).append(" x ")
        append(name)
    }

/**
 * Ordinary Equipment prose/detail metadata is deliberately not projected into the PDF.
 *
 * The character data remains intact in the model; this only defines the player-facing sheet
 * projection. Special Equipment keeps its own dedicated native module semantics.
 */
fun CharacterInventoryItem.pdfOrdinaryEquipmentDetailOrNull(): String? = null

/** Campaign Notes are independent from ordinary Equipment metadata. */
fun CharacterSheet.pdfCampaignNoteParagraphs(): List<String> = buildList {
    generalNotes.trim().takeIf { it.isNotEmpty() }?.let(::add)
    noteCards.sortedBy { it.sortOrder }.forEach { card ->
        val title = card.title.trim()
        val body = card.content.trim()
        when {
            title.isNotEmpty() && body.isNotEmpty() -> add("$title: $body")
            title.isNotEmpty() -> add(title)
            body.isNotEmpty() -> add(body)
        }
    }
}

fun CharacterSheet.pdfOrdinaryEquipmentDetailParagraphs(): List<String> = emptyList()

fun CharacterSheet.pdfNoteParagraphs(): List<String> = pdfCampaignNoteParagraphs()
