package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
internal fun DesktopApplicationSettingsScreen(
    preferences: DesktopPreferences,
    onPreferencesChange: (DesktopPreferences) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(desktopSpacing(18.dp)),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(4.dp))) {
                Text(
                    text = "Configuración de la aplicación",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Preferencias de este dispositivo con vista previa antes de seleccionar fuente y tema.",
                    style = MaterialTheme.typography.body1,
                )
            }
        }

        item {
            DesktopPercentSlider(
                label = "Tamaño de texto",
                value = preferences.fontScalePercent,
                options = DESKTOP_FONT_SCALE_OPTIONS,
                onSelect = { onPreferencesChange(preferences.copy(fontScalePercent = it)) },
                detail = "Ajusta la escala tipográfica del workbench sin cambiar los datos de la campaña.",
            )
        }

        item {
            DesktopPercentSlider(
                label = "Densidad de espacios",
                value = preferences.spacingScalePercent,
                options = DESKTOP_SPACING_SCALE_OPTIONS,
                onSelect = { onPreferencesChange(preferences.copy(spacingScalePercent = it)) },
                detail = "50–90% = más denso · 100% = equilibrado · 110–150% = más espacioso.",
            )
        }

        item {
            FontPreviewSelector(
                selected = preferences.fontChoice,
                onSelect = { onPreferencesChange(preferences.copy(fontChoice = it)) },
            )
        }

        item {
            ThemePreviewSelector(
                selected = preferences.themeChoice,
                fontChoice = preferences.fontChoice,
                onSelect = { onPreferencesChange(preferences.copy(themeChoice = it)) },
            )
        }

        item {
            DesktopSimpleSelector(
                label = "Densidad del espacio de trabajo",
                value = preferences.workspaceDensity.label,
                options = DesktopWorkspaceDensity.entries,
                optionLabel = { it.label },
                onSelect = { onPreferencesChange(preferences.copy(workspaceDensity = it)) },
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                Column(
                    modifier = Modifier.padding(desktopSpacing(18.dp)),
                    verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp)),
                ) {
                    Text("Vista previa actual", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
                    Text("Alyra Voss · Maga 7", style = MaterialTheme.typography.h6)
                    Text("CD 15 · CA 17 · 1d20 + 7")
                    Text(
                        "${preferences.themeChoice.label} · ${preferences.fontChoice.label} · " +
                            "Texto ${preferences.fontScalePercent}% · Espacios ${preferences.spacingScalePercent}% · " +
                            preferences.workspaceDensity.label,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }
    }
}

@Composable
private fun FontPreviewSelector(
    selected: DesktopFontChoice,
    onSelect: (DesktopFontChoice) -> Unit,
) {
    val choices = remember { DesktopFontChoice.selectableChoices() }

    Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
        Column(
            modifier = Modifier.padding(desktopSpacing(18.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(10.dp)),
        ) {
            Text("Fuente", style = MaterialTheme.typography.h6)
            Text(
                "Cada opción se muestra con la fuente que realmente usará. Las fuentes Android no instaladas en este equipo no se ofrecen bajo un nombre falso.",
                style = MaterialTheme.typography.caption,
            )

            choices.forEach { choice ->
                val isSelected = choice == selected
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(choice) },
                    elevation = if (isSelected) 6.dp else 1.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(desktopSpacing(14.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(desktopSpacing(14.dp)),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                choice.label,
                                fontFamily = choice.family,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "Alyra Voss · Maga 7 · CA 17 · CD 15 · 1d20 + 7",
                                fontFamily = choice.family,
                            )
                            Text(
                                "El velo púrpura cubre el antiguo observatorio.",
                                fontFamily = choice.family,
                                style = MaterialTheme.typography.caption,
                            )
                        }
                        Button(onClick = { onSelect(choice) }) {
                            Text(if (isSelected) "Seleccionada" else "Usar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemePreviewSelector(
    selected: DesktopThemeChoice,
    fontChoice: DesktopFontChoice,
    onSelect: (DesktopThemeChoice) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
        Column(
            modifier = Modifier.padding(desktopSpacing(18.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(10.dp)),
        ) {
            Text("Tema", style = MaterialTheme.typography.h6)
            Text(
                "Vista previa de fondo, superficie y acentos antes de aplicar el tema al workbench.",
                style = MaterialTheme.typography.caption,
            )

            DesktopThemeChoice.entries.forEach { choice ->
                ThemePreviewCard(
                    choice = choice,
                    selected = choice == selected,
                    fontChoice = fontChoice,
                    onSelect = { onSelect(choice) },
                )
            }
        }
    }
}

@Composable
private fun ThemePreviewCard(
    choice: DesktopThemeChoice,
    selected: Boolean,
    fontChoice: DesktopFontChoice,
    onSelect: () -> Unit,
) {
    val previewColors = desktopColors(choice)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        elevation = if (selected) 6.dp else 1.dp,
    ) {
        MaterialTheme(
            colors = previewColors,
            typography = androidx.compose.material.Typography(defaultFontFamily = fontChoice.family),
        ) {
            Surface(
                color = previewColors.background,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(desktopSpacing(14.dp)),
                    verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp)),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(choice.label, fontWeight = FontWeight.Bold)
                            Text("Alyra Voss · Maga 7", style = MaterialTheme.typography.subtitle1)
                            Text("CA 17 · CD 15 · Concentración", style = MaterialTheme.typography.caption)
                        }
                        Button(onClick = onSelect) {
                            Text(if (selected) "Seleccionado" else "Usar")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(6.dp))) {
                        PaletteSwatch(previewColors.primary)
                        PaletteSwatch(previewColors.secondary)
                        PaletteSwatch(previewColors.surface)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaletteSwatch(color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .width(56.dp)
            .height(18.dp)
            .background(color),
    )
}

@Composable
private fun DesktopPercentSlider(
    label: String,
    value: Int,
    options: List<Int>,
    onSelect: (Int) -> Unit,
    detail: String,
) {
    val currentIndex = options.indexOf(value).coerceAtLeast(0)
    Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
        Column(
            modifier = Modifier.padding(desktopSpacing(18.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(6.dp)),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, fontWeight = FontWeight.Bold)
                Text("$value%")
            }
            Slider(
                value = currentIndex.toFloat(),
                onValueChange = { rawIndex ->
                    val index = rawIndex.roundToInt().coerceIn(options.indices)
                    val selectedValue = options[index]
                    if (selectedValue != value) onSelect(selectedValue)
                },
                valueRange = 0f..options.lastIndex.toFloat(),
                steps = (options.size - 2).coerceAtLeast(0),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${options.first()}%", style = MaterialTheme.typography.caption)
                Text("${options.last()}%", style = MaterialTheme.typography.caption)
            }
            Text(detail, style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
private fun <T> DesktopSimpleSelector(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
        Row(
            modifier = Modifier.padding(desktopSpacing(18.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontWeight = FontWeight.Bold)
                Text(value)
            }
            Box {
                Button(onClick = { expanded = true }) { Text("Cambiar") }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                onSelect(option)
                                expanded = false
                            },
                        ) {
                            Text(optionLabel(option))
                        }
                    }
                }
            }
        }
    }
}
