from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected one match, found {count}")
    return text.replace(old, new, 1)


path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt')
ui = path.read_text()

# Preserve enum identifiers/storage compatibility; only owner-approved visible labels change.
old_theme_enum = '''internal enum class AppThemeChoice(val label: String) {
    SYSTEM("Sistema"),
    LIGHT("Claro"),
    DARK("Oscuro"),
    GRAY("Gris"),
    DARK_PURPLE("Morado oscuro"),
    DARK_CYAN("Cian oscuro"),
    LIGHT_CYAN("Cian claro"),
    NIGHT_BLUE("Azul noche"),
    LIGHT_NIGHT_BLUE("Azul noche claro"),
    FOREST_GREEN("Verde bosque"),
    LIGHT_FOREST_GREEN("Verde bosque claro"),
    PARCHMENT("Pergamino"),
    HIGH_CONTRAST("Alto contraste"),
    MATRIX("Matrix"),
}
'''
new_theme_enum = '''internal enum class AppThemeChoice(val label: String) {
    SYSTEM("Sistema"),
    LIGHT("Claro"),
    DARK("Oscuro"),
    GRAY("Gris"),
    DARK_PURPLE("Púrpura"),
    DARK_CYAN("Cyan"),
    LIGHT_CYAN("Cyan claro"),
    NIGHT_BLUE("Noche"),
    LIGHT_NIGHT_BLUE("Noche despejada"),
    FOREST_GREEN("Bosque"),
    LIGHT_FOREST_GREEN("Oasis"),
    PARCHMENT("Pergamino"),
    CRIMSON("Carmesí"),
    AMBER("Ámbar"),
    GLACIER("Glaciar"),
    LAVENDER("Lavanda"),
    SLATE("Pizarra"),
    TERRACOTTA("Terracota"),
    HIGH_CONTRAST("Alto contraste"),
    MATRIX("Matrix"),
}
'''
ui = replace_once(ui, old_theme_enum, new_theme_enum, 'theme labels and additions')

