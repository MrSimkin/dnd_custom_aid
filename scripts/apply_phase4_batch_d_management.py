from pathlib import Path

path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
text = path.read_text(encoding="utf-8")

persist_marker = """    fun persistClosureState(updated: CharacterClosureState) {
        if (updated == closureState) return
        closureState = closureRepository.saveState(characterId, updated)
        savedMessage = \"Guardado\"
    }
"""
if text.count(persist_marker) != 1:
    raise RuntimeError(f"persist marker expected once, found {text.count(persist_marker)}")
persist_replacement = persist_marker + """

    fun persistOperationalSheet(updated: CharacterSheet) {
        if (updated == stored) return
        stored = repository.saveCharacter(updated)
        savedMessage = \"Guardado\"
    }
"""
text = text.replace(persist_marker, persist_replacement, 1)

equipment_marker = "                        CharacterTabV4.EQUIPMENT -> CharacterEquipmentTabV4(\n"
if text.count(equipment_marker) != 1:
    raise RuntimeError(f"equipment branch marker expected once, found {text.count(equipment_marker)}")
management_branch = """                        CharacterTabV4.MANAGEMENT -> CharacterManagementTabV4(
                            sheet = stored,
                            closureState = closureState,
                            onSheetChange = ::persistOperationalSheet,
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
"""
text = text.replace(equipment_marker, management_branch + equipment_marker, 1)

path.write_text(text, encoding="utf-8")
print("Batch D Gestión editor integration applied.")
