#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
EDITOR = ROOT / "CharacterEditorV4.kt"
CLASS_WRAPPER = ROOT / "CharacterClassIdentityV4.kt"
CLASS_IDENTITY = ROOT / "CharacterClassIdentitySuccessorV4.kt"
GENERAL_CLOSURE = ROOT / "CharacterGeneralClosureV4.kt"
POLICY_TEST = Path("shared/src/commonTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterTableModePolicyTest.kt")


def require(text: str, needle: str, label: str) -> None:
    if needle not in text:
        raise AssertionError(f"Missing {label}: {needle}")


def main() -> None:
    editor = EDITOR.read_text(encoding="utf-8")
    class_wrapper = CLASS_WRAPPER.read_text(encoding="utf-8")
    class_identity = CLASS_IDENTITY.read_text(encoding="utf-8")
    general_closure = GENERAL_CLOSURE.read_text(encoding="utf-8")
    policy_test = POLICY_TEST.read_text(encoding="utf-8")

    require(editor, "T8_TABLE_MODE_AFFORDANCES_V4", "T8 Overview marker")
    require(editor, "T8_SKILLS_TABLE_MODE_AFFORDANCES_V4", "T8 Skills marker")
    require(editor, "IdentityCardV4(draft, stored, onDraftChange, structuralEditingEnabled)", "read-only identity routing")
    require(editor, "AbilitiesCardV4(draft, onDraftChange, structuralEditingEnabled)", "ability structural routing")
    require(editor, "CharacterClassIdentityCardV4(", "class identity card")
    require(editor, "if (!structuralEditingEnabled) {\n        SectionCardV4(\"Referencia de combate\")", "Table Mode Combat branch")
    require(editor, "CharacterCombatOperationalCardV4(", "operational HP surface in Table Mode Overview")
    require(editor, "onSheetChange = onOperationalSheetChange", "operational HP persistence route")
    require(editor, "Text(\"Bono competencia\"", "read-only proficiency reference")
    require(editor, "Text(\"Percepción pasiva\"", "read-only passive perception reference")
    require(editor, "CharacterGeneralSuccessorCardsV4(", "operational quick-state surface retained")
    require(editor, "onInspirationChange = { enabled ->", "operational inspiration retained")
    require(editor, "onResourceValueChange = { resourceId, value ->", "operational resource current values retained")

    # Skills tab: presentation choice stays usable, structural score/training/proficiency/adjustment controls are gated.
    require(editor, "SkillViewSelectorV4(skillLayoutChoice, onSkillLayoutChange)", "presentation-only skill layout selector retained")
    require(editor, "SavesCardV4(draft, wide, onDraftChange, structuralEditingEnabled)", "save structural routing")
    require(editor, "structuralEditingEnabled = structuralEditingEnabled,\n                        )", "Skills child policy propagation")
    require(editor, ".clickable(enabled = enabled) { dialogOpen = true }", "derived adjustment dialog gate")
    require(editor, "if (enabled && dialogOpen)", "derived adjustment dialog hard gate")
    require(editor, ".clickable(enabled = enabled, onClick = onToggle)", "save proficiency gate")
    require(editor, "enabled = enabled,\n            modifier = Modifier", "training selector button gate")
    require(editor, "DropdownMenu(expanded = expanded && enabled", "training dropdown gate")
    require(editor, "if (structuralEditingEnabled) {\n                    CompactIntInputV4(", "ability score read-only gate")
    require(editor, "SkillRowV4(skill, draft, onDraftChange, structuralEditingEnabled)", "standard skill structural routing")

    require(class_wrapper, "structuralEditingEnabled: Boolean = true", "class wrapper structural flag")
    require(class_wrapper, "structuralEditingEnabled = structuralEditingEnabled", "class wrapper forwarding")
    require(class_identity, "structuralEditingEnabled: Boolean = true", "class structural flag")
    require(class_identity, "if (structuralEditingEnabled) {\n                    CompactClassActionV4(\"+ Clase\")", "class add gate")
    require(class_identity, "if (structuralEditingEnabled && editorOpen)", "class editor dialog gate")
    require(class_identity, "if (structuralEditingEnabled) deleteId?.let", "class delete dialog gate")

    require(general_closure, "structuralEditingEnabled: Boolean = true", "general closure structural flag")
    require(general_closure, "structuralEditingEnabled = structuralEditingEnabled", "general closure child propagation")
    require(general_closure, "if (structuralEditingEnabled) {\n                TextButton(", "portrait/token action gate")
    require(general_closure, ".clickable(enabled = structuralEditingEnabled)", "reference editor click gate")
    require(general_closure, "if (structuralEditingEnabled) {\n                        StableRemoveIconButton", "reference delete gate")
    require(general_closure, "if (structuralEditingEnabled) {\n                TextButton(onClick = onAdd)", "reference add gate")
    require(general_closure, "if (structuralEditingEnabled && defenseEditorOpen)", "defense dialog gate")
    require(general_closure, "if (structuralEditingEnabled && senseEditorOpen)", "sense dialog gate")
    require(general_closure, "if (structuralEditingEnabled && movementEditorOpen)", "movement dialog gate")

    require(policy_test, "operationalSheetMergeAppliesLiveValuesAndRejectsStructure", "shared operational/structural policy test")
    require(policy_test, "assertEquals(4, merged.tempHp)", "operational temporary HP assertion")
    require(policy_test, "assertEquals(3, merged.spellSlots.single().spentSlots)", "operational spent-slot assertion")
    require(policy_test, "assertEquals(4, merged.resources.single().currentValue)", "operational resource assertion")
    require(policy_test, "assertEquals(\"Persisted\", merged.name)", "structural-name rejection assertion")
    require(policy_test, "assertEquals(15, merged.armorClass)", "structural-AC rejection assertion")

    print("Player Table Mode affordance guard passed across Overview, Skills, class identity and general structural references")


if __name__ == "__main__":
    main()
