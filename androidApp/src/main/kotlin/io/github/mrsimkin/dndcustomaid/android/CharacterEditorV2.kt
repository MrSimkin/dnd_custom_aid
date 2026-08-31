package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

/**
 * Second manual-QA layout for the Phase 4 character foundation.
 *
 * This intentionally keeps the accepted durable character model unchanged while addressing
 * the concrete phone QA findings recorded on this branch: input filtering, IME visibility,
 * compactness, easier training controls, constrained hit-die choices and landscape width use.
 * Open product choices such as final font family, application font-size settings and the final
 * tab/accordion navigation model remain deliberately unresolved.
 */
@Composable
internal fun CharacterEditorScreenV2(
    characterId: Uuid,
    repository: CharacterRepository,
    onBack: () -> Unit,
) {
    var stored by remember(characterId) {
        mutableStateOf(requireNotNull(repository.character(characterId)))
    }
    var draft by remember(characterId) { mutableStateOf(CharacterEditorDraftV2.from(stored)) }
    var savedMessage by remember { mutableStateOf<String?>(null) }
    val savable = draft.toSheetOrNull(stored) != null

    fun markChanged() {
        savedMessage = null
    }

    fun save() {
        val candidate = draft.toSheetOrNull(stored) ?: return
        stored = repository.saveCharacter(candidate)
        draft = CharacterEditorDraftV2.from(stored)
        savedMessage = "Guardado"
    }

    Scaffold { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .widthIn(max = 1080.dp)
                    .fillMaxSize(),
            ) {
                val wide = maxWidth >= 700.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(horizontal = if (wide) 18.dp else 10.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    EditorTopBar(
                        onBack = onBack,
                        onSave = ::save,
                        savable = savable,
                    )

                    IdentityCard(
                        draft = draft,
                        stored = stored,
                        savedMessage = savedMessage,
                        onNameChange = {
                            draft = draft.copy(name = it)
                            markChanged()
                        },
                        onStatusChange = {
                            draft = draft.copy(status = it)
                            markChanged()
                        },
                    )

                    if (wide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                ClassesCard(
                                    classes = draft.classes,
                                    onClassesChange = {
                                        draft = draft.copy(classes = it)
                                        markChanged()
                                    },
                                )
                                AbilitiesCard(
                                    draft = draft,
                                    onDraftChange = {
                                        draft = it
                                        markChanged()
                                    },
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                CombatCard(
                                    draft = draft,
                                    onDraftChange = {
                                        draft = it
                                        markChanged()
                                    },
                                )
                                SavesCard(
                                    draft = draft,
                                    onDraftChange = {
                                        draft = it
                                        markChanged()
                                    },
                                )
                            }
                        }
                    } else {
                        ClassesCard(
                            classes = draft.classes,
                            onClassesChange = {
                                draft = draft.copy(classes = it)
                                markChanged()
                            },
                        )
                        AbilitiesCard(
                            draft = draft,
                            onDraftChange = {
                                draft = it
                                markChanged()
                            },
                        )
                        CombatCard(
                            draft = draft,
                            onDraftChange = {
                                draft = it
                                markChanged()
                            },
                        )
                        SavesCard(
                            draft = draft,
                            onDraftChange = {
                                draft = it
                                markChanged()
                            },
                        )
                    }

                    SkillsCard(
                        skills = draft.skills,
                        wide = wide,
                        onSkillChange = { changed ->
                            draft = draft.copy(
                                skills = draft.skills.map { existing ->
                                    if (existing.key == changed.key) changed else existing
                                },
                            )
                            markChanged()
                        },
                    )

                    Button(
                        onClick = ::save,
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
private fun EditorTopBar(
    onBack: () -> Unit,
    onSave: () -> Unit,
    savable: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onBack) { Text("← Personajes") }
        Button(onClick = onSave, enabled = savable) { Text("Guardar") }
    }
}

@Composable
private fun IdentityCard(
    draft: CharacterEditorDraftV2,
    stored: CharacterSheet,
    savedMessage: String?,
    onNameChange: (String) -> Unit,
    onStatusChange: (CharacterStatus) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ficha de personaje", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Último guardado: ${formatSavedAtV2(stored.updatedAtEpochSeconds)}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                savedMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = onNameChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                StatusSelector(
                    status = draft.status,
                    onStatusChange = onStatusChange,
                )
            }
        }
    }
}

