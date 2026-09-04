package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterProficiencies
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal fun characterProficienciesToJsonV4(
    proficiencies: List<CharacterProficiency>,
): String {
    val array = JSONArray()
    normalizeCharacterProficiencies(proficiencies).forEach { proficiency ->
        array.put(
            JSONObject()
                .put("id", proficiency.id.toString())
                .put("type", proficiency.type.name)
                .put("name", proficiency.name)
                .put("source", proficiency.source)
                .put("notes", proficiency.notes)
                .put("sortOrder", proficiency.sortOrder),
        )
    }
    return array.toString()
}

internal fun characterProficienciesFromJsonV4(raw: String): List<CharacterProficiency> = runCatching {
    val array = JSONArray(raw)
    buildList {
        repeat(array.length()) { index ->
            val item = array.getJSONObject(index)
            add(
                CharacterProficiency(
                    id = Uuid.parse(item.getString("id")),
                    type = runCatching {
                        CharacterProficiencyType.valueOf(item.getString("type"))
                    }.getOrDefault(CharacterProficiencyType.OTHER),
                    name = item.getString("name"),
                    source = item.optionalProficiencyTextV4("source"),
                    notes = item.optionalProficiencyTextV4("notes"),
                    sortOrder = item.optInt("sortOrder", index),
                ),
            )
        }
    }
}.getOrElse { emptyList() }.let(::normalizeCharacterProficiencies)

private fun JSONObject.optionalProficiencyTextV4(key: String): String? =
    if (!has(key) || isNull(key)) null else getString(key).trim().takeIf { it.isNotEmpty() }
