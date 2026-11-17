package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomAttribute
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkillPresentation
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.characterAbilityReferenceAbbreviation
import io.github.mrsimkin.dndcustomaid.shared.character.customSavingThrowTotal
import io.github.mrsimkin.dndcustomaid.shared.character.customSkillTotal
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterSkills
// Refactor F3: the PC editor's Habilidades tab (saves, skills, ability groups), moved out of CharacterEditorV4.kt.

@Composable
internal fun SkillsTabV4(
    draft: CharacterEditorDraftV4,
    closureState: CharacterClosureState,
    calculationSheet: CharacterSheet,
    proficiencies: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency>,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    skillLayoutChoice: SkillLayoutChoice,
    onSkillLayoutChange: (SkillLayoutChoice) -> Unit,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    onProficienciesChange: (List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency>) -> Unit,
) {
    val successorState = LocalCharacterPcSettingsContextV4.current?.successorState ?: CharacterSuccessorState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = appSpacingV4(if (wide) 10.dp else 5.dp),
                    end = appSpacingV4(if (wide) 10.dp else 5.dp),
                    top = appSpacingV4(5.dp),
                ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            SkillViewSelectorV4(skillLayoutChoice, onSkillLayoutChange)
            CharacterPassiveSkillsCardV4(calculationSheet)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = appSpacingV4(if (wide) 10.dp else 5.dp),
                end = appSpacingV4(if (wide) 10.dp else 5.dp),
                top = 0.dp,
                bottom = appSpacingV4(170.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            when (skillLayoutChoice) {
                SkillLayoutChoice.BY_SKILLS -> {
                    // T8_SKILLS_TABLE_MODE_AFFORDANCES_V4: preserve projection while gating structural controls.
                    item { AbilitiesCardV4(draft, onDraftChange, structuralEditingEnabled) }
                    item { SavesCardV4(draft, wide, onDraftChange, structuralEditingEnabled) }
                    item {
                        SkillsListCardV4(
                            draft = draft,
                            wide = wide,
                            onDraftChange = onDraftChange,
                            customSkills = closureState.customSkills,
                            calculationSheet = calculationSheet,
                            successorState = successorState,
                            structuralEditingEnabled = structuralEditingEnabled,
                        )
                    }
                }
                SkillLayoutChoice.BY_ATTRIBUTE -> {
                    item {
                        AbilityGroupsCardV4(
                            draft = draft,
                            wide = wide,
                            onDraftChange = onDraftChange,
                            customSkills = closureState.customSkills,
                            calculationSheet = calculationSheet,
                            successorState = successorState,
                            structuralEditingEnabled = structuralEditingEnabled,
                        )
                    }
                }
            }
            item {
                CharacterProficienciesCardV4(
                    proficiencies = proficiencies,
                    structuralEditingEnabled = structuralEditingEnabled,
                    onProficienciesChange = onProficienciesChange,
                )
            }
        }
    }
}

