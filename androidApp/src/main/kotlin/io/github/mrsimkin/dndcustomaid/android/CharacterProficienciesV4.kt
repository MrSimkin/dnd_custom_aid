package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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

@Composable
internal fun CharacterProficienciesCardV4(
    proficiencies: List<CharacterProficiency>,
    structuralEditingEnabled: Boolean,
    onProficienciesChange: (List<CharacterProficiency>) -> Unit,
) {
    var editorOpen by rememberSaveable("proficiency-editor-open") { mutableStateOf(false) }
    var editingId by rememberSaveable("proficiency-editor-id") { mutableStateOf<String?>(null) }
    var deleteId by rememberSaveable("proficiency-delete-id") { mutableStateOf<String?>(null) }
    val ordered = remember(proficiencies) { normalizeCharacterProficiencies(proficiencies) }

    fun beginAdd() {
        if (!structuralEditingEnabled) return
        editingId = null
        editorOpen = true
    }

    fun beginEdit(proficiency: CharacterProficiency) {
        if (!structuralEditingEnabled) return
        editingId = proficiency.id.toString()
        editorOpen = true
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                    Text("Idiomas y competencias", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Idiomas, herramientas, armaduras, armas y otras competencias. La ficha no valida legalidad.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) {
                    Text("+ Añadir")
                }
            }

            if (ordered.isEmpty()) {
                CharacterUsefulEmptyState(
                    title = "Sin idiomas o competencias",
                    message = "Añade referencias de entrenamiento o competencia que quieras conservar en la ficha.",
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
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Text(proficiency.name, style = MaterialTheme.typography.labelLarge)
                            Text(
                                listOfNotNull(
                                    proficiencyTypeLabelV4(proficiency.type),
                                    proficiency.source?.takeIf { it.isNotBlank() },
                                ).joinToString(" · "),
                                style = MaterialTheme.typography.labelSmall,
                            )
                            proficiency.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                                Text(notes, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                            }
                            if (structuralEditingEnabled) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
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
    }

    if (editorOpen && structuralEditingEnabled) {
        val existing = editingId?.let { id -> ordered.firstOrNull { it.id.toString() == id } }
        CharacterProficiencyEditorDialogV4(
            existing = existing,
            onCancel = { editorOpen = false },
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
private fun CharacterProficiencyEditorDialogV4(
    existing: CharacterProficiency?,
    onCancel: () -> Unit,
    onSave: (CharacterProficiency) -> Unit,
) {
    var typeName by rememberSaveable(existing?.id?.toString(), "proficiency-type") {
        mutableStateOf((existing?.type ?: CharacterProficiencyType.LANGUAGE).name)
    }
    var name by rememberSaveable(existing?.id?.toString(), "proficiency-name") {
        mutableStateOf(existing?.name.orEmpty())
    }
    var source by rememberSaveable(existing?.id?.toString(), "proficiency-source") {
        mutableStateOf(existing?.source.orEmpty())
    }
    var notes by rememberSaveable(existing?.id?.toString(), "proficiency-notes") {
        mutableStateOf(existing?.notes.orEmpty())
    }
    var typeMenuOpen by rememberSaveable { mutableStateOf(false) }
    val type = runCatching { CharacterProficiencyType.valueOf(typeName) }
        .getOrDefault(CharacterProficiencyType.OTHER)

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir idioma o competencia" else "Editar idioma o competencia",
        onCancel = onCancel,
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
        supportingText = "Registro libre: la app conserva la información sin imponer reglas de clase, especie o dote.",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Tipo", style = MaterialTheme.typography.labelSmall)
            Box {
                OutlinedButton(
                    onClick = { typeMenuOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(proficiencyTypeLabelV4(type))
                }
                DropdownMenu(
                    expanded = typeMenuOpen,
                    onDismissRequest = { typeMenuOpen = false },
                ) {
                    CharacterProficiencyType.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(proficiencyTypeLabelV4(option)) },
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
        CharacterInlineValidationMessage(
            if (name.isNotEmpty() && name.isBlank()) "El nombre no puede quedar vacío." else null,
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

internal fun proficiencyTypeLabelV4(type: CharacterProficiencyType): String = when (type) {
    CharacterProficiencyType.LANGUAGE -> "Idioma"
    CharacterProficiencyType.TOOL -> "Herramienta"
    CharacterProficiencyType.ARMOR -> "Armadura"
    CharacterProficiencyType.WEAPON -> "Arma"
    CharacterProficiencyType.OTHER -> "Otra"
}
