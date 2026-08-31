package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal fun combatEntriesToJsonV4(entries: List<CharacterCombatEntry>): String = JSONArray().apply {
    entries.forEachIndexed { index, entry ->
        put(JSONObject().apply {
            put("id", entry.id.toString())
            put("name", entry.name)
            put("type", entry.type.name)
            put("attackModifier", entry.attackModifier ?: JSONObject.NULL)
            put("damageEffect", entry.damageEffect)
            put("rangeText", entry.rangeText ?: JSONObject.NULL)
            put("notes", entry.notes ?: JSONObject.NULL)
            put("sortOrder", index)
        })
    }
}.toString()

internal fun combatEntriesFromJsonV4(raw: String): List<CharacterCombatEntry> = runCatching {
    val array = JSONArray(raw)
    buildList {
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            add(
                CharacterCombatEntry(
                    id = Uuid.parse(item.getString("id")),
                    name = item.getString("name"),
                    type = CharacterCombatEntryType.valueOf(item.getString("type")),
                    attackModifier = if (item.isNull("attackModifier")) null else item.getInt("attackModifier"),
                    damageEffect = item.optString("damageEffect", ""),
                    rangeText = if (item.isNull("rangeText")) null else item.getString("rangeText"),
                    notes = if (item.isNull("notes")) null else item.getString("notes"),
                    sortOrder = index,
                ),
            )
        }
    }
}.getOrDefault(emptyList())
