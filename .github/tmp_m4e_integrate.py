from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    assert count == 1, f"{label}: expected exactly one match, found {count}"
    return text.replace(old, new, 1)


class_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassIdentityV4.kt')
text = class_path.read_text(encoding='utf-8')

text = replace_once(
    text,
    '''    val secondary = listOfNotNull(\n        rulesFamilyLabelClassV4(identityRules).takeIf { identityRules != CharacterRulesFamily.UNSPECIFIED },\n        identitySource?.takeIf { it.isNotBlank() },\n    ).joinToString(" · ").ifBlank { "Identidad manual / sin fuente especificada" }\n''',
    '',
    'remove combined class rules/source text',
)

text = replace_once(
    text,
    '''                Text(secondary, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)\n''',
    '''                CharacterRulesSourceBadgesV4(\n                    rulesFamily = identityRules,\n                    source = identitySource,\n                )\n''',
    'class row semantic badges',
)

text = replace_once(
    text,
    '''            selectedClass?.let { entry ->\n                Text(\n                    "${rulesFamilyLabelClassV4(entry.rulesFamily)} · ${entry.source}",\n                    style = MaterialTheme.typography.labelSmall,\n                )\n            }\n''',
    '''            selectedClass?.let { entry ->\n                CharacterRulesSourceBadgesV4(\n                    rulesFamily = entry.rulesFamily,\n                    source = entry.source,\n                )\n            }\n''',
    'official class selected badges',
)

text = replace_once(
    text,
    '''                selectedSubclass?.let { entry ->\n                    Text(\n                        "${rulesFamilyLabelClassV4(entry.rulesFamily)} · ${entry.source}",\n                        style = MaterialTheme.typography.labelSmall,\n                    )\n                }\n''',
    '''                selectedSubclass?.let { entry ->\n                    CharacterRulesSourceBadgesV4(\n                        rulesFamily = entry.rulesFamily,\n                        source = entry.source,\n                    )\n                }\n''',
    'official subclass selected badges',
)

assert text.count('CharacterRulesSourceBadgesV4(') == 3
assert 'val secondary = listOfNotNull(' not in text
class_path.write_text(text, encoding='utf-8')

prefs_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt')
prefs = prefs_path.read_text(encoding='utf-8')

prefs = replace_once(
    prefs,
    '''import androidx.compose.ui.unit.Density\nimport androidx.compose.ui.unit.dp\n\ninternal enum class AppFontChoice''',
    '''import androidx.compose.ui.unit.Density\nimport androidx.compose.ui.unit.dp\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily\nimport io.github.mrsimkin.dndcustomaid.shared.character.characterRulesFamilyBadgeLabel\n\ninternal enum class AppFontChoice''',
    'settings semantic label imports',
)

prefs = replace_once(
    prefs,
    '''                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {\n                            SettingsPreviewBadge("5.5e")\n                            SettingsPreviewBadge("Preparado")\n                        }\n''',
    '''                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {\n                            CharacterSemanticBadgeV4(\n                                label = characterRulesFamilyBadgeLabel(CharacterRulesFamily.DND_5_5E),\n                                kind = CharacterSemanticBadgeKindV4.RULES,\n                            )\n                            CharacterSemanticBadgeV4(\n                                label = "Preparado",\n                                kind = CharacterSemanticBadgeKindV4.STATE,\n                            )\n                        }\n''',
    'settings miniature shared badges',
)

prefs = replace_once(
    prefs,
    '''@Composable\nprivate fun SettingsPreviewBadge(label: String) {\n    Surface(\n        shape = MaterialTheme.shapes.extraSmall,\n        color = MaterialTheme.colorScheme.secondaryContainer,\n    ) {\n        Text(\n            label,\n            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),\n            style = MaterialTheme.typography.labelSmall,\n            color = MaterialTheme.colorScheme.onSecondaryContainer,\n        )\n    }\n}\n\n''',
    '',
    'remove local settings badge duplicate',
)

assert 'SettingsPreviewBadge' not in prefs
assert 'characterRulesFamilyBadgeLabel(CharacterRulesFamily.DND_5_5E)' in prefs
assert 'CharacterSemanticBadgeKindV4.STATE' in prefs
prefs_path.write_text(prefs, encoding='utf-8')
