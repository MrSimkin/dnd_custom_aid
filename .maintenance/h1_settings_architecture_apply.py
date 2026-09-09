from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected one match, found {count}")
    return text.replace(old, new, 1)


# MainActivity: one app-wide haptic preference provider and an explicit full-screen overlay
# that keeps the underlying character editor composed while settings are open.
main_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt')
main = main_path.read_text()
main = replace_once(
    main,
    '    private val uiPreferencesStore by lazy { UiPreferencesStore(applicationContext) }\n    private val characterNavigationPreferenceStore by lazy { CharacterNavigationPreferenceStore(applicationContext) }\n',
    '    private val uiPreferencesStore by lazy { UiPreferencesStore(applicationContext) }\n    private val hapticPreferencesStore by lazy { CharacterHapticPreferencesStore(applicationContext) }\n    private val characterNavigationPreferenceStore by lazy { CharacterNavigationPreferenceStore(applicationContext) }\n',
    'root haptic store',
)
main = replace_once(
    main,
    '            DndCustomAidTheme(preferences = preferences) {\n                DndCustomAidApp(\n',
    '            DndCustomAidTheme(preferences = preferences) {\n                CharacterHapticSettingsProviderV4(store = hapticPreferencesStore) {\n                    DndCustomAidApp(\n',
    'root haptic provider opening',
)
main = replace_once(
    main,
    '                    onPreferencesChange = ::updatePreferences,\n                )\n            }\n',
    '                        onPreferencesChange = ::updatePreferences,\n                    )\n                }\n            }\n',
    'root haptic provider closing',
)
main = replace_once(
    main,
    '    when (screen) {\n',
    '    Box(modifier = Modifier.fillMaxSize()) {\n        when (screen) {\n',
    'root screen box opening',
)
old_tail = '''    }

    if (showSettings) {
        AppSettingsDialog(
            preferences = preferences,
            onPreferencesChange = onPreferencesChange,
            onDismiss = { showSettings = false },
        )
    }
}
'''
new_tail = '''        }

        if (showSettings) {
            AppSettingsScreen(
                preferences = preferences,
                onPreferencesChange = onPreferencesChange,
                onDismiss = { showSettings = false },
            )
        }
    }
}
'''
main = replace_once(main, old_tail, new_tail, 'full-screen settings overlay')
main_path.write_text(main)


# Character PC state no longer owns a duplicate device-wide haptic provider.
pc_context_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterPcSettingsContextV4.kt')
pc_context = pc_context_path.read_text()
pc_context = pc_context.replace('import androidx.compose.ui.platform.LocalContext\n', '')
pc_context = replace_once(
    pc_context,
    '''    val androidContext = LocalContext.current.applicationContext
    val hapticStore = remember(androidContext) { CharacterHapticPreferencesStore(androidContext) }

''',
    '',
    'remove nested haptic store',
)
pc_context = replace_once(
    pc_context,
    '''    CharacterHapticSettingsProviderV4(store = hapticStore) {
        CompositionLocalProvider(LocalCharacterPcSettingsContextV4 provides settingsContext) {
            content()
        }
    }
''',
    '''    CompositionLocalProvider(LocalCharacterPcSettingsContextV4 provides settingsContext) {
        content()
    }
''',
    'remove nested haptic provider',
)
pc_context_path.write_text(pc_context)


# PC Settings keeps per-character activation only; global strength/duration moves to Application Settings.
pc_successor_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterPcSuccessorSettingsV4.kt')
pc_successor = pc_successor_path.read_text()
start = pc_successor.index('@Composable\ninternal fun CharacterHapticProfileSettingsV4(')
end = pc_successor.index('\n@Composable\nprivate fun CustomAttributeEditorDialogV4(', start)
new_haptic_profile = '''@Composable
internal fun CharacterHapticProfileSettingsV4(
    closureState: CharacterClosureState,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
    val hapticContext = LocalCharacterHapticSettingsV4.current

    SuccessorSettingCardV4(
        title = "Respuesta háptica",
        description = "La activación pertenece a esta ficha. La intensidad y la duración son preferencias del dispositivo y se cambian en Configuración de la aplicación.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(if (closureState.hapticsEnabled) "Activada" else "Desactivada")
            Switch(
                checked = closureState.hapticsEnabled,
                onCheckedChange = { enabled ->
                    onClosureStateChange(closureState.copy(hapticsEnabled = enabled))
                },
            )
        }
        Text(
            "Perfil del dispositivo · Intensidad ${hapticContext.preferences.strength.label} · Duración ${hapticContext.preferences.duration.label}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
'''
pc_successor = pc_successor[:start] + new_haptic_profile + pc_successor[end:]
pc_successor_path.write_text(pc_successor)


