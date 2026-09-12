from pathlib import Path

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
WORKFLOW = Path(".github/workflows/phase4a-p13-copy-audit.yml")
SCRIPT = Path(".github/scripts/phase4a_p13_copy_audit.py")


def replace_once(path: Path, old: str, new: str, label: str) -> None:
    text = path.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match in {path.name}, found {count}")
    path.write_text(text.replace(old, new, 1).rstrip() + "\n")


# Repeated split-editor prose: keep the action, remove obvious persistence narration.
replacements = [
    (
        "CharacterArtificeModuleV4.kt",
        "Selecciona un plan o dispositivo. La lista conserva búsqueda, filtros y orden mientras editas.",
        "Selecciona un plan o dispositivo.",
        "artifice editor hint",
    ),
    (
        "CharacterClassOptionModulesV4.kt",
        "Selecciona un registro para editarlo. La lista conserva búsqueda, filtros y orden.",
        "Selecciona un registro para editarlo.",
        "class-option editor hint",
    ),
    (
        "CharacterCompanionsModuleV4.kt",
        "Selecciona un compañero para editarlo. La lista conserva búsqueda, filtros y orden.",
        "Selecciona un compañero para editarlo.",
        "companion editor hint",
    ),
    (
        "CharacterEquipmentClosureV4.kt",
        "Selecciona un objeto de la lista para editarlo sin perder tu posición, búsqueda ni filtros.",
        "Selecciona un objeto para editarlo.",
        "equipment editor hint",
    ),
    (
        "CharacterFormsModuleV4.kt",
        "Selecciona una forma de la biblioteca o añade una nueva. La ficha base no se modifica al abrirla.",
        "Selecciona una forma o añade una nueva.",
        "forms editor hint",
    ),
    (
        "CharacterSpellListClosureV4.kt",
        "Selecciona un conjuro de la lista o añade uno nuevo. La lista conserva su búsqueda, filtros y posición mientras editas.",
        "Selecciona un conjuro o añade uno nuevo.",
        "spell editor hint",
    ),
    (
        "CharacterTraitsClosureV4.kt",
        "Clase, raza, trasfondo, dotes, dones / bendiciones y contenido personalizado.",
        "Rasgos del personaje y contenido personalizado.",
        "traits permanent subtitle",
    ),
]
for file_name, old, new, label in replacements:
    replace_once(ROOT / file_name, old, new, label)

# Redundant canonical-state narration under a read-only reference block: the labels/values already show this.
replace_once(
    ROOT / "CharacterCombatTabV4.kt",
    '''            Text(\n                "Estos valores son referencias de la misma ficha; no son copias independientes.",\n                style = MaterialTheme.typography.labelSmall,\n            )\n''',
    "",
    "combat redundant canonical-state note",
)

# Useful explanatory copy should use the P12 contextual-help system instead of permanent body prose.
replace_once(
    ROOT / "CharacterEditorV4.kt",
    '''        Text(\n            "Marca competencia cuando corresponda. Toca el total para ver el cálculo y editar Ajuste adicional.",\n            style = MaterialTheme.typography.labelSmall,\n        )\n''',
    '''        CharacterHelpV4(\n            "Marca competencia cuando corresponda. Toca el total para ver el cálculo y editar Ajuste adicional.",\n        )\n''',
    "saving-throw explanation to contextual help",
)
replace_once(
    ROOT / "CharacterEditorV4.kt",
    '''        Text(\n            "Las habilidades estándar se editan aquí. Las personalizadas se configuran en Ajustes del PJ y aparecen integradas en la misma lista.",\n            style = MaterialTheme.typography.labelSmall,\n        )\n''',
    '''        CharacterHelpV4(\n            "Las habilidades estándar se editan aquí. Las personalizadas se configuran en Ajustes del PJ y aparecen en esta misma lista.",\n        )\n''',
    "skill-list explanation to contextual help",
)
replace_once(
    ROOT / "CharacterCustomSkillsV4.kt",
    '''                    Text(\n                        "Homebrew u otras habilidades asociadas a una característica. Usan el mismo cálculo de competencia/pericia.",\n                        style = MaterialTheme.typography.labelSmall,\n                    )\n''',
    '''                    Text(\n                        "Homebrew y otras habilidades asociadas a una característica.",\n                        style = MaterialTheme.typography.labelSmall,\n                    )\n                    CharacterHelpV4(\n                        "Usan el mismo cálculo de competencia y pericia que las habilidades estándar.",\n                    )\n''',
    "custom-skill explanation split",
)
replace_once(
    ROOT / "CharacterProficienciesV4.kt",
    '''                    Text(\n                        "Idiomas, herramientas, armaduras, armas y otras competencias. La ficha no valida legalidad.",\n                        style = MaterialTheme.typography.labelSmall,\n                    )\n''',
    '''                    Text(\n                        "Idiomas, herramientas, armaduras, armas y otras competencias.",\n                        style = MaterialTheme.typography.labelSmall,\n                    )\n                    CharacterHelpV4(\n                        "La ficha conserva estas referencias, pero no valida su legalidad.",\n                    )\n''',
    "proficiency rule-boundary to contextual help",
)

# Guard P13: the clearly repetitive permanent prose audited above must be gone.
for path in ROOT.glob("*.kt"):
    text = path.read_text()
    forbidden = [
        "La lista conserva búsqueda, filtros y orden mientras editas.",
        "La lista conserva búsqueda, filtros y orden.",
        "sin perder tu posición, búsqueda ni filtros.",
        "La lista conserva su búsqueda, filtros y posición mientras editas.",
        "Estos valores son referencias de la misma ficha; no son copias independientes.",
    ]
    hits = [phrase for phrase in forbidden if phrase in text]
    if hits:
        raise SystemExit(f"{path.name}: repetitive P13 copy remains: {hits}")

# Temporary audit/repair machinery must not survive the repair commit.
WORKFLOW.unlink()
SCRIPT.unlink()
