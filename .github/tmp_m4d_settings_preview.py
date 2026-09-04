from pathlib import Path

path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt')
text = path.read_text(encoding='utf-8')


def replace_once(old: str, new: str, label: str) -> None:
    global text
    count = text.count(old)
    assert count == 1, f'{label}: expected exactly one match, found {count}'
    text = text.replace(old, new, 1)

replace_once(
'''                item {
                    ThemeChoicePicker(
                        selected = preferences.themeChoice,
                        onSelect = { onPreferencesChange(preferences.copy(themeChoice = it)) },
                    )
                }
                item {
                    Text(
''',
'''                item {
                    ThemeChoicePicker(
                        selected = preferences.themeChoice,
                        onSelect = { onPreferencesChange(preferences.copy(themeChoice = it)) },
                    )
                }
                item {
                    SettingsSheetPreview(preferences)
                }
                item {
                    Text(
''',
'settings preview reachability',
)

insert_before = '''@Composable
private fun FontChoicePicker(
'''
preview = '''@Composable
private fun SettingsSheetPreview(preferences: UiPreferences) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text("Vista previa · ficha", style = MaterialTheme.typography.labelLarge)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            "Alyra Voss",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            "Maga 7 · Evocación",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            SettingsPreviewBadge("5.5e")
                            SettingsPreviewBadge("Preparado")
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    SettingsPreviewStatCell("CA", "17", Modifier.weight(1f))
                    SettingsPreviewStatCell("PG", "42 / 42", Modifier.weight(1f))
                    SettingsPreviewStatCell("CD", "15", Modifier.weight(1f))
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Bola de fuego · Nivel 3", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Concentración · V/S/M",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    "${preferences.themeChoice.label} · ${preferences.fontChoice.label} · Texto ${preferences.fontScalePercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SettingsPreviewBadge(label: String) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
private fun SettingsPreviewStatCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.titleSmall)
        }
    }
}

'''
count = text.count(insert_before)
assert count == 1, f'preview function insertion: expected exactly one marker, found {count}'
text = text.replace(insert_before, preview + insert_before, 1)

assert 'SettingsSheetPreview(preferences)' in text
assert '"Alyra Voss"' in text
assert 'SettingsPreviewStatCell("CA", "17"' in text
assert 'SettingsPreviewBadge("Preparado")' in text
assert 'preferences.fontScalePercent' in text
path.write_text(text, encoding='utf-8')
