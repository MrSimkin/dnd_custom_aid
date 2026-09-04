package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleOverrideMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.moduleOverrideMode
import io.github.mrsimkin.dndcustomaid.shared.character.withModuleOverride

@Composable
internal fun CharacterPcSettingsClosureV4(
    characterName: String,
    status: CharacterStatus,
    spellcasterEnabled: Boolean,
    closureState: CharacterClosureState,
    suggestedModules: Set<CharacterModuleKind>,
    tableModeCanEnable: Boolean,
    onBack: () -> Unit,
    onStatusChange: (CharacterStatus) -> Unit,
    onSpellcasterEnabledChange: (Boolean) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    onOpenSupercompact: () -> Unit,
    onOpenApplicationSettings: () -> Unit,
) {
    var progressEditorOpen by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val wide = maxWidth >= 720.dp
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = if (wide) 18.dp else 8.dp,
                    end = if (wide) 18.dp else 8.dp,
                    top = 8.dp,
                    bottom = 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "pc-settings-header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        StableBackIconButton(
                            onClick = onBack,
                            contentDescription = "Volver a la ficha",
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ajustes de personaje", style = MaterialTheme.typography.titleLarge)
                            Text(
                                characterName.ifBlank { "Ficha de personaje" },
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Text(
                                "Los cambios de esta pantalla se guardan al aplicarlos.",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }

                item(key = "pc-settings-identity") {
                    PcSettingsPairClosureV4(
                        wide = wide,
                        first = {
                            LifecycleStatusCardClosureV4(
                                status = status,
                                onStatusChange = onStatusChange,
                            )
                        },
                        second = {
                            SpellcastingSettingsCardClosureV4(
                                enabled = spellcasterEnabled,
                                onEnabledChange = onSpellcasterEnabledChange,
                            )
                        },
                    )
                }

                item(key = "pc-settings-behavior") {
                    PcSettingsPairClosureV4(
                        wide = wide,
                        first = {
                            BooleanSettingCardClosureV4(
                                title = "Respuesta háptica",
                                description = "Usa una vibración breve y consistente en interacciones como arrastrar y reordenar.",
                                checked = closureState.hapticsEnabled,
                                onCheckedChange = { enabled ->
                                    onClosureStateChange(closureState.copy(hapticsEnabled = enabled))
                                },
                            )
                        },
                        second = {
                            BooleanSettingCardClosureV4(
                                title = "Modo mesa / solo lectura",
                                description = if (!tableModeCanEnable && !closureState.tableModeEnabled) {
                                    "Guarda o descarta los cambios estructurales pendientes antes de activar Modo Mesa."
                                } else {
                                    "Bloquea la edición estructural durante el uso en mesa y conserva los controles operativos intencionales."
                                },
                                checked = closureState.tableModeEnabled,
                                enabled = closureState.tableModeEnabled || tableModeCanEnable,
                                onCheckedChange = { enabled ->
                                    onClosureStateChange(closureState.copy(tableModeEnabled = enabled))
                                },
                            )
                        },
                    )
                }

                item(key = "pc-settings-progress") {
                    ProgressSettingsCardClosureV4(
                        state = closureState,
                        onModeChange = { mode ->
                            onClosureStateChange(closureState.copy(progressMode = mode))
                        },
                        onEditProgress = { progressEditorOpen = true },
                    )
                }

                item(key = "pc-settings-modules") {
                    ModuleSettingsCardClosureV4(
                        state = closureState,
                        suggestedModules = suggestedModules,
                        onStateChange = onClosureStateChange,
                    )
                }

                item(key = "pc-settings-entries") {
                    PcSettingsPairClosureV4(
                        wide = wide,
                        first = {
                            NavigationSettingCardClosureV4(
                                title = "Vista supercompacta",
                                description = "Abre la vista experimental de consulta rápida para comprobar densidad y utilidad en teléfono y tablet.",
                                actionLabel = "Abrir vista",
                                onClick = onOpenSupercompact,
                            )
                        },
                        second = {
                            NavigationSettingCardClosureV4(
                                title = "Configuración de la aplicación",
                                description = "Tema, tipografía y escala de texto siguen siendo preferencias globales de la aplicación.",
                                actionLabel = "Abrir configuración",
                                onClick = onOpenApplicationSettings,
                            )
                        },
                    )
                }
            }
        }
    }

    if (progressEditorOpen) {
        ProgressEditorDialogClosureV4(
            state = closureState,
            onDismiss = { progressEditorOpen = false },
            onSave = { experiencePoints, milestoneProgress ->
                onClosureStateChange(
                    closureState.copy(
                        experiencePoints = experiencePoints,
                        milestoneProgress = milestoneProgress,
                    ),
                )
                progressEditorOpen = false
            },
        )
    }
}

