package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import org.json.JSONObject

internal fun characterBackgroundToJsonV4(background: CharacterBackground): String =
    JSONObject()
        .put("name", background.name)
        .put("summary", background.summary)
        .put("race", background.race)
        .put("religionFaith", background.religionFaith)
        .put("personalityTraits", background.personalityTraits)
        .put("ideals", background.ideals)
        .put("bonds", background.bonds)
        .put("flaws", background.flaws)
        .put("story", background.story)
        .toString()

internal fun characterBackgroundFromJsonV4(raw: String): CharacterBackground = runCatching {
    val json = JSONObject(raw)
    CharacterBackground(
        name = json.optString("name", ""),
        summary = json.optString("summary", ""),
        race = json.optString("race", ""),
        religionFaith = json.optString("religionFaith", ""),
        personalityTraits = json.optString("personalityTraits", ""),
        ideals = json.optString("ideals", ""),
        bonds = json.optString("bonds", ""),
        flaws = json.optString("flaws", ""),
        story = json.optString("story", ""),
    )
}.getOrDefault(CharacterBackground())
