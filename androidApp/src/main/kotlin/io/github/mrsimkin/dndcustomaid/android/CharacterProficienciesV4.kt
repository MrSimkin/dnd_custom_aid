package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.moveCharacterProficiencyManual
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterProficiencies
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

internal fun characterProficienciesToJsonV4(
    proficiencies: List<CharacterProficiency>,
): String {
    val array = JSONArray()
    normalizeCharacterProficiencies(proficiencies).forEach { proficiency ->
        array.put(
            JSONObject()
                .put("id", proficiency.id.toString())
                .put("type", proficiency.type.name)
                .put("name", proficiency.name)
                .put("source", proficiency.source)
                .put("notes", proficiency.notes)
                .put("sortOrder", proficiency.sortOrder),
        )
    }
    return array.toString()
}

internal fun characterProficienciesFromJsonV4(raw: String): List<CharacterProficiency> = runCatching {
    val array = JSONArray(raw)
    buildList {
        repeat(array.length()) { index ->
            val item = array.getJSONObject(index)
            add(
                CharacterProficiency(
                    id = Uuid.parse(item.getString("id")),
                    type = runCatching {
                        CharacterProficiencyType.valueOf(item.getString("type"))
                    }.getOrDefault(CharacterProficiencyType.OTHER),
                    name = item.getString("name"),
                    source = item.optionalTextM4("source"),
                    notes = item.optionalTextM4("notes"),
                    sortOrder = item.optInt("sortOrder", index),
                ),
            )
        }
    }
}.getOrElse { emptyList() }.let(::normalizeCharacterProficiencies)

private fun JSONObject.optionalTextM4(key: String): String? =
    if (!has(key) || isNull(key)) null else optString(key).takeIf { it.isNotBlank() }

@Composable
internal fun CharacterProficienciesCardV4(
    proficiencies: List<CharacterProficiency>,
    structuralEditingEnabled: Boolean,
    onProficienciesChange: (List<CharacterProficiency>) -> Unit,
) {
    var editorOpen by rememberSaveable("m4-proficiency-editor") { mutableStateOf(false) }
    var editingId by rememberSaveable("m4-proficiency-id") { mutableStateOf<String?>(null) }
    var deleteId by rememberSaveable("m4-proficiency-delete") { mutableStateOf<String?>(null) }
    val ordered = remember(proficiencies) { normalizeCharacterProficiencies(proficiencies) }

    fun beginAdd() {
        if (!structuralEditingEnabled) return
        editingId = null
        editorOpen = true
    }

    fun beginEdit(item: CharacterProficiency) {
        if (!structuralEditingEnabled) return
        editingId = item.id.toString()
        editorOpen = true
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Idiomas y competencias", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Idiomas, herramientas, armaduras, armas y otras competencias. La app las registra sin validar legalidad.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text("+ Añadir") }
            }

            if (ordered.isEmpty()) {
                CharacterUsefulEmptyState(
                    title = "Sin idiomas o competencias",
                    message = "Añade solo las referencias que quieras conservar en la ficha.",
                    onAdd = if (structuralEditingEnabled) ::beginAdd else null,
                    addLabel = "Añadir competencia",
                )
            } else {
                ordered.forEachIndexed { index, proficiency ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = structuralEditingEnabled) { beginEdit(proficiency) },
                        shape = MaterialTheme.shapes.small,
                        tonalElevation = 1.dp,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(proficiency.name, style = MaterialTheme.typography.labelLarge)
                                Text(
                                    listOfNotNull(
                                        proficiencyTypeLabelM4(proficiency.type),
                                        proficiency.source?.takeIf { it.isNotBlank() },
                                    ).joinToString(" · "),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                                proficiency.notes?.takeIf { it.isNotBlank() }?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                }
                            }
                            if (structuralEditingEnabled) {
                                TextButton(
                                    onClick = {
                                        onProficienciesChange(
                                            moveCharacterProficiencyManual(ordered, proficiency.id, -1),
                                        )
                                    },
                                    enabled = index > 0,
                                ) { Text("↑") }
                                TextButton(
                                    onClick = {
                                        onProficienciesChange(
                                            moveCharacterProficiencyManual(ordered, proficiency.id, 1),
                                        )
                                    },
                                    enabled = index < ordered.lastIndex,
                                ) { Text("↓") }
                                StableRemoveIconButton(
                                    onClick = { deleteId = proficiency.id.toString() },
                                    contentDescription = "Eliminar ${proficiency.name}",
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (editorOpen && structuralEditingEnabled) {
        val existing = editingId?.let { id -> ordered.firstOrNull { it.id.toString() == id } }
        ProficiencyEditorDialogM4(
            existing = existing,
            onDismiss = { editorOpen = false },
            onSave = { saved ->
                val updated = if (existing == null) {
                    ordered + saved.copy(sortOrder = ordered.size)
                } else {
                    ordered.map { current ->
                        if (current.id == existing.id) saved.copy(sortOrder = existing.sortOrder) else current
                    }
                }
                onProficienciesChange(normalizeCharacterProficiencies(updated))
                editorOpen = false
            },
        )
    }

    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->
        val target = ordered.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "idioma o competencia",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onProficienciesChange(
                        normalizeCharacterProficiencies(ordered.filterNot { it.id == target.id }),
                    )
                    deleteId = null
                },
            )
        }
    }
}

