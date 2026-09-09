from pathlib import Path

path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellSourceEditorV4.kt')
text = path.read_text()
old = '''private fun spellOriginAbilityKeyV4(reference: CharacterAbilityReference): String = when {
    reference.builtIn != null -> "BUILTIN:${reference.builtIn.name}"
    reference.customAttributeId != null -> "CUSTOM:${reference.customAttributeId}"
    else -> "NONE"
}
'''
new = '''private fun spellOriginAbilityKeyV4(reference: CharacterAbilityReference): String {
    val builtIn = reference.builtIn
    if (builtIn != null) return "BUILTIN:${builtIn.name}"
    val customAttributeId = reference.customAttributeId
    if (customAttributeId != null) return "CUSTOM:$customAttributeId"
    return "NONE"
}
'''
if text.count(old) != 1:
    raise SystemExit(f'guard failed: expected one ability-key helper, found {text.count(old)}')
path.write_text(text.replace(old, new, 1))
print('Applied cross-module smart-cast compiler fix')
