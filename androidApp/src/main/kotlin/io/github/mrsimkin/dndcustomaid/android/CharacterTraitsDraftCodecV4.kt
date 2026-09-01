package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal fun characterTraitsToJsonV4(traits: List<CharacterTrait>): String {
    val array = JSONArray()
    traits.forEachIndexed { index, trait ->
        array.put(
            JSONObject()
                .put("id", trait.id.toString())
                .put("name", trait.name)
                .put("source", trait.source)
                .put("type", trait.type.name)
                .put("description", trait.description)
                .put("notes", trait.notes ?: JSONObject.NULL)
                .put("maxUses", trait.maxUses ?: JSONObject.NULL)
                .put("spentUses", trait.spentUses)
                .put("recovery", trait.recovery ?: JSONObject.NULL)
                .put("activation", trait.activation?.name ?: JSONObject.NULL)
                .put("sortOrder", index),
        )
    }
    return array.toString()
}

internal fun characterTraitsFromJsonV4(raw: String): List<CharacterTrait> = runCatching {
    val array = JSONArray(raw)
    buildList {
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            val maxUses = if (item.isNull("maxUses")) null else item.getInt("maxUses")
            val activation = if (item.isNull("activation")) {
                null
            } else {
                runCatching { CharacterActivationType.valueOf(item.getString("activation")) }.getOrNull()
            }
            add(
                CharacterTrait(
                    id = runCatching { Uuid.parse(item.getString("id")) }.getOrElse { Uuid.random() },
                    name = item.optString("name", ""),
                    source = item.optString("source", ""),
                    type = runCatching {
                        CharacterTraitType.valueOf(item.optString("type", CharacterTraitType.OTHER.name))
                    }.getOrDefault(CharacterTraitType.OTHER),
                    description = item.optString("description", ""),
                    notes = if (item.isNull("notes")) null else item.optString("notes", ""),
                    maxUses = maxUses,
                    spentUses = item.optInt("spentUses", 0),
                    recovery = if (item.isNull("recovery")) null else item.optString("recovery", ""),
                    activation = activation,
                    sortOrder = index,
                ),
            )
        }
    }
}.getOrDefault(emptyList())
