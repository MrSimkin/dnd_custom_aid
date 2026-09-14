#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
MARKER = "T8_SKILLS_TABLE_MODE_AFFORDANCES_V4"


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, observed {count}")
    return text.replace(old, new, 1)


def main() -> None:
    text = PATH.read_text(encoding="utf-8")
    if MARKER in text:
        print("T8 Skills affordance migration already applied")
        return

    text = replace_once(
        text,
        """                SkillLayoutChoice.BY_SKILLS -> {\n                    item { AbilitiesCardV4(draft, onDraftChange) }\n                    item { SavesCardV4(draft, wide, onDraftChange) }\n                    item {\n                        SkillsListCardV4(\n                            draft = draft,\n                            wide = wide,\n                            onDraftChange = onDraftChange,\n                            customSkills = closureState.customSkills,\n                            calculationSheet = calculationSheet,\n                            successorState = successorState,\n                        )\n                    }\n                }\n""",
        """                SkillLayoutChoice.BY_SKILLS -> {\n                    // T8_SKILLS_TABLE_MODE_AFFORDANCES_V4: preserve projection while gating structural controls.\n                    item { AbilitiesCardV4(draft, onDraftChange, structuralEditingEnabled) }\n                    item { SavesCardV4(draft, wide, onDraftChange, structuralEditingEnabled) }\n                    item {\n                        SkillsListCardV4(\n                            draft = draft,\n                            wide = wide,\n                            onDraftChange = onDraftChange,\n                            customSkills = closureState.customSkills,\n                            calculationSheet = calculationSheet,\n                            successorState = successorState,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                        )\n                    }\n                }\n""",
        "BY_SKILLS structural propagation",
    )

    text = replace_once(
        text,
        """                        AbilityGroupsCardV4(\n                            draft = draft,\n                            wide = wide,\n                            onDraftChange = onDraftChange,\n                            customSkills = closureState.customSkills,\n                            calculationSheet = calculationSheet,\n                            successorState = successorState,\n                        )\n""",
        """                        AbilityGroupsCardV4(\n                            draft = draft,\n                            wide = wide,\n                            onDraftChange = onDraftChange,\n                            customSkills = closureState.customSkills,\n                            calculationSheet = calculationSheet,\n                            successorState = successorState,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                        )\n""",
        "BY_ATTRIBUTE structural propagation",
    )

    text = replace_once(
        text,
        """private fun SavesCardV4(\n    draft: CharacterEditorDraftV4,\n    wide: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n) {\n""",
        """private fun SavesCardV4(\n    draft: CharacterEditorDraftV4,\n    wide: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean = true,\n) {\n""",
        "Saves card signature",
    )
    text = replace_once(
        text,
        """                        onDraftChange = onDraftChange,\n                        modifier = Modifier.weight(1f),\n                    )\n""",
        """                        onDraftChange = onDraftChange,\n                        structuralEditingEnabled = structuralEditingEnabled,\n                        modifier = Modifier.weight(1f),\n                    )\n""",
        "Save row structural propagation",
    )

    text = replace_once(
        text,
        """private fun SaveRowV4(\n    ability: CharacterAbility,\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    modifier: Modifier = Modifier,\n) {\n""",
        """private fun SaveRowV4(\n    ability: CharacterAbility,\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean = true,\n    modifier: Modifier = Modifier,\n) {\n""",
        "Save row signature",
    )
    text = replace_once(
        text,
        """                onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },\n                modifier = Modifier.weight(1f),\n            )\n            SaveProficiencyToggleV4(\n                proficient = save.proficient,\n                onToggle = {\n                    onDraftChange(draft.withSave(save.copy(proficient = !save.proficient)))\n                },\n            )\n""",
        """                onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },\n                enabled = structuralEditingEnabled,\n                modifier = Modifier.weight(1f),\n            )\n            SaveProficiencyToggleV4(\n                proficient = save.proficient,\n                enabled = structuralEditingEnabled,\n                onToggle = {\n                    onDraftChange(draft.withSave(save.copy(proficient = !save.proficient)))\n                },\n            )\n""",
        "Save controls gate",
    )

    text = replace_once(
        text,
        """private fun SaveProficiencyToggleV4(\n    proficient: Boolean,\n    onToggle: () -> Unit,\n) {\n""",
        """private fun SaveProficiencyToggleV4(\n    proficient: Boolean,\n    onToggle: () -> Unit,\n    enabled: Boolean = true,\n) {\n""",
        "Save toggle signature",
    )
    text = replace_once(
        text,
        """        modifier = Modifier\n            .size(36.dp)\n            .clickable(onClick = onToggle),\n""",
        """        modifier = Modifier\n            .size(36.dp)\n            .clickable(enabled = enabled, onClick = onToggle),\n""",
        "Save toggle click gate",
    )

    text = replace_once(
        text,
        """private fun SkillsListCardV4(\n    draft: CharacterEditorDraftV4,\n    wide: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    customSkills: List<CharacterCustomSkill>,\n    calculationSheet: CharacterSheet,\n    successorState: CharacterSuccessorState,\n) {\n""",
        """private fun SkillsListCardV4(\n    draft: CharacterEditorDraftV4,\n    wide: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    customSkills: List<CharacterCustomSkill>,\n    calculationSheet: CharacterSheet,\n    successorState: CharacterSuccessorState,\n    structuralEditingEnabled: Boolean = true,\n) {\n""",
        "Skills card signature",
    )
    text = text.replace(
        "UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange)",
        "UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange, structuralEditingEnabled)",
        3,
    )

    text = replace_once(
        text,
        """private fun UnifiedSkillRowV4(\n    row: CharacterSkillPresentation,\n    draft: CharacterEditorDraftV4,\n    customSkills: List<CharacterCustomSkill>,\n    calculationSheet: CharacterSheet,\n    successorState: CharacterSuccessorState,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n) {\n""",
        """private fun UnifiedSkillRowV4(\n    row: CharacterSkillPresentation,\n    draft: CharacterEditorDraftV4,\n    customSkills: List<CharacterCustomSkill>,\n    calculationSheet: CharacterSheet,\n    successorState: CharacterSuccessorState,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean = true,\n) {\n""",
        "Unified skill signature",
    )
    text = replace_once(
        text,
        """        SkillRowV4(skill, draft, onDraftChange)\n""",
        """        SkillRowV4(skill, draft, onDraftChange, structuralEditingEnabled)\n""",
        "Built-in skill structural propagation",
    )

    text = replace_once(
        text,
        """private fun SkillRowV4(\n    skill: SkillDraftV4,\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n) {\n""",
        """private fun SkillRowV4(\n    skill: SkillDraftV4,\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean = true,\n) {\n""",
        "Skill row signature",
    )
    text = replace_once(
        text,
        """            onAdjustmentChange = { onDraftChange(draft.withSkill(skill.copy(adjustment = it))) },\n            modifier = Modifier.width(58.dp),\n        )\n        TrainingSelectorV4(\n            training = skill.training,\n            onTrainingChange = { onDraftChange(draft.withSkill(skill.copy(training = it))) },\n        )\n""",
        """            onAdjustmentChange = { onDraftChange(draft.withSkill(skill.copy(adjustment = it))) },\n            enabled = structuralEditingEnabled,\n            modifier = Modifier.width(58.dp),\n        )\n        TrainingSelectorV4(\n            training = skill.training,\n            enabled = structuralEditingEnabled,\n            onTrainingChange = { onDraftChange(draft.withSkill(skill.copy(training = it))) },\n        )\n""",
        "Skill controls gate",
    )

    text = replace_once(
        text,
        """private fun TrainingSelectorV4(\n    training: SkillTraining,\n    onTrainingChange: (SkillTraining) -> Unit,\n) {\n""",
        """private fun TrainingSelectorV4(\n    training: SkillTraining,\n    onTrainingChange: (SkillTraining) -> Unit,\n    enabled: Boolean = true,\n) {\n""",
        "Training selector signature",
    )
    text = replace_once(
        text,
        """        OutlinedButton(\n            onClick = { expanded = true },\n""",
        """        OutlinedButton(\n            onClick = { expanded = true },\n            enabled = enabled,\n""",
        "Training selector gate",
    )
    text = replace_once(
        text,
        """        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {\n""",
        """        DropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {\n""",
        "Training dropdown gate",
    )

    text = replace_once(
        text,
        """private fun AbilityGroupsCardV4(\n    draft: CharacterEditorDraftV4,\n    wide: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    customSkills: List<CharacterCustomSkill>,\n    calculationSheet: CharacterSheet,\n    successorState: CharacterSuccessorState,\n) {\n""",
        """private fun AbilityGroupsCardV4(\n    draft: CharacterEditorDraftV4,\n    wide: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    customSkills: List<CharacterCustomSkill>,\n    calculationSheet: CharacterSheet,\n    successorState: CharacterSuccessorState,\n    structuralEditingEnabled: Boolean = true,\n) {\n""",
        "Ability groups signature",
    )
    text = replace_once(
        text,
        """                        successorState = successorState,\n                        onDraftChange = onDraftChange,\n                        modifier = Modifier.weight(1f),\n                    )\n""",
        """                        successorState = successorState,\n                        onDraftChange = onDraftChange,\n                        structuralEditingEnabled = structuralEditingEnabled,\n                        modifier = Modifier.weight(1f),\n                    )\n""",
        "Ability group structural propagation",
    )

    text = replace_once(
        text,
        """private fun AbilityGroupV4(\n    ability: CharacterAbility,\n    draft: CharacterEditorDraftV4,\n    relatedSkills: List<CharacterSkillPresentation>,\n    customSkills: List<CharacterCustomSkill>,\n    calculationSheet: CharacterSheet,\n    successorState: CharacterSuccessorState,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    modifier: Modifier = Modifier,\n) {\n""",
        """private fun AbilityGroupV4(\n    ability: CharacterAbility,\n    draft: CharacterEditorDraftV4,\n    relatedSkills: List<CharacterSkillPresentation>,\n    customSkills: List<CharacterCustomSkill>,\n    calculationSheet: CharacterSheet,\n    successorState: CharacterSuccessorState,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean = true,\n    modifier: Modifier = Modifier,\n) {\n""",
        "Ability group signature",
    )
    text = replace_once(
        text,
        """                CompactIntInputV4(\n                    value = draft.abilityValue(ability),\n                    onValueChange = { onDraftChange(draft.withAbilityValue(ability, it)) },\n                    modifier = Modifier.width(52.dp),\n                )\n""",
        """                if (structuralEditingEnabled) {\n                    CompactIntInputV4(\n                        value = draft.abilityValue(ability),\n                        onValueChange = { onDraftChange(draft.withAbilityValue(ability, it)) },\n                        modifier = Modifier.width(52.dp),\n                    )\n                } else {\n                    Text(draft.abilityValue(ability).ifBlank { \"—\" }, style = MaterialTheme.typography.bodyMedium)\n                }\n""",
        "Ability-group score read-only gate",
    )
    text = replace_once(
        text,
        """                    onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },\n                    modifier = Modifier.weight(1f),\n                )\n                SaveProficiencyToggleV4(\n                    proficient = save.proficient,\n                    onToggle = {\n                        onDraftChange(draft.withSave(save.copy(proficient = !save.proficient)))\n                    },\n                )\n""",
        """                    onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },\n                    enabled = structuralEditingEnabled,\n                    modifier = Modifier.weight(1f),\n                )\n                SaveProficiencyToggleV4(\n                    proficient = save.proficient,\n                    enabled = structuralEditingEnabled,\n                    onToggle = {\n                        onDraftChange(draft.withSave(save.copy(proficient = !save.proficient)))\n                    },\n                )\n""",
        "Ability-group save gate",
    )
    text = replace_once(
        text,
        """                UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange)\n""",
        """                UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange, structuralEditingEnabled)\n""",
        "Ability-group skill propagation",
    )

    text = replace_once(
        text,
        """private fun DerivedTotalControlV4(\n    total: String,\n    adjustment: String,\n    dialogTitle: String,\n    breakdownLines: List<String>,\n    onAdjustmentChange: (String) -> Unit,\n    modifier: Modifier = Modifier,\n) {\n""",
        """private fun DerivedTotalControlV4(\n    total: String,\n    adjustment: String,\n    dialogTitle: String,\n    breakdownLines: List<String>,\n    onAdjustmentChange: (String) -> Unit,\n    modifier: Modifier = Modifier,\n    enabled: Boolean = true,\n) {\n""",
        "Derived control signature",
    )
    text = replace_once(
        text,
        """        modifier = modifier\n            .heightIn(min = 34.dp)\n            .clickable { dialogOpen = true },\n""",
        """        modifier = modifier\n            .heightIn(min = 34.dp)\n            .clickable(enabled = enabled) { dialogOpen = true },\n""",
        "Derived control click gate",
    )
    text = replace_once(
        text,
        """    if (dialogOpen) {\n        var pendingAdjustment by remember(dialogOpen, adjustment) { mutableStateOf(adjustment) }\n""",
        """    if (enabled && dialogOpen) {\n        var pendingAdjustment by remember(dialogOpen, adjustment) { mutableStateOf(adjustment) }\n""",
        "Derived dialog gate",
    )

    PATH.write_text(text, encoding="utf-8")
    print("Applied T8 Skills Table Mode affordance migration")


if __name__ == "__main__":
    main()
