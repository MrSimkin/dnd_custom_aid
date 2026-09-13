from pathlib import Path

path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
text = path.read_text(encoding="utf-8")

replacements = [
    (
        "import io.github.mrsimkin.dndcustomaid.shared.character.mergeCharacterOperationalState\nimport io.github.mrsimkin.dndcustomaid.shared.character.standardProficiencyBonusForLevel",
        "import io.github.mrsimkin.dndcustomaid.shared.character.mergeCharacterOperationalState\nimport io.github.mrsimkin.dndcustomaid.shared.character.setCharacterHitPoints\nimport io.github.mrsimkin.dndcustomaid.shared.character.standardProficiencyBonusForLevel",
        "canonical HP import",
    ),
    (
        "        val proficiencies = characterProficienciesFromJsonV4(proficiencyDraftJson)\n        val integrated = candidate.copy(\n",
        "        val proficiencies = characterProficienciesFromJsonV4(proficiencyDraftJson)\n        val normalizedCandidate = setCharacterHitPoints(\n            sheet = candidate,\n            currentHp = candidate.currentHp,\n            maxHp = candidate.maxHp,\n        ).copy(tempHp = candidate.tempHp.coerceAtLeast(0))\n        val integrated = normalizedCandidate.copy(\n",
        "normal structural save HP normalization",
    ),
    (
        "        draft = draft.copy(\n            currentHp = stored.currentHp.toString(),\n            tempHp = stored.tempHp.toString(),",
        "        draft = draft.copy(\n            maxHp = stored.maxHp.toString(),\n            currentHp = stored.currentHp.toString(),\n            tempHp = stored.tempHp.toString(),",
        "operational draft max HP sync",
    ),
    (
        "    fun persistOperationalSheet(updated: CharacterSheet) {\n        val effective = mergeCharacterOperationalState(stored, updated)\n        if (effective == stored) return\n        stored = repository.saveCharacter(effective)\n        syncOperationalDraftsFromStored()\n        savedMessage = \"Guardado\"\n    }\n\n    fun persistStructuralSheet(updated: CharacterSheet) {",
        "    fun persistOperationalSheet(updated: CharacterSheet) {\n        val effective = mergeCharacterOperationalState(stored, updated)\n        if (effective == stored) return\n        stored = repository.saveCharacter(effective)\n        syncOperationalDraftsFromStored()\n        savedMessage = \"Guardado\"\n    }\n\n    fun persistGeneralHitPointsFromDraft() {\n        val currentHp = draft.currentHp.trim().toIntOrNull() ?: return\n        val maxHp = draft.maxHp.trim().toIntOrNull() ?: return\n        val tempHp = draft.tempHp.trim().toIntOrNull() ?: return\n        val updated = setCharacterHitPoints(\n            sheet = stored,\n            currentHp = currentHp,\n            maxHp = maxHp,\n        ).copy(tempHp = tempHp.coerceAtLeast(0))\n        persistOperationalSheet(updated)\n    }\n\n    fun persistStructuralSheet(updated: CharacterSheet) {",
        "General canonical HP flush",
    ),
    (
        "                    onSelect = { selectedTabName = it.name },",
        "                    onSelect = { targetTab ->\n                        if (selectedTab == CharacterTabV4.OVERVIEW && targetTab != CharacterTabV4.OVERVIEW) {\n                            persistGeneralHitPointsFromDraft()\n                        }\n                        selectedTabName = targetTab.name\n                    },",
        "General-to-other-tab HP flush",
    ),
]

for old, new, label in replacements:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one source match, found {count}")
    text = text.replace(old, new, 1)

path.write_text(text, encoding="utf-8")
print("R2 editor patch applied with all exact-match guards satisfied")
