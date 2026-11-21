package io.github.mrsimkin.dndcustomaid.shared.character

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * The attacks of a PC while they are being edited, written as text so the editor can keep a draft
 * and compare it with what is saved.
 *
 * Owner's C (2026-11-20): this lived only in the tablet App, where nothing could test it, and the
 * QA of 0.7.0-preqa.4 found it dropping the weapon or spell a attack was linked to. It lives here
 * now, with its tests, and the tablet uses it from here.
 */

@Serializable
private data class CombatEntryDraft(
    val id: String,
    val name: String,
    val type: String,
    val attackModifier: Int? = null,
    val damageEffect: String = "",
    val rangeText: String? = null,
    val notes: String? = null,
    val sortOrder: Int = 0,
    val pinned: Boolean = false,
    /** QA 0.7.0-preqa.4: losing these two made a linked attack lose its 🗡 or ✨. */
    val linkedItemId: String? = null,
    val linkedSpellId: String? = null,
)

private val draftJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}

fun combatEntriesToJson(entries: List<CharacterCombatEntry>): String =
    draftJson.encodeToString(
        kotlinx.serialization.builtins.ListSerializer(CombatEntryDraft.serializer()),
        entries.mapIndexed { index, entry ->
            CombatEntryDraft(
                id = entry.id.toString(),
                name = entry.name,
                type = entry.type.name,
                attackModifier = entry.attackModifier,
                damageEffect = entry.damageEffect,
                rangeText = entry.rangeText,
                notes = entry.notes,
                sortOrder = index,
                pinned = entry.pinned,
                linkedItemId = entry.linkedItemId?.toString(),
                linkedSpellId = entry.linkedSpellId?.toString(),
            )
        },
    )

/** Anything that cannot be read comes back as no attacks, never as a crash in the middle of play. */
fun combatEntriesFromJson(raw: String): List<CharacterCombatEntry> = runCatching {
    draftJson.decodeFromString(
        kotlinx.serialization.builtins.ListSerializer(CombatEntryDraft.serializer()),
        raw,
    ).mapIndexedNotNull { index, draft ->
        val type = CharacterCombatEntryType.entries.firstOrNull { it.name == draft.type } ?: return@mapIndexedNotNull null
        val id = runCatching { Uuid.parse(draft.id) }.getOrNull() ?: return@mapIndexedNotNull null
        CharacterCombatEntry(
            id = id,
            name = draft.name,
            type = type,
            attackModifier = draft.attackModifier,
            damageEffect = draft.damageEffect,
            rangeText = draft.rangeText,
            notes = draft.notes,
            sortOrder = index,
            pinned = draft.pinned,
            linkedItemId = draft.linkedItemId?.let { value -> runCatching { Uuid.parse(value) }.getOrNull() },
            linkedSpellId = draft.linkedSpellId?.let { value -> runCatching { Uuid.parse(value) }.getOrNull() },
        )
    }
}.getOrDefault(emptyList())
