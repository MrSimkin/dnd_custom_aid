package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleOverrideMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.moduleOverrideMode
import io.github.mrsimkin.dndcustomaid.shared.character.withModuleOverride

private enum class PcSettingsPageClosureV4 {
    MAIN,
    TAB_ORDER,
    CUSTOM_ATTRIBUTES,
    CUSTOM_SKILLS,
    CUSTOM_MARKERS,
    MODULES,
}

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
    var pageName by rememberSaveable { mutableStateOf(PcSettingsPageClosureV4.MAIN.name) }
    val requestedPage = runCatching { PcSettingsPageClosureV4.valueOf(pageName) }.getOrDefault(PcSettingsPageClosureV4.MAIN)
    val page = if (closureState.tableModeEnabled && requestedPage != PcSettingsPageClosureV4.MAIN) PcSettingsPageClosureV4.MAIN else requestedPage
    val layoutContext = characterLayoutContextV4()
    val wide = layoutContext.availableWidthDp >= 720
    val pcContext = LocalCharacterPcSettingsContextV4.current

    fun requestStatus(requested: CharacterStatus) {
        if (closureState.tableModeEnabled) return
        when (requested) {
            CharacterStatus.RETIRED,
            CharacterStatus.DEAD,
            -> pendingLifecycleStatusName = requested.name
            else -> onStatusChange(requested)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (page) {
            PcSettingsPageClosureV4.MAIN -> PcSettingsMainClosureV4(
                characterName = characterName,
                status = status,
                spellcasterEnabled = spellcasterEnabled,
                closureState = closureState,
                suggestedModules = suggestedModules,
                tableModeCanEnable = tableModeCanEnable,
                wide = wide,
                customAttributeCount = pcContext?.successorState?.customAttributes?.size ?: 0,
                customMarkerCount = pcContext?.successorState?.customMarkers?.size ?: 0,
                tabCount = pcContext?.successorState?.preferences?.tabOrder?.size ?: 0,
                inspirationVisible = pcContext?.pcConfiguration?.inspirationVisible ?: true,
                structuralSettingsEnabled = !closureState.tableModeEnabled,
                onInspirationVisibleChange = { visible ->
                    pcContext?.onPcConfigurationChange?.invoke(
                        pcContext.pcConfiguration.copy(inspirationVisible = visible),
                    )
                },
                onBack = onBack,
                onStatusChange = ::requestStatus,
                onSpellcasterEnabledChange = onSpellcasterEnabledChange,
                onClosureStateChange = onClosureStateChange,
                onNavigate = { pageName = it.name },
                onOpenSupercompact = onOpenSupercompact,
                backupExportEnabled = backupExportEnabled,
                onExportBackup = onExportBackup,
                onOpenApplicationSettings = onOpenApplicationSettings,
            )

            PcSettingsPageClosureV4.TAB_ORDER -> CharacterTabOrderDragSettingsV4(
                hapticsEnabled = closureState.hapticsEnabled,
                enabled = !closureState.tableModeEnabled,
                onBack = { pageName = PcSettingsPageClosureV4.MAIN.name },
            )

            PcSettingsPageClosureV4.CUSTOM_ATTRIBUTES -> PcSettingsSubpageClosureV4(
                title = "Características personalizadas",
                onBack = { pageName = PcSettingsPageClosureV4.MAIN.name },
            ) { CharacterCustomAttributesSettingsV4() }

            PcSettingsPageClosureV4.CUSTOM_SKILLS -> PcSettingsSubpageClosureV4(
                title = "Habilidades personalizadas",
                onBack = { pageName = PcSettingsPageClosureV4.MAIN.name },
            ) {
                CharacterCustomSkillsSettingsV4(
                    closureState = closureState,
                    onClosureStateChange = onClosureStateChange,
                )
            }

            PcSettingsPageClosureV4.CUSTOM_MARKERS -> PcSettingsSubpageClosureV4(
                title = "Marcadores personalizados",
                onBack = { pageName = PcSettingsPageClosureV4.MAIN.name },
            ) { CharacterCustomMarkersSettingsV4() }

            PcSettingsPageClosureV4.MODULES -> PcSettingsSubpageClosureV4(
                title = "Módulos especiales",
                onBack = { pageName = PcSettingsPageClosureV4.MAIN.name },
            ) {
                ModuleSettingsCardClosureV4(
                    state = closureState,
                    suggestedModules = suggestedModules,
                    onStateChange = onClosureStateChange,
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
                    CharacterStatus.RETIRED -> "El personaje quedará Retirado. Sus datos se conservarán y podrás cambiar el estado después."
                    CharacterStatus.DEAD -> "El personaje quedará Muerto. Sus datos se conservarán y podrás cambiar el estado después."
                    else -> "Se cambiará el estado del personaje."
                },
                onDismissRequest = { pendingLifecycleStatusName = null },
                onConfirm = {
                    onStatusChange(pending)
                    pendingLifecycleStatusName = null
                },
                confirmLabel = when (pending) {
                    CharacterStatus.RETIRED -> "Retirar"
                    CharacterStatus.DEAD -> "Marcar como muerto"
                    else -> "Cambiar"
                },
            )
        }
}

@Composable
private fun PcSettingsMainClosureV4(
    characterName: String,
    status: CharacterStatus,
    spellcasterEnabled: Boolean,
    closureState: CharacterClosureState,
    suggestedModules: Set<CharacterModuleKind>,
    tableModeCanEnable: Boolean,
    wide: Boolean,
    customAttributeCount: Int,
    customMarkerCount: Int,
    tabCount: Int,
    inspirationVisible: Boolean,
    structuralSettingsEnabled: Boolean,
    onInspirationVisibleChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onStatusChange: (CharacterStatus) -> Unit,
    onSpellcasterEnabledChange: (Boolean) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    onNavigate: (PcSettingsPageClosureV4) -> Unit,
    onOpenSupercompact: () -> Unit,
    backupExportEnabled: Boolean,
    onExportBackup: () -> Unit,
    onOpenApplicationSettings: () -> Unit,
) {
    val visibleModules = CharacterModuleKind.entries.count { module ->
        when (closureState.moduleOverrideMode(module)) {
            CharacterModuleOverrideMode.AUTO -> module in suggestedModules
            CharacterModuleOverrideMode.FORCE_SHOW -> true
            CharacterModuleOverrideMode.FORCE_HIDE -> false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = appSpacingV4(if (wide) 14.dp else 7.dp),
            end = appSpacingV4(if (wide) 14.dp else 7.dp),
            top = appSpacingV4(5.dp),
            bottom = appSpacingV4(28.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
    ) {
        item(key = "pc-settings-header") {
            PcSettingsHeaderClosureV4(characterName = characterName, onBack = onBack)
        }

        if (wide) {
            item(key = "pc-settings-wide-primary") {
                PcSettingsPairClosureV4(
                    wide = true,
                    first = {
                        PcSettingsSectionClosureV4("Ficha y navegación") {
                            PcToggleRowClosureV4(
                                title = "Lanzamiento de conjuros",
                                checked = spellcasterEnabled,
                                enabled = structuralSettingsEnabled,
                                onCheckedChange = onSpellcasterEnabledChange,
                            )
                            PcSettingsDividerClosureV4()
                            PcToggleRowClosureV4(
                                title = "Inspiración",
                                checked = inspirationVisible,
                                onCheckedChange = onInspirationVisibleChange,
                            )
                            PcSettingsDividerClosureV4()
                            PcNavigationRowClosureV4(
                                title = "Orden de pestañas",
                                summary = if (tabCount == 1) "1 pestaña" else "$tabCount pestañas",
                                enabled = structuralSettingsEnabled,
                                onClick = { onNavigate(PcSettingsPageClosureV4.TAB_ORDER) },
                            )
                        }
                    },
                    second = {
                        PcSettingsSectionClosureV4("Contenido personalizado") {
                            PcNavigationRowClosureV4(
                                title = "Características personalizadas",
                                summary = customAttributeCount.toString(),
                                enabled = structuralSettingsEnabled,
                                onClick = { onNavigate(PcSettingsPageClosureV4.CUSTOM_ATTRIBUTES) },
                            )
                            PcSettingsDividerClosureV4()
                            PcNavigationRowClosureV4(
                                title = "Habilidades personalizadas",
                                summary = closureState.customSkills.size.toString(),
                                enabled = structuralSettingsEnabled,
                                onClick = { onNavigate(PcSettingsPageClosureV4.CUSTOM_SKILLS) },
                            )
                            PcSettingsDividerClosureV4()
                            PcNavigationRowClosureV4(
                                title = "Marcadores personalizados",
                                summary = customMarkerCount.toString(),
                                enabled = structuralSettingsEnabled,
                                onClick = { onNavigate(PcSettingsPageClosureV4.CUSTOM_MARKERS) },
                            )
                            PcSettingsDividerClosureV4()
                            PcNavigationRowClosureV4(
                                title = "Módulos especiales",
                                summary = "$visibleModules visibles",
                                enabled = structuralSettingsEnabled,
                                onClick = { onNavigate(PcSettingsPageClosureV4.MODULES) },
                            )
                        }
                    },
                )
            }
        } else {
            item(key = "pc-settings-sheet-nav") {
                PcSettingsSectionClosureV4("Ficha y navegación") {
                    PcToggleRowClosureV4(
                        title = "Lanzamiento de conjuros",
                        checked = spellcasterEnabled,
                        onCheckedChange = onSpellcasterEnabledChange,
                        enabled = structuralSettingsEnabled,
                    )
                    PcSettingsDividerClosureV4()
                    PcToggleRowClosureV4("Inspiración", inspirationVisible, onInspirationVisibleChange)
                    PcSettingsDividerClosureV4()
                    PcNavigationRowClosureV4(
                        title = "Orden de pestañas",
                        summary = if (tabCount == 1) "1 pestaña" else "$tabCount pestañas",
                        enabled = structuralSettingsEnabled,
                        onClick = { onNavigate(PcSettingsPageClosureV4.TAB_ORDER) },
                    )
                }
            }
            item(key = "pc-settings-custom") {
                PcSettingsSectionClosureV4("Contenido personalizado") {
                    PcNavigationRowClosureV4(
                        title = "Características personalizadas",
                        summary = customAttributeCount.toString(),
                        enabled = structuralSettingsEnabled,
                        onClick = { onNavigate(PcSettingsPageClosureV4.CUSTOM_ATTRIBUTES) },
                    )
                    PcSettingsDividerClosureV4()
                    PcNavigationRowClosureV4(
                        title = "Habilidades personalizadas",
                        summary = closureState.customSkills.size.toString(),
                        enabled = structuralSettingsEnabled,
                        onClick = { onNavigate(PcSettingsPageClosureV4.CUSTOM_SKILLS) },
                    )
                    PcSettingsDividerClosureV4()
                    PcNavigationRowClosureV4(
                        title = "Marcadores personalizados",
                        summary = customMarkerCount.toString(),
                        enabled = structuralSettingsEnabled,
                        onClick = { onNavigate(PcSettingsPageClosureV4.CUSTOM_MARKERS) },
                    )
                    PcSettingsDividerClosureV4()
                    PcNavigationRowClosureV4(
                        title = "Módulos especiales",
                        summary = "$visibleModules visibles",
                        enabled = structuralSettingsEnabled,
                        onClick = { onNavigate(PcSettingsPageClosureV4.MODULES) },
                    )
                }
            }
        }

        item(key = "pc-settings-table-use") {
            PcSettingsSectionClosureV4("Uso en mesa") {
                PcToggleRowClosureV4(
                    title = "Modo Mesa",
                    checked = closureState.tableModeEnabled,
                    enabled = true,
                    secondary = if (!tableModeCanEnable && !closureState.tableModeEnabled) {
                        "Cambios de edición pendientes: revisar al activar"
                    } else {
                        null
                    },
                    onCheckedChange = { enabled ->
                        onClosureStateChange(closureState.copy(tableModeEnabled = enabled))
                    },
                )
                PcSettingsDividerClosureV4()
                CharacterHapticInlineSettingsClosureV4(
                    closureState = closureState,
                    onClosureStateChange = onClosureStateChange,
                )
                PcSettingsDividerClosureV4()
                PcNavigationRowClosureV4(
                    title = "Vista supercompacta",
                    onClick = onOpenSupercompact,
                )
            }
        }

        item(key = "pc-settings-character-data") {
            PcSettingsSectionClosureV4("Personaje y datos") {
                LifecycleStatusRowClosureV4(
                    status = status,
                    enabled = structuralSettingsEnabled,
                    onStatusChange = onStatusChange,
                )
                PcSettingsDividerClosureV4()
                PcActionRowClosureV4(
                    title = "Respaldo local",
                    action = "Exportar",
                    enabled = backupExportEnabled,
                    secondary = if (backupExportEnabled) null else "Guarda o descarta los cambios pendientes",
                    onClick = onExportBackup,
                )
            }
        }

        item(key = "pc-settings-application") {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp))) {
                Text(
                    "APLICACIÓN",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider()
                PcNavigationRowClosureV4(
                    title = "Configuración de la aplicación",
                    onClick = onOpenApplicationSettings,
                )
            }
        }
    }
}

@Composable
private fun PcSettingsHeaderClosureV4(characterName: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
    ) {
        StableBackIconButton(onClick = onBack, contentDescription = "Volver a la ficha")
        Column(modifier = Modifier.weight(1f)) {
            Text("Ajustes de personaje", style = MaterialTheme.typography.titleLarge)
            Text(
                characterName.ifBlank { "Ficha de personaje" },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PcSettingsSubpageClosureV4(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = appSpacingV4(7.dp),
            end = appSpacingV4(7.dp),
            top = appSpacingV4(5.dp),
            bottom = appSpacingV4(28.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
    ) {
        item(key = "subpage-header-$title") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            ) {
                StableBackIconButton(onClick = onBack, contentDescription = "Volver a Ajustes de personaje")
                Text(title, style = MaterialTheme.typography.titleLarge)
            }
        }
        item(key = "subpage-content-$title") { content() }
    }
}

@Composable
private fun PcSettingsSectionClosureV4(
    title: String,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(6.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            content()
        }
    }
}

@Composable
private fun PcSettingsDividerClosureV4() {
    HorizontalDivider(modifier = Modifier.padding(vertical = appSpacingV4(1.dp)))
}

@Composable
private fun PcNavigationRowClosureV4(
    title: String,
    summary: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = appSpacingV4(7.dp)),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        summary?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("›", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PcToggleRowClosureV4(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    secondary: String? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = appSpacingV4(3.dp)),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            secondary?.takeIf { it.isNotBlank() }?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Switch(checked = checked, enabled = enabled, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PcActionRowClosureV4(
    title: String,
    action: String,
    enabled: Boolean,
    secondary: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = appSpacingV4(4.dp)),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            secondary?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        TextButton(onClick = onClick, enabled = enabled) { Text(action) }
    }
}

@Composable
private fun CharacterHapticInlineSettingsClosureV4(
    closureState: CharacterClosureState,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
    val hapticContext = LocalCharacterHapticSettingsV4.current
    PcToggleRowClosureV4(
        title = "Respuesta háptica",
        checked = closureState.hapticsEnabled,
        secondary = "${hapticContext.preferences.strength.label} · ${hapticContext.preferences.duration.label}",
        onCheckedChange = { enabled -> onClosureStateChange(closureState.copy(hapticsEnabled = enabled)) },
    )
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
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            verticalAlignment = Alignment.Top,
        ) {
            Box(modifier = Modifier.weight(1f)) { first() }
            Box(modifier = Modifier.weight(1f)) { second() }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
        ) {
            first()
            second()
        }
    }
}

