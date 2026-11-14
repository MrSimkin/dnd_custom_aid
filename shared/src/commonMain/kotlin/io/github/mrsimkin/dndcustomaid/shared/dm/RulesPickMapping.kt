package io.github.mrsimkin.dndcustomaid.shared.dm

/*
 * Redesign R5 (decision D9): what a pick from the SRD or the homebrew fills in the PC editor.
 * Pure functions, so the tablet's editor and the tests read the same mapping.
 */

/** The fields of «Añadir conjuro», filled from a picked spell. */
data class SpellPick(
    val name: String,
    val level: Int,
    val school: String,
    val castingTime: String,
    val rangeText: String,
    val verbal: Boolean,
    val somatic: Boolean,
    val material: Boolean,
    val materialText: String,
    val duration: String,
    val concentration: Boolean,
    val ritual: Boolean,
    val description: String,
)

fun RulesOption.toSpellPick(): SpellPick {
    val components = fields["componentes"].orEmpty()
    val materialText = Regex("""M\s*\(([^)]*)\)""").find(components)?.groupValues?.get(1).orEmpty()
    val head = components.substringBefore("(")
    val duration = fields["duracion"].orEmpty()
    val castingTime = fields["tiempo"].orEmpty()
    return SpellPick(
        name = name,
        level = fields["nivel"]?.toIntOrNull()?.coerceIn(0, 9) ?: 0,
        school = fields["escuela"].orEmpty(),
        castingTime = castingTime.replace(Regex(""",?\s*o un ritual$"""), "").ifBlank { castingTime },
        rangeText = fields["alcance"].orEmpty(),
        verbal = Regex("""\bV\b""").containsMatchIn(head),
        somatic = Regex("""\bS\b""").containsMatchIn(head),
        material = Regex("""\bM\b""").containsMatchIn(head),
        materialText = materialText,
        duration = duration.removePrefix("Concentración, ").replaceFirstChar { it.uppercase() },
        concentration = duration.startsWith("Concentración"),
        ritual = fields["ritual"] == "sí" || castingTime.contains("ritual"),
        description = body.ifBlank { summary },
    )
}

/** The fields of «Añadir ataque o acción», filled from a picked weapon. */
data class AttackPick(val name: String, val damageEffect: String, val rangeText: String, val notes: String)

fun RulesOption.toAttackPick(): AttackPick {
    val properties = fields["propiedades"].orEmpty().takeIf { it != "—" }.orEmpty()
    val range = Regex("""alcance ([\d,./]+)""").find(properties)?.groupValues?.get(1)?.let { "$it m" }.orEmpty()
    val notes = listOfNotNull(
        properties.takeIf { it.isNotBlank() },
        fields["maestria"]?.let { "Maestría: $it" },
        "$source",
    ).joinToString(" · ")
    return AttackPick(name = name, damageEffect = fields["dano"].orEmpty().takeIf { it != "—" }.orEmpty(), rangeText = range, notes = notes)
}

/** The fields of «Añadir objeto», filled from a picked weapon, armour or item. */
data class ItemPick(val name: String, val weightLb: Double?, val description: String, val notes: String, val special: Boolean)

fun RulesOption.toItemPick(): ItemPick {
    // The Spanish SRD converts 1 lb to 0.5 kg; the sheet keeps pounds.
    val weightLb = fields["peso"]?.let { weight ->
        val number = Regex("""[\d,]+""").find(weight)?.value?.replace(',', '.')?.toDoubleOrNull()
        when {
            number == null -> null
            weight.trim().endsWith(" g") -> number / 1000.0 * 2.0
            else -> number * 2.0
        }
    }
    val description = listOfNotNull(
        fields["ca"]?.let { "CA $it" },
        fields["dano"]?.takeIf { it.isNotBlank() && it != "—" },
        fields["propiedades"]?.takeIf { it.isNotBlank() && it != "—" },
        fields["fuerza"]?.takeIf { it.isNotBlank() && it != "—" },
        fields["sigilo"]?.takeIf { it == "Desventaja" }?.let { "Sigilo con desventaja" },
    ).joinToString(" · ").ifBlank { summary }
    val notes = listOfNotNull(fields["precio"]?.let { "Precio: $it" }, source).joinToString(" · ")
    return ItemPick(
        name = name,
        weightLb = weightLb,
        description = if (body.isNotBlank() && body.length > description.length) "$description\n$body".trim() else description,
        notes = notes,
        special = origin.name != "SRD" && (kind.name == "MAGIC_ITEM" || kind.name == "LEGENDARY_ITEM"),
    )
}

/** The fields of «Añadir rasgo», filled from a picked feat, background, species or rule. */
data class TraitPick(val name: String, val source: String, val description: String)

fun RulesOption.toTraitPick(): TraitPick = TraitPick(name = name, source = source, description = body.ifBlank { summary })
