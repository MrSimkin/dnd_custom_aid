#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

EDITOR = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
WRAPPER = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassIdentityV4.kt")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, observed {count}")
    return text.replace(old, new, 1)


def main() -> None:
    editor = EDITOR.read_text(encoding="utf-8")
    old_abilities = """private fun AbilitiesRowV4(\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n) {\n"""
    new_abilities = """private fun AbilitiesRowV4(\n    draft: CharacterEditorDraftV4,\n    onDraftChange: (CharacterEditorDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean = true,\n) {\n"""
    if old_abilities in editor:
        editor = replace_once(editor, old_abilities, new_abilities, "ability-row compatibility default")
        EDITOR.write_text(editor, encoding="utf-8")
    elif new_abilities not in editor:
        raise RuntimeError("ability-row compatibility shape not found")

    wrapper = WRAPPER.read_text(encoding="utf-8")
    old_wrapper = """internal fun CharacterClassIdentityCardV4(\n    classes: List<ClassLevelDraftV4>,\n    onClassesChange: (List<ClassLevelDraftV4>) -> Unit,\n) {\n    CharacterClassIdentitySuccessorCardV4(\n        classes = classes,\n        onClassesChange = onClassesChange,\n    )\n}\n"""
    new_wrapper = """internal fun CharacterClassIdentityCardV4(\n    classes: List<ClassLevelDraftV4>,\n    onClassesChange: (List<ClassLevelDraftV4>) -> Unit,\n    structuralEditingEnabled: Boolean = true,\n) {\n    CharacterClassIdentitySuccessorCardV4(\n        classes = classes,\n        onClassesChange = onClassesChange,\n        structuralEditingEnabled = structuralEditingEnabled,\n    )\n}\n"""
    if old_wrapper in wrapper:
        wrapper = replace_once(wrapper, old_wrapper, new_wrapper, "class identity wrapper forwarding")
        WRAPPER.write_text(wrapper, encoding="utf-8")
    elif new_wrapper not in wrapper:
        raise RuntimeError("class identity wrapper shape not found")

    print("Applied T8 compile compatibility fix")


if __name__ == "__main__":
    main()