@Composable
private fun LifecycleStatusRowClosureV4(
    status: CharacterStatus,
    enabled: Boolean,
    onStatusChange: (CharacterStatus) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = appSpacingV4(4.dp)),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Estado del personaje", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Box {
            OutlinedButton(onClick = { expanded = true }, enabled = enabled) { Text(pcStatusLabelClosureV4(status)) }
            DropdownMenu(expanded = enabled && expanded, onDismissRequest = { expanded = false }) {
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
private fun ModuleSettingsCardClosureV4(
    state: CharacterClosureState,
    suggestedModules: Set<CharacterModuleKind>,
    onStateChange: (CharacterClosureState) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
    ) {
        CharacterModuleKind.entries.forEachIndexed { index, module ->
            ModuleSettingRowClosureV4(
                module = module,
                suggested = module in suggestedModules,
                mode = state.moduleOverrideMode(module),
                onModeChange = { mode -> onStateChange(state.withModuleOverride(module, mode)) },
            )
            if (index < CharacterModuleKind.entries.lastIndex) HorizontalDivider()
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
        modifier = Modifier.fillMaxWidth().padding(vertical = appSpacingV4(5.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(pcModuleLabelClosureV4(module), style = MaterialTheme.typography.bodyMedium)
            Text(
                "${pcOverrideLabelClosureV4(mode)} · ${if (visible) "Visible" else "Oculto"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