@Composable
private fun PcSettingsPairClosureV4(
    wide: Boolean,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
) {
    if (wide) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(modifier = Modifier.weight(1f)) { first() }
            Box(modifier = Modifier.weight(1f)) { second() }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            first()
            second()
        }
    }
}

@Composable
private fun LifecycleStatusCardClosureV4(
    status: CharacterStatus,
    onStatusChange: (CharacterStatus) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    PcSettingCardClosureV4(
        title = "Estado del personaje",
        description = "Configura su estado de ciclo de vida. No es estado temporal de combate.",
    ) {
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(pcStatusLabelClosureV4(status))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CharacterStatus.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(pcStatusLabelClosureV4(option)) },
                        onClick = {
                            onStatusChange(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SpellcastingSettingsCardClosureV4(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
) {
    PcSettingCardClosureV4(
        title = "Lanzador de conjuros",
        description = if (enabled) {
            "Muestra Lanzamiento de Conjuros y la pestaña Conjuros."
        } else {
            "Oculta las superficies de conjuros sin borrar sus datos guardados."
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(if (enabled) "Visible" else "Oculto", style = MaterialTheme.typography.labelLarge)
            Switch(checked = enabled, onCheckedChange = onEnabledChange)
        }
    }
}

@Composable
private fun BooleanSettingCardClosureV4(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    PcSettingCardClosureV4(title = title, description = description) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(if (checked) "Activado" else "Desactivado", style = MaterialTheme.typography.labelLarge)
            Switch(checked = checked, enabled = enabled, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun ProgressSettingsCardClosureV4(
    state: CharacterClosureState,
    onModeChange: (CharacterProgressMode) -> Unit,
    onEditProgress: () -> Unit,
) {
    PcSettingCardClosureV4(
        title = "Progreso",
        description = "Elige si esta ficha presenta avance por hitos o por puntos de experiencia.",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (state.progressMode == CharacterProgressMode.MILESTONE) {
                Button(onClick = { onModeChange(CharacterProgressMode.MILESTONE) }) { Text("Hitos") }
            } else {
                OutlinedButton(onClick = { onModeChange(CharacterProgressMode.MILESTONE) }) { Text("Hitos") }
            }
            if (state.progressMode == CharacterProgressMode.EXPERIENCE) {
                Button(onClick = { onModeChange(CharacterProgressMode.EXPERIENCE) }) { Text("PX") }
            } else {
                OutlinedButton(onClick = { onModeChange(CharacterProgressMode.EXPERIENCE) }) { Text("PX") }
            }
        }
        Text(
            when (state.progressMode) {
                CharacterProgressMode.MILESTONE -> state.milestoneProgress.ifBlank { "Sin nota de progreso de hitos." }
                CharacterProgressMode.EXPERIENCE -> "${state.experiencePoints} PX"
            },
            style = MaterialTheme.typography.bodyMedium,
        )
        TextButton(onClick = onEditProgress) { Text("Editar progreso") }
    }
}

@Composable
private fun ModuleSettingsCardClosureV4(
    state: CharacterClosureState,
    suggestedModules: Set<CharacterModuleKind>,
    onStateChange: (CharacterClosureState) -> Unit,
) {
    PcSettingCardClosureV4(
        title = "Módulos especiales",
        description = "Automático usa las sugerencias de clase/subclase. Mostrar u Ocultar permite corregir casos personalizados sin borrar datos.",
    ) {
        CharacterModuleKind.entries.forEach { module ->
            ModuleSettingRowClosureV4(
                module = module,
                suggested = module in suggestedModules,
                mode = state.moduleOverrideMode(module),
                onModeChange = { mode -> onStateChange(state.withModuleOverride(module, mode)) },
            )
        }
    }
}

@Composable
private fun ModuleSettingRowClosureV4(
    module: CharacterModuleKind,
    suggested: Boolean,
    mode: CharacterModuleOverrideMode,
    onModeChange: (CharacterModuleOverrideMode) -> Unit,
) {
    var expanded by remember(module) { mutableStateOf(false) }
    val visible = when (mode) {
        CharacterModuleOverrideMode.AUTO -> suggested
        CharacterModuleOverrideMode.FORCE_SHOW -> true
        CharacterModuleOverrideMode.FORCE_HIDE -> false
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(pcModuleLabelClosureV4(module), style = MaterialTheme.typography.labelLarge)
            Text(
                buildString {
                    append(if (suggested) "Sugerido por clase/subclase" else "Sin sugerencia automática")
                    append(" · ")
                    append(if (visible) "Visible" else "Oculto")
                },
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Box {
            OutlinedButton(onClick = { expanded = true }) { Text(pcOverrideLabelClosureV4(mode)) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CharacterModuleOverrideMode.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(pcOverrideLabelClosureV4(option)) },
                        onClick = {
                            onModeChange(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationSettingCardClosureV4(
    title: String,
    description: String,
    actionLabel: String,
    onClick: () -> Unit,
) {
    PcSettingCardClosureV4(title = title, description = description) {
        TextButton(onClick = onClick) { Text(actionLabel) }
    }
}

@Composable
private fun PcSettingCardClosureV4(
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodySmall)
            content()
        }
    }
}

@Composable
private fun ProgressEditorDialogClosureV4(
    state: CharacterClosureState,
    onDismiss: () -> Unit,
    onSave: (experiencePoints: Int, milestoneProgress: String) -> Unit,
) {
    var experienceText by rememberSaveable(state.experiencePoints) {
        mutableStateOf(state.experiencePoints.toString())
    }
    var milestoneText by rememberSaveable(state.milestoneProgress) {
        mutableStateOf(state.milestoneProgress)
    }
    val parsedExperience = experienceText.toIntOrNull()
    val valid = parsedExperience != null && parsedExperience >= 0

    CharacterImeSafeEditorDialog(
        title = "Editar progreso",
        onCancel = onDismiss,
        onSave = { onSave(parsedExperience ?: 0, milestoneText.trim()) },
        saveEnabled = valid,
    ) {
        OutlinedTextField(
            value = experienceText,
            onValueChange = { raw -> experienceText = raw.filter(Char::isDigit) },
            label = { Text("Puntos de experiencia") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        CharacterInlineValidationMessage(
            if (experienceText.isBlank()) "Escribe 0 o un valor de PX no negativo." else null,
        )
        OutlinedTextField(
            value = milestoneText,
            onValueChange = { milestoneText = it },
            label = { Text("Nota de progreso por hitos") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
        )
    }
}

private fun pcStatusLabelClosureV4(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}

private fun pcModuleLabelClosureV4(module: CharacterModuleKind): String = when (module) {
    CharacterModuleKind.ARTIFICER -> "Artífice"
    CharacterModuleKind.FORMS -> "Formas"
    CharacterModuleKind.TECHNIQUES -> "Técnicas"
    CharacterModuleKind.METAMAGIC -> "Metamagia"
    CharacterModuleKind.PACTS -> "Pactos"
    CharacterModuleKind.COMPANIONS -> "Compañeros"
}

private fun pcOverrideLabelClosureV4(mode: CharacterModuleOverrideMode): String = when (mode) {
    CharacterModuleOverrideMode.AUTO -> "Automático"
    CharacterModuleOverrideMode.FORCE_SHOW -> "Mostrar"
    CharacterModuleOverrideMode.FORCE_HIDE -> "Ocultar"
}
