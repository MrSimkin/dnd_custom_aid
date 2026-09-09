package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatDamageProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponent
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponentKind
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal fun characterCombatDamageProfilesToJsonV4(
    profiles: List<CharacterCombatDamageProfile>,
): String = JSONArray().apply {
    profiles.forEach { profile ->
        put(
            JSONObject().apply {
                put("combatEntryId", profile.combatEntryId.toString())
                put("components", JSONArray(characterDamageComponentsToJsonV4(profile.components)))
            },
        )
    }
}.toString()

internal fun characterCombatDamageProfilesFromJsonV4(
    raw: String,
): List<CharacterCombatDamageProfile> = runCatching {
    val array = JSONArray(raw)
    buildList {
        repeat(array.length()) { index ->
            val item = array.getJSONObject(index)
            val combatEntryId = Uuid.parse(item.getString("combatEntryId"))
            val componentsArray = item.optJSONArray("components") ?: JSONArray()
            add(
                CharacterCombatDamageProfile(
                    combatEntryId = combatEntryId,
                    components = characterDamageComponentsFromJsonV4(componentsArray.toString()),
                ),
            )
        }
    }
}.getOrDefault(emptyList())

internal fun characterDamageComponentsToJsonV4(
    components: List<CharacterDamageComponent>,
): String = JSONArray().apply {
    components.forEach { component ->
        put(
            JSONObject().apply {
                put("kind", component.kind.name)
                put("expression", component.expression)
                component.typeText?.let { put("typeText", it) }
            },
        )
    }
}.toString()

internal fun characterDamageComponentsFromJsonV4(
    raw: String,
): List<CharacterDamageComponent> = runCatching {
    val array = JSONArray(raw)
    buildList {
        repeat(array.length()) { index ->
            val item = array.getJSONObject(index)
            add(
                CharacterDamageComponent(
                    kind = runCatching {
                        CharacterDamageComponentKind.valueOf(item.optString("kind"))
                    }.getOrDefault(CharacterDamageComponentKind.TEXT),
                    expression = item.optString("expression"),
                    typeText = item.optString("typeText").takeIf { it.isNotBlank() },
                ),
            )
        }
    }
}.getOrDefault(emptyList())