# Six deliberately different audition families, with light/dark contrast pairs.
new_theme_cases = '''    AppThemeChoice.CRIMSON -> darkColorScheme(
        primary = Color(0xFFFFB3B8),
        onPrimary = Color(0xFF650019),
        primaryContainer = Color(0xFF8E1D35),
        onPrimaryContainer = Color(0xFFFFDADD),
        secondary = Color(0xFFE6BDC0),
        secondaryContainer = Color(0xFF5A3F42),
        background = Color(0xFF1B0C10),
        onBackground = Color(0xFFF6DDE0),
        surface = Color(0xFF251216),
        onSurface = Color(0xFFF6DDE0),
        surfaceVariant = Color(0xFF52383C),
        onSurfaceVariant = Color(0xFFDCC2C5),
        outline = Color(0xFFA78C90),
    )
    AppThemeChoice.AMBER -> darkColorScheme(
        primary = Color(0xFFFFCC80),
        onPrimary = Color(0xFF452B00),
        primaryContainer = Color(0xFF624000),
        onPrimaryContainer = Color(0xFFFFDDB0),
        secondary = Color(0xFFD9C3A4),
        secondaryContainer = Color(0xFF504532),
        background = Color(0xFF181108),
        onBackground = Color(0xFFF0E0CA),
        surface = Color(0xFF22180D),
        onSurface = Color(0xFFF0E0CA),
        surfaceVariant = Color(0xFF4A4032),
        onSurfaceVariant = Color(0xFFD2C4B1),
        outline = Color(0xFF9C8F7B),
    )
    AppThemeChoice.GLACIER -> lightColorScheme(
        primary = Color(0xFF285D78),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC7E8F8),
        onPrimaryContainer = Color(0xFF001E2B),
        secondary = Color(0xFF4E616B),
        secondaryContainer = Color(0xFFD1E6F0),
        background = Color(0xFFF1FAFF),
        onBackground = Color(0xFF151D21),
        surface = Color(0xFFFAFDFF),
        onSurface = Color(0xFF151D21),
        surfaceVariant = Color(0xFFDCE5E9),
        onSurfaceVariant = Color(0xFF40484C),
        outline = Color(0xFF70787C),
    )
    AppThemeChoice.LAVENDER -> lightColorScheme(
        primary = Color(0xFF66558A),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE9DDFF),
        onPrimaryContainer = Color(0xFF211047),
        secondary = Color(0xFF625B70),
        secondaryContainer = Color(0xFFE8DEF8),
        background = Color(0xFFFBF8FF),
        onBackground = Color(0xFF1D1A20),
        surface = Color(0xFFFFF8FF),
        onSurface = Color(0xFF1D1A20),
        surfaceVariant = Color(0xFFE8E0EB),
        onSurfaceVariant = Color(0xFF4A454E),
        outline = Color(0xFF7B757F),
    )
    AppThemeChoice.SLATE -> darkColorScheme(
        primary = Color(0xFFA9C7E5),
        onPrimary = Color(0xFF123149),
        primaryContainer = Color(0xFF294961),
        onPrimaryContainer = Color(0xFFCDE5FF),
        secondary = Color(0xFFBAC8D5),
        secondaryContainer = Color(0xFF354550),
        background = Color(0xFF10171E),
        onBackground = Color(0xFFDCE3EA),
        surface = Color(0xFF17212A),
        onSurface = Color(0xFFDCE3EA),
        surfaceVariant = Color(0xFF3F4851),
        onSurfaceVariant = Color(0xFFC1C7CE),
        outline = Color(0xFF8B9299),
    )
    AppThemeChoice.TERRACOTTA -> lightColorScheme(
        primary = Color(0xFF8A4633),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDBD1),
        onPrimaryContainer = Color(0xFF351000),
        secondary = Color(0xFF77574D),
        secondaryContainer = Color(0xFFFFDBD1),
        background = Color(0xFFFFF8F5),
        onBackground = Color(0xFF241914),
        surface = Color(0xFFFFF8F5),
        onSurface = Color(0xFF241914),
        surfaceVariant = Color(0xFFF5DED7),
        onSurfaceVariant = Color(0xFF53433E),
        outline = Color(0xFF85736D),
    )
'''
ui = replace_once(
    ui,
    '    AppThemeChoice.HIGH_CONTRAST -> darkColorScheme(\n',
    new_theme_cases + '    AppThemeChoice.HIGH_CONTRAST -> darkColorScheme(\n',
    'new theme color schemes',
)

# Column selectors now explain themselves visually using the actual chosen count and current app spacing.
layout_start = ui.index('@Composable\nprivate fun LayoutColumnSettingsV4(')
preview_start = ui.index('@Composable\nprivate fun SettingsSheetPreview(', layout_start)
new_layout = '''@Composable
private fun LayoutColumnSettingsV4(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
    ) {
        Text("Columnas de tarjetas", style = MaterialTheme.typography.labelLarge)
        Text(
            "Cada orientación conserva su propia preferencia. La miniatura muestra la distribución solicitada con el espaciado actual.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ColumnCountSettingV4(
            label = "Teléfono · vertical",
            value = preferences.phonePortraitColumns,
            options = (1..4).toList(),
            onSelect = { onPreferencesChange(preferences.copy(phonePortraitColumns = it)) },
        )
        ColumnCountSettingV4(
            label = "Teléfono · horizontal",
            value = preferences.phoneLandscapeColumns,
            options = (1..5).toList(),
            onSelect = { onPreferencesChange(preferences.copy(phoneLandscapeColumns = it)) },
        )
        ColumnCountSettingV4(
            label = "Tablet · vertical",
            value = preferences.tabletPortraitColumns,
            options = (1..5).toList(),
            onSelect = { onPreferencesChange(preferences.copy(tabletPortraitColumns = it)) },
        )
        ColumnCountSettingV4(
            label = "Tablet · horizontal",
            value = preferences.tabletLandscapeColumns,
            options = (1..6).toList(),
            onSelect = { onPreferencesChange(preferences.copy(tabletLandscapeColumns = it)) },
        )
    }
}

@Composable
private fun ColumnCountSettingV4(
    label: String,
    value: Int,
    options: List<Int>,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
    ) {
        SettingSelector(
            label = label,
            value = value.toString(),
            options = options,
            optionLabel = Int::toString,
            onSelect = onSelect,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(appSpacingV4(4.dp)),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            ) {
                repeat(value) { index ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Text(
                            if (index == 0) "Aa" else "${index + 1}",
                            modifier = Modifier.padding(horizontal = appSpacingV4(3.dp), vertical = appSpacingV4(4.dp)),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

'''
ui = ui[:layout_start] + new_layout + ui[preview_start:]

