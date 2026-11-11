package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSense
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.passiveInsight
import io.github.mrsimkin.dndcustomaid.shared.character.passiveInvestigation
import kotlin.uuid.Uuid

/*
 * Base para DM, package 3: Pantalla del DM, Vista Grupo (owner decision P9).
 *
 * One row per datum and one column per PC. Only fixed sheet data, never current HP. In numeric
 * rows the best value is marked (several PCs can share it). Text rows (senses, languages) are
 * shown as written and never marked.
 */

enum class PartyLensRowKind {
    ARMOR_CLASS,
    MAX_HIT_POINTS,
    PASSIVE_PERCEPTION,
    PASSIVE_INSIGHT,
    PASSIVE_INVESTIGATION,
    SPEED,
    SENSES,
    LANGUAGES,
}

data class PartyLensColumn(
    val characterId: Uuid,
    val name: String,
)

data class PartyLensCell(
    val number: Int? = null,
    val text: String = "",
    val best: Boolean = false,
)

data class PartyLensRow(
    val kind: PartyLensRowKind,
    val cells: List<PartyLensCell>,
)

data class PartyLens(
    val columns: List<PartyLensColumn>,
    val rows: List<PartyLensRow>,
)

/** Fixed sheet data of one PC, as the Vista Grupo needs it. */
data class PartyLensMember(
    val sheet: CharacterSheet,
    val senses: List<CharacterSense>,
)

/**
 * Builds the Vista Grupo. [formatFeet] turns a distance in feet into the text the App shows
 * (the Android App formats meters for Spanish players).
 */
fun partyLens(members: List<PartyLensMember>, formatFeet: (Int) -> String): PartyLens {
    val ordered = members.sortedWith(compareBy<PartyLensMember> { it.sheet.name.lowercase() }.thenBy { it.sheet.id.toString() })
    val columns = ordered.map { PartyLensColumn(it.sheet.id, it.sheet.name) }

    fun numeric(kind: PartyLensRowKind, value: (CharacterSheet) -> Int, text: (Int) -> String = Int::toString): PartyLensRow {
        val values = ordered.map { value(it.sheet) }
        val best = values.maxOrNull()
        return PartyLensRow(
            kind = kind,
            cells = values.map { PartyLensCell(number = it, text = text(it), best = ordered.size > 1 && it == best) },
        )
    }

    fun textRow(kind: PartyLensRowKind, value: (PartyLensMember) -> String) = PartyLensRow(
        kind = kind,
        cells = ordered.map { PartyLensCell(text = value(it).ifBlank { "—" }) },
    )

    return PartyLens(
        columns = columns,
        rows = listOf(
            numeric(PartyLensRowKind.ARMOR_CLASS, { it.armorClass }),
            numeric(PartyLensRowKind.MAX_HIT_POINTS, { it.maxHp }),
            numeric(PartyLensRowKind.PASSIVE_PERCEPTION, { it.passivePerception }),
            numeric(PartyLensRowKind.PASSIVE_INSIGHT, { it.passiveInsight }),
            numeric(PartyLensRowKind.PASSIVE_INVESTIGATION, { it.passiveInvestigation }),
            numeric(PartyLensRowKind.SPEED, { it.speed }, formatFeet),
            textRow(PartyLensRowKind.SENSES) { member ->
                member.senses.sortedBy { it.sortOrder }.joinToString(", ") { sense ->
                    sense.rangeFeet?.let { "${sense.name.trim()} ${formatFeet(it)}" } ?: sense.name.trim()
                }
            },
            textRow(PartyLensRowKind.LANGUAGES) { member ->
                member.sheet.proficiencies
                    .filter { it.type == CharacterProficiencyType.LANGUAGE }
                    .sortedWith(compareBy({ it.sortOrder }, { it.name.lowercase() }))
                    .joinToString(", ") { it.name.trim() }
            },
        ),
    )
}

fun PartyLensRowKind.spanishLabel(): String = when (this) {
    PartyLensRowKind.ARMOR_CLASS -> "CA"
    PartyLensRowKind.MAX_HIT_POINTS -> "PV máximos"
    PartyLensRowKind.PASSIVE_PERCEPTION -> "Percepción pasiva"
    PartyLensRowKind.PASSIVE_INSIGHT -> "Perspicacia pasiva"
    PartyLensRowKind.PASSIVE_INVESTIGATION -> "Investigación pasiva"
    PartyLensRowKind.SPEED -> "Velocidad"
    PartyLensRowKind.SENSES -> "Sentidos"
    PartyLensRowKind.LANGUAGES -> "Idiomas"
}
