package io.github.mrsimkin.dndcustomaid.android

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.characterRulesFamilyBadgeLabel
// Refactor F3: the App's Configuración screen and its previews, moved out of UiPreferences.kt (which keeps the preferences, fonts and theme).

@Composable
internal fun AppSettingsScreen(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
    onDismiss: () -> Unit,
) {
    var showAbout by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                        "Preferencias de este dispositivo",
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
                // Owner point 5: one public table of what is done where, the same in both Apps.
                item { io.github.mrsimkin.dndcustomaid.dmui.WhereToDoItCard() }
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
                                "En teléfono, ${preferences.fontScalePercent}% deja menos área útil; la app mantiene el valor y usa scroll cuando hace falta.",
                                modifier = Modifier.padding(appSpacingV4(7.dp)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }
                    }
                }
                item {
                    SteppedPercentSettingV4(
                        label = "Densidad de espacios",
                        value = preferences.spacingScalePercent,
                        options = SPACING_SCALE_OPTIONS,
                        onSelect = { onPreferencesChange(preferences.copy(spacingScalePercent = it)) },
                        previewTitle = when {
                            preferences.spacingScalePercent < 100 -> "Más denso"
                            preferences.spacingScalePercent > 100 -> "Más espacioso"
                            else -> "Equilibrado"
                        },
                        previewPrimary = "Tarjeta y contenido",
                        previewSecondary = "Margen · separación · padding interno",
                    )
                    Text(
                        "50–90% = más denso · 100% = equilibrado · 110–150% = más espacioso. Texto, iconos y áreas táctiles conservan su tamaño.",
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
                            "Muestra explicaciones siempre, desde el icono de información, o las oculta.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item { HapticDeviceSettingsV4() }
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
                if (BuildConfig.DEBUG) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                            Text(
                                "Herramientas DEV",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                "Solo disponible en builds debug. Abre autenticación, sincronización y diagnóstico QA hospedado.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(
                                        Intent().setClassName(
                                            context.packageName,
                                            MainActivity::class.java.name.substringBeforeLast('.') + ".HostedDevAuthActivity",
                                        ),
                                    )
                                },
                            ) {
                                Text("Abrir QA / diagnóstico DEV")
                            }
                        }
                    }
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
internal fun SteppedPercentSettingV4(
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
internal fun HapticDeviceSettingsV4() {
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
            "Ninguna desactiva la respuesta háptica generada por la app en este dispositivo. Intensidad y duración son globales; el interruptor de cada ficha sigue permitiendo desactivar sus hápticos individualmente.",
        )
    }
}

@Composable
internal fun AboutBuildDialogV4(onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier.imePadding().navigationBarsPadding(),
        onDismissRequest = onDismiss,
        title = { Text("Acerca de D&D Custom Aid") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column(
                        modifier = Modifier.padding(appSpacingV4(8.dp)),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
                    ) {
                        Text("VERSIÓN", style = MaterialTheme.typography.labelMedium)
                        Text(BuildConfig.VERSION_NAME, style = MaterialTheme.typography.titleLarge)
                        Text("BUILD", style = MaterialTheme.typography.labelMedium)
                        Text(BuildConfig.VERSION_CODE.toString(), style = MaterialTheme.typography.titleLarge)
                        Text("TIPO", style = MaterialTheme.typography.labelMedium)
                        Text(BuildConfig.BUILD_TYPE.uppercase(), style = MaterialTheme.typography.titleMedium)
                    }
                }
                Text(
                    "Revisión actual: versión ${BuildConfig.VERSION_NAME} · build ${BuildConfig.VERSION_CODE} · ${BuildConfig.BUILD_TYPE}.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(BuildConfig.APPLICATION_ID, style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Cerrar") } },
    )
}

