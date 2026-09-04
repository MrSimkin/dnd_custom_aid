package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOption
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOptionKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCompanion
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterForm
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal data class CharacterH1ModuleDraftV4(
    val classOptions: List<CharacterClassOption>,
    val forms: List<CharacterForm>,
    val companions: List<CharacterCompanion> = emptyList(),
)

internal fun characterH1ModuleDraftToJsonV4(draft: CharacterH1ModuleDraftV4): String =
    JSONObject().apply {
        put(
            "classOptions",
            JSONArray().apply {
                draft.classOptions.forEach { option ->
                    put(
                        JSONObject().apply {
                            put("id", option.id.toString())
                            put("linkedClassId", option.linkedClassId?.toString() ?: JSONObject.NULL)
                            put("kind", option.kind.name)
                            put("name", option.name)
                            put("source", option.source ?: JSONObject.NULL)
                            put("costText", option.costText ?: JSONObject.NULL)
                            put("effectSummary", option.effectSummary)
                            put("notes", option.notes ?: JSONObject.NULL)
                            put("active", option.active)
                            put("pinned", option.pinned)
                            put("sortOrder", option.sortOrder)
                        },
                    )
                }
            },
        )
        put(
            "forms",
            JSONArray().apply {
                draft.forms.forEach { form ->
                    put(
                        JSONObject().apply {
                            put("id", form.id.toString())
                            put("name", form.name)
                            put("source", form.source ?: JSONObject.NULL)
                            put("challengeRatingText", form.challengeRatingText ?: JSONObject.NULL)
                            put("armorClass", form.armorClass ?: JSONObject.NULL)
                            put("hitPoints", form.hitPoints ?: JSONObject.NULL)
                            put("movement", form.movement ?: JSONObject.NULL)
                            put("senses", form.senses ?: JSONObject.NULL)
                            put("actionSummary", form.actionSummary)
                            put("notes", form.notes ?: JSONObject.NULL)
                            put("pinned", form.pinned)
                            put("sortOrder", form.sortOrder)
                        },
                    )
                }
            },
        )
        put(
            "companions",
            JSONArray().apply {
                draft.companions.forEach { companion ->
                    put(
                        JSONObject().apply {
                            put("id", companion.id.toString())
                            put("linkedClassId", companion.linkedClassId?.toString() ?: JSONObject.NULL)
                            put("name", companion.name)
                            put("kind", companion.kind)
                            put("source", companion.source ?: JSONObject.NULL)
                            put("armorClass", companion.armorClass ?: JSONObject.NULL)
                            put("maxHp", companion.maxHp ?: JSONObject.NULL)
                            put("currentHp", companion.currentHp ?: JSONObject.NULL)
                            put("tempHp", companion.tempHp)
                            put("speed", companion.speed ?: JSONObject.NULL)
                            put("abilitySummary", companion.abilitySummary ?: JSONObject.NULL)
                            put("sensesProficiencies", companion.sensesProficiencies ?: JSONObject.NULL)
                            put("traitsActions", companion.traitsActions)
                            put("notes", companion.notes ?: JSONObject.NULL)
                            put("active", companion.active)
                            put("sortOrder", companion.sortOrder)
                        },
                    )
                }
            },
        )
    }.toString()

internal fun characterH1ModuleDraftFromJsonV4(json: String): CharacterH1ModuleDraftV4 {
    val root = runCatching { JSONObject(json) }.getOrElse { JSONObject() }
    val classOptionsArray = root.optJSONArray("classOptions") ?: JSONArray()
    val formsArray = root.optJSONArray("forms") ?: JSONArray()
    val companionsArray = root.optJSONArray("companions") ?: JSONArray()

    val classOptions = buildList {
        for (index in 0 until classOptionsArray.length()) {
            val item = classOptionsArray.optJSONObject(index) ?: continue
            val id = item.optUuidV4("id") ?: continue
            val kind = runCatching {
                CharacterClassOptionKind.valueOf(item.optString("kind", CharacterClassOptionKind.OTHER.name))
            }.getOrDefault(CharacterClassOptionKind.OTHER)
            add(
                CharacterClassOption(
                    id = id,
                    linkedClassId = item.optNullableStringV4("linkedClassId")?.let { raw ->
                        runCatching { Uuid.parse(raw) }.getOrNull()
                    },
                    kind = kind,
                    name = item.optString("name"),
                    source = item.optNullableStringV4("source"),
                    costText = item.optNullableStringV4("costText"),
                    effectSummary = item.optString("effectSummary"),
                    notes = item.optNullableStringV4("notes"),
                    active = item.optBoolean("active", true),
                    pinned = item.optBoolean("pinned", false),
                    sortOrder = item.optInt("sortOrder", index),
                ),
            )
        }
    }

    val forms = buildList {
        for (index in 0 until formsArray.length()) {
            val item = formsArray.optJSONObject(index) ?: continue
            val id = item.optUuidV4("id") ?: continue
            add(
                CharacterForm(
                    id = id,
                    name = item.optString("name"),
                    source = item.optNullableStringV4("source"),
                    challengeRatingText = item.optNullableStringV4("challengeRatingText"),
                    armorClass = item.optNullableIntV4("armorClass"),
                    hitPoints = item.optNullableIntV4("hitPoints"),
                    movement = item.optNullableStringV4("movement"),
                    senses = item.optNullableStringV4("senses"),
                    actionSummary = item.optString("actionSummary"),
                    notes = item.optNullableStringV4("notes"),
                    pinned = item.optBoolean("pinned", false),
                    sortOrder = item.optInt("sortOrder", index),
                ),
            )
        }
    }

    val companions = buildList {
        for (index in 0 until companionsArray.length()) {
            val item = companionsArray.optJSONObject(index) ?: continue
            val id = item.optUuidV4("id") ?: continue
            add(
                CharacterCompanion(
                    id = id,
                    linkedClassId = item.optNullableStringV4("linkedClassId")?.let { raw ->
                        runCatching { Uuid.parse(raw) }.getOrNull()
                    },
                    name = item.optString("name"),
                    kind = item.optString("kind"),
                    source = item.optNullableStringV4("source"),
                    armorClass = item.optNullableIntV4("armorClass"),
                    maxHp = item.optNullableIntV4("maxHp"),
                    currentHp = item.optNullableIntV4("currentHp"),
                    tempHp = item.optInt("tempHp", 0),
                    speed = item.optNullableStringV4("speed"),
                    abilitySummary = item.optNullableStringV4("abilitySummary"),
                    sensesProficiencies = item.optNullableStringV4("sensesProficiencies"),
                    traitsActions = item.optString("traitsActions"),
                    notes = item.optNullableStringV4("notes"),
                    active = item.optBoolean("active", true),
                    sortOrder = item.optInt("sortOrder", index),
                ),
            )
        }
    }

    return CharacterH1ModuleDraftV4(
        classOptions = classOptions,
        forms = forms,
        companions = companions,
    )
}

private fun JSONObject.optNullableStringV4(key: String): String? =
    if (!has(key) || isNull(key)) null else optString(key)

private fun JSONObject.optNullableIntV4(key: String): Int? =
    if (!has(key) || isNull(key)) null else optInt(key)

private fun JSONObject.optUuidV4(key: String): Uuid? =
    optNullableStringV4(key)?.let { raw -> runCatching { Uuid.parse(raw) }.getOrNull() }
