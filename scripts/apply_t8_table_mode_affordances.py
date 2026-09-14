#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

EDITOR = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
CLASS_IDENTITY = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassIdentitySuccessorV4.kt")
GENERAL_CLOSURE = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterGeneralClosureV4.kt")
MARKER = "T8_TABLE_MODE_AFFORDANCES_V4"


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, observed {count}")
    return text.replace(old, new, 1)


def migrate_editor() -> None:
    text = EDITOR.read_text(encoding="utf-8")
    if MARKER in text:
        print("T8 editor migration already applied")
        return

    text = replace_once(
        text,
        """                        CharacterTabV4.OVERVIEW -> OverviewTabV4(\n                            draft = draft,\n                            stored = stored,\n                            projectionSheet = overviewProjectionSheet,\n                            closureState = closureState,\n                            wide = wide,\n                            onDraftChange = ::updateStructuralDraft,\n                            onOperationalSheetChange = ::persistOperationalSheet,\n                            onClosureStateChange = ::persistStructuralClosureState,\n                        )\n""",
        """                        CharacterTabV4.OVERVIEW -> OverviewTabV4(\n                            draft = draft,\n                            stored = stored,\n                            projectionSheet = overviewProjectionSheet,\n                            closureState = closureState,\n                            wide = wide,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            onDraftChange = ::updateStructuralDraft,\n                            onOperationalSheetChange = ::persistOperationalSheet,\n                            onClosureStateChange = ::persistStructuralClosureState,\n                        )\n""",
        "Overview call",
    )

    text = replace_once(
        text,
        """private fun OverviewTabV4(\n    draft: CharacterEditorDraftV4,\n    stored: CharacterSheet,\n    projectionSheet: CharacterSheet,\n    closureState: CharacterClosureState,\n    wide: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    onOperationalSheetChange: (CharacterSheet) -> Unit,\n    onClosureStateChange: (CharacterClosureState) -> Unit,\n) {\n""",
        """private fun OverviewTabV4(\n    draft: CharacterEditorDraftV4,\n    stored: CharacterSheet,\n    projectionSheet: CharacterSheet,\n    closureState: CharacterClosureState,\n    wide: Boolean,\n    structuralEditingEnabled: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    onOperationalSheetChange: (CharacterSheet) -> Unit,\n    onClosureStateChange: (CharacterClosureState) -> Unit,\n) {\n    // T8_TABLE_MODE_AFFORDANCES_V4: structural references become visibly read-only; live state stays operational.\n""",
        "Overview signature",
    )

    text = replace_once(
        text,
        """        item {\n            IdentityCardV4(draft, stored, onDraftChange)\n        }\n        item {\n            CharacterClassIdentityCardV4(\n                classes = draft.classes,\n                onClassesChange = { onDraftChange(draft.copy(classes = it)) },\n            )\n        }\n        item {\n            AbilitiesCardV4(draft, onDraftChange)\n        }\n        item {\n            CombatCardV4(draft, wide, onDraftChange)\n        }\n""",
        """        item {\n            IdentityCardV4(draft, stored, onDraftChange, structuralEditingEnabled)\n        }\n        item {\n            CharacterClassIdentityCardV4(\n                classes = draft.classes,\n                onClassesChange = { onDraftChange(draft.copy(classes = it)) },\n                structuralEditingEnabled = structuralEditingEnabled,\n            )\n        }\n        item {\n            AbilitiesCardV4(draft, onDraftChange, structuralEditingEnabled)\n        }\n        item {\n            CombatCardV4(\n                draft = draft,\n                stored = stored,\n                wide = wide,\n                structuralEditingEnabled = structuralEditingEnabled,\n                hapticsEnabled = closureState.hapticsEnabled,\n                onDraftChange = onDraftChange,\n                onOperationalSheetChange = onOperationalSheetChange,\n            )\n        }\n""",
        "Overview structural cards",
    )

    text = replace_once(
        text,
        """            CharacterGeneralClosureCardsV4(\n                state = closureState,\n                onStateChange = onClosureStateChange,\n                wide = wide,\n            )\n""",
        """            CharacterGeneralClosureCardsV4(\n                state = closureState,\n                onStateChange = onClosureStateChange,\n                wide = wide,\n                structuralEditingEnabled = structuralEditingEnabled,\n            )\n""",
        "General closure call",
    )

    text = replace_once(
        text,
        """private fun IdentityCardV4(\n    draft: CharacterEditorDraftV4,\n    stored: CharacterSheet,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n) {\n    SectionCardV4(\"Personaje\") {\n        Text(\"Nombre\", style = MaterialTheme.typography.labelSmall)\n        CompactTextFieldV4(\n            value = draft.name,\n            onValueChange = { onDraftChange(draft.copy(name = characterProperNameInput(it))) },\n            modifier = Modifier.fillMaxWidth(),\n        )\n""",
        """private fun IdentityCardV4(\n    draft: CharacterEditorDraftV4,\n    stored: CharacterSheet,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n) {\n    SectionCardV4(\"Personaje\") {\n        Text(\"Nombre\", style = MaterialTheme.typography.labelSmall)\n        if (structuralEditingEnabled) {\n            CompactTextFieldV4(\n                value = draft.name,\n                onValueChange = { onDraftChange(draft.copy(name = characterProperNameInput(it))) },\n                modifier = Modifier.fillMaxWidth(),\n            )\n        } else {\n            Text(draft.name.ifBlank { \"—\" }, style = MaterialTheme.typography.bodyMedium)\n        }\n""",
        "Identity read-only",
    )

    text = replace_once(
        text,
        """private fun AbilitiesCardV4(\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n) {\n    SectionCardV4(\"Características\") {\n        AbilitiesRowV4(draft, onDraftChange)\n    }\n}\n\n@Composable\nprivate fun AbilitiesRowV4(\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n) {\n""",
        """private fun AbilitiesCardV4(\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n) {\n    SectionCardV4(\"Características\") {\n        AbilitiesRowV4(draft, onDraftChange, structuralEditingEnabled)\n    }\n}\n\n@Composable\nprivate fun AbilitiesRowV4(\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n) {\n""",
        "Abilities signatures",
    )

    text = replace_once(
        text,
        """                CompactIntInputV4(\n                    value = draft.abilityValue(ability),\n                    onValueChange = { onDraftChange(draft.withAbilityValue(ability, it)) },\n                    modifier = Modifier.fillMaxWidth(),\n                )\n""",
        """                if (structuralEditingEnabled) {\n                    CompactIntInputV4(\n                        value = draft.abilityValue(ability),\n                        onValueChange = { onDraftChange(draft.withAbilityValue(ability, it)) },\n                        modifier = Modifier.fillMaxWidth(),\n                    )\n                } else {\n                    Text(\n                        draft.abilityValue(ability).ifBlank { \"—\" },\n                        style = MaterialTheme.typography.bodyMedium,\n                        maxLines = 1,\n                    )\n                }\n""",
        "Ability read-only",
    )

    old_combat = """private fun CombatCardV4(\n    draft: CharacterEditorDraftV4,\n    wide: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n) {\n    SectionCardV4(\"Referencia de combate\") {\n"""
    new_combat = """private fun CombatCardV4(\n    draft: CharacterEditorDraftV4,\n    stored: CharacterSheet,\n    wide: Boolean,\n    structuralEditingEnabled: Boolean,\n    hapticsEnabled: Boolean,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    onOperationalSheetChange: (CharacterSheet) -> Unit,\n) {\n    if (!structuralEditingEnabled) {\n        SectionCardV4(\"Referencia de combate\") {\n            CharacterCombatOperationalCardV4(\n                armorClass = draft.armorClass,\n                initiative = draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(),\n                speed = draft.speed,\n                sheet = stored,\n                onSheetChange = onOperationalSheetChange,\n                hapticsEnabled = hapticsEnabled,\n            )\n            Row(\n                modifier = Modifier.fillMaxWidth(),\n                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),\n            ) {\n                Column(modifier = Modifier.weight(1f)) {\n                    Text(\"Bono competencia\", style = MaterialTheme.typography.labelSmall)\n                    Text(draft.finalProficiencyBonus()?.let(::formatSignedV4) ?: \"—\", style = MaterialTheme.typography.bodyMedium)\n                }\n                Column(modifier = Modifier.weight(1f)) {\n                    Text(\"Percepción pasiva\", style = MaterialTheme.typography.labelSmall)\n                    Text(draft.passivePerceptionTotal()?.toString() ?: \"—\", style = MaterialTheme.typography.bodyMedium)\n                }\n            }\n        }\n        return\n    }\n\n    SectionCardV4(\"Referencia de combate\") {\n"""
    text = replace_once(text, old_combat, new_combat, "Combat Table Mode operational branch")

    EDITOR.write_text(text, encoding="utf-8")


