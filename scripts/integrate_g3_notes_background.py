from pathlib import Path


def replace_once(path: str, old: str, new: str) -> None:
    file = Path(path)
    text = file.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"Expected exactly one match in {path}, found {count}")
    file.write_text(text.replace(old, new, 1))


notes = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNotesTabV4.kt"
replace_once(
    notes,
    "import androidx.compose.foundation.lazy.LazyColumn\n",
    "import androidx.compose.foundation.lazy.LazyColumn\nimport androidx.compose.foundation.lazy.rememberLazyListState\n",
)
replace_once(
    notes,
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterNote\n",
    """import io.github.mrsimkin.dndcustomaid.shared.character.CharacterNote
import io.github.mrsimkin.dndcustomaid.shared.character.duplicateCharacterNote
import io.github.mrsimkin.dndcustomaid.shared.character.moveCharacterNoteManual
import io.github.mrsimkin.dndcustomaid.shared.character.nextCharacterNoteSortOrder
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterNotes
""",
)
replace_once(
    notes,
    """    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)

    fun normalize(cards: List<CharacterNote>): List<CharacterNote> =
        cards.mapIndexed { index, note -> note.copy(sortOrder = index) }
""",
    """    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val listState = rememberLazyListState()
""",
)
replace_once(
    notes,
    """    fun move(index: Int, offset: Int): Boolean {
        val target = index + offset
        if (target !in draft.cards.indices) return false
        val reordered = draft.cards.toMutableList()
        val item = reordered.removeAt(index)
        reordered.add(target, item)
        onDraftChange(draft.copy(cards = normalize(reordered)))
        return true
    }

    LazyColumn(
        modifier = Modifier
""",
    """    fun move(index: Int, offset: Int): Boolean {
        val note = draft.cards.getOrNull(index) ?: return false
        val before = normalizeCharacterNotes(draft.cards)
        val moved = moveCharacterNoteManual(draft.cards, note.id, offset)
        if (moved == before) return false
        onDraftChange(draft.copy(cards = moved))
        return true
    }

    fun duplicate(note: CharacterNote) {
        val copied = duplicateCharacterNote(
            source = note,
            newId = Uuid.random(),
            sortOrder = nextCharacterNoteSortOrder(draft.cards),
        )
        onDraftChange(draft.copy(cards = normalizeCharacterNotes(draft.cards + copied)))
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
""",
)
replace_once(
    notes,
    """                                        onEdit = { beginEdit(note) },
                                        onDelete = { deleteId = note.id.toString() },
                                        onMove = { offset -> move(index, offset) },
""",
    """                                        onEdit = { beginEdit(note) },
                                        onDuplicate = { duplicate(note) },
                                        onDelete = { deleteId = note.id.toString() },
                                        onMove = { offset -> move(index, offset) },
""",
)
replace_once(
    notes,
    """                                onEdit = { beginEdit(note) },
                                onDelete = { deleteId = note.id.toString() },
                                onMove = { offset -> move(index, offset) },
""",
    """                                onEdit = { beginEdit(note) },
                                onDuplicate = { duplicate(note) },
                                onDelete = { deleteId = note.id.toString() },
                                onMove = { offset -> move(index, offset) },
""",
)
replace_once(
    notes,
    "onDraftChange(draft.copy(cards = normalize(updated)))",
    "onDraftChange(draft.copy(cards = normalizeCharacterNotes(updated)))",
)
replace_once(
    notes,
    "onDraftChange(draft.copy(cards = normalize(draft.cards.filterNot { it.id == target.id })))",
    "onDraftChange(draft.copy(cards = normalizeCharacterNotes(draft.cards.filterNot { it.id == target.id })))",
)
replace_once(
    notes,
    """private fun CharacterNoteCardV4(
    note: CharacterNote,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
""",
    """private fun CharacterNoteCardV4(
    note: CharacterNote,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
""",
)
replace_once(
    notes,
    """                Text(
                    note.content.ifBlank { "Sin contenido" },
                    style = if (note.content.isBlank()) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
""",
    """                Text(
                    note.content.ifBlank { "Sin contenido" },
                    style = if (note.content.isBlank()) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDuplicate,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    ) {
                        Text("Duplicar")
                    }
                }
""",
)

background = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterBackgroundTabV4.kt"
replace_once(
    background,
    "import androidx.compose.foundation.layout.fillMaxWidth\n",
    "import androidx.compose.foundation.layout.fillMaxWidth\nimport androidx.compose.foundation.layout.heightIn\n",
)
replace_once(
    background,
    "import androidx.compose.material3.Text\n",
    "import androidx.compose.material3.Text\nimport androidx.compose.material3.TextButton\n",
)
replace_once(
    background,
    """    var editingFieldName by rememberSaveable { mutableStateOf<String?>(null) }
    var editorText by rememberSaveable { mutableStateOf("") }
""",
    """    var editingFieldName by rememberSaveable { mutableStateOf<String?>(null) }
    var editorText by rememberSaveable { mutableStateOf("") }
    var storyExpanded by rememberSaveable("background-story-expanded") { mutableStateOf(false) }
""",
)
replace_once(
    background,
    """        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("Historia del personaje", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Espacio amplio para la historia completa del personaje.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                    OutlinedTextField(
                        value = background.story,
                        onValueChange = { onBackgroundChange(background.copy(story = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Historia") },
                        minLines = if (wide) 10 else 8,
                    )
                }
            }
        }
""",
    """        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Historia del personaje", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Historia larga, disponible completa al expandir.",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        TextButton(onClick = { storyExpanded = !storyExpanded }) {
                            Text(
                                if (storyExpanded) {
                                    "Ocultar"
                                } else if (background.story.isBlank()) {
                                    "Añadir"
                                } else {
                                    "Mostrar"
                                },
                            )
                        }
                    }
                    if (storyExpanded) {
                        OutlinedTextField(
                            value = background.story,
                            onValueChange = { onBackgroundChange(background.copy(story = it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(
                                    min = if (wide) 260.dp else 220.dp,
                                    max = if (wide) 420.dp else 360.dp,
                                ),
                            label = { Text("Historia") },
                            minLines = if (wide) 10 else 8,
                            maxLines = if (wide) 20 else 16,
                            supportingText = {
                                if (background.story.length > 500) {
                                    Text("↕ Texto largo: desliza dentro del campo para recorrerlo.")
                                }
                            },
                        )
                    } else {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { storyExpanded = true },
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                            ) {
                                Text(
                                    background.story.ifBlank { "Sin historia registrada" },
                                    style = if (background.story.isBlank()) {
                                        MaterialTheme.typography.labelSmall
                                    } else {
                                        MaterialTheme.typography.bodySmall
                                    },
                                    maxLines = 3,
                                )
                                Text(
                                    if (background.story.isBlank()) "Toca para añadir" else "Toca para expandir y editar",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }
            }
        }
""",
)
