package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleOverrideMode
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
    backupExportEnabled: Boolean,
    onExportBackup: () -> Unit,
    onOpenApplicationSettings: () -> Unit,
) {
    var pendingLifecycleStatusName by rememberSaveable { mutableStateOf<String?>(null) }
    val layoutContext = characterLayoutContextV4()
    val wide = layoutContext.isTablet

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = appSpacingV4(if (wide) 14.dp else 6.dp),
                end = appSpacingV4(if (wide) 14.dp else 6.dp),
                top = appSpacingV4(6.dp),
                bottom = appSpacingV4(28.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
        ) {
            item(key = "pc-settings-header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
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

            // Identity/lifecycle and safe character-level actions stay first.
            item(key = "pc-settings-identity-actions") {
                PcSettingsPairClosureV4(
                    wide = wide,
                    first = {
                        LifecycleStatusCardClosureV4(
                            status = status,
                            onStatusChange = { requested ->
                                when (requested) {
                                    CharacterStatus.RETIRED,
                                    CharacterStatus.DEAD,
                                    -> pendingLifecycleStatusName = requested.name

                                    else -> onStatusChange(requested)
                                }
                            },
                        )
                    },
                    second = {
                        NavigationSettingCardClosureV4(
                            title = "Respaldo local",
                            description = if (backupExportEnabled) {
                                "Exporta la última versión guardada de este personaje a un archivo propio de la aplicación."
                            } else {
                                "Guarda o descarta los cambios pendientes antes de exportar un respaldo."
                            },
                            actionLabel = "Exportar respaldo",
                            enabled = backupExportEnabled,
                            onClick = onExportBackup,
                        )
                    },
                )
            }

            // Global application configuration is intentionally easy to find but remains separate.
            item(key = "pc-settings-application") {
                NavigationSettingCardClosureV4(
                    title = "Configuración de la aplicación",
                    description = "Abre las preferencias globales de tema, tipografía, escala, densidad y otras opciones de la aplicación.",
                    actionLabel = "Abrir configuración",
                    onClick = onOpenApplicationSettings,
                )
            }

            item(key = "pc-settings-visibility") {
                PcSettingsPairClosureV4(
                    wide = wide,
                    first = {
                        SpellcastingSettingsCardClosureV4(
                            enabled = spellcasterEnabled,
                            onEnabledChange = onSpellcasterEnabledChange,
                        )
                    },
                    second = { CharacterInspirationVisibilitySettingsV4() },
                )
            }

            item(key = "pc-settings-tab-order") {
                CharacterTabOrderSettingsV4()
            }

            item(key = "pc-settings-custom-attributes") {
                CharacterCustomAttributesSettingsV4()
            }

            item(key = "pc-settings-custom-skills") {
                CharacterCustomSkillsSettingsV4(
                    closureState = closureState,
                    onClosureStateChange = onClosureStateChange,
                )
            }

            item(key = "pc-settings-custom-markers") {
                CharacterCustomMarkersSettingsV4()
            }

            item(key = "pc-settings-modules") {
                ModuleSettingsCardClosureV4(
                    state = closureState,
                    suggestedModules = suggestedModules,
                    onStateChange = onClosureStateChange,
                )
            }

            item(key = "pc-settings-behavior") {
                PcSettingsPairClosureV4(
                    wide = wide,
                    first = {
                        CharacterHapticProfileSettingsV4(
                            closureState = closureState,
                            onClosureStateChange = onClosureStateChange,
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

            item(key = "pc-settings-supercompact") {
                NavigationSettingCardClosureV4(
                    title = "Vista supercompacta",
                    description = "Abre la vista experimental de consulta rápida. Su utilidad y densidad todavía requieren aceptación del propietario en dispositivo.",
                    actionLabel = "Abrir vista",
                    onClick = onOpenSupercompact,
                )
            }
        }
    }

    pendingLifecycleStatusName
        ?.let { runCatching { CharacterStatus.valueOf(it) }.getOrNull() }
        ?.let { pending ->
            CharacterConfirmationDialog(
                title = when (pending) {
                    CharacterStatus.RETIRED -> "Retirar personaje"
                    CharacterStatus.DEAD -> "Marcar personaje como muerto"
                    else -> "Cambiar estado del personaje"
                },
                message = when (pending) {
                    CharacterStatus.RETIRED -> "El personaje quedará marcado como Retirado. Sus datos se conservarán y el estado podrá cambiarse después."
                    CharacterStatus.DEAD -> "El personaje quedará marcado como Muerto. Sus datos se conservarán y el estado podrá cambiarse después."
                    else -> "Se cambiará el estado de ciclo de vida del personaje."
                },
                onDismissRequest = { pendingLifecycleStatusName = null },
                onConfirm = {
                    onStatusChange(pending)
                    pendingLifecycleStatusName = null
                },
                confirmLabel = when (pending) {
                    CharacterStatus.RETIRED -> "Retirar"
                    CharacterStatus.DEAD -> "Marcar como muerto"
                    else -> "Cambiar estado"
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
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
            verticalAlignment = Alignment.Top,
        ) {
            Box(modifier = Modifier.weight(1f)) { first() }
            Box(modifier = Modifier.weight(1f)) { second() }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
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
        description = "Estado de ciclo de vida del personaje; no representa condiciones ni estado temporal de combate.",
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
                            expanded = false
                            if (option != status) onStatusChange(option)
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
        title = "Lanzamiento de Conjuros",
        description = if (enabled) {
            "Muestra las superficies y la pestaña Conjuros."
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
private fun ModuleSettingsCardClosureV4(
    state: CharacterClosureState,
    suggestedModules: Set<CharacterModuleKind>,
    onStateChange: (CharacterClosureState) -> Unit,
) {
    PcSettingCardClosureV4(
        title = "Módulos especiales",
        description = "Automático usa sugerencias de clase/subclase. Mostrar u Ocultar permite adaptar personajes personalizados sin borrar datos.",
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
            .padding(vertical = appSpacingV4(2.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
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
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    PcSettingCardClosureV4(title = title, description = description) {
        TextButton(onClick = onClick, enabled = enabled) { Text(actionLabel) }
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
                .padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(7.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.labelSmall)
            content()
        }
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