def migrate_class_identity() -> None:
    text = CLASS_IDENTITY.read_text(encoding="utf-8")
    text = replace_once(
        text,
        """internal fun CharacterClassIdentitySuccessorCardV4(\n    classes: List<ClassLevelDraftV4>,\n    onClassesChange: (List<ClassLevelDraftV4>) -> Unit,\n) {\n""",
        """internal fun CharacterClassIdentitySuccessorCardV4(\n    classes: List<ClassLevelDraftV4>,\n    onClassesChange: (List<ClassLevelDraftV4>) -> Unit,\n    structuralEditingEnabled: Boolean = true,\n) {\n""",
        "Class identity signature",
    )
    text = replace_once(
        text,
        """                CompactClassActionV4(\"+ Clase\") {\n                    editorId = null\n                    editorOpen = true\n                }\n""",
        """                if (structuralEditingEnabled) {\n                    CompactClassActionV4(\"+ Clase\") {\n                        editorId = null\n                        editorOpen = true\n                    }\n                }\n""",
        "Class add affordance",
    )
    text = replace_once(
        text,
        """                        CompactClassActionV4(\"Editar\") {\n                            editorId = item.id.toString()\n                            editorOpen = true\n                        }\n                        CompactClassActionV4(\"Quitar\") { deleteId = item.id.toString() }\n""",
        """                        if (structuralEditingEnabled) {\n                            CompactClassActionV4(\"Editar\") {\n                                editorId = item.id.toString()\n                                editorOpen = true\n                            }\n                            CompactClassActionV4(\"Quitar\") { deleteId = item.id.toString() }\n                        }\n""",
        "Class edit/delete affordances",
    )
    text = replace_once(text, "    if (editorOpen) {\n", "    if (structuralEditingEnabled && editorOpen) {\n", "Class editor gate")
    text = replace_once(text, "    deleteId?.let { id ->\n", "    if (structuralEditingEnabled) deleteId?.let { id ->\n", "Class delete gate")
    CLASS_IDENTITY.write_text(text, encoding="utf-8")