# Font source/provider stays in technical metadata but disappears from normal choice UI.
font_start = ui.index('@Composable\nprivate fun FontChoicePicker(')
theme_picker_start = ui.index('@Composable\nprivate fun ThemeChoicePicker(', font_start)
new_font_picker = '''@Composable
private fun FontChoicePicker(
    selected: AppFontChoice,
    onSelect: (AppFontChoice) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Tipografía", style = MaterialTheme.typography.labelLarge)
        AppFontChoice.entries.forEach { choice ->
            val isSelected = choice == selected
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(choice) },
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                ),
                tonalElevation = if (isSelected) 2.dp else 0.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(5.dp)),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
                    ) {
                        Text(
                            choice.label,
                            style = MaterialTheme.typography.titleSmall.copy(fontFamily = choice.family()),
                            maxLines = 1,
                        )
                        Text(
                            "Aventura · CD 15 · CA 17 · 1d20 + 7",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = choice.family()),
                            maxLines = 1,
                        )
                    }
                    if (isSelected) {
                        Text("Seleccionada", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

'''
ui = ui[:font_start] + new_font_picker + ui[theme_picker_start:]

# Remove audition framing and replace color swatches with a miniature usable sheet fragment.
ui = replace_once(
    ui,
    '        Text("Tema · audición", style = MaterialTheme.typography.labelLarge)\n',
    '        Text("Tema", style = MaterialTheme.typography.labelLarge)\n',
    'theme picker heading',
)
preview_card_start = ui.index('@Composable\nprivate fun ThemePreviewCard(')
selector_start = ui.index('@Composable\nprivate fun <T> SettingSelector(', preview_card_start)
new_theme_preview = '''@Composable
private fun ThemePreviewCard(
    choice: AppThemeChoice,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = resolveColorScheme(choice)
    Surface(
        modifier = modifier.clickable(onClick = onSelect),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(choice.label, style = MaterialTheme.typography.labelMedium, maxLines = 2)
                if (selected) Text("✓", style = MaterialTheme.typography.labelMedium)
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraSmall,
                color = scheme.background,
                contentColor = scheme.onBackground,
            ) {
                Column(
                    modifier = Modifier.padding(appSpacingV4(4.dp)),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraSmall,
                        color = scheme.primaryContainer,
                        contentColor = scheme.onPrimaryContainer,
                    ) {
                        Text(
                            "Alyra · Maga 7",
                            modifier = Modifier.padding(horizontal = appSpacingV4(4.dp), vertical = appSpacingV4(3.dp)),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.extraSmall,
                            color = scheme.surfaceVariant,
                            contentColor = scheme.onSurfaceVariant,
                        ) {
                            Text(
                                "CA 17",
                                modifier = Modifier.padding(appSpacingV4(3.dp)),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                            )
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.extraSmall,
                            color = scheme.primary,
                            contentColor = scheme.onPrimary,
                        ) {
                            Text(
                                "CD 15",
                                modifier = Modifier.padding(appSpacingV4(3.dp)),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

'''
ui = ui[:preview_card_start] + new_theme_preview + ui[selector_start:]

path.write_text(ui)
