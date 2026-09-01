package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal data class CharacterSpellcastingDraftV4(
    val sources: List<CharacterSpellcastingSource>,
    val spells: List<CharacterSpell>,
)

internal fun characterSpellcastingDraftToJsonV4(draft: CharacterSpellcastingDraftV4): String {
    val sources = JSONArray()
    draft.sources.forEachIndexed { index, source ->
        sources.put(
            JSONObject()
                .put("id", source.id.toString())
                .put("name", source.name)
                .put("linkedClassId", source.linkedClassId?.toString() ?: JSONObject.NULL)
                .put("sortOrder", index),
        )
    }

    val spells = JSONArray()
    draft.spells.forEach { spell ->
        val associations = JSONArray()
        spell.sourceAssociations.forEach { association ->
            associations.put(
                JSONObject()
                    .put("sourceId", association.sourceId.toString())
                    .put("prepared", association.prepared),
            )
        }
        spells.put(
            JSONObject()
                .put("id", spell.id.toString())
                .put("name", spell.name)
                .put("level", spell.level)
                .put("castingTime", spell.castingTime)
                .put("rangeText", spell.rangeText)
                .put("verbal", spell.verbal)
                .put("somatic", spell.somatic)
                .put("material", spell.material)
                .put("materialText", spell.materialText ?: JSONObject.NULL)
                .put("duration", spell.duration)
                .put("concentration", spell.concentration)
                .put("ritual", spell.ritual)
                .put("description", spell.description)
                .put("notes", spell.notes ?: JSONObject.NULL)
                .put("sortOrder", spell.sortOrder)
                .put("sourceAssociations", associations),
        )
    }

    return JSONObject()
        .put("sources", sources)
        .put("spells", spells)
        .toString()
}

internal fun characterSpellcastingDraftFromJsonV4(raw: String): CharacterSpellcastingDraftV4 = runCatching {
    val root = JSONObject(raw)
    val sourceArray = root.optJSONArray("sources") ?: JSONArray()
    val sources = buildList {
        for (index in 0 until sourceArray.length()) {
            val item = sourceArray.getJSONObject(index)
            add(
                CharacterSpellcastingSource(
                    id = runCatching { Uuid.parse(item.getString("id")) }.getOrElse { Uuid.random() },
                    name = item.optString("name", ""),
                    linkedClassId = if (item.isNull("linkedClassId")) {
                        null
                    } else {
                        runCatching { Uuid.parse(item.getString("linkedClassId")) }.getOrNull()
                    },
                    sortOrder = index,
                ),
            )
        }
    }

    val spellArray = root.optJSONArray("spells") ?: JSONArray()
    val spells = buildList {
        for (index in 0 until spellArray.length()) {
            val item = spellArray.getJSONObject(index)
            val associationArray = item.optJSONArray("sourceAssociations") ?: JSONArray()
            val associations = buildList {
                for (associationIndex in 0 until associationArray.length()) {
                    val association = associationArray.getJSONObject(associationIndex)
                    val sourceId = runCatching { Uuid.parse(association.getString("sourceId")) }.getOrNull()
                    if (sourceId != null) {
                        add(
                            CharacterSpellSourceAssociation(
                                sourceId = sourceId,
                                prepared = association.optBoolean("prepared", false),
                            ),
                        )
                    }
                }
            }
            add(
                CharacterSpell(
                    id = runCatching { Uuid.parse(item.getString("id")) }.getOrElse { Uuid.random() },
                    name = item.optString("name", ""),
                    level = item.optInt("level", 0).coerceIn(0, 9),
                    castingTime = item.optString("castingTime", ""),
                    rangeText = item.optString("rangeText", ""),
                    verbal = item.optBoolean("verbal", false),
                    somatic = item.optBoolean("somatic", false),
                    material = item.optBoolean("material", false),
                    materialText = if (item.isNull("materialText")) null else item.optString("materialText", ""),
                    duration = item.optString("duration", ""),
                    concentration = item.optBoolean("concentration", false),
                    ritual = item.optBoolean("ritual", false),
                    description = item.optString("description", ""),
                    notes = if (item.isNull("notes")) null else item.optString("notes", ""),
                    sortOrder = item.optInt("sortOrder", index),
                    sourceAssociations = associations,
                ),
            )
        }
    }

    CharacterSpellcastingDraftV4(sources = sources, spells = spells)
}.getOrDefault(CharacterSpellcastingDraftV4(emptyList(), emptyList()))
