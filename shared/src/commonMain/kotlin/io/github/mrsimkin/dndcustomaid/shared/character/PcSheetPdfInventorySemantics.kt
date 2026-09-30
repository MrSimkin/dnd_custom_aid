package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Compact ordinary-equipment identity used by PDF families.
 *
 * Location, description and notes are intentionally excluded: they are detail semantics and must
 * not make a normal inventory item consume extra Equipment rows merely because metadata exists.
 */
fun CharacterInventoryItem.pdfCompactEquipmentLabel(): String =
    buildString {
        if (quantity > 1) append(quantity).append(" x ")
        append(name)
    }

/**
 * Detail text for an ordinary item.
 *
 * This metadata does not belong in Notes and does not belong in the visible ordinary Equipment
 * identity row. It is retained only as a semantic helper for a future Equipment-specific detail
 * surface if one is explicitly required.
 */
fun CharacterInventoryItem.pdfOrdinaryEquipmentDetailOrNull(): String? {
    if (special) return null

    val descriptionText = description?.trim().orEmpty()
    val notesText = notes?.trim().orEmpty()
    // A bare storage/location tag is useful metadata, but it is not enough by itself to justify
    // allocating a PDF detail/Notes surface. If richer detail exists, preserve location with it.
    if (descriptionText.isEmpty() && notesText.isEmpty()) return null

    val detail = buildList {
        location?.trim()?.takeIf { it.isNotEmpty() }?.let { add("Ubicación: $it") }
        descriptionText.takeIf { it.isNotEmpty() }?.let(::add)
        notesText.takeIf { it.isNotEmpty() }?.let(::add)
    }.joinToString(" · ")

    return "$name — $detail"
}


/**
 * Semantic PDF notes are actual character/campaign Notes only.
 *
 * Ordinary Equipment metadata must not be rerouted into Notes merely to preserve text: semantic
 * association is part of PDF correctness.
 */
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

fun CharacterSheet.pdfOrdinaryEquipmentDetailParagraphs(): List<String> =
    inventoryItems
        .sortedBy { it.sortOrder }
        .filterNot { it.special }
        .mapNotNull { it.pdfOrdinaryEquipmentDetailOrNull() }

fun CharacterSheet.pdfNoteParagraphs(): List<String> =
    pdfCampaignNoteParagraphs()
