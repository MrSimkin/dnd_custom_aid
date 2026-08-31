package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.uuid.Uuid

@Composable
internal fun CharacterListScreen(
    campaign: Campaign,
    repository: CharacterRepository,
    onBack: () -> Unit,
    onEdit: (Uuid) -> Unit,
) {
    var characters by remember(campaign.id) { mutableStateOf(repository.listCharacters(campaign.id)) }
    var showCreateDialog by remember { mutableStateOf(false) }

    fun reload() {
        characters = repository.listCharacters(campaign.id)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Text("+")
            }
        },
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 900.dp)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = onBack) {
                            Text("← Campañas")
                        }
                        Text("Personajes", style = MaterialTheme.typography.headlineMedium)
                        Text(campaign.name, style = MaterialTheme.typography.titleMedium)
                    }
                }

                if (characters.isEmpty()) {
                    item {
                        Text("Aún no hay personajes en esta campaña. Usa + para crear uno.")
                    }
                } else {
                    items(characters, key = { it.id.toString() }) { character ->
                        CharacterCard(character = character, onClick = { onEdit(character.id) })
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCharacterDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                val character = repository.createCharacter(campaign.id, name)
                reload()
                showCreateDialog = false
                onEdit(character.id)
            },
        )
    }
}

@Composable
private fun CharacterCard(
    character: CharacterSheet,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(character.name, style = MaterialTheme.typography.titleMedium)
            val classSummary = if (character.classes.isEmpty()) {
                "Sin clase registrada"
            } else {
                character.classes.joinToString(" / ") { "${it.name} ${it.level}" }
            }
            Text(classSummary, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${statusLabel(character.status)} · Nivel total ${character.totalLevel}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun CreateCharacterDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo personaje") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del personaje") },
                singleLine = true,
            )
        },
        confirmButton = {
            Button(onClick = { onCreate(name) }, enabled = name.trim().isNotEmpty()) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

@Composable
internal fun CharacterEditorScreen(
    characterId: Uuid,
    repository: CharacterRepository,
    onBack: () -> Unit,
) {
    var stored by remember(characterId) {
        mutableStateOf(requireNotNull(repository.character(characterId)))
    }
    var draft by remember(characterId) { mutableStateOf(CharacterEditorDraft.from(stored)) }
    var savedMessage by remember { mutableStateOf<String?>(null) }
    val savable = draft.toSheetOrNull(stored) != null

    Scaffold { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 960.dp)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = onBack) { Text("← Personajes") }
                        Button(
                            onClick = {
                                val candidate = draft.toSheetOrNull(stored) ?: return@Button
                                stored = repository.saveCharacter(candidate)
                                draft = CharacterEditorDraft.from(stored)
                                savedMessage = "Guardado"
                            },
                            enabled = savable,
                        ) {
                            Text("Guardar")
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Ficha de personaje", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Último guardado: ${formatSavedAt(stored.updatedAtEpochSeconds)}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        savedMessage?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                        OutlinedTextField(
                            value = draft.name,
                            onValueChange = { draft = draft.copy(name = it); savedMessage = null },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        TextButton(
                            onClick = {
                                draft = draft.copy(status = nextStatus(draft.status))
                                savedMessage = null
                            },
                        ) {
                            Text("Estado: ${statusLabel(draft.status)}")
                        }
                    }
                }

                item { SectionTitle("Clases y Dados de Golpe") }
                if (draft.classes.isEmpty()) {
                    item { Text("Sin clases registradas.") }
                } else {
                    items(draft.classes, key = { it.id.toString() }) { classDraft ->
                        ClassLevelEditor(
                            draft = classDraft,
                            onChange = { changed ->
                                draft = draft.copy(
                                    classes = draft.classes.map { existing ->
                                        if (existing.id == changed.id) changed else existing
                                    },
                                )
                                savedMessage = null
                            },
                            onRemove = {
                                draft = draft.copy(classes = draft.classes.filterNot { it.id == classDraft.id })
                                savedMessage = null
                            },
                        )
                    }
                }
                item {
                    TextButton(
                        onClick = {
                            draft = draft.copy(
                                classes = draft.classes + ClassLevelDraft(
                                    id = Uuid.random(),
                                    name = "",
                                    level = "1",
                                    hitDieSides = "8",
                                    hitDiceRemaining = "1",
                                ),
                            )
                            savedMessage = null
                        },
                    ) {
                        Text("+ Añadir clase")
                    }
                }

                item { SectionTitle("Características") }
                item {
                    NumericPair(
                        "FUE", draft.strength, { draft = draft.copy(strength = it); savedMessage = null },
                        "DES", draft.dexterity, { draft = draft.copy(dexterity = it); savedMessage = null },
                    )
                }
                item {
                    NumericPair(
                        "CON", draft.constitution, { draft = draft.copy(constitution = it); savedMessage = null },
                        "INT", draft.intelligence, { draft = draft.copy(intelligence = it); savedMessage = null },
                    )
                }
                item {
                    NumericPair(
                        "SAB", draft.wisdom, { draft = draft.copy(wisdom = it); savedMessage = null },
                        "CAR", draft.charisma, { draft = draft.copy(charisma = it); savedMessage = null },
                    )
                }

                item { SectionTitle("Referencia de combate") }
                item {
                    NumericPair(
                        "CA", draft.armorClass, { draft = draft.copy(armorClass = it); savedMessage = null },
                        "Iniciativa", draft.initiativeModifier, { draft = draft.copy(initiativeModifier = it); savedMessage = null },
                    )
                }
                item {
                    NumericPair(
                        "PG máximos", draft.maxHp, { draft = draft.copy(maxHp = it); savedMessage = null },
                        "PG actuales", draft.currentHp, { draft = draft.copy(currentHp = it); savedMessage = null },
                    )
                }
                item {
                    NumericPair(
                        "PG temporales", draft.tempHp, { draft = draft.copy(tempHp = it); savedMessage = null },
                        "Velocidad", draft.speed, { draft = draft.copy(speed = it); savedMessage = null },
                    )
                }
                item {
                    NumericPair(
                        "Bonif. competencia", draft.proficiencyBonus, { draft = draft.copy(proficiencyBonus = it); savedMessage = null },
                        "Percepción pasiva", draft.passivePerception, { draft = draft.copy(passivePerception = it); savedMessage = null },
                    )
                }
                item {
                    OutlinedTextField(
                        value = draft.spellSaveDc,
                        onValueChange = { draft = draft.copy(spellSaveDc = it); savedMessage = null },
                        label = { Text("CD salvación de conjuros (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                item { SectionTitle("Tiradas de salvación") }
                item {
                    NumericPair(
                        "FUE", draft.strengthSave, { draft = draft.copy(strengthSave = it); savedMessage = null },
                        "DES", draft.dexteritySave, { draft = draft.copy(dexteritySave = it); savedMessage = null },
                    )
                }
                item {
                    NumericPair(
                        "CON", draft.constitutionSave, { draft = draft.copy(constitutionSave = it); savedMessage = null },
                        "INT", draft.intelligenceSave, { draft = draft.copy(intelligenceSave = it); savedMessage = null },
                    )
                }
                item {
                    NumericPair(
                        "SAB", draft.wisdomSave, { draft = draft.copy(wisdomSave = it); savedMessage = null },
                        "CAR", draft.charismaSave, { draft = draft.copy(charismaSave = it); savedMessage = null },
                    )
                }

                item { SectionTitle("Habilidades") }
                items(draft.skills, key = { it.key.name }) { skill ->
                    SkillEditor(
                        draft = skill,
                        onChange = { changed ->
                            draft = draft.copy(
                                skills = draft.skills.map { existing ->
                                    if (existing.key == changed.key) changed else existing
                                },
                            )
                            savedMessage = null
                        },
                    )
                }

                item {
                    Button(
                        onClick = {
                            val candidate = draft.toSheetOrNull(stored) ?: return@Button
                            stored = repository.saveCharacter(candidate)
                            draft = CharacterEditorDraft.from(stored)
                            savedMessage = "Guardado"
                        },
                        enabled = savable,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Guardar personaje")
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassLevelEditor(
    draft: ClassLevelDraft,
    onChange: (ClassLevelDraft) -> Unit,
    onRemove: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = draft.name,
                onValueChange = { onChange(draft.copy(name = it)) },
                label = { Text("Clase") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            NumericPair(
                "Nivel", draft.level, { onChange(draft.copy(level = it)) },
                "Dado de Golpe (dX)", draft.hitDieSides, { onChange(draft.copy(hitDieSides = it)) },
            )
            OutlinedTextField(
                value = draft.hitDiceRemaining,
                onValueChange = { onChange(draft.copy(hitDiceRemaining = it)) },
                label = { Text("Dados de Golpe disponibles") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            TextButton(onClick = onRemove) { Text("Quitar clase") }
        }
    }
}

@Composable
private fun SkillEditor(
    draft: SkillDraft,
    onChange: (SkillDraft) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(skillLabel(draft.key), modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = draft.modifier,
            onValueChange = { onChange(draft.copy(modifier = it)) },
            label = { Text("Mod.") },
            modifier = Modifier.width(92.dp),
            singleLine = true,
        )
        TextButton(onClick = { onChange(draft.copy(training = nextTraining(draft.training))) }) {
            Text(trainingLabel(draft.training))
        }
    }
}

@Composable
private fun NumericPair(
    firstLabel: String,
    firstValue: String,
    onFirstChange: (String) -> Unit,
    secondLabel: String,
    secondValue: String,
    onSecondChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedTextField(
            value = firstValue,
            onValueChange = onFirstChange,
            label = { Text(firstLabel) },
            modifier = Modifier.weight(1f),
            singleLine = true,
        )
        OutlinedTextField(
            value = secondValue,
            onValueChange = onSecondChange,
            label = { Text(secondLabel) },
            modifier = Modifier.weight(1f),
            singleLine = true,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge)
}

private data class ClassLevelDraft(
    val id: Uuid,
    val name: String,
    val level: String,
    val hitDieSides: String,
    val hitDiceRemaining: String,
)

private data class SkillDraft(
    val key: SkillKey,
    val modifier: String,
    val training: SkillTraining,
)

private data class CharacterEditorDraft(
    val name: String,
    val status: CharacterStatus,
    val strength: String,
    val dexterity: String,
    val constitution: String,
    val intelligence: String,
    val wisdom: String,
    val charisma: String,
    val armorClass: String,
    val maxHp: String,
    val currentHp: String,
    val tempHp: String,
    val initiativeModifier: String,
    val speed: String,
    val proficiencyBonus: String,
    val strengthSave: String,
    val dexteritySave: String,
    val constitutionSave: String,
    val intelligenceSave: String,
    val wisdomSave: String,
    val charismaSave: String,
    val passivePerception: String,
    val spellSaveDc: String,
    val classes: List<ClassLevelDraft>,
    val skills: List<SkillDraft>,
) {
    fun toSheetOrNull(original: CharacterSheet): CharacterSheet? {
        val normalizedName = name.trim().takeIf { it.isNotEmpty() } ?: return null
        fun parsed(value: String): Int? = value.trim().toIntOrNull()

        val parsedClasses = classes.mapIndexed { index, classDraft ->
            val className = classDraft.name.trim().takeIf { it.isNotEmpty() } ?: return null
            val level = parsed(classDraft.level)?.takeIf { it > 0 } ?: return null
            val die = parsed(classDraft.hitDieSides)?.takeIf { it > 0 } ?: return null
            val remaining = parsed(classDraft.hitDiceRemaining)?.takeIf { it >= 0 } ?: return null
            CharacterClassLevel(classDraft.id, className, level, die, remaining, index)
        }

        val parsedSkills = skills.map { skill ->
            CharacterSkill(skill.key, parsed(skill.modifier) ?: return null, skill.training)
        }

        val spellDc = if (spellSaveDc.isBlank()) null else parsed(spellSaveDc) ?: return null

        return original.copy(
            name = normalizedName,
            status = status,
            strength = parsed(strength) ?: return null,
            dexterity = parsed(dexterity) ?: return null,
            constitution = parsed(constitution) ?: return null,
            intelligence = parsed(intelligence) ?: return null,
            wisdom = parsed(wisdom) ?: return null,
            charisma = parsed(charisma) ?: return null,
            armorClass = parsed(armorClass) ?: return null,
            maxHp = parsed(maxHp) ?: return null,
            currentHp = parsed(currentHp) ?: return null,
            tempHp = parsed(tempHp) ?: return null,
            initiativeModifier = parsed(initiativeModifier) ?: return null,
            speed = parsed(speed) ?: return null,
            proficiencyBonus = parsed(proficiencyBonus) ?: return null,
            strengthSave = parsed(strengthSave) ?: return null,
            dexteritySave = parsed(dexteritySave) ?: return null,
            constitutionSave = parsed(constitutionSave) ?: return null,
            intelligenceSave = parsed(intelligenceSave) ?: return null,
            wisdomSave = parsed(wisdomSave) ?: return null,
            charismaSave = parsed(charismaSave) ?: return null,
            passivePerception = parsed(passivePerception) ?: return null,
            spellSaveDc = spellDc,
            classes = parsedClasses,
            skills = parsedSkills,
        )
    }

    companion object {
        fun from(sheet: CharacterSheet) = CharacterEditorDraft(
            name = sheet.name,
            status = sheet.status,
            strength = sheet.strength.toString(),
            dexterity = sheet.dexterity.toString(),
            constitution = sheet.constitution.toString(),
            intelligence = sheet.intelligence.toString(),
            wisdom = sheet.wisdom.toString(),
            charisma = sheet.charisma.toString(),
            armorClass = sheet.armorClass.toString(),
            maxHp = sheet.maxHp.toString(),
            currentHp = sheet.currentHp.toString(),
            tempHp = sheet.tempHp.toString(),
            initiativeModifier = sheet.initiativeModifier.toString(),
            speed = sheet.speed.toString(),
            proficiencyBonus = sheet.proficiencyBonus.toString(),
            strengthSave = sheet.strengthSave.toString(),
            dexteritySave = sheet.dexteritySave.toString(),
            constitutionSave = sheet.constitutionSave.toString(),
            intelligenceSave = sheet.intelligenceSave.toString(),
            wisdomSave = sheet.wisdomSave.toString(),
            charismaSave = sheet.charismaSave.toString(),
            passivePerception = sheet.passivePerception.toString(),
            spellSaveDc = sheet.spellSaveDc?.toString().orEmpty(),
            classes = sheet.classes.map {
                ClassLevelDraft(it.id, it.name, it.level.toString(), it.hitDieSides.toString(), it.hitDiceRemaining.toString())
            },
            skills = sheet.skills.map { SkillDraft(it.key, it.modifier.toString(), it.training) },
        )
    }
}

private fun nextStatus(status: CharacterStatus): CharacterStatus = when (status) {
    CharacterStatus.ACTIVE -> CharacterStatus.INACTIVE
    CharacterStatus.INACTIVE -> CharacterStatus.RETIRED
    CharacterStatus.RETIRED -> CharacterStatus.DEAD
    CharacterStatus.DEAD -> CharacterStatus.ACTIVE
}

private fun statusLabel(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}

private fun nextTraining(training: SkillTraining): SkillTraining = when (training) {
    SkillTraining.NONE -> SkillTraining.PROFICIENT
    SkillTraining.PROFICIENT -> SkillTraining.EXPERTISE
    SkillTraining.EXPERTISE -> SkillTraining.NONE
}

private fun trainingLabel(training: SkillTraining): String = when (training) {
    SkillTraining.NONE -> "—"
    SkillTraining.PROFICIENT -> "Competente"
    SkillTraining.EXPERTISE -> "Pericia"
}

private fun skillLabel(key: SkillKey): String = when (key) {
    SkillKey.ACROBATICS -> "Acrobacias"
    SkillKey.ANIMAL_HANDLING -> "Trato con Animales"
    SkillKey.ARCANA -> "Arcanos"
    SkillKey.ATHLETICS -> "Atletismo"
    SkillKey.DECEPTION -> "Engaño"
    SkillKey.HISTORY -> "Historia"
    SkillKey.INSIGHT -> "Perspicacia"
    SkillKey.INTIMIDATION -> "Intimidación"
    SkillKey.INVESTIGATION -> "Investigación"
    SkillKey.MEDICINE -> "Medicina"
    SkillKey.NATURE -> "Naturaleza"
    SkillKey.PERCEPTION -> "Percepción"
    SkillKey.PERFORMANCE -> "Interpretación"
    SkillKey.PERSUASION -> "Persuasión"
    SkillKey.RELIGION -> "Religión"
    SkillKey.SLEIGHT_OF_HAND -> "Juego de Manos"
    SkillKey.STEALTH -> "Sigilo"
    SkillKey.SURVIVAL -> "Supervivencia"
}

private fun formatSavedAt(epochSeconds: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).format(formatter)
}
