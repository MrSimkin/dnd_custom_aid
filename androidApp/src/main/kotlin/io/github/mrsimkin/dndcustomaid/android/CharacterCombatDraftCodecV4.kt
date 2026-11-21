package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.combatEntriesFromJson
import io.github.mrsimkin.dndcustomaid.shared.character.combatEntriesToJson

/*
 * Owner's C (2026-11-20): this codec moved to `shared`, where it is tested. The QA of
 * 0.7.0-preqa.4 found the version that lived here dropping the weapon or spell an attack was
 * linked to, and nothing could have caught it: this App had no tests.
 */

internal fun combatEntriesToJsonV4(entries: List<CharacterCombatEntry>): String = combatEntriesToJson(entries)

internal fun combatEntriesFromJsonV4(raw: String): List<CharacterCombatEntry> = combatEntriesFromJson(raw)