@Composable
private fun ProficiencyEditorDialogM4(
    existing: CharacterProficiency?,
    onDismiss: () -> Unit,
    onSave: (CharacterProficiency) -> Unit,
) {
    var typeName by rememberSaveable(existing?.id?.toString(), "type") {
        mutableStateOf((existing?.type ?: CharacterProficiencyType.LANGUAGE).name)
    }
    var name by rememberSaveable(existing?.id?.toString(), "name") { mutableStateOf(existing?.name.orEmpty()) }
    var source by rememberSaveable(existing?.id?.toString(), "source") { mutableStateOf(existing?.source.orEmpty()) }
    var notes by rememberSaveable(existing?.id?.toString(), "notes") { mutableStateOf(existing?.notes.orEmpty()) }
    var typeMenuOpen by rememberSaveable { mutableStateOf(false) }
    val type = runCatching { CharacterProficiencyType.valueOf(typeName) }
        .getOrDefault(CharacterProficiencyType.OTHER)

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir idioma o competencia" else "Editar idioma o competencia",
        onCancel = onDismiss,
        onSave = {
            onSave(
                CharacterProficiency(
                    id = existing?.id ?: Uuid.random(),
                    type = type,
                    name = name.trim(),
                    source = source.trim().takeIf { it.isNotEmpty() },
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: 0,
                ),
            )
        },
        saveEnabled = name.trim().isNotEmpty(),
    ) {
        Column {
            Text("Tipo", style = MaterialTheme.typography.labelSmall)
            androidx.compose.foundation.layout.Box {
                OutlinedButton(onClick = { typeMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(proficiencyTypeLabelM4(type))
                }
                DropdownMenu(expanded = typeMenuOpen, onDismissRequest = { typeMenuOpen = false }) {
                    CharacterProficiencyType.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(proficiencyTypeLabelM4(option)) },
                            onClick = {
                                typeName = option.name
                                typeMenuOpen = false
                            },
                        )
                    }
                }
            }
        }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = source,
            onValueChange = { source = it },
            label = { Text("Fuente opcional") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notas opcionales") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
        )
    }
}

internal fun proficiencyTypeLabelM4(type: CharacterProficiencyType): String = when (type) {
    CharacterProficiencyType.LANGUAGE -> "Idioma"
    CharacterProficiencyType.TOOL -> "Herramienta"
    CharacterProficiencyType.ARMOR -> "Armadura"
    CharacterProficiencyType.WEAPON -> "Arma"
    CharacterProficiencyType.OTHER -> "Otra"
}