@Composable
internal fun LayoutColumnSettingsV4(
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
    ) {
        Text("Distribución de tarjetas", style = MaterialTheme.typography.labelLarge)
        Text(
            "Cada orientación conserva una preferencia adaptativa. La app calcula cuántas tarjetas caben con el ancho disponible, el tamaño de texto y la densidad de espacios; no promete un número exacto de columnas.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SettingSelector(
            label = "Vertical",
            value = preferences.portraitCardDensity.label,
            options = CharacterCardDensityV4.entries,
            optionLabel = { it.label },
            onSelect = { onPreferencesChange(preferences.copy(portraitCardDensity = it)) },
        )
        SettingSelector(
            label = "Horizontal",
            value = preferences.landscapeCardDensity.label,
            options = CharacterCardDensityV4.entries,
            optionLabel = { it.label },
            onSelect = { onPreferencesChange(preferences.copy(landscapeCardDensity = it)) },
        )
        Text(
            "Cómodo prioriza tarjetas más anchas; Equilibrado es el valor normal; Compacto y Denso aprovechan progresivamente más ancho cuando sigue siendo legible.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun SettingsSheetPreview(preferences: UiPreferences) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Vista previa · ficha", style = MaterialTheme.typography.labelLarge)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(appSpacingV4(6.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(4.dp)),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
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
                        Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                            CharacterSemanticBadgeV4(
                                label = characterRulesFamilyBadgeLabel(CharacterRulesFamily.DND_5_5E),
                                kind = CharacterSemanticBadgeKindV4.RULES,
                            )
                            CharacterSemanticBadgeV4(
                                label = "Preparado",
                                kind = CharacterSemanticBadgeKindV4.STATE,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                ) {
                    SettingsPreviewStatCell("CA", "17", Modifier.weight(1f))
                    SettingsPreviewStatCell("PG", "42 / 42", Modifier.weight(1f))
                    SettingsPreviewStatCell("CD", "15", Modifier.weight(1f))
                }

                Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
                    Text("Bola de fuego · Nivel 3", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Concentración · V/S/M",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                CharacterCompactOutlinedTextFieldV4(
                    value = "Texto libre para historia, notas o descripciones largas.",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Texto libre") },
                    minLines = characterCompactTextAreaMinLinesV4(2),
                    maxLines = 3,
                )

                Text(
                    "${preferences.themeChoice.label} · ${preferences.fontChoice.label} · Texto ${preferences.fontScalePercent}% · Espacios ${preferences.spacingScalePercent}% · Ayuda ${preferences.helpMode.label}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun SettingsPreviewStatCell(
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
            modifier = Modifier.padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(3.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
internal fun FontChoicePicker(
    selected: AppFontChoice,
    onSelect: (AppFontChoice) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Tipografía", style = MaterialTheme.typography.labelLarge)
        SELECTABLE_FONT_CHOICES_V4.forEach { choice ->
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

@Composable
internal fun ThemeChoicePicker(
    selected: AppThemeChoice,
    onSelect: (AppThemeChoice) -> Unit,
) {
    val width = LocalConfiguration.current.screenWidthDp
    val columns = when {
        width >= 900 -> 4
        width >= 600 -> 3
        else -> 2
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Text("Tema", style = MaterialTheme.typography.labelLarge)
        AppThemeChoice.entries.chunked(columns).forEach { rowThemes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                rowThemes.forEach { choice ->
                    ThemePreviewCard(
                        choice = choice,
                        selected = choice == selected,
                        onSelect = { onSelect(choice) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowThemes.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun ThemePreviewCard(
    choice: AppThemeChoice,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = resolveColorScheme(choice)
    Surface(
        modifier = modifier.clickable(onClick = onSelect),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(5.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(choice.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, maxLines = 2)
                Surface(
                    modifier = Modifier.size(16.dp),
                    shape = CircleShape,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    border = BorderStroke(1.5.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                ) {}
            }
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                ThemePaletteSwatchV4(palette.background)
                ThemePaletteSwatchV4(palette.surface)
                ThemePaletteSwatchV4(palette.primary)
            }
        }
    }
}

@Composable
internal fun ThemePaletteSwatchV4(color: Color) {
    Surface(
        modifier = Modifier.size(18.dp),
        shape = MaterialTheme.shapes.extraSmall,
        color = color,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {}
}

@Composable
internal fun <T> SettingSelector(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(value, maxLines = 2)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option), maxLines = 2) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