@Composable
internal fun SkillViewSelectorV4(
    current: SkillLayoutChoice,
    onChange: (SkillLayoutChoice) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            SkillLayoutChoice.entries.forEach { choice ->
                val selected = choice == current
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onChange(choice) },
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                ) {
                    Text(
                        choice.label,
                        modifier = Modifier.padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(8.dp)),
                        textAlign = TextAlign.Center,
                        style = if (selected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

@Composable
internal fun SavesCardV4(
    draft: CharacterEditorDraftV4,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    structuralEditingEnabled: Boolean = true,
) {
    SectionCardV4("Tiradas de salvación") {
        CharacterHelpV4(
            "Marca competencia cuando corresponda. Toca el total para ver el cálculo y editar Ajuste adicional.",
        )
        val columns = if (wide) 3 else 2
        CharacterAbility.entries.chunked(columns).forEach { abilities ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                abilities.forEach { ability ->
                    SaveRowV4(
                        ability = ability,
                        draft = draft,
                        onDraftChange = onDraftChange,
                        structuralEditingEnabled = structuralEditingEnabled,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - abilities.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
internal fun SaveRowV4(
    ability: CharacterAbility,
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    structuralEditingEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val save = draft.saveFor(ability)
    val abilityModifier = draft.abilityModifier(ability)
    val proficiency = draft.finalProficiencyBonus()
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(abilityAbbreviationV4(ability), style = MaterialTheme.typography.labelMedium)
            DerivedTotalControlV4(
                total = draft.savingThrowTotal(ability)?.let(::formatSignedV4).orEmpty(),
                adjustment = save.adjustment,
                dialogTitle = "Salvación ${abilityAbbreviationV4(ability)}",
                breakdownLines = listOf(
                    "${abilityAbbreviationV4(ability)} ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                    if (save.proficient) {
                        "Competencia ${proficiency?.let(::formatSignedV4) ?: "—"}"
                    } else {
                        "Sin competencia +0"
                    },
                ),
                onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },
                enabled = structuralEditingEnabled,
                modifier = Modifier.weight(1f),
            )
            SaveProficiencyToggleV4(
                proficient = save.proficient,
                enabled = structuralEditingEnabled,
                onToggle = {
                    onDraftChange(draft.withSave(save.copy(proficient = !save.proficient)))
                },
            )
        }
    }
}

@Composable
internal fun SaveProficiencyToggleV4(
    proficient: Boolean,
    onToggle: () -> Unit,
    enabled: Boolean = true,
) {
    val color = if (proficient) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clickable(enabled = enabled, onClick = onToggle),
        shape = CircleShape,
        border = BorderStroke(1.5.dp, color),
        color = if (proficient) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        if (proficient) {
            Canvas(modifier = Modifier.padding(9.dp)) {
                val stroke = 2.2.dp.toPx()
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.12f, size.height * 0.55f),
                    end = Offset(size.width * 0.42f, size.height * 0.82f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.42f, size.height * 0.82f),
                    end = Offset(size.width * 0.9f, size.height * 0.18f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
internal fun SkillsListCardV4(
    draft: CharacterEditorDraftV4,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    structuralEditingEnabled: Boolean = true,
) {
    val rows = presentCharacterSkills(
        builtInSkills = calculationSheet.skills,
        customSkills = customSkills,
        successorState = successorState,
    )
    SectionCardV4("Habilidades") {
        CharacterHelpV4(
            "Las habilidades estándar se editan aquí. Las personalizadas se configuran en Ajustes del PJ y aparecen en esta misma lista.",
        )
        if (wide) {
            val midpoint = (rows.size + 1) / 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(10.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    rows.take(midpoint).forEachIndexed { index, row ->
                        UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange, structuralEditingEnabled)
                        if (index < midpoint - 1) HorizontalDivider()
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    val second = rows.drop(midpoint)
                    second.forEachIndexed { index, row ->
                        UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange, structuralEditingEnabled)
                        if (index < second.lastIndex) HorizontalDivider()
                    }
                }
            }
        } else {
            rows.forEachIndexed { index, row ->
                UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange, structuralEditingEnabled)
                if (index < rows.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
internal fun UnifiedSkillRowV4(
    row: CharacterSkillPresentation,
    draft: CharacterEditorDraftV4,
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    structuralEditingEnabled: Boolean = true,
) {
    val builtInKey = row.builtInKey
    if (builtInKey != null) {
        val skill = draft.skills.firstOrNull { it.key == builtInKey } ?: return
        SkillRowV4(skill, draft, onDraftChange, structuralEditingEnabled)
        return
    }
    val customSkill = row.customSkillId?.let { id -> customSkills.firstOrNull { it.id == id } } ?: return
    CustomSkillProjectionRowV4(row, customSkill, calculationSheet, successorState)
}

@Composable
internal fun CustomSkillProjectionRowV4(
    row: CharacterSkillPresentation,
    skill: CharacterCustomSkill,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
) {
    val total = calculationSheet.customSkillTotal(skill, successorState)
    val abbreviation = characterAbilityReferenceAbbreviation(row.ability, successorState)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${row.label} ($abbreviation)",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
            maxLines = 3,
        )
        ReadOnlySkillTotalV4(total)
        ReadOnlyTrainingV4(skill.training)
    }
}

@Composable
internal fun ReadOnlySkillTotalV4(total: Int?) {
    Surface(
        modifier = Modifier.width(58.dp).heightIn(min = 34.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
            Text(total?.let(::formatSignedV4) ?: "—", style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        }
    }
}

@Composable
internal fun ReadOnlyTrainingV4(training: SkillTraining) {
    // Match the 48dp layout footprint reserved by the ordinary M3 training selector
    // while keeping the custom projection visually compact and read-only.
    Box(
        modifier = Modifier.width(48.dp).heightIn(min = 48.dp, max = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.width(44.dp).heightIn(min = 34.dp, max = 34.dp),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box(contentAlignment = Alignment.Center) { TrainingGlyphV4(training) }
        }
    }
}

@Composable
internal fun SkillRowV4(
    skill: SkillDraftV4,
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    structuralEditingEnabled: Boolean = true,
) {
    val abilityModifier = draft.abilityModifier(skill.key.ability)
    val proficiency = draft.finalProficiencyBonus()
    val proficiencyContribution = proficiency?.let {
        when (skill.training) {
            SkillTraining.NONE -> 0
            SkillTraining.PROFICIENT -> it
            SkillTraining.EXPERTISE -> it * 2
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${skillLabelV4(skill.key)} (${abilityAbbreviationV4(skill.key.ability)})",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
        )
        DerivedTotalControlV4(
            total = draft.skillTotal(skill.key)?.let(::formatSignedV4).orEmpty(),
            adjustment = skill.adjustment,
            dialogTitle = skillLabelV4(skill.key),
            breakdownLines = listOf(
                "${abilityAbbreviationV4(skill.key.ability)} ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                "${trainingLabelV4(skill.training)} ${proficiencyContribution?.let(::formatSignedV4) ?: "—"}",
            ),
            onAdjustmentChange = { onDraftChange(draft.withSkill(skill.copy(adjustment = it))) },
            enabled = structuralEditingEnabled,
            modifier = Modifier.width(58.dp),
        )
        TrainingSelectorV4(
            training = skill.training,
            enabled = structuralEditingEnabled,
            onTrainingChange = { onDraftChange(draft.withSkill(skill.copy(training = it))) },
        )
    }
}

@Composable
internal fun TrainingSelectorV4(
    training: SkillTraining,
    onTrainingChange: (SkillTraining) -> Unit,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            enabled = enabled,
            modifier = Modifier
                .width(44.dp)
                .heightIn(min = 34.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            TrainingGlyphV4(training)
        }
        DropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {
            SkillTraining.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(trainingLabelV4(option)) },
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
internal fun TrainingGlyphV4(training: SkillTraining) {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(modifier = Modifier.size(20.dp)) {
        val border = 1.5.dp.toPx()
        drawRect(color = color, style = Stroke(width = border))
        if (training != SkillTraining.NONE) {
            fun check(offsetY: Float) {
                val stroke = 1.8.dp.toPx()
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.12f, size.height * (0.48f + offsetY)),
                    end = Offset(size.width * 0.4f, size.height * (0.72f + offsetY)),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.4f, size.height * (0.72f + offsetY)),
                    end = Offset(size.width * 0.88f, size.height * (0.22f + offsetY)),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }
            when (training) {
                SkillTraining.PROFICIENT -> check(0f)
                SkillTraining.EXPERTISE -> {
                    check(-0.12f)
                    check(0.12f)
                }
                SkillTraining.NONE -> Unit
            }
        }
    }
}

@Composable
internal fun AbilityGroupsCardV4(
    draft: CharacterEditorDraftV4,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    structuralEditingEnabled: Boolean = true,
) {
    val rows = presentCharacterSkills(
        builtInSkills = calculationSheet.skills,
        customSkills = customSkills,
        successorState = successorState,
    )
    SectionCardV4("Características, salvaciones y habilidades") {
        val columns = if (wide) 3 else 2
        CharacterAbility.entries.chunked(columns).forEach { rowAbilities ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                rowAbilities.forEach { ability ->
                    AbilityGroupV4(
                        ability = ability,
                        draft = draft,
                        relatedSkills = rows.filter { it.ability.builtIn == ability },
                        customSkills = customSkills,
                        calculationSheet = calculationSheet,
                        successorState = successorState,
                        onDraftChange = onDraftChange,
                        structuralEditingEnabled = structuralEditingEnabled,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowAbilities.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }

        val customGroups = successorState.customAttributes
            .sortedBy { it.sortOrder }
            .mapNotNull { attribute ->
                val related = rows.filter { it.ability.customAttributeId == attribute.id }
                if (related.isEmpty()) null else attribute to related
            }
        customGroups.chunked(columns).forEach { groups ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                groups.forEach { (attribute, related) ->
                    CustomAttributeAbilityGroupV4(
                        attribute = attribute,
                        relatedSkills = related,
                        customSkills = customSkills,
                        calculationSheet = calculationSheet,
                        successorState = successorState,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - groups.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
internal fun AbilityGroupV4(
    ability: CharacterAbility,
    draft: CharacterEditorDraftV4,
    relatedSkills: List<CharacterSkillPresentation>,
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    structuralEditingEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val save = draft.saveFor(ability)
    val abilityModifier = draft.abilityModifier(ability)
    val proficiency = draft.finalProficiencyBonus()
    val abbreviation = abilityAbbreviationV4(ability)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(abbreviation, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, maxLines = 1)
                if (structuralEditingEnabled) {
                    CompactIntInputV4(
                        value = draft.abilityValue(ability),
                        onValueChange = { onDraftChange(draft.withAbilityValue(ability, it)) },
                        modifier = Modifier.width(52.dp),
                    )
                } else {
                    Text(draft.abilityValue(ability).ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    "Mod ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Salv.", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                DerivedTotalControlV4(
                    total = draft.savingThrowTotal(ability)?.let(::formatSignedV4).orEmpty(),
                    adjustment = save.adjustment,
                    dialogTitle = "Salvación $abbreviation",
                    breakdownLines = listOf(
                        "$abbreviation ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                        if (save.proficient) {
                            "Competencia ${proficiency?.let(::formatSignedV4) ?: "—"}"
                        } else {
                            "Sin competencia +0"
                        },
                    ),
                    onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },
                    enabled = structuralEditingEnabled,
                    modifier = Modifier.weight(1f),
                )
                SaveProficiencyToggleV4(
                    proficient = save.proficient,
                    enabled = structuralEditingEnabled,
                    onToggle = {
                        onDraftChange(draft.withSave(save.copy(proficient = !save.proficient)))
                    },
                )
            }
            relatedSkills.forEach { row ->
                UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange, structuralEditingEnabled)
            }
        }
    }
}

@Composable
internal fun CustomAttributeAbilityGroupV4(
    attribute: CharacterCustomAttribute,
    relatedSkills: List<CharacterSkillPresentation>,
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Text("${attribute.name} (${attribute.abbreviation})", style = MaterialTheme.typography.labelLarge)
            Text(
                "Puntuación ${attribute.score} · Mod ${formatSignedV4(attribute.modifier)}",
                style = MaterialTheme.typography.labelMedium,
            )
            if (attribute.savingThrowEnabled) {
                Text(
                    "Salv. ${calculationSheet.customSavingThrowTotal(attribute)?.let(::formatSignedV4) ?: "—"}",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            relatedSkills.forEach { row ->
                val customSkill = row.customSkillId?.let { id -> customSkills.firstOrNull { it.id == id } }
                if (customSkill != null) {
                    CustomSkillProjectionRowV4(row, customSkill, calculationSheet, successorState)
                }
            }
        }
    }
}
