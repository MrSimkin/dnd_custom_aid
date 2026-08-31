package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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

private enum class CharacterTab(val label: String) {
    OVERVIEW("Resumen"),
    SKILLS("Habilidades"),
}

private enum class AbilityKey(val abbreviation: String) {
    STRENGTH("FUE"),
    DEXTERITY("DES"),
    CONSTITUTION("CON"),
    INTELLIGENCE("INT"),
    WISDOM("SAB"),
    CHARISMA("CAR"),
}

/*
 * Exact localized class names from the official Spanish SRD 5.2.1 class index.
 * Artífice is deliberately added outside the SRD list under owner decision D-0045.
 */
private val srd521ClassNames = listOf(
    "Bárbaro",
    "Bardo",
    "Brujo",
    "Clérigo",
    "Druida",
    "Explorador",
    "Guerrero",
    "Hechicero",
    "Mago",
    "Monje",
    "Paladín",
    "Pícaro",
)
private val selectableClassNames = srd521ClassNames + "Artífice"

@Composable
internal fun CharacterEditorScreenV3(
    characterId: Uuid,
    repository: CharacterRepository,
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
) {
    var stored by remember(characterId) {
        mutableStateOf(requireNotNull(repository.character(characterId)))
    }
    var draft by rememberSaveable(
        characterId.toString(),
        stateSaver = CharacterEditorDraftV3.Saver,
    ) {
        mutableStateOf(CharacterEditorDraftV3.from(stored))
    }
    var savedMessage by rememberSaveable(characterId.toString()) { mutableStateOf<String?>(null) }
    var selectedTabName by rememberSaveable(characterId.toString()) {
        mutableStateOf(CharacterTab.OVERVIEW.name)
    }
    val selectedTab = runCatching { CharacterTab.valueOf(selectedTabName) }
        .getOrDefault(CharacterTab.OVERVIEW)
    val savable = draft.toSheetOrNull(stored) != null

    fun markChanged() {
        savedMessage = null
    }

    fun updateDraft(updated: CharacterEditorDraftV3) {
        draft = updated
        markChanged()
    }

    fun save() {
        val candidate = draft.toSheetOrNull(stored) ?: return
        stored = repository.saveCharacter(candidate)
        draft = CharacterEditorDraftV3.from(stored)
        savedMessage = "Guardado"
    }

    Scaffold { scaffoldPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
        ) {
            val wide = maxWidth >= 700.dp

            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                EditorHeaderV3(
                    characterName = draft.name,
                    stored = stored,
                    savedMessage = savedMessage,
                    savable = savable,
                    onBack = onBack,
                    onSave = ::save,
                    onOpenSettings = onOpenSettings,
                )

                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CharacterTab.entries.forEach { tab ->
                        Tab(
                            selected = tab == selectedTab,
                            onClick = { selectedTabName = tab.name },
                            text = { Text(tab.label) },
                        )
                    }
                }

                when (selectedTab) {
                    CharacterTab.OVERVIEW -> OverviewTabV3(
                        draft = draft,
                        stored = stored,
                        wide = wide,
                        onDraftChange = ::updateDraft,
                    )

                    CharacterTab.SKILLS -> SkillsTabV3(
                        draft = draft,
                        wide = wide,
                        skillLayoutChoice = preferences.skillLayoutChoice,
                        onSkillLayoutChange = {
                            onPreferencesChange(preferences.copy(skillLayoutChoice = it))
                        },
                        onDraftChange = ::updateDraft,
                    )
                }
            }
        }
    }
}

@Composable
private fun EditorHeaderV3(
    characterName: String,
    stored: CharacterSheet,
    savedMessage: String?,
    savable: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onBack) { Text("←") }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                characterName.ifBlank { "Ficha de personaje" },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
            )
            Text(
                savedMessage ?: "Guardado: ${formatSavedAtV3(stored.updatedAtEpochSeconds)}",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }
        TextButton(onClick = onOpenSettings) { Text("⚙") }
        Button(onClick = onSave, enabled = savable) { Text("Guardar") }
    }
}