# Application Settings: real full-screen surface, discrete stepped sliders with live examples,
# and the single device-wide haptic strength/duration controls.
ui_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt')
ui = ui_path.read_text()
ui = replace_once(
    ui,
    'import androidx.compose.foundation.layout.fillMaxWidth\n',
    'import androidx.compose.foundation.layout.fillMaxSize\nimport androidx.compose.foundation.layout.fillMaxWidth\n',
    'fillMaxSize import',
)
ui = replace_once(
    ui,
    'import androidx.compose.foundation.layout.size\n',
    'import androidx.compose.foundation.layout.size\nimport androidx.compose.foundation.layout.statusBarsPadding\n',
    'status bars import',
)
ui = replace_once(
    ui,
    'import androidx.compose.material3.Surface\n',
    'import androidx.compose.material3.Surface\nimport androidx.compose.material3.Slider\n',
    'Slider import',
)
ui = replace_once(
    ui,
    'import androidx.compose.ui.unit.dp\n',
    'import androidx.compose.ui.unit.dp\nimport kotlin.math.roundToInt\n',
    'roundToInt import',
)
# Put density options in natural slider direction while preserving exactly the approved values.
ui = replace_once(
    ui,
    'internal val SPACING_SCALE_OPTIONS = listOf(100, 90, 80, 70, 60, 40)\n',
    'internal val SPACING_SCALE_OPTIONS = listOf(40, 60, 70, 80, 90, 100)\n',
    'spacing slider order',
)

