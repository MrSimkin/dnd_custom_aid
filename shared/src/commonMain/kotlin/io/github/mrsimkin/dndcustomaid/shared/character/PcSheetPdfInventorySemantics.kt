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

    val detail = buildList {
        location?.trim()?.takeIf { it.isNotEmpty() }?.let { add("Ubicación: $it") }
        description?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
        notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
    }.joinToString(" · ")

    return detail.takeIf { it.isNotEmpty() }?.let { "$name — $it" }
}
