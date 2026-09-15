package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterOwnedBackgroundIdentity
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterOwnedSpeciesIdentity
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterOwnedSubraceIdentity
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import kotlin.uuid.Uuid
import org.json.JSONObject

/**
 * Unsaved P7 canonical-origin transaction owned by the normal Player editor.
 *
 * Raza, Subraza and Trasfondo identities remain canonical entities while the user edits them;
 * this draft is intentionally not persisted until the character's normal Guardar action.
 */
internal data class CharacterCanonicalOriginsDraftP7V4(
    val speciesIdentity: CharacterOwnedSpeciesIdentity?,
    val subraceIdentity: CharacterOwnedSubraceIdentity?,
    val backgroundIdentity: CharacterOwnedBackgroundIdentity?,
) {
    fun withSpeciesName(rawName: String): CharacterCanonicalOriginsDraftP7V4 {
        val current = speciesIdentity
        val updated = if (current != null) {
            current.copy(name = rawName)
        } else if (rawName.isBlank()) {
            null
        } else {
            CharacterOwnedSpeciesIdentity(id = Uuid.random(), name = rawName)
        }
        return copy(speciesIdentity = updated)
    }

    fun withSubraceName(rawName: String): CharacterCanonicalOriginsDraftP7V4 {
        val species = speciesIdentity ?: return if (rawName.isBlank()) copy(subraceIdentity = null) else this
        if (species.name.isBlank()) return this
        val current = subraceIdentity?.takeIf { it.parentSpeciesId == species.id }
        val updated = if (current != null) {
            current.copy(name = rawName)
        } else if (rawName.isBlank()) {
            null
        } else {
            CharacterOwnedSubraceIdentity(
                id = Uuid.random(),
                parentSpeciesId = species.id,
                name = rawName,
                rulesFamily = species.rulesFamily,
            )
        }
        return copy(subraceIdentity = updated)
    }

    fun withBackgroundName(rawName: String): CharacterCanonicalOriginsDraftP7V4 {
        val current = backgroundIdentity
        val updated = if (current != null) {
            current.copy(name = rawName)
        } else if (rawName.isBlank()) {
            null
        } else {
            CharacterOwnedBackgroundIdentity(id = Uuid.random(), name = rawName)
        }
        return copy(backgroundIdentity = updated)
    }

    fun normalized(): CharacterCanonicalOriginsDraftP7V4 {
        val currentSpecies = speciesIdentity
        val currentSubrace = subraceIdentity
        val currentBackground = backgroundIdentity
        val species = currentSpecies
            ?.copy(name = currentSpecies.name.trim())
            ?.takeIf { it.name.isNotEmpty() }
        val subrace = currentSubrace
            ?.copy(name = currentSubrace.name.trim())
            ?.takeIf { child ->
                child.name.isNotEmpty() && species != null && child.parentSpeciesId == species.id
            }
        val background = currentBackground
            ?.copy(name = currentBackground.name.trim())
            ?.takeIf { it.name.isNotEmpty() }
        return CharacterCanonicalOriginsDraftP7V4(species, subrace, background)
    }

    fun projectOnto(state: CharacterSuccessorState): CharacterSuccessorState {
        val normalized = normalized()
        return state.copy(
            speciesIdentity = normalized.speciesIdentity,
            subraceIdentity = normalized.subraceIdentity,
            backgroundIdentity = normalized.backgroundIdentity,
        )
    }

    companion object {
        fun from(state: CharacterSuccessorState): CharacterCanonicalOriginsDraftP7V4 =
            CharacterCanonicalOriginsDraftP7V4(
                speciesIdentity = state.speciesIdentity,
                subraceIdentity = state.subraceIdentity,
                backgroundIdentity = state.backgroundIdentity,
            )
    }
}

internal fun characterCanonicalOriginsDraftToJsonP7V4(
    draft: CharacterCanonicalOriginsDraftP7V4,
): String = JSONObject().apply {
    put("species", draft.speciesIdentity?.let(::speciesJsonP7V4) ?: JSONObject.NULL)
    put("subrace", draft.subraceIdentity?.let(::subraceJsonP7V4) ?: JSONObject.NULL)
    put("background", draft.backgroundIdentity?.let(::backgroundJsonP7V4) ?: JSONObject.NULL)
}.toString()

internal fun characterCanonicalOriginsDraftFromJsonP7V4(
    raw: String,
): CharacterCanonicalOriginsDraftP7V4 = runCatching {
    val json = JSONObject(raw)
    CharacterCanonicalOriginsDraftP7V4(
        speciesIdentity = json.optJSONObject("species")?.let { item ->
            CharacterOwnedSpeciesIdentity(
                id = Uuid.parse(item.getString("id")),
                name = item.getString("name"),
                definitionKey = optionalStringP7V4(item, "definitionKey"),
                rulesFamily = rulesFamilyP7V4(item),
            )
        },
        subraceIdentity = json.optJSONObject("subrace")?.let { item ->
            CharacterOwnedSubraceIdentity(
                id = Uuid.parse(item.getString("id")),
                parentSpeciesId = Uuid.parse(item.getString("parentSpeciesId")),
                name = item.getString("name"),
                definitionKey = optionalStringP7V4(item, "definitionKey"),
                rulesFamily = rulesFamilyP7V4(item),
            )
        },
        backgroundIdentity = json.optJSONObject("background")?.let { item ->
            CharacterOwnedBackgroundIdentity(
                id = Uuid.parse(item.getString("id")),
                name = item.getString("name"),
                definitionKey = optionalStringP7V4(item, "definitionKey"),
                rulesFamily = rulesFamilyP7V4(item),
            )
        },
    )
}.getOrElse { CharacterCanonicalOriginsDraftP7V4(null, null, null) }

private fun speciesJsonP7V4(item: CharacterOwnedSpeciesIdentity): JSONObject = JSONObject().apply {
    put("id", item.id.toString())
    put("name", item.name)
    put("definitionKey", item.definitionKey ?: JSONObject.NULL)
    put("rulesFamily", item.rulesFamily.name)
}

private fun subraceJsonP7V4(item: CharacterOwnedSubraceIdentity): JSONObject = JSONObject().apply {
    put("id", item.id.toString())
    put("parentSpeciesId", item.parentSpeciesId.toString())
    put("name", item.name)
    put("definitionKey", item.definitionKey ?: JSONObject.NULL)
    put("rulesFamily", item.rulesFamily.name)
}

private fun backgroundJsonP7V4(item: CharacterOwnedBackgroundIdentity): JSONObject = JSONObject().apply {
    put("id", item.id.toString())
    put("name", item.name)
    put("definitionKey", item.definitionKey ?: JSONObject.NULL)
    put("rulesFamily", item.rulesFamily.name)
}

private fun optionalStringP7V4(json: JSONObject, key: String): String? =
    if (json.has(key) && !json.isNull(key)) json.getString(key) else null

private fun rulesFamilyP7V4(json: JSONObject): CharacterRulesFamily = runCatching {
    CharacterRulesFamily.valueOf(json.optString("rulesFamily", CharacterRulesFamily.UNSPECIFIED.name))
}.getOrDefault(CharacterRulesFamily.UNSPECIFIED)
