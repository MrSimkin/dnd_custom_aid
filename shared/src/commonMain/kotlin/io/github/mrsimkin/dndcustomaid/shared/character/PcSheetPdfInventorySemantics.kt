package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Compact ordinary-equipment identity used by PDF families.
 *
 * Location, description and notes are intentionally excluded: they are detail semantics and must
 * not make a normal inventory item consume extra Equipment rows merely because metadata exists.
 */
fun CharacterInventoryItem.pdfCompactEquipmentLabel(): String =
    buildList {
        add(
            buildString {
                if (quantity > 1) append(quantity).append(" x ")
                append(name)
            },
        )
        weightLb?.let { weight ->
            add(
                if (weight % 1.0 == 0.0) {
                    weight.toInt().toString() + " lb"
                } else {
                    weight.toString() + " lb"
                },
            )
        }
    }.joinToString(" · ")

/**
 * Detail text for an ordinary item. This belongs in a details/notes semantic destination, not the
 * ordinary Equipment identity row.
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
 * Semantic PDF notes: campaign notes plus ordinary-item metadata that does not belong on an
 * Equipment identity row. Special equipment keeps its dedicated location/detail surface.
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
    pdfCampaignNoteParagraphs() + pdfOrdinaryEquipmentDetailParagraphs()