settings_start = ui.index('@Composable\ninternal fun AppSettingsDialog(')
about_start = ui.index('@Composable\nprivate fun AboutBuildDialogV4(', settings_start)
new_settings = '''@Composable
internal fun AppSettingsScreen(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    var showAbout by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val phoneLike = minOf(configuration.screenWidthDp, configuration.screenHeightDp) < 600
    val veryLargePhoneText = phoneLike && preferences.fontScalePercent >= 145

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(4.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            ) {
                StableBackIconButton(
                    onClick = onDismiss,
                    contentDescription = "Volver desde Configuración de la aplicación",
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("Configuración de la aplicación", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Cambios inmediatos · preferencias de este dispositivo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TextButton(onClick = { showAbout = true }) { Text("Acerca de") }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = appSpacingV4(8.dp),
                    end = appSpacingV4(8.dp),
                    top = appSpacingV4(4.dp),
                    bottom = appSpacingV4(18.dp),
                ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(9.dp)),
            ) {
                item {
                    SteppedPercentSettingV4(
                        label = "Tamaño de texto",
                        value = preferences.fontScalePercent,
                        options = FONT_SCALE_OPTIONS,
                        onSelect = { onPreferencesChange(preferences.copy(fontScalePercent = it)) },
                        previewTitle = "Ejemplo de texto",
                        previewPrimary = "Alyra Voss · Maga 7",
                        previewSecondary = "CD 15 · CA 17 · 1d20 + 7",
                    )
                    if (veryLargePhoneText) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                "Advertencia para teléfono: ${preferences.fontScalePercent}% reduce mucho el área útil. La app conserva el valor y recurre a scroll cuando sea necesario.",
                                modifier = Modifier.padding(appSpacingV4(7.dp)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }
                    }
                }
                item {
                    SteppedPercentSettingV4(
                        label = "Compactación de espacios",
                        value = preferences.spacingScalePercent,
                        options = SPACING_SCALE_OPTIONS,
                        onSelect = { onPreferencesChange(preferences.copy(spacingScalePercent = it)) },
                        previewTitle = "Ejemplo de espaciado",
                        previewPrimary = "Tarjeta compacta",
                        previewSecondary = "Margen · separación · contenido",
                    )
                    Text(
                        "No reemplaza la vista Supercompacta. Reduce márgenes, paddings y separaciones controlados por la app; iconos y touch targets conservan su tamaño.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                        SettingSelector(
                            label = "Ayuda contextual",
                            value = preferences.helpMode.label,
                            options = CharacterHelpModeV4.entries,
                            optionLabel = { it.label },
                            onSelect = { onPreferencesChange(preferences.copy(helpMode = it)) },
                        )
                        Text(
                            "Controla si las explicaciones aparecen siempre, desde ⓘ, o se ocultan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item { HapticDeviceSettingsV4() }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                        SettingSelector(
                            label = "Resultados de dados",
                            value = preferences.diceResultMode.label,
                            options = DiceResultModeChoice.entries,
                            optionLabel = { it.label },
                            onSelect = { onPreferencesChange(preferences.copy(diceResultMode = it)) },
                        )
                        Text(
                            "El resultado compacto prioriza densidad. Dados visibles muestra los d20 de forma prominente; ambos conservan la misma descomposición matemática.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item {
                    FontChoicePicker(
                        selected = preferences.fontChoice,
                        onSelect = { onPreferencesChange(preferences.copy(fontChoice = it)) },
                    )
                }
                item {
                    LayoutColumnSettingsV4(
                        preferences = preferences,
                        onPreferencesChange = onPreferencesChange,
                    )
                }
                item {
                    ThemeChoicePicker(
                        selected = preferences.themeChoice,
                        onSelect = { onPreferencesChange(preferences.copy(themeChoice = it)) },
                    )
                }
                item { SettingsSheetPreview(preferences) }
            }
        }
    }

    if (showAbout) {
        AboutBuildDialogV4(onDismiss = { showAbout = false })
    }
}

@Composable
private fun SteppedPercentSettingV4(
    label: String,
    value: Int,
    options: List<Int>,
    onSelect: (Int) -> Unit,
    previewTitle: String,
    previewPrimary: String,
    previewSecondary: String,
) {
    val currentIndex = options.indexOf(value).coerceAtLeast(0)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text("$value%", style = MaterialTheme.typography.titleSmall)
        }
        Slider(
            value = currentIndex.toFloat(),
            onValueChange = { rawIndex ->
                val index = rawIndex.roundToInt().coerceIn(options.indices)
                val selected = options[index]
                if (selected != value) onSelect(selected)
            },
            valueRange = 0f..options.lastIndex.toFloat(),
            steps = (options.size - 2).coerceAtLeast(0),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("${options.first()}%", style = MaterialTheme.typography.labelSmall)
            Text("${options.last()}%", style = MaterialTheme.typography.labelSmall)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(
                modifier = Modifier.padding(appSpacingV4(7.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            ) {
                Text(previewTitle, style = MaterialTheme.typography.labelSmall)
                Text(previewPrimary, style = MaterialTheme.typography.titleSmall)
                Text(previewSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun HapticDeviceSettingsV4() {
    val hapticContext = LocalCharacterHapticSettingsV4.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Respuesta háptica · dispositivo", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SettingSelector(
                    label = "Intensidad",
                    value = hapticContext.preferences.strength.label,
                    options = CharacterHapticStrengthV4.entries,
                    optionLabel = { it.label },
                    onSelect = { option ->
                        hapticContext.onChange(hapticContext.preferences.copy(strength = option))
                    },
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                SettingSelector(
                    label = "Duración",
                    value = hapticContext.preferences.duration.label,
                    options = CharacterHapticDurationV4.entries,
                    optionLabel = { it.label },
                    onSelect = { option ->
                        hapticContext.onChange(hapticContext.preferences.copy(duration = option))
                    },
                )
            }
        }
        CharacterHelpV4(
            "Intensidad y duración son preferencias globales de este dispositivo. Activar o desactivar la respuesta háptica sigue perteneciendo a cada ficha de personaje.",
        )
    }
}

'''
ui = ui[:settings_start] + new_settings + ui[about_start:]
ui_path.write_text(ui)