@Composable
private fun StatusSelector(
    status: CharacterStatus,
    onStatusChange: (CharacterStatus) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(statusLabelV2(status))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CharacterStatus.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(statusLabelV2(option)) },
                    onClick = {
                        onStatusChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun ClassesCard(
    classes: List<ClassLevelDraftV2>,
    onClassesChange: (List<ClassLevelDraftV2>) -> Unit,
) {
    SectionCard("Clases y Dados de Golpe") {
        if (classes.isEmpty()) {
            Text("Sin clases registradas.", style = MaterialTheme.typography.bodySmall)
        } else {
            classes.forEach { classDraft ->
                CompactClassEditor(
                    draft = classDraft,
                    onChange = { changed ->
                        onClassesChange(
                            classes.map { existing ->
                                if (existing.id == changed.id) changed else existing
                            },
                        )
                    },
                    onRemove = {
                        onClassesChange(classes.filterNot { it.id == classDraft.id })
                    },
                )
            }
        }

        TextButton(
            onClick = {
                onClassesChange(
                    classes + ClassLevelDraftV2(
                        id = Uuid.random(),
                        name = "",
                        level = "1",
                        hitDieSides = "8",
                        hitDiceRemaining = "1",
                    ),
                )
            },
        ) {
            Text("+ Añadir clase")
        }
    }
}

@Composable
private fun CompactClassEditor(
    draft: ClassLevelDraftV2,
    onChange: (ClassLevelDraftV2) -> Unit,
    onRemove: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            val wide = maxWidth >= 520.dp
            if (wide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = draft.name,
                        onValueChange = { onChange(draft.copy(name = it)) },
                        label = { Text("Clase / personalizada") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    UnsignedIntField(
                        label = "Nivel",
                        value = draft.level,
                        onValueChange = { onChange(draft.copy(level = it)) },
                        modifier = Modifier.width(82.dp),
                    )
                    HitDieSelector(
                        value = draft.hitDieSides,
                        onValueChange = { onChange(draft.copy(hitDieSides = it)) },
                    )
                    UnsignedIntField(
                        label = "Restan",
                        value = draft.hitDiceRemaining,
                        onValueChange = { onChange(draft.copy(hitDiceRemaining = it)) },
                        modifier = Modifier.width(88.dp),
                    )
                    TextButton(onClick = onRemove) { Text("Quitar") }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        OutlinedTextField(
                            value = draft.name,
                            onValueChange = { onChange(draft.copy(name = it)) },
                            label = { Text("Clase / personalizada") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        UnsignedIntField(
                            label = "Nivel",
                            value = draft.level,
                            onValueChange = { onChange(draft.copy(level = it)) },
                            modifier = Modifier.width(82.dp),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HitDieSelector(
                            value = draft.hitDieSides,
                            onValueChange = { onChange(draft.copy(hitDieSides = it)) },
                        )
                        UnsignedIntField(
                            label = "Dados restantes",
                            value = draft.hitDiceRemaining,
                            onValueChange = { onChange(draft.copy(hitDiceRemaining = it)) },
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = onRemove) { Text("Quitar") }
                    }
                }
            }
        }
    }
}

@Composable
private fun HitDieSelector(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val commonDice = listOf("4", "6", "8", "10", "12")
    var expanded by remember { mutableStateOf(false) }
    var custom by remember(value) { mutableStateOf(value !in commonDice) }

    if (custom) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UnsignedIntField(
                label = "Dado dX",
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.width(92.dp),
            )
            TextButton(onClick = { custom = false; expanded = true }) {
                Text("Lista")
            }
        }
    } else {
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(if (value.isBlank()) "Dado" else "d$value")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                commonDice.forEach { sides ->
                    DropdownMenuItem(
                        text = { Text("d$sides") },
                        onClick = {
                            onValueChange(sides)
                            custom = false
                            expanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("Otro / personalizado") },
                    onClick = {
                        custom = true
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AbilitiesCard(
    draft: CharacterEditorDraftV2,
    onDraftChange: (CharacterEditorDraftV2) -> Unit,
) {
    SectionCard("Características") {
        NumericTriple(
            "FUE", draft.strength, { onDraftChange(draft.copy(strength = it)) },
            "DES", draft.dexterity, { onDraftChange(draft.copy(dexterity = it)) },
            "CON", draft.constitution, { onDraftChange(draft.copy(constitution = it)) },
            signed = false,
        )
        NumericTriple(
            "INT", draft.intelligence, { onDraftChange(draft.copy(intelligence = it)) },
            "SAB", draft.wisdom, { onDraftChange(draft.copy(wisdom = it)) },
            "CAR", draft.charisma, { onDraftChange(draft.copy(charisma = it)) },
            signed = false,
        )
    }
}

@Composable
private fun CombatCard(
    draft: CharacterEditorDraftV2,
    onDraftChange: (CharacterEditorDraftV2) -> Unit,
) {
    SectionCard("Referencia de combate") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            UnsignedIntField("CA", draft.armorClass, { onDraftChange(draft.copy(armorClass = it)) }, Modifier.weight(1f))
            SignedIntField("Iniciativa", draft.initiativeModifier, { onDraftChange(draft.copy(initiativeModifier = it)) }, Modifier.weight(1f))
            UnsignedIntField("Velocidad", draft.speed, { onDraftChange(draft.copy(speed = it)) }, Modifier.weight(1f))
        }
        NumericTriple(
            "PG máx.", draft.maxHp, { onDraftChange(draft.copy(maxHp = it)) },
            "PG act.", draft.currentHp, { onDraftChange(draft.copy(currentHp = it)) },
            "PG temp.", draft.tempHp, { onDraftChange(draft.copy(tempHp = it)) },
            signed = false,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SignedIntField("Compet.", draft.proficiencyBonus, { onDraftChange(draft.copy(proficiencyBonus = it)) }, Modifier.weight(1f))
            UnsignedIntField("Perc. pasiva", draft.passivePerception, { onDraftChange(draft.copy(passivePerception = it)) }, Modifier.weight(1f))
            OptionalUnsignedIntField("CD conjuros", draft.spellSaveDc, { onDraftChange(draft.copy(spellSaveDc = it)) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SavesCard(
    draft: CharacterEditorDraftV2,
    onDraftChange: (CharacterEditorDraftV2) -> Unit,
) {
    SectionCard("Tiradas de salvación") {
        NumericTriple(
            "FUE", draft.strengthSave, { onDraftChange(draft.copy(strengthSave = it)) },
            "DES", draft.dexteritySave, { onDraftChange(draft.copy(dexteritySave = it)) },
            "CON", draft.constitutionSave, { onDraftChange(draft.copy(constitutionSave = it)) },
            signed = true,
        )
        NumericTriple(
            "INT", draft.intelligenceSave, { onDraftChange(draft.copy(intelligenceSave = it)) },
            "SAB", draft.wisdomSave, { onDraftChange(draft.copy(wisdomSave = it)) },
            "CAR", draft.charismaSave, { onDraftChange(draft.copy(charismaSave = it)) },
            signed = true,
        )
    }
}

@Composable
private fun SkillsCard(
    skills: List<SkillDraftV2>,
    wide: Boolean,
    onSkillChange: (SkillDraftV2) -> Unit,
) {
    SectionCard("Habilidades") {
        if (wide) {
            val split = (skills.size + 1) / 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    skills.take(split).forEach { SkillEditorV2(it, onSkillChange) }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    skills.drop(split).forEach { SkillEditorV2(it, onSkillChange) }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                skills.forEach { SkillEditorV2(it, onSkillChange) }
            }
        }
    }
}

@Composable
private fun SkillEditorV2(
    draft: SkillDraftV2,
    onChange: (SkillDraftV2) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            skillLabelV2(draft.key),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
        SignedIntField(
            label = "Mod.",
            value = draft.modifier,
            onValueChange = { onChange(draft.copy(modifier = it)) },
            modifier = Modifier.width(78.dp),
        )
        TrainingSelector(
            training = draft.training,
            onTrainingChange = { onChange(draft.copy(training = it)) },
        )
    }
}

@Composable
private fun TrainingSelector(
    training: SkillTraining,
    onTrainingChange: (SkillTraining) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(112.dp),
        ) {
            Text(trainingShortLabelV2(training))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SkillTraining.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(trainingLongLabelV2(option)) },
                    onClick = {
                        onTrainingChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun NumericTriple(
    firstLabel: String,
    firstValue: String,
    onFirstChange: (String) -> Unit,
    secondLabel: String,
    secondValue: String,
    onSecondChange: (String) -> Unit,
    thirdLabel: String,
    thirdValue: String,
    onThirdChange: (String) -> Unit,
    signed: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (signed) {
            SignedIntField(firstLabel, firstValue, onFirstChange, Modifier.weight(1f))
            SignedIntField(secondLabel, secondValue, onSecondChange, Modifier.weight(1f))
            SignedIntField(thirdLabel, thirdValue, onThirdChange, Modifier.weight(1f))
        } else {
            UnsignedIntField(firstLabel, firstValue, onFirstChange, Modifier.weight(1f))
            UnsignedIntField(secondLabel, secondValue, onSecondChange, Modifier.weight(1f))
            UnsignedIntField(thirdLabel, thirdValue, onThirdChange, Modifier.weight(1f))
        }
    }
}

@Composable
private fun UnsignedIntField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(sanitizeUnsignedInt(it)) },
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@Composable
private fun OptionalUnsignedIntField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) = UnsignedIntField(label, value, onValueChange, modifier)

@Composable
private fun SignedIntField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(sanitizeSignedInt(it)) },
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

private fun sanitizeUnsignedInt(value: String): String = value.filter { it.isDigit() }

private fun sanitizeSignedInt(value: String): String {
    val trimmed = value.filterNot { it.isWhitespace() }
    if (trimmed.isEmpty()) return ""
    val sign = trimmed.firstOrNull()?.takeIf { it == '+' || it == '-' }
    val digits = trimmed.drop(if (sign == null) 0 else 1).filter { it.isDigit() }
    return buildString {
        if (sign != null) append(sign)
        append(digits)
    }
}

private data class ClassLevelDraftV2(
    val id: Uuid,
    val name: String,
    val level: String,
    val hitDieSides: String,
    val hitDiceRemaining: String,
)

private data class SkillDraftV2(
    val key: SkillKey,
    val modifier: String,
    val training: SkillTraining,
)

private data class CharacterEditorDraftV2(
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
    val classes: List<ClassLevelDraftV2>,
    val skills: List<SkillDraftV2>,
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
        fun from(sheet: CharacterSheet) = CharacterEditorDraftV2(
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
                ClassLevelDraftV2(it.id, it.name, it.level.toString(), it.hitDieSides.toString(), it.hitDiceRemaining.toString())
            },
            skills = sheet.skills.map { SkillDraftV2(it.key, it.modifier.toString(), it.training) },
        )
    }
}

private fun statusLabelV2(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}

private fun trainingShortLabelV2(training: SkillTraining): String = when (training) {
    SkillTraining.NONE -> "—"
    SkillTraining.PROFICIENT -> "Competente"
    SkillTraining.EXPERTISE -> "Pericia"
}

private fun trainingLongLabelV2(training: SkillTraining): String = when (training) {
    SkillTraining.NONE -> "Sin competencia"
    SkillTraining.PROFICIENT -> "Competente"
    SkillTraining.EXPERTISE -> "Pericia"
}

private fun skillLabelV2(key: SkillKey): String = when (key) {
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

private fun formatSavedAtV2(epochSeconds: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).format(formatter)
}