@Composable
private fun OverviewTabV3(
    draft: CharacterEditorDraftV3,
    stored: CharacterSheet,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV3) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = if (wide) 14.dp else 7.dp,
            end = if (wide) 14.dp else 7.dp,
            top = 6.dp,
            bottom = 160.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item {
            IdentityCardV3(
                draft = draft,
                stored = stored,
                onDraftChange = onDraftChange,
            )
        }
        item {
            ClassesCardV3(
                classes = draft.classes,
                onClassesChange = { onDraftChange(draft.copy(classes = it)) },
            )
        }
        item {
            AbilitiesCardV3(
                draft = draft,
                onDraftChange = onDraftChange,
            )
        }
        item {
            CombatCardV3(
                draft = draft,
                wide = wide,
                onDraftChange = onDraftChange,
            )
        }
    }
}

@Composable
private fun IdentityCardV3(
    draft: CharacterEditorDraftV3,
    stored: CharacterSheet,
    onDraftChange: (CharacterEditorDraftV3) -> Unit,
) {
    SectionCardV3(title = "Personaje") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = draft.name,
                onValueChange = { onDraftChange(draft.copy(name = it)) },
                label = { Text("Nombre") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            StatusSelectorV3(
                status = draft.status,
                onStatusChange = { onDraftChange(draft.copy(status = it)) },
            )
        }
        Text(
            "Nivel total ${draft.classes.sumOf { it.level.toIntOrNull() ?: 0 }} · Último guardado ${formatSavedAtV3(stored.updatedAtEpochSeconds)}",
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun StatusSelectorV3(
    status: CharacterStatus,
    onStatusChange: (CharacterStatus) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(statusLabelV3(status))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            CharacterStatus.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(statusLabelV3(option)) },
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
private fun ClassesCardV3(
    classes: List<ClassLevelDraftV3>,
    onClassesChange: (List<ClassLevelDraftV3>) -> Unit,
) {
    SectionCardV3(title = "Clases y Dados de Golpe") {
        if (classes.isEmpty()) {
            Text("Sin clases registradas.", style = MaterialTheme.typography.bodySmall)
        }

        classes.forEach { classDraft ->
            ClassRowV3(
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

        TextButton(
            onClick = {
                onClassesChange(
                    classes + ClassLevelDraftV3(
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
private fun ClassRowV3(
    draft: ClassLevelDraftV3,
    onChange: (ClassLevelDraftV3) -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            ClassSelectorV3(
                draft = draft,
                onChange = onChange,
                modifier = Modifier.weight(1f),
            )
            CompactIntFieldV3(
                label = "Nv.",
                value = draft.level,
                onValueChange = { onChange(draft.copy(level = it)) },
                modifier = Modifier.width(45.dp),
            )
            CompactIntFieldV3(
                label = "DG",
                value = draft.hitDiceRemaining,
                onValueChange = { onChange(draft.copy(hitDiceRemaining = it)) },
                modifier = Modifier.width(45.dp),
            )
            HitDieSelectorV3(
                value = draft.hitDieSides,
                onValueChange = { onChange(draft.copy(hitDieSides = it)) },
            )
            TextButton(
                onClick = onRemove,
                modifier = Modifier.width(38.dp),
            ) {
                Text("×")
            }
        }
    }
}

@Composable
private fun ClassSelectorV3(
    draft: ClassLevelDraftV3,
    onChange: (ClassLevelDraftV3) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var customMode by remember(draft.id, draft.name) {
        mutableStateOf(draft.name.isNotBlank() && draft.name !in selectableClassNames)
    }

    Column(modifier = modifier) {
        Text("Clase", style = MaterialTheme.typography.labelSmall)
        if (customMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompactTextFieldV3(
                    value = draft.name,
                    onValueChange = { onChange(draft.copy(name = it)) },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    onClick = {
                        customMode = false
                        expanded = true
                    },
                    modifier = Modifier.width(32.dp),
                ) {
                    Text("▾")
                }
            }
        } else {
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        draft.name.ifBlank { "Elegir" },
                        maxLines = 1,
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    selectableClassNames.forEach { className ->
                        DropdownMenuItem(
                            text = { Text(className) },
                            onClick = {
                                onChange(draft.copy(name = className))
                                customMode = false
                                expanded = false
                            },
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Otro") },
                        onClick = {
                            onChange(
                                draft.copy(
                                    name = if (draft.name in selectableClassNames) "" else draft.name,
                                ),
                            )
                            customMode = true
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun HitDieSelectorV3(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val commonDice = listOf("4", "6", "8", "10", "12")
    var expanded by remember { mutableStateOf(false) }
    var customMode by remember(value) { mutableStateOf(value.isNotBlank() && value !in commonDice) }

    Column(modifier = Modifier.width(58.dp)) {
        Text("Tipo", style = MaterialTheme.typography.labelSmall)
        if (customMode) {
            CompactIntFieldV3(
                label = "",
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
            )
            TextButton(
                onClick = {
                    customMode = false
                    expanded = true
                },
            ) {
                Text("Lista")
            }
        } else {
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (value.isBlank()) "d?" else "d$value")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    commonDice.forEach { sides ->
                        DropdownMenuItem(
                            text = { Text("d$sides") },
                            onClick = {
                                onValueChange(sides)
                                customMode = false
                                expanded = false
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Otro") },
                        onClick = {
                            customMode = true
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AbilitiesCardV3(
    draft: CharacterEditorDraftV3,
    onDraftChange: (CharacterEditorDraftV3) -> Unit,
) {
    SectionCardV3(title = "Características") {
        AbilitiesRowV3(
            draft = draft,
            onDraftChange = onDraftChange,
        )
    }
}

@Composable
private fun AbilitiesRowV3(
    draft: CharacterEditorDraftV3,
    onDraftChange: (CharacterEditorDraftV3) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        AbilityKey.entries.forEach { ability ->
            CompactIntFieldV3(
                label = ability.abbreviation,
                value = draft.abilityValue(ability),
                onValueChange = {
                    onDraftChange(draft.withAbilityValue(ability, it))
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CombatCardV3(
    draft: CharacterEditorDraftV3,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV3) -> Unit,
) {
    SectionCardV3(title = "Referencia de combate") {
        if (wide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                CompactIntFieldV3("CA", draft.armorClass, { onDraftChange(draft.copy(armorClass = it)) }, Modifier.weight(1f))
                CompactIntFieldV3("Inic.", draft.initiativeModifier, { onDraftChange(draft.copy(initiativeModifier = it)) }, Modifier.weight(1f), signed = true)
                CompactIntFieldV3("Vel.", draft.speed, { onDraftChange(draft.copy(speed = it)) }, Modifier.weight(1f))
                CompactIntFieldV3("PG máx.", draft.maxHp, { onDraftChange(draft.copy(maxHp = it)) }, Modifier.weight(1f))
                CompactIntFieldV3("PG act.", draft.currentHp, { onDraftChange(draft.copy(currentHp = it)) }, Modifier.weight(1f), signed = true)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                CompactIntFieldV3("PG temp.", draft.tempHp, { onDraftChange(draft.copy(tempHp = it)) }, Modifier.weight(1f), signed = true)
                CompactIntFieldV3("Comp.", draft.proficiencyBonus, { onDraftChange(draft.copy(proficiencyBonus = it)) }, Modifier.weight(1f), signed = true)
                CompactIntFieldV3("Perc. pas.", draft.passivePerception, { onDraftChange(draft.copy(passivePerception = it)) }, Modifier.weight(1f), signed = true)
                CompactIntFieldV3("CD conj.", draft.spellSaveDc, { onDraftChange(draft.copy(spellSaveDc = it)) }, Modifier.weight(1f), allowBlank = true)
                Spacer(modifier = Modifier.weight(1f))
            }
        } else {
            CompactTripleRowV3(
                "CA", draft.armorClass, { onDraftChange(draft.copy(armorClass = it)) },
                "Inic.", draft.initiativeModifier, { onDraftChange(draft.copy(initiativeModifier = it)) },
                "Vel.", draft.speed, { onDraftChange(draft.copy(speed = it)) },
                signedSecond = true,
            )
            CompactTripleRowV3(
                "PG máx.", draft.maxHp, { onDraftChange(draft.copy(maxHp = it)) },
                "PG act.", draft.currentHp, { onDraftChange(draft.copy(currentHp = it)) },
                "PG temp.", draft.tempHp, { onDraftChange(draft.copy(tempHp = it)) },
                signedSecond = true,
                signedThird = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                CompactIntFieldV3(
                    "Comp.",
                    draft.proficiencyBonus,
                    { onDraftChange(draft.copy(proficiencyBonus = it)) },
                    Modifier.weight(1f),
                    signed = true,
                )
                CompactIntFieldV3(
                    "Perc. pas.",
                    draft.passivePerception,
                    { onDraftChange(draft.copy(passivePerception = it)) },
                    Modifier.weight(1f),
                    signed = true,
                )
                CompactIntFieldV3(
                    "CD conj.",
                    draft.spellSaveDc,
                    { onDraftChange(draft.copy(spellSaveDc = it)) },
                    Modifier.weight(1f),
                    allowBlank = true,
                )
            }
        }
    }
}

@Composable
private fun SkillsTabV3(
    draft: CharacterEditorDraftV3,
    wide: Boolean,
    skillLayoutChoice: SkillLayoutChoice,
    onSkillLayoutChange: (SkillLayoutChoice) -> Unit,
    onDraftChange: (CharacterEditorDraftV3) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = if (wide) 14.dp else 7.dp,
            end = if (wide) 14.dp else 7.dp,
            top = 6.dp,
            bottom = 180.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item {
            SkillViewSelectorV3(
                current = skillLayoutChoice,
                onChange = onSkillLayoutChange,
            )
        }

        when (skillLayoutChoice) {
            SkillLayoutChoice.BY_SKILLS -> {
                item {
                    AbilitiesCardV3(
                        draft = draft,
                        onDraftChange = onDraftChange,
                    )
                }
                item {
                    SavesCardBySkillsV3(
                        draft = draft,
                        onDraftChange = onDraftChange,
                    )
                }
                item {
                    SkillsListCardV3(
                        skills = draft.skills,
                        wide = wide,
                        onSkillChange = { changed ->
                            onDraftChange(
                                draft.copy(
                                    skills = draft.skills.map { existing ->
                                        if (existing.key == changed.key) changed else existing
                                    },
                                ),
                            )
                        },
                    )
                }
            }

            SkillLayoutChoice.BY_ATTRIBUTE -> {
                item {
                    AbilityGroupsCardV3(
                        draft = draft,
                        wide = wide,
                        onDraftChange = onDraftChange,
                    )
                }
            }
        }
    }
}

@Composable
private fun SkillViewSelectorV3(
    current: SkillLayoutChoice,
    onChange: (SkillLayoutChoice) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Organización de la ficha", style = MaterialTheme.typography.titleSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text("⚙ ${current.label}")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                SkillLayoutChoice.entries.forEach { choice ->
                    DropdownMenuItem(
                        text = { Text(choice.label) },
                        onClick = {
                            onChange(choice)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SavesCardBySkillsV3(
    draft: CharacterEditorDraftV3,
    onDraftChange: (CharacterEditorDraftV3) -> Unit,
) {
    SectionCardV3(title = "Tiradas de salvación") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            AbilityKey.entries.forEach { ability ->
                CompactIntFieldV3(
                    label = ability.abbreviation,
                    value = draft.saveValue(ability),
                    onValueChange = {
                        onDraftChange(draft.withSaveValue(ability, it))
                    },
                    modifier = Modifier.weight(1f),
                    signed = true,
                )
            }
        }
    }
}

@Composable
private fun SkillsListCardV3(
    skills: List<SkillDraftV3>,
    wide: Boolean,
    onSkillChange: (SkillDraftV3) -> Unit,
) {
    SectionCardV3(title = "Habilidades") {
        if (wide) {
            val midpoint = (skills.size + 1) / 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    skills.take(midpoint).forEachIndexed { index, skill ->
                        CompactSkillRowV3(skill, onSkillChange)
                        if (index < midpoint - 1) HorizontalDivider()
                    }
                }
                val secondHalf = skills.drop(midpoint)
                Column(modifier = Modifier.weight(1f)) {
                    secondHalf.forEachIndexed { index, skill ->
                        CompactSkillRowV3(skill, onSkillChange)
                        if (index < secondHalf.lastIndex) HorizontalDivider()
                    }
                }
            }
        } else {
            skills.forEachIndexed { index, skill ->
                CompactSkillRowV3(skill, onSkillChange)
                if (index < skills.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
private fun CompactSkillRowV3(
    skill: SkillDraftV3,
    onSkillChange: (SkillDraftV3) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${skillLabelV3(skill.key)} (${skillAbility(skill.key).abbreviation})",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
        )
        CompactIntInputOnlyV3(
            value = skill.modifier,
            onValueChange = { onSkillChange(skill.copy(modifier = it)) },
            modifier = Modifier.width(52.dp),
            signed = true,
        )
        TrainingSelectorV3(
            training = skill.training,
            onTrainingChange = { onSkillChange(skill.copy(training = it)) },
        )
    }
}

@Composable
private fun TrainingSelectorV3(
    training: SkillTraining,
    onTrainingChange: (SkillTraining) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .width(82.dp)
                .heightIn(min = 40.dp),
        ) {
            Text(trainingShortLabelV3(training), maxLines = 1)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            SkillTraining.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(trainingLabelV3(option)) },
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
private fun AbilityGroupsCardV3(
    draft: CharacterEditorDraftV3,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV3) -> Unit,
) {
    SectionCardV3(title = "Características, salvaciones y habilidades") {
        val columns = if (wide) 3 else 2
        AbilityKey.entries.chunked(columns).forEach { rowAbilities ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top,
            ) {
                rowAbilities.forEach { ability ->
                    AbilityGroupV3(
                        ability = ability,
                        draft = draft,
                        onDraftChange = onDraftChange,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowAbilities.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AbilityGroupV3(
    ability: AbilityKey,
    draft: CharacterEditorDraftV3,
    onDraftChange: (CharacterEditorDraftV3) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                CompactIntFieldV3(
                    label = ability.abbreviation,
                    value = draft.abilityValue(ability),
                    onValueChange = { onDraftChange(draft.withAbilityValue(ability, it)) },
                    modifier = Modifier.weight(1f),
                )
                CompactIntFieldV3(
                    label = "Salv.",
                    value = draft.saveValue(ability),
                    onValueChange = { onDraftChange(draft.withSaveValue(ability, it)) },
                    modifier = Modifier.weight(1f),
                    signed = true,
                )
            }

            val relatedSkills = draft.skills.filter { skillAbility(it.key) == ability }
            if (relatedSkills.isEmpty()) {
                Text(
                    "Sin habilidades asociadas",
                    style = MaterialTheme.typography.labelSmall,
                )
            } else {
                relatedSkills.forEach { skill ->
                    CompactAbilitySkillRowV3(
                        skill = skill,
                        onSkillChange = { changed ->
                            onDraftChange(
                                draft.copy(
                                    skills = draft.skills.map { existing ->
                                        if (existing.key == changed.key) changed else existing
                                    },
                                ),
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactAbilitySkillRowV3(
    skill: SkillDraftV3,
    onSkillChange: (SkillDraftV3) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            skillLabelV3(skill.key),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
        )
        CompactIntInputOnlyV3(
            value = skill.modifier,
            onValueChange = { onSkillChange(skill.copy(modifier = it)) },
            modifier = Modifier.width(44.dp),
            signed = true,
        )
        CompactTrainingCycleV3(
            training = skill.training,
            onChange = { onSkillChange(skill.copy(training = it)) },
        )
    }
}

@Composable
private fun CompactTrainingCycleV3(
    training: SkillTraining,
    onChange: (SkillTraining) -> Unit,
) {
    TextButton(
        onClick = { onChange(nextTrainingV3(training)) },
        modifier = Modifier.width(42.dp),
    ) {
        Text(
            when (training) {
                SkillTraining.NONE -> "—"
                SkillTraining.PROFICIENT -> "C"
                SkillTraining.EXPERTISE -> "P"
            },
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SectionCardV3(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            content()
        }
    }
}

@Composable
private fun CompactTripleRowV3(
    firstLabel: String,
    firstValue: String,
    onFirstChange: (String) -> Unit,
    secondLabel: String,
    secondValue: String,
    onSecondChange: (String) -> Unit,
    thirdLabel: String,
    thirdValue: String,
    onThirdChange: (String) -> Unit,
    signedFirst: Boolean = false,
    signedSecond: Boolean = false,
    signedThird: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CompactIntFieldV3(firstLabel, firstValue, onFirstChange, Modifier.weight(1f), signed = signedFirst)
        CompactIntFieldV3(secondLabel, secondValue, onSecondChange, Modifier.weight(1f), signed = signedSecond)
        CompactIntFieldV3(thirdLabel, thirdValue, onThirdChange, Modifier.weight(1f), signed = signedThird)
    }
}

@Composable
private fun CompactIntFieldV3(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    signed: Boolean = false,
    allowBlank: Boolean = true,
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }
        CompactIntInputOnlyV3(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            signed = signed,
            allowBlank = allowBlank,
        )
    }
}

@Composable
private fun CompactIntInputOnlyV3(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    signed: Boolean = false,
    allowBlank: Boolean = true,
) {
    Surface(
        modifier = modifier.heightIn(min = 40.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = { raw ->
                    val cleaned = sanitizeIntInputV3(raw, signed)
                    if (allowBlank || cleaned.isNotBlank()) {
                        onValueChange(cleaned)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
private fun CompactTextFieldV3(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.heightIn(min = 40.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

private fun sanitizeIntInputV3(raw: String, signed: Boolean): String {
    if (!signed) return raw.filter(Char::isDigit)
    if (raw.isBlank()) return ""

    val sign = raw.firstOrNull()?.takeIf { it == '+' || it == '-' }?.toString().orEmpty()
    val digits = raw.drop(if (sign.isEmpty()) 0 else 1).filter(Char::isDigit)
    return sign + digits
}

private data class ClassLevelDraftV3(
    val id: Uuid,
    val name: String,
    val level: String,
    val hitDieSides: String,
    val hitDiceRemaining: String,
)

private data class SkillDraftV3(
    val key: SkillKey,
    val modifier: String,
    val training: SkillTraining,
)

private data class CharacterEditorDraftV3(
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
    val classes: List<ClassLevelDraftV3>,
    val skills: List<SkillDraftV3>,
) {
    fun toSheetOrNull(original: CharacterSheet): CharacterSheet? {
        val normalizedName = name.trim().takeIf { it.isNotEmpty() } ?: return null
        fun parsed(value: String): Int? = value.trim().toIntOrNull()

        val parsedClasses = classes.mapIndexed { index, classDraft ->
            val className = classDraft.name.trim().takeIf { it.isNotEmpty() } ?: return null
            val levelValue = parsed(classDraft.level)?.takeIf { it > 0 } ?: return null
            val die = parsed(classDraft.hitDieSides)?.takeIf { it > 0 } ?: return null
            val remaining = parsed(classDraft.hitDiceRemaining)?.takeIf { it >= 0 } ?: return null
            CharacterClassLevel(
                id = classDraft.id,
                name = className,
                level = levelValue,
                hitDieSides = die,
                hitDiceRemaining = remaining,
                sortOrder = index,
            )
        }

        val parsedSkills = skills.map { skill ->
            CharacterSkill(
                key = skill.key,
                modifier = parsed(skill.modifier) ?: return null,
                training = skill.training,
            )
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

    fun abilityValue(key: AbilityKey): String = when (key) {
        AbilityKey.STRENGTH -> strength
        AbilityKey.DEXTERITY -> dexterity
        AbilityKey.CONSTITUTION -> constitution
        AbilityKey.INTELLIGENCE -> intelligence
        AbilityKey.WISDOM -> wisdom
        AbilityKey.CHARISMA -> charisma
    }

    fun saveValue(key: AbilityKey): String = when (key) {
        AbilityKey.STRENGTH -> strengthSave
        AbilityKey.DEXTERITY -> dexteritySave
        AbilityKey.CONSTITUTION -> constitutionSave
        AbilityKey.INTELLIGENCE -> intelligenceSave
        AbilityKey.WISDOM -> wisdomSave
        AbilityKey.CHARISMA -> charismaSave
    }

    fun withAbilityValue(key: AbilityKey, value: String): CharacterEditorDraftV3 = when (key) {
        AbilityKey.STRENGTH -> copy(strength = value)
        AbilityKey.DEXTERITY -> copy(dexterity = value)
        AbilityKey.CONSTITUTION -> copy(constitution = value)
        AbilityKey.INTELLIGENCE -> copy(intelligence = value)
        AbilityKey.WISDOM -> copy(wisdom = value)
        AbilityKey.CHARISMA -> copy(charisma = value)
    }

    fun withSaveValue(key: AbilityKey, value: String): CharacterEditorDraftV3 = when (key) {
        AbilityKey.STRENGTH -> copy(strengthSave = value)
        AbilityKey.DEXTERITY -> copy(dexteritySave = value)
        AbilityKey.CONSTITUTION -> copy(constitutionSave = value)
        AbilityKey.INTELLIGENCE -> copy(intelligenceSave = value)
        AbilityKey.WISDOM -> copy(wisdomSave = value)
        AbilityKey.CHARISMA -> copy(charismaSave = value)
    }

    companion object {
        fun from(sheet: CharacterSheet) = CharacterEditorDraftV3(
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
                ClassLevelDraftV3(
                    id = it.id,
                    name = it.name,
                    level = it.level.toString(),
                    hitDieSides = it.hitDieSides.toString(),
                    hitDiceRemaining = it.hitDiceRemaining.toString(),
                )
            },
            skills = sheet.skills.map {
                SkillDraftV3(
                    key = it.key,
                    modifier = it.modifier.toString(),
                    training = it.training,
                )
            },
        )

        val Saver = listSaver<CharacterEditorDraftV3, String>(
            save = { draft ->
                buildList {
                    add(draft.name)
                    add(draft.status.name)
                    add(draft.strength)
                    add(draft.dexterity)
                    add(draft.constitution)
                    add(draft.intelligence)
                    add(draft.wisdom)
                    add(draft.charisma)
                    add(draft.armorClass)
                    add(draft.maxHp)
                    add(draft.currentHp)
                    add(draft.tempHp)
                    add(draft.initiativeModifier)
                    add(draft.speed)
                    add(draft.proficiencyBonus)
                    add(draft.strengthSave)
                    add(draft.dexteritySave)
                    add(draft.constitutionSave)
                    add(draft.intelligenceSave)
                    add(draft.wisdomSave)
                    add(draft.charismaSave)
                    add(draft.passivePerception)
                    add(draft.spellSaveDc)
                    add(draft.classes.size.toString())
                    draft.classes.forEach { classDraft ->
                        add(classDraft.id.toString())
                        add(classDraft.name)
                        add(classDraft.level)
                        add(classDraft.hitDieSides)
                        add(classDraft.hitDiceRemaining)
                    }
                    add(draft.skills.size.toString())
                    draft.skills.forEach { skill ->
                        add(skill.key.name)
                        add(skill.modifier)
                        add(skill.training.name)
                    }
                }
            },
            restore = { values ->
                var index = 0
                fun next(): String = values[index++]

                val name = next()
                val status = CharacterStatus.valueOf(next())
                val strength = next()
                val dexterity = next()
                val constitution = next()
                val intelligence = next()
                val wisdom = next()
                val charisma = next()
                val armorClass = next()
                val maxHp = next()
                val currentHp = next()
                val tempHp = next()
                val initiativeModifier = next()
                val speed = next()
                val proficiencyBonus = next()
                val strengthSave = next()
                val dexteritySave = next()
                val constitutionSave = next()
                val intelligenceSave = next()
                val wisdomSave = next()
                val charismaSave = next()
                val passivePerception = next()
                val spellSaveDc = next()

                val classCount = next().toInt()
                val classes = List(classCount) {
                    ClassLevelDraftV3(
                        id = Uuid.parse(next()),
                        name = next(),
                        level = next(),
                        hitDieSides = next(),
                        hitDiceRemaining = next(),
                    )
                }

                val skillCount = next().toInt()
                val skills = List(skillCount) {
                    SkillDraftV3(
                        key = SkillKey.valueOf(next()),
                        modifier = next(),
                        training = SkillTraining.valueOf(next()),
                    )
                }

                CharacterEditorDraftV3(
                    name = name,
                    status = status,
                    strength = strength,
                    dexterity = dexterity,
                    constitution = constitution,
                    intelligence = intelligence,
                    wisdom = wisdom,
                    charisma = charisma,
                    armorClass = armorClass,
                    maxHp = maxHp,
                    currentHp = currentHp,
                    tempHp = tempHp,
                    initiativeModifier = initiativeModifier,
                    speed = speed,
                    proficiencyBonus = proficiencyBonus,
                    strengthSave = strengthSave,
                    dexteritySave = dexteritySave,
                    constitutionSave = constitutionSave,
                    intelligenceSave = intelligenceSave,
                    wisdomSave = wisdomSave,
                    charismaSave = charismaSave,
                    passivePerception = passivePerception,
                    spellSaveDc = spellSaveDc,
                    classes = classes,
                    skills = skills,
                )
            },
        )
    }
}

private fun skillAbility(key: SkillKey): AbilityKey = when (key) {
    SkillKey.ATHLETICS -> AbilityKey.STRENGTH
    SkillKey.ACROBATICS,
    SkillKey.SLEIGHT_OF_HAND,
    SkillKey.STEALTH,
    -> AbilityKey.DEXTERITY
    SkillKey.ARCANA,
    SkillKey.HISTORY,
    SkillKey.INVESTIGATION,
    SkillKey.NATURE,
    SkillKey.RELIGION,
    -> AbilityKey.INTELLIGENCE
    SkillKey.ANIMAL_HANDLING,
    SkillKey.INSIGHT,
    SkillKey.MEDICINE,
    SkillKey.PERCEPTION,
    SkillKey.SURVIVAL,
    -> AbilityKey.WISDOM
    SkillKey.DECEPTION,
    SkillKey.INTIMIDATION,
    SkillKey.PERFORMANCE,
    SkillKey.PERSUASION,
    -> AbilityKey.CHARISMA
}

private fun nextTrainingV3(training: SkillTraining): SkillTraining = when (training) {
    SkillTraining.NONE -> SkillTraining.PROFICIENT
    SkillTraining.PROFICIENT -> SkillTraining.EXPERTISE
    SkillTraining.EXPERTISE -> SkillTraining.NONE
}

private fun trainingShortLabelV3(training: SkillTraining): String = when (training) {
    SkillTraining.NONE -> "—"
    SkillTraining.PROFICIENT -> "Comp."
    SkillTraining.EXPERTISE -> "Pericia"
}

private fun trainingLabelV3(training: SkillTraining): String = when (training) {
    SkillTraining.NONE -> "Sin competencia"
    SkillTraining.PROFICIENT -> "Competente"
    SkillTraining.EXPERTISE -> "Pericia"
}

private fun statusLabelV3(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}

private fun skillLabelV3(key: SkillKey): String = when (key) {
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

private fun formatSavedAtV3(epochSeconds: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return Instant.ofEpochSecond(epochSeconds)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