def migrate_general_closure() -> None:
    text = GENERAL_CLOSURE.read_text(encoding="utf-8")
    text = replace_once(
        text,
        """internal fun CharacterGeneralClosureCardsV4(\n    state: CharacterClosureState,\n    onStateChange: (CharacterClosureState) -> Unit,\n    wide: Boolean,\n) {\n""",
        """internal fun CharacterGeneralClosureCardsV4(\n    state: CharacterClosureState,\n    onStateChange: (CharacterClosureState) -> Unit,\n    wide: Boolean,\n    structuralEditingEnabled: Boolean = true,\n) {\n""",
        "General closure signature",
    )
    text = replace_once(
        text,
        """        CharacterMediaCardV4(state = state, onStateChange = onStateChange, wide = wide)\n        CharacterDefensesSensesMovementCardV4(state = state, onStateChange = onStateChange, wide = wide)\n""",
        """        CharacterMediaCardV4(\n            state = state,\n            onStateChange = onStateChange,\n            wide = wide,\n            structuralEditingEnabled = structuralEditingEnabled,\n        )\n        CharacterDefensesSensesMovementCardV4(\n            state = state,\n            onStateChange = onStateChange,\n            wide = wide,\n            structuralEditingEnabled = structuralEditingEnabled,\n        )\n""",
        "General closure children",
    )
    text = replace_once(
        text,
        """private fun CharacterMediaCardV4(\n    state: CharacterClosureState,\n    onStateChange: (CharacterClosureState) -> Unit,\n    wide: Boolean,\n) {\n""",
        """private fun CharacterMediaCardV4(\n    state: CharacterClosureState,\n    onStateChange: (CharacterClosureState) -> Unit,\n    wide: Boolean,\n    structuralEditingEnabled: Boolean,\n) {\n""",
        "Media signature",
    )
    text = text.replace(
        """                        imageSize = 56.dp,\n                        modifier = Modifier.weight(1f),\n                    )\n""",
        """                        imageSize = 56.dp,\n                        structuralEditingEnabled = structuralEditingEnabled,\n                        modifier = Modifier.weight(1f),\n                    )\n""",
        2,
    )
    text = text.replace(
        """                    imageSize = 44.dp,\n                )\n""",
        """                    imageSize = 44.dp,\n                    structuralEditingEnabled = structuralEditingEnabled,\n                )\n""",
        2,
    )
    text = replace_once(
        text,
        """    round: Boolean,\n    imageSize: androidx.compose.ui.unit.Dp,\n    modifier: Modifier = Modifier,\n) {\n""",
        """    round: Boolean,\n    imageSize: androidx.compose.ui.unit.Dp,\n    structuralEditingEnabled: Boolean,\n    modifier: Modifier = Modifier,\n) {\n""",
        "Image reference signature",
    )
    text = replace_once(
        text,
        """            TextButton(\n                onClick = onChoose,\n                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 5.dp, vertical = 1.dp),\n            ) { Text(if (uriRef == null) \"Elegir\" else \"Cambiar\", style = MaterialTheme.typography.labelSmall) }\n            if (uriRef != null) {\n                StableRemoveIconButton(onClick = onClear, contentDescription = \"Quitar $title\")\n            }\n""",
        """            if (structuralEditingEnabled) {\n                TextButton(\n                    onClick = onChoose,\n                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 5.dp, vertical = 1.dp),\n                ) { Text(if (uriRef == null) \"Elegir\" else \"Cambiar\", style = MaterialTheme.typography.labelSmall) }\n                if (uriRef != null) {\n                    StableRemoveIconButton(onClick = onClear, contentDescription = \"Quitar $title\")\n                }\n            }\n""",
        "Image actions gate",
    )
    text = replace_once(
        text,
        """private fun CharacterDefensesSensesMovementCardV4(\n    state: CharacterClosureState,\n    onStateChange: (CharacterClosureState) -> Unit,\n    wide: Boolean,\n) {\n""",
        """private fun CharacterDefensesSensesMovementCardV4(\n    state: CharacterClosureState,\n    onStateChange: (CharacterClosureState) -> Unit,\n    wide: Boolean,\n    structuralEditingEnabled: Boolean,\n) {\n""",
        "Reference card signature",
    )
    text = text.replace(
        """                        addLabel = \"Añadir defensa\",\n                        modifier = Modifier.weight(1f),\n""",
        """                        addLabel = \"Añadir defensa\",\n                        structuralEditingEnabled = structuralEditingEnabled,\n                        modifier = Modifier.weight(1f),\n""",
        1,
    )
    text = text.replace(
        """                        addLabel = \"Añadir sentido\",\n                        modifier = Modifier.weight(1f),\n""",
        """                        addLabel = \"Añadir sentido\",\n                        structuralEditingEnabled = structuralEditingEnabled,\n                        modifier = Modifier.weight(1f),\n""",
        1,
    )
    text = text.replace(
        """                        addLabel = \"Añadir movimiento\",\n                        modifier = Modifier.weight(1f),\n""",
        """                        addLabel = \"Añadir movimiento\",\n                        structuralEditingEnabled = structuralEditingEnabled,\n                        modifier = Modifier.weight(1f),\n""",
        1,
    )
    for label in ("defensa", "sentido", "movimiento"):
        text = replace_once(
            text,
            f"""                    addLabel = \"Añadir {label}\",\n                )\n""",
            f"""                    addLabel = \"Añadir {label}\",\n                    structuralEditingEnabled = structuralEditingEnabled,\n                )\n""",
            f"Narrow {label} group",
        )
    text = replace_once(text, "    if (defenseEditorOpen) {\n", "    if (structuralEditingEnabled && defenseEditorOpen) {\n", "Defense editor gate")
    text = replace_once(text, "    defenseDeleteId?.let { id ->\n", "    if (structuralEditingEnabled) defenseDeleteId?.let { id ->\n", "Defense delete gate")
    text = replace_once(text, "    if (senseEditorOpen) {\n", "    if (structuralEditingEnabled && senseEditorOpen) {\n", "Sense editor gate")
    text = replace_once(text, "    senseDeleteId?.let { id ->\n", "    if (structuralEditingEnabled) senseDeleteId?.let { id ->\n", "Sense delete gate")
    text = replace_once(text, "    if (movementEditorOpen) {\n", "    if (structuralEditingEnabled && movementEditorOpen) {\n", "Movement editor gate")
    text = replace_once(text, "    movementDeleteId?.let { id ->\n", "    if (structuralEditingEnabled) movementDeleteId?.let { id ->\n", "Movement delete gate")
    text = replace_once(
        text,
        """    onAdd: () -> Unit,\n    addLabel: String,\n    modifier: Modifier = Modifier,\n) {\n""",
        """    onAdd: () -> Unit,\n    addLabel: String,\n    structuralEditingEnabled: Boolean,\n    modifier: Modifier = Modifier,\n) {\n""",
        "Reference group signature",
    )
    text = replace_once(
        text,
        """                    Column(\n                        modifier = Modifier.weight(1f).clickable { onOpen(entry) }.padding(vertical = 4.dp),\n                    ) {\n""",
        """                    Column(\n                        modifier = Modifier\n                            .weight(1f)\n                            .clickable(enabled = structuralEditingEnabled) { onOpen(entry) }\n                            .padding(vertical = 4.dp),\n                    ) {\n""",
        "Reference row click gate",
    )
    text = replace_once(
        text,
        """                    StableRemoveIconButton(onClick = { onDelete(entry) }, contentDescription = \"Eliminar ${label(entry)}\")\n                }\n            }\n            TextButton(onClick = onAdd) { Text(\"+ $addLabel\") }\n""",
        """                    if (structuralEditingEnabled) {\n                        StableRemoveIconButton(onClick = { onDelete(entry) }, contentDescription = \"Eliminar ${label(entry)}\")\n                    }\n                }\n            }\n            if (structuralEditingEnabled) {\n                TextButton(onClick = onAdd) { Text(\"+ $addLabel\") }\n            }\n""",
        "Reference structural actions gate",
    )
    GENERAL_CLOSURE.write_text(text, encoding="utf-8")


def main() -> None:
    migrate_editor()
    migrate_class_identity()
    migrate_general_closure()
    print("Applied bounded T8 Table Mode affordance migration")


if __name__ == "__main__":
    main()
