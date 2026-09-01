package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterNote
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal data class CharacterNotesDraftV4(
    val generalNotes: String = "",
    val cards: List<CharacterNote> = emptyList(),
)

internal fun characterNotesDraftToJsonV4(draft: CharacterNotesDraftV4): String =
    JSONObject()
        .put("generalNotes", draft.generalNotes)
        .put(
            "cards",
            JSONArray().apply {
                draft.cards.forEach { note ->
                    put(
                        JSONObject()
                            .put("id", note.id.toString())
                            .put("title", note.title)
                            .put("content", note.content)
                            .put("sortOrder", note.sortOrder),
                    )
                }
            },
        )
        .toString()

internal fun characterNotesDraftFromJsonV4(raw: String): CharacterNotesDraftV4 = runCatching {
    val json = JSONObject(raw)
    val cardsJson = json.optJSONArray("cards") ?: JSONArray()
    val cards = buildList {
        for (index in 0 until cardsJson.length()) {
            val item = cardsJson.getJSONObject(index)
            val id = runCatching { Uuid.parse(item.getString("id")) }.getOrNull() ?: continue
            add(
                CharacterNote(
                    id = id,
                    title = item.optString("title", ""),
                    content = item.optString("content", ""),
                    sortOrder = item.optInt("sortOrder", index),
                ),
            )
        }
    }
    CharacterNotesDraftV4(
        generalNotes = json.optString("generalNotes", ""),
        cards = cards.sortedWith(compareBy<CharacterNote> { it.sortOrder }.thenBy { it.id.toString() }),
    )
}.getOrDefault(CharacterNotesDraftV4())
