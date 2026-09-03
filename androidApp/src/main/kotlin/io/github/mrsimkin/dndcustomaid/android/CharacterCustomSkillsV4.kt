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
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.customSkillTotal
import kotlin.uuid.Uuid

@Composable
internal fun CharacterCustomSkillsCardV4(
    skills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    layoutChoice: SkillLayoutChoice,
    onSkillsChange: (List<CharacterCustomSkill>) -> Unit,
) {
    var editorId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Habilidades personalizadas", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Homebrew u otras habilidades asociadas a una característica. Usan el mismo cálculo de competencia/pericia.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                TextButton(onClick = { editorId = null; editorOpen = true }) { Text("+ Añadir") }
            }

            if (skills.isEmpty()) {
                Text("Sin habilidades personalizadas.", style = MaterialTheme.typography.labelSmall)
            } else when (layoutChoice) {
                SkillLayoutChoice.BY_SKILLS -> {
                    skills.sortedBy { it.sortOrder }.forEach { skill ->
                        CustomSkillRowV4(
                            skill = skill,
                            total = calculationSheet.customSkillTotal(skill),
                            onOpen = { editorId = skill.id.toString(); editorOpen = true },
                            onDelete = { deleteId = skill.id.toString() },
                        )
                    }
                }
                SkillLayoutChoice.BY_ATTRIBUTE -> {
                    CharacterAbility.entries.forEach { ability ->
                        val related = skills.filter { it.ability == ability }.sortedBy { it.sortOrder }
                        if (related.isNotEmpty()) {
                            Text(abilityAbbrevCustomV4(ability), style = MaterialTheme.typography.labelLarge)
                            related.forEach { skill ->
                                CustomSkillRowV4(
                                    skill = skill,
                                    total = calculationSheet.customSkillTotal(skill),
                                    onOpen = { editorId = skill.id.toString(); editorOpen = true },
                                    onDelete = { deleteId = skill.id.toString() },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (editorOpen) {
        val existing = editorId?.let { id -> skills.firstOrNull { it.id.toString() == id } }
        CustomSkillEditorDialogV4(
            existing = existing,
            onDismiss = { editorOpen = false },
            onSave = { saved ->
                val updated = if (existing == null) {
                    skills + saved.copy(sortOrder = skills.size)
                } else {
                    skills.map { if (it.id == existing.id) saved.copy(sortOrder = it.sortOrder) else it }
                }
                onSkillsChange(updated)
                editorOpen = false
            },
        )
    }

    deleteId?.let { id ->
        val target = skills.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "habilidad personalizada",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onSkillsChange(
                        skills.filterNot { it.id == target.id }
                            .mapIndexed { index, item -> item.copy(sortOrder = index) },
                    )
                    deleteId = null
                },
            )
        }
    }
}

@Composable
private fun CustomSkillRowV4(
    skill: CharacterCustomSkill,
    total: Int,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f).clickable(onClick = onOpen).padding(vertical = 4.dp),
            ) {
                Text("${skill.name} (${abilityAbbrevCustomV4(skill.ability)})", style = MaterialTheme.typography.bodySmall)
                Text(
                    listOfNotNull(trainingLabelCustomV4(skill.training), skill.source?.takeIf { it.isNotBlank() }).joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Text(if (total >= 0) "+$total" else total.toString(), style = MaterialTheme.typography.titleSmall)
            CharacterD20RollButtonV4(label = skill.name, modifier = total)
            TextButton(onClick = onDelete) { Text("Eliminar") }
        }
    }
}

@Composable
private fun CustomSkillEditorDialogV4(
    existing: CharacterCustomSkill?,
    onDismiss: () -> Unit,
    onSave: (CharacterCustomSkill) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var abilityName by rememberSaveable { mutableStateOf((existing?.ability ?: CharacterAbility.INTELLIGENCE).name) }
    var trainingName by rememberSaveable { mutableStateOf((existing?.training ?: SkillTraining.NONE).name) }
    var adjustment by rememberSaveable { mutableStateOf(existing?.adjustment?.toString() ?: "0") }
    var source by rememberSaveable { mutableStateOf(existing?.source.orEmpty()) }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    val ability = runCatching { CharacterAbility.valueOf(abilityName) }.getOrDefault(CharacterAbility.INTELLIGENCE)
    val training = runCatching { SkillTraining.valueOf(trainingName) }.getOrDefault(SkillTraining.NONE)
    val parsedAdjustment = adjustment.takeIf { it.isNotBlank() && it != "+" && it != "-" }?.toIntOrNull()
    val valid = name.trim().isNotEmpty() && parsedAdjustment != null

    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir habilidad personalizada" else "Editar habilidad personalizada",
        onCancel = onDismiss,
        onSave = {
            onSave(
                CharacterCustomSkill(
                    id = existing?.id ?: Uuid.random(),
                    name = name.trim(),
                    ability = ability,
                    training = training,
                    adjustment = parsedAdjustment ?: 0,
                    source = source.trim().takeIf { it.isNotEmpty() },
                    notes = notes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: 0,
                ),
            )
        },
        saveEnabled = valid,
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        CustomEnumDropdownV4(
            label = "Característica",
            current = abilityAbbrevCustomV4(ability),
            options = CharacterAbility.entries.map { it.name to abilityAbbrevCustomV4(it) },
            onSelect = { abilityName = it },
        )
        CustomEnumDropdownV4(
            label = "Entrenamiento",
            current = trainingLabelCustomV4(training),
            options = SkillTraining.entries.map { it.name to trainingLabelCustomV4(it) },
            onSelect = { trainingName = it },
        )
        OutlinedTextField(
            value = adjustment,
            onValueChange = { raw ->
                val sign = raw.firstOrNull()?.takeIf { it == '+' || it == '-' }?.toString().orEmpty()
                val digits = raw.drop(if (sign.isEmpty()) 0 else 1).filter(Char::isDigit)
                adjustment = sign + digits
            },
            label = { Text("Ajuste adicional") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        CharacterInlineValidationMessage(if (adjustment.isNotBlank() && parsedAdjustment == null) "Escribe un ajuste numérico válido." else null)
        OutlinedTextField(value = source, onValueChange = { source = it }, label = { Text("Fuente opcional") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
    }
}

@Composable
private fun CustomEnumDropdownV4(
    label: String,
    current: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(current) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, value) ->
                    DropdownMenuItem(text = { Text(value) }, onClick = { onSelect(key); expanded = false })
                }
            }
        }
    }
}

private fun abilityAbbrevCustomV4(ability: CharacterAbility): String = when (ability) {
    CharacterAbility.STRENGTH -> "FUE"
    CharacterAbility.DEXTERITY -> "DES"
    CharacterAbility.CONSTITUTION -> "CON"
    CharacterAbility.INTELLIGENCE -> "INT"
    CharacterAbility.WISDOM -> "SAB"
    CharacterAbility.CHARISMA -> "CAR"
}

private fun trainingLabelCustomV4(training: SkillTraining): String = when (training) {
    SkillTraining.NONE -> "Sin competencia"
    SkillTraining.PROFICIENT -> "Competente"
    SkillTraining.EXPERTISE -> "Pericia"
}
